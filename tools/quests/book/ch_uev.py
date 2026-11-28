"""Tier 10 - UEV: Indalloy and neutronium machines, the Plasma Forge and its catalysts, the metals UIV is made of."""
from fw import Chapter

C = Chapter('uev', '&aTier 10 - UEV', 'gtceu:uev_machine_hull', '', 11, '8,388,608 EU/t: the first tier past GregTech', 'end', 'pick_end', xp_base=150)
C.derive = False
q = C.q

q('start', '&a&lTier 10: UEV', ['gtceu:uev_machine_hull'], [
    'Welcome to &aUEV&r: 8,388,608 EU/t per amp.',
    'A UEV casing is still neutronium plates. What is new is around it: the &ePlasma Forge&r smelts what no blast furnace can, and the &eSpace Assembler&r builds what the Assembly Line cannot.',
    'This tier ends with the three metals UIV is made of: &ecelestial tungsten&r, &eattuned tengam&r and &ehypogen&r.',
], ['uhv/uev'], kind='hub', guide='progression/10-uev-plus.md')
q('indalloy', '&eIndalloy 140', ['gtna:indalloy_140_ingot*8'], [
    'Bismuth, lead, tin, cadmium and indium dust in the Alloy Blast Smelter.',
    'Molten Indalloy 140 is the solder of every UEV and UIV component.',
], ['start'], kind='main')
q('parts', '&eUEV Components', ['gtceu:uev_electric_motor', 'gtceu:uev_electric_pump', 'gtceu:uev_electric_piston', 'gtceu:uev_conveyor_module'], [
    'Assembly Line recipes at UHV voltage: neutronium, samarium magnets, americium wire and the UHV superconductor, in molten Indalloy 140 and naquadria.',
], ['indalloy'], kind='main')
q('robot_arm', 'UEV Robot Arm', ['gtceu:uev_robot_arm'], [
    'Takes three circuits: one UEV, one UHV and one UV.',
], ['parts'])
q('emitter', 'UEV Emitter and Sensor', ['gtceu:uev_emitter', 'gtceu:uev_sensor'], [
    'A Gravi-Star and an Optical Computer each, on a neutronium frame.',
], ['parts'])
q('field_gen', '&eUEV Field Generator', ['gtceu:uev_field_generator'], [
    'A Quantum Star, a UEV emitter and an Optical Computer. Almost every controller from here on asks for one.',
], ['emitter'], kind='main')
q('energy_hatch', '&eUEV Energy Hatch', ['gtceu:uev_energy_input_hatch*2'], [
    'Two hatches of the highest tier let a multiblock run recipes one tier higher. That trick matters more than ever from here on.',
], ['parts'], kind='main', guide='gregtech/oc/oc-index.md')
q('assembler', 'UEV Assembler', ['gtceu:uev_assembler'], [
    'Two robot arms, two conveyors and two UEV circuits around a hull. Many UEV controllers are plain assembler recipes at this voltage.',
], ['robot_arm'], kind='opt')
q('lasers', 'UEV Laser Hatches', ['gtceu:uev_256a_laser_target_hatch', 'gtceu:uev_256a_laser_source_hatch'], [
    '256 amps of UEV through one laser pipe.',
], ['emitter'], kind='opt')

# ---- space
q('motor3', 'Space Elevator Motor MK-III', ['gtapexadditions:space_elevator_motor_mk3'], [
    'The motor tier limits which modules the Space Elevator can hold and how many. From MK-III on the structure can be extended for more modules.',
], ['parts'], kind='opt', guide='multiblocks/space_elevator.md')
q('pump2', 'Space Pumping Module MK-II', ['gtapexadditions:space_pumping_module_mk2'], [
    'UEV pumping recipes: argon, neon, krypton, xenon, radon, helium-3, tritium and &eoganesson&r, each picked with a programmed circuit.',
], ['parts', 'uhv/pumping_module'], kind='opt')
q('drone', 'UEV Mining Drone', ['gtapexadditions:mining_drone_uev'], [
    'Each drone is the one before it with this tier\'s motor, sensor and circuit.',
], ['parts'])
q('mining3', 'Space Mining Module MK-III', ['gtapexadditions:space_mining_module_mk3'], [
    'With a UEV drone it mines stellar cores: raw neutronium, raw naquadria, raw iridium and tritanium dust.',
    'Every asteroid needs its drone (not used up), a programmed circuit and a plasma: helium, iron, nickel, nitrogen or oxygen.',
], ['drone'], kind='opt')
q('swarm', 'Dyson Swarm Module', ['gtapexadditions:dyson_swarm_module*4'], [
    'Space Assembler: neutronium foil, tritanium plates, a UHV emitter and sensor, in grade 6 purified water. Stock up for the launch site at UIV.',
], ['field_gen'], kind='opt')

# ---- metals
q('celestial', '&eCelestial Tungsten', ['gtna:celestial_tungsten_ingot*8'], [
    'Engrave tungsten dust with the &eSpecial Laser Lens&r in a UEV laser engraver, then blast the dust.',
    'UIV casings and components are celestial tungsten.',
], ['start', 'uv/special_lens'], kind='main')
q('tengam', '&eAttuned Tengam', ['gtna:tengam_attuned_ingot*4'], [
    'Mix neodymium, samarium, europium and lanthanum dust into raw tengam. An electromagnetic separator pulls the purified dust out of it.',
    'Blast it, then polarize the ingot. Every motor from UIV up turns on an attuned tengam rod.',
], ['start'], kind='main')
q('churitsu', 'Quantum and Shijima', ['gtna:quantum_ingot*4', 'gtna:shijima_ingot*4'], [
    'Churitsu is trinium, naquadria, ruthenium and americium dust mixed in radon. A centrifuge splits it into Quantum and Shijima dust.',
], ['start'])
q('rhugnor', 'Rhugnor', ['gtna:rhugnor_ingot*2'], [
    'Fusion: molten infinity and molten quantum.',
], ['churitsu'])
q('dragon', 'Dragon Metal', ['gtna:dragon_metal_ingot*2'], [
    'Dragon\'s breath, naquadria dust and neutron dust mixed in molten blaze.',
], ['start'])
q('hypogen', '&eHypogen', ['gtna:hypogen_ingot*4'], [
    'Fusion: molten dragon metal and rhugnor.',
    'UIV emitters, sensors and field generators are assembled in molten hypogen.',
], ['rhugnor', 'dragon'], kind='main')
q('nuclear_star', 'Nuclear Star', ['gtapexadditions:nuclear_star'], [
    'A Gravi-Star grown in molten infinity in a UEV autoclave. The big UIV controllers are built around them.',
], ['start'])
q('star_matter', 'Raw Star Matter', ['gtna:raw_star_matter_bucket'], [
    'Star Fuel put through a UEV extractor.',
], ['start'], kind='opt')

# ---- fusion
q('fcoil5', 'Ultimate Fusion Coil', ['gtapexadditions:fusion_coil_mk5*4'], [
    'Space Assembler: an Advanced Fusion Coil, UHV superconductor, a UHV field generator and an Optical Computer, in metastable oganesson and grade 5 purified water.',
], ['start', 'uhv/oganesson'])
q('fusion5', 'Fusion Reactor MK V', ['gtapexadditions:uev_fusion_reactor'], [
    'Space Assembler: the MK IV reactor, an Ultimate Fusion Coil, a UEV field generator and dense neutronium plates.',
], ['fcoil5', 'field_gen'], kind='opt', guide='lines/fusion.md')
q('compact5', 'Compact Fusion Computer MK-V', ['gtapexadditions:compact_fusion_mk5'], [
    'The MK V reactor folded small, with parallels.',
], ['fusion5'], kind='opt', guide='lines/fusion.md')
q('celestial_plasma', 'Celestial Tungsten Plasma', ['gtna:celestial_tungsten_plasma_bucket'], [
    'Fusion: molten celestial tungsten and helium-3. The Plasma Forge and the Transcendent Plasma Mixer both ask for it later.',
], ['celestial'], kind='opt')
q('water8', 'Grade 8 Purified Water', ['gtapexadditions:purification_unit_baryonic_perfection'], [
    'The last purification unit: grade 7 water and molten neutronium with a &eQuark Creation Catalyst&r, which is not used up.',
], ['field_gen'], kind='opt', guide='multiblocks/purification_plant.md')

# ---- plasma forge
q('dtpf', '&e&lDTPF Prototype MK-II', ['gtapexadditions:plasma_forge_prototype_mk2'], [
    'The &eDimensionally Transcendent Plasma Forge&r prototype: 65x43x65 blocks, from a UEV assembler.',
    'It runs Plasma Forge recipes, up to 16 at once with parallel hatches. Every recipe is fed by a catalyst.',
    '> The Structure Upgrader builds it for you.',
], ['field_gen'], kind='hub', guide='multiblocks/plasma_forge_prototype_mk2.md')
q('dtcc', 'Crude Catalyst', ['gtna:excited_dtcc_bucket'], [
    'Mix helium, iron, calcium and niobium plasma into Dimensionally Transcendent Crude Catalyst, then excite it in a laser engraver.',
    'Calcium plasma is fused from magnesium and oxygen, niobium plasma from cobalt and silicon.',
], ['dtpf'])
q('residue', '&eDT Residue', ['gtna:dimensionally_transcendent_residue_bucket'], [
    'Plasma Forge: excited crude catalyst and molten neutronium.',
    'Residue goes into mellion, protomatter and creon plasma later. Most forge recipes give some back.',
], ['dtcc'], kind='main')
q('dtpc', 'Prosaic Catalyst', ['gtna:excited_dtpc_bucket'], [
    'Crude catalyst plus radon, nickel, boron and sulfur plasma.',
], ['dtcc'])
q('dtrc', 'Resplendent Catalyst', ['gtna:excited_dtrc_bucket'], [
    'Prosaic catalyst plus nitrogen, zinc, silver and titanium plasma.',
    'With it the forge turns a Gravi-Star and molten neutronium into molten infinity.',
], ['dtpc'])
q('qft2', 'Cosmic Fabric Manipulator', ['gtapexadditions:qft_manipulator_t2'], [
    'The second tier of the Quantum Force Transformer\'s manipulators.',
], ['field_gen'], kind='opt', guide='multiblocks/quantum_force_transformer.md')
q('fuel2', 'Naquadah Fuel MkII', ['gtapexadditions:naquadah_based_liquid_fuel_mkii_bucket'], [
    'Fuel Refining Complex at UEV: light and heavy naquadah fuel, atomic separation catalyst, naquadah gas and americium plasma around a Gravi-Star.',
], ['start'], kind='opt', guide='lines/naquadah.md')
q('optical_mainframe', '&eOptical Mainframe', ['gtna:optical_mainframe*2'], [
    'The UIV circuit: an Optical Computer on a neutronium frame, on the Assembly Line.',
], ['parts'], kind='main', guide='gregtech/circuits.md')

q('uiv', '&2&lOn to UIV', ['gtceu:uiv_machine_hull'], [
    'A celestial tungsten casing: the &2UIV Machine Hull&r. Open &2Tier 11 - UIV&r.',
], ['celestial', 'tengam', 'hypogen', 'field_gen', 'energy_hatch', 'optical_mainframe', 'residue'], kind='hub', bag='end')
