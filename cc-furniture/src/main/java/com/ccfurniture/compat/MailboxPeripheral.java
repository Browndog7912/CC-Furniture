package com.ccfurniture.compat;

import com.ccfurniture.blockentity.MailboxBlockEntity;
import com.ccfurniture.block.MailboxBlock;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "mailbox". Slots 1-9 are the inbox, slots 10-18 the outbox. */
public class MailboxPeripheral implements IPeripheral {
	private final MailboxBlockEntity be;

	public MailboxPeripheral(MailboxBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "mailbox"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof MailboxPeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	private static Object[] result(MailboxBlockEntity.Result r) {
		return r == MailboxBlockEntity.Result.OK ? new Object[]{true} : new Object[]{false, r.name().toLowerCase()};
	}

	/** This mailbox's address, or "" if none is set. */
	@LuaFunction(mainThread = true)
	public final String getAddress() { return be.getAddress(); }

	/** Sets the address (1-24 of a-z 0-9 _ . -). Returns true, or false + reason ("invalid" / "taken"). */
	@LuaFunction(mainThread = true)
	public final Object[] setAddress(String address) { return result(be.setAddress(address)); }

	/** Sends the whole outbox to an address. Returns true, or false + reason ("no_address" / "no_such" / "empty"). */
	@LuaFunction(mainThread = true)
	public final Object[] send(String address) { return result(be.send(address)); }

	/** True while there is mail in the inbox. */
	@LuaFunction(mainThread = true)
	public final boolean hasMail() { return be.hasMail(); }

	/** True while the flag is up (new mail nobody has looked at yet). */
	@LuaFunction(mainThread = true)
	public final boolean isFlagUp() { return be.getCachedState().get(MailboxBlock.FLAG); }

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
