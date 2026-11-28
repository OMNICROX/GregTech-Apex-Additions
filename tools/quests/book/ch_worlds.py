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
