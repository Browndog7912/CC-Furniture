package com.ccfurniture.compat;

import com.ccfurniture.blockentity.PlateBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.ItemScatterer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "plate". */
public class PlatePeripheral implements IPeripheral {
	private final PlateBlockEntity be;

	public PlatePeripheral(PlateBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "plate"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof PlatePeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	@LuaFunction(mainThread = true)
	public final boolean isEmpty() { return !be.hasFood(); }

	/** The food on the plate as {name, displayName, count, maxCount}, or nil. */
	@LuaFunction(mainThread = true)
	public final Map<String, Object> getItem() { return LuaUtil.describe(be.getFood()); }

	/** Tips the food off the plate onto the ground. Returns false if it was empty. */
	@LuaFunction(mainThread = true)
	public final boolean clear() {
		if (!be.hasFood() || be.getWorld() == null) return false;
		var pos = be.getPos();
		ItemScatterer.spawn(be.getWorld(), pos.getX(), pos.getY() + 1, pos.getZ(), be.takeFood());
		return true;
	}
}
