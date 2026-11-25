package com.ccfurniture.compat;

import com.ccfurniture.blockentity.WaffleMakerBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "waffle_maker". Slots 1-2 are wheat, slots 3-4 are eggs. */
public class WaffleMakerPeripheral implements IPeripheral {
	private final WaffleMakerBlockEntity be;

	public WaffleMakerPeripheral(WaffleMakerBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "waffle_maker"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof WaffleMakerPeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	/** Closes the lid and cooks. Returns false if already cooking or there is no wheat + egg pair. */
	@LuaFunction(mainThread = true)
	public final boolean start() { return be.start(); }

	/** Opens the lid without finishing the waffles. */
	@LuaFunction(mainThread = true)
	public final boolean stop() { return be.stop(); }

	@LuaFunction(mainThread = true)
	public final boolean isCooking() { return be.isRunning(); }

	/** Cooking progress from 0 to 1. */
	@LuaFunction(mainThread = true)
	public final double getProgress() {
		return be.isRunning() ? Math.min(1.0, be.getProgress() / (double) WaffleMakerBlockEntity.CYCLE_TICKS) : 0;
	}

	/** How many waffles the current contents will make (0-2). */
	@LuaFunction(mainThread = true)
	public final int getBatches() { return be.batches(); }

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
