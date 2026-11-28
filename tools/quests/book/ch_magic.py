"""Magic: Botania, Blood Magic and Mystical Agriculture, each gated by GregTech tiers the GT New Horizons way."""
from fw import Chapter

C = Chapter('magic', '&4Magic', 'botania:mana_pool', 'side', 21, 'Botania, Blood Magic and Mystical Agriculture', None, None, xp_base=25)
C.derive = False
q = C.q

q('start', '&4&lMagic Follows the Tiers', ['check'], [
    'Magic is not a shortcut here. Each mod\'s steps are locked behind a voltage tier, mostly by moving a key recipe into a GregTech assembler.',
    'In return, each gets a GregTech multiblock at IV that does all its crafting in bulk.',
], ['stone/begin'], kind='hub', guide='magic/magic-index.md')

# ---------------------------------------------------------------- Botania
q('lexicon', '&aBotania', ['botania:lexicon'], [
    'A sapling and a book. The Lexica Botania explains everything Botania does; this chapter only tells you when you can do it.',
], ['start'], kind='main', guide='magic/botania.md')
q('apothecary', 'Petal Apothecary and Pure Daisy', ['botania:apothecary_default', 'botania:pure_daisy'], [
    'Mystical flowers grow everywhere. The apothecary turns petals into functional flowers; the Pure Daisy turns stone and wood into &elivingrock&r and &elivingwood&r.',
], ['lexicon'])
q('mana', '&eMana', ['botania:mana_pool', 'botania:mana_spreader', 'botania:endoflame'], [
    'A generating flower, a spreader and a pool. Endoflames burn fuel on the ground for mana. All steam-age.',
], ['apothecary'], kind='main')
q('manasteel', 'Manasteel', ['botania:manasteel_ingot*8'], [
    'In Apex, manasteel is infused from &esteel&r, not iron. So Botania really starts with the Primitive Blast Furnace.',
], ['mana', 'steam/steel'])
q('runic_altar', '&7Runic Altar', ['botania:runic_altar'], [
    'An &7LV&r assembler recipe: a mana diamond, livingrock, a steel plate and a circuit. Runes open most of Botania\'s tools and flowers.',
], ['manasteel', 'lv/assembler'], kind='main')
q('mechanical', 'Botanical Machinery', ['botanicalmachinery:mechanical_daisy', 'botanicalmachinery:mechanical_mana_pool'], [
    'Automated versions of the daisy, apothecary, pool and altar, built on machine hulls from LV to HV.',
], ['runic_altar'], kind='opt')
q('terrasteel', '&bTerrasteel', ['botania:terra_plate', 'botania:terrasteel_ingot'], [
    'The Terrestrial Agglomeration Plate is an &bMV&r assembler recipe. Terrasteel takes half a pool of mana per ingot.',
], ['runic_altar', 'mv/good_circuit'], kind='main')
q('alfheim', 'The Elven Gateway', ['botania:alfheim_portal', 'botania:elementium_ingot*4'], [
    'The gateway core is &bMV&r too. Trade manasteel, quartz and more with Alfheim for elementium, dragonstone and pixie dust.',
], ['terrasteel'])
q('gaia', '&6The Gaia Guardian', ['botania:gaia_pylon*4', 'botania:life_essence*8'], [
    'Gaia Pylons need an &6HV&r Field Generator. Kill the Guardian for Gaia Spirits.',
    'After the fight, recipes that use Gaia Spirit run only on the Alfheim Teleportation Array.',
], ['alfheim', 'hv/field_gen'], kind='main')
q('alfsteel', 'Alfsteel', ['mythicbotany:alfsteel_pylon', 'mythicbotany:alfsteel_ingot'], [
    'Mythic Botany\'s Alfsteel Pylon is an &5EV&r recipe. Alfsteel is infused from elementium, dragonstone and pixie dust.',
], ['gaia'], kind='opt')
q('array', '&9Alfheim Teleportation Array', ['gtapexadditions:alfheim_teleportation_array'], [
    'The &9IV&r multiblock for all of Botania: mana pool, runic altar, daisy, apothecary, agglomeration and elven trade as GregTech recipes, with parallels. Runes and catalysts are kept.',
], ['gaia'], kind='hub', guide='multiblocks/alfheim_teleportation_array.md')
q('gaia_ingot', 'Gaia Spirit Ingot', ['botania:gaia_ingot'], [
    'Made on the Array. Botania\'s top materials go into Avaritia\'s Infinity Catalyst.',
], ['array'], kind='opt')

# ---------------------------------------------------------------- Blood Magic
q('blood', '&cBlood Magic', ['bloodmagic:sacrificialdagger', 'bloodmagic:altar'], [
    'The Blood Altar is an &7LV&r assembler recipe. Fill it with your own blood, or with someone else\'s.',
    'Each orb tier needs a &eRaw Blood Orb&r made in a GregTech assembler first. The altar soaks the raw orb.',
], ['start', 'lv/assembler'], kind='main', guide='magic/blood-magic.md')
q('orb1', '&7Weak Blood Orb', ['gtapexadditions:raw_blood_orb_1', 'bloodmagic:weakbloodorb'], [
    'LV: a diamond, a redstone plate, a steel plate and a circuit, with molten glowstone. Soak it in a tier 1 altar.',
], ['blood'], kind='main')
q('slates', 'Slates and Runes', ['bloodmagic:blankslate*8', 'bloodmagic:blankrune*8'], [
    'Slates are stone soaked in the altar. Runes and every other recipe that uses an orb are made in the assembler at that orb\'s tier; the orb is not used up.',
], ['orb1'])
q('orb2', '&bApprentice Blood Orb', ['gtapexadditions:raw_blood_orb_2', 'bloodmagic:apprenticebloodorb'], [
    'MV, around a redstone block. Tier 2 altar. Speed, capacity and displacement runes are MV as well.',
], ['slates', 'mv/good_circuit'], kind='main')
q('forge', 'Hellfire Forge', ['bloodmagic:soulforge', 'bloodmagic:soulsnare'], [
    'Demon will: snare a mob, kill it with a sentient sword, and forge with the will it drops.',
], ['orb1'], kind='opt')
q('orb3', '&6Magician\'s Blood Orb', ['gtapexadditions:raw_blood_orb_3', 'bloodmagic:magicianbloodorb'], [
    'HV, around a gold block. Tier 3 altar.',
], ['orb2', 'hv/hv_assembler'], kind='main')
q('rituals', 'Rituals', ['bloodmagic:masterritualstone', 'bloodmagic:ritualdiviner'], [
    'The Master Ritual Stone is an HV assembler recipe with a Magician\'s orb. The Well of Suffering fills an altar from a mob farm.',
], ['orb3'])
q('orb4', '&5Master Blood Orb', ['gtapexadditions:raw_blood_orb_4', 'bloodmagic:masterbloodorb'], [
    'EV, around a weak blood shard. Tier 4 altar.',
], ['orb3', 'ev/start'], kind='main')
q('orb5', '&9Archmage\'s Blood Orb', ['gtapexadditions:raw_blood_orb_5', 'bloodmagic:archmagebloodorb'], [
    'IV. Tier 5 altar, and the tier 2 runes.',
], ['orb4', 'iv/start'], kind='main')
q('bloody_hell', '&9Bloody Hell', ['gtapexadditions:bloody_hell'], [
    'The &9IV&r multiblock for all of Blood Magic: every altar, alchemy table and alchemy array recipe in bulk, soaked from a pool of Life Essence in the structure.',
], ['orb4', 'rituals'], kind='hub', guide='multiblocks/bloody_hell.md')

# ---------------------------------------------------------------- Mystical Agriculture
q('ma', '&aMystical Agriculture', ['mysticalagriculture:inferium_essence*16', 'mysticalagriculture:prosperity_shard*8'], [
    'Inferium drops from mobs and ore; prosperity shards from ore. Resource crops grow essence that crafts into materials.',
], ['start'], kind='main', guide='magic/mystical-agriculture.md')
q('altar', 'Infusion Altar', ['mysticalagriculture:infusion_altar', 'mysticalagriculture:inferium_seeds'], [
    'Seeds are infused on the altar from essence and the crop\'s material.',
], ['ma'])
q('tiers', 'Higher Essence', ['mysticalagriculture:prudentium_essence*4', 'mysticalagriculture:supremium_essence*4'], [
    'Four inferium and an infusion crystal make the next tier, up to supremium. Higher tier crops need higher essence.',
], ['altar'])
q('cultivation', '&7Mystical Cultivation Array', ['gtapexadditions:mystical_cultivation_array'], [
    'An &7LV&r multiblock farm. Seeds sit in its Seed Hatches and are never used up; essence comes out of the output bus.',
], ['altar', 'lv/assembler'], kind='hub', guide='multiblocks/mystical_cultivation_array.md')
q('synth', 'Seed Synthesizer', ['gtapexadditions:lv_seed_synthesizer'], [
    'Seeds for &eGregTech metals&r come only from the Seed Synthesizer, one tier of machine per tier of metal. They grow in the Cultivation Array, and 24 essence extract into one dust.',
], ['cultivation'], kind='main', guide='magic/mystical-agriculture.md')
q('insanium', 'Insanium', ['mysticalagradditions:insanium_essence'], [
    'The tier above supremium, for the crops of the endgame.',
], ['tiers'], kind='opt')
