"""Tier 13 - OpV: the Eye of Harmony, SpaceTime and what it unlocks, the Forge of the Gods."""
from fw import Chapter

C = Chapter('opv', '&9Tier 13 - OpV', 'gtceu:opv_machine_hull', '', 14, '536,870,912 EU/t', 'end', 'pick_end', xp_base=180)
C.derive = False
q = C.q

q('start', '&9&lTier 13: OpV', ['gtceu:opv_machine_hull'], [
    'Welcome to &9OpV&r: 536,870,912 EU/t per amp.',
    'Two machines define this tier: the &dEye of Harmony&r, which makes SpaceTime, and the &6Forge of the Gods&r.',
], ['uxv/opv'], kind='hub', guide='progression/10-uev-plus.md')
q('parts', '&eOpV Components', ['gtceu:opv_electric_motor', 'gtceu:opv_electric_pump', 'gtceu:opv_electric_piston', 'gtceu:opv_conveyor_module'], [
    'Assembly Line at UXV voltage: six-phased copper and the UMV superconductor, in mutated living solder and molten shirabon.',
], ['start'], kind='main')
q('robot_arm', 'OpV Robot Arm', ['gtceu:opv_robot_arm'], [
    'An OpV Universal Circuit, an Exotic Mainframe and an Optical Mainframe.',
], ['parts'])
q('space_parts', '&eOpV Emitter and Sensor', ['gtceu:opv_emitter', 'gtceu:opv_sensor'], [
    'Space Assembler, on six-phased copper frames.',
], ['parts'], kind='main')
q('field_gen', '&eOpV Field Generator', ['gtceu:opv_field_generator'], [
    'The last field generator.',
], ['space_parts'], kind='main')
q('energy_hatch', '&eOpV Energy Hatch', ['gtceu:opv_energy_input_hatch*2'], [
    'UMV superconductor and a six-phased copper spring.',
], ['start'], kind='main')
q('drone', 'OpV Mining Drone', ['gtapexadditions:mining_drone_opv'], [
    'The last drone.',
], ['parts'], kind='opt')

# ---- Eye of Harmony
q('eoh_casings', 'Spacetime Casings', ['gtna:infinite_spacetime_energy_boundary_casing', 'gtna:reinforced_spatial_structure_casing', 'gtna:reinforced_temporal_structure_casing'], [
    'The three casings of the Eye. The controller takes one of each.',
], ['start'])
q('eoh_fields', 'Field Generators', ['gtna:spacetime_compression_field_generator_tier_0', 'gtna:time_acceleration_field_generator_tier_0', 'gtna:stabilisation_field_generator_tier_0'], [
    'The structure holds 138 compression, 168 time acceleration and 48 stabilisation field generators. Each family comes in 9 tiers and must be one tier throughout.',
    '&eCompression&r unlocks planets and shortens runs. &eAcceleration&r halves the time per tier and costs success chance. &eStabilisation&r adds chance and costs yield.',
], ['start'])
q('planet', 'Planet Blocks', ['gtna:eye_of_harmony_planet_overworld'], [
    'A Planet Block in the controller chooses what the Eye extracts: Overworld, Nether, End, Moon, Mars, Venus, Mercury or Glacio.',
    'Higher planets need more gas, more time and better compression fields.',
], ['start'])
q('eoh', '&d&lEye of Harmony', ['gtna:eye_of_harmony'], [
    'It creates a miniature universe and extracts its resources. Space Assembler, in molten hypogen, celestial tungsten, metastable oganesson and shirabon.',
    'Startup power comes from the Nexus network. The Overworld needs 1,000,000 buckets each of hydrogen and helium; all stored gas is used at startup, and too much lowers chance and yield.',
    'Ports are ME only: one item input bus, two fluid input hatches, one item and one fluid output.',
], ['eoh_casings', 'eoh_fields', 'planet', 'field_gen'], kind='hub')
q('spacetime', '&eSpaceTime', ['gtna:space_time_ingot'], [
    'A &cfailed&r cycle gives molten SpaceTime instead of products. That is the source of it.',
], ['eoh'], kind='main')
q('eoh_fluids', 'Stellar Matter', ['gtna:universium_ingot', 'gtna:white_dwarf_matter_ingot', 'gtna:black_dwarf_matter_ingot'], [
    'Universium, White Dwarf Matter and Black Dwarf Matter are the Eye\'s special fluids. Solidify them like any metal.',
], ['eoh'])
q('spatial', 'Spatial and Temporal Fluid', ['gtna:spatial_fluid_bucket', 'gtna:temporal_fluid_bucket'], [
    'Centrifuge a Tesseract and a Special Laser Lens in molten SpaceTime.',
], ['spacetime'])
q('primordial', 'Primordial Matter', ['gtna:primordial_matter_bucket'], [
    'Transcendent Plasma Mixer: raw star matter, SpaceTime, spatial fluid and DT residue.',
], ['spatial'])
q('eternity', '&eEternity', ['gtna:eternity_ingot'], [
    'Plasma Forge: molten infinity, shirabon and universium with primordial matter.',
], ['primordial', 'eoh_fluids'], kind='main')
q('glass', 'Hexanite and Universium Glass', ['gtapexadditions:hexanite_reinforced_borosilicate_glass*8', 'gtapexadditions:universium_reinforced_borosilicate_glass*8'], [
    'Borosilicate glass solidified in molten SpaceTime, and in universium: the top tiers of multiblock glass.',
], ['spacetime', 'eoh_fluids'], kind='opt', guide='gregtech/glass-tiers.md')
q('hyper2', 'Advanced Hyper Naquadah Reactor', ['gtapexadditions:advanced_hyper_naquadah_reactor'], [
    'A Hyper Naquadah Reactor, an Ultimate Fusion Coil and a UIV field generator, assembled in molten SpaceTime.',
], ['spacetime'], kind='opt', guide='lines/naquadah.md')
q('nanites', 'Stellar Nanites', ['gtna:universium_nanites', 'gtna:white_dwarf_matter_nanites'], [
    'Nano Forge: a block of the material under a magneto resonatic lens and a special laser lens.',
], ['eoh_fluids'], kind='opt')
q('dtsc', 'Stellar Catalyst', ['gtna:excited_dtsc_bucket'], [
    'Exotic catalyst plus lead, thorium and naquadria plasma and raw star matter. The last of the five.',
], ['start', 'uiv/dtec'])

# ---- Forge of the Gods
q('fog_casings', 'Godforge Casings', ['gtna:plasma_forge_bridge_casing', 'gtna:stellar_energy_siphon_casing', 'gtna:boundless_gravitationally_severed_structure_casing', 'gtna:celestial_matter_guidance_casing'], [
    'Assembly Line recipes in mellion, creon, hypogen and six-phased copper. Look each one up: none is small.',
], ['start'])
q('fog', '&6&lForge of the Gods', ['gtna:forge_of_gods'], [
    'A stabilized neutron star for material processing. It holds up to 16 modules, all powered through the Nexus network.',
    'It has an upgrade tree: milestones and materials unlock module types, heat levels and speed. Click the logo in its GUI for the full manual.',
], ['fog_casings', 'field_gen'], kind='hub')
q('fog_smelt', 'Smelting Module', ['gtna:godforge_smelting_module'], [
    'The first module: furnace and blast furnace recipes, built for speed and heat.',
], ['fog'])
q('fog_molten', 'Molten Module', ['gtna:godforge_molten_module'], [
    'Melts materials straight into their liquid form, built for parallels.',
], ['fog_smelt'])
q('fog_plasma', 'Plasma Module', ['gtna:godforge_plasma_module'], [
    'Turns materials straight into plasma. Some plasmas need upgrades first.',
], ['fog_molten'])
q('fog_exotic', '&eExotic Module', ['gtna:godforge_exotic_module'], [
    'Breaks matter apart. Quark-gluon plasma comes right away; &eMagmatter&r needs a fully upgraded Godforge.',
], ['fog_plasma'], kind='main')
q('modulators', 'Graviton Flow Modulators', ['gtna:remote_graviton_flow_modulator', 'gtna:medial_graviton_flow_modulator', 'gtna:central_graviton_flow_modulator'], [
    'Remote at UIV, medial at UXV, central at OpV with a MAX circuit.',
], ['fog'], kind='opt')
q('magmatter', '&eMagmatter', ['gtna:magmatter_ingot'], [
    'Magnetic monopole matter, from the Exotic Module of a fully upgraded Godforge. Solidify it at UXV.',
], ['fog_exotic'], kind='main')
q('star', 'Artificial Star', ['gtna:annihilate_generator', 'gtna:neutronium_antimatter_fuel_rod'], [
    'A generator that burns antimatter fuel rods: neutronium, draconium, cosmic neutronium and infinity.',
], ['field_gen'], kind='opt')
q('mainframes', 'Cosmic and Transcendent Mainframes', ['gtapexadditions:cosmic_mainframe', 'gtapexadditions:transcendent_mainframe'], [
    'OpV circuit assembler recipes, each around a Nuclear Star.',
], ['start', 'uxv/cosmic_computer'], kind='opt')
q('max_circuit', '&cMAX Universal Circuit', ['gtapexadditions:max_universal_circuit'], [
    'Space Assembler at OpV: an OpV circuit, an OpV emitter and sensor and neutronium foil.',
], ['space_parts'], kind='main', guide='gregtech/circuits.md')

q('max', '&c&lOn to MAX', ['gtceu:max_machine_hull'], [
    'A shirabon casing: the &cMAX Machine Hull&r. Open &cTier 14 - MAX&r.',
], ['spacetime', 'eternity', 'magmatter', 'max_circuit', 'energy_hatch'], kind='hub', bag='end')
