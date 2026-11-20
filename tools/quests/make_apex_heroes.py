import random
random.seed(20261119)
def hid(): return random.choice('1234567') + ''.join(random.choice('0123456789ABCDEF') for _ in range(15))
quests=[]
def q(key,title,desc,tasks,x,y,deps=(),shape=None,size=None):
    quests.append(dict(key=key,id=hid(),title=title,desc=desc,tasks=tasks,x=x,y=y,deps=list(deps),shape=shape,size=size))
def item(i,c=1): return ('item',i,c)
def check(): return ('checkmark',)
q('casing','Steel Booth',"The Suit Fabrication Station is one building in four tiers. Start with Steel Booth Casings (MV assembler).",[item('gtapexadditions:suit_casing_t1',16)],0,0)
q('station','Suit Fabrication Station',"5x5x6 booth: casing floor and roof, glass on the front, Suit Display Monitors in the top ring and a lamp in the roof centre. The controller goes in the middle of the front floor row.|Open the &dSuit Picker&r tab to browse every suit with its hero kit, pick one and press &6Fabricate&r.",[item('gtapexadditions:suit_fabrication_station')],2,0,['casing'],'hexagon',1.5)
q('monitor','Suit Display Monitors',"Twelve of them ring the top of the booth.",[item('gtapexadditions:suit_monitor',12)],2,-1.5,['station'])
q('first_suit','Suit Up',"Make any suit in the station. Each one comes with its own hero kit of powers: speedsters run, fliers fly, bruisers hit hard.",[check()],4,0,['station'])
q('me','Autocraft the Parts',"Put an ME Input Bus on the station. Fabricate then sets the bus to pull the suit parts and asks your crafting CPUs for any part that is short.",[item('gtceu:me_input_bus')],4,1.5,['first_suit'])
q('t2','Showcase Pod',"Tier II casings (EV) unlock the bigger hero suits. Same building: swap the casings (the Structure Upgrader does it in one click).",[item('gtapexadditions:suit_casing_t2',16)],6,0,['first_suit'])
q('t3','Holo Chamber',"Tier III casings (LuV).",[item('gtapexadditions:suit_casing_t3',16)],8,0,['t2'])
q('t4','Ascension Forge',"Tier IV casings (UHV): Kryptonians, Viltrumites and cosmic heroes.",[item('gtapexadditions:suit_casing_t4',16)],10,0,['t3'],'hexagon')
q('core','Hero Core',"Break a tier IV hero suit chestplate down into a Hero Core in the Ascension Forge.",[item('gtapexadditions:hero_core')],12,0,['t4'])
q('apex','Apex Ascension',"The final suit. Twelve Hero Cores, UEV parts and Grade 8 purified water.|Full set: flight, regeneration, night vision, immune to fire, falls, drowning, wither and poison, and +50% damage against infernal mobs.",[item('gtapexadditions:apex_helmet'),item('gtapexadditions:apex_chestplate'),item('gtapexadditions:apex_leggings'),item('gtapexadditions:apex_boots')],14,0,['core'],'gear',2.0)
q('infernal','Infernal Mobs',"Some hostile spawns are &eElite&r, &6Ultra&r or &4Infernal&r: more health, nasty modifiers and much better loot. Kill one.",[check()],0,4)
q('eec','Extreme Entity Crusher',"A spawn egg in an input bus picks the mob; a sword sets looting. No more mob towers.",[item('gtapexadditions:extreme_entity_crusher')],2,4,['infernal'])
q('elite','Elite Sigil',"Put a sigil in an input bus of the crusher to crush that rank of the mob: loot x3, bonus drops, more power.",[item('gtapexadditions:elite_sigil')],4,4,['eec'])
q('ultra','Ultra Sigil',"Loot x5.",[item('gtapexadditions:ultra_sigil')],6,4,['elite'])
q('infernal_sigil','Infernal Sigil',"Loot x9. An infernal witch crusher pays for itself.",[item('gtapexadditions:infernal_sigil')],8,4,['ultra'],'hexagon')
q('upgrader','Structure Upgrader',"Shift + right-click a formed controller: upgrade every coil or tiered casing at once from your inventory, and hatches if you carry better ones.",[item('gtapexadditions:structure_upgrader')],0,7)
q('aether','Into the Aether',"Golden aerclouds centrifuge into Helium-3, enchanted gravitite doubles Gravi Stars.",[check()],2,7)
q('ring','Angel Ring',"Flight from the sky itself: enchanted gravitite, a golden feather and an angel block.",[item('gtapexadditions:angel_ring')],4,7,['aether'],'hexagon')
keys={x['key']:x['id'] for x in quests}
def s(t): return '"'+t.replace('\\','\\\\').replace('"','\\"')+'"'
out=['{','\tdefault_hide_dependency_lines: false','\tdefault_quest_shape: ""','\tfilename: "apex_heroes"','\tgroup: ""',
     '\ticon: { Count: 1b, id: "gtapexadditions:suit_fabrication_station" }',f'\tid: "{hid()}"','\torder_index: 99','\tquest_links: [ ]','\tquests: [']
for x in quests:
    out.append('\t\t{')
    if x['deps']: out.append('\t\t\tdependencies: ['+' '.join(s(keys[d]) for d in x['deps'])+']')
    lines=[]
    for i,p in enumerate(x['desc'].split('|')):
        if i: lines.append('')
        lines.append(p)
    out.append('\t\t\tdescription: ['+' '.join(s(l) for l in lines)+']')
    out.append(f'\t\t\tid: "{x["id"]}"')
    if x['shape']: out.append(f'\t\t\tshape: "{x["shape"]}"')
    if x['size']: out.append(f'\t\t\tsize: {x["size"]}d')
    out.append('\t\t\ttasks: [')
    for t in x['tasks']:
        if t[0]=='item': out.append(f'\t\t\t\t{{ count: {t[2]}L, id: "{hid()}", item: {{ Count: 1b, id: "{t[1]}" }}, type: "item" }}')
        else: out.append(f'\t\t\t\t{{ id: "{hid()}", type: "checkmark" }}')
    out.append('\t\t\t]')
    out.append(f'\t\t\ttitle: {s(x["title"])}')
    out.append(f'\t\t\tx: {float(x["x"])}d')
    out.append(f'\t\t\ty: {float(x["y"])}d')
    out.append('\t\t}')
out+=['\t]','\ttitle: "Heroes & Infernals"','}']
open('apex_heroes.snbt','w').write('\n'.join(out)+'\n')
print(len(quests),'quests')
