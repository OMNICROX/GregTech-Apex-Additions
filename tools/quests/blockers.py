"""For an unreachable item, find the deepest unreachable ingredients (root causes)."""
import json, sys
args = sys.argv[1:]; sys.argv = sys.argv[:1]
exec(open('tiers.py').read().split("for rnd in range(80):")[0])
D = json.load(open(S + 'tiers.json')); T = D['tier']; F = D['ftier']; TT = D['type_tier']
by = {}; fby = {}
for r in R:
    for o in r['o']: by.setdefault(o, []).append(r)
    for f in r['fo']: fby.setdefault(f, []).append(r)
def bad_parts(r):
    out = []
    for a in r['i']:
        a = [x for x in a if x not in IGNORE_INPUT]
        if a and min(T.get(x, 99) for x in a) >= 99: out.append(('i', a[0]))
    for f in r['fi']:
        if f in fluid_made and F.get(f, 99) >= 99: out.append(('f', f))
    if r['t'] in TT and TT[r['t']] >= 99 and not VANILLA_FREE(r['t']): out.append(('m', r['t']))
    return out
roots = {}
def walk(kind, it, path, seen):
    if (kind, it) in seen:
        if it in path: roots.setdefault('CYCLE ' + it, path[path.index(it):] + [it])
        return
    seen.add((kind, it))
    rs = (fby if kind == 'f' else by).get(it, []) if kind != 'm' else [r for m in TYPE_M.get(it, []) for r in by.get(m, [])]
    if kind == 'm':
        for m in TYPE_M.get(it, []): walk('i', m, path + [it], seen)
        return
    if not rs:
        roots.setdefault(it, path); return
    # follow the recipe with the fewest blockers
    best = min(rs, key=lambda r: len(bad_parts(r)))
    bp = bad_parts(best)
    if not bp: return
    for k, x in bp: walk(k, x, path + [it], seen)
for a in args:
    roots.clear(); walk('i', a, [], set())
    print('==', a)
    for k, p in roots.items(): print('   ', k, ':', ' > '.join(p[-8:]))
