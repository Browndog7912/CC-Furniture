"""Assets for the Kitchen Hood, Toaster, Bread Slice and Toast. Called from gen_extra.py."""
import json, os
import gen_assets as g
import gen_extra as x
from PIL import Image, ImageDraw

MOD = g.MOD
T = x.T
cube = x.cube


# ------------------------------------------------------------------ textures
def hood_textures():
    # vent grille underneath
    im = g.tex((52, 54, 60), 61, 3)
    d = ImageDraw.Draw(im)
    for yy in range(1, 16, 3):
        d.line([(1, yy), (14, yy)], fill=(20, 21, 25))
    d.rectangle([0, 0, 15, 15], outline=(120, 126, 134))
    g.save(im, "hood_vent")
    # clock screen (the time itself is drawn by the block entity renderer)
    im = g.tex((12, 20, 16), 62, 2)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=(40, 60, 48))
    g.save(im, "hood_screen")


def toaster_top(on):
    im = g.tex((200, 205, 211), 63, 4)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=(150, 156, 164))
    for z0 in (5, 9):  # the model's top face maps x 2..14, z 4..12 straight onto texture pixels
        d.rectangle([4, z0, 11, z0 + 1], fill=(255, 150, 40) if on else (24, 24, 28))
    g.save(im, "toaster_top_on" if on else "toaster_top")


def item_sprite(name, crust, crumb):
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for off, col in ((0, crust), (1, crumb)):
        d.rectangle([3 + off, 7, 12 - off, 14 - off], fill=col)
        d.ellipse([2 + off, 2 + off, 8, 8], fill=col)
        d.ellipse([7, 2 + off, 13 - off, 8], fill=col)
    os.makedirs(os.path.join(g.ASSETS, "textures", "item"), exist_ok=True)
    im.save(os.path.join(g.ASSETS, "textures", "item", name + ".png"))


def make_textures():
    x.material("mat_bread", (236, 206, 150), 64, 5, (210, 175, 115))
    x.material("mat_toast", (190, 128, 62), 65, 6, (150, 92, 40))
    hood_textures()
    toaster_top(False)
    toaster_top(True)
    item_sprite("bread_slice", (200, 140, 70), (242, 218, 164))
    item_sprite("toast", (140, 84, 36), (214, 160, 84))


# ------------------------------------------------------------------ models
def make_models():
    # ---- kitchen hood (front faces NORTH; the clock screen is on the canopy's front)
    els = [
        cube([0, 0, 1], [16, 4, 16], "s", {"down": {"texture": "#vent"}}),   # canopy
        cube([2, 4, 3], [14, 8, 16], "s"),                                    # taper
        cube([4, 8, 5], [12, 16, 16], "s"),                                   # chimney
        cube([3, 0.5, 0.5], [13, 3.5, 1], "b", {"north": {"texture": "#screen"}}),  # clock screen plate
        cube([1, 0, 2], [15, 0.4, 3], "b"),                                   # lower trim
    ]
    x.block_model("kitchen_hood", {"s": T("mat_steel"), "b": T("mat_black"), "vent": T("hood_vent"),
                                   "screen": T("hood_screen"), "particle": T("mat_steel")}, els)
    x.item_model("kitchen_hood")
    g.wj(os.path.join(g.ASSETS, "blockstates", "kitchen_hood.json"), {"variants": {
        f"facing={f}": {"model": f"{MOD}:block/kitchen_hood", "y": r} for f, r in g.YROT.items()}})

    # ---- toaster (front faces NORTH, long axis along x)
    variants = {}
    for on in (False, True):
        for n in (0, 1, 2):
            els = [
                cube([2, 0, 4], [14, 1, 12], "b"),                                   # base
                cube([2, 1, 4], [14, 7, 12], "s", {"up": {"texture": "#top"}}),     # chrome body
                cube([11, 3, 3.5], [13, 5, 4], "btn"),                              # toast lever
                cube([7, 0.5, 12], [9, 1.5, 14], "b"),                              # power cord
            ]
            if n >= 1:
                els.append(cube([4.5, 7, 5.2], [11.5, 9.5, 6.8], "bread"))
            if n >= 2:
                els.append(cube([4.5, 7, 9.2], [11.5, 9.5, 10.8], "bread"))
            name = f"toaster{'_on' if on else ''}_s{n}"
            x.block_model(name, {"s": T("mat_steel"), "b": T("mat_black"),
                                 "top": T("toaster_top_on" if on else "toaster_top"),
                                 "btn": T("mat_btn_green" if on else "mat_btn_red"),
                                 "bread": T("mat_toast" if on else "mat_bread"),
                                 "particle": T("mat_steel")}, els)
            for f, r in g.YROT.items():
                variants[f"facing={f},running={str(on).lower()},slices={n}"] = {
                    "model": f"{MOD}:block/{name}", "y": r}
    g.wj(os.path.join(g.ASSETS, "blockstates", "toaster.json"), {"variants": variants})
    g.wj(os.path.join(g.ASSETS, "models", "item", "toaster.json"), {"parent": f"{MOD}:block/toaster_s0"})

    # ---- flat items
    for n in ("bread_slice", "toast"):
        g.wj(os.path.join(g.ASSETS, "models", "item", n + ".json"),
             {"parent": "minecraft:item/generated", "textures": {"layer0": f"{MOD}:item/{n}"}})


# ------------------------------------------------------------------ data
def make_data():
    I = "minecraft:iron_ingot"
    g.shaped("kitchen_hood", [" I ", "ICI", "BBB"],
             {"I": I, "C": "minecraft:clock", "B": "minecraft:iron_bars"})
    g.shaped("toaster", ["IRI", "III"], {"I": I, "R": "minecraft:redstone"})
    g.wj(os.path.join(g.DATA, "recipes", "bread_slice.json"), {
        "type": "minecraft:crafting_shapeless", "category": "food",
        "ingredients": [{"item": "minecraft:bread"}],
        "result": {"item": f"{MOD}:bread_slice", "count": 4},
    })
    new = ["kitchen_hood", "toaster"]
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
        "block.cc_furniture.kitchen_hood": "Kitchen Hood",
        "block.cc_furniture.toaster": "Toaster",
        "item.cc_furniture.bread_slice": "Bread Slice",
        "item.cc_furniture.toast": "Toast",
        "container.cc_furniture.toaster": "Toaster",
        "message.cc_furniture.toaster_started": "Toasting...",
        "message.cc_furniture.toaster_busy": "The toaster is already running.",
        "message.cc_furniture.toaster_nothing": "Put a bread slice in first.",
        "message.cc_furniture.toaster_full": "Both slots are full.",
    })
    g.wj(path, lang)


def make():
    make_textures()
    make_models()
    make_data()
    make_lang()
    import gen_extra3
    gen_extra3.make()
