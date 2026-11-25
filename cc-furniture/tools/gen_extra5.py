"""Assets for the Grill (3D element model, lit / unlit). Called from gen_extra4.make()."""
import json, os
import gen_assets as g
import gen_extra as x

MOD = g.MOD
T = x.T
cube = x.cube


def textures():
    x.material("mat_ember", (255, 120, 30), 91, 14, (255, 210, 80))
    x.material("mat_charcoal", (36, 34, 36), 92, 6, (90, 88, 92))


def make_models():
    hinge = {"origin": [8, 7, 14], "axis": "x", "angle": 45}
    variants = {}
    for lit in (False, True):
        els = [
            # legs
            cube([1.5, 0, 2.5], [3, 3, 4], "b"), cube([13, 0, 2.5], [14.5, 3, 4], "b"),
            cube([1.5, 0, 12], [3, 3, 13.5], "b"), cube([13, 0, 12], [14.5, 3, 13.5], "b"),
            # firebox: floor, four walls
            cube([2, 3, 3], [14, 4, 13], "s"),
            cube([1, 3, 2], [15, 7, 3], "s"),
            cube([1, 4, 13], [15, 7, 14], "s"),
            cube([1, 4, 3], [2, 7, 13], "s"),
            cube([14, 4, 3], [15, 7, 13], "s"),
            # coals
            cube([2, 4, 3], [14, 5.5, 13], "coal"),
            # control knobs on the front
            cube([3, 4.5, 1.2], [4.5, 6, 2], "b"),
            cube([7.25, 4.5, 1.2], [8.75, 6, 2], "b"),
            cube([11.5, 4.5, 1.2], [13, 6, 2], "b"),
        ]
        # grate bars
        for i in range(7):
            z = 3.4 + i * 1.5
            els.append(cube([2, 6, z], [14, 6.6, z + 0.6], "grate"))
        lid = cube([1, 7, 2], [15, 9.5, 14], "b", {"up": {"texture": "#s"}})
        handle = cube([4, 8, 0.6], [12, 9, 2], "s")
        if lit:
            lid["rotation"] = dict(hinge)
            handle["rotation"] = dict(hinge)
        els += [lid, handle]
        name = "grill_on" if lit else "grill"
        x.block_model(name, {"b": T("mat_black"), "s": T("mat_steel"), "grate": T("mat_black"),
                             "coal": T("mat_ember" if lit else "mat_charcoal"), "particle": T("mat_steel")}, els)
        for f, r in g.YROT.items():
            variants[f"facing={f},lit={str(lit).lower()}"] = {"model": f"{MOD}:block/{name}", "y": r}
    g.wj(os.path.join(g.ASSETS, "blockstates", "grill.json"), {"variants": variants})
    x.item_model("grill")


def make_data():
    g.shaped("grill", ["BBB", "ICI", "I I"], {"B": "minecraft:iron_bars", "I": "minecraft:iron_ingot", "C": "minecraft:campfire"})
    g.wj(os.path.join(g.DATA, "loot_tables", "blocks", "grill.json"), {
        "type": "minecraft:block",
        "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
                   "entries": [{"type": "minecraft:item", "name": f"{MOD}:grill"}],
                   "conditions": [{"condition": "minecraft:survives_explosion"}]}],
    })
    tag = os.path.join(g.ROOT, "data", "minecraft", "tags", "blocks", "mineable", "pickaxe.json")
    vals = json.load(open(tag))["values"] + [f"{MOD}:grill"]
    g.wj(tag, {"replace": False, "values": vals})


def make_lang():
    path = os.path.join(g.ASSETS, "lang", "en_us.json")
    lang = json.load(open(path, encoding="utf-8"))
    lang.update({"block.cc_furniture.grill": "Grill", "container.cc_furniture.grill": "Grill"})
    g.wj(path, lang)


def make():
    textures()
    make_models()
    make_data()
    make_lang()
    import gen_extra6
    gen_extra6.make()
