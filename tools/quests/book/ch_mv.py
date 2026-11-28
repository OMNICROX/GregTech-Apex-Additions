"""Tier 2 - MV: aluminium machines, the first real chemistry (silicone rubber, polyethylene), silicon chips from the
Laser Engraver, and the road to stainless steel and HV. Every link below is a real recipe requirement."""
from fw import Chapter

C = Chapter('mv', '&bTier 2 - MV', 'gtceu:mv_machine_hull', '', 3, '128 EU/t: medium voltage', 'mv', 'pick_mv', xp_base=20)
C.derive = False   # hand-written links only: the grid layout reads best with one clear path per quest
q = C.q

# ---------------------------------------------------------------- the start
q('start', '&b&lTier 2: MV', ['gtceu:mv_machine_hull'], [
    'Welcome to &bMV&r: 128 EU/t per amp, four times LV.',
    'MV is where GregTech stops being metalwork and becomes chemistry. Three big goals run through this chapter:',
    '&e1.&r Plastics: &esilicone rubber&r for pumps and conveyors, then &epolyethylene&r for HV hulls.',
    '&e2.&r Chips: silicon boules, wafers and the &eLaser Engraver&r, which end in the &eMV Energy Hatch&r.',
    '&e3.&r Stainless steel, which needs your EBF running &eHV&r recipes. That is the door to HV.',
    '> Build MV machines only where speed matters. LV machines still run every LV recipe, and an MV machine overclocks them 2x faster for 4x the power.',
], ['lv/mv'], kind='hub')
q('mv_power', 'MV Power', ['gtceu:mv_steam_turbine'], [
    'An &eAdvanced Steam Turbine&r turns 256 mB/t of steam into 128 EU/t. Your boilers probably can\'t feed that yet: a Large Bronze Boiler or two can.',
    'Gas turbines and combustion generators also come in MV. The fuels that make them worth it (ethanol, refinery gas) arrive in this chapter.',
    '> Running LV generators into an LV->MV transformer works too. 4 LV amps in, 1 MV amp out.',
], ['start'])
q('mv_transformer', 'Stepping Up', ['gtceu:mv_transformer_1a'], [
    'A transformer steps 4 amps of LV up to 1 amp of MV (screwdriver to flip it), or MV down to LV. Your LV generators can power MV machines through one.',
], ['mv_power'], kind='opt')
q('mv_battery', 'MV Batteries', ['gtceu:mv_lithium_battery'], [
    'Medium batteries hold far more than LV ones. &eLithium&r comes from &eLepidolite&r and &eSpodumene&r in the &eSalts vein&r (Y 30-70), split in an &eMV Electrolyzer&r.',
    'Put them in an MV Battery Buffer and your MV network stops browning out.',
], ['mv_power'], kind='opt')
q('mv_buffer', 'MV Battery Buffer', ['gtceu:mv_battery_buffer_4x'], [
    'One amp out per battery inside, at MV. The heart of your MV grid.',
], ['mv_battery'], kind='opt')

# ---------------------------------------------------------------- components
q('motor', '&eMV Motor', ['gtceu:mv_electric_motor'], [
    'Every MV component starts here: &ecupronickel wire&r, &ealuminium rods&r and a &emagnetic steel rod&r.',
    'Magnetic steel comes from the &ePolarizer&r: one steel rod in, one magnetic rod out.',
    '> Make motors in batches of 8 or more. Pumps, conveyors, pistons and robot arms all need them.',
], ['start'], kind='main')
q('polarizer', 'Polarizer', ['gtceu:lv_polarizer', 'gtceu:magnetic_steel_rod*4'], [
    'Magnetizes rods for motors. An LV one is plenty.',
], ['motor'])
q('piston', 'MV Piston', ['gtceu:mv_electric_piston'], [
    'Aluminium plates and rods, a small aluminium gear and a motor. The Laser Engraver needs two.',
], ['motor'])
q('chem_reactor', '&eAdvanced Chemical Reactor', ['gtceu:mv_chemical_reactor'], [
    'The most important MV machine. Glass, a bronze rotor, an MV motor and good circuits. &oNo pump needed&r, which matters: you need this reactor to make the rubber that pumps are made from.',
    'Most of this chapter\'s chemistry needs MV voltage, so the LV reactor can\'t do it.',
], ['motor', 'good_circuit'], kind='main')

# ---------------------------------------------------------------- MV circuits
q('phenolic', 'Phenolic Circuit Boards', ['gtceu:phenolic_printed_circuit_board*4'], [
    'Wood pulp and glue in the assembler make a &ePhenolic Circuit Board&r. Wrap it in silver wire by hand, or etch it with silver foil and &esodium persulfate&r in a chemical reactor.',
    'Sodium persulfate: salt and sulfuric acid make sodium bisulfate (chemical reactor), and the electrolyzer splits it.',
], ['start'])
q('gaas', 'Gallium Arsenide', ['gtceu:gallium_arsenide_dust'], [
    'Gallium and arsenic in the mixer. Both are ore byproducts, so you may already have some:',
    '&eGallium&r: &eSphalerite&r (Nether sulfur vein, Y 10-30) or &eBauxite&r (deepslate mica vein, Y -40 to -10), through the thermal centrifuge or electrolyzer.',
    '&eArsenic&r: &eRealgar&r in the copper-tin vein, or &eCobaltite&r in the nickel vein.',
    '> A small pile of gallium arsenide goes into every silicon boule too. Keep a stock.',
], ['start'])
q('diode', 'Diodes', ['gtceu:diode*8'], [
    'Fine annealed copper wire and a small pile of gallium arsenide, with molten glass, in the assembler. Later, silicon wafers and polyethylene make them more cheaply.',
], ['gaas'])
q('good_circuit', '&eGood Electronic Circuit', ['gtceu:good_electronic_circuit*4'], [
    'Your first &bMV circuit&r: a phenolic printed circuit board, two basic circuits, two diodes and copper wire. The circuit assembler makes them with solder.',
    'Every MV machine uses two or three. This is the main MV bottleneck until integrated circuits.',
], ['phenolic', 'diode'], kind='main')
q('robot_arm', 'MV Robot Arm', ['gtceu:mv_robot_arm'], [
    'Two motors, a piston, a good circuit and aluminium rods.',
], ['piston', 'good_circuit'])

# ---------------------------------------------------------------- silicone rubber
q('silicon', 'Silicon', ['gtceu:silicon_dust*16'], [
    'Silicon is a byproduct of &eZeolite&r (copper-tin vein) and &eFuller\'s Earth&r (mineral sand vein). Electrolyzing silicon dioxide also gives it.',
    'You need a lot: silicone rubber, silicon boules and later solar panels all eat silicon.',
], ['start'])
q('chloromethane', 'Chloromethane', ['gtceu:chloromethane_bucket'], [
    'Methane and chlorine in a chemical reactor (LV is fine). Methane from centrifuging mushrooms or nether wart; chlorine from electrolyzing salt or hydrochloric acid.',
    'The hydrochloric acid byproduct electrolyzes back into chlorine. Loop it.',
], ['start'])
q('pdms', 'Polydimethylsiloxane', ['gtceu:polydimethylsiloxane_dust*4'], [
    'Silicon dust and chloromethane make dimethyldichlorosilane; add water and it becomes &ePolydimethylsiloxane&r. Both steps need the &eAdvanced Chemical Reactor&r.',
], ['silicon', 'chloromethane', 'chem_reactor'])
q('silicone_rubber', '&eSilicone Rubber', ['gtceu:silicone_rubber_plate*8'], [
    '&cImportant:&r in this pack, &eMV pumps and conveyors don\'t take normal rubber&r. They need &eSilicone Rubber&r (or styrene-butadiene rubber, which is HV).',
    'Polydimethylsiloxane and sulfur in a chemical reactor make liquid silicone rubber. Cast it into sheets in the fluid solidifier.',
], ['pdms'], kind='main')
q('pump', 'MV Pump', ['gtceu:mv_electric_pump'], [
    'A steel fluid pipe, a bronze rotor, silicone rubber rings and an MV motor. The Distillery needs one.',
], ['silicone_rubber', 'motor'])
q('conveyor', 'MV Conveyor', ['gtceu:mv_conveyor_module'], [
    'Two MV motors in silicone rubber sheets. Most MV processing machines take one.',
], ['silicone_rubber', 'motor'])

# ---------------------------------------------------------------- MV machines worth building
q('mv_machines', 'An MV Workshop', ['gtceu:mv_centrifuge', 'gtceu:mv_electrolyzer'], [
    'With conveyors and pumps, the MV versions of your ore machines open up. The centrifuge and electrolyzer gain recipes LV can\'t run, including most of the MV byproduct chains.',
    '> Only upgrade a machine when a recipe needs MV or the LV one can\'t keep up. Most LV machines stay useful all game.',
], ['conveyor', 'pump'])
q('sifter', 'Sifter', ['gtceu:lv_sifter'], [
    'Sifts purified gem ores into exquisite, flawless and normal gems. Exquisite gems make lenses; flawless emeralds make emitters.',
], ['mv_machines'], kind='opt')
q('em_separator', 'Electromagnetic Separator', ['gtceu:mv_electromagnetic_separator'], [
    'Pulls magnetic byproducts out of purified dusts: extra iron, nickel, neodymium and gold.',
], ['mv_machines'], kind='opt')

# ---------------------------------------------------------------- plastics
q('biomass', 'Biomass', ['gtceu:lv_brewery', 'gtceu:biomass_bucket'], [
    'The &eBrewery&r turns plants (saplings, seeds, crops, mushrooms...) and water into &eBiomass&r. Any farm works.',
], ['start'])
q('fermenter', 'Fermenting', ['gtceu:lv_fermenter', 'gtceu:fermented_biomass_bucket'], [
    'Biomass ferments into &eFermented Biomass&r. Slow: build several fermenters.',
], ['biomass'])
q('distillery', '&eAdvanced Distillery', ['gtceu:mv_distillery'], [
    'Glass, a cupronickel spring, an &eMV pump&r and good circuits. It separates fluids, and many of its recipes need MV.',
    'Fermented biomass distills into &eEthanol&r (and fertilizer). Wood vinegar from the Pyrolyse Oven distills into methanol and more.',
], ['pump', 'fermenter'], kind='main')
q('ethanol', 'Ethanol', ['gtceu:ethanol_bucket'], [
    'A fuel (combustion generators love it) and the start of plastics.',
], ['distillery'])
q('ethylene', 'Ethylene', ['gtceu:ethylene_bucket'], [
    'Ethanol and sulfuric acid in the Advanced Chemical Reactor make ethylene. The diluted sulfuric acid distills back into sulfuric acid.',
    '> Later, cracking oil fuels gives ethylene in bulk. The bio route is the reliable one at MV.',
], ['ethanol', 'chem_reactor'])
q('polyethylene', '&e&lPolyethylene', ['gtceu:polyethylene_plate*16'], [
    'Ethylene and oxygen in a chemical reactor make &ePolyethylene&r, GregTech\'s first plastic. Cast it into sheets in the fluid solidifier.',
    'The HV hull is wrapped in polyethylene sheets, and the assembler uses liquid polyethylene for MV hulls and diodes. Keep a tank of it running.',
], ['ethylene'], kind='main')
q('pyrolyse', 'Pyrolyse Oven', ['gtceu:pyrolyse_oven'], [
    'An MV multiblock (cupronickel coils) that bakes logs into charcoal plus &ecreosote, wood vinegar, wood gas or wood tar&r depending on the circuit.',
    'Wood vinegar distills into &emethanol&r, acetone and acetic acid. A tree farm feeding this oven runs a lot of chemistry.',
], ['distillery'], kind='opt')
q('oil', 'Oil', ['gtceu:mv_fluid_drilling_rig'], [
    'The Advanced Fluid Drilling Rig pumps oil from underground fluid veins. The single-block distillery splits oil into fuels, and the fuels into ethylene and benzene. The big oil chains arrive with the HV Distillation Tower.',
], ['distillery'], kind='opt')

# ---------------------------------------------------------------- silicon chips
q('emitter', 'Emitters and Sensors', ['gtceu:mv_emitter', 'gtceu:mv_sensor'], [
    'Both need a &eFlawless Emerald&r and electrum rods. Emeralds come from mountain emerald ore and the Nether beryllium vein (Y 5-30). Sift the purified ore for flawless and exquisite gems.',
], ['good_circuit'])
q('engraver', '&e&lLaser Engraver', ['gtceu:mv_laser_engraver'], [
    'Two pistons, an emitter and good circuits: the machine behind every chip in GregTech.',
    'A &elens&r in the slot picks what it engraves into a silicon wafer. Ruby makes logic chips, sapphire makes the low-power chips energy hatches need, and many more come later.',
], ['emitter', 'piston'], kind='hub')
q('lenses', 'Lenses', ['gtceu:ruby_lens', 'gtceu:sapphire_lens'], [
    'Turn an exquisite gem (sifter) in the lathe, or a gem plate in an MV lathe. &eRuby&r: redstone veins. &eSapphire&r: the deepslate sapphire vein (Y -40 to 0). Lenses never wear out.',
], ['engraver'])
q('boule', '&eSilicon Boule', ['gtceu:silicon_boule'], [
    'Silicon dust and a small pile of gallium arsenide, 7.5 minutes in the EBF. MV recipe: your two LV energy hatches are enough.',
    '> Start boules early and run them all the time. Everything chip-shaped waits on them.',
], ['silicon', 'gaas'], kind='main')
q('wafer', 'Silicon Wafers', ['gtceu:mv_cutter', 'gtceu:silicon_wafer*16'], [
    'An &eMV Cutter&r slices a boule into wafers. Distilled water or lubricant cut faster than plain water.',
], ['boule'])
q('ic', 'Integrated Circuits', ['gtceu:ilc_chip*8', 'gtceu:basic_integrated_circuit*4'], [
    'Ruby lens: wafer to &eILC Wafer&r, then the cutter makes &eIC Chips&r.',
    'An IC chip, a resin board, a resistor, a diode, fine wire and a bolt make a &eBasic Integrated Circuit&r: an LV circuit without vacuum tubes.',
], ['engraver', 'wafer', 'lenses'])
q('good_ic', 'Good Integrated Circuit', ['gtceu:good_integrated_circuit*4'], [
    'A basic integrated circuit on a phenolic board, with fine gold wire and silver bolts: an MV circuit that skips the two basic circuits. Cheaper per circuit once chips flow.',
], ['ic', 'good_circuit'])
q('ulpic', 'ULPIC Chips', ['gtceu:ulpic_chip*4'], [
    'Sapphire lens: wafer to &eULPIC Wafer&r, then the cutter. Ultra-low-power chips go into every &eenergy hatch&r and dynamo from MV up.',
], ['engraver', 'wafer', 'lenses'])
q('energy_hatch', '&eMV Energy Hatch', ['gtceu:mv_energy_input_hatch*2'], [
    'An MV hull, copper cable, a ULPIC chip and a &eMV Voltage Coil&r.',
    'Two MV hatches let a multiblock run &6HV recipes&r (up to 512 EU/t). That is how you make stainless steel.',
], ['ulpic'], kind='main')

# ---------------------------------------------------------------- stainless steel and the road to HV
q('chrome', 'Chrome and Manganese', ['gtceu:chromium_dust*4', 'gtceu:manganese_dust*4'], [
    '&eChrome&r is a byproduct of &eRuby&r (redstone veins). Chromite in the End is a richer source later.',
    '&eManganese&r comes from the deepslate &emanganese vein&r (Y -30 to 0): pyrolusite, spessartine and tantalite. Tungstate in the Nether gives it too.',
], ['start'])
q('stainless_dust', 'Stainless Steel Dust', ['gtceu:mv_mixer', 'gtceu:stainless_steel_dust*9'], [
    'Iron, nickel, manganese and chrome in an &eMV Mixer&r (invar can stand in for the nickel and some iron). Check the recipe for the exact amounts.',
], ['chrome', 'mv_machines'])
q('stainless', '&6&lStainless Steel', ['gtceu:stainless_steel_ingot*16'], [
    'Stainless steel melts at 1700 K, so cupronickel coils are fine. But it is an &6HV recipe&r: the EBF needs your two &eMV Energy Hatches&r.',
    'It is everywhere in HV: casings, hulls, pipes and the Cleanroom\'s rotors. Make a stack and keep the EBF busy.',
    '> Nitrogen speeds it up. Centrifuge air.',
], ['stainless_dust', 'energy_hatch'], kind='main')
q('hv', '&6&lOn to HV', ['gtceu:hv_machine_hull'], [
    'A stainless steel casing, polyethylene sheets and gold cable: the &6HV Machine Hull&r.',
    'HV is 512 EU/t. It brings the Cleanroom, the Vacuum Freezer (and with it kanthal coils), the Distillation Tower and advanced circuits. Open &6Tier 3 - HV&r.',
], ['stainless', 'polyethylene'], kind='hub', bag='mv')

# ---------------------------------------------------------------- the rest of the pack at MV
q('iron_man', 'Mark I', ['satsu_iron_man_addon:marks/mark_01/mark_01_chest'], [
    'The first Iron Man suit uses MV parts. Clunky, but it is your first suit of armour you built yourself. See the &cHeroes&r chapter.',
], ['robot_arm'], kind='opt')
q('rocket_fuel', 'Fuel Refinery', ['ad_astra:fuel_refinery'], [
    'Ad Astra\'s refinery is an MV build here. Rockets are still a long way off, but fuel production can start.',
], ['distillery'], kind='opt')
q('mana_pool', 'Mechanical Mana Pool', ['botanicalmachinery:mechanical_mana_pool'], [
    'Botanical Machinery\'s automated pool, built around an MV hull. Infusions without the floor space.',
], ['mv_machines'], kind='opt')
q('greenhouse', 'Greenhouse', ['gtna:greenhouse'], [
    'A multiblock farm: saplings and crops in, wood and produce out. Pairs with the Brewery and Pyrolyse Oven.',
], ['biomass'], kind='opt')
