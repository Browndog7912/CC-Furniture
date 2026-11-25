package com.ccfurniture.blockentity;

import com.ccfurniture.block.BlenderBlock;
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
 * Blender: 4 slots, one fruit each. A blend (button, redstone pulse or computer) needs at least 2 fruits
 * and turns every pair of them into one Smoothie.
 */
public class BlenderBlockEntity extends SimpleContainerBlockEntity {
	public static final int SIZE = 4;
	public static final int CYCLE_TICKS = 60;

	private int progress = 0;
	private boolean wasPowered = false;

	public BlenderBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BLENDER, pos, state, SIZE);
	}

	public static boolean isIngredient(ItemStack s) {
		return s.isOf(Items.APPLE) || s.isOf(Items.SWEET_BERRIES) || s.isOf(Items.GLOW_BERRIES) || s.isOf(Items.MELON_SLICE);
	}

	public boolean isRunning() { return getCachedState().get(BlenderBlock.RUNNING); }
	public int getProgress() { return progress; }

	@Override
	public int getMaxCountPerStack() { return 1; }

	@Override
	public boolean isValid(int slot, ItemStack stack) { return isIngredient(stack); }

	public int count() {
		int n = 0;
		for (ItemStack s : inventory) if (!s.isEmpty()) n++;
		return n;
	}

	/** Puts one fruit into a free slot. @return false if the jar is full. */
	public boolean insertFruit(ItemStack from) {
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).isEmpty()) {
				setStack(i, from.copyWithCount(1));
				return true;
			}
		}
		return false;
	}

	public boolean start() {
		if (world == null || world.isClient || isRunning() || count() < 2) return false;
		progress = 0;
		setRunningState(true);
		markDirty();
		return true;
	}

	public boolean stop() {
		if (world == null || world.isClient || !isRunning()) return false;
		progress = 0;
		setRunningState(false);
		markDirty();
		return true;
	}

	private void setRunningState(boolean running) {
		BlockState s = getCachedState();
		if (world != null && s.get(BlenderBlock.RUNNING) != running) {
			world.setBlockState(pos, s.with(BlenderBlock.RUNNING, running), 3);
		}
	}

	/** Mirrors the number of fruits into the block state so the jar shows its fill level. */
	private void syncLevel() {
		if (world == null || world.isClient) return;
		BlockState s = getCachedState();
		int n = count();
		if (s.get(BlenderBlock.LEVEL) != n) {
			world.setBlockState(pos, s.with(BlenderBlock.LEVEL, n), 3);
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		super.setStack(slot, stack);
		syncLevel();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack r = super.removeStack(slot, amount);
		syncLevel();
		return r;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack r = super.removeStack(slot);
		syncLevel();
		return r;
	}

	@Override
	public void clear() {
		super.clear();
		syncLevel();
	}

	public void onRedstone(boolean powered) {
		if (powered && !wasPowered) start();
		wasPowered = powered;
	}

	public static void tick(World world, BlockPos pos, BlockState state, BlenderBlockEntity be) {
		if (world.isClient || !state.get(BlenderBlock.RUNNING)) return;
		be.progress++;
		if (be.progress % 6 == 0) {
			world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 0.25f, 1.7f);
		}
		if (be.progress >= CYCLE_TICKS) be.finishCycle();
		be.markDirty();
	}

	private void finishCycle() {
		progress = 0;
		setRunningState(false);
		if (world == null) return;
		int smoothies = count() / 2;
		int toRemove = smoothies * 2;
		for (int i = inventory.size() - 1; i >= 0 && toRemove > 0; i--) {
			if (!inventory.get(i).isEmpty()) {
				inventory.set(i, ItemStack.EMPTY);
				toRemove--;
			}
		}
		if (smoothies > 0) {
			ItemEntity out = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
				new ItemStack(ModItems.SMOOTHIE, smoothies));
			out.setVelocity((world.random.nextDouble() - 0.5) * 0.05, 0.2, (world.random.nextDouble() - 0.5) * 0.05);
			world.spawnEntity(out);
			world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.6f, 1.2f);
		}
		syncLevel();
		markDirty();
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.blender"); }

	/** The blender has no GUI. */
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
