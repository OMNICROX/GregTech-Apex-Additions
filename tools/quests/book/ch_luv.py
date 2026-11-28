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
