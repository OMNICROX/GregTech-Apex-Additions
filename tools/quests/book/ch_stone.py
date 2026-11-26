"""Tier 0 - Stone Age: from punching a tree to the first bronze boiler."""
from fw import Chapter

C = Chapter('stone', '&7Tier 0 - Stone Age', 'gtceu:flint_mortar', '', 0,
            'Flint, ore veins, fire and bronze', 'stone', 'pick_stone', xp_base=5)
q = C.q

# ---------------------------------------------------------------- the start
q('begin', '&6&lAnd So It Begins', ['check'], [
    'Welcome to &6Apex Ascension&r: a GregTech pack that goes from a flint mortar all the way to charging a Stargate with more energy than GregTech can count.',
    'This book is your guide. Each &eTier&r chapter covers one stage of the climb. &eBig quests&r are the spine of the tier: follow them and you will never be stuck. &eSmall quests&r around them teach a trick, show a better way to do something, or point at something worth having. &eRound quests&r are optional.',
    'Read the descriptions. They are written to save you hours: where an ore spawns, what a machine is really for, which shortcut is a trap.',
    '> Most quests let you pick a reward from a short list. Big quests also give a loot bag of the tier.',
], at=(0, 0), kind='hub', xp=10)

q('wood', 'Punch a Tree', ['check'], [
    'Every journey starts with a log. Get a few, make planks, a crafting table and some sticks.',
    'GregTech is gentle here: wooden and stone tools work as usual. It starts asking more of you once you have iron.',
    '> Check this off once you have a crafting table.',
], ['begin'], at=(-3, -3), kind='tip')
q('flint', 'Flint', ['minecraft:flint*4'], [
    'Dig gravel. Flint is the first thing GregTech really wants: the &eFlint Mortar&r is your first GregTech tool and it needs two.',
    'Keep a stack. Flint dust goes into glass, and flint tools are a fine stopgap.',
], ['wood'], at=(-4.5, -3))
q('backpack', 'Somewhere to Put It All', ['sophisticatedbackpacks:backpack'], [
    'GregTech has a lot of items. A lot. Four leather, a chest and string make a &eBackpack&r.',
    'Upgrade it with iron, gold, diamond and netherite as you go. The &ePickup&r, &eMagnet&r, &eFilter&r and &eCrafting&r upgrades are all worth their cost.',
], ['wood'], at=(-3, -1.5))
q('food', 'Something Better Than Bread', ['farmersdelight:cooking_pot'], [
    'Farmer\'s Delight\'s &eCooking Pot&r (on a campfire or a &eStove&r) turns a few crops into meals that heal a lot and give buffs.',
    'You will be underground a lot. Good food keeps you there longer. Varied food is better food.',
], ['wood'], at=(-4.5, -1.5), kind='opt')
q('claim', 'Claim Your Land', ['check'], [
    'Open the map (&eM&r by default) and drag over chunks to claim them. Claimed chunks are safe from other players and from explosions.',
    'Later you can &eforce-load&r claimed chunks so your machines keep running while you are away. GregTech machines only work in loaded chunks.',
], ['begin'], at=(-3, 0), kind='tip')
q('map', 'Never Get Lost', ['check'], [
    'Xaero\'s Minimap and World Map are in the pack. Press &eB&r to add a waypoint where you stand. Put one on every ore vein you find: you will want to come back.',
    'Your death point gets a waypoint automatically.',
], ['claim'], at=(-4.5, 0), kind='tip')
q('grave', 'Death Is Not the End', ['check'], [
    'When you die your items go into a &egrave&r where you fell. Walk back and open it to get everything back, armor and all.',
    'Die in lava and the grave still appears on solid ground nearby.',
], ['begin'], at=(-3, 1.5), kind='tip')
q('compass', 'Point Me There', ['naturescompass:naturescompass'], [
    'The &eNature\'s Compass&r finds any biome. Its partner, the &eExplorer\'s Compass&r (cobwebs and cracked stone bricks), finds any structure: dungeons, villages, towers.',
    'Rubber trees grow in forests, swamps and jungles. A compass to a swamp is a good first use.',
], ['grave'], at=(-4.5, 1.5), kind='opt')
q('night', 'Not All Monsters Are Equal', ['check'], [
    'Some monsters spawn as &eElite&r, &6Ultra&r or &4Infernal&r. Their name tells you. They have far more health and nasty extra abilities, but they also drop far better loot.',
    'Early on, run. Later you will build a machine that farms them on purpose.',
], ['begin'], at=(-3, 3), kind='tip')

# ---------------------------------------------------------------- GregTech tools
q('tools', '&eGregTech Tools', ['gtceu:iron_hammer'], [
    'GregTech tools are &ecrafting tools&r. You put them in the crafting grid with your materials and they lose a little durability instead of being used up.',
    'The &eHammer&r is the first one you need: one hammer and two ingots make a &eplate&r, and almost every GregTech recipe starts with plates.',
    'Tools can be made from any metal: iron, bronze, steel, later much better ones. Better metal, more durability. Iron is fine for now.',
    '> A hammer also mines like a pickaxe, slowly. Do not use it as one.',
], ['begin'], at=(4, 0), kind='hub')
q('mortar', 'Flint Mortar', ['gtceu:flint_mortar'], [
    'Two flint and stone. The mortar grinds one ingot, gem or brick into one &edust&r in the crafting grid.',
    'That matters because GregTech mixes alloys from dusts: three copper dust and one tin dust make four &ebronze dust&r right in the grid. No machine needed.',
    'It also turns bricks into brick dust and clay into clay dust, which you will want for fireclay soon.',
], ['tools'], at=(2.5, -2.5))
q('file', 'File', ['gtceu:iron_file'], [
    'Files shape metal into &erods&r, and turn bolts into &escrews&r. Rods and screws are in half the recipes after plates.',
], ['tools'], at=(4, -2.5))
q('wrench', 'Wrench', ['gtceu:iron_wrench'], [
    'The wrench is the right tool for mining GregTech machines. Right-click a side to rotate a machine or set the side it outputs to.',
    'Pipes and cables: right-click with a wrench to connect or disconnect a side.',
], ['tools'], at=(5.5, -2.5))
q('saw', 'Saw', ['gtceu:iron_saw'], [
    'A saw cuts logs into more planks than your hands can, cuts long rods in half, and makes the &eEmpty Wooden Form&r from planks.',
], ['tools'], at=(6.5, -1))
q('screwdriver', 'Screwdriver', ['gtceu:iron_screwdriver'], [
    'Needed in rotor, primitive pump and many machine recipes. On a machine, the screwdriver toggles settings like auto-output; on covers it opens their options.',
], ['tools'], at=(6.5, 1))
q('knife', 'Knife', ['gtceu:iron_knife'], [
    'Mostly for one thing right now: carving an &eEmpty Wooden Form&r into a &eBrick Wooden Form&r. Any GregTech knife works.',
], ['tools'], at=(5.5, 2.5))
q('cutter', 'Wire Cutter', ['gtceu:iron_wire_cutter'], [
    'A plate and a wire cutter make a &e1x wire&r. Copper wire goes into resistors, vacuum tubes and your first motors; tin wire becomes cable.',
    'Wire is lossy and shocks you. Add a rubber sheet in the grid to make insulated &ecable&r.',
], ['tools'], at=(4, 2.5))
q('plates', '&ePlates, Rods, Screws', ['gtceu:iron_plate*8', 'gtceu:iron_rod*4', 'gtceu:iron_screw*2'], [
    'The basic shapes of GregTech. By hand, two ingots make one plate: that is expensive, and on purpose. Machines later make one plate per ingot.',
    'Check any recipe with &eR&r: if it wants a shape you do not know how to make, look that shape up too. The recipe viewer is your best friend.',
], ['file', 'wrench', 'saw', 'screwdriver'], at=(8.5, 0), kind='main')
q('tinkers', 'The Smeltery', ['tconstruct:smeltery_controller'], [
    'Tinkers\' Construct is in the pack and its smeltery works early: seared bricks from &eGrout&r (clay, sand, gravel), a controller, a drain, a faucet and casting tables.',
    'Casting tables can pour plates, rods and gears, though here they cost at least as much metal as GregTech does. Where the smeltery shines is &etools&r: Tinkers\' tools are the only modular tools in the pack and they get very good.',
], ['plates'], at=(8.5, 2.5), kind='opt')

# ---------------------------------------------------------------- ore veins
q('veins', '&eHow Ores Work Here', ['check'], [
    'There are no scattered ores. GregTech ore comes in &eveins&r: big mixed clusters a few chunks apart, each with a fixed set of related ores at a fixed depth.',
    'Look for &esurface rocks&r: small stones on the ground with an ore\'s colour. They sit right above a vein. Dig straight down and you will hit it.',
    'Mine every block of a vein. A vein is hundreds of ores, and every ore type in it is useful sooner or later.',
    'Mined ore drops &eraw ore&r. Smelting it gives one ingot. Later, crushing and washing it gives two or more, plus byproducts. Early on, just smelt what you need.',
], ['tools'], at=(12, -4), kind='hub')
q('copper_tin', 'Copper and Tin', ['gtceu:raw_chalcopyrite*16', 'gtceu:raw_cassiterite*16'], [
    'The &eCopper-Tin vein&r: Chalcopyrite (copper), Zeolite, Cassiterite (tin) and a little Realgar. It spawns in stone anywhere from Y -10 to 160, so you often find it on the surface or in a cave wall.',
    'You need lots of both: bronze is three parts copper to one part tin.',
    '> Vanilla copper ore also generates in the deep copper vein (Chalcopyrite, Iron, Pyrite, Copper) between Y -40 and 10.',
], ['veins'], at=(10, -6.5))
q('iron', 'Iron', ['gtceu:raw_goethite*16'], [
    'The &eIron vein&r: Goethite, Limonite, Hematite and Malachite, Y -10 to 60. All four smelt into iron (Malachite into copper).',
    'This is the most common vein. You will need a frankly ridiculous amount of iron.',
    '> The Magnetite vein (Magnetite, Vanadium Magnetite, Gold, Y 10 to 60) is the other iron source, and gold comes with it.',
], ['veins'], at=(12, -7))
q('coal', 'Coal', ['gtceu:raw_coal*32'], [
    'The &eCoal vein&r, Y 10 to 140. Coal fuels your furnaces now, bakes into coke soon, and steel needs it as the carbon.',
    'Charcoal works almost everywhere coal does if you have trees and no vein yet.',
], ['veins'], at=(14, -6.5))
q('galena', 'Lead and Silver', ['gtceu:raw_galena*16'], [
    'The &eGalena vein&r: Galena, Silver and Lead, Y -15 to 45. Galena smelts to lead, and gives silver when processed properly.',
    'Lead goes into &ePotin&r (for the Steam Grinder) and later batteries; &esilver plates&r make the solar boiler.',
], ['veins'], at=(15.5, -4))
q('nickel', 'Nickel', ['gtceu:raw_garnierite*8'], [
    'The &eNickel vein&r: Garnierite, Nickel, Cobaltite and Pentlandite, Y -10 to 60. Rarer than the others.',
    'One nickel and two iron make &eInvar&r. You need invar gears for the Steam Oven and many machines after.',
], ['veins'], at=(14, -2))
q('deep', 'Diamonds Before Machines', ['gtceu:raw_diamond*4'], [
    'The &eDiamond vein&r is deep: Graphite, Diamond and Coal in deepslate, Y -55 to -30.',
    'You need diamonds early: the steam &eMacerator&r and &eRock Crusher&r each take two. Bring a pickaxe that can mine deepslate and plenty of torches.',
    '> The Redstone vein (Redstone, Ruby, Cinnabar) is down there too, Y -65 to -10.',
], ['veins'], at=(12, -1.5))
q('salts', 'Salt and Sand Veins', ['check'], [
    'Two veins you will come back for: the &eSalts vein&r (Rock Salt, Salt, Lepidolite, Spodumene; Y 30 to 70) for sodium, lithium and chlorine later, and the &eGarnet Tin vein&r (Cassiterite Sand, Garnet Sand, Asbestos, Diatomite; Y 30 to 60), an easy extra source of tin.',
    'Mark them on your map when you see them. You do not need them yet.',
], ['veins'], at=(10, -2), kind='tip')

# ---------------------------------------------------------------- fire and clay
q('fire', '&eFire and Clay', ['minecraft:bricks*16'], [
    'Clay balls smelt into bricks, and four bricks make a brick block. You need brick blocks for every steam machine and the boiler.',
    'Find clay in rivers, swamps and lush caves. Take more than you think: the coke oven and fireclay eat it too.',
], ['tools'], at=(12, 4), kind='hub')
q('form', 'A Wooden Form', ['gtceu:brick_wooden_form'], [
    'Saw some planks into an &eEmpty Wooden Form&r, then carve it with a knife into a &eBrick Wooden Form&r. The form is a crafting tool: it is not used up.',
    'Clay balls, sand and the form make &eCompressed Coke Clay&r.',
], ['fire', 'knife'], at=(10, 2.5))
q('coke_brick', 'Coke Oven Bricks', ['gtceu:coke_oven_bricks*29'], [
    'Smelt Compressed Coke Clay into Coke Oven Bricks (the item), and craft four into a block. A Coke Oven is a hollow 3x3x3 cube: &e25 brick blocks&r for the walls, and the controller in the middle of one side takes 4 more.',
], ['form'], at=(10, 4.5))
q('coke_oven', '&eCoke Oven', ['gtceu:coke_oven'], [
    'Your first multiblock. It bakes &ecoal into coke&r and &elogs into charcoal&r, slowly, with no fuel at all, and fills itself with &eCreosote&r while it works.',
    'Coke burns twice as long as coal and makes steel faster. Build two or three ovens: they are cheap and they never stop.',
    'Right-click the controller to see the preview. Every GregTech multiblock shows its shape there, layer by layer.',
], ['coke_brick'], at=(12, 6.5), kind='main')
q('hatch', 'Coke Oven Hatch', ['gtceu:coke_oven_hatch'], [
    'Swap one wall block for a &eCoke Oven Hatch&r and hoppers and pipes can feed coal in and pull coke and creosote out. Automation, Stone Age style.',
    'You also need one for the &eBrick Kiln&r.',
], ['coke_oven'], at=(14, 8))
q('creosote', 'Creosote', ['gtceu:creosote_bucket'], [
    'Pull it out of the coke oven with a bucket. Creosote soaks planks into &eTreated Wood&r and is a fine fuel for liquid boilers.',
], ['coke_oven'], at=(14, 5))
q('treated', 'Treated Wood', ['gtceu:treated_wood_planks*8'], [
    'Eight planks around a creosote bucket. Treated wood makes the &ePrimitive Pump&r, wooden fluid pipes and rubber-free item holders.',
], ['creosote'], at=(15.5, 5), kind='opt')
q('coke', 'Coke', ['gtceu:coke_gem*32'], [
    'Coke: better fuel, and the best carbon for steel in the Primitive Blast Furnace. Keep your coke ovens running from now until the end of LV.',
], ['coke_oven'], at=(15.5, 6.5))
q('wrought', 'Wrought Iron', ['gtceu:wrought_iron_ingot*8'], [
    'Smelt &eiron nuggets&r and you get &ewrought iron nuggets&r. Nine make an ingot. It is purer iron: GregTech wants it for the Brick Kiln, steam machine hulls, the LV machine hull and wrought iron pipes.',
    'Annoying to make by hand. The Primitive Blast Furnace also turns wrought iron into steel faster than plain iron.',
], ['fire'], at=(13.5, 2.5))
q('kiln', 'Brick Kiln', ['gtna:brick_kiln'], [
    'A primitive multiblock that fires &ebricks, coke oven bricks and firebricks&r from their compressed clays and coal, many at once, and makes &eClay Compound&r from clay, stone and flint dust.',
    'Clay compound becomes the plates and gears of the &eHydraulic&r parts in the Steam Age. You will want this soon.',
], ['hatch'], at=(16, 8))
q('stone_furnace', 'Primitive Stone Furnace', ['gtna:primitive_stone_furnace'], [
    'Eight stone around a furnace makes the controller of a small multiblock that smelts like a furnace, with no fuel at all. Free smelting for the hundreds of raw ores a vein gives.',
], ['fire'], at=(10, 6.5), kind='opt')
q('iron_furnace', 'Faster Furnaces', ['ironfurnaces:iron_furnace'], [
    'Iron Furnaces: copper, iron, gold, diamond and up. Each tier smelts faster and uses less fuel. Craft the copper or iron one now and upgrade it in place later.',
], ['stone_furnace'], at=(10, 8), kind='opt')
q('chests', 'Bigger Chests', ['ironchest:iron_chest'], [
    'Iron Chests: copper, iron, gold, diamond... Upgrade a chest in place without emptying it using the upgrade items. Sophisticated Storage barrels and chests are the other option, with upgrades like compacting and voiding.',
], ['iron_furnace'], at=(11.5, 8), kind='opt')

# ---------------------------------------------------------------- bronze and the way out
q('resin', 'Sticky Resin', ['gtceu:sticky_resin*8'], [
    'Rubber trees grow in forests, swamps and jungles. Breaking naturally grown rubber logs sometimes drops &eSticky Resin&r. Plant the saplings: you will want a farm.',
    'Resin makes the &eResin Circuit Board&r and your first resistors, and it is the start of rubber. Slimeballs also smelt into resin.',
], ['veins'], at=(17, -6.5))
q('bronze', '&6Bronze', ['gtceu:bronze_ingot*16'], [
    'Grind copper and tin with the mortar, mix &e3 copper dust + 1 tin dust&r into 4 bronze dust in the grid, and smelt.',
    'Bronze is the metal of the whole Steam Age: plates, pipes, casings, gears. You need a lot, so mine a lot of copper.',
    '> Once you have a steam Alloy Smelter it makes bronze straight from ingots, no grinding needed.',
], ['copper_tin'], at=(18, -2.5), kind='main')
q('bronze_tools', 'Bronze Tools', ['gtceu:bronze_hammer', 'gtceu:bronze_wrench'], [
    'Bronze tools last longer than iron ones. If your iron hammer is about to break, make the next one bronze.',
], ['bronze'], at=(18, -4.5), kind='opt')
q('casing', 'Bronze Casings', ['gtceu:bronze_brick_casing', 'gtceu:bronze_machine_casing'], [
    'Steam machines are built around two casings: the &eBricked Bronze Casing&r (bronze plates and bricks) for heat machines like the alloy smelter and furnace, and the &eBronze Machine Casing&r (eight plates) for the rest.',
], ['bronze'], at=(18, 0.5))
q('pipe', 'Bronze Fluid Pipes', ['gtceu:bronze_small_fluid_pipe*8'], [
    'A bronze plate, a wrench and a hammer make a Small Bronze Fluid Pipe. Steam machines need them in their recipes, and they carry steam from your boiler to your machines.',
], ['bronze'], at=(19.5, -1))
q('to_steam', '&6&lSteam Power', ['gtceu:lp_steam_solid_boiler'], [
    'Bronze plates, bricks and a furnace: the &eLow Pressure Steam Solid Boiler&r. Burn coal, coke, charcoal or wood in it, give it water, and it makes steam for every steam machine.',
    'This is the end of the Stone Age. Open &6Tier 0.5 - Steam Age&r.',
    '> Read the Steam Age\'s first quest before you place this. Boilers explode.',
], ['casing', 'pipe'], at=(21.5, 0.5), kind='hub', bag='steam')

# ---------------------------------------------------------------- layout
POS = {
    'fire': (5, 5), 'form': (3, 4.3), 'coke_brick': (3, 5.8), 'coke_oven': (4.5, 7.5), 'creosote': (6.5, 7), 'treated': (8, 7),
    'coke': (6.5, 8.5), 'hatch': (4.5, 9.3), 'kiln': (6.5, 10), 'wrought': (7, 4.3),
    'stone_furnace': (1, 6), 'iron_furnace': (1, 7.5), 'chests': (1, 9),
    'veins': (12, -4), 'copper_tin': (14.5, -5.5), 'iron': (12, -7), 'coal': (10, -6.5), 'galena': (9.5, -4),
    'nickel': (10, -1.5), 'deep': (12, -1.2), 'salts': (14.3, -2.3), 'resin': (13.8, -8.3),
    'bronze': (17, -4), 'bronze_tools': (17, -6), 'casing': (17, -1), 'pipe': (18.7, -2.5), 'to_steam': (20.5, -1),
    'tinkers': (8.5, 2.3),
}
HIDE = set()
for _q in C.quests:
    k = _q.key.split('/', 1)[1]
    if k in POS: _q.x, _q.y = map(float, POS[k])
    if k in HIDE: _q.hide = True
