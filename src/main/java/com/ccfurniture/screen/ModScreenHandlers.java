package com.ccfurniture.screen;

import com.ccfurniture.CCFurniture;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public final class ModScreenHandlers {
	public static final ScreenHandlerType<MailboxScreenHandler> MAILBOX = Registry.register(
		Registries.SCREEN_HANDLER, CCFurniture.id("mailbox"),
		new ExtendedScreenHandlerType<>((syncId, inv, buf) -> new MailboxScreenHandler(syncId, inv, buf)));

	public static final ScreenHandlerType<CollectionBoxScreenHandler> COLLECTION_BOX = Registry.register(
		Registries.SCREEN_HANDLER, CCFurniture.id("collection_box"),
		new ExtendedScreenHandlerType<>((syncId, inv, buf) -> new CollectionBoxScreenHandler(syncId, inv, buf)));

	public static void register() {
		// Touching this class runs the static initializer above.
	}

	private ModScreenHandlers() {}
}
