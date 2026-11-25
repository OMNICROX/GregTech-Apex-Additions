"""Turns howester84's Botania Sanctuary schematic into the Sylvan Arbiter's pattern file.
Usage: python3 convert.py  (writes src/main/resources/data/gtapexadditions/structures/sylvan_arbiter.txt)"""
import nbtlib, os
HERE = os.path.dirname(os.path.abspath(__file__))
# Create isn't in the pack: its limestone and glass become Quark's, its deepslate pillars Chipped's
REPLACE = {
    'create:cut_limestone_bricks': 'quark:limestone_bricks',
    'create:limestone_pillar': 'quark:limestone_pillar',
    'create:cut_limestone_brick_stairs': 'quark:limestone_bricks_stairs',
    'create:cut_limestone_brick_slab': 'quark:limestone_bricks_slab',
    'create:cut_limestone_brick_wall': 'quark:limestone_bricks_wall',
    'create:polished_cut_limestone_stairs': 'quark:polished_limestone_stairs',
    'create:polished_cut_limestone_slab': 'quark:polished_limestone_slab',
    'create:vertical_framed_glass': 'quark:framed_glass',
    'create:deepslate_pillar': 'chipped:deepslate_pillar',
    'create:polished_cut_deepslate_stairs': 'minecraft:polished_deepslate_stairs',
}
# blocks that may also be something else (gravel falls if anything under it is missing)
ALTS = {'minecraft:gravel': 'minecraft:gravel|minecraft:coarse_dirt'}
n = nbtlib.load(os.path.join(HERE, 'botania_sanctuary.nbt')); pal = n['palette']
X, Y, Z = [int(v) for v in n['size']]
G = {}
for b in n['blocks']:
    p = tuple(int(v) for v in b['pos']); s = pal[int(b['state'])]
    G[p] = (REPLACE.get(str(s['Name']), str(s['Name'])), ','.join(f'{k}={v}' for k, v in sorted(s.get('Properties', {}).items())))

CONTROLLER = (24, 5, 14)
INSIDE = lambda x, z: 11 <= x <= 37 and 12 <= z <= 38
OPTIONAL = ('minecraft:grass', 'minecraft:tall_grass', 'minecraft:moss_carpet', 'minecraft:wall_torch', 'minecraft:torch',
            'minecraft:lantern', 'minecraft:chain')
def kind(p, name):
    x, y, z = p
    if p == CONTROLLER: return 'controller', ''
    if name == 'botania:mana_spreader': return 'core', ''
    if name == 'botania:apothecary_livingrock': return 'pool', ''
    if name == 'minecraft:oak_leaves': return 'leaves', ''
    if name == 'minecraft:water': return ('glass', '') if INSIDE(x, z) else ('optional', None)
    if name in OPTIONAL or 'mystical_flower' in name or 'corundum_cluster' in name: return 'optional', None
    if (y == 0 and INSIDE(x, z)) or (z == 39 and 23 <= x <= 27 and 2 <= y <= 6 and p[2] + 1 and (x, y, 40) not in G):
        return 'hatch', None
    return 'block', None

legend, grid = {}, {}
nxt = [0x4E00]
for p, (name, props) in G.items():
    k, _ = kind(p, name)
    key = (k, '' if k in ('controller', 'core', 'pool', 'leaves', 'glass') else name,
           '' if k in ('controller', 'core', 'pool', 'leaves', 'glass') else props)
    if key[1] in ALTS: key = (key[0], ALTS[key[1]], key[2])
    if key not in legend:
        legend[key] = chr(nxt[0]); nxt[0] += 1
    grid[p] = legend[key]

out = [f'SIZE {X} {Y} {Z}', f'CONTROLLER {CONTROLLER[0]} {CONTROLLER[1]} {CONTROLLER[2]}']
for (k, name, props), c in sorted(legend.items(), key=lambda t: t[1]):
    out.append(f'K\t{ord(c)}\t{k}\t{name}\t{props}')
out.append('GRID')
for y in range(Y):
    for z in range(Z):
        out.append(''.join(grid.get((x, y, z), ' ') for x in range(X)).rstrip() or '.')
dst = os.path.join(HERE, '../../src/main/resources/data/gtapexadditions/structures/sylvan_arbiter.txt')
open(dst, 'w', encoding='utf-8').write('\n'.join(out) + '\n')
from collections import Counter
print(len(legend), 'symbols', Counter(k[0] for k in legend), Counter(kind(p, G[p][0])[0] for p in G))

# a Litematica-ready copy with the Arbiter's parts swapped in (Mana Cores tier 1, pools, mana glass, bloom leaves,
# the controller over the door)
SWAP = {'core': ('gtapexadditions:mana_core_t1', {}), 'pool': ('botania:mana_pool', {}),
        'glass': ('botania:mana_glass', {}), 'leaves': ('gtapexadditions:mana_bloom_leaves', {}),
        'controller': ('gtapexadditions:mana_nexus', {'facing': 'north'})}
newpal, index, blocks = [], {}, []
for b in n['blocks']:
    p = tuple(int(v) for v in b['pos']); s = pal[int(b['state'])]
    k, _ = kind(p, str(s['Name']))
    if k in SWAP:
        name, props = SWAP[k]
    else:
        name, props = REPLACE.get(str(s['Name']), str(s['Name'])), {str(a): str(v) for a, v in s.get('Properties', {}).items()}
        if name.startswith('quark:limestone_pillar') or name == 'chipped:deepslate_pillar':
            props = {k: v for k, v in props.items() if k == 'axis'}
        elif name.endswith('_wall'):
            props = {k: v for k, v in props.items() if k in ('up', 'waterlogged')}
        elif name == 'quark:framed_glass':
            props = {}
    key = (name, tuple(sorted(props.items())))
    if key not in index:
        index[key] = len(newpal)
        e = nbtlib.Compound({'Name': nbtlib.String(name)})
        if props: e['Properties'] = nbtlib.Compound({a: nbtlib.String(v) for a, v in props.items()})
        newpal.append(e)
    nb = nbtlib.Compound({'pos': b['pos'], 'state': nbtlib.Int(index[key])})
    blocks.append(nb)
out_nbt = nbtlib.File({'size': n['size'], 'entities': nbtlib.List[nbtlib.Compound](), 'palette': nbtlib.List[nbtlib.Compound](newpal),
                       'blocks': nbtlib.List[nbtlib.Compound](blocks), 'DataVersion': n['DataVersion']}, gzipped=True)
out_nbt.save(os.path.join(HERE, 'sylvan_arbiter.nbt'))
print('wrote sylvan_arbiter.nbt', len(blocks), 'blocks')
