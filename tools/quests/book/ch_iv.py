"""Tier 5 - IV: the platinum group, quantum processors, the Assembly Line and LuV components, and the first wave of
Apex's own multiblocks. Gates follow the pack's recipes."""
from fw import Chapter

C = Chapter('iv', '&9Tier 5 - IV', 'gtceu:iv_machine_hull', '', 6, '8,192 EU/t: insane voltage', 'iv', 'pick_iv', xp_base=55)
C.derive = False
q = C.q

q('start', '&9&lTier 5: IV', ['gtceu:iv_machine_hull'], [
    'Welcome to &9IV&r: 8,192 EU/t per amp.',
    'IV is the last tier you build by hand. Its goals:',
    '&e1.&r The &eplatinum group&r: palladium, rhodium, ruthenium, iridium and osmium.',
    '&e2.&r The &eAssembly Line&r. Every LuV component is made in it.',
    '&e3.&r &eQuantum processors&r and the new plastic, polybenzimidazole.',
    '> Apex\'s own multiblocks start here in force. Each one replaces a whole room of machines.',
], ['ev/iv'], kind='hub', guide='progression/05-iv.md')

# ---------------------------------------------------------------- components
q('graphene', 'Graphene', ['gtceu:graphene_dust*8'], [
    'IV motors are wound in graphene wire. Graphene is made from graphite, silicon and carbon in a mixer; check the recipe.',
], ['start'])
q('motor', '&eIV Motor', ['gtceu:iv_electric_motor'], [
    'Graphene wire, tungstensteel rods, a magnetic neodymium rod and tungsten cable.',
], ['graphene'], kind='main')
q('parts', 'IV Components', ['gtceu:iv_electric_pump', 'gtceu:iv_conveyor_module', 'gtceu:iv_electric_piston'], [
    'The same shapes one tier up, in tungstensteel. Check each recipe for its rubber.',
], ['motor'])
q('robot_arm', 'IV Robot Arm', ['gtceu:iv_robot_arm'], [
    'The Assembly Line controller needs two.',
], ['parts'])
q('quantum_star', 'Quantum Star', ['gtceu:quantum_star'], [
    'A Nether Star bathed in radon. IV and LuV field generators are built around one.',
], ['start'])
q('field_gen', '&eIV Field Generator', ['gtceu:iv_field_generator'], [
    'A Quantum Star, double tungstensteel plates, IV circuits and samarium iron arsenic oxide wire. Half of Apex\'s IV multiblocks ask for one.',
], ['quantum_star', 'motor'], kind='main')
q('hpic', 'HPIC Chips', ['gtceu:hpic_chip*8'], [
    'High-power ICs, cut at IV. They need &eindium gallium phosphide&r: indium is squeezed out of sphalerite and galena byproduct chemistry.',
], ['start'])
q('energy_hatch', '&eIV Energy Hatch', ['gtceu:iv_energy_input_hatch*2'], [
    'An IV hull, tungsten cable, an HPIC chip and an Insane Voltage Coil. Two of them run &dLuV recipes&r.',
], ['hpic'], kind='main', guide='gregtech/oc/tier-skipping.md')

# ---------------------------------------------------------------- platinum group
q('sludge', 'Platinum Group Sludge', ['gtceu:platinum_group_sludge_dust*8'], [
    'The leftover of sheldonite, and of nickel, copper and chalcopyrite chemistry: electrolyzing their purified ores or washing them in nitric acid leaves this sludge.',
], ['start'], guide='lines/platinum.md')
q('pgm', '&e&lThe Platinum Group', ['gtceu:palladium_ingot*8', 'gtceu:platinum_ingot*8'], [
    'Dissolve the sludge in &eaqua regia&r (nitric and hydrochloric acid). Platinum and palladium come out as salts first; the residues go on.',
    'Follow the Ore Processing Diagram and JEI step by step. It is a long line, and all of it ends up automated.',
], ['sludge'], kind='hub', guide='lines/platinum.md')
q('rhodium', 'Rhodium and Ruthenium', ['gtceu:rhodium_dust*4', 'gtceu:ruthenium_dust*4'], [
    'From the &einert metal mixture&r left by the platinum step. Ruthenium goes into ruridit (LuV wire), rhodium onto palladium.',
], ['pgm'])
q('iridium', 'Iridium and Osmium', ['gtceu:iridium_dust*4', 'gtceu:osmium_dust*4'], [
    'From the &erarest metal mixture&r, through acidic osmium solution and iridium chloride. Iridium is the ZPM metal; osmiridium is everywhere from LuV.',
], ['pgm'])
q('rpp', '&dRhodium-Plated Palladium', ['gtceu:rhodium_plated_palladium_ingot*8'], [
    'Palladium and rhodium, 3:1, at 4,500 K: &eRTM alloy coils&r. This is the LuV hull metal.',
], ['rhodium', 'rtm_coils'], kind='main')

# ---------------------------------------------------------------- coils and steels
q('rtm_coils', '&eRTM Alloy Coils', ['gtceu:rtm_alloy_coil_block*16'], [
    'Ruthenium, tungsten and molybdenum make RTM alloy; its wire with vanadium steel foil and molten nichrome makes the coil. &e4,500 K&r.',
], ['rhodium'], kind='main', guide='gregtech/ebf-and-coils.md')
q('hss', 'High Speed Steels', ['gtceu:hssg_ingot*8', 'gtceu:hsse_ingot*8', 'gtceu:hsss_ingot*8'], [
    '&eHSS-G&r (tungstensteel, chrome, molybdenum, vanadium) is the base. &eHSS-E&r adds cobalt, manganese and silicon; &eHSS-S&r adds iridium and osmium.',
    'LuV motors and frames are HSS-S, and HSS-G makes the next coils. Use the Alloy Blast Smelter.',
], ['rtm_coils'])
q('hssg_coils', 'HSS-G Coils', ['gtceu:hssg_coil_block*16'], [
    'HSS-G wire, tungsten carbide foil and molten tungsten. &e5,400 K&r: naquadah and the first fusion materials.',
], ['hss'], kind='opt', guide='gregtech/ebf-and-coils.md')

# ---------------------------------------------------------------- plastics and circuits
q('pbi', '&ePolybenzimidazole', ['gtceu:polybenzimidazole_plate*16'], [
    'The last plastic: diaminobenzidine and diphenyl isophthalate, an &9IV&r reactor recipe at the end of a long organic chain.',
    'Advanced SMD parts are made with it, and every hull from ZPM up is wrapped in it.',
], ['start'], kind='main', guide='lines/rubber-and-plastics.md')
q('adv_smd', 'Advanced SMD Parts', ['gtceu:advanced_smd_resistor*16', 'gtceu:advanced_smd_capacitor*16', 'gtceu:advanced_smd_transistor*16'], [
    'Made with liquid polybenzimidazole in an IV assembler. Quantum and crystal processors take these.',
], ['pbi'])
q('fiber_board', 'Fiber-Reinforced Boards', ['gtceu:fiber_reinforced_printed_circuit_board*8'], [
    'Reinforced epoxy resin sheets (epoxy over glass or carbon fiber) with annealed copper foil.',
], ['start'])
q('quantum', '&eQuantum Processor', ['gtceu:quantum_processor*4'], [
    'A fiber-reinforced board, a &eQubit CPU chip&r, a nano CPU chip and advanced SMD parts, at IV in the Cleanroom. An &5EV&r circuit.',
], ['fiber_board', 'adv_smd'], kind='main', guide='gregtech/circuits.md')
q('quantum_assembly', 'Quantum Assembly', ['gtceu:quantum_processor_assembly*2'], [
    'The &9IV&r circuit of the quantum line.',
], ['quantum'])
q('quantum_computer', 'Quantum Supercomputer', ['gtceu:quantum_processor_computer*2'], [
    'A &dLuV&r circuit.',
], ['quantum_assembly'])
q('nano_mainframe', '&eNanoprocessor Mainframe', ['gtceu:nano_processor_mainframe*2'], [
    'The &dLuV&r circuit you can make right now, and the one most LuV multiblock controllers ask for.',
], ['start'], kind='main')

# ---------------------------------------------------------------- assembly line
q('scanner', 'Research Data', ['gtceu:ev_scanner', 'gtceu:data_stick'], [
    'Assembly Line recipes must be &eresearched&r: scan the item named in the recipe onto a data stick, and put the stick in a Data Access Hatch on the line.',
], ['start'], guide='gtmultis/research.md')
q('assline', '&e&lAssembly Line', ['gtceu:assembly_line'], [
    'A long multiblock, 5 to 16 slices. Each slice has one input bus underneath; items go in &oin recipe order&r, one stack type per bus, and fluids in order too.',
    'The controller takes IV circuits, IV robot arms and Assembly Line Casings.',
], ['robot_arm', 'scanner'], kind='hub', guide='gtmultis/assembly_line.md')
q('luv_motor', '&dLuV Motor', ['gtceu:luv_electric_motor'], [
    'Your first Assembly Line product: a long magnetic &esamarium&r rod, HSS-S parts, fine &eruridit&r wire and niobium-titanium cable, with soldering alloy and lubricant.',
    'It is an &9IV&r recipe, so the line runs on IV power.',
], ['assline', 'hss'], kind='main')
q('cal', 'Component Assembly Line', ['gtapexadditions:component_assembly_line'], [
    'Builds motors, pumps, pistons, conveyors, arms, emitters, sensors and field generators in bulk. Its casing tier limits which tiers it makes.',
], ['assline', 'iridium'], kind='opt', guide='multiblocks/component_assembly_line.md')

# ---------------------------------------------------------------- power
q('ece', 'Extreme Combustion Engine', ['gtceu:extreme_combustion_engine'], [
    'The IV engine: 8,192 EU/t on diesel fuels, more with liquid oxygen. It needs a LuV circuit.',
], ['motor', 'nano_mainframe'], kind='opt')
q('lnr', 'Large Naquadah Reactor', ['gtapexadditions:large_naquadah_reactor'], [
    'Burns liquid nuclear and naquadah fuels. It needs liquid air every second, or it voids the fuel. This is the long-term power plant from IV to UV.',
], ['assline', 'field_gen'], kind='opt', guide='multiblocks/large_naquadah_reactor.md')
q('pss', 'Power Substation', ['gtceu:power_substation'], [
    'A battery the size of a building: palladium substation casings and layers of lapotronic capacitors. Loses 1% a day.',
], ['nano_mainframe'], kind='opt', guide='gtmultis/power_substation.md')

# ---------------------------------------------------------------- Apex multiblocks
q('ruina', 'Library of Ruina', ['gtapexadditions:library_of_ruina'], [
    'Built from the trophies of the Naga, the Lich, the Hydra and the Ur-Ghast. It makes Twilight Forest boss loot in bulk.',
], ['field_gen'], kind='opt', guide='multiblocks/library_of_ruina.md')
q('bloody_hell', 'Bloody Hell', ['gtapexadditions:bloody_hell'], [
    'Every Blood Altar, Alchemy Table and Alchemy Array recipe in bulk, soaked from a pool of Life Essence. It needs a Master Blood Orb.',
], ['start'], kind='opt', guide='multiblocks/bloody_hell.md')
q('alfheim', 'Alfheim Teleportation Array', ['gtapexadditions:alfheim_teleportation_array'], [
    'Every Botania altar craft as a GregTech recipe. After the Gaia Guardian, Gaia Spirit recipes are made only here.',
], ['field_gen'], kind='opt', guide='multiblocks/alfheim_teleportation_array.md')
q('craft_center', 'Extreme Craft Center', ['gtapexadditions:extreme_craft_center'], [
    'An ME crafting provider: crafting patterns run inside it with pattern magnification, like a wall of molecular assemblers.',
], ['robot_arm'], kind='opt', guide='multiblocks/extreme_craft_center.md')
q('nether_interface', 'Nether Interface', ['gtapexadditions:nether_interface'], [
    'Pulls netherrack, lava, quartz and glowstone out of the Nether with power.',
], ['field_gen'], kind='opt', guide='multiblocks/nether_interface.md')
q('silicon_rock', 'Silicon Rock Synthesizer', ['gtapexadditions:silicon_rock_synthesizer'], [
    'Stone and silicon dust in bulk. Upgrade of the Industrial Rock Breaker.',
], ['parts'], kind='opt', guide='multiblocks/silicon_rock_synthesizer.md')

# ---------------------------------------------------------------- worlds and other mods
q('venus', '&6Venus and Mercury', ['ad_astra:tier_3_rocket'], [
    'The Tier 3 Rocket is ostrum from Mars. The &eJet Suit&r is an IV assembler recipe.',
], ['motor'], kind='opt', guide='dimensions/space.md')
q('draconium', 'Draconic Evolution', ['draconicevolution:draconium_core', 'draconicevolution:crafting_core'], [
    'Draconium is smelted in the blast furnace, and Draconic Evolution\'s base items are IV assembler recipes. Wyvern is LuV.',
], ['start'], kind='opt', guide='lines/draconic-evolution.md')
q('blood_orb', 'Archmage Blood Orb', ['gtapexadditions:raw_blood_orb_5'], [
    'The last raw orb. See the &4Magic&r chapter.',
], ['start'], kind='opt', guide='magic/blood-magic.md')
q('w_glass', 'Tungstensteel Glass', ['gtapexadditions:tungstensteel_reinforced_borosilicate_glass*8'], [
    'The IV tier of multiblock glass.',
], ['start'], kind='opt', guide='gregtech/glass-tiers.md')

q('luv', '&d&lOn to LuV', ['gtceu:luv_machine_hull'], [
    'Rhodium-plated palladium plates, PTFE sheets and niobium-titanium cable: the &dLuV Machine Hull&r.',
    'LuV is 32,768 EU/t. It brings fusion, naquadah and wetware. Open &dTier 6 - LuV&r.',
], ['rpp', 'luv_motor'], kind='hub', bag='iv')
