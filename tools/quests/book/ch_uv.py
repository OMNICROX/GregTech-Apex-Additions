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
