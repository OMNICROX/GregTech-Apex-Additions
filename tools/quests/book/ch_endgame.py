"""The Endgame - UEV to MAX: antimatter, the DTPF, spacetime, GTNA's capstones and the MAX circuit."""
from fw import Chapter

C = Chapter('end', '&aThe Endgame', 'gtceu:uev_machine_hull', '', 11, 'UEV, UIV, UXV, OpV and MAX', 'end', 'pick_end', xp_base=150)
C.derive = False
q = C.q

q('start', '&a&lThe Endgame', ['gtceu:uev_machine_hull'], [
    'Five tiers remain: &aUEV&r, &2UIV&r, &eUXV&r, &9OpV&r and &cMAX&r. Each multiplies power by four again.',
    'There is no single path any more. The big projects are &eantimatter&r, the &ePlasma Forge&r, &espacetime&r and GTNA\'s three capstones.',
], ['uhv/uev'], kind='hub', guide='progression/10-uev-plus.md')

# ---------------------------------------------------------------- UEV
q('uev_parts', '&aUEV Components', ['gtceu:uev_electric_motor', 'gtceu:uev_field_generator'], [
    'UHV-voltage Assembly Line recipes. The Space Assembler takes over from UIV.',
], ['start'], kind='main')
q('fusion5', 'Fusion Reactor MK V', ['gtapexadditions:uev_fusion_reactor'], [
    'Made in the Space Assembler with ultimate fusion coils and optical computers. It fuses the heaviest plasmas, and the celestial tungsten the Forge of the Gods starts from.',
], ['uev_parts'], kind='opt', guide='lines/fusion.md')
q('sass', '&e&lAntimatter', ['gtapexadditions:antimatter_forge'], [
    'The &eSemi-Stable Antimatter Stabilization Sequencer&r turns protomatter into antimatter, held in an Antimatter Hatch.',
], ['uev_parts'], kind='hub', guide='lines/antimatter.md')
q('slam', '&eAntimatter Power', ['gtapexadditions:antimatter_generator'], [
    'The &eShielded Lagrangian Annihilation Matrix&r reacts antimatter with matter. Nothing else in the pack makes this much power.',
], ['sass'], kind='main', guide='multiblocks/antimatter_generator.md')
q('dtpf', '&eDTPF Prototype MK-II', ['gtapexadditions:plasma_forge_prototype_mk2'], [
    'The Dimensionally Transcendent Plasma Forge prototype: 65x43x65 blocks. It smelts what no blast furnace can, with catalysts.',
    '> The Structure Upgrader builds it for you.',
], ['uev_parts'], kind='main', guide='multiblocks/plasma_forge_prototype_mk2.md')
q('cosmic', 'Cosmic Circuits', ['gtapexadditions:cosmic_processor'], [
    'Cosmic and transcendent circuits are Apex\'s own top circuit lines, built on optical parts with infinity and beyond.',
], ['start'], kind='opt', guide='gregtech/circuits.md')

# ---------------------------------------------------------------- UIV
q('uiv', '&2&lUIV', ['gtceu:uiv_machine_hull', 'gtna:optical_mainframe'], [
    '&2UIV&r: 134,217,728 EU/t. Its circuit is the &eOptical Mainframe&r.',
], ['uev_parts'], kind='hub')
q('nuclear_star', 'Nuclear Star', ['gtapexadditions:nuclear_star'], [
    'A Gravi-Star grown in molten infinity. UIV controllers are built around them.',
], ['uiv'])
q('spacetime', 'Micro SpaceTime Fabricatorio', ['gtapexadditions:micro_spacetime_fabricatorio'], [
    'Quantum anomalies and nuclear stars in bulk.',
], ['uiv'], kind='opt', guide='multiblocks/micro_spacetime_fabricatorio.md')
q('sma', 'Strange Matter Aggregator', ['gtapexadditions:strange_matter_aggregator'], [
    'Breeds antimatter. Rings of field generators set its tier.',
], ['nuclear_star', 'sass'], kind='opt', guide='multiblocks/strange_matter_aggregator.md')
q('mfg', 'Mass Fabricator Genesis', ['gtapexadditions:mass_fabricator_genesis'], [
    'UU-matter with perfect overclocks.',
], ['nuclear_star'], kind='opt', guide='multiblocks/mass_fabricator_genesis.md')
q('dyson', 'Dyson Sphere Launch Site', ['gtapexadditions:dyson_sphere_launch_site'], [
    'Launches Dyson swarm modules into orbit around the sun. Power from a star.',
], ['uiv'], kind='opt')
q('transcendent', 'Transcendent Circuits', ['gtapexadditions:transcendent_processor'], [
    'The line above cosmic.',
], ['cosmic', 'uiv'], kind='opt', guide='gregtech/circuits.md')

# ---------------------------------------------------------------- UXV, OpV, MAX
q('uxv', '&e&lUXV', ['gtceu:uxv_machine_hull', 'gtna:exotic_mainframe'], [
    '&eUXV&r: 536,870,912 EU/t. Its circuit is the &eExotic Mainframe&r.',
], ['uiv'], kind='hub')
q('isa', 'Divine Isa Forge', ['gtapexadditions:divine_isa_forge'], [
    'Two IsaMills and a Space Assembler in one multiblock.',
], ['uxv'], kind='opt', guide='multiblocks/divine_isa_forge.md')
q('rag', 'Real Annihilation Generator', ['gtapexadditions:real_annihilation_generator'], [
    'Antimatter straight into wireless EU.',
], ['uxv', 'slam'], kind='opt', guide='multiblocks/real_annihilation_generator.md')
q('opv', '&9&lOpV', ['gtceu:opv_machine_hull', 'extendedfeatures:opv_universal_circuit'], [
    '&9OpV&r: 2,147,483,648 EU/t. The only OpV circuit is made in the Space Assembler from two UXV circuits.',
], ['uxv'], kind='hub')

# ---------------------------------------------------------------- capstones
q('eoh', '&d&lEye of Harmony', ['gtna:eye_of_harmony'], [
    'GTNA\'s first capstone: it compresses whole planets into materials and power.',
], ['opv'], kind='main', guide='gtmultis/eye_of_harmony.md')
q('fog', '&6&lForge of the Gods', ['gtna:forge_of_gods'], [
    'A star in a box, with modules for smelting, plasma, molten metal and exotic matter.',
], ['opv'], kind='main')
q('tpm', '&b&lTranscendent Plasma Mixer', ['gtna:transcendent_plasma_mixer'], [
    'Mixes the plasmas the other two capstones are fed with.',
], ['opv'], kind='main')
q('max', '&c&lMAX', ['gtapexadditions:max_universal_circuit', 'gtceu:max_machine_hull'], [
    'The &cMAX Universal Circuit&r: an OpV circuit, an OpV emitter and sensor and neutronium foil, in the Space Assembler.',
    'This is the top of Apex Ascension. Everything after it is what you decide to build.',
], ['eoh', 'fog', 'tpm'], kind='hub', bag='end')


# ================================================================ more to do in the endgame (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'end/' + key)
    x.deps += [d if '/' in d else 'end/' + d for d in deps if (d if '/' in d else 'end/' + d) not in x.deps]


q('uev_fg', 'UEV Field Generator', ['gtceu:uev_field_generator'], [
    'A tier up, as ever.',
], ['uev_parts'])
q('uiv_fg', 'UIV Field Generator', ['gtceu:uiv_field_generator'], [
    'And again.',
], ['uiv'])
q('uxv_fg', 'UXV Field Generator', ['gtceu:uxv_field_generator'], [
    'And again.',
], ['uxv'])
q('opv_fg', 'OpV Field Generator', ['gtceu:opv_field_generator'], [
    'The last one.',
], ['opv'])
q('mars_cat', '&dMars', ['placeablemaxwell:mars'], [
    'The other way to finish the pack is a cat. Four cats, then a fifth.',
    'Each of Maxwell\'s friends is one &eMiracle Top&r recipe at MAX: a thousand of several dusts, 64 of the tier\'s parts and 9,999,999 seconds. Parallels and overclocks do not apply; only more rings make it faster.',
], ['start', 'uhv/miracle_top'], kind='main')
q('poomba', '&dPoomba', ['placeablemaxwell:poomba'], [
    'The second friend.',
], ['mars_cat'], kind='main')
q('valenok', '&dValenok', ['placeablemaxwell:valenok'], [
    'The third.',
], ['poomba'], kind='main')
q('vasilisa', '&dVasilisa', ['placeablemaxwell:vasilisa'], [
    'The fourth.',
], ['valenok'], kind='main')
q('maxwell', '&d&lMaxwell', ['placeablemaxwell:maxwell'], [
    'All four friends, Concentrated Effort, a Proof of Heroes, a Giga Chad Token, sixteen Space Warpers and a bucket of Liquid Stargate, in the Miracle Top for 99,999,999 seconds.',
    'Or 64 cod, 64 salmon, 64 string, a black wool and a bucket of milk in a &eMaxwell Maker&r at LV, for 16,000,000 seconds. He does not mind which.',
], ['vasilisa'], kind='hub')
