package com.ccfurniture.client;

import com.ccfurniture.block.ModBlocks;
import com.ccfurniture.blockentity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class CCFurnitureClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(ModBlockEntities.KITCHEN_HOOD, KitchenHoodRenderer::new);
		// the blender jar is translucent glass
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLENDER, RenderLayer.getTranslucent());
	}
}
