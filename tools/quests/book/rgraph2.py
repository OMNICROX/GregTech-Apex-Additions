"""Filtered recipe links: only to earlier quests, no tools, earliest provider only."""
import re, collections
import rgraph
from rgraph import needs, BUCKET, TYPES_OF

TOOL = re.compile(r'_(hammer|file|saw|wrench|mortar|knife|screwdriver|wire_cutter|crowbar|mallet|soft_mallet|butchery_knife|plunger|pickaxe|axe|shovel|sword|hoe|scythe|mining_hammer|spade|drill.*|chainsaw.*)$')
SKIP = {'minecraft:water', 'gtceu:programmed_circuit', 'gtceu:steam', 'minecraft:stick', 'minecraft:cobblestone'}


def is_tool(i): return bool(TOOL.search(i))


def derive(order, items_of, max_per=4):
    pos = {k: n for n, k in enumerate(order)}
    prov, mtype = {}, {}
    for k in order:
        for it in items_of(k):
            if is_tool(it): continue
            prov.setdefault(it, k)
            if it in BUCKET: prov.setdefault(BUCKET[it], k)
            for t in TYPES_OF.get(it, ()): mtype.setdefault(t, k)
    out = {}
    for k in order:
        got = collections.Counter()
        def take(c, w=1):
            if c and c != k and pos[c] < pos[k]: got[c] += w
        for it in items_of(k):
            ings, types = needs(it)
            for alts in ings:
                if any(is_tool(a) for a in alts) or alts & SKIP: continue
                hits = [prov[a] for a in alts if a in prov]
                if hits:
                    take(min(hits, key=pos.get), 2); continue
                a = sorted(alts)[0]
                sub, subtypes = needs(a)
                for alts2 in sub:
                    if any(is_tool(b) for b in alts2) or alts2 & SKIP: continue
                    h2 = [prov[b] for b in alts2 if b in prov]
                    if h2: take(min(h2, key=pos.get))
                for t in subtypes: take(mtype.get(t))
            for t in types: take(mtype.get(t), 2)
        out[k] = [c for c, _ in got.most_common(max_per)]
    return out
