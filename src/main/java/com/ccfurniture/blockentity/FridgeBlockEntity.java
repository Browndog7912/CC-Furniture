package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** Fridge: a 27-slot container that a computer can lock so players can't open it. */
public class FridgeBlockEntity extends SimpleContainerBlockEntity {
	public static final int SIZE = 27;

	private boolean locked = false;

	public FridgeBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FRIDGE, pos, state, SIZE);
	}

	public boolean isLocked() { return locked; }

	public void setLocked(boolean locked) {
		this.locked = locked;
		markDirty();
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.fridge"); }

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putBoolean("Locked", locked);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		locked = nbt.getBoolean("Locked");
	}
}
