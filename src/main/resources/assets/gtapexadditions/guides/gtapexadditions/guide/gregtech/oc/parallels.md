---
navigation:
  title: "Parallels"
  parent: gregtech/oc/oc-index.md
  position: 2
  icon: gtceu:iv_parallel_hatch
---

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


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
