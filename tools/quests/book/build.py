"""Builds the hand-written book into out/config/ftbquests/quests, checks it, and renders a layout preview per chapter."""
import os, sys, shutil, importlib
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import fw
from fw import hid, to_snbt, table_hex, exists, name
import picks

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, 'out', 'config', 'ftbquests', 'quests')
PREVIEW = os.path.join(HERE, 'out', 'preview')
CHAPTERS = ['ch_stone', 'ch_steam', 'ch_lv', 'ch_mv', 'ch_hv', 'ch_ev', 'ch_iv', 'ch_luv', 'ch_zpm', 'ch_uv', 'ch_uhv', 'ch_endgame', 'ch_worlds', 'ch_magic', 'ch_ae2', 'ch_heroes']
GROUPS = [('side', '&dWorlds, Magic and Automation'), ('gtnh_ref', '&8GTNH Reference (dev only)')]


def render(ch, path):
    from PIL import Image, ImageDraw, ImageFont
    qs = ch.quests
    xs = [q.x for q in qs]; ys = [q.y for q in qs]
    S = 46
    mx, my = min(xs) - 2, min(ys) - 2
    W = int((max(xs) - mx + 3) * S); H = int((max(ys) - my + 3) * S)
    im = Image.new('RGB', (W, H), (24, 24, 30)); d = ImageDraw.Draw(im)
    f = ImageFont.load_default()
    pos = {q.key: q for q in qs}
    P = lambda q: ((q.x - mx) * S, (q.y - my) * S)
    for q in qs:
        if q.hide: continue
        for dk in q.deps:
            if dk in pos:
                d.line([P(q), P(pos[dk])], fill=(200, 40, 40), width=4)
    col = {'hub': (240, 180, 60), 'main': (120, 200, 240), 'quest': (200, 200, 200), 'opt': (130, 200, 130), 'tip': (200, 140, 220)}
    for q in qs:
        r = fw.KIND[q.kind][0] * S * 0.42
        x, y = P(q)
        d.rectangle([x - r, y - r, x + r, y + r], outline=col[q.kind], width=3, fill=(34, 34, 44))
        t = q.title.replace('&l', '')
        import re
        t = re.sub('&.', '', t)[:18]
        if q.kind in ('hub', 'main'): d.text((x - r, y + r + 2), t[:14], fill=(220, 220, 220), font=f)
    im.save(path)


LOCKED = os.path.join(HERE, 'locked')   # chapter files as you arranged them in game; their layout always wins


def keep_user_layout(ch, d):
    """If you've arranged this chapter in game, keep your positions, sizes, shapes and lines exactly; only text,
    tasks and rewards come from here. New quests you haven't placed yet go in a column to the right, flagged."""
    p = os.path.join(LOCKED, ch.key + '.snbt')
    if not os.path.exists(p): return
    import snbt_read
    from fw import Dbl
    user, links = snbt_read.layout(p)
    xs = [u['x'] for u in user.values()] or [0]
    new = 0
    for q in d['quests']:
        u = user.get(q['id'])
        if not u:
            q['x'] = Dbl(max(xs) + 4); q['y'] = Dbl(new * 2.0); new += 1
            q['title'] = '&c[NEW] &r' + q['title']
            continue
        q['x'], q['y'] = Dbl(u['x']), Dbl(u['y'])
        for k in ('size', 'shape'):
            if u[k] is not None: q[k] = Dbl(u[k]) if k == 'size' else u[k]
        if u['deps']: q['dependencies'] = u['deps']
        else: q.pop('dependencies', None)
        if u['hide']: q['hide_dependency_lines'] = True
    if links: d['quest_links'] = links
    gone = set(user) - {q['id'] for q in d['quests']}
    print(f'  {ch.key}: kept your layout for {len(user) - len(gone)} quests, {new} new, {len(gone)} removed')


def main():
    chapters = [importlib.import_module(m).C for m in CHAPTERS]
    import guide_map
    byk = {q.key: q for ch in chapters for q in ch.quests}
    for k, page in guide_map.MAP.items():
        if k not in byk: fw.ERRORS.append(f'guide_map: no quest {k}')
        elif not byk[k].guide: byk[k].guide = page
    # links from the recipes, merged with the hand-written ones, then reduced and laid out as a flow
    import rgraph2, flow
    allq = {q.key: q for ch in chapters for q in ch.quests}
    items = {k: [t.split('*')[0] for t in q.tasks if ':' in t and t.split(':')[0] not in ('dim', 'kill', 'structure')]
             for k, q in allq.items()}
    derived = rgraph2.derive(list(allq), lambda k: items[k])
    flow.merge_deps(chapters, derived)
    flow.reduce(allq)
    for ch in chapters:
        flow.flow(ch); print(ch.key, 'line conflicts:', len(flow.conflicts(ch)))
    import gtnh_ref
    refs = gtnh_ref.chapters()
    chapters = chapters + refs
    idmap = {}
    for ch in chapters:
        for q in ch.quests:
            assert q.key not in idmap, q.key
            idmap[q.key] = q.id
    if os.path.exists(OUT): shutil.rmtree(OUT)
    os.makedirs(OUT + '/chapters'); os.makedirs(OUT + '/reward_tables'); os.makedirs(PREVIEW, exist_ok=True)
    total = 0
    for ch in chapters:
        d = fw.chapter_snbt(ch, idmap, hid('b2/group/' + ch.group) if ch.group else '')
        keep_user_layout(ch, d)
        open(f'{OUT}/chapters/{ch.key}.snbt', 'w').write(to_snbt(d) + '\n')
        total += len(ch.quests)
        if not ch.key.startswith('gtnh_'): render(ch, f'{PREVIEW}/{ch.key}.png')
        ov = fw.overlaps(ch)
        if ov: print('OVERLAP', ch.key, ov)
    open(f'{OUT}/chapter_groups.snbt', 'w').write(to_snbt({'chapter_groups': [{'id': hid('b2/group/' + g), 'title': t} for g, t in GROUPS]}) + '\n')
    for key, t in picks.tables().items():
        open(f'{OUT}/reward_tables/{key}.snbt', 'w').write(to_snbt(t) + '\n')
    data = '/mnt/user-data/uploads/Apex Ascension/config/ftbquests/quests/data.snbt'
    if os.path.exists(data): shutil.copy(data, f'{OUT}/data.snbt')
    # the pack copy: everything except the GTNH reference chapters
    REL = os.path.join(HERE, 'out', 'release', 'config', 'ftbquests', 'quests')
    if os.path.exists(REL): shutil.rmtree(REL)
    shutil.copytree(OUT, REL, ignore=lambda d, names: [n for n in names if n.startswith('gtnh_')])
    open(f'{REL}/chapter_groups.snbt', 'w').write(to_snbt({'chapter_groups': [{'id': hid('b2/group/' + g), 'title': t} for g, t in GROUPS if g != 'gtnh_ref']}) + '\n')
    print(f'{len(chapters)} chapters, {total} quests')
    for ch in chapters:
        kinds = {}
        for q in ch.quests: kinds[q.kind] = kinds.get(q.kind, 0) + 1
        print(' ', ch.key, len(ch.quests), kinds)
    groot = os.path.normpath(os.path.join(HERE, '../../../src/main/resources/assets/gtapexadditions/guides/gtapexadditions/guide'))
    for page, qid, title, chap in fw.GUIDE_LINKS:
        if not os.path.exists(os.path.join(groot, page)): fw.ERRORS.append(f'guide page missing: {page} ({title})')
    import json
    json.dump(sorted(set(fw.GUIDE_LINKS)), open(os.path.normpath(os.path.join(HERE, '../../guide/quest_links.json')), 'w'), indent=0)
    print('guide links:', len(fw.GUIDE_LINKS))
    print('missing items:', fw.MISSING + picks.MISSING)
    print('errors:', fw.ERRORS)


if __name__ == '__main__':
    main()
