package com.ccfurniture.compat;

import com.ccfurniture.blockentity.*;
import dan200.computercraft.api.peripheral.PeripheralLookup;

/** Only loaded when CC: Tweaked is installed (see CCFurniture#onInitialize). */
public final class FurniturePeripherals {
	public static void register() {
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new FurnacePeripheral("stove", (KitchenFurnaceBlockEntity) be), ModBlockEntities.STOVE);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new FurnacePeripheral("oven", (KitchenFurnaceBlockEntity) be), ModBlockEntities.OVEN);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new FurnacePeripheral("grill", (KitchenFurnaceBlockEntity) be), ModBlockEntities.GRILL);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new DishwasherPeripheral((DishwasherBlockEntity) be), ModBlockEntities.DISHWASHER);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new KitchenSinkPeripheral((KitchenSinkBlockEntity) be), ModBlockEntities.KITCHEN_SINK);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new FridgePeripheral((FridgeBlockEntity) be), ModBlockEntities.FRIDGE);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new MicrowavePeripheral((MicrowaveBlockEntity) be), ModBlockEntities.MICROWAVE);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new CoffeeMakerPeripheral((CoffeeMakerBlockEntity) be), ModBlockEntities.COFFEE_MAKER);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new PlatePeripheral((PlateBlockEntity) be), ModBlockEntities.PLATE);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new ToasterPeripheral((ToasterBlockEntity) be), ModBlockEntities.TOASTER);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new StoragePeripheral("kitchen_counter", (SimpleContainerBlockEntity) be), ModBlockEntities.KITCHEN_COUNTER);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new StoragePeripheral("kitchen_cabinet", (SimpleContainerBlockEntity) be), ModBlockEntities.KITCHEN_CABINET);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new MailboxPeripheral((MailboxBlockEntity) be), ModBlockEntities.MAILBOX);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new CollectionBoxPeripheral((CollectionBoxBlockEntity) be), ModBlockEntities.COLLECTION_BOX);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new PowerTogglePeripheral("fan", be, com.ccfurniture.block.FanBlock.ON), ModBlockEntities.FAN);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new PowerTogglePeripheral("desk_lamp", be, com.ccfurniture.block.DeskLampBlock.ON), ModBlockEntities.DESK_LAMP);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new StoragePeripheral("bedside_table", (SimpleContainerBlockEntity) be), ModBlockEntities.BEDSIDE_TABLE);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new BlenderPeripheral((BlenderBlockEntity) be), ModBlockEntities.BLENDER);
		PeripheralLookup.get().registerForBlockEntity(
			(be, side) -> new WaffleMakerPeripheral((WaffleMakerBlockEntity) be), ModBlockEntities.WAFFLE_MAKER);
	}

	private FurniturePeripherals() {}
}
