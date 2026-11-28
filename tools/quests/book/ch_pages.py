"""One page per mod. The quests were written in the broad Magic, Worlds, AE2 and Heroes chapters; here they move to pages
of their own. New quests for a mod go in its block below."""
from fw import Chapter
from carve import carve

PAGES = []


def page(src, key, title, icon, order, subtitle, start_title, start_task, start_desc, start_deps, keys, guide=None):
    C = Chapter(key, title, icon, 'side', order, subtitle, None, None, xp_base=25)
    C.derive = False
    s = C.q('start', start_title, [start_task], start_desc, start_deps, kind='hub', guide=guide)
    carve(src, C, keys, s)
    PAGES.append(C)
    return C


BOTANIA = page('ch_magic', 'magic', '&aFlower Power', 'botania:lexicon', 30, 'Botania, Mythic Botany and the mana machines',
    '&a&lFlower Power', 'check', [
        'Botania runs on mana, made by flowers. Its steps are locked behind voltage tiers here, mostly by moving a key recipe into a GregTech assembler.',
        'In return it gets a GregTech multiblock at IV that does its crafting in bulk.',
    ], ['stone/begin'],
    ['lexicon', 'apothecary', 'pure_daisy', 'living', 'wand', 'mana', 'spreader', 'endoflame', 'hydroangeas', 'tablet', 'manasteel', 'pearl',
     'spark', 'runic_altar', 'rune', 'mechanical', 'terrasteel', 'pylon', 'natura', 'orechid', 'alfheim', 'elementium', 'dreamwood', 'life',
     'tiara', 'terra_tools', 'gaia', 'gaia_ingot', 'alfsteel', 'infuser', 'alfsteel_ingot', 'array'], guide='magic/botania.md')

BLOOD = page('ch_magic', 'blood', '&4Paying the Highest Price', 'bloodmagic:altar', 31, 'Blood Magic: altars, orbs, slates and rituals',
    '&4&lPaying the Highest Price', 'check', [
        'Blood Magic trades life for power. Every orb tier here needs a Raw Blood Orb from a GregTech assembler first, so the altar follows the voltage tiers.',
    ], ['stone/begin'],
    ['blood', 'b_altar', 'snare', 'orb1', 'slates', 'blank_rune', 'runes', 'orb2', 'forge', 'gem', 'sentient', 'alchemy_table', 'sigils', 'arc',
     'reinforced', 'orb3', 'imbued', 'rituals', 'ritual_stones', 'orb4', 'demon_slate', 'living_armor', 'orb5', 'ethereal', 'bloody_hell'],
    guide='magic/blood-magic.md')

MYSTICAL = page('ch_magic', 'mystical', '&2The Green Revolution', 'mysticalagriculture:inferium_seeds', 32, 'Mystical Agriculture: resources that grow',
    '&2&lThe Green Revolution', 'check', [
        'Mystical Agriculture grows materials as crops. The seed tiers follow the voltage tiers, and GregTech materials get seeds of their own.',
    ], ['stone/begin'],
    ['ma', 'prosperity', 'altar', 'crystal', 'first_seed', 'farmland', 'watering_can', 'tiers', 'souls', 'tertium', 'master_crystal', 'cultivation',
     'synth', 'awakening', 'awakened', 'accelerator', 'harvester', 'insanium'], guide='magic/mystical-agriculture.md')

AETHER = page('ch_worlds', 'aether', '&bThe Aether', 'aether:zanite_gemstone', 33, 'Sky islands, dungeons and the first aluminium',
    '&b&lThe Aether', 'check', [
        'Islands in the sky, reached through a glowstone portal lit with water. Its ore veins carry what the Overworld lacks.',
    ], ['worlds/start'],
    ['aether', 'ambrosium', 'zanite', 'gravitite', 'bauxite', 'aether_altar', 'parachute', 'bronze_key', 'silver_key', 'gold_key'], guide='dimensions/aether.md')

TWILIGHT = page('ch_worlds', 'twilight', '&2The Twilight Forest', 'twilightforest:naga_scale', 34, 'A forest of bosses, in order',
    '&2&lThe Twilight Forest', 'check', [
        'A world of endless dusk. Its bosses guard each other: each one opens the way to the next.',
    ], ['worlds/start'],
    ['twilight', 'magic_map', 'ore_magnet', 'sapphire', 'ironwood', 'naga', 'lich', 'minoshroom', 'hydra', 'fiery', 'phantoms', 'knightmetal',
     'ur_ghast', 'yeti', 'snow_queen', 'lamp', 'uncrafting', 'ruina'])

UNDER = page('ch_worlds', 'undergarden', '&8The Undergarden', 'undergarden:cloggrum_ingot', 35, 'Deep below: rare earths and uranium',
    '&8&lThe Undergarden', 'check', [
        'A dark world under the world. Its veins carry rare earths and uranium.',
    ], ['worlds/start'],
    ['undergarden', 'cloggrum', 'utherium', 'forgotten', 'rare_earth', 'uranium'])

SPACE = page('ch_worlds', 'space', '&9Space Race', 'ad_astra:tier_1_rocket', 36, 'Rockets, suits and every planet',
    '&9&lSpace Race', 'check', [
        'Ad Astra\'s rockets are gated by voltage tier, one planet at a time. Every planet has ore veins found nowhere else.',
    ], ['worlds/start'],
    ['nasa', 'launch_pad', 'suit', 'oxygen', 'moon', 'desh', 'cheese', 'rover', 'mars', 'ostrum', 'hot_suit', 'venus', 'calorite', 'glacio', 'outer', 'personal'])

DRAGONS = page('ch_worlds', 'dragons', '&cHere Be Dragons', 'iceandfire:dragonbone', 37, 'Ice and Fire: dragons and what they are made of',
    '&c&lHere Be Dragons', 'check', [
        'Ice and Fire fills the Overworld with dragons. They are dangerous, and every part of one is useful.',
    ], ['worlds/start'],
    ['dragon', 'scales', 'dragon_blood', 'dragonforge', 'dragonsteel'])

MOBS = page('ch_ae2', 'mobs', '&cKill All the Things', 'hostilenetworks:deep_learner', 38, 'Mob drops without the mobs',
    '&c&lKill All the Things', 'check', [
        'Mob drops by simulation and by machine: Hostile Neural Networks, and GregTech\'s own entity crusher.',
    ], ['ae2/start'],
    ['learner', 'data_model', 'sim', 'loot_fab', 'crusher'])

STONES = page('ch_heroes', 'stones', '&dThe Infinity Stones', 'infinity:infinity_gauntlet', 39, 'Six stones, hidden across the worlds',
    '&d&lThe Infinity Stones', 'check', [
        'The six stones have no recipes. Each waits in a structure somewhere in the worlds, and each has a guardian or a trick.',
    ], ['heroes/start'],
    ['dwarf_star', 'kree_ship', 'temple', 'gauntlet', 'space_stone', 'time_stone', 'mind_stone', 'power_stone', 'reality_stone', 'soul_stone', 'thanos'])
