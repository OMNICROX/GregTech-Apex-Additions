---
navigation:
  title: "Research and Data"
  parent: gtmultis/gtmultis-index.md
  position: 21
  icon: gtceu:research_station
item_ids:
- gtceu:research_station
- gtceu:high_performance_computation_array
- gtceu:data_bank
---

# Research and Data

<Multiblock id="gtceu:research_station" />

**Unlocks:** LuV

Late Assembly Line recipes need research done in the **Research Station**, which needs **computation** (CWU/t)
from a **High Performance Computation Array**.

## Research Station

<ItemLink id="gtceu:research_station" /> scans an item onto a **Data Orb** or **Data Module**. It needs a steady
supply of computation through an **Optical Data Reception Hatch**; more computation finishes the research faster.

## High Performance Computation Array (HPCA)

<Multiblock id="gtceu:high_performance_computation_array" />

The <ItemLink id="gtceu:high_performance_computation_array" /> produces computation. Fill its 3x3 component grid:

* **Computation components** produce CWU/t (and heat).
* **Cooling components**: heat sinks are passive; active coolers need **PCB Coolant** through an Input Hatch.
* **Bridges** let several HPCAs pass computation through one another.

If the array gets too hot, components take damage. Send the computation out through an **Optical Data
Transmission Hatch** and optical cables to the Research Station.

## Data Bank

<Multiblock id="gtceu:data_bank" />

The <ItemLink id="gtceu:data_bank" /> stores research data orbs and modules in **Data Access Hatches** and shares
them with many Assembly Lines over optical cables. Data Banks can be chained. It uses a small amount of power per
hatch.

## Controller recipe

<GTRecipesFor id="gtceu:research_station" limit="1" fallbackText="See JEI for the controller recipe." />


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
