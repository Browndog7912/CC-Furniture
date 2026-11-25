package com.ccfurniture.client;

import com.ccfurniture.block.KitchenHoodBlock;
import com.ccfurniture.blockentity.KitchenHoodBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

/** Draws the in-game time (24h, blinking colon) on the hood's screen. */
public class KitchenHoodRenderer implements BlockEntityRenderer<KitchenHoodBlockEntity> {
	private final TextRenderer textRenderer;

	public KitchenHoodRenderer(BlockEntityRendererFactory.Context ctx) {
		this.textRenderer = ctx.getTextRenderer();
	}

	@Override
	public void render(KitchenHoodBlockEntity be, float tickDelta, MatrixStack matrices,
	                   VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = be.getWorld();
		if (world == null) return;
		BlockState state = be.getCachedState();
		if (!(state.getBlock() instanceof KitchenHoodBlock)) return;
		Direction facing = state.get(KitchenHoodBlock.FACING);

		// Minecraft day starts at 06:00 when the time of day is 0.
		long t = world.getTimeOfDay() % 24000L;
		int hour = (int) ((t / 1000L + 6L) % 24L);
		int minute = (int) ((t % 1000L) * 60L / 1000L);
		boolean colon = (world.getTime() / 10L) % 2L == 0L;
		String text = String.format("%02d%s%02d", hour, colon ? ":" : " ", minute);

		matrices.push();
		matrices.translate(0.5, 0.5, 0.5);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
		// screen centre: 2px above the block's bottom, just in front of the screen plate (0.5px from the front edge)
		matrices.translate(0.0, -0.375, 0.4625 + 0.002);
		float scale = 0.018f;
		matrices.scale(scale, -scale, scale);
		float width = textRenderer.getWidth(text);
		textRenderer.draw(text, -width / 2f, -4f, 0x55FF55, false, matrices.peek().getPositionMatrix(),
			vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		matrices.pop();
	}
}
