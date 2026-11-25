package com.ccfurniture.blockentity;

import com.ccfurniture.block.MailboxBlock;
import com.ccfurniture.mail.MailRegistry;
import com.ccfurniture.screen.MailboxScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Mailbox. Every mailbox can own one unique address. Slots 0-8 are the inbox (received mail, take only),
 * slots 9-17 are the outbox. "Send" moves the whole outbox to the mailbox that owns the target address.
 * Mail is any item.
 */
public class MailboxBlockEntity extends SimpleContainerBlockEntity implements ExtendedScreenHandlerFactory {
	public static final int INBOX = 9;
	public static final int SIZE = 18;
	private static final Pattern VALID = Pattern.compile("[a-z0-9_.-]{1,24}");

	public enum Result {
		OK(null), INVALID("message.cc_furniture.mailbox_invalid"), TAKEN("message.cc_furniture.mailbox_taken"),
		NO_SUCH("message.cc_furniture.mailbox_no_such"), EMPTY("message.cc_furniture.mailbox_empty"),
		NO_ADDRESS("message.cc_furniture.mailbox_no_address");

		public final String key;
		Result(String key) { this.key = key; }
	}

	private String address = "";

	public MailboxBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MAILBOX, pos, state, SIZE);
	}

	public String getAddress() { return address; }

	public static String normalize(String raw) { return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT); }

	@Override
	public boolean isValid(int slot, ItemStack stack) { return slot >= INBOX; }

	public boolean hasMail() {
		for (int i = 0; i < INBOX; i++) if (!inventory.get(i).isEmpty()) return true;
		return false;
	}

	private String dim() { return world.getRegistryKey().getValue().toString(); }

	private MailRegistry registry() {
		return MailRegistry.get(((ServerWorld) world).getServer());
	}

	private boolean isServer() { return world instanceof ServerWorld; }

	/** Sets this mailbox's unique address. */
	public Result setAddress(String raw) {
		if (!isServer()) return Result.INVALID;
		String a = normalize(raw);
		if (!VALID.matcher(a).matches()) return Result.INVALID;
		MailRegistry reg = registry();
		if (!reg.claim(a, dim(), pos)) return Result.TAKEN;
		if (!address.isEmpty() && !address.equals(a)) reg.release(address, dim(), pos);
		address = a;
		markDirty();
		pullMail();
		return Result.OK;
	}

	/** Gives the address up (called when the mailbox is removed). */
	public void releaseAddress() {
		if (isServer() && !address.isEmpty()) registry().release(address, dim(), pos);
	}

	/** Sends everything in the outbox to the mailbox with the given address. */
	public Result send(String rawTarget) {
		if (!isServer()) return Result.INVALID;
		if (address.isEmpty()) return Result.NO_ADDRESS;
		boolean any = false;
		for (int i = INBOX; i < SIZE; i++) if (!inventory.get(i).isEmpty()) any = true;
		if (!any) return Result.EMPTY;
		String target = normalize(rawTarget);
		MinecraftServer server = ((ServerWorld) world).getServer();
		MailRegistry reg = registry();
		if (!reg.isOwned(target)) return Result.NO_SUCH;
		for (int i = INBOX; i < SIZE; i++) {
			ItemStack s = inventory.get(i);
			if (!s.isEmpty()) {
				reg.deposit(target, s);
				inventory.set(i, ItemStack.EMPTY);
			}
		}
		markDirty();
		world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.6f, 0.8f);
		deliver(server, reg, target);
		return Result.OK;
	}

	/** Hands queued mail to the target mailbox right away if it is loaded; otherwise it arrives when its chunk loads. */
	public static void deliver(MinecraftServer server, MailRegistry reg, String target) {
		MailRegistry.Entry e = reg.get(target);
		if (e == null || !e.owned) return;
		ServerWorld w = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(e.dim)));
		BlockPos p = BlockPos.fromLong(e.pos);
		if (w != null && w.isChunkLoaded(p) && w.getBlockEntity(p) instanceof MailboxBlockEntity m) m.pullMail();
	}

	/** Moves mail that is waiting in the registry into the inbox; raises the flag if anything arrived. */
	public void pullMail() {
		if (!isServer() || address.isEmpty()) return;
		MailRegistry reg = registry();
		List<ItemStack> waiting = reg.drain(address);
		if (waiting.isEmpty()) return;
		boolean arrived = false;
		for (ItemStack stack : waiting) {
			ItemStack left = stack.copy();
			int before = left.getCount();
			insertInbox(left);
			if (left.getCount() != before) arrived = true;
			if (!left.isEmpty()) reg.deposit(address, left);
		}
		if (arrived) {
			markDirty();
			BlockState s = getCachedState();
			if (!s.get(MailboxBlock.FLAG)) world.setBlockState(pos, s.with(MailboxBlock.FLAG, true), 3);
			world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.BLOCKS, 0.8f, 1.4f);
		}
	}

	private void insertInbox(ItemStack stack) {
		for (int i = 0; i < INBOX && !stack.isEmpty(); i++) {
			ItemStack s = inventory.get(i);
			if (!s.isEmpty() && ItemStack.canCombine(s, stack)) {
				int move = Math.min(stack.getCount(), s.getMaxCount() - s.getCount());
				if (move > 0) {
					s.increment(move);
					stack.decrement(move);
				}
			}
		}
		for (int i = 0; i < INBOX && !stack.isEmpty(); i++) {
			if (inventory.get(i).isEmpty()) {
				inventory.set(i, stack.copy());
				stack.setCount(0);
			}
		}
	}

	public void playerSetAddress(ServerPlayerEntity player, String raw) {
		Result r = setAddress(raw);
		player.sendMessage(r == Result.OK ? Text.translatable("message.cc_furniture.mailbox_set", address)
			: Text.translatable(r.key), true);
	}

	public void playerSend(ServerPlayerEntity player, String raw) {
		Result r = send(raw);
		player.sendMessage(Text.translatable(r == Result.OK ? "message.cc_furniture.mailbox_sent" : r.key), true);
	}

	public static void tick(World world, BlockPos pos, BlockState state, MailboxBlockEntity be) {
		if (world.isClient || world.getTime() % 40 != 0 || be.address.isEmpty()) return;
		// self-heal: if a copy of this mailbox lost the address race (clone, structure), drop the address
		if (!be.registry().claim(be.address, be.dim(), pos)) {
			be.address = "";
			be.markDirty();
			return;
		}
		be.pullMail();
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.mailbox"); }

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new MailboxScreenHandler(syncId, playerInventory, this, this);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeString(address);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putString("Address", address);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		address = nbt.getString("Address");
	}
}
