"""Quest links from the recipe data: a quest depends on the quests whose item goes into its item's recipe, or whose
machine is the one that makes it. This is how GTNH's lines work: every line means "you need that to make this"."""
import json, re, collections

S = '/tmp/claude-0/-home-claude/e2e2a1ea-2802-5c5f-8206-9605d72f9e6a/scratchpad/'
R = json.load(open(S + 'apex_quest_recipes.json'))['recipes']
MACHINES = json.load(open(S + 'apex_machines.json'))['machines']
TYPES_OF = {m['id']: set(m['types']) for m in MACHINES}
for k, v in {'gtceu:lp_steam_macerator': 'gtceu:macerator', 'gtceu:hp_steam_macerator': 'gtceu:macerator'}.items():
    TYPES_OF.setdefault(k, set()).add(v)

RECYCLE = {'gtceu:arc_furnace', 'gtceu:macerator', 'gtceu:extractor', 'gtna:liquefaction_furnace', 'gtceu:packer',
           'gtceu:unpacker', 'gtceu:centrifuge', 'gtceu:fluid_solidifier', 'gtceu:forge_hammer', 'gtceu:alloy_smelter',
           'gtceu:compressor', 'gtceu:large_chemical_reactor', 'gtceu:component_assembly_line', 'gtna:quantum_force_transformer'}
CRAFT = {'minecraft:crafting_shaped', 'minecraft:crafting_shapeless'}
PROD = collections.defaultdict(list)
for r in R:
    for o in r['o']: PROD[o].append(r)


def _mat(i):
    m = re.match(r'[a-z_]+:(?:hot_)?([a-z_]+?)_(ingot|nugget|dust|block|plate|gem|small_dust|tiny_dust)$', i)
    return m.group(1) if m else None


def recipes(item):
    """the recipes a player would really use to make item: no recycling, no block/nugget repacking, lowest tier"""
    rs = []
    for r in PROD.get(item, []):
        if r['t'] in RECYCLE and item.split(':')[1] not in ('crushed', 'purified') and not _keep(r, item): continue
        ins = [a[0] for a in r['i'] if a]
        if ins and all(_mat(x) and _mat(x) == _mat(item) for x in ins): continue   # repacking the same metal
        rs.append(r)
    if not rs: return []
    low = min(r.get('tier', 0) for r in rs)
    return [r for r in rs if r.get('tier', 0) == low]


def _keep(r, item):
    # real production steps that use "recycling" machine types
    t = r['t'].split(':')[1]
    if t == 'macerator' and any(re.search(r'(raw_|crushed|purified|_ore$)', a[0]) for a in r['i'] if a): return True
    if t in ('extractor', 'centrifuge', 'alloy_smelter', 'compressor', 'forge_hammer', 'fluid_solidifier'):
        ins = [a[0] for a in r['i'] if a]
        return not all(_mat(x) == _mat(item) for x in ins if _mat(x))
    return False


def needs(item):
    """(ingredient alternatives, machine types) over the item's real recipes"""
    ings, types, craftable = [], set(), False
    for r in recipes(item):
        if r['t'] in CRAFT: craftable = True
        else: types.add(r['t'])
        ings += [set(a) for a in r['i'] if a]
        ings += [{f} for f in r.get('fi', [])]
    return ings, (set() if craftable else types)


BUCKET = {'gtceu:creosote_bucket': 'gtceu:creosote', 'gtceu:sulfuric_acid_bucket': 'gtceu:sulfuric_acid',
          'gtceu:glue_bucket': 'gtceu:glue', 'minecraft:water_bucket': 'minecraft:water', 'minecraft:lava_bucket': 'minecraft:lava'}


def derive(quests, items_of):
    """quests: list of quest keys; items_of(key) -> item ids it asks for. Returns {key: set(keys it needs)}."""
    provides = collections.defaultdict(set)   # item/fluid -> quest keys that hand it to you
    makes_type = collections.defaultdict(set)  # recipe type -> quest keys whose machine runs it
    for k in quests:
        for it in items_of(k):
            provides[it].add(k)
            if it in BUCKET: provides[BUCKET[it]].add(k)
            for t in TYPES_OF.get(it, ()): makes_type[t].add(k)
    out = {}
    for k in quests:
        got = set()
        for it in items_of(k):
            ings, types = needs(it)
            frontier = ings
            # look through one step of intermediates nobody's quest gives you
            for alts in frontier:
                hit = set().union(*(provides.get(a, set()) for a in alts))
                if hit:
                    got |= hit
                    continue
                for a in list(alts)[:1]:
                    sub, subtypes = needs(a)
                    for alts2 in sub:
                        got |= set().union(*(provides.get(b, set()) for b in alts2))
                    for t in subtypes: got |= makes_type.get(t, set())
            for t in types: got |= makes_type.get(t, set())
        got.discard(k)
        out[k] = got
    return out
