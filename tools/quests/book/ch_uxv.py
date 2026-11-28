"""Tier 12 - UXV: six-phased copper, the Transcendent Plasma Mixer, creon, the phonon line, shirabon."""
from fw import Chapter

C = Chapter('uxv', '&eTier 12 - UXV', 'gtceu:uxv_machine_hull', '', 13, '134,217,728 EU/t', 'end', 'pick_end', xp_base=170)
C.derive = False
q = C.q

q('start', '&e&lTier 12: UXV', ['gtceu:uxv_machine_hull'], [
    'Welcome to &eUXV&r: 134,217,728 EU/t per amp.',
    'The hull is the easy part. UXV components are assembled in molten &ecreon&r, and creon only comes out of the &eTranscendent Plasma Mixer&r.',
], ['uiv/uxv'], kind='hub', guide='progression/10-uev-plus.md')
q('six', '&eSix-Phased Copper', ['gtna:six_phased_copper_ingot*8'], [
    'Plasma Forge at UXV voltage: molten celestial tungsten, astral titanium, hypogen, chronomatic glass, rhugnor and mellion around a Gravi-Star.',
], ['start'], kind='main')
q('nexus', 'Power without Wires', ['gtna:nexus_flux_matrix', 'gtna:wireless_dynamo_hatch_1a_uxv'], [
    'The Transcendent Plasma Mixer, the Eye of Harmony and the Forge of the Gods take power only from GTNA\'s wireless &eNexus network&r.',
    'A Nexus Flux Matrix stores it; wireless dynamo hatches on your generators push energy in.',
], ['start'])
q('tpm', '&e&lTranscendent Plasma Mixer', ['gtna:transcendent_plasma_mixer'], [
    'Space Assembler: a plasma forge bridge casing, a UIV energy hatch, dense six-phased copper plates and UIV superconductor, in excited exotic catalyst.',
    'A Parallel Hatch sets the parallels. Each one costs 10 x EU/t x duration, paid when the recipe starts. Everything scales except time.',
], ['six', 'nexus', 'uiv/dtec'], kind='hub')
q('creon_plasma', 'Creon Plasma', ['gtna:creon_plasma_bucket'], [
    'In the mixer: americium, thorium, celestial tungsten and calcium plasma with DT residue.',
], ['tpm'])
q('creon', '&eCreon', ['gtna:creon_ingot*4'], [
    'Blast mellion dust in creon plasma into &eHarmonic Compound&r. Freeze that with atomic separation catalyst: a mellion ingot and molten creon.',
], ['creon_plasma'], kind='main')
q('parts', '&eUXV Components', ['gtceu:uxv_electric_motor', 'gtceu:uxv_electric_pump', 'gtceu:uxv_electric_piston', 'gtceu:uxv_conveyor_module'], [
    'Assembly Line at UIV voltage: transcendent metal and the UIV superconductor, in mutated living solder and molten creon.',
], ['creon'], kind='main')
q('robot_arm', 'UXV Robot Arm', ['gtceu:uxv_robot_arm'], [
    'An Exotic Mainframe, an Optical Mainframe and an Optical Computer.',
], ['parts'])
q('space_parts', '&eUXV Emitter and Sensor', ['gtceu:uxv_emitter', 'gtceu:uxv_sensor'], [
    'Space Assembler, on transcendent metal frames, in living solder and creon.',
], ['parts'], kind='main')
q('field_gen', '&eUXV Field Generator', ['gtceu:uxv_field_generator'], [
    'Space Assembler: a Quantum Star, a UXV emitter and an Exotic Mainframe.',
], ['space_parts'], kind='main')
q('energy_hatch', '&eUXV Energy Hatch', ['gtceu:uxv_energy_input_hatch*2'], [
    'UIV superconductor and a transcendent metal spring on a UXV hull.',
], ['start'], kind='main')

# ---- phonons and shirabon
q('dilithium', 'Dilithium', ['gtna:dilithium_gem*4'], [
    'Lithium dust and a diamond in an autoclave with helium. A lathe turns exquisite dilithium into a lens and dust.',
], ['start'])
q('seed_crystal', 'Phononic Seed Crystal', ['gtna:phononic_seed_crystal'], [
    'Autoclave at UXV: mellion dust and transcendent metal dust in DT residue.',
], ['start'])
q('phonon', '&ePhonon Medium', ['gtna:phonon_medium_bucket'], [
    'Plasma Forge: a seed crystal and dilithium dust in molten mellion and six-phased copper give phonon crystal solution.',
    'Alloy Blast Smelter: the solution, metastable oganesson, a magneto resonatic gem, praseodymium dust and UIV superconductor base dust.',
], ['seed_crystal', 'dilithium', 'six'], kind='main')
q('shirabon', '&eShirabon', ['gtna:shirabon_ingot*8'], [
    'Plasma Forge: molten creon and mellion, phonon medium and excited exotic catalyst.',
    'OpV components are assembled in molten shirabon, and a MAX casing is shirabon plates.',
], ['phonon', 'creon'], kind='main')
q('sc_umv', '&eUMV Superconductor', ['gtna:superconductor_umv_single_wire*8'], [
    'Base dust: UIV superconductor base, creon, mellion and six-phased copper dust mixed in phonon medium. Blast it, then freeze it in liquid helium.',
], ['phonon', 'creon'], kind='main')
q('opv_circuit', '&eOpV Universal Circuit', ['extendedfeatures:opv_universal_circuit'], [
    'The only OpV circuit. Space Assembler: an Exotic Mainframe, a UXV emitter and sensor and neutronium foil.',
], ['space_parts'], kind='main', guide='gregtech/circuits.md')

# ---- side projects
q('assembler3', 'Space Assembler Module Mk-III', ['gtapexadditions:space_assembler_module_mk3'], [
    'The Mk-II module with UXV robot arms and conveyors and an Exotic Mainframe.',
], ['robot_arm'], kind='opt')
q('pump3', 'Space Pumping Module MK-III', ['gtapexadditions:space_pumping_module_mk3'], [
    'UXV pumping recipes: fluorine, oxygen, liquid helium and &espatial fluid&r.',
], ['parts'], kind='opt')
q('motor5', 'Space Elevator Motor MK-V', ['gtapexadditions:space_elevator_motor_mk5'], [
    'The last motor: every module tier, and the most modules.',
], ['parts'], kind='opt', guide='multiblocks/space_elevator.md')
q('drill', 'Space Drill MK-123', ['gtapexadditions:space_drill_mk123'], [
    'Space Assembler: a Mining Module MK-III, a UXV drone, motor and field generator and an Exotic Mainframe.',
], ['field_gen'], kind='opt')
q('isa', 'Divine Isa Forge', ['gtapexadditions:divine_isa_forge'], [
    'Two IsaMills and a Space Assembler in one multiblock. The controller takes the top tier of all three Eye of Harmony field generators.',
], ['field_gen', 'opv_circuit'], kind='opt', guide='multiblocks/divine_isa_forge.md')
q('rag', 'Real Annihilation Generator', ['gtapexadditions:real_annihilation_generator'], [
    'Antimatter straight into wireless EU.',
], ['field_gen', 'opv_circuit', 'uiv/slam'], kind='opt', guide='multiblocks/real_annihilation_generator.md')
q('qft4', 'SpaceTime Continuum Ripper', ['gtapexadditions:qft_manipulator_t4'], [
    'The top manipulator for the Quantum Force Transformer.',
], ['field_gen'], kind='opt', guide='multiblocks/quantum_force_transformer.md')
q('mega_battery', 'Mega Ultimate Battery', ['gtna:mega_ultimate_battery'], [
    'An Insanely Ultimate Battery, dense dragon metal plates, an OpV circuit and a UXV field generator.',
], ['field_gen', 'opv_circuit'], kind='opt')
q('cosmic_computer', 'Cosmic Supercomputer', ['gtapexadditions:cosmic_computer'], [
    'A UXV circuit assembler recipe.',
], ['start', 'uiv/cosmic_assembly'], kind='opt')
q('nano2', 'Neutronium Nanites', ['gtna:neutronium_nanites'], [
    'Nano Forge at UXV: a neutronium block under an orundum lens, in UU-matter. A nanite in the input bus sets the forge\'s tier and is never used up: neutronium is tier 2.',
], ['start'], kind='opt')
q('fuel4', 'Naquadah Fuel MkIV', ['gtapexadditions:naquadah_based_liquid_fuel_mkiv_bucket'], [
    'From MkIII in the Naquadah Fuel Refinery or the Fuel Refining Complex, at UXV.',
], ['start'], kind='opt', guide='lines/naquadah.md')

q('opv', '&9&lOn to OpV', ['gtceu:opv_machine_hull'], [
    'A six-phased copper casing: the &9OpV Machine Hull&r. Open &9Tier 13 - OpV&r.',
], ['shirabon', 'sc_umv', 'opv_circuit', 'field_gen', 'energy_hatch'], kind='hub', bag='end')
