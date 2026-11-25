package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** Oven: a furnace that bakes food twice as fast (smoking recipes). */
public class OvenBlockEntity extends KitchenFurnaceBlockEntity {
	public OvenBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.OVEN, pos, state, RecipeType.SMOKING);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.cc_furniture.oven");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory inventory) {
		return new SmokerScreenHandler(syncId, inventory, this, this.propertyDelegate);
	}
}
