---
navigation:
  title: "Vacuum Drying Furnace"
  parent: addons/gtna/gtna-index.md
  position: 80
  icon: gtna:vacuum_drying_furnace
item_ids:
- gtna:vacuum_drying_furnace
---

# Vacuum Drying Furnace

<Multiblock id="gtna:vacuum_drying_furnace" />

**From:** GTNA

**Runs:** Vacuum Drying, Dehydrator

## What it does

* Main Function: Dries flotation ore foams back into GTCEu dusts, Red Mud and Water, or runs the Dehydrator family.
* Vacuum Drying mode: 4000 mB ore foam → six dust stacks + 200 mB Red Mud + 2000 mB Water. Coil temperature must reach the recipe temperature.
* Dehydrator mode: every 900K of coil temperature doubles the parallel count (2^(temperature / 900)); the Vacuum Drying mode stays serial.
* Red Mud is neutralised with hydrochloric acid in a Mixer. GTO's Mega Vacuum Drying Furnace and its trinium-compound recipe are not ported.

## Controller recipe

<GTRecipesFor id="gtna:vacuum_drying_furnace" limit="1" fallbackText="See JEI for the controller recipe." />
