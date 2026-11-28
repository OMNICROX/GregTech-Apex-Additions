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

GX = 0.75   # clear space between two columns
GY = 0.5    # clear space between two rows
OVER, CROSS = 300.0, 3.0
BAND = 6    # grid rows between two rows of big quests (unused ones take no room)
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
    def step(k):   # columns from the big quest before it: room for what sits between and around them
        b = bpar[k]
        load = nX.get(b, 0) / (4 if b == root else 2) + nP.get(k, 0) + nX.get(k, 0) / 2
        return 1 + max(via[k], int(math.ceil(max(0.0, load - 3) / 4)))
    lanes, lane = [], {}
    def run(b, l):
        lane[b] = l; lanes[l].append(b)
        ks = sorted(bkids.get(b, []), key=lambda x: (x not in sidx, -far(x), order[x]))
        for i, k in enumerate(ks):
            if i == 0: run(k, l)
            else: lanes.append([]); run(k, len(lanes) - 1)
    lanes.append([]); run(root, 0)
    bcol, brow, used, floating = {}, {}, set(), set()
    path_cols = []
    def fits(r, a, b): return all((r, c) not in used for c in range(a, b + 1))
    for l, members in enumerate(lanes):
        rel = [0]
        for k in members[1:]: rel.append(rel[-1] + step(k))
        f = members[0]
        if l == 0:
            s, r = 0, 0
        elif one_path and bpar[f] == root:
            # an optional line of this tier: it goes above or below the path, about where it was written in
            before = [k for k in spine if order[k] < order[f]]
            t = max(1, bcol[before[-1]] if before else 1)
            end = max(path_cols)
            home = lambda b, c: c == 1 + via[f] and abs(b) == 1   # right beside the first quest: keeps its link
            opts = sorted((abs(c - t) + 7 * (abs(b) - 1) + 3 * max(0, c + rel[-1] - end) - 6 * home(b, c)
                           + 0.1 * (b > 0) + 0.01 * c, b, c)
                          for b in (-1, 1, -2, 2, -3, 3, -4, 4) for c in range(1, max(path_cols) + 6))
            r, s = next((b, c) for _, b, c in opts if fits(b, c - 1, c + rel[-1] + 1))
            if not home(r, s): floating.add(f)   # too far from the first quest for a link
        else:
            p = bpar[f]
            s = bcol[p] + step(f)
            base = brow[p]
            above = sum(1 for x in brow.values() if x < base); below = sum(1 for x in brow.values() if x > base)
            sides = (1, -1) if below <= above else (-1, 1)
            r = next(base + sd * d for d in range(1, 99) for sd in sides if fits(base + sd * d, s - 1, s + rel[-1] + 1))
        for k, x in zip(members, rel): bcol[k], brow[k] = s + x, r
        for c in range(s - (1 if l else 0), s + rel[-1] + 2): used.add((r, c))
        if l == 0: path_cols = [bcol[k] for k in members]
    cells = {b: (bcol[b], brow[b] * BAND) for b in bcol}
    reserved = set()
    for members in lanes:   # the cells a path link runs through stay empty
        for a, b in zip(members, members[1:]):
            if via[b]: continue   # no direct link: the quests between them may stand on the row
            for c in range(bcol[a] + 1, bcol[b]): reserved.add((c, cells[a][1]))

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
    return {'cells': cells, 'reserved': reserved, 'ins': ins, 'big': big, 'bpar': bpar, 'spine': spine, 'cl': cl,
            'role': role, 'dropped': dropped, 'hidden': hidden, 'floating': sorted(floating)}


def _layout(ch, plan, seed):
    qs = ch.quests
    keys = [q.key for q in qs]
    ix = {k: i for i, k in enumerate(keys)}
    N = len(keys)
    ins, bigk, cl, role = plan['ins'], plan['big'], plan['cl'], plan['role']
    sz = [KIND[q.kind][0] for q in qs]
    isbig = [bigk[k] for k in keys]
    deps = [[ix[d] for d in ins[k]] for k in keys]
    users = [[] for _ in keys]
    for i in range(N):
        for d in deps[i]: users[d].append(i)
    small = [i for i in range(N) if not isbig[i]]
    CL = np.array([ix[cl[k]] if k in cl else i for i, k in enumerate(keys)])
    SMALL = np.array([not b for b in isbig])
    PREP = np.array([role.get(k) == 'prep' for k in keys])
    EXTRA = np.array([role.get(k) == 'extra' for k in keys])
    E = [(d, i) for i in range(N) for d in deps[i]]
    if not E: E = [(0, 0)]
    EA = np.array([a for a, b in E]); EB = np.array([b for a, b in E])
    shown = np.array([0.0 if (qs[b].hide or a == b) else 1.0 for a, b in E])
    NOTEND = np.ones((len(E), N), bool)
    for e, (a, b) in enumerate(E): NOTEND[e, a] = NOTEND[e, b] = False
    R2 = np.array([(s / 2 + 0.15) ** 2 for s in sz])
    NOSHARE = np.triu(np.array([[len({a, b, c, d}) == 4 for (c, d) in E] for (a, b) in E]), 1)

    bcells, reserved = plan['cells'], plan['reserved']
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
        cw, rh = {}, {}
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
        t += 0.4 * float(((dx < -0.1) * vis).sum())   # a link running right to left reads backwards
        # side quests hug their big quest; preparation sits before it, extras not before it
        cx, cy = X - X[CL], Y - Y[CL]
        mine = SMALL & placed
        t += 0.45 * float((np.sqrt(cx * cx + cy * cy) * mine).sum())
        t += 0.6 * float(((cx > 0.1) & PREP & placed).sum()) + 0.3 * float(((cx < -0.1) & EXTRA & placed).sum())
        px = X[None, :] - ax[:, None]; py = Y[None, :] - ay[:, None]
        u = np.clip((px * dx[:, None] + py * dy[:, None]) / L2[:, None], 0.0, 1.0)
        ex = px - u * dx[:, None]; ey = py - u * dy[:, None]
        t += OVER * float(((ex * ex + ey * ey < R2[None, :]) & NOTEND & (vis[:, None] > 0) & placed[None, :]).sum())
        M1 = dx[:, None] * (ay[None, :] - ay[:, None]) - dy[:, None] * (ax[None, :] - ax[:, None])
        M2 = dx[:, None] * (by[None, :] - ay[:, None]) - dy[:, None] * (bx[None, :] - ax[:, None])
        t += CROSS * float(((M1 * M2 < -1e-9) & (M1.T * M2.T < -1e-9) & NOSHARE & (vis[:, None] * vis[None, :] > 0)).sum())
        xs, ys = X[placed], Y[placed]
        return t + 0.5 * float(ys.max() - ys.min()) + 0.25 * float(xs.max() - xs.min())

    # ---- side quests, one at a time, each into the best free cell near what it belongs to
    todo = sorted(small, key=lambda i: (col[CL[i]], not PREP[i], i))
    done = set(i for i in range(N) if isbig[i])
    while todo:
        i = next((i for i in todo if all(d in done for d in deps[i])), todo[0])
        todo.remove(i); done.add(i)
        b = int(CL[i])
        cand = {(c, r) for c in range(col[b] - 3, col[b] + 4) for r in range(row[b] - 5, row[b] + 6)}
        for d in deps[i]:
            if placed[d]: cand |= {(c, r) for c in range(col[d] - 2, col[d] + 3) for r in range(row[d] - 2, row[d] + 3)}
        placed[i] = True
        best = None
        for c, r in sorted(cand):
            if (c, r) in occ or (c, r) in reserved: continue
            col[i], row[i] = c, r
            v = cost()
            if best is None or v < best[0] - 1e-9: best = (v, c, r)
        col[i], row[i] = best[1], best[2]; occ[(best[1], best[2])] = i

    # ---- search: move side quests (alone, with what hangs off them, or swapped) while the page gets better
    first = [deps[i][0] if deps[i] and not isbig[deps[i][0]] else None for i in range(N)]
    hang = [[] for _ in range(N)]
    for i in small:
        if first[i] is not None: hang[first[i]].append(i)
    def subtree(i):
        out, stack = [], [i]
        while stack:
            j = stack.pop(); out.append(j); stack += hang[j]
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
        ids = list(small); rnd.shuffle(ids)
        for i in ids:
            b = int(CL[i])
            moves = set(NEAR) | {(col[b] + dc - col[i], row[b] + dr - row[i]) for dc in range(-2, 3) for dr in range(-3, 4)}
            moves.discard((0, 0))
            got |= try_group([i], sorted(moves))
            g = subtree(i)
            if len(g) > 1: got |= try_group(g, STEP)
            for j in ids:   # swap with another side quest nearby
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
    if small:
        while sweep() and time.time() - t0 < BUDGET: pass
        best = (state['best'], snapshot())
        while time.time() - t0 < BUDGET:   # kicks: shake a few loose, settle again, keep it if the page got better
            for _ in range(3):
                shifted(subtree(rnd.choice(small)), rnd.choice([-2, -1, 1, 2]), rnd.choice([-2, -1, 1, 2]))
            state['best'] = cost()
            while sweep() and time.time() - t0 < BUDGET: pass
            if state['best'] < best[0] - 1e-6: best = (state['best'], snapshot())
            else: restore(best[1]); state['best'] = best[0]
        restore(best[1])
    X, Y = places()
    return state['best'], {keys[i]: (round(float(X[i]) * 8) / 8, round(float(Y[i]) * 8) / 8) for i in range(N)}
