package com.ccfurniture.compat;

import com.ccfurniture.blockentity.ToasterBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "toaster". */
public class ToasterPeripheral implements IPeripheral {
	private final ToasterBlockEntity be;

	public ToasterPeripheral(ToasterBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "toaster"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof ToasterPeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	/** Pushes the lever down. Returns false if already toasting or there is no bread inside. */
	@LuaFunction(mainThread = true)
	public final boolean start() { return be.start(); }

	/** Pops the lever back up without toasting. */
	@LuaFunction(mainThread = true)
	public final boolean stop() { return be.stop(); }

	@LuaFunction(mainThread = true)
	public final boolean isToasting() { return be.isRunning(); }

	/** Toasting progress from 0 to 1. */
	@LuaFunction(mainThread = true)
	public final double getProgress() {
		return be.isRunning() ? Math.min(1.0, be.getProgress() / (double) ToasterBlockEntity.CYCLE_TICKS) : 0;
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
