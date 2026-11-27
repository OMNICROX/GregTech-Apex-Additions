---
navigation:
  title: "Electric Blast Furnace and Coils"
  parent: gregtech/gregtech-index.md
  position: 50
  icon: gtceu:electric_blast_furnace
item_ids:
- gtceu:electric_blast_furnace
---

# Electric Blast Furnace and Coils

The <ItemLink id="gtceu:electric_blast_furnace" /> smelts metals that need heat. Its **coils** set the temperature.

| Coil | Heat | Unlocks |
|---|---|---|
| <ItemLink id="gtceu:cupronickel_coil_block" /> | 1,800 K | Aluminium, steel in bulk, silicon |
| <ItemLink id="gtceu:kanthal_coil_block" /> | 2,700 K | Stainless steel, titanium |
| <ItemLink id="gtceu:nichrome_coil_block" /> | 3,600 K | Tungstensteel, tungsten |
| <ItemLink id="gtceu:rtm_alloy_coil_block" /> | 4,500 K | HSS-G and higher |
| <ItemLink id="gtceu:hssg_coil_block" /> | 5,400 K | Naquadah, iridium |
| <ItemLink id="gtceu:naquadah_coil_block" /> | 7,200 K | Naquadah alloy, trinium |
| <ItemLink id="gtceu:trinium_coil_block" /> | 9,001 K | Late UV metals |
| <ItemLink id="gtceu:tritanium_coil_block" /> | 10,800 K | UHV metals |

* Every 900 K above a recipe's heat: 5% less energy. Every 1,800 K above: one **perfect** overclock.
* Use the energy hatch tier of the voltage you want to run; two hatches = 2 amps.

## Building it

<Multiblock id="gtceu:electric_blast_furnace" />

Bottom layer: casings with the controller, an input bus, an output bus, energy hatches and a maintenance hatch.
Two layers of coils (hollow middle), then a casing top with a **muffler hatch** in the middle facing up.

<GTRecipesFor id="gtceu:electric_blast_furnace" limit="1" />
