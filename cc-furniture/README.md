# CC: Furniture (Fabric 1.20.4)

Kitchen furniture where every block works on its own and can be driven from a CC: Tweaked computer.
CC: Tweaked is optional. Without it the blocks still work, there are just no peripherals.

## Blocks

| Block | In game | Peripheral type |
|---|---|---|
| Stove | Works exactly like a furnace (smelting). | `stove` |
| Oven | Furnace GUI, bakes food (smoking recipes, 2x speed). | `oven` |
| Grill | 3D model: coal tray, grate, knobs and a hinged lid that swings open while it burns. Works like the Oven (furnace GUI, smoking recipes, 2x speed) and glows at light level 15. | `grill` |
| Fridge | 27-slot storage. Hoppers work. A computer can lock it. | `fridge` |
| Dishwasher | 9 slots + water tank (a water bucket fills it, 4 cycles). A cycle takes 5 s and strips dye from leather armor and shulker boxes and patterns from banners. Start: sneak + empty hand, a redstone pulse, or a computer. Right-click opens the GUI. | `dishwasher` |
| Kitchen Sink | 3D model: cabinet with doors, counter with a basin, faucet with hot/cold knobs and running water. Empty hand toggles the tap. With the tap on: bucket -> water bucket (infinite), bottle -> water bottle, and held dyed gear or banners are washed. | `kitchen_sink` |

| Microwave | 3D model. 5 slots. Click the **START button** on its front panel (or redstone / computer) to cook every food item inside with smoker recipes in 3 s. Click anywhere else to open it. | `microwave` |
| Coffee Maker | 3D model. Water bucket = water (4 brews), cocoa beans = coffee beans. Click the **brew button** on its head (or redstone / computer): 5 s, 2 cups. Empty hand takes a cup (or shows status). | `coffee_maker` |
| Plate | 3D model. Right-click with food to place it, empty hand to eat it, sneak + empty hand to take it back. | `plate` |
| Coffee (item) | 3D mug from the coffee maker. Drink it for Speed II + Haste for 90 s. | - |

| Kitchen Hood | Decorative 3D hood. A screen on the front shows the in-game time (24h, blinking colon). | - |
| Toaster | 3D model, 2 slots. Right-click with a Bread Slice to load it (empty hand takes it back), click the **lever** on the front (or redstone / computer) to toast; the Toast pops out after 5 s. | `toaster` |
| Bread Slice / Toast (items) | Craft 1 bread into 4 bread slices. Toasting them makes Toast, which fills more hunger. | - |

| Blender | 3D model with a glass jar that fills up. Right-click with fruit (apple, sweet berries, glow berries, melon slice; 4 max), empty hand takes one back. Click the **button** on the base (or redstone / computer): 3 s, every 2 fruits make 1 Smoothie. | `blender` |
| Waffle Maker | 3D model with a hinged lid. Right-click with wheat (2 max) and eggs (2 max), empty hand takes them back. Click the **button** on the front (or redstone / computer): the lid closes, 6 s later each wheat + egg pair pops out as a Waffle. | `waffle_maker` |
| Kitchen Counter | 3D base unit with a drawer, two doors and a granite top (full height, flush with the stove and dishwasher). 9 slots. Hoppers work. | `kitchen_counter` |
| Kitchen Cabinet | 3D wall cabinet with double doors, 12 px deep so it sits flush against the wall. 18 slots. Hoppers work. | `kitchen_cabinet` |
| Mailbox | 3D post-mounted mailbox with a flag that goes up when mail arrives. Right-click opens a GUI with a text box for **this mailbox's unique address** (type it, press **Set**), a **Send to** text box, a 9-slot **Inbox** (take only) and a 9-slot **Outbox**. **Send** moves the whole outbox to the mailbox that owns that address, anywhere in the world (other dimensions too, even if its chunk is unloaded: mail waits and arrives when it loads). Mail is just items. Addresses are 1-24 characters of `a-z 0-9 _ . -` (case-insensitive) and can only belong to one mailbox. Breaking a mailbox frees its address; mail still queued for it is kept for the next mailbox that claims it. | `mailbox` |
| Collection Box | 3D blue drop box (domed top, drop slot, pull-down door, blank plate; no logos). **Send only**: no address and no inbox. Right-click holding an item to drop the stack through the slot, or empty hand / sneak to open the GUI, type a **Send to** address and press **Send** to mail all 9 slots to that mailbox. | `collection_box` |
| Fan | 3D desk fan: base, neck, motor, square cage and a red speed knob. The four blades are drawn by a block entity renderer and **really spin**: they ease up to speed when switched on and wind down when off. Right-click toggles; a redstone signal switches it on, losing the signal switches it off. | `fan` |
| Desk Lamp | 3D banker-style lamp: black base, brass arm, green tilted shade and a bulb that lights up. Right-click toggles; redstone switches it. Light level 14. | `desk_lamp` |
| Smoothie / Waffle (items) | Smoothie is a drink (Regeneration for 5 s). Waffle fills 5 hunger. | - |

The Dishwasher also has a **START button** on its front panel now (the sneak shortcut still works).

All blocks face the player when placed and are crafted from iron ingots plus a vanilla machine (see `data/cc_furniture/recipes`).

## Lua API

```lua
local stove = peripheral.find("stove")   -- also "oven" and "grill"
stove.isLit(); stove.getBurnTime(); stove.getCookTime(); stove.getCookTimeTotal(); stove.getProgress()
stove.getInput(); stove.getFuel(); stove.getOutput()   -- {name, displayName, count, maxCount} or nil
stove.size(); stove.list(); stove.getItemDetail(slot); stove.isOutputFull(); stove.isFuel()

local fridge = peripheral.find("fridge")
fridge.size(); fridge.list(); fridge.getItemDetail(slot); fridge.getItemCount()
fridge.getFreeSlots(); fridge.isFull(); fridge.isEmpty(); fridge.setLocked(true); fridge.isLocked()

local dw = peripheral.find("dishwasher")
dw.start(); dw.stop(); dw.isRunning(); dw.getProgress()   -- progress 0..1
dw.getWater(); dw.getMaxWater(); dw.size(); dw.list(); dw.getItemDetail(slot)

local sink = peripheral.find("kitchen_sink")
sink.setTap(true); sink.toggleTap(); sink.isTapOn(); sink.getWaterUsed(); sink.resetWaterUsed()   -- millilitres
```

```lua
local mw = peripheral.find("microwave")
mw.start(); mw.stop(); mw.isRunning(); mw.getProgress(); mw.size(); mw.list(); mw.getItemDetail(slot)

local cm = peripheral.find("coffee_maker")
cm.brew(); cm.isBrewing(); cm.getProgress(); cm.getWater(); cm.getBeans(); cm.getCups(); cm.getMaxCups(); cm.dispenseCup()

local plate = peripheral.find("plate")
plate.isEmpty(); plate.getItem(); plate.clear()
```

```lua
local t = peripheral.find("toaster")
t.start(); t.stop(); t.isToasting(); t.getProgress(); t.size(); t.list(); t.getItemDetail(slot)
```

```lua
local b = peripheral.find("blender")
b.start(); b.stop(); b.isRunning(); b.getProgress(); b.size(); b.list(); b.getItemDetail(slot)

local w = peripheral.find("waffle_maker")   -- slots 1-2 wheat, 3-4 eggs
w.start(); w.stop(); w.isCooking(); w.getProgress(); w.getBatches(); w.size(); w.list(); w.getItemDetail(slot)
```

```lua
local c = peripheral.find("kitchen_cabinet")   -- also "kitchen_counter"
c.size(); c.list(); c.getItemDetail(slot); c.getItemCount(); c.isEmpty()
```

```lua
local mb = peripheral.find("mailbox")   -- slots 1-9 inbox, 10-18 outbox
mb.getAddress(); mb.setAddress("home")   -- true, or false + "invalid" / "taken"
mb.send("shop")                          -- true, or false + "no_address" / "no_such" / "empty"
mb.hasMail(); mb.isFlagUp(); mb.size(); mb.list(); mb.getItemDetail(slot)
```

```lua
local cb = peripheral.find("collection_box")
cb.send("shop")   -- true, or false + "no_such" / "empty"
cb.size(); cb.list(); cb.getItemDetail(slot)
```

```lua
local f = peripheral.find("fan")   -- also "desk_lamp"
f.isOn(); f.setOn(true); f.toggle()
```

## Building

1. Install JDK 17.
2. `gradle wrapper --gradle-version 8.5` (once, if there is no `gradlew` yet), then `./gradlew build`.
3. The jar is in `build/libs/`.
4. `./gradlew runClient` starts a test client with CC: Tweaked included.

`gradle.properties` pins `cct_version`. If Gradle can't resolve it, pick a 1.20.4 build from
https://maven.squiddev.cc/cc/tweaked/ and update the property.

`python3 tools/gen_assets.py` regenerates all textures, models, blockstates, recipes, loot tables and lang
(needs Pillow). Edit the script to change the look or recipes.

## Notes
- Because these peripherals are custom `IPeripheral`s, CC's generic `inventory` methods (`pushItems`/`pullItems`)
  are not available on them. Use hoppers or pipes to move items in and out.
