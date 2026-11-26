"""Side chapters carried over from the earlier quest book, cleaned for this pack and gated by GregTech tier.

Quests whose items no longer exist are dropped (their dependents inherit their dependencies), recipe lines are
removed (GregTech changes many recipes), the old reward tables become this book's tier bags, and every quest needs
the GregTech gateway of the tier its items first become available at."""
import re
from core import *
from snbt import parse
from rewards import bag_for_tier

OLD = S + 'q/config/ftbquests/quests/chapters/'
NS = {k.split(':')[0] for k in NAMES} | {
    'minecraft', 'dungeons_arise', 'betterdungeons', 'betterdeserttemples', 'betterjungletemples', 'bettermineshafts',
    'betterfortresses', 'betteroceanmonuments', 'betterstrongholds', 'betterwitchhuts', 'betterendisland', 'yungsbridges',
    'yungsextras', 'underground_village', 'ad_astra_more_structures', 'dungeon_and_village_ad_astra', 'echoes_core'}
DROPPED = []
PHRASE_FIX = [('at the Fabrication Table', 'in the Suit Fabrication Station (see the Hero Suits chapter)'),
              ('Build any full hero suit and try out its powers.', 'Build any full hero suit and try out its powers. Costumes come from the Tailoring Bench; the real hero suits, with their powers, from the Suit Fabrication Station.')]
TEXT_FIX = {'heroes': {
    'Most suits start at the &eHero Workshop&r: lead, titanium, circuits and the Fabrication Table. &eBen 10&r, &ePower Rangers&r and &eMoon Knight&r can be started right away.':
    'Hero materials come first: lead, titanium and circuits. The real hero suits, with their powers, are made in GregTech\'s &6Suit Fabrication Station&r (Hero Suits chapter). &eBen 10&r, &ePower Rangers&r and &eMoon Knight&r can be started right away.'}}
KJS = '/home/claude/gtaa/tools/kubejs/'
_v13 = open(KJS + 'apex_v13_integration.js').read()
# mod metals folded into GregTech's: the quest asks for the GregTech one
SWAP = {a: c for a, b, c in re.findall(r"\['([a-z0-9_.]+:[a-z0-9_]+)', '([^']+)', '([a-z0-9_.]+:[a-z0-9_]+)'\]", _v13)}
SWAP.update({'avaritia:neutronium_ingot': 'gtceu:neutronium_ingot', 'avaritia:neutron_ingot': 'gtceu:neutronium_ingot',
             'avaritia:infinity_ingot': 'gtna:infinity_ingot', 'avaritia:neutron_pile': 'gtceu:tiny_neutronium_dust'})
# items the pack's scripts made uncraftable on purpose
REMOVED = set(re.findall(r"'([a-z0-9_.]+:[a-z0-9_]+)'", _v13.split('REMOVE:')[1].split(']')[0]))
REMOVED |= set(re.findall(r"'(cyclic:[a-z0-9_]+)'", open(KJS + 'apex_v12_cleanup.js').read()))
REMOVED |= {'fsang:suit_table', 'avaritia:neutron_collector', 'avaritia:dense_neutron_collector', 'avaritia:denser_neutron_collector',
            'avaritia:densest_neutron_collector', 'botania:orechid', 'botania:orechid_ignem', 'botania:mana_fluxfield', 'corewithstuff:generator'}
import sys as _s
_s.argv = _s.argv[:1]
UNAVAILABLE = re.compile(re.search(r"UNAVAILABLE = re.compile\(r'(.*)'\)", open('tiers.py').read()).group(1))


def _item_id(it):
    if isinstance(it, dict): return it.get('id', '')
    return it


def _ns_ok(rl):
    return isinstance(rl, str) and rl.split(':')[0] in NS


def _task_ok(t):
    ty = t['type']
    if ty == 'item':
        i = _item_id(t['item'])
        if i in SWAP:
            i = SWAP[i]
            if isinstance(t['item'], dict): t['item']['id'] = i
            else: t['item'] = i
        return exists(i) and i not in REMOVED and not UNAVAILABLE.search(i)
    if ty == 'kill': return _ns_ok(t.get('entity', 'minecraft:x'))
    if ty == 'structure': return t.get('structure', '').startswith('#') or _ns_ok(t.get('structure'))
    if ty == 'dimension': return _ns_ok(t.get('dimension'))
    if ty == 'biome': return t.get('biome', '').startswith('#') or _ns_ok(t.get('biome'))
    if ty == 'advancement': return _ns_ok(t.get('advancement'))
    if ty == 'questsadditions:place': return exists(t.get('block', ''))
    return True


def _qtier(q):
    ts = [tier(_item_id(t['item'])) for t in q['tasks'] if t['type'] == 'item']
    ts = [t for t in ts if t < 99]
    return max(ts) if ts else 0


def load(fname, group, order, title=None, gate=None, cap_tier=13, extra_desc=None):
    """gate: a quest key every root quest of the chapter needs (e.g. 't1/gate')"""
    c = parse(open(OLD + fname + '.snbt').read())
    quests = c['quests']
    byid = {q['id']: q for q in quests}
    keep = {}
    for q in quests:
        tasks = [t for t in q.get('tasks', []) if _task_ok(t)]
        gone = [t for t in q.get('tasks', []) if t not in tasks]
        if gone: DROPPED.append((fname, q.get('title'), [t.get('item', t.get('structure', t.get('entity', t['type']))) for t in gone]))
        if not tasks and q.get('tasks'):
            continue
        q['tasks'] = tasks
        keep[q['id']] = q

    # dependencies: route around dropped quests
    def resolve(i, seen=()):
        if i in keep: return [i]
        if i not in byid or i in seen: return []
        out = []
        for d in byid[i].get('dependencies', []): out += resolve(d, seen + (i,))
        return out

    gate_ids = {}
    for q in keep.values():
        deps = []
        for d in q.get('dependencies', []): deps += resolve(d)
        t = min(_qtier(q), cap_tier)
        if t >= 1: deps.append(hid(f't{t}/gate'))
        if gate and not q.get('dependencies'): deps.append(hid(gate))
        q['dependencies'] = list(dict.fromkeys(deps))
        if not q['dependencies']: del q['dependencies']
        # descriptions: no recipe lines
        if 'description' in q:
            lines = [l for l in q['description'] if not re.match(r'\s*(&8Recipe|\{)', l)
                     and not re.search(r'&8Pack change|Powah|ProjectE|CrazyCraft', l)]
            lines = [TEXT_FIX.get(fname, {}).get(l, l) for l in lines]
            for a, b in PHRASE_FIX: lines = [l.replace(a, b) for l in lines]
            while lines and lines[-1] == '': lines.pop()
            q['description'] = lines
        # rewards: the old tables become this book's bags
        rew = []
        bag = False
        for r in q.get('rewards', []):
            if r['type'] in ('random', 'choice', 'loot'):
                if not bag:
                    rew.append({'id': r['id'], 'type': 'random', 'table_id': Long(int(table_hex(bag_for_tier(t)), 16)),
                                'exclude_from_claim_all': True})
                    bag = True
            elif r['type'] == 'item':
                if exists(_item_id(r['item'])): rew.append(r)
            else:
                rew.append(r)
        q['rewards'] = rew
    c['quests'] = list(keep.values())
    c['images'] = [im for im in c.get('images', []) if _ns_ok(im.get('image', '')) and not im['image'].startswith('ccquests:')]
    if not c['images']: del c['images']
    c['group'] = hid('group/' + group)
    c['order_index'] = order
    c['filename'] = 'side_' + fname
    if title: c['title'] = title
    c['quest_links'] = []
    return c


# (old file, group, title override, gate quest)
IMPORTS = [
    ('storage', 'tech', '&bStorage \\& AE2', 's/to_steam'),
    ('utilities', 'tech', None, None),
    ('cyclic', 'tech', None, 't1/gate'),
    ('corewithstuff', 'tech', None, 't1/gate'),
    ('draconic', 'tech', None, 't4/gate'),
    ('mystical', 'magic', None, None),
    ('infinity', 'heroes', None, 't2/gate'),
    ('heroes', 'heroes', None, 't1/gate'),
    ('tinkers', 'gear', None, None),
    ('artifacts', 'gear', None, None),
    ('chancecubes', 'gear', None, None),
    ('invpets', 'gear', None, None),
    ('twilight', 'explore', None, 's/to_steam'),
    ('undergarden', 'explore', None, None),
    ('eoh', 'explore', None, None),
    ('explore', 'explore', None, None),
    ('iceandfire', 'mobs', None, None),
    ('bomd', 'mobs', None, None),
    ('alexsmobs', 'mobs', None, None),
    ('mutants', 'mobs', None, None),
    ('chaos', 'mobs', None, None),
    ('threateningly', 'mobs', None, None),
    ('bounties', 'mobs', None, None),
    ('farmersdelight', 'life', None, None),
    ('quark', 'life', None, None),
    ('supplementaries', 'life', None, None),
    ('securitycraft', 'life', None, None),
    ('decor', 'life', None, None),
]


def chapters():
    out = []
    for i, (f, g, title, gate) in enumerate(IMPORTS):
        out.append(load(f, g, 100 + i, title, gate))
    return out
