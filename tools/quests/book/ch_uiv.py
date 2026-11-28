"""Tier 11 - UIV: celestial tungsten machines, the tesseract and transcendent metal, antimatter, spacetime circuits."""
from fw import Chapter

C = Chapter('uiv', '&2Tier 11 - UIV', 'gtceu:uiv_machine_hull', '', 12, '33,554,432 EU/t', 'end', 'pick_end', xp_base=160)
C.derive = False
q = C.q

q('start', '&2&lTier 11: UIV', ['gtceu:uiv_machine_hull'], [
    'Welcome to &2UIV&r: 33,554,432 EU/t per amp.',
    'The road to UXV is one chain: &emellion&r, the &eTesseract&r, &etranscendent metal&r, the &eUIV superconductor&r. Antimatter starts here too.',
], ['uev/uiv'], kind='hub', guide='progression/10-uev-plus.md')
q('parts', '&eUIV Components', ['gtceu:uiv_electric_motor', 'gtceu:uiv_electric_pump', 'gtceu:uiv_electric_piston', 'gtceu:uiv_conveyor_module'], [
    'Assembly Line at UEV voltage: celestial tungsten parts around an attuned tengam rod, still in Indalloy 140.',
], ['start'], kind='main')
q('robot_arm', 'UIV Robot Arm', ['gtceu:uiv_robot_arm'], [
    'An Optical Mainframe, an Optical Computer and a Wetware Mainframe.',
], ['parts'])
q('space_parts', '&eUIV Emitter and Sensor', ['gtceu:uiv_emitter', 'gtceu:uiv_sensor'], [
    'From here the emitter, sensor and field generator are &eSpace Assembler&r recipes only, in molten hypogen.',
], ['parts'], kind='main', guide='multiblocks/space_elevator.md')
q('field_gen', '&eUIV Field Generator', ['gtceu:uiv_field_generator'], [
    'Space Assembler: a Quantum Star, a UIV emitter and an Optical Mainframe on a celestial tungsten frame.',
], ['space_parts'], kind='main')
q('energy_hatch', '&eUIV Energy Hatch', ['gtceu:uiv_energy_input_hatch*2'], [
    'Two of them on a multiblock mixer run the UXV-voltage recipe for mellion.',
], ['parts'], kind='main', guide='gregtech/oc/oc-index.md')
q('assembler2', 'Space Assembler Module Mk-II', ['gtapexadditions:space_assembler_module_mk2'], [
    'The Mk-I module with UIV robot arms and conveyors and an Optical Mainframe.',
], ['robot_arm'], kind='opt')
q('motor4', 'Space Elevator Motor MK-IV', ['gtapexadditions:space_elevator_motor_mk4'], [
    'More modules, and higher ones.',
], ['parts'], kind='opt', guide='multiblocks/space_elevator.md')

# ---- the chain to UXV
q('solder', '&eMutated Living Solder', ['gtna:mutated_living_solder_bucket'], [
    'Mixer at UIV: bismuth, tin and infinity dust and a Gravi-Star in molten Indalloy 140.',
    'The solder of UXV and OpV.',
], ['start'], kind='main')
q('mellion', '&eMellion', ['gtna:mellion_dust*4'], [
    'Mixer: tritanium, orundum and rubidium dust, blaze powder, redstone and atomic separation catalyst dust in DT residue.',
    '&cThe recipe is UXV voltage.&r Run it in a multiblock mixer with two UIV energy hatches.',
], ['energy_hatch', 'uev/residue'], kind='main')
q('tesseract', '&eTesseract', ['gtna:tesseract'], [
    'Assembly Line: an infinity frame, a Quantum Star, a UEV field generator and dense neutronium plates, in molten celestial tungsten and mellion.',
], ['mellion'], kind='main')
q('transcendent', '&eTranscendent Metal', ['gtna:transcendent_metal_ingot*8'], [
    'Macerate a Tesseract: that is the only source of transcendent metal dust.',
    'Blast the dust with molten tungsten. You get celestial tungsten back with the ingot.',
], ['tesseract'], kind='main')
q('sc_uiv', '&eUIV Superconductor', ['gtna:superconductor_uiv_single_wire*8'], [
    'Base dust: carbon, osmium, silver and transcendent metal dust mixed in oxygen and molten infinity.',
    'Blast it, then freeze the ingot in liquid helium.',
], ['transcendent'], kind='main')
q('glass', 'Transcendent Glass', ['gtapexadditions:transcendentally_reinforced_borosilicate_glass*8'], [
    'Borosilicate glass solidified in molten transcendent metal.',
], ['transcendent'], kind='opt', guide='gregtech/glass-tiers.md')
q('dtec', 'Exotic Catalyst', ['gtna:excited_dtec_bucket'], [
    'Resplendent catalyst plus americium, bismuth, oxygen and tin plasma. The Transcendent Plasma Mixer and shirabon need it.',
], ['uev/dtrc'])
q('forge_infinity', 'Infinity from the Forge', ['gtna:infinity_ingot*8'], [
    'Plasma Forge: a Gravi-Star and molten neutronium with excited resplendent catalyst. No more extreme crafting for infinity.',
], ['uev/dtrc'], kind='opt')
q('batteries', 'Ultimate Batteries', ['gtna:really_ultimate_battery', 'gtna:extremely_ultimate_battery', 'gtna:insanely_ultimate_battery'], [
    'Each is the battery before it with a better plate, circuit and field generator, in mutated living solder. The Forge of the Gods wants them later.',
], ['solder', 'field_gen'], kind='opt')

# ---- spacetime and circuits
q('msf', 'Micro SpaceTime Fabricatorio', ['gtapexadditions:micro_spacetime_fabricatorio'], [
    'Makes &eQuantum Anomalies&r from a Stable Boson Containment Unit and hydrogen plasma, and Nuclear Stars from a Gravi-Star and molten infinity.',
], ['field_gen', 'uev/nuclear_star'], kind='opt', guide='multiblocks/micro_spacetime_fabricatorio.md')
q('anomaly', 'Quantum Anomaly', ['gtca:quantum_anomaly'], [
    'Only the Micro SpaceTime Fabricatorio makes them.',
], ['msf'])
q('cosmic', 'Cosmic Circuits', ['gtapexadditions:cosmic_circuit_board', 'gtapexadditions:cosmic_processor'], [
    'The board is an optical circuit board, a Quantum Anomaly and neutronium foil in molten infinity.',
    'Processor, assembly, supercomputer and mainframe follow, each one voltage tier higher than the last.',
], ['anomaly'], kind='opt', guide='gregtech/circuits.md')
q('cosmic_assembly', 'Cosmic Assembly', ['gtapexadditions:cosmic_assembly'], [
    'A UIV circuit assembler recipe.',
], ['cosmic'], kind='opt')
q('trans_circuits', 'Transcendent Circuits', ['gtapexadditions:transcendent_circuit_board', 'gtapexadditions:transcendent_processor'], [
    'A cosmic board, a Nuclear Star and neutronium foil in molten transcendent metal. The line above cosmic.',
], ['cosmic', 'transcendent'], kind='opt', guide='gregtech/circuits.md')

# ---- antimatter
q('protomatter', 'Protomatter', ['gtapexadditions:protomatter_bucket'], [
    'Laser-engrave DT residue at UIV. SpaceTime and primordial matter give far more per bucket later.',
], ['uev/residue'])
q('first_antimatter', 'The First Antimatter', ['gtapexadditions:antimatter_bucket'], [
    'A dense transcendent metal plate and protomatter in a laser engraver. Slow, and only needed once: the SASS takes over.',
], ['protomatter', 'transcendent'])
q('sass_parts', 'Antimatter Casings', ['gtapexadditions:antimatter_containment_casing*8', 'gtapexadditions:protomatter_activation_coil*4', 'gtapexadditions:antimatter_hatch'], [
    'Containment casings from an assembler, activation coils and the Antimatter Hatch from the Space Assembler.',
], ['field_gen'])
q('sass', '&e&lAntimatter', ['gtapexadditions:antimatter_forge'], [
    'The &eSemi-Stable Antimatter Stabilization Sequencer&r turns protomatter into antimatter, held in an Antimatter Hatch.',
    'The controller is a UEV Space Assembler recipe, but it has nothing to work on before protomatter.',
], ['sass_parts', 'first_antimatter'], kind='hub', guide='lines/antimatter.md')
q('slam', '&eAntimatter Power', ['gtapexadditions:antimatter_generator', 'gtapexadditions:antimatter_annihilation_matrix*4'], [
    'The &eShielded Lagrangian Annihilation Matrix&r reacts antimatter with matter. Nothing else in the pack makes this much power.',
], ['sass'], kind='main', guide='multiblocks/antimatter_generator.md')
q('sma', 'Strange Matter Aggregator', ['gtapexadditions:strange_matter_aggregator'], [
    'Breeds antimatter. Rings of field generators set its tier.',
], ['sass', 'uev/nuclear_star'], kind='opt', guide='multiblocks/strange_matter_aggregator.md')
q('mfg', 'Mass Fabricator Genesis', ['gtapexadditions:mass_fabricator_genesis'], [
    'UU-matter from a programmed circuit and power, nothing else.',
], ['field_gen', 'uev/nuclear_star'], kind='opt', guide='multiblocks/mass_fabricator_genesis.md')
q('dyson', 'Dyson Sphere Launch Site', ['gtapexadditions:dyson_sphere_launch_site'], [
    'Space Assembler, in grade 8 purified water. It launches Dyson Swarm Modules into orbit around the sun.',
], ['field_gen', 'uev/swarm'], kind='opt')
q('rcm', 'Resource Collection Module', ['gtapexadditions:resource_collection_module'], [
    'A Mining Module MK-III and a Pumping Module MK-III in one: it runs both kinds of recipe.',
], ['field_gen'], kind='opt')
q('qft3', 'Infinity Infused Manipulator', ['gtapexadditions:qft_manipulator_t3'], [
    'Tier three for the Quantum Force Transformer.',
], ['field_gen'], kind='opt', guide='multiblocks/quantum_force_transformer.md')
q('exotic_mainframe', '&eExotic Mainframe', ['gtna:exotic_mainframe*2'], [
    'The UXV circuit: an Exotic Computer on a celestial tungsten frame, on the Assembly Line.',
], ['parts'], kind='main', guide='gregtech/circuits.md')

q('uxv', '&e&lOn to UXV', ['gtceu:uxv_machine_hull'], [
    'A transcendent metal casing: the &eUXV Machine Hull&r. Open &eTier 12 - UXV&r.',
], ['transcendent', 'sc_uiv', 'solder', 'field_gen', 'exotic_mainframe'], kind='hub', bag='end')
