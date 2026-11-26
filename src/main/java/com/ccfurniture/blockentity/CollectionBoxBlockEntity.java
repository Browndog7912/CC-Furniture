package com.ccfurniture.blockentity;

import com.ccfurniture.mail.MailRegistry;
import com.ccfurniture.screen.CollectionBoxScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * Collection box: send only. It has no address and no inbox; it holds up to 9 stacks and sends all of them
 * to the mailbox that owns the address you type.
 */
public class CollectionBoxBlockEntity extends SimpleContainerBlockEntity implements ExtendedScreenHandlerFactory {
	public static final int SIZE = 9;

	public CollectionBoxBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.COLLECTION_BOX, pos, state, SIZE);
	}

	/** Drops as much of the stack as fits into the box (the "chute"). The stack is shrunk by what was taken. */
	public int deposit(ItemStack stack) {
		int before = stack.getCount();
		for (int i = 0; i < SIZE && !stack.isEmpty(); i++) {
			ItemStack s = inventory.get(i);
			if (!s.isEmpty() && ItemStack.canCombine(s, stack)) {
				int move = Math.min(stack.getCount(), s.getMaxCount() - s.getCount());
				if (move > 0) {
					s.increment(move);
					stack.decrement(move);
				}
			}
		}
		for (int i = 0; i < SIZE && !stack.isEmpty(); i++) {
			if (inventory.get(i).isEmpty()) {
				inventory.set(i, stack.copy());
				stack.setCount(0);
			}
		}
		int moved = before - stack.getCount();
		if (moved > 0) markDirty();
		return moved;
	}

	/** Sends everything in the box to the mailbox with the given address. */
	public MailboxBlockEntity.Result send(String rawTarget) {
		if (!(world instanceof ServerWorld sw)) return MailboxBlockEntity.Result.INVALID;
		if (isEmpty()) return MailboxBlockEntity.Result.EMPTY;
		String target = MailboxBlockEntity.normalize(rawTarget);
		MinecraftServer server = sw.getServer();
		MailRegistry reg = MailRegistry.get(server);
		if (!reg.isOwned(target)) return MailboxBlockEntity.Result.NO_SUCH;
		for (int i = 0; i < SIZE; i++) {
			ItemStack s = inventory.get(i);
			if (!s.isEmpty()) {
				reg.deposit(target, s);
				inventory.set(i, ItemStack.EMPTY);
			}
		}
		markDirty();
		world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.6f, 0.8f);
		MailboxBlockEntity.deliver(server, reg, target);
		return MailboxBlockEntity.Result.OK;
	}

	public void playerSend(ServerPlayerEntity player, String raw) {
		MailboxBlockEntity.Result r = send(raw);
		player.sendMessage(Text.translatable(r == MailboxBlockEntity.Result.OK
			? "message.cc_furniture.mailbox_sent" : r.key), true);
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.collection_box"); }

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new CollectionBoxScreenHandler(syncId, playerInventory, this, this);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		// nothing to send: the box has no state the screen needs
	}
}
