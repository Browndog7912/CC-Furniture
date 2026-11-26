package com.ccfurniture.block;

import com.ccfurniture.CCFurniture;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;

public final class ModBlocks {
	private static AbstractBlock.Settings metal(MapColor color) {
		return AbstractBlock.Settings.create().mapColor(color).strength(3.0f, 6.0f).sounds(BlockSoundGroup.METAL);
	}

	public static final Block STOVE = register("stove",
		new StoveBlock(metal(MapColor.IRON_GRAY).nonOpaque().luminance(s -> s.get(Properties.LIT) ? 13 : 0)));
	public static final Block OVEN = register("oven",
		new OvenBlock(metal(MapColor.IRON_GRAY).luminance(s -> s.get(Properties.LIT) ? 13 : 0)));
	public static final Block DISHWASHER = register("dishwasher", new DishwasherBlock(metal(MapColor.WHITE_GRAY)));
	public static final Block KITCHEN_SINK = register("kitchen_sink", new KitchenSinkBlock(metal(MapColor.WHITE_GRAY).nonOpaque()));
	public static final Block FRIDGE = register("fridge", new FridgeBlock(metal(MapColor.WHITE)));
	public static final Block MICROWAVE = register("microwave", new MicrowaveBlock(metal(MapColor.IRON_GRAY).strength(2.0f, 4.0f).nonOpaque()));
	public static final Block COFFEE_MAKER = register("coffee_maker", new CoffeeMakerBlock(metal(MapColor.BLACK).strength(2.0f, 4.0f).nonOpaque()));
	public static final Block PLATE = register("plate", new PlateBlock(
		AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(0.5f).sounds(BlockSoundGroup.STONE).nonOpaque()));

	public static final Block KITCHEN_HOOD = register("kitchen_hood",
		new KitchenHoodBlock(metal(MapColor.IRON_GRAY).strength(2.0f, 4.0f).nonOpaque().luminance(s -> 5)));
	public static final Block TOASTER = register("toaster",
		new ToasterBlock(metal(MapColor.IRON_GRAY).strength(1.0f, 3.0f).nonOpaque()));

	public static final Block BLENDER = register("blender",
		new BlenderBlock(metal(MapColor.BLACK).strength(1.0f, 3.0f).nonOpaque()));
	public static final Block WAFFLE_MAKER = register("waffle_maker",
		new WaffleMakerBlock(metal(MapColor.BLACK).strength(1.0f, 3.0f).nonOpaque()));

	public static final Block KITCHEN_COUNTER = register("kitchen_counter",
		new KitchenCounterBlock(metal(MapColor.WHITE_GRAY).strength(2.0f, 4.0f)));
	public static final Block KITCHEN_CABINET = register("kitchen_cabinet",
		new KitchenCabinetBlock(metal(MapColor.WHITE_GRAY).strength(2.0f, 4.0f).nonOpaque()));

	public static final Block GRILL = register("grill",
		new GrillBlock(metal(MapColor.BLACK).strength(2.0f, 4.0f).nonOpaque().luminance(s -> s.get(Properties.LIT) ? 15 : 0)));

	public static final Block MAILBOX = register("mailbox",
		new MailboxBlock(metal(MapColor.BLUE).strength(2.0f, 4.0f).nonOpaque()));

	public static final Block COLLECTION_BOX = register("collection_box",
		new CollectionBoxBlock(metal(MapColor.BLUE).strength(3.0f, 6.0f).nonOpaque()));

	public static final Block FAN = register("fan",
		new FanBlock(metal(MapColor.IRON_GRAY).strength(1.5f, 3.0f).nonOpaque()));
	public static final Block DESK_LAMP = register("desk_lamp",
		new DeskLampBlock(metal(MapColor.GREEN).strength(1.0f, 3.0f).nonOpaque()
			.luminance(s -> s.get(DeskLampBlock.ON) ? 14 : 0)));

	public static final Block KITCHEN_FLOOR = register("kitchen_floor",
		new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(1.5f, 6.0f).sounds(BlockSoundGroup.STONE)));
	public static final Block BEDSIDE_TABLE = register("bedside_table",
		new BedsideTableBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1.5f, 3.0f)
			.sounds(BlockSoundGroup.WOOD).nonOpaque()));

	private static Block register(String name, Block block) {
		Registry.register(Registries.ITEM, CCFurniture.id(name), new BlockItem(block, new Item.Settings()));
		return Registry.register(Registries.BLOCK, CCFurniture.id(name), block);
	}

	public static void register() {
		// Touching this class runs the static initializer above.
	}

	private ModBlocks() {}
}
