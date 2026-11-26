"""Assets for the Kitchen Counter and the Kitchen Cabinet (3D element models). Called from gen_extra3.make()."""
import json, os
import gen_assets as g
import gen_extra as x

MOD = g.MOD
T = x.T
cube = x.cube
SIDE = "cc_furniture:block/kitchen_sink_side"
UV_FULL = {"north": {"uv": [0, 0, 16, 16]}}


def counter_model():
    els = [
        cube([0, 0, 2], [16, 14, 16], "s", {"north": {"texture": "#b"}}),                 # carcass
        cube([0, 14, 0], [16, 16, 16], "c"),                                               # granite top
        cube([0.5, 9.5, 0.5], [15.5, 13.5, 2], "door", UV_FULL),                           # drawer front
        cube([0.5, 1, 0.5], [7.75, 9, 2], "door", UV_FULL),                                # doors
        cube([8.25, 1, 0.5], [15.5, 9, 2], "door", UV_FULL),
        cube([5.5, 11, 0], [10.5, 11.8, 0.5], "m"),                                        # handles
        cube([6.2, 4, 0], [6.9, 7, 0.5], "m"),
        cube([9.1, 4, 0], [9.8, 7, 0.5], "m"),
    ]
    x.block_model("kitchen_counter", {"s": SIDE, "door": T("sink_door"), "c": T("sink_counter"),
                                      "m": T("mat_steel"), "b": T("mat_black"), "particle": T("mat_steel")}, els)


def cabinet_model():
    els = [
        cube([0, 0, 6], [16, 16, 16], "s", {"north": {"texture": "#b"}}),                  # carcass
        cube([0, 0, 4], [16, 1.5, 16], "c"),                                               # bottom lip
        cube([0, 15, 4], [16, 16, 16], "c"),                                               # top crown
        cube([0.5, 1.5, 4.5], [7.75, 15, 6], "door", UV_FULL),                             # doors
        cube([8.25, 1.5, 4.5], [15.5, 15, 6], "door", UV_FULL),
        cube([6.5, 6, 3.8], [7.3, 10, 4.5], "m"),                                          # handles
        cube([8.7, 6, 3.8], [9.5, 10, 4.5], "m"),
    ]
    x.block_model("kitchen_cabinet", {"s": SIDE, "door": T("sink_door"), "c": T("sink_counter"),
                                      "m": T("mat_steel"), "b": T("mat_black"), "particle": T("mat_steel")}, els)


def make_models():
    counter_model()
    cabinet_model()
    for n in ("kitchen_counter", "kitchen_cabinet"):
        x.item_model(n)
        g.wj(os.path.join(g.ASSETS, "blockstates", n + ".json"), {"variants": {
            f"facing={f}": {"model": f"{MOD}:block/{n}", "y": r} for f, r in g.YROT.items()}})


def make_data():
    I = "minecraft:iron_ingot"
    g.shaped("kitchen_counter", ["SSS", "IBI"], {"S": "minecraft:stone_slab", "I": I, "B": "minecraft:barrel"})
    g.shaped("kitchen_cabinet", ["III", "ICI"], {"I": I, "C": "minecraft:chest"})
    new = ["kitchen_counter", "kitchen_cabinet"]
    for n in new:
        g.wj(os.path.join(g.DATA, "loot_tables", "blocks", n + ".json"), {
            "type": "minecraft:block",
            "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
                       "entries": [{"type": "minecraft:item", "name": f"{MOD}:{n}"}],
                       "conditions": [{"condition": "minecraft:survives_explosion"}]}],
        })
    tag = os.path.join(g.ROOT, "data", "minecraft", "tags", "blocks", "mineable", "pickaxe.json")
    vals = json.load(open(tag))["values"] + [f"{MOD}:{n}" for n in new]
    g.wj(tag, {"replace": False, "values": vals})


def make_lang():
    path = os.path.join(g.ASSETS, "lang", "en_us.json")
    lang = json.load(open(path, encoding="utf-8"))
    lang.update({
        "block.cc_furniture.kitchen_counter": "Kitchen Counter",
        "block.cc_furniture.kitchen_cabinet": "Kitchen Cabinet",
        "container.cc_furniture.kitchen_counter": "Kitchen Counter",
        "container.cc_furniture.kitchen_cabinet": "Kitchen Cabinet",
    })
    g.wj(path, lang)


def make():
    make_models()
    make_data()
    make_lang()
