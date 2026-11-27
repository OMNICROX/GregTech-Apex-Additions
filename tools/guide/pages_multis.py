from pages_core import page

# id, title, tier, origin, category, description
M = [
 ('mystical_cultivation_array', 'Mystical Cultivation Array', 'LV', 'Apex', 'Farming',
  'A crop farm for Mystical Agriculture and the GregTech crops. Seeds go in its Seed Hatches (up to 2) and are never used up; each seed is one crop. Its output bus spot also takes an ME Output Bus.'),
 ('extreme_entity_crusher', 'Extreme Entity Crusher', 'MV', 'KubaTech (GTNH)', 'Farming',
  'A mob farm. Put a spawn egg or a Hostile Neural Networks data model in an input bus (not used up) and a sword (for looting) if you like: every cycle rolls the mob\'s loot many times in parallel. Power scales with the mob\'s health; bosses are refused. A data model\'s tier adds bonus loot rolls. Hostile mobs can roll as infernal (sigils raise the odds).'),
 ('industrial_rock_breaker', 'Industrial Rock Breaker', 'MV', 'GT++ (GTNH)', 'Processing',
  'Makes stone blocks from lava and water. A programmed circuit picks the block: 1 cobblestone, 2 stone, 3 obsidian (uses redstone), 4 basalt.'),
 ('library_of_ruina', 'Library of Ruina', 'IV', 'GT-Not-Leisure', 'Farming',
  'Makes Twilight Forest boss loot in bulk. Built with the Twilight bosses\' trophies, so you explore the Twilight Forest at HV and harvest it at IV.'),
 ('bloody_hell', 'Bloody Hell', 'IV', 'Twist Space Technology', 'Magic',
  'Every Blood Altar, Alchemy Table and Alchemy Array recipe in bulk, soaked from a pool of Life Essence inside the structure (tiers 3-6). Pipe Life Essence into an input hatch: the pool fills itself from it, 1,000 mB per block, up to 64 blocks a second, and the machine shows how much is still missing. Recipes start once the pool is full. Circuit 1 = altar, 2 = alchemy table (needs a Blood Orb in a bus), 11 = binding rituals. Tiers 1-6 give tier-cubed parallels and a higher effective altar tier; blood runes speed it up.'),
 ('alfheim_teleportation_array', 'Alfheim Teleportation Array', 'IV', 'GT-Not-Leisure', 'Magic',
  'Every Botania altar craft (mana pool, runic altar, pure daisy, petal apothecary, terrestrial agglomeration, elven trade, Mythic Botany) as a GregTech recipe. After the Gaia Guardian, recipes with Gaia Spirit are made only here. Runes and catalysts are kept.'),
 ('component_assembly_line', 'Component Assembly Line', 'IV', 'GoodGenerator (GTNH)', 'Assembly',
  'Assembles basic components (motors, pumps, pistons, conveyors, robot arms, emitters, sensors, field generators) in large batches. Its Component Assembly Line Casing tier limits which tiers it can make.'),
 ('large_naquadah_reactor', 'Large Naquadah Reactor', 'IV', 'GoodGenerator (GTNH)', 'Power',
  'Generates power from liquid nuclear fuel or liquid naquadah fuel. It needs liquid air every second or it voids the fuel; boosting fluids raise its output.'),
 ('extreme_craft_center', 'Extreme Craft Center', 'IV', 'Twist Space Technology', 'Automation',
  'An ME crafting provider: crafting patterns run inside it with pattern magnification, so it acts like a huge set of molecular assemblers on your AE2 network.'),
 ('nether_interface', 'Nether Interface', 'IV', 'Twist Space Technology', 'Processing',
  'Pulls Nether materials (netherrack, lava, quartz, glowstone...) from the Nether with power.'),
 ('silicon_rock_synthesizer', 'Silicon Rock Synthesizer', 'IV', 'Twist Space Technology', 'Processing',
  'Makes silicon-based rock and silicon dust from basic inputs in bulk.'),
 ('circuit_assembly_line', 'Circuit Assembly Line', 'LuV', 'GT++ / GTNH', 'Assembly',
  'Circuits in bulk. Imprinted by the first circuit it makes (imprints are permanent), it then makes that circuit from its parts much cheaper.'),
 ('draconic_fusion_crafting', 'Draconic Fusion Crafting', 'LuV', 'GT-Not-Leisure', 'Magic',
  'Draconic Evolution\'s fusion crafting as a multiblock: no injectors to set up, parallels from its casings.'),
 ('lightning_spire', 'Lightning Spire', 'LuV', 'Apex', 'Power',
  'Holds up to 512 Spire Lightning Rods (feed them through an input bus). Every 12.8 s each rod adds 28,000,000 EU to its buffer, which drains through dynamo hatches.'),
 ('laser_meteor_miner', 'Laser Meteor Miner', 'LuV', 'Twist Space Technology', 'Processing',
  'Mines a meteor (or anything) far above it with a laser. Tier 1 mines one block, tier 2 a row, tier 3 a layer. A Botania Terra Shatterer in the input bus gives fortune.'),
 ('ore_processing_factory', 'General Ore Processing Factory', 'LuV', 'Twist Space Technology', 'Processing',
  'Ore processing in one step: raw ore in, dusts and byproducts out, with big parallels.'),
 ('matter_fabricator', 'Matter Fabricator', 'ZPM', 'GT-Not-Leisure', 'Processing',
  'Makes UU-matter and scrap-based matter in bulk.'),
 ('exothermic_hearth', 'Exothermic Hearth', 'ZPM', 'GT5-Unofficial', 'Processing',
  'A huge blast furnace that heats itself.'),
 ('mega_distillation_tower', 'Mega Distillation Tower', 'ZPM', 'GTNH', 'Processing',
  'Distillation with up to 256 parallels. One fluid output hatch per layer, 2 to 13 layers (it grows in stages).'),
 ('eden_garden', 'Eden Garden', 'ZPM', 'GT-Not-Leisure', 'Farming',
  'A greenhouse that runs greenhouse recipes 100x faster.'),
 ('spinmatron', 'Spinmatron-2737', 'UV', 'GT5-Unofficial', 'Processing',
  'An industrial centrifuge with huge parallels.'),
 ('electric_implosion_compressor', 'Electric Implosion Compressor', 'UV', 'GTNH', 'Processing',
  'Runs every implosion compressor recipe without explosives. The 27 containment blocks set the parallels.'),
 ('nanochip_assembly_complex', 'Nanochip Assembly Complex', 'UV', 'GTNH', 'Assembly',
  'Every circuit recipe from the circuit assembler and the assembly line (circuits only), in bulk.'),
 ('integrated_assembly_matrix', 'Integrated Assembly Matrix', 'UV', 'Twist Space Technology', 'Assembly',
  'A huge assembler and assembly line in one.'),
 ('naquadah_fuel_refinery', 'Naquadah Fuel Refinery', 'UV', 'GoodGenerator (GTNH)', 'Power',
  'Produces naquadah fuels; 64 parallels per Field Restriction Coil tier.'),
 ('space_elevator', 'Space Elevator', 'UHV', 'GTNH', 'Space',
  'Elevates to space. Up to 12 modules: mining drones, pumps and the Space Assembler (which makes UIV+ components and GTNA\'s capstones).'),
 ('quantum_force_transformer', 'Quantum Force Transformer', 'UHV', 'GT++ (GTNH)', 'Processing',
  'Whole processing lines in one step. Every recipe needs a catalyst in an input bus (not consumed).'),
 ('black_hole_compressor', 'Pseudostable Black Hole Containment Field', 'UHV', 'GTNH', 'Processing',
  'A compressor and neutronium compressor powered by a black hole. Insert a Black Hole Seed to open it at 100 stability; stability drops over time; a Black Hole Collapser closes it.'),
 ('miracle_top', 'Miracle Top', 'UHV', 'Twist Space Technology', 'Assembly',
  'A circuit assembler built from a ring section and a conveying section, 2-16 rings (grows in stages).'),
 ('super_water_purifier', 'Super Water Purifier', 'UHV', 'Twist Space Technology', 'Processing',
  'Every grade of purified water in one machine, no units. A programmed circuit picks the grade (1-8).'),
 ('singularity_data_hub', 'Singularity Data Hub', 'UHV', 'GT-Not-Leisure', 'Automation',
  'Bulk item storage on your AE2 network. Its vault survives being broken (saved with the controller). Runs on wireless power if it has no energy hatch.'),
 ('crystalline_infinitier', 'Crystalline Infinitier', 'UHV', 'Twist Space Technology', 'Processing',
  'Grows crystals and gems in bulk.'),
 ('fuel_refining_complex', 'Fuel Refining Complex', 'UHV', 'GoodGenerator (GTNH)', 'Power',
  'Refines light and heavy naquadah fuel and atomic separation catalyst straight into naquadah fuel MkI-MkVI.'),
 ('antimatter_forge', 'SASS (Antimatter Forge)', 'UEV', 'GTNH', 'Power',
  'The Semi-Stable Antimatter Stabilization Sequencer converts protomatter into antimatter.'),
 ('antimatter_generator', 'SLAM (Antimatter Generator)', 'UEV', 'GTNH', 'Power',
  'The Shielded Lagrangian Annihilation Matrix generates energy by reacting semi-stable antimatter with matter.'),
 ('plasma_forge_prototype_mk2', 'DTPF Prototype MK-II', 'UEV', 'Twist Space Technology', 'Processing',
  'The Dimensionally Transcendent Plasma Forge prototype, huge (65x43x65): runs its recipes, parallel hatches add up to 16 parallels.'),
 ('micro_spacetime_fabricatorio', 'Micro SpaceTime Fabricatorio', 'UIV', 'Twist Space Technology', 'Processing',
  'Quantum anomalies and nuclear stars in bulk.'),
 ('strange_matter_aggregator', 'Strange Matter Aggregator', 'UIV', 'Twist Space Technology', 'Power',
  'Breeds antimatter. 3-8 rings of GTNA field generators set its tier; feed hydrogen plasma, Nuclear Star constrainers and Stellar Alloy frames.'),
 ('mass_fabricator_genesis', 'Mass Fabricator Genesis', 'UIV', 'Twist Space Technology', 'Processing',
  'UU-matter and fabrication with perfect overclocks; parallel hatches add up.'),
 ('divine_isa_forge', 'Divine Isa Forge', 'UXV', '123Technology', 'Processing',
  'Two IsaMills and a Space Assembler in one: ore milling at the end of the game.'),
 ('real_annihilation_generator', 'Real Annihilation Generator', 'UXV', 'GT-Not-Leisure', 'Power',
  'Antimatter to wireless EU, in three field-generator tiers.'),
 ('purification_plant', 'Water Purification Plant', 'UV', 'GT5-Unofficial', 'Processing',
  'Main controller for the eight purification units (clarifier, ozonation, flocculation, pH adjustment, plasma heater, UV treatment, degasser, baryonic perfection). Every unit within 32 blocks links to it; each makes the next grade of purified water.'),
]

page('multiblocks/multiblocks-index.md', 'Apex Multiblocks', '''
# Apex Multiblocks

Every multiblock Apex Ascension adds, ported from GT New Horizons and its addons (credits in each page).
To see one in 3D, press **U** on its controller in JEI (see [Seeing multiblocks](../getting-started/multiblock-preview.md)).

| Multiblock | Tier | Kind |
|---|---|---|
''' + '\n'.join(f'| [{t}]({i}.md) | {tier} | {cat} |' for i, t, tier, o, cat, d in M) + '''

Also in Apex: the Compact Fusion Computers MK-I to MK-V (GoodGenerator), the Quantum Force Transformer catalysts,
and GTNH multis from GT++, GoodGenerator, KubaTech, GT-Not-Leisure, Twist Space Technology and 123Technology.
''', pos=70, icon='gtapexadditions:extreme_entity_crusher')

for i, (mid, title, tier, origin, cat, desc) in enumerate(M):
    page(f'multiblocks/{mid}.md', title, f'''
# {title}

<Multiblock id="gtapexadditions:{mid}" />

**Tier:** {tier} · **Kind:** {cat} · **From:** {origin}

{desc}

## Building it

The structure above works like the JEI multiblock page: left drag rotates, right drag or the scroll wheel zooms,
middle drag pans; the sliders show one layer or another tier, **H** shows hatch spots, and the row underneath lists
every block you need (click one for its recipe). The [Structure Upgrader](../items/structure-upgrader.md) builds it for you.

## Controller recipe

<GTRecipesFor id="gtapexadditions:{mid}" limit="1" fallbackText="See JEI for the controller recipe." />
''', parent='multiblocks/multiblocks-index.md', pos=10 + i, icon=f'gtapexadditions:{mid}', items=[f'gtapexadditions:{mid}'],
         cats=[cat.lower()])
