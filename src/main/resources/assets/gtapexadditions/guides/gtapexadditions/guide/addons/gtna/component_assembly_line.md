---
navigation:
  title: "Component Assembly Line"
  parent: addons/gtna/gtna-index.md
  position: 9
  icon: gtna:component_assembly_line
item_ids:
- gtna:component_assembly_line
---

# Component Assembly Line

<Multiblock id="gtna:component_assembly_line" />

**From:** GTNA

**Runs:** Component Assembly

## What it does

* Main Function: Runs the component assembly batches as a tier-cased line with a Parallel Hatch.
* Every component assembly line casing must share one tier; a batch cannot start above that tier.
* The line handles the LV–IV batches plus the eight LuV, ZPM and UV batches of the Component Assembler extension.
* GTNA ports the LV–UV casing tiers; UHV and above are documented as out of scope. GTO's cross-recipe threads are not ported.

## Controller recipe

<GTRecipesFor id="gtna:component_assembly_line" limit="1" fallbackText="See JEI for the controller recipe." />
