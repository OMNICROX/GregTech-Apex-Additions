import re, sys, json, glob
sys.path.insert(0, '.')
from pageset import GT, SRC
from gt5u_names import resolve
ids = {}
for src in GT:
    s = open(f'{SRC}/gregtech/guidenh/_en_us/{src}').read()
    for m in re.finditer(r'(?:id|controller)="([^"]+)"|icon: (\S+)|^\s+- (\S+:\S+)', s, re.M):
        i = m.group(1) or m.group(2) or m.group(3)
        ids[i] = ids.get(i, 0) + 1
names = {}
for f in glob.glob('/home/claude/gtnh/apexlang/*.json'):
    for k, v in json.load(open(f)).items():
        mm = re.match(r'^(?:block|item)\.([a-z0-9_]+)\.([a-z0-9_]+)$', k)
        if mm: names.setdefault(v.lower(), f'{mm.group(1)}:{mm.group(2)}')
out = []
for i, n in sorted(ids.items(), key=lambda x: -x[1]):
    nm = resolve(i)
    guess = names.get((nm or '').lower())
    out.append((i, n, nm, guess))
json.dump(out, open('/tmp/ids_needed.json', 'w'), indent=0)
print(len(out))
for o in out: print(o)
