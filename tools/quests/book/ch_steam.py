"""Tier 0.5 - Steam Age: boilers, the first machines, ore processing, steel, the hydraulic line, steam multiblocks,
and the parts for the first LV machine."""
from fw import Chapter

C = Chapter('steam', '&6Tier 0.5 - Steam Age', 'gtceu:lp_steam_solid_boiler', '', 1,
            'Steam to electricity', 'steam', 'pick_steam', xp_base=8)
q = C.q

# ---------------------------------------------------------------- the boiler
q('boiler', '&6&lStoke the Boiler', ['gtceu:lp_steam_solid_boiler', 'minecraft:water_bucket'], [
    'Your boiler burns solid fuel and turns water into &esteam&r. Every machine in this chapter runs on it.',
    '&cGive it water before you light it&r, and never let it run dry. A boiler that heats up empty and then gets water &cexplodes&r. Keep its water supply automatic and you will never think about it again.',
    'A boiler takes a while to reach full temperature. The gauge in its window shows heat, water and steam.',
], ['stone/to_steam'], at=(0, 0), kind='hub')
q('explode', 'Why Boilers Explode', ['check'], [
    'The rule: &chot + dry + water = boom&r. If a boiler runs out of water while hot, break it (or let it cool completely) before refilling.',
    'Two boilers feeding one set of machines is safer than one: if one stops, the other keeps going.',
], ['boiler'], at=(-2, -1.5), kind='tip')
q('water', 'Infinite Water', ['check'], [
    'Two water source blocks next to each other make an infinite source. A pipe or a bucket can pull from it forever, but only one bucket at a time.',
    'For a real supply build the &ePrimitive Pump&r below, or put your boiler right next to a big water source and pipe it in with a wooden or bronze fluid pipe.',
], ['boiler'], at=(-2, 0), kind='tip')
q('pump', 'Primitive Pump', ['gtceu:primitive_pump'], [
    'A small treated-wood multiblock that pulls water out of the biome it stands in. It works best in a river or ocean biome. Check its preview for the shape.',
    'One pump feeds several small boilers. It never runs out.',
], ['water', 'stone/treated'], at=(-2, 1.5))
q('drum', 'Bronze Drum', ['gtceu:bronze_drum'], [
    'A portable tank: break it and it keeps its fluid. Good for carrying creosote, water or steam. A wrench toggles auto-output from the bottom.',
], ['boiler'], at=(-3.5, 1.5), kind='opt')

# ---------------------------------------------------------------- the first machines
q('vent', 'Mind the Exhaust', ['check'], [
    'Every steam machine has an &eexhaust&r side that vents used steam. It must face air. A machine with a blocked exhaust stops working and says so when you look at it.',
    'Steam machines run any recipe up to &eLV&r (32 EU/t), so the recipe viewer is honest with you: if a recipe says LV or below, steam can do it.',
], ['boiler'], at=(2, 1.5), kind='tip')
q('macerator', '&eSteam Macerator', ['gtceu:lp_steam_macerator'], [
    'Two diamonds, pipes, pistons and a Bronze Machine Casing. It grinds raw ore into &ecrushed ore&r, and almost anything else into dust.',
    'It has one output slot, so it throws away byproducts for now. That is fine: crushing your ore is what matters.',
    '> If you have no diamonds yet, the Diamond vein lives in deepslate at Y -55 to -30.',
], ['boiler', 'stone/deep'], at=(4, -1.5), kind='main')
q('hammer', 'Steam Forge Hammer', ['gtceu:lp_steam_forge_hammer'], [
    'Hammers &eone ingot into one plate&r. Your hand needs two. This machine pays for itself in a few minutes.',
    'It also crushes raw ore into crushed ore and stone into cobble, gravel and sand.',
], ['boiler'], at=(5.5, -1.5), kind='main')
q('compressor', 'Steam Compressor', ['gtceu:lp_steam_compressor'], [
    'Presses dust into plates and blocks, and makes &eCompressed Fireclay&r: you cannot make firebricks without it, and firebricks make steel.',
], ['boiler'], at=(7, -1.5), kind='main')
q('extractor', 'Steam Extractor', ['gtceu:lp_steam_extractor'], [
    'Pulls things out of things. Most important now: &eSticky Resin into Raw Rubber Pulp&r. Rubber leaves, saplings and logs work too.',
], ['boiler'], at=(8.5, -1.5), kind='main')
q('alloy', '&eSteam Alloy Smelter', ['gtceu:lp_steam_alloy_smelter'], [
    'The busiest machine of the Steam Age. It makes &ebronze straight from ingots&r, red alloy from copper and redstone, invar, potin, stronze, glass tubes, and rubber from raw pulp and sulfur.',
    'Make two. Seriously.',
], ['boiler', 'stone/casing'], at=(4, 1.5), kind='main')
q('furnace', 'Steam Furnace', ['gtceu:lp_steam_furnace'], [
    'A furnace that runs on steam. Not faster than a good furnace, but it needs no fuel of its own.',
], ['boiler', 'stone/casing'], at=(5.5, 1.5))
q('crusher', 'Steam Rock Crusher', ['gtceu:lp_steam_rock_crusher'], [
    'With lava on one side and water on the other, it makes cobblestone, stone, basalt and more out of nothing. Endless stone for the Stone SuperHeater later.',
], ['macerator'], at=(7, 1.5), kind='opt')

# ---------------------------------------------------------------- ore processing
q('ores', '&eDoubling Your Ore', ['gtceu:crushed_chalcopyrite_ore*16'], [
    'From now on, &edo not smelt raw ore&r. The macerator turns one raw ore into crushed ore; from there each step gives more.',
    'The simple path: crushed ore &7->&r wash it &7->&r purified ore &7->&r macerate &7->&r dust &7->&r smelt. Two ingots per raw ore instead of one, plus a chance at byproducts.',
], ['macerator'], at=(6, 5), kind='hub')
q('wash', 'Washing by Hand', ['gtceu:purified_chalcopyrite_ore*8'], [
    'Right-click a &ewater cauldron&r with crushed ore (or impure dust) to wash it. Each wash uses some water. Purified ore macerates into clean dust.',
    'Later the Ore Washer does this with byproducts. For now a few cauldrons next to a water source are plenty.',
], ['ores'], at=(4, 4))
q('dust', 'Clean Dust', ['gtceu:chalcopyrite_dust*16'], [
    'Macerated purified ore. Smelt it, or keep it as dust for the alloy smelter.',
    '> Chalcopyrite dust smelts to copper. Each ore\'s dust smelts into its main metal: hover over it in the recipe viewer.',
], ['wash'], at=(4, 6))
q('forge_ore', 'The Lazy Way', ['check'], [
    'The steam Forge Hammer also turns raw ore into crushed ore, just slower and without the macerator\'s chance of bonus. Feed it with a hopper while you sleep.',
], ['ores', 'hammer'], at=(8, 4), kind='tip')
q('byproduct', 'Byproducts Matter', ['check'], [
    'Every ore has &ebyproducts&r: Chalcopyrite gives pyrite and cobalt, Galena gives silver and sulfur, Goethite gives zinc, Garnierite gives nickel... Look at any ore in the recipe viewer to see them.',
    'Steam machines miss most of them. The LV Macerator, Ore Washer, Thermal Centrifuge and Centrifuge get them all. That is one of the big reasons to go electric.',
], ['ores'], at=(8, 6), kind='tip')

# ---------------------------------------------------------------- steel
q('fireclay', 'Fireclay', ['gtceu:fireclay_dust*16'], [
    'One brick dust (mortar a brick block) and one clay dust (mortar a clay block) make &eFireclay Dust&r in the grid.',
    'The steam Compressor presses it into Compressed Fireclay; smelting that gives a firebrick, and four make a &eFirebricks&r block.',
], ['compressor'], at=(10.5, -3))
q('firebricks', 'Firebricks', ['gtceu:firebricks*16'], [
    'The Primitive Blast Furnace is built from firebricks. The Brick Kiln fires them in bulk if you built one.',
], ['fireclay'], at=(12, -3))
q('pbf', '&6Primitive Blast Furnace', ['gtceu:primitive_blast_furnace'], [
    'A tall firebrick multiblock that turns &eiron and carbon into steel&r, slowly and with no fuel besides the carbon in the recipe. Check its preview for the shape.',
    'It is slow. Build &etwo or three&r and keep them all busy.',
], ['firebricks'], at=(14, -1.5), kind='hub')
q('pbf_tips', 'Faster Steel', ['check'], [
    'Recipe times in the Primitive Blast Furnace:',
    'Iron + coal: 90 seconds. Iron + &ecoke&r: 75 seconds. &eWrought iron&r + coal: 40 seconds. Wrought iron + &ecoke&r: &a30 seconds&r.',
    'Coke also leaves ashes instead of dark ashes. Wrought iron is iron nuggets smelted in a furnace: tedious but worth it if you are short on time.',
    '> Iron blocks with coal blocks work too, for big batches.',
], ['pbf'], at=(14, 1), kind='tip')
q('steel', '&eSteel', ['gtceu:steel_ingot*32'], [
    'Steel: the metal of LV. You will need it for high pressure steam machines, every steam multiblock, the mold for glass tubes, the bolts in vacuum tubes and the LV machine casing.',
    'Keep the furnaces running. You will never have enough steel.',
], ['pbf'], at=(16, -1.5), kind='main')
q('hp_boiler', 'High Pressure Boilers', ['gtceu:hp_steam_solid_boiler'], [
    'Steel boilers make twice the steam of bronze ones from the same fuel. Replace your bronze boiler once you have spare steel.',
], ['steel'], at=(17.5, -3))
q('hp_machines', '&eHigh Pressure Machines', ['gtceu:hp_steam_macerator', 'gtceu:hp_steam_alloy_smelter'], [
    'Steel versions of every steam machine. Here they are much better than usual: high pressure machines run &eall ULV recipes in half the time&r, for three times the steam.',
    'If your boilers keep up, swap your busiest machines (macerator, alloy smelter, forge hammer) first.',
], ['hp_boiler'], at=(19, -3), kind='main')
q('mold', 'Molds', ['gtceu:empty_mold', 'gtceu:ball_casting_mold'], [
    'Four steel plates, a hammer and a file make an &eEmpty Mold&r. Hammer it into shapes: the &eBall&r mold makes glass tubes in the alloy smelter, the &eIngot&r mold casts ingots, the &ePlate&r mold makes rubber sheets.',
], ['steel'], at=(17.5, 0))

# ---------------------------------------------------------------- power
q('silver', 'Silver', ['gtceu:silver_plate*3'], [
    'The Galena vein has silver ore of its own, and processing galena gives silver as a byproduct. Three plates for each solar boiler.',
], ['ores'], at=(-2, 5))
q('solar', 'Solar Boiler', ['gtceu:lp_steam_solar_boiler'], [
    'Glass, three silver plates and a bricked bronze casing. It makes steam from &esunlight&r with no fuel at all, only water. Slower than a coal boiler, and only by day.',
    'A row of them is the free power of the Steam Age.',
], ['silver'], at=(-2, 6.5))
q('liquid', 'Liquid Boiler', ['gtceu:lp_steam_liquid_boiler'], [
    'Burns &elava&r or &ecreosote&r. Lava is the best: one bucket lasts a long time. Creosote is free from your coke ovens.',
], ['boiler'], at=(-3.5, 5))
q('cactus', 'Cactus Wonder', ['gtna:steam_cactus_wonder'], [
    'A boiler multiblock wrapped in cacti that burns coal, charcoal and coke (blocks too) into steam. The &eHydraulic Regulator&r in its recipe comes later in this chapter.',
], ['regulator'], at=(-3.5, 6.5), kind='opt')
q('large_solar', '&eLarge Steam Solar Boiler', ['gtna:large_steam_solar_boiler'], [
    'A field of solar heat collector cells around a High Pressure Solar Boiler. Each sunlit cell makes &e900 mB of steam&r. A big one runs a whole steam factory for free all day.',
    'Cells are tinted glass, tiny steel pipes and double silver plates. It also needs a Hydraulic Pump and a Precision Steam Component.',
], ['solar', 'precision', 'hydraulic_pump'], at=(-2, 8.5), kind='main')

# ---------------------------------------------------------------- alloys
q('alloys', '&eAlloys of the Steam Age', ['gtceu:invar_ingot*8', 'gtceu:potin_ingot*8'], [
    'Steam multiblocks are built from alloys. Most can be mixed from dusts in the grid and smelted, or made in the alloy smelter:',
    '&ePotin&r: 6 copper, 2 tin, 1 lead. For the Steam Grinder\'s gears.',
    '&eInvar&r: 2 iron, 1 nickel. For the Steam Oven\'s gears, and many machines later.',
], ['alloy', 'stone/galena', 'stone/nickel'], at=(10, 4), kind='hub')
q('stronze', 'Stronze', ['gtna:stronze_ingot*16'], [
    'Bronze and steel together in the alloy smelter. Stronze plates, pipes and gears make the frames of the advanced steam multiblocks: the Stone SuperHeater, the large steam machines and the Steam Cracker.',
], ['alloys', 'steel'], at=(12, 3))
q('breel', 'Breel', ['gtna:breel_ingot*8'], [
    'Two bronze dust and one steel dust make three &eBreel dust&r in the grid. Breel pipes and plates go into the large steam machines and the Steam Manufacturer.',
], ['alloys', 'steel'], at=(12, 5))
q('brass', 'Brass', ['gtceu:brass_ingot*8'], [
    'Three copper and one zinc. Zinc comes from Sphalerite (Nether sulfur vein) and is a byproduct of several ores. Brass makes the Industrial Steam Casings.',
], ['alloys'], at=(10, 6.5), kind='opt')
q('clay_compound', 'Clay Compound', ['gtna:clay_compound_ingot*16'], [
    'Clay dust, stone dust and flint dust fired in the &eBrick Kiln&r. Clay compound plates and gears are the body of every hydraulic part except the motor and pump.',
], ['alloys', 'stone/kiln'], at=(12, 7))

# ---------------------------------------------------------------- hydraulics
q('motor', '&6Hydraulic Motor', ['gtna:hydraulic_motor'], [
    'The steam-powered motor. Two bronze gears, four tiny bronze fluid pipes, two iron rods and an iron rotor.',
    'Every hydraulic part is built around it: the piston, arm, conveyor and pump, and through them every big steam multiblock. Make several.',
], ['alloy', 'hammer'], at=(16, 4), kind='hub')
q('piston', 'Hydraulic Piston', ['gtna:hydraulic_piston'], [
    'Clay compound plates, an iron rotor, rods, a tiny bronze pipe, a bronze gear and a motor.',
], ['motor', 'clay_compound'], at=(14.5, 6))
q('arm', 'Hydraulic Arm', ['gtna:hydraulic_arm'], [
    'Two motors, a piston, a clay compound gear, plates and rods. The Steam Manufacturer needs nine of them across its casings, so start a stockpile.',
], ['piston'], at=(14.5, 7.5))
q('conveyor', 'Hydraulic Conveyor', ['gtna:hydraulic_conveyor'], [
    'Six rubber sheets, two motors and a clay compound gear. Your rubber has to come first.',
], ['motor', 'clay_compound', 'rubber'], at=(16, 7.5))
q('hydraulic_pump', 'Hydraulic Pump', ['gtna:hydraulic_pump'], [
    'Four steel screws, four small bronze fluid pipes and a steel rotor. It goes into the Large Steam Solar Boiler, the Steam Woodcutter, the Steam Cracker and the Primitive Distillation Tower.',
], ['motor', 'steel'], at=(17.5, 6))
q('regulator', 'Hydraulic Regulator', ['gtna:hydraulic_regulator'], [
    'Iron plates, copper fluid pipes, small steel springs and a small bronze gear. It controls the flow in the Cactus Wonder.',
], ['motor', 'steel'], at=(17.5, 7.5))
q('precision', '&ePrecision Steam Component', ['gtna:precision_steam_component*4'], [
    'Bronze rods and gears, small bronze gears, small bronze and steel springs. The "circuit" of the Steam Age: almost every advanced steam multiblock needs one or more.',
], ['motor', 'steel'], at=(18, 4), kind='main')

# ---------------------------------------------------------------- steam multiblocks
q('multis', '&6&lSteam Multiblocks', ['gtceu:steam_input_hatch', 'gtceu:steam_input_bus', 'gtceu:steam_output_bus'], [
    'Steam multiblocks process many items at once. They take steam through a &eSteam Input Hatch&r and items through &eSteam Input/Output Buses&r. Normal GregTech hatches do not fit them.',
    'They run up to &e8 recipes in parallel&r, at 1.5 times the duration each. Eight times the output of a single machine for the space of a small room.',
], ['precision'], at=(22, 4), kind='hub')
q('grinder', '&eSteam Grinder', ['gtceu:steam_grinder'], [
    'A multiblock macerator: a steam macerator ringed with steam casings and two &ePotin gears&r. It needs at least 14 bronze casings to form.',
    'Your ore crushing problem, solved for the rest of the Steam Age.',
], ['multis', 'alloys'], at=(20.5, 2))
q('oven', 'Steam Oven', ['gtceu:steam_oven'], [
    'A multiblock furnace: a steam furnace, &eInvar gears&r and bronze firebox casings. The steam hatch must go on the bottom layer, only one.',
], ['multis', 'alloys'], at=(22, 2))
q('superheater', '&eStone SuperHeater', ['gtna:stone_superheater'], [
    'Melts &estone, cobblestone, granite or diorite into lava&r: 100 mB per block. Pair it with a Rock Crusher or a Steam Cobbler and you have endless lava for liquid boilers.',
    'Built around a &eStronze-Wrapped Casing&r, stronze plates, hydraulic motors and a Precision Steam Component.',
], ['multis', 'stronze'], at=(23.5, 2), kind='main')
q('large_furnace', 'Large Steam Furnace', ['gtna:large_steam_furnace'], [
    'An upgraded steam oven built from precision components, stronze pipes and tiny breel pipes. More parallel, less fuss.',
], ['multis', 'breel', 'stronze'], at=(20.5, 6))
q('large_crusher', 'Large Steam Crusher', ['gtna:large_steam_crusher'], [
    'Four Steam Grinders fused together with stronze. Expensive, and worth it once you process whole veins at a time.',
], ['grinder', 'stronze'], at=(22, 6))
q('large_compressor', 'Large Steam Compressor', ['gtna:large_steam_compressor'], [
    'Hydraulic pistons around a steam compressor. Plates and blocks in bulk.',
], ['multis', 'piston', 'stronze'], at=(23.5, 6))
q('thermal', 'Large Steam Thermal Centrifuge', ['gtna:large_steam_thermal_centrifuge'], [
    'A thermal centrifuge on steam. It separates purified ore into refined dust and gets the byproducts the steam macerator throws away.',
], ['multis', 'breel'], at=(20.5, 7.5), kind='opt')
q('bending', 'Large Steam Bending Machine', ['gtna:large_steam_bending'], [
    'Bends ingots into plates (one ingot, one plate) and plates into foil, wires and more, in parallel.',
], ['multis', 'conveyor'], at=(22, 7.5), kind='opt')
q('manufacturer', '&6Steam Manufacturer', ['gtna:steam_manufacturer'], [
    'Builds &ehydraulic parts by machine&r: motors, pistons, arms, conveyors, pumps, regulators and the parts for the &eSteam Elevator&r and its modules. Much cheaper than by hand.',
    'Hydraulic arms, two Hydraulic Assembler Casings (three more arms each), a double stronze plate, steel gearbox casings and a hydraulic conveyor. A real project. The payoff is the rest of the gtna steam line.',
], ['multis', 'arm', 'conveyor'], at=(25.5, 4), kind='main')
q('cobbler', 'Steam Cobbler', ['gtna:steam_cobbler'], [
    'A multiblock rock crusher: water and lava inside make cobblestone and stone in bulk. Food for the SuperHeater.',
], ['multis', 'clay_compound'], at=(23.5, 7.5), kind='opt')
q('woodcutter', 'Steam Woodcutter', ['gtna:steam_woodcutter'], [
    'A tree farm in a box: put a sapling in and get logs, saplings and leaves. A rubber sapling gives rubber logs and sticky resin too. Check the recipe viewer for the power its recipes need before you build it.',
], ['multis', 'hydraulic_pump'], at=(25, 7.5), kind='opt')
q('infernal', 'Infernal Coke Oven', ['gtna:infernal_coke_oven'], [
    'A coke oven wrapped in nether bricks, breel and stronze: the same coke, charcoal and creosote as a multiblock you can feed with buses and hatches.',
], ['multis', 'breel', 'nether'], at=(25, 1.5), kind='opt')
q('big_bus', 'Huge Steam Input Bus', ['gtna:huge_steam_input_bus'], [
    'Eight bronze crates around a steam input bus. Holds a whole lot more, so you fill your multiblocks less often.',
], ['multis'], at=(26.5, 6), kind='opt')

# ---------------------------------------------------------------- the road to LV
q('nether', 'Into the Nether', ['dim:minecraft:the_nether'], [
    'You need &esulfur&r for rubber, and the easiest sulfur is in the Nether: the &eSulfur vein&r (Sulfur, Pyrite, Sphalerite) sits at Y 10 to 30 in netherrack. Sphalerite gives &ezinc&r too.',
    'Bring fire protection if you can. The Nether is not friendly.',
    '> Pyrite and galena also give some sulfur when processed, and the Mystical Agriculture Sulfur Essence works if you have it.',
], ['steel'], at=(27.5, -4.5))
q('sulfur', 'Sulfur', ['gtceu:sulfur_dust*16'], [
    'Macerate sulfur ore. You need one sulfur dust per rubber ingot.',
], ['nether'], at=(29, -4.5))
q('rubber', '&eRubber', ['gtceu:rubber_plate*16'], [
    'Extract sticky resin into &eRaw Rubber Pulp&r, alloy-smelt it with sulfur into &eRubber Ingots&r, and alloy-smelt those with a plate mold into &eRubber Sheets&r.',
    'Rubber insulates every cable you will make in LV, and it is in the conveyor of every LV machine that moves items.',
], ['sulfur'], at=(30.5, -4.5), kind='main')
q('cable', 'Tin Cable', ['gtceu:tin_single_cable*16'], [
    'Tin plate and a wire cutter make tin wire; wire and a rubber sheet in the grid make &e1x Tin Cable&r: the LV cable.',
    'LV cable carries 32 EU/t per amp. Tin loses a little on every block, which is fine over short runs.',
], ['rubber', 'stone/cutter'], at=(32, -4.5))
q('red_alloy', 'Red Alloy Cable', ['gtceu:red_alloy_single_cable*3'], [
    'Red alloy from the alloy smelter, wired and insulated. Three of these go into every Basic Electronic Circuit.',
], ['rubber'], at=(30.5, -3))
q('glass_tube', 'Glass Tubes', ['gtceu:glass_tube*2'], [
    'Hammer glass into glass dust, then alloy-smelt it with the &eBall mold&r. Vacuum tubes need them.',
], ['mold', 'alloy'], at=(27.5, -1.5))
q('vacuum', '&eVacuum Tubes', ['gtceu:vacuum_tube*4'], [
    'A glass tube, two steel bolts and three copper wires, by hand. Two go into every Basic Electronic Circuit.',
    '> Once you have an LV Assembler it makes them far cheaper, with red alloy instead of most of the wire.',
], ['glass_tube', 'steel'], at=(29, -1.5), kind='main')
q('board', 'Circuit Boards', ['gtceu:resin_printed_circuit_board*2'], [
    'Macerate logs into wood pulp and compress it into &eWood Planks&r (the GregTech item, not vanilla planks). A plank and two sticky resin make a Resin Circuit Board; eight copper wires around it make the &eResin Printed Circuit Board&r.',
], ['ores', 'compressor'], at=(27.5, 0))
q('resistor', 'Resistors', ['gtceu:resistor*4'], [
    'Sticky resin, paper, coal dust and copper wire. Two go into every Basic Electronic Circuit.',
], ['board'], at=(29, 0))
q('circuit', '&6Basic Electronic Circuit', ['gtceu:basic_electronic_circuit*4'], [
    'Two resistors, two vacuum tubes, a steel plate, a printed board and three red alloy cables. Your first &eLV circuit&r.',
    'Every LV machine needs at least one, and the &eLarge Bronze Boiler&r and &eSteam Lava Maker&r need several. Make a stack of parts at once.',
], ['vacuum', 'resistor', 'red_alloy'], at=(31, 0), kind='hub')
q('magnet', 'Magnetic Iron', ['gtceu:magnetic_iron_rod*2'], [
    'An iron rod and four redstone dust make a magnetic iron rod: the heart of the &eLV Electric Motor&r.',
], ['steel'], at=(27.5, 1.5))
q('lv_motor', 'LV Electric Motor', ['gtceu:lv_electric_motor*2'], [
    'Two tin cables, four copper wires, two iron rods and a magnetic iron rod. Motors make pumps, pistons, conveyors and robot arms: the components of every LV machine.',
], ['magnet', 'cable'], at=(29, 1.5))
q('lv_casing', 'LV Machine Casing', ['gtceu:lv_machine_casing'], [
    'Eight steel plates and a wrench.',
], ['steel'], at=(32, 1.5))
q('hull', '&b&lElectricity', ['gtceu:lv_machine_hull'], [
    'Wrought iron plates, a steel plate, tin cables and the LV Machine Casing: the &bLV Machine Hull&r. Every LV machine is built on one.',
    'This is the end of the Steam Age, though not of steam: your boilers will power your first generator. Open &bTier 1 - LV&r.',
], ['circuit', 'lv_casing', 'lv_motor', 'stone/wrought'], at=(34, 0), kind='hub', bag='lv')

# ---------------------------------------------------------------- layout (kept apart from the content)
POS = {
    'boiler': (1, 0), 'explode': (-1, -1.5), 'water': (-1, 0), 'pump': (-1, 1.5), 'drum': (-2.5, 1.5), 'vent': (1, 2.2),
    'macerator': (3.5, -1.5), 'hammer': (5, -1.5), 'compressor': (6.5, -1.5), 'extractor': (8, -1.5),
    'alloy': (3.5, 1.5), 'furnace': (5, 1.5), 'crusher': (6.5, 1.5),
    'ores': (5.5, 5), 'wash': (3.5, 4.3), 'dust': (3.5, 5.8), 'forge_ore': (7.5, 4.3), 'byproduct': (7.5, 5.8),
    'silver': (1.5, 6.5), 'solar': (0, 6.5), 'liquid': (-1, 4.8), 'large_solar': (0, 8.5), 'cactus': (-1.5, 8.5),
    'fireclay': (9.5, -3), 'firebricks': (11, -3), 'pbf': (12.5, -1.5), 'pbf_tips': (12.5, 0.6), 'steel': (14.5, -1.5),
    'mold': (14.5, 0.6), 'hp_boiler': (16, -3), 'hp_machines': (17.7, -3),
    'alloys': (10.5, 5), 'stronze': (12.5, 4.2), 'breel': (12.5, 5.8), 'brass': (10.5, 7), 'clay_compound': (12.5, 7.6),
    'motor': (16, 5), 'piston': (14.5, 7.6), 'arm': (14.5, 9.1), 'conveyor': (16, 9.1), 'hydraulic_pump': (17.5, 7.6),
    'regulator': (17.5, 9.1), 'precision': (17.8, 4.2),
    'multis': (21.5, 5), 'grinder': (20, 3), 'oven': (21.5, 3), 'superheater': (23, 3), 'infernal': (24.5, 3),
    'large_furnace': (20, 7), 'large_crusher': (21.5, 7), 'large_compressor': (23, 7), 'thermal': (20, 8.5), 'bending': (21.5, 8.5),
    'cobbler': (23, 8.5), 'woodcutter': (24.5, 8.5), 'manufacturer': (24.7, 5), 'big_bus': (24.5, 7),
    'nether': (27, -4.5), 'sulfur': (28.5, -4.5), 'rubber': (30, -4.5), 'cable': (31.7, -4.5), 'red_alloy': (30, -3),
    'glass_tube': (27, -1.5), 'vacuum': (28.5, -1.5), 'board': (27, 0), 'resistor': (28.5, 0), 'circuit': (30.5, -0.5),
    'magnet': (27, 1.5), 'lv_motor': (28.5, 1.5), 'lv_casing': (31.7, 1.5), 'hull': (33.5, -0.5),
}
# cross-area links: the dependency still counts, the line is hidden so the page stays readable
HIDE = {'nether', 'cactus', 'large_solar', 'silver', 'infernal', 'conveyor', 'board', 'magnet', 'lv_casing', 'glass_tube',
        'precision', 'hydraulic_pump', 'regulator', 'stronze', 'breel', 'mold', 'hp_boiler', 'woodcutter'}
for _q in C.quests:
    k = _q.key.split('/', 1)[1]
    if k in POS: _q.x, _q.y = map(float, POS[k])
    if k in HIDE: _q.hide = True
