package com.ccfurniture.blockentity;

import com.ccfurniture.block.DishwasherBlock;
import com.ccfurniture.util.WashingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Dishwasher: 9 slots + an internal water tank (4 units, one water bucket fills it).
 * A wash cycle uses 1 unit and takes 5 seconds. It strips dye from leather armor and shulker boxes
 * and removes patterns from banners. Start it with sneak + empty hand, a redstone pulse or a computer.
 */
public class DishwasherBlockEntity extends SimpleContainerBlockEntity {
	public static final int SIZE = 9;
	public static final int MAX_WATER = 4;
	public static final int CYCLE_TICKS = 100;

	private int water = 0;
	private int progress = 0;
	private boolean wasPowered = false;

	public DishwasherBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DISHWASHER, pos, state, SIZE);
	}

	public int getWater() { return water; }
	public int getProgress() { return progress; }
	public boolean isRunning() { return getCachedState().get(DishwasherBlock.RUNNING); }

	/** Fills the tank. @return false if it was already full. */
	public boolean fillWater() {
		if (water >= MAX_WATER) return false;
		water = MAX_WATER;
		markDirty();
		return true;
	}

	/** Starts a wash cycle. @return false if it's already running, has no water, or is empty. */
	public boolean start() {
		if (world == null || world.isClient || isRunning() || water <= 0 || isEmpty()) return false;
		progress = 0;
		setRunningState(true);
		world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.8f, 1.0f);
		markDirty();
		return true;
	}

	/** Cancels a running cycle (the water is not refunded). */
	public boolean stop() {
		if (world == null || world.isClient || !isRunning()) return false;
		progress = 0;
		setRunningState(false);
		markDirty();
		return true;
	}

	private void setRunningState(boolean running) {
		BlockState s = getCachedState();
		if (world != null && s.get(DishwasherBlock.RUNNING) != running) {
			world.setBlockState(pos, s.with(DishwasherBlock.RUNNING, running), 3);
		}
	}

	public void onRedstone(boolean powered) {
		if (powered && !wasPowered) start();
		wasPowered = powered;
	}

	public static void tick(World world, BlockPos pos, BlockState state, DishwasherBlockEntity be) {
		if (world.isClient || !state.get(DishwasherBlock.RUNNING)) return;
		be.progress++;
		if (be.progress % 20 == 0) {
			world.playSound(null, pos, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.6f, 1.0f);
		}
		if (be.progress >= CYCLE_TICKS) {
			be.finishCycle();
		}
		be.markDirty();
	}

	private void finishCycle() {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack washed = WashingHelper.wash(inventory.get(i));
			if (washed != null) inventory.set(i, washed);
		}
		water = Math.max(0, water - 1);
		progress = 0;
		setRunningState(false);
		if (world != null) {
			world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0f, 1.2f);
		}
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.dishwasher"); }

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("Water", water);
		nbt.putInt("Progress", progress);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		water = nbt.getInt("Water");
		progress = nbt.getInt("Progress");
	}
}
