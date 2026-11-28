"""Tier 3 - HV: stainless steel, the Cleanroom and real processors, kanthal and the Vacuum Freezer, oil, PTFE, the
Twilight Forest and the Moon, and titanium as the door to EV. Gates follow the pack's recipes."""
from fw import Chapter

C = Chapter('hv', '&6Tier 3 - HV', 'gtceu:hv_machine_hull', '', 4, '512 EU/t: high voltage', 'hv', 'pick_hv', xp_base=30)
C.derive = False   # every link below is written by hand from the recipes
q = C.q

# ---------------------------------------------------------------- the start
q('start', '&6&lTier 3: HV', ['gtceu:hv_machine_hull'], [
    'Welcome to &6HV&r: 512 EU/t per amp.',
    'HV is the tier of big multiblocks. Four goals run through this chapter:',
    '&e1.&r The &eCleanroom&r, and with it the processor line of circuits.',
    '&e2.&r The &eVacuum Freezer&r and &eKanthal coils&r: 2,700 K in the blast furnace.',
    '&e3.&r Oil: the &eDistillation Tower&r, real fuels and the plastics that come out of them.',
    '&e4.&r &eTitanium&r, the EV metal.',
    '> New worlds open here too: the Twilight Forest and the Moon.',
], ['mv/hv'], kind='hub', guide='progression/03-hv.md')
q('motor', '&eHV Motor', ['gtceu:hv_electric_motor'], [
    'Electrum wire, stainless steel rods and a magnetic steel rod, with silver cable.',
    '> Batch them. Pumps, conveyors, pistons and robot arms all start from motors.',
], ['start'], kind='main')
q('piston', 'HV Piston', ['gtceu:hv_electric_piston'], [
    'Stainless steel plates and rods, a small gear and a motor.',
], ['motor'])
q('pump', 'HV Pump', ['gtceu:hv_electric_pump'], [
    'A stainless steel fluid pipe, a steel rotor and rubber rings. Three of them go into the Vacuum Freezer, two into the Distillation Tower.',
], ['motor'])
q('conveyor', 'HV Conveyor', ['gtceu:hv_conveyor_module'], [
    'Two motors in rubber sheets. Plain rubber works again at HV.',
], ['motor'])
q('ic', '&eAdvanced Integrated Circuit', ['gtceu:advanced_integrated_circuit*4'], [
    'Your first &6HV circuit&r, and it needs no Cleanroom: a good integrated circuit, IC chips, &eRAM chips&r, transistors and fine electrum wire in the circuit assembler.',
    'RAM chips: an &eEmerald Lens&r in the Laser Engraver, then the cutter.',
    'Slow and costly, but it builds the first HV machines and the Cleanroom itself.',
], ['start'], kind='main', guide='gregtech/circuits.md')
q('robot_arm', 'HV Robot Arm', ['gtceu:hv_robot_arm'], [
    'Two motors, a piston, an advanced integrated circuit and stainless steel rods.',
], ['piston', 'ic'])
q('emitter', 'Emitters and Sensors', ['gtceu:hv_emitter', 'gtceu:hv_sensor'], [
    'Chromium rods around an &eEye of Ender&r. Hunt endermen, or farm them later in the Extreme Entity Crusher.',
], ['ic'])
q('hv_assembler', 'HV Assembler', ['gtceu:hv_assembler'], [
    'Many HV parts (SMD components, HV coils, the energy hatch) are 512 EU/t recipes. Two robot arms, two conveyors and circuits.',
], ['robot_arm', 'conveyor'])

# ---------------------------------------------------------------- power
q('power', 'HV Power', ['gtceu:hv_gas_turbine'], [
    'One HV machine at full load draws 512 EU/t. Steam turbines scale badly from here; burn &egases and fuels&r instead.',
    'Refinery gas, methane and LPG run gas turbines. Diesel and biodiesel run combustion generators.',
    '> Transformers still work: 4 MV amps in, 1 HV amp out.',
], ['start'], guide='gregtech/power/generation-and-consumption.md')
q('combustion', 'HV Combustion Generator', ['gtceu:hv_combustion'], [
    'Burns diesel, biodiesel and gasoline. Biodiesel (seed or fish oil with ethanol) keeps one running before you have oil.',
], ['power'], kind='opt')
q('steam_turbine', 'Large Steam Turbine', ['gtceu:steam_large_turbine'], [
    'A multiblock turbine that takes a &erotor&r in its rotor holder. The rotor material sets its power and efficiency. Feed it from Large Boilers.',
], ['power'], kind='opt', guide='gtmultis/large_turbines.md')
q('battery', 'HV Batteries', ['gtceu:hv_battery_buffer_4x', 'gtceu:hv_lithium_battery'], [
    'A buffer of large batteries keeps the grid steady when a multiblock starts. Each battery adds 1 A out.',
], ['power'], kind='opt', guide='gregtech/power/batteries.md')
q('energy_crystal', 'Energium Crystal', ['gtceu:energy_crystal'], [
    'Energium dust grown in an HV Autoclave: 6.4M EU, more than three large lithium batteries.',
], ['battery'], kind='opt')
q('transformer', 'HV Transformer', ['gtceu:hv_transformer_1a'], [
    'Needs &eLPIC chips&r, like everything in the HV power line.',
], ['lpic'], kind='opt', guide='gregtech/power/distribution.md')

# ---------------------------------------------------------------- cleanroom and processors
q('plascrete', 'Plascrete', ['gtceu:plascrete*64'], [
    'Steel frames, polyethylene sheets and concrete in the assembler. A 5x5x5 Cleanroom takes about 80 blocks; windows can be Cleanroom Glass.',
], ['start'])
q('filter', 'Filter Casings', ['gtceu:filter_casing*9'], [
    'The ceiling of the Cleanroom, except its edge. Item filters, iron bars, a steel frame, a rotor and an MV motor each.',
], ['plascrete'])
q('cleanroom', '&e&lCleanroom', ['gtceu:cleanroom'], [
    'A hollow box from 5x5x5 up to 15x15x15: plascrete walls and floor, filter casings in the ceiling with the controller in the middle, and a door.',
    'It takes a while to get clean, and it needs power the whole time. Machines &oinside&r run cleanroom recipes.',
    'Power and items go through the walls with &ehulls&r (power), &ediodes&r and &epassthrough hatches&r.',
    '> In this pack, multiblocks need a cleaning maintenance hatch instead: a controller inside the room is not enough.',
], ['filter', 'ic', 'motor'], kind='hub', guide='gregtech/cleanroom.md')
q('cpu', 'CPU Chips', ['gtceu:cpu_chip*8'], [
    'A &eDiamond Lens&r engraves a wafer into a CPU wafer. Cutting it into chips is a &bCleanroom&r recipe.',
], ['cleanroom'])
q('plastic_board', 'Plastic Circuit Boards', ['gtceu:plastic_printed_circuit_board*8'], [
    'Polyethylene sheet, copper foil and sulfuric acid make the board; more copper foil and sodium persulfate or iron chloride print it.',
], ['start'])
q('microprocessor', '&eMicroprocessor', ['gtceu:micro_processor*8'], [
    'A plastic printed board, a CPU chip, a resistor, a capacitor and a transistor. An &bMV&r circuit, made in the Cleanroom, two at a time.',
    'From here on, circuits come in lines: processor, assembly, supercomputer, mainframe. Each step is one tier higher.',
], ['cpu', 'plastic_board'], kind='main', guide='gregtech/circuits.md')
q('assembly', 'Microprocessor Assembly', ['gtceu:micro_processor_assembly*4'], [
    'Microprocessors, inductors, capacitors and RAM on a plastic board: the cheap &6HV&r circuit.',
], ['microprocessor'])
q('supercomputer', '&eMicroprocessor Supercomputer', ['gtceu:micro_processor_computer*4'], [
    'An assembly with diodes, RAM and fine electrum wire: your first &5EV circuit&r, long before EV.',
    'The Vacuum Freezer, the Distillation Tower and the scanner all need EV circuits. This is the HV bottleneck.',
], ['assembly'], kind='main')
q('smd', 'SMD Components', ['gtceu:smd_resistor*16', 'gtceu:smd_capacitor*16', 'gtceu:smd_transistor*16'], [
    'Surface-mount parts, made in the HV assembler with liquid polyethylene. They replace the hand-made resistor, capacitor and transistor in every circuit recipe and are far cheaper per circuit.',
    '> SMD diodes need platinum wire. They can wait for EV.',
], ['hv_assembler'], kind='opt')
q('scanner', 'Scanner', ['gtceu:hv_scanner'], [
    'Scans items onto data sticks. The Assembly Line (IV) needs scanned research, so you will want one eventually.',
], ['supercomputer', 'emitter'], kind='opt')

# ---------------------------------------------------------------- the HV power line
q('lpic', 'LPIC Chips', ['gtceu:lpic_chip*8'], [
    'An &eorange glass lens&r turns wafers into low-power IC wafers. Cut them at HV.',
], ['start'])
q('energy_hatch', '&eHV Energy Hatch', ['gtceu:hv_energy_input_hatch*2'], [
    'An HV hull, gold cable, an LPIC chip and an &eHigh Voltage Coil&r (fine black steel wire on a magnetic steel rod), with sodium-potassium alloy.',
    'Two HV hatches run &5EV recipes&r in a multiblock: titanium needs that.',
], ['lpic', 'hv_assembler'], kind='main', guide='gregtech/oc/tier-skipping.md')

# ---------------------------------------------------------------- vacuum freezer and kanthal
q('freezer', '&e&lVacuum Freezer', ['gtceu:vacuum_freezer'], [
    'Three HV pumps, three &5EV circuits&r and a Frost Proof casing. The structure is a 3x3x3 box of frost proof aluminium casings.',
    'Every metal that leaves the blast furnace as a &ehot ingot&r is cooled here. It also liquefies air and gases.',
], ['supercomputer', 'pump'], kind='hub', guide='gtmultis/vacuum_freezer.md')
q('kanthal', 'Kanthal', ['gtceu:kanthal_ingot*16'], [
    'Iron, aluminium and chrome. An &6HV&r blast furnace recipe that comes out hot: the Vacuum Freezer cools it.',
], ['freezer', 'energy_hatch'])
q('kanthal_coils', '&eKanthal Coils', ['gtceu:kanthal_coil_block*16'], [
    'Kanthal wire, aluminium foil and molten copper. Sixteen coils take the Electric Blast Furnace to &e2,700 K&r.',
    'Hotter coils also make every cooler recipe cheaper and faster.',
], ['kanthal'], kind='main', guide='gregtech/ebf-and-coils.md')
q('multi_smelter', 'Multi Smelter', ['gtceu:multi_smelter'], [
    'A furnace multiblock. It smelts many items at once, and better coils mean more at a time. Your old cupronickel coils have a new home.',
], ['ic'], kind='opt')
q('tempered_glass', 'Glass for Multiblocks', ['gtceu:tempered_glass*8', 'gtapexadditions:borosilicate_glass*8'], [
    'Tempered glass comes out of the blast furnace with oxygen. &eBorosilicate glass&r is the HV tier of the glass many Apex multiblocks ask for.',
], ['start'], kind='opt', guide='gregtech/glass-tiers.md')

# ---------------------------------------------------------------- oil and chemistry
q('oil', 'Oil', ['gtceu:oil_bucket'], [
    'Pump it with a Fluid Drilling Rig from an underground oil field. A prospector in fluid mode finds the fields.',
    'Light, heavy and raw oil all work; they give different shares of the same products.',
], ['start'], guide='lines/oil.md')
q('tower', '&e&lDistillation Tower', ['gtceu:distillation_tower'], [
    'Four &5EV circuits&r, two HV pumps and large stainless steel pipes. Built from clean stainless steel casings, one layer per output, up to 12 high.',
    'It splits a fluid into &oall&r its products at once. Oil becomes sulfuric fuels and gases; desulfurize them with hydrogen, and distill again.',
], ['supercomputer', 'pump', 'oil'], kind='hub', guide='gtmultis/distillation_tower.md')
q('diesel', 'Diesel', ['gtceu:diesel_bucket'], [
    'Light fuel and heavy fuel in a mixer. The standard fuel for combustion generators.',
    '> Mixed with tetranitromethane it becomes &ecetane-boosted diesel&r, the best fuel for the Large Combustion Engine at EV.',
], ['tower'])
q('cracker', 'Cracker', ['gtceu:cracker'], [
    'Cracks fuels and gases with hydrogen or steam. Cracked naphtha and gases distill into &eethylene, propene, butadiene and benzene&r: the feedstock for every plastic from here on.',
], ['tower'], kind='opt', guide='gtmultis/cracker.md')
q('lubricant', 'Lubricant', ['gtceu:lubricant_bucket'], [
    'Distill oil products, or brew it from soapstone, talc or redstone with creosote. The cutter runs faster on lubricant, and Assembly Line recipes drink it.',
], ['start'], kind='opt')
q('ptfe', '&ePTFE', ['gtceu:polytetrafluoroethylene_plate*16'], [
    'Chloroform and hydrofluoric acid make tetrafluoroethylene (an HV reactor recipe); add oxygen for &ePolytetrafluoroethylene&r.',
    'Chemically inert casings and pipes are made of it, and so is the IV hull.',
], ['start'], kind='main', guide='lines/rubber-and-plastics.md')
q('lcr', '&e&lLarge Chemical Reactor', ['gtceu:large_chemical_reactor'], [
    'Four advanced integrated circuits, large PTFE pipes, a stainless steel rotor and an HV motor. A 3x3x3 of inert PTFE casings with one coil block.',
    'It runs &oevery&r chemical reactor recipe, with perfect overclocks: four times the speed for four times the power. Move your chemistry here.',
], ['ptfe', 'ic'], kind='hub', guide='gtmultis/large_chemical_reactor.md')
q('epoxy', 'Epoxy', ['gtceu:epoxy_plate*8'], [
    'Epichlorohydrin and bisphenol A with sodium hydroxide. Epoxy boards carry the nanoprocessors of EV, so the line is worth starting now.',
], ['lcr'], kind='opt')
q('sbr', 'Styrene-Butadiene Rubber', ['gtceu:styrene_butadiene_rubber_plate*8'], [
    'Styrene and butadiene from cracked fuels. EV pumps want it, and it insulates cables up to IV.',
], ['cracker'], kind='opt')

# ---------------------------------------------------------------- field generator and new worlds
q('quantum_eye', 'Quantum Eye', ['gtceu:quantum_eye'], [
    'An Eye of Ender bathed in &eradon&r in an HV Chemical Bath. Radon comes from centrifuging uranium and thorium ores, or from air on the End.',
], ['emitter'])
q('field_gen', '&eHV Field Generator', ['gtceu:hv_field_generator'], [
    'A Quantum Eye, circuits and quadruple mercury barium calcium cuprate wire (the HV superconductor base).',
    'It lights the Twilight Forest portal.',
], ['quantum_eye'], kind='main')
q('twilight', '&2The Twilight Forest', ['dim:twilightforest:twilight_forest'], [
    'Ring a 2x2 pool of water with flowers and throw in an &eHV Field Generator&r.',
    '&eSapphire&r and &eolivine&r veins only exist here. Its nickel, molybdenum and galena veins are rich, and olivine and magnesite give the &emagnesium&r titanium needs.',
    'Keep the boss trophies: at IV they build the Library of Ruina.',
], ['field_gen'], kind='main', guide='dimensions/twilight-forest.md')
q('moon', '&7The Moon', ['ad_astra:tier_1_rocket', 'ad_astra:launch_pad'], [
    'The NASA Workbench, launch pad and Tier 1 Rocket are HV assembler builds. You also need a space suit and oxygen.',
    'The Moon has its own GregTech veins, and desh for the Tier 2 Rocket.',
], ['robot_arm'], kind='opt', guide='dimensions/space.md')
q('angel_ring', 'Angel Ring', ['gtapexadditions:angel_ring'], [
    'Creative flight, powered by Aether gravitite. An HV assembler recipe.',
], ['emitter'], kind='opt', guide='items/angel-ring.md')
q('blood_orb', 'Magician Blood Orb', ['gtapexadditions:raw_blood_orb_3'], [
    'The HV raw orb. Soak it in a tier 3 altar. See the &4Magic&r chapter.',
], ['hv_assembler'], kind='opt', guide='magic/blood-magic.md')
q('gaia_pylon', 'Gaia Pylons', ['botania:gaia_pylon*4'], [
    'With an HV Field Generator, Botania\'s Gaia Pylons come into reach, and with them the Gaia Guardian. See the &4Magic&r chapter.',
], ['field_gen'], kind='opt', guide='magic/botania.md')

# ---------------------------------------------------------------- titanium and EV
q('rutile', 'Rutile', ['gtceu:rutile_dust*8'], [
    'Titanium ore. &eIlmenite&r and &ebauxite&r give it as a byproduct (thermal centrifuge), and the Aether\'s bauxite veins are full of both.',
], ['start'], guide='lines/titanium.md')
q('ticl4', 'Titanium Tetrachloride', ['gtceu:titanium_tetrachloride_bucket'], [
    'Rutile, carbon and chlorine in an &6HV&r chemical reactor.',
], ['rutile', 'lcr'])
q('titanium', '&5&lTitanium', ['gtceu:titanium_ingot*16'], [
    'Titanium tetrachloride and &emagnesium&r in the blast furnace at 2,140 K: kanthal coils. It is an &5EV recipe&r, so the furnace needs two HV energy hatches.',
    'The ingots come out hot. Electrolyze the magnesium chloride to get your magnesium and chlorine back.',
], ['ticl4', 'kanthal_coils', 'energy_hatch'], kind='main', guide='lines/titanium.md')
q('ev', '&5&lOn to EV', ['gtceu:ev_machine_hull'], [
    'Titanium plates, polyethylene sheets and aluminium cable: the &5EV Machine Hull&r.',
    'EV is 2,048 EU/t. It brings nichrome coils and tungstensteel, the Implosion Compressor, nanoprocessors and Mars. Open &5Tier 4 - EV&r.',
], ['titanium'], kind='hub', bag='hv')


# ================================================================ more to do in HV (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'hv/' + key)
    x.deps += [d if '/' in d else 'hv/' + d for d in deps if (d if '/' in d else 'hv/' + d) not in x.deps]


q('hv_cables', 'HV Cables', ['gtceu:gold_single_cable*16'], [
    '&eGold&r and &eelectrum&r carry HV. Copper does not.',
], ['start'])
q('workhorses', 'The Workhorses, Again', ['gtceu:hv_macerator', 'gtceu:hv_centrifuge', 'gtceu:hv_electrolyzer'], [
    'Ore processing is where the hours go. HV versions of the three machines at the heart of it are worth their stainless steel.',
], ['start'])
q('clean_casing', 'Clean Stainless Casing', ['gtceu:clean_machine_casing*16'], [
    'Stainless steel plates and a frame. The distillation tower is built of these, and so is a good deal after it.',
], ['start'])
also('tower', 'clean_casing')
q('sensor', 'HV Sensor', ['gtceu:hv_sensor'], [
    'Emitter and sensor come in pairs from here on: the scanner wants both.',
], ['emitter'])
also('scanner', 'sensor')
q('hv_ca', 'HV Circuit Assembler', ['gtceu:hv_circuit_assembler'], [
    'Faster circuits, and the tier some of the processor recipes ask for.',
], ['hv_assembler'], kind='opt')
q('hv_battery', 'Lithium, HV', ['gtceu:hv_lithium_battery'], [
    'Fill the battery buffer. A machine that runs out of power in the middle of a recipe loses its progress.',
], ['battery'], kind='opt')
q('hv_prospector', 'HV Prospector', ['gtceu:prospector.hv'], [
    'A wider scan, and a second mode for &efluids&r: it shows the oil and gas under each chunk before you set a drilling rig on it.',
], ['battery'])
q('adv_jetpack', 'Advanced Jetpack', ['gtceu:advanced_electric_jetpack'], [
    'Faster, longer, and it can hover.',
], ['battery'], kind='opt')
q('ss_drum', 'Stainless Drum', ['gtceu:stainless_steel_drum'], [
    'Stainless steel holds &eacids&r. Bronze and steel drums do not: check a pipe or tank\'s tooltip before you put acid in it.',
], ['start'], kind='opt')
q('hv_super', 'HV Super Chest and Tank', ['gtceu:hv_super_chest', 'gtceu:hv_super_tank'], [
    'Bulk storage keeps pace with the tier.',
], ['start'], kind='opt')
q('hv_miner', 'HV Miner', ['gtceu:hv_miner'], [
    'The last of the single-block miners, and the best.',
], ['pump'], kind='opt')

# ---------------------------------------------------------------- the cleanroom's extras
q('cleaning_hatch', 'Cleaning Maintenance Hatch', ['gtceu:cleaning_maintenance_hatch'], [
    'In this pack a multiblock that makes clean things needs to be clean itself. A &eCleaning Maintenance Hatch&r counts as a cleanroom for the multiblock it is on.',
], ['cleanroom'], guide='gregtech/cleanroom.md')
q('ram', 'RAM', ['gtceu:ram_chip*8'], [
    'Engrave a wafer with a different lens and cut it. Memory goes into every assembly, computer and mainframe.',
], ['cleanroom'])
also('assembly', 'ram')
q('smd_more', 'The Rest of the SMDs', ['gtceu:smd_capacitor*8', 'gtceu:smd_transistor*8', 'gtceu:smd_diode*8'], [
    'Surface-mount parts replace the big ones in every circuit recipe at a fraction of the materials.',
], ['smd'], kind='opt')
q('ebf_heat', 'Coils and Heat', ['check'], [
    'A blast furnace hotter than its recipe needs runs the recipe cheaper, and hot enough it overclocks without the usual waste.',
    'Kanthal is the second coil. The guide page has the numbers for every coil.',
], ['kanthal_coils'], kind='tip', guide='gregtech/ebf-and-coils.md')

# ---------------------------------------------------------------- oil and gas
q('naphtha', 'Naphtha', ['gtceu:naphtha_bucket'], [
    'One of the tower\'s cuts. Cracked, it gives the ethylene, propene and benzene that plastics are made of, far faster than fermenting plants.',
], ['tower'])
q('refinery_gas', 'Refinery Gas', ['gtceu:refinery_gas_bucket'], [
    'The lightest cut. Burn it in gas turbines, or distill it for methane and LPG.',
], ['tower'], kind='opt')
q('light_fuel', 'Light and Heavy Fuel', ['gtceu:light_fuel_bucket', 'gtceu:heavy_fuel_bucket'], [
    'Mix the two for diesel. Every cut comes out sulfuric: it needs &ehydrogen&r to take the sulfur out before it is any use.',
], ['tower'])
also('diesel', 'light_fuel')
q('benzene', 'Benzene', ['gtceu:benzene_bucket'], [
    'From cracked naphtha or from wood tar. Benzene is the start of phenol, styrene and a long list of later plastics.',
], ['naphtha'], kind='opt')
also('sbr', 'benzene')

# ---------------------------------------------------------------- acids and bases
q('ammonia', 'Ammonia', ['gtceu:ammonia_bucket'], [
    'Nitrogen and hydrogen in the chemical reactor. Fertilizer on a farm; here, the start of nitric acid.',
], ['start'])
q('nitric', 'Nitric Acid', ['gtceu:nitric_acid_bucket'], [
    'Ammonia, oxygen and water, in a few steps. It is needed for dynamite now and for platinum processing later.',
], ['ammonia'])
q('implosion', '&eImplosion Compressor', ['gtceu:implosion_compressor'], [
    'A steel box that crushes dust back into gems with an explosion: every recipe uses up TNT, dynamite or industrial TNT.',
    'It needs a &emuffler hatch&r. Diamond, ruby, sapphire and the other gem dusts become gems again.',
], ['start'], kind='main')
q('dynamite', 'Dynamite', ['gtceu:dynamite*8'], [
    'Cheaper per recipe than TNT once the chemistry is running: glycerol and the two acids make the charge.',
], ['nitric', 'implosion'], kind='opt')
q('ilmenite', 'Ilmenite', ['gtceu:raw_ilmenite*16'], [
    'The other titanium ore. It gives rutile and iron.',
], ['start'], kind='opt')
