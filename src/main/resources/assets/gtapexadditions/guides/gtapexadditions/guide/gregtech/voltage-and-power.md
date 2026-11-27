---
navigation:
  title: "Voltage, Amps and Power"
  parent: gregtech/gregtech-index.md
  position: 10
  icon: gtceu:lv_battery_hull
---

# Voltage, Amps and Power

GregTech power is **EU**. Every machine has a **voltage tier**; a machine runs recipes up to its own tier.

| Tier | Voltage | Recipe power (VA) | Typical cable |
|---|---|---|---|
| ULV | 8 | 7 | Lead |
| LV | 32 | 30 | Tin |
| MV | 128 | 120 | Copper |
| HV | 512 | 480 | Gold |
| EV | 2,048 | 1,920 | Aluminium |
| IV | 8,192 | 7,680 | Platinum / Tungsten |
| LuV | 32,768 | 30,720 | Niobium-Titanium |
| ZPM | 131,072 | 122,880 | Vanadium-Gallium |
| UV | 524,288 | 491,520 | Yttrium Barium Cuprate |
| UHV | 2,097,152 | 1,966,080 | Europium |

Higher tiers (UEV, UIV, UXV, OpV, MAX) keep multiplying by 4.

## The rules

* **Too much voltage explodes machines.** Feeding a higher voltage than a machine's tier blows it up. Use a
  transformer between tiers.
* **Amps** are how many "packets" a cable carries. A cable carries a fixed amperage; machines only draw what they need.
* **Cable loss**: every block of cable loses a little EU per amp. Superconductors have no loss.
* **Batteries** in a battery buffer smooth out power.
* **FE machines**: GregTech cables power Forge Energy machines directly (4 FE per EU), so other mods' FE blocks run
  off GT power.

## Generators by stage

* Steam: boilers + steam turbines. Early LV: <ItemLink id="gtceu:lv_steam_turbine" />,
  <ItemLink id="gtceu:lv_combustion" /> (oil/fuels), <ItemLink id="gtceu:lv_gas_turbine" />.
* Mid game: <ItemLink id="gtceu:large_combustion_engine" />, <ItemLink id="gtceu:steam_large_turbine" />,
  <ItemLink id="gtceu:gas_large_turbine" />.
* Late game: fusion + <ItemLink id="gtceu:plasma_large_turbine" />, the
  [Large Naquadah Reactor](../multiblocks/large_naquadah_reactor.md), the
  [Lightning Spire](../multiblocks/lightning_spire.md), antimatter ([SLAM](../multiblocks/antimatter_generator.md)).
* [Wireless energy](wireless-energy.md) moves EU across the world without cables.
