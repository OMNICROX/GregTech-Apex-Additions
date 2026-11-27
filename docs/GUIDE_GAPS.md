# Apex Guide: gaps found against GT New Horizons' guide (GuideNH DefaultGuide 2.9)

Compared on 2026-11-27. "Filled" means a page now exists; "open" means it still needs writing.

## Filled in this pass

| Topic | Where | Source |
|---|---|---|
| Power: EU, volts, amps, tiers, generation, the network, cables, cable loss, transformers/buffers/diodes | `gregtech/power/` | GuideNH power pages, rewritten for GTCEu |
| Overclocking rules, subtick parallels, parallels vs batch mode, tier skipping, two-hatch rule | `gregtech/oc/` | GuideNH T_O_P pages, rewritten for GTCEu |
| Multiblock basics: controller, buses, hatches, energy, maintenance, muffler, wall sharing, troubleshooting | `gregtech/multiblock-basics.md` | GuideNH gtnh_basics/multiblocks |
| Single-block machines: facing, steam machines, electric machines | `gregtech/singleblocks.md` | GuideNH singleblock index |
| EBF build steps, two hatches, first aluminium | `gregtech/ebf-and-coils.md` | GuideNH EBF |
| Cleanroom rules, doors, hulls/diodes, multiblocks need cleaning hatches in Apex | `gregtech/cleanroom.md` | GuideNH cleanroom, GTCEu rules |
| Every GTCEu multiblock (coke oven to fusion), GCYM in one page, steam multis | `gtmultis/` | GuideNH multiblock pages + GTCEu code |
| Covers, pipes, chemical hazards (hazards are on in Apex) | `gregtech/` | new |
| All ~150 GTNA / GTCA / GTSE / GTMUtils multiblocks | `addons/` | generated from each mod's own tooltips |

## Not imported, and why

* GuideNH pages for machines Apex doesn't have: Beamline (LINAC, synchrotron, source/target chamber, beam
  crafter...), Bose-Einstein Condensate line, Algae Farm, Amazon Warehousing Depot, Boldarnator, Zyngen,
  Exo-Foundry, Stargate, Electric Air Filter, Advanced Coke Oven.
* "Transmission internal resistance": GT5u only; GTCEu has no such loss.
* AE2 (116 pages): written for 1.7.10 AE2; AE2 1.20 ships its own guide.
* Thaumcraft, Forestry, the changelog: not in Apex / not relevant. Blood Magic and Botania pages were empty stubs.

## Still open

1. **Add-on multiblock pages without a description** (68 of them, mostly GTCA and GTSE: their mods have no tooltip
   text). They show the structure, recipe type and controller recipe; a sentence or two each would help.
2. **Ore processing in depth**: byproducts per step, which machine for which ore, washing vs thermal centrifuge.
3. **Batteries and energy storage**: battery tiers, lapotronic orbs, which buffer to use when.
4. **Tools**: GregTech hand tools, electric tools, what each tool does to machines.
5. **Boilers and the steam age**: small boilers, water, steam math (the progression page covers it briefly).
6. **Programmed circuits** and recipe conflicts.
7. **Screenshots** still placeholders: steam setup, LV power, ore processing line, cleanroom, assembly line, AE2 subnet.
8. **Quest links**: GuideNH links pages to quests both ways; Apex Guide doesn't have quest tags yet.
