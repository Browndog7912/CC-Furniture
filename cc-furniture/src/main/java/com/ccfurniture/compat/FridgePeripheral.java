package com.ccfurniture.compat;

import com.ccfurniture.blockentity.FridgeBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Peripheral type "fridge". */
public class FridgePeripheral implements IPeripheral {
	private final FridgeBlockEntity be;

	public FridgePeripheral(FridgeBlockEntity be) { this.be = be; }

	@Override
	public String getType() { return "fridge"; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof FridgePeripheral p && p.be == be;
	}

	@Nullable
	@Override
	public Object getTarget() { return be; }

	@LuaFunction(mainThread = true)
	public final int size() { return be.size(); }

	@LuaFunction(mainThread = true)
	public final Map<Integer, Object> list() { return LuaUtil.list(be); }

	@LuaFunction(mainThread = true)
	public final Map<String, Object> getItemDetail(int slot) throws LuaException {
		if (slot < 1 || slot > be.size()) throw new LuaException("Slot out of range (1-" + be.size() + ")");
		return LuaUtil.describe(be.getStack(slot - 1));
	}

	/** Total number of items stored. */
	@LuaFunction(mainThread = true)
	public final int getItemCount() {
		int n = 0;
		for (int i = 0; i < be.size(); i++) {
			ItemStack s = be.getStack(i);
			n += s.getCount();
		}
		return n;
	}

	@LuaFunction(mainThread = true)
	public final int getFreeSlots() {
		int n = 0;
		for (int i = 0; i < be.size(); i++) if (be.getStack(i).isEmpty()) n++;
		return n;
	}

	@LuaFunction(mainThread = true)
	public final boolean isFull() { return getFreeSlots() == 0; }

	@LuaFunction(mainThread = true)
	public final boolean isEmpty() { return be.isEmpty(); }

	/** Lock the door so players can't open it (hoppers still work). */
	@LuaFunction(mainThread = true)
	public final void setLocked(boolean locked) { be.setLocked(locked); }

	@LuaFunction(mainThread = true)
	public final boolean isLocked() { return be.isLocked(); }
}
