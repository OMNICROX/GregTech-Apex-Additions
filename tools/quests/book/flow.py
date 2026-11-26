"""Layered (left-to-right) layout over the real dependency graph, GTNH style: every quest unlocks from the quests
whose items it is made from, columns are steps of progression, small dead-end quests hug the quest they hang off."""
import collections
from fw import KIND, ERRORS

BAN = {('steam/sulfur', 'steam/thermal'), ('steam/resistor', 'steam/boiler'), ('steam/cobbler', 'steam/boiler')}


def merge_deps(chapters, derived):
    for ch in chapters:
        keys = {q.key for q in ch.quests}
        for q in ch.quests:
            if q.kind == 'tip':
                q.deps = list(q.deps0); continue
            extra = [d for d in derived.get(q.key, []) if d in keys and (q.key, d) not in BAN]
            q.deps = list(dict.fromkeys(q.deps0 + extra))
            q.derived = set(extra) - set(q.deps0)


def reduce(allq):
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


def flow(ch, colw=3.7, gap=0.45, iters=40, only=None):
    byk = {q.key: q for q in ch.quests if only is None or q.key in only}
    par = {k: [d for d in byk[k].deps if d in byk] for k in byk}
    kids = collections.defaultdict(list)
    for k, ps in par.items():
        for p in ps: kids[p].append(k)
    # satellites: dead ends with one parent that is a big quest
    sat = {k for k in byk if not kids[k] and len(par[k]) == 1 and byk[k].kind in ('tip', 'opt', 'quest')
           and byk[par[k][0]].kind in ('hub', 'main')}
    core = [k for k in byk if k not in sat]
    # layers: longest path
    layer = {}
    def L(k):
        if k not in layer:
            ps = [p for p in par[k] if p not in sat]
            layer[k] = 0 if not ps else 1 + max(L(p) for p in ps)
        return layer[k]
    for k in core: L(k)
    # recipe-derived links that would cross half the page are dropped: the hand-written line already covers it
    changed = False
    for k in core:
        q = byk[k]
        for d in list(par[k]):
            if d in getattr(q, 'derived', ()) and d in layer and layer[k] - layer[d] > 2 and len(par[k]) > 1:
                par[k].remove(d); q.deps.remove(d); kids[d].remove(k); changed = True
    if changed:
        sat = {k for k in byk if not kids[k] and len(par[k]) == 1 and byk[k].kind in ('tip', 'opt', 'quest')
               and byk[par[k][0]].kind in ('hub', 'main')}
        core = [k for k in byk if k not in sat]
        layer.clear()
        for k in core: L(k)
    def is_sat(k):
        return not kids[k] and len(par[k]) == 1 and byk[k].kind in ('tip', 'opt', 'quest') and byk[par[k][0]].kind in ('hub', 'main')
    def relayer(shorten=True):
        nonlocal sat, core
        sat = {k for k in byk if is_sat(k)}
        core = [k for k in byk if k not in sat]
        layer.clear()
        for k in core: L(k)
        # shorten edges: move each quest within [after its parents, before its children] toward its neighbours
        for _ in range(40 if shorten else 0):
            moved = False
            for k in core:
                ps = [layer[p] for p in par[k] if p in layer]
                cs = [layer[c] for c in kids[k] if c in layer]
                if not ps and not cs: continue
                lo = max(ps) + 1 if ps else min(cs) - 1
                hi = min(cs) - 1 if cs else lo
                nb = sorted(ps + cs)
                med = nb[len(nb) // 2]
                best = min(range(lo, hi + 1), key=lambda l: (sum(abs(l - v) for v in nb), abs(l - med), l))
                if best != layer[k]: layer[k] = best; moved = True
            if not moved: break
        m0 = min(layer.values())
        for k in layer: layer[k] -= m0
    relayer(False)
    # GTNH quests hang off one or two recent steps: keep the latest prerequisites, drop far ones, but never
    # leave a quest that leads nowhere because its only onward line was cut
    for k in sorted(core, key=lambda k: layer[k]):
        q = byk[k]
        ps = sorted([p for p in par[k] if p in layer], key=lambda p: -layer[p])
        cap = 3 if q.kind in ('hub', 'main') else 2
        keep = [p for i, p in enumerate(ps) if i < cap and (i == 0 or layer[k] - layer[p] <= 3)]
        for d in ps:
            if d in keep: continue
            if len(kids[d]) == 1 and byk[d].kind in ('hub', 'main', 'quest') and d in q.deps0: continue
            par[k].remove(d); q.deps.remove(d); kids[d].remove(k)
    relayer()
    nl = max(layer.values()) + 1
    # dummy nodes for long edges
    nodes = {k: layer[k] for k in core}
    up = collections.defaultdict(list)   # node -> neighbours in previous layer
    dn = collections.defaultdict(list)
    did = 0
    for k in core:
        for p in par[k]:
            if p in sat or p not in nodes: continue
            a = p
            for l in range(layer[p] + 1, layer[k]):
                d = f'~{did}'; did += 1; nodes[d] = l
                dn[a].append(d); up[d].append(a); a = d
            dn[a].append(k); up[k].append(a)
    sats_of = collections.defaultdict(list)
    for k in byk:
        if k in sat: sats_of[par[k][0]].append(k)
    def size(n):
        return 0.25 if n.startswith('~') else KIND[byk[n].kind][0]
    def ext(n):  # half heights (top, bottom) including satellites
        s = size(n) / 2
        if n.startswith('~'): return s, s
        c = len(sats_of[n])
        off = s + 0.55 + 0.5
        top = off if c >= 1 else s
        bot = off if c >= 2 else s
        return top, bot
    rows = [[] for _ in range(nl)]
    # initial order: by file order
    order = {q.key: i for i, q in enumerate(ch.quests)}
    for n, l in nodes.items(): rows[l].append(n)
    for r in rows: r.sort(key=lambda n: order.get(n, 0))
    pos = {}
    def renum():
        for r in rows:
            for i, n in enumerate(r): pos[n] = i
    renum()
    def cross():
        c = 0
        for l in range(1, nl):
            es = [(pos[a], pos[b]) for b in rows[l] for a in up[b]]
            for i in range(len(es)):
                for j in range(i + 1, len(es)):
                    if (es[i][0] - es[j][0]) * (es[i][1] - es[j][1]) < 0: c += 1
        return c
    best = (cross(), [list(r) for r in rows])
    for it in range(iters):
        rng = range(1, nl) if it % 2 == 0 else range(nl - 2, -1, -1)
        for l in rng:
            nb = up if it % 2 == 0 else dn
            def bc(n):
                v = [pos[m] for m in nb[n]]
                return sum(v) / len(v) if v else pos[n]
            rows[l].sort(key=bc)
            for i, n in enumerate(rows[l]): pos[n] = i
        c = cross()
        if c < best[0]: best = (c, [list(r) for r in rows])
    rows = best[1]; renum()
    def lcross(l):
        c = 0
        for ll, nbm in ((l, up), (l + 1, up)):
            if ll < 1 or ll >= nl: continue
            es = [(pos[a], pos[b]) for b in rows[ll] for a in nbm[b]]
            for i in range(len(es)):
                for j in range(i + 1, len(es)):
                    if (es[i][0] - es[j][0]) * (es[i][1] - es[j][1]) < 0: c += 1
        return c
    for _ in range(12):
        imp = False
        for l in range(nl):
            r = rows[l]
            for i in range(len(r) - 1):
                before = lcross(l)
                r[i], r[i + 1] = r[i + 1], r[i]; pos[r[i]], pos[r[i + 1]] = i, i + 1
                if lcross(l) < before: imp = True
                else:
                    r[i], r[i + 1] = r[i + 1], r[i]; pos[r[i]], pos[r[i + 1]] = i, i + 1
        if not imp: break
    best = (cross(), rows)
    # y coordinates: relax toward neighbours, keep order and spacing
    y = {}
    for r in rows:
        acc = 0
        for n in r:
            t, b = ext(n); acc += t; y[n] = acc; acc += b + gap
    def pack(r, want):
        # minimal-movement packing with order preserved (pool adjacent violators)
        blocks = []
        for n in r:
            t, b = ext(n)
            blocks.append([[n], want[n], t, b])
            while len(blocks) > 1:
                A, B = blocks[-2], blocks[-1]
                # position of first element of block; block spans
                def span(bl):
                    off = [0]; 
                    for i in range(1, len(bl[0])):
                        p, q_ = bl[0][i - 1], bl[0][i]
                        off.append(off[-1] + ext(p)[1] + gap + ext(q_)[0])
                    return off
                oa = span(A)
                endA = A[1] + oa[-1] + ext(A[0][-1])[1]
                startB = B[1] - ext(B[0][0])[0]
                if endA + gap <= startB + 1e-9: break
                merged = A[0] + B[0]
                M = [merged, 0, 0, 0]
                om = span(M)
                M[1] = sum(want[m] - o for m, o in zip(merged, om)) / len(merged)
                blocks[-2:] = [M]
        for bl in blocks:
            off = [0]
            for i in range(1, len(bl[0])):
                p, q_ = bl[0][i - 1], bl[0][i]
                off.append(off[-1] + ext(p)[1] + gap + ext(q_)[0])
            for m, o in zip(bl[0], off): y[m] = bl[1] + o
    for it in range(60):
        for l in (range(nl) if it % 2 else range(nl - 1, -1, -1)):
            want = {}
            for n in rows[l]:
                nb = up[n] + dn[n]
                want[n] = (sum(y[m] for m in nb) / len(nb)) if nb else y[n]
                if nb and n.startswith('~'): want[n] = sum(y[m] for m in nb) / len(nb)
            pack(rows[l], want)
    # place
    for n, l in nodes.items():
        if n.startswith('~'): continue
        q = byk[n]; q.x, q.y = round(l * colw * 4) / 4, round(y[n] * 4) / 4
    for m, ks in sats_of.items():
        mq = byk[m]
        s = KIND[mq.kind][0] / 2
        off = s + 0.55
        slots = [(0, -off), (0, off), (1.05, -off), (1.05, off), (-1.05, -off), (-1.05, off),
                 (2.1, -off), (2.1, off), (-2.1, -off), (-2.1, off)]
        if len(ks) == 3: slots = [(-0.55, -off), (0, off), (0.55, -off)]
        if len(ks) == 4: slots = [(-0.55, -off), (-0.55, off), (0.55, -off), (0.55, off)]
        if len(ks) in (5, 6): slots = [(-1.05, -off), (-1.05, off), (0, -off), (0, off), (1.05, -off), (1.05, off)]
        for k, (dx, dy) in zip(ks, slots):
            byk[k].x, byk[k].y = mq.x + dx, mq.y + dy
        if len(ks) > len(slots): ERRORS.append(f'too many satellites on {m}')
    return best[0]


def cluster(ch, center, pitch=2.0, iters=40000, seed=1, prune=True):
    """GTNH 'Welcome to Tier One' style: one line into a big centre quest, and everything that grows out of it
    packed on a tight grid around it, placed so lines stay short and do not run over other quests."""
    import random, math
    rnd = random.Random(seed)
    byk = {q.key: q for q in ch.quests}
    center = f'{ch.key}/{center}'
    kids = collections.defaultdict(list)
    for q in ch.quests:
        for d in q.deps:
            if d in byk: kids[d].append(q.key)
    # everything downstream of the centre
    down, st = set(), [center]
    while st:
        for c in kids[st.pop()]:
            if c not in down: down.add(c); st.append(c)
    rest = [q.key for q in ch.quests if q.key not in down]
    # one line into the cluster: inside it, quests only hang off each other and the centre
    for k in down:
        q = byk[k]
        q.deps = [d for d in q.deps if d not in byk or d in down or d == center]
        if not any(d in byk for d in q.deps): q.deps.append(center)
    kids = collections.defaultdict(list)
    for q in ch.quests:
        for d in q.deps:
            if d in byk: kids[d].append(q.key)
    # trim the cluster's lines like flow() does: at most 2-3 links, keep the most recent ones
    if prune:
        from functools import lru_cache
        @lru_cache(None)
        def depth(k):
            ps = [d for d in byk[k].deps if d in down]
            return 1 + max([depth(d) for d in ps], default=0)
        for k in down:
            q = byk[k]
            inner = sorted([d for d in q.deps if d in byk], key=lambda d: -(depth(d) if d in down else 0))
            cap = 3 if q.kind in ('hub', 'main') else 2
            for d in inner[cap:]: q.deps.remove(d)
    flow(ch, only=set(rest))
    cq = byk[center]
    cq.kind = 'hub'
    left = max(byk[k].x + KIND[byk[k].kind][0] / 2 for k in rest)
    feeders = [d for d in cq.deps if d in byk]
    cy = sum(byk[d].y for d in feeders) / len(feeders) if feeders else 0
    nodes = sorted(down, key=lambda k: [q.key for q in ch.quests].index(k))
    R = math.ceil(math.sqrt(len(nodes) + 1) / 2) + 1
    fx = max((byk[d].x for d in feeders), default=left)
    cx = fx + 4.5
    H = 3
    W = math.ceil(1.5 * len(nodes) / (2 * H + 1))
    cells = [(i, j) for i in range(-1, W) for j in range(-H, H + 1) if (i, j) != (0, 0) and not (j == 0 and i < 0)]
    # the centre's own big neighbours need room: hubs take one cell plus clear diagonal space by size alone
    P = lambda c: (cx + c[0] * pitch, cy + c[1] * pitch)
    from fw import _seg_dist as _sd
    fedges = [(byk[a], byk[b]) for b in rest for a in byk[b].deps if a in byk and a in rest]
    def clear(c):
        x, y = P(c)
        for k in rest:
            q = byk[k]; s_ = KIND[q.kind][0]
            if abs(x - q.x) < (s_ + 1.75) / 2 + 0.5 and abs(y - q.y) < (s_ + 1.75) / 2 + 0.3: return False
        return all(_sd(x, y, a.x, a.y, b.x, b.y) > 1.3 for a, b in fedges)
    cells = [c for c in cells if clear(c)]
    while len(cells) < 1.4 * len(nodes):
        W += 1
        cells += [c for c in ((W - 1, j) for j in range(-H, H + 1)) if clear(c)]
    cells.sort(key=lambda c: (c[0] ** 2 + c[1] ** 2, -c[0]))
    pos = {k: (byk[k].x, byk[k].y) for k in rest}
    pos[center] = (cx, cy)
    # start: breadth-first from the centre, nearest cells first
    from functools import lru_cache
    @lru_cache(None)
    def dd(k):
        ps = [d for d in byk[k].deps if d in down]
        return 1 + max([dd(d) for d in ps], default=0)
    order = sorted(nodes, key=lambda k: (dd(k), byk[k].kind not in ('hub', 'main')))
    cell = {k: cells[i] for i, k in enumerate(order)}
    free = cells[len(order):]
    edges = [(d, q.key) for q in ch.quests for d in q.deps if d in byk and (q.key in down or d in down)]
    inc = collections.defaultdict(list)
    for e in edges:
        inc[e[0]].append(e); inc[e[1]].append(e)
    def xy(k): return P(cell[k]) if k in cell else pos[k]
    def seg_pen(a, b, skip):
        (ax, ay), (bx, by) = xy(a), xy(b)
        pen = 0
        for k, c in cell.items():
            if k in skip: continue
            px, py = P(c)
            if min(ax, bx) - 1 < px < max(ax, bx) + 1 and min(ay, by) - 1 < py < max(ay, by) + 1:
                r = KIND[byk[k].kind][0] / 2 + 0.1
                from fw import _seg_dist  # noqa
                if _seg_dist(px, py, ax, ay, bx, by) < r: pen += 1
        return pen
    def ecost(e):
        (ax, ay), (bx, by) = xy(e[0]), xy(e[1])
        L = math.hypot(ax - bx, ay - by)
        back = 0
        if e[0] in cell and e[1] in cell:   # children sit further out than parents
            ra = math.hypot(*cell[e[0]]); rb = math.hypot(*cell[e[1]])
            back = max(0, ra - rb + 0.5) * 1.5
        return L + back + 6 * seg_pen(e[0], e[1], set(e))
    def node_block(k):   # lines from elsewhere that run over this quest
        if k not in cell: return 0
        px, py = P(cell[k]); r = KIND[byk[k].kind][0] / 2 + 0.1
        from fw import _seg_dist
        n = 0
        for a, b in edges:
            if k in (a, b): continue
            (ax, ay), (bx, by) = xy(a), xy(b)
            if min(ax, bx) - 1 < px < max(ax, bx) + 1 and min(ay, by) - 1 < py < max(ay, by) + 1 and _seg_dist(px, py, ax, ay, bx, by) < r: n += 1
        return 6 * n
    def big_clash(k):  # two big quests side by side squeeze; keep mains off each other's 8-neighbourhood a bit
        if k not in cell or byk[k].kind not in ('hub', 'main'): return 0
        i, j = cell[k]; n = 0
        for o, (a, b) in cell.items():
            if o != k and byk[o].kind in ('hub', 'main') and max(abs(a - i), abs(b - j)) == 1: n += 1
        return 0.8 * n
    def local(ks):
        es = set(e for k in ks for e in inc[k])
        return sum(ecost(e) for e in es) + sum(node_block(k) + big_clash(k) for k in ks)
    T0 = 3.0
    for it in range(iters):
        T = T0 * (1 - it / iters) + 0.02
        a = rnd.choice(nodes)
        if rnd.random() < 0.3 and free:
            fi = rnd.randrange(len(free))
            before = local([a]); old = cell[a]; cell[a] = free[fi]
            after = local([a])
            if after <= before or rnd.random() < math.exp((before - after) / T): free[fi] = old
            else: cell[a] = old
        else:
            b = rnd.choice(nodes)
            if a == b: continue
            before = local([a, b]); cell[a], cell[b] = cell[b], cell[a]
            after = local([a, b])
            if not (after <= before or rnd.random() < math.exp((before - after) / T)):
                cell[a], cell[b] = cell[b], cell[a]
    minx = min(P(cell[k])[0] - KIND[byk[k].kind][0] / 2 for k in nodes)
    minx = min(minx, cx - KIND['hub'][0] / 2)
    shift = 0
    from fw import _seg_dist
    flow_edges = [(byk[a], byk[b]) for b in rest for a in byk[b].deps if a in byk and a in rest]
    def ok(sh):
        pts = [(P(cell[k])[0] + sh, P(cell[k])[1], KIND[byk[k].kind][0]) for k in nodes] + [(cx + sh, cy, KIND['hub'][0])]
        for k in rest:
            q = byk[k]; s_ = KIND[q.kind][0]
            for x, y, sz in pts:
                if abs(x - q.x) < (s_ + sz) / 2 + 1.2 and abs(y - q.y) < (s_ + sz) / 2 + 0.6: return False
        for a, b in flow_edges:
            for x, y, sz in pts:
                if _seg_dist(x, y, a.x, a.y, b.x, b.y) < sz / 2 + 0.4: return False
        return True
    cq.x, cq.y = cx + shift, cy
    for k in nodes:
        x, y = P(cell[k]); byk[k].x, byk[k].y = x + shift, y
    tot = sum(seg_pen(a, b, {a, b}) for a, b in edges)
    return tot
