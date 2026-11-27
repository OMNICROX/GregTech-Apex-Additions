---
navigation:
  title: "Cold Ice Freezer"
  parent: addons/gtna/gtna-index.md
  position: 6
  icon: gtna:cold_ice_freezer
item_ids:
- gtna:cold_ice_freezer
---

# Cold Ice Freezer

<Multiblock id="gtna:cold_ice_freezer" />

**From:** GTNA

**Runs:** Vacuum Freezer, Atomization Condensation

## What it does

* Main Function: Runs Vacuum Freezer recipes in up to 64 parallel at half duration.
* Consumes liquid Ice when a recipe starts and every second while working.
* Liquid Ice upkeep is 2^(voltage tier - 2) × 10 mB per payment.
* The base structure only runs Vacuum Freezer recipes; the Naquadah Alloy auxiliary tower unlocks the Atomization Condensation recipes plus one Accelerate Hatch and up to six extra Energy Hatches.

> A liquid ice leak led to the discovery of this cryogenic machine. The ice reacted with the aluminium frame to create a stable superconducting space. Tungsten steel pipes circulate the cold in a vortex and freeze 64 samples at once. The employee behind the accident vanished, but the chairman treasures the machine.

## Controller recipe

<GTRecipesFor id="gtna:cold_ice_freezer" limit="1" fallbackText="See JEI for the controller recipe." />
