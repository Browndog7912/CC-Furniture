"""Assets for the Fan (static body; the blades are drawn and spun by a block entity renderer) and the Desk Lamp.
Called from gen_extra7.make()."""
import json, os
import gen_assets as g
import gen_extra as x

MOD = g.MOD
T = x.T
cube = x.cube


def textures():
    x.material("mat_fan_blade", (176, 196, 218), 121, 4, (214, 226, 240))
    x.material("mat_lamp_green", (24, 96, 70), 122, 4, (44, 128, 96))
    x.material("mat_bulb_on", (255, 236, 150), 123, 6, (255, 252, 214))
    x.material("mat_bulb_off", (226, 224, 208), 124, 3)
    x.material("mat_brass", (196, 150, 60), 125, 5, (230, 190, 92))


def fan_models():
    els = [
        cube([4, 0, 5], [12, 1.5, 13], "b"),                 # base
        cube([7.5, 1.5, 8.5], [8.5, 7.5, 9.5], "m"),         # neck
        cube([6, 7.5, 7.5], [10, 12.5, 11], "b"),            # motor housing
        cube([7, 9, 5.2], [9, 11, 7.5], "m"),                # hub (blades are spun by the renderer)
        # square cage around the blades
        cube([2, 15, 4.5], [14, 16, 7.5], "m"), cube([2, 4, 4.5], [14, 5, 7.5], "m"),
        cube([2, 5, 4.5], [3, 15, 7.5], "m"), cube([13, 5, 4.5], [14, 15, 7.5], "m"),
        cube([3, 9.5, 4.5], [13, 10.5, 5.2], "m"), cube([7.5, 5, 4.5], [8.5, 15, 5.2], "m"),
        cube([3, 6.7, 4.5], [13, 7.3, 5.2], "m"), cube([3, 12.7, 4.5], [13, 13.3, 5.2], "m"),
        cube([5, 5, 4.5], [5.6, 15, 5.2], "m"), cube([10.4, 5, 4.5], [11, 15, 5.2], "m"),
        cube([6.5, 11.5, 7.5], [9.5, 15, 8.2], "m"),         # arm up from the motor to the cage
        cube([7.2, 9, 11], [8.8, 10.6, 11.8], "k"),          # speed knob on the back
    ]
    tex = {"b": T("mat_black"), "m": T("mat_steel"), "k": T("mat_btn_red"), "blade": T("mat_fan_blade"),
           "particle": T("mat_steel")}
    x.block_model("fan", tex, els)
    # inventory icon: same body plus a static blade cross, so the item isn't bladeless
    x.block_model("fan_item", tex, els + [
        cube([7, 5.5, 5.8], [9, 14.5, 6.3], "blade"), cube([3.5, 9, 5.8], [12.5, 11, 6.3], "blade")])
    g.wj(os.path.join(g.ASSETS, "blockstates", "fan.json"), {"variants": {
        f"facing={f},on={o},powered={p}": {"model": f"{MOD}:block/fan", "y": r}
        for f, r in g.YROT.items() for o in ("true", "false") for p in ("true", "false")}})
    g.wj(os.path.join(g.ASSETS, "models", "item", "fan.json"), {"parent": f"{MOD}:block/fan_item"})


def lamp_models():
    tilt = {"origin": [8, 10, 5], "axis": "x", "angle": -22.5}
    variants = {}
    for on in (False, True):
        shade = cube([5, 7.5, 2.5], [11, 9.2, 7.5], "s", {"down": {"texture": "#glow"}})
        dome = cube([6, 9.2, 3.2], [10, 10.6, 6.8], "s")
        bulb = cube([7, 6.8, 3.8], [9, 7.5, 6.2], "glow")
        for e in (shade, dome, bulb):
            e["rotation"] = dict(tilt)
        els = [
            cube([5, 0, 7.5], [11, 1, 13.5], "b"),
            cube([7.6, 1, 10], [8.4, 10, 10.8], "m"),
            cube([7.3, 9.6, 9.7], [8.7, 10.9, 11.1], "m"),
            cube([7.6, 10.1, 4.5], [8.4, 10.9, 10.8], "m"),
            shade, dome, bulb,
        ]
        name = "desk_lamp_on" if on else "desk_lamp"
        x.block_model(name, {"b": T("mat_black"), "m": T("mat_brass"), "s": T("mat_lamp_green"),
                             "glow": T("mat_bulb_on" if on else "mat_bulb_off"), "particle": T("mat_brass")}, els)
        for f, r in g.YROT.items():
            for p in ("true", "false"):
                variants[f"facing={f},on={str(on).lower()},powered={p}"] = {"model": f"{MOD}:block/{name}", "y": r}
    g.wj(os.path.join(g.ASSETS, "blockstates", "desk_lamp.json"), {"variants": variants})
    x.item_model("desk_lamp")


def make_data():
    g.shaped("fan", [" B ", "BRB", "III"], {"B": "minecraft:iron_bars", "R": "minecraft:redstone", "I": "minecraft:iron_ingot"})
    g.shaped("desk_lamp", [" L ", " I ", "III"], {"L": "minecraft:redstone_lamp", "I": "minecraft:iron_ingot"})
    new = ["fan", "desk_lamp"]
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
    lang.update({"block.cc_furniture.fan": "Fan", "block.cc_furniture.desk_lamp": "Desk Lamp"})
    g.wj(path, lang)


def make():
    textures()
    fan_models()
    lamp_models()
    make_data()
    make_lang()
