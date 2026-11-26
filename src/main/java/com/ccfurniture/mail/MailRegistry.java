package com.ccfurniture.mail;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * World-wide address book for mailboxes. Maps a unique address to the mailbox that owns it and keeps
 * mail that has been sent but not yet picked up by that mailbox (so mail is never lost when a chunk is unloaded).
 */
public class MailRegistry extends PersistentState {
	public static class Entry {
		public String dim = "";
		public long pos = 0L;
		public boolean owned = false;
		public final List<ItemStack> pending = new ArrayList<>();
	}

	private final Map<String, Entry> entries = new HashMap<>();

	public static MailRegistry get(MinecraftServer server) {
		return server.getOverworld().getPersistentStateManager().getOrCreate(
			new PersistentState.Type<>(MailRegistry::new, MailRegistry::fromNbt, null), "cc_furniture_mail");
	}

	public Entry get(String address) { return entries.get(address); }

	public boolean isOwned(String address) {
		Entry e = entries.get(address);
		return e != null && e.owned;
	}

	/** Claims an address for a mailbox. @return false if another mailbox already owns it. */
	public boolean claim(String address, String dim, BlockPos pos) {
		Entry e = entries.computeIfAbsent(address, k -> new Entry());
		if (e.owned && !(e.dim.equals(dim) && e.pos == pos.asLong())) return false;
		if (!e.owned || !e.dim.equals(dim) || e.pos != pos.asLong()) {
			e.owned = true;
			e.dim = dim;
			e.pos = pos.asLong();
			markDirty();
		}
		return true;
	}

	/** Gives an address up (mailbox removed or renamed). Undelivered mail stays queued for the next owner. */
	public void release(String address, String dim, BlockPos pos) {
		Entry e = entries.get(address);
		if (e == null || !e.owned || !e.dim.equals(dim) || e.pos != pos.asLong()) return;
		e.owned = false;
		if (e.pending.isEmpty()) entries.remove(address);
		markDirty();
	}

	public void deposit(String address, ItemStack stack) {
		if (stack.isEmpty()) return;
		entries.computeIfAbsent(address, k -> new Entry()).pending.add(stack.copy());
		markDirty();
	}

	/** Removes and returns all mail waiting for an address. */
	public List<ItemStack> drain(String address) {
		Entry e = entries.get(address);
		if (e == null || e.pending.isEmpty()) return List.of();
		List<ItemStack> out = new ArrayList<>(e.pending);
		e.pending.clear();
		if (!e.owned) entries.remove(address);
		markDirty();
		return out;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtList list = new NbtList();
		for (Map.Entry<String, Entry> me : entries.entrySet()) {
			Entry e = me.getValue();
			NbtCompound c = new NbtCompound();
			c.putString("Address", me.getKey());
			c.putString("Dim", e.dim);
			c.putLong("Pos", e.pos);
			c.putBoolean("Owned", e.owned);
			NbtList pending = new NbtList();
			for (ItemStack s : e.pending) pending.add(s.writeNbt(new NbtCompound()));
			c.put("Pending", pending);
			list.add(c);
		}
		nbt.put("Entries", list);
		return nbt;
	}

	public static MailRegistry fromNbt(NbtCompound nbt) {
		MailRegistry reg = new MailRegistry();
		NbtList list = nbt.getList("Entries", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < list.size(); i++) {
			NbtCompound c = list.getCompound(i);
			Entry e = new Entry();
			e.dim = c.getString("Dim");
			e.pos = c.getLong("Pos");
			e.owned = c.getBoolean("Owned");
			NbtList pending = c.getList("Pending", NbtElement.COMPOUND_TYPE);
			for (int j = 0; j < pending.size(); j++) {
				ItemStack s = ItemStack.fromNbt(pending.getCompound(j));
				if (!s.isEmpty()) e.pending.add(s);
			}
			reg.entries.put(c.getString("Address"), e);
		}
		return reg;
	}
}
