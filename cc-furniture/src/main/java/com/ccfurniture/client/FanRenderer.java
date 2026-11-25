package com.ccfurniture.client;

import com.ccfurniture.block.FanBlock;
import com.ccfurniture.blockentity.FanBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

/** Draws the four fan blades and spins them while the fan is on, easing up to speed and winding down. */
public class FanRenderer implements BlockEntityRenderer<FanBlockEntity> {
	private static final Identifier TEXTURE = new Identifier("cc_furniture", "textures/block/mat_fan_blade.png");
	private static final float TOP_SPEED = 28f;   // degrees per tick

	public FanRenderer(BlockEntityRendererFactory.Context ctx) {}

	@Override
	public void render(FanBlockEntity be, float tickDelta, MatrixStack matrices,
	                   VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = be.getWorld();
		if (world == null) return;
		BlockState state = be.getCachedState();
		if (!(state.getBlock() instanceof FanBlock)) return;

		float now = world.getTime() + tickDelta;
		float dt = be.lastTime < 0f ? 0f : Math.min(5f, Math.max(0f, now - be.lastTime));
		be.lastTime = now;
		float target = state.get(FanBlock.ON) ? TOP_SPEED : 0f;
		be.speed += (target - be.speed) * Math.min(1f, 0.06f * dt);
		if (Math.abs(be.speed) < 0.05f && target == 0f) be.speed = 0f;
		be.spin = (be.spin + be.speed * dt) % 360f;

		Direction facing = state.get(FanBlock.FACING);
		VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));

		matrices.push();
		matrices.translate(0.5, 0.5, 0.5);
		// the model's front is -z for north; see KitchenHoodRenderer for the other convention
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f - facing.asRotation()));
		// blade centre: x 8, y 10, z 6 (in 1/16 block units, relative to the block centre)
		matrices.translate(0.0, 2.0 / 16.0, -2.0 / 16.0);
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(be.spin));
		matrices.scale(1f / 16f, 1f / 16f, 1f / 16f);
		for (int i = 0; i < 4; i++) {
			matrices.push();
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * 90f));
			matrices.translate(0f, 0.9f, 0f);
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(28f));   // blade pitch
			box(vc, matrices.peek(), -1.5f, 0f, -0.2f, 1.5f, 3.9f, 0.2f, light, overlay);
			matrices.pop();
		}
		matrices.pop();
	}

	private static void box(VertexConsumer vc, MatrixStack.Entry e, float x0, float y0, float z0,
	                        float x1, float y1, float z1, int light, int overlay) {
		quad(vc, e, x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0, 0, 0, -1, light, overlay);   // north
		quad(vc, e, x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1, 0, 0, 1, light, overlay);    // south
		quad(vc, e, x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1, -1, 0, 0, light, overlay);   // west
		quad(vc, e, x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0, 1, 0, 0, light, overlay);    // east
		quad(vc, e, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, 0, 1, 0, light, overlay);    // up
		quad(vc, e, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, 0, -1, 0, light, overlay);   // down
	}

	private static void quad(VertexConsumer vc, MatrixStack.Entry e,
	                         float ax, float ay, float az, float bx, float by, float bz,
	                         float cx, float cy, float cz, float dx, float dy, float dz,
	                         float nx, float ny, float nz, int light, int overlay) {
		vertex(vc, e, ax, ay, az, 0f, 1f, nx, ny, nz, light, overlay);
		vertex(vc, e, bx, by, bz, 1f, 1f, nx, ny, nz, light, overlay);
		vertex(vc, e, cx, cy, cz, 1f, 0f, nx, ny, nz, light, overlay);
		vertex(vc, e, dx, dy, dz, 0f, 0f, nx, ny, nz, light, overlay);
	}

	private static void vertex(VertexConsumer vc, MatrixStack.Entry e, float x, float y, float z, float u, float v,
	                           float nx, float ny, float nz, int light, int overlay) {
		vc.vertex(e.getPositionMatrix(), x, y, z).color(255, 255, 255, 255).texture(u, v)
			.overlay(overlay).light(light).normal(e.getNormalMatrix(), nx, ny, nz).next();
	}
}
