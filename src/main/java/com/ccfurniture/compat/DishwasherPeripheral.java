package com.ccfurniture.compat;

import com.ccfurniture.blockentity.DishwasherBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "dishwasher". */
public class DishwasherPeripheral implements IPeripheral {
	private final DishwasherBlockEntity be;

	public DishwasherPeripheral(DishwasherBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "dishwasher"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof DishwasherPeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	/** Starts a wash cycle. Returns false if it is already running, has no water or no items. */
	@LuaFunction(mainThread = true)
	public final boolean start() { return be.start(); }

	@LuaFunction(mainThread = true)
	public final boolean stop() { return be.stop(); }

	@LuaFunction(mainThread = true)
	public final boolean isRunning() { return be.isRunning(); }

	/** Cycle progress from 0 to 1. */
	@LuaFunction(mainThread = true)
	public final double getProgress() {
		return be.isRunning() ? Math.min(1.0, be.getProgress() / (double) DishwasherBlockEntity.CYCLE_TICKS) : 0;
	}

	/** Water units left in the tank (a bucket fills it). */
	@LuaFunction(mainThread = true)
	public final int getWater() { return be.getWater(); }

	@LuaFunction(mainThread = true)
	public final int getMaxWater() { return DishwasherBlockEntity.MAX_WATER; }

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
