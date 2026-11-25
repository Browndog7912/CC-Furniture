package com.ccfurniture.blockentity;

import com.ccfurniture.block.PlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

/** Holds one food item. The block state's FULL property mirrors whether there is food on the plate. */
public class PlateBlockEntity extends BlockEntity {
	private ItemStack food = ItemStack.EMPTY;

	public PlateBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PLATE, pos, state);
	}

	public boolean hasFood() { return !food.isEmpty(); }

	public ItemStack getFood() { return food; }

	public void setFood(ItemStack stack) {
		this.food = stack;
		markDirty();
		syncState();
	}

	/** Removes and returns the food. */
	public ItemStack takeFood() {
		ItemStack out = food;
		food = ItemStack.EMPTY;
		markDirty();
		syncState();
		return out;
	}

	private void syncState() {
		if (world != null && !world.isClient) {
			BlockState s = getCachedState();
			if (s.get(PlateBlock.FULL) != hasFood()) {
				world.setBlockState(pos, s.with(PlateBlock.FULL, hasFood()), 3);
			}
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (!food.isEmpty()) nbt.put("Food", food.writeNbt(new NbtCompound()));
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		food = nbt.contains("Food") ? ItemStack.fromNbt(nbt.getCompound("Food")) : ItemStack.EMPTY;
	}
}
