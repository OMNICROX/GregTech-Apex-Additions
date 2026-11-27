---
navigation:
  title: "Multiblock Basics"
  parent: gregtech/gregtech-index.md
  position: 31
  icon: gtceu:maintenance_hatch
---

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


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
