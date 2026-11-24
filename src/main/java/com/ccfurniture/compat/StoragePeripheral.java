package com.ccfurniture.compat;

import com.ccfurniture.blockentity.SimpleContainerBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Read-only peripheral for the storage furniture ("kitchen_counter", "kitchen_cabinet"). */
public class StoragePeripheral implements IPeripheral {
	private final String type;
	private final SimpleContainerBlockEntity be;

	public StoragePeripheral(String type, SimpleContainerBlockEntity be) {
		this.type = type;
		this.be = be;
	}

	@Override
	public String getType() { return type; }

	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return other instanceof StoragePeripheral p && p.be == be;
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
	public final boolean isEmpty() { return be.isEmpty(); }
}
