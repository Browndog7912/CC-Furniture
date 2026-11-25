package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

/** A fixed-size container block entity with its own inventory list (avoids the unnamed Lootable* mappings). */
public abstract class SimpleContainerBlockEntity extends LockableContainerBlockEntity {
	protected DefaultedList<ItemStack> inventory;

	protected SimpleContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
		super(type, pos, state);
		this.inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	@Override
	public int size() { return inventory.size(); }

	@Override
	public boolean isEmpty() {
		for (ItemStack s : inventory) {
			if (!s.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) { return inventory.get(slot); }

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack result = Inventories.splitStack(inventory, slot, amount);
		if (!result.isEmpty()) markDirty();
		return result;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(inventory, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (stack.getCount() > getMaxCountPerStack()) stack.setCount(getMaxCountPerStack());
		markDirty();
	}

	@Override
	public void clear() { inventory.clear(); }

	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this, player);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		inventory = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, inventory);
	}
}
