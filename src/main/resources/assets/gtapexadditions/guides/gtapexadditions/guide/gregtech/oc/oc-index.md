---
navigation:
  title: "Overclocking and Parallels"
  parent: gregtech/gregtech-index.md
  position: 21
  icon: gtceu:mv_macerator
---

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


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
