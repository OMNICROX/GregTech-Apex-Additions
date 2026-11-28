---
navigation:
  title: "Single-Block Machines"
  parent: gregtech/gregtech-index.md
  position: 32
  icon: gtceu:lv_macerator
---

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


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>

## Quests

These quests cover this page. Click one to open it in the quest book.

* <QuestLink id="28471CD55141152D">High Pressure Machines</QuestLink> (Tier 0.5 - Steam Age)
* <QuestLink id="306D120352961847">Steam Macerator</QuestLink> (Tier 0.5 - Steam Age)
