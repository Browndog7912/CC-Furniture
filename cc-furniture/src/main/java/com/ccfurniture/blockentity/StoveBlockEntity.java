package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** Stove: behaves exactly like a furnace (smelting recipes). */
public class StoveBlockEntity extends KitchenFurnaceBlockEntity {
	public StoveBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.STOVE, pos, state, RecipeType.SMELTING);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.cc_furniture.stove");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory inventory) {
		return new FurnaceScreenHandler(syncId, inventory, this, this.propertyDelegate);
	}
}
