package com.ccfurniture.block;

import com.ccfurniture.blockentity.ModBlockEntities;
import com.ccfurniture.blockentity.WaffleMakerBlockEntity;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Waffle maker. Right-click with wheat and eggs to load the plate (2 of each at most), empty hand takes them
 * back, click the button on the front (or redstone / computer) to close the lid and cook.
 * Each wheat + egg pair makes one Waffle.
 */
public class WaffleMakerBlock extends BlockWithEntity {
	public static final MapCodec<WaffleMakerBlock> CODEC = createCodec(WaffleMakerBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty RUNNING = BooleanProperty.of("running");
	public static final IntProperty BATTER = IntProperty.of("batter", 0, 2);

	private static final VoxelShape NS = Block.createCuboidShape(2, 0, 3, 14, 5.5, 13);
	private static final VoxelShape EW = Block.createCuboidShape(3, 0, 2, 13, 5.5, 14);

	public WaffleMakerBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState()
			.with(FACING, Direction.NORTH).with(RUNNING, false).with(BATTER, 0));
	}

	@Override
	public MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, RUNNING, BATTER);
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
		return new WaffleMakerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : validateTicker(type, ModBlockEntities.WAFFLE_MAKER, WaffleMakerBlockEntity::tick);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.SUCCESS;
		if (!(world.getBlockEntity(pos) instanceof WaffleMakerBlockEntity maker)) return ActionResult.PASS;
		ItemStack held = player.getStackInHand(hand);

		// start button on the front (model space, see ButtonHelper)
		if (ButtonHelper.hit(hit, pos, state.get(FACING), 10.5, 12.5, 0.8, 2)) {
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.6f, 1.0f);
			if (maker.isRunning()) {
				player.sendMessage(Text.translatable("message.cc_furniture.waffle_busy"), true);
			} else if (maker.start()) {
				player.sendMessage(Text.translatable("message.cc_furniture.waffle_started"), true);
			} else {
				player.sendMessage(Text.translatable("message.cc_furniture.waffle_nothing"), true);
			}
			return ActionResult.CONSUME;
		}

		if (held.isOf(Items.WHEAT) || held.isOf(Items.EGG)) {
			if (maker.isRunning()) {
				player.sendMessage(Text.translatable("message.cc_furniture.waffle_busy"), true);
			} else if (maker.insert(held)) {
				if (!player.getAbilities().creativeMode) held.decrement(1);
				world.playSound(null, pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.8f, 1.0f);
			} else {
				player.sendMessage(Text.translatable("message.cc_furniture.waffle_full"), true);
			}
			return ActionResult.CONSUME;
		}

		if (held.isEmpty() && !maker.isRunning()) {
			for (int i = maker.size() - 1; i >= 0; i--) {
				if (!maker.getStack(i).isEmpty()) {
					player.getInventory().offerOrDrop(maker.removeStack(i));
					world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.4f, 1.2f);
					return ActionResult.CONSUME;
				}
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (!world.isClient && world.getBlockEntity(pos) instanceof WaffleMakerBlockEntity m) {
			m.onRedstone(world.isReceivingRedstonePower(pos));
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.get(RUNNING)) return;
		world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5,
			pos.getY() + 0.4, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4, 0, 0.03, 0);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof Inventory inv) {
				ItemScatterer.spawn(world, pos, inv);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}
}
