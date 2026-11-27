---
navigation:
  title: "Large Chemical Reactor"
  parent: gtmultis/gtmultis-index.md
  position: 11
  icon: gtceu:large_chemical_reactor
item_ids:
- gtceu:large_chemical_reactor
---

# Large Chemical Reactor

<Multiblock id="gtceu:large_chemical_reactor" />

**Unlocks:** HV

The **Large Chemical Reactor** (LCR) runs every Chemical Reactor recipe plus many recipes no single block can do.
It uses **perfect overclocks** (4x power, 4x speed), so it never wastes energy by running at a higher tier.

* A 3x3x3 of **Chemically Inert Machine Casings** around **PTFE Pipe Casings**, with exactly one
  **Cupronickel Coil Block** in the middle.
* Many recipes need several fluids and up to 6 outputs: give it enough hatches.
* Use [distinct buses](../gregtech/multiblock-basics.md) and programmed circuits to keep similar recipes apart.

Apex's pack setting makes multiblocks follow cleanroom rules: for cleanroom recipes, give it a **Cleaning
Maintenance Hatch** or build it inside a [Cleanroom](../gregtech/cleanroom.md).

The GTNA [Chemical Plant](chemical_plant.md) runs the same recipes with parallels.

## Controller recipe

<GTRecipesFor id="gtceu:large_chemical_reactor" limit="1" fallbackText="See JEI for the controller recipe." />


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
