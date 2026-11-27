---
navigation:
  title: "Industrial Flotation Cell"
  parent: addons/gtna/gtna-index.md
  position: 29
  icon: gtna:industrial_flotation_cell
item_ids:
- gtna:industrial_flotation_cell
---

# Industrial Flotation Cell

<Multiblock id="gtna:industrial_flotation_cell" />

**From:** GTNA

**Runs:** Flotating Beneficiation

## What it does

* Main Function: Froth-floats a MILLED ore with an ethylxanthate reagent and turpentine into an ore foam with a perfect overclock.
* Inputs: 32 ethylxanthate dust (sodium or potassium), 64 Milled ore and turpentine; output: 1000 mB of the matching ore foam.
* Milled ore comes from the ISA Mill; a Parallel Hatch multiplies the batch. Each recipe runs at its own Voltage tier.
* GTO's metal-compound-particle recipe is not ported (it needs GTO space-era materials), so only the twelve ore foams are available.

> The Isa U-276 ore separator is a mature purification machine. Pine oil flotation leaves the workshop smelling of essential balm. At least mosquitoes rarely bite anyone working here. Safety first; stay alert around the machinery!

## Controller recipe

<GTRecipesFor id="gtna:industrial_flotation_cell" limit="1" fallbackText="See JEI for the controller recipe." />
