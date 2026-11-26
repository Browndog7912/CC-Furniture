package com.ccfurniture.client;

import com.ccfurniture.block.ModBlocks;
import com.ccfurniture.blockentity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class CCFurnitureClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(ModBlockEntities.KITCHEN_HOOD, KitchenHoodRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.FAN, FanRenderer::new);
		HandledScreens.register(com.ccfurniture.screen.ModScreenHandlers.MAILBOX, MailboxScreen::new);
		HandledScreens.register(com.ccfurniture.screen.ModScreenHandlers.COLLECTION_BOX, CollectionBoxScreen::new);
		// the blender jar is translucent glass
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLENDER, RenderLayer.getTranslucent());
	}
}
