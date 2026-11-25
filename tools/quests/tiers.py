"""Earliest voltage tier for every item from the pack's recipe dump."""
import json, math, re, collections, sys, os
S = '/tmp/claude-0/-home-claude/e2e2a1ea-2802-5c5f-8206-9605d72f9e6a/scratchpad/'
R = json.load(open(S + 'apex_quest_recipes.json')); I = json.load(open(S + 'apex_quest_items.json'))
NAMES = dict(I['items']); TAGS = R['tags']; RECIPES = R['recipes']
TN = ['ULV', 'LV', 'MV', 'HV', 'EV', 'IV', 'LuV', 'ZPM', 'UV', 'UHV', 'UEV', 'UIV', 'UXV', 'OpV', 'MAX']
PREF = ['ulv', 'lv', 'mv', 'hv', 'ev', 'iv', 'luv', 'zpm', 'uv', 'uhv', 'uev', 'uiv', 'uxv', 'opv', 'max']
def vt(eu):
    if eu <= 8: return 0
    return min(14, math.ceil(math.log(eu / 8, 4)))
INF = 99
tier = collections.defaultdict(lambda: INF)
producible = set()
for r in RECIPES:
    for o in r['o']: producible.add(o)
for it in NAMES:
    if it not in producible: tier[it] = 0          # world, loot or creative
# machines for each recipe type
types = set(r['t'] for r in RECIPES)
def machines_for(t):
    ns, name = t.split(':')
    ms = [i for i in NAMES if i.split(':')[1] == name or re.fullmatch(r'(lp|hp|' + '|'.join(PREF) + r')_' + re.escape(name), i.split(':')[1])]
    return ms
TYPE_M = {t: machines_for(t) for t in types if not t.startswith('minecraft:')}
def ing_tier(r):
    t = 0
    for i in r['i']: t = max(t, tier[i])
    for tg in r['t2']:
        items = TAGS.get(tg)
        if items: t = max(t, min(tier[x] for x in items))
    return t
changed = True; rounds = 0
while changed and rounds < 60:
    changed = False; rounds += 1
    tt = {}
    for t, ms in TYPE_M.items():
        tt[t] = min([tier[m] for m in ms], default=0)
    for r in RECIPES:
        need = max(vt(r['eu']) if r['eu'] else 0, ing_tier(r), tt.get(r['t'], 0))
        for o in r['o']:
            if need < tier[o]: tier[o] = need; changed = True
print('rounds', rounds, file=sys.stderr)
json.dump({k: v for k, v in tier.items() if v < INF}, open(S + 'tiers.json', 'w'))
c = collections.Counter(min(v, 15) for v in tier.values())
print({(TN[k] if k < 15 else 'never'): n for k, n in sorted(c.items())})
for m in ['gtceu:lv_assembler', 'gtceu:electric_blast_furnace', 'gtceu:mv_chemical_reactor', 'ae2:controller', 'botania:mana_pool',
          'gtapexadditions:mana_nexus', 'gtceu:fusion_reactor', 'draconicevolution:draconium_ingot', 'ad_astra:tier_1_rocket', 'gtceu:steel_ingot',
          'gtceu:aluminium_ingot', 'gtceu:tungstensteel_ingot', 'gtapexadditions:ascension_gate', 'placeablemaxwell:maxwell', 'gtceu:hp_steam_macerator']:
    print(m, TN[tier[m]] if tier[m] < 15 else tier[m])
