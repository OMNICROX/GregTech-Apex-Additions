---
navigation:
  title: "Batteries and Energy Storage"
  parent: gregtech/power/power-index.md
  position: 7
  icon: gtceu:lv_lithium_battery
item_ids:
- gtceu:lv_battery_buffer_4x
- gtceu:lv_charger_4x
- gtceu:lapotronic_energy_orb
---

# Batteries and Energy Storage

Batteries store EU. They go in a battery buffer to store power for a network, in a machine's charge slot to keep it
running, or in an electric tool. A battery charges and discharges at its own tier's voltage.

## Battery tiers

| Tier | Battery | Capacity (EU) |
|---|---|---|
| ULV | <ItemLink id="gtceu:tantalum_capacitor" /> | 1,000 |
| LV | <ItemLink id="gtceu:lv_sodium_battery" /> / <ItemLink id="gtceu:lv_cadmium_battery" /> / <ItemLink id="gtceu:lv_lithium_battery" /> | 80k / 100k / 120k |
| MV | <ItemLink id="gtceu:mv_sodium_battery" /> / <ItemLink id="gtceu:mv_cadmium_battery" /> / <ItemLink id="gtceu:mv_lithium_battery" /> | 360k / 400k / 420k |
| HV | <ItemLink id="gtceu:hv_sodium_battery" /> / <ItemLink id="gtceu:hv_cadmium_battery" /> / <ItemLink id="gtceu:hv_lithium_battery" /> | 1.2M / 1.6M / 1.8M |
| HV | <ItemLink id="gtceu:energy_crystal" /> | 6.4M |
| EV | <ItemLink id="gtceu:ev_vanadium_battery" /> | 10.24M |
| EV | <ItemLink id="gtceu:lapotron_crystal" /> | 25M |
| IV | <ItemLink id="gtceu:iv_vanadium_battery" /> | 40.96M |
| IV | <ItemLink id="gtceu:lapotronic_energy_orb" /> | 250M |
| LuV | <ItemLink id="gtceu:luv_vanadium_battery" /> | 163.84M |
| LuV | <ItemLink id="gtceu:lapotronic_energy_orb_cluster" /> | 1G |
| ZPM | <ItemLink id="gtceu:zpm_naquadria_battery" /> | 655.36M |
| ZPM | <ItemLink id="gtceu:energy_module" /> | 4G |
| UV | <ItemLink id="gtceu:uv_naquadria_battery" /> | 2.62G |
| UV | <ItemLink id="gtceu:energy_cluster" /> | 20G |
| UHV | <ItemLink id="gtceu:max_battery" /> | practically unlimited |

* Sodium is the cheapest, lithium holds the most; cadmium sits between.
* The crystal / orb line holds far more than the plain battery of the same tier, so switch to it as soon as you can
  make it.
* The <ItemLink id="gtceu:zero_point_module" /> (ZPM, 2T EU) is a one-use battery: it can't be recharged.

## Battery buffers

A <ItemLink id="gtceu:lv_battery_buffer_4x" /> comes with 4, 8 or 16 slots at every tier.

* Power **out of the front**, power **in on the other five sides**.
* Each battery adds **2 A in** and **1 A out**. A 4-slot buffer full of batteries takes up to 8 A and gives up to 4 A.
* It charges the batteries inside it from the network, and drains them back into the network when generation drops.
* Use batteries of the buffer's tier. Because the buffer outputs fewer amps than it takes in, it also caps how
  much a line can draw.

![LV power: two steam turbines and a battery buffer feeding four machines over one tin cable](../assets/images/lv_power.png)

## Chargers

A <ItemLink id="gtceu:lv_charger_4x" /> charges batteries and electric tools quickly. Electric machines also charge
a tool or battery placed in their charge slot.

## Big storage

The <ItemLink id="gtceu:power_substation" /> (EV) is the end-game bank. It holds up to 18 layers of capacitor blocks
(<ItemLink id="gtceu:ev_lapotronic_battery" /> up to <ItemLink id="gtceu:uhv_ultimate_battery" />; empty capacitors
fill the rest) and moves power in and out through energy and dynamo hatches. It loses 1% of what it holds per
real-time day. See [Power Substation](../../gtmultis/power_substation.md).

## Which storage when

| Stage | Use |
|---|---|
| Steam / early LV | An LV battery buffer with sodium or lithium batteries |
| MV-HV | Buffers of your main tier; energy crystals at HV |
| EV-IV | Lapotron crystals and lapotronic orbs in buffers; plan the Power Substation |
| LuV+ | Power Substation with lapotronic capacitors |

## Quests

These quests cover this page. Click one to open it in the quest book.

* <QuestLink id="1C9282C5C8C062F7">Battery Buffer</QuestLink> (Tier 1 - LV)
* <QuestLink id="1D00CD3B85A58385">Batteries</QuestLink> (Tier 1 - LV)
* <QuestLink id="21E790755EB2D8AE">HV Batteries</QuestLink> (Tier 3 - HV)
* <QuestLink id="340F3224E6F0203F">Lapotron Crystal</QuestLink> (Tier 4 - EV)
* <QuestLink id="4DAE8F9266318AAE">MV Batteries</QuestLink> (Tier 2 - MV)
