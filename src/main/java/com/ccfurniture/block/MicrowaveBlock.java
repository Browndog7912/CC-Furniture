package com.ccfurniture.block;

import com.ccfurniture.blockentity.MicrowaveBlockEntity;
import com.ccfurniture.blockentity.ModBlockEntities;
import com.ccfurniture.util.ButtonHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** Microwave with a clickable start button on the front panel. Anywhere else opens the 5-slot GUI. */
public class MicrowaveBlock extends BlockWithEntity {
	public static final MapCodec<MicrowaveBlock> CODEC = createCodec(MicrowaveBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty RUNNING = BooleanProperty.of("running");
	private static final VoxelShape SHAPE = Block.createCuboidShape(1, 0, 1, 15, 9, 15);

	public MicrowaveBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(RUNNING, false));
	}

	@Override
	public MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, RUNNING);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
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
		return new MicrowaveBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : validateTicker(type, ModBlockEntities.MICROWAVE, MicrowaveBlockEntity::tick);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.SUCCESS;
		if (!(world.getBlockEntity(pos) instanceof MicrowaveBlockEntity mw)) return ActionResult.PASS;

		// start button on the front panel (model space, see ButtonHelper)
		if (ButtonHelper.hit(hit, pos, state.get(FACING), 2, 5, 1, 4)) {
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.6f, 1.2f);
			if (mw.isRunning()) {
				player.sendMessage(Text.translatable("message.cc_furniture.microwave_busy"), true);
			} else if (mw.start()) {
				player.sendMessage(Text.translatable("message.cc_furniture.microwave_started"), true);
			} else {
				player.sendMessage(Text.translatable("message.cc_furniture.microwave_nothing"), true);
			}
			return ActionResult.CONSUME;
		}

		player.openHandledScreen(mw);
		return ActionResult.CONSUME;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (!world.isClient && world.getBlockEntity(pos) instanceof MicrowaveBlockEntity mw) {
			mw.onRedstone(world.isReceivingRedstonePower(pos));
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof Inventory inv) {
				ItemScatterer.spawn(world, pos, inv);
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) { return true; }

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}
}
