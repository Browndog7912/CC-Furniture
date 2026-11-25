package com.ccfurniture.blockentity;

import com.ccfurniture.block.MicrowaveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Microwave: 5 slots. A 3-second cycle (start button, redstone pulse or computer) cooks every food item
 * inside that has a cooking recipe (the same recipes the smoker uses).
 */
public class MicrowaveBlockEntity extends SimpleContainerBlockEntity {
	public static final int SIZE = 5;
	public static final int CYCLE_TICKS = 60;

	private int progress = 0;
	private boolean wasPowered = false;

	public MicrowaveBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MICROWAVE, pos, state, SIZE);
	}

	public boolean isRunning() { return getCachedState().get(MicrowaveBlock.RUNNING); }
	public int getProgress() { return progress; }

	@Nullable
	private ItemStack cooked(ItemStack stack) {
		if (stack.isEmpty() || world == null) return null;
		Optional<RecipeEntry<SmokingRecipe>> r =
			world.getRecipeManager().getFirstMatch(RecipeType.SMOKING, new SimpleInventory(stack), world);
		if (r.isEmpty()) return null;
		ItemStack out = r.get().value().getResult(world.getRegistryManager()).copy();
		out.setCount(Math.min(out.getMaxCount(), out.getCount() * stack.getCount()));
		return out;
	}

	public boolean hasCookable() {
		for (ItemStack s : inventory) {
			if (cooked(s) != null) return true;
		}
		return false;
	}

	/** Starts a cycle. @return false if running already or nothing inside can be cooked. */
	public boolean start() {
		if (world == null || world.isClient || isRunning() || !hasCookable()) return false;
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
		if (world != null && s.get(MicrowaveBlock.RUNNING) != running) {
			world.setBlockState(pos, s.with(MicrowaveBlock.RUNNING, running), 3);
		}
	}

	public void onRedstone(boolean powered) {
		if (powered && !wasPowered) start();
		wasPowered = powered;
	}

	public static void tick(World world, BlockPos pos, BlockState state, MicrowaveBlockEntity be) {
		if (world.isClient || !state.get(MicrowaveBlock.RUNNING)) return;
		be.progress++;
		if (be.progress % 10 == 0) {
			world.playSound(null, pos, SoundEvents.BLOCK_SMOKER_SMOKE, SoundCategory.BLOCKS, 0.5f, 1.4f);
		}
		if (be.progress >= CYCLE_TICKS) be.finishCycle();
		be.markDirty();
	}

	private void finishCycle() {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack out = cooked(inventory.get(i));
			if (out != null) inventory.set(i, out);
		}
		progress = 0;
		setRunningState(false);
		if (world != null) {
			world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.BLOCKS, 1.0f, 1.5f);
		}
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.microwave"); }

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new HopperScreenHandler(syncId, playerInventory, this);
	}

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
