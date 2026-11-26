import sys, reach
from reach import have, PROD, machine_ok, mapped, N
def one(item, n=4):
    rs = PROD.get(item, [])
    print(f'{item} ({N.get(item,"")}) recipes={len(rs)}')
    out = []
    for r in rs:
        miss = [sorted(alt)[0] for alt in r['i'] if alt and not any(a in have for a in alt)] + [f for f in r.get('fi', []) if f not in have]
        if not machine_ok(r['t']): miss.append('MACHINE ' + r['t'] + ' (' + ', '.join(sorted(mapped[r['t']])[:2]) + ')')
        out.append((len(miss), r['t'], miss))
    for m in sorted(out)[:n]: print('   ', m[1], 'missing:', m[2])
for a in sys.argv[1:]: one(a)
