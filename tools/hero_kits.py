#!/usr/bin/env python3
"""Generates Apex hero kits: a Palladium power per FSang suit set, granted (with the matching FSang origin
power) while the full suit is worn. Output: data/gtapexadditions/palladium/{powers/hero,suit_set_powers}."""
import json, glob, os, re, sys, uuid, shutil, csv
HERO = sys.argv[1]           # extracted FSang jar dir
OUT = sys.argv[2]            # .../src/main/resources
NS = 'gtapexadditions'

def A(attr, amount, title, icon, op=0):
    return {"type": "palladium:attribute_modifier", "title": title, "icon": icon, "hidden": False,
            "hidden_in_bar": True, "attribute": attr, "amount": amount, "operation": op}
def heal(freq, amt, title="Regeneration"):
    return {"type": "palladium:healing", "title": title, "icon": "minecraft:golden_apple", "hidden": False,
            "hidden_in_bar": True, "frequency": freq, "amount": amt}
def effect(eff, title, icon):
    return {"type": "palladium:command", "title": title, "icon": icon, "hidden": False, "hidden_in_bar": True,
            "first_tick_commands": [f"effect give @s {eff} infinite 0 true"],
            "last_tick_commands": [f"effect clear @s {eff}"], "commands": []}
def immune(sources, title, icon):
    return {"type": "palladium:damage_immunity", "title": title, "icon": icon, "hidden": False, "hidden_in_bar": True,
            "damage_sources": sources}

SPD = lambda v: A("minecraft:generic.movement_speed", v, f"Speed +{int(v*1000)}%", "minecraft:sugar")
ARM = lambda v: A("minecraft:generic.armor", v, f"Armor +{v}", "minecraft:iron_chestplate")
TGH = lambda v: A("minecraft:generic.armor_toughness", v, f"Toughness +{v}", "minecraft:diamond_chestplate")
HP = lambda v: A("minecraft:generic.max_health", v, f"Health +{v//2} hearts", "minecraft:red_dye")
ATK = lambda v: A("minecraft:generic.attack_damage", v, f"Attack +{v}", "minecraft:iron_sword")
PUNCH = lambda v: A("palladium:punch_damage", v, f"Punch +{v}", "minecraft:iron_ingot")
KB = lambda v: A("minecraft:generic.knockback_resistance", v, f"Knockback Resistance +{int(v*100)}%", "minecraft:anvil")
FALL = lambda v: A("palladium:fall_resistance", v, f"Fall Resistance +{v}", "minecraft:feather")
JUMP = lambda v: A("palladium:jump_power", v, f"Jump Boost +{v}", "minecraft:rabbit_foot")
LEAP = lambda v: A("palladium:leaping", v, f"Leaping +{v}", "minecraft:slime_ball")
STEP = lambda v: A("forge:step_height_addition", v, f"Step Assist +{v}", "minecraft:oak_stairs")
SWIM = lambda v: A("forge:swim_speed", v, f"Swim Speed +{int(v*100)}%", "minecraft:prismarine_shard")
ASPD = lambda v: A("minecraft:generic.attack_speed", v, f"Attack Speed +{v}", "minecraft:golden_sword")
FLY = lambda v: A("palladium:flight_speed", v, "Flight", "minecraft:elytra")
DIG = lambda v: A("palladium:destroy_speed", v, f"Mining Speed +{v}", "minecraft:iron_pickaxe")
REACH = lambda v: A("forge:entity_reach", v, f"Reach +{v}", "minecraft:stick")
NV = lambda: effect("minecraft:night_vision", "Night Vision", "minecraft:spider_eye")
WB = lambda: effect("minecraft:water_breathing", "Water Breathing", "minecraft:pufferfish")
FIREPROOF = lambda: immune(["minecraft:in_fire", "minecraft:on_fire", "minecraft:lava", "minecraft:hot_floor"], "Fireproof", "minecraft:magma_cream")
COLDPROOF = lambda: immune(["minecraft:freeze"], "Cold Immunity", "minecraft:powder_snow_bucket")
NOFALL = lambda: immune(["minecraft:fall"], "No Fall Damage", "minecraft:feather")

# archetype -> (description, abilities)
ARCH = {
 'speedster':  ("Speedster", lambda: [SPD(.06), STEP(1), FALL(30), ASPD(1.5), JUMP(.5), ATK(2)]),
 'merc':       ("Unkillable Mercenary", lambda: [heal(30, 1, "Healing Factor"), SPD(.025), ATK(4), JUMP(1), LEAP(1), FALL(30), TGH(2), ASPD(.5)]),
 'feral':      ("Feral Healer", lambda: [heal(40, 1, "Healing Factor"), ATK(5), SPD(.02), JUMP(.5), FALL(20), NV(), TGH(2)]),
 'vigilante':  ("Vigilante", lambda: [ARM(4), TGH(2), ATK(2), SPD(.015), FALL(20), KB(.2), NV()]),
 'assassin':   ("Assassin", lambda: [ATK(4), ASPD(1), SPD(.025), JUMP(.5), FALL(20), NV()]),
 'archer':     ("Marksman", lambda: [SPD(.015), FALL(15), ARM(2), NV(), ATK(2)]),
 'spider':     ("Spider-Sense", lambda: [SPD(.03), JUMP(1), LEAP(2), FALL(60), ATK(2), STEP(1), NV()]),
 'brute':      ("Unstoppable Brute", lambda: [HP(12), ARM(6), TGH(4), PUNCH(6), KB(.7), DIG(2), SPD(-.01)]),
 'soldier':    ("Super Soldier", lambda: [HP(6), ARM(4), ATK(3), SPD(.02), KB(.3), FALL(15), JUMP(.5)]),
 'god':        ("Godlike", lambda: [HP(10), ARM(6), PUNCH(4), KB(.5), FALL(60), TGH(2)]),
 'kryptonian': ("Man of Steel", lambda: [ARM(4), HP(4), KB(.5), NOFALL()]),
 'viltrumite': ("Viltrumite", lambda: [ARM(4), HP(6), KB(.5), NOFALL(), PUNCH(2)]),
 'aquatic':    ("Child of Atlantis", lambda: [SWIM(1.5), WB(), ARM(2), HP(4), ATK(2), NV()]),
 'flier':      ("Cosmic Flier", lambda: [FLY(.3), ARM(4), HP(4), PUNCH(3), NOFALL()]),
 'mystic':     ("Sorcerer", lambda: [HP(4), ARM(2), FALL(40), NV(), REACH(1)]),
 'tech':       ("High-Tech Gear", lambda: [ARM(5), TGH(2), FLY(.2), FALL(40), ATK(2)]),
 'fire':       ("Living Flame", lambda: [FIREPROOF(), ATK(2), ARM(2), SPD(.01)]),
 'ice':        ("Ice Cold", lambda: [COLDPROOF(), ARM(3), SPD(.01), ATK(1)]),
 'psychic':    ("Psychic", lambda: [HP(4), FALL(30), NV(), ARM(2)]),
 'cosmic':     ("Cosmic Tyrant", lambda: [HP(16), ARM(8), TGH(4), PUNCH(6), KB(.8), NOFALL()]),
 'tiny':       ("Shrinking Hero", lambda: [SPD(.02), FALL(60), JUMP(1), ATK(2)]),
 'oneforall':  ("One For All", lambda: [PUNCH(8), SPD(.03), JUMP(2), LEAP(2), FALL(100), HP(6)]),
 'saiyan':     ("Saiyan", lambda: [PUNCH(6), FLY(.3), SPD(.03), HP(6), ARM(2)]),
 'speedster_evil': ("Negative Speed", lambda: [SPD(.065), STEP(1), FALL(30), ASPD(2), ATK(3)]),
 'electric':   ("Living Current", lambda: [SPD(.03), ATK(3), ARM(2), FALL(20), immune(["minecraft:lightning_bolt"], "Shockproof", "minecraft:lightning_rod")]),
 'magnet':     ("Master of Magnetism", lambda: [FLY(.2), ARM(4), KB(.4), NOFALL()]),
 'winged':     ("Winged", lambda: [FLY(.35), FALL(60), SPD(.01)]),
 'stretch':    ("Elastic", lambda: [REACH(3), FALL(100), STEP(1), KB(.3)]),
 'ghostly':    ("Spectral", lambda: [SPD(.02), FALL(50), NV(), ARM(2)]),
 'costume':    ("Hero Costume", lambda: [ARM(2), SPD(.01), FALL(10)]),
}

# (regex on suit id, [archetypes], origin power or None)
RULES = [
 (r'superman|supergirl|superboy|power_girl|zod|kryptonian', ['kryptonian'], 'kryptonian'),
 (r'black_adam|shazam', ['god'], 'shazam'),
 (r'reverse_flash|zoom|savitar|godspeed|red_death', ['speedster_evil'], 'speedforce_negative'),
 (r'flash|kid_flash|impulse|jesse_quick|tornado_twins|red_rush', ['speedster'], 'speedforce'),
 (r'a_train|quicksilver', ['speedster'], 'speedster_chem'),
 (r'miles', ['spider'], 'spiderman_miles'), (r'miguel|2099', ['spider'], 'spiderman_miguel'),
 (r'silk', ['spider'], 'spiderman_silk'), (r'jessica', ['spider'], 'spiderman_jessica'),
 (r'kaine|scarlet_spider', ['spider'], 'spiderman_kaine'), (r'spider', ['spider'], 'spiderman'),
 (r'beta_ray_bill|thor|jane_foster', ['god'], 'thor'),
 (r'loki|sylvie|odin|asgard|valkyrie|sif', ['god', 'mystic'], 'asgardian'),
 (r'she_hulk|hulk|abomination', ['brute'], 'hulk'),
 (r'ladypool|deadpool', ['merc'], 'regenerative_healing'),
 (r'wolverine|x_23|logan', ['feral'], 'wolverine'), (r'sabretooth', ['feral'], 'sabretooth'),
 (r'cyclops', ['vigilante'], 'cyclops'), (r'havok', ['vigilante'], 'havok'),
 (r'magneto|polaris', ['magnet'], 'magnetism'), (r'storm', ['flier'], 'storm'),
 (r'jean_grey|phoenix', ['psychic', 'flier'], 'telekinesis'), (r'scarlet_witch|wanda', ['mystic'], 'reality_warping'),
 (r'professor_x|xavier', ['psychic'], 'telepathy'), (r'magik', ['mystic', 'assassin'], 'portal_creator'),
 (r'us_?agent|captain_america|bucky_cap|sam_wilson_cap', ['soldier'], 'super_soldier'),
 (r'winter_soldier', ['soldier'], 'super_soldier_winter_soldier'), (r'red_skull', ['soldier'], 'super_soldier_red_skull'),
 (r'aquaman|aqualad|mera|namor', ['aquatic'], 'atlantean'),
 (r'wonder_woman|amazon|donna_troy', ['god'], 'amazon'),
 (r'invincible|omni_man|omniman|viltrum|anissa|conquest|thragg', ['viltrumite'], 'viltrumite'),
 (r'atom_eve', ['flier'], 'matter_absorption'),
 (r'homelander', ['kryptonian'], 'homelander'),
 (r'fantastic_four_mcu_johnny|human_torch|johnny_storm', ['fire'], 'human_torch'),
 (r'fantastic_four_mcu_ben|the_thing', ['brute'], None),
 (r'fantastic_four_mcu_sue|invisible_woman|sue_storm', ['psychic'], 'invisibility'),
 (r'fantastic_four_mcu_reed|mr_fantastic', ['stretch'], None),
 (r'fantastic_four', ['soldier'], None),
 (r'iceman|frozone', ['ice'], 'cryokinesis'), (r'killer_frost', ['ice'], 'cryokinesis'),
 (r'sentry', ['flier', 'god'], 'sentry'), (r'blue_marvel', ['flier', 'god'], 'anti_matter'),
 (r'martian', ['flier', 'psychic'], 'martian_green'),
 (r'daredevil', ['vigilante'], 'daredevil'), (r'elektra', ['assassin'], None),
 (r'gambit', ['assassin'], 'gambit'), (r'colossus', ['brute'], 'colossus'),
 (r'sandman', ['brute'], 'sandman'), (r'ghost_rider|spirit', ['fire', 'merc'], 'spirit_of_vengence_contract'),
 (r'anti_venom|venom|carnage|symbiote', ['spider', 'brute'], 'symbiosis_lasher'),
 (r'lobo', ['merc', 'brute'], 'lobo'), (r'darkseid|apocalypse|thanos', ['cosmic'], 'apocalypse'),
 (r'dr_doom', ['mystic', 'tech'], None),
 (r'vision', ['flier'], 'phasing'), (r'kitty|shadowcat', ['assassin'], 'phasing'), (r'ghost_mcu', ['ghostly'], 'phasing'),
 (r'ant_man|wasp|giant_man|yellowjacket', ['tiny'], 'size_change'),
 (r'nightcrawler', ['assassin'], 'teleportation'),
 (r'captain_marvel|carol|ms_marvel|kree', ['flier'], 'kree_hybrid'),
 (r'radiant_black', ['flier'], 'radiant_black'), (r'blue_beetle', ['tech'], None),
 (r'spawn', ['merc', 'mystic'], 'spawn'), (r'rogue|mimic', ['psychic', 'soldier'], 'mimicry'),
 (r'static_shock|electro', ['electric'], 'electrokinesis'), (r'goblin', ['soldier', 'winged'], 'super_soldier_goblin'),
 (r'falcon', ['winged'], None), (r'green_lantern', ['flier'], None), (r'booster_gold', ['tech'], None),
 (r'zatana|zatanna|constantine|raven|doctor_strange|dr_strange', ['mystic'], None),
 (r'iron_fist', ['assassin', 'brute'], None), (r'babel|owlman|arkham_knight|azrael|red_x|deathstroke', ['assassin'], None),
 (r'batman|batgirl|robin|nightwing|red_hood|catwoman|black_widow|punisher|moon_knight|shield_agent', ['vigilante'], None),
 (r'hawkeye|green_arrow', ['archer'], None),
 (r'all_might|deku', ['oneforall'], None), (r'goku', ['saiyan'], None), (r'jewel', ['psychic'], None),
 (r'x_men', ['soldier'], None), (r'rexsplode', ['merc'], 'self_detonation'), (r'dog_welder', ['tech'], None),
 (r'picotech', ['tech'], None), (r'vulcan', ['flier', 'fire'], None), (r'cerebro', ['psychic'], 'telepathy'),
 (r'cop_|formal_|ld_corp|mine_labs|respawn', ['costume'], None),
]

powers = set(os.path.basename(p)[:-5] for p in glob.glob(f'{HERO}/data/fsang/palladium/powers/*.json'))
existing = {}
for p in glob.glob(f'{HERO}/data/*/palladium/suit_set_powers/*.json'):
    j = json.load(open(p)); ss = j.get('suit_set')
    for s in (ss if isinstance(ss, list) else [ss]): existing[s] = j.get('power')
sets = sorted(f"fsang:{os.path.basename(p)[:-5]}" for p in glob.glob(f'{HERO}/addon/fsang/suit_sets/*.json'))
lang = {}
for p in glob.glob(f'{HERO}/assets/*/lang/en_us.json'):
    try: lang.update(json.load(open(p, encoding='utf-8')))
    except Exception: pass

pdir = f'{OUT}/data/{NS}/palladium/powers/hero'; sdir = f'{OUT}/data/{NS}/palladium/suit_set_powers'
for d in (pdir, sdir):
    shutil.rmtree(d, ignore_errors=True); os.makedirs(d)
rows = []
station = {}
for sid in sets:
    short = sid.split(':')[1]
    if sid in existing:
        rows.append((short, 'kept own powers', str(existing[sid]), ''))
        station[sid] = {'archs': ['own'], 'origin': None, 'kit': 'Built-in powers'}
        continue
    rule = next((r for r in RULES if re.search(r[0], short)), (None, ['costume'], None))
    archs, origin = rule[1], rule[2]
    if origin and origin not in powers: origin = None
    abilities = {}
    names = []
    for a in archs:
        title, fn = ARCH[a]; names.append(title)
        for ab in fn():
            key = re.sub(r'[^a-z0-9]+', '_', (ab.get('attribute') or ab['type'] + ab['title']).lower()).strip('_')
            if key in abilities:  # stack duplicates from two archetypes: keep the stronger one
                if ab.get('amount', 0) > abilities[key].get('amount', 0): abilities[key] = ab
                continue
            abilities[key] = ab
    for k, ab in abilities.items():
        if ab['type'] == 'palladium:attribute_modifier':
            ab['uuid'] = str(uuid.uuid5(uuid.NAMESPACE_URL, f'gtaa:{short}:{k}'))
    nice = lang.get(f'suit_set.fsang.{short}') or short.replace('_', ' ').title()
    power = {"name": f"{nice}: {' + '.join(names)}", "background": "minecraft:textures/block/gray_concrete.png",
             "icon": "minecraft:nether_star", "abilities": abilities}
    json.dump(power, open(f'{pdir}/{short}.json', 'w'), indent=1)
    plist = [f'{NS}:hero/{short}'] + ([f'fsang:{origin}'] if origin else [])
    json.dump({"suit_set": sid, "power": plist}, open(f'{sdir}/{short}.json', 'w'), indent=1)
    summary = ', '.join(ab['title'] for ab in abilities.values())
    station[sid] = {'archs': archs, 'origin': origin, 'kit': ' + '.join(names), 'abilities': [ab['title'] for ab in abilities.values()]}
    rows.append((short, ' + '.join(names), origin or '', summary))
with open(sys.argv[3], 'w', newline='') as f:
    w = csv.writer(f); w.writerow(['suit_set', 'kit', 'origin_power', 'kit_abilities']); w.writerows(rows)
print(len(rows), 'suits;', sum(1 for r in rows if r[1] != 'kept own powers'), 'kits;',
      sum(1 for r in rows if r[1] == 'Hero Costume'), 'generic costume;', sum(1 for r in rows if r[2] and r[1] != 'kept own powers'), 'with origin power')

# ---------------- Suit Fabrication Station data ----------------
TIER = {'costume': 1, 'vigilante': 2, 'archer': 2, 'assassin': 2, 'spider': 2, 'speedster': 2, 'aquatic': 2, 'ice': 2,
        'fire': 2, 'electric': 2, 'tiny': 2, 'stretch': 2, 'winged': 2, 'ghostly': 2, 'psychic': 2, 'mystic': 2,
        'soldier': 3, 'feral': 3, 'merc': 3, 'tech': 3, 'magnet': 3, 'speedster_evil': 3, 'brute': 3, 'oneforall': 3,
        'saiyan': 3, 'flier': 3, 'god': 3, 'kryptonian': 4, 'viltrumite': 4, 'cosmic': 4}
TOP_ORIGINS = {'kryptonian', 'homelander', 'viltrumite', 'sentry', 'anti_matter', 'apocalypse', 'thor', 'reality_warping', 'shazam'}
def tier_of(sid, info):
    short = sid.split(':')[1]
    if info['archs'] == ['own']:
        if re.search(r'iron_man_base', short): return 1
        if re.search(r'iron_man|war_machine|iron_patriot|rescue|iron_heart|iron_spider', short): return 3
        return 2
    t = max(TIER.get(a, 2) for a in info['archs'])
    if info['origin'] in TOP_ORIGINS: t = 4
    return t

recipes = {}
for p in glob.glob(f'{HERO}/data/fsang/recipes/**/*.json', recursive=True):
    try: j = json.load(open(p))
    except Exception: continue
    if j.get('type') == 'palladium:suit_set_tailoring':
        recipes[j['suit_set']] = ('fsang:' + os.path.relpath(p, f'{HERO}/data/fsang/recipes')[:-5].replace(os.sep, '/'), j)
suitsets = {}
for p in glob.glob(f'{HERO}/addon/fsang/suit_sets/*.json'):
    suitsets['fsang:' + os.path.basename(p)[:-5]] = json.load(open(p))
entries = []
for sid, (rid, j) in sorted(recipes.items()):
    if sid not in suitsets or sid not in station: continue
    ss = suitsets[sid]
    armor = ['fsang:' + ss[k]['item_name'] for k in ('head', 'chest', 'legs', 'feet') if isinstance(ss.get(k), dict)]
    info = station[sid]
    short = sid.split(':')[1]
    entries.append({'suit': sid, 'name': lang.get(f'suit_set.fsang.{short}') or short.replace('_', ' ').title(),
                    'recipe': rid, 'tier': tier_of(sid, info), 'kit': info['kit'], 'origin': info['origin'],
                    'abilities': info.get('abilities', []), 'armor': armor,
                    'ingredients': [{**i['ingredient'], 'count': i['count']} for i in j['ingredients']]})
# programmed circuit only where two suits would otherwise have identical inputs
groups = {}
for e in entries:
    k = json.dumps(sorted(json.dumps(i, sort_keys=True) for i in e['ingredients']))
    groups.setdefault(k, []).append(e)
for g in groups.values():
    if len(g) > 1:
        for n, e in enumerate(g, 1): e['circuit'] = n
# suits the station can't make (multiversal variants etc.) get no kit
made = {e['suit'] for e in entries}
dropped = 0
for sid, info in station.items():
    if info['archs'] == ['own'] or sid in made: continue
    short = sid.split(':')[1]
    for f in (f'{pdir}/{short}.json', f'{sdir}/{short}.json'):
        if os.path.exists(f): os.remove(f)
    rows[:] = [r for r in rows if r[0] != short]
    dropped += 1
with open(sys.argv[3], 'w', newline='') as f:
    w = csv.writer(f); w.writerow(['suit_set', 'kit', 'origin_power', 'kit_abilities']); w.writerows(rows)
print('no kit (not made in the station):', dropped)
os.makedirs(f'{OUT}/gtapexadditions', exist_ok=True)
json.dump(entries, open(f'{OUT}/gtapexadditions/suit_station.json', 'w'), indent=1)
# tier-IV suit chestplates: broken down into Hero Cores for the Apex Ascension Suit
tdir = f'{OUT}/data/gtapexadditions/tags/items'
os.makedirs(tdir, exist_ok=True)
t4 = sorted({a for e in entries if e['tier'] == 4 for a in e['armor'] if a.endswith('chestplate')})
json.dump({'replace': False, 'values': [{'id': a, 'required': False} for a in t4]},
          open(f'{tdir}/tier4_suit_chestplates.json', 'w'), indent=1)
print('tier-IV chestplates for Hero Cores:', len(t4))
moved = [e['recipe'] for e in entries if e['tier'] >= 2]
js = """// priority: -95
// Apex Ascension 1.2 - Suit Fabrication Station (GregTech Apex Additions)
// Suits at station tier 2+ are only made in the Suit Fabrication Station now (their full kit of powers comes with
// them), so their tailoring-bench recipes are removed. Tier 1 costumes stay craftable at the bench.
// Generated by GTAA tools/hero_kits.py - do not edit by hand.
const APEX_STATION_SUITS = """ + json.dumps(moved) + """

ServerEvents.recipes(event => {
  let n = 0
  APEX_STATION_SUITS.forEach(id => { event.remove({ id: id }); n++ })
  console.info(`[APEX-SUITS] ${n} hero suits moved from the tailoring bench to the Suit Fabrication Station`)
})
"""
open(sys.argv[4], 'w').write(js)
from collections import Counter
print('station suits:', len(entries), 'by tier:', dict(Counter(e['tier'] for e in entries)), 'with circuit:', sum(1 for e in entries if 'circuit' in e))
