---
navigation:
  title: "Glass Tiers"
  parent: gregtech/gregtech-index.md
  position: 60
  icon: gtapexadditions:borosilicate_glass
item_ids:
- gtapexadditions:borosilicate_glass
- gtapexadditions:titanium_reinforced_borosilicate_glass
---

# Glass Tiers

As in GT New Horizons, glass is part of a multiblock's tier. A multi's glass spots take **any tiered glass**; the
**lowest** glass in the structure is its glass tier, and an **energy hatch above that tier stops the multi**. The
controller shows *Glass tier* and a red warning when the hatch is too high.

| Glass | Tier | Made at |
|---|---|---|
| <ItemLink id="gtapexadditions:borosilicate_glass" /> | HV | MV fluid solidifier |
| <ItemLink id="gtapexadditions:titanium_reinforced_borosilicate_glass" /> | EV | HV |
| <ItemLink id="gtapexadditions:tungstensteel_reinforced_borosilicate_glass" /> | IV | EV |
| <ItemLink id="gtapexadditions:rhodium_plated_palladium_reinforced_borosilicate_glass" /> | LuV | IV |
| <ItemLink id="gtapexadditions:iridium_reinforced_borosilicate_glass" /> | ZPM | LuV |
| <ItemLink id="gtapexadditions:osmium_reinforced_borosilicate_glass" /> | UV | ZPM |
| <ItemLink id="gtapexadditions:neutronium_reinforced_borosilicate_glass" /> | UHV | UV |
| <ItemLink id="gtapexadditions:infinity_reinforced_borosilicate_glass" /> | UEV | UHV |
| <ItemLink id="gtapexadditions:transcendentally_reinforced_borosilicate_glass" /> | UIV | UEV |
| <ItemLink id="gtapexadditions:hexanite_reinforced_borosilicate_glass" /> | UMV | UIV |
| <ItemLink id="gtapexadditions:universium_reinforced_borosilicate_glass" /> | UXV | UMV |

GregTech's own glass counts too: tempered glass = HV, laminated glass = IV, fusion glass = LuV.

Each tier is a borosilicate block solidified with 8 ingots of molten metal:

<GTRecipesFor id="gtapexadditions:titanium_reinforced_borosilicate_glass" limit="1" />

Upgrading: the [Structure Upgrader](../items/structure-upgrader.md) swaps all glass in a multi to a better tier.
(Server owners can turn the energy rule off: `glassTierLimitsEnergyHatches` in gtapexadditions-common.toml.)

## Quests

These quests cover this page. Click one to open it in the quest book.

* <QuestLink id="00EDB1B21E0B53D7">Glass for Multiblocks</QuestLink> (Tier 3 - HV)
* <QuestLink id="043C530924BFAB3C">Tungstensteel Glass</QuestLink> (Tier 5 - IV)
* <QuestLink id="13274D8B0F871BFF">Titanium Glass</QuestLink> (Tier 4 - EV)
* <QuestLink id="2B53170302E75951">Osmium Glass</QuestLink> (Tier 8 - UV)
* <QuestLink id="2D2CBD1DD32375D3">Iridium Glass</QuestLink> (Tier 7 - ZPM)
* <QuestLink id="5B6B4677EF343328">Rhodium-Plated Palladium Glass</QuestLink> (Tier 6 - LuV)
* <QuestLink id="72C1359FFC3BF38F">Neutronium Glass</QuestLink> (Tier 9 - UHV)
