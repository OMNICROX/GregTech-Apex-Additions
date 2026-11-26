"""Side chapters written for this pack: Botania and the Sylvan Arbiter, Avaritia, space, the Aether, the Suit
Fabrication Station and monsters. Each quest also needs the GregTech gateway of the tier its item unlocks at."""
from core import *


def build(prefix, rows, root_deps=(), gate_items=True, dx=1.6, dy=1.7):
    """rows: list of rows; each entry (key, title, tasks, desc, opts). opts: d=[deps] (default: previous entry in
    the row, or the first entry of the row above), t=forced tier gate, plus any Q kwarg."""
    qs = []
    prev_row_first = None
    for r, row in enumerate(rows):
        prev = None
        for c, e in enumerate(row):
            key, title, tasks, desc = e[:4]
            o = dict(e[4]) if len(e) > 4 else {}
            deps = o.pop('d', None)
            if deps is None:
                deps = [prev] if prev else ([prev_row_first] if prev_row_first else list(root_deps))
            deps = [d if ('/' in d) else f'{prefix}/{d}' for d in deps if d]
            gate = o.pop('t', None)
            if gate is None and gate_items:
                ts = [tier(t.split('*')[0]) for t in tasks if isinstance(t, str) and t != 'check' and exists(t.split('*')[0])]
                ts = [t for t in ts if t < 99]
                gate = max(ts) if ts else 0
            if gate: deps.append(f't{min(gate, 13)}/gate')
            o.setdefault('xp', 25)
            q = Q(f'{prefix}/{key}', title, tasks, desc, deps=deps, **o)
            q.x, q.y = c * dx, r * dy
            qs.append(q)
            if c == 0: first = q.key
            prev = q.key
        prev_row_first = first
    return qs


def botania():
    rows = [
        [('lexicon', '&dLexica Botania', ['botania:lexicon'], 'Botania\'s guide book: every flower and device with its recipe. Botania here is gated by GregTech voltage: the early magic is free, the big devices need GT assemblers.', dict(shape='gear', size=1.5, reward='steam')),
         ('apothecary', 'Petal Apothecary', ['botania:apothecary_default'], 'Fill it with water and throw in petals and seeds to make flowers. Mystical flowers grow in the world; shear or break them for petals.'),
         ('daisy', 'Pure Daisy', ['botania:pure_daisy'], 'Turns stone into livingrock and logs into livingwood when placed around it. The base of every Botania build.'),
         ('livingrock', 'Livingrock and Livingwood', ['botania:livingrock', 'botania:livingwood_log'], 'Placed around a Pure Daisy, they change one by one.'),
         ('spreader', 'Mana Spreader', ['botania:mana_spreader'], 'Collects mana from nearby generating flowers and fires it at a pool. Bind it with a Wand of the Forest.'),
         ('pool', 'Mana Pool', ['botania:mana_pool'], 'Stores mana. Throw items in to infuse them: iron becomes &bManasteel&r, diamonds become Mana Diamonds, ender pearls become Mana Pearls.'),
         ('manasteel', 'Manasteel', ['botania:manasteel_ingot'], 'Infused iron: the metal of early Botania.', dict(reward='steam'))],
        [('endoflame', 'Endoflame', ['botania:endoflame'], 'A generating flower that burns fuel items dropped next to it. Your first mana.', dict(d=['apothecary'])),
         ('hydroangeas', 'Hydroangeas', ['botania:hydroangeas'], 'Drinks still water around it for a trickle of mana. Slow but free.'),
         ('thermalily', 'Thermalily', ['botania:thermalily'], 'Burns lava for a lot of mana in bursts. Lava from the Stone SuperHeater keeps it going.'),
         ('gourmaryllis', 'Gourmaryllis', ['botania:gourmaryllis'], 'Eats food. Varied meals give more mana.', dict(optional=True)),
         ('entropinnyum', 'Entropinnyum', ['botania:entropinnyum'], 'Absorbs TNT explosions for huge mana. Loud.', dict(optional=True))],
        [('runic', 'Runic Altar', ['botania:runic_altar'], 'Crafts runes and Botania\'s mid-game items with mana. Made in a GT assembler at LV here.', dict(d=['pool'], reward='lv')),
         ('rune_water', 'Elemental Runes', ['botania:rune_water', 'botania:rune_fire'], 'Water, fire, earth and air runes start every rune tree.'),
         ('rune_mana', 'Rune of Mana', ['botania:rune_mana'], 'Runes keep going: seasons, then the seven sins.'),
         ('tablet', 'Mana Tablet', ['botania:mana_tablet'], 'Portable mana for tools and rods.', dict(optional=True)),
         ('mech_daisy', 'Mechanical Daisy', ['botanicalmachinery:mechanical_daisy'], 'A Pure Daisy in a box: livingrock and livingwood without the circle.', dict(d=['runic'])),
         ('mech_apothecary', 'Mechanical Apothecary', ['botanicalmachinery:mechanical_apothecary'], 'Crafts flowers automatically from petals and seeds.')],
        [('terra_plate', 'Terrestrial Agglomeration Plate', ['botania:terra_plate'], 'Fuses manasteel, a mana pearl and a mana diamond into &aTerrasteel&r with an enormous amount of mana. MV assembler.', dict(d=['runic'], reward='mv')),
         ('terrasteel', 'Terrasteel', ['botania:terrasteel_ingot'], 'Botania\'s second metal. Terrasteel tools are some of the best in the game.', dict(reward='mv')),
         ('terra_pick', 'Terra Shatterer', ['botania:terra_pick'], 'A pickaxe that levels up with mana and digs huge areas.', dict(optional=True)),
         ('mech_pool', 'Mechanical Mana Pool', ['botanicalmachinery:mechanical_mana_pool'], 'Infuses items without throwing them in a pool.', dict(d=['terra_plate'])),
         ('mech_runic', 'Mechanical Runic Altar', ['botanicalmachinery:mechanical_runic_altar'], 'An automatic runic altar.')],
        [('portal', 'Elven Gateway', ['botania:alfheim_portal'], 'Opens a portal to Alfheim. Throw items in to trade with the elves: Elementium, Pixie Dust, Dragonstone and Dreamwood. HV assembler.', dict(d=['terrasteel'], reward='hv')),
         ('elementium', 'Elementium', ['botania:elementium_ingot'], 'The elves\' metal.'),
         ('pixie', 'Pixie Dust and Dragonstone', ['botania:pixie_dust', 'botania:dragonstone'], 'Elven trade goods for the late devices.'),
         ('market', 'Alfheim Market', ['botanicalmachinery:alfheim_market'], 'Trades with the elves without the portal.'),
         ('battery', 'Mana Battery', ['botanicalmachinery:mana_battery'], 'A huge mana store.', dict(optional=True))],
        [('infuser', 'Mana Infuser', ['mythicbotany:mana_infuser'], 'Mythic Botany\'s infuser: Alfsteel and the other Norse materials. EV.', dict(d=['elementium'], reward='ev')),
         ('alfsteel', 'Alfsteel', ['mythicbotany:alfsteel_ingot'], 'The strongest metal Mythic Botany makes.'),
         ('gaia_pylon', 'Gaia Pylon', ['botania:gaia_pylon'], 'Pylons for the Gaia Guardian ritual.'),
         ('gaia', 'Gaia Guardian', ['botania:life_essence'], 'Summon the Gaia Guardian with a Terrasteel ingot on a beacon surrounded by Gaia Pylons. Kill it for Gaia Spirits.', dict(reward='ev')),
         ('gaia_ingot', 'Gaia Spirit Ingot', ['botania:gaia_ingot'], 'Terrasteel fused with Gaia Spirits.'),
         ('relics', 'Relics', ['botania:king_key', 'botania:thor_ring'], 'Gaia\'s rare drops: the Key of the King\'s Law, Rings of Thor, Odin and Loki...', dict(optional=True))],
        [('elven_altar', 'Elven Altar', ['botanicadds:elven_altar'], 'Botanic Additions\' altar for Gaiasteel and the other top materials. IV.', dict(d=['gaia_ingot'], reward='iv')),
         ('dreaming', 'Dreaming Mana Pool', ['botanicadds:dreaming_pool'], 'A bigger, better pool.'),
         ('gaiasteel', 'Gaiasteel', ['botanicadds:gaiasteel_ingot'], 'Botanic Additions\' top metal.'),
         ('tesseract', 'Mana Tesseract', ['botanicadds:mana_tesseract'], 'Moves mana anywhere. LuV.', dict(reward='luv')),
         ('terra_catalyst', 'Terra Catalyst', ['botanicadds:terra_catalyst'], 'Gaia-tier agglomeration.')],
    ]
    qs = build('bot', rows, root_deps=['s/to_steam'])
    # the Sylvan Arbiter: Botania as a GregTech multiblock
    arb = [
        [('arbiter', '&dSylvan Arbiter', ['gtapexadditions:mana_nexus'],
          ['A great tree in a glass-domed conservatory: every Botania altar craft as a GregTech recipe, with parallels. Mana pool infusion, the runic altar, the Pure Daisy and the apothecary at first; terrestrial agglomeration, elven trades and the rune rituals as its Mana Cores improve.',
           'It grows a floor at a time: Roots, Gallery, Terrace, Canopy and Crown. The preview shows each stage; hatches go in the bottom layer under the dome or the 5x5 gap in the back wall.',
           '&7Build by howester84.'], dict(d=['bot/pool'], shape='gear', size=2.0, reward='lv', t=1)),
         ('core1', 'Manasteel Mana Cores', ['gtapexadditions:mana_core_t1*8'], 'Cores replace the spreaders of the original build. Their tier is the Arbiter\'s tier.', dict(d=['arbiter'])),
         ('core2', 'Terrasteel Mana Cores', ['gtapexadditions:mana_core_t2*8'], 'Unlock terrestrial agglomeration and elven trades.', dict(d=['core1', 'bot/terrasteel'])),
         ('core3', 'Elementium Mana Cores', ['gtapexadditions:mana_core_t3*8'], 'Unlock the Mana Infuser\'s recipes.', dict(d=['core2', 'bot/elementium'])),
         ('core4', 'Alfsteel Mana Cores', ['gtapexadditions:mana_core_t4*8'], 'Unlock the rune rituals.', dict(d=['core3', 'bot/alfsteel'])),
         ('core5', 'Gaiasteel Mana Cores', ['gtapexadditions:mana_core_t5*8'], 'The most storage and parallels.', dict(d=['core4', 'bot/gaiasteel'], reward='luv'))],
        [('leaves', 'Mana Bloom Leaves', ['gtapexadditions:mana_bloom_leaves*32'], 'The tree\'s canopy. Their colour follows the Arbiter\'s tier.', dict(d=['arbiter'])),
         ('hatch', 'Mana Hatch', ['gtapexadditions:mana_hatch'], 'Feeds mana into the Arbiter.'),
         ('casing', 'Sylvan Casing', ['gtapexadditions:mana_nexus_casing*16'], 'The casing hatches sit in.', dict(d=['arbiter']))],
    ]
    aq = build('arb', arb)
    for q in aq: q.y += 13
    return Ch('botania', '&dBotania', 'botania:lexicon', 'magic', qs + aq, subtitle='Flowers, mana and the Sylvan Arbiter', order=60)


def avaritia():
    rows = [
        [('table', '&6Extreme Crafting Table', ['avaritia:extreme_crafting_table'],
          'A 9x9 crafting grid for Avaritia\'s huge recipes. It is gated behind the bosses of this world: check its recipe.', dict(shape='gear', size=1.5, reward='iv')),
         ('compressor', 'Neutronium Compressor', ['avaritia:neutron_compressor'], 'Compresses thousands of one material into a Singularity.'),
         ('singularity', 'Your First Singularity', ['avaritia:singularity'], 'Iron, gold, diamond, redstone, lapis, copper, quartz, amethyst, netherite... each takes a mountain of material. Automate the feeding with AE2.', dict(reward='luv')),
         ('eternal', 'Eternal Singularity', ['avaritia:eternal_singularity'], 'Every singularity in one. Maxwell\'s cats need lots of these.', dict(reward='zpm')),
         ('catalyst', 'Infinity Catalyst', ['avaritia:infinity_catalyst'], 'All the singularities and Botania\'s top materials: the heart of the Infinity Ingot.', dict(reward='uv'))],
        [('neutronium', 'Neutronium', ['gtceu:neutronium_ingot'], 'Here neutronium is GregTech\'s: Avaritia\'s Neutron Collector is gone. It comes from fusion and the Neutronium boule line.', dict(d=['table'], reward='zpm')),
         ('matrix', 'Crystal Matrix', ['avaritia:crystal_matrix_ingot'], 'Diamond lattices and nether stars.'),
         ('infinity', '&dInfinity Ingot', ['gtna:infinity_ingot'], 'Crafted on the Extreme Crafting Table, but it comes out as GregTech Infinity: every GregTech recipe that shapes or uses it runs at UV or higher.', dict(d=['catalyst', 'neutronium'], shape='hexagon', reward='uv')),
         ('sword', 'Sword of the Cosmos', ['avaritia:infinity_sword'], 'Kills anything.', dict(optional=True)),
         ('pick', 'World Breaker', ['avaritia:infinity_pickaxe'], 'Mines anything, any amount.', dict(optional=True))],
        [('armor', '&dInfinity Armor', ['avaritia:infinity_helmet', 'avaritia:infinity_chestplate', 'avaritia:infinity_boots'], 'True invincibility.', dict(d=['infinity'], optional=True)),
         ('stew', 'Ultimate Stew', ['avaritia:ultimate_stew'], 'A stew of every food.', dict(optional=True, d=['matrix'])),
         ('meatballs', 'Cosmic Meatballs', ['avaritia:cosmic_meatballs'], 'Every meat, compressed.', dict(optional=True, d=['matrix']))],
    ]
    return Ch('avaritia', '&dAvaritia', 'avaritia:extreme_crafting_table', 'endgame', build('ava', rows, root_deps=['t5/gate']),
              subtitle='Singularities and Infinity', order=80)


def space():
    rows = [
        [('workbench', '&9NASA Workbench', ['ad_astra:nasa_workbench'],
          'Rockets are put together here. Their parts, the launch pad and the main Ad Astra machines are GregTech assembler recipes here, a tier per planet.', dict(shape='gear', size=1.5, reward='hv', t=3)),
         ('launch_pad', 'Launch Pad', ['ad_astra:launch_pad'], 'Rockets launch from here.'),
         ('fuel', 'Rocket Fuel', ['check'], 'Ad Astra\'s own oil refining is gone: GregTech Rocket Fuel (and the other GT fuels) work instead. Pump it into the rocket.'),
         ('suit', 'Space Suit', ['ad_astra:space_helmet', 'ad_astra:space_suit'], 'Space has no air. Wear a full suit and fill it with oxygen.'),
         ('oxygen', 'Oxygen Loader', ['ad_astra:oxygen_loader'], 'Fills suits and tanks with oxygen. GregTech oxygen works.'),
         ('distributor', 'Oxygen Distributor', ['ad_astra:oxygen_distributor'], 'Fills a sealed base with air.', dict(optional=True))],
        [('t1', 'Tier 1 Rocket: the Moon', ['ad_astra:tier_1_rocket'], 'Steel parts, HV circuits. The Moon has cheese and desh.', dict(d=['workbench'], reward='hv', t=3)),
         ('moon', 'Walk on the Moon', ['dim:ad_astra:moon'], 'Land on the Moon.', dict(reward='hv')),
         ('desh', 'Desh', ['ad_astra:desh_ingot'], 'The Moon\'s metal, smelted in the Electric Blast Furnace.'),
         ('t2', 'Tier 2 Rocket: Mars', ['ad_astra:tier_2_rocket'], 'Desh parts, EV.', dict(reward='ev', t=4)),
         ('ostrum', 'Ostrum', ['ad_astra:ostrum_ingot'], 'Mars\' metal.'),
         ('t3', 'Tier 3 Rocket: Venus and Mercury', ['ad_astra:tier_3_rocket'], 'Ostrum parts, IV.', dict(reward='iv', t=5)),
         ('calorite', 'Calorite', ['ad_astra:calorite_ingot'], 'From Venus.'),
         ('t4', 'Tier 4 Rocket: Glacio', ['ad_astra:tier_4_rocket'], 'Calorite parts, LuV. Glacio orbits another star.', dict(reward='luv', t=6))],
        [('t5', 'Tier 5 Rocket: Jupiter', ['ad_astra_rocketed:tier_5_rocket'], 'Jupiter and its moons: Io, Europa, Ganymede, Callisto and more. ZPM.', dict(d=['t1'], reward='zpm', t=7)),
         ('t6', 'Tier 6 Rocket: Saturn', ['ad_astra_rocketed:tier_6_rocket'], 'Saturn and its moons. UV.', dict(reward='uv', t=8)),
         ('t7', 'Tier 7 Rocket: Uranus and Neptune', ['ad_astra_rocketed:tier_7_rocket'], 'The edge of the solar system. UHV.', dict(reward='uhv', t=9)),
         ('rover', 'Rover', ['ad_astra:tier_1_rover'], 'Drive around a planet.', dict(d=['t1'], optional=True)),
         ('gravity', 'Gravity Normalizer', ['ad_astra:gravity_normalizer'], 'Earth gravity in your base.', dict(optional=True))],
        [('elevator', '&5Space Elevator', ['gtapexadditions:space_elevator'],
          'A cable to orbit. Its modules mine asteroids, pump planets\' gases and assemble the parts only space can make. The UIV+ field generators, emitters and sensors and the endgame multiblocks are made in the Space Assembler.',
          dict(d=['t7'], shape='gear', size=1.5, reward='uhv', t=9)),
         ('mining1', 'Space Mining Module', ['gtapexadditions:space_mining_module_mk1'], 'Drones mine asteroids.'),
         ('pump1', 'Space Pumping Module', ['gtapexadditions:space_pumping_module_mk1'], 'Pumps gases from the planets.', dict(d=['elevator'])),
         ('assembler1', 'Space Assembler Module', ['gtapexadditions:space_assembler_module_mk1'], 'Assembles the parts for UIV and beyond.', dict(d=['elevator'])),
         ('assembler2', 'Space Assembler MK II', ['gtapexadditions:space_assembler_module_mk2'], 'UXV parts.'),
         ('assembler3', 'Space Assembler MK III', ['gtapexadditions:space_assembler_module_mk3'], 'OpV parts and circuits.'),
         ('dyson', 'Dyson Swarm', ['gtapexadditions:dyson_sphere_launch_site'], 'Launches Dyson Swarm modules around the sun: a power plant in orbit.', dict(d=['elevator']))],
    ]
    qs = build('space', rows, root_deps=['t3/gate'])
    return Ch('space', '&9Space', 'ad_astra:tier_1_rocket', 'explore', qs, subtitle='Rockets, planets and the Space Elevator', order=70)


def aether():
    rows = [
        [('portal', '&bThe Aether', ['dim:aether:the_aether'], 'Build a frame of glowstone and pour water in it: the portal to the Aether, a world of floating islands.', dict(shape='gear', size=1.5, reward='steam')),
         ('skyroot', 'Skyroot', ['aether:skyroot_planks'], 'The Aether\'s wood. Skyroot tools double drops of Aether blocks.'),
         ('holystone', 'Holystone', ['aether:holystone'], 'The Aether\'s stone.'),
         ('ambrosium', 'Ambrosium', ['aether:ambrosium_shard'], 'Fuel for the Altar and Freezer, and it heals.'),
         ('zanite', 'Zanite', ['aether:zanite_gemstone'], 'Gets stronger as it wears out.'),
         ('altar', 'Altar', ['aether:altar'], 'Enchants Aether items with ambrosium.')],
        [('gravitite', 'Gravitite', ['aether:gravitite_ore'], 'Floats up when mined. Enchant it at the Altar.', dict(d=['zanite'])),
         ('enchanted', 'Enchanted Gravitite', ['aether:enchanted_gravitite'], 'Doubles Gravi Stars in a GregTech recipe (UV).'),
         ('aerclouds', 'Aerclouds', ['aether:cold_aercloud', 'aether:blue_aercloud', 'aether:golden_aercloud'], 'Cold, blue and golden aerclouds centrifuge into gases. Golden ones give &bHelium-3&r.'),
         ('ring', 'Angel Ring', ['gtapexadditions:angel_ring'], 'Flight from the sky itself: enchanted gravitite, a golden feather and an angel block.', dict(shape='hexagon', reward='uv'))],
        [('bronze', 'Bronze Dungeon', ['aether:bronze_dungeon_key'], 'Beat the Slider in the bronze dungeon.', dict(d=['portal'], reward='lv')),
         ('silver', 'Silver Dungeon', ['aether:silver_dungeon_key'], 'Beat the Valkyrie Queen.', dict(reward='mv')),
         ('gold', 'Gold Dungeon', ['aether:gold_dungeon_key'], 'Beat the Sun Spirit.', dict(reward='hv')),
         ('valkyrie', 'Valkyrie Gear', ['aether:valkyrie_lance'], 'Loot of the silver dungeon.', dict(optional=True, d=['silver'])),
         ('phoenix', 'Phoenix Armor', ['aether:phoenix_chestplate'], 'Fire immunity. Gold dungeon loot.', dict(optional=True, d=['gold']))],
        [('redux', 'Aether Redux', ['aether_redux:veridium_ingot'], 'New biomes, Veridium and Sentrite. Veridium tools get infused with ambrosium.', dict(d=['holystone'])),
         ('blight', 'The Blight', ['aether_redux:blightmoss_block'], 'A corrupted biome of the Aether.', dict(optional=True))],
    ]
    return Ch('aether', '&bThe Aether', 'aether:aether_portal_frame', 'explore', build('aether', rows, root_deps=['s/to_steam']),
              subtitle='Floating islands and Helium-3', order=71)


def suits():
    rows = [
        [('casing', 'Steel Booth', ['gtapexadditions:suit_casing_t1*16'], 'The Suit Fabrication Station is one building in four tiers. Start with Steel Booth Casings (MV assembler).', dict(t=2)),
         ('station', '&6Suit Fabrication Station', ['gtapexadditions:suit_fabrication_station'],
          ['5x5x6 booth: casing floor and roof, glass on the front, Suit Display Monitors in the top ring and a lamp in the roof centre. The controller goes in the middle of the front floor row.',
           'Open the &dSuit Picker&r tab to browse every suit with its hero kit, pick one and press &6Fabricate&r.'], dict(shape='gear', size=1.5, reward='mv')),
         ('first_suit', 'Suit Up', ['check'], 'Make any suit in the station. Each one comes with its own hero kit of powers: speedsters run, fliers fly, bruisers hit hard.'),
         ('t2', 'Showcase Pod', ['gtapexadditions:suit_casing_t2*16'], 'Tier II casings (EV) unlock the bigger hero suits. Same building: swap the casings (the Structure Upgrader does it in one click).', dict(reward='ev')),
         ('t3', 'Holo Chamber', ['gtapexadditions:suit_casing_t3*16'], 'Tier III casings (LuV).', dict(reward='luv')),
         ('t4', 'Ascension Forge', ['gtapexadditions:suit_casing_t4*16'], 'Tier IV casings (UHV): Kryptonians, Viltrumites and cosmic heroes.', dict(reward='uhv', shape='hexagon')),
         ('core', 'Hero Core', ['gtapexadditions:hero_core'], 'Break a tier IV hero suit chestplate down into a Hero Core in the Ascension Forge.'),
         ('apex', '&d&lApex Ascension Suit', ['gtapexadditions:apex_helmet', 'gtapexadditions:apex_chestplate', 'gtapexadditions:apex_leggings', 'gtapexadditions:apex_boots'],
          ['The final suit. Twelve Hero Cores, UEV parts and Grade 8 purified water.',
           'Full set: flight, regeneration, night vision, immune to fire, falls, drowning, wither and poison, and +50% damage against infernal mobs.'], dict(shape='gear', size=2.0, reward='uev'))],
        [('monitor', 'Suit Display Monitors', ['gtapexadditions:suit_monitor*12'], 'Twelve of them ring the top of the booth.', dict(d=['station'])),
         ('me', 'Autocraft the Parts', ['gtceu:me_input_bus'], 'Put an ME Input Bus on the station. Fabricate then sets the bus to pull the suit parts and asks your crafting CPUs for any part that is short.', dict(d=['first_suit']))],
        [('infernal', 'Infernal Mobs', ['check'], 'Some hostile spawns are &eElite&r, &6Ultra&r or &4Infernal&r: more health, nasty modifiers and much better loot. Kill one.', dict(d=['w/start'])),
         ('eec', 'Extreme Entity Crusher', ['gtapexadditions:extreme_entity_crusher'], 'A spawn egg in an input bus picks the mob; a sword sets looting. No more mob towers.', dict(reward='mv')),
         ('elite', 'Elite Sigil', ['gtapexadditions:elite_sigil'], 'Put a sigil in an input bus of the crusher to crush that rank of the mob: loot x3, bonus drops, more power.'),
         ('ultra', 'Ultra Sigil', ['gtapexadditions:ultra_sigil'], 'Loot x5.'),
         ('infernal_sigil', 'Infernal Sigil', ['gtapexadditions:infernal_sigil'], 'Loot x9. An infernal witch crusher pays for itself.', dict(shape='hexagon'))],
        [('upgrader', 'Structure Upgrader', ['gtapexadditions:structure_upgrader'], 'Shift + right-click a formed controller: upgrade every coil or tiered casing at once from your inventory, and hatches if you carry better ones.', dict(d=['t1/gate']))],
    ]
    return Ch('suits', '&6Hero Suits', 'gtapexadditions:suit_fabrication_station', 'heroes', build('suit', rows, root_deps=['t2/gate']),
              subtitle='The Suit Fabrication Station', order=50)


def chapters():
    return [botania(), avaritia(), space(), aether(), suits()]
