package com.ccfurniture.block;

import com.ccfurniture.blockentity.DeskLampBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/** Desk lamp: right-click or a redstone signal turns it on (light level 14). */
public class DeskLampBlock extends BlockWithEntity {
	public static final MapCodec<DeskLampBlock> CODEC = createCodec(DeskLampBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty ON = BooleanProperty.of("on");
	public static final BooleanProperty POWERED = Properties.POWERED;
	private static final VoxelShape NS = Block.createCuboidShape(5, 0, 2.5, 11, 12, 13.5);
	private static final VoxelShape EW = Block.createCuboidShape(2.5, 0, 5, 13.5, 12, 11);

	public DeskLampBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState()
			.with(FACING, Direction.NORTH).with(ON, false).with(POWERED, false));
	}

	@Override
	public MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, ON, POWERED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(FACING).getAxis() == Direction.Axis.Z ? NS : EW;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		boolean powered = ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos());
		return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
			.with(POWERED, powered).with(ON, powered);
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
		return new DeskLampBlockEntity(pos, state);
	}

	/** Right-click toggles. */
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.SUCCESS;
		boolean on = !state.get(ON);
		world.setBlockState(pos, state.with(ON, on), 3);
		world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.4f, on ? 1.4f : 1.1f);
		return ActionResult.CONSUME;
	}

	/** A redstone signal switches it on, losing the signal switches it off. */
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (world.isClient) return;
		boolean powered = world.isReceivingRedstonePower(pos);
		if (powered != state.get(POWERED)) {
			world.setBlockState(pos, state.with(POWERED, powered).with(ON, powered), 3);
		}
	}
}
