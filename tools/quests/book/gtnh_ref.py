"""Reference-only chapters: GT New Horizons' own Stone, Steam and LV tabs rebuilt in FTB Quests at their exact
positions, with their lines and titles, so the layout can be compared and copied in game. Tasks are checkmarks.
Where one of our quests covers the same step, the description links to it.

GTNH quest book (c) the GTNH team, CC BY-NC-SA 4.0. NOT FOR RELEASE: delete the gtnh_* chapters before shipping."""
import re, sys
sys.path.insert(0, '/home/claude/gtnh_study')
import study
from fw import Chapter, exists

TABS = [('Tier0StoneAge', 'gtnh_stone', 'Stone Age', 'minecraft:flint'),
        ('Tier05Steam', 'gtnh_steam', 'Steam', 'gtceu:lp_steam_solid_boiler'),
        ('Tier1LV', 'gtnh_lv', 'LV', 'gtceu:lv_machine_hull')]
OURS = {
    # Stone
    'Getting Iron': 'stone/iron', 'Copper Ore': 'stone/copper_tin', 'Copper': 'stone/copper_tin', 'Tin': 'stone/copper_tin',
    'Making Bronze': 'stone/bronze', 'How the Heck Do I Find Ores?': 'stone/veins', 'Clay: The Gathering': 'stone/fire',
    'Automated Coke Oven': 'stone/coke_oven', 'Creosote': 'stone/creosote', 'Important Tools': 'stone/tools',
    'A Better Furnace!': 'stone/iron_furnace', 'Better Storage': 'stone/chests', 'Digging Deeper': 'stone/deep',
    'Getting (Char)coal': 'stone/coal', 'Macerator v0.1 Alpha': 'stone/mortar', 'When All You Have is a Hammer...': 'stone/plates',
    'GT6-Styled Pipes': 'stone/pipe', 'Ready, Set, Go!': 'stone/begin', 'You Shall Proceed': 'stone/to_steam',
    # Steam
    'Welcome to Tier 0.5, Steam!': 'steam/boiler', 'The Power of the Sun': 'steam/solar', 'Blastoff!': 'steam/pbf',
    'Better Than Iron': 'steam/steel', 'Basic Crafting: Machine Hulls': 'steam/lv_casing', 'Molds, Molds, Molds': 'steam/mold',
    'Circuit Boards': 'steam/board', 'Hammer time v2': 'steam/hammer', 'THE NETHER': 'steam/nether',
    'Your First Electronic Circuit': 'steam/circuit', 'Resistor': 'steam/resistor', 'GT6-Styled Wires and Cables': 'steam/cable',
    'Macerator v1.0!': 'steam/macerator', 'Extracting Stuff': 'steam/extractor', 'How to Make Rubber': 'steam/rubber',
    'Basic Crafting: Motors': 'steam/lv_motor', 'Compressing Stuff': 'steam/compressor', 'Using Steam to Cook Things': 'steam/furnace',
    'More Advanced Alloys': 'steam/alloys', 'Pumping Water': 'steam/pump', 'Where Can I Put All the Liquids?': 'steam/drum',
    'Pollution What....?': 'steam/vent',
    # LV
    'Welcome to Tier 1, LV': 'lv/start', 'EBF-Time': 'lv/ebf', 'Hey DJ, Mix it!': 'lv/mixer', 'Simple Ore Washer': 'lv/ore_washer',
    'Chemical Bath': 'lv/chemical_bath', 'Thermal Centrifuge': 'lv/thermal', 'Electrically Separating Dusts': 'lv/electrolyzer',
    'Sulfuric Acid': 'lv/sulfuric', 'Battery Alloy': 'lv/battery_alloy', 'Basic Canner': 'lv/canner', 'Glue': 'lv/glue',
    'Electric Macerator': 'lv/macerator', 'Avengers, Assemble!': 'lv/assembler', 'Saving Metal With Bending': 'lv/bender',
    'Cutting Things Apart': 'lv/lathe', 'Darn You, Wire Cutter!': 'lv/wiremill', 'Basic Fluid Solidifier': 'lv/solidifier',
    'Getting Ready for Tier 2, MV': 'lv/mv', 'Good Circuits': 'lv/circuits', 'Exploding Fluids For Power': 'lv/combustion',
    'Basic Transport: Items': 'lv/pipes', 'H for Hydrogen... the Rest of the Alphabet is Too Complicated...': 'lv/water_split',
}
PX = 24.0   # GTNH pixels per FTB Quests unit (a small GTNH quest is 24px)


def chapters(order0=100):
    out, keyof = [], {}
    tabs = []
    for n, (line, key, title, icon) in enumerate(TABS):
        meta, ents = study.line(line)
        ch = Chapter(key, f'&8GTNH ref: {title}', icon, 'gtnh_ref', order0 + n,
                     'Reference only: GT New Horizons quest book, CC BY-NC-SA 4.0. Delete before release.', None, None, xp_base=0)
        for qid, x, y, s in ents:
            keyof.setdefault(qid, f'{key}/{qid}')
        tabs.append((ch, ents))
    for ch, ents in tabs:
        here = {qid: f'{ch.key}/{qid}' for qid, *_ in ents}
        for qid, x, y, s in ents:
            g = study.Q[qid]
            desc = [re.sub('§', '&', p) for p in g['desc'].split('\n') if p.strip()] or ['(no text)']
            ours = OURS.get(g['name'])
            if ours: desc = [('link', ours, 'Our version of this step')] + desc
            deps = [here.get(d) or keyof.get(d) for d in g['deps']]
            deps = [d for d in deps if d]
            icon = g['icon'] if g['icon'].startswith('minecraft:') and exists(g['icon']) else None
            size = s / PX
            kind = 'main' if size >= 1.5 else 'quest'
            q = ch.q(str(qid), g['name'], ['check'], desc, [], at=((x + s / 2) / PX, (y + s / 2) / PX), kind=kind,
                     any_dep=g['logic'] == 'OR', icon=icon, xp=0)
            q.deps = deps; q.deps0 = list(deps)
        out.append(ch)
    return out
