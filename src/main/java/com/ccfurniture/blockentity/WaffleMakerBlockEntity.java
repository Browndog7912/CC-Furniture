package com.ccfurniture.blockentity;

import com.ccfurniture.block.WaffleMakerBlock;
import com.ccfurniture.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Waffle maker: slots 0-1 hold wheat, slots 2-3 hold eggs. Each wheat + egg pair makes one waffle
 * (so up to 2 per batch). Start with the button, a redstone pulse or a computer; the lid closes while it cooks.
 */
public class WaffleMakerBlockEntity extends SimpleContainerBlockEntity {
	public static final int SIZE = 4;
	public static final int CYCLE_TICKS = 120;

	private int progress = 0;
	private boolean wasPowered = false;

	public WaffleMakerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.WAFFLE_MAKER, pos, state, SIZE);
	}

	public boolean isRunning() { return getCachedState().get(WaffleMakerBlock.RUNNING); }
	public int getProgress() { return progress; }

	@Override
	public int getMaxCountPerStack() { return 1; }

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return slot < 2 ? stack.isOf(Items.WHEAT) : stack.isOf(Items.EGG);
	}

	private int filled(int from, int to) {
		int n = 0;
		for (int i = from; i < to; i++) if (!inventory.get(i).isEmpty()) n++;
		return n;
	}

	public int wheat() { return filled(0, 2); }
	public int eggs() { return filled(2, 4); }
	/** How many waffles the current contents will make. */
	public int batches() { return Math.min(wheat(), eggs()); }

	/** Loads one wheat or egg into the matching free slot. @return false if it doesn't fit. */
	public boolean insert(ItemStack from) {
		int start = from.isOf(Items.WHEAT) ? 0 : 2;
		for (int i = start; i < start + 2; i++) {
			if (inventory.get(i).isEmpty()) {
				setStack(i, from.copyWithCount(1));
				return true;
			}
		}
		return false;
	}

	public boolean start() {
		if (world == null || world.isClient || isRunning() || batches() < 1) return false;
		progress = 0;
		setRunningState(true);
		world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 0.8f, 1.0f);
		markDirty();
		return true;
	}

	public boolean stop() {
		if (world == null || world.isClient || !isRunning()) return false;
		progress = 0;
		setRunningState(false);
		world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0.8f, 1.0f);
		markDirty();
		return true;
	}

	private void setRunningState(boolean running) {
		BlockState s = getCachedState();
		if (world != null && s.get(WaffleMakerBlock.RUNNING) != running) {
			world.setBlockState(pos, s.with(WaffleMakerBlock.RUNNING, running), 3);
		}
	}

	/** Mirrors the number of batches into the block state so the open plate shows batter. */
	private void syncBatter() {
		if (world == null || world.isClient) return;
		BlockState s = getCachedState();
		int n = batches();
		if (s.get(WaffleMakerBlock.BATTER) != n) {
			world.setBlockState(pos, s.with(WaffleMakerBlock.BATTER, n), 3);
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		super.setStack(slot, stack);
		syncBatter();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack r = super.removeStack(slot, amount);
		syncBatter();
		return r;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack r = super.removeStack(slot);
		syncBatter();
		return r;
	}

	@Override
	public void clear() {
		super.clear();
		syncBatter();
	}

	public void onRedstone(boolean powered) {
		if (powered && !wasPowered) start();
		wasPowered = powered;
	}

	public static void tick(World world, BlockPos pos, BlockState state, WaffleMakerBlockEntity be) {
		if (world.isClient || !state.get(WaffleMakerBlock.RUNNING)) return;
		be.progress++;
		if (be.progress % 20 == 0) {
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.4f, 1.2f);
		}
		if (be.progress >= CYCLE_TICKS) be.finishCycle();
		be.markDirty();
	}

	private void finishCycle() {
		progress = 0;
		if (world == null) return;
		int waffles = batches();
		for (int i = 0; i < waffles; i++) {
			inventory.set(i, ItemStack.EMPTY);       // wheat slot
			inventory.set(2 + i, ItemStack.EMPTY);   // egg slot
		}
		setRunningState(false);
		if (waffles > 0) {
			ItemEntity out = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				new ItemStack(ModItems.WAFFLE, waffles));
			out.setVelocity((world.random.nextDouble() - 0.5) * 0.05, 0.2, (world.random.nextDouble() - 0.5) * 0.05);
			world.spawnEntity(out);
			world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0.8f, 1.2f);
			world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.6f, 1.4f);
		}
		syncBatter();
		markDirty();
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.waffle_maker"); }

	/** The waffle maker has no GUI. */
	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) { return null; }

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("Progress", progress);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		progress = nbt.getInt("Progress");
	}
}
