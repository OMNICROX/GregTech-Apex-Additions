---
navigation:
  title: "Power"
  parent: gregtech/gregtech-index.md
  position: 15
  icon: gtceu:lv_battery_buffer_4x
---

# Power

GregTech machines run on **EU** (Energy Units). Generators push EU into cables, cables carry it to machines, and
machines spend it while running recipes. This section explains the rules of that network.

* [Voltage Tiers](voltage-tiers.md): the tier ladder from ULV to MAX.
* [Generation and Consumption](generation-and-consumption.md): how generators produce and machines use EU.
* [The Power Network](enet.md): how packets travel and how to power a machine safely and reliably.
* [Wires and Cables](cables.md) and [Cable Loss](cable-loss.md).
* [Transformers, Buffers and Diodes](distribution.md).
* For tier skipping, overclocking and parallels, see [Overclocking and Parallels](../oc/oc-index.md).

## Voltage, current and power

EU moves in **packets**.

* **Voltage** (V) is how much EU one packet carries: 1 V = 1 EU per packet. Voltage decides the
  [tier](voltage-tiers.md) of generators and machines.
* **Current** (A, amps) is how many packets move per tick: 1 A = 1 packet per tick. Packets are whole numbers, so
  the current in any one tick is a whole number; an average can be a fraction.
* **Power** is voltage times current, in **EU/t**. A machine using 64 EU/t at 32 V draws 2 A.

Energy is power times time: a recipe at 30 EU/t for 200 ticks costs 6,000 EU.

## Forge Energy

GregTech cables power **Forge Energy** machines directly (4 FE per EU), so other mods' FE blocks run off GT power.

## Generators by stage

* Steam: boilers and steam turbines. Early LV: <ItemLink id="gtceu:lv_steam_turbine" />,
  <ItemLink id="gtceu:lv_combustion" /> (oil and fuels), <ItemLink id="gtceu:lv_gas_turbine" />.
* Mid game: <ItemLink id="gtceu:large_combustion_engine" />, <ItemLink id="gtceu:steam_large_turbine" />,
  <ItemLink id="gtceu:gas_large_turbine" />.
* Late game: fusion with the <ItemLink id="gtceu:plasma_large_turbine" />, the
  [Large Naquadah Reactor](../../multiblocks/large_naquadah_reactor.md), the
  [Lightning Spire](../../multiblocks/lightning_spire.md), antimatter ([SLAM](../../multiblocks/antimatter_generator.md)).
* [Wireless energy](../wireless-energy.md) moves EU across the world without cables.


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
