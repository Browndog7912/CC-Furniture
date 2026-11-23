package com.ccfurniture.client;

import com.ccfurniture.blockentity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class CCFurnitureClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(ModBlockEntities.KITCHEN_HOOD, KitchenHoodRenderer::new);
	}
}
