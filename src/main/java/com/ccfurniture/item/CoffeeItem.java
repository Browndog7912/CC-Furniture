package com.ccfurniture.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;

/** A mug of coffee: always drinkable, gives Speed and Haste for 90 seconds. */
public class CoffeeItem extends Item {
	public CoffeeItem() {
		super(new Item.Settings().maxCount(16).food(new FoodComponent.Builder()
			.hunger(2)
			.saturationModifier(0.3f)
			.alwaysEdible()
			.statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 90, 1), 1.0f)
			.statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20 * 90, 0), 1.0f)
			.build()));
	}

	@Override
	public UseAction getUseAction(ItemStack stack) { return UseAction.DRINK; }

	@Override
	public SoundEvent getEatSound() { return SoundEvents.ENTITY_GENERIC_DRINK; }

	@Override
	public SoundEvent getDrinkSound() { return SoundEvents.ENTITY_GENERIC_DRINK; }
}
