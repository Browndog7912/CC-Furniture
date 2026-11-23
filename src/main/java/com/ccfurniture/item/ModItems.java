package com.ccfurniture.item;

import com.ccfurniture.CCFurniture;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModItems {
	public static final Item COFFEE = Registry.register(Registries.ITEM, CCFurniture.id("coffee"), new CoffeeItem());

	public static void register() {
		// Touching this class runs the static initializer above.
	}

	private ModItems() {}
}
