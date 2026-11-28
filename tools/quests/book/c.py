"""c.py id... : one compact line each: exists, name, lowest-tier recipe (machine, tier, inputs). Patterns with * list ids."""
import sys, os, fnmatch
ARGS = sys.argv[1:]
import rgraph, q as Q
N = Q.N
for a in ARGS:
    if '*' in a:
        hits = sorted(k for k in N if fnmatch.fnmatch(k, a))
        print(f'{a}: {len(hits)}: ' + ', '.join(h.split(":")[1] if a.split(":")[0] != "*" else h for h in hits[:int(os.environ.get("M", "40"))])); continue
    rs = [r for r in rgraph.recipes(a) if r['t'].split(':')[1] not in Q.BAD] or Q.fl_recipes(a)
    s = ('OK ' if a in N else '-- ') + a.split(':')[1] + ' "' + Q.nm(a) + '"'
    if rs:
        r = rs[0]
        ins = ', '.join(Q.nm(x[0])[:28] for x in r['i'] if x)
        fl = ','.join(x.split(':')[1] for x in r.get('fi', []))
        s += f" <- {r['t'].split(':')[1]} T{r.get('tier', '')}: {ins}" + (f' | FL {fl}' if fl else '')
        if len(rs) > 1: s += f' (+{len(rs) - 1})'
    print(s[:int(os.environ.get('W', '230'))])
