package com.ccfurniture.block;

import com.ccfurniture.blockentity.DishwasherBlockEntity;
import com.ccfurniture.blockentity.ModBlockEntities;
import com.ccfurniture.util.ButtonHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DishwasherBlock extends BlockWithEntity {
	public static final MapCodec<DishwasherBlock> CODEC = createCodec(DishwasherBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty RUNNING = BooleanProperty.of("running");

	public DishwasherBlock(Settings settings) {
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
		return new DishwasherBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : validateTicker(type, ModBlockEntities.DISHWASHER, DishwasherBlockEntity::tick);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.SUCCESS;
		if (!(world.getBlockEntity(pos) instanceof DishwasherBlockEntity dw)) return ActionResult.PASS;

		ItemStack held = player.getStackInHand(hand);

		// start button on the front panel (model space, see ButtonHelper)
		if (ButtonHelper.hit(hit, pos, state.get(FACING), 2, 5, 11, 14)) {
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.6f, 1.2f);
			pressStart(dw, player);
			return ActionResult.CONSUME;
		}

		if (held.isOf(Items.WATER_BUCKET)) {
			if (dw.fillWater()) {
				player.setStackInHand(hand, ItemUsage.exchangeStack(held, player, new ItemStack(Items.BUCKET)));
				world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
				player.sendMessage(Text.translatable("message.cc_furniture.dishwasher_filled"), true);
			} else {
				player.sendMessage(Text.translatable("message.cc_furniture.dishwasher_full"), true);
			}
			return ActionResult.CONSUME;
		}

		if (player.isSneaking() && held.isEmpty()) {
			pressStart(dw, player);
			return ActionResult.CONSUME;
		}

		player.openHandledScreen(dw);
		return ActionResult.CONSUME;
	}

	private static void pressStart(DishwasherBlockEntity dw, PlayerEntity player) {
		if (dw.isRunning()) {
			player.sendMessage(Text.translatable("message.cc_furniture.dishwasher_busy"), true);
		} else if (dw.getWater() <= 0) {
			player.sendMessage(Text.translatable("message.cc_furniture.dishwasher_no_water"), true);
		} else if (!dw.start()) {
			player.sendMessage(Text.translatable("message.cc_furniture.dishwasher_empty"), true);
		} else {
			player.sendMessage(Text.translatable("message.cc_furniture.dishwasher_started"), true);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (!world.isClient && world.getBlockEntity(pos) instanceof DishwasherBlockEntity dw) {
			dw.onRedstone(world.isReceivingRedstonePower(pos));
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.get(RUNNING)) return;
		Direction dir = state.get(FACING);
		world.addParticle(ParticleTypes.SPLASH,
			pos.getX() + 0.5 + dir.getOffsetX() * 0.52 + (random.nextDouble() - 0.5) * 0.4,
			pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.4,
			pos.getZ() + 0.5 + dir.getOffsetZ() * 0.52 + (random.nextDouble() - 0.5) * 0.4, 0, 0.05, 0);
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
