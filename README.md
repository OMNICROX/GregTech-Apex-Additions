# GregTech Apex Additions

GTNH-style late-game multiblocks for **GregTech CEu Modern** (Minecraft 1.20.1, Forge).
Made for the [Apex Ascension](https://www.curseforge.com/minecraft/modpacks/apex-ascension) modpack, but every
machine is a real Java multiblock, so any GTCEu pack can use it. Recipes are normal GT recipes, so packs can change
them with KubeJS.

## Machines

| Machine | Tier | Status |
| --- | --- | --- |
| Fusion Reactor Computer MK IV | UHV | ✅ in 0.1.0 |
| Fusion Reactor Computer MK V | UEV | ✅ in 0.1.0 |
| Compact Fusion Computer MK I-V | LuV-UEV | planned |
| Semi-Stable Antimatter Stabilization Sequencer (SASS) | UEV | ✅ in 0.2.0 |
| Shielded Lagrangian Annihilation Matrix (SLAM) | UEV | ✅ in 0.2.0 |
| Draconic Fusion Crafter | HV-UV | planned (needs Draconic Evolution) |
| Mystical Cultivation Array | LV-LuV | planned (needs Mystical Agriculture) |
| Space Elevator (Motor MK-I to V, extendable to 24 modules) | UHV | ✅ in 0.3.0 |
| Space Assembler Module Mk-I / II / III | UHV / UIV / UXV | ✅ in 0.3.0 |
| Space Mining / Space Pumping Modules Mk-I to III | UV-UXV | planned |

Fusion MK IV/V use the same ring as GTCEu's MK I-III, with GTNH's energy buffers
(320M / 1.28B EU per energy hatch) and perfect overclocks.

## Requirements
- Minecraft 1.20.1, Forge 47.4+
- GregTech CEu Modern 7.5.3+

## Building
```
./gradlew build
```
Needs JDK 17. The jar ends up in `build/libs`.

## License
LGPL-3.0-or-later. Ported GTNH content keeps its original credits; see [CREDITS.md](CREDITS.md).
