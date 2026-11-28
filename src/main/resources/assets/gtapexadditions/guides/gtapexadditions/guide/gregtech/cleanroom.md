---
navigation:
  title: "Cleanroom"
  parent: gregtech/gregtech-index.md
  position: 80
  icon: gtceu:cleanroom
item_ids:
- gtceu:cleanroom
---

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

![A 5x5x5 cleanroom: plascrete walls, filter ceiling, iron door, cleanroom glass and an energy hatch](../assets/images/cleanroom.png)

<GTRecipesFor id="gtceu:cleanroom" limit="1" />


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>

## Quests

These quests cover this page. Click one to open it in the quest book.

* <QuestLink id="0C5D349ECFADB3AD">Stem Cells</QuestLink> (Tier 6 - LuV)
* <QuestLink id="65DFA1E47A5DE093">Cleanroom</QuestLink> (Tier 3 - HV)
