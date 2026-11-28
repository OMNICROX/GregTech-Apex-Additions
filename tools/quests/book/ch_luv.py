"""Tier 6 - LuV: Assembly Line components, naquadah, superconductors, the Fusion Reactor, crystal circuits and the
start of wetware. Gates follow the pack's recipes."""
from fw import Chapter

C = Chapter('luv', '&dTier 6 - LuV', 'gtceu:luv_machine_hull', '', 7, '32,768 EU/t: ludicrous voltage', 'luv', 'pick_luv', xp_base=70)
C.derive = False
q = C.q

q('start', '&d&lTier 6: LuV', ['gtceu:luv_machine_hull'], [
    'Welcome to &dLuV&r: 32,768 EU/t per amp.',
    'From here every component, hatch and big controller is an &eAssembly Line&r recipe. The goals:',
    '&e1.&r &eNaquadah&r, and the alloy the ZPM hull is made of.',
    '&e2.&r The &eFusion Reactor&r. It makes europium, the metals after it, and plasma for power.',
    '&e3.&r &eCrystal&r circuits now, &ewetware&r soon.',
], ['iv/luv'], kind='hub', guide='progression/06-luv.md')

# ---------------------------------------------------------------- components
q('parts', '&eLuV Components', ['gtceu:luv_electric_pump', 'gtceu:luv_electric_piston', 'gtceu:luv_conveyor_module', 'gtceu:luv_robot_arm'], [
    'All Assembly Line recipes in HSS-S, with soldering alloy and lubricant. Research each one with the scanner first.',
    '> The Component Assembly Line makes them in bulk once its casings are LuV.',
], ['start'], kind='main', guide='gtmultis/assembly_line.md')
q('emitter', 'LuV Emitter and Sensor', ['gtceu:luv_emitter', 'gtceu:luv_sensor'], [
    'An HSS-S frame, a motor, ruridit, a &eQuantum Star&r, LuV circuits and palladium foil.',
], ['parts'])
q('field_gen', '&eLuV Field Generator', ['gtceu:luv_field_generator'], [
    'A Quantum Star, an emitter, a LuV circuit and fine indium tin barium titanium cuprate wire: the LuV superconductor.',
], ['emitter'], kind='main')
q('uhpic', 'UHPIC Chips', ['gtceu:uhpic_chip*8'], [
    'Ultra-high-power ICs. Every energy hatch from LuV up takes them, and each fusion reactor takes several.',
], ['start'])
q('energy_hatch', '&eLuV Energy Hatch', ['gtceu:luv_energy_input_hatch*2'], [
    'An Assembly Line recipe now: hull, niobium-titanium cable, a power chip, a LuV circuit and a voltage coil.',
], ['uhpic', 'parts'], kind='main', guide='gregtech/oc/tier-skipping.md')
q('lasers', 'Laser Hatches', ['gtceu:luv_1024a_laser_target_hatch', 'gtceu:luv_1024a_laser_source_hatch'], [
    'Laser hatches move hundreds of amps between multiblocks through laser pipes with no loss. The &eActive Transformer&r converts between lasers and normal hatches.',
], ['emitter'], kind='opt', guide='gtmultis/active_transformer.md')

# ---------------------------------------------------------------- naquadah
q('naquadah', '&eNaquadah', ['gtceu:naquadah_ingot*16'], [
    'Naquadah ore is in the End and on the outer planets. It smelts in the blast furnace at 5,000 K: &eHSS-G coils&r.',
    'Its ore chemistry also gives &eenriched naquadah&r, &enaquadria&r and &etrinium&r. You will want all of them.',
], ['start'], kind='main', guide='lines/naquadah.md')
q('naq_coils', '&eNaquadah Coils', ['gtceu:naquadah_coil_block*16'], [
    'Naquadah wire, osmium foil and molten tungstensteel. &e7,200 K&r.',
], ['naquadah'], kind='main', guide='gregtech/ebf-and-coils.md')
q('osmiridium', 'Osmiridium', ['gtceu:osmiridium_ingot*16'], [
    'Iridium and osmium, 3:1. ZPM components are made of it, and the fusion reactor is plated in it.',
], ['start'])
q('naq_alloy', '&cNaquadah Alloy', ['gtceu:naquadah_alloy_ingot*16'], [
    'Naquadah, osmiridium and trinium at 7,200 K: naquadah coils. This is the ZPM hull metal.',
], ['naq_coils', 'osmiridium'], kind='main')

# ---------------------------------------------------------------- fusion
q('superconductor', 'Superconducting Coils', ['gtceu:superconducting_coil*16'], [
    'LuV superconductor wire and niobium-titanium foil with molten trinium. The ring of the fusion reactor is made of these.',
], ['naquadah'])
q('fusion_casing', 'Fusion Casings and Glass', ['gtceu:fusion_casing*32', 'gtceu:fusion_glass*8'], [
    'A LuV casing, a superconducting coil, an &eiridium neutron reflector&r, a LuV pump and tungstensteel, with polybenzimidazole. The reactor needs over a hundred casings.',
], ['superconductor', 'parts'])
q('fusion', '&e&lFusion Reactor MK I', ['gtceu:luv_fusion_reactor'], [
    'The controller is an Assembly Line recipe with &cZPM circuits&r (quantum mainframes), plutonium-241 and osmiridium plates, IV field generators and power chips.',
    'A recipe starts only if the reactor has stored its &estart-up energy&r. More energy hatches store more: up to 16 LuV hatches around the ring.',
    'It runs recipes up to 160M EU to start.',
], ['fusion_casing', 'field_gen', 'quantum_mainframe'], kind='hub', guide='lines/fusion.md')
q('europium', '&eEuropium', ['gtceu:europium_ingot*8'], [
    'Neodymium and hydrogen fuse into molten europium. ZPM motors are wound in europium wire, and the MK II reactor is plated with it.',
], ['fusion'], kind='main')
q('plasma', 'Plasma Power', ['gtceu:plasma_large_turbine', 'gtceu:helium_plasma_bucket'], [
    'Deuterium and tritium (or helium-3) fuse into &ehelium plasma&r. The Large Plasma Turbine burns plasma for far more power than the fusion took.',
], ['fusion'], kind='opt', guide='gtmultis/large_turbines.md')
q('compact_fusion', 'Compact Fusion Computer', ['gtapexadditions:compact_fusion_mk1'], [
    'A fusion reactor in a fraction of the space, with parallels. Runs every MK I recipe.',
], ['fusion'], kind='opt', guide='lines/fusion.md')

# ---------------------------------------------------------------- circuits
q('quantum_mainframe', '&eQuantum Mainframe', ['gtceu:quantum_processor_mainframe*2'], [
    'An HSS-G frame, quantum supercomputers and advanced SMD parts: a &cZPM circuit&r from an IV circuit assembler.',
], ['start'], kind='main', guide='gregtech/circuits.md')
q('crystal_chip', 'Crystal Chips', ['gtceu:engraved_crystal_chip*4'], [
    'Grow &eraw crystal chips&r in the autoclave (emerald or olivine in fluids; bacterial sludge works best), then bake them with an emerald or olivine plate in the blast furnace.',
], ['start'])
q('crystal', '&eCrystal Processor', ['gtceu:crystal_processor*4'], [
    'A &elime glass lens&r engraves the chip into a Crystal CPU at LuV. With a multi-layer fiber-reinforced board it becomes an &9IV&r circuit.',
], ['crystal_chip'], kind='main', guide='gregtech/circuits.md')
q('crystal_line', 'Crystal Assembly and Supercomputer', ['gtceu:crystal_processor_assembly*2', 'gtceu:crystal_processor_computer*2'], [
    'The &dLuV&r and &cZPM&r circuits of the crystal line.',
], ['crystal'])
q('stem_cells', 'Stem Cells', ['gtceu:stem_cells*16'], [
    'Osmiridium dust, bacteria and &esterilized growth medium&r in a LuV reactor. The growth medium line starts with meat, agar and a lot of patience.',
    'Wetware circuits are grown from these. They need a &esterile&r Cleanroom: sterilizing filter casings in the ceiling.',
], ['osmiridium'], guide='gregtech/cleanroom.md')
q('wetware_board', 'Wetware Circuit Board', ['gtceu:wetware_circuit_board*4'], [
    'A multi-layer board, a petri dish, a LuV pump, an IV sensor and an IV circuit, filled with sterilized growth medium.',
], ['stem_cells', 'parts'])

# ---------------------------------------------------------------- research
q('data_bank', 'Data Bank', ['gtceu:data_bank'], [
    'Stores research for Assembly Lines and shares it over optical fiber. The first block of the research setup that ZPM recipes need.',
], ['parts'], kind='opt', guide='gtmultis/research.md')

# ---------------------------------------------------------------- Apex multiblocks
q('circuit_line', 'Circuit Assembly Line', ['gtapexadditions:circuit_assembly_line'], [
    'Imprint it with a circuit and it makes that circuit from its parts in bulk, much cheaper.',
], ['parts'], kind='opt', guide='multiblocks/circuit_assembly_line.md')
q('ore_factory', 'General Ore Processing Factory', ['gtapexadditions:ore_processing_factory'], [
    'Raw ore in, dusts and byproducts out, in one step with big parallels.',
], ['parts'], kind='opt', guide='multiblocks/ore_processing_factory.md')
q('spire', 'Lightning Spire', ['gtapexadditions:lightning_spire'], [
    'Up to 512 lightning rods, each adding 28M EU every 12.8 seconds.',
], ['field_gen'], kind='opt', guide='multiblocks/lightning_spire.md')
q('meteor_miner', 'Laser Meteor Miner', ['gtapexadditions:laser_meteor_miner'], [
    'Mines whatever is high above it with a laser.',
], ['emitter'], kind='opt', guide='multiblocks/laser_meteor_miner.md')
q('purification', 'Water Purification Plant', ['gtapexadditions:purification_plant'], [
    'The controller of the purification units. Each unit makes the next grade of purified water; the best chips want the best water.',
], ['field_gen'], kind='opt', guide='multiblocks/purification_plant.md')
q('defc', 'Draconic Fusion Crafting', ['gtapexadditions:draconic_fusion_crafting', 'draconicevolution:wyvern_core'], [
    'Wyvern-tier Draconic Evolution is LuV. This multiblock does fusion crafting without injectors.',
], ['field_gen'], kind='opt', guide='multiblocks/draconic_fusion_crafting.md')
q('glacio', '&bGlacio', ['ad_astra:tier_4_rocket'], [
    'The Tier 4 Rocket is calorite from Venus. Glacio is the last Ad Astra planet before the outer system.',
], ['parts'], kind='opt', guide='dimensions/space.md')
q('glass', 'Rhodium-Plated Palladium Glass', ['gtapexadditions:rhodium_plated_palladium_reinforced_borosilicate_glass*8'], [
    'The LuV tier of multiblock glass.',
], ['start'], kind='opt', guide='gregtech/glass-tiers.md')

q('zpm', '&c&lOn to ZPM', ['gtceu:zpm_machine_hull'], [
    'Naquadah alloy plates, &epolybenzimidazole&r sheets and vanadium-gallium cable: the &cZPM Machine Hull&r.',
    'ZPM is 131,072 EU/t. It brings fusion MK II, research computers and wetware. Open &cTier 7 - ZPM&r.',
], ['naq_alloy', 'europium'], kind='hub', bag='luv')


# ================================================================ more to do in LuV (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'luv/' + key)
    x.deps += [d if '/' in d else 'luv/' + d for d in deps if (d if '/' in d else 'luv/' + d) not in x.deps]


q('luv_cables', 'LuV Cables', ['gtceu:niobium_titanium_single_cable*16'], [
    '&eNiobium-titanium&r carries LuV.',
], ['start'])
q('sensor', 'LuV Sensor', ['gtceu:luv_sensor'], [
    'Assembly line made, like every LuV part.',
], ['emitter'])
q('robot_arm', 'LuV Robot Arm', ['gtceu:luv_robot_arm'], [
    'The same again, a tier up.',
], ['parts'])
q('ruridit', 'Ruridit', ['gtceu:ruridit_ingot*8'], [
    'Ruthenium and iridium. LuV motors are wound with it.',
], ['start'])
q('enriched', 'Enriched Naquadah', ['gtceu:enriched_naquadah_ingot*4'], [
    'Naquadah\'s first refinement. It goes into ZPM parts and into the alloys of the tiers after.',
], ['naquadah'])
q('naquadria', 'Naquadria', ['gtceu:naquadria_ingot*4'], [
    'The second refinement, and the unstable one.',
], ['enriched'], kind='opt')
q('active_transformer', 'Active Transformer', ['gtceu:active_transformer'], [
    'Turns power into a &elaser&r and a laser back into power. Lasers carry thousands of amps with no loss.',
], ['lasers'])
q('laser_pipe', 'Laser Pipes', ['gtceu:normal_laser_pipe*16'], [
    'A laser only runs in a straight line: no corners, no branches. Plan the line before you place the hatches.',
], ['active_transformer'])
q('fusion_glass', 'Fusion Glass', ['gtceu:fusion_glass*16'], [
    'It can replace some of the fusion casings, so you can watch the plasma go round.',
], ['fusion_casing'], kind='opt')
q('helium_plasma', 'Plasma', ['gtceu:helium_plasma_bucket'], [
    'What a fusion reactor makes besides new elements. A plasma turbine burns it for more power than the reactor took.',
], ['fusion'])
also('plasma', 'helium_plasma')
q('raw_crystal', 'Raw Crystal Chip', ['gtceu:raw_crystal_chip*4'], [
    'Grown in the autoclave from emerald or olivine. Each one is engraved into a crystal CPU.',
], ['start'])
also('crystal_chip', 'raw_crystal')
q('crystal_cpu', 'Crystal CPU', ['gtceu:crystal_cpu*4'], [
    'The engraved chip, cut. A crystal processor is built around one.',
], ['crystal_chip'])
also('crystal', 'crystal_cpu')
q('luv_battery', 'LuV Lapotronic Capacitor', ['gtceu:luv_lapotronic_battery'], [
    'A bigger storage block for the substation.',
], ['start'], kind='opt')
q('adv_data_hatch', 'Advanced Data Access Hatch', ['gtceu:advanced_data_access_hatch'], [
    'Holds more research than the plain hatch.',
], ['data_bank'], kind='opt')


# ================================================================ the multis of LuV (2026-11-28, second pass)
def also2(key, *deps):
    x = next(x for x in C.quests if x.key == 'luv/' + key)
    x.deps += [d if '/' in d else 'luv/' + d for d in deps if (d if '/' in d else 'luv/' + d) not in x.deps]


q('filter_mesh', 'Activated Carbon Filter Mesh', ['gtapexadditions:activated_carbon_filter_mesh*4'], [
    'Activated carbon dust and zinc foil. The Clarifier uses one up for every batch.',
], ['purification'])
q('water1', 'Grade 1 Purified Water', ['gtapexadditions:purification_unit_clarifier'], [
    'The Clarifier unit filters plain water through a filter mesh. Place it within 32 blocks of the Purification Plant; the plant powers every unit linked to it.',
], ['filter_mesh'], kind='opt', guide='multiblocks/purification_plant.md')
q('water2', 'Grade 2 Purified Water', ['gtapexadditions:purification_unit_ozonation'], [
    'The Ozonation unit: grade 1 water and ozone. Ozone is air under a glass lens in a LuV laser engraver.',
], ['water1'], kind='opt', guide='multiblocks/purification_plant.md')
q('adv_fusion', 'Advanced Fusion Reactor', ['gtna:advanced_fusion_reactor'], [
    'Runs every fusion recipe, with Parallel, Accelerate, Thread and Overclock hatches. It starts at LuV; each extension structure built in order raises it one tier, up to UEV.',
], ['fusion'], kind='opt', guide='lines/fusion.md')
q('chem_plant', 'Chemical Plant', ['gtna:chemical_plant'], [
    'Large Chemical Reactor recipes with a perfect overclock. Every coil tier above cupronickel cuts energy and time by 5%.',
], ['parts'], kind='opt')
q('isa_mill', 'ISA Mill', ['gtna:isa_mill'], [
    'Wet-grinds ores into milled ore with a perfect overclock. It needs a Ball Hatch with a grinding ball, which wears down.',
], ['parts'], kind='opt')
q('flotation', 'Industrial Flotation Cell', ['gtna:industrial_flotation_cell'], [
    'Floats 64 milled ore with 32 ethylxanthate dust and turpentine into a bucket of ore foam.',
], ['isa_mill'], kind='opt')
q('vacuum_drying', 'Vacuum Drying Furnace', ['gtna:vacuum_drying_furnace'], [
    'Dries four buckets of ore foam into six stacks of dust, red mud and water. The coils must reach the recipe\'s temperature.',
], ['flotation'], kind='opt')
q('greenhouse', 'Large Greenhouse', ['gtna:large_greenhouse'], [
    'Grows crops and trees, in two recipe modes.',
], ['parts'], kind='opt')
q('sc_turbine', 'Supercritical Steam Turbine', ['gtna:supercritical_steam_turbine'], [
    'Expands supercritical steam through a rotor. Base output 16,384 EU/t.',
], ['parts'], kind='opt')
q('luv_miner', 'Elite Large Miner II', ['gtceu:luv_large_miner'], [
    'The biggest of the large miners.',
], ['parts'], kind='opt')
q('drone', 'LuV Mining Drone', ['gtapexadditions:mining_drone_luv'], [
    'Drones are built in a chain from LV. The Space Mining Modules send them out much later, so the chain can wait, but it is cheap now.',
], ['emitter'], kind='opt')
q('seed_synth', 'Seed Synthesizer', ['gtapexadditions:luv_seed_synthesizer'], [
    'Turns a block of a GregTech material, a circuit and a prosperity seed base into that material\'s seeds. Higher synthesizers make the seeds of higher materials.',
], ['field_gen'], kind='opt')
q('compact_coil', 'Compact Fusion Coil', ['gtapexadditions:compact_fusion_coil_mk1*4'], [
    'Naquadah alloy plates, LuV superconductor and a Nano Mainframe.',
], ['fusion'])
also2('compact_fusion', 'compact_coil')
