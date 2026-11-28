"""Tier 14 - MAX: the last voltage."""
from fw import Chapter

C = Chapter('max', '&cTier 14 - MAX', 'gtceu:max_machine_hull', '', 15, '2,147,483,648 EU/t: there is nothing above', 'end', 'pick_end', xp_base=200)
C.derive = False
q = C.q

q('start', '&c&lTier 14: MAX', ['gtceu:max_machine_hull', 'gtapexadditions:max_universal_circuit'], [
    '&cMAX&r: 2,147,483,648 EU/t per amp. There are no MAX motors or machines: MAX is hatches, circuits and the recipes that ask for them.',
], ['opv/max'], kind='hub', guide='progression/10-uev-plus.md')
q('energy_hatch', '&eMAX Energy Hatch', ['gtceu:max_energy_input_hatch*2'], [
    'UMV superconductor and a shirabon spring on a MAX hull.',
], ['start'], kind='main')
q('dynamo', 'MAX Dynamo Hatch', ['gtceu:max_energy_output_hatch'], [
    'For the generators that can fill it.',
], ['start'], kind='opt')
q('buses', 'MAX Buses and Hatches', ['gtceu:max_input_bus', 'gtceu:max_output_bus', 'gtceu:max_input_hatch', 'gtceu:max_output_hatch'], [
    'The biggest of each.',
], ['start'], kind='opt')
q('wireless', 'MAX Wireless Hatches', ['gtna:wireless_energy_hatch_1a_max', 'gtna:wireless_dynamo_hatch_1a_max'], [
    'Pull from and push to the Nexus network at MAX.',
], ['energy_hatch'], kind='opt')
q('capacitors', 'MAX Nexus Capacitors', ['gtna:nexus_capacitor_max*4'], [
    '750 MAX capacitors in a Nexus Flux Matrix unlock unlimited storage and network transfer.',
], ['start'], kind='opt')
q('gtna_hatches', 'MAX Thread, Overclock and Accelerate', ['gtna:thread_hatch_max', 'gtna:overclock_hatch_max', 'gtna:accelerate_hatch_max'], [
    'A Thread Hatch lets a machine run different recipes at the same time. An Overclock Hatch changes the overclock scaling. An Accelerate Hatch cuts the final duration.',
], ['start'], kind='opt')
q('nanites', 'Six-Phased Copper Nanites', ['gtna:six_phased_copper_nanites'], [
    'A MAX Nano Forge recipe.',
], ['start'], kind='opt')
q('eternity_nanites', 'Eternity Nanites', ['gtna:eternity_nanites'], [
    'Nano Forge at MAX: universium nanites, an eternity block and a Gravi-Star in spatial fluid, stellar catalyst and primordial matter.',
], ['nanites'], kind='opt')
q('fuel6', 'Naquadah Fuel MkVI', ['gtapexadditions:naquadah_based_liquid_fuel_mkvi_bucket'], [
    'Fuel Refining Complex at MAX: both naquadah fuels with atomic separation catalyst, celestial tungsten, mellion, shirabon and raw star matter. Universium halves the time.',
    'The Hyper Naquadah Reactor burns it with quark-gluon plasma.',
], ['energy_hatch'], kind='opt', guide='lines/naquadah.md')
q('done', '&c&lThe Top', ['gtapexadditions:max_universal_circuit*4'], [
    'This is the top of the voltage ladder. What is left is what you decide to build: open &dEndgame Goals&r.',
], ['energy_hatch'], kind='hub', bag='end')
