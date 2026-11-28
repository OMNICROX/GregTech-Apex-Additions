"""Tier 8 - UV: tritanium and americium, Fusion MK III, neutronium, wetware mainframes, Infinity, and Apex's big
processing multiblocks. Gates follow the pack's recipes."""
from fw import Chapter

C = Chapter('uv', '&3Tier 8 - UV', 'gtceu:uv_machine_hull', '', 9, '524,288 EU/t: ultimate voltage', 'uv', 'pick_uv', xp_base=100)
C.derive = False
q = C.q

q('start', '&3&lTier 8: UV', ['gtceu:uv_machine_hull'], [
    'Welcome to &3UV&r: 524,288 EU/t per amp.',
    '&e1.&r &eFusion MK III&r and tritanium coils.',
    '&e2.&r &eNeutronium&r, the UHV metal, and the &eGravi-Star&r.',
    '&e3.&r The &ewetware mainframe&r: the UHV circuit.',
    '> Avaritia\'s Infinity belongs to this tier too.',
], ['zpm/uv'], kind='hub', guide='progression/08-uv.md')

q('parts', '&eUV Components', ['gtceu:uv_electric_motor', 'gtceu:uv_electric_pump', 'gtceu:uv_robot_arm'], [
    'Tritanium parts and fine americium wire on yttrium barium cuprate cable. ZPM-voltage Assembly Line recipes, with researched data.',
], ['start'], kind='main')
q('gravi_star', '&eGravi-Star', ['gtceu:gravi_star'], [
    'A Quantum Star grown in molten &eneutronium&r in an IV autoclave. UV field generators take one each, and so do several UHV controllers.',
], ['neutronium'], kind='main')
q('field_gen', '&eUV Field Generator', ['gtceu:uv_field_generator'], [
    'A tritanium frame, a Gravi-Star, a UV emitter, a UV circuit and fine enriched naquadah trinium europium duranide wire.',
], ['gravi_star', 'parts'], kind='main')
q('energy_hatch', '&eUV Energy Hatch', ['gtceu:uv_energy_input_hatch*2'], [
    'Hull, yttrium barium cuprate cable, a UHPIC chip, a UV circuit and a voltage coil.',
], ['parts'], kind='main')

# ---------------------------------------------------------------- fusion and metals
q('fusion3', '&e&lFusion Reactor MK III', ['gtceu:uv_fusion_reactor'], [
    'Fusion coils, &4UHV circuits&r (wetware mainframes), a Quantum Star, americium plates and ZPM field generators. Casings are Fusion Machine Casing MK III.',
    'It runs recipes up to 640M EU to start.',
], ['wetware_mainframe'], kind='hub', guide='lines/fusion.md')
q('tritanium_coils', '&eTritanium Coils', ['gtceu:tritanium_coil_block*16'], [
    'Tritanium wire, naquadria foil and molten trinium. &e10,800 K&r, the hottest coil GregTech has.',
], ['start'], kind='main', guide='gregtech/ebf-and-coils.md')
q('neutronium', '&4Neutronium', ['gtceu:neutronium_ingot*16'], [
    'The UHV metal. Fusion makes it from americium and naquadria, and the far planets have neutronium ore.',
    'Avaritia\'s neutronium is the same metal here: the Neutronium Collector and Compressor feed the same supply.',
], ['start'], kind='main')
q('uhv_parts', 'First UHV Components', ['gtceu:uhv_electric_motor'], [
    'Neutronium rods and rings, fine americium wire and ruthenium trinium americium neutronate cable. A UV-voltage Assembly Line recipe.',
], ['neutronium', 'parts'])

# ---------------------------------------------------------------- circuits
q('wetware_computer', 'Wetware Supercomputer', ['gtceu:wetware_processor_computer*2'], [
    'An Assembly Line recipe: the &3UV&r circuit of the wetware line.',
], ['start'])
q('wetware_mainframe', '&eWetware Mainframe', ['gtceu:wetware_processor_mainframe*2'], [
    'A tritanium frame, wetware supercomputers and stacks of advanced SMD parts on the Assembly Line at UV. The &4UHV circuit&r.',
    'Every UHV controller in the pack asks for these.',
], ['wetware_computer'], kind='main', guide='gregtech/circuits.md')
q('optical', 'Optical Processor', ['gtna:optical_processor*4'], [
    'The next circuit line starts at UV: optical processors on wetware boards with HASoC chips. Optical and exotic circuits carry you through UEV to UXV.',
], ['wetware_computer'], kind='opt', guide='gregtech/circuits.md')
q('nanochip', 'Nanochip Assembly Complex', ['gtapexadditions:nanochip_assembly_complex'], [
    'Every circuit recipe in bulk, from the circuit assembler and the Assembly Line. Its modules do the steps.',
], ['wetware_mainframe', 'parts'], kind='opt', guide='multiblocks/nanochip_assembly_complex.md')

# ---------------------------------------------------------------- Apex multiblocks
q('iam', 'Integrated Assembly Matrix', ['gtapexadditions:integrated_assembly_matrix'], [
    'A huge assembler and Assembly Line in one.',
], ['field_gen', 'wetware_mainframe'], kind='opt', guide='multiblocks/integrated_assembly_matrix.md')
q('spinmatron', 'Spinmatron-2737', ['gtapexadditions:spinmatron'], [
    'An industrial centrifuge with huge parallels.',
], ['parts'], kind='opt', guide='multiblocks/spinmatron.md')
q('eic', 'Electric Implosion Compressor', ['gtapexadditions:electric_implosion_compressor'], [
    'Every implosion recipe without explosives.',
], ['field_gen'], kind='opt', guide='multiblocks/electric_implosion_compressor.md')
q('fuel_refinery', 'Naquadah Fuel Refinery', ['gtapexadditions:naquadah_fuel_refinery'], [
    'Refines naquadah fuels for the Large Naquadah Reactor. 64 parallels per field restriction coil tier.',
], ['field_gen'], kind='opt', guide='lines/naquadah.md')
q('mining_module', 'Space Mining Module', ['gtapexadditions:space_mining_module_mk1'], [
    'The first Space Elevator module can be built now. The elevator itself is UHV.',
], ['parts'], kind='opt', guide='multiblocks/space_elevator.md')

# ---------------------------------------------------------------- Avaritia and other mods
q('extreme_table', 'Extreme Crafting Table', ['avaritia:extreme_crafting_table'], [
    'Nether Stars, diamond blocks and a crafting table: a 9x9 grid.',
], ['start'], kind='opt', guide='lines/avaritia.md')
q('compressor', 'Neutronium Compressor', ['avaritia:neutron_compressor'], [
    'Needs a Void Thorn and a Blazing Eye from Bosses of Mass Destruction, netherite blocks and Terrasteel blocks. It compresses thousands of an item into a singularity.',
], ['extreme_table'], kind='opt', guide='lines/avaritia.md')
q('infinity', '&e&lInfinity', ['avaritia:infinity_ingot'], [
    'The Infinity Catalyst takes every singularity and the best of Botania. The ingot is crafted in the Extreme Crafting Table, but it is GregTech Infinity: shaping it into parts is UV work.',
], ['compressor'], kind='main', guide='lines/avaritia.md')
q('chaotic', 'Chaotic Tier', ['draconicevolution:chaotic_core'], [
    'Chaos shards and the Chaotic Core: UV. The Chaos Guardian has to die first.',
], ['start'], kind='opt', guide='lines/draconic-evolution.md')
q('saturn', '&eSaturn', ['ad_astra_rocketed:tier_6_rocket'], [
    'The Tier 6 Rocket.',
], ['parts'], kind='opt', guide='dimensions/space.md')
q('mega64', 'MEGA 64M Cells', ['megacells:cell_component_64m'], [
    'The big MEGA cells and the first InsaneAE parts are UV. See the &bAE2&r chapter.',
], ['start'], kind='opt', guide='automation/ae2.md')
q('glass', 'Osmium Glass', ['gtapexadditions:osmium_reinforced_borosilicate_glass*8'], [
    'The UV tier of multiblock glass.',
], ['start'], kind='opt', guide='gregtech/glass-tiers.md')

q('uhv', '&4&lOn to UHV', ['gtceu:uhv_machine_hull'], [
    'Neutronium plates, polybenzimidazole sheets and europium cable: the &4UHV Machine Hull&r.',
    'UHV is 2,097,152 EU/t. GregTech\'s own progression ends here; the Space Elevator and Apex\'s endgame take over. Open &4Tier 9 - UHV&r.',
], ['neutronium', 'wetware_mainframe'], kind='hub', bag='uv')


# ================================================================ more to do in UV (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'uv/' + key)
    x.deps += [d if '/' in d else 'uv/' + d for d in deps if (d if '/' in d else 'uv/' + d) not in x.deps]


q('uv_cables', 'UV Cables', ['gtceu:yttrium_barium_cuprate_single_cable*16'], [
    '&eYttrium barium cuprate&r carries UV.',
], ['start'])
q('sensor', 'UV Emitter and Sensor', ['gtceu:uv_emitter', 'gtceu:uv_sensor'], [
    'Both, for the field generator and nearly everything after it.',
], ['parts'])
q('robot_arm', 'UV Robot Arm', ['gtceu:uv_robot_arm'], [
    'A tier up.',
], ['parts'])
q('energy_cluster', 'Energy Cluster', ['gtceu:energy_cluster'], [
    'The UV battery.',
], ['start'], kind='opt')
q('uv_superconductor', 'UV Superconductor', ['gtceu:enriched_naquadah_trinium_europium_duranide_single_wire*8'], [
    'The longest name in the pack so far.',
], ['start'], kind='opt')
q('uv_quantum', 'UV Quantum Chest', ['gtceu:uv_quantum_chest'], [
    'The last of the quantum chests.',
], ['field_gen'], kind='opt')
q('adv_quarktech', 'Advanced QuarkTech', ['gtceu:advanced_quarktech_chestplate'], [
    'QuarkTech with a jetpack built in.',
], ['gravi_star'], kind='opt')


# ================================================================ the multis and materials of UV (2026-11-28, second pass)
q('special_lens', '&eSpecial Laser Lens', ['gtna:laser_lens_special'], [
    'Assembly Line: a diamond lens, dense duranium plates, a Quantum Star and a Crystal Mainframe in naquadria.',
    'It engraves ordinary dusts into the metals of the endgame: advanced nitinol now, astral titanium, chronomatic glass and celestial tungsten later.',
], ['parts'], kind='main')
q('adv_nitinol', 'Advanced Nitinol', ['gtna:advanced_nitinol_ingot*4'], [
    'Nitinol 60 dust under the special lens in a UV laser engraver, then blasted.',
], ['special_lens'])
q('stellar_alloy', 'Stellar Alloy', ['gtna:stellar_alloy_ingot*4'], [
    'Alloy Blast Smelter: neutron dust, naquadria dust and trinium dust.',
], ['start'], kind='opt')
q('uv_assembler', 'Ultimate Assembler', ['gtceu:uv_assembler'], [
    'Most UV controllers that are not on the Assembly Line are made in one.',
], ['robot_arm'])
q('uv_4a', 'UV 4A Energy Hatch', ['gtceu:uv_energy_input_hatch_4a'], [
    'Four amps through one hatch. Long recipes in this pack ask for more amps, not more volts.',
], ['energy_hatch'], kind='opt', guide='gregtech/oc/oc-index.md')
q('drone', 'UV Mining Drone', ['gtapexadditions:mining_drone_uv'], [
    'Drones are built in a chain from LV: each is the one before it with this tier\'s motor, sensor and circuit.',
], ['sensor'])
q('motor1', 'Space Elevator Motor MK-I', ['gtapexadditions:space_elevator_motor_mk1'], [
    'A neutronium frame, a UV motor and a Crystal Mainframe. The Space Elevator at UHV needs motors.',
], ['parts', 'neutronium'], kind='opt', guide='multiblocks/space_elevator.md')
q('water5', 'Grade 5 Purified Water', ['gtapexadditions:purification_unit_plasma_heater'], [
    'The Plasma Heater unit: grade 4 water, helium plasma and liquid helium.',
], ['parts', 'zpm/water4'], kind='opt', guide='multiblocks/purification_plant.md')
q('water6', 'Grade 6 Purified Water', ['gtapexadditions:purification_unit_uv_treatment'], [
    'The High Energy Laser unit: grade 5 water under nine lenses, from glass to amethyst.',
], ['water5'], kind='opt', guide='multiblocks/purification_plant.md')
q('compact3', 'Compact Fusion Computer MK-III', ['gtapexadditions:compact_fusion_mk3'], [
    'The MK III reactor, a compact coil, a UV field generator and dense tritanium plates.',
], ['fusion3', 'field_gen'], kind='opt', guide='lines/fusion.md')
q('nac_casings', 'Nanochip Casings', ['gtapexadditions:nanochip_mesh_interface_casing*8', 'gtapexadditions:nanochip_computational_matrix_casing*4'], [
    'The body of the Nanochip Assembly Complex.',
], ['start'])
q('nac_modules', 'Nanochip Modules', ['gtapexadditions:nac_module_assembly_matrix', 'gtapexadditions:nac_module_board_processor', 'gtapexadditions:nac_module_wire_tracer'], [
    'The complex is built from modules, nine kinds in all. Each is a computational matrix casing around the UV machine that does its job.',
], ['nac_casings'], kind='opt', guide='multiblocks/nanochip_assembly_complex.md')
q('coil_t1', 'Field Restriction Coil', ['gtapexadditions:field_restriction_coil_t1*4'], [
    'Dense naquadah alloy, ZPM superconductor and a UV field generator. The Naquadah Fuel Refinery\'s coil; better coils let it refine better fuel.',
], ['field_gen'], guide='multiblocks/naquadah_fuel_refinery.md')
q('naq_fuels', 'Naquadah Fuels', ['gtapexadditions:light_naquadah_fuel_bucket', 'gtapexadditions:heavy_naquadah_fuel_bucket'], [
    'Light fuel is distilled from naquadah solution, heavy fuel from naquadah asphalt in a distillation tower. Every liquid naquadah fuel starts from these two.',
], ['start'], kind='opt', guide='lines/naquadah.md')
q('collapser', 'Black Hole Collapser', ['gtapexadditions:black_hole_collapser'], [
    'A Gravi-Star, a neutronium plate and a Crystal Mainframe. It closes the black hole of the UHV compressor; make it before you need it.',
], ['gravi_star'], kind='opt', guide='multiblocks/black_hole_compressor.md')
q('nano_forge', 'Nano Forge', ['gtna:nano_forge', 'gtna:carbon_nanites'], [
    'The Nanite Fabricator. A nanite in an input bus sets its tier and is never used up: carbon is tier 1.',
    'Carbon nanites are an Assembly Line recipe; the forge makes the rest.',
], ['parts'], kind='opt')
q('wireless', 'Wireless Energy', ['gtna:nexus_flux_matrix', 'gtna:wireless_energy_hatch_1a_uv'], [
    'A &eNexus Flux Matrix&r is a wireless energy store that reaches everywhere. Wireless energy hatches pull from it, wireless dynamo hatches push into it.',
], ['sensor'], kind='opt')
q('thread', 'Thread Hatch', ['gtna:thread_hatch_uv'], [
    'Gives a GTNA multiblock extra threads, so it can run different recipes at the same time.',
], ['robot_arm'], kind='opt')
q('optical_proc', 'Optical Processor', ['gtna:optical_processor*4'], [
    'A UV circuit assembler recipe on a wetware board, in Indalloy 140. The first of the optical line.',
], ['start'], guide='gregtech/circuits.md')
q('crystal_matrix', 'Crystal Matrix', ['avaritia:crystal_matrix_ingot*4'], [
    'The heart of the infinity ingot: the extreme crafting recipe is neutronium ingots around crystal matrix.',
], ['extreme_table'], guide='lines/avaritia.md')
q('singularity', 'Eternal Singularity', ['avaritia:eternal_singularity'], [
    'One of every singularity, crafted together. The Neutronium Compressor makes each one from thousands of a material.',
], ['compressor'], kind='opt', guide='lines/avaritia.md')
q('catalyst', 'Infinity Catalyst', ['avaritia:infinity_catalyst'], [
    'Crystal matrix, neutronium, cosmic meatballs, ultimate stew, an endest pearl, a record fragment, life essence, terrasteel and alfsteel, among others.',
], ['singularity', 'crystal_matrix'], kind='opt', guide='lines/avaritia.md')
also('infinity', 'crystal_matrix')
also('nanochip', 'nac_casings')
also('fuel_refinery', 'coil_t1')
