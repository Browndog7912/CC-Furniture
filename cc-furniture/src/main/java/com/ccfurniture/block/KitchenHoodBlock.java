package com.ccfurniture.block;

import com.ccfurniture.blockentity.KitchenHoodBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

/** Decorative kitchen hood (extractor). The in-game clock on its front is drawn by KitchenHoodRenderer. */
public class KitchenHoodBlock extends BlockWithEntity {
	public static final MapCodec<KitchenHoodBlock> CODEC = createCodec(KitchenHoodBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	// The model is 1px in from its front face and flush at the back, so the shape depends on the facing.
	private static final VoxelShape NORTH = Block.createCuboidShape(0, 0, 1, 16, 16, 16);
	private static final VoxelShape SOUTH = Block.createCuboidShape(0, 0, 0, 16, 16, 15);
	private static final VoxelShape EAST = Block.createCuboidShape(0, 0, 0, 15, 16, 16);
	private static final VoxelShape WEST = Block.createCuboidShape(1, 0, 0, 16, 16, 16);

	public KitchenHoodBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return switch (state.get(FACING)) {
			case SOUTH -> SOUTH;
			case EAST -> EAST;
			case WEST -> WEST;
			default -> NORTH;
		};
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new KitchenHoodBlockEntity(pos, state);
	}
}
