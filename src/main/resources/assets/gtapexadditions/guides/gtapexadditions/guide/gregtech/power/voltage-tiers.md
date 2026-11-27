---
navigation:
  title: "Voltage Tiers"
  parent: gregtech/power/power-index.md
  position: 1
  icon: gtceu:lv_machine_hull
---

# Voltage Tiers

Progress in Apex is measured in **voltage tiers**. Each tier's **standard voltage** is 4x the one before
(8 x 4^n V, where n is the tier index: ULV is 0, LV is 1 and so on).

| Index | Tier | Name | Standard voltage (V) | Recipe power (EU/t) | Typical cable |
|---|---|---|---|---|---|
| 0 | ULV | Ultra Low Voltage | 8 | 7 | Lead |
| 1 | LV | Low Voltage | 32 | 30 | Tin |
| 2 | MV | Medium Voltage | 128 | 120 | Copper |
| 3 | HV | High Voltage | 512 | 480 | Gold |
| 4 | EV | Extreme Voltage | 2,048 | 1,920 | Aluminium |
| 5 | IV | Insane Voltage | 8,192 | 7,680 | Platinum / Tungsten |
| 6 | LuV | Ludicrous Voltage | 32,768 | 30,720 | Niobium-Titanium |
| 7 | ZPM | Zero Point Module | 131,072 | 122,880 | Vanadium-Gallium |
| 8 | UV | Ultimate Voltage | 524,288 | 491,520 | Yttrium Barium Cuprate |
| 9 | UHV | Highly Ultimate Voltage | 2,097,152 | 1,966,080 | Europium |
| 10 | UEV | Extremely Ultimate Voltage | 8,388,608 | 7,864,320 | |
| 11 | UIV | Insanely Ultimate Voltage | 33,554,432 | 31,457,280 | |
| 12 | UXV | Extremely Extreme Ultimate Voltage | 134,217,728 | 125,829,120 | |
| 13 | OpV | Overpowered Voltage | 536,870,912 | 503,316,480 | |
| 14 | MAX | Maximum Voltage | 2,147,483,647 | | |

**Recipe power** is the most EU/t a recipe of that tier uses: 15/16 of 1 A at the standard voltage. That small
margin lets one amp run a full-tier recipe even after a little cable loss.

GTCEu names the top tiers differently from GT New Horizons: after UIV come **UXV**, **OpV** and **MAX** (GTNH has
UMV and UXV there). A machine or hatch of a tier accepts packets up to that tier's voltage.

The [tier ladder](../../progression/tier-ladder.md) says what each tier unlocks in Apex.


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
