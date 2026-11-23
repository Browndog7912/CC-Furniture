# CC: Furniture (Fabric 1.20.4)

Kitchen furniture where every block works on its own and can be driven from a CC: Tweaked computer.
CC: Tweaked is optional. Without it the blocks still work, there are just no peripherals.

## Blocks

| Block | In game | Peripheral type |
|---|---|---|
| Stove | Works exactly like a furnace (smelting). | `stove` |
| Oven | Furnace GUI, bakes food (smoking recipes, 2x speed). | `oven` |
| Fridge | 27-slot storage. Hoppers work. A computer can lock it. | `fridge` |
| Dishwasher | 9 slots + water tank (a water bucket fills it, 4 cycles). A cycle takes 5 s and strips dye from leather armor and shulker boxes and patterns from banners. Start: sneak + empty hand, a redstone pulse, or a computer. Right-click opens the GUI. | `dishwasher` |
| Kitchen Sink | Empty hand toggles the tap. With the tap on: bucket -> water bucket (infinite), bottle -> water bottle, and held dyed gear or banners are washed. | `kitchen_sink` |

| Microwave | 3D model. 5 slots. Click the **START button** on its front panel (or redstone / computer) to cook every food item inside with smoker recipes in 3 s. Click anywhere else to open it. | `microwave` |
| Coffee Maker | 3D model. Water bucket = water (4 brews), cocoa beans = coffee beans. Click the **brew button** on its head (or redstone / computer): 5 s, 2 cups. Empty hand takes a cup (or shows status). | `coffee_maker` |
| Plate | 3D model. Right-click with food to place it, empty hand to eat it, sneak + empty hand to take it back. | `plate` |
| Coffee (item) | 3D mug from the coffee maker. Drink it for Speed II + Haste for 90 s. | - |

The Dishwasher also has a **START button** on its front panel now (the sneak shortcut still works).

All blocks face the player when placed and are crafted from iron ingots plus a vanilla machine (see `data/cc_furniture/recipes`).

## Lua API

```lua
local stove = peripheral.find("stove")   -- also "oven"
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
