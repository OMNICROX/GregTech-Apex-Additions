---
navigation:
  title: "The Power Network"
  parent: gregtech/power/power-index.md
  position: 3
  icon: gtceu:tin_single_cable
---

# The Power Network

A power network has three parts:

* **Sources** push packets out of their buffer: generators, the output side of transformers and battery buffers,
  Dynamo Hatches.
* **Cables** carry packets and lose a little voltage on the way ([cable loss](cable-loss.md)). They store nothing.
* **Consumers** take packets into their buffer: machines, the input side of transformers and buffers, Energy Hatches.

Apex uses **GT6-style cables**: a placed cable only connects on the sides you connect it to with a wire cutter (or
the side you placed it against), not to everything around it.

## Powering safely

* A packet above a consumer's voltage **explodes the consumer**. Don't run an LV machine on an MV line; step down
  with a [transformer](distribution.md) first.
* A cable carrying more **voltage** than its rating **burns** instantly. Too many **amps** heat it up until it burns.
  Amps add up: a cable segment carries the sum of everything flowing through it.

## Powering reliably

Each tick a source can push only so many packets, and a consumer can take only so many. For a machine to never
stall:

* the amps all machines need together must not exceed what the sources can push, and
* each machine's need must fit its own input limit (a normal Energy Hatch takes 2 A).

Needed amps = recipe EU/t divided by the voltage that actually arrives (after cable loss).

## Tools

Right-click a cable or machine with a **Portable Scanner** or look at it with Jade to see its stored energy and
input/output voltage and amps.


<Color id="gray">Adapted from the GuideNH DefaultGuide (GT New Horizons) for Apex Ascension.</Color>
