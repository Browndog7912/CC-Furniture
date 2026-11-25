package com.ccfurniture.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;

/** Shared base for the Stove and the Oven: a furnace with a few read-only getters for computers. */
public abstract class KitchenFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	protected KitchenFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
	                                    RecipeType<? extends AbstractCookingRecipe> recipeType) {
		super(type, pos, state, recipeType);
	}

	/** Ticks of fuel left in the currently burning item. */
	public int getBurnTimeLeft() { return propertyDelegate.get(0); }
	/** Total burn time of the current fuel item. */
	public int getBurnTimeTotal() { return propertyDelegate.get(1); }
	/** Cooking progress of the current item, in ticks. */
	public int getCookProgress() { return propertyDelegate.get(2); }
	/** Ticks needed to finish the current item. */
	public int getCookTotal() { return propertyDelegate.get(3); }

	public ItemStack getInputStack() { return getStack(0); }
	public ItemStack getFuelStack() { return getStack(1); }
	public ItemStack getOutputStack() { return getStack(2); }
}
