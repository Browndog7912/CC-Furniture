"""Assets for the Mailbox (3D model, GUI texture). Called from gen_extra5.make()."""
import json, os
import gen_assets as g
import gen_extra as x
from PIL import Image, ImageDraw

MOD = g.MOD
T = x.T
cube = x.cube


def textures():
    x.material("mat_mail_blue", (46, 88, 172), 101, 5, (70, 112, 196))
    x.material("mat_mail_blue_dark", (34, 66, 140), 102, 4)
    x.material("mat_wood", (142, 102, 62), 103, 7, (110, 76, 44))
    # GUI: 176x204 panel on a 256x256 sheet, vanilla-style grey with inset slots
    im = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 175, 203], fill=(198, 198, 198, 255))
    d.rectangle([0, 0, 175, 203], outline=(30, 30, 30, 255))
    d.line([(1, 1), (174, 1)], fill=(255, 255, 255, 255))
    d.line([(1, 1), (1, 202)], fill=(255, 255, 255, 255))
    d.line([(174, 2), (174, 202)], fill=(85, 85, 85, 255))
    d.line([(2, 202), (174, 202)], fill=(85, 85, 85, 255))

    def slot(sx, sy):  # 18x18 inset around the item area at (sx+1, sy+1)
        d.rectangle([sx, sy, sx + 17, sy + 17], fill=(139, 139, 139, 255))
        d.line([(sx, sy), (sx + 17, sy)], fill=(55, 55, 55, 255))
        d.line([(sx, sy), (sx, sy + 17)], fill=(55, 55, 55, 255))
        d.line([(sx + 1, sy + 17), (sx + 17, sy + 17)], fill=(255, 255, 255, 255))
        d.line([(sx + 17, sy + 1), (sx + 17, sy + 17)], fill=(255, 255, 255, 255))

    for i in range(9):
        slot(7 + i * 18, 61)    # inbox
        slot(7 + i * 18, 91)    # outbox
        slot(7 + i * 18, 179)   # hotbar
        for r in range(3):
            slot(7 + i * 18, 121 + r * 18)
    d.line([(8, 57), (167, 57)], fill=(150, 150, 150, 255))
    d.line([(8, 87), (167, 87)], fill=(150, 150, 150, 255))
    os.makedirs(os.path.join(g.ASSETS, "textures", "gui"), exist_ok=True)
    im.save(os.path.join(g.ASSETS, "textures", "gui", "mailbox.png"))


def make_models():
    for flag in (False, True):
        els = [
            cube([5, 0, 5], [11, 1, 11], "w"),                        # foot
            cube([7, 1, 7], [9, 9, 9], "w"),                          # post
            cube([4, 9, 2], [12, 14, 14], "b"),                       # box
            cube([5, 14, 2], [11, 15, 14], "b"),                      # rounded top, stepped
            cube([6.5, 15, 3], [9.5, 16, 13], "b"),
            cube([4.5, 9.5, 1.5], [11.5, 14.5, 2], "d"),              # door
            cube([7.5, 12, 1], [8.5, 13, 1.5], "m"),                  # door knob
            cube([12, 10.3, 5], [12.8, 11.3, 5.8], "m"),              # flag pivot
        ]
        if flag:
            els.append(cube([12, 11, 5.2], [12.6, 15.6, 9.6], "r"))   # flag up
        else:
            els.append(cube([12, 9.6, 5.2], [12.6, 11, 9.6], "r"))    # flag down
        name = "mailbox_flag" if flag else "mailbox"
        x.block_model(name, {"b": T("mat_mail_blue"), "d": T("mat_mail_blue_dark"), "w": T("mat_wood"),
                             "m": T("mat_steel"), "r": T("mat_btn_red"), "particle": T("mat_mail_blue")}, els)
    variants = {}
    for f, r in g.YROT.items():
        variants[f"facing={f},flag=false"] = {"model": f"{MOD}:block/mailbox", "y": r}
        variants[f"facing={f},flag=true"] = {"model": f"{MOD}:block/mailbox_flag", "y": r}
    g.wj(os.path.join(g.ASSETS, "blockstates", "mailbox.json"), {"variants": variants})
    x.item_model("mailbox")


def make_data():
    g.shaped("mailbox", ["III", "ICI", " S "], {"I": "minecraft:iron_ingot", "C": "minecraft:chest", "S": "minecraft:stick"})
    g.wj(os.path.join(g.DATA, "loot_tables", "blocks", "mailbox.json"), {
        "type": "minecraft:block",
        "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
                   "entries": [{"type": "minecraft:item", "name": f"{MOD}:mailbox"}],
                   "conditions": [{"condition": "minecraft:survives_explosion"}]}],
    })
    tag = os.path.join(g.ROOT, "data", "minecraft", "tags", "blocks", "mineable", "pickaxe.json")
    vals = json.load(open(tag))["values"] + [f"{MOD}:mailbox"]
    g.wj(tag, {"replace": False, "values": vals})


def make_lang():
    path = os.path.join(g.ASSETS, "lang", "en_us.json")
    lang = json.load(open(path, encoding="utf-8"))
    lang.update({
        "block.cc_furniture.mailbox": "Mailbox",
        "container.cc_furniture.mailbox": "Mailbox",
        "gui.cc_furniture.mailbox.address": "Address:",
        "gui.cc_furniture.mailbox.send_to": "Send to:",
        "gui.cc_furniture.mailbox.set": "Set",
        "gui.cc_furniture.mailbox.send": "Send",
        "gui.cc_furniture.mailbox.inbox": "Inbox",
        "gui.cc_furniture.mailbox.outbox": "Outbox",
        "message.cc_furniture.mailbox_set": "Address set to \"%s\".",
        "message.cc_furniture.mailbox_invalid": "Addresses are 1-24 characters: letters, digits, _ . -",
        "message.cc_furniture.mailbox_taken": "That address is already taken.",
        "message.cc_furniture.mailbox_no_address": "Set your own address first.",
        "message.cc_furniture.mailbox_no_such": "No mailbox has that address.",
        "message.cc_furniture.mailbox_empty": "Put something in the outbox first.",
        "message.cc_furniture.mailbox_sent": "Mail sent!",
    })
    g.wj(path, lang)


def make():
    textures()
    make_models()
    make_data()
    make_lang()
    import gen_extra7
    gen_extra7.make()
