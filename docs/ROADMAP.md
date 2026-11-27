
## Apex Guide (requested 2026-11-27, do later)

A GuideNH-style in-game guide mod ("apexguide"), working together with the questbook:
- written pages per topic (what to do next, how a machine works, its recipes shown live from the recipe registry)
- mechanic pages with player-made images: coke oven, AE2 subnets to a drive, etc. (the user will make the images)
- quests link to guide pages and guide pages link back to quests; the user arranges quests, the guide carries the text
- consider building on GuideME (already in the pack via AE2) instead of starting from scratch

## Multi ports
All requested multis are in (last batch 2026-11-27: Bloody Hell, Laser Meteor Miner, Singularity Data Hub,
Strange Matter Aggregator, Mass Fabricator Genesis). Next: Blood Magic tier gating vs GregTech (NHCore's
ScriptBloodMagic as the reference).

## GTNH-style multiblock preview (requested 2026-11-27)
Replace GTCEu's JEI multiblock page with a GTNH NEI-style one (BlockRenderer6343 look): tier slider for tiered blocks,
layer slider, hatch toggle (H), "?" info and the block list down the side. Built in ApexLib as its own recipe-viewer
category, fed by every GTCEu multiblock's pattern / shape infos; GTCEu's own page hidden.

## Convention (2026-11-27)
New features get an on/off switch (or a tuning value) in config/gtapexadditions-client.toml (visual / client) or
config/gtapexadditions-common.toml (gameplay) whenever that makes sense.

## 2026-11-27: removed, to be redone later
- Ascension Gate / Apex Stargate (gate frame blocks, event horizon, credits, gate charge). The Stargate Journey
  data pack (its recipes off, planets as Ad Astra destinations) stays.
- Suit Fabrication Station (hero suits are made at their tailoring benches again; apex_suit_station.js is a stub).
- Personal Space: code kept, off by default (personalSpace.enabled) because GTNA has its own personal dimension.
