"""Assets for the Collection Box (3D model, GUI texture). Called from gen_extra6.make()."""
import json, os
import gen_assets as g
import gen_extra as x
import gen_extra6 as m6
from PIL import Image, ImageDraw

MOD = g.MOD
T = x.T
cube = x.cube


def textures():
    x.material("mat_collect_blue", (24, 62, 150), 111, 4, (44, 90, 190))
    x.material("mat_collect_dark", (16, 44, 108), 112, 3)
    x.material("mat_plate", (232, 234, 238), 113, 2)   # blank pickup-times plate, no text or logo
    im = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    W, H = 176, 160
    d.rectangle([0, 0, W - 1, H - 1], fill=(198, 198, 198, 255), outline=(30, 30, 30, 255))
    d.line([(1, 1), (W - 2, 1)], fill=(255, 255, 255, 255))
    d.line([(1, 1), (1, H - 2)], fill=(255, 255, 255, 255))
    d.line([(W - 2, 2), (W - 2, H - 2)], fill=(85, 85, 85, 255))
    d.line([(2, H - 2), (W - 2, H - 2)], fill=(85, 85, 85, 255))

    def slot(sx, sy):
        d.rectangle([sx, sy, sx + 17, sy + 17], fill=(139, 139, 139, 255))
        d.line([(sx, sy), (sx + 17, sy)], fill=(55, 55, 55, 255))
        d.line([(sx, sy), (sx, sy + 17)], fill=(55, 55, 55, 255))
        d.line([(sx + 1, sy + 17), (sx + 17, sy + 17)], fill=(255, 255, 255, 255))
        d.line([(sx + 17, sy + 1), (sx + 17, sy + 17)], fill=(255, 255, 255, 255))

    for i in range(9):
        slot(7 + i * 18, 45)
        slot(7 + i * 18, 135)
        for r in range(3):
            slot(7 + i * 18, 77 + r * 18)
    d.line([(8, 33), (167, 33)], fill=(150, 150, 150, 255))
    os.makedirs(os.path.join(g.ASSETS, "textures", "gui"), exist_ok=True)
    im.save(os.path.join(g.ASSETS, "textures", "gui", "collection_box.png"))


def make_models():
    els = [
        cube([3, 0, 3], [13, 1, 13], "m"),                             # base plate
        cube([4, 1, 4], [12, 12, 12], "b"),                            # body
        cube([3.5, 12, 3.5], [12.5, 13.5, 12.5], "b"),                 # domed top, stepped
        cube([5, 13.5, 5], [11, 15, 11], "b"),
        cube([3.8, 11.5, 3.8], [12.2, 12.2, 12.2], "m"),               # steel band under the dome
        cube([4.5, 9.5, 3.2], [11.5, 11.5, 4], "d"),                   # drop-slot hood
        cube([5.5, 10, 3.1], [10.5, 10.8, 3.2], "k"),                  # the slot
        cube([4.5, 2.5, 3.5], [11.5, 8.5, 4], "d"),                    # pull-down door
        cube([6, 7.2, 3], [10, 8, 3.5], "m"),                          # door handle
        cube([6, 4.3, 3.4], [10, 6.2, 3.5], "p"),                      # blank times plate
    ]
    x.block_model("collection_box", {
        "b": T("mat_collect_blue"), "d": T("mat_collect_dark"), "m": T("mat_steel"),
        "k": T("mat_black"), "p": T("mat_plate"), "particle": T("mat_collect_blue")}, els)
    g.wj(os.path.join(g.ASSETS, "blockstates", "collection_box.json"), {"variants": {
        f"facing={f}": {"model": f"{MOD}:block/collection_box", "y": r} for f, r in g.YROT.items()}})
    x.item_model("collection_box")


def make_data():
    g.shaped("collection_box", ["III", "IHI", "III"], {"I": "minecraft:iron_ingot", "H": "minecraft:hopper"})
    g.wj(os.path.join(g.DATA, "loot_tables", "blocks", "collection_box.json"), {
        "type": "minecraft:block",
        "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
                   "entries": [{"type": "minecraft:item", "name": f"{MOD}:collection_box"}],
                   "conditions": [{"condition": "minecraft:survives_explosion"}]}],
    })
    tag = os.path.join(g.ROOT, "data", "minecraft", "tags", "blocks", "mineable", "pickaxe.json")
    vals = json.load(open(tag))["values"] + [f"{MOD}:collection_box"]
    g.wj(tag, {"replace": False, "values": vals})


def make_lang():
    path = os.path.join(g.ASSETS, "lang", "en_us.json")
    lang = json.load(open(path, encoding="utf-8"))
    lang.update({
        "block.cc_furniture.collection_box": "Collection Box",
        "container.cc_furniture.collection_box": "Collection Box",
        "gui.cc_furniture.collection_box.mail": "Mail",
        "message.cc_furniture.collection_full": "The collection box is full.",
    })
    g.wj(path, lang)


def make():
    textures()
    make_models()
    make_data()
    make_lang()
    import gen_extra8
    gen_extra8.make()
