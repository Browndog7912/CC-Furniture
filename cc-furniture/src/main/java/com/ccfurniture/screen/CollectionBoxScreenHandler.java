package com.ccfurniture.screen;

import com.ccfurniture.blockentity.CollectionBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

/** Collection box GUI: one row of 9 slots, then the player inventory. */
public class CollectionBoxScreenHandler extends ScreenHandler {
	public final Inventory inventory;
	@Nullable
	public final CollectionBoxBlockEntity be;

	/** Client side. */
	public CollectionBoxScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
		this(syncId, playerInventory, new SimpleInventory(CollectionBoxBlockEntity.SIZE), null);
	}

	/** Server side. */
	public CollectionBoxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
	                                  @Nullable CollectionBoxBlockEntity be) {
		super(ModScreenHandlers.COLLECTION_BOX, syncId);
		checkSize(inventory, CollectionBoxBlockEntity.SIZE);
		this.inventory = inventory;
		this.be = be;
		inventory.onOpen(playerInventory.player);
		for (int i = 0; i < 9; i++) addSlot(new Slot(inventory, i, 8 + i * 18, 46));
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 9; c++) addSlot(new Slot(playerInventory, c + r * 9 + 9, 8 + c * 18, 78 + r * 18));
		}
		for (int c = 0; c < 9; c++) addSlot(new Slot(playerInventory, c, 8 + c * 18, 136));
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slotIndex) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = slots.get(slotIndex);
		if (slot != null && slot.hasStack()) {
			ItemStack original = slot.getStack();
			result = original.copy();
			if (slotIndex < CollectionBoxBlockEntity.SIZE) {
				if (!insertItem(original, CollectionBoxBlockEntity.SIZE, slots.size(), true)) return ItemStack.EMPTY;
			} else if (!insertItem(original, 0, CollectionBoxBlockEntity.SIZE, false)) {
				return ItemStack.EMPTY;
			}
			if (original.isEmpty()) slot.setStack(ItemStack.EMPTY);
			else slot.markDirty();
		}
		return result;
	}

	@Override
	public boolean canUse(PlayerEntity player) { return inventory.canPlayerUse(player); }

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		inventory.onClose(player);
	}
}
