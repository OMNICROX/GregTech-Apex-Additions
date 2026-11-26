"""Explain how an item reaches its tier: follow the cheapest recipe back to where it starts."""
import json, re, sys
args = sys.argv[1:]; sys.argv = sys.argv[:1]
exec(open('tiers.py').read().split("for rnd in range(80):")[0])
D = json.load(open(S + 'tiers.json')); T = D['tier']; FT = D['ftier']
n = lambda x: TN[x] if x < 15 else 'never'
by_out = {}; fby = {}
for r in R:
    for o in r['o']: by_out.setdefault(o, []).append(r)
    for f in r['fo']: fby.setdefault(f, []).append(r)
def need(r):
    v = r.get('tier', 0)
    for a in r['i']:
        a = [x for x in a if x not in IGNORE_INPUT]
        if a: v = max(v, min(T.get(x, 99) for x in a))
    for f in r['fi']: v = max(v, FT.get(f, 0))
    return v
def explain(it, depth=0, seen=None, fluid=False):
    seen = seen or set()
    if it in seen or depth > 7: return
    seen.add(it)
    rs = (fby if fluid else by_out).get(it, [])
    tv = (FT if fluid else T).get(it, 99)
    good = [r for r in rs if need(r) <= tv]
    if not good:
        print('  ' * depth + f'{it} [{n(tv)}] <- no recipe (world, loot or creative)'); return
    r = min(good, key=lambda r: (len(r['i']) + len(r['fi'])))
    print('  ' * depth + f'{it} [{n(tv)}] <- {r["t"]} {r["id"]}')
    for a in r['i']:
        a = [x for x in a if x not in IGNORE_INPUT]
        if not a: continue
        x = min(a, key=lambda z: T.get(z, 99))
        if T.get(x, 99) >= tv and depth < 7: explain(x, depth + 1, seen)
    for f in r['fi']:
        if FT.get(f, 0) >= tv and f in fby: explain(f, depth + 1, seen, True)
for a in args:
    explain(a); print()
