"""The two finales: the Ascension Gate (the Apex Stargate) and Maxwell's cats."""
from core import *
from ch_early import at


def ascension():
    qs = []

    def add(key, title, tasks, desc, x, y, deps=(), **kw):
        kw.setdefault('xp', 500)
        q = at(Q('asc/' + key, title, tasks, desc, deps=[d if '/' in d else 'asc/' + d for d in deps], **kw), x, y)
        qs.append(q)
        return q

    add('intro', '&5The Apex Stargate', ['check'],
        ['Every Stargate in this world is sealed. Dialing does nothing; the gates you find in ruins are dead stone.',
         'One gate can wake them all: the &5Apex Stargate&r, built on the Ascension Gate and charged with &b9.22 Qn EU&r, the largest number GregTech can hold.',
         'This chapter is the end of Apex Ascension.'], 0, 0, ['t9/gate'], shape='gear', size=2.0, reward='uhv')
    add('ring', 'Ascension Gate Rings', ['gtapexadditions:gate_ring*14'],
        'The gate\'s ring: neutronium frames, infinity and UHV field generators, made in the Assembly Line at UHV. You need 14.', 2, -1.5, ['intro'], reward='uhv')
    add('chevron', 'Ascension Gate Chevrons', ['gtapexadditions:gate_chevron*9'],
        'A ring fitted with UEV emitters and sensors: the gate\'s 9 chevrons. Each one is built on a ring, so make 23 rings in all.', 4, -1.5, ['ring', 't10/gate'], reward='uev')
    add('base', 'Ascension Gate Base', ['gtapexadditions:gate_base'],
        'The bottom of the gate, where the dial sits: infinity blocks, chaotic cores, magmatter and UIV field generators.', 6, -1.5, ['chevron', 't11/gate'], reward='uiv')
    add('pedestal', 'Ascension Gate Pedestal', ['gtapexadditions:gate_pedestal*4'],
        'The pedestal under the gate: a row of 9 with the controller in the middle. At least 4 must be pedestal blocks; the rest can be energy or laser hatches. UXV assembler.',
        8, -1.5, ['base', 't12/gate'], reward='uxv')
    add('controller', '&dAscension Gate', ['gtapexadditions:ascension_gate'],
        ['The controller. OpV assembly line: a magmatter block, an eternal singularity, chaotic cores and OpV field generators.',
         'Place it in the middle of the pedestal row and build the frame on top: the Base right above the controller, then the Rings and Chevrons around it. The multiblock preview shows every spot.'],
        10, 0, ['pedestal', 't13/gate'], shape='rsquare', size=1.5, reward='opv')
    add('power', 'Feeding a Gate', ['gtmthings:wireless_energy_binding_tool'],
        ['9.22 Qn EU is more than any battery holds. The gate drinks from its energy and laser hatches &eand&r from your wireless networks: GTM Things wireless energy and gtna\'s Nexus.',
         'At most ~768 T EU/t goes in, so the fastest charge takes 10 minutes. Antimatter, the Dyson Swarm and the Forge of the Gods\' star all help.'],
        8, 1.5, ['controller'], xp=200)
    add('warpgate', 'The Warpgate', ['check'],
        'When the frame is complete it turns into the real gate: the Warpgate. It stays sealed until the charge is full. The controller shows how far along it is.',
        12, -1.5, ['controller'], xp=1000)
    add('ascend', '&d&lAscension', ['gtapexadditions:maxwell'],
        ['Charge the gate to 100%. It opens one way, just once. The credits roll, every gate in the world wakes, and something comes through.',
         'It is completely useless. It is perfect.', '&7Thank you for playing Apex Ascension.'],
        14, 0, ['warpgate', 'power'], shape='gear', size=3.0, reward='max', xp=10000)
    add('dial', 'Every Gate Awake', ['sgjourney:milky_way_dhd', 'sgjourney:classic_dhd', 'sgjourney:pegasus_dhd'],
        'After the Ascension the gates you find in the world can dial again. Use a DHD on any of them and go explore the worlds behind them.',
        16, 0, ['ascend'], optional=True, xp=500)
    return Ch('ascension', '&5&lAscension', 'gtapexadditions:ascension_gate', 'main', qs, subtitle='The Apex Stargate', order=30)


def cats():
    qs = []

    def add(key, title, tasks, desc, x, y, deps=(), **kw):
        kw.setdefault('xp', 500)
        q = at(Q('cat/' + key, title, tasks, desc, deps=[d if '/' in d else 'cat/' + d for d in deps], **kw), x, y)
        qs.append(q)
        return q

    add('intro', '&dMaxwell\'s Cats', ['check'],
        ['The other final goal. Five placeable cats, each built in the Assembly Line from a whole tier\'s best parts. They do nothing at all. That is the point.',
         'Mars at UHV, Poomba at UEV, Valenok at UIV, Vasilisa at UXV, and Maxwell himself, who needs all four, at OpV.'], 0, 0, ['t9/gate'], shape='gear', size=1.5, reward='uhv')
    add('mars', 'Mars', ['placeablemaxwell:mars'], 'UHV: field generators, robot arms, motors, 16 infinity ingots and 16 nether stars.', 2, -1, ['intro'], reward='uhv')
    add('poomba', 'Poomba', ['placeablemaxwell:poomba'], 'UEV: emitters, sensors, infinity blocks and a chaotic core.', 4, -1, ['mars', 't10/gate'], reward='uev')
    add('valenok', 'Valenok', ['placeablemaxwell:valenok'], 'UIV: robot arms, magmatter and an eternal singularity.', 6, -1, ['poomba', 't11/gate'], reward='uiv')
    add('vasilisa', 'Vasilisa', ['placeablemaxwell:vasilisa'], 'UXV: emitters, magmatter, eternal singularities and infinity.', 8, -1, ['valenok', 't12/gate'], reward='uxv')
    add('maxwell', '&d&lMaxwell', ['placeablemaxwell:maxwell'],
        ['OpV: all four cats, a magmatter block, 16 eternal singularities and 4 infinity catalysts. Place him. Watch him spin.'],
        10, 0, ['vasilisa', 't13/gate'], shape='gear', size=2.5, reward='max', xp=5000)
    return Ch('cats', '&d&lMaxwell\'s Cats', 'placeablemaxwell:maxwell', 'main', qs, subtitle='Completely useless. Very important.', order=31)
