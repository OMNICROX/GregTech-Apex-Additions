from pages_core import page

page('progression/progression-index.md', 'Progression', '''
# Progression

One chapter per voltage tier. Each says what to aim for, which machines and materials matter, which worlds open up
and which Apex multiblocks you can build.

<SubPages />
''', pos=20, icon='gtceu:lv_machine_hull')

page('progression/tier-ladder.md', 'The Tier Ladder', '''
# The Tier Ladder

| Tier | Key goal | New worlds / mods | Apex multis |
|---|---|---|---|
| Steam | Bronze, steel, steam machines | Nether, Botania basics | |
| LV | First electric machines, EBF | AE2 basics, Blood Magic (weak orb) | Mystical Cultivation Array |
| MV | Aluminium, chemistry | Aether ores, AE2 autocrafting, Apprentice orb, Hostile Neural Networks | Extreme Entity Crusher, Industrial Rock Breaker |
| HV | Stainless steel, cleanroom, oil | **Twilight Forest**, Moon, Gaia Guardian | |
| EV | Titanium, tungstensteel | Mars, Undergarden ores, Master orb | |
| IV | Tungstensteel, platinum line, Assembly Line | End ores, Venus / Mercury, Archmage orb, Alfheim Array | Library of Ruina, Bloody Hell, Component Assembly Line, Large Naquadah Reactor, Extreme Craft Center, Nether Interface, Silicon Rock Synthesizer |
| LuV | Fusion MK-I, naquadah | Glacio, Draconic (Wyvern) | Circuit Assembly Line, Draconic Fusion Crafting, Lightning Spire, Laser Meteor Miner, Ore Processing Factory |
| ZPM | Fusion MK-II | Jupiter, MEGA cells | Matter Fabricator, Exothermic Hearth, Mega Distillation Tower, Eden Garden |
| UV | Fusion MK-III, infinity | Saturn, Draconic (Chaotic) | Spinmatron, Electric Implosion Compressor, Nanochip Complex, Integrated Assembly Matrix, Naquadah Fuel Refinery |
| UHV | Space Elevator | Uranus, Neptune | QFT, Black Hole Compressor, Miracle Top, Super Water Purifier, Singularity Data Hub, Crystalline Infinitier, Fuel Refining Complex |
| UEV+ | Antimatter, the plasma forge | Avaritia endgame | SASS + SLAM, DTPF MK-II, Micro SpaceTime Fabricatorio, Strange Matter Aggregator, Mass Fabricator Genesis, Divine Isa Forge, Real Annihilation Generator |
''', parent='progression/progression-index.md', pos=5, icon='gtceu:uv_machine_hull')

STEAM_SCENES = '''
## Coke Oven

<Multiblock id="gtceu:coke_oven" />

A 3x3x3 of <ItemLink id="gtceu:coke_oven_bricks" /> with a hollow middle and the <ItemLink id="gtceu:coke_oven" />
in the middle of one side. Coal becomes **coal coke** (a better fuel) and **creosote**. A
<ItemLink id="gtceu:coke_oven_hatch" /> in the wall lets pipes and hoppers work with it.

## Primitive Blast Furnace

<Multiblock id="gtceu:primitive_blast_furnace" />

<ItemLink id="gtceu:firebricks" />, 3 wide, 4 tall, hollow in the middle and open on top, controller low on one side.
Iron + coal / coal coke / charcoal -> **steel** (and ash). Slow, so build several.
'''

page('progression/00-steam.md', 'Stone Age and Steam', '''
# Stone Age and Steam

**Goal:** steel, bronze, a steam setup, and the materials for your first LV machines.

## Checklist

1. Tools from flint and iron; a <ItemLink id="gtceu:iron_hammer" /> for plates.
2. A **Tinkers smeltery** (lava fuel) for early metal parts.
3. **Coke Oven** -> coal coke and creosote. **Primitive Blast Furnace** -> steel.
4. **Bronze** (copper + tin) and the **steam machines**: a solid-fuel boiler
   (<ItemLink id="gtceu:lp_steam_solid_boiler" />), then <ItemLink id="gtceu:lp_steam_macerator" />,
   <ItemLink id="gtceu:lp_steam_extractor" />, <ItemLink id="gtceu:lp_steam_compressor" />,
   <ItemLink id="gtceu:lp_steam_forge_hammer" />, <ItemLink id="gtceu:lp_steam_alloy_smelter" />.
5. Upgrade to **high pressure** (steel) steam machines. Rubber (from rubber trees or later chemistry) for cables.
6. Visit the **Nether**: sulfur, quartz, redstone, saltpeter veins; blazes for blaze rods.
7. **Botania basics** work at steam level: Pure Daisy, Petal Apothecary, Mana Pool, generating flowers
   (see [Botania](../magic/botania.md)).

![A steam start: a coke oven, a coal boiler piping steam into four steam machines, and a Primitive Blast Furnace](../assets/images/steam_setup.png)

The steam-age multis from GTNA (large steam machines) are good, but the Steam Lava Maker and the
Dimensionally Transcendent Steam Oven are LV / MV recipes in Apex.
''' + STEAM_SCENES, parent='progression/progression-index.md', pos=10, icon='gtceu:lp_steam_solid_boiler')

def tier(path, title, pos, icon, body):
    page('progression/' + path, title, body, parent='progression/progression-index.md', pos=pos, icon=icon)

tier('01-lv.md', 'LV (32 V)', 20, 'gtceu:lv_machine_hull', '''
# LV - Low Voltage (32 V)

**Goal:** your first electric machines, a steady power supply and the Electric Blast Furnace.

## Checklist

1. <ItemLink id="gtceu:lv_machine_hull" /> needs steel and tin cables. **LV circuits**:
   <ItemLink id="gtceu:basic_electronic_circuit" /> (vacuum tubes, resistors, boards).
2. Power: <ItemLink id="gtceu:lv_steam_turbine" /> off your boilers, or <ItemLink id="gtceu:lv_combustion" /> on
   biodiesel / creosote / oil fuels. Store it in a battery buffer.

   ![LV power: two steam turbines and a battery buffer feeding four machines over one tin cable](../assets/images/lv_power.png)
3. Machines: Macerator, Ore Washer, Centrifuge, Electrolyzer, Wiremill, Bender, Lathe, Extruder, **Assembler**
   (most mods' recipes run in it), Chemical Reactor, Fluid Solidifier.
4. The <ItemLink id="gtceu:electric_blast_furnace" /> with cupronickel coils (see
   [EBF and coils](../gregtech/ebf-and-coils.md)). Two LV energy hatches let it run MV recipes like aluminium early.
5. Rubber in bulk (Chemical Reactor + sulfur) for insulated cables.

## Unlocks

* **AE2** network basics: 1k storage, buses, terminals (no autocrafting yet). See [AE2 in Apex](../automation/ae2.md).
* **Blood Magic**: the [Weak Blood Orb](../magic/blood-magic.md) from an LV Raw Blood Orb.
* **Botania**: Runic Altar, mechanical daisy / apothecary.
* [Mystical Cultivation Array](../multiblocks/mystical_cultivation_array.md): crop farming multiblock.
* Hostile Neural Networks' Deep Learner and Prediction Matrix (handcrafted).

<GTRecipesFor id="gtceu:lv_machine_hull" limit="1" />
''')

tier('02-mv.md', 'MV (128 V)', 30, 'gtceu:mv_machine_hull', '''
# MV - Medium Voltage (128 V)

**Goal:** aluminium, chemistry, and automation.

## Checklist

1. **Aluminium**: EBF (1,700 K). The best aluminium source is the **Aether's bauxite veins**
   (see [Aether](../dimensions/aether.md)); sapphire, ruby and mica also give some.
2. <ItemLink id="gtceu:mv_machine_hull" />, MV circuits (<ItemLink id="gtceu:good_electronic_circuit" />,
   <ItemLink id="gtceu:good_integrated_circuit" />).
3. Chemistry: Chemical Reactor lines (sulfuric acid, nitric acid, polyethylene), Distillery, Electrolyzer.
4. A Cleanroom comes next; start collecting plascrete materials.
5. [Borosilicate Glass](../gregtech/glass-tiers.md) (MV fluid solidifier) for HV multis.

## Unlocks

* **AE2 autocrafting**: crafting CPU, pattern providers, molecular assemblers, interfaces, 4k / 16k cells, P2P.
* **Blood Magic**: Apprentice orb (MV Raw Blood Orb).
* **Botania**: Terrestrial Agglomeration Plate (Terrasteel), Alfheim Portal.
* **Hostile Neural Networks**: Simulation Chamber (MV assembler); predictions go into a GT Extractor.
* [Extreme Entity Crusher](../multiblocks/extreme_entity_crusher.md): mob farming with spawn eggs or data models.
* [Industrial Rock Breaker](../multiblocks/industrial_rock_breaker.md).
* The Dimensionally Transcendent Steam Oven (GTNA) is an MV recipe.

<GTRecipesFor id="gtceu:mv_machine_hull" limit="1" />
''')

tier('03-hv.md', 'HV (512 V)', 40, 'gtceu:hv_machine_hull', '''
# HV - High Voltage (512 V)

**Goal:** stainless steel, the cleanroom, oil processing, and a whole new world.

## Checklist

1. **Stainless steel** (EBF, Kanthal coils later). <ItemLink id="gtceu:hv_machine_hull" />.
2. <ItemLink id="gtceu:cleanroom" /> for microprocessor wafers. HV circuits:
   <ItemLink id="gtceu:advanced_integrated_circuit" />, <ItemLink id="gtceu:micro_processor" />.
3. Oil: <ItemLink id="gtceu:distillation_tower" />, fuels for <ItemLink id="gtceu:large_combustion_engine" />.
4. <ItemLink id="gtceu:vacuum_freezer" /> for hot ingots, <ItemLink id="gtceu:large_chemical_reactor" />.
5. An <ItemLink id="gtceu:hv_field_generator" /> opens the **Twilight Forest**.

## Unlocks

* **[Twilight Forest](../dimensions/twilight-forest.md)**: the portal is lit with an HV Field Generator. Sapphire and
  olivine veins only spawn there. Its bosses' trophies build the IV [Library of Ruina](../multiblocks/library_of_ruina.md).
* **Moon** (tier-1 rocket): see [Space](../dimensions/space.md).
* **Blood Magic**: Magician orb. **Botania**: Gaia pylons, the **Gaia Guardian** fight around HV / EV.
* AE2's default tier is HV: most remaining AE2 items.
* The Eye of Wood (Twilight) is an HV recipe.

<GTRecipesFor id="gtceu:hv_machine_hull" limit="1" />
''')

tier('04-ev.md', 'EV (2,048 V)', 50, 'gtceu:ev_machine_hull', '''
# EV - Extreme Voltage (2,048 V)

**Goal:** titanium, tungsten, and the first big multis.

## Checklist

1. **Titanium** (rutile / ilmenite -> titanium tetrachloride -> magnesium reduction, EBF). <ItemLink id="gtceu:ev_machine_hull" />.
2. **Tungstensteel** needs tungsten from scheelite / tungstate (End, Mars) and Nichrome coils (3,600 K).
3. <ItemLink id="gtceu:implosion_compressor" /> for gems, nano processors (<ItemLink id="gtceu:nano_processor" />).
4. Titanium Reinforced Borosilicate Glass for EV-hatch multis.

## Unlocks

* **Mars** (tier-2 rocket, desh): tungsten veins. Gravity normalizer (needs Aether gravitite).
* **[Undergarden](../dimensions/undergarden.md)** ore veins: uranium, thorium, rare earths, tantalite.
* **Blood Magic**: Master orb. **Botania**: Alfsteel pylon, Elven altar.
* AE2: quantum link / ring, spatial storage, big crafting accelerators.

<GTRecipesFor id="gtceu:ev_machine_hull" limit="1" />
''')

tier('05-iv.md', 'IV (8,192 V)', 60, 'gtceu:iv_machine_hull', '''
# IV - Insane Voltage (8,192 V)

**Goal:** the platinum line, the Assembly Line and the first wave of Apex multis.

## Checklist

1. **Platinum group** from sheldonite (End) and byproducts: platinum, palladium, then iridium / osmium / rhodium / ruthenium.
2. <ItemLink id="gtceu:iv_machine_hull" />, crystal processors later. HSS-G coils.
3. <ItemLink id="gtceu:assembly_line" /> (research data for some recipes).

   <Multiblock id="gtceu:assembly_line" />

   ![An Assembly Line: controller, data access hatch, laminated glass and an input hatch on the front, input buses underneath](../assets/images/assembly_line.png)
4. Power: <ItemLink id="gtceu:steam_large_turbine" /> / <ItemLink id="gtceu:gas_large_turbine" />, or the
   [Large Naquadah Reactor](../multiblocks/large_naquadah_reactor.md).

## Unlocks

* **End** ores (platinum, naquadah, tungsten), **Venus / Mercury** (ostrum rockets), the jet suit.
* **Blood Magic**: Archmage orb, [Bloody Hell](../multiblocks/bloody_hell.md).
* **Botania** post-Gaia on the [Alfheim Teleportation Array](../multiblocks/alfheim_teleportation_array.md).
* Draconic Evolution base tier. AE2: AdvancedAE quantum computer, very large cells.
* Apex multis: [Library of Ruina](../multiblocks/library_of_ruina.md), [Component Assembly Line](../multiblocks/component_assembly_line.md),
  [Extreme Craft Center](../multiblocks/extreme_craft_center.md), [Nether Interface](../multiblocks/nether_interface.md),
  [Silicon Rock Synthesizer](../multiblocks/silicon_rock_synthesizer.md).

<GTRecipesFor id="gtceu:iv_machine_hull" limit="1" />
''')

tier('06-luv.md', 'LuV (32,768 V)', 70, 'gtceu:luv_machine_hull', '''
# LuV - Ludicrous Voltage (32,768 V)

**Goal:** fusion and naquadah.

1. <ItemLink id="gtceu:luv_fusion_reactor" />: plasmas for turbines and materials (europium, americium...).
2. Naquadah processing; <ItemLink id="gtceu:luv_machine_hull" />, wetware begins (sterile cleanroom).
3. Rhodium-Plated Palladium Reinforced Borosilicate Glass.

**Unlocks:** Glacio (calorite rockets), Draconic Evolution Wyvern tier, the
[Circuit Assembly Line](../multiblocks/circuit_assembly_line.md), [Draconic Fusion Crafting](../multiblocks/draconic_fusion_crafting.md),
[Lightning Spire](../multiblocks/lightning_spire.md), [Laser Meteor Miner](../multiblocks/laser_meteor_miner.md),
[General Ore Processing Factory](../multiblocks/ore_processing_factory.md).

<GTRecipesFor id="gtceu:luv_machine_hull" limit="1" />
''')

tier('07-zpm.md', 'ZPM (131,072 V)', 80, 'gtceu:zpm_machine_hull', '''
# ZPM - Zero Point Module (131,072 V)

1. <ItemLink id="gtceu:zpm_fusion_reactor" /> (MK-II), <ItemLink id="gtceu:zpm_machine_hull" />.
2. Iridium Reinforced Borosilicate Glass.

**Unlocks:** Jupiter and its moons (tier-5 rockets), Draconic tier, MEGA cells (AE2), the
[Matter Fabricator](../multiblocks/matter_fabricator.md), [Exothermic Hearth](../multiblocks/exothermic_hearth.md),
[Mega Distillation Tower](../multiblocks/mega_distillation_tower.md), [Eden Garden](../multiblocks/eden_garden.md).

<GTRecipesFor id="gtceu:zpm_machine_hull" limit="1" />
''')

tier('08-uv.md', 'UV (524,288 V)', 90, 'gtceu:uv_machine_hull', '''
# UV - Ultimate Voltage (524,288 V)

1. <ItemLink id="gtceu:uv_fusion_reactor" /> (MK-III), <ItemLink id="gtceu:uv_machine_hull" />.
2. **Infinity** (as in GTNH): the ingot is still crafted in Avaritia's extreme crafting table, but it is GregTech
   Infinity, and every GT recipe that shapes it runs at UV (fluid solidifier parts).
3. Osmium Reinforced Borosilicate Glass.

**Unlocks:** Saturn, Chaotic Draconic, MEGA 64M/256M and InsaneAE basics, the [Spinmatron-2737](../multiblocks/spinmatron.md),
[Electric Implosion Compressor](../multiblocks/electric_implosion_compressor.md),
[Nanochip Assembly Complex](../multiblocks/nanochip_assembly_complex.md),
[Integrated Assembly Matrix](../multiblocks/integrated_assembly_matrix.md),
[Naquadah Fuel Refinery](../multiblocks/naquadah_fuel_refinery.md).

<GTRecipesFor id="gtceu:uv_machine_hull" limit="1" />
''')

tier('09-uhv.md', 'UHV (2,097,152 V)', 100, 'gtceu:uhv_machine_hull', '''
# UHV - Ultra High Voltage

1. <ItemLink id="gtceu:uhv_machine_hull" />, optical circuits start (GTCA, see [Circuits](../gregtech/circuits.md)).
2. The [Space Elevator](../multiblocks/space_elevator.md) and its modules: space mining, the Space Assembler
   (UIV+ components and GTNA's capstones are made there).
3. Neutronium Reinforced Borosilicate Glass.

**Unlocks:** Uranus and Neptune (tier-7 rockets), the [Quantum Force Transformer](../multiblocks/quantum_force_transformer.md),
[Pseudostable Black Hole Containment Field](../multiblocks/black_hole_compressor.md), [Miracle Top](../multiblocks/miracle_top.md),
[Super Water Purifier](../multiblocks/super_water_purifier.md), [Singularity Data Hub](../multiblocks/singularity_data_hub.md),
[Crystalline Infinitier](../multiblocks/crystalline_infinitier.md), [Fuel Refining Complex](../multiblocks/fuel_refining_complex.md).

<GTRecipesFor id="gtceu:uhv_machine_hull" limit="1" />
''')

tier('10-uev-plus.md', 'UEV and Beyond', 110, 'gtceu:uev_machine_hull', '''
# UEV and Beyond (UEV, UIV, UXV, OpV, MAX)

The endgame: antimatter, spacetime and the Avaritia finale.

* **Antimatter**: the [SASS](../multiblocks/antimatter_forge.md) makes it, the [SLAM](../multiblocks/antimatter_generator.md)
  burns it for huge power; the [Real Annihilation Generator](../multiblocks/real_annihilation_generator.md) (UXV) sends it wireless.
* The [DTPF MK-II](../multiblocks/plasma_forge_prototype_mk2.md) (UEV), the [Micro SpaceTime Fabricatorio](../multiblocks/micro_spacetime_fabricatorio.md),
  [Strange Matter Aggregator](../multiblocks/strange_matter_aggregator.md) and [Mass Fabricator Genesis](../multiblocks/mass_fabricator_genesis.md) (UIV),
  the [Divine Isa Forge](../multiblocks/divine_isa_forge.md) (UXV).
* GTNA's capstones (Eye of Harmony, Forge of the Gods, Transcendent Plasma Mixer) come from the Space Assembler.
* Cosmic and Transcendent circuits; Infinity / Transcendent / Universium glass.
* **Avaritia**: the extreme crafting table needs a Nether Star (Wither), the neutronium compressor needs a Void
  Thorn and a Blazing Eye (Bosses of Mass Destruction) and a Terrasteel block; the Infinity Catalyst needs every
  singularity plus Botania's top materials.
''')
