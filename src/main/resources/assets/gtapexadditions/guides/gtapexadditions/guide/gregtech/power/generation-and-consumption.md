---
navigation:
  title: "Generation and Consumption"
  parent: gregtech/power/power-index.md
  position: 2
  icon: gtceu:lv_steam_turbine
---

# Generation and Consumption

## Generators

* **Single-block generators** (steam turbines, combustion and gas generators...) fill an internal EU buffer and push
  **1 A at their tier's voltage**. They only burn fuel while the buffer has room, so you can stock them up without
  waste.
* **Multiblock generators** (Large Steam Turbine, Large Combustion Engine, naquadah reactors...) output through
  **Dynamo Hatches**. Their tier and amperage limit what the multi can push.

## Machines

<Color id="red">Feeding a machine packets above its tier's voltage makes it explode.</Color>

* A machine accepts packets up to its tier's voltage and can only run recipes whose EU/t fits within that tier.
* **Single-block machines** take as many amps as the running recipe needs (normally 1 A).
* **Multiblocks** take power through **Energy Hatches**: a normal hatch takes up to **2 A**; the 4 A and 16 A
  hatches, the 64 A substation hatches and laser hatches take more, if the multi allows them.

## Running out of power

When a machine can't get the EU it needs for a tick, the recipe stalls and its progress resets to the start. The
inputs were taken when the recipe started, so they are not lost: once power returns, the recipe starts over from
zero. A **multiblock** that keeps failing for 5 attempts suspends itself until you restart it (a Machine Controller
cover set to "prevent power fail" stops that).


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
