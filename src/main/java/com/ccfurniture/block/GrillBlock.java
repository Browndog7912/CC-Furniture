package com.ccfurniture.block;

import com.ccfurniture.blockentity.GrillBlockEntity;
import com.ccfurniture.blockentity.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** Grill: works like the oven (furnace GUI, smoking recipes). The lid swings open while it burns. */
public class GrillBlock extends AbstractFurnaceBlock {
	public static final MapCodec<GrillBlock> CODEC = createCodec(GrillBlock::new);
	private static final VoxelShape NS = Block.createCuboidShape(1, 0, 2, 15, 10, 14);
	private static final VoxelShape EW = Block.createCuboidShape(2, 0, 1, 14, 10, 15);

	public GrillBlock(Settings settings) {
		super(settings);
	}

	@Override
	public MapCodec<? extends AbstractFurnaceBlock> getCodec() {
		return CODEC;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(FACING).getAxis() == Direction.Axis.Z ? NS : EW;
	}

	@Override
	protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof GrillBlockEntity grill) {
			player.openHandledScreen((NamedScreenHandlerFactory) grill);
			player.incrementStat(Stats.INTERACT_WITH_SMOKER);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new GrillBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(world, type, ModBlockEntities.GRILL);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.get(LIT)) return;
		double x = pos.getX() + 0.5, y = pos.getY() + 0.4, z = pos.getZ() + 0.5;
		if (random.nextDouble() < 0.15) {
			world.playSound(x, y, z, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.6f, 1.0f, false);
		}
		double ox = (random.nextDouble() - 0.5) * 0.6;
		double oz = (random.nextDouble() - 0.5) * 0.5;
		world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x + ox, y + 0.3, z + oz, 0, 0.04, 0);
		if (random.nextInt(4) == 0) {
			world.addParticle(ParticleTypes.FLAME, x + ox, y, z + oz, 0, 0.01, 0);
		}
	}
}
