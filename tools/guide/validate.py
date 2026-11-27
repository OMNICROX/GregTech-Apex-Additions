import os, re, json, zipfile, glob, sys
ROOT = os.path.normpath(os.path.join(os.path.dirname(__file__), '../../src/main/resources/assets/gtapexadditions/guides/gtapexadditions/guide'))
MODS = '/mnt/user-data/uploads/Apex Ascension/mods'
known = {}
def add(ns, i): known.setdefault(ns, set()).add(i)
# lang-based ids from jars
for jar in glob.glob(MODS + '/*.jar'):
    try:
        z = zipfile.ZipFile(jar)
    except Exception: continue
    for n in z.namelist():
        m = re.match(r'assets/([a-z0-9_]+)/lang/en_us\.json$', n)
        if m:
            try: d = json.loads(z.read(n).decode('utf-8', 'ignore'))
            except Exception: continue
            for k in d:
                mm = re.match(r'^(item|block)\.([a-z0-9_]+)\.([a-z0-9_/.]+)$', k)
                if mm: add(mm.group(2), mm.group(3))
        m = re.match(r'assets/([a-z0-9_]+)/models/item/([a-z0-9_/]+)\.json$', n)
        if m: add(m.group(1), m.group(2))
for i in open('/tmp/claude-0/gt_ids.txt').read().split(): add('gtceu', i)
G = os.path.normpath(os.path.join(os.path.dirname(__file__), '../../src/generated/resources/assets/gtapexadditions/lang/en_us.json'))
for k in json.load(open(G)):
    mm = re.match(r'^(item|block)\.gtapexadditions\.([a-z0-9_]+)$', k)
    if mm: add('gtapexadditions', mm.group(2))
for f in glob.glob(os.path.join(os.path.dirname(G), '../models/item/*.json')) + glob.glob(os.path.normpath(os.path.join(os.path.dirname(__file__), '../../src/main/resources/assets/gtapexadditions/models/item/*.json'))):
    add('gtapexadditions', os.path.basename(f)[:-5])
GT_MAT = re.compile(r'^(iron|steel|gold|copper|tin|bronze)_(hammer|ingot|plate|wrench|file|saw)$')
bad = []
for path in glob.glob(ROOT + '/**/*.md', recursive=True):
    s = open(path).read()
    rel = os.path.relpath(path, ROOT)
    for m in re.finditer(r'(?:id="|icon: |^- )([a-z0-9_]+:[a-z0-9_/.]+)', s, re.M):
        ns, i = m.group(1).split(':')
        if ns == 'minecraft': continue
        if ns == 'gtceu' and GT_MAT.match(i): continue
        if ns not in known or i not in known[ns]:
            bad.append((rel, m.group(1)))
    for m in re.finditer(r'\]\(([^)#]+\.md)(#[^)]*)?\)', s):
        tgt = os.path.normpath(os.path.join(os.path.dirname(path), m.group(1)))
        if not os.path.exists(tgt): bad.append((rel, 'LINK ' + m.group(1)))
    for m in re.finditer(r'\]\((assets/[^)]+)\)', s):
        tgt = os.path.normpath(os.path.join(os.path.dirname(path), m.group(1)))
        if not os.path.exists(tgt): bad.append((rel, 'IMG ' + m.group(1)))
for b in sorted(set(bad)): print(*b)
print(len(set(bad)), 'problems')
