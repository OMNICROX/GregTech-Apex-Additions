"""Tier 9 - UHV: neutronium machines, the Space Elevator and its modules, Fusion MK IV, and Apex's late processing
multiblocks. Gates follow the pack's recipes."""
from fw import Chapter

C = Chapter('uhv', '&4Tier 9 - UHV', 'gtceu:uhv_machine_hull', '', 10, '2,097,152 EU/t: ultra high voltage', 'uhv', 'pick_uhv', xp_base=120)
C.derive = False
q = C.q

q('start', '&4&lTier 9: UHV', ['gtceu:uhv_machine_hull'], [
    'Welcome to &4UHV&r: 2,097,152 EU/t per amp.',
    'GregTech\'s own tiers end here. What follows is Apex\'s endgame, in the spirit of GT New Horizons:',
    '&e1.&r The &eSpace Elevator&r. Its Space Assembler makes everything from UEV up.',
    '&e2.&r &eOptical and exotic circuits&r.',
    '&e3.&r Power on a new scale: Fusion MK IV and the Hyper Naquadah Reactor.',
], ['uv/uhv'], kind='hub', guide='progression/09-uhv.md')

q('parts', '&eUHV Components', ['gtceu:uhv_electric_motor', 'gtceu:uhv_electric_pump', 'gtceu:uhv_robot_arm', 'gtceu:uhv_conveyor_module'], [
    'Neutronium parts and fine americium wire. UV-voltage Assembly Line recipes.',
], ['start'], kind='main')
q('emitter', 'UHV Emitter and Sensor', ['gtceu:uhv_emitter', 'gtceu:uhv_sensor'], [
    'The Space Elevator controller takes one of each.',
], ['parts'])
q('field_gen', '&eUHV Field Generator', ['gtceu:uhv_field_generator'], [
    'A neutronium frame, a Quantum Star, a UHV emitter and a wetware mainframe. Nearly every UHV controller asks for one.',
], ['emitter'], kind='main')
q('energy_hatch', '&eUHV Energy Hatch', ['gtceu:uhv_energy_input_hatch*2'], [
    'Hull, europium cable, a UHPIC chip, a wetware mainframe and ruthenium trinium americium neutronate wire.',
], ['parts'], kind='main')

# ---------------------------------------------------------------- space elevator
q('elevator', '&e&lSpace Elevator', ['gtapexadditions:space_elevator'], [
    'A tower to orbit. Up to 12 &emodules&r attach around its base; better elevator motors run them faster.',
    'The modules are multiblocks of their own: mining drones, gas pumps and the Space Assembler.',
], ['field_gen'], kind='hub', guide='multiblocks/space_elevator.md')
q('assembler_module', '&eSpace Assembler Module', ['gtapexadditions:space_assembler_module_mk1'], [
    'The assembler of the endgame. Every UEV, UIV, UXV and OpV component, the MK V fusion computer, the antimatter machines and GTNA\'s capstones are made here.',
], ['elevator'], kind='main', guide='multiblocks/space_elevator.md')
q('mining_module', 'Space Mining Module', ['gtapexadditions:space_mining_module_mk1'], [
    'Sends mining drones to asteroids. Give it a drone, drilling rods and computation, and pick an asteroid.',
], ['elevator'], kind='opt', guide='multiblocks/space_elevator.md')
q('pumping_module', 'Space Pumping Module', ['gtapexadditions:space_pumping_module_mk1'], [
    'Pumps gases and fluids from the planets: helium-3, deuterium, oil, chlorobenzene and more, with no veins to run dry.',
], ['elevator'], kind='opt', guide='multiblocks/space_elevator.md')

# ---------------------------------------------------------------- power
q('fusion4', '&eFusion Reactor MK IV', ['gtapexadditions:uhv_fusion_reactor'], [
    'A MK I computer rebuilt with advanced fusion coils, wetware mainframes and dense neutronium. It runs the UHV fusion recipes.',
], ['field_gen'], kind='main', guide='lines/fusion.md')
q('hyper_naq', 'Hyper Naquadah Reactor', ['gtapexadditions:hyper_naquadah_reactor'], [
    'The Large Naquadah Reactor\'s successor. It burns the refined naquadah fuels for enormous power.',
], ['field_gen'], kind='opt', guide='lines/naquadah.md')
q('fuel_complex', 'Fuel Refining Complex', ['gtapexadditions:fuel_refining_complex'], [
    'Light and heavy naquadah fuel straight into naquadah fuel MkI to MkVI.',
], ['field_gen'], kind='opt', guide='multiblocks/fuel_refining_complex.md')

# ---------------------------------------------------------------- circuits
q('optical_assembly', '&eOptical Assembly', ['gtna:optical_assembly*2'], [
    'The &4UHV&r circuit of the optical line, made at UV in a circuit assembler. Cheaper than a wetware mainframe.',
], ['start'], kind='main', guide='gregtech/circuits.md')
q('exotic', 'Exotic Processor', ['gtna:exotic_processor*2'], [
    'The exotic line starts at UHV and ends in the UXV Exotic Mainframe.',
], ['optical_assembly'])
q('optical_computer', '&aOptical Computer', ['gtna:optical_computer*2'], [
    'The &aUEV circuit&r. The antimatter machines and the MK V fusion computer need these.',
], ['optical_assembly'], kind='main')
q('miracle_top', 'Miracle Top', ['gtapexadditions:miracle_top'], [
    'A ring-shaped circuit assembler that grows from 2 to 16 rings.',
], ['field_gen'], kind='opt', guide='multiblocks/miracle_top.md')

# ---------------------------------------------------------------- Apex multiblocks
q('qft', 'Quantum Force Transformer', ['gtapexadditions:quantum_force_transformer'], [
    'Whole processing lines in one step. Every recipe needs a catalyst in an input bus; it is not used up.',
], ['field_gen'], kind='opt', guide='multiblocks/quantum_force_transformer.md')
q('black_hole', 'Black Hole Compressor', ['gtapexadditions:black_hole_compressor', 'gtapexadditions:black_hole_seed'], [
    'A compressor and neutronium compressor powered by a black hole. A seed opens it, its stability drops over time, and a collapser closes it.',
], ['field_gen'], kind='opt', guide='multiblocks/black_hole_compressor.md')
q('water', 'Super Water Purifier', ['gtapexadditions:super_water_purifier'], [
    'Every grade of purified water in one machine. A programmed circuit picks the grade.',
], ['field_gen'], kind='opt', guide='multiblocks/super_water_purifier.md')
q('data_hub', 'Singularity Data Hub', ['gtapexadditions:singularity_data_hub'], [
    'Bulk item storage on your AE2 network. Its contents survive breaking the controller.',
], ['start'], kind='opt', guide='multiblocks/singularity_data_hub.md')
q('infinitier', 'Crystalline Infinitier', ['gtapexadditions:crystalline_infinitier'], [
    'Grows crystals and gems in bulk.',
], ['field_gen'], kind='opt', guide='multiblocks/crystalline_infinitier.md')
q('outer_planets', '&bUranus and Neptune', ['ad_astra_rocketed:tier_7_rocket'], [
    'The Tier 7 Rocket reaches the edge of the system.',
], ['parts'], kind='opt', guide='dimensions/space.md')
q('glass', 'Neutronium Glass', ['gtapexadditions:neutronium_reinforced_borosilicate_glass*8'], [
    'The UHV tier of multiblock glass.',
], ['start'], kind='opt', guide='gregtech/glass-tiers.md')

q('uev', '&a&lBeyond UHV', ['gtceu:uev_machine_hull'], [
    'The &aUEV Machine Hull&r. From here the tiers are UEV, UIV, UXV, OpV and MAX, and the Space Assembler builds their parts.',
    'Open &aThe Endgame&r.',
], ['assembler_module', 'optical_computer'], kind='hub', bag='uhv')


# ================================================================ more to do in UHV (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'uhv/' + key)
    x.deps += [d if '/' in d else 'uhv/' + d for d in deps if (d if '/' in d else 'uhv/' + d) not in x.deps]


q('uhv_cables', 'UHV Cables', ['gtceu:europium_single_cable*16'], [
    '&eEuropium&r carries UHV. All of it comes out of a fusion reactor.',
], ['start'])
q('sensor', 'UHV Sensor', ['gtceu:uhv_sensor'], [
    'A tier up.',
], ['emitter'])
q('robot_arm', 'UHV Robot Arm', ['gtceu:uhv_robot_arm'], [
    'A tier up.',
], ['parts'])
q('uhv_superconductor', 'UHV Superconductor', ['gtceu:ruthenium_trinium_americium_neutronate_single_wire*8'], [
    'Ruthenium trinium americium neutronate.',
], ['start'], kind='opt')
q('ultimate_battery', 'Ultimate Battery', ['gtceu:max_battery'], [
    'A battery you are not expected to fill.',
], ['start'], kind='opt')
