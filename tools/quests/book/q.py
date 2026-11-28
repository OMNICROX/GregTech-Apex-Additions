"""q.py item_or_fluid ... : the real lowest-tier ways to make it (rgraph filters), with names. Fluids by id."""
import sys, re, json, rgraph
U='/mnt/user-data/uploads/Apex Ascension/kubejs/'
N=dict(json.load(open(U+'apex_quest_items.json'))['items'])
nm=lambda i: re.sub('§.','',N.get(i,i.split(':')[-1]))
FPROD={}
for r in rgraph.R:
    for f in r.get('fo',[]): FPROD.setdefault(f,[]).append(r)
BAD={'liquefaction_furnace','godforge_molten','atomization_condensation','extreme_heat_exchanger','quantum_force_transformer','component_assembly_line','large_chemical_reactor','distillation_tower','gas_collection'}
def fl_recipes(f):
    rs=[r for r in FPROD.get(f,[]) if r['t'].split(':')[1] not in BAD and not (r['t'].endswith('extractor') and r['i'])]
    if not rs: return []
    lo=min(r.get('tier',0) for r in rs); return [r for r in rs if r.get('tier',0)==lo]
def show(r): return f"  [{r['t'].split(':')[1]} T{r.get('tier','')} {r.get('d','')}t] {', '.join(nm(a[0])+('*' if len(a)>1 else '') for a in r['i'])} {'FL:'+','.join(x.split(':')[1] for x in r.get('fi',[])) if r.get('fi') else ''} -> {', '.join(nm(o) for o in r['o'])} {'FO:'+','.join(x.split(':')[1] for x in r.get('fo',[])) if r.get('fo') else ''}"
n=int(__import__("os").environ.get("N","3"))
if __name__ != "__main__": sys.argv=sys.argv[:1]
for a in sys.argv[1:]:
    rs=[r for r in rgraph.recipes(a) if r['t'].split(':')[1] not in BAD] or fl_recipes(a)
    print(a, len(rs))
    for r in rs[:n]: print(show(r)[:300])
