---
navigation:
  title: "Cable Loss"
  parent: gregtech/power/power-index.md
  position: 5
  icon: gtceu:tin_single_wire
---

# Cable Loss

Every block of cable lowers the voltage of each packet by its **loss per block per amp** (in the tooltip). Over a
path of L blocks with loss r:

* voltage that arrives = voltage sent - r x L
* EU lost per tick = amps x r x L

Example: Tin Cable loses 1 V per block. 32 V at 2 A over 2 blocks arrives as 30 V at 2 A; 4 EU/t was lost.

Loss is per amp, so **sending the same power at a higher voltage loses less**: one tier up carries the same power
with a quarter of the amps, so a quarter of the loss. That is why long lines run at high voltage and are stepped
down next to the machines.

When loss drops the voltage, a machine needs a bit more current for the same power. If the cable or hatch can't
carry that, use a lower-loss material, a thicker cable, a shorter path or a higher line voltage.


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
