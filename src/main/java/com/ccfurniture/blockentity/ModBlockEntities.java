package com.ccfurniture.blockentity;

import com.ccfurniture.CCFurniture;
import com.ccfurniture.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
	public static final BlockEntityType<StoveBlockEntity> STOVE = register("stove",
		FabricBlockEntityTypeBuilder.create(StoveBlockEntity::new, ModBlocks.STOVE).build());
	public static final BlockEntityType<OvenBlockEntity> OVEN = register("oven",
		FabricBlockEntityTypeBuilder.create(OvenBlockEntity::new, ModBlocks.OVEN).build());
	public static final BlockEntityType<DishwasherBlockEntity> DISHWASHER = register("dishwasher",
		FabricBlockEntityTypeBuilder.create(DishwasherBlockEntity::new, ModBlocks.DISHWASHER).build());
	public static final BlockEntityType<KitchenSinkBlockEntity> KITCHEN_SINK = register("kitchen_sink",
		FabricBlockEntityTypeBuilder.create(KitchenSinkBlockEntity::new, ModBlocks.KITCHEN_SINK).build());
	public static final BlockEntityType<FridgeBlockEntity> FRIDGE = register("fridge",
		FabricBlockEntityTypeBuilder.create(FridgeBlockEntity::new, ModBlocks.FRIDGE).build());

	public static final BlockEntityType<MicrowaveBlockEntity> MICROWAVE = register("microwave",
		FabricBlockEntityTypeBuilder.create(MicrowaveBlockEntity::new, ModBlocks.MICROWAVE).build());
	public static final BlockEntityType<CoffeeMakerBlockEntity> COFFEE_MAKER = register("coffee_maker",
		FabricBlockEntityTypeBuilder.create(CoffeeMakerBlockEntity::new, ModBlocks.COFFEE_MAKER).build());
	public static final BlockEntityType<PlateBlockEntity> PLATE = register("plate",
		FabricBlockEntityTypeBuilder.create(PlateBlockEntity::new, ModBlocks.PLATE).build());

	public static final BlockEntityType<KitchenHoodBlockEntity> KITCHEN_HOOD = register("kitchen_hood",
		FabricBlockEntityTypeBuilder.create(KitchenHoodBlockEntity::new, ModBlocks.KITCHEN_HOOD).build());
	public static final BlockEntityType<ToasterBlockEntity> TOASTER = register("toaster",
		FabricBlockEntityTypeBuilder.create(ToasterBlockEntity::new, ModBlocks.TOASTER).build());

	public static final BlockEntityType<BlenderBlockEntity> BLENDER = register("blender",
		FabricBlockEntityTypeBuilder.create(BlenderBlockEntity::new, ModBlocks.BLENDER).build());
	public static final BlockEntityType<WaffleMakerBlockEntity> WAFFLE_MAKER = register("waffle_maker",
		FabricBlockEntityTypeBuilder.create(WaffleMakerBlockEntity::new, ModBlocks.WAFFLE_MAKER).build());

	public static final BlockEntityType<KitchenCounterBlockEntity> KITCHEN_COUNTER = register("kitchen_counter",
		FabricBlockEntityTypeBuilder.create(KitchenCounterBlockEntity::new, ModBlocks.KITCHEN_COUNTER).build());
	public static final BlockEntityType<KitchenCabinetBlockEntity> KITCHEN_CABINET = register("kitchen_cabinet",
		FabricBlockEntityTypeBuilder.create(KitchenCabinetBlockEntity::new, ModBlocks.KITCHEN_CABINET).build());

	public static final BlockEntityType<GrillBlockEntity> GRILL = register("grill",
		FabricBlockEntityTypeBuilder.create(GrillBlockEntity::new, ModBlocks.GRILL).build());

	public static final BlockEntityType<MailboxBlockEntity> MAILBOX = register("mailbox",
		FabricBlockEntityTypeBuilder.create(MailboxBlockEntity::new, ModBlocks.MAILBOX).build());

	public static final BlockEntityType<CollectionBoxBlockEntity> COLLECTION_BOX = register("collection_box",
		FabricBlockEntityTypeBuilder.create(CollectionBoxBlockEntity::new, ModBlocks.COLLECTION_BOX).build());

	public static final BlockEntityType<FanBlockEntity> FAN = register("fan",
		FabricBlockEntityTypeBuilder.create(FanBlockEntity::new, ModBlocks.FAN).build());
	public static final BlockEntityType<DeskLampBlockEntity> DESK_LAMP = register("desk_lamp",
		FabricBlockEntityTypeBuilder.create(DeskLampBlockEntity::new, ModBlocks.DESK_LAMP).build());

	public static final BlockEntityType<BedsideTableBlockEntity> BEDSIDE_TABLE = register("bedside_table",
		FabricBlockEntityTypeBuilder.create(BedsideTableBlockEntity::new, ModBlocks.BEDSIDE_TABLE).build());

	private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, CCFurniture.id(name), type);
	}

	public static void register() {
		// Touching this class runs the static initializer above.
	}

	private ModBlockEntities() {}
}
