package com.ccfurniture.compat;

import com.ccfurniture.blockentity.CollectionBoxBlockEntity;
import com.ccfurniture.blockentity.MailboxBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "collection_box". */
public class CollectionBoxPeripheral implements IPeripheral {
	private final CollectionBoxBlockEntity be;

	public CollectionBoxPeripheral(CollectionBoxBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "collection_box"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof CollectionBoxPeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	/** Sends everything in the box to an address. Returns true, or false + reason ("no_such" / "empty"). */
	@LuaFunction(mainThread = true)
	public final Object[] send(String address) {
		MailboxBlockEntity.Result r = be.send(address);
		return r == MailboxBlockEntity.Result.OK ? new Object[]{true} : new Object[]{false, r.name().toLowerCase()};
	}

	@LuaFunction(mainThread = true)
	public final int size() { return be.size(); }

	@LuaFunction(mainThread = true)
	public final Map<Integer, Object> list() { return LuaUtil.list(be); }

	@LuaFunction(mainThread = true)
	public final Map<String, Object> getItemDetail(int slot) throws LuaException {
		if (slot < 1 || slot > be.size()) throw new LuaException("Slot out of range (1-" + be.size() + ")");
		return LuaUtil.describe(be.getStack(slot - 1));
	}
}
