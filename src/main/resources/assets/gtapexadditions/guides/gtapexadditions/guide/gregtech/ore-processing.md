---
navigation:
  title: "Ore Processing"
  parent: gregtech/gregtech-index.md
  position: 40
  icon: gtceu:lv_macerator
item_ids:
- gtceu:lv_ore_washer
- gtceu:lv_thermal_centrifuge
- gtceu:lv_chemical_bath
- gtceu:lv_sifter
- gtceu:lv_electromagnetic_separator
---

# Ore Processing

Smelting raw ore gives one ingot. Running it through GregTech machines gives at least **two**, plus
**byproducts**. Many materials only come from byproducts, among them gallium, cadmium, cobalt, palladium,
platinum and the rare earths.

The exact chain for any ore is in JEI: look up the ore and open the **Ore Processing Diagram** tab. It draws every
route below with that ore's real byproducts.

![An early ore line, left to right: chest, macerator, ore washer (water pipe at the back), thermal centrifuge, centrifuge, chest](../assets/images/ore_processing_line.png)

## The steps

| Step | Machine | In | Out |
|---|---|---|---|
| 1. Crush | <ItemLink id="gtceu:lv_macerator" /> | raw ore | **2** crushed ore, 14% byproduct 1 |
| 1. Crush (cheap) | <ItemLink id="gtceu:lv_forge_hammer" /> | raw ore | 1 crushed ore |
| 2. Wash | <ItemLink id="gtceu:lv_ore_washer" /> | crushed ore + 1,000 mB water | purified ore, 1/3 byproduct 1, stone dust |
| 2. Bathe | <ItemLink id="gtceu:lv_chemical_bath" /> | crushed ore + mercury or sodium persulfate | purified ore, **70%** byproduct 4 |
| 3. Spin | <ItemLink id="gtceu:lv_thermal_centrifuge" /> | purified (or crushed) ore | refined ore, 1/3 byproduct 2, stone dust |
| 4. Grind | <ItemLink id="gtceu:lv_macerator" /> | refined ore | dust, 14% byproduct 3 |

Every stage can also be ground or smelted on its own, so you can stop anywhere:

* **Crushed ore** ground -> impure dust (14% byproduct 1). Centrifuge impure dust for a 1/9 byproduct 1, or wash it.
* **Purified ore** ground -> purified dust (14% byproduct 2). Centrifuge it for a 1/9 byproduct 2.
* Crushed, purified and refined ore all smelt straight to an ingot if the metal doesn't need a blast furnace.

The ore washer also has a quick recipe with only 100 mB water: no byproduct, but it doesn't drain your water.
Distilled water (100 mB) gives the byproduct.

## Gems and magnetic ores

* <ItemLink id="gtceu:lv_sifter" />: purified gem ores (diamond, emerald, ruby, sapphire...) sift into exquisite,
  flawless, normal and flawed gems plus dust.
* <ItemLink id="gtceu:lv_electromagnetic_separator" />: purified dust of some ores splits off a magnetic metal. Iron
  from nickel, tin, chromite, pyrite, pentlandite and ilmenite; gold from magnetite; neodymium from monazite and
  bastnasite.

## Which route?

* **Early LV**: macerate, then wash, then smelt or grind. That already doubles everything.
* **When you need a specific byproduct**: find which step gives it in the diagram. Byproduct 2 only comes from
  the thermal centrifuge (or purified dust in a centrifuge). Byproduct 4 only comes from the chemical bath.
* **The chemical bath** has the best byproduct chance (70%). Mercury baths give gold from copper and chalcopyrite,
  palladium from sheldonite and platinum from nickel. Sodium persulfate gives gold from iron.

## Byproducts of common ores

1 = macerator / ore washer, 2 = thermal centrifuge, 3 = grinding refined ore, 4 = chemical bath.

| Ore | 1 | 2 | 3 | 4 (bath) |
|---|---|---|---|---|
| Iron | Nickel | Tin | Tin | Gold (sodium persulfate) |
| Copper | Cobalt | Gold | Nickel | Gold (mercury) |
| Tin | Iron | Zinc | Zinc | Zinc (sodium persulfate) |
| Gold | Copper | Nickel | Silver | Silver (mercury) |
| Silver | Lead | Sulfur | Sulfur | Gold (mercury) |
| Nickel | Cobalt | Iron | Platinum | Platinum (mercury) |
| Galena | Sulfur | Silver | Silver | Silver (mercury) |
| Cassiterite | Tin | Bismuth | Bismuth | - |
| Chalcopyrite | Pyrite | Cobalt | Cadmium | Gold (mercury) |
| Sphalerite | Yellow Garnet | **Gallium** | Cadmium | Zinc (sodium persulfate) |
| Tetrahedrite | Antimony | Zinc | Cadmium | Cadmium (sodium persulfate) |
| Pentlandite | Iron | Sulfur | Cobalt | Cobalt (sodium persulfate) |
| Bauxite | Grossular | Rutile | **Gallium** | Gallium (sodium persulfate) |
| Ilmenite | Iron | Rutile | Rutile | Ilmenite Slag (sodium persulfate) |
| Magnetite | Iron | Gold | Gold | Gold (mercury) |
| Sheldonite (Cooperite) | Nickel | Nickel | Cobalt | **Palladium** (mercury) |
| Tungstate | Manganese | Silver | Lithium | Lithium (mercury) |
| Scheelite | Manganese | Molybdenum | Calcium | - |
| Tantalite | Manganese | Niobium | Tantalum | - |
| Lepidolite | Lithium | Caesium | Boron | - |
| Monazite | Thorium | Neodymium | Rare Earth | - |
| Bastnasite | Neodymium | Rare Earth | Rare Earth | - |
| Pitchblende | Thorium | Uraninite | Lead | - |
| Redstone | Cinnabar | Rare Earth | Glowstone | - |

Some ores give more than two crushed ores per raw ore (redstone, for example, gives three); the diagram shows it.

## Later on

* Multiblock versions (the GCYM large macerator, large chemical bath and large centrifuge) run the same recipes
  with parallels; see [GCYM Large Machines](../gtmultis/gcym.md).
* The [General Ore Processing Factory](../multiblocks/ore_processing_factory.md) (LuV) runs the whole chain in
  one block.

## Quests

These quests cover this page. Click one to open it in the quest book.

* <QuestLink id="0782B27FB9EF0B39">Electromagnetic Separator</QuestLink> (Tier 2 - MV)
* <QuestLink id="0F623F3849B53B67">Macerator</QuestLink> (Tier 1 - LV)
* <QuestLink id="2C9EE65E5132DFC7">Doubling Your Ore</QuestLink> (Tier 0.5 - Steam Age)
* <QuestLink id="5B9E1F5CF58BE9A9">Byproducts Matter</QuestLink> (Tier 0.5 - Steam Age)
* <QuestLink id="5CEE957BC41498DB">Ore Washer</QuestLink> (Tier 1 - LV)
* <QuestLink id="609CAAA7587AC5E1">Aether Bauxite</QuestLink> (The Aether)
* <QuestLink id="690D41F27017971D">Centrifuge</QuestLink> (Tier 1 - LV)
* <QuestLink id="703D7171C3817BE3">Sifter</QuestLink> (Tier 2 - MV)
* <QuestLink id="7573CC4AB7CFE3D4">Chemical Bath</QuestLink> (Tier 1 - LV)
* <QuestLink id="7990B3AA204C961A">Thermal Centrifuge</QuestLink> (Tier 1 - LV)
* <QuestLink id="7F371F6AF478FCD8">First Platinum</QuestLink> (Tier 4 - EV)
