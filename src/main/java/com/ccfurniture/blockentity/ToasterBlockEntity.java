package com.ccfurniture.blockentity;

import com.ccfurniture.block.ToasterBlock;
import com.ccfurniture.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Toaster: 2 slots, one bread slice each. A toasting cycle (start lever, redstone pulse or computer)
 * turns the slices into toast and pops it out of the top.
 */
public class ToasterBlockEntity extends SimpleContainerBlockEntity {
	public static final int SIZE = 2;
	public static final int CYCLE_TICKS = 100;

	private int progress = 0;
	private boolean wasPowered = false;

	public ToasterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.TOASTER, pos, state, SIZE);
	}

	public boolean isRunning() { return getCachedState().get(ToasterBlock.RUNNING); }
	public int getProgress() { return progress; }

	@Override
	public int getMaxCountPerStack() { return 1; }

	@Override
	public boolean isValid(int slot, ItemStack stack) { return stack.isOf(ModItems.BREAD_SLICE); }

	public boolean hasBread() {
		for (ItemStack s : inventory) {
			if (s.isOf(ModItems.BREAD_SLICE)) return true;
		}
		return false;
	}

	/** Puts one slice into a free slot. @return false if both slots are taken. */
	public boolean insertSlice(ItemStack from) {
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).isEmpty()) {
				setStack(i, from.copyWithCount(1));
				return true;
			}
		}
		return false;
	}

	public boolean start() {
		if (world == null || world.isClient || isRunning() || !hasBread()) return false;
		progress = 0;
		setRunningState(true);
		world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.8f, 0.6f);
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
		if (world != null && s.get(ToasterBlock.RUNNING) != running) {
			world.setBlockState(pos, s.with(ToasterBlock.RUNNING, running), 3);
		}
	}

	/** Mirrors the number of slices into the block state so the model shows them. */
	private void syncSlices() {
		if (world == null || world.isClient) return;
		int n = 0;
		for (ItemStack s : inventory) if (!s.isEmpty()) n++;
		BlockState s = getCachedState();
		if (s.get(ToasterBlock.SLICES) != n) {
			world.setBlockState(pos, s.with(ToasterBlock.SLICES, n), 3);
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		super.setStack(slot, stack);
		syncSlices();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack r = super.removeStack(slot, amount);
		syncSlices();
		return r;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack r = super.removeStack(slot);
		syncSlices();
		return r;
	}

	@Override
	public void clear() {
		super.clear();
		syncSlices();
	}

	public void onRedstone(boolean powered) {
		if (powered && !wasPowered) start();
		wasPowered = powered;
	}

	public static void tick(World world, BlockPos pos, BlockState state, ToasterBlockEntity be) {
		if (world.isClient || !state.get(ToasterBlock.RUNNING)) return;
		be.progress++;
		if (be.progress % 20 == 0) {
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.4f, 1.4f);
		}
		if (be.progress >= CYCLE_TICKS) be.finishCycle();
		be.markDirty();
	}

	private void finishCycle() {
		progress = 0;
		setRunningState(false);
		if (world == null) return;
		boolean popped = false;
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).isOf(ModItems.BREAD_SLICE)) {
				inventory.set(i, ItemStack.EMPTY);
				ItemEntity toast = new ItemEntity(world, pos.getX() + 0.5 + (i == 0 ? -0.15 : 0.15), pos.getY() + 0.7,
					pos.getZ() + 0.5, new ItemStack(ModItems.TOAST));
				toast.setVelocity((world.random.nextDouble() - 0.5) * 0.05, 0.25, (world.random.nextDouble() - 0.5) * 0.05);
				world.spawnEntity(toast);
				popped = true;
			}
		}
		if (popped) {
			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.7f, 1.6f);
		}
		syncSlices();
		markDirty();
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.toaster"); }

	/** The toaster has no GUI. */
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
