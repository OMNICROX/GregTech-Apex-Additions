"""Endgame Goals: the things to do when the ladder is climbed. Maxwell and his friends, the Stargates, the trophies."""
from fw import Chapter

C = Chapter('end', '&dEndgame Goals', 'placeablemaxwell:maxwell', '', 16, 'A cat, four more cats, and a gate to the stars', 'end', 'pick_end', xp_base=250)
C.derive = False
q = C.q

q('start', '&d&lEndgame Goals', ['gtapexadditions:miracle_top'], [
    'The ladder ends at MAX. This page is for after that, though some of it can be started much earlier.',
    'The goal is a cat. Four cats, then a fifth. All of them are made in the &eMiracle Top&r.',
], ['uhv/miracle_top'], kind='hub', guide='multiblocks/miracle_top.md')
q('maker', 'The Patient Way', ['gtapexadditions:maxwell_maker'], [
    'The &eMaxwell Maker&r is an LV multiblock: a 3x3x3 box of solid steel casings with one input bus, one output bus and one energy hatch.',
    '64 cod, 64 salmon, 64 string, a black wool and a bucket of milk, at 8 EU/t, for 16,000,000 seconds. That is about 185 days.',
    'It cannot be overclocked and takes no parallels. Progress is kept when you log off; the chunk has to be loaded.',
], ['lv/start'], kind='opt')

# ---- the four friends
q('mars_cat', '&dMars', ['placeablemaxwell:mars'], [
    'Each of Maxwell\'s friends is one Miracle Top recipe at MAX voltage, 9,999,999 seconds long. Parallels and overclocks do not apply; only more rings make the Top faster.',
    'Mars: a UHV field generator, a nether star, an infinity ingot and a thousand each of several dusts, in neutronium and lava.',
], ['start'], kind='main')
q('poomba', '&dPoomba', ['placeablemaxwell:poomba'], [
    'A UEV field generator and sensor, an infinity block, a chaotic core and sugar, in neutronium and milk.',
], ['mars_cat'], kind='main')
q('valenok', '&dValenok', ['placeablemaxwell:valenok'], [
    'A UIV field generator and robot arm, a magmatter ingot, an eternal singularity and ice dust, in neutronium and water.',
], ['poomba'], kind='main')
q('vasilisa', '&dVasilisa', ['placeablemaxwell:vasilisa'], [
    'A UXV field generator and emitter, a magmatter ingot, an eternal singularity and an infinity ingot, in neutronium and milk.',
], ['valenok'], kind='main')

# ---- the steps
q('effort', '&eConcentrated Effort', ['gtapexadditions:concentrated_effort'], [
    'A hundred thousand of every electric motor from LV to UEV, in a billion millibuckets of lubricant. Miracle Top.',
], ['start'], kind='main')
q('heroes', '&eProof of Heroes', ['gtapexadditions:proof_of_heroes'], [
    '64 each of eleven of the pack\'s biggest controllers, the Miracle Top itself among them. 1,919,810 seconds.',
], ['effort'], kind='main')
q('chad', '&eGiga Chad Token', ['gtapexadditions:giga_chad_token'], [
    'Plasma Forge: a Gravi-Star, a Quantum Star and a Quantum Eye in neutronium and naquadria, at 2,000,000,000 EU/t for 172,800 seconds. Two days.',
], ['heroes'], kind='main')
q('warper', '&eSpace Warper', ['gtapexadditions:space_warper*16'], [
    'An Assembly Line recipe at UIV. Maxwell wants sixteen.',
], ['chad'], kind='main')

# ---- the stargates
q('gate_milky', 'Milky Way Stargate', ['sgjourney:milky_way_stargate'], [
    'The first of four gates. Liquid Stargate takes four of each kind.',
], ['start'])
q('gate_pegasus', 'Pegasus Stargate', ['sgjourney:pegasus_stargate'], [
    'The second.',
], ['gate_milky'])
q('gate_universe', 'Universe Stargate', ['sgjourney:universe_stargate'], [
    'The third.',
], ['gate_pegasus'])
q('gate_tollan', 'Tollan Stargate', ['sgjourney:tollan_stargate'], [
    'The fourth.',
], ['gate_universe'])
q('stargate', '&eLiquid Stargate', ['gtapexadditions:liquid_stargate_bucket'], [
    'Four Milky Way, four Pegasus, four Universe and four Tollan Stargates dissolved in universium in the Miracle Top, for 99,999,999 seconds.',
], ['gate_tollan', 'warper'], kind='main')

q('maxwell', '&d&lMaxwell', ['placeablemaxwell:maxwell'], [
    'All four friends, Concentrated Effort, a Proof of Heroes, a Giga Chad Token, sixteen Space Warpers and a bucket of Liquid Stargate, in the Miracle Top for 99,999,999 seconds.',
    'Or the Maxwell Maker. He does not mind which.',
], ['vasilisa', 'stargate'], kind='hub', bag='end')
