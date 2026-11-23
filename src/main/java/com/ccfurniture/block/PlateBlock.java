package com.ccfurniture.block;

import com.ccfurniture.blockentity.PlateBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Plate: right-click with food to put it on the plate, right-click with an empty hand to eat it
 * (sneak + empty hand takes it back).
 */
public class PlateBlock extends BlockWithEntity {
	public static final MapCodec<PlateBlock> CODEC = createCodec(PlateBlock::new);
	public static final BooleanProperty FULL = BooleanProperty.of("full");
	private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 2, 14);

	public PlateBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(FULL, false));
	}

	@Override
	public MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FULL);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PlateBlockEntity(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof PlateBlockEntity plate)) return ActionResult.PASS;
		ItemStack held = player.getStackInHand(hand);

		if (!plate.hasFood()) {
			if (!held.isFood()) return ActionResult.PASS;
			if (world.isClient) return ActionResult.SUCCESS;
			plate.setFood(held.copyWithCount(1));
			if (!player.getAbilities().creativeMode) held.decrement(1);
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8f, 1.2f);
			return ActionResult.CONSUME;
		}

		if (!held.isEmpty()) return ActionResult.PASS;
		if (world.isClient) return ActionResult.SUCCESS;

		if (player.isSneaking()) {
			player.getInventory().offerOrDrop(plate.takeFood());
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.8f, 1.0f);
			return ActionResult.CONSUME;
		}

		FoodComponent fc = plate.getFood().getItem().getFoodComponent();
		if (fc == null || !player.canConsume(fc.isAlwaysEdible())) {
			player.sendMessage(Text.translatable("message.cc_furniture.not_hungry"), true);
			return ActionResult.CONSUME;
		}
		player.eatFood(world, plate.takeFood());
		return ActionResult.CONSUME;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (world.getBlockEntity(pos) instanceof PlateBlockEntity plate && plate.hasFood()) {
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), plate.getFood());
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}
}
