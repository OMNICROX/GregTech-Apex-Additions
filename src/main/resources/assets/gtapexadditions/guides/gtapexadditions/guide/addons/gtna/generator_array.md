---
navigation:
  title: "Generator Array"
  parent: addons/gtna/gtna-index.md
  position: 20
  icon: gtna:generator_array
item_ids:
- gtna:generator_array
---

# Generator Array

<Multiblock id="gtna:generator_array" />

**From:** GTNA

**Runs:** Steam Turbine, Gas Turbine, Combustion Generator

## What it does

* Main Function: Runs matching GTCEu generators using their normal fuel recipes.
* Generator limit: 4 of the same type and tier, in the controller slot or input bus.
* Generation multiplier: 1.3× per installed generator; fuel use rises with output.
* Fuel enters through fluid hatches; energy exits through the dynamo hatch.
* Wireless mode: sends energy to the owner's Nexus Flux Matrix using its configured loss policy.
* Supported generators: Steam Turbine, Gas Turbine, Combustion Generator.

## Controller recipe

<GTRecipesFor id="gtna:generator_array" limit="1" fallbackText="See JEI for the controller recipe." />
