package com.ccfurniture.util;

import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

/** What "washing" does: strips dye from leather gear and shulker boxes, and patterns from banners. */
public final class WashingHelper {
	/** @return the washed stack, or null when there was nothing to wash off. */
	@Nullable
	public static ItemStack wash(ItemStack stack) {
		if (stack.isEmpty()) return null;

		if (stack.getItem() instanceof DyeableItem dyeable && dyeable.hasColor(stack)) {
			ItemStack out = stack.copy();
			dyeable.removeColor(out);
			return out;
		}

		if (stack.getItem() instanceof BlockItem blockItem
			&& blockItem.getBlock() instanceof ShulkerBoxBlock shulker
			&& shulker.getColor() != null) {
			ItemStack out = new ItemStack(Blocks.SHULKER_BOX.asItem(), stack.getCount());
			if (stack.hasNbt()) out.setNbt(stack.getNbt().copy());
			return out;
		}

		if (stack.getItem() instanceof BannerItem) {
			NbtCompound be = stack.getSubNbt("BlockEntityTag");
			if (be != null && be.contains("Patterns", NbtElement.LIST_TYPE)) {
				ItemStack out = stack.copy();
				NbtCompound outBe = out.getSubNbt("BlockEntityTag");
				outBe.remove("Patterns");
				if (outBe.isEmpty()) out.removeSubNbt("BlockEntityTag");
				return out;
			}
		}
		return null;
	}

	private WashingHelper() {}
}
