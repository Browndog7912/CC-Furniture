package com.ccfurniture.compat;

import com.ccfurniture.blockentity.KitchenFurnaceBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "stove" or "oven". */
public class FurnacePeripheral implements IPeripheral {
	private final KitchenFurnaceBlockEntity be;
	private final String type;

	public FurnacePeripheral(String type, KitchenFurnaceBlockEntity be) {
		this.type = type;
		this.be = be;
	}

	@Override
	public String getType() { return type; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof FurnacePeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	@LuaFunction(mainThread = true)
	public final boolean isLit() { return be.getBurnTimeLeft() > 0; }

	@LuaFunction(mainThread = true)
	public final int getBurnTime() { return be.getBurnTimeLeft(); }

	@LuaFunction(mainThread = true)
	public final int getCookTime() { return be.getCookProgress(); }

	@LuaFunction(mainThread = true)
	public final int getCookTimeTotal() { return be.getCookTotal(); }

	/** Cooking progress from 0 to 1. */
	@LuaFunction(mainThread = true)
	public final double getProgress() {
		int total = be.getCookTotal();
		return total <= 0 ? 0 : Math.min(1.0, be.getCookProgress() / (double) total);
	}

	@LuaFunction(mainThread = true)
	public final int size() { return be.size(); }

	@LuaFunction(mainThread = true)
	public final Map<Integer, Object> list() { return LuaUtil.list(be); }

	@LuaFunction(mainThread = true)
	public final Map<String, Object> getInput() { return LuaUtil.describe(be.getInputStack()); }

	@LuaFunction(mainThread = true)
	public final Map<String, Object> getFuel() { return LuaUtil.describe(be.getFuelStack()); }

	@LuaFunction(mainThread = true)
	public final Map<String, Object> getOutput() { return LuaUtil.describe(be.getOutputStack()); }

	@LuaFunction(mainThread = true)
	public final Map<String, Object> getItemDetail(int slot) throws LuaException {
		if (slot < 1 || slot > be.size()) throw new LuaException("Slot out of range (1-" + be.size() + ")");
		return LuaUtil.describe(be.getStack(slot - 1));
	}

	/** True when the output slot is a full stack (cooking will stall). */
	@LuaFunction(mainThread = true)
	public final boolean isOutputFull() {
		var out = be.getOutputStack();
		return !out.isEmpty() && out.getCount() >= out.getMaxCount();
	}

	/** True when the item in the fuel slot can be burned. */
	@LuaFunction(mainThread = true)
	public final boolean isFuel() {
		return AbstractFurnaceBlockEntity.canUseAsFuel(be.getFuelStack());
	}
}
