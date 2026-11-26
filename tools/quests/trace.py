import json, sys
sys.argv, args = sys.argv[:1], sys.argv[1:]
exec(open('tiers.py').read().split("for rnd in range(80):")[0])
D = json.load(open(S + 'tiers.json')); T = D['tier']; TT = D['type_tier']; FT = D['ftier']
n = lambda x: TN[x] if x < 15 else 'never'
for it in args:
    print('==', it, n(T.get(it, 99)))
    rs = sorted([r for r in R if it in r['o']], key=lambda r: max([r.get('tier', 0)] + [min(T.get(z, 99) for z in a) for a in r['i'] if a]))
    for r in rs[:6]:
        ins = [(min(a, key=lambda z: T.get(z, 99)), n(min(T.get(z, 99) for z in a))) for a in r['i'] if a]
        print('  ', r['t'], '[type', n(TT.get(r['t'], 0)) + ']', 'tier', n(r.get('tier', 0)), r['id'], ins, [(f, n(FT.get(f, 0))) for f in r['fi']])
