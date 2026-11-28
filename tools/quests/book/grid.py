"""GTNH-style layout: one obvious path of main quests, side quests packed around them.

The path. In a tier chapter the big quests you need for the next tier (everything the door quest rests on) are put in
one order, the order they are written in wherever the requirements allow, and each is made to require the one before.
They run along one straight row, left to right, so the page reads as: do this, then this, then this. Big quests that are
not needed for the door (optional goals) branch off the one they follow from and run along a row of their own. In a
chapter with several independent lines (magic, worlds, ...) every line is such a row.

The side quests. Every small quest belongs to one big quest: the first one that needs it (it is preparation for that
quest and sits just before it), or else the last one it follows from (it is an extra and sits around it). It is placed
in the free cell nearest to what it links to, GTNH's rosette, and a search then moves side quests around for as long as
that makes links shorter, less crossed and the page smaller.

Links. A link that would run across the page is left out: if a quest has a requirement nearby the far one is dropped
(the path already forces it in nearly every case), and if all of its requirements are far away it keeps them but does
not draw them (GTNH does the same with requirements from other tabs).

Everything sits on one grid. A column is as wide as its biggest quest and a row as tall, with half a small quest of
clear space between (the density of GTNH's own tabs: 24 px quests on a 36 px pitch); empty rows and columns take no
room. A link running over a quest that is not one of its ends costs more than anything else in the search."""
import hashlib, json, math, os, random, time
import numpy as np
from fw import KIND

GX = 0.125  # clear space between two columns: quests stand almost shoulder to shoulder, as on the hand-made Stone page
GY = 0.125  # clear space between two rows
LINK = 0.75 # the least room between two big quests on a line, so the link between them shows
OVER, CLIP, CROSS = 300.0, 2.5, 3.0   # a link through a quest / clipping a quest's edge / crossing another link
BAND = 7    # grid rows between two rows of big quests (unused ones take no room)
BUDGET = 6.0   # seconds of search per chapter and seed
CACHE = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'out', 'layout_cache.json')


def layout(ch, one_path=True, seeds=(1, 2)):
    sig = hashlib.sha1(json.dumps([[q.key, q.kind, list(q.deps), bool(q.hide), bool(q.any_dep)] for q in ch.quests]
                                  + [one_path]).encode() + open(__file__, 'rb').read()).hexdigest()
    plan = prepare(ch, one_path)
    try: cache = json.load(open(CACHE))
    except Exception: cache = {}
    hit = cache.get(ch.key)
    if hit and hit['sig'] == sig:
        pos = hit['pos']
    else:
        best = None
        for sd in seeds:
            c, pos = _layout(ch, plan, sd)
            if best is None or c < best[0] - 1e-6: best = (c, pos)
        pos = best[1]
        cache[ch.key] = {'sig': sig, 'pos': pos}
        os.makedirs(os.path.dirname(CACHE), exist_ok=True)
        json.dump(cache, open(CACHE, 'w'))
    for q in ch.quests: q.x, q.y = pos[q.key]
    return plan


def prepare(ch, one_path=True):
    """Works out the path and the clusters, and edits the requirements to match (see the top of the file)."""
    qs = ch.quests
    byk = {q.key: q for q in qs}
    order = {q.key: i for i, q in enumerate(qs)}
    root = qs[0].key
    big = {k: byk[k].kind in ('hub', 'main') or k == root for k in byk}
    ins = {q.key: [d for d in q.deps if d in byk and d != q.key] for q in qs}
    def ancestors():
        memo = {}
        def A(k):
            if k not in memo:
                memo[k] = set()
                s = set()
                for d in ins[k]: s.add(d); s |= A(d)
                memo[k] = s
            return memo[k]
        return {k: A(k) for k in byk}
    def link(a, b):
        if a not in byk[b].deps: byk[b].deps.append(a)
        if a not in ins[b]: ins[b].append(a)
    anc = ancestors()

    spine = [root]
    if one_path:
        used = {d for k in byk for d in ins[k]}
        ends = [k for k in byk if big[k] and k not in used and k != root]
        door = max(ends, key=lambda k: (byk[k].kind == 'hub', len(anc[k])))
        left = {k for k in byk if big[k] and (k == door or k in anc[door])} | {root}
        spine = []
        while left:   # written order wherever the requirements allow
            k = min((k for k in left if not (anc[k] & left)), key=lambda k: (k != root, order[k]))
            spine.append(k); left.remove(k)
        for a, b in zip(spine, spine[1:]): link(a, b)
        anc = ancestors()
    sidx = {k: i for i, k in enumerate(spine)}

    bpar = {}   # the big quest before each big quest
    for k in sorted(byk, key=lambda k: order[k]):
        if not big[k] or k == root: continue
        if k in sidx: bpar[k] = spine[sidx[k] - 1]; continue
        cands = [a for a in anc[k] if big[a]]
        last = [a for a in cands if not any(a in anc[o] for o in cands if o != a)]
        bpar[k] = max(last, key=lambda a: (a in ins[k], len(anc[a]), -order[a])) if last else root
    for k in byk:   # a big quest nothing in the chapter leads to hangs off the first quest
        if big[k] and k != root and not ins[k]: link(root, k)
    anc = ancestors()

    bigs = [k for k in byk if big[k]]
    rank = lambda b: (0, sidx[b]) if b in sidx else (1, len(anc[b]), order[b])
    cl, role = {}, {}
    for k in byk:
        if big[k]: continue
        req = [b for b in bigs if k in anc[b]]
        if req: cl[k], role[k] = min(req, key=rank), 'prep'
    def C(k):
        if big[k]: return k
        if k not in cl:
            cs = [C(d) for d in ins[k]]
            cl[k], role[k] = (max(cs, key=lambda b: (len(anc[b]), order[b])) if cs else root), 'extra'
        return cl[k]
    for k in byk: C(k)

    via = {}   # how many small quests stand between a big quest and the one before it (0: linked directly)
    for k, p in bpar.items():
        seen, ring, n = {k}, [k], 0
        while ring and p not in {d for x in ring for d in ins[x]}:
            ring = [d for x in ring for d in ins[x] if not big[d] and d not in seen and (d == p or p in anc[d])]
            seen |= set(ring); n += 1
        via[k] = n if ring else 0

    # ---- the cells of the big quests: the path along row 0, the other lines along rows of their own
    nP = {}; nX = {}
    for k, b in cl.items():
        d = nP if role[k] == 'prep' else nX
        d[b] = d.get(b, 0) + 1
    bkids = {}
    for b in sorted(bpar, key=lambda b: order[b]): bkids.setdefault(bpar[b], []).append(b)
    reach = {}
    def far(b):
        if b not in reach: reach[b] = 1 + max([far(x) for x in bkids.get(b, [])], default=0)
        return reach[b]
    def step(k):   # columns from the big quest before it: one between them at least, more if a lot sits around them
        b = bpar[k]
        load = nX.get(b, 0) / (4 if b == root else 2) + nP.get(k, 0) + nX.get(k, 0) / 2
        return max(2, 1 + via[k]) + int(math.ceil(max(0.0, load - 8) / 8))   # a crowd spreads up and down first
    cells, reserved, gap_cols, gap_rows, anchor, floating = {}, set(), set(), set(), {}, set()
    if one_path:
        # the path folds into rows like a snake: right along one row, down, back left along the next
        pos = [0]
        for k in spine[1:]: pos.append(pos[-1] + step(k))
        # as many rows as bring the page nearest to twice as wide as tall (a screenful)
        R = min(range(1, 6), key=lambda n: abs((pos[-1] / n * 1.5 + 2) / (n * 5.0) - 2.0) if len(spine) >= 2 * n else 99)
        per = pos[-1] / R
        rows, cur = [], [0]
        for i in range(1, len(spine)):
            if len(rows) < R - 1 and len(cur) >= 2 and pos[i] - pos[cur[0]] > per + 0.5: rows.append(cur); cur = [i]
            else: cur.append(i)
        rows.append(cur)
        c0 = 0
        for n, members in enumerate(rows):
            sgn = 1 if n % 2 == 0 else -1
            r = n * BAND
            for i in members: cells[spine[i]] = (c0 + sgn * (pos[i] - pos[members[0]]), r)
            for i, j in zip(members, members[1:]):
                ca, cb = cells[spine[i]][0], cells[spine[j]][0]
                for c in range(min(ca, cb) + 1, max(ca, cb)): reserved.add((c, r))
                gap_cols.add(ca + sgn)
            c0 = cells[spine[members[-1]]][0]
            if n < len(rows) - 1:   # the turn: straight down to the next row
                for x in range(r + 1, r + BAND): reserved.add((c0, x))
                gap_rows.add(r + 1)
        # optional big quests are placed like side quests: next to what they follow from, or, when that is only the
        # first quest of the tier, next to the path quest they were written in after (unlinked)
        home = 0
        for b in sorted((k for k in byk if big[k] and k not in sidx), key=lambda k: order[k]):
            if bpar[b] != root: anchor[b] = bpar[b]; continue
            before = [k for k in spine if order[k] < order[b]]
            if home < 2 or len(before) <= 2: anchor[b] = root; home += 1
            else: anchor[b] = before[-1]; floating.add(b)
    else:
        # independent lines: each runs along a row of its own, branching off where it starts
        lanes, lane = [], {}
        def run(b, l):
            lane[b] = l; lanes[l].append(b)
            ks = sorted(bkids.get(b, []), key=lambda x: (-far(x), order[x]))
            for i, k in enumerate(ks):
                if i == 0: run(k, l)
                else: lanes.append([]); run(k, len(lanes) - 1)
        lanes.append([]); run(root, 0)
        bcol, brow, used = {}, {}, set()
        def fits(r, a, b): return all((r, c) not in used for c in range(a, b + 1))
        for l, members in enumerate(lanes):
            rel = [0]
            for k in members[1:]: rel.append(rel[-1] + step(k))
            f = members[0]
            if l == 0: s, r = 0, 0
            else:
                p = bpar[f]
                s = bcol[p] + step(f)
                base = brow[p]
                above = sum(1 for x in brow.values() if x < base); below = sum(1 for x in brow.values() if x > base)
                sides = (1, -1) if below <= above else (-1, 1)
                r = next(base + sd * d for d in range(1, 99) for sd in sides if fits(base + sd * d, s - 1, s + rel[-1] + 1))
            for k, x in zip(members, rel): bcol[k], brow[k] = s + x, r
            for c in range(s - (1 if l else 0), s + rel[-1] + 2): used.add((r, c))
        cells = {b: (bcol[b], brow[b] * BAND) for b in bcol}
        for members in lanes:   # the cells a line's link runs through stay empty
            for a, b in zip(members, members[1:]):
                if via[b]: continue   # no direct link: the quests between them may stand on the row
                for c in range(bcol[a] + 1, bcol[b]): reserved.add((c, cells[a][1]))
                gap_cols.add(bcol[a] + 1)

    # ---- links that would run across the page
    def near(d, x):
        cd, cx = C(d), C(x)
        if cd == root and cx in floating: return False
        return cd == cx or bpar.get(cx) == cd or bpar.get(cd) == cx
    dropped, hidden = [], []
    for q in qs:
        k = q.key
        if k == root or q.any_dep or not ins[k]: continue
        if big[k]: keep = [d for d in ins[k] if (d == bpar[k] and k not in floating) or (not big[d] and cl[d] == k)]
        else: keep = [d for d in ins[k] if near(d, k)]
        if keep:
            for d in ins[k]:
                if d not in keep: dropped.append((d, k))
            ins[k] = keep
            q.deps = [d for d in q.deps if d not in byk or d in keep]
        elif not q.hide:
            q.hide = True; hidden.append(k)
    anc = ancestors()
    for q in qs:   # a small quest does not need a link its other links already imply
        k = q.key
        if big[k] or q.any_dep or q.hide: continue
        red = [d for d in ins[k] if any(d in anc[o] for o in ins[k] if o != d)]
        if red:
            ins[k] = [d for d in ins[k] if d not in red]
            q.deps = [d for d in q.deps if d not in red]
    return {'anchor': anchor, 'gap_cols': gap_cols, 'gap_rows': gap_rows, 'cells': cells, 'reserved': reserved, 'ins': ins, 'big': big, 'bpar': bpar, 'spine': spine, 'cl': cl,
            'role': role, 'dropped': dropped, 'hidden': hidden, 'floating': sorted(floating)}


def _layout(ch, plan, seed):
    qs = ch.quests
    keys = [q.key for q in qs]
    ix = {k: i for i, k in enumerate(keys)}
    N = len(keys)
    ins, cl, anchor = plan['ins'], plan['cl'], plan['anchor']
    sz = [KIND[q.kind][0] for q in qs]
    deps = [[ix[d] for d in ins[k]] for k in keys]
    users = [[] for _ in keys]
    for i in range(N):
        for d in deps[i]: users[d].append(i)
    bcells, reserved = plan['cells'], plan['reserved']
    loose = [i for i, k in enumerate(keys) if k not in bcells]
    # what a loose quest belongs to: its big quest, or for an optional big quest the quest it stands next to
    CL = np.array([ix[cl[k]] if k in cl else ix[anchor[k]] if k in anchor else i for i, k in enumerate(keys)])
    LOOSE = np.zeros(N, bool); LOOSE[loose] = True
    PULL = np.array([LOOSE[i] and (k not in anchor or k in plan['floating']) for i, k in enumerate(keys)])
    E = [(d, i) for i in range(N) for d in deps[i]] or [(0, 0)]
    EA = np.array([a for a, b in E]); EB = np.array([b for a, b in E])
    shown = np.array([0.0 if (qs[b].hide or a == b) else 1.0 for a, b in E])
    NOTEND = np.ones((len(E), N), bool)
    for e, (a, b) in enumerate(E): NOTEND[e, a] = NOTEND[e, b] = False
    HARD = np.array([(0.35 * s) ** 2 for s in sz])        # through the middle of a quest
    SOFT = np.array([(s / 2 + 0.05) ** 2 for s in sz])    # clipping its edge
    HALF = np.array([x / 2 for x in sz])
    BIGPAIR = np.array([1.0 if sz[a] >= 1.5 and sz[b] >= 1.5 else 0.0 for a, b in E])
    NOSHARE = np.triu(np.array([[len({a, b, c, d}) == 4 for (c, d) in E] for (a, b) in E]), 1)

    col = [0] * N; row = [0] * N
    placed = np.zeros(N, bool)
    occ = {}
    for k, (c, r) in bcells.items():
        i = ix[k]; col[i], row[i] = c, r; occ[(c, r)] = i; placed[i] = True

    def axis(w, gap):
        out, at, prev = {}, 0.0, None
        for i in sorted(w):
            if prev is not None: at += (w[prev] + w[i]) / 2 + gap
            out[i] = at; prev = i
        return out
    def places():
        cw = {c: LINK for c in plan['gap_cols']}; rh = {r: LINK for r in plan['gap_rows']}
        for i in range(N):
            if not placed[i]: continue
            s = sz[i]
            if s > cw.get(col[i], 0): cw[col[i]] = s
            if s > rh.get(row[i], 0): rh[row[i]] = s
        xs, ys = axis(cw, GX), axis(rh, GY)
        return (np.array([xs[col[i]] if placed[i] else 0.0 for i in range(N)]),
                np.array([ys[row[i]] if placed[i] else 0.0 for i in range(N)]))
    def cost():
        X, Y = places()
        ax, ay, bx, by = X[EA], Y[EA], X[EB], Y[EB]
        dx, dy = bx - ax, by - ay
        vis = shown * placed[EA] * placed[EB]
        L2 = dx * dx + dy * dy + 1e-9
        t = float((np.sqrt(L2) * vis).sum())
        t += 0.3 * float((np.minimum(np.abs(dx), np.abs(dy)) * vis).sum())   # straight links read best
        t += 3.0 * float(((np.sqrt(L2) - HALF[EA] - HALF[EB] < 0.5) * BIGPAIR * vis).sum())   # two big quests: show the link
        cx, cy = X - X[CL], Y - Y[CL]
        t += 0.45 * float((np.sqrt(cx * cx + cy * cy) * (PULL & placed)).sum())   # stay by what you belong to
        t += 1.0 * float((np.minimum(np.abs(dx), np.abs(dy)) * BIGPAIR * vis).sum())  # lines of big quests run straight
        px = X[None, :] - ax[:, None]; py = Y[None, :] - ay[:, None]
        u = np.clip((px * dx[:, None] + py * dy[:, None]) / L2[:, None], 0.0, 1.0)
        ex = px - u * dx[:, None]; ey = py - u * dy[:, None]
        d2 = ex * ex + ey * ey
        live = NOTEND & (vis[:, None] > 0) & placed[None, :]
        t += OVER * float(((d2 < HARD[None, :]) & live).sum()) + CLIP * float(((d2 < SOFT[None, :]) & live).sum())
        M1 = dx[:, None] * (ay[None, :] - ay[:, None]) - dy[:, None] * (ax[None, :] - ax[:, None])
        M2 = dx[:, None] * (by[None, :] - ay[:, None]) - dy[:, None] * (bx[None, :] - ax[:, None])
        t += CROSS * float(((M1 * M2 < -1e-9) & (M1.T * M2.T < -1e-9) & NOSHARE & (vis[:, None] * vis[None, :] > 0)).sum())
        xs, ys = X[placed], Y[placed]
        return t + 0.8 * float(ys.max() - ys.min()) + 0.4 * float(xs.max() - xs.min())

    # ---- loose quests, one at a time, each into the best free cell near what it belongs to:
    # optional big quests first (what follows from them goes around them), then the side quests in the order of play
    isbigl = [k in anchor for k in keys]
    todo = [i for i in loose if isbigl[i]] + sorted((i for i in loose if not isbigl[i]), key=lambda i: (col[CL[i]], i))
    done = set(ix[k] for k in bcells)
    while todo:
        ready = lambda i: placed[CL[i]] and (isbigl[i] or all(d in done for d in deps[i]))
        i = next((i for i in todo if ready(i)), None)
        if i is None: i = next((i for i in todo if placed[CL[i]]), todo[0])
        todo.remove(i); done.add(i)
        cand = set()
        for j in [int(CL[i])] + deps[i] + users[i]:
            if not placed[j]: continue
            w = 4 if j == CL[i] else 2
            cand |= {(c, r) for c in range(col[j] - w, col[j] + w + 1) for r in range(row[j] - w, row[j] + w + 1)}
        placed[i] = True
        best = None
        for c, r in sorted(cand):
            if (c, r) in occ or (c, r) in reserved: continue
            col[i], row[i] = c, r
            v = cost()
            if best is None or v < best[0] - 1e-9: best = (v, c, r)
        col[i], row[i] = best[1], best[2]; occ[(best[1], best[2])] = i

    # ---- search: move loose quests (alone, with what hangs off them, or swapped) while the page gets better
    hang = [[] for _ in range(N)]
    for i in loose:
        first = deps[i][0] if deps[i] and LOOSE[deps[i][0]] and not isbigl[i] else None
        up = first if first is not None else (int(CL[i]) if LOOSE[CL[i]] else None)
        if up is not None and up != i: hang[up].append(i)
    def subtree(i):
        out, stack, seen = [], [i], {i}
        while stack:
            j = stack.pop(); out.append(j)
            for x in hang[j]:
                if x not in seen: seen.add(x); stack.append(x)
        return out
    def shifted(group, dc, dr):
        old = [(col[i], row[i]) for i in group]
        for c in old: del occ[c]
        new = [(c + dc, r + dr) for c, r in old]
        if any(c in occ or c in reserved for c in new):
            for i, c in zip(group, old): occ[c] = i
            return False
        for i, c in zip(group, new): col[i], row[i] = c; occ[c] = i
        return True
    rnd = random.Random(seed)
    state = {'best': cost()}
    def try_group(group, moves):
        got, keep = False, (0, 0)
        for dc, dr in moves:
            if not shifted(group, dc, dr): continue
            c = cost()
            if c < state['best'] - 1e-6: state['best'], keep, got = c, (dc, dr), True
            shifted(group, -dc, -dr)
        if got: shifted(group, *keep)
        return got
    NEAR = [(dc, dr) for dc in range(-3, 4) for dr in range(-4, 5) if (dc, dr) != (0, 0)]
    STEP = [(dc, dr) for dc in range(-2, 3) for dr in range(-3, 4) if (dc, dr) != (0, 0)]
    def sweep():
        got = False
        ids = list(loose); rnd.shuffle(ids)
        for i in ids:
            b = int(CL[i])
            moves = set(NEAR) | {(col[b] + dc - col[i], row[b] + dr - row[i]) for dc in range(-2, 3) for dr in range(-3, 4)}
            moves.discard((0, 0))
            got |= try_group([i], sorted(moves))
            g = subtree(i)
            if len(g) > 1: got |= try_group(g, STEP)
            for j in ids:   # swap with another loose quest nearby
                if j <= i or abs(col[i] - col[j]) > 3 or abs(row[i] - row[j]) > 4: continue
                ci, cj = (col[i], row[i]), (col[j], row[j])
                col[i], row[i] = cj; col[j], row[j] = ci; occ[cj] = i; occ[ci] = j
                c = cost()
                if c < state['best'] - 1e-6: state['best'] = c; got = True
                else: col[i], row[i] = ci; col[j], row[j] = cj; occ[ci] = i; occ[cj] = j
        return got
    def snapshot(): return (list(col), list(row))
    def restore(s):
        col[:], row[:] = s[0], s[1]
        occ.clear()
        for i in range(N): occ[(col[i], row[i])] = i

    t0 = time.time()
    if loose:
        while sweep() and time.time() - t0 < BUDGET: pass
        best = (state['best'], snapshot())
        while time.time() - t0 < BUDGET:   # kicks: shake a few loose, settle again, keep it if the page got better
            for _ in range(3):
                shifted(subtree(rnd.choice(loose)), rnd.choice([-2, -1, 1, 2]), rnd.choice([-2, -1, 1, 2]))
            state['best'] = cost()
            while sweep() and time.time() - t0 < BUDGET: pass
            if state['best'] < best[0] - 1e-6: best = (state['best'], snapshot())
            else: restore(best[1]); state['best'] = best[0]
        restore(best[1])
    X, Y = places()
    return state['best'], {keys[i]: (round(float(X[i]) * 8) / 8, round(float(Y[i]) * 8) / 8) for i in range(N)}
