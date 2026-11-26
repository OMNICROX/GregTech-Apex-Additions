"""Earliest voltage tier for every item from the pack's recipe dump (kubejs apex_quest_dump.js)."""
import json, re, collections, sys, os
S = '/tmp/claude-0/-home-claude/e2e2a1ea-2802-5c5f-8206-9605d72f9e6a/scratchpad/'
R = json.load(open(S + 'apex_quest_recipes.json'))['recipes']; I = json.load(open(S + 'apex_quest_items.json'))
NAMES = dict(I['items'])
TN = ['ULV', 'LV', 'MV', 'HV', 'EV', 'IV', 'LuV', 'ZPM', 'UV', 'UHV', 'UEV', 'UIV', 'UXV', 'OpV', 'MAX']
PREF = ['ulv', 'lv', 'mv', 'hv', 'ev', 'iv', 'luv', 'zpm', 'uv', 'uhv', 'uev', 'uiv', 'uxv', 'opv', 'max']
INF = 99
IGNORE_INPUT = {'gtceu:programmed_circuit'}
# recycling (arc furnace / macerator / extractor / liquefaction of finished items) isn't a way to get something new
RECYCLE = re.compile(r'^gtceu:(arc_furnace/arc_|macerator/macerate_|extractor/extract_)|liquefaction')
ASSEMBLY = ('gtceu:assembler', 'gtceu:circuit_assembler', 'gtceu:assembly_line', 'gtapexadditions:component_assembly_line',
            'gtna:component_assembly', 'gtceu:wiremill', 'gtceu:extruder', 'gtceu:bender', 'gtceu:lathe', 'gtceu:cutter')
MADE = set()
ALL_OUT = set(o for r in R for o in r['o'])
for r in R:
    if r['t'] in ASSEMBLY or (r['t'] in ('minecraft:crafting_shaped', 'gtceu:crafting_shaped_strict') and len(set(a[0] for a in r['i'] if a)) >= 2):
        MADE.update(r['o'])
def recycling(r):
    """arc furnace / macerator / extractor turning a finished part back into its materials"""
    if r['t'] not in ('gtceu:arc_furnace', 'gtceu:macerator', 'gtceu:extractor', 'gtna:liquefaction_furnace'): return False
    if not re.search(r'/(arc|macerate|extract)_', r['id']): return False
    return any(a and (a[0] in MADE or a[0] not in ALL_OUT) for a in r['i'])
# Tinkers casting/melting: the dump has no fluids for them, and apex_tinkers_heat gates the melting by smeltery fuel
R = [r for r in R if not recycling(r) and not r['t'].startswith('tconstruct:')]
# sources the pack has since removed (ores taken out of worldgen, links cut)
R = [r for r in R if not r['id'].startswith(('apex:v13/unify/chaospersists_', 'apex:v13/unify/corewithstuff_titanium'))]
# repairs and upgrades that need the item itself don't make it
R = [r for r in R if not any(o in a for o in r['o'] for a in r['i'])]
UNAVAILABLE = re.compile(r'^(sgjourney:.*(ore|raw_naquadah|raw_trinium|naquadah_nugget|trinium_nugget|naquadah_iron_nugget)|gravecore:.*(ore|raw_palladium)|grave\.core:titanium_|palladium:(raw_)?titanium|sgjourney:naquadah_iron_alloy|sgjourney:(naquadah|trinium|naquadah_iron|pure_naquadah)(_ingot|_block|_nugget)?$|sgjourney:raw_|gtceu:.*neutronium_ore)')
# what the pack also has that the dump can't see: Pure Daisy turns stone/wood in the world
R.append({'id': 'synthetic:pure_daisy', 't': 'botania:pure_daisy', 'i': [], 'o': ['botania:livingrock', 'botania:livingwood_log'], 'fi': [], 'fo': [], 'tier': 0})
for extra in json.load(open(S + 'extra_recipes.json')) if os.path.exists(S + 'extra_recipes.json') else []:
    R.append(extra)
producible = set(o for r in R for o in r['o'])
fluid_made = set(f for r in R for f in r['fo'])
ftier = collections.defaultdict(lambda: INF)
tier = collections.defaultdict(lambda: INF)
for it in NAMES:
    if it not in producible and not UNAVAILABLE.search(it): tier[it] = 0
types = set(r['t'] for r in R)
def machines_for(t):
    ns, name = t.split(':')
    alts = [name, name.replace('_recipes', '')]
    pat = re.compile(r'(?:(?:lp|hp|' + '|'.join(PREF) + r')_)?(?:steam_)?(' + '|'.join(map(re.escape, alts)) + r')')
    return [i for i in NAMES if pat.fullmatch(i.split(':')[1])]
TYPE_M = {}
for m in json.load(open(S + 'apex_machines.json'))['machines']:
    for t in m['types']:
        TYPE_M.setdefault(t, []).append(m['id'])
for t in types:
    if t.startswith('minecraft:') or t in TYPE_M: continue
    # other mods' recipe types are treated like crafting stations (no voltage)
VANILLA_FREE = lambda t: t.startswith('minecraft:') or t in ('gtceu:crafting_shaped_strict',)
def need(r, tt):
    n = r.get('tier', 0)
    for alts in r['i']:
        alts = [a for a in alts if a not in IGNORE_INPUT]
        if alts: n = max(n, min(tier[a] for a in alts))
    for f in r['fi']:
        # molten metals come from Tinkers' smeltery (gated by fuel heat, not in the dump): count them as unknown
        n = max(n, ftier[f] if f in fluid_made else (INF if 'molten' in f else 0))
    if not VANILLA_FREE(r['t']): n = max(n, tt.get(r['t'], 0))
    return n
def solve():
    rnd = 0
    for rnd in range(80):
        tt = {t: min([tier[m] for m in ms], default=0) for t, ms in TYPE_M.items()}
        ch = False
        for r in R:
            n = need(r, tt)
            for o in r['o']:
                if n < tier[o]: tier[o] = n; ch = True
            for f in r['fo']:
                if n < ftier[f]: ftier[f] = n; ch = True
        if not ch: break
    return rnd
rnd = solve()
# Things stuck in closed loops (sugar cane <-> sugar cane block, raw ore <-> raw ore block, boss drops) come from the
# world: a loop with no way in from outside gets tier 0, then solve again.
sys.setrecursionlimit(100000)
for _ in range(6):
    stuck = [it for it in NAMES if tier[it] >= INF and it in producible and not UNAVAILABLE.search(it)]
    if not stuck: break
    stuck_set = set(stuck)
    graph = {it: set() for it in stuck}
    for r in R:
        for o in r['o']:
            if o in stuck_set:
                for a in r['i']:
                    for x in a:
                        if x in stuck_set: graph[o].add(x)
    # Tarjan SCC (iterative)
    index = {}; low = {}; onst = set(); st = []; sccs = []; idx = [0]
    for v0 in stuck:
        if v0 in index: continue
        work = [(v0, iter(graph[v0]))]; index[v0] = low[v0] = idx[0]; idx[0] += 1; st.append(v0); onst.add(v0)
        while work:
            v, it_ = work[-1]
            nxt = next(it_, None)
            if nxt is None:
                work.pop()
                if work: low[work[-1][0]] = min(low[work[-1][0]], low[v])
                if low[v] == index[v]:
                    comp = []
                    while True:
                        w = st.pop(); onst.discard(w); comp.append(w)
                        if w == v: break
                    sccs.append(comp)
            elif nxt not in index:
                index[nxt] = low[nxt] = idx[0]; idx[0] += 1; st.append(nxt); onst.add(nxt)
                work.append((nxt, iter(graph[nxt])))
            elif nxt in onst:
                low[v] = min(low[v], index[nxt])
    freed = 0
    for comp in sccs:
        cs = set(comp)
        if len(comp) < 2 and comp[0] not in graph[comp[0]]: continue
        # closed: every recipe making a member only needs members or reachable things
        closed = True
        for r in R:
            if any(o in cs for o in r['o']):
                for a in r['i']:
                    if a and all(tier[x] >= INF and x not in cs for x in a): closed = False
                if any(f in fluid_made and ftier[f] >= INF for f in r['fi']): closed = False
        # GregTech materials always have a processing chain; their loops are never 'from the world'
        if any(it.split(':')[0] in ('gtceu', 'gtca', 'gtna', 'gtapexadditions') for it in comp): closed = False
        if any(UNAVAILABLE.search(it) for it in comp): closed = False
        if closed:
            for it in comp: tier[it] = 0; freed += 1
    if not freed: break
    rnd = solve()
print('rounds', rnd, file=sys.stderr)
T = {k: v for k, v in tier.items() if v < INF}
json.dump({'tier': T, 'ftier': {k: v for k, v in ftier.items() if v < INF}, 'type_tier': {t: min([tier[m] for m in ms], default=INF) for t, ms in TYPE_M.items()},
           'type_machines': TYPE_M}, open(S + 'tiers.json', 'w'))
c = collections.Counter(min(v, 15) for v in tier.values())
print({(TN[k] if k < 15 else 'never'): n for k, n in sorted(c.items())})
if __name__ == '__main__':
    for m in sys.argv[1:]:
        print(m, TN[tier[m]] if tier[m] < 15 else 'never', NAMES.get(m))
