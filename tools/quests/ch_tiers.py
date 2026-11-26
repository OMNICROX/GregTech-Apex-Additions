"""One chapter per voltage tier, LV to OpV: the tier's hull, circuits, components, metals, machines, power and multiblocks."""
import json, re
from core import *
from docs_machines import FAMILY, FAMILY_ORDER, ENERGY_ORDER, PART_ORDER, COMPONENTS, TIER_TEXT

R = json.load(open(S + 'apex_quest_recipes.json'))['recipes']
PRODUCIBLE = set(o for r in R for o in r['o'])
INV = json.load(open(S + 'inventory.json'))
LANG = json.load(open(S + 'lang.json'))
TIPS = json.load(open(S + 'gtaa_tips.json'))

CIRCUITS = {
    1: ['gtceu:basic_electronic_circuit', 'gtceu:basic_integrated_circuit', 'gtceu:microchip_processor'],
    2: ['gtceu:good_electronic_circuit', 'gtceu:good_integrated_circuit', 'gtceu:micro_processor'],
    3: ['gtceu:advanced_integrated_circuit', 'gtceu:micro_processor_assembly', 'gtceu:nano_processor'],
    4: ['gtceu:micro_processor_computer', 'gtceu:nano_processor_assembly', 'gtceu:quantum_processor'],
    5: ['gtceu:micro_processor_mainframe', 'gtceu:nano_processor_computer', 'gtceu:quantum_processor_assembly', 'gtceu:crystal_processor'],
    6: ['gtceu:nano_processor_mainframe', 'gtceu:quantum_processor_computer', 'gtceu:crystal_processor_assembly', 'gtceu:wetware_processor'],
    7: ['gtceu:quantum_processor_mainframe', 'gtceu:crystal_processor_computer', 'gtceu:wetware_processor_assembly'],
    8: ['gtceu:crystal_processor_mainframe', 'gtceu:wetware_processor_computer', 'gtna:optical_processor'],
    9: ['gtceu:wetware_processor_mainframe', 'gtna:optical_assembly', 'gtna:exotic_processor'],
    10: ['gtna:optical_computer', 'gtna:exotic_assembly'],
    11: ['gtna:optical_mainframe', 'gtna:exotic_computer'],
    12: ['gtna:exotic_mainframe'],
    13: ['extendedfeatures:opv_universal_circuit'],
}
CIRCUIT_TEXT = {
    1: 'Your first circuits. Basic Electronic Circuits are crafted by hand from vacuum tubes and resistors; the Integrated and Microchip versions are cheaper once you have a Circuit Assembler and a Laser Engraver.',
    2: 'MV circuits. Good Electronic Circuits need plastic boards; the Microprocessor line needs silicon wafers and a Laser Engraver.',
    3: 'HV circuits. Nanoprocessors need a Cleanroom: build one before you try. Advanced Integrated Circuits are the slow but clean-room-free route.',
    4: 'EV circuits. Quantum Processors start here; every circuit line has an EV member.',
    5: 'IV circuits. Crystal Processors need Crystal Chips from the Autoclave and Laser Engraver.',
    6: 'LuV circuits. Wetware starts here and needs Stem Cells and a sterile Cleanroom.',
    7: 'ZPM circuits.', 8: 'UV circuits. GT Nexus Optical circuits begin.', 9: 'UHV circuits: the end of GregTech\'s own circuit lines; Optical and Exotic take over.',
    10: 'UEV circuits.', 11: 'UIV circuits.', 12: 'UXV circuits: the Exotic Mainframe.',
    13: 'OpV circuits: made in the Space Assembler from two UXV circuits. The only OpV circuit in the pack.',
}
COILS = {1: ['gtceu:cupronickel_coil_block'], 2: ['gtceu:kanthal_coil_block'], 3: ['gtceu:nichrome_coil_block'],
         4: ['gtceu:rtm_alloy_coil_block'], 5: ['gtceu:hssg_coil_block'], 6: ['gtceu:naquadah_coil_block'],
         7: ['gtceu:trinium_coil_block'], 8: ['gtceu:tritanium_coil_block']}
BATTERIES = {1: ['gtceu:lv_lithium_battery', 'gtceu:lv_cadmium_battery', 'gtceu:lv_sodium_battery'],
             2: ['gtceu:mv_lithium_battery', 'gtceu:mv_cadmium_battery', 'gtceu:mv_sodium_battery'],
             3: ['gtceu:hv_lithium_battery', 'gtceu:hv_cadmium_battery', 'gtceu:hv_sodium_battery'],
             4: ['gtceu:ev_vanadium_battery'], 5: ['gtceu:iv_vanadium_battery'], 6: ['gtceu:luv_vanadium_battery', 'gtceu:luv_lapotronic_battery'],
             7: ['gtceu:zpm_naquadria_battery', 'gtceu:zpm_lapotronic_battery'], 8: ['gtceu:uv_naquadria_battery', 'gtceu:uv_lapotronic_battery'],
             9: ['gtceu:uhv_ultimate_battery']}

# multiblocks that are disabled on purpose (duplicates of better ones), never shown
DISABLED_MULTIS = re.compile(r'^(gtse:|gtca:(.*purification_unit|space_|isamill|flotation_cell|mega_lcr|green_house|nano_forge|shd_turbine)|gtna:(blaze_blast|cold_ice|leap_forward|steam_elevator_(entity_crusher|oil_drill)|duration_tester|void_miner_steam_gate)|gtceu:(mv|hv|ev)_bedrock_ore_miner|gtapexadditions:ascension_gate)')

MULTI_TEXT = {
    'gtceu:electric_blast_furnace': 'The single most important multiblock in GregTech. Smelts steel, aluminium and every high-temperature metal. Its coils set the maximum temperature: better coils, hotter metals and faster recipes.',
    'gtceu:cleanroom': 'Makes the space inside it sterile. Machines inside a formed Cleanroom can run the recipes that need one (nanoprocessors, many HV+ chips).',
    'gtceu:assembly_line': 'Builds the components and machines of every tier from LuV up. Recipes need a Data Stick written by a Scanner (or Research Station later).',
    'gtceu:vacuum_freezer': 'Cools hot ingots from the blast furnace and liquefies gases. Every metal above 1,750 K comes out hot and needs this.',
    'gtceu:distillation_tower': 'Splits oil, coal tar and other mixtures into all their parts at once. Oil processing for HV power and plastics runs through here.',
    'gtceu:large_chemical_reactor': 'Runs every chemical reaction, with perfect overclocks. Most HV+ chemistry should move here.',
    'gtceu:implosion_compressor': 'Compresses dust into gems with explosives. Needed for industrial diamonds and many EV parts.',
    'gtceu:luv_fusion_reactor': 'Your first Fusion Reactor. Fuses light elements into plasmas and rare metals like Europium and Americium.',
    'gtceu:research_station': 'Researches Assembly Line recipes with computation from an HPCA.',
    'gtapexadditions:uhv_fusion_reactor': 'Fusion Mark IV. Runs the UHV fusion recipes: heavier plasmas for the endgame.',
    'gtapexadditions:uev_fusion_reactor': 'Fusion Mark V. Runs the UEV fusion recipes, including the Celestial Tungsten starter for the Forge of the Gods.',
    'gtapexadditions:compact_fusion_mk1': 'A compact fusion reactor (GTNH style): runs every fusion recipe up to its Mark with parallels, in a fraction of the space.',
    'gtapexadditions:compact_fusion_mk2': 'Compact fusion, Mark II.', 'gtapexadditions:compact_fusion_mk3': 'Compact fusion, Mark III.',
    'gtapexadditions:compact_fusion_mk4': 'Compact fusion, Mark IV.', 'gtapexadditions:compact_fusion_mk5': 'Compact fusion, Mark V.',
    'gtapexadditions:hyper_naquadah_reactor': 'Burns hyper naquadah fuels for enormous power. The step after the Large Naquadah Reactor.',
    'gtapexadditions:advanced_hyper_naquadah_reactor': 'The strongest naquadah reactor, burning fuels refined with SpaceTime.',
    'gtapexadditions:dyson_sphere_launch_site': 'Launches Dyson Swarm modules into orbit: a power plant around the sun.',
    'gtapexadditions:space_mining_module_mk1': 'A Space Elevator module that mines asteroids with drones.',
    'gtapexadditions:space_mining_module_mk2': 'Better asteroid mining: more drones, richer asteroids.',
    'gtapexadditions:space_mining_module_mk3': 'The best asteroid mining module.',
    'gtapexadditions:space_pumping_module_mk1': 'A Space Elevator module that pumps gases from the planets.',
    'gtapexadditions:space_pumping_module_mk2': 'Pumps more planets and more fluids.', 'gtapexadditions:space_pumping_module_mk3': 'The best space pump.',
    'gtapexadditions:space_assembler_module_mk1': 'A Space Elevator module that assembles the parts only space can make.',
    'gtapexadditions:space_assembler_module_mk2': 'Space Assembler, Mark II.', 'gtapexadditions:space_assembler_module_mk3': 'Space Assembler, Mark III.',
    'gtapexadditions:space_drill_mk123': 'The deepest space drill: pulls rare ores straight out of planets.',
    'gtapexadditions:resource_collection_module': 'Collects the resources the other space modules can\'t.',
    'gtapexadditions:purification_plant': 'Purifies water through 8 grades, each needing its own Purification Unit. Higher grades make the best chips.',
}

import collections
_CNT = collections.defaultdict(collections.Counter)
for _r in R: _CNT[_r['t']][_r.get('tier', 0)] += 1


def _type_tier(t):
    c = _CNT.get(t)
    if not c: return 0
    tot, acc = sum(c.values()), 0
    for k in sorted(c):
        acc += c[k]
        if acc >= min(3, tot): return k


MULTI_TIER = {
    'gtceu:implosion_compressor': 3, 'gtceu:multi_smelter': 2, 'gtceu:steel_large_boiler': 1, 'gtceu:titanium_large_boiler': 3,
    'gtceu:tungstensteel_large_boiler': 4, 'gtceu:bronze_multiblock_tank': 0, 'gtceu:steel_multiblock_tank': 1,
    'gtceu:wooden_multiblock_tank': 0, 'gtceu:charcoal_pile_igniter': 0, 'gtna:thermal_power_pump': 1, 'gtceu:central_monitor': 2,
    'gtceu:mv_fluid_drilling_rig': 2, 'gtceu:hv_fluid_drilling_rig': 3, 'gtceu:ev_fluid_drilling_rig': 4,
    'gtapexadditions:advanced_hyper_naquadah_reactor': 12, 'gtna:annihilate_generator': 13, 'gtna:stone_superheater': 0,
    'gtna:steam_lava_maker': 0, 'gtna:infernal_coke_oven': 0, 'gtca:industrial_coke_oven': 2, 'gtceu:steam_large_turbine': 2,
    'gtna:supercritical_steam_turbine': 5, 'gtna:dimensionally_transcendent_steam_boiler': 3, 'gtna:dimensionally_transcendent_steam_oven': 2,
    'gtna:steam_mega_compressor': 2, 'gtca:steam_pressurizer': 2, 'gtna:large_steam_extruder': 1,
}
STEAM_RE = re.compile(r'steam|primitive|coke_oven|brick_kiln|charcoal|bronze_large|wooden|cobbler|woodcutter|cactus')


def multi_tier(m):
    """the chapter a multiblock belongs to: 0 = Steam Age"""
    if m['id'] in MULTI_TIER: return MULTI_TIER[m['id']]
    if STEAM_RE.search(m['id']) and m['tier'] <= 2 and 'steam_elevator' not in m['id']: return 0
    if 'steam_elevator' in m['id']: return max(1, m['tier'])
    return min(13, max(1, m['tier'], min([_type_tier(t) for t in m['types']] or [0])))


def mtext(m):
    if m['id'] in MULTI_TEXT: return [MULTI_TEXT[m['id']]]
    p = m['id'].split(':')[1]
    if m['id'].startswith('gtapexadditions:') and p in TIPS:
        return [' '.join(t for t in TIPS[p][:6])]
    if m['desc']: return [' '.join(m['desc'][:4])]
    if m['types']:
        return ['Runs ' + ', '.join(t.split(':')[1].replace('_', ' ') for t in m['types'][:4]) + ' recipes.']
    return ['']


def tier_chapter(t, order):
    p, tn, col = TP[t], TN[t], TC[t]
    title_long, text = TIER_TEXT[t]
    qs = []
    hull = f'gtceu:{p}_machine_hull'
    gate = Q(f't{t}/gate', f'{col}{tn}: {title_long}', [hull],
             [text, f'Every machine of this tier starts with a {name(hull)}.'],
             deps=[f't{t-1}/gate'] if t > 1 else ['steam/lv_ready'], reward=p, shape='gear', size=2.0)
    qs.append(('gate', gate, []))

    # circuits
    cs = [c for c in CIRCUITS.get(t, []) if exists(c)]
    if cs:
        anchor = Q(f't{t}/circ', f'{col}{tn} Circuits', [cs[0]], CIRCUIT_TEXT.get(t, ''), deps=[gate.key], reward=p, shape='hexagon', icon=cs[0])
        rest = [Q(f't{t}/c/{c}', name(c), [c], f'Another {tn} circuit. Any {tn} circuit works wherever a recipe asks for one.',
                  deps=[anchor.key], xp=10 * t, hide_lines=True) for c in cs[1:]]
        qs.append(('circuits', anchor, rest))

    # components
    comps = [(f'gtceu:{p}_{c}', n, d) for c, n, d in COMPONENTS if exists(f'gtceu:{p}_{c}')]
    if comps:
        anchor = Q(f't{t}/comp', f'{col}{tn} Components', [comps[0][0]], comps[0][2], deps=[gate.key], reward=p, shape='square', icon=comps[0][0])
        rest = [Q(f't{t}/k/{i}', name(i), [i], d, deps=[anchor.key], xp=10 * t, hide_lines=True) for i, n, d in comps[1:]]
        qs.append(('components', anchor, rest))

    # metals that need this tier's blast furnace, and its coil
    mets = sorted(set(o for r in R if r['t'] == 'gtceu:electric_blast_furnace' and r.get('tier', 0) == t
                      for o in r['o'] if re.match(r'(gtceu|gtca|gtna):(hot_)?[a-z_0-9]+_ingot$', o)))
    mets = [m.replace(':hot_', ':') for m in mets]
    mets.sort(key=lambda m: (not m.startswith('gtceu:'), m))
    mets = [m for m in dict.fromkeys(mets) if exists(m)][:14]
    coils = [c for c in COILS.get(t, []) if exists(c)]
    if mets or coils:
        first = coils[0] if coils else mets[0]
        anchor = Q(f't{t}/metal', f'{col}{tn} Metals', [first],
                   (f'{name(first)}: the heating coil for this tier. Put it in your Electric Blast Furnace to reach the temperatures of the metals in this row. ' if coils else '') +
                   f'These metals need an Electric Blast Furnace running at {tn}.', deps=[gate.key], reward=p, shape='diamond', icon=first)
        rest = [Q(f't{t}/m/{m}', name(m), [m], f'Smelted in the Electric Blast Furnace at {tn}. Hot ingots go through the Vacuum Freezer.',
                  deps=[anchor.key], xp=10 * t, hide_lines=True) for m in (mets if coils else mets[1:])]
        qs.append(('metals', anchor, rest))

    # machines
    mach = []
    for fam in FAMILY_ORDER:
        mid = f'gtceu:{p}_{fam}'
        if not exists(mid) or mid not in PRODUCIBLE: continue
        role, first_text, tip = FAMILY[fam]
        if t == 1 or not exists(f'gtceu:{TP[t-1]}_{fam}') or f'gtceu:{TP[t-1]}_{fam}' not in PRODUCIBLE:
            d = [first_text] + ([tip] if tip else [])
        else:
            d = [f'{role}, now at {tn}: twice as fast as the {TN[t-1]} one on old recipes, and it unlocks the {tn} recipes.']
        mach.append(Q(f't{t}/mach/{fam}', name(mid), [mid], d, deps=[], xp=15 * t, hide_lines=True))
    if mach:
        anchor = mach[0]; anchor.deps = [gate.key]; anchor.hide_lines = False; anchor.reward = p
        for m in mach[1:]: m.deps = [anchor.key]
        qs.append(('machines', anchor, mach[1:]))

    # power: transformers, buffers, batteries, generators, energy hatches
    pw = []
    for fam in ENERGY_ORDER:
        mid = f'gtceu:{p}_{fam}'
        if exists(mid) and mid in PRODUCIBLE:
            role, first_text, tip = FAMILY[fam]
            pw.append(Q(f't{t}/pw/{fam}', name(mid), [mid], [first_text] + ([tip] if tip and t == 1 else []), xp=15 * t, hide_lines=True))
    for b in BATTERIES.get(t, []):
        if exists(b): pw.append(Q(f't{t}/bat/{b}', name(b), [b], 'A battery for this tier: goes in battery buffers, chargers and electric tools.', xp=10 * t, hide_lines=True))
    if pw:
        anchor = pw[0]; anchor.deps = [gate.key]; anchor.hide_lines = False; anchor.reward = p
        for m in pw[1:]: m.deps = [anchor.key]
        qs.append(('power', anchor, pw[1:]))

    # buses and hatches
    parts = []
    for fam in PART_ORDER:
        mid = f'gtceu:{p}_{fam}'
        if exists(mid) and mid in PRODUCIBLE:
            role, first_text, tip = FAMILY.get(fam, (fam, '', ''))
            parts.append(Q(f't{t}/part/{fam}', name(mid), [mid], [first_text] if (t == 1 or fam in ('super_chest', 'quantum_chest', 'parallel_hatch')) else [f'{role}, {tn} tier: holds more and moves it faster.'], xp=10 * t, hide_lines=True))
    if parts:
        anchor = parts[0]; anchor.deps = [gate.key]; anchor.hide_lines = False
        for m in parts[1:]: m.deps = [anchor.key]
        qs.append(('parts', anchor, parts[1:]))

    # multiblocks
    ms = [m for m in INV if m['multi'] and m['exists'] and multi_tier(m) == t and m['id'] in PRODUCIBLE and not DISABLED_MULTIS.search(m['id'])]
    ms.sort(key=lambda m: (not m['id'].startswith('gtceu:'), m['name']))
    if ms:
        mq = [Q(f't{t}/multi/{m["id"]}', f'{col}{m["name"]}', [m['id']], mtext(m), reward=p, shape='rsquare', hide_lines=True) for m in ms]
        anchor = mq[0]; anchor.deps = [gate.key]; anchor.hide_lines = False
        for m in mq[1:]: m.deps = [anchor.key]
        qs.append(('multiblocks', anchor, mq[1:]))

    return Ch(f'tier_{p}', f'{col}{tn}&r - {title_long}', hull, 'main', layout(qs), subtitle=f'{tn}: {2 ** (2 * t + 3):,} EU/t'.replace(',', ','), order=order)


def layout(sections, cols=12):
    """gate on the left; each section a row of its own to the right"""
    out = []
    y = 0.0
    gate = sections[0][1]
    gate.x, gate.y = -2.5, 0.0
    out.append(gate)
    rows = []
    for kind, anchor, rest in sections[1:]:
        anchor.x, anchor.y = 0.0, y
        out.append(anchor)
        for i, q in enumerate(rest):
            q.x = 1.5 + (i % cols) * 1.2
            q.y = y + (i // cols) * 1.2
            out.append(q)
        y += 1.6 + (max(0, len(rest) - 1) // cols) * 1.2
    gate.y = (y - 1.6) / 2
    return out


def chapters():
    return [tier_chapter(t, 10 + t) for t in range(1, 14)]
