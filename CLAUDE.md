# GregTech Apex Additions (GTAA) - notes for Claude Code

Addon for the **Apex Ascension** modpack: Minecraft 1.20.1, Forge 47.4.10, GTCEu 7.5.3. Work happens on branch
`apex-platform`. Build with `./gradlew build -x test` (Java 17); the jar is `build/libs/gtapexadditions-1.20.1-0.1.0.jar`.

## Pack instance (on the owner's PC)
`D:\maxim\curseforge\Minecraft\Instances\Apex Ascension`

## Rules the owner set
- Ask before changing anything in `mods` except the GTAA, apexlib and apexcore jars.
- Installing GTAA: overwrite the jar in `mods` AND put a copy in `apex_updates`.
- Back up whatever is replaced to `apex_backup\<date>\<label>\` first.
- Only install jars when the game is closed: `logs\stdout-logs.txt` ends with `[EXIT]` or `logs\latest.log` ends
  with `Stopping!`.
- KubeJS scripts (`kubejs\server_scripts`, copies in `downloads/kubejs/server_scripts`) can change while the game
  runs (`/reload`).
- Third-party mods: only the official CurseForge jars (a modified jar shows as "modified" in CurseForge).
- No survival worlds yet, so breaking changes are fine.
- New features get an on/off switch or a tuning value in `config/gtapexadditions-client.toml` (visual) or
  `config/gtapexadditions-common.toml` (gameplay) whenever it makes sense.
- Ported code keeps its credits: see `CREDITS.md`; GT-Not-Leisure ports keep their author lines and the additional
  terms in `licenses/GT-Not-Leisure/`.

## Layout
- Multis: `src/main/java/com/omnicrox/gtaa/common/data/GTAAMachines.java`, `GTAAGTNHMachines.java`
- Structures: `src/main/resources/gtapexadditions/structures/*.json` ([z][y] strings; converters in `tools/structures`)
- Recipes: `src/main/java/com/omnicrox/gtaa/data/` (GTAACompatRecipes uses `needing(...)` placeholders for other mods)
- Multiblock preview (JEI / NEI Rewrite page): `src/main/java/com/omnicrox/gtaa/client/preview/`
- Roadmap: `docs/ROADMAP.md`
