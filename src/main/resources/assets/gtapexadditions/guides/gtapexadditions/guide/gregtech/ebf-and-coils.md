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

<Multiblock id="gtceu:electric_blast_furnace" />

The <ItemLink id="gtceu:electric_blast_furnace" /> (EBF) is usually your first electric multiblock, built in LV.
Electricity and **heating coils** reach temperatures the Primitive Blast Furnace can't: your first big job is
**aluminium**, then stainless steel, titanium, tungstensteel and most hot metals of the game.

## Coils

The coils set the furnace's **heat**. A recipe only runs if the heat is at least the recipe's temperature (JEI
shows it under the recipe).

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

Bonuses:

* **+100 K** for every Energy Hatch tier above MV.
* Every **900 K** above the recipe's temperature: **5% less energy** (multiplied, before overclocking).
* Every **1,800 K** above: one overclock becomes **perfect** (4x power, 4x speed).

## Building it

3 x 3 x 4:

1. **Bottom layer**: the controller at the front middle, then the Energy Hatches, a Maintenance Hatch, at least one
   input (bus or hatch) and one output (bus or hatch). Fill the rest with
   <ItemLink id="gtceu:heatproof_machine_casing" />. The bottom middle block is part of the structure too.
2. **Two rings of 8 coils** above it, all the same tier, with **air** in the middle of both rings.
3. **Top layer**: casings around a <ItemLink id="gtceu:lv_muffler_hatch" /> in the middle, facing up into open air.
   Output Hatches may replace top casings for gas outputs.

The preview above can build it for you. Keep the two middle blocks empty: a torch or cable there breaks the
structure.

## Two Energy Hatches

An LV Energy Hatch takes 2 A at 32 V. With **two LV hatches** the EBF can run **MV recipes** (up to 120 EU/t,
since 4 A x 32 V = 128 EU/t). The cable still carries **LV**: give it more LV amps, never MV voltage. See
[Overclocking and Parallels](oc/oc-index.md).

Maintenance matters here: each maintenance problem slows the furnace and raises its energy use, so an early EBF on
two LV hatches can run short. Fix problems at the Maintenance Hatch.

## Your first aluminium

1. Look up the aluminium EBF recipes in JEI and pick one you can supply: items, circuit, gas, EU/t and heat.
2. Check the controller says the structure is formed with no maintenance problems, and the muffler is free.
3. Make room in the outputs.
4. Get power stable first: generators running, batteries charged, both hatches connected.
5. Put in one small batch and watch a full recipe finish before you automate it.

Metals hotter than **1,750 K** come out as **hot ingots**: cool them in a
[Vacuum Freezer](../gtmultis/vacuum_freezer.md).

<GTRecipesFor id="gtceu:electric_blast_furnace" limit="1" />


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>

## Quests

These quests cover this page. Click one to open it in the quest book.

* <QuestLink id="0116C52B2F76769A">Electric Blast Furnace</QuestLink> (Tier 1 - LV)
* <QuestLink id="074C2D87D3528D5C">Trinium Coils</QuestLink> (Tier 7 - ZPM)
* <QuestLink id="0F297ECA9F2BA990">Nichrome Coils</QuestLink> (Tier 4 - EV)
* <QuestLink id="156EC5914C56EBE7">Stainless Steel</QuestLink> (Tier 2 - MV)
* <QuestLink id="420224800D523C8A">HSS-G Coils</QuestLink> (Tier 5 - IV)
* <QuestLink id="53CA5ADEF4FFDC2D">Cupronickel Coils</QuestLink> (Tier 1 - LV)
* <QuestLink id="75C8E59308A65961">Naquadah Coils</QuestLink> (Tier 6 - LuV)
* <QuestLink id="7870AF3E3C00E3A2">Tritanium Coils</QuestLink> (Tier 8 - UV)
* <QuestLink id="79B95D2DDA485860">Kanthal Coils</QuestLink> (Tier 3 - HV)
* <QuestLink id="7C764C7102119213">RTM Alloy Coils</QuestLink> (Tier 5 - IV)
