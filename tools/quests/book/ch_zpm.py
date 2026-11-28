"""Tier 7 - ZPM: osmiridium and europium components, Fusion MK II, trinium coils, the Research Station and wetware
processors. Gates follow the pack's recipes."""
from fw import Chapter

C = Chapter('zpm', '&cTier 7 - ZPM', 'gtceu:zpm_machine_hull', '', 8, '131,072 EU/t: zero point module', 'zpm', 'pick_zpm', xp_base=85)
C.derive = False
q = C.q

q('start', '&c&lTier 7: ZPM', ['gtceu:zpm_machine_hull'], [
    'Welcome to &cZPM&r: 131,072 EU/t per amp.',
    '&e1.&r &eFusion MK II&r: americium, duranium and the darmstadtium of the UV hull.',
    '&e2.&r The &eResearch Station&r. Most recipes from here need researched data, not a scan.',
    '&e3.&r &eWetware processors&r.',
], ['luv/zpm'], kind='hub', guide='progression/07-zpm.md')

q('parts', '&eZPM Components', ['gtceu:zpm_electric_motor', 'gtceu:zpm_electric_pump', 'gtceu:zpm_robot_arm'], [
    'Osmiridium parts, long magnetic samarium rods and fine &eeuropium&r wire, on vanadium-gallium cable. LuV-voltage Assembly Line recipes.',
], ['start'], kind='main')
q('emitter', 'ZPM Emitter and Sensor', ['gtceu:zpm_emitter', 'gtceu:zpm_sensor'], [
    'Quantum Stars again, on naquadah alloy frames.',
], ['parts'])
q('field_gen', '&eZPM Field Generator', ['gtceu:zpm_field_generator'], [
    'A naquadah alloy frame, a Quantum Star, an emitter, a ZPM circuit and fine uranium rhodium dinaquadide wire.',
], ['emitter'], kind='main')
q('energy_hatch', '&eZPM Energy Hatch', ['gtceu:zpm_energy_input_hatch*2'], [
    'Hull, vanadium-gallium cable, a UHPIC chip, a ZPM circuit and a voltage coil.',
], ['parts'], kind='main')

# ---------------------------------------------------------------- research
q('hpca', '&eHigh Performance Computation Array', ['gtceu:high_performance_computation_array', 'gtceu:hpca_computation_component*2'], [
    'A 3x3 grid of components: computation, coolers and bridges. It makes the &eCWU/t&r that research consumes. Cool it with PCB coolant, or it damages its own parts.',
], ['parts'], kind='main', guide='gtmultis/research.md')
q('research', '&e&lResearch Station', ['gtceu:research_station', 'gtceu:object_holder'], [
    'Put the item to research and a &edata orb or module&r in the Object Holder, feed computation over optical fiber, and wait.',
    'Recipes list their research cost in CWU. The finished data goes in a Data Bank or a Data Access Hatch.',
], ['hpca', 'field_gen'], kind='hub', guide='gtmultis/research.md')
q('network_switch', 'Network Switch', ['gtceu:network_switch'], [
    'Joins several computation arrays into one supply.',
], ['research'], kind='opt')

# ---------------------------------------------------------------- fusion and metals
q('fusion_coil', 'Fusion Coils', ['gtceu:fusion_coil*16'], [
    'A superconducting coil, IV field generators and pumps, neutron reflectors and LuV circuits. The MK II ring.',
], ['start'])
q('fusion2', '&e&lFusion Reactor MK II', ['gtceu:zpm_fusion_reactor'], [
    'Fusion coils, &3UV circuits&r (crystal mainframes), naquadria and europium plates and LuV field generators. Casings are Fusion Machine Casing MK II.',
    'It runs recipes up to 320M EU to start.',
], ['fusion_coil', 'crystal_mainframe', 'field_gen'], kind='hub', guide='lines/fusion.md')
q('americium', '&eAmericium', ['gtceu:americium_ingot*8'], [
    'Lutetium and chromium fuse into americium; lutetium itself is fused from lanthanum and silicon. UV motors are wound in americium wire and the MK III is plated with it.',
], ['fusion2'], kind='main')
q('duranium', 'Duranium and Tritanium', ['gtceu:duranium_ingot*8', 'gtceu:tritanium_ingot*8'], [
    'Gallium and radon fuse into duranium; duranium and titanium into &etritanium&r, the UV structural metal: frames, rods, rotors and the next coils.',
], ['fusion2'])
q('darmstadtium', '&3Darmstadtium', ['gtceu:darmstadtium_ingot*8'], [
    'The UV hull metal: arsenic and ruthenium, fused.',
], ['fusion2'], kind='main')
q('trinium_coils', '&eTrinium Coils', ['gtceu:trinium_coil_block*16'], [
    'Trinium wire, enriched naquadah foil and molten naquadah. &e9,001 K&r.',
], ['start'], kind='main', guide='gregtech/ebf-and-coils.md')
q('rhf', 'Rotary Hearth Furnace', ['gtceu:mega_blast_furnace'], [
    'The mega blast furnace: every EBF recipe with parallels. Needs ZPM field generators and quantum mainframes.',
], ['field_gen', 'trinium_coils'], kind='opt')

# ---------------------------------------------------------------- circuits
q('crystal_mainframe', '&eCrystal Mainframe', ['gtceu:crystal_processor_mainframe*2'], [
    'An Assembly Line recipe at LuV voltage: an HSS-E frame, crystal supercomputers, RAM, power chips and advanced SMD parts. A &3UV circuit&r.',
], ['start'], kind='main', guide='gregtech/circuits.md')
q('neuro', 'Neuro Processing Unit', ['gtceu:neuro_processing_unit*4'], [
    'A wetware printed board, stem cells, polybenzimidazole pipes and sterilized growth medium, in the sterile Cleanroom.',
], ['start'])
q('wetware', '&eWetware Processor', ['gtceu:wetware_processor*4'], [
    'A neuro processing unit, a crystal CPU, a nano CPU and advanced SMD parts at ZPM. A &dLuV&r circuit.',
], ['neuro'], kind='main', guide='gregtech/circuits.md')
q('wetware_assembly', 'Wetware Assembly', ['gtceu:wetware_processor_assembly*2'], [
    'The &cZPM&r circuit of the wetware line.',
], ['wetware'])

# ---------------------------------------------------------------- Apex multiblocks and other mods
q('matter_fab', 'Matter Fabricator', ['gtapexadditions:matter_fabricator'], [
    'UU-matter in bulk.',
], ['field_gen'], kind='opt', guide='multiblocks/matter_fabricator.md')
q('hearth', 'Exothermic Hearth', ['gtapexadditions:exothermic_hearth'], [
    'A huge blast furnace that heats itself.',
], ['crystal_mainframe'], kind='opt', guide='multiblocks/exothermic_hearth.md')
q('mega_dt', 'Mega Distillation Tower', ['gtapexadditions:mega_distillation_tower'], [
    'Distillation with up to 256 parallels.',
], ['field_gen'], kind='opt', guide='multiblocks/mega_distillation_tower.md')
q('eden', 'Eden Garden', ['gtapexadditions:eden_garden'], [
    'Greenhouse recipes, a hundred times faster.',
], ['parts'], kind='opt', guide='multiblocks/eden_garden.md')
q('mega_cells', 'MEGA Cells', ['megacells:cell_component_1m', 'megacells:mega_crafting_unit'], [
    'MEGA Cells\' storage and crafting CPUs are ZPM assembler recipes. See the &bAE2&r chapter.',
], ['start'], kind='opt', guide='automation/ae2.md')
q('draconic', 'Draconic Tier', ['draconicevolution:awakened_core'], [
    'Awakened draconium and the Draconic Core: ZPM. Fusion-craft them in Draconic Fusion Crafting.',
], ['start'], kind='opt', guide='lines/draconic-evolution.md')
q('jupiter', '&6Jupiter', ['ad_astra_rocketed:tier_5_rocket'], [
    'The Tier 5 Rocket reaches Jupiter and its moons.',
], ['parts'], kind='opt', guide='dimensions/space.md')
q('glass', 'Iridium Glass', ['gtapexadditions:iridium_reinforced_borosilicate_glass*8'], [
    'The ZPM tier of multiblock glass.',
], ['start'], kind='opt', guide='gregtech/glass-tiers.md')

q('uv', '&3&lOn to UV', ['gtceu:uv_machine_hull'], [
    'Darmstadtium plates, polybenzimidazole sheets and yttrium barium cuprate cable: the &3UV Machine Hull&r.',
    'UV is 524,288 EU/t: the last of GregTech\'s own tiers, and the first of the endgame. Open &3Tier 8 - UV&r.',
], ['darmstadtium', 'americium'], kind='hub', bag='zpm')


# ================================================================ more to do in ZPM (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'zpm/' + key)
    x.deps += [d if '/' in d else 'zpm/' + d for d in deps if (d if '/' in d else 'zpm/' + d) not in x.deps]


q('zpm_cables', 'ZPM Cables', ['gtceu:vanadium_gallium_single_cable*16'], [
    '&eVanadium-gallium&r carries ZPM.',
], ['start'])
q('sensor', 'ZPM Sensor', ['gtceu:zpm_sensor'], [
    'A tier up.',
], ['emitter'])
q('robot_arm', 'ZPM Robot Arm', ['gtceu:zpm_robot_arm'], [
    'A tier up.',
], ['parts'])
q('hpca_parts', 'Computation Components', ['gtceu:hpca_computation_component*2'], [
    'What goes inside the High Performance Computing Array. Each component makes &eCWU/t&r and heat.',
], ['hpca'])
q('hpca_cooling', 'Cooling', ['gtceu:hpca_heat_sink_component*2', 'gtceu:hpca_active_cooler_component'], [
    'Heat sinks cool a little for nothing; active coolers cool a lot for coolant. An array that overheats damages its own components.',
], ['hpca_parts'])
q('hpca_bridge', 'Bridge Component', ['gtceu:hpca_bridge_component'], [
    'Lets a network switch combine this array with others.',
], ['hpca_parts'], kind='opt')
also('network_switch', 'hpca_bridge')
q('optical', 'Optical Fiber', ['gtceu:normal_optical_pipe*16'], [
    'Computation travels on optical fiber, from a transmitter hatch to a receiver hatch. A fiber joins exactly two things: no branches.',
], ['hpca'])
q('comp_hatches', 'Computation Hatches', ['gtceu:computation_transmitter_hatch', 'gtceu:computation_receiver_hatch'], [
    'One on the array, one on what needs the computation.',
], ['optical'])
also('research', 'comp_hatches')
q('holder', 'Object Holder', ['gtceu:object_holder'], [
    'The part of the Research Station that holds the item being researched and the data item it is written to.',
], ['hpca'])
also('research', 'holder')
q('data_module', 'Data Module', ['gtceu:data_module'], [
    'The research of this tier and above is too big for a data orb.',
], ['research'])
q('trinium', 'Trinium', ['gtceu:trinium_ingot*8'], [
    'From naquadah processing. The coil of this tier is made of it.',
], ['start'])
also('trinium_coils', 'trinium')
q('tritanium', 'Tritanium', ['gtceu:tritanium_ingot*4'], [
    'Titanium and duranium in the fusion reactor.',
], ['duranium'])
q('energy_module', 'Energy Module', ['gtceu:energy_module'], [
    'The ZPM battery.',
], ['start'], kind='opt')
q('zpm_superconductor', 'ZPM Superconductor', ['gtceu:uranium_rhodium_dinaquadide_single_wire*8'], [
    'Uranium rhodium dinaquadide.',
], ['start'], kind='opt')
q('mega_freezer', 'Bulk Blast Chiller', ['gtceu:mega_vacuum_freezer'], [
    'The blast furnace\'s partner at the same scale.',
], ['rhf'], kind='opt')


# ================================================================ the multis of ZPM (2026-11-28, second pass)
q('water3', 'Grade 3 Purified Water', ['gtapexadditions:purification_unit_flocculation'], [
    'The Flocculation unit: grade 2 water, better with polyaluminium chloride (aluminium dust in hydrochloric acid).',
], ['parts', 'luv/water2'], kind='opt', guide='multiblocks/purification_plant.md')
q('water4', 'Grade 4 Purified Water', ['gtapexadditions:purification_unit_ph_adjustment'], [
    'The pH Neutralization unit: grade 3 water with sodium hydroxide dust and hydrochloric acid.',
], ['water3'], kind='opt', guide='multiblocks/purification_plant.md')
q('compact2', 'Compact Fusion Computer MK-II', ['gtapexadditions:compact_fusion_mk2'], [
    'The MK II reactor, an advanced compact coil, a ZPM field generator and dense duranium plates.',
], ['fusion2'], kind='opt', guide='lines/fusion.md')
q('mega_abs', 'Mega Alloy Blast Smelter', ['gtna:mega_alloy_blast_smelter'], [
    'Alloy Blast Smelter recipes with a Parallel Hatch, at 80% of the energy and 60% of the time.',
], ['field_gen'], kind='opt')
q('me_storage', 'ME Storage Multiblock', ['gtna:me_storage'], [
    'An expandable ME store: repeat the core slice for up to 128 storage sections.',
], ['field_gen'], kind='opt', guide='automation/ae2.md')
q('molecular_forge', 'Nexus Molecular Forge', ['gtna:nexus_molecular_forge'], [
    'AE2 mass crafting: it queues jobs from pattern hatches and makes every queued output at once, for 1 EU per crafted item.',
], ['field_gen'], kind='opt', guide='automation/ae2.md')
q('trinaquadalloy', 'Trinaquadalloy', ['gtna:trinaquadalloy_ingot*8'], [
    'Trinium, naquadah and carbon dust, alloyed and blasted.',
], ['trinium'], kind='opt')
q('magneto', 'Magneto Resonatic Gem', ['gtna:magneto_resonatic_gem'], [
    'Amethyst, bismuth, zirconium and magnetic steel dust mixed, then grown in an autoclave. The lenses of the Nano Forge are cut from it.',
], ['start'], kind='opt')
q('drone', 'ZPM Mining Drone', ['gtapexadditions:mining_drone_zpm'], [
    'A ZPM drone reaches naquadah asteroids once the Space Elevator stands.',
], ['emitter'], kind='opt')
q('zpm_assembler', 'ZPM Assembler', ['gtceu:zpm_assembler'], [
    'A tier up.',
], ['robot_arm'])
