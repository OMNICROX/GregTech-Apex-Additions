"""info.py id... : does it exist, its name, and the lowest-tier real recipe(s). Patterns with * list matching ids."""
import sys, re, json, fnmatch, os
ARGS = sys.argv[1:]
import rgraph, q as Q
N = Q.N
n = int(os.environ.get('N', '2'))
for a in ARGS:
    if '*' in a:
        hits = sorted(k for k in N if fnmatch.fnmatch(k, a))
        print(a, len(hits)); 
        for h in hits[:int(os.environ.get('M', '60'))]: print('   ', h, '=', Q.nm(h))
        continue
    rs = [r for r in rgraph.recipes(a) if r['t'].split(':')[1] not in Q.BAD] or Q.fl_recipes(a)
    print(('OK ' if a in N else '-- ') + a, '=', Q.nm(a), f'({len(rs)} recipes)')
    for r in rs[:n]: print(Q.show(r)[:330])
