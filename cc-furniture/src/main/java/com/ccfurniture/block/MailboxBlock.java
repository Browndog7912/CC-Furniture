package com.ccfurniture.block;

import com.ccfurniture.blockentity.MailboxBlockEntity;
import com.ccfurniture.blockentity.ModBlockEntities;
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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Mailbox. Right-click opens the GUI (address, recipient, inbox, outbox). The flag goes up when mail arrives
 * and comes down when the mailbox is opened.
 */
public class MailboxBlock extends BlockWithEntity {
	public static final MapCodec<MailboxBlock> CODEC = createCodec(MailboxBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty FLAG = BooleanProperty.of("flag");

	private static final VoxelShape NS = Block.createCuboidShape(4, 0, 2, 12, 16, 14);
	private static final VoxelShape EW = Block.createCuboidShape(2, 0, 4, 14, 16, 12);

	public MailboxBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(FLAG, false));
	}

	@Override
	public MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, FLAG);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(FACING).getAxis() == Direction.Axis.Z ? NS : EW;
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
		return new MailboxBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : validateTicker(type, ModBlockEntities.MAILBOX, MailboxBlockEntity::tick);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.SUCCESS;
		if (world.getBlockEntity(pos) instanceof MailboxBlockEntity mailbox) {
			mailbox.pullMail();
			BlockState current = world.getBlockState(pos);
			if (current.get(FLAG)) world.setBlockState(pos, current.with(FLAG, false), 3);
			world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0.5f, 1.3f);
			player.openHandledScreen(mailbox);
		}
		return ActionResult.CONSUME;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof MailboxBlockEntity mailbox) mailbox.releaseAddress();
			if (be instanceof Inventory inv) {
				ItemScatterer.spawn(world, pos, inv);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}
}
