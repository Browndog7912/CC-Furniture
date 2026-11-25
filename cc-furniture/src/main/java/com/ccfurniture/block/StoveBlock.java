package com.ccfurniture.block;

import com.ccfurniture.blockentity.ModBlockEntities;
import com.ccfurniture.blockentity.StoveBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StoveBlock extends AbstractFurnaceBlock {
	public static final MapCodec<StoveBlock> CODEC = createCodec(StoveBlock::new);

	public StoveBlock(Settings settings) {
		super(settings);
	}

	@Override
	public MapCodec<? extends AbstractFurnaceBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof StoveBlockEntity stove) {
			player.openHandledScreen((NamedScreenHandlerFactory) stove);
			player.incrementStat(Stats.INTERACT_WITH_FURNACE);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new StoveBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(world, type, ModBlockEntities.STOVE);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.get(LIT)) return;
		double x = pos.getX() + 0.5, y = pos.getY(), z = pos.getZ() + 0.5;
		if (random.nextDouble() < 0.1) {
			world.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
		}
		Direction dir = state.get(FACING);
		Direction.Axis axis = dir.getAxis();
		double f = random.nextDouble() * 0.6 - 0.3;
		double ox = axis == Direction.Axis.X ? dir.getOffsetX() * 0.52 : f;
		double oy = random.nextDouble() * 6.0 / 16.0;
		double oz = axis == Direction.Axis.Z ? dir.getOffsetZ() * 0.52 : f;
		world.addParticle(ParticleTypes.SMOKE, x + ox, y + oy, z + oz, 0, 0, 0);
		world.addParticle(ParticleTypes.FLAME, x + ox, y + oy, z + oz, 0, 0, 0);
		// burners on the top face
		world.addParticle(ParticleTypes.SMALL_FLAME, pos.getX() + 0.25 + (random.nextBoolean() ? 0.5 : 0),
			pos.getY() + 1.02, pos.getZ() + 0.25 + (random.nextBoolean() ? 0.5 : 0), 0, 0, 0);
	}
}
