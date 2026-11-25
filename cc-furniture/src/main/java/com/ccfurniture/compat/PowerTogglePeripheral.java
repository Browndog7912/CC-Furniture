package com.ccfurniture.compat;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.BooleanProperty;
import org.jetbrains.annotations.Nullable;

/** Peripheral for simple on/off furniture: "fan" and "desk_lamp". */
public class PowerTogglePeripheral implements IPeripheral {
	private final String type;
	private final BlockEntity be;
	private final BooleanProperty on;

	public PowerTogglePeripheral(String type, BlockEntity be, BooleanProperty on) {
		this.type = type;
		this.be = be;
		this.on = on;
	}

	@Override
	public String getType() { return type; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof PowerTogglePeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	@LuaFunction(mainThread = true)
	public final boolean isOn() { return be.getCachedState().get(on); }

	/** Switches it on or off. Note a redstone signal also switches it. */
	@LuaFunction(mainThread = true)
	public final void setOn(boolean value) {
		BlockState s = be.getCachedState();
		if (be.getWorld() != null && s.get(on) != value) {
			be.getWorld().setBlockState(be.getPos(), s.with(on, value), 3);
		}
	}

	@LuaFunction(mainThread = true)
	public final boolean toggle() {
		setOn(!isOn());
		return isOn();
	}
}
