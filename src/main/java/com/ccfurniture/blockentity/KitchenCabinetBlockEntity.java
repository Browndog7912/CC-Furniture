package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** Kitchen cabinet: an 18-slot wall unit. */
public class KitchenCabinetBlockEntity extends SimpleContainerBlockEntity {
	public static final int SIZE = 18;

	public KitchenCabinetBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.KITCHEN_CABINET, pos, state, SIZE);
	}

	@Override
	protected Text getContainerName() { return Text.translatable("container.cc_furniture.kitchen_cabinet"); }

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X2, syncId, playerInventory, this, 2);
	}
}
