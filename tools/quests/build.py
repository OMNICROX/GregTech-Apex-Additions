"""Builds the Apex Ascension quest book into out/config/ftbquests/quests and checks it."""
import os, shutil, json, collections, sys
from core import *
import ch_early, ch_tiers, ch_end, ch_side, ch_import, rewards

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'out', 'config', 'ftbquests', 'quests')

GROUPS = [('tech', 'Tech \\& Storage'), ('magic', 'Magic'), ('heroes', 'Heroes'), ('explore', 'Worlds \\& Space'),
          ('mobs', 'Monsters \\& Bosses'), ('gear', 'Gear \\& Treasure'), ('life', 'Building, Food \\& Fun'), ('endgame', 'Endgame')]


def main():
    steam_multis = [m for m in ch_tiers.INV if m['multi'] and m['exists'] and ch_tiers.multi_tier(m) == 0
                    and m['id'] in ch_tiers.PRODUCIBLE and not ch_tiers.DISABLED_MULTIS.search(m['id'])]
    mine = [ch_early.welcome(), ch_early.stone(), ch_early.steam(steam_multis, lambda m: ch_tiers.mtext(m)[0])]
    mine += ch_tiers.chapters() + [ch_end.ascension(), ch_end.cats()] + ch_side.chapters()
    imported = ch_import.chapters()

    # every quest id in the book, for dependency checks
    ids = set()
    for ch in mine:
        for q in ch.quests:
            assert q.id not in ids, ('duplicate quest', q.key)
            ids.add(q.id)
    for c in imported:
        for q in c['quests']:
            assert q['id'] not in ids, ('duplicate quest', q['id'])
            ids.add(q['id'])
    known_keys = {q.id: q.key for ch in mine for q in ch.quests}

    missing_deps = collections.Counter()
    for ch in mine:
        for q in ch.quests:
            for d in q.deps:
                if hid(d) not in ids: missing_deps[d] += 1

    if os.path.exists(OUT): shutil.rmtree(OUT)
    os.makedirs(OUT + '/chapters'); os.makedirs(OUT + '/reward_tables')
    total = 0
    group_id = lambda g: '' if g == 'main' else hid('group/' + g)
    for ch in mine:
        qs = [quest_snbt(q, ids) for q in ch.quests]
        total += len(qs)
        d = {'default_hide_dependency_lines': False, 'default_quest_shape': '', 'filename': ch.key, 'group': group_id(ch.group),
             'icon': ch.icon, 'id': hid('chapter/' + ch.key), 'order_index': ch.order, 'quest_links': [], 'quests': qs, 'title': ch.title}
        if ch.subtitle: d['subtitle'] = [ch.subtitle]
        open(f'{OUT}/chapters/{ch.key}.snbt', 'w').write(to_snbt(d) + '\n')
    for c in imported:
        for q in c['quests']:
            q['dependencies'] = [x for x in q.get('dependencies', []) if x in ids]
            if not q['dependencies']: del q['dependencies']
        total += len(c['quests'])
        open(f'{OUT}/chapters/{c["filename"]}.snbt', 'w').write(to_snbt(c) + '\n')
    groups = {'chapter_groups': [{'id': hid('group/' + g), 'title': t} for g, t in GROUPS]}
    open(f'{OUT}/chapter_groups.snbt', 'w').write(to_snbt(groups) + '\n')
    for key, t in rewards.tables().items():
        open(f'{OUT}/reward_tables/{key}.snbt', 'w').write(to_snbt(t) + '\n')
    data = open('/mnt/user-data/uploads/Apex Ascension/config/ftbquests/quests/data.snbt').read()
    open(f'{OUT}/data.snbt', 'w').write(data)

    # checks
    used_tables = set()
    for f in os.listdir(OUT + '/chapters'):
        s = open(f'{OUT}/chapters/{f}').read()
        used_tables |= set(int(x) for x in __import__('re').findall(r'table_id: (\d+)L', s))
    have_tables = {int(table_hex(k), 16) for k in rewards.BAGS}
    print(f'{len(mine) + len(imported)} chapters, {total} quests')
    print('per chapter:', ', '.join(f'{ch.key}={len(ch.quests)}' for ch in mine))
    print('imported:', ', '.join(f'{c["filename"]}={len(c["quests"])}' for c in imported))
    print('missing items:', sorted(MISSING))
    print('missing deps:', dict(missing_deps))
    print('unknown reward tables:', used_tables - have_tables)
    # cycles
    graph = {}
    for ch in mine:
        for q in ch.quests: graph[q.id] = [hid(d) for d in q.deps if hid(d) in ids]
    for c in imported:
        for q in c['quests']: graph[q['id']] = list(q.get('dependencies', []))
    state = {}

    def dfs(n, path):
        if state.get(n) == 1: raise SystemExit(f'cycle: {[known_keys.get(p, p) for p in path]}')
        if state.get(n) == 2: return
        state[n] = 1
        for m in graph.get(n, []): dfs(m, path + [m])
        state[n] = 2
    sys.setrecursionlimit(10000)
    for n in graph: dfs(n, [n])
    print('no cycles')


if __name__ == '__main__':
    main()
