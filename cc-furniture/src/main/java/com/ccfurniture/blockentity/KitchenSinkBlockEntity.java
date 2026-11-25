package com.ccfurniture.blockentity;

import com.ccfurniture.block.KitchenSinkBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

/** Kitchen sink: the tap state lives in the block state; this just counts the water used. */
public class KitchenSinkBlockEntity extends BlockEntity {
	private long waterUsedMl = 0;

	public KitchenSinkBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.KITCHEN_SINK, pos, state);
	}

	public boolean isTapOn() {
		return getCachedState().get(KitchenSinkBlock.RUNNING);
	}

	public void setTap(boolean on) {
		BlockState s = getCachedState();
		if (world != null && !world.isClient && s.get(KitchenSinkBlock.RUNNING) != on) {
			world.setBlockState(pos, s.with(KitchenSinkBlock.RUNNING, on), 3);
		}
	}

	public long getWaterUsedMl() { return waterUsedMl; }

	public void addWaterUsed(long ml) {
		waterUsedMl += ml;
		markDirty();
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putLong("WaterUsed", waterUsedMl);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		waterUsedMl = nbt.getLong("WaterUsed");
	}
}
