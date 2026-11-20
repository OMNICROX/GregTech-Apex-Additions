#!/usr/bin/env python3
"""Adds Satsu's Iron Man Addon and Powerborne Heroes suits to the Suit Fabrication Station.

Run after hero_kits.py:
    python3 tools/extra_suits.py <satsu jar> <powerborne jar> <resources dir> <kubejs station script>

These suits carry their own Palladium powers, so they get no GTAA kit. Their tailoring recipes (tier 2+) move to
the station; the station tier comes from what the suit is built from.
"""
import json
import re
import sys
import zipfile

SATSU, PB, RES, JS = sys.argv[1:5]
KIT = 'Suit powers built in'


def load(jar, ns):
    z = zipfile.ZipFile(jar)
    names = z.namelist()
    lang = {}
    try:
        lang = json.loads(z.read(f'assets/{ns}/lang/en_us.json'))
    except KeyError:
        pass
    sets = {}
    for n in names:
        m = re.match(rf'addon/{ns}/suit_sets/(.+)\.json$', n)
        if m:
            j = json.loads(z.read(n))
            sets[f'{ns}:{m.group(1)}'] = [f'{ns}:' + j[s]['item_name'] for s in ('head', 'chest', 'legs', 'feet')
                                          if s in j and 'item_name' in j[s]]
    out = []
    for n in names:
        m = re.match(rf'data/{ns}/recipes/(.+)\.json$', n)
        if not m:
            continue
        j = json.loads(z.read(n))
        t = j.get('type', '')
        if t not in ('palladium:item_tailoring', 'palladium:suit_set_tailoring'):
            continue
        rid = f'{ns}:{m.group(1)}'
        nbt = {}
        if t == 'palladium:item_tailoring':
            armor = []
            for r in (j.get('results') or {}).values():
                if isinstance(r, dict):
                    armor.append(r['item'])
                    if r.get('nbt'):
                        nbt[r['item']] = r['nbt']
                else:
                    armor.append(r)
        else:
            armor = sets.get(j.get('suit_set', rid), sets.get(rid, []))
        if not armor:
            continue
        ings = []
        for i in j['ingredients']:
            ii = i['ingredient']
            ii = ii[0] if isinstance(ii, list) else ii
            e = {'count': i.get('count', 1)}
            if 'item' in ii:
                e['item'] = ii['item']
            elif 'tag' in ii:
                e['tag'] = ii['tag']
            else:
                continue
            ings.append(e)
        title = j.get('title') or {}
        name = lang.get(title.get('translate', ''), '') if isinstance(title, dict) else ''
        if not name:
            first = armor[0].split(':')[1]
            name = lang.get(f'item.{ns}.{first.replace("/", ".")}', '')
        if not name:
            base = m.group(1).replace('/main', '').split('/')[-1]
            name = base.replace('_', ' ').title()
        out.append({'rid': rid, 'name': name, 'armor': armor, 'nbt': nbt, 'ingredients': ings})
    return out


def satsu_tier(e):
    ids = ' '.join(i.get('item', i.get('tag', '')) for i in e['ingredients']) + ' ' + e['rid']
    num = re.search(r'mark_(\d+)', e['rid'])
    if re.search(r'extremis|mysterium|symbiote|pym_particle|modular|kirbon', ids):
        return 4
    if re.search(r'nanoparticle|nano_circuit', ids):
        return 4 if num and int(num.group(1)) >= 80 else 3
    if re.search(r'advanced_circuit|steel_gold_refined|medium_circuit|repulsor|reinforced_rt|high_density_carbon', ids):
        return 2
    return 1


PB_TIER = {'captain_america': 2, 'spiderman': 2, 'sentry': 4, 'superman': 4}


def pb_tier(e):
    for k, v in PB_TIER.items():
        if k in e['rid']:
            return v
    return 2


extra = []
for jar, ns, tierf, origin in ((SATSU, 'satsu_iron_man_addon', satsu_tier, 'Iron Man (Satsu)'),
                               (PB, 'powerborne', pb_tier, 'Powerborne Heroes')):
    for e in load(jar, ns):
        tier = tierf(e)
        entry = {'suit': e['rid'], 'name': e['name'], 'recipe': e['rid'], 'tier': tier, 'kit': KIT,
                 'origin': None, 'abilities': [f'{origin}: powers come with the suit'],
                 'armor': e['armor'], 'ingredients': e['ingredients']}
        if e['nbt']:
            entry['armor_nbt'] = e['nbt']
        extra.append(entry)

path = f'{RES}/gtapexadditions/suit_station.json'
entries = [e for e in json.load(open(path)) if e.get('kit') != KIT]
entries += extra

# identical parts at the same tier would make ambiguous recipes: give each one its own programmed circuit
groups = {}
for e in entries:
    key = (e['tier'], json.dumps(sorted((i.get('item', '#' + i.get('tag', '')), i['count']) for i in e['ingredients'])))
    groups.setdefault(key, []).append(e)
for g in groups.values():
    if len(g) > 1:
        for n, e in enumerate(g, 1):
            e['circuit'] = n
    else:
        g[0].pop('circuit', None)
json.dump(entries, open(path, 'w'), indent=1)

# bench recipes for tier 2+ extra suits are removed by the station script
js = open(JS).read()
m = re.search(r'const APEX_STATION_SUITS = (\[.*?\])', js, re.S)
ids = [i for i in json.loads(m.group(1)) if not i.startswith(('satsu_iron_man_addon:', 'powerborne:'))]
ids += [e['recipe'] for e in extra if e['tier'] >= 2]
js = js[:m.start(1)] + json.dumps(ids) + js[m.end(1):]
open(JS, 'w').write(js)

by = {}
for e in extra:
    by[e['tier']] = by.get(e['tier'], 0) + 1
print('extra station suits:', len(extra), 'by tier:', dict(sorted(by.items())),
      'moved off the bench:', sum(1 for e in extra if e['tier'] >= 2))
print('circuit groups:', sum(1 for g in groups.values() if len(g) > 1))

# tier IV extra suits also break down into Hero Cores
tagp = f'{RES}/data/gtapexadditions/tags/items/tier4_suit_chestplates.json'
tag = json.load(open(tagp))
have = {v['id'] for v in tag['values']}
for e in extra:
    if e['tier'] == 4:
        a = e['armor'][1] if len(e['armor']) > 1 else e['armor'][0]
        if a not in have:
            tag['values'].append({'id': a, 'required': False})
            have.add(a)
json.dump(tag, open(tagp, 'w'), indent=1)
print('Hero Core sources:', len(tag['values']))
