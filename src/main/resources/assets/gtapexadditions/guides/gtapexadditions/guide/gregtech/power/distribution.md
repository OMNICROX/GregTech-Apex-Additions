---
navigation:
  title: "Transformers, Buffers and Diodes"
  parent: gregtech/power/power-index.md
  position: 6
  icon: gtceu:lv_transformer_1a
---

# Transformers, Buffers and Diodes

These blocks sit between two parts of a network: they take power in on one side, keep it in a buffer and push it
out on the other.

## Transformers

<Color id="red">A transformer that receives a packet above its input voltage explodes; check the mode before you
power it.</Color>

A <ItemLink id="gtceu:lv_transformer_1a" /> changes voltage by one tier, and current by 4x the other way.
It has one high-voltage face and five low-voltage faces. By default it steps **down** (high face in, low faces out);
right-click it with a **screwdriver** to switch to step-up. Transformers come in 1 A, 2 A, 4 A and 16 A versions
(the amps are on the low side; the high side carries a quarter).

For a lot of power, the multiblock <ItemLink id="gtceu:active_transformer" /> takes any number of Energy Hatches
and Dynamo Hatches (and laser hatches) and moves power between them at any tier.

## Battery buffers

A <ItemLink id="gtceu:lv_battery_buffer_4x" /> holds batteries of its tier. It takes up to 2 A per battery and
pushes out up to 1 A per battery, at its tier's voltage. Because input and output differ, a buffer also works as a
current limiter. For big storage, build a <ItemLink id="gtceu:power_substation" />.

## Diodes

A <ItemLink id="gtceu:lv_diode" /> lets power through in one direction only, up to its voltage and a set
number of amps (change it with a soft mallet). Use diodes to stop power flowing back into the wrong part of a
network, and to limit amps.


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
