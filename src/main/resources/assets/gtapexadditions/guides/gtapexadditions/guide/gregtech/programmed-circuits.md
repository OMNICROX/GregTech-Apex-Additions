---
navigation:
  title: "Programmed Circuits"
  parent: gregtech/gregtech-index.md
  position: 91
  icon: gtceu:programmed_circuit
---

# Programmed Circuits

Many machines have more than one recipe for the same inputs. A distillery turns one fluid into several products; an
assembler makes several things from the same parts. The **programmed circuit** number picks which recipe runs.

<ItemLink id="gtceu:programmed_circuit" />

* A circuit is **never used up**. Its number is part of the recipe in JEI ("Configuration: 3").
* A recipe that shows a circuit only runs with that number set. A recipe without one runs with any number or none.

## Setting it on a machine

You don't need the item. Every machine has a **circuit slot** in its GUI (the slot with the small circuit icon):

| Action | Effect |
|---|---|
| Left-click | Next number |
| Right-click | Previous number |
| Mouse wheel | Up or down |
| Shift + left-click | A grid of all numbers (0-32) to pick from |
| Shift + right-click | Clear the circuit |

With the item in hand, right-click in the air to pick its number, then shift-right-click a machine to set its
circuit to that number.

## Multiblocks

Every **input bus** and **input hatch** has its own circuit slot. A multiblock reads the circuits in all its buses.

* To run two different circuit recipes in one multiblock, turn on **Distinct** mode on each input bus (button in
  the bus GUI). Each bus is then checked on its own, with its own circuit.
* Give each bus one job: one bus with circuit 1 and the parts for recipe A, one with circuit 2 for recipe B.

## Recipe conflicts

If a machine picks the wrong recipe:

1. Look the output up in JEI and check the circuit number. Set it.
2. If two recipes still match, it's a real conflict: the machine runs whichever it finds first. Split the inputs over
   two distinct buses or two machines.
3. AE2 autocrafting: set the circuit in the machine or bus, not in the pattern. Circuits aren't used up, so a
   pattern that includes one sends a new circuit every craft and they pile up in the bus.
