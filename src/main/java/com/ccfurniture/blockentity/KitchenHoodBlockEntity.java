package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/** Decorative: no data, it only exists so the client can draw the clock on it. */
public class KitchenHoodBlockEntity extends BlockEntity {
	public KitchenHoodBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.KITCHEN_HOOD, pos, state);
	}
}
