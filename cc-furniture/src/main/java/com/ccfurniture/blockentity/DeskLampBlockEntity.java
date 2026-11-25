package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/** Desk lamp. Holds no data; it exists so computers can attach to it. */
public class DeskLampBlockEntity extends BlockEntity {
	public DeskLampBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DESK_LAMP, pos, state);
	}
}
