"""Welcome, Stone Age and Steam Age: written by hand."""
from core import *


def at(q, x, y):
    q.x, q.y = float(x), float(y)
    return q


def welcome():
    qs = [
        at(Q('w/start', '&6Welcome to Apex Ascension', ['check'],
             ['A GregTech pack from the first flint tool to the Forge of the Gods, with heroes, magic, space and a lot of monsters along the way.',
              'This book follows GregTech\'s voltage tiers. Each &eGregTech&r chapter is one tier: finish its gateway quest and the next tier opens. The &eside chapters&r (magic, storage, exploration, heroes...) unlock as your tier allows.',
              'Most quests give a &6loot bag&r of their tier: useful materials, never progression skips. Open them as you go.'],
             reward='stone', shape='gear', size=2.0), 0, 0),
        at(Q('w/book', 'How This Book Works', ['check'],
             ['&eChapters on the left&r: the GregTech chapters run top to bottom, Stone Age to OpV. Side chapters are grouped below them.',
              '&eBig quests&r are gateways and multiblocks. &eSmall quests&r without lines are optional: they show every machine, part and material of the tier so you can tick them off as you build.',
              'A quest with an item task completes as soon as the item is in your inventory. Nothing is taken from you.'],
             deps=['w/start'], xp=10), 2, -1.5),
        at(Q('w/recipes', 'Look It Up', ['check'],
             ['Press &eR&r on any item for its recipes and &eU&r for its uses. GregTech recipes show the voltage they need: you can only run a recipe in a machine of that tier or higher.',
              'Every multiblock has a &epreview&r in its uses page: it shows the shape, layer by layer, and which blocks can go where.'],
             deps=['w/start'], xp=10), 2, 0),
        at(Q('w/claim', 'Claim Your Land', ['check'],
             ['Open the map (&eM&r) and drag over chunks to claim them. Claimed chunks are protected from other players and explosions, and can be force-loaded so your machines keep running while you\'re away.'],
             deps=['w/start'], xp=10), 2, 1.5),
        at(Q('w/backpack', 'Somewhere to Put It', ['sophisticatedbackpacks:backpack'],
             'A backpack. GregTech has a lot of items: upgrade it as you go (iron, gold, diamond, netherite), and add pickup, magnet and crafting upgrades.',
             deps=['w/book'], xp=20), 4, -1.5),
        at(Q('w/compass', 'Find Your Way', ['naturescompass:naturescompass', 'explorerscompass:explorerscompass'],
             'The Nature\'s Compass finds any biome; the Explorer\'s Compass finds any structure. Both save a lot of wandering.',
             deps=['w/recipes'], xp=20), 4, 0),
        at(Q('w/grave', 'Death Is Not the End', ['check'],
             'When you die your items go into a grave where you fell. Walk back and open it to get everything back, armor and all.',
             deps=['w/claim'], xp=10), 4, 1.5),
        at(Q('w/ores', 'Where the Ores Are', ['check'],
             ['GregTech ores come in &eveins&r: big mixed clusters of 4 related ores, a few chunks apart. When you find one ore, dig around: the rest of the vein is close.',
              'Small surface rocks and indicator ores on the surface hint at the vein under them. Mine everything: you will need all of it.'],
             deps=['w/recipes'], xp=20), 6, 0),
        at(Q('w/to_stone', '&7On to the Stone Age', ['check'], 'Open the &7Stone Age&r chapter.', deps=['w/ores', 'w/backpack', 'w/grave'], xp=10, shape='hexagon'), 8, 0),
    ]
    return Ch('welcome', '&6Welcome', 'minecraft:nether_star', 'main', qs, subtitle='Start here', order=0)


def stone():
    qs = []

    def add(key, title, tasks, desc, x, y, deps=(), **kw):
        kw.setdefault('xp', 15)
        q = at(Q('s/' + key, title, tasks, desc, deps=['s/' + d for d in deps] if deps else ['w/start'] if key == 'wood' else [], **kw), x, y)
        qs.append(q)
        return q

    add('wood', 'Punch a Tree', ['minecraft:oak_log'], 'Every pack starts here. Any log works.', 0, 0, shape='circle')
    add('flint', 'Flint', ['minecraft:flint'], 'Dig gravel for flint. Flint tools and the &eFlint Mortar&r come first: the mortar grinds things into dust by hand.', 1.5, 0, ['wood'])
    add('mortar', 'Flint Mortar', ['gtceu:flint_mortar'], 'Grinds items into dust in the crafting grid. Copper and tin dust from the mortar make your first bronze.', 3, 0, ['flint'])
    add('copper', 'Copper and Tin', ['gtceu:crushed_copper_ore', 'gtceu:tin_ingot'],
        'Find copper and tin veins. Ores give &ecrushed ore&r when hammered, or raw ore you can smelt.', 3, -1.5, ['flint'])
    add('bronze', 'Bronze', ['gtceu:bronze_ingot'],
        '3 copper dust + 1 tin dust make 4 bronze dust; smelt it. Bronze is the metal of the whole Steam Age.', 4.5, -0.75, ['mortar', 'copper'], shape='hexagon', reward='stone')
    add('hammer', 'Hammer', ['gtceu:iron_hammer', 'gtceu:bronze_hammer'],
        'GregTech tools are crafting tools: a hammer turns ingots into plates and ores into crushed ore in the grid. Iron or bronze, either works.', 6, -1.5, ['bronze'])
    add('file', 'File', ['gtceu:iron_file'], 'Files make rods, bolts and rings in the grid.', 6, -0.5, ['bronze'], hide_lines=True)
    add('wrench', 'Wrench', ['gtceu:iron_wrench', 'gtceu:bronze_wrench'],
        'The wrench picks up machines (break them with it, or they drop scrap) and rotates their faces. Shift-right-click a side to set the output.', 6, 0.5, ['bronze'], hide_lines=True)
    add('saw', 'Saw', ['gtceu:iron_saw'], 'Saws cut logs into far more planks and make wooden plates.', 6, 1.5, ['bronze'], hide_lines=True)
    add('plates', 'Plates', ['gtceu:bronze_plate', 'gtceu:wrought_iron_plate'], 'Almost every early recipe takes plates. Hammer two ingots into one plate now; the Forge Hammer and the Bender do it better later.', 7.5, -0.75, ['hammer'])

    add('clay', 'Compressed Clay', ['gtceu:compressed_clay'], 'Clay pressed with a wooden brick form. Mixed with coke dust or brick dust first, it becomes Compressed Coke Clay or Compressed Fireclay: the Brick Kiln fires those into the bricks for your first multiblocks.', 0, 3, ['wood'])
    add('kiln', 'Brick Kiln', ['gtna:brick_kiln'], 'A primitive multiblock that fires bricks and ceramics from compressed clay and coal. No energy.', 1.5, 3, ['clay'], shape='rsquare', reward='stone')
    add('coke_bricks', 'Coke Oven Bricks', ['gtceu:coke_oven_bricks*26'], 'Compressed Coke Clay fired in the Brick Kiln. You need 26 for a Coke Oven.', 3, 2.25, ['kiln'])
    add('coke_oven', 'Coke Oven', ['gtceu:coke_oven'],
        ['Bakes logs into charcoal and coal into coke, and gives &eCreosote&r as a byproduct. A Coke Oven Hatch lets pipes and hoppers move items and fluids in and out.',
         'Coke burns twice as long as coal and is needed for steel in the Primitive Blast Furnace.'], 4.5, 2.25, ['coke_bricks'], shape='rsquare', reward='stone')
    add('creosote', 'Creosote', ['gtceu:creosote_bucket'], 'Treats wood into Treated Wood (better planks for poles and frames) and burns in liquid boilers.', 6, 2.25, ['coke_oven'])
    add('treated', 'Treated Wood', ['gtceu:treated_wood_planks'], 'Wood soaked in creosote.', 7.5, 2.25, ['creosote'], hide_lines=True)
    add('firebricks', 'Firebricks', ['gtceu:firebricks*16'], 'Compressed Fireclay fired in the Brick Kiln. You need 16 blocks for a Primitive Blast Furnace.', 3, 3.75, ['kiln'])
    add('pbf', 'Primitive Blast Furnace', ['gtceu:primitive_blast_furnace'],
        ['Your first steel. Iron + coal or coke, a long wait, and out comes steel. Build two or three: they are slow.',
         'Steel is the step from the Steam Age to electricity.'], 4.5, 3.75, ['firebricks'], shape='rsquare', reward='stone')
    add('steel', 'Steel', ['gtceu:steel_ingot'], 'The metal of LV.', 6, 3.75, ['pbf'], shape='hexagon', reward='stone')
    add('wrought', 'Wrought Iron', ['gtceu:wrought_iron_ingot'], 'Purer iron for steam machine hulls and pipes. It comes from smelting iron: press R on it for the exact recipe.', 7.5, 3.75, ['steel'], hide_lines=True)

    add('stone_furnace', 'Primitive Stone Furnace', ['gtna:primitive_stone_furnace'], 'A multiblock furnace with no energy: smelts in bulk. A good place for your ore early on.', 0, 5.5, ['wood'], shape='rsquare')
    add('superheater', 'Stone SuperHeater', ['gtna:stone_superheater'],
        ['Melts stone, cobblestone, granite or diorite into lava, 100 mB per block. A steady supply of lava for liquid boilers without bucket trips to the nether.'],
        1.5, 5.5, ['stone_furnace'], shape='rsquare')
    add('resin', 'Sticky Resin', ['gtceu:sticky_resin'], 'Find a rubber tree (they grow in forests and swamps) and break the logs: they sometimes drop resin. Plant the saplings. Resin makes rubber and glue.', 3, 5.5, ['stone_furnace'])
    add('rubber', 'Rubber', ['gtceu:rubber_plate'], 'Rubber insulates your first cables. Smelt raw rubber dust into rubber; plate it.', 4.5, 5.5, ['resin'])
    add('tinkers', 'The Smeltery', ['tconstruct:smeltery_controller'],
        ['Tinkers\' Construct melts ore into liquid metal for tools and casts. A smeltery doubles some ores early on.',
         'Grout makes seared bricks; build a 3x3 tank with a controller, drain and faucet.'], 3, 7, ['stone_furnace'], optional=True)
    add('to_steam', '&6Into the Steam Age', ['gtceu:lp_steam_solid_boiler'],
        'With bronze in hand you can build your first steam machines. Open the &6Steam Age&r chapter.', 9.5, 1.5, ['plates', 'steel'], shape='gear', size=1.5, reward='steam')
    for q in qs:
        if q.y >= 2 and q.key != 's/to_steam': q.y += 1.0
    return Ch('stone', '&7Stone Age', 'gtceu:flint_mortar', 'main', qs, subtitle='Flint, bronze and steel', order=1)


def steam(steam_multis, multi_text):
    qs = []

    def add(key, title, tasks, desc, x, y, deps=(), **kw):
        kw.setdefault('xp', 20)
        q = at(Q('steam/' + key, title, tasks, desc, deps=[d if '/' in d else 'steam/' + d for d in deps], **kw), x, y)
        qs.append(q)
        return q

    add('boiler', 'Small Coal Boiler', ['gtceu:lp_steam_solid_boiler'],
        ['Burns coal, charcoal, coke or wood into steam. Give it water first: a boiler that runs dry and then gets water explodes.',
         'Steam machines take steam through any side. Put them next to the boiler or pipe it with bronze fluid pipes.'], 0, 0, ['s/to_steam'],
        shape='gear', size=1.5, reward='steam')
    add('pipes', 'Bronze Fluid Pipes', ['gtceu:bronze_small_fluid_pipe', 'gtceu:bronze_normal_fluid_pipe'], 'Move steam and water.', 1.5, -1.5, ['boiler'])
    add('water', 'Water Supply', ['gtceu:primitive_pump'],
        'The Primitive Pump is a small multiblock that pumps water from the biome it stands in. An infinite water source for boilers.', 1.5, 1.5, ['boiler'], shape='rsquare')
    machines = [
        ('lp_steam_macerator', 'Grinds ores into crushed ore and items into dust: 2 dust per ore once crushed ore is washed. Your best friend now.'),
        ('lp_steam_alloy_smelter', 'Makes bronze, red alloy, invar and more from ingots or dust, and bricks from clay.'),
        ('lp_steam_compressor', 'Compresses dust into plates, ingots into blocks, and makes compressed clay.'),
        ('lp_steam_forge_hammer', 'Hammers ingots into plates (better than by hand) and crushes ores.'),
        ('lp_steam_extractor', 'Extracts rubber from resin and melts items into fluids.'),
        ('lp_steam_furnace', 'A furnace that runs on steam.'),
        ('lp_steam_rock_crusher', 'Makes stone, cobble, basalt and more from lava and water next to it.'),
    ]
    for i, (m, d) in enumerate(machines):
        add(m, name('gtceu:' + m), ['gtceu:' + m], d, 3 + (i % 4) * 1.5, -0.75 + (i // 4) * 1.5, ['boiler'], hide_lines=i > 0)
    add('hp_boiler', 'High Pressure Boiler', ['gtceu:hp_steam_solid_boiler'],
        'Steel boilers make twice the steam. Upgrade once you have steel.', 0, 3, ['boiler', 's/steel'])
    add('hp_machines', 'High Pressure Machines', ['gtceu:hp_steam_macerator', 'gtceu:hp_steam_alloy_smelter'],
        ['Steel steam machines run the ULV recipes at &ehalf the time&r for three times the steam, and unlock the higher-pressure recipes.',
         'Worth it once your boilers keep up.'], 1.5, 3, ['hp_boiler'], reward='steam')
    add('solar', 'Solar Boilers', ['gtceu:lp_steam_solar_boiler'], 'Make steam from sunlight, for free. Slow, but they never need fuel. Daytime only.', 0, 4.5, ['boiler'])
    add('liquid', 'Liquid Boilers', ['gtceu:lp_steam_liquid_boiler'], 'Burn lava or creosote. Pair with the Stone SuperHeater for lava.', 1.5, 4.5, ['solar'], hide_lines=True)
    add('large_solar', 'Large Steam Solar Boiler', ['gtna:large_steam_solar_boiler'],
        'A multiblock field of solar cells: 900 mB of steam per cell in sunlight. Enough for a whole steam factory.', 3, 4.5, ['solar'], shape='rsquare', reward='steam')
    add('large_bronze', 'Large Bronze Boiler', ['gtceu:bronze_large_boiler'],
        'A multiblock boiler: lots of steam from any solid or liquid fuel. Throttle it with a screwdriver.', 4.5, 4.5, ['hp_boiler'], shape='rsquare', reward='steam')

    # steam multiblocks
    special = {
        'gtceu:steam_grinder': 'A multiblock macerator: 8 items in parallel.',
        'gtceu:steam_oven': 'A multiblock furnace: 8 items in parallel.',
        'gtna:steam_lava_maker': 'Melts stone, cobble, granite or diorite into lava with steam. Its recipe needs an LV Assembler and 10 LV circuits, so it comes at the end of the Steam Age.',
        'gtna:stone_superheater': None, 'gtceu:primitive_blast_furnace': None, 'gtceu:coke_oven': None, 'gtna:brick_kiln': None,
        'gtna:primitive_stone_furnace': None, 'gtna:large_steam_solar_boiler': None, 'gtceu:bronze_large_boiler': None, 'gtceu:primitive_pump': None,
    }
    sm = [m for m in steam_multis if special.get(m['id'], '') is not None]
    sm.sort(key=lambda m: (m['tier'], m['name']))
    anchor = add('multis', '&6Steam Multiblocks', ['gtceu:steam_grinder'], special['gtceu:steam_grinder'] +
                 ' Steam multiblocks take steam through a Steam Input Hatch and items through Steam Input Buses. They are your bulk processing until LV.',
                 0, 6.5, ['hp_machines'], shape='rsquare', reward='steam')
    rest = [m for m in sm if m['id'] != 'gtceu:steam_grinder']
    for i, m in enumerate(rest):
        d = special.get(m['id']) or multi_text(m)
        add('m/' + m['id'], m['name'], [m['id']], d, 1.5 + (i % 10) * 1.2, 6.5 + (i // 10) * 1.2, ['multis'], hide_lines=True, xp=25)

    # the way to LV
    add('vacuum', 'Vacuum Tubes', ['gtceu:vacuum_tube'], 'Glass tubes, copper wire and steel bolts. The heart of your first circuits.', 9.5, -1.5, ['hp_machines'])
    add('resistor', 'Resistors', ['gtceu:resistor'], 'Coal or charcoal dust, fine copper wire and sticky resin.', 9.5, 0, ['hp_machines'])
    add('red_alloy', 'Red Alloy', ['gtceu:red_alloy_ingot'], 'Copper and redstone in the alloy smelter. Red alloy wire carries signals and is in every early circuit.', 9.5, 1.5, ['hp_machines'])
    add('cable', 'Tin Cable', ['gtceu:tin_single_cable'], 'Tin wire covered in rubber: LV cable. Uninsulated wire shocks you and loses power.', 11, 1.5, ['red_alloy', 's/rubber'])
    add('circuit', 'Basic Electronic Circuit', ['gtceu:basic_electronic_circuit'], 'Your first circuit, made by hand. An LV circuit: every LV machine needs some.', 11, -0.75, ['vacuum', 'resistor'], shape='hexagon', reward='steam')
    add('lv_ready', '&7Ready for Electricity', ['gtceu:lv_machine_hull'],
        'Steel plates, tin cable and a casing: the LV Machine Hull. Every LV machine is built around one. Open the &7LV&r chapter.',
        12.5, 0.25, ['circuit', 'cable'], shape='gear', size=2.0, reward='steam')
    return Ch('steam', '&6Steam Age', 'gtceu:lp_steam_solid_boiler', 'main', qs, subtitle='Bronze machines and boilers', order=2)
