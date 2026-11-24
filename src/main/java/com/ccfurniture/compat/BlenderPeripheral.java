package com.ccfurniture.compat;

import com.ccfurniture.blockentity.BlenderBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "blender". */
public class BlenderPeripheral implements IPeripheral {
	private final BlenderBlockEntity be;

	public BlenderPeripheral(BlenderBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "blender"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof BlenderPeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	/** Starts blending. Returns false if already running or fewer than 2 fruits are inside. */
	@LuaFunction(mainThread = true)
	public final boolean start() { return be.start(); }

	/** Stops blending without making anything. */
	@LuaFunction(mainThread = true)
	public final boolean stop() { return be.stop(); }

	@LuaFunction(mainThread = true)
	public final boolean isRunning() { return be.isRunning(); }

	/** Blending progress from 0 to 1. */
	@LuaFunction(mainThread = true)
	public final double getProgress() {
		return be.isRunning() ? Math.min(1.0, be.getProgress() / (double) BlenderBlockEntity.CYCLE_TICKS) : 0;
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
