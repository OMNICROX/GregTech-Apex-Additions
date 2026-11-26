"""Can the pack be finished? Fixed-point over every recipe from what the world gives you, then report what the
end goals and each tier's milestones need that can never be made."""
import json, re, collections, rgraph
U = '/mnt/user-data/uploads/Apex Ascension/kubejs/'
N = dict(json.load(open(U + 'apex_quest_items.json'))['items'])
ALIAS = {'ad_astra:oxygen': 'gtceu:oxygen', 'ad_astra:hydrogen': 'gtceu:hydrogen', 'ad_astra:oil': 'gtceu:oil'}
import re as _re
_made = set(f for r in rgraph.R for f in r.get('fo', []))
for r in rgraph.R:
    for f in r.get('fi', []):
        if f not in _made and f not in ALIAS:
            g = 'gtceu:' + _re.sub(r'^molten_', '', f.split(':')[1])
            if g in _made: ALIAS[f] = g
R = [dict(r, fi=[ALIAS.get(f, f) for f in r.get('fi', [])], fo=[ALIAS.get(f, f) for f in r.get('fo', [])]) for r in rgraph.R]
V = json.load(open('/tmp/claude-0/-home-claude/e2e2a1ea-2802-5c5f-8206-9605d72f9e6a/scratchpad/gt_veins.json'))
GTNS = {'gtceu', 'gtna', 'gtca', 'gtapexadditions', 'gtmthings', 'gtmutils', 'gtnh_arch', 'gtopt', 'extendedfeatures'}
made = set(o for r in R for o in r['o']) | set(f for r in R for f in r.get('fo', []))
snake = lambda s: re.sub(r'(?<!^)(?=[A-Z])', '_', s).lower()
vein_mats = {snake(m) for v in V for m in v['mats']}
base = set()
for i in N:
    if i in made: continue
    ns, p = i.split(':', 1)
    if ns in GTNS:
        m = re.match(r'(?:raw_|(?:[a-z_]+_)?)?([a-z_]+?)(?:_ore)?$', p)
        if p.startswith('raw_') and p[4:] in vein_mats: base.add(i)
        elif p.endswith('_ore') and any(p.endswith(mat + '_ore') for mat in vein_mats): base.add(i)
        elif p.endswith('_indicator'): base.add(i)
        continue
    base.add(i)   # vanilla and mod items with no recipe: world, mobs, loot (assumed findable)
FLUIDS = {'minecraft:water', 'minecraft:lava', 'gtceu:oil', 'gtceu:oil_light', 'gtceu:oil_heavy', 'gtceu:oil_medium',
          'gtceu:natural_gas', 'gtceu:salt_water', 'gtceu:raw_oil', 'gtceu:helium_3', 'gtceu:steam'}
have = set(base) | FLUIDS
mapped = collections.defaultdict(set)
for m, ts in rgraph.TYPES_OF.items():
    for t in ts: mapped[t].add(m)
def machine_ok(t):
    if t.startswith('minecraft:') or t not in mapped: return True
    return any(m in have for m in mapped[t])
changed = True; rounds = 0
pending = list(R)
while changed:
    changed = False; rounds += 1; nxt = []
    for r in pending:
        if all(any(a in have for a in alt) for alt in r['i'] if alt) and all(f in have for f in r.get('fi', [])) and machine_ok(r['t']):
            for o in r['o'] + r.get('fo', []):
                if o not in have: have.add(o); changed = True
        else: nxt.append(r)
    pending = nxt
print('rounds', rounds, 'reachable items', len([i for i in N if i in have]), 'of', len(N))
PROD = collections.defaultdict(list)
for r in R:
    for o in r['o'] + r.get('fo', []): PROD[o].append(r)
def why(item, depth=0, seen=None):
    """the leaf items that block item (no recipe and not from the world, or only recipes that loop)"""
    seen = seen if seen is not None else set()
    if item in have: return set()
    if item in seen: return {'loop:' + item}
    seen = seen | {item}
    rs = PROD.get(item, [])
    if not rs: return {item}
    best = None
    for r in rs:
        miss = set()
        for alt in r['i']:
            if alt and not any(a in have for a in alt):
                miss |= why(sorted(alt)[0], depth + 1, seen)
        for f in r.get('fi', []):
            if f not in have: miss |= why(f, depth + 1, seen)
        if not machine_ok(r['t']):
            miss |= set().union(*(why(m, depth + 1, seen) for m in sorted(mapped[r['t']])[:1]))
        if best is None or len(miss) < len(best): best = miss
        if depth > 14: break
    return best or {item}
