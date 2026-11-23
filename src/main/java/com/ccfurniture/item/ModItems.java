package com.ccfurniture.item;

import com.ccfurniture.CCFurniture;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModItems {
	public static final Item COFFEE = Registry.register(Registries.ITEM, CCFurniture.id("coffee"), new CoffeeItem());

	public static final Item BREAD_SLICE = Registry.register(Registries.ITEM, CCFurniture.id("bread_slice"),
		new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.4f).build())));
	public static final Item TOAST = Registry.register(Registries.ITEM, CCFurniture.id("toast"),
		new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(4).saturationModifier(0.6f).build())));

	public static void register() {
		// Touching this class runs the static initializer above.
	}

	private ModItems() {}
}
