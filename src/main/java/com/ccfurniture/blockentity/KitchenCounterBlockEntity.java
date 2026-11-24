package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** Kitchen counter: a 9-slot drawer unit. */
public class KitchenCounterBlockEntity extends SimpleContainerBlockEntity {
	public static final int SIZE = 9;

	public KitchenCounterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.KITCHEN_COUNTER, pos, state, SIZE);
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.kitchen_counter"); }

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory, this, 1);
	}
}
