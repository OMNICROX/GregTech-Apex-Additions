# GregTech Apex Additions

GTNH-style late-game multiblocks, systems and progression for **GregTech CEu Modern** (Minecraft 1.20.1, Forge).
Made for the [Apex Ascension](https://www.curseforge.com/minecraft/modpacks/apex-ascension) modpack, but every
machine is a real Java multiblock, so any GTCEu pack can use it. Recipes are normal GT recipes, so packs can change
them with KubeJS. Machines that need another mod (Draconic Evolution, Mystical Agriculture, FSang's Heroes, AE2,
the Aether) only get their recipes when that mod is installed.

## Multiblocks

| Machine | Tier | Notes |
| --- | --- | --- |
| Fusion Reactor Computer MK IV / MK V | UHV / UEV | GTCEu's fusion ring with GTNH energy buffers (320M / 1.28B EU per hatch) and perfect overclocks |
| Compact Fusion Computer MK I-V | LuV-UEV | GoodGenerator's compact fusion, one controller per tier |
| Large Naquadah Reactor | IV+ | Burns the naquadah, uranium, thorium and plutonium liquid fuels |
| Naquadah Fuel Refinery | UV | Makes Naquadah Based Liquid Fuel MkI-VI |
| Semi-Stable Antimatter Stabilization Sequencer (SASS) | UEV | Antimatter Forge with GoodGenerator's animated antimatter core render |
| Shielded Lagrangian Annihilation Matrix (SLAM) | UEV | Antimatter generator: antimatter + one matter (Molten Copper, Superconductor Base UIV / UMV) in its 2 fluid hatches, burst every 5 s; screwdriver for wireless mode (GTM Things, UMV base, capped by hatch capacity, ignores the wireless rate limit); full burst readout in the UI. GTAA makes the UIV / UMV base fluids in the EBF |
| Space Elevator | UHV | Motors MK-I to V, extendable to 24 modules, cable and climber render |
| Space Assembler Module Mk-I / II / III | UHV / UIV / UXV | Space Elevator module |
| Space Mining Module Mk-I / II / III | UV-UXV | Space Elevator module |
| Space Pumping Module Mk-I / II / III | UV-UXV | Space Elevator module |
| Resource Collection Module | UIV | Space Elevator module |
| Space Drill MK-123 | UXV | Space Elevator module |
| Dyson Sphere Launch Site | UIV | Launches Dyson Swarm Modules |
| Component Assembly Line | IV+ | Tiered casings, makes GT components in bulk |
| Quantum Force Transformer | UHV | Tiered manipulators and shielding |
| Water Purification Plant + 8 units | LuV+ | Clarifier, Ozonation, Flocculation, pH Neutralization, Plasma Heater, UV Treatment, Degasser, Baryonic Perfection; Grade 1-8 purified water (GTCA's when installed) |
| Mega Distillation Tower | ZPM | |
| Extreme Entity Crusher | IV | Mob farm from a spawn egg; Elite / Ultra / Infernal Sigils crush infernal ranks for 3x / 5x / 9x loot |
| Draconic Fusion Crafter | LuV-UHV | Needs Draconic Evolution; tiered DE cores |
| Mystical Cultivation Array | LV+ | Needs Mystical Agriculture; tiered soil and glass, fixed hatch layout, up to 2 Seed Hatches (16-50 seed types, 256 each), ME Output Bus support |
| Mana Nexus | LV-LuV | Needs Botania; stores 10M-2B mana (Mana Hatch for spreaders/sparks, liquid mana from Mana Fluid), does every Botania altar craft (pool, runic altar, terra plate, elven trade, daisy, apothecary, Mythic Botany infuser/rituals); Manasteel to Gaiasteel cores |
| Hyper / Advanced Hyper Naquadah Reactor | UHV / UIV | After GT Not Leisure: naquadah fuel + catalyst reactions with a fixed EU/t per parallel (UV for MkI + hydrogen up to 64x MAX for MkVI + quark-gluon plasma); 4 / 16 parallels, x4 with Naquadah Gas / SpaceTime boost (plus x8 / x16 power, x4 speed) |
| Fuel Refining Complex | UHV-MAX | After GT Not Leisure: bulk Naquadah Fuel MkI-MkVI from light / heavy naquadah fuel + atomic separation catalyst; heating coils (EBF heat) + Field Restriction Coils |
| Lightning Spire | LuV | After Twist Space Technology: up to 512 Spire Lightning Rods, 28M EU per rod every 12.8 s; coolant + molten iron per strike |
| Ascension Gate | MV-MAX | The finale. A ring of 15 chevrons, one per tier ULV to MAX; each locks when its milestone items are in an input bus and the gate has power at that tier. All 15 locked: dial it, and Maxwell comes through |
| Suit Fabrication Station | MV-UHV | Hero suits with their hero kits; four casing tiers (Steel Booth, Showcase Pod, Holo Chamber, Ascension Forge); Suit Picker tab with 3D preview, ME autocraft of missing parts, in-booth suit hologram |

## Systems and items

- **Hero kits**: every suit made in the Suit Fabrication Station comes with its own power kit (speed, strength,
  flight, regeneration, ...). Covers FSang's Heroes, Satsu's Iron Man Addon and Powerborne Heroes.
- **Ascension Gate and Maxwell**: the end of the pack. Build the gate at MV and it tracks your whole run; chevron
  milestones include Terrasteel, a rocket, Alfsteel, Gaiasteel, Naquadah seeds, Hero Cores, the full Apex suit and
  an Eternal Singularity. Dialing opens an event horizon, announces the player to the server, sets off fireworks,
  tints the sky pink for a minute and rolls credits with the player's stats. Maxwell, a tuxedo cat, walks out as a
  pet and also arrives as an item. He does nothing. Any sitting cat named Maxwell spins.
- **Apex Ascension Suit**: the UEV capstone (it is the gate's UEV chevron). Twelve Hero Cores (broken-down tier IV hero suits) plus UEV parts and
  Grade 8 purified water. Full set: flight, regeneration, night vision, immunity to fire / falls / drowning /
  wither / poison, +50% damage to infernal mobs.
- **Infernal mobs** (GT New Horizons parity): 1 in 20 hostile spawns rolls 2-5 of the 27 classic modifiers
  (1 in 10 of those 5-10, 1 in 7 of those 8-15); blazes, ghasts, magma cubes and witches always do. Rank by count:
  Rare / Ultra / Infernal. Health = base x modifiers x 1.8. Look at one for its boss bar (title + modifier list);
  they trail spell particles and drop 25 xp plus one enchanted item per 5 modifiers. Ender, Lifesteal, Ninja,
  Rust and Sticky are off like in GTNH. Config: `gtapexadditions-infernal.toml`. Ops:
  `/gtapexadditions infernal rare|ultra|infernal` or `... infernal mods Choke Storm Vengeance`.
- **GT crops**: 125 CropsNH-style GregTech material crops (Mystical Customization files in `tools/mysticalcustomization`).
  Seeds come only from the **Seed Synthesizer** (LV-UHV): 32 blocks of the material at the crop's tier, halving
  per Synthesizer tier above (floor 1/8). 24 essence -> 1 dust in a GT extractor.
- **Structure Upgrader**: shift-use a formed controller to swap every coil or tiered casing at once, and hatches
  you carry better versions of. On the Space Elevator it also swaps motors, places module controllers you carry into
  free slots (facing the right way, frame built from your inventory) and upgrades modules to the next Mk.
- **Forge of the Gods** (GT Nexus Additions): boots on Re-Avaritia's Star Fuel (GTAA makes it from Neutronium Blocks
  in an assembler); Star Fuel also extracts into Raw Star Matter, its second running fuel.
- **Angel Ring**: flight from the Aether (enchanted gravitite, golden feather) and an Extra Utilities angel block.
- **Aether**: aerclouds centrifuge into helium and helium-3; enchanted gravitite doubles Gravi Stars.
- Own animated textures for every GTAA fluid; uses GT Nexus Additions and GTCA materials when those are installed.

## Pack scripts

`tools/kubejs` holds the KubeJS scripts Apex Ascension ships with this mod: suit station bench removal, Ad Astra
gated by GT tiers (Moon HV, Mars EV, Venus/Mercury IV, Glacio LuV, GT rocket fuel), purified water in high-tier
wafers, Extra Utilities generators off, Botania gated LV-LuV (Orechid and the mana->FE Fluxfield removed), Avaritia neutronium / infinity unified with GregTech's, Tinkers' Avaritia as
the one Avaritia Tinkers material set. `tools/hero_kits.py` and `tools/extra_suits.py` regenerate the hero kits
and the station suit list.

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
