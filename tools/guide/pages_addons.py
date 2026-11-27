# Reference pages for the add-on multiblocks in the pack (GTNA, GTCA, GTSE, GTMUtils), generated from each mod's own
# tooltips and recipe types. These fill the biggest hole found when comparing the guide with GT New Horizons' guide:
# every multiblock in the pack should have a page with its preview, what it runs and its controller recipe.
import json, re, os
from pages_core import page, P

LANG = '/home/claude/gtnh/apexlang'
MULTIS = '/home/claude/gtnh/apex_multis_named.txt'
MODS = {'gtna': ('GTNA', 'gtna-0.5.2.json', 'GT New Horizons machines ported by GTNA.'),
        'gtca': ('GTCA', 'gtca-1.20.1-2.2.0.json', 'Machines from GregTech Community Additions.'),
        'gtse': ('GTSE', 'gtse-1.3.1.json', 'Machines from GregTech Space Expansion.'),
        'gtmutils': ('GTMUtils', 'gtmutils-2.10.3.json', 'Machines from GT Modern Utilities.')}

if os.path.isdir(LANG) and os.path.isfile(MULTIS):
    lang = {}
    for f in os.listdir(LANG):
        if f.endswith('.json'):
            lang.update(json.load(open(os.path.join(LANG, f))))
    covered = {i for p in P.values() for i in p['items']}

    def clean(t):
        t = re.sub(r'§.', '', t).replace('%%', '%')
        return t.strip()

    def rtype(t):
        t = t.strip()
        return clean(lang.get(f'gtceu.{t}', t.replace('_', ' ').title()))

    by_mod = {}
    for line in open(MULTIS):
        parts = [x.strip() for x in line.split('|')]
        if len(parts) < 3 or parts[0] not in MODS:
            continue
        ns, name, types = parts[0], parts[1], parts[2]
        ids = [k.split('.', 2)[2] for k, v in lang.items()
               if v == name and k.startswith(f'block.{ns}.')]
        if not ids:
            continue
        mid = f'{ns}:{ids[0]}'
        if mid in covered:
            continue
        path = ids[0]
        tips = []
        for key in sorted(k for k in lang if re.search(rf'\.{re.escape(path)}\.tooltip(\.\d+)?$', k)
                          or re.search(rf'tooltip\.{re.escape(path)}\.', k)):
            v = clean(lang[key])
            if v and v not in tips:
                tips.append(v)
        story = [clean(lang[k]) for k in sorted(lang) if k.startswith(f'{ns}.story.{path}.')]
        rts = [rtype(t) for t in types.split(',') if t.strip() and t.strip() != 'dummy']
        by_mod.setdefault(ns, []).append((mid, name, tips, story, rts))

    for ns, entries in by_mod.items():
        label, _, blurb = MODS[ns]
        idx = f'addons/{ns}/{ns}-index.md'
        rows = '\n'.join(f'| [{n}]({m.split(":")[1]}.md) | {", ".join(r) or "special"} |'
                         for m, n, t, s, r in sorted(entries, key=lambda e: e[1]))
        page(idx, f'{label} Multiblocks', f"""
# {label} Multiblocks

{blurb} Each page shows the structure, what the machine runs, its own description and the controller recipe.

| Machine | Runs |
|---|---|
{rows}
""", parent='addons/addons-index.md', pos=list(MODS).index(ns), icon=entries[0][0])
        for pos, (mid, name, tips, story, rts) in enumerate(sorted(entries, key=lambda e: e[1])):
            body = f'# {name}\n\n<Multiblock id="{mid}" />\n\n**From:** {label}\n\n'
            if rts:
                body += '**Runs:** ' + ', '.join(rts) + '\n\n'
            if tips:
                body += '## What it does\n\n' + '\n'.join(f'* {t}' for t in tips) + '\n\n'
            if story:
                body += '> ' + ' '.join(story) + '\n\n'
            body += ('## Controller recipe\n\n'
                     f'<GTRecipesFor id="{mid}" limit="1" fallbackText="See JEI for the controller recipe." />\n')
            page(f'addons/{ns}/{mid.split(":")[1]}.md', name, body, parent=idx, pos=pos, icon=mid, items=[mid])

    page('addons/addons-index.md', 'Add-on Multiblocks', """
# Add-on Multiblocks

The other GregTech add-ons in Apex bring well over a hundred more multiblocks, many of them ported from GT New
Horizons. Their pages are built from each mod's own descriptions, so they always match the version in the pack.

<SubPages />
""", pos=56, icon='gtna:universal_factory')
