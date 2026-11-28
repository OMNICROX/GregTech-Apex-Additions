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
## Filled 2026-11-27

* Ore processing in depth (`gregtech/ore-processing.md`): every step with its byproduct slot, chemical bath,
  sifter, electromagnetic separator, byproduct table for 24 ores. From GTCEu's OreRecipeHandler and ore properties.
* Boilers and steam (`gregtech/boilers-and-steam.md`): boiler outputs, heat-up, explosions, steam per EU,
  turbine math.
* Batteries and storage (`gregtech/power/batteries.md`): every battery's capacity, buffers, chargers, the PSS.
* Tools (`gregtech/tools.md`): what each tool does to machines, pipes and maintenance; crafting and electric tools.
* Programmed circuits (`gregtech/programmed-circuits.md`): the ghost slot, distinct buses, conflicts, AE2.
* Screenshots: all six are real now (the AE2 subnet one is from the owner's world).
* Quest links: quests open guide pages (a gold "Read more in the Apex Guide" line, run through `/apexpage`), and guide
  pages list their quests (`<QuestLink>`; the list is generated from `tools/guide/quest_links.json`).
