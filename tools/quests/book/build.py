"""Builds the hand-written book into out/config/ftbquests/quests, checks it, and renders a layout preview per chapter."""
import os, sys, shutil, importlib
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import fw
from fw import hid, to_snbt, table_hex, exists, name
import picks

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, 'out', 'config', 'ftbquests', 'quests')
PREVIEW = os.path.join(HERE, 'out', 'preview')
CHAPTERS = ['ch_stone', 'ch_steam']
GROUPS = []   # side groups come later


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


def main():
    chapters = [importlib.import_module(m).C for m in CHAPTERS]
    # transitive reduction: drop a dependency another dependency already implies (fewer, clearer lines)
    allq = {q.key: q for ch in chapters for q in ch.quests}
    from functools import lru_cache
    @lru_cache(None)
    def anc(k):
        out = set()
        for d in allq[k].deps if k in allq else []:
            out.add(d); out |= anc(d)
        return frozenset(out)
    for q in allq.values():
        if q.any_dep: continue
        q.deps = [d for d in q.deps if not any(d in anc(o) for o in q.deps if o != d)]
    idmap = {}
    for ch in chapters:
        for q in ch.quests:
            assert q.key not in idmap, q.key
            idmap[q.key] = q.id
    if os.path.exists(OUT): shutil.rmtree(OUT)
    os.makedirs(OUT + '/chapters'); os.makedirs(OUT + '/reward_tables'); os.makedirs(PREVIEW, exist_ok=True)
    total = 0
    for ch in chapters:
        open(f'{OUT}/chapters/{ch.key}.snbt', 'w').write(to_snbt(fw.chapter_snbt(ch, idmap, '')) + '\n')
        total += len(ch.quests)
        render(ch, f'{PREVIEW}/{ch.key}.png')
        ov = fw.overlaps(ch)
        if ov: print('OVERLAP', ch.key, ov)
    open(f'{OUT}/chapter_groups.snbt', 'w').write(to_snbt({'chapter_groups': [{'id': hid('b2/group/' + g), 'title': t} for g, t in GROUPS]}) + '\n')
    for key, t in picks.tables().items():
        open(f'{OUT}/reward_tables/{key}.snbt', 'w').write(to_snbt(t) + '\n')
    data = '/mnt/user-data/uploads/Apex Ascension/config/ftbquests/quests/data.snbt'
    if os.path.exists(data): shutil.copy(data, f'{OUT}/data.snbt')
    print(f'{len(chapters)} chapters, {total} quests')
    for ch in chapters:
        kinds = {}
        for q in ch.quests: kinds[q.kind] = kinds.get(q.kind, 0) + 1
        print(' ', ch.key, len(ch.quests), kinds)
    print('missing items:', fw.MISSING + picks.MISSING)
    print('errors:', fw.ERRORS)


if __name__ == '__main__':
    main()
