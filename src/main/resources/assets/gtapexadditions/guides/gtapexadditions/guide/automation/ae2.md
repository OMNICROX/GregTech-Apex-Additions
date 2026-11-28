---
navigation:
  title: "AE2 in Apex"
  parent: automation/automation-index.md
  position: 10
  icon: ae2:controller
---

# AE2 in Apex

AE2's own guide (hold G over any AE2 item) explains how AE2 works. In Apex its crafting recipes move into the
GregTech assembler at a tier (same ingredients + soldering alloy):

| Tier | AE2 |
|---|---|
| LV | Network basics, 1k storage, buses, terminals. **No autocrafting.** |
| MV | 4k / 16k cells, autocrafting (crafting CPU, pattern provider, molecular assembler, interface), P2P |
| HV | Everything else (the default) |
| EV | Quantum link / ring, spatial, big crafting accelerators, assembler matrix |
| IV | AdvancedAE quantum computer and quantum armor, very large cells |
| ZPM | MEGA Cells (1M-16M, MEGA crafting CPU, MEGA interface / pattern provider) |
| UV | MEGA 64M / 256M; InsaneAE basics |
| UHV-UIV | InsaneAE T / P / E cells, quantum CPU, top energy cells |

InsaneAE's machine acceleration cards are disabled. GregTech machines connect to AE2 with ME input / output buses
and hatches; the [Extreme Craft Center](../multiblocks/extreme_craft_center.md) is an ME crafting provider with pattern magnification.

![An ME drive subnet: a separate cable line with its own drives, linked to the main network](../assets/images/ae2_subnet.png)
