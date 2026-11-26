"""GT metals that need an Electric Blast Furnace at tier X but can be had earlier some other way."""
import json, re, sys, collections
sys.argv = sys.argv[:1]
exec(open('tiers.py').read().split("for rnd in range(80):")[0])
D = json.load(open(S + 'tiers.json')); T = D['tier']; FT = D['ftier']
n = lambda x: TN[x] if x < 15 else 'never'
ebf = {}
for r in R:
    if r['t'] == 'gtceu:electric_blast_furnace':
        for o in r['o']:
            m = re.match(r'gtceu:(hot_)?(.+)_ingot$', o)
            if m: ebf[m.group(2)] = min(ebf.get(m.group(2), 99), max(r.get('tier', 0), 1))
def need(r):
    v = r.get('tier', 0)
    for a in r['i']:
        a = [x for x in a if x not in IGNORE_INPUT]
        if a: v = max(v, min(T.get(x, 99) for x in a))
    for f in r['fi']: v = max(v, FT.get(f, 0))
    return v
out = []
for mat, et in sorted(ebf.items(), key=lambda x: -x[1]):
    ing = f'gtceu:{mat}_ingot'
    if T.get(ing, 99) < et:
        best = min((r for r in R if ing in r['o']), key=need)
        # follow one more step if the best route goes through another item
        why = best['id']
        src = [min(a, key=lambda z: T.get(z, 99)) for a in best['i'] if a and a[0] not in IGNORE_INPUT]
        out.append((et, mat, n(T[ing]), n(et), why, src, best['fi']))
for et, mat, have, want, why, src, fi in out:
    print(f'{mat:24s} EBF {want:4s} but {have:4s} via {why}  {src} {fi}')
