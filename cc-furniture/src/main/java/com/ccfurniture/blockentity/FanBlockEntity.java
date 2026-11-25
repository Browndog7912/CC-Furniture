package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/** Fan. Holds no data; the client-only fields drive the blade animation. */
public class FanBlockEntity extends BlockEntity {
	/** Client only: current blade angle in degrees. */
	public float spin = 0f;
	/** Client only: current speed in degrees per tick (eases up and down). */
	public float speed = 0f;
	/** Client only: game time of the previous frame, -1 before the first one. */
	public float lastTime = -1f;

	public FanBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FAN, pos, state);
	}
}
