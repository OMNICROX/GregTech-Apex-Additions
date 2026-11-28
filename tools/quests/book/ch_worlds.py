"""Worlds and Space: every dimension, how to get there and which ores only it has."""
from fw import Chapter

C = Chapter('worlds', '&2Worlds and Space', 'minecraft:ender_eye', 'side', 20, 'Each world has its own ore veins', None, None, xp_base=25)
C.derive = False
q = C.q

q('start', '&2&lOther Worlds', ['check'], [
    'As in GT New Horizons, every world has its &eown ore veins&r. Some metals exist in one world only, so progress means travel.',
    'This chapter follows the order you will need them. JEI\'s ore vein pages show each vein\'s world and height.',
], ['stone/veins'], kind='hub', guide='dimensions/dimensions-index.md')

# ---------------------------------------------------------------- early worlds
q('nether', '&cThe Nether', ['dim:minecraft:the_nether'], [
    'Open it in the steam age. &eSulfur&r for rubber and sulfuric acid, nether quartz and certus, redstone, saltpeter, tetrahedrite, beryllium and molybdenum.',
    'Blazes give the rods half the pack asks for.',
], ['start'], kind='main', guide='dimensions/nether.md')
q('blaze', 'Blaze Rods', ['minecraft:blaze_rod*8'], [
    'Brewing, eyes of ender, and glowstone for the Aether portal all start here.',
], ['nether'])
q('aether', '&bThe Aether', ['dim:aether:the_aether'], [
    'A frame of glowstone, lit with a bucket of water. Floating islands with the richest &ealuminium&r in the game: bauxite and ilmenite veins. Also lithium, mica and topaz.',
    'Go at LV or MV, when the blast furnace is ready for aluminium.',
], ['nether'], kind='main', guide='dimensions/aether.md')
q('zanite', 'Zanite and Ambrosium', ['aether:zanite_gemstone*8', 'aether:ambrosium_shard*16'], [
    'Zanite centrifuges into sapphire dust and a little titanium. Ambrosium gives glowstone and sulfur.',
], ['aether'], kind='opt')
q('gravitite', 'Gravitite', ['aether:enchanted_gravitite*4'], [
    'The anti-gravity metal. The Angel Ring, Ad Astra\'s Gravity Normalizer and the Jet Suit need enchanted gravitite.',
], ['aether'])
q('bauxite', 'Aether Bauxite', ['gtceu:raw_bauxite*32'], [
    'Electrolyze bauxite dust for aluminium. Its byproducts are rutile (titanium) and gallium.',
], ['aether'], guide='gregtech/ore-processing.md')

# ---------------------------------------------------------------- twilight
q('twilight', '&2The Twilight Forest', ['dim:twilightforest:twilight_forest'], [
    'A 2x2 pool of water ringed with flowers, lit with an &6HV Field Generator&r instead of a diamond.',
    'Sapphire and olivine veins exist only here.',
], ['start', 'hv/field_gen'], kind='main', guide='dimensions/twilight-forest.md')
q('sapphire', 'Sapphire and Olivine', ['gtceu:raw_sapphire*16', 'gtceu:raw_olivine*16'], [
    'Sapphire makes lenses and aluminium. Olivine gives magnesium for titanium, and its plates become crystal chips at LuV.',
], ['twilight'])
q('naga', 'The Naga', ['twilightforest:naga_trophy'], [
    'The first boss, in a walled courtyard. Keep the trophy.',
], ['twilight'])
q('lich', 'The Lich', ['twilightforest:lich_trophy'], [
    'At the top of the Lich Tower.',
], ['naga'])
q('hydra', 'The Hydra', ['twilightforest:hydra_trophy'], [
    'In the fire swamp. Its fiery blood makes fiery ingots.',
], ['lich'])
q('ur_ghast', 'The Ur-Ghast', ['twilightforest:ur_ghast_trophy'], [
    'At the top of the Dark Tower.',
], ['hydra'])
q('ruina', 'Library of Ruina', ['gtapexadditions:library_of_ruina'], [
    'All four trophies build this IV multiblock, which makes Twilight boss loot in bulk from then on.',
], ['ur_ghast'], kind='opt', guide='multiblocks/library_of_ruina.md')

# ---------------------------------------------------------------- undergarden and end
q('undergarden', '&8The Undergarden', ['dim:undergarden:undergarden'], [
    'Build a stone brick frame and light it with the Light a deepslate brick frame with the &eCatalyst&r.eCatalystLight a deepslate brick frame with the &eCatalyst&r.r. A dark world with &euranium and thorium&r, &erare earth&r, tantalite and cinnabar veins. Worth it from EV.',
], ['start'], kind='main', guide='dimensions/undergarden.md')
q('rare_earth', 'Rare Earths', ['gtceu:raw_bastnasite*16'], [
    'Bastnasite and monazite give neodymium for EV and IV motors, and the rare earth dust that centrifuges into lanthanum, cerium, yttrium and more.',
], ['undergarden'], guide='lines/rare-earths.md')
q('uranium', 'Uranium and Thorium', ['gtceu:raw_pitchblende*16'], [
    'Pitchblende and uraninite. Radon for Quantum Eyes and Stars comes from here, and plutonium later.',
], ['undergarden'], guide='lines/rare-earths.md')
q('end', '&dThe End', ['dim:minecraft:the_end'], [
    'Find a stronghold with eyes of ender. The End\'s veins hold tungsten, the platinum group and naquadah.',
], ['blaze'], kind='main', guide='dimensions/the-end.md')
q('scheelite', 'Tungsten Ore', ['gtceu:raw_scheelite*16'], [
    'Scheelite and tungstate: the EV to IV metal.',
], ['end'], guide='lines/tungsten.md')
q('sheldonite', 'Sheldonite', ['gtceu:raw_cooperite*16'], [
    'Platinum, palladium and the platinum group sludge that the IV chapter is built on.',
], ['end'], guide='lines/platinum.md')
q('naquadah', 'Naquadah Ore', ['gtceu:raw_naquadah*16'], [
    'The LuV metal. Stock up when you see it.',
], ['end'], guide='lines/naquadah.md')

# ---------------------------------------------------------------- space
q('suit', 'Space Suit', ['ad_astra:space_helmet', 'ad_astra:space_suit'], [
    'Steel plates, gas tanks and an oxygen gear. Fill it at an oxygen loader. No suit, no air.',
], ['start', 'hv/moon'], guide='dimensions/space.md')
q('moon', '&7The Moon', ['ad_astra:tier_1_rocket'], [
    'HV. Rocket fuel is a GregTech product. The Moon has its own GregTech veins, and &edesh&r.',
], ['suit'], kind='main', guide='dimensions/space.md')
q('mars', '&cMars', ['ad_astra:tier_2_rocket'], [
    'EV, built from desh. Tungsten veins and &eostrum&r.',
], ['moon'], kind='main', guide='dimensions/space.md')
q('venus', '&6Venus and Mercury', ['ad_astra:tier_3_rocket'], [
    'IV, built from ostrum. &eCalorite&r on Venus.',
], ['mars'], kind='main', guide='dimensions/space.md')
q('glacio', '&bGlacio', ['ad_astra:tier_4_rocket'], [
    'LuV, built from calorite.',
], ['venus'], kind='main', guide='dimensions/space.md')
q('outer', '&eThe Outer Planets', ['ad_astra_rocketed:tier_5_rocket', 'ad_astra_rocketed:tier_6_rocket', 'ad_astra_rocketed:tier_7_rocket'], [
    'Jupiter and its moons at ZPM, Saturn at UV, Uranus and Neptune at UHV. Each step out has heavier metals, ending in neutronium.',
], ['glacio'], kind='main', guide='dimensions/space.md')
q('personal', 'A World of Your Own', ['gtna:personal_space_portal'], [
    'GTNA\'s personal dimension: an empty world for a base with no neighbours and no mobs.',
], ['start'], kind='opt', guide='dimensions/personal-space.md')


# ================================================================ more to do in the other worlds (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'worlds/' + key)
    x.deps += [d if '/' in d else 'worlds/' + d for d in deps if (d if '/' in d else 'worlds/' + d) not in x.deps]


# ---------------------------------------------------------------- the Nether
q('netherite', 'Netherite', ['minecraft:netherite_ingot'], [
    'Ancient debris, deep in the Nether.',
], ['nether'], kind='opt')
q('wither_skulls', 'Three Skulls', ['minecraft:wither_skeleton_skull*3'], [
    'Wither skeletons in a fortress. The Wither\'s star is a GregTech ingredient from EV on, so this is not the last time.',
], ['blaze'])

# ---------------------------------------------------------------- the Aether
q('ambrosium', 'Ambrosium', ['aether:ambrosium_shard*16'], [
    'The Aether\'s coal. It fuels the Altar and the Freezer, and turns grass into enchanted grass.',
], ['aether'])
q('aether_altar', 'Altar and Freezer', ['aether:altar', 'aether:freezer'], [
    'The &eAltar&r repairs and enchants: gravitite ore becomes enchanted gravitite in it. The &eFreezer&r turns water to ice and makes the ice accessories.',
], ['ambrosium'])
also('gravitite', 'aether_altar')
q('bronze_key', 'Bronze Dungeon', ['aether:bronze_dungeon_key'], [
    'The stone dungeons inside the islands. The Slider at the end only takes damage from a pickaxe.',
], ['aether'])
q('silver_key', 'Silver Dungeon', ['aether:silver_dungeon_key'], [
    'The temple on its own cloud. Beat the valkyries for medals, then their queen.',
], ['bronze_key'])
q('gold_key', 'Gold Dungeon', ['aether:gold_dungeon_key'], [
    'The island with the golden oaks, and the Sun Spirit inside it. It only takes damage from the ice it throws: hit it back.',
], ['silver_key'])
q('parachute', 'Golden Parachute', ['aether:golden_parachute'], [
    'Falling off the Aether drops you into the Overworld from the top of the sky. A parachute makes that a way home.',
], ['aether'], kind='opt')

# ---------------------------------------------------------------- the Twilight Forest
q('magic_map', 'Magic Map', ['twilightforest:magic_map_focus'], [
    'A raven feather, glowstone and a torchberry. The &eMagic Map&r shows every boss lair in the forest around you.',
], ['twilight'])
q('ore_magnet', 'Ore Magnet', ['twilightforest:ore_magnet'], [
    'Pulls ore up through the ground towards you.',
], ['naga'], kind='opt')
q('ironwood', 'Ironwood and Steeleaf', ['twilightforest:ironwood_ingot*4', 'twilightforest:steeleaf_ingot*4'], [
    'The forest\'s first two metals: one crafted from roots and iron, one found in labyrinth and tower chests.',
], ['twilight'], kind='opt')
q('minoshroom', 'The Minoshroom', ['twilightforest:minoshroom_trophy'], [
    'At the bottom of a labyrinth in the swamp. Its stew is what lets you walk into the Fire Swamp.',
], ['lich'])
also('hydra', 'minoshroom')
q('fiery', 'Fiery Ingots', ['twilightforest:fiery_ingot*4'], [
    'Iron and the Hydra\'s blood. Fiery tools smelt what they mine.',
], ['hydra'], kind='opt')
q('phantoms', 'The Knight Phantoms', ['twilightforest:knight_phantom_trophy'], [
    'Under the Goblin Knight Stronghold in the Dark Forest. Six of them.',
], ['lich'])
also('ur_ghast', 'phantoms')
q('knightmetal', 'Knightmetal', ['twilightforest:knightmetal_ingot*4'], [
    'Melted down from the armour shards the goblin knights drop.',
], ['phantoms'], kind='opt')
q('yeti', 'The Alpha Yeti', ['twilightforest:alpha_yeti_trophy'], [
    'In a cave in the Snowy Forest. Its fur is the armour for what comes next.',
], ['lich'])
q('snow_queen', 'The Snow Queen', ['twilightforest:snow_queen_trophy'], [
    'At the top of the Aurora Palace on the glacier.',
], ['yeti'])
q('lamp', 'Lamp of Cinders', ['twilightforest:lamp_of_cinders'], [
    'With the Hydra, the Ur-Ghast and the Snow Queen beaten, the Highlands open. The lamp burns the thorns around the last plateau.',
], ['hydra', 'ur_ghast', 'snow_queen'])
q('uncrafting', 'Uncrafting Table', ['twilightforest:uncrafting_table'], [
    'Takes an item apart into its ingredients, for experience.',
], ['twilight'], kind='opt')

# ---------------------------------------------------------------- the Undergarden
q('cloggrum', 'Cloggrum and Froststeel', ['undergarden:cloggrum_ingot*8', 'undergarden:froststeel_ingot*8'], [
    'The Undergarden\'s two common metals. Cloggrum is heavy and slow; froststeel chills what it hits.',
], ['undergarden'])
q('utherium', 'Utherium', ['undergarden:utherium_crystal*4'], [
    'The red crystal. Utherium tools do extra damage to Rotspawn.',
], ['undergarden'], kind='opt')
q('forgotten', 'Forgotten Metal', ['undergarden:forgotten_ingot'], [
    'Dropped in pieces by the Forgotten Guardian, deep down.',
], ['cloggrum'], kind='opt')

# ---------------------------------------------------------------- the End
q('elytra', 'Wings', ['minecraft:elytra'], [
    'In the ship beside an End City. Until the jetpack, and often after, this is how you get around.',
], ['end'])
q('shulker', 'Shulker Boxes', ['minecraft:shulker_shell*2'], [
    'A chest that keeps its contents in your pocket.',
], ['end'], kind='opt')
q('dragon_breath', 'Dragon\'s Breath', ['minecraft:dragon_breath*4'], [
    'Bottle the purple cloud. A few recipes ask for it.',
], ['end'], kind='opt')

# ---------------------------------------------------------------- space
q('nasa', 'NASA Workbench', ['ad_astra:nasa_workbench'], [
    'Rockets are assembled here, piece by piece.',
], ['suit'])
q('launch_pad', 'Launch Pad', ['ad_astra:launch_pad'], [
    'A rocket stands on a launch pad. Take a second one with you: you need it to come back.',
], ['suit'])
also('moon', 'nasa', 'launch_pad')
q('oxygen', 'Air to Breathe', ['ad_astra:oxygen_loader', 'ad_astra:oxygen_distributor'], [
    'The &eOxygen Loader&r fills suits and tanks; the &eOxygen Distributor&r fills a sealed room so you can take the helmet off.',
], ['moon'])
q('desh', 'Desh', ['ad_astra:desh_ingot*8'], [
    'The Moon\'s metal. The next rocket is made of it.',
], ['moon'])
also('mars', 'desh')
q('rover', 'Rover', ['ad_astra:tier_1_rover'], [
    'Four wheels, a fuel tank and a trunk.',
], ['desh'], kind='opt')
q('cheese', 'It Really Is', ['ad_astra:cheese*8'], [
    'Cheese ore. On the Moon.',
], ['moon'], kind='opt')
q('ostrum', 'Ostrum', ['ad_astra:ostrum_ingot*8'], [
    'Mars\'s metal, for the rocket after that.',
], ['mars'])
also('venus', 'ostrum')
q('hot_suit', 'Netherite Space Suit', ['ad_astra:netherite_space_suit'], [
    'Venus and Mercury are hot enough to kill through the first suit. This one holds.',
], ['mars'])
also('venus', 'hot_suit')
q('calorite', 'Calorite', ['ad_astra:calorite_ingot*8'], [
    'Venus\'s metal, and the last of Ad Astra\'s three.',
], ['venus'])
also('glacio', 'calorite')

# ---------------------------------------------------------------- dragons
q('dragon', '&cDragons', ['iceandfire:dragonbone*8'], [
    'Ice and Fire\'s dragons nest in the Overworld: fire dragons in warm places, ice dragons in cold ones, lightning dragons in jungles and badlands. Their roosts are scorched, frozen or charred ground.',
    'A young one is a fight. An old one in its cave is a project. Bones, scales, heart and blood are all worth having.',
], ['start'], kind='main')
q('scales', 'Scales', ['iceandfire:dragonscales_red*8'], [
    'Dragon scale armour, in the colour of the dragon.',
], ['dragon'], kind='opt')
q('dragon_blood', 'Blood and a Heart', ['iceandfire:fire_dragon_blood', 'iceandfire:fire_dragon_heart'], [
    'Use empty bottles on the corpse for blood before you strip it. The heart drops once.',
], ['dragon'])
q('dragonforge', 'Dragonforge', ['iceandfire:dragonforge_fire_core'], [
    'A forge of dragonforge bricks around a core, with a tamed dragon of the right kind breathing into it.',
], ['dragon_blood'])
q('dragonsteel', 'Dragonsteel', ['iceandfire:dragonsteel_fire_ingot*4'], [
    'Iron and dragon blood in the forge.',
], ['dragonforge'])
