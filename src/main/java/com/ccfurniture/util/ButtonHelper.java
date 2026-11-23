package com.ccfurniture.util;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Detects clicks on a "button" drawn on a block's front face.
 * The button is given in model space for a model that faces NORTH (x and y in 1/16 block units),
 * so it works for every rotation of the block.
 */
public final class ButtonHelper {
	public static boolean hit(BlockHitResult hit, BlockPos pos, Direction facing,
	                          double minX, double maxX, double minY, double maxY) {
		if (hit.getSide() != facing) return false;
		Vec3d l = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
		double mx = switch (facing) {
			case EAST -> l.z;
			case SOUTH -> 1.0 - l.x;
			case WEST -> 1.0 - l.z;
			default -> l.x;
		} * 16.0;
		double my = l.y * 16.0;
		double pad = 0.5;
		return mx >= minX - pad && mx <= maxX + pad && my >= minY - pad && my <= maxY + pad;
	}

	private ButtonHelper() {}
}
