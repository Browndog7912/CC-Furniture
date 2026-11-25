package com.ccfurniture.block;

import com.ccfurniture.blockentity.KitchenSinkBlockEntity;
import com.ccfurniture.util.WashingHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
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
import net.minecraft.world.World;

/**
 * Kitchen sink. Empty hand toggles the tap. With the tap on, a bucket becomes a water bucket (the sink never runs dry),
 * a glass bottle becomes a water bottle, and dyed leather armor / dyed shulker boxes / patterned banners get washed.
 */
public class KitchenSinkBlock extends BlockWithEntity {
	public static final MapCodec<KitchenSinkBlock> CODEC = createCodec(KitchenSinkBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty RUNNING = BooleanProperty.of("running");

	public KitchenSinkBlock(Settings settings) {
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
		return new KitchenSinkBlockEntity(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack held = player.getStackInHand(hand);
		boolean usable = held.isEmpty() || held.isOf(Items.BUCKET) || held.isOf(Items.GLASS_BOTTLE)
			|| WashingHelper.wash(held) != null;
		if (!usable) return ActionResult.PASS;
		if (world.isClient) return ActionResult.SUCCESS;
		if (!(world.getBlockEntity(pos) instanceof KitchenSinkBlockEntity sink)) return ActionResult.PASS;

		if (held.isEmpty()) {
			boolean on = !sink.isTapOn();
			sink.setTap(on);
			world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.5f, on ? 0.7f : 0.5f);
			player.sendMessage(Text.translatable(on ? "message.cc_furniture.tap_on" : "message.cc_furniture.tap_off"), true);
			return ActionResult.CONSUME;
		}

		if (!sink.isTapOn()) {
			player.sendMessage(Text.translatable("message.cc_furniture.tap_needed"), true);
			return ActionResult.CONSUME;
		}

		if (held.isOf(Items.BUCKET)) {
			player.setStackInHand(hand, ItemUsage.exchangeStack(held, player, new ItemStack(Items.WATER_BUCKET)));
			world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
			sink.addWaterUsed(1000);
		} else if (held.isOf(Items.GLASS_BOTTLE)) {
			ItemStack bottle = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
			player.setStackInHand(hand, ItemUsage.exchangeStack(held, player, bottle));
			world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
			sink.addWaterUsed(250);
		} else {
			ItemStack washed = WashingHelper.wash(held);
			if (washed != null) {
				player.setStackInHand(hand, washed);
				world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.5f, 1.2f);
				sink.addWaterUsed(100L * washed.getCount());
			}
		}
		return ActionResult.CONSUME;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.get(RUNNING)) return;
		Direction dir = state.get(FACING);
		double x = pos.getX() + 0.5 - dir.getOffsetX() * 0.094;
		double z = pos.getZ() + 0.5 - dir.getOffsetZ() * 0.094;
		world.addParticle(ParticleTypes.FALLING_WATER, x, pos.getY() + 0.85, z, 0, 0, 0);
		if (random.nextInt(8) == 0) {
			world.playSound(pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
				SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.3f, 1.0f, false);
		}
	}
}
