import json, re, collections
S = '/tmp/claude-0/-home-claude/e2e2a1ea-2802-5c5f-8206-9605d72f9e6a/scratchpad/'
M = json.load(open(S + 'apex_machines.json'))['machines']
T = json.load(open(S + 'tiers.json'))['tier']
NAMES = dict(json.load(open(S + 'apex_quest_items.json'))['items'])
L = json.load(open(S + 'lang.json'))
TN = ['ULV', 'LV', 'MV', 'HV', 'EV', 'IV', 'LuV', 'ZPM', 'UV', 'UHV', 'UEV', 'UIV', 'UXV', 'OpV', 'MAX']
def clean(s): return re.sub(r'§.', '', s or '').strip()
def desc(mid):
    ns, p = mid.split(':')
    out = []
    for k in [f'{ns}.multiblock.{p}.description', f'gtceu.multiblock.{p}.description']:
        if k in L: out.append(L[k]); break
    i = 0
    while True:
        k = f'{ns}.machine.{p}.tooltip.{i}'
        if k not in L: break
        out.append(L[k]); i += 1
    if f'{ns}.machine.{p}.tooltip' in L and not out: out.append(L[f'{ns}.machine.{p}.tooltip'])
    return [clean(x) for x in out]
inv = []
for m in M:
    mid = m['id']
    t = T.get(mid, 99)
    inv.append({'id': mid, 'name': clean(NAMES.get(mid, mid)), 'multi': m['multi'], 'mtier': m['tier'], 'tier': t,
                'types': m['types'], 'desc': desc(mid), 'exists': mid in NAMES})
json.dump(inv, open(S + 'inventory.json', 'w'), indent=0)
mult = [x for x in inv if x['multi']]
by = collections.defaultdict(list)
for x in mult: by[TN[x['tier']] if x['tier'] < 15 else 'never'].append(x['name'] + ' (' + x['id'] + ')')
for k in TN + ['never']:
    if by[k]: print(f'\n## {k} ({len(by[k])})\n' + '; '.join(sorted(by[k])))
