---
navigation:
  title: "Overclocking Rules"
  parent: gregtech/oc/oc-index.md
  position: 1
  icon: gtceu:hv_macerator
---

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


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>

## Quests

These quests cover this page. Click one to open it in the quest book.

* <QuestLink id="503B56BFD3E03728">An MV Workshop</QuestLink> (Tier 2 - MV)
