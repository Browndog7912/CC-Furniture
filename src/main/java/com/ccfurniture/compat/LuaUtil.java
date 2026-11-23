package com.ccfurniture.compat;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/** Converts item stacks into plain Lua-friendly tables. */
final class LuaUtil {
	@Nullable
	static Map<String, Object> describe(ItemStack stack) {
		if (stack.isEmpty()) return null;
		Map<String, Object> m = new HashMap<>();
		m.put("name", Registries.ITEM.getId(stack.getItem()).toString());
		m.put("displayName", stack.getName().getString());
		m.put("count", stack.getCount());
		m.put("maxCount", stack.getMaxCount());
		return m;
	}

	/** slot -> {name, count} for all non-empty slots (1-based, like CC's inventory peripheral). */
	static Map<Integer, Object> list(Inventory inv) {
		Map<Integer, Object> out = new HashMap<>();
		for (int i = 0; i < inv.size(); i++) {
			Map<String, Object> d = describe(inv.getStack(i));
			if (d != null) {
				Map<String, Object> brief = new HashMap<>();
				brief.put("name", d.get("name"));
				brief.put("count", d.get("count"));
				out.put(i + 1, brief);
			}
		}
		return out;
	}

	private LuaUtil() {}
}
