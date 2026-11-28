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


# ================================================================ more to do in magic (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'magic/' + key)
    x.deps += [d if '/' in d else 'magic/' + d for d in deps if (d if '/' in d else 'magic/' + d) not in x.deps]


# ---------------------------------------------------------------- Botania
q('pure_daisy', 'Pure Daisy', ['botania:pure_daisy'], [
    'Four white petals in the apothecary. Blocks placed around a &ePure Daisy&r slowly change: logs into livingwood, stone into livingrock.',
], ['apothecary'])
q('living', 'Livingwood and Livingrock', ['botania:livingwood_log*8', 'botania:livingrock*8'], [
    'Nearly every Botania block is made of one or the other. Ring the daisy with logs and stone and come back later.',
], ['pure_daisy'])
also('mana', 'living')
q('wand', 'Wand of the Forest', ['botania:twig_wand'], [
    'Botania\'s wrench. It binds flowers to spreaders and spreaders to pools, and shows how full a pool is.',
], ['living'])
q('spreader', 'Mana Spreader', ['botania:mana_spreader'], [
    'Flowers give their mana to the nearest spreader; the spreader shoots it at whatever you aimed it at with the wand.',
], ['living'])
q('endoflame', 'Endoflame', ['botania:endoflame'], [
    'The first flower that makes mana: it burns fuel dropped next to it. Coal, charcoal, anything a furnace takes.',
], ['apothecary'])
also('mana', 'endoflame', 'spreader')
q('hydroangeas', 'Hydroangeas', ['botania:hydroangeas'], [
    'Drinks still water for a trickle of mana. Free, weak, and it wilts after a few days.',
], ['apothecary'], kind='opt')
q('tablet', 'Mana Tablet', ['botania:mana_tablet'], [
    'A battery for mana. Throw it on a pool to fill it; tools and rings draw from it in your inventory.',
], ['mana'], kind='opt')
q('pearl', 'Mana Pearl and Diamond', ['botania:mana_pearl', 'botania:mana_diamond'], [
    'An ender pearl and a diamond, thrown into the pool. Both go into the Runic Altar.',
], ['mana'])
also('runic_altar', 'pearl')
q('spark', 'Sparks', ['botania:spark*4'], [
    'A spark on a pool moves mana to other sparked pools without a spreader, and it is what feeds the Terrestrial Agglomeration Plate.',
], ['mana'])
also('terrasteel', 'spark')
q('rune', 'Runes', ['botania:rune_mana'], [
    'The Runic Altar makes runes from items and mana: the four elements first, then the seasons, then the seven sins. The Rune of Mana is the last basic one.',
], ['runic_altar'])
q('pylon', 'Mana Pylon', ['botania:mana_pylon'], [
    'Gold, manasteel and a mana diamond. Pylons power the Mana Enchanter, and they are the base of the two pylons after this one.',
], ['manasteel'])
q('natura', 'Natura Pylon', ['botania:natura_pylon'], [
    'A mana pylon with terrasteel. Two of them power the portal to Alfheim.',
], ['terrasteel', 'pylon'])
also('alfheim', 'natura')
q('orechid', 'Orechid', ['botania:orechid'], [
    'Turns stone near it into ore, for mana.',
], ['runic_altar'], kind='opt')
q('elementium', 'Elven Trade', ['botania:elementium_ingot*4', 'botania:pixie_dust*4', 'botania:dragonstone'], [
    'Throw things through the portal and the elves throw something back: manasteel for elementium, mana pearls for pixie dust, mana diamonds for dragonstone.',
], ['alfheim'])
q('dreamwood', 'Dreamwood', ['botania:dreamwood*8'], [
    'Livingwood, traded. Elven spreaders and the better wands are made of it.',
], ['alfheim'], kind='opt')
also('gaia', 'elementium')
q('life', 'Gaia Spirits', ['botania:life_essence*8'], [
    'What the Guardian of Gaia drops. The second, harder fight (start it with a Gaia ingot) drops far more.',
], ['gaia'])
also('gaia_ingot', 'life')
q('tiara', 'Flugel Tiara', ['botania:flight_tiara'], [
    'Creative-style flight for as long as the bar lasts, paid in mana.',
], ['life'], kind='opt')
q('terra_tools', 'Terra Shatterer', ['botania:terra_pick'], [
    'A pickaxe that stores mana and digs a wider and wider area the more it holds.',
], ['terrasteel'], kind='opt')
q('infuser', 'Mana Infuser', ['mythicbotany:mana_infuser'], [
    'Mythic Botany\'s altar, built on a platform of gold and shimmerrock. It makes terrasteel and alfsteel from their ingredients.',
], ['alfheim'])
q('alfsteel_ingot', 'Alfsteel', ['mythicbotany:alfsteel_ingot'], [
    'Elementium, dragonstone and pixie dust in the infuser.',
], ['infuser', 'elementium'])
also('alfsteel', 'alfsteel_ingot')

# ---------------------------------------------------------------- Blood Magic
q('b_altar', 'Blood Altar', ['bloodmagic:altar'], [
    'Stand next to the altar and use the &eSacrificial Knife&r: your health becomes &eLife Essence&r in the basin.',
], ['blood'])
also('orb1', 'b_altar')
q('snare', 'Demon Will', ['bloodmagic:soulsnare*8'], [
    'Throw a &eSnare&r at a monster and kill it while it sparkles: it drops &eDemonic Will&r.',
], ['blood'])
also('forge', 'snare')
q('gem', 'Tartaric Gem', ['bloodmagic:soulgempetty'], [
    'A gem in your inventory collects will as you fight. The Hellfire Forge only works with enough will in the gem you give it.',
], ['forge'])
q('sentient', 'Sentient Sword', ['bloodmagic:soulsword'], [
    'A sword that needs no snare: everything it kills drops will, and it grows stronger the more will you carry.',
], ['gem'])
q('blank_rune', 'Blank Runes', ['bloodmagic:blankrune*8'], [
    'The altar grows by tiers: a ring of runes under it for tier two, pillars and capstones after that. Blank runes are the plain ones.',
], ['slates'])
also('orb2', 'blank_rune')
q('runes', 'Speed and Sacrifice', ['bloodmagic:speedrune*4', 'bloodmagic:sacrificerune*4'], [
    'Runes with a purpose replace blank ones: speed runes craft faster, sacrifice runes give more essence per mob, self-sacrifice runes more per heart.',
], ['blank_rune'])
q('alchemy_table', 'Alchemy Table', ['bloodmagic:alchemytable'], [
    'Blood Magic\'s crafting station for reagents. It draws its cost from the network of the orb in its slot.',
], ['slates'])
q('sigils', 'Water and Lava Sigils', ['bloodmagic:watersigil', 'bloodmagic:lavasigil'], [
    'Endless water and endless lava, for a little essence each use. The lava one alone pays for the altar.',
], ['alchemy_table', 'forge'])
q('arc', 'Alchemical Reaction Chamber', ['bloodmagic:alchemicalreactionchamber'], [
    'A machine that runs on a tool in its slot instead of power. It doubles ore, among other things.',
], ['alchemy_table'], kind='opt')
q('reinforced', 'Reinforced Slates', ['bloodmagic:reinforcedslate*4'], [
    'Blank slates in a tier two altar.',
], ['orb2'])
q('imbued', 'Imbued Slates', ['bloodmagic:infusedslate*4'], [
    'Tier three.',
], ['orb3', 'reinforced'])
q('demon_slate', 'Demonic Slates', ['bloodmagic:demonslate*4'], [
    'Tier four.',
], ['orb4', 'imbued'])
q('ethereal', 'Ethereal Slates', ['bloodmagic:etherealslate*4'], [
    'Tier five.',
], ['orb5', 'demon_slate'])
q('ritual_stones', 'Ritual Stones', ['bloodmagic:ritualstone*8', 'bloodmagic:ritualdiviner'], [
    'A ritual is a pattern of stones around a master stone. The &eRitual Diviner&r shows the pattern and places the stones for you.',
], ['reinforced'])
also('rituals', 'ritual_stones')
q('living_armor', 'Living Armour', ['bloodmagic:livingplate'], [
    'Armour that learns from what you do in it: run and it gets faster, get hit and it gets tougher.',
], ['rituals'], kind='opt')

# ---------------------------------------------------------------- Mystical Agriculture
q('prosperity', 'Prosperity Shards', ['mysticalagriculture:prosperity_shard*8'], [
    'The other ore of Mystical Agriculture. Seed bases, the infusion crystal and the altar all need it.',
], ['ma'])
q('crystal', 'Infusion Crystal', ['mysticalagriculture:infusion_crystal'], [
    'Four essences and the crystal in the crafting grid make one essence of the next tier. The crystal wears out.',
], ['prosperity'])
also('tiers', 'crystal')
q('first_seed', 'A First Seed', ['mysticalagriculture:iron_seeds'], [
    'A seed base in the middle of the altar, essence and the resource on the pedestals, and a wand to start it.',
    'From here on, anything with a seed is something you no longer mine.',
], ['altar'])
q('farmland', 'Essence Farmland', ['mysticalagriculture:inferium_farmland*8'], [
    'Farmland with essence in it. Crops on better farmland have a chance to drop a second seed.',
], ['altar'], kind='opt')
q('watering_can', 'Watering Can', ['mysticalagriculture:watering_can'], [
    'Fill it and hold right-click over the field.',
], ['ma'], kind='opt')
q('souls', 'Soul Jars', ['mysticalagriculture:soulium_dagger', 'mysticalagriculture:soul_jar*4'], [
    'Mob seeds need souls: kill the mob with a &eSoulium Dagger&r with empty jars in your inventory.',
], ['altar'])
q('tertium', 'Tertium to Supremium', ['mysticalagriculture:tertium_essence*8', 'mysticalagriculture:imperium_essence*8', 'mysticalagriculture:supremium_essence*8'], [
    'Four of each make one of the next. Better seeds ask for better essence.',
], ['tiers'])
also('insanium', 'tertium')
q('master_crystal', 'Master Infusion Crystal', ['mysticalagriculture:master_infusion_crystal'], [
    'An infusion crystal that never wears out.',
], ['tertium'], kind='opt')
q('awakening', 'Awakening Altar', ['mysticalagriculture:awakening_altar'], [
    'The second altar. It takes supremium essence and the four elemental essences and gives awakened supremium.',
], ['tertium'])
q('awakened', 'Awakened Supremium', ['mysticalagriculture:awakened_supremium_essence*4'], [
    'For the last tier of gear and the last seeds.',
], ['awakening'])
q('accelerator', 'Growth Accelerators', ['mysticalagriculture:inferium_growth_accelerator*4'], [
    'Stack them under the farmland: every block speeds up the crop above.',
], ['altar'], kind='opt')
q('harvester', 'Harvester', ['mysticalagriculture:harvester'], [
    'Harvests and replants the field around it, on fuel.',
], ['altar'], kind='opt')
