"""Earliest voltage tier for every item from the pack's recipe dump (kubejs apex_quest_dump.js)."""
import json, re, collections, sys
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
    return any(a and (a[0] in MADE or a[0] not in ALL_OUT) for a in r['i'])
# Tinkers casting/melting: the dump has no fluids for them, and apex_tinkers_heat gates the melting by smeltery fuel
R = [r for r in R if not recycling(r) and not r['t'].startswith('tconstruct:')]
producible = set(o for r in R for o in r['o'])
fluid_made = set(f for r in R for f in r['fo'])
ftier = collections.defaultdict(lambda: INF)
tier = collections.defaultdict(lambda: INF)
for it in NAMES:
    if it not in producible: tier[it] = 0
types = set(r['t'] for r in R)
def machines_for(t):
    ns, name = t.split(':')
    alts = [name, name.replace('_recipes', '')]
    pat = re.compile(r'(?:(?:lp|hp|' + '|'.join(PREF) + r')_)?(?:steam_)?(' + '|'.join(map(re.escape, alts)) + r')')
    return [i for i in NAMES if pat.fullmatch(i.split(':')[1])]
TYPE_M = {t: machines_for(t) for t in types if not t.startswith('minecraft:')}
VANILLA_FREE = lambda t: t.startswith('minecraft:') or t in ('gtceu:crafting_shaped_strict',)
def need(r, tt):
    n = r.get('tier', 0)
    for alts in r['i']:
        alts = [a for a in alts if a not in IGNORE_INPUT]
        if alts: n = max(n, min(tier[a] for a in alts))
    for f in r['fi']:
        n = max(n, ftier[f] if f in fluid_made else 0)
    if not VANILLA_FREE(r['t']): n = max(n, tt.get(r['t'], 0))
    return n
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
print('rounds', rnd, file=sys.stderr)
T = {k: v for k, v in tier.items() if v < INF}
json.dump({'tier': T, 'ftier': {k: v for k, v in ftier.items() if v < INF}, 'type_tier': {t: min([tier[m] for m in ms], default=INF) for t, ms in TYPE_M.items()},
           'type_machines': TYPE_M}, open(S + 'tiers.json', 'w'))
c = collections.Counter(min(v, 15) for v in tier.values())
print({(TN[k] if k < 15 else 'never'): n for k, n in sorted(c.items())})
if __name__ == '__main__':
    for m in sys.argv[1:]:
        print(m, TN[tier[m]] if tier[m] < 15 else 'never', NAMES.get(m))
