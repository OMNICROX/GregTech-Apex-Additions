# Apex Guide: deeper GregTech mechanics pages (ore processing, boilers, batteries, tools, programmed circuits).
# Every number here comes from GTCEu 7.5.3's code (OreRecipeHandler, MaterialFlagAddition, SteamBoilerMachine,
# SimpleSteamMachine, GTItems, BatteryBufferMachine, PowerSubstationMachine, MetaMachine, GhostCircuitSlotWidget)
# and the pack's gtceu.yaml defaults.
from pages_core import page

# ====================================================================== ore processing
page('gregtech/ore-processing.md', 'Ore Processing', '''
# Ore Processing

Smelting raw ore gives one ingot. Running it through GregTech machines gives at least **two**, plus
**byproducts**. Many materials only come from byproducts, among them gallium, cadmium, cobalt, palladium,
platinum and the rare earths.

The exact chain for any ore is in JEI: look up the ore and open the **Ore Processing Diagram** tab. It draws every
route below with that ore's real byproducts.

![An early ore line, left to right: chest, macerator, ore washer (water pipe at the back), thermal centrifuge, centrifuge, chest](../assets/images/ore_processing_line.png)

## The steps

| Step | Machine | In | Out |
|---|---|---|---|
| 1. Crush | <ItemLink id="gtceu:lv_macerator" /> | raw ore | **2** crushed ore, 14% byproduct 1 |
| 1. Crush (cheap) | <ItemLink id="gtceu:lv_forge_hammer" /> | raw ore | 1 crushed ore |
| 2. Wash | <ItemLink id="gtceu:lv_ore_washer" /> | crushed ore + 1,000 mB water | purified ore, 1/3 byproduct 1, stone dust |
| 2. Bathe | <ItemLink id="gtceu:lv_chemical_bath" /> | crushed ore + mercury or sodium persulfate | purified ore, **70%** byproduct 4 |
| 3. Spin | <ItemLink id="gtceu:lv_thermal_centrifuge" /> | purified (or crushed) ore | refined ore, 1/3 byproduct 2, stone dust |
| 4. Grind | <ItemLink id="gtceu:lv_macerator" /> | refined ore | dust, 14% byproduct 3 |

Every stage can also be ground or smelted on its own, so you can stop anywhere:

* **Crushed ore** ground -> impure dust (14% byproduct 1). Centrifuge impure dust for a 1/9 byproduct 1, or wash it.
* **Purified ore** ground -> purified dust (14% byproduct 2). Centrifuge it for a 1/9 byproduct 2.
* Crushed, purified and refined ore all smelt straight to an ingot if the metal doesn't need a blast furnace.

The ore washer also has a quick recipe with only 100 mB water: no byproduct, but it doesn't drain your water.
Distilled water (100 mB) gives the byproduct.

## Gems and magnetic ores

* <ItemLink id="gtceu:lv_sifter" />: purified gem ores (diamond, emerald, ruby, sapphire...) sift into exquisite,
  flawless, normal and flawed gems plus dust.
* <ItemLink id="gtceu:lv_electromagnetic_separator" />: purified dust of some ores splits off a magnetic metal. Iron
  from nickel, tin, chromite, pyrite, pentlandite and ilmenite; gold from magnetite; neodymium from monazite and
  bastnasite.

## Which route?

* **Early LV**: macerate, then wash, then smelt or grind. That already doubles everything.
* **When you need a specific byproduct**: find which step gives it in the diagram. Byproduct 2 only comes from
  the thermal centrifuge (or purified dust in a centrifuge). Byproduct 4 only comes from the chemical bath.
* **The chemical bath** has the best byproduct chance (70%). Mercury baths give gold from copper and chalcopyrite,
  palladium from sheldonite and platinum from nickel. Sodium persulfate gives gold from iron.

## Byproducts of common ores

1 = macerator / ore washer, 2 = thermal centrifuge, 3 = grinding refined ore, 4 = chemical bath.

| Ore | 1 | 2 | 3 | 4 (bath) |
|---|---|---|---|---|
| Iron | Nickel | Tin | Tin | Gold (sodium persulfate) |
| Copper | Cobalt | Gold | Nickel | Gold (mercury) |
| Tin | Iron | Zinc | Zinc | Zinc (sodium persulfate) |
| Gold | Copper | Nickel | Silver | Silver (mercury) |
| Silver | Lead | Sulfur | Sulfur | Gold (mercury) |
| Nickel | Cobalt | Iron | Platinum | Platinum (mercury) |
| Galena | Sulfur | Silver | Silver | Silver (mercury) |
| Cassiterite | Tin | Bismuth | Bismuth | - |
| Chalcopyrite | Pyrite | Cobalt | Cadmium | Gold (mercury) |
| Sphalerite | Yellow Garnet | **Gallium** | Cadmium | Zinc (sodium persulfate) |
| Tetrahedrite | Antimony | Zinc | Cadmium | Cadmium (sodium persulfate) |
| Pentlandite | Iron | Sulfur | Cobalt | Cobalt (sodium persulfate) |
| Bauxite | Grossular | Rutile | **Gallium** | Gallium (sodium persulfate) |
| Ilmenite | Iron | Rutile | Rutile | Ilmenite Slag (sodium persulfate) |
| Magnetite | Iron | Gold | Gold | Gold (mercury) |
| Sheldonite (Cooperite) | Nickel | Nickel | Cobalt | **Palladium** (mercury) |
| Tungstate | Manganese | Silver | Lithium | Lithium (mercury) |
| Scheelite | Manganese | Molybdenum | Calcium | - |
| Tantalite | Manganese | Niobium | Tantalum | - |
| Lepidolite | Lithium | Caesium | Boron | - |
| Monazite | Thorium | Neodymium | Rare Earth | - |
| Bastnasite | Neodymium | Rare Earth | Rare Earth | - |
| Pitchblende | Thorium | Uraninite | Lead | - |
| Redstone | Cinnabar | Rare Earth | Glowstone | - |

Some ores give more than two crushed ores per raw ore (redstone, for example, gives three); the diagram shows it.

## Later on

* Multiblock versions (the GCYM large macerator, large chemical bath and large centrifuge) run the same recipes
  with parallels; see [GCYM Large Machines](../gtmultis/gcym.md).
* The [General Ore Processing Factory](../multiblocks/ore_processing_factory.md) (LuV) runs the whole chain in
  one block.
''', parent='gregtech/gregtech-index.md', pos=40, icon='gtceu:lv_macerator',
     items=['gtceu:lv_ore_washer', 'gtceu:lv_thermal_centrifuge', 'gtceu:lv_chemical_bath', 'gtceu:lv_sifter',
            'gtceu:lv_electromagnetic_separator'])

# ====================================================================== boilers & steam
page('gregtech/boilers-and-steam.md', 'Boilers and Steam', '''
# Boilers and Steam

Steam runs everything before LV, and keeps running LV through steam turbines. A boiler turns water and fuel into
steam; steam machines and turbines use it.

![A steam start: a coke oven, a coal boiler piping steam into four steam machines, and a Primitive Blast Furnace](../assets/images/steam_setup.png)

## Small boilers

| Boiler | Fuel | Steam at full heat |
|---|---|---|
| <ItemLink id="gtceu:lp_steam_solid_boiler" /> | coal, coke, charcoal, wood... | 120 mB/s (6 mB/t) |
| <ItemLink id="gtceu:hp_steam_solid_boiler" /> | same | 300 mB/s (15 mB/t) |
| <ItemLink id="gtceu:lp_steam_liquid_boiler" /> | lava, creosote and other fluid fuels | 240 mB/s (12 mB/t) |
| <ItemLink id="gtceu:hp_steam_liquid_boiler" /> | same | 600 mB/s (30 mB/t) |
| <ItemLink id="gtceu:lp_steam_solar_boiler" /> | daylight with open sky | 120 mB/s (6 mB/t) |
| <ItemLink id="gtceu:hp_steam_solar_boiler" /> | same | 360 mB/s (18 mB/t) |

JEI's boiler fuel category lists every fuel and how long it burns.

## How a boiler works

* **It heats up slowly.** A cold boiler makes no steam. Steam starts once it is about a tenth of the way hot,
  and output grows with the heat bar until it is full. From cold to full takes about **10 minutes** for both the
  bronze and the steel boilers.
* **It cools down when the fuel runs out**, so keep it fed. A hopper or a pipe into the fuel slot is enough.
* **It pushes steam out by itself** into anything next to it, on every side except the front and the bottom.
  Put steam machines right against its sides, back or top, or run a bronze fluid pipe.
* **Solid boilers make ash.** Take it out of the ash slot from time to time, or pull it out with a pipe.
* **When the steam tank is full**, the boiler vents steam out of its front and wastes it. That's harmless.
* **Water**: boilers use very little, but they must never run dry while hot.

<Color id="red">If a hot boiler runs out of water and you then add water, it explodes. If one runs dry, let it cool
down (or break it) before refilling.</Color> A steady water supply is the safe setup: a primitive pump, or an electric
pump on a water source, piped straight into the boiler.

## How much steam machines use

A steam machine burns **steam in place of EU**, and only runs recipes up to 32 EU/t (LV).

* **Bronze (low pressure)**: 1 mB of steam per EU, but recipes take **twice as long**.
* **Steel (high pressure)**: 2 mB of steam per EU, at normal speed.

So both use the same steam per item; the steel machine is just twice as fast. One bronze solid boiler at full heat
(6 mB/t) runs a bronze machine on a 4 EU/t recipe with steam to spare. Watch the steam bar in the machine GUI:
if it sits empty, add a boiler.

Steam machines **vent** after every recipe out of their output side (set it with a wrench). A blocked vent pauses
the machine, and the steam hurts anyone standing in front of it.

## Steam into EU

A <ItemLink id="gtceu:lv_steam_turbine" /> turns 2 mB of steam into 1 EU. At its full 32 EU/t it needs 64 mB/t:
about ten bronze solid boilers, or four or five steel ones. With less steam it just runs part of the time, so start
with what you have and add boilers. This is the normal way to power your first LV machines; see
[LV](../progression/01-lv.md). A [Large Boiler](../gtmultis/large_boilers.md) feeds several turbines on its own.

## Going bigger

* [Large Boilers](../gtmultis/large_boilers.md) (bronze, steel, titanium, tungstensteel) make thousands of mB per
  tick and feed large steam turbines.
* [Steam Multiblocks](../gtmultis/steam-multiblocks.md) run steam recipes with up to 8 parallels in Apex.
''', parent='gregtech/gregtech-index.md', pos=31, icon='gtceu:lp_steam_solid_boiler',
     items=['gtceu:lp_steam_solid_boiler', 'gtceu:hp_steam_solid_boiler', 'gtceu:lp_steam_liquid_boiler',
            'gtceu:hp_steam_liquid_boiler', 'gtceu:lp_steam_solar_boiler', 'gtceu:hp_steam_solar_boiler'])

# ====================================================================== batteries
page('gregtech/power/batteries.md', 'Batteries and Energy Storage', '''
# Batteries and Energy Storage

Batteries store EU. They go in a battery buffer to store power for a network, in a machine's charge slot to keep it
running, or in an electric tool. A battery charges and discharges at its own tier's voltage.

## Battery tiers

| Tier | Battery | Capacity (EU) |
|---|---|---|
| ULV | <ItemLink id="gtceu:tantalum_capacitor" /> | 1,000 |
| LV | <ItemLink id="gtceu:lv_sodium_battery" /> / <ItemLink id="gtceu:lv_cadmium_battery" /> / <ItemLink id="gtceu:lv_lithium_battery" /> | 80k / 100k / 120k |
| MV | <ItemLink id="gtceu:mv_sodium_battery" /> / <ItemLink id="gtceu:mv_cadmium_battery" /> / <ItemLink id="gtceu:mv_lithium_battery" /> | 360k / 400k / 420k |
| HV | <ItemLink id="gtceu:hv_sodium_battery" /> / <ItemLink id="gtceu:hv_cadmium_battery" /> / <ItemLink id="gtceu:hv_lithium_battery" /> | 1.2M / 1.6M / 1.8M |
| HV | <ItemLink id="gtceu:energy_crystal" /> | 6.4M |
| EV | <ItemLink id="gtceu:ev_vanadium_battery" /> | 10.24M |
| EV | <ItemLink id="gtceu:lapotron_crystal" /> | 25M |
| IV | <ItemLink id="gtceu:iv_vanadium_battery" /> | 40.96M |
| IV | <ItemLink id="gtceu:lapotronic_energy_orb" /> | 250M |
| LuV | <ItemLink id="gtceu:luv_vanadium_battery" /> | 163.84M |
| LuV | <ItemLink id="gtceu:lapotronic_energy_orb_cluster" /> | 1G |
| ZPM | <ItemLink id="gtceu:zpm_naquadria_battery" /> | 655.36M |
| ZPM | <ItemLink id="gtceu:energy_module" /> | 4G |
| UV | <ItemLink id="gtceu:uv_naquadria_battery" /> | 2.62G |
| UV | <ItemLink id="gtceu:energy_cluster" /> | 20G |
| UHV | <ItemLink id="gtceu:max_battery" /> | practically unlimited |

* Sodium is the cheapest, lithium holds the most; cadmium sits between.
* The crystal / orb line holds far more than the plain battery of the same tier, so switch to it as soon as you can
  make it.
* The <ItemLink id="gtceu:zero_point_module" /> (ZPM, 2T EU) is a one-use battery: it can't be recharged.

## Battery buffers

A <ItemLink id="gtceu:lv_battery_buffer_4x" /> comes with 4, 8 or 16 slots at every tier.

* Power **out of the front**, power **in on the other five sides**.
* Each battery adds **2 A in** and **1 A out**. A 4-slot buffer full of batteries takes up to 8 A and gives up to 4 A.
* It charges the batteries inside it from the network, and drains them back into the network when generation drops.
* Use batteries of the buffer's tier. Because the buffer outputs fewer amps than it takes in, it also caps how
  much a line can draw.

![LV power: two steam turbines and a battery buffer feeding four machines over one tin cable](../assets/images/lv_power.png)

## Chargers

A <ItemLink id="gtceu:lv_charger_4x" /> charges batteries and electric tools quickly. Electric machines also charge
a tool or battery placed in their charge slot.

## Big storage

The <ItemLink id="gtceu:power_substation" /> (EV) is the end-game bank. It holds up to 18 layers of capacitor blocks
(<ItemLink id="gtceu:ev_lapotronic_battery" /> up to <ItemLink id="gtceu:uhv_ultimate_battery" />; empty capacitors
fill the rest) and moves power in and out through energy and dynamo hatches. It loses 1% of what it holds per
real-time day. See [Power Substation](../../gtmultis/power_substation.md).

## Which storage when

| Stage | Use |
|---|---|
| Steam / early LV | An LV battery buffer with sodium or lithium batteries |
| MV-HV | Buffers of your main tier; energy crystals at HV |
| EV-IV | Lapotron crystals and lapotronic orbs in buffers; plan the Power Substation |
| LuV+ | Power Substation with lapotronic capacitors |
''', parent='gregtech/power/power-index.md', pos=7, icon='gtceu:lv_lithium_battery',
     items=['gtceu:lv_battery_buffer_4x', 'gtceu:lv_charger_4x', 'gtceu:lapotronic_energy_orb'])

# ====================================================================== tools
page('gregtech/tools.md', 'Tools', '''
# Tools

GregTech tools are made from any tool-grade material (iron, bronze, steel and up), and better materials last longer
and mine faster. Most of them double as crafting tools: they stay in the grid and lose durability. Hold **Shift**
over a tool for what it does.

Holding a tool over a machine or pipe shows a **grid** on the face you're pointing at. The grid picks which side
you act on, so you can change the back of a machine from the front.

## What each tool does to machines

| Tool | Right-click | Shift + right-click |
|---|---|---|
| <ItemLink id="gtceu:steel_wrench" /> | Sets the **output side** (also the vent of steam machines) | Turns the **front** of the machine |
| <ItemLink id="gtceu:steel_screwdriver" /> | On the output side: toggles **auto-output**. On a cover: opens its settings | On the output side: lets the machine **take input from its output side** too |
| Soft Mallet | **Pauses or resumes** the machine (after its current recipe) | |
| <ItemLink id="gtceu:iron_hammer" /> (hard hammer) | **Muffles** the machine's sounds | |
| <ItemLink id="gtceu:steel_crowbar" /> | **Removes a cover** | |
| <ItemLink id="gtceu:steel_wire_cutter" /> | **Connects or disconnects** a cable on that side | |
| Plunger | Drains fluid out of a tank or machine | |

* Clicking a multiblock controller's front with a wrench rotates it on that face.
* A wrench is the fastest tool for breaking machines.
* The wrench has **modes**: items, fluids or both. Its right-click sets the item output side, the fluid output side,
  or both. The mode shows in its tooltip.

## Pipes and cables

* **Wrench** on a pipe sets which sides it connects to; shift-wrench **blocks** a side so nothing connects there.
* **Wire cutter** does the same for wires and cables.
* GregTech cables in Apex only connect where you tell them to (GT6-style), so you'll use these a lot.

## Maintenance

Multiblocks in Apex need maintenance. Each problem is fixed by one tool: wrench, screwdriver, soft mallet, hard
hammer, wire cutter or crowbar. Open the maintenance hatch and click each problem with the right
tool in your inventory, or use **duct tape** to fix everything at once. See [Multiblock Basics](multiblock-basics.md).

## Crafting tools

| Tool | Used for |
|---|---|
| Hard hammer | Two ingots into a plate; crushed ore into dust by hand |
| <ItemLink id="gtceu:steel_file" /> | Rods, rings and gears (with a hammer) |
| <ItemLink id="gtceu:steel_saw" /> | Planks, slabs, cutting rods into bolts |
| Wire cutter | Plates into wires |
| Mortar | Grinding items into dust |
| Knife | Cutting food and rubber |
| Screwdriver / wrench | Machine hulls and many machine recipes |

## Electric tools

From LV on you can make battery-powered tools. They use EU instead of durability and charge in any charge slot.

* **Drills** (LV to IV): fast mining, larger ones mine an area.
* **Chainsaws**: cut down whole trees.
* **Electric wrench, screwdriver and wire cutter**: the same actions as the hand tools.
* **Buzzsaw**: a saw for crafting.

Also useful early: the <ItemLink id="gtceu:portable_scanner" /> (machine and pipe info), the
<ItemLink id="gtceu:prospector.lv" /> (finds ore veins) and the <ItemLink id="gtceu:terminal" /> (builds
multiblocks for you; see [Multiblock preview](../getting-started/multiblock-preview.md)).
''', parent='gregtech/gregtech-index.md', pos=34, icon='gtceu:steel_wrench')

# ====================================================================== programmed circuits
page('gregtech/programmed-circuits.md', 'Programmed Circuits', '''
# Programmed Circuits

Many machines have more than one recipe for the same inputs. A distillery turns one fluid into several products; an
assembler makes several things from the same parts. The **programmed circuit** number picks which recipe runs.

<ItemLink id="gtceu:programmed_circuit" />

* A circuit is **never used up**. Its number is part of the recipe in JEI ("Configuration: 3").
* A recipe that shows a circuit only runs with that number set. A recipe without one runs with any number or none.

## Setting it on a machine

You don't need the item. Every machine has a **circuit slot** in its GUI (the slot with the small circuit icon):

| Action | Effect |
|---|---|
| Left-click | Next number |
| Right-click | Previous number |
| Mouse wheel | Up or down |
| Shift + left-click | A grid of all numbers (0-32) to pick from |
| Shift + right-click | Clear the circuit |

With the item in hand, right-click in the air to pick its number, then shift-right-click a machine to set its
circuit to that number.

## Multiblocks

Every **input bus** and **input hatch** has its own circuit slot. A multiblock reads the circuits in all its buses.

* To run two different circuit recipes in one multiblock, turn on **Distinct** mode on each input bus (button in
  the bus GUI). Each bus is then checked on its own, with its own circuit.
* Give each bus one job: one bus with circuit 1 and the parts for recipe A, one with circuit 2 for recipe B.

## Recipe conflicts

If a machine picks the wrong recipe:

1. Look the output up in JEI and check the circuit number. Set it.
2. If two recipes still match, it's a real conflict: the machine runs whichever it finds first. Split the inputs over
   two distinct buses or two machines.
3. AE2 autocrafting: set the circuit in the machine or bus, not in the pattern. Circuits aren't used up, so a
   pattern that includes one sends a new circuit every craft and they pile up in the bus.
''', parent='gregtech/gregtech-index.md', pos=91, icon='gtceu:programmed_circuit')
