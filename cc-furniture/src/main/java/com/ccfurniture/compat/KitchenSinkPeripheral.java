package com.ccfurniture.compat;

import com.ccfurniture.blockentity.KitchenSinkBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

/** Peripheral type "kitchen_sink". */
public class KitchenSinkPeripheral implements IPeripheral {
	private final KitchenSinkBlockEntity be;

	public KitchenSinkPeripheral(KitchenSinkBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "kitchen_sink"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof KitchenSinkPeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	/** Turn the tap on or off. Players can only fill buckets/bottles and wash items while it is on. */
	@LuaFunction(mainThread = true)
	public final void setTap(boolean on) { be.setTap(on); }

	@LuaFunction(mainThread = true)
	public final boolean isTapOn() { return be.isTapOn(); }

	@LuaFunction(mainThread = true)
	public final void toggleTap() { be.setTap(!be.isTapOn()); }

	/** Total water dispensed, in millilitres. */
	@LuaFunction(mainThread = true)
	public final long getWaterUsed() { return be.getWaterUsedMl(); }

	@LuaFunction(mainThread = true)
	public final void resetWaterUsed() { be.addWaterUsed(-be.getWaterUsedMl()); }
}
