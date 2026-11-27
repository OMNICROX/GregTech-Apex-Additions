"""Resolve GT5u (GTNH) item ids like gregtech:gt.blockmachines:1000 to readable enum names."""
import re, os
SRC = '/home/claude/gt5u/src/main/java/gregtech'

def _enum(path):
    out = {}
    for m in re.finditer(r'^\s*([A-Za-z0-9_]+)\((\d+)\)\s*[,;]', open(path).read(), re.M):
        out.setdefault(int(m.group(2)), m.group(1))
    return out

MTE = _enum(f'{SRC}/api/enums/MetaTileEntityIDs.java')
MI = {f'gt.metaitem.0{i}': _enum(f'{SRC}/common/items/IDMetaItem0{i}.java') for i in (1, 2, 3)}

def pretty(name):
    name = re.sub(r'_CONTROLLER$', '', name)
    return ' '.join(w.capitalize() for w in name.replace('_', ' ').split())

def resolve(item_id):
    """Best-effort human name of a GT5u id, or None."""
    m = re.match(r'^gregtech:(gt\.[a-z0-9.]+):(\d+)$', item_id)
    if not m:
        return None
    kind, meta = m.group(1), int(m.group(2))
    if kind == 'gt.blockmachines' and meta in MTE:
        return pretty(MTE[meta])
    if kind in MI:
        table = MI[kind]
        for off in (32000, 0):
            if meta - off in table:
                return pretty(table[meta - off])
    return None
