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
