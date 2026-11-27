from pages_core import page

page('dimensions/dimensions-index.md', 'Dimensions', '''
# Dimensions

As in GT New Horizons, each world has its **own ore veins**, so you travel for the metals only it has. Portals and
rockets are how you get there; the ores and the worlds' own materials are why.

| World | How to get there | Why go |
|---|---|---|
| [Nether](nether.md) | Obsidian portal (steam age) | Sulfur, quartz, redstone, saltpeter, blaze rods |
| [Aether](aether.md) | Glowstone frame + water | Aluminium (bauxite), lithium, mica, topaz; gravitite, zanite |
| [Twilight Forest](twilight-forest.md) | Portal lit with an **HV Field Generator** | Sapphire and olivine (only here), nickel, molybdenum, galena; boss trophies |
| [Undergarden](undergarden.md) | Undergarden catalyst | Uranium, thorium, rare earths, tantalite; utherium, regalium |
| [The End](the-end.md) | Stronghold portal | Platinum line, naquadah, tungsten, chromite |
| [Space](space.md) | Ad Astra rockets (HV-UHV) | Planet ores (tungsten on Mars, then heavier metals) |
| [Personal dimension](personal-space.md) | GTNA | Your own world |

<SubPages />
''', pos=40, icon='minecraft:ender_eye')

def dim(path, title, pos, icon, body, items=None):
    page('dimensions/' + path, title, body, parent='dimensions/dimensions-index.md', pos=pos, icon=icon, items=items)

dim('nether.md', 'The Nether', 10, 'minecraft:netherrack', '''
# The Nether

Open it in the steam age. GregTech's Nether veins: **sulfur** (rubber, sulfuric acid), **nether quartz** and
**certus**, **redstone**, **saltpeter**, **tetrahedrite**, **beryllium**, **molybdenum**, **monazite**, **topaz**.
Blazes give blaze rods (needed for many things, including the Aether's glowstone portal via glowstone).
''')

dim('aether.md', 'The Aether', 20, 'aether:holystone', '''
# The Aether

A glowstone-framed portal (light it with water) leads to floating islands.

## GregTech ores (Aether holystone)

| Vein | Ores |
|---|---|
| Bauxite | Bauxite, ilmenite, **aluminium** |
| Mica | Kyanite, mica, pollucite |
| Lithium | Lepidolite, spodumene, lithium |
| Topaz | Blue topaz, topaz, chalcocite |

These are the richest aluminium source in the game, so the Aether is an **MV** destination.

## Aether materials in GregTech

* <ItemLink id="aether:zanite_gemstone" /> centrifuges into sapphire dust and a little titanium.
* <ItemLink id="aether:ambrosium_shard" /> centrifuges into glowstone and sulfur.
* <ItemLink id="aether:enchanted_gravitite" /> is the anti-gravity metal: the Ad Astra **gravity normalizer** and the
  **jet suit** need it, and so does the [Angel Ring](../items/angel-ring.md).
* Holystone macerates to stone dust. Golden aerclouds give helium-3.

<GTRecipesFor id="aether:zanite_gemstone" limit="1" type="gtceu:centrifuge" fallbackText="Zanite centrifuging: see JEI." />
''', items=['aether:zanite_gemstone', 'aether:ambrosium_shard', 'aether:enchanted_gravitite'])

dim('twilight-forest.md', 'The Twilight Forest', 30, 'twilightforest:twilight_portal_miniature_structure', '''
# The Twilight Forest

The portal (a pool of water ringed with flowers) is lit with an <ItemLink id="gtceu:hv_field_generator" />
instead of a diamond, so the Twilight Forest is an **HV** world. Exploring it is how you tier up to IV: its bosses'
trophies build the [Library of Ruina](../multiblocks/library_of_ruina.md), which then makes Twilight loot in bulk.

## GregTech ores (as in GT New Horizons)

| Vein | Ores |
|---|---|
| Sapphire | Almandine, pyrope, **sapphire**, green sapphire (only spawns here) |
| Olivine | Bentonite, magnesite, **olivine**, glauconite sand (only spawns here) |
| Galena | Galena, silver, lead |
| Nickel | Garnierite, nickel, cobaltite, pentlandite |
| Molybdenum | Wulfenite, molybdenite, molybdenum, powellite |

## Twilight materials in GregTech

* Ironwood and knightmetal macerate into iron / gold and steel dust; steeleaf centrifuges into steel and plant balls.
* Carminite centrifuges into redstone and ruby; fiery ingots into iron and blaze dust.
* Trophies (Naga, Lich, Hydra, Ur-Ghast and more) are ingredients for the Library of Ruina.
''')

dim('undergarden.md', 'The Undergarden', 40, 'undergarden:depthrock', '''
# The Undergarden

A dark world below the world, reached with the Undergarden catalyst.

## GregTech ores (depthrock)

| Vein | Ores |
|---|---|
| Uranium | Pitchblende, uraninite, thorium |
| Rare earth | Bastnasite, monazite, neodymium |
| Tantalite | Tantalite, pyrolusite, pyrochlore |
| Cinnabar | Cinnabar, redstone, ruby |

Nuclear materials and rare earths make it an **EV** destination.

## Undergarden materials in GregTech

* <ItemLink id="undergarden:utherium_crystal" /> and utheric shards centrifuge into uranium-238 and thorium.
* <ItemLink id="undergarden:regalium_crystal" /> gives gold and diamond dust; cloggrum and froststeel macerate into
  iron / tin and steel.
''')

dim('the-end.md', 'The End', 50, 'minecraft:end_stone', '''
# The End

GregTech's End veins hold the **platinum line** (sheldonite / cooperite), **naquadah**, **scheelite / tungstate**,
**bauxite**, **chromite** and **pitchblende**. You'll want it by EV-IV for tungsten and platinum.
The Ender Dragon and End cities are also where elytra, shulker shells and dragon's breath come from.
''')

dim('space.md', 'Space (Ad Astra)', 60, 'ad_astra:launch_pad', '''
# Space

Ad Astra's rockets, launch pad, NASA workbench and machines are made in GregTech assemblers at these tiers:

| Destination | Rocket | Tier |
|---|---|---|
| Moon | Tier 1 (steel) | HV |
| Mars | Tier 2 (desh) | EV |
| Venus, Mercury | Tier 3 (ostrum) | IV |
| Glacio | Tier 4 (calorite) | LuV |
| Jupiter and moons | Tier 5 | ZPM |
| Saturn | Tier 6 | UV |
| Uranus, Neptune | Tier 7 | UHV |

* Rocket fuel comes from GregTech (Ad Astra's oil refining is removed).
* GTNA adds GregTech ore veins in the planets' stones; tungsten is on Mars, and each step out has heavier metals.
* Desh, ostrum and calorite plates come from the GregTech bender or forge hammer.
* The gravity normalizer and jet suit need Aether gravitite.
* Stargate Journey's worlds are reachable as Ad Astra destinations (its own gates are turned off for now).
''')

dim('personal-space.md', 'Personal Dimension', 70, 'minecraft:ender_pearl', '''
# Personal Dimension

GT: New Age (GTNA) has its own personal dimension, which Apex uses. Look up its items in JEI.
(Apex's own GTNH-style Personal Space portal is turned off in the config.)
''')

# ------------------------------------------------------------------ magic
page('magic/magic-index.md', 'Magic', '''
# Magic

Magic mods follow the voltage tiers too.

<SubPages />
''', pos=50, icon='bloodmagic:weakbloodorb')

page('magic/blood-magic.md', 'Blood Magic', '''
# Blood Magic

Blood Magic is gated by GregTech the GT New Horizons way.

## Blood Orbs

The altar doesn't soak the raw item any more: first make a **Raw Blood Orb** in a GregTech assembler, then soak it.

| Orb | Raw orb | Tier |
|---|---|---|
| Weak | <ItemLink id="gtapexadditions:raw_blood_orb_1" /> (diamond core) | LV |
| Apprentice | <ItemLink id="gtapexadditions:raw_blood_orb_2" /> (redstone block) | MV |
| Magician | <ItemLink id="gtapexadditions:raw_blood_orb_3" /> (gold block) | HV |
| Master | <ItemLink id="gtapexadditions:raw_blood_orb_4" /> (weak blood shard) | EV |
| Archmage | <ItemLink id="gtapexadditions:raw_blood_orb_5" /> (dungeon metal) | IV |

<GTRecipesFor id="gtapexadditions:raw_blood_orb_1" limit="1" />

## Runes and rituals

Every Blood Magic recipe that needs an orb (runes, ritual stones, the master ritual stone, incense altar, lava
crystal...) is made in the **assembler** at that orb's tier. The orb stays in the machine; any orb of that tier or
higher works. Speed / capacity / displacement runes are MV, the tier-2 runes IV, the Blood Altar itself LV.

## Bloody Hell

The IV [Bloody Hell](../multiblocks/bloody_hell.md) runs every altar, alchemy table and binding ritual recipe in bulk
from a pool of Life Essence.
''', parent='magic/magic-index.md', pos=10, icon='bloodmagic:weakbloodorb', items=['gtapexadditions:raw_blood_orb_1', 'gtapexadditions:raw_blood_orb_2', 'gtapexadditions:raw_blood_orb_3', 'gtapexadditions:raw_blood_orb_4', 'gtapexadditions:raw_blood_orb_5'])

page('magic/botania.md', 'Botania', '''
# Botania

Botania is paced like GT New Horizons: basics through LV-HV, the Gaia Guardian around HV / EV, post-Gaia crafting on
the IV [Alfheim Teleportation Array](../multiblocks/alfheim_teleportation_array.md).

| Tier | Botania |
|---|---|
| Steam | Pure Daisy, Petal Apothecary, Mana Pool, spreaders, generating flowers, Manasteel (from steel) |
| LV | Runic Altar, Mechanical Daisy / Apothecary |
| MV | Terrestrial Agglomeration Plate, Alfheim Portal, mechanical mana pool / runic altar |
| HV | Gaia Pylon, Mana Battery, Alfheim Market, mana infuser, agglomeration factory |
| EV | Alfsteel Pylon, Elven Altar, Dreaming Mana Pool |
| IV | Gaia Agglomeration Plate, Mana Tesseract, Terra Catalyst, Gaiasteel Pylon (post-Gaia, with the Array) |

* After the Gaia Guardian, recipes with Gaia Spirit are made on the Alfheim Teleportation Array.
* Removed exploits: Orechid (stone to ores) and the Elven Mana Fluxfield (mana to FE).
* Terrasteel blocks are part of Avaritia's neutronium compressor; the Infinity Catalyst needs Botania's top materials.
''', parent='magic/magic-index.md', pos=20, icon='botania:mana_pool')

page('magic/mystical-agriculture.md', 'Mystical Agriculture and GT Crops', '''
# Mystical Agriculture and GT Crops

* Mystical Agriculture's crops grow as usual; essence crafting has a few changes (fire essence melts into lava in a
  GT extractor instead of crafting lava buckets).
* **GT crops** (CropsNH-style): seeds for GregTech metals come only from the **Seed Synthesizer**, and they grow in the
  [Mystical Cultivation Array](../multiblocks/mystical_cultivation_array.md). 24 essence -> 1 dust in a GT extractor.
''', parent='magic/magic-index.md', pos=30, icon='mysticalagriculture:inferium_essence')

# ------------------------------------------------------------------ automation
page('automation/automation-index.md', 'Automation', '''
# Automation

<SubPages />
''', pos=60, icon='ae2:controller')

page('automation/ae2.md', 'AE2 in Apex', '''
# AE2 in Apex

AE2's own guide (hold G over any AE2 item) explains how AE2 works. In Apex its crafting recipes move into the
GregTech assembler at a tier (same ingredients + soldering alloy):

| Tier | AE2 |
|---|---|
| LV | Network basics, 1k storage, buses, terminals. **No autocrafting.** |
| MV | 4k / 16k cells, autocrafting (crafting CPU, pattern provider, molecular assembler, interface), P2P |
| HV | Everything else (the default) |
| EV | Quantum link / ring, spatial, big crafting accelerators, assembler matrix |
| IV | AdvancedAE quantum computer and quantum armor, very large cells |
| ZPM | MEGA Cells (1M-16M, MEGA crafting CPU, MEGA interface / pattern provider) |
| UV | MEGA 64M / 256M; InsaneAE basics |
| UHV-UIV | InsaneAE T / P / E cells, quantum CPU, top energy cells |

InsaneAE's machine acceleration cards are disabled. GregTech machines connect to AE2 with ME input / output buses
and hatches; the [Extreme Craft Center](../multiblocks/extreme_craft_center.md) is an ME crafting provider with pattern magnification.

![An AE2 subnet feeding a drive](../assets/images/ae2_subnet.png)
''', parent='automation/automation-index.md', pos=10, icon='ae2:controller')

page('automation/hostile-neural-networks.md', 'Hostile Neural Networks', '''
# Hostile Neural Networks

* **Data models** are attuned per dimension: Apex's blank models (Overworld, Nether, End, Twilight, Undergarden,
  Boss, Master) replace HNN's single blank model, so you train models for the mobs of the world you visit.
* The **Simulation Chamber** (MV) trains models and makes **predictions**; it uses an eighth of HNN's usual power.
* Put a **prediction** in a GregTech **Extractor**: you get every drop the model lists (MV, 4 s). The Loot Fabricator is removed.
* A trained model also works in the [Extreme Entity Crusher](../multiblocks/extreme_entity_crusher.md): its tier adds bonus loot rolls.
''', parent='automation/automation-index.md', pos=20, icon='hostilenetworks:sim_chamber')

page('automation/infernal-mobs.md', 'Infernal Mobs', '''
# Infernal Mobs

Hostile mobs have a chance to spawn as **Elite**, **Ultra** or **Infernal**, with 1-several of 21 modifiers
(more health, regeneration, lifesteal, explosions, webs...). They drop much better loot.
The [Extreme Entity Crusher](../multiblocks/extreme_entity_crusher.md) rolls infernal loot too, and **sigils** raise
the odds.
''', parent='automation/automation-index.md', pos=30, icon='minecraft:zombie_head')

# ------------------------------------------------------------------ items
page('items/items-index.md', 'Items and Tools', '''
# Items and Tools

<SubPages />
''', pos=80, icon='gtapexadditions:structure_upgrader')

page('items/structure-upgrader.md', 'Structure Upgrader', '''
# Structure Upgrader

<ItemImage id="gtapexadditions:structure_upgrader" scale="2" />

Right-click a multiblock controller to build it, or to upgrade every tiered part (coils, glass, casings, generator
tiers...) at once. Staged multis (the Miracle Top, Bloody Hell, Mega Distillation Tower...) grow stage by stage.
Its advisor tells you which part is holding a multi back.
''', parent='items/items-index.md', pos=10, icon='gtapexadditions:structure_upgrader', items=['gtapexadditions:structure_upgrader'])

page('items/angel-ring.md', 'Angel Ring', '''
# Angel Ring

<ItemImage id="gtapexadditions:angel_ring" scale="2" />

Creative flight while worn (Curios). Made with Aether gravitite.

<GTRecipesFor id="gtapexadditions:angel_ring" limit="1" />
''', parent='items/items-index.md', pos=20, icon='gtapexadditions:angel_ring', items=['gtapexadditions:angel_ring'])
