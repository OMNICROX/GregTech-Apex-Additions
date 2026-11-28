---
navigation:
  title: "Assembly Line"
  parent: gtmultis/gtmultis-index.md
  position: 20
  icon: gtceu:assembly_line
item_ids:
- gtceu:assembly_line
---

# Assembly Line

<Multiblock id="gtceu:assembly_line" />

**Unlocks:** IV

The **Assembly Line** makes the big components of the late game: LuV and higher motors, pumps, field generators,
fusion parts, high-tier circuits and machine hulls. Every recipe needs **research data** first.

## Research

* Early recipes are researched in a **Scanner**: put the item (shown in JEI) and an empty **Data Stick** in.
* Later recipes need a **Data Orb** or **Data Module** and the [Research Station](research.md).
* The finished data goes into a **Data Access Hatch** on the Assembly Line (or, for many recipes, a
  [Data Bank](research.md) connected with optical cables).

## Building it

* 5 to 16 slices long. Each slice holds one **Input Bus** at the bottom; the first slice holds the controller.
* **Item order matters in Apex**: the first ingredient goes in the first bus (next to the controller), the second in
  the second, and so on. **Fluids are ordered too**: put each fluid in the hatches in the order JEI lists them.
* Energy Hatches go on the top; the Output Bus is at the far end.
* Normal overclocks, no subtick parallels.

Use [distinct buses](../gregtech/multiblock-basics.md) only where needed; with ordered inputs the Assembly Line
already reads each bus by position. Many players feed each bus from its own ME Input Bus or interface.

## Controller recipe

<GTRecipesFor id="gtceu:assembly_line" limit="1" fallbackText="See JEI for the controller recipe." />


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>

## Quests

These quests cover this page. Click one to open it in the quest book.

* <QuestLink id="4C0004B8AC067C32">Assembly Line</QuestLink> (Tier 5 - IV)
* <QuestLink id="734D5E2C8E00EF74">LuV Components</QuestLink> (Tier 6 - LuV)
