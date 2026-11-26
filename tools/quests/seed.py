import json, sys
args = sys.argv[1:]; sys.argv = sys.argv[:1]
src = open('tiers.py').read()
src = src.replace("            for o in r['o']:\n                if n < tier[o]: tier[o] = n; ch = True", "            for o in r['o']:\n                if n < tier[o]: tier[o] = n; ch = True; SETBY[o] = r")
src = src.replace("            for f in r['fo']:\n                if n < ftier[f]: ftier[f] = n; ch = True", "            for f in r['fo']:\n                if n < ftier[f]: ftier[f] = n; ch = True; FSETBY[f] = r")
src = src.split("print('rounds', rnd")[0]
SETBY = {}; FSETBY = {}
exec(src)
def show(it, d=0, fl=False, seen=set()):
    if (it, fl) in seen or d > 12: return
    seen.add((it, fl))
    r = (FSETBY if fl else SETBY).get(it)
    t = (ftier if fl else tier)[it]
    if not r: print('  ' * d + f'{it} [{t}] BASE'); return
    print('  ' * d + f'{it} [{t}] <- {r["id"]}')
    for a in r['i']:
        a = [x for x in a if x not in IGNORE_INPUT]
        if a:
            x = min(a, key=lambda z: tier[z])
            if tier[x] == t: show(x, d + 1, False, seen)
    for f in r['fi']:
        if f in fluid_made and ftier[f] == t: show(f, d + 1, True, seen)
for a in args: show(a)
