package com.ccfurniture.screen;

import com.ccfurniture.blockentity.MailboxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

/** Mailbox GUI: row of 9 inbox slots (take only), row of 9 outbox slots, then the player inventory. */
public class MailboxScreenHandler extends ScreenHandler {
	public static final int INBOX_Y = 62;
	public static final int OUTBOX_Y = 92;

	public final Inventory inventory;
	@Nullable
	public final MailboxBlockEntity be;
	private final String address;

	/** Client side: the address comes with the screen-opening data. */
	public MailboxScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
		this(syncId, playerInventory, new SimpleInventory(MailboxBlockEntity.SIZE), null, buf.readString());
	}

	/** Server side. */
	public MailboxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, MailboxBlockEntity be) {
		this(syncId, playerInventory, inventory, be, be.getAddress());
	}

	private MailboxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
	                             @Nullable MailboxBlockEntity be, String address) {
		super(ModScreenHandlers.MAILBOX, syncId);
		checkSize(inventory, MailboxBlockEntity.SIZE);
		this.inventory = inventory;
		this.be = be;
		this.address = address;
		inventory.onOpen(playerInventory.player);

		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(inventory, i, 8 + i * 18, INBOX_Y) {
				@Override
				public boolean canInsert(ItemStack stack) { return false; }
			});
		}
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(inventory, 9 + i, 8 + i * 18, OUTBOX_Y));
		}
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 9; c++) {
				addSlot(new Slot(playerInventory, c + r * 9 + 9, 8 + c * 18, 122 + r * 18));
			}
		}
		for (int c = 0; c < 9; c++) {
			addSlot(new Slot(playerInventory, c, 8 + c * 18, 180));
		}
	}

	public String getAddress() { return address; }

	@Override
	public ItemStack quickMove(PlayerEntity player, int slotIndex) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = slots.get(slotIndex);
		if (slot != null && slot.hasStack()) {
			ItemStack original = slot.getStack();
			result = original.copy();
			if (slotIndex < MailboxBlockEntity.SIZE) {
				if (!insertItem(original, MailboxBlockEntity.SIZE, slots.size(), true)) return ItemStack.EMPTY;
			} else if (!insertItem(original, MailboxBlockEntity.INBOX, MailboxBlockEntity.SIZE, false)) {
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
