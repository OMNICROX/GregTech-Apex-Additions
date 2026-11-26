import json, re, collections, rgraph
S='/tmp/claude-0/-home-claude/e2e2a1ea-2802-5c5f-8206-9605d72f9e6a/scratchpad/'
M=json.load(open(S+'apex_machines.json'))['machines']
N=dict(json.load(open('/mnt/user-data/uploads/Apex Ascension/kubejs/apex_quest_items.json'))['items'])
T=['ulv','lv','mv','hv','ev','iv','luv','zpm','uv','uhv','uev','uiv','uxv','opv','max']
CIRC={'basic_electronic':1,'good_electronic':2,'basic_integrated':1,'good_integrated':2,'advanced_integrated':3,'microprocessor':2,'micro_processor':2,'processor_assembly':3,'micro_processor_assembly':3,
 'nano_processor':4,'nano_processor_assembly':5,'nano_computer':6,'nano_mainframe':7,'quantum_processor':5,'quantum_assembly':6,'quantum_computer':7,'quantum_mainframe':8,
 'crystal_processor':6,'crystal_assembly':7,'crystal_computer':8,'crystal_mainframe':9,'wetware_processor':7,'wetware_assembly':8,'wetware_computer':9,'wetware_mainframe':10,
 'bioware':8,'optical':10,'exotic':11,'cosmic':12,'supracausal':13,'workstation':3,'mainframe':4,'vacuum_tube':0,'electronic_circuit':1}
def tier_of(i):
    p=i.split(':')[1]
    m=re.match(r'(ulv|lv|mv|hv|ev|iv|luv|zpm|uv|uhv|uev|uiv|uxv|opv|max)_',p)
    if m: return T.index(m.group(1))
    m=re.match(r'.*?(ulv|lv|mv|hv|ev|iv|luv|zpm|uv|uhv|uev|uiv|uxv|opv|max)_universal_circuit',p)
    if m: return T.index(m.group(1))
    for k,v in sorted(CIRC.items(), key=lambda kv:-len(kv[0])):
        if p.startswith(k): return v
    return None
PROD=collections.defaultdict(list)
for r in rgraph.R:
    for o in r['o']: PROD[o].append(r)
def unlock(item):
    best=None
    for r in PROD.get(item,[]):
        t=r.get('tier',0) if not r['t'].startswith('minecraft') else 0
        for alt in r['i']:
            ts=[tier_of(a) for a in alt if a!=item]
            ts=[x for x in ts if x is not None]
            if ts: t=max(t,min(ts))
        best=t if best is None else min(best,t)
    return best
