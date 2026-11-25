package com.ccfurniture.block;

import com.ccfurniture.blockentity.CoffeeMakerBlockEntity;
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
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Coffee maker with a clickable brew button.
 * Water bucket = water, cocoa beans = coffee beans, empty hand = take a cup (or show status).
 */
public class CoffeeMakerBlock extends BlockWithEntity {
	public static final MapCodec<CoffeeMakerBlock> CODEC = createCodec(CoffeeMakerBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty RUNNING = BooleanProperty.of("running");
	private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 15, 14);

	public CoffeeMakerBlock(Settings settings) {
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
		return new CoffeeMakerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : validateTicker(type, ModBlockEntities.COFFEE_MAKER, CoffeeMakerBlockEntity::tick);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.SUCCESS;
		if (!(world.getBlockEntity(pos) instanceof CoffeeMakerBlockEntity cm)) return ActionResult.PASS;
		ItemStack held = player.getStackInHand(hand);

		// brew button on the front of the machine's head (model space, see ButtonHelper)
		if (ButtonHelper.hit(hit, pos, state.get(FACING), 10, 12, 12, 14)) {
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.6f, 1.2f);
			String key;
			if (cm.isBrewing()) key = "busy";
			else if (cm.start()) key = "started";
			else if (cm.getWater() <= 0) key = "no_water";
			else if (cm.getBeans() <= 0) key = "no_beans";
			else key = "full";
			player.sendMessage(Text.translatable("message.cc_furniture.coffee_" + key), true);
			return ActionResult.CONSUME;
		}

		if (held.isOf(Items.WATER_BUCKET)) {
			if (cm.fillWater()) {
				player.setStackInHand(hand, ItemUsage.exchangeStack(held, player, new ItemStack(Items.BUCKET)));
				world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
			} else {
				player.sendMessage(Text.translatable("message.cc_furniture.coffee_water_full"), true);
			}
			return ActionResult.CONSUME;
		}

		if (held.isOf(Items.COCOA_BEANS)) {
			int added = cm.addBeans(held.getCount());
			if (added > 0) {
				if (!player.getAbilities().creativeMode) held.decrement(added);
				world.playSound(null, pos, SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 0.8f, 1.0f);
			} else {
				player.sendMessage(Text.translatable("message.cc_furniture.coffee_beans_full"), true);
			}
			return ActionResult.CONSUME;
		}

		if (held.isEmpty()) {
			ItemStack cup = cm.takeCup();
			if (!cup.isEmpty()) {
				player.getInventory().offerOrDrop(cup);
				world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.0f);
			} else {
				player.sendMessage(Text.translatable("message.cc_furniture.coffee_status",
					cm.getWater(), CoffeeMakerBlockEntity.MAX_WATER, cm.getBeans(), cm.getCups()), true);
			}
			return ActionResult.CONSUME;
		}
		return ActionResult.PASS;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (!world.isClient && world.getBlockEntity(pos) instanceof CoffeeMakerBlockEntity cm) {
			cm.onRedstone(world.isReceivingRedstonePower(pos));
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.get(RUNNING)) return;
		world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.45,
			0, 0.02, 0);
	}
}
