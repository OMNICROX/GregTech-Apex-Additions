# Apex Guide pages adapted from the GuideNH DefaultGuide (GT New Horizons guide team), rewritten for GTCEu 7.5.3
# and the Apex Ascension pack. Facts follow GTCEu's code and the pack's gtceu.yaml, not GT5u.
from pages_core import page, P

SRC = '\n\n<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>\n'

# ====================================================================== power
page('gregtech/power/power-index.md', 'Power', '''
# Power

GregTech machines run on **EU** (Energy Units). Generators push EU into cables, cables carry it to machines, and
machines spend it while running recipes. This section explains the rules of that network.

* [Voltage Tiers](voltage-tiers.md): the tier ladder from ULV to MAX.
* [Generation and Consumption](generation-and-consumption.md): how generators produce and machines use EU.
* [The Power Network](enet.md): how packets travel and how to power a machine safely and reliably.
* [Wires and Cables](cables.md) and [Cable Loss](cable-loss.md).
* [Transformers, Buffers and Diodes](distribution.md).
* For tier skipping, overclocking and parallels, see [Overclocking and Parallels](../oc/oc-index.md).

## Voltage, current and power

EU moves in **packets**.

* **Voltage** (V) is how much EU one packet carries: 1 V = 1 EU per packet. Voltage decides the
  [tier](voltage-tiers.md) of generators and machines.
* **Current** (A, amps) is how many packets move per tick: 1 A = 1 packet per tick. Packets are whole numbers, so
  the current in any one tick is a whole number; an average can be a fraction.
* **Power** is voltage times current, in **EU/t**. A machine using 64 EU/t at 32 V draws 2 A.

Energy is power times time: a recipe at 30 EU/t for 200 ticks costs 6,000 EU.

## Forge Energy

GregTech cables power **Forge Energy** machines directly (4 FE per EU), so other mods' FE blocks run off GT power.

## Generators by stage

* Steam: boilers and steam turbines. Early LV: <ItemLink id="gtceu:lv_steam_turbine" />,
  <ItemLink id="gtceu:lv_combustion" /> (oil and fuels), <ItemLink id="gtceu:lv_gas_turbine" />.
* Mid game: <ItemLink id="gtceu:large_combustion_engine" />, <ItemLink id="gtceu:steam_large_turbine" />,
  <ItemLink id="gtceu:gas_large_turbine" />.
* Late game: fusion with the <ItemLink id="gtceu:plasma_large_turbine" />, the
  [Large Naquadah Reactor](../../multiblocks/large_naquadah_reactor.md), the
  [Lightning Spire](../../multiblocks/lightning_spire.md), antimatter ([SLAM](../../multiblocks/antimatter_generator.md)).
* [Wireless energy](../wireless-energy.md) moves EU across the world without cables.
''' + SRC, parent='gregtech/gregtech-index.md', pos=15, icon='gtceu:lv_battery_buffer_4x')

page('gregtech/power/voltage-tiers.md', 'Voltage Tiers', '''
# Voltage Tiers

Progress in Apex is measured in **voltage tiers**. Each tier's **standard voltage** is 4x the one before
(8 x 4^n V, where n is the tier index: ULV is 0, LV is 1 and so on).

| Index | Tier | Name | Standard voltage (V) | Recipe power (EU/t) | Typical cable |
|---|---|---|---|---|---|
| 0 | ULV | Ultra Low Voltage | 8 | 7 | Lead |
| 1 | LV | Low Voltage | 32 | 30 | Tin |
| 2 | MV | Medium Voltage | 128 | 120 | Copper |
| 3 | HV | High Voltage | 512 | 480 | Gold |
| 4 | EV | Extreme Voltage | 2,048 | 1,920 | Aluminium |
| 5 | IV | Insane Voltage | 8,192 | 7,680 | Platinum / Tungsten |
| 6 | LuV | Ludicrous Voltage | 32,768 | 30,720 | Niobium-Titanium |
| 7 | ZPM | Zero Point Module | 131,072 | 122,880 | Vanadium-Gallium |
| 8 | UV | Ultimate Voltage | 524,288 | 491,520 | Yttrium Barium Cuprate |
| 9 | UHV | Highly Ultimate Voltage | 2,097,152 | 1,966,080 | Europium |
| 10 | UEV | Extremely Ultimate Voltage | 8,388,608 | 7,864,320 | |
| 11 | UIV | Insanely Ultimate Voltage | 33,554,432 | 31,457,280 | |
| 12 | UXV | Extremely Extreme Ultimate Voltage | 134,217,728 | 125,829,120 | |
| 13 | OpV | Overpowered Voltage | 536,870,912 | 503,316,480 | |
| 14 | MAX | Maximum Voltage | 2,147,483,647 | | |

**Recipe power** is the most EU/t a recipe of that tier uses: 15/16 of 1 A at the standard voltage. That small
margin lets one amp run a full-tier recipe even after a little cable loss.

GTCEu names the top tiers differently from GT New Horizons: after UIV come **UXV**, **OpV** and **MAX** (GTNH has
UMV and UXV there). A machine or hatch of a tier accepts packets up to that tier's voltage.

The [tier ladder](../../progression/tier-ladder.md) says what each tier unlocks in Apex.
''' + SRC, parent='gregtech/power/power-index.md', pos=1, icon='gtceu:lv_machine_hull')

page('gregtech/power/generation-and-consumption.md', 'Generation and Consumption', '''
# Generation and Consumption

## Generators

* **Single-block generators** (steam turbines, combustion and gas generators...) fill an internal EU buffer and push
  **1 A at their tier's voltage**. They only burn fuel while the buffer has room, so you can stock them up without
  waste.
* **Multiblock generators** (Large Steam Turbine, Large Combustion Engine, naquadah reactors...) output through
  **Dynamo Hatches**. Their tier and amperage limit what the multi can push.

## Machines

<Color id="red">Feeding a machine packets above its tier's voltage makes it explode.</Color>

* A machine accepts packets up to its tier's voltage and can only run recipes whose EU/t fits within that tier.
* **Single-block machines** take as many amps as the running recipe needs (normally 1 A).
* **Multiblocks** take power through **Energy Hatches**: a normal hatch takes up to **2 A**; the 4 A and 16 A
  hatches, the 64 A substation hatches and laser hatches take more, if the multi allows them.

## Running out of power

When a machine can't get the EU it needs for a tick, the recipe stalls and its progress resets to the start. The
inputs were taken when the recipe started, so they are not lost: once power returns, the recipe starts over from
zero. A **multiblock** that keeps failing for 5 attempts suspends itself until you restart it (a Machine Controller
cover set to "prevent power fail" stops that).
''' + SRC, parent='gregtech/power/power-index.md', pos=2, icon='gtceu:lv_steam_turbine')

page('gregtech/power/enet.md', 'The Power Network', '''
# The Power Network

A power network has three parts:

* **Sources** push packets out of their buffer: generators, the output side of transformers and battery buffers,
  Dynamo Hatches.
* **Cables** carry packets and lose a little voltage on the way ([cable loss](cable-loss.md)). They store nothing.
* **Consumers** take packets into their buffer: machines, the input side of transformers and buffers, Energy Hatches.

Apex uses **GT6-style cables**: a placed cable only connects on the sides you connect it to with a wire cutter (or
the side you placed it against), not to everything around it.

## Powering safely

* A packet above a consumer's voltage **explodes the consumer**. Don't run an LV machine on an MV line; step down
  with a [transformer](distribution.md) first.
* A cable carrying more **voltage** than its rating **burns** instantly. Too many **amps** heat it up until it burns.
  Amps add up: a cable segment carries the sum of everything flowing through it.

## Powering reliably

Each tick a source can push only so many packets, and a consumer can take only so many. For a machine to never
stall:

* the amps all machines need together must not exceed what the sources can push, and
* each machine's need must fit its own input limit (a normal Energy Hatch takes 2 A).

Needed amps = recipe EU/t divided by the voltage that actually arrives (after cable loss).

## Tools

Right-click a cable or machine with a **Portable Scanner** or look at it with Jade to see its stored energy and
input/output voltage and amps.
''' + SRC, parent='gregtech/power/power-index.md', pos=3, icon='gtceu:tin_single_cable')

page('gregtech/power/cables.md', 'Wires and Cables', '''
# Wires and Cables

Wires and cables connect sources to consumers. Each has three numbers in its tooltip:

* **Max voltage**: set by the material. A cable can always carry lower voltages too.
* **Max amperage**: the material's base amps times the thickness (1x, 2x, 4x, 8x, 12x, 16x).
* **Loss per block per amp**: see [Cable Loss](cable-loss.md). It depends on the material and on insulation, not
  on thickness. **Cables** (insulated, made with rubber) lose less than bare **wires**, and bare wires shock you.

Some materials have no loss at all, the **superconductors**.

## Placing

* Cables connect only where you connect them (GT6-style). Use a **wire cutter** on a side to connect or disconnect.
* Shift-right-click a placed cable with another cable to swap it in place.
* Paint cables with spray cans to tell different lines apart; differently painted cables don't connect.
''' + SRC, parent='gregtech/power/power-index.md', pos=4, icon='gtceu:copper_single_cable')

page('gregtech/power/cable-loss.md', 'Cable Loss', '''
# Cable Loss

Every block of cable lowers the voltage of each packet by its **loss per block per amp** (in the tooltip). Over a
path of L blocks with loss r:

* voltage that arrives = voltage sent - r x L
* EU lost per tick = amps x r x L

Example: Tin Cable loses 1 V per block. 32 V at 2 A over 2 blocks arrives as 30 V at 2 A; 4 EU/t was lost.

Loss is per amp, so **sending the same power at a higher voltage loses less**: one tier up carries the same power
with a quarter of the amps, so a quarter of the loss. That is why long lines run at high voltage and are stepped
down next to the machines.

When loss drops the voltage, a machine needs a bit more current for the same power. If the cable or hatch can't
carry that, use a lower-loss material, a thicker cable, a shorter path or a higher line voltage.
''' + SRC, parent='gregtech/power/power-index.md', pos=5, icon='gtceu:tin_single_wire')

page('gregtech/power/distribution.md', 'Transformers, Buffers and Diodes', '''
# Transformers, Buffers and Diodes

These blocks sit between two parts of a network: they take power in on one side, keep it in a buffer and push it
out on the other.

## Transformers

<Color id="red">A transformer that receives a packet above its input voltage explodes; check the mode before you
power it.</Color>

A <ItemLink id="gtceu:lv_transformer_1a" /> changes voltage by one tier, and current by 4x the other way.
It has one high-voltage face and five low-voltage faces. By default it steps **down** (high face in, low faces out);
right-click it with a **screwdriver** to switch to step-up. Transformers come in 1 A, 2 A, 4 A and 16 A versions
(the amps are on the low side; the high side carries a quarter).

For a lot of power, the multiblock <ItemLink id="gtceu:active_transformer" /> takes any number of Energy Hatches
and Dynamo Hatches (and laser hatches) and moves power between them at any tier.

## Battery buffers

A <ItemLink id="gtceu:lv_battery_buffer_4x" /> holds batteries of its tier. It takes up to 2 A per battery and
pushes out up to 1 A per battery, at its tier's voltage. Because input and output differ, a buffer also works as a
current limiter. For big storage, build a <ItemLink id="gtceu:power_substation" />.

## Diodes

A <ItemLink id="gtceu:lv_diode" /> lets power through in one direction only, up to its voltage and a set
number of amps (change it with a soft mallet). Use diodes to stop power flowing back into the wrong part of a
network, and to limit amps.
''' + SRC, parent='gregtech/power/power-index.md', pos=6, icon='gtceu:lv_transformer_1a')

# ====================================================================== overclocking & parallels
page('gregtech/oc/oc-index.md', 'Overclocking and Parallels', '''
# Overclocking and Parallels

A recipe has a **voltage tier** (the tier of its EU/t) and a **duration**. A machine with more power than the
recipe needs uses the extra in one of three ways:

* **[Overclocking](overclocking-rules.md)**: run the recipe faster for more EU/t.
* **[Parallels](parallels.md)**: run several copies of the recipe at once.
* **[Tier skipping](tier-skipping.md)**: what decides which recipes a machine may run at all.

## Rated power of a multiblock

GTCEu works out the highest voltage a multi can use from its Energy Hatches:

* With one hatch (or one at the highest tier), the multi runs at that hatch's tier.
* With **two or more hatches of the highest tier**, it may run recipes **one tier higher**: two LV Energy Hatches
  (4 A at 32 V) let an Electric Blast Furnace run MV recipes. This is the classic "two hatches" trick.

The recipe's own amperage still has to fit, and the multi's controller shows the voltage it is running at.
''' + SRC, parent='gregtech/gregtech-index.md', pos=21, icon='gtceu:mv_macerator')

page('gregtech/oc/overclocking-rules.md', 'Overclocking Rules', '''
# Overclocking Rules

**Overclocking** spends more power to finish a recipe sooner. A machine gets one overclock for each voltage tier it
has above the recipe's tier.

## Kinds of overclock

* **Normal (4/2)**: each overclock is 4x EU/t and half the duration, so twice the total energy. Most machines.
* **Perfect (4/4)**: 4x EU/t and a quarter of the duration, so the same total energy. The Electric Blast Furnace
  gets perfect overclocks from extra coil heat (see [Coils and heat](../ebf-and-coils.md)); some multis are
  perfect by default (check their tooltip).

## Below one tick: subtick parallels

A recipe can't take less than 1 tick. In GTCEu, most multis turn the overclocks that would go below 1 tick into
**subtick parallels**: instead of finishing faster, they finish more recipes in the same tick. So extra power still
pays off. Single-block machines simply stop at 1 tick.

## Small recipes

ULV recipes (8 EU/t or less) lose one overclock: they overclock as if they were LV.

## Durations

Durations are counted in ticks (20 per second) and rounded down after overclocking.

Apex also shortens very long endgame recipes with the [recipe time curve](../duration-curve.md).
''' + SRC, parent='gregtech/oc/oc-index.md', pos=1, icon='gtceu:hv_macerator')

page('gregtech/oc/parallels.md', 'Parallels', '''
# Parallels

A **parallel** runs one more copy of the recipe at the same time: two parallels take twice the inputs and twice the
EU/t, and give twice the outputs in the same duration. The total energy per item stays the same, so parallels are
"free" speed if you have the power.

Where parallels come from in Apex:

* **Parallel Control Hatches** (IV and up) on GCYM multis like the Alloy Blast Smelter, Large Mixing Vessel and
  Large Chemical Bath: 4 / 16 / 64 / 256 parallels by tier.
* **Steam multiblocks**: up to 8 parallels (this pack's setting).
* Some multis have built-in parallels (see their tooltip or page).
* **[Subtick parallels](overclocking-rules.md)** from overclocking past 1 tick.

## Parallels vs batch mode

* **Parallels**: more recipes per run, more EU/t, same duration. Real extra throughput.
* **Batch mode** (a button on many controllers): several runs bundled into one longer run, same EU/t. It only
  saves recipe checks (server performance); it does not speed anything up.

## Order of operations

The machine first applies energy discounts, then parallels, then overclocks:

EU/t = base EU/t x discount x parallels x 4^overclocks
''' + SRC, parent='gregtech/oc/oc-index.md', pos=2, icon='gtceu:iv_parallel_hatch')

page('gregtech/oc/tier-skipping.md', 'Tier Skipping', '''
# Tier Skipping

A machine can run any recipe whose voltage tier is **at or below** its own tier (for a multi, the rated tier from
its [Energy Hatches](oc-index.md)). An MV machine runs LV and MV recipes; an LV machine can't run an MV recipe at
all, no matter how long you wait.

Using a higher-tier machine than needed doesn't change the outputs, only the speed (through
[overclocking](overclocking-rules.md)).

Some recipes also need more than voltage: coil heat in the Electric Blast Furnace, a [cleanroom](../cleanroom.md),
or research data for the Assembly Line. JEI shows these under the recipe.
''' + SRC, parent='gregtech/oc/oc-index.md', pos=3, icon='gtceu:lv_machine_hull')

# ====================================================================== multiblock basics
page('gregtech/multiblock-basics.md', 'Multiblock Basics', '''
# Multiblock Basics

A **multiblock** is a machine built from many blocks around a **controller**. Most are better than their
single-block version: faster, parallel, or running recipes no single block can. The parts that move items, fluids
and power are the same for almost all of them:

* **Input / Output Buses** for items,
* **Input / Output Hatches** for fluids,
* **Energy / Dynamo Hatches** for power,
* a **Maintenance Hatch**, and on smoky machines a **Muffler Hatch**.

The controller's tooltip and the [3D preview](../getting-started/multiblock-preview.md) show where each part may
go. The preview can also build the structure for you.

## Controller

Right-click the controller to see its status, the voltage it is running at and its buttons: power on/off, voiding
mode (what happens when outputs are full) and, on many machines, batch mode. Tool shortcuts:

* **Wrench** on the controller: turn it (and the whole structure).
* **Soft mallet** on the controller: pause or resume the machine.
* **Distinct** button in an Input Bus: the bus is searched on its own, so recipes from different buses don't mix.
  Put each recipe's items (and its programmed circuit) in its own distinct bus.

## Buses

<ItemLink id="gtceu:lv_input_bus" /> and <ItemLink id="gtceu:lv_output_bus" /> carry items. The tier sets the
slots: ULV 1, LV 4, MV 9, HV 16, EV 25, IV 36 and so on. Output buses push into an adjacent inventory when you
enable auto-output in their GUI. **ME Input/Output Buses** (GTCEu x AE2) connect straight to your ME network.

## Hatches

<ItemLink id="gtceu:lv_input_hatch" /> and <ItemLink id="gtceu:lv_output_hatch" /> carry fluids. A normal hatch
holds 8,000 mB at ULV and doubles each tier (16,000 mB at LV, 32,000 mB at MV...). Quadruple and nonuple hatches
hold 4 or 9 different fluids. ME Input/Output Hatches connect to AE2.

## Energy and Dynamo Hatches

<ItemLink id="gtceu:lv_energy_input_hatch" /> takes up to 2 A into the multi; a Dynamo Hatch pushes 1 A out of a
generator multi. 4 A, 16 A, 64 A (substation) and laser hatches carry more, on the multis that accept them. Two
hatches of the same top tier let a multi run one tier higher (see [Overclocking and Parallels](oc/oc-index.md)).

## Maintenance Hatch

Multis slowly develop **maintenance problems** (a 1 in 6,000 roll every 50 seconds of running, so roughly one every
80 hours of runtime). Each problem makes the machine slower; with enough problems it stops. Open the
<ItemLink id="gtceu:maintenance_hatch" /> and fix each problem with its tool, or put **Duct Tape** in it to fix
everything at once.

| Problem | Tool |
|---|---|
| Pipe is loose | Wrench |
| Screws are loose | Screwdriver |
| Something is stuck | Soft Mallet |
| Platings are dented | Hammer |
| Circuitry burned out | Soldering Iron |
| That doesn't belong there | Crowbar |

The **Configurable Maintenance Hatch** trades speed for fewer problems, the **Automatic Maintenance Hatch** fixes
problems by itself, and the **Cleaning Maintenance Hatch** also keeps the machine sterile (a small cleanroom).

## Muffler Hatch

Some multis need a <ItemLink id="gtceu:lv_muffler_hatch" />. Its face must point at **open air**; anything in front
of it (even a pipe) stops the machine. Mufflers sometimes return ash and dark ash.

## Sharing walls

Two multis may share blocks: casings, coils, even Maintenance and Muffler Hatches. Share Energy Hatches with care,
since one 2 A hatch has to feed both machines.

## When a multi won't run

Open the controller and read the status line.

* **Structure not formed**: a part is missing or in the wrong place, coils or casings are mixed, the glass tier is
  too low for the Energy Hatches (see [Glass Tiers](glass-tiers.md)), or the controller faces the wrong way.
  Compare with the preview.
* **No power / not enough power**: check the hatches are connected, the cable is GT6-connected to them, the voltage
  is right and the line can deliver the amps.
* **No recipe**: an item or fluid is in the wrong bus or hatch, the programmed circuit is missing, or the machine's
  tier, coils or cleanroom don't meet the recipe.
* **Output full**: empty the output buses and hatches, or give the multi more of them. The EBF needs a hatch on top
  for gas outputs.
* **Needs maintenance**: fix it at the Maintenance Hatch.

## Apex extras

* Apex multis paint their hatches with the casing texture.
* The preview's auto-builder places casings before hatches, so you only swap in the hatches you want.
* **Parallel Hatches** let many multis run several recipes at once (see [Parallels](oc/parallels.md)).
''' + SRC, parent='gregtech/gregtech-index.md', pos=31, icon='gtceu:maintenance_hatch')

# replaced by the sections above
for old in ('gregtech/voltage-and-power.md', 'gregtech/overclocking.md', 'gregtech/multiblocks.md'):
    P.pop(old, None)

# ====================================================================== single-block machines
page('gregtech/singleblocks.md', 'Single-Block Machines', """
# Single-Block Machines

Single-block machines are one block each. They do one job at one tier, and you replace them with a new machine to
upgrade. Every multiblock that has a single-block version is faster, but single blocks carry you through the steam
age, LV and MV, and some stay useful all game. Most players move to multiblocks around EV and IV.

## Facing

Every machine has a **front** (its face, which never moves items or power) and an **output side**.

* Wrench a side to move the **output side** there; shift-wrench to turn the **front**.
* The other four sides take items, fluids, steam and power.

## Steam machines

Steam machines run on steam from a boiler. Each comes in a bronze (low pressure) and a steel (high pressure) version;
the steel one uses steam twice as fast and runs recipes twice as fast.

| Job | Low pressure | High pressure |
|---|---|---|
| Furnace | <ItemLink id="gtceu:lp_steam_furnace" /> | <ItemLink id="gtceu:hp_steam_furnace" /> |
| Alloy Smelter | <ItemLink id="gtceu:lp_steam_alloy_smelter" /> | <ItemLink id="gtceu:hp_steam_alloy_smelter" /> |
| Compressor | <ItemLink id="gtceu:lp_steam_compressor" /> | <ItemLink id="gtceu:hp_steam_compressor" /> |
| Macerator | <ItemLink id="gtceu:lp_steam_macerator" /> | <ItemLink id="gtceu:hp_steam_macerator" /> |
| Extractor | <ItemLink id="gtceu:lp_steam_extractor" /> | <ItemLink id="gtceu:hp_steam_extractor" /> |
| Forge Hammer | <ItemLink id="gtceu:lp_steam_forge_hammer" /> | <ItemLink id="gtceu:hp_steam_forge_hammer" /> |
| Rock Crusher | <ItemLink id="gtceu:lp_steam_rock_crusher" /> | <ItemLink id="gtceu:hp_steam_rock_crusher" /> |

* Steam machines don't mind rain and never explode.
* Each one **vents** spent steam out of its vent side after every recipe. If that side is blocked the machine
  waits, and standing in front of the vent hurts. Turn the vent with a wrench.
* They don't push their outputs out; pull them with a hopper or pipe.

For more throughput, the GTNA **Large Steam** multiblocks run the same recipes with parallels; see
[Steam Multiblocks](../gtmultis/steam-multiblocks.md).

## Electric machines

* **Buffer**: each machine stores 64x its voltage in EU (2,048 EU at LV).
* **Slots**: item and fluid inputs and outputs, a charge slot for a battery or tool, and the **programmed circuit**
  slot (click or scroll to pick a circuit number, so you don't need a real circuit item).
* **Auto-output**: the two buttons at the bottom left push items and fluids out of the output side.
* **Overclocking**: a higher-tier machine runs lower recipes faster, 4x power for 2x speed each tier; see
  [Overclocking Rules](oc/overclocking-rules.md).
* **Power failure**: if the buffer runs dry, the recipe restarts from zero. The inputs stay.

<Color id="red">A packet above the machine's voltage makes it explode.</Color> In Apex, weather and fire never set off
machines (that GTCEu option is off); only overvoltage does.
""" + SRC, parent='gregtech/gregtech-index.md', pos=32, icon='gtceu:lv_macerator')

# ====================================================================== GregTech multiblocks
def gtmulti(path, title, mid, tier, body, pos, extra_items=(), recipe=True, icon=None):
    """A GregTech multiblock page: preview first, then the text, then the controller recipe."""
    text = f'# {title}\n\n<Multiblock id="{mid}" />\n\n**Unlocks:** {tier}\n\n' + body.strip('\n') + '\n'
    if recipe:
        text += f'\n## Controller recipe\n\n<GTRecipesFor id="{mid}" limit="1" fallbackText="See JEI for the controller recipe." />\n'
    page(f'gtmultis/{path}.md', title, text + SRC, parent='gtmultis/gtmultis-index.md', pos=pos,
         icon=icon or mid, items=[mid, *extra_items])

page('gtmultis/gtmultis-index.md', 'GregTech Multiblocks', """
# GregTech Multiblocks

The multiblocks from GregTech itself (GTCEu), its GCYM add-on, and the GT New Horizons machines that GTNA and GTCA
bring back. Apex's own machines are under [Apex Multiblocks](../multiblocks/multiblocks-index.md). How hatches,
maintenance and mufflers work is in [Multiblock Basics](../gregtech/multiblock-basics.md).

## Steam age

| Machine | What it does |
|---|---|
| [Coke Oven](coke_oven.md) | Coal and logs into coke, charcoal and creosote, no fuel |
| [Primitive Blast Furnace](primitive_blast_furnace.md) | Your first steel |
| [Steam Multiblocks](steam-multiblocks.md) | Steam Grinder, Steam Oven and the GTNA Large Steam machines |

## LV to EV

| Machine | Tier | What it does |
|---|---|---|
| [Electric Blast Furnace](../gregtech/ebf-and-coils.md) | LV | Hot metals: aluminium, stainless steel, titanium... |
| [Vacuum Freezer](vacuum_freezer.md) | HV | Cools hot ingots, freezes gases |
| [Large Chemical Reactor](large_chemical_reactor.md) | HV | Chemistry with perfect overclocks |
| [Distillation Tower](distillation_tower.md) | HV | Oil and other fluids into their fractions |
| [Pyrolyse Oven](pyrolyse_oven.md) | MV | Logs into charcoal, creosote and wood gases |
| [Implosion Compressor](implosion_compressor.md) | HV | Gem dust into gems with explosives |
| [Cleanroom](../gregtech/cleanroom.md) | HV | Clean (and later sterile) room for circuits |
| [Industrial Coke Oven](industrial_coke_oven.md) | MV | Electric, faster coke oven (GTCA) |

## IV and up

| Machine | Tier | What it does |
|---|---|---|
| [Assembly Line](assembly_line.md) | IV | High-tier components from research data |
| [Research and Data](research.md) | LuV | Research Station, HPCA, Data Bank |
| [Alloy Blast Smelter](alloy_blast_smelter.md) | EV | Dusts straight into molten alloys |
| [GCYM Large Machines](gcym.md) | IV | Large versions of every single-block machine, with parallels |
| [Power Substation](power_substation.md) | EV | Huge power storage |
| [Active Transformer](active_transformer.md) | IV | Moves power between any hatches, and lasers |
| [Chemical Plant](chemical_plant.md) | IV | LCR recipes with parallels and coil bonuses (GTNA) |
| [Mega Alloy Blast Smelter](mega_alloy_blast_smelter.md) | UV | Faster, cheaper ABS (GTNA) |
| [Eye of Harmony](eye_of_harmony.md) | UXV | A miniature universe (GTNA) |

The tier is when you can usually build it; JEI has the exact controller recipe.
""" + SRC, parent=None, pos=55, icon='gtceu:electric_blast_furnace')

gtmulti('coke_oven', 'Coke Oven', 'gtceu:coke_oven', 'Steam age', """
The **Coke Oven** bakes coal into **coke** and logs into **charcoal**, giving **creosote** as a by-product. It needs
no fuel and no power.

* A 3x3x3 cube of **Coke Oven Bricks** with the controller in the middle of one side. It is hollow.
* Put items into the controller (or a **Coke Oven Hatch** in the wall, which also lets pipes and hoppers in and out).
* It holds 32 buckets of creosote. Pump creosote out through a hatch, or the oven stops when the tank is full.

Coke burns longer than coal and is what the [Primitive Blast Furnace](primitive_blast_furnace.md) likes best.
Creosote fuels a steam boiler or treats wood.
""", 1, icon='gtceu:coke_oven')

gtmulti('primitive_blast_furnace', 'Primitive Blast Furnace', 'gtceu:primitive_blast_furnace', 'Steam age', """
The **Primitive Blast Furnace** (PBF) makes your **first steel** from iron, before you have electricity. It is slow,
so most players build several.

* 3 wide, 3 deep, 4 tall, made of **Firebricks**, with the controller in the bottom layer. The middle is hollow
  and the top is open.
* Put iron ingots (or iron dust, wrought iron) in, plus fuel: **coke** is fastest, coal and charcoal work too.
  Each batch also gives a little ash.
* Firebricks come from **fireclay** (clay plus brick dust); bake them in a furnace.

The [Electric Blast Furnace](../gregtech/ebf-and-coils.md) replaces it, but a few PBFs left running keep steel
coming for free while the EBF does other metals.
""", 2)

gtmulti('vacuum_freezer', 'Vacuum Freezer', 'gtceu:vacuum_freezer', 'HV', """
The **Vacuum Freezer** cools **hot ingots** from the Electric Blast Furnace into normal ingots, and freezes gases
and fluids (water into ice, air into liquid air, and so on). Anything hotter than 1,750 K leaves the EBF as a hot
ingot, so from stainless steel and titanium on you need one.

* A 3x3x3 of **Frost Proof Machine Casings** with the controller in the middle of one side.
* Buses and hatches replace casings anywhere. Normal overclocking, with subtick parallels.

The GCYM **Bulk Blast Chiller** is the large version, with parallels (see [GCYM Large Machines](gcym.md)).
""", 10)

gtmulti('large_chemical_reactor', 'Large Chemical Reactor', 'gtceu:large_chemical_reactor', 'HV', """
The **Large Chemical Reactor** (LCR) runs every Chemical Reactor recipe plus many recipes no single block can do.
It uses **perfect overclocks** (4x power, 4x speed), so it never wastes energy by running at a higher tier.

* A 3x3x3 of **Chemically Inert Machine Casings** around **PTFE Pipe Casings**, with exactly one
  **Cupronickel Coil Block** in the middle.
* Many recipes need several fluids and up to 6 outputs: give it enough hatches.
* Use [distinct buses](../gregtech/multiblock-basics.md) and programmed circuits to keep similar recipes apart.

Apex's pack setting makes multiblocks follow cleanroom rules: for cleanroom recipes, give it a **Cleaning
Maintenance Hatch** or build it inside a [Cleanroom](../gregtech/cleanroom.md).

The GTNA [Chemical Plant](chemical_plant.md) runs the same recipes with parallels.
""", 11)

gtmulti('distillation_tower', 'Distillation Tower', 'gtceu:distillation_tower', 'HV', """
The **Distillation Tower** splits a fluid into all its fractions at once: oil into fuels and gases, wood tar, liquid
air and many more. A Distillery only gives one output per recipe; the tower gives all of them.

* Built from **Clean Stainless Steel Casings**. The bottom layer holds the controller, the Input Hatch, an Output
  Bus (for solid outputs) and the Energy Hatch.
* Each layer above holds **exactly one Output Hatch**. Outputs go up the tower in the order JEI lists them: the
  first output in the first layer above the bottom, and so on. A recipe with 6 fluid outputs needs 6 layers.
* Normal overclocking, with subtick parallels.

See the [oil processing](../lines/oil.md) line for what to do with each fraction.
""", 12)

gtmulti('pyrolyse_oven', 'Pyrolyse Oven', 'gtceu:pyrolyse_oven', 'MV', """
The **Pyrolyse Oven** is an electric coke oven: it turns **logs** into **charcoal** and wood gases or creosote, and
**coal** into **coke**, with programmed circuits choosing the products. It is an early source of methane and benzene
precursors.

* Coils set the speed: **Cupronickel** coils are 25% slower, **Kanthal** is normal speed, and each coil tier after
  Kanthal adds 50% speed.
* Needs a Muffler Hatch.
""", 13)

gtmulti('implosion_compressor', 'Implosion Compressor', 'gtceu:implosion_compressor', 'HV', """
The **Implosion Compressor** turns gem dust into gems using explosives (TNT, dynamite, powderbarrels or Industrial
TNT). It is the main way to make many gems, flawless gems for lenses, and some late-game materials.

* A 3x3x3 of **Solid Steel Machine Casings** with a Muffler Hatch.
* The explosive is used up each recipe; JEI shows which ones each recipe accepts.

Apex's [Electric Implosion Compressor](../multiblocks/electric_implosion_compressor.md) does the same job without
explosives, later.
""", 14)

gtmulti('industrial_coke_oven', 'Industrial Coke Oven', 'gtca:industrial_coke_oven', 'MV', """
The **Industrial Coke Oven** (from GTCA) is an electric, multi-recipe coke oven: coal into coke, logs into charcoal
and creosote, faster and in bulk. Use it once the brick [Coke Oven](coke_oven.md) can't keep up. Its controller
tooltip and JEI page list its exact speed and recipes.
""", 15)

gtmulti('assembly_line', 'Assembly Line', 'gtceu:assembly_line', 'IV', """
The **Assembly Line** makes the big components of the late game: LuV and higher motors, pumps, field generators,
fusion parts, high-tier circuits and machine hulls. Every recipe needs **research data** first.

## Research

* Early recipes are researched in a **Scanner**: put the item (shown in JEI) and an empty **Data Stick** in.
* Later recipes need a **Data Orb** or **Data Module** and the [Research Station](research.md).
* The finished data goes into a **Data Access Hatch** on the Assembly Line (or, for many recipes, a
  [Data Bank](research.md) connected with optical cables).

## Building it

* 5 to 16 slices long. Each slice holds one **Input Bus** at the bottom; the first slice holds the controller.
* **Item order matters in Apex**: the first ingredient goes in the first bus (next to the controller), the second in
  the second, and so on. **Fluids are ordered too**: put each fluid in the hatches in the order JEI lists them.
* Energy Hatches go on the top; the Output Bus is at the far end.
* Normal overclocks, no subtick parallels.

Use [distinct buses](../gregtech/multiblock-basics.md) only where needed; with ordered inputs the Assembly Line
already reads each bus by position. Many players feed each bus from its own ME Input Bus or interface.
""", 20)

gtmulti('research', 'Research and Data', 'gtceu:research_station', 'LuV', """
Late Assembly Line recipes need research done in the **Research Station**, which needs **computation** (CWU/t)
from a **High Performance Computation Array**.

## Research Station

<ItemLink id="gtceu:research_station" /> scans an item onto a **Data Orb** or **Data Module**. It needs a steady
supply of computation through an **Optical Data Reception Hatch**; more computation finishes the research faster.

## High Performance Computation Array (HPCA)

<Multiblock id="gtceu:high_performance_computation_array" />

The <ItemLink id="gtceu:high_performance_computation_array" /> produces computation. Fill its 3x3 component grid:

* **Computation components** produce CWU/t (and heat).
* **Cooling components**: heat sinks are passive; active coolers need **PCB Coolant** through an Input Hatch.
* **Bridges** let several HPCAs pass computation through one another.

If the array gets too hot, components take damage. Send the computation out through an **Optical Data
Transmission Hatch** and optical cables to the Research Station.

## Data Bank

<Multiblock id="gtceu:data_bank" />

The <ItemLink id="gtceu:data_bank" /> stores research data orbs and modules in **Data Access Hatches** and shares
them with many Assembly Lines over optical cables. Data Banks can be chained. It uses a small amount of power per
hatch.
""", 21, extra_items=('gtceu:high_performance_computation_array', 'gtceu:data_bank'))

gtmulti('alloy_blast_smelter', 'Alloy Blast Smelter', 'gtceu:alloy_blast_smelter', 'EV', """
The **Alloy Blast Smelter** (ABS, from GCYM) melts the dusts of an alloy straight into the **molten alloy**,
skipping the Mixer and the EBF. Cool the molten metal in a Vacuum Freezer or Fluid Solidifier.

* Uses **heating coils** like the EBF, and the same bonuses: 5% less energy per 900 K above the recipe, a perfect
  overclock per 1,800 K, +100 K per Energy Hatch tier above MV.
* Takes a **Parallel Control Hatch** for 4 to 256 parallels.
* Many ABS recipes use programmed circuits; use one distinct Input Bus per circuit.

The GTNA [Mega Alloy Blast Smelter](mega_alloy_blast_smelter.md) is the late upgrade.
""", 30)

gtmulti('mega_alloy_blast_smelter', 'Mega Alloy Blast Smelter', 'gtna:mega_alloy_blast_smelter', 'UV', """
The **Mega Alloy Blast Smelter** (GTNA, ported from GTNH) runs Alloy Blast Smelter recipes in bulk:

* Uses **80% of the recipe's EU** and **60% of its time**.
* The coil temperature must meet the recipe's heat.
* Takes a **Parallel Control Hatch** for more parallels.
""", 31)

gtmulti('chemical_plant', 'Chemical Plant', 'gtna:chemical_plant', 'IV', """
The **Chemical Plant** (GTNA, ported from GTNH) runs **Large Chemical Reactor** recipes with a Parallel Hatch and
**perfect overclocks**.

* Every coil tier above Cupronickel cuts energy and duration by 5%.
* The Parallel Hatch multiplies the batch; the controller shows the current multipliers.
* GTNH's catalyst hatch is not part of this port.
""", 32)

gtmulti('power_substation', 'Power Substation', 'gtceu:power_substation', 'EV', """
The **Power Substation** (PSS) stores huge amounts of EU in layers of **capacitor blocks**.

* Build it taller with more capacitor layers (up to the number in the tooltip). Capacitors don't have to be the same
  tier; empty capacitors fill unused spots.
* Charge it through Energy Hatches (or 64 A substation and laser hatches) and draw from it through Dynamo Hatches.
* It loses 1% of its stored energy every 24 hours, capped per capacitor block.

Point a Jade or a Portable Scanner at it, or open the controller, to see the average input and output.
""", 40)

gtmulti('active_transformer', 'Active Transformer', 'gtceu:active_transformer', 'IV', """
The **Active Transformer** combines **any number of Energy Hatches** (inputs) into **any number of Dynamo Hatches**
(outputs), at any tiers, and works with **laser hatches** to send huge amounts of power over long distances through
laser pipes.

* Typical uses: turn many amps of one voltage into fewer amps of a higher one, or feed a laser line.
* In Apex the "harmless active transformers" option is off: a formed transformer holding energy explodes if its
  structure breaks. Empty it before you change it.
""", 41)

gtmulti('eye_of_harmony', 'Eye of Harmony', 'gtna:eye_of_harmony', 'UXV', """
The **Eye of Harmony** (GTNA, ported from GTNH) creates a miniature universe and pulls whole planets' worth of
items and fluids out of it.

* Startup power comes from your **GTNA wireless network**, not from Energy Hatches. Bind it with a Data Stick to
  change the network owner.
* Each run needs huge amounts of **Hydrogen** and **Helium** (the Overworld planet needs 1,000,000 buckets of each).
  All buffered gas is used at startup; extra lowers the chance and yield.
* A programmed circuit from 0 to 24 picks the speed: each step halves the time, and the startup EU grows with
  (circuit + 1) squared.
* The planet blocks decide what it produces; outputs go to normal buses and hatches.
""", 50)

# GCYM in one page
GCYM = [('large_maceration_tower', 'Macerator'), ('large_chemical_bath', 'Chemical Bath / Ore Washer'),
        ('large_centrifuge', 'Centrifuge / Thermal Centrifuge'), ('large_mixer', 'Mixer'),
        ('large_electrolyzer', 'Electrolyzer'), ('large_electromagnet', 'Electromagnetic Separator / Polarizer'),
        ('large_packer', 'Packer'), ('large_assembler', 'Assembler'), ('large_circuit_assembler', 'Circuit Assembler'),
        ('large_arc_smelter', 'Arc Furnace'), ('large_engraving_laser', 'Laser Engraver'),
        ('large_sifting_funnel', 'Sifter'), ('large_autoclave', 'Autoclave'),
        ('large_material_press', 'Bender / Compressor / Forge Hammer / Forming Press'), ('large_brewer', 'Brewery / Fermenter / Fluid Heater'),
        ('large_cutter', 'Cutter / Lathe'), ('large_distillery', 'Distillery / Distillation Tower'),
        ('large_extractor', 'Extractor / Canner'), ('large_extruder', 'Extruder'), ('large_solidifier', 'Fluid Solidifier'),
        ('large_wiremill', 'Wiremill'), ('mega_blast_furnace', 'Electric Blast Furnace (Rotary Hearth Furnace)'),
        ('mega_vacuum_freezer', 'Vacuum Freezer (Bulk Blast Chiller)')]
rows = '\n'.join(f'| <ItemLink id="gtceu:{i}" /> | {r} |' for i, r in GCYM)
page('gtmultis/gcym.md', 'GCYM Large Machines', f"""
# GCYM Large Machines

GCYM (GregicalityMultiblocks, part of GTCEu) adds a large multiblock for almost every single-block machine. They all
work the same way:

* They take a **Parallel Control Hatch** (IV: 4, LuV: 16, ZPM: 64, UV: 256 parallels).
* Normal overclocks with subtick parallels, and batch mode.
* Most have a few recipe modes; switch with the button in the controller.
* Most can be built from IV, and the casings, pipe and gearbox blocks are made in the Assembler.

| Machine | Replaces |
|---|---|
{rows}

<Multiblock id="gtceu:large_mixer" />
""" + SRC, parent='gtmultis/gtmultis-index.md', pos=35, icon='gtceu:large_mixer',
     items=[f'gtceu:{i}' for i, _ in GCYM])

# steam multis
STEAM = [('gtceu:steam_grinder', 'Macerator', 'up to 8 (pack setting)', 'GTCEu; Steam Bus and Steam Hatch only'),
         ('gtceu:steam_oven', 'Furnace', 'up to 8 (pack setting)', 'GTCEu; Steam Bus and Steam Hatch only'),
         ('gtna:large_steam_crusher', 'Macerator', '128', '2x speed, 80% steam'),
         ('gtna:large_steam_furnace', 'Furnace', '128', '10x speed, 50% steam'),
         ('gtna:large_steam_alloy_smelter', 'Alloy Smelter', '64', '43% faster'),
         ('gtna:large_steam_compressor', 'Compressor', '48', '2.5x speed'),
         ('gtna:large_steam_extractor', 'Extractor', '48', '75% faster'),
         ('gtna:large_steam_hammer', 'Forge Hammer', '64', '2x speed'),
         ('gtna:large_steam_mixer', 'Mixer', '64', '2x speed'),
         ('gtna:large_steam_centrifuge', 'Centrifuge', '64', '3.5x speed'),
         ('gtna:large_steam_thermal_centrifuge', 'Thermal Centrifuge', '64', '3x speed'),
         ('gtna:large_steam_ore_washer', 'Ore Washer', '96', '5x speed'),
         ('gtna:large_steam_bath', 'Chemical Bath', '64', '2x speed'),
         ('gtna:large_steam_forming_press', 'Forming Press', '32', '2.5x speed, 40% steam'),
         ('gtna:large_steam_bending', 'Bender', '16', '2x speed, 50% steam'),
         ('gtna:large_steam_cutting', 'Cutter', '16', '2x speed, 50% steam'),
         ('gtna:large_steam_extruder', 'Extruder', '16', '2x speed, 50% steam'),
         ('gtna:large_steam_lathe', 'Lathe', '16', '2x speed, 50% steam'),
         ('gtna:large_steam_wiremill', 'Wiremill', '16', '2x speed, 50% steam'),
         ('gtna:large_steam_sifter', 'Sifter', '16', '2x speed, 50% steam'),
         ('gtna:large_steam_circuit_assembler', 'Circuit Assembler', '64', 'engrave a circuit to target it')]
srows = '\n'.join(f'| <ItemLink id="{i}" /> | {r} | {p} | {n} |' for i, r, p, n in STEAM)
page('gtmultis/steam-multiblocks.md', 'Steam Multiblocks', f"""
# Steam Multiblocks

Steam multiblocks run steam-age recipes with **parallels**, long before you have electricity to spare. GTCEu has
two; GTNA adds the GT New Horizons steam machines.

| Machine | Recipes | Parallels | Notes |
|---|---|---|---|
{srows}

* Steam goes in through a **Steam Hatch**; the GTCEu ones only accept **Steam Input/Output Buses**.
* Speeds are compared with the single-block steam machine; check each controller's tooltip for its structure.
* Feed them from a big boiler: a Large Bronze Boiler, or the GTNA **Large Steam Solar Boiler** and **Large Steam
  Storage Tank**.

<Multiblock id="gtna:large_steam_crusher" />
""" + SRC, parent='gtmultis/gtmultis-index.md', pos=3, icon='gtceu:steam_grinder',
     items=[i for i, *_ in STEAM])

# ====================================================================== EBF and cleanroom (replace the short pages)
page('gregtech/ebf-and-coils.md', 'Electric Blast Furnace and Coils', """
# Electric Blast Furnace and Coils

<Multiblock id="gtceu:electric_blast_furnace" />

The <ItemLink id="gtceu:electric_blast_furnace" /> (EBF) is usually your first electric multiblock, built in LV.
Electricity and **heating coils** reach temperatures the Primitive Blast Furnace can't: your first big job is
**aluminium**, then stainless steel, titanium, tungstensteel and most hot metals of the game.

## Coils

The coils set the furnace's **heat**. A recipe only runs if the heat is at least the recipe's temperature (JEI
shows it under the recipe).

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

Bonuses:

* **+100 K** for every Energy Hatch tier above MV.
* Every **900 K** above the recipe's temperature: **5% less energy** (multiplied, before overclocking).
* Every **1,800 K** above: one overclock becomes **perfect** (4x power, 4x speed).

## Building it

3 x 3 x 4:

1. **Bottom layer**: the controller at the front middle, then the Energy Hatches, a Maintenance Hatch, at least one
   input (bus or hatch) and one output (bus or hatch). Fill the rest with
   <ItemLink id="gtceu:heatproof_machine_casing" />. The bottom middle block is part of the structure too.
2. **Two rings of 8 coils** above it, all the same tier, with **air** in the middle of both rings.
3. **Top layer**: casings around a <ItemLink id="gtceu:lv_muffler_hatch" /> in the middle, facing up into open air.
   Output Hatches may replace top casings for gas outputs.

The preview above can build it for you. Keep the two middle blocks empty: a torch or cable there breaks the
structure.

## Two Energy Hatches

An LV Energy Hatch takes 2 A at 32 V. With **two LV hatches** the EBF can run **MV recipes** (up to 120 EU/t,
since 4 A x 32 V = 128 EU/t). The cable still carries **LV**: give it more LV amps, never MV voltage. See
[Overclocking and Parallels](oc/oc-index.md).

Maintenance matters here: each maintenance problem slows the furnace and raises its energy use, so an early EBF on
two LV hatches can run short. Fix problems at the Maintenance Hatch.

## Your first aluminium

1. Look up the aluminium EBF recipes in JEI and pick one you can supply: items, circuit, gas, EU/t and heat.
2. Check the controller says the structure is formed with no maintenance problems, and the muffler is free.
3. Make room in the outputs.
4. Get power stable first: generators running, batteries charged, both hatches connected.
5. Put in one small batch and watch a full recipe finish before you automate it.

Metals hotter than **1,750 K** come out as **hot ingots**: cool them in a
[Vacuum Freezer](../gtmultis/vacuum_freezer.md).

<GTRecipesFor id="gtceu:electric_blast_furnace" limit="1" />
""" + SRC, parent='gregtech/gregtech-index.md', pos=50, icon='gtceu:electric_blast_furnace',
     items=['gtceu:electric_blast_furnace'])

page('gregtech/cleanroom.md', 'Cleanroom', """
# Cleanroom

<Multiblock id="gtceu:cleanroom" />

Advanced circuits, wafers and many components need a **clean environment**. The <ItemLink id="gtceu:cleanroom" />
provides it to every machine built inside it. It doesn't process anything itself: the machines inside do, with their
own power and pipes.

| | |
|---|---|
| Unlocks | HV (it runs fine on LV power) |
| Size | 5x5x5 up to 15x15x15 (outside) |
| Power | 30 EU/t while dirty, 4 EU/t once clean; a higher Energy Hatch cleans faster |
| Inside | Any machines except generators, mufflers, drills and primitive machines |

## Building it

* Walls, floor and ceiling edge of <ItemLink id="gtceu:plascrete" /> (or <ItemLink id="gtceu:cleanroom_glass" />).
* The ceiling inside the edge is <ItemLink id="gtceu:filter_casing" />, with the controller in the middle of the
  ceiling. Sterile Filter Casings make a **sterile** cleanroom, needed for wetware and other late recipes.
* One Energy Hatch and one Maintenance Hatch in the walls.
* Up to **4 doors**. The room stays clean while a door is open.
* To bring power in, put **Machine Hulls** or **Diodes** in the walls. Hulls also pass items, fluids and up to 8 AE2
  channels.

## Running it

1. Fix the maintenance problems.
2. Power it and wait until the controller says it is **clean**.
3. Then start the machines inside. A recipe marked "Requires Cleanroom" in JEI won't start in a dirty room.

Keep it powered: if it loses power or the structure breaks, it gets dirty again.

## Multiblocks and cleanrooms

In Apex, **multiblocks follow cleanroom rules too** (the pack doesn't exempt them). Give a multiblock that runs
cleanroom recipes a **Cleaning Maintenance Hatch** (a small built-in cleanroom), or build it inside a cleanroom.

![A finished cleanroom](../assets/images/cleanroom.png)

<GTRecipesFor id="gtceu:cleanroom" limit="1" />
""" + SRC, parent='gregtech/gregtech-index.md', pos=80, icon='gtceu:cleanroom', items=['gtceu:cleanroom'])

# ====================================================================== holes: more GTCEu multis
gtmulti('multi_smelter', 'Multi Smelter', 'gtceu:multi_smelter', 'MV', """
The **Multi Smelter** smelts many items at once in normal furnace recipes (and alloy smelter recipes in its second
mode). Its **coils** set how many items it handles per run: hotter coils, more parallel smelting. It uses very little
power per item, so it is the best bulk furnace of the mid game.
""", 16)

gtmulti('cracker', 'Oil Cracking Unit', 'gtceu:cracker', 'HV', """
The **Oil Cracking Unit** cracks heavy fuels and gases with **steam** or **hydrogen** into lighter, more useful
products (for example heavy fuel into light fuel and naphtha, ethane into ethylene). Cracked products then go into the
[Distillation Tower](distillation_tower.md).

* Coils lower its energy use: each coil tier above Cupronickel saves 10%.
* See the [oil processing](../lines/oil.md) line for which cracked products to make.
""", 17)

gtmulti('large_boilers', 'Large Boilers', 'gtceu:bronze_large_boiler', 'Steam age to EV', """
The **Large Boilers** burn solid or liquid fuel and make a lot of steam for steam turbines and steam multis.

| Boiler | Max temperature | Notes |
|---|---|---|
| <ItemLink id="gtceu:bronze_large_boiler" /> | 800 K | Steam age |
| <ItemLink id="gtceu:steel_large_boiler" /> | 1,800 K | LV |
| <ItemLink id="gtceu:titanium_large_boiler" /> | 3,200 K | EV |
| <ItemLink id="gtceu:tungstensteel_large_boiler" /> | 6,400 K | IV, heats up twice as fast |

* They need a while to heat up; steam output grows with temperature.
* Each mB of water becomes 160 mB of steam (pack setting). **Never let a hot boiler run dry**: refilling it with water
  while hot makes it explode. Keep the water supply steady.
* The throttle button in the controller lowers fuel use and output.
""", 4, icon='gtceu:steel_large_boiler', extra_items=('gtceu:steel_large_boiler', 'gtceu:titanium_large_boiler',
                                                      'gtceu:tungstensteel_large_boiler'))

gtmulti('large_turbines', 'Large Turbines and Combustion Engines', 'gtceu:steam_large_turbine', 'HV to LuV', """
The big generators of the mid game. All output through a **Dynamo Hatch**.

| Generator | Fuel |
|---|---|
| <ItemLink id="gtceu:steam_large_turbine" /> | Steam |
| <ItemLink id="gtceu:gas_large_turbine" /> | Gas fuels (methane, natural gas, ...) |
| <ItemLink id="gtceu:plasma_large_turbine" /> | Plasma from fusion |
| <ItemLink id="gtceu:large_combustion_engine" /> | Liquid fuels (diesel, gasoline...); oxygen boosts it |
| <ItemLink id="gtceu:extreme_combustion_engine" /> | Liquid fuels; liquid oxygen boosts it |

## Turbines

* A turbine needs a **turbine rotor** in its **Rotor Holder**. The rotor's material sets efficiency, power and
  durability; the rotor wears out and must be replaced.
* A higher-tier Rotor Holder spins faster and gives more power.
* Turbines take time to spin up and spin down.

## Combustion engines

* Need **lubricant** as well as fuel. Adding oxygen (or liquid oxygen for the Extreme engine) boosts output.
* The intake side must face open air.
""", 5, extra_items=('gtceu:gas_large_turbine', 'gtceu:plasma_large_turbine', 'gtceu:large_combustion_engine',
                     'gtceu:extreme_combustion_engine'))

gtmulti('drilling', 'Fluid Drilling Rigs and Large Miners', 'gtceu:mv_fluid_drilling_rig', 'MV to LuV', """
## Fluid Drilling Rig

The <ItemLink id="gtceu:mv_fluid_drilling_rig" /> (and its HV and EV versions) pumps **bedrock fluid veins**: oil,
natural gas, salt water, lava and more, depending on the area. Every chunk group has one hidden fluid vein; a
**Prospector** in fluid mode shows what is under you and how much. Veins slowly deplete down to a minimum rate;
higher-tier rigs pump more and deplete veins slower.

## Large Miner

<Multiblock id="gtceu:ev_large_miner" />

The <ItemLink id="gtceu:ev_large_miner" /> (and IV and LuV versions) mines a large area below it chunk by chunk,
doubling or tripling ore output with **drilling fluid**. It needs fluid input and power, and replaces mined blocks
with cobblestone in this pack.
""", 18, extra_items=('gtceu:ev_large_miner', 'gtceu:hv_fluid_drilling_rig', 'gtceu:ev_fluid_drilling_rig'))

gtmulti('fusion_reactor', 'Fusion Reactors', 'gtceu:luv_fusion_reactor', 'LuV to UV', """
The **Fusion Reactors** (MK I at LuV, MK II at ZPM, MK III at UV) fuse light elements into heavy ones and make
plasma for the Plasma Turbine. Each recipe needs a burst of **startup energy** stored in the reactor's own buffer, so
a reactor needs many Energy Hatches of its tier.

The full walkthrough is on the [Fusion](../lines/fusion.md) page; Apex adds Fusion MK IV and MK V and compact fusion
computers on top (see [Apex Multiblocks](../multiblocks/multiblocks-index.md)).
""", 42, extra_items=('gtceu:zpm_fusion_reactor', 'gtceu:uv_fusion_reactor'))

gtmulti('charcoal_pile_igniter', 'Charcoal Pile Igniter', 'gtceu:charcoal_pile_igniter', 'Steam age', """
The **Charcoal Pile Igniter** turns a big pile of **logs** covered with dirt or grass into **charcoal** without any
furnace. Build the log pile next to it (the controller shows the size), cover it, and light the igniter with flint and
steel. A cheap way to get lots of charcoal early.
""", 6)

gtmulti('primitive_pump', 'Primitive Water Pump', 'gtceu:primitive_pump', 'Steam age', """
The **Primitive Water Pump** gives free water for your first boilers. It pumps more in wet biomes (oceans, rivers,
swamps) and more with a better output hatch. Pipe the water out of its pump hatch.
""", 7)

# ====================================================================== holes: covers, pipes, hazards, circuits
page('gregtech/covers.md', 'Covers', """
# Covers

**Covers** go on the side of a machine, pipe or tank and add a job to that side. Shift-right-click a side with a cover
to place it; open it with a screwdriver or by right-clicking; remove it with a crowbar.

| Cover | What it does |
|---|---|
| <ItemLink id="gtceu:lv_conveyor_module" /> | Moves items in or out of the side. Faster per tier. |
| <ItemLink id="gtceu:lv_robot_arm" /> | Like a conveyor, but can move exact amounts or keep a stock |
| <ItemLink id="gtceu:lv_electric_pump" /> | Moves fluids |
| <ItemLink id="gtceu:lv_fluid_regulator" /> | Like a pump, with exact amounts |
| <ItemLink id="gtceu:item_filter" /> / <ItemLink id="gtceu:item_tag_filter" /> | Filters for conveyors, arms and filter covers |
| <ItemLink id="gtceu:machine_controller_cover" /> | Turns the machine on or off with redstone |
| <ItemLink id="gtceu:energy_detector_cover" /> | Gives a redstone signal from stored energy |
| <ItemLink id="gtceu:lv_solar_panel" /> | A small solar generator on top of a machine |
| <ItemLink id="gtceu:shutter_module_cover" /> | Blocks the side completely |

* Conveyors, pumps and arms have a direction (import or export) and can be set to only work with a redstone signal.
* A conveyor on a machine's output side, set to export, is the simplest automation: it pushes products into the next
  machine or chest.
* The **Machine Controller** set to "prevent power fail" stops a multiblock from shutting down when power runs out.
""" + SRC.replace('Adapted from the GuideNH DefaultGuide (GT New Horizons)', 'Written'),
     parent='gregtech/gregtech-index.md', pos=33, icon='gtceu:lv_conveyor_module')

page('gregtech/pipes.md', 'Pipes', """
# Pipes

## Item pipes

GregTech item pipes move items between inventories. Wider pipes and better materials move more items per second; a
**restrictive** pipe makes items prefer other routes. Items flow from an inventory that pushes (a machine with
auto-output, or a conveyor cover set to export) to the nearest free inventory.

## Fluid pipes

Fluid pipes have a **throughput** (mB/t), a **max temperature** and flags in their tooltip: whether they hold **gases**,
**acids**, **cryogenics** and **plasma**. A pipe that can't hold a fluid leaks or burns. Use bronze or steel early,
stainless steel and PTFE (polytetrafluoroethylene) for acids, and better materials for hot or cryogenic fluids.
Quadruple and nonuple pipes carry several fluids side by side.

## Connections

Apex uses GT6-style pipes and cables: they connect only where you connect them, with a **wrench** for pipes or a
**wire cutter** for cables. Shift-right-click a placed pipe with another pipe to swap it.
""" + SRC.replace('Adapted from the GuideNH DefaultGuide (GT New Horizons)', 'Written'),
     parent='gregtech/gregtech-index.md', pos=34, icon='gtceu:bronze_normal_fluid_pipe')

page('gregtech/hazards.md', 'Chemical Hazards', """
# Chemical Hazards

Hazards are **on** in Apex. Many dangerous materials (toxic dusts, acids, radioactive elements, carcinogens...)
hurt you while you carry them or stand near them as fluids, and some give lasting **medical conditions** that get
worse over time. The item's tooltip tells you its hazard and what protects against it.

## Protection

* <ItemLink id="gtceu:face_mask" /> and <ItemLink id="gtceu:rubber_gloves" />: early protection against breathing
  and touching hazardous materials.
* The full **Hazmat suit** (<ItemLink id="gtceu:hazmat_headpiece" />, <ItemLink id="gtceu:hazmat_chestpiece" />,
  <ItemLink id="gtceu:hazmat_leggings" />, <ItemLink id="gtceu:hazmat_boots" />): protects against everything.
* Medicine such as <ItemLink id="gtceu:paracetamol_pill" /> and <ItemLink id="gtceu:rad_away_pill" /> treats some
  conditions.

Keep hazardous materials in machines, tanks and your ME system rather than in your inventory, and wear protection
when you handle them.
""" + SRC.replace('Adapted from the GuideNH DefaultGuide (GT New Horizons)', 'Written'),
     parent='gregtech/gregtech-index.md', pos=35, icon='gtceu:hazmat_headpiece')
