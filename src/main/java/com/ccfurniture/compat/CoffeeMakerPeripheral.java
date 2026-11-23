package com.ccfurniture.compat;

import com.ccfurniture.blockentity.CoffeeMakerBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

/** Peripheral type "coffee_maker". */
public class CoffeeMakerPeripheral implements IPeripheral {
	private final CoffeeMakerBlockEntity be;

	public CoffeeMakerPeripheral(CoffeeMakerBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "coffee_maker"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof CoffeeMakerPeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	/** Presses the brew button. Needs water, beans and room for 2 more cups. */
	@LuaFunction(mainThread = true)
	public final boolean brew() { return be.start(); }

	@LuaFunction(mainThread = true)
	public final boolean isBrewing() { return be.isBrewing(); }

	/** Brew progress from 0 to 1. */
	@LuaFunction(mainThread = true)
	public final double getProgress() {
		return be.isBrewing() ? Math.min(1.0, be.getProgress() / (double) CoffeeMakerBlockEntity.BREW_TICKS) : 0;
	}

	@LuaFunction(mainThread = true)
	public final int getWater() { return be.getWater(); }

	@LuaFunction(mainThread = true)
	public final int getBeans() { return be.getBeans(); }

	@LuaFunction(mainThread = true)
	public final int getCups() { return be.getCups(); }

	@LuaFunction(mainThread = true)
	public final int getMaxCups() { return CoffeeMakerBlockEntity.MAX_CUPS; }

	/** Drops one cup of coffee in front of the machine. Returns false if there is none. */
	@LuaFunction(mainThread = true)
	public final boolean dispenseCup() { return be.dispenseCup(); }
}
