#!/usr/bin/env python3
"""GT crops for the Mystical Cultivation Array (CropsNH-style).

python3 tools/gt_crops.py <materials dump json> <repo root>
Writes Mystical Customization crop files (tools/mysticalcustomization/crops/gt_*.json), the crop on/off file
(configure-crops.json: Mystical Agriculture's own copies of these materials go off) and the crop list inside
tools/kubejs/apex_gt_crops.js. The materials dump is [id, name, rgb, blast K, 'ingot'|'gem', has block, radioactive]
from a running game.
"""
import json
import os
import re
import sys

DUMP, ROOT = sys.argv[1:3]
TIERS = ['ulv', 'lv', 'mv', 'hv', 'ev', 'iv', 'luv', 'zpm', 'uv', 'uhv']
T = {n: i for i, n in enumerate(TIERS)}

PICK = """aluminium antimony americium beryllium bismuth chromium cobalt darmstadtium duranium europium gadolinium gallium
germanium holmium indium iridium lead manganese molybdenum naquadah enriched_naquadah naquadria neodymium neutronium
nickel niobium osmium palladium platinum plutonium plutonium_241 rhodium ruthenium samarium silicon silver tantalum
tellurium thorium thulium tin titanium trinium tritanium tungsten uranium uranium_235 vanadium yttrium zinc
steel wrought_iron bronze brass invar electrum red_alloy battery_alloy cupronickel kanthal nichrome magnalium
soldering_alloy stainless_steel black_steel vanadium_steel tungsten_carbide tungsten_steel rtm_alloy hssg hsse hsss
niobium_titanium naquadah_alloy osmiridium rhodium_plated_palladium ruridit ultimet damascus_steel rose_gold
sterling_silver black_bronze bismuth_bronze cobalt_brass potin blue_alloy hsla_steel maraging_steel_300 incoloy_ma_956
hastelloy_x hastelloy_c_276 stellite_100 zeron_100 watertight_steel titanium_carbide tantalum_carbide
molybdenum_disilicide gallium_arsenide indium_gallium_phosphide titanium_tungsten_carbide nickel_zinc_ferrite
ruby sapphire green_sapphire topaz blue_topaz opal olivine almandine andradite grossular pyrope spessartine uvarovite
red_garnet yellow_garnet apatite quartzite lazurite sodalite malachite monazite rutile cinnabar realgar""".split()

# where the blast temperature alone puts a material too early or too late
OVERRIDE = {
    'platinum': 'ev', 'palladium': 'ev', 'rhodium': 'iv', 'ruthenium': 'iv', 'iridium': 'iv', 'osmium': 'iv',
    'osmiridium': 'iv', 'ruridit': 'luv', 'rhodium_plated_palladium': 'luv',
    'uranium': 'ev', 'thorium': 'ev', 'uranium_235': 'iv', 'plutonium': 'iv', 'plutonium_241': 'luv',
    'americium': 'zpm', 'naquadah': 'luv', 'enriched_naquadah': 'zpm', 'naquadria': 'uv', 'neutronium': 'uhv',
    'trinium': 'zpm', 'duranium': 'zpm', 'tritanium': 'uv', 'darmstadtium': 'uv', 'europium': 'luv',
    'naquadah_alloy': 'zpm', 'tantalum': 'ev', 'indium': 'hv', 'tellurium': 'hv', 'beryllium': 'mv', 'gallium': 'mv',
    'manganese': 'mv', 'samarium': 'hv', 'gadolinium': 'hv', 'holmium': 'iv', 'thulium': 'iv',
    'ruby': 'mv', 'sapphire': 'mv', 'green_sapphire': 'mv', 'topaz': 'mv', 'blue_topaz': 'mv', 'opal': 'mv',
    'monazite': 'hv', 'rutile': 'mv', 'magnalium': 'mv', 'battery_alloy': 'mv', 'cupronickel': 'mv',
    'electrum': 'lv', 'soldering_alloy': 'lv',
    'indium_gallium_phosphide': 'iv', 'nickel_zinc_ferrite': 'hv', 'molybdenum': 'hv', 'germanium': 'mv',
    'blue_alloy': 'mv', 'stainless_steel': 'hv', 'kanthal': 'hv', 'gallium_arsenide': 'hv', 'molybdenum_disilicide': 'ev',
    'ultimet': 'iv', 'vanadium_steel': 'hv', 'hsla_steel': 'hv',
}

# Mystical Agriculture's own crops for these materials go off, so there's one seed per material
MA_DUPES = ['aluminum', 'tin', 'lead', 'silver', 'nickel', 'zinc', 'cobalt', 'bronze', 'brass', 'invar', 'electrum',
            'steel', 'titanium', 'tungsten', 'platinum', 'iridium', 'osmium', 'chrome', 'uranium', 'ruby', 'sapphire',
            'apatite', 'silicon', 'constantan']


def tier_of(name, blast):
    if name in OVERRIDE:
        return OVERRIDE[name]
    if blast <= 0:
        return 'lv'
    for limit, t in ((1800, 'mv'), (2700, 'hv'), (3600, 'ev'), (4500, 'iv'), (5400, 'luv'), (7200, 'zpm'), (9000, 'uv')):
        if blast <= limit:
            return t
    return 'uhv'


def ma_tier(t):
    i = T[t]
    return 2 if i <= T['mv'] else 3 if i <= T['ev'] else 4 if i <= T['luv'] else 5


mats = {m[0].split(':', 1)[1]: m for m in json.load(open(DUMP)) if m[0].startswith('gtceu:')}
crops = []
for name in PICK:
    m = mats.get(name)
    if not m:
        print('missing', name)
        continue
    _, label, rgb, blast, form, block, rad = m
    t = tier_of(name, blast)
    crops.append((name, label, rgb & 0xFFFFFF, t, form, block))
crops.sort(key=lambda c: (T[c[3]], c[0]))

out = f'{ROOT}/tools/mysticalcustomization'
for f in os.listdir(f'{out}/crops'):
    if f.startswith('gt_'):
        os.remove(f'{out}/crops/{f}')
for name, label, rgb, t, form, block in crops:
    gem = form == 'gem'
    json.dump({
        'name': label,
        'type': 'mysticalagriculture:resource',
        'tier': f'mysticalagriculture:{ma_tier(t)}',
        'ingredient': {'tag': f'forge:{"gems" if gem else "ingots"}/{name}'},
        'color': f'#{rgb:06X}',
        'textures': {'essence': f'mysticalagriculture:item/essence_{"gem" if gem else "ingot"}',
                     'flower': f'mysticalagriculture:block/flower_{"rock" if gem else "ingot"}'},
        'recipes': {'crafting': False, 'infusion': False, 'reprocessor': False},
    }, open(f'{out}/crops/gt_{name}.json', 'w'), indent=2)

conf = {}
src = f'{ROOT}/tools/mysticalcustomization/configure-crops.json'
if os.path.exists(src):
    conf = json.load(open(src))
for n in MA_DUPES:
    conf[f'mysticalagriculture:{n}'] = {'enabled': False}
json.dump(conf, open(src, 'w'), indent=2)

js = f'{ROOT}/tools/kubejs/apex_gt_crops.js'
s = open(js).read()
rows = ',\n  '.join(json.dumps([n, t, f, b]) for n, _, _, t, f, b in crops)
s = re.sub(r'const APEX_GT_CROPS = \[.*?\n\]', 'const APEX_GT_CROPS = [\n  ' + rows + '\n]', s, flags=re.S)
open(js, 'w').write(s)

by = {}
for c in crops:
    by[c[3]] = by.get(c[3], 0) + 1
print(len(crops), 'GT crops', {k: by[k] for k in TIERS if k in by})
