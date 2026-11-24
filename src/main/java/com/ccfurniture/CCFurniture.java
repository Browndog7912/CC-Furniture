package com.ccfurniture;

import com.ccfurniture.block.ModBlocks;
import com.ccfurniture.blockentity.ModBlockEntities;
import com.ccfurniture.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCFurniture implements ModInitializer {
	public static final String MOD_ID = "cc_furniture";
	public static final Logger LOGGER = LoggerFactory.getLogger("CC: Furniture");

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	public static final ItemGroup GROUP = Registry.register(Registries.ITEM_GROUP, id("main"),
		FabricItemGroup.builder()
			.icon(() -> new ItemStack(ModBlocks.FRIDGE))
			.displayName(Text.translatable("itemGroup.cc_furniture.main"))
			.entries((ctx, entries) -> {
				entries.add(ModBlocks.STOVE);
				entries.add(ModBlocks.OVEN);
				entries.add(ModBlocks.GRILL);
				entries.add(ModBlocks.DISHWASHER);
				entries.add(ModBlocks.KITCHEN_SINK);
				entries.add(ModBlocks.FRIDGE);
				entries.add(ModBlocks.MICROWAVE);
				entries.add(ModBlocks.COFFEE_MAKER);
				entries.add(ModBlocks.PLATE);
				entries.add(ModBlocks.KITCHEN_HOOD);
				entries.add(ModBlocks.TOASTER);
				entries.add(ModBlocks.KITCHEN_COUNTER);
				entries.add(ModBlocks.KITCHEN_CABINET);
				entries.add(ModBlocks.BLENDER);
				entries.add(ModBlocks.WAFFLE_MAKER);
				entries.add(ModItems.COFFEE);
				entries.add(ModItems.BREAD_SLICE);
				entries.add(ModItems.TOAST);
				entries.add(ModItems.SMOOTHIE);
				entries.add(ModItems.WAFFLE);
			})
			.build());

	@Override
	public void onInitialize() {
		ModItems.register();
		ModBlocks.register();
		ModBlockEntities.register();

		if (FabricLoader.getInstance().isModLoaded("computercraft")) {
			com.ccfurniture.compat.FurniturePeripherals.register();
			LOGGER.info("CC: Tweaked found - furniture peripherals registered.");
		}
	}
}
