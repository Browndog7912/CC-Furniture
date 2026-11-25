package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** Grill: a furnace that grills food (smoking recipes) over burning fuel. */
public class GrillBlockEntity extends KitchenFurnaceBlockEntity {
	public GrillBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GRILL, pos, state, RecipeType.SMOKING);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.cc_furniture.grill");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory inventory) {
		return new SmokerScreenHandler(syncId, inventory, this, this.propertyDelegate);
	}
}
