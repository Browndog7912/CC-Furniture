package com.ccfurniture.blockentity;

import com.ccfurniture.block.CoffeeMakerBlock;
import com.ccfurniture.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Coffee maker. Fill it with a water bucket (4 brews) and cocoa beans (1 bean per brew).
 * A brew takes 5 seconds and makes 2 cups, up to 8 waiting cups.
 */
public class CoffeeMakerBlockEntity extends BlockEntity {
	public static final int MAX_WATER = 4;
	public static final int MAX_BEANS = 16;
	public static final int MAX_CUPS = 8;
	public static final int CUPS_PER_BREW = 2;
	public static final int BREW_TICKS = 100;

	private int water, beans, cups, progress;
	private boolean wasPowered = false;

	public CoffeeMakerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.COFFEE_MAKER, pos, state);
	}

	public int getWater() { return water; }
	public int getBeans() { return beans; }
	public int getCups() { return cups; }
	public int getProgress() { return progress; }
	public boolean isBrewing() { return getCachedState().get(CoffeeMakerBlock.RUNNING); }

	public boolean fillWater() {
		if (water >= MAX_WATER) return false;
		water = MAX_WATER;
		markDirty();
		return true;
	}

	/** @return how many beans were accepted. */
	public int addBeans(int count) {
		int n = Math.max(0, Math.min(count, MAX_BEANS - beans));
		beans += n;
		if (n > 0) markDirty();
		return n;
	}

	public boolean start() {
		if (world == null || world.isClient || isBrewing() || water <= 0 || beans <= 0 || cups + CUPS_PER_BREW > MAX_CUPS) {
			return false;
		}
		progress = 0;
		setRunningState(true);
		world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.8f, 0.8f);
		markDirty();
		return true;
	}

	/** Takes a cup out. @return empty if there is none. */
	public ItemStack takeCup() {
		if (cups <= 0) return ItemStack.EMPTY;
		cups--;
		markDirty();
		return new ItemStack(ModItems.COFFEE);
	}

	/** Drops a cup in front of the machine (used by computers). */
	public boolean dispenseCup() {
		ItemStack cup = takeCup();
		if (cup.isEmpty() || world == null) return false;
		Direction f = getCachedState().get(CoffeeMakerBlock.FACING);
		ItemScatterer.spawn(world, pos.getX() + 0.5 + f.getOffsetX() * 0.8, pos.getY() + 0.3,
			pos.getZ() + 0.5 + f.getOffsetZ() * 0.8, cup);
		return true;
	}

	private void setRunningState(boolean running) {
		BlockState s = getCachedState();
		if (world != null && s.get(CoffeeMakerBlock.RUNNING) != running) {
			world.setBlockState(pos, s.with(CoffeeMakerBlock.RUNNING, running), 3);
		}
	}

	public void onRedstone(boolean powered) {
		if (powered && !wasPowered) start();
		wasPowered = powered;
	}

	public static void tick(World world, BlockPos pos, BlockState state, CoffeeMakerBlockEntity be) {
		if (world.isClient || !state.get(CoffeeMakerBlock.RUNNING)) return;
		be.progress++;
		if (be.progress % 20 == 0) {
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.4f, 1.6f);
		}
		if (be.progress >= BREW_TICKS) {
			be.water--;
			be.beans--;
			be.cups = Math.min(MAX_CUPS, be.cups + CUPS_PER_BREW);
			be.progress = 0;
			be.setRunningState(false);
			world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.BLOCKS, 1.0f, 1.2f);
		}
		be.markDirty();
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("Water", water);
		nbt.putInt("Beans", beans);
		nbt.putInt("Cups", cups);
		nbt.putInt("Progress", progress);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		water = nbt.getInt("Water");
		beans = nbt.getInt("Beans");
		cups = nbt.getInt("Cups");
		progress = nbt.getInt("Progress");
	}
}
