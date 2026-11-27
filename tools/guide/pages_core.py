# Apex Guide pages: start, GregTech basics, mechanics. Each entry: path -> (frontmatter dict, body markdown)
P = {}

def page(path, title, body, parent=None, pos=0, icon=None, items=None, cats=None):
    P[path] = dict(title=title, body=body.strip('\n') + '\n', parent=parent, pos=pos, icon=icon, items=items or [], cats=cats or [])

# ------------------------------------------------------------------ index
page('index.md', 'Welcome', '''
# Apex Ascension

Apex Ascension is a GregTech pack in the spirit of **GT New Horizons**: progress is measured in **voltage tiers**
(Steam, LV, MV, HV ... up to MAX), and almost every other mod (AE2, Botania, Blood Magic, Ad Astra, the Aether,
the Twilight Forest, Draconic Evolution, Avaritia...) is tied into those tiers. This guide tells you what to do next,
how the machines work and what every Apex multiblock does.

## How to use this guide

* The sidebar on the left is the table of contents. Use the search box at the top to find anything.
* Hold **G** while hovering over an item anywhere (inventory, JEI) to jump to its page, if it has one.
* Recipes in this guide come straight from the game, so they are always the ones you can actually make.
  Click an item to see it in JEI; press **R** / **U** over it for recipes / uses.
* Pages marked with a screenshot show a setup you can copy block by block.

## Where to start

1. [Your first days](getting-started/first-days.md): tools, food, the first base.
2. [The tier ladder](progression/tier-ladder.md): what each voltage tier unlocks, at a glance.
3. [Stone Age and Steam](progression/00-steam.md): coke ovens, bronze, steel, steam machines.
4. Then follow the [progression chapters](progression/progression-index.md) tier by tier.

## Chapters

* [Getting Started](getting-started/getting-started-index.md)
* [Progression](progression/progression-index.md): one page per voltage tier
* [GregTech Mechanics](gregtech/gregtech-index.md): power, cables, overclocking, ore processing, multiblocks
* [Processing Lines](lines/lines-index.md): oil, titanium, platinum, fusion, naquadah, antimatter...
* [Dimensions](dimensions/dimensions-index.md): which world has which ores, and how to get there
* [Magic](magic/magic-index.md): Blood Magic, Botania, Mystical Agriculture
* [Automation](automation/automation-index.md): AE2, Hostile Neural Networks, mob farming
* [Apex Multiblocks](multiblocks/multiblocks-index.md): every multiblock this pack adds
* [Items and Tools](items/items-index.md)
''', pos=0, icon='minecraft:book')

# ------------------------------------------------------------------ getting started
page('getting-started/getting-started-index.md', 'Getting Started', '''
# Getting Started

<SubPages />
''', pos=10, icon='minecraft:wooden_pickaxe')

page('getting-started/first-days.md', 'Your First Days', '''
# Your First Days

GregTech changes a lot of vanilla crafting, so the first hours feel different from other packs.

## The first steps

1. **Wood, flint and stone.** Gather logs, make a crafting table, then flint tools. Iron is harder to use than
   usual: GregTech tools and most metal parts need **plates** and **rods**, which you make with a hammer and a file.
2. **GregTech tools.** A <ItemLink id="gtceu:iron_hammer" /> flattens ingots into plates on the crafting table,
   a file makes rods, a saw cuts planks, a wrench picks up machines, a screwdriver and wire cutter configure them.
   Tools you craft keep working for a long time; better metals last longer.
3. **Tinkers' Construct** gives you a smeltery early. Its heat follows GregTech: lava melts metals up to MV,
   hotter fuels come later (see [Tinkers heat](../gregtech/tinkers-heat.md)).
4. **Food and safety.** Mobs are tougher than vanilla (and some are [infernal](../automation/infernal-mobs.md)).
   Build a shelter with light; Farmer's Delight and Apple Skin help with food.
5. **Find ores.** GregTech ores spawn in big **veins**, not scattered single blocks. Small surface rocks show what
   is below. Each vein has 3-4 related ores. See [Ore veins](../gregtech/ore-veins.md).

## Your first goal: Steel

Steel unlocks the steam age and the first real machines. Build a
[Coke Oven](../progression/00-steam.md#coke-oven) for coal coke and creosote, then a
[Primitive Blast Furnace](../progression/00-steam.md#primitive-blast-furnace) for steel.

## The Apex Guide item

You got this guide on your first join. If you lose it, craft a new one from a book and any planks.

<RecipeFor id="guideme:guide" fallbackText="(book + planks)" />
''', parent='getting-started/getting-started-index.md', pos=10, icon='minecraft:crafting_table')

page('getting-started/how-apex-is-different.md', 'How Apex Is Different', '''
# How Apex Is Different

Coming from other packs? These are the rules that change everything.

* **Voltage is the gate.** Most mods' crafting-table recipes are moved into the GregTech **Assembler** at a set
  voltage tier, with their original ingredients plus soldering alloy. An LV Assembler can't run an MV recipe, so
  you can't skip ahead. AE2, Botania, Ad Astra, Draconic Evolution, Iron Chests, backpacks and many more work this way.
* **Worlds have their own ores.** As in GTNH, some metals only come from one dimension: aluminium-rich veins in the
  Aether, sapphire and olivine in the Twilight Forest, uranium in the Undergarden, platinum in the End, tungsten on
  Mars... See [Dimensions](../dimensions/dimensions-index.md).
* **Glass has tiers.** Multiblocks built with glass take any tiered glass, and the **lowest** glass limits the
  energy hatch tier. See [Glass tiers](../gregtech/glass-tiers.md).
* **Lava is scarce.** No infinite lava loops; fire essence and late machines are your lava sources.
* **Long recipes are shortened.** Recipes over 15 minutes run on a log curve (an hour becomes ~23 minutes) but need
  more amps. See [Recipe time curve](../gregtech/duration-curve.md).
* **Boss keys.** Avaritia's extreme crafting and compressor need boss drops (Wither, Void Blossom, Gauntlet).
* **No free power.** Generators from other mods that skip GregTech were removed.
''', parent='getting-started/getting-started-index.md', pos=20, icon='gtceu:lv_assembler')

page('getting-started/multiblock-preview.md', 'Seeing Multiblocks', '''
# Seeing Multiblocks

Every multiblock has a 3D structure page in JEI (the NEI-style preview):

1. Hover the controller in JEI and press **U** (uses). The **Multiblock Structure** page shows the build.
2. **Left drag** rotates, **right drag** zooms, **middle drag** pans, the **scroll wheel** zooms too.
3. **Layer** slider: look at one layer at a time. **Tier** slider: switch coils, glass and other tiered parts.
4. **H** highlights where hatches can go. **R** resets the camera. **?** shows the controls.
5. The two arrow buttons switch between shapes (multis with several sizes or stages).
6. The row of blocks under the preview is everything you need for the shape and tier on screen (counts update).
   Click a block for its recipe, right-click for its uses.

## In this guide

Multiblock pages show the same structure in a 3D view beside the text:

* **Left drag** rotates, the **scroll wheel** zooms, **right or middle drag** pans. Right-click a block to see what
  that spot accepts.
* The buttons on the right: zoom in, zoom out, reset the view, next shape (multis with several sizes), show where
  hatches can go, and list every block you need.
* The sliders underneath switch the tier of coils, glass and other parts, or show one layer at a time.

To build it in the world, hold a <ItemLink id="gtceu:terminal" /> or use the
[Structure Upgrader](../items/structure-upgrader.md), which also upgrades an existing multi to better parts.
''', parent='getting-started/getting-started-index.md', pos=30, icon='gtceu:terminal')

# ------------------------------------------------------------------ GregTech mechanics
page('gregtech/gregtech-index.md', 'GregTech Mechanics', '''
# GregTech Mechanics

How GregTech works, from power to ore processing. These pages hold for the whole game.

<SubPages />
''', pos=30, icon='gtceu:lv_machine_hull')

page('gregtech/voltage-and-power.md', 'Voltage, Amps and Power', '''
# Voltage, Amps and Power

GregTech power is **EU**. Every machine has a **voltage tier**; a machine runs recipes up to its own tier.

| Tier | Voltage | Recipe power (VA) | Typical cable |
|---|---|---|---|
| ULV | 8 | 7 | Lead |
| LV | 32 | 30 | Tin |
| MV | 128 | 120 | Copper |
| HV | 512 | 480 | Gold |
| EV | 2,048 | 1,920 | Aluminium |
| IV | 8,192 | 7,680 | Platinum / Tungsten |
| LuV | 32,768 | 30,720 | Niobium-Titanium |
| ZPM | 131,072 | 122,880 | Vanadium-Gallium |
| UV | 524,288 | 491,520 | Yttrium Barium Cuprate |
| UHV | 2,097,152 | 1,966,080 | Europium |

Higher tiers (UEV, UIV, UXV, OpV, MAX) keep multiplying by 4.

## The rules

* **Too much voltage explodes machines.** Feeding a higher voltage than a machine's tier blows it up. Use a
  transformer between tiers.
* **Amps** are how many "packets" a cable carries. A cable carries a fixed amperage; machines only draw what they need.
* **Cable loss**: every block of cable loses a little EU per amp. Superconductors have no loss.
* **Batteries** in a battery buffer smooth out power.
* **FE machines**: GregTech cables power Forge Energy machines directly (4 FE per EU), so other mods' FE blocks run
  off GT power.

## Generators by stage

* Steam: boilers + steam turbines. Early LV: <ItemLink id="gtceu:lv_steam_turbine" />,
  <ItemLink id="gtceu:lv_combustion" /> (oil/fuels), <ItemLink id="gtceu:lv_gas_turbine" />.
* Mid game: <ItemLink id="gtceu:large_combustion_engine" />, <ItemLink id="gtceu:steam_large_turbine" />,
  <ItemLink id="gtceu:gas_large_turbine" />.
* Late game: fusion + <ItemLink id="gtceu:plasma_large_turbine" />, the
  [Large Naquadah Reactor](../multiblocks/large_naquadah_reactor.md), the
  [Lightning Spire](../multiblocks/lightning_spire.md), antimatter ([SLAM](../multiblocks/antimatter_generator.md)).
* [Wireless energy](wireless-energy.md) moves EU across the world without cables.
''', parent='gregtech/gregtech-index.md', pos=10, icon='gtceu:lv_battery_hull')

page('gregtech/overclocking.md', 'Overclocking', '''
# Overclocking

A machine of a higher tier than a recipe runs it faster:

* Each tier above the recipe: **4x the power, 2x the speed** (normal overclock).
* Some multis do **perfect overclocks**: 4x power, **4x** speed.
* The Electric Blast Furnace and other coil machines get extra bonuses from hotter coils
  (see [Coils and heat](ebf-and-coils.md)).

A recipe can't go faster than 1 tick. Check the machine's JEI page or tooltip for its overclock type.
''', parent='gregtech/gregtech-index.md', pos=20, icon='gtceu:mv_macerator')

page('gregtech/ore-veins.md', 'Ore Veins', '''
# Ore Veins

GregTech ores come in **veins**: large bodies of 3-4 related ores at certain heights. A vein shows on the surface as
small **indicator rocks** of its main ore.

* Use JEI's **Ore Veins** pages (search the ore and look at its uses) for each vein's heights, size and dimension.
* A **Prospector** (LV+) scans for veins around you.
* Some veins only exist in one world. In Apex this is a big part of progression; see
  [Dimensions](../dimensions/dimensions-index.md).

## Where things come from (summary)

| World | Notable veins |
|---|---|
| Overworld | Iron, copper, tin, coal, gold, lapis, redstone, galena, cassiterite, salts, mica, nickel... |
| Nether | Sulfur, quartz, redstone, saltpeter, tetrahedrite, beryllium, molybdenum, monazite |
| Aether | Bauxite / ilmenite / aluminium, mica / kyanite / pollucite, lithium, topaz |
| Twilight Forest | Sapphire (only here), olivine (only here), galena / silver, nickel / cobalt, molybdenum |
| Undergarden | Pitchblende / uraninite / thorium, rare earths, tantalite / pyrochlore, cinnabar |
| End | Platinum line (sheldonite), naquadah, scheelite / tungstate, bauxite, chromite, pitchblende |
| Moon, Mars, Venus, Mercury, Glacio | Planet veins (GTNA): tungsten on Mars, then heavier metals further out |
''', parent='gregtech/gregtech-index.md', pos=30, icon='gtceu:prospector.lv')

page('gregtech/ore-processing.md', 'Ore Processing', '''
# Ore Processing

Raw ore can be smelted for one ingot, but processing it gives 2-4x and **byproducts**.

1. **Macerator**: raw ore -> crushed ore (+ chance of an extra).
2. **Ore Washer** (water): crushed -> purified crushed ore + a byproduct dust. Or a Chemical Bath (mercury /
   sodium persulfate) for other byproducts.
3. **Thermal Centrifuge**: purified -> refined ore + byproduct.
4. **Macerator** again -> dust, then **Centrifuge** impure / purified dusts for more byproducts.
5. **Electric Blast Furnace** for metals that need heat; otherwise a furnace.

Shortcut later: the [General Ore Processing Factory](../multiblocks/ore_processing_factory.md) (LuV) does it in bulk.

![An early ore processing line](../assets/images/ore_processing_line.png)

Byproducts matter: gallium, indium, platinum group metals and rare earths mostly come from byproducts.
''', parent='gregtech/gregtech-index.md', pos=40, icon='gtceu:lv_macerator')

page('gregtech/ebf-and-coils.md', 'Electric Blast Furnace and Coils', '''
# Electric Blast Furnace and Coils

The <ItemLink id="gtceu:electric_blast_furnace" /> smelts metals that need heat. Its **coils** set the temperature.

| Coil | Heat | Unlocks |
|---|---|---|
| <ItemLink id="gtceu:cupronickel_coil_block" /> | 1,800 K | Aluminium, steel in bulk, silicon |
| <ItemLink id="gtceu:kanthal_coil_block" /> | 2,700 K | Stainless steel, titanium |
| <ItemLink id="gtceu:nichrome_coil_block" /> | 3,600 K | Tungstensteel, tungsten |
| <ItemLink id="gtceu:rtm_alloy_coil_block" /> | 4,500 K | HSS-G and higher |
| <ItemLink id="gtceu:hssg_coil_block" /> | 5,400 K | Naquadah, iridium |
| <ItemLink id="gtceu:naquadah_coil_block" /> | 7,200 K | Naquadah alloy, trinium |
| <ItemLink id="gtceu:trinium_coil_block" /> | 9,001 K | Late UV metals |
| <ItemLink id="gtceu:tritanium_coil_block" /> | 10,800 K | UHV metals |

* Every 900 K above a recipe's heat: 5% less energy. Every 1,800 K above: one **perfect** overclock.
* Use the energy hatch tier of the voltage you want to run; two hatches = 2 amps.

## Building it

<Multiblock id="gtceu:electric_blast_furnace" />

Bottom layer: casings with the controller, an input bus, an output bus, energy hatches and a maintenance hatch.
Two layers of coils (hollow middle), then a casing top with a **muffler hatch** in the middle facing up.

<GTRecipesFor id="gtceu:electric_blast_furnace" limit="1" />
''', parent='gregtech/gregtech-index.md', pos=50, icon='gtceu:electric_blast_furnace', items=['gtceu:electric_blast_furnace'])

page('gregtech/glass-tiers.md', 'Glass Tiers', '''
# Glass Tiers

As in GT New Horizons, glass is part of a multiblock's tier. A multi's glass spots take **any tiered glass**; the
**lowest** glass in the structure is its glass tier, and an **energy hatch above that tier stops the multi**. The
controller shows *Glass tier* and a red warning when the hatch is too high.

| Glass | Tier | Made at |
|---|---|---|
| <ItemLink id="gtapexadditions:borosilicate_glass" /> | HV | MV fluid solidifier |
| <ItemLink id="gtapexadditions:titanium_reinforced_borosilicate_glass" /> | EV | HV |
| <ItemLink id="gtapexadditions:tungstensteel_reinforced_borosilicate_glass" /> | IV | EV |
| <ItemLink id="gtapexadditions:rhodium_plated_palladium_reinforced_borosilicate_glass" /> | LuV | IV |
| <ItemLink id="gtapexadditions:iridium_reinforced_borosilicate_glass" /> | ZPM | LuV |
| <ItemLink id="gtapexadditions:osmium_reinforced_borosilicate_glass" /> | UV | ZPM |
| <ItemLink id="gtapexadditions:neutronium_reinforced_borosilicate_glass" /> | UHV | UV |
| <ItemLink id="gtapexadditions:infinity_reinforced_borosilicate_glass" /> | UEV | UHV |
| <ItemLink id="gtapexadditions:transcendentally_reinforced_borosilicate_glass" /> | UIV | UEV |
| <ItemLink id="gtapexadditions:hexanite_reinforced_borosilicate_glass" /> | UMV | UIV |
| <ItemLink id="gtapexadditions:universium_reinforced_borosilicate_glass" /> | UXV | UMV |

GregTech's own glass counts too: tempered glass = HV, laminated glass = IV, fusion glass = LuV.

Each tier is a borosilicate block solidified with 8 ingots of molten metal:

<GTRecipesFor id="gtapexadditions:titanium_reinforced_borosilicate_glass" limit="1" />

Upgrading: the [Structure Upgrader](../items/structure-upgrader.md) swaps all glass in a multi to a better tier.
(Server owners can turn the energy rule off: `glassTierLimitsEnergyHatches` in gtapexadditions-common.toml.)
''', parent='gregtech/gregtech-index.md', pos=60, icon='gtapexadditions:borosilicate_glass',
     items=['gtapexadditions:borosilicate_glass', 'gtapexadditions:titanium_reinforced_borosilicate_glass'])

page('gregtech/multiblocks.md', 'Multiblocks, Hatches and Maintenance', '''
# Multiblocks, Hatches and Maintenance

A multiblock is a controller plus a structure of casings. **Hatches** replace some casings and are how items,
fluids and power go in and out:

* **Input / Output Bus**: items. **Input / Output Hatch**: fluids.
* **Energy Hatch**: power in (1A, 4A, 16A versions, laser hatches later). **Dynamo Hatch**: power out (generators).
* **Muffler Hatch**: required on smoky machines, must face open air.
* **Maintenance Hatch**: a new multi needs its problems fixed with tools (or use an auto / cleaning maintenance hatch).
* **Parallel Hatch**: lets many multis run several recipes at once.

The controller's screen tells you what's wrong when it doesn't form. Use the [preview](../getting-started/multiblock-preview.md)
(press **H** to see allowed hatch spots).

Apex multis repaint their hatches with the casing texture, and the auto-builder places casings before hatches.
''', parent='gregtech/gregtech-index.md', pos=70, icon='gtceu:maintenance_hatch')

page('gregtech/cleanroom.md', 'Cleanroom', '''
# Cleanroom

Advanced circuits and wafers need a <ItemLink id="gtceu:cleanroom" />: a hollow box of
<ItemLink id="gtceu:plascrete" /> with <ItemLink id="gtceu:filter_casing" /> covering the ceiling, a door, and the
controller in the middle of the ceiling. Machines inside get the clean environment. Sterile cleanrooms (later)
are needed for wetware, optical and higher circuits.

<Multiblock id="gtceu:cleanroom" />

![A finished cleanroom](../assets/images/cleanroom.png)

<GTRecipesFor id="gtceu:cleanroom" limit="1" />
''', parent='gregtech/gregtech-index.md', pos=80, icon='gtceu:cleanroom')

page('gregtech/circuits.md', 'Circuits', '''
# Circuits

Circuits are tiered: any circuit of the right tier works in a recipe. Each line has 4 circuits (processor, assembly,
supercomputer, mainframe) covering 4 tiers.

| Line | Tiers | Notes |
|---|---|---|
| Vacuum tubes / electronic | ULV-MV | <ItemLink id="gtceu:vacuum_tube" />, <ItemLink id="gtceu:basic_electronic_circuit" /> |
| Integrated | LV-HV | <ItemLink id="gtceu:basic_integrated_circuit" />, <ItemLink id="gtceu:advanced_integrated_circuit" /> |
| Microprocessor | LV-EV | <ItemLink id="gtceu:microchip_processor" />, needs a cleanroom for its wafers |
| Nano | HV-LuV | <ItemLink id="gtceu:nano_processor" /> |
| Quantum | EV-ZPM | <ItemLink id="gtceu:quantum_processor" /> |
| Crystal | IV-UV | <ItemLink id="gtceu:crystal_processor" /> |
| Wetware | LuV-UHV | <ItemLink id="gtceu:wetware_processor" />, sterile cleanroom |
| Optical | UV-UIV | <ItemLink id="gtca:optical_processor" /> (GTNH's optical line) |
| Exotic | UHV-UXV | GTNA |
| Cosmic | UIV-OpV | <ItemLink id="gtapexadditions:cosmic_processor" /> |
| Transcendent | UXV-MAX | Apex |

Bulk circuit making: the [Circuit Assembly Line](../multiblocks/circuit_assembly_line.md),
[Nanochip Assembly Complex](../multiblocks/nanochip_assembly_complex.md) and [Miracle Top](../multiblocks/miracle_top.md).
''', parent='gregtech/gregtech-index.md', pos=90, icon='gtceu:basic_electronic_circuit')

page('gregtech/wireless-energy.md', 'Wireless Energy', '''
# Wireless Energy

GregTech Modern Things adds a **wireless energy network** per player (or team): wireless energy hatches and covers
put EU into one shared pool and take it out anywhere, across dimensions.

* Wireless energy hatches / covers exist from LV up to OpV (the UHV+ covers are Apex recipes).
* Some Apex multis (Real Annihilation Generator, Singularity Data Hub) draw from or feed the wireless network directly.
* Use the wireless energy monitor to see the stored amount.
''', parent='gregtech/gregtech-index.md', pos=100, icon='gtmthings:lv_wireless_energy_receive_cover')

page('gregtech/duration-curve.md', 'Recipe Time Curve', '''
# Recipe Time Curve

Very long recipes are shortened so endgame crafts stay playable, but they draw more **amps** to keep the total
energy the same:

| Original | Apex |
|---|---|
| up to 15 min | unchanged |
| 30 min | ~18 min, 2x amps |
| 1 h | ~23 min, 3x amps |
| 3 h | ~32 min |
| 12.5 h | ~42 min |

The voltage tier never changes, only amperage: a UV recipe stays UV but may need 4A / 16A / laser hatches.
''', parent='gregtech/gregtech-index.md', pos=110, icon='minecraft:clock')

page('gregtech/tinkers-heat.md', 'Tinkers Heat', '''
# Tinkers Heat

The smeltery's heat follows GregTech:

| Fuel | Temperature | Melts |
|---|---|---|
| Lava | 1,000 | metals up to MV (no EBF, or EBF up to 1,800 K) |
| Blazing Blood | 1,500 | HV - IV metals (EBF 1,801 - 3,600 K) |
| GregTech plasmas | 3,000 - 9,000 | LuV and up |

Melting and casting temperatures of every Tinkers metal that is also a GregTech metal are raised to its GT blast
temperature, so the smeltery never skips the Electric Blast Furnace.
''', parent='gregtech/gregtech-index.md', pos=120, icon='tconstruct:seared_melter')
