"""Tier 1 - LV: the first electric machines, power, ore byproducts, chemistry and the Electric Blast Furnace."""
from fw import Chapter, place

C = Chapter('lv', '&7Tier 1 - LV', 'gtceu:lv_machine_hull', '', 2, '32 EU/t: low voltage', 'lv', 'pick_lv', xp_base=12)
C.derive = False   # hand-written links only: the grid layout reads best with one clear path per quest
q = C.q

# ---------------------------------------------------------------- the start
q('start', '&7&lTier 1: LV', ['gtceu:lv_machine_hull'], [
    'Welcome to electricity. &eLV&r is 32 EU/t per amp. Almost every kind of GregTech machine has an LV version, and most of them you can build right now. Try not to be overwhelmed: this chapter shows the order that pays off.',
    'Three rules for the rest of the game:',
    '&cNever feed a machine a higher voltage than its tier.&r An LV machine on an MV cable explodes.',
    'A machine draws only as many &eamps&r as it needs; a cable carries a limited number of amps and loses a little EU per block. Keep LV networks short.',
    'Recipes show their voltage. An LV machine runs anything up to 32 EU/t; a recipe needing less runs faster through &eoverclocking&r in a higher tier machine.',
], ['steam/hull'], kind='hub')
q('voltage', 'Voltage, Amps and Explosions', ['check'], [
    'Every generator outputs one voltage. A &eTransformer&r steps it down (or up with a screwdriver) between tiers.',
    'Machines explode when fed more voltage than their tier, when rained on with exposed cables and wires, and when pipes of hot fluid melt. Use cables, not bare wires, and keep them out of the rain.',
], ['start'], kind='tip')
q('components', 'LV Components', ['gtceu:lv_electric_pump', 'gtceu:lv_electric_piston', 'gtceu:lv_conveyor_module'], [
    'Every LV machine is a hull, circuits, cable and some of these: &eMotor&r, &ePump&r, &ePiston&r, &eConveyor&r and &eRobot Arm&r.',
    'Pumps need rubber rings and a tin rotor; pistons need steel; conveyors need rubber sheets. Make them in batches.',
], ['start'])
q('robot_arm', 'LV Robot Arm', ['gtceu:lv_robot_arm'], [
    'Two motors, a piston, a circuit, cable and rods. The Assembler needs two.',
], ['start'])

# ---------------------------------------------------------------- shaping metal
q('wiremill', '&eWiremill', ['gtceu:lv_wiremill'], [
    'One ingot makes two wires here instead of a plate per wire by hand, and wire becomes fine wire. Your cable problem is solved.',
], ['start'])
q('fine_wire', 'Fine Copper Wire', ['gtceu:fine_copper_wire*16'], [
    'Resistors, the first coils and many circuits use fine wire. The wiremill makes four from one wire.',
], ['wiremill'])
q('bender', '&eBender', ['gtceu:lv_bender'], [
    'One ingot, one plate. Also foil, double plates and springs. From now on, never hammer plates by hand.',
], ['wiremill'])
q('foil', 'Foil', ['gtceu:bronze_foil*8'], [
    'The bender rolls plates into foil. Bronze foil goes into Cupronickel Coils; copper foil into circuit boards.',
], ['bender'])
q('lathe', 'Lathe and Cutter', ['gtceu:lv_lathe', 'gtceu:lv_cutter'], [
    'The &eLathe&r turns ingots into rods and bolts into screws; the &eCutter&r cuts rods into bolts and blocks into plates. Together they replace your file and saw.',
    'The cutter needs water (or lubricant, which is faster).',
], ['bender'])
q('assembler', '&6Assembler', ['gtceu:lv_assembler'], [
    'The most important LV machine. It builds components, circuits parts, hatches, casings and a large part of the pack\'s other mods: AE2, Pipez, item collectors, the construction wand, Botania\'s altars...',
    'It makes vacuum tubes from glass tubes, bolts and wire with red alloy instead of most of the copper, and resistors with glue. Build two.',
], ['lathe', 'robot_arm'], kind='hub')
q('emitter', 'Emitters and Sensors', ['gtceu:lv_emitter', 'gtceu:lv_sensor'], [
    'Brass rods, quartzite and circuits. Covers, the Circuit Assembler and the Generator Array need emitters; detectors need sensors.',
], ['assembler'])
q('circuits', '&eMass-Produced Circuits', ['gtceu:basic_electronic_circuit*16'], [
    'With an assembler, circuits get much cheaper: vacuum tubes with red alloy, resistors with glue, and printed boards from copper foil and wood planks with glue.',
    'You will need dozens of LV circuits this tier. Automate the parts.',
], ['assembler'])

# ---------------------------------------------------------------- power
q('turbine', '&eSteam Into Power', ['gtceu:lv_steam_turbine'], [
    'The &eBasic Steam Turbine&r burns the steam from your boilers into EU. Your steam setup becomes your power plant.',
    'It turns 2 mB of steam into 1 EU, so it needs 64 mB/t of steam to run flat out at 32 EU/t. Large boilers, solar fields and liquid boilers all feed it.',
], ['start'])
q('combustion', 'Combustion Generator', ['gtceu:lv_combustion'], [
    'Burns liquid fuels. Look at its uses (&eU&r) for every fuel it takes. Most of them (oil fuels, ethanol, biodiesel) come in MV, so this one waits a little.',
], ['turbine'])
q('gas_turbine', 'Gas Turbine', ['gtceu:lv_gas_turbine'], [
    'Burns gases: methane from centrifuging mushrooms and rubber logs, refinery gas later. Another way to turn farms into power.',
], ['turbine'])
q('large_boiler', 'Large Bronze Boiler', ['gtceu:bronze_large_boiler'], [
    'Basic circuits, tin cable and a firebox casing: a multiblock boiler that makes far more steam than small ones from any fuel. Throttle it in its screen to save fuel.',
], ['turbine'])
q('generator_array', 'Generator Array', ['gtna:generator_array'], [
    'A multiblock that runs many steam turbines, gas turbines or combustion generators as one: put the generators in its input and get their power together. One building instead of a wall of generators.',
], ['turbine'])
q('buffer', '&eBattery Buffer', ['gtceu:lv_battery_buffer_4x'], [
    'Stores power in batteries and releases it at LV. Put one between your generators and machines: it smooths everything out and is your network\'s heart.',
    'A buffer outputs 1 amp per battery inside.',
], ['start'])
q('transformer', 'Transformer', ['gtceu:lv_transformer_1a'], [
    'Steps MV down to LV, or LV up to MV with a screwdriver. Your first MV machines will want one.',
], ['buffer'])
q('charger', 'Charger', ['gtceu:lv_charger_4x'], [
    'Charges batteries and electric tools. Electric tools (drill, chainsaw, wrench, screwdriver) never wear out as long as you charge them.',
], ['buffer'])
q('batteries', '&eBatteries', ['gtceu:lv_sodium_battery'], [
    'Battery Alloy (lead and antimony) and tin cable make a Small Battery Hull; the &eCanner&r fills it with sodium, lithium or cadmium dust.',
    'Lithium holds the most; sodium is the easiest to get (electrolyze salt from the Salts vein).',
], ['buffer'])
q('battery_alloy', 'Battery Alloy', ['gtceu:battery_alloy_ingot*4'], [
    'Lead and antimony. Antimony comes from Stibnite, and from Realgar in the copper-tin vein.',
], ['batteries'])
q('canner', 'Canner', ['gtceu:lv_canner'], [
    'Fills battery hulls, cells and fluid containers.',
], ['batteries'])

# ---------------------------------------------------------------- ore processing
q('macerator', '&eMacerator', ['gtceu:lv_macerator'], [
    'An electric macerator with byproduct slots. Every ore now gives its byproducts too: a chance at gold from copper, nickel from iron, silver and sulfur from galena.',
], ['start'])
q('ore_washer', '&eOre Washer', ['gtceu:lv_ore_washer'], [
    'Washes crushed ore into purified ore with water, and gives the washing byproduct. No more cauldrons.',
], ['macerator'])
q('chemical_bath', 'Chemical Bath', ['gtceu:lv_chemical_bath'], [
    'Washing with mercury or sodium persulfate instead of water gives a different, often better byproduct: gold from copper, platinum from nickel... Check each ore.',
], ['ore_washer'])
q('thermal', '&eThermal Centrifuge', ['gtceu:lv_thermal_centrifuge'], [
    'Spins purified ore into &erefined&r ore with a third byproduct. Purified, then thermal, then macerate: the full three-byproduct chain.',
], ['ore_washer'])
q('centrifuge', '&eCentrifuge', ['gtceu:lv_centrifuge'], [
    'Splits dusts and fluids into their parts. Impure and purified dust gives a last byproduct. It also makes &eglue&r from sticky resin, and methane from mushrooms and rubber logs.',
], ['thermal'])
q('glue', 'Glue', ['gtceu:glue_bucket'], [
    'Centrifuged sticky resin. The assembler uses glue for resistors, printed boards and LV buses.',
], ['centrifuge'])
q('electrolyzer', '&eElectrolyzer', ['gtceu:lv_electrolyzer'], [
    'Splits compounds into elements. Water into hydrogen and oxygen; salt into sodium and chlorine; ruby, sapphire and bauxite into aluminium.',
    'It needs gold wire. Gold comes with magnetite and as a byproduct of copper.',
], ['centrifuge'])
q('water_split', 'Hydrogen and Oxygen', ['check'], [
    'Electrolyze water into hydrogen and oxygen. Oxygen makes steel faster in the EBF and is in half of LV chemistry; hydrogen is a gas turbine fuel and goes into ammonia and hydrochloric acid later.',
], ['electrolyzer'], kind='tip')
q('aluminium_ore', 'Where Aluminium Comes From', ['gtceu:sapphire_gem*4'], [
    'Aluminium is the MV metal. Sources: the &eSapphire vein&r (Almandine, Pyrope, Sapphire, Green Sapphire; deepslate, Y -40 to 0), ruby from the Redstone vein, and Bauxite in the Mica vein (deepslate, Y -40 to -10).',
    'Electrolyze them into aluminium dust, or roast ruby and sapphire in the EBF.',
], ['electrolyzer'])
q('aluminium', '&6Aluminium', ['gtceu:aluminium_ingot*16'], [
    'Aluminium dust smelted in the Electric Blast Furnace at 1700 K. Cupronickel coils reach 1800 K, so your first EBF can do it.',
    'The recipe needs &eMV power&r (120 EU/t): two LV Energy Hatches on the EBF are enough.',
], ['aluminium_ore', 'ebf_power'], kind='main')

# ---------------------------------------------------------------- chemistry
q('mixer', '&eMixer', ['gtceu:lv_mixer'], [
    'Mixes dusts and fluids into alloys and compounds. Most alloy dusts you mixed by hand come out of here in bulk.',
], ['start'])
q('tin_alloy', 'Tin Alloy', ['gtceu:tin_alloy_ingot*4'], [
    'Tin and iron. Molten tin alloy goes into Cupronickel Coils.',
], ['mixer'])
q('soldering', '&eSoldering Alloy', ['gtceu:soldering_alloy_ingot*8'], [
    'Tin, lead and antimony in the mixer. Molten solder is how the assembler joins circuit parts, and many of the pack\'s assembler recipes (AE2, Botania, waystones...) need it.',
    'Melt it in an Extractor to use it as a fluid.',
], ['mixer'])
q('extractor', 'Extractor', ['gtceu:lv_extractor'], [
    'The electric Extractor also melts solids into fluids: solder, red alloy, tin alloy, glass. Assemblers and solidifiers eat molten metal.',
], ['soldering'])
q('chem_reactor', '&eChemical Reactor', ['gtceu:lv_chemical_reactor'], [
    'Runs chemistry. Most LV and MV chemistry starts here: sulfur dioxide, sulfuric acid, hydrochloric acid, and later plastics.',
], ['soldering'])
q('sulfuric', 'Sulfuric Acid', ['gtceu:sulfuric_acid_bucket'], [
    'Sulfur and oxygen make sulfur dioxide, more oxygen makes sulfur trioxide, and water makes &eSulfuric Acid&r. It is used in huge amounts for the rest of the game: in ethylene, in ore processing, in batteries.',
    'Set up an automatic line now and you will thank yourself later.',
], ['chem_reactor'])
q('solidifier', 'Fluid Solidifier', ['gtceu:lv_fluid_solidifier'], [
    'Casts molten metal and plastic into plates, ingots, rods and more using molds.',
], ['chem_reactor'])

# ---------------------------------------------------------------- the Electric Blast Furnace
q('invar', 'Heat Proof Casings', ['gtceu:heatproof_machine_casing*16'], [
    'Invar plates around an invar frame. The EBF needs a lot of them.',
], ['start'])
q('coils', '&eCupronickel Coils', ['gtceu:cupronickel_coil_block*16'], [
    'Cupronickel wire (copper and nickel), bronze foil and molten tin alloy, in the assembler. Coils set the EBF\'s temperature: cupronickel reaches &e1800 K&r.',
    'Better coils later (kanthal, nichrome...) unlock hotter metals and make every recipe faster.',
], ['invar', 'tin_alloy'])
q('ebf', '&6&lElectric Blast Furnace', ['gtceu:electric_blast_furnace'], [
    'The most important multiblock in GregTech. Three furnaces, circuits, cable and a heat proof casing make the controller. The structure is two rings of 8 coils on a casing base: check the preview.',
    'It smelts every metal that needs heat: aluminium now, and stainless steel, titanium and tungsten later. Each tier\'s best metals go through an EBF.',
], ['coils'], kind='hub')
q('ebf_power', '&ePowering the EBF', ['gtceu:lv_energy_input_hatch*2'], [
    'Multiblocks take power through &eEnergy Hatches&r. Two LV hatches let the EBF run &eMV recipes&r (up to 128 EU/t), which is what aluminium needs.',
    'Keep the EBF fed: if power runs out mid-recipe, the progress is lost.',
], ['ebf'], kind='main')
q('maintenance', 'Maintenance Hatch', ['gtceu:maintenance_hatch'], [
    'Every big multiblock needs one. Occasionally it develops problems (wrench, screwdriver, hammer, mallet, wire cutter, crowbar); fix them by opening it with the tools in your inventory, or each problem slows the machine.',
], ['ebf'])
q('muffler', 'Muffler Hatch', ['gtceu:lv_muffler_hatch'], [
    'The EBF vents smoke through a muffler on top. Keep the block above it clear. Better mufflers give a chance at extra ashes.',
], ['ebf'])
q('buses', 'Input and Output Buses', ['gtceu:lv_input_bus', 'gtceu:lv_output_bus'], [
    'Items go in and out of multiblocks through buses; fluids through hatches. The assembler makes them with glue. A screwdriver flips one into the other.',
], ['ebf'])
q('ebf_steel', 'Steel, the Fast Way', ['check'], [
    'In the EBF, iron and oxygen become steel in 25 seconds, wrought iron and oxygen in 15. Your Primitive Blast Furnaces can retire.',
], ['ebf_power'], kind='tip')

# ---------------------------------------------------------------- the rest of the pack at LV
q('cultivation', 'Mystical Cultivation Array', ['gtapexadditions:mystical_cultivation_array'], [
    'Mystical Agriculture crops grown inside a GregTech multiblock, faster and without farmland.',
], ['assembler'], kind='opt')
q('lava_maker', 'Steam Lava Maker', ['gtna:steam_lava_maker'], [
    'Ten LV circuits in the assembler: a steam multiblock that melts stone, cobblestone, granite and diorite into lava.',
], ['assembler'], kind='opt')
q('ae2', 'Applied Energistics Begins', ['ae2:inscriber'], [
    'AE2 recipes are made in the assembler here, and the first ones are LV: the &eInscriber&r, the presses, and the processors behind the ME Controller.',
    'Digital storage this early is a big deal. Certus quartz and meteorites come first.',
], ['assembler'], kind='opt')
q('pipes', 'Item Pipes', ['pipez:item_pipe'], [
    'Pipez item pipes come from the assembler. Fast, simple item transport with filters. GregTech item pipes also exist and are better for long runs.',
], ['assembler'], kind='opt')
q('wand', 'Construction Wand', ['constructionwand:iron_wand'], [
    'Places a whole row or face of blocks at once. Building the EBF\'s coil rings by hand gets old.',
], ['assembler'], kind='opt')

# ---------------------------------------------------------------- the end of LV
q('mv', '&b&lOn to MV', ['gtceu:mv_machine_hull'], [
    'Aluminium plates and copper cable around an MV casing: the &bMV Machine Hull&r.',
    'MV is 128 EU/t. It brings the Laser Engraver (and with it real chips), polyethylene, the Cleanroom\'s parts and better coils. Open &bTier 2 - MV&r.',
], ['aluminium', 'circuits'], kind='hub', bag='lv')

# ---------------------------------------------------------------- layout
place(C, col=3.6, row=2.9, cells={
    'wiremill': (1, 0), 'bender': (2, 0), 'lathe': (3, 0), 'assembler': (4, 0), 'circuits': (5, 0),
    'turbine': (1, 1), 'buffer': (2, 1), 'batteries': (3, 1),
    'start': (0, 2), 'macerator': (1, 2), 'ore_washer': (2, 2), 'thermal': (3, 2), 'centrifuge': (4, 2), 'electrolyzer': (5, 2),
    'aluminium': (6, 2), 'mv': (7, 2),
    'mixer': (1, 3), 'soldering': (2, 3), 'chem_reactor': (3, 3), 'sulfuric': (4, 3),
    'invar': (1, 4), 'coils': (2, 4), 'ebf': (3, 4), 'ebf_power': (4, 4),
}, deps={
    'wiremill': ['start'], 'bender': ['wiremill'], 'lathe': ['bender'], 'assembler': ['lathe'], 'circuits': ['assembler'],
    'turbine': ['start'], 'buffer': ['turbine'], 'batteries': ['buffer'],
    'macerator': ['start'], 'ore_washer': ['macerator'], 'thermal': ['ore_washer'], 'centrifuge': ['thermal'],
    'electrolyzer': ['centrifuge'], 'aluminium': ['electrolyzer', 'ebf_power'], 'mv': ['aluminium', 'circuits'],
    'mixer': ['start'], 'soldering': ['mixer'], 'chem_reactor': ['soldering'], 'sulfuric': ['chem_reactor'],
    'invar': ['start'], 'coils': ['invar', 'mixer'], 'ebf': ['coils'], 'ebf_power': ['ebf'],
}, hubs={'start', 'ebf', 'mv'}, sats={
    'start': ['voltage', 'components', 'robot_arm'],
    'wiremill': ['fine_wire'],
    'bender': ['foil'],
    'assembler': ['emitter', 'cultivation', 'lava_maker'],
    'circuits': ['ae2', 'pipes', 'wand'],
    'turbine': ['combustion', 'gas_turbine', 'large_boiler', 'generator_array'],
    'buffer': ['transformer', 'charger'],
    'batteries': ['battery_alloy', 'canner'],
    'ore_washer': ['chemical_bath'],
    'centrifuge': ['glue'],
    'electrolyzer': ['water_split', 'aluminium_ore'],
    'mixer': ['tin_alloy'],
    'soldering': ['extractor'],
    'chem_reactor': ['solidifier'],
    'ebf': ['maintenance', 'muffler', 'buses'],
    'ebf_power': ['ebf_steel'],
})


# ================================================================ more to do in LV (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'lv/' + key)
    x.deps += [d if '/' in d else 'lv/' + d for d in deps if (d if '/' in d else 'lv/' + d) not in x.deps]


# ---------------------------------------------------------------- the rest of the LV machines
q('e_furnace', 'Electric Furnace', ['gtceu:lv_electric_furnace'], [
    'Smelts faster than a furnace and takes power instead of fuel. The first machine most people build after the generator.',
], ['start'])
q('alloy_smelter', 'Alloy Smelter', ['gtceu:lv_alloy_smelter'], [
    'Same recipes as the steam one, faster, and it does not need venting. It also shapes metal with a &emold&r in the second slot.',
], ['start'])
q('compressor', 'Compressor', ['gtceu:lv_compressor'], [
    'Plates into dense plates, dust into blocks, and a handful of things nothing else makes.',
], ['start'], kind='opt')
q('forge_hammer', 'Forge Hammer', ['gtceu:lv_forge_hammer'], [
    'Crushes raw ore without the byproducts of a macerator, turns stone to gravel to sand, and hammers ingots into plates when the bender is busy.',
], ['start'], kind='opt')
q('cutter', 'Cutting Machine', ['gtceu:lv_cutter'], [
    'Cuts blocks into plates and rods into bolts. It needs &ewater&r; lubricant makes it much faster. The saw can retire.',
], ['lathe'])
q('forming_press', 'Forming Press', ['gtceu:lv_forming_press'], [
    'Presses items into shapes with a mold. Mostly used later, but AE2\'s printed circuits can be pressed here.',
], ['bender'], kind='opt')
q('polarizer', 'Polarizer', ['gtceu:lv_polarizer'], [
    'Magnetises iron and steel rods with power instead of redstone. Motors get cheaper.',
], ['wiremill'])
q('packer', 'Packer', ['gtceu:lv_packer'], [
    'Four small dusts or nine tiny dusts into one dust. Ore processing leaves a lot of both behind.',
], ['macerator'], kind='opt')
q('sifter', 'Sifter', ['gtceu:lv_sifter'], [
    'Sifts purified gem ore into gems of every quality instead of one dust. Diamond, emerald, sapphire, ruby: all worth sifting.',
], ['ore_washer'])
q('separator', 'Electromagnetic Separator', ['gtceu:lv_electromagnetic_separator'], [
    'Pulls the magnetic part out of purified dust: iron, nickel or neodymium from ores that carry it. The byproduct slot of the ore tells you.',
], ['thermal'], kind='opt')
q('autoclave', 'Autoclave', ['gtceu:lv_autoclave'], [
    'Grows gems from dust in water. The way to turn the gem dust your macerator makes back into something useful.',
], ['mixer'], kind='opt')
q('distillery', 'Distillery', ['gtceu:lv_distillery'], [
    'Takes one product out of a fluid, picked by the programmed circuit. A distillation tower takes all of them at once, but that is two tiers away.',
], ['chem_reactor'])
q('fluid_heater', 'Fluid Heater', ['gtceu:lv_fluid_heater'], [
    'Heats fluids. Mostly water into steam for a chemistry step or two.',
], ['chem_reactor'], kind='opt')
q('brewery', 'Brewery and Fermenter', ['gtceu:lv_brewery', 'gtceu:lv_fermenter'], [
    'Plants into &eBiomass&r in the brewery, biomass into fermented biomass in the fermenter. This is the road to plastic and to ethanol in MV.',
    'Lubricant is brewed here as well: redstone or talc in oil or creosote.',
], ['mixer'])
q('arc_furnace', 'Arc Furnace', ['gtceu:lv_arc_furnace'], [
    'Melts parts back into ingots with a little &eoxygen&r, and makes wrought iron and annealed copper outright.',
    'Old tools, wrong machines, spare cables: all of it comes back as metal.',
], ['electrolyzer'])
q('rock_crusher', 'Rock Crusher', ['gtceu:lv_rock_crusher'], [
    'Cobblestone and stone forever, with water on one side and lava on the other. Feed it to a macerator for endless stone dust.',
], ['start'], kind='opt')
q('gas_collector', 'Gas Collector', ['gtceu:lv_gas_collector'], [
    'Collects &eair&r. Air in a centrifuge gives nitrogen and oxygen, and oxygen speeds up many blast furnace recipes.',
], ['centrifuge'])
q('circuit_assembler', '&eCircuit Assembler', ['gtceu:lv_circuit_assembler'], [
    'Circuits made here cost less than the hand recipe and give two instead of one for the first tier.',
    'It needs &esoldering alloy&r (or tin, at double the amount) as a fluid.',
], ['circuits', 'soldering'], kind='main')

# ---------------------------------------------------------------- automation without AE2
q('conveyor', 'Conveyor Module', ['gtceu:lv_conveyor_module'], [
    'Stick it on the side of a machine as a &ecover&r and it moves items in or out through that side. Right-click it with a screwdriver to open its settings.',
], ['components'])
q('regulator', 'Fluid Regulator', ['gtceu:lv_fluid_regulator'], [
    'A pump that moves an exact amount per tick. For recipes that must not be overfed.',
], ['components'], kind='opt')
q('filters', 'Filters', ['gtceu:item_filter', 'gtceu:fluid_filter'], [
    'Put a filter in a conveyor, a pump or a robot arm and it only moves what is on the list.',
    'One conveyor with a filter per product sorts a macerator\'s four outputs into four chests.',
], ['conveyor'])
q('controller', 'Machine Controller', ['gtceu:machine_controller_cover'], [
    'A cover that turns the machine on or off from a redstone signal. With an &eActivity Detector&r on another machine you can make one wait for the other.',
], ['conveyor'], kind='opt')
q('prog_circuit', 'Programmed Circuit', ['gtceu:programmed_circuit'], [
    'Many machines have several recipes for the same input. The number on the &eProgrammed Circuit&r picks one; click the circuit slot of the machine to set it without crafting anything.',
], ['assembler'], kind='tip')
q('output_sides', 'Which Side Is Out?', ['check'], [
    'Every electric machine has one output side, marked with a dot. A wrench moves it to another side, and the machine can be set to push items and fluids out through it on its own (the buttons are in its screen).',
    'A machine that stops for no reason usually has a full output slot.',
], ['start'], kind='tip')
q('super_chest', 'Super Chest', ['gtceu:lv_super_chest'], [
    'One item type, a great many of it. The right home for cobblestone, dusts and anything else that comes in thousands.',
], ['start'], kind='opt')
q('super_tank', 'Super Tank', ['gtceu:lv_super_tank'], [
    'The same idea for one fluid. It keeps its contents when you wrench it.',
], ['start'], kind='opt')
q('fluid_cell', 'Fluid Cells', ['gtceu:fluid_cell*8'], [
    'A cell holds one bucket and stacks. Most machines fill and empty them in their fluid slots.',
], ['bender'], kind='opt')

# ---------------------------------------------------------------- powered tools and finding ore
q('drill', 'Electric Drill', ['gtceu:lv_steel_drill'], [
    'Mines a 3x3 and runs on a battery in its recipe: recharge it in a charger or a battery buffer.',
], ['batteries'])
q('power_wrench', 'Electric Wrench', ['gtceu:lv_steel_wrench'], [
    'No durability, only charge. One of these and the hand wrench is done.',
], ['batteries'], kind='opt')
q('prospector', '&eProspector', ['gtceu:prospector.lv'], [
    'Right-click to scan the chunks around you and see every ore vein on a map. Charge it first.',
    'This is how you find the veins the surface rocks did not show you.',
], ['batteries', 'circuits'], kind='main')
q('terminal', 'Terminal', ['gtceu:terminal'], [
    'Right-click a multiblock controller with the &eTerminal&r to build its structure from your inventory.',
], ['circuits'], kind='opt')

# ---------------------------------------------------------------- machines that work on their own
q('e_miner', 'Electric Miner', ['gtceu:lv_miner'], [
    'The steam miner\'s bigger brother: a wider square and faster. It still only takes ores.',
], ['components'])
q('e_pump', 'Pump', ['gtceu:lv_pump'], [
    'Stands over a lake of water, lava or oil and drains it into its tank. Lava from the Nether this way feeds a boiler for days.',
], ['components'], kind='opt')
q('fisher', 'Fisher', ['gtceu:lv_fisher'], [
    'String in, fish and treasure out, over a pool of water. Food without a farm.',
], ['components'], kind='opt')
q('item_collector', 'Item Collector', ['gtceu:lv_item_collector'], [
    'Picks up dropped items in a radius. Under a mob farm or a tree farm it replaces a field of hoppers.',
], ['components'], kind='opt')
q('block_breaker', 'Block Breaker', ['gtceu:lv_block_breaker'], [
    'Breaks the block in front of it. With a rock crusher or a cobblestone generator, an endless supply of whatever that makes.',
], ['components'], kind='opt')

# ---------------------------------------------------------------- chemistry starts here
q('salt', 'Salt', ['gtceu:salt_dust*16'], [
    'Electrolyze salt for &esodium&r and &echlorine&r. Sodium goes into batteries, chlorine into nearly all the chemistry ahead.',
], ['electrolyzer'])
q('naoh', 'Sodium Hydroxide', ['gtceu:sodium_hydroxide_dust*4'], [
    'Sodium and water. A base you will keep needing: the first use is cleaning aluminium ore in MV.',
], ['salt', 'chem_reactor'], kind='opt')
q('oxygen', 'Oxygen', ['gtceu:oxygen_bucket'], [
    'From air in a centrifuge or from water in an electrolyzer. Steel and many other blast furnace recipes take oxygen and run much faster for it.',
], ['gas_collector'])
also('ebf_steel', 'oxygen')
q('lithium_battery', 'Better Batteries', ['gtceu:lv_lithium_battery'], [
    'Sodium is the cheapest battery. &eCadmium&r holds more and &elithium&r more again, for the same hull.',
], ['batteries'], kind='opt')
q('annealed', 'Annealed Copper', ['gtceu:annealed_copper_ingot*8'], [
    'Copper smelted with oxygen in the arc furnace. It carries a little more than copper and loses less.',
], ['arc_furnace'], kind='opt')
q('frames', 'Frames', ['gtceu:steel_frame*4'], [
    'Four rods and a wrench. Frames are climbable, cheap, and part of many multiblocks.',
], ['lathe'], kind='opt')
q('hatches', 'Fluid Hatches', ['gtceu:lv_input_hatch', 'gtceu:lv_output_hatch'], [
    'Buses carry items in and out of a multiblock; &ehatches&r carry fluids. The blast furnace wants an input hatch as soon as you feed it oxygen.',
], ['buses'])
also('ebf_steel', 'hatches')
