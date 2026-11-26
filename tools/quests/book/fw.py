"""Framework for the hand-written Apex Ascension quest book (GTNH style).

A chapter is a list of quests placed by hand. Kinds:
  hub   - a big gateway quest a whole area hangs off (size 2, gear)
  main  - a main-line quest (size 1.5, hexagon)
  quest - a normal quest (size 1)
  opt   - optional: nice to have, not needed for progress (size 1, optional)
  tip   - a lesson with a checkmark (size 1, rsquare)
Tasks: 'item', 'item*N', 'check', 'dim:<id>', 'kill:<entity>*N', 'structure:<id>'.
Descriptions: a list of paragraphs. A paragraph starting with '>' is a tip (grey italic).
  ('link', 'quest key', 'text') makes a clickable line that opens that quest.
"""
import json, re, sys, os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from core import hid, table_hex, to_snbt, Long, Dbl, NAMES, name, exists  # noqa

TN = ['ULV', 'LV', 'MV', 'HV', 'EV', 'IV', 'LuV', 'ZPM', 'UV', 'UHV', 'UEV', 'UIV', 'UXV', 'OpV', 'MAX']
TP = ['ulv', 'lv', 'mv', 'hv', 'ev', 'iv', 'luv', 'zpm', 'uv', 'uhv', 'uev', 'uiv', 'uxv', 'opv', 'max']
MISSING, ERRORS = [], []
KIND = {'hub': (2.25, 'gear'), 'main': (1.75, 'square'), 'quest': (1.0, 'square'), 'opt': (1.0, 'circle'), 'tip': (1.0, 'rsquare')}


class Q:
    def __init__(self, key, title, tasks, desc, deps=(), at=(0, 0), kind='quest', bag=None, pick=None, xp=None,
                 any_dep=False, icon=None, hide=False, sub=None):
        self.key, self.title, self.tasks, self.desc = key, title, list(tasks), desc
        self.deps = list(deps)
        self.x, self.y = float(at[0]), float(at[1])
        self.kind, self.bag, self.pick, self.xp = kind, bag, pick, xp
        self.any_dep, self.icon, self.hide, self.sub = any_dep, icon, hide, sub

    @property
    def id(self):
        return hid('b2/' + self.key)


class Chapter:
    def __init__(self, key, title, icon, group, order, subtitle, tier_bag, tier_pick, xp_base=10):
        self.key, self.title, self.icon, self.group, self.order, self.subtitle = key, title, icon, group, order, subtitle
        self.tier_bag, self.tier_pick, self.xp_base = tier_bag, tier_pick, xp_base
        self.quests = []

    def q(self, key, *a, **k):
        q = Q(f'{self.key}/{key}', *a, **k)
        q.deps = [d if '/' in d else f'{self.key}/{d}' for d in q.deps]
        q.deps0 = list(q.deps)
        self.quests.append(q)
        return q


def _task(qid, i, t):
    tid = hid(f'{qid}/t{i}')
    if t == 'check': return {'id': tid, 'type': 'checkmark'}
    if t.startswith('dim:'): return {'id': tid, 'type': 'dimension', 'dimension': t[4:]}
    if t.startswith('structure:'): return {'id': tid, 'type': 'structure', 'structure': t[10:]}
    if t.startswith('kill:'):
        e, n = (t[5:].split('*') + ['1'])[:2]
        return {'id': tid, 'type': 'kill', 'entity': e, 'value': Long(int(n))}
    item, n = (t.split('*') + ['1'])[:2]
    d = {'id': tid, 'type': 'item', 'item': item}
    if int(n) > 1: d['count'] = Long(int(n))
    return d


def _desc(desc, idmap, key):
    if isinstance(desc, str): desc = [desc]
    out = []
    for i, p in enumerate(desc):
        if i: out.append('')
        if isinstance(p, tuple) and p[0] == 'link':
            target = idmap.get(p[1])
            if not target:
                ERRORS.append(f'{key}: link to unknown quest {p[1]}')
                out.append(p[2]); continue
            out.append(json.dumps({'text': '➤ ' + p[2], 'color': 'aqua', 'underlined': True,
                                   'clickEvent': {'action': 'change_page', 'value': target}}))
        elif p.startswith('>'):
            out.append('&7&o' + p[1:].strip())
        else:
            out.append(p)
    return out


def quest_snbt(ch, q, idmap):
    tasks = []
    for t in q.tasks:
        base = t.split('*')[0]
        if t == 'check' or ':' in t and t.split(':')[0] in ('dim', 'kill', 'structure'):
            tasks.append(t)
        elif exists(base):
            tasks.append(t)
        else:
            MISSING.append((q.key, t))
    if not tasks: tasks = ['check']
    size, shape = KIND[q.kind]
    d = {'id': q.id, 'x': Dbl(q.x), 'y': Dbl(q.y), 'title': q.title, 'description': _desc(q.desc, idmap, q.key),
         'tasks': [_task(q.id, i, t) for i, t in enumerate(tasks)], 'size': Dbl(size)}
    if shape: d['shape'] = shape
    if q.sub: d['subtitle'] = q.sub
    deps = []
    for k in q.deps:
        if k in idmap: deps.append(idmap[k])
        else: ERRORS.append(f'{q.key}: unknown dependency {k}')
    if deps: d['dependencies'] = deps
    if q.any_dep and len(deps) > 1: d['dependency_requirement'] = 'one_completed'
    if q.kind == 'opt': d['optional'] = True
    if q.hide: d['hide_dependency_lines'] = True
    if q.icon: d['icon'] = q.icon
    rew = []
    xp = q.xp if q.xp is not None else ch.xp_base * {'hub': 5, 'main': 3, 'quest': 2, 'opt': 1, 'tip': 1}[q.kind]
    if xp: rew.append({'id': hid(q.id + '/xp'), 'type': 'xp', 'xp': xp})
    bag = q.bag if q.bag is not None else (ch.tier_bag if q.kind in ('hub', 'main') else None)
    if bag: rew.append({'id': hid(q.id + '/bag'), 'type': 'random', 'table_id': Long(int(table_hex(bag), 16))})
    pick = q.pick if q.pick is not None else (ch.tier_pick if q.kind in ('hub', 'main', 'quest') else None)
    if pick: rew.append({'id': hid(q.id + '/pick'), 'type': 'choice', 'table_id': Long(int(table_hex(pick), 16))})
    if rew: d['rewards'] = rew
    d['min_width'] = 300
    return d


def chapter_snbt(ch, idmap, group_id):
    d = {'default_hide_dependency_lines': False, 'default_quest_shape': '', 'filename': ch.key, 'group': group_id,
         'icon': ch.icon, 'id': hid('b2/chapter/' + ch.key), 'order_index': ch.order, 'quest_links': [],
         'quests': [quest_snbt(ch, q, idmap) for q in ch.quests], 'title': ch.title}
    if ch.subtitle: d['subtitle'] = [ch.subtitle]
    return d


def overlaps(ch):
    out = []
    qs = ch.quests
    for i in range(len(qs)):
        for j in range(i + 1, len(qs)):
            a, b = qs[i], qs[j]
            r = (KIND[a.kind][0] + KIND[b.kind][0]) / 2 * 0.95
            if abs(a.x - b.x) < r and abs(a.y - b.y) < r: out.append((a.key, b.key))
    return out


# ---------------------------------------------------------------- GTNH-style layout
SLOTS = [(0, -1.4), (0, 1.4), (1.05, -1.4), (1.05, 1.4), (-1.05, -1.4), (-1.05, 1.4),   # just above / below
         (2.0, -1.4), (2.0, 1.4), (-2.0, -1.4), (-2.0, 1.4)]                                # between two main quests


def layout(ch, rows, sats, col=4.0, row=2.8, hubs=(), extra=None, keep_cross=()):
    """rows: lists of main-quest keys; rows snake (left to right, then right to left...). Each main quest
    depends on the one before it, so the main line reads as one path. sats: {main: [side quest keys]}: side
    quests sit right beside their main quest and depend on it (and on side quests of the same main they list).
    extra: {key: [deps]} extra dependencies to keep. keep_cross: side quests whose own dependencies are kept."""
    extra = extra or {}
    full = lambda k: k if '/' in k else f'{ch.key}/{k}'
    byk = {q.key: q for q in ch.quests}
    order = []
    for r, keys in enumerate(rows):
        cols = list(range(len(keys)))
        if r % 2: cols = [max(len(rr) for rr in rows) - 1 - c for c in cols]
        for c, k in zip(cols, keys):
            q = byk[full(k)]
            q.x, q.y = c * col, r * row
            q.kind = 'hub' if k in hubs else 'main'
            order.append(q)
    first_deps = [d for d in order[0].deps if not d.startswith(ch.key + '/')]
    for i, q in enumerate(order):
        q.deps = (first_deps if i == 0 else [order[i - 1].key]) + [full(d) for d in extra.get(q.key.split('/', 1)[1], [])]
        q.hide = False
    taken = [(q.x, q.y, KIND[q.kind][0]) for q in order]
    placed = set(q.key for q in order)
    for m, keys in sats.items():
        mq = byk[full(m)]
        own = set(full(k) for k in keys)
        for k in keys:
            q = byk[full(k)]
            if q.kind in ('hub', 'main'): q.kind = 'quest'
            if q.key not in [full(x) for x in keep_cross]:
                q.deps = [mq.key]
            for dx, dy in SLOTS:
                x, y = mq.x + dx, mq.y + dy
                if all(abs(x - tx) >= (1 + ts) / 2 + 0.02 or abs(y - ty) >= (1 + ts) / 2 + 0.02 for tx, ty, ts in taken):
                    q.x, q.y = x, y
                    taken.append((x, y, 1.0))
                    break
            else:
                ERRORS.append(f'no room next to {m} for {k}')
            placed.add(q.key)
    for q in ch.quests:
        if q.key not in placed: ERRORS.append(f'not laid out: {q.key}')


def place(ch, cells, deps, sats, col=4.0, row=3.0, hubs=(), sat_deps=None):
    """Hand-placed lattice (GTNH style). cells: {main: (col, row)}; deps: {main: [mains it needs]} (the lines
    of the tree); sats: {main: [side quests]} placed tight around it. sat_deps: {side: [extra deps]}."""
    sat_deps = sat_deps or {}
    full = lambda k: k if '/' in k else f'{ch.key}/{k}'
    byk = {q.key: q for q in ch.quests}
    for k, (c, r) in cells.items():
        q = byk[full(k)]
        q.x, q.y = c * col, r * row
        q.kind = 'hub' if k in hubs else 'main'
        cross = [d for d in q.deps if not d.startswith(ch.key + '/')]
        q.deps = cross + [full(d) for d in deps.get(k, [])]
        q.hide = False
    layout_sats(ch, byk, sats, sat_deps, full)


def _seg_dist(px, py, ax, ay, bx, by):
    dx, dy = bx - ax, by - ay
    t = 0 if dx == dy == 0 else max(0, min(1, ((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy)))
    return ((px - ax - t * dx) ** 2 + (py - ay - t * dy) ** 2) ** 0.5


def layout_sats(ch, byk, sats, sat_deps, full):
    mains = [q for q in ch.quests if q.kind in ('hub', 'main')]
    taken = [(q.x, q.y, KIND[q.kind][0]) for q in mains]
    segs = [(q.x, q.y, byk[d].x, byk[d].y) for q in mains for d in q.deps if d in byk and byk[d].kind in ('hub', 'main')]
    placed = set(q.key for q in mains)
    for m, keys in sats.items():
        mq = byk[full(m)]
        off = KIND[mq.kind][0] / 2 + 0.55
        ring = [(0, -off), (0, off), (1.05, -off), (-1.05, off), (-1.05, -off), (1.05, off),
                (off, -1.05), (-off, 1.05), (-off, -1.05), (off, 1.05), (off, 0), (-off, 0), (0, -off),
                (2.1, -off), (-2.1, off), (-2.1, -off), (2.1, off),
                (off + 1.05, -1.05), (-off - 1.05, 1.05), (-off - 1.05, -1.05), (off + 1.05, 1.05),
                (off + 1.05, 0), (-off - 1.05, 0), (2.1, -off - 1.05), (-2.1, off + 1.05)]
        for k in keys:
            q = byk[full(k)]
            if q.kind in ('hub', 'main'): q.kind = 'quest'
            q.deps = [mq.key] + [full(d) for d in sat_deps.get(k, [])]
            for dx, dy in ring:
                x, y = mq.x + dx, mq.y + dy
                if not all(abs(x - tx) >= (1 + ts) / 2 + 0.02 or abs(y - ty) >= (1 + ts) / 2 + 0.02 for tx, ty, ts in taken):
                    continue
                if any(_seg_dist(x, y, *sg) < 0.62 for sg in segs):
                    continue
                q.x, q.y = x, y
                taken.append((x, y, 1.0)); segs.append((x, y, mq.x, mq.y)); break
            else:
                ERRORS.append(f'no room next to {m} for {k}')
            placed.add(q.key)
    for q in ch.quests:
        if q.key not in placed: ERRORS.append(f'not laid out: {q.key}')
