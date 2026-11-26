"""Assets for the 3D Kitchen Sink, the Blender and the Waffle Maker (plus Smoothie and Waffle items).
Called from gen_extra2.make(), so it runs after (and overrides) the flat sink model from gen_assets.py."""
import json, os
import gen_assets as g
import gen_extra as x
from PIL import Image, ImageDraw

MOD = g.MOD
T = x.T
cube = x.cube
FACING_Y = g.YROT


# ------------------------------------------------------------------ textures
def textures():
    # sink
    im = g.tex((228, 231, 235), 71, 3)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=(170, 175, 182))
    d.rectangle([2, 2, 13, 13], outline=(196, 201, 208))
    g.save(im, "sink_door")
    im = g.tex((64, 68, 78), 72, 9)
    px = im.load()
    import random
    r = random.Random(73)
    for _ in range(26):
        px[r.randint(0, 15), r.randint(0, 15)] = (150, 156, 168, 255)
    g.save(im, "sink_counter")
    x.material("mat_water", (64, 128, 224), 74, 6, (150, 200, 255))
    x.material("mat_blue_knob", (60, 110, 230), 75, 3)

    # blender
    x.material("mat_smoothie", (210, 64, 110), 76, 8, (240, 130, 160))
    x.material("mat_smoothie_on", (236, 96, 140), 77, 12, (255, 190, 210))
    im = Image.new("RGBA", (16, 16))
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], fill=(190, 225, 245, 96))
    d.rectangle([0, 0, 15, 15], outline=(235, 248, 255, 170))
    g.save(im, "mat_glass")
    im = g.tex((36, 38, 44), 78, 3)
    d = ImageDraw.Draw(im)
    for px_ in (5, 6, 10, 11):  # speed marks, row 11 = model y 4..5
        d.point((px_, 11), fill=(150, 156, 166))
    d.point((8, 11), fill=(90, 200, 120))
    g.save(im, "blender_front")

    # waffle maker
    im = g.tex((176, 120, 56), 79, 5)
    d = ImageDraw.Draw(im)
    for i in range(0, 16, 4):
        d.line([(i, 0), (i, 15)], fill=(110, 68, 30))
        d.line([(0, i), (15, i)], fill=(110, 68, 30))
    g.save(im, "waffle_grid")
    x.material("mat_batter", (240, 224, 168), 80, 4, (255, 244, 205))
    x.material("mat_led_red", (230, 50, 40), 81, 2)
    x.material("mat_led_green", (70, 235, 110), 82, 2)

    # items
    item_dir = os.path.join(g.ASSETS, "textures", "item")
    os.makedirs(item_dir, exist_ok=True)
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.polygon([(4, 4), (12, 4), (11, 14), (5, 14)], fill=(200, 230, 245, 255), outline=(120, 150, 170, 255))
    d.polygon([(5, 6), (11, 6), (10.5, 13), (5.5, 13)], fill=(214, 70, 112, 255))
    d.line([(5, 8), (10, 8)], fill=(246, 150, 176, 255))
    d.line([(9, 1), (8, 7)], fill=(250, 230, 90, 255), width=1)  # straw
    d.line([(10, 1), (9, 7)], fill=(250, 230, 90, 255), width=1)
    im.save(os.path.join(item_dir, "smoothie.png"))
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.rectangle([2, 2, 13, 13], fill=(218, 160, 78, 255), outline=(150, 96, 40, 255))
    for i in (5, 8, 11):
        d.line([(i, 3), (i, 12)], fill=(160, 104, 46, 255))
        d.line([(3, i), (12, i)], fill=(160, 104, 46, 255))
    d.rectangle([6, 6, 9, 9], fill=(250, 222, 110, 255))  # butter
    im.save(os.path.join(item_dir, "waffle.png"))


# ------------------------------------------------------------------ models
def write_state(name, variants):
    g.wj(os.path.join(g.ASSETS, "blockstates", name + ".json"), {"variants": variants})


def sink_models():
    for on in (False, True):
        els = [
            # cabinet
            cube([0, 0, 2], [16, 9, 16], "s", {"north": {"texture": "#b"}}),
            cube([0.5, 1, 0.5], [7.75, 8.5, 2], "door", {"north": {"uv": [0, 0, 16, 16]}}),
            cube([8.25, 1, 0.5], [15.5, 8.5, 2], "door", {"north": {"uv": [0, 0, 16, 16]}}),
            cube([6.2, 4, 0], [6.9, 6.5, 0.5], "m"),   # door handles
            cube([9.1, 4, 0], [9.8, 6.5, 0.5], "m"),
            # counter top with a basin cut out
            cube([0, 9, 0], [16, 13, 3], "c", {"south": {"texture": "#m"}}),
            cube([0, 9, 12], [16, 13, 16], "c", {"north": {"texture": "#m"}}),
            cube([0, 9, 3], [3, 13, 12], "c", {"east": {"texture": "#m"}}),
            cube([13, 9, 3], [16, 13, 12], "c", {"west": {"texture": "#m"}}),
            cube([3, 9, 3], [13, 10, 12], "m"),         # basin floor
            cube([7, 10, 7], [9, 10.2, 9], "b"),        # drain
            # faucet
            cube([7, 13, 12.5], [9, 14, 14.5], "m"),
            cube([7.5, 14, 13], [8.5, 16, 14], "m"),
            cube([7.5, 15, 9], [8.5, 16, 14], "m"),
            cube([7.5, 14, 9], [8.5, 15, 10], "m"),
            cube([5.5, 13, 13], [6.5, 14.5, 14], "cold"),
            cube([9.5, 13, 13], [10.5, 14.5, 14], "hot"),
        ]
        if on:
            els.append(cube([7.7, 10.2, 9.2], [8.3, 14, 9.8], "w"))      # water stream
            els.append(cube([3.2, 10, 3.2], [12.8, 11.5, 11.8], "w"))    # water in the basin
        x.block_model("kitchen_sink_on" if on else "kitchen_sink", {
            "s": "cc_furniture:block/kitchen_sink_side", "door": T("sink_door"), "c": T("sink_counter"),
            "m": T("mat_steel"), "b": T("mat_black"), "w": T("mat_water"), "cold": T("mat_blue_knob"),
            "hot": T("mat_btn_red"), "particle": T("mat_steel")}, els)


def blender_models():
    variants = {}
    for on in (False, True):
        for level in range(5):
            h = 1.9 * level
            els = [
                cube([3.5, 0, 3.5], [12.5, 1, 12.5], "b"),
                cube([4, 1, 4], [12, 5, 12], "b", {"north": {"texture": "#front", "uv": [4, 11, 12, 15]}}),
                cube([5, 5, 5], [11, 6, 11], "s"),
                cube([6, 6, 7.5], [10, 6.4, 8.5], "s"),         # blades
                cube([7.5, 6, 6], [8.5, 6.4, 10], "s"),
                cube([6.5, 1.5, 3.5], [9.5, 3.5, 4], "btn"),     # power button
                cube([5, 6, 5], [11, 14.5, 11], "glass"),         # jar
                cube([5.5, 14.5, 5.5], [10.5, 15.5, 10.5], "b"),  # lid
                cube([7, 15.5, 7], [9, 16, 9], "b"),
                cube([11, 12, 7.5], [12.5, 13.2, 8.5], "b"),      # handle
                cube([12, 7, 7.5], [12.5, 13.2, 8.5], "b"),
                cube([11, 7, 7.5], [12.5, 8, 8.5], "b"),
            ]
            if level:
                els.insert(7, cube([5.5, 6.4, 5.5], [10.5, 6.4 + h, 10.5], "fruit"))
            name = f"blender_l{level}{'_on' if on else ''}"
            x.block_model(name, {
                "b": T("mat_black"), "s": T("mat_steel"), "front": T("blender_front"),
                "btn": T("mat_btn_green" if on else "mat_btn_red"), "glass": T("mat_glass"),
                "fruit": T("mat_smoothie_on" if on else "mat_smoothie"), "particle": T("mat_black")}, els)
            for f, r in FACING_Y.items():
                variants[f"facing={f},running={str(on).lower()},level={level}"] = {
                    "model": f"{MOD}:block/{name}", "y": r}
    write_state("blender", variants)
    g.wj(os.path.join(g.ASSETS, "models", "item", "blender.json"), {"parent": f"{MOD}:block/blender_l0"})


def waffle_models():
    rot = {"origin": [8, 3.5, 13], "axis": "x", "angle": 45}

    def build(name, opened, batter, on):
        els = [
            cube([2, 0, 3], [14, 2.5, 13], "b"),
            cube([3, 2.5, 4], [13, 3.5, 12], "s", {"up": {"texture": "#grid"}}),
            cube([10.5, 0.8, 2.5], [12.5, 2, 3], "btn"),
            cube([3.5, 1, 2.5], [4.5, 2, 3], "led"),
        ]
        lid = cube([2, 3.5, 3], [14, 5.5, 13], "s", {"down": {"texture": "#grid"}})
        handle = cube([5, 3.8, 1.5], [11, 5, 3], "b")
        if opened:
            lid["rotation"] = dict(rot)
            handle["rotation"] = dict(rot)
        els += [lid, handle]
        if opened and batter >= 1:
            els.append(cube([4, 3.5, 5], [7.5, 3.9, 11], "batter"))
        if opened and batter >= 2:
            els.append(cube([8.5, 3.5, 5], [12, 3.9, 11], "batter"))
        x.block_model(name, {
            "b": T("mat_black"), "s": T("mat_steel"), "grid": T("waffle_grid"), "batter": T("mat_batter"),
            "btn": T("mat_btn_green" if on else "mat_btn_red"),
            "led": T("mat_led_green" if on else "mat_led_red"), "particle": T("mat_black")}, els)

    for n in range(3):
        build(f"waffle_maker_open{n}", True, n, False)
    build("waffle_maker_closed", False, 0, True)
    variants = {}
    for f, r in FACING_Y.items():
        for n in range(3):
            variants[f"facing={f},running=false,batter={n}"] = {"model": f"{MOD}:block/waffle_maker_open{n}", "y": r}
            variants[f"facing={f},running=true,batter={n}"] = {"model": f"{MOD}:block/waffle_maker_closed", "y": r}
    write_state("waffle_maker", variants)
    g.wj(os.path.join(g.ASSETS, "models", "item", "waffle_maker.json"), {"parent": f"{MOD}:block/waffle_maker_open0"})


def make_models():
    sink_models()
    blender_models()
    waffle_models()
    for n in ("smoothie", "waffle"):
        g.wj(os.path.join(g.ASSETS, "models", "item", n + ".json"),
             {"parent": "minecraft:item/generated", "textures": {"layer0": f"{MOD}:item/{n}"}})


# ------------------------------------------------------------------ data
def make_data():
    I = "minecraft:iron_ingot"
    g.shaped("blender", [" G ", " G ", "IRI"], {"G": "minecraft:glass", "I": I, "R": "minecraft:redstone"})
    g.shaped("waffle_maker", ["III", "BRB", "III"], {"I": I, "B": "minecraft:iron_bars", "R": "minecraft:redstone"})
    new = ["blender", "waffle_maker"]
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
        "block.cc_furniture.blender": "Blender",
        "block.cc_furniture.waffle_maker": "Waffle Maker",
        "item.cc_furniture.smoothie": "Smoothie",
        "item.cc_furniture.waffle": "Waffle",
        "container.cc_furniture.blender": "Blender",
        "container.cc_furniture.waffle_maker": "Waffle Maker",
        "message.cc_furniture.blender_started": "Blending...",
        "message.cc_furniture.blender_busy": "The blender is already running.",
        "message.cc_furniture.blender_nothing": "Add at least 2 fruits first (apple, berries, melon slice).",
        "message.cc_furniture.blender_full": "The jar is full.",
        "message.cc_furniture.waffle_started": "Cooking waffles...",
        "message.cc_furniture.waffle_busy": "The waffle maker is already running.",
        "message.cc_furniture.waffle_nothing": "Add wheat and an egg first.",
        "message.cc_furniture.waffle_full": "No room for that (2 wheat + 2 eggs max).",
    })
    g.wj(path, lang)


def make():
    textures()
    make_models()
    make_data()
    make_lang()
    import gen_extra4
    gen_extra4.make()
