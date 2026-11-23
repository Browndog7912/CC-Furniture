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

	private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, CCFurniture.id(name), type);
	}

	public static void register() {
		// Touching this class runs the static initializer above.
	}

	private ModBlockEntities() {}
}
