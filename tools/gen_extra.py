"""Assets for the Plate, Microwave, Coffee Maker and Coffee Mug (3D element models). Called from gen_assets.py."""
import json, os
import gen_assets as g
from PIL import ImageDraw

MOD = g.MOD
FACES = ("north", "east", "south", "west", "up", "down")


def cube(frm, to, tex, faces=None):
    el = {"from": frm, "to": to, "faces": {f: {"texture": "#" + tex} for f in FACES}}
    for f, v in (faces or {}).items():
        el["faces"][f].update(v)
    return el


def block_model(name, textures, elements):
    g.wj(os.path.join(g.ASSETS, "models", "block", name + ".json"),
         {"parent": "minecraft:block/block", "textures": textures, "elements": elements})


def item_model(name):
    g.wj(os.path.join(g.ASSETS, "models", "item", name + ".json"), {"parent": f"{MOD}:block/{name}"})


def T(name):
    return f"{MOD}:block/{name}"


# ------------------------------------------------------------------ textures
def material(name, base, seed, noise=5, speckle=None):
    im = g.tex(base, seed, noise)
    if speckle:
        px = im.load()
        import random
        r = random.Random(seed + 99)
        for _ in range(18):
            px[r.randint(0, 15), r.randint(0, 15)] = speckle + (255,)
    g.save(im, name)


def microwave_front(on):
    im = g.tex((196, 202, 208), 41, 4)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 13, 8], outline=(120, 126, 134))
    # door (viewer-left, columns 0-8): frame + window
    d.rectangle([0, 0, 8, 8], outline=(70, 74, 82))
    for y in range(1, 8):
        for x in range(1, 8):
            if on:
                d.point((x, y), fill=(255, 214, 110) if (x + y) % 5 else (255, 240, 170))
            else:
                d.point((x, y), fill=(26, 32, 40) if (x + y) % 5 else (44, 56, 70))
    if on:
        d.ellipse([2, 3, 6, 6], outline=(255, 160, 40))  # turntable glow
    # control panel (columns 9-13)
    d.rectangle([9, 0, 13, 8], fill=(40, 42, 48), outline=(30, 32, 36))
    d.rectangle([10, 1, 12, 2], fill=(20, 40, 24))
    d.point((10, 1), fill=(90, 255, 130) if on else (50, 90, 60))
    d.point((11, 1), fill=(90, 255, 130) if on else (50, 90, 60))
    d.point((12, 1), fill=(90, 255, 130) if on else (50, 90, 60))
    d.rectangle([10, 3, 12, 4], fill=(110, 116, 126))   # keypad
    d.rectangle([10, 5, 12, 7], fill=(80, 220, 110) if on else (210, 70, 60))  # START button
    g.save(im, "microwave_front_on" if on else "microwave_front")


def make_textures():
    material("mat_steel", (200, 205, 211), 51, 5)
    material("mat_black", (40, 42, 48), 52, 3)
    material("mat_ceramic", (246, 246, 242), 53, 2)
    material("mat_pot_glass", (170, 200, 216), 54, 4)
    material("mat_coffee", (76, 44, 28), 55, 5, (110, 70, 44))
    material("mat_btn_red", (210, 70, 60), 56, 3)
    material("mat_btn_green", (80, 220, 110), 57, 3)
    material("mat_food", (196, 122, 62), 58, 8, (150, 80, 40))
    material("mat_food2", (96, 164, 72), 59, 8, (60, 120, 50))
    microwave_front(False)
    microwave_front(True)


# ------------------------------------------------------------------ models
def make_models():
    # ---- plate
    plate = [
        cube([4, 0, 4], [12, 0.5, 12], "c"),
        cube([3, 0.5, 3], [13, 1, 4], "c"),
        cube([3, 0.5, 12], [13, 1, 13], "c"),
        cube([3, 0.5, 4], [4, 1, 12], "c"),
        cube([12, 0.5, 4], [13, 1, 12], "c"),
    ]
    tex = {"c": T("mat_ceramic"), "particle": T("mat_ceramic")}
    block_model("plate", tex, plate)
    meal = plate + [
        cube([5, 0.5, 5], [11, 2.5, 11], "f"),
        cube([6, 2.5, 6], [10, 3.5, 10], "f2"),
    ]
    block_model("plate_full", dict(tex, f=T("mat_food"), f2=T("mat_food2")), meal)
    item_model("plate")
    g.wj(os.path.join(g.ASSETS, "blockstates", "plate.json"), {"variants": {
        "full=false": {"model": f"{MOD}:block/plate"},
        "full=true": {"model": f"{MOD}:block/plate_full"},
    }})

    # ---- microwave (model faces NORTH; the front uses a 14x9 region of the front texture)
    for on in (False, True):
        els = [
            cube([1, 0, 3], [15, 9, 14], "s", {"north": {"texture": "#front", "uv": [0, 0, 14, 9]}}),
            cube([6.5, 1.5, 2], [7.5, 7.5, 3], "b"),          # door handle
            cube([2, 0, 4], [4, 0.5, 6], "b"), cube([12, 0, 4], [14, 0.5, 6], "b"),
            cube([2, 0, 11], [4, 0.5, 13], "b"), cube([12, 0, 11], [14, 0.5, 13], "b"),
            cube([3, 9, 5], [13, 9.4, 12], "b"),                # top vent plate
        ]
        block_model("microwave_on" if on else "microwave",
                    {"s": T("mat_steel"), "b": T("mat_black"),
                     "front": T("microwave_front_on" if on else "microwave_front"),
                     "particle": T("mat_steel")}, els)
    item_model("microwave")
    g.blockstate("microwave", "running")

    # ---- coffee maker
    for on in (False, True):
        els = [
            cube([2, 0, 2], [14, 1, 14], "b"),                     # base
            cube([3, 1, 9], [13, 15, 13], "b"),                    # tower
            cube([3, 11, 3], [13, 15, 9], "b"),                    # head
            cube([7, 10, 5], [9, 11, 7], "s"),                     # spout
            cube([5, 1, 4], [11, 8, 8], "pot"),                    # carafe
            cube([11, 2, 5.5], [12, 7, 6.5], "b"),                 # carafe handle
            cube([10, 12.5, 2.5], [12, 14, 3], "btn"),             # brew button
            cube([4, 13, 2.5], [6, 14, 3], "s"),                   # status strip
        ]
        block_model("coffee_maker_on" if on else "coffee_maker",
                    {"b": T("mat_black"), "s": T("mat_steel"),
                     "pot": T("mat_coffee" if on else "mat_pot_glass"),
                     "btn": T("mat_btn_green" if on else "mat_btn_red"),
                     "particle": T("mat_black")}, els)
    item_model("coffee_maker")
    g.blockstate("coffee_maker", "running")

    # ---- coffee mug item (3D)
    mug = [
        cube([5, 0, 5], [11, 6, 11], "c"),
        cube([5.5, 6, 5.5], [10.5, 6.2, 10.5], "cof"),
        cube([11, 1.5, 7], [13, 4.5, 9], "c"),
        cube([11, 1.5, 7], [12, 2.5, 9], "c"),
    ]
    g.wj(os.path.join(g.ASSETS, "models", "item", "coffee.json"), {
        "parent": "minecraft:block/block",
        "textures": {"c": T("mat_ceramic"), "cof": T("mat_coffee"), "particle": T("mat_ceramic")},
        "elements": mug,
        "display": {
            "gui": {"rotation": [30, 225, 0], "translation": [0, -1, 0], "scale": [1.6, 1.6, 1.6]},
            "ground": {"translation": [0, 2, 0], "scale": [0.8, 0.8, 0.8]},
            "fixed": {"rotation": [0, 180, 0], "scale": [1.2, 1.2, 1.2]},
            "thirdperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 2.5, 1], "scale": [0.9, 0.9, 0.9]},
            "firstperson_righthand": {"rotation": [0, -45, 0], "translation": [1.5, 2, 0], "scale": [1.0, 1.0, 1.0]},
        },
    })


# ------------------------------------------------------------------ data
def make_data():
    I = "minecraft:iron_ingot"
    g.shaped("microwave", ["III", "GRI", "IFI"],
             {"I": I, "G": "minecraft:glass_pane", "R": "minecraft:redstone", "F": "minecraft:furnace"})
    g.shaped("coffee_maker", ["III", "IGI", "IRI"],
             {"I": I, "G": "minecraft:glass_bottle", "R": "minecraft:redstone"})
    # plate: 4 per craft, needs a count, so write it by hand
    g.wj(os.path.join(g.DATA, "recipes", "plate.json"), {
        "type": "minecraft:crafting_shaped", "category": "misc",
        "pattern": ["C C", " C "], "key": {"C": {"item": "minecraft:clay_ball"}},
        "result": {"item": f"{MOD}:plate", "count": 4},
    })
    new = ["microwave", "coffee_maker", "plate"]
    for n in new:
        g.wj(os.path.join(g.DATA, "loot_tables", "blocks", n + ".json"), {
            "type": "minecraft:block",
            "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
                       "entries": [{"type": "minecraft:item", "name": f"{MOD}:{n}"}],
                       "conditions": [{"condition": "minecraft:survives_explosion"}]}],
        })
    tag = os.path.join(g.ROOT, "data", "minecraft", "tags", "blocks", "mineable", "pickaxe.json")
    g.wj(tag, {"replace": False, "values": [f"{MOD}:{n}" for n in g.NAMES + new]})


def make_lang():
    path = os.path.join(g.ASSETS, "lang", "en_us.json")
    lang = json.load(open(path, encoding="utf-8"))
    lang.update({
        "block.cc_furniture.microwave": "Microwave",
        "block.cc_furniture.coffee_maker": "Coffee Maker",
        "block.cc_furniture.plate": "Plate",
        "item.cc_furniture.coffee": "Coffee",
        "container.cc_furniture.microwave": "Microwave",
        "message.cc_furniture.not_hungry": "You're not hungry.",
        "message.cc_furniture.microwave_started": "Microwave started.",
        "message.cc_furniture.microwave_busy": "The microwave is already running.",
        "message.cc_furniture.microwave_nothing": "Put some food inside first.",
        "message.cc_furniture.coffee_started": "Brewing...",
        "message.cc_furniture.coffee_busy": "Already brewing.",
        "message.cc_furniture.coffee_no_water": "The coffee maker needs water (use a water bucket on it).",
        "message.cc_furniture.coffee_no_beans": "The coffee maker needs beans (use cocoa beans on it).",
        "message.cc_furniture.coffee_full": "Take some coffee out first.",
        "message.cc_furniture.coffee_water_full": "The water tank is full.",
        "message.cc_furniture.coffee_beans_full": "The bean hopper is full.",
        "message.cc_furniture.coffee_status": "Water %s/%s, beans %s, cups ready %s",
    })
    g.wj(path, lang)


def make():
    make_textures()
    make_models()
    make_data()
    make_lang()
