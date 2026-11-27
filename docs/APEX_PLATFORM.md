# The Apex platform

GT New Horizons is not one mod: GTNHLib holds shared code, NewHorizonsCoreMod holds the pack's own recipes and
tweaks, GT5-Unofficial and its addons hold the content, and small mods patch others. Apex Ascension follows the
same split:

| Mod | Folder | Job |
| --- | --- | --- |
| **Apex Lib** (`apexlib`) | `apex-lib/` | Shared code: multiblock helpers (parallels, counted casings, hatches, JSON structures), GTNH-style tooltips, number formatting, soft compatibility with other mods' APIs. Does nothing on its own. |
| **GregTech Apex Additions** (`gtapexadditions`) | root | GregTech content: machines, materials, ports of GTNH / Twist Space Technology / 123Technology multiblocks, the Ascension Gate. Depends on Apex Lib. |
| **Apex Core** (`apexcore`) | `apex-core/` | The pack's own changes, moved out of KubeJS one script at a time: recipe fixes, tier gating, ore veins, mod integrations, quest hooks. Depends on Apex Lib. |
| **Apex Fixes** (`apexfixes`) | separate | Patches to other mods while they load, so their jars stay the official files. |

## Building

    ./gradlew build          # builds apex-lib/build/libs/apexlib-*.jar, apex-core/build/libs/apexcore-*.jar
                             # and build/libs/gtapexadditions-*.jar
    ./gradlew runData        # regenerates GTAA's lang, models and blockstates

All three jars (`apexlib`, `apexcore`, `gtapexadditions`) go in the pack's `mods` folder.

## Moved out of KubeJS

| KubeJS script | Apex Core class | Notes |
| --- | --- | --- |
| `apex_purified_water.js` | `recipe.WaferRecipes` | delete the script when installing Apex Core; also fixes the GTCA wafer bug |


## Apex Lib at a glance

- `multiblock.ApexModifiers` — `parallel(max, speed, eu)` recipe modifier (limited by power, inputs and output
  space), `tier(machine)`, `counted(ctx, blocks)` / `count(machine, ctx)` to count rings, coils and the like.
- `multiblock.ApexHatches` — read and drain input hatches outside recipe matching (`amount`, `has`, `drain`).
- `multiblock.ApexStructures` — GTNH StructureLib shapes as JSON (`/<namespace>/structures/<file>.json`) or as a
  Java `String[][]`; porting a GTNH machine is copying its `shapeMain`.
- `multiblock.IStagedMulti` — multiblocks that grow in stages.
- `tooltip.ApexTooltips` — the same controller tooltip layout on every machine.
- `util.ApexFormat` — numbers with commas, scientific notation from a billion (GTNH style).
- `compat.ApexWireless` — GTM Things and GTNA wireless energy networks through reflection (no hard dependency).

## Licence

GPL-3.0-or-later for all Apex mods. Ported content is credited in `CREDITS.md`.
