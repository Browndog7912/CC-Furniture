#!/usr/bin/env python3
"""Generates every texture/model/blockstate/recipe/loot/lang file for CC: Furniture.
Run from the project root:  python3 tools/gen_assets.py   (needs Pillow)"""
import json, os, random
from PIL import Image, ImageDraw

MOD = "cc_furniture"
ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources")
ASSETS = os.path.join(ROOT, "assets", MOD)
DATA = os.path.join(ROOT, "data", MOD)


def w(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)


def wj(path, obj):
    w(path, json.dumps(obj, indent=2) + "\n")


# ---------------------------------------------------------------- textures
def tex(base, seed, noise=6):
    rnd = random.Random(seed)
    im = Image.new("RGBA", (16, 16))
    px = im.load()
    for x in range(16):
        for y in range(16):
            n = rnd.randint(-noise, noise)
            px[x, y] = tuple(max(0, min(255, c + n)) for c in base) + (255,)
    return im


def border(im, col, light=None):
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=col)
    if light:
        d.line([(1, 1), (14, 1)], fill=light)
        d.line([(1, 1), (1, 14)], fill=light)


def save(im, name):
    os.makedirs(os.path.join(ASSETS, "textures", "block"), exist_ok=True)
    im.save(os.path.join(ASSETS, "textures", "block", name + ".png"))


STEEL = (196, 202, 208)
STEEL_DARK = (140, 146, 154)
WHITE = (236, 239, 242)
WHITE_DARK = (170, 175, 182)
BLACK = (24, 24, 28)


def steel_side(name, base=STEEL, edge=STEEL_DARK, seed=1):
    im = tex(base, seed)
    border(im, edge, tuple(min(255, c + 25) for c in base))
    save(im, name)


def stove_top(on):
    im = tex((48, 48, 54), 11, 4)
    border(im, (30, 30, 34))
    d = ImageDraw.Draw(im)
    for cx, cy in [(4, 4), (11, 4), (4, 11), (11, 11)]:
        ring = (235, 95, 25) if on else (110, 110, 118)
        d.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], outline=ring)
        d.point((cx, cy), fill=(255, 190, 60) if on else (70, 70, 78))
    save(im, "stove_top_on" if on else "stove_top")


def stove_front(on):
    im = tex(STEEL, 2)
    border(im, STEEL_DARK, (225, 230, 235))
    d = ImageDraw.Draw(im)
    for kx in (2, 5, 9, 12):
        d.rectangle([kx, 2, kx + 1, 3], fill=(40, 40, 46))
        d.point((kx, 2), fill=(255, 70, 40) if on else (150, 150, 158))
    d.rectangle([2, 5, 13, 14], outline=(60, 62, 68))
    d.line([(3, 6), (12, 6)], fill=(235, 238, 242))  # handle
    for y in range(8, 14):
        for x in range(3, 13):
            if on:
                t = (y - 8) / 6.0
                d.point((x, y), fill=(255, int(190 - 90 * t), int(60 - 30 * t)))
            else:
                d.point((x, y), fill=(22, 24, 30))
    save(im, "stove_front_on" if on else "stove_front")


def oven_front(on):
    im = tex((184, 190, 198), 3)
    border(im, STEEL_DARK, (215, 220, 226))
    d = ImageDraw.Draw(im)
    d.rectangle([2, 2, 13, 4], fill=(30, 32, 38))
    for x in (3, 4, 5):
        d.point((x, 3), fill=(60, 230, 110) if on else (50, 70, 55))
    d.point((11, 3), fill=(255, 80, 40) if on else (90, 60, 60))
    d.point((12, 3), fill=(255, 80, 40) if on else (90, 60, 60))
    d.rectangle([1, 6, 14, 14], outline=(70, 72, 80))
    d.line([(2, 7), (13, 7)], fill=(240, 242, 246))  # handle
    for y in range(9, 14):
        for x in range(3, 13):
            if on:
                t = (y - 9) / 5.0
                d.point((x, y), fill=(255, int(170 - 80 * t), int(40 - 20 * t)))
            else:
                d.point((x, y), fill=(18, 20, 26))
    save(im, "oven_front_on" if on else "oven_front")


def dishwasher_front(on):
    im = tex((222, 226, 231), 4)
    border(im, WHITE_DARK, (245, 247, 250))
    d = ImageDraw.Draw(im)
    d.rectangle([2, 2, 13, 4], fill=(60, 64, 72))
    d.point((3, 3), fill=(70, 230, 120) if on else (60, 90, 70))
    d.point((5, 3), fill=(255, 190, 60) if on else (95, 85, 60))
    d.rectangle([11, 2, 13, 4], fill=(80, 220, 110) if on else (210, 70, 60), outline=(90, 40, 40))  # start button
    d.line([(3, 6), (12, 6)], fill=(130, 136, 146))  # handle
    d.ellipse([4, 7, 11, 14], fill=(205, 210, 216), outline=(120, 126, 136))
    d.ellipse([5, 8, 10, 13], fill=(70, 150, 235) if on else (40, 48, 58))
    if on:
        d.point((7, 10), fill=(220, 240, 255))
        d.point((9, 12), fill=(220, 240, 255))
    save(im, "dishwasher_front_on" if on else "dishwasher_front")


def fridge_front():
    im = tex(WHITE, 5, 4)
    border(im, WHITE_DARK, (252, 252, 254))
    d = ImageDraw.Draw(im)
    d.line([(1, 6), (14, 6)], fill=(150, 156, 164))
    d.rectangle([12, 2, 12, 4], fill=(110, 116, 126))   # freezer handle
    d.rectangle([12, 8, 12, 12], fill=(110, 116, 126))  # fridge handle
    d.point((3, 3), fill=(110, 190, 245))
    d.point((4, 3), fill=(110, 190, 245))
    save(im, "fridge_front")


def sink_front():
    im = tex((228, 231, 235), 6, 4)
    border(im, WHITE_DARK, (248, 249, 251))
    d = ImageDraw.Draw(im)
    d.line([(8, 1), (8, 14)], fill=(150, 156, 164))
    d.rectangle([3, 3, 12, 13], outline=(190, 195, 202))
    d.rectangle([6, 3, 6, 5], fill=(110, 116, 126))
    d.rectangle([10, 3, 10, 5], fill=(110, 116, 126))
    save(im, "kitchen_sink_front")


def sink_top(on):
    im = tex(STEEL, 7, 4)
    border(im, STEEL_DARK, (225, 230, 235))
    d = ImageDraw.Draw(im)
    d.rectangle([3, 3, 12, 11], fill=(70, 120, 215) if on else (118, 126, 138),
                outline=(98, 106, 118))
    d.point((7, 9), fill=(28, 30, 36))
    d.point((8, 9), fill=(28, 30, 36))
    # tap at the back edge (image bottom = block back)
    d.rectangle([7, 13, 8, 14], fill=(235, 238, 242))
    d.point((7, 12), fill=(235, 238, 242))
    if on:
        d.point((7, 11), fill=(180, 215, 255))
        d.point((7, 10), fill=(180, 215, 255))
    save(im, "kitchen_sink_top_on" if on else "kitchen_sink_top")


def make_textures():
    steel_side("stove_side", seed=21)
    steel_side("oven_side", base=(184, 190, 198), seed=22)
    steel_side("oven_top", base=(176, 182, 190), seed=23)
    steel_side("dishwasher_side", base=(222, 226, 231), edge=WHITE_DARK, seed=24)
    steel_side("dishwasher_top", base=(214, 218, 224), edge=WHITE_DARK, seed=25)
    steel_side("fridge_side", base=WHITE, edge=WHITE_DARK, seed=26)
    steel_side("fridge_top", base=(228, 231, 235), edge=WHITE_DARK, seed=27)
    steel_side("kitchen_sink_side", base=(228, 231, 235), edge=WHITE_DARK, seed=28)
    for on in (False, True):
        stove_top(on); stove_front(on); oven_front(on); dishwasher_front(on); sink_top(on)
    fridge_front(); sink_front()
    # mod icon: the fridge front, scaled up
    icon = Image.open(os.path.join(ASSETS, "textures", "block", "fridge_front.png")).resize((128, 128), Image.NEAREST)
    icon.save(os.path.join(ASSETS, "icon.png"))


# ---------------------------------------------------------------- models / blockstates
YROT = {"north": 0, "east": 90, "south": 180, "west": 270}


def model(name, front, side, top):
    wj(os.path.join(ASSETS, "models", "block", name + ".json"), {
        "parent": "minecraft:block/orientable",
        "textures": {"front": f"{MOD}:block/{front}", "side": f"{MOD}:block/{side}", "top": f"{MOD}:block/{top}"},
    })
    wj(os.path.join(ASSETS, "models", "item", name + ".json"), {"parent": f"{MOD}:block/{name}"}) if not name.endswith("_on") else None


def blockstate(name, prop=None):
    variants = {}
    for facing, rot in YROT.items():
        if prop is None:
            variants[f"facing={facing}"] = {"model": f"{MOD}:block/{name}", "y": rot}
        else:
            for val in ("false", "true"):
                m = f"{MOD}:block/{name}" + ("_on" if val == "true" else "")
                variants[f"facing={facing},{prop}={val}"] = {"model": m, "y": rot}
    wj(os.path.join(ASSETS, "blockstates", name + ".json"), {"variants": variants})


def make_models():
    model("stove", "stove_front", "stove_side", "stove_top")
    model("stove_on", "stove_front_on", "stove_side", "stove_top_on")
    model("oven", "oven_front", "oven_side", "oven_top")
    model("oven_on", "oven_front_on", "oven_side", "oven_top")
    model("dishwasher", "dishwasher_front", "dishwasher_side", "dishwasher_top")
    model("dishwasher_on", "dishwasher_front_on", "dishwasher_side", "dishwasher_top")
    model("kitchen_sink", "kitchen_sink_front", "kitchen_sink_side", "kitchen_sink_top")
    model("kitchen_sink_on", "kitchen_sink_front", "kitchen_sink_side", "kitchen_sink_top_on")
    model("fridge", "fridge_front", "fridge_side", "fridge_top")
    blockstate("stove", "lit")
    blockstate("oven", "lit")
    blockstate("dishwasher", "running")
    blockstate("kitchen_sink", "running")
    blockstate("fridge")


# ---------------------------------------------------------------- data
NAMES = ["stove", "oven", "dishwasher", "kitchen_sink", "fridge"]


def shaped(name, pattern, key):
    wj(os.path.join(DATA, "recipes", name + ".json"), {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "pattern": pattern,
        "key": {k: {"item": v} for k, v in key.items()},
        "result": {"item": f"{MOD}:{name}"},
    })


def make_data():
    I = "minecraft:iron_ingot"
    shaped("stove", ["III", "IFI", "III"], {"I": I, "F": "minecraft:furnace"})
    shaped("oven", ["III", "IGI", "ISI"], {"I": I, "G": "minecraft:glass_pane", "S": "minecraft:smoker"})
    shaped("dishwasher", ["III", "IDI", "ICI"], {"I": I, "D": "minecraft:dispenser", "C": "minecraft:cauldron"})
    shaped("kitchen_sink", [" I ", "ICI", "SSS"], {"I": I, "C": "minecraft:cauldron", "S": "minecraft:stone_slab"})
    shaped("fridge", ["III", "ICI", "IPI"], {"I": I, "C": "minecraft:chest", "P": "minecraft:packed_ice"})
    for n in NAMES:
        wj(os.path.join(DATA, "loot_tables", "blocks", n + ".json"), {
            "type": "minecraft:block",
            "pools": [{
                "rolls": 1.0,
                "bonus_rolls": 0.0,
                "entries": [{"type": "minecraft:item", "name": f"{MOD}:{n}"}],
                "conditions": [{"condition": "minecraft:survives_explosion"}],
            }],
        })
    wj(os.path.join(ROOT, "data", "minecraft", "tags", "blocks", "mineable", "pickaxe.json"),
       {"replace": False, "values": [f"{MOD}:{n}" for n in NAMES]})


def make_lang():
    wj(os.path.join(ASSETS, "lang", "en_us.json"), {
        "itemGroup.cc_furniture.main": "CC: Furniture",
        "block.cc_furniture.stove": "Stove",
        "block.cc_furniture.oven": "Oven",
        "block.cc_furniture.dishwasher": "Dishwasher",
        "block.cc_furniture.kitchen_sink": "Kitchen Sink",
        "block.cc_furniture.fridge": "Fridge",
        "container.cc_furniture.stove": "Stove",
        "container.cc_furniture.oven": "Oven",
        "container.cc_furniture.dishwasher": "Dishwasher",
        "container.cc_furniture.fridge": "Fridge",
        "message.cc_furniture.fridge_locked": "The fridge is locked.",
        "message.cc_furniture.dishwasher_filled": "Dishwasher tank filled.",
        "message.cc_furniture.dishwasher_full": "The dishwasher tank is already full.",
        "message.cc_furniture.dishwasher_busy": "The dishwasher is already running.",
        "message.cc_furniture.dishwasher_no_water": "The dishwasher needs water (use a water bucket on it).",
        "message.cc_furniture.dishwasher_empty": "Put something in the dishwasher first.",
        "message.cc_furniture.dishwasher_started": "Wash cycle started.",
        "message.cc_furniture.tap_on": "Tap on",
        "message.cc_furniture.tap_off": "Tap off",
        "message.cc_furniture.tap_needed": "Turn the tap on first (empty hand).",
    })


if __name__ == "__main__":
    make_textures()
    make_models()
    make_data()
    make_lang()
    import sys
    sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
    import gen_extra
    gen_extra.make()
    print("assets generated")
