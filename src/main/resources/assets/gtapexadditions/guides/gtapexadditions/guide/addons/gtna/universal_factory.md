---
navigation:
  title: "Universal Factory"
  parent: addons/gtna/gtna-index.md
  position: 79
  icon: gtna:universal_factory
item_ids:
- gtna:universal_factory
---

# Universal Factory

<Multiblock id="gtna:universal_factory" />

**From:** GTNA

**Runs:** Bender, Compressor, Forge Hammer, Cutter, Extruder, Lathe, Wiremill, Forming Press, Polarizer, Laser Engraver, Fluid Solidifier, Assembler, Arc Furnace, Circuit Assembler, Canner, Centrifuge, Thermal Centrifuge, Electrolyzer, Sifter, Macerator, Extractor, Chemical Reactor, Mixer, Chemical Bath, Ore Washer, Large Chemical Reactor, Packer, Distillery, Autoclave, Fluid Heater, Brewery, Fermenter

## What it does

* Main Function: Processes many different recipes at once with cross-recipe parallelism and threads.
* Threads and parallel follow the selected balance mode and operating voltage tier.
* Legacy mode supports thermal warmup and batch; shared budget divides capacity among threads.
* The machine UI shows effective capacity. Special hatches require enabling them in balance config.

## Controller recipe

<GTRecipesFor id="gtna:universal_factory" limit="1" fallbackText="See JEI for the controller recipe." />
