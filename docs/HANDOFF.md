# Handoff (2026-11-27): where the work stands

## Not yet installed in the pack
The current `apex-platform` build: GTNH-style multiblock preview (BlockRenderer6343 controls: left-drag rotate,
right-drag zoom, middle-drag pan, scroll zoom, right-click a block = what that spot accepts, Tier/Layer sliders,
H = hatch spot highlight, R = reset, ? = help) and the config files `gtapexadditions-client.toml` /
`gtapexadditions-common.toml`. Build, back up the old jar, install to `mods` and `apex_updates` (game closed).

## Multiblock preview: what to check first
- Code: `src/main/java/com/omnicrox/gtaa/client/preview/` + `integration/jei/`.
- NEI Rewrite shows JEI categories but never passes mouse input, so `PreviewPanel` polls GLFW buttons every frame
  and `MouseHandlerScrollMixin` (SRG `m_91526_`) hands the wheel to `PreviewInput` first.
- `ApexSceneRenderer` = LDLib FBO renderer that restores the framebuffer and viewport (LDLib's own leaves the
  viewport at FBO size and blanked the NEI page). Scenes need a `ParticleManager` (sets up the camera).
- Render failures are logged once per multi as `[Apex preview]` in latest.log.
- Last confirmed in game: scene renders, but clicks didn't register (before the polling rewrite). Untested since.

## Done this session (all on apex-platform)
Bloody Hell, Laser Meteor Miner, Singularity Data Hub (AE2 storage), Strange Matter Aggregator (adapted: breeds
GTAA antimatter), Mass Fabricator Genesis, DTPF MK-II with Twist's real structure, Bloody Hell's own casings
(Twist/NHCore textures), gtocore CTM textures for gtna's magenta casings, auto-build casing-first mixins,
Structure Upgrader stages for Miracle Top / Bloody Hell / LMM / SMA / Mega Distillation Tower.

## Next (owner's order)
1. Get the multiblock preview fully working and matching GTNH's NEI page.
2. "apexguide" guide mod working together with the questbook (see ROADMAP.md).
3. Blood Magic tier gating vs GregTech (GTNH NHCore ScriptBloodMagic as reference).

## Quest book (2026-11-27)
`tools/quests/book/build.py` writes the whole book: 16 chapters, 580 quests (Stone to MAX, plus Worlds, Magic, AE2,
Heroes). `out/release/` is the pack copy (no GTNH reference chapters); it is installed in the instance's
`config/ftbquests/quests`. Quest ids must stay below 0x8000... (FTB Quests re-ids anything higher and drops its links).
A quest's `guide=` (or `guide_map.py` for the first four chapters) adds the guide line and feeds the guide's Quests lists:
rebuild the quests first, then `tools/guide/build_guide.py`. Chapters arranged by hand in game go in `book/locked/`.

Layout (`book/grid.py`, 2026-11-28): modelled on the Stone page the owner arranged by hand (kept in
`book/locked_archive/`; Stone itself was rebuilt when it doubled in size). A chapter with a file in `book/locked/` is
never laid out or re-linked by the build. Quests stand almost shoulder to shoulder (0.125 between them). In a tier chapter the big quests the door
quest rests on are one chain (written order where requirements allow, each REQUIRES the one before) that folds into
rows like a snake: right along one row, down, back left along the next. Optional big quests and all small quests are
placed in the free cells next to what they belong to, then moved by a search (short, straight, uncrossed links; small
page). Links that would cross the page are dropped (when the quest has a nearer requirement) or kept but not drawn
(`hide_dependency_lines`); the build prints the counts. Worlds, Magic, AE2 and Heroes (`MANY_LINES` in build.py) keep
their real links: every line is a row of its own.
Positions are cached in `out/layout_cache.json` (keyed by the chapter's quests and grid.py); a clean build takes ~3 min.

## Quest expansion (started 2026-11-28)
The owner wants the book to guide the whole pack, GTNH-full. Every chapter has a block at its end ("more to do in
...") with the added quests: 580 -> 991 in total. Still thin against GTNH: the later tiers (30-57 quests each).
The Maxwell step items (Concentrated Effort, Proof of Heroes, Giga Chad Token, Space Warper, Liquid Stargate) and the
Maxwell Maker have no quests yet: they are not in the item dump until the game has loaded the new jar.
New quests only use item ids checked against `apex_quest_items.json`, and descriptions only say what was verified.
To do when the item dump is refreshed: a quest for the Maxwell Maker (LV, optional).

## Maxwell (2026-11-28)
Maxwell and his four friends are made in the Miracle Top with its own recipe type (`gtapexadditions:miracle_top`: MAX,
9,999,999 s per friend, 99,999,999 s for Maxwell; no parallels or overclocks, only the rings' 4x speed each). The
assembly line recipes are gone. The joke alternative is the Maxwell Maker (`GTAAMachines.MAXWELL_MAKER`, 3x3x3 steel
casings, one energy hatch, no recipe modifiers): 8 EU/t for 16,000,000 s. Switch: `maxwell.maxwellMaker` in the common
config.
Maxwell's own recipe also takes four step items and a fluid, each one absurd recipe (`maxwellSteps` in
GTAACompatRecipes): Concentrated Effort, Proof of Heroes and Liquid Stargate in the Miracle Top, the Giga Chad Token in
GTNA's plasma forge (10,800 K), Space Warpers on the assembly line. After adding a machine, item or recipe type run
`./gradlew runData` (it writes blockstates, models and names into `src/generated`); without it the block has no model.

## InsaneAE cells hold one type (2026-11-28)
The owner found InsaneAE's storage cells too strong (up to millions of types per cell). `InsaneCellTypesMixin` replaces
`ModCells.totalTypes(...)` with `insaneAE.typesPerCell` from the common config (default 1, 0 = the mod's own numbers),
so a cell is like GTNH's singularity cell: one item or fluid, a huge amount. Storing many types at that scale is the
Singularity Data Hub's job. InsaneAE is not in the dev environment: the mixin is `@Pseudo` with `require = 0` and has
only been checked against the decompiled jar (1.1.3), not in a running game.
