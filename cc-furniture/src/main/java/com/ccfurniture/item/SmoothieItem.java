package com.ccfurniture.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;

/** A fruit smoothie from the blender: drinkable, gives a short Regeneration. */
public class SmoothieItem extends Item {
	public SmoothieItem() {
		super(new Item.Settings().maxCount(16).food(new FoodComponent.Builder()
			.hunger(5)
			.saturationModifier(0.5f)
			.statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 5, 0), 1.0f)
			.build()));
	}

	@Override
	public UseAction getUseAction(ItemStack stack) { return UseAction.DRINK; }

	@Override
	public SoundEvent getEatSound() { return SoundEvents.ENTITY_GENERIC_DRINK; }

	@Override
	public SoundEvent getDrinkSound() { return SoundEvents.ENTITY_GENERIC_DRINK; }
}
