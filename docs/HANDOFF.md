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

Layout (`book/grid.py`, 2026-11-28): one obvious path per tier chapter. The big quests the door quest rests on are put in
one order (written order where requirements allow), each REQUIRES the one before, and they run along one row. Optional
big quests branch off or sit as unlinked rows above/below the path, about where they were written in. Each small quest
belongs to one big quest and sits in a free cell next to it. Links that would cross the page are dropped (when the quest
has a nearer requirement) or kept but not drawn (`hide_dependency_lines`); the build prints the counts per chapter.
Worlds, Magic, AE2 and Heroes (`MANY_LINES` in build.py) keep their real links: every line is a row of its own.
Positions are cached in `out/layout_cache.json` (keyed by the chapter's quests and grid.py); a clean build takes ~3 min.
