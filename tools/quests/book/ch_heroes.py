"""Heroes: the Palladium hero mods. Suits are made at their own benches; the circuits and machines behind them are
GregTech assembler recipes."""
from fw import Chapter

C = Chapter('heroes', '&cHeroes', 'satsu_iron_man_addon:arc_reactor', 'side', 23, 'Suits, powers and the machines behind them', None, None, xp_base=25)
C.derive = False
q = C.q

q('start', '&c&lHeroes', ['palladium:tailoring_bench'], [
    'Apex still has its heroes. Suits are sewn and built at each mod\'s own bench; the &ecircuits and machines&r behind them are GregTech assembler recipes, mostly at HV and EV.',
], ['stone/begin'], kind='hub', guide='lines/heroes.md')
q('suit_stand', 'Suit Stand', ['palladiumstands:oak_suit_stand'], [
    'Somewhere to hang a suit. Right-click with a full set to swap in and out.',
], ['start'], kind='opt')
q('spider', 'Spider-Man', ['powerborne:spiderman_suit'], [
    'Fabric, cobweb and radioactive silk at the Tailoring Bench. The silk comes from a radioactive spider.',
], ['start'], kind='opt')
q('moon_knight', 'The Archeology Table', ['pantheonsent:archeology_table'], [
    'A brush, a book and planks. Restore what you dig up in the desert, and Khonshu may notice you.',
], ['start'], kind='opt')
q('mark1', '&bMark I', ['satsu_iron_man_addon:marks/mark_01/mark_01_chest'], [
    'Iron, copper wire and basic circuits: the first Iron Man armour, built in a cave.',
], ['start', 'mv/robot_arm'], kind='main')
q('quartz_circuit', '&6Hero Circuits', ['palladium:quartz_circuit*4'], [
    'Palladium\'s quartz circuit is an &6HV&r assembler recipe. Flux capacitors and most hero machines are built on them.',
], ['start', 'hv/ic'], kind='main')
q('vibranium', 'Vibranium', ['palladium:vibranium_ingot*4'], [
    'From meteorites. Shields, suits and the vibranium circuit.',
], ['quartz_circuit'])
q('genetic', 'Genetic Chamber', ['powerborne:genetic_chamber'], [
    'Titanium, lead blocks, a quartz circuit and a vibranium flux capacitor. Powers written into your genes.',
], ['vibranium', 'hv/titanium'], kind='opt')
q('omnitrix_bench', 'Omnitrix Workbench', ['alienevo:omnitrix_workbench'], [
    'An &6HV&r assembler recipe with galvan scrap and an omni crystal.',
], ['quartz_circuit'], kind='main')
q('omnitrix', '&5Prototype Omnitrix', ['alienevo:prototype_omnitrix'], [
    'An &5EV&r assembler recipe: a galvan circuit, a galvan plate and the prototype core.',
], ['omnitrix_bench', 'ev/start'], kind='main')
q('arc', '&5Arc Reactor', ['satsu_iron_man_addon:arc_reactor'], [
    'An &5EV&r assembler recipe. The later Marks are built at the Stark and Gantry stations.',
], ['mark1', 'ev/start'], kind='main')
q('iterator', 'Multiversal Iterator', ['palladium:multiversal_iterator'], [
    'An &5EV&r assembler recipe around an end crystal and a vibranium circuit. It reads other universes for suit variants.',
], ['vibranium', 'ev/start'], kind='opt')
q('jet_suit', '&9Jet Suit', ['ad_astra:jet_suit'], [
    'Not a hero\'s, but it flies: calorite, a netherite space suit and Aether gravitite, in an &9IV&r assembler.',
], ['start', 'iv/start'], kind='opt', guide='dimensions/space.md')
q('wyvern', '&dWyvern Armour', ['draconicevolution:wyvern_chestpiece'], [
    'Draconic Evolution\'s modular armour starts at &dLuV&r, fusion-crafted.',
], ['start', 'luv/defc'], kind='opt', guide='lines/draconic-evolution.md')


# ================================================================ more to do in heroes (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'heroes/' + key)
    x.deps += [d if '/' in d else 'heroes/' + d for d in deps if (d if '/' in d else 'heroes/' + d) not in x.deps]


# ---------------------------------------------------------------- Palladium's parts
q('flux_crystal', 'Redstone Flux Crystals', ['palladium:redstone_flux_crystal*4'], [
    'They grow in geodes underground, like amethyst.',
], ['start'])
q('lead_circuit', 'Lead Circuit', ['palladium:lead_circuit'], [
    'The first of three tiers of hero circuit: lead, quartz, vibranium.',
], ['start'])
also('quartz_circuit', 'lead_circuit')
q('lead_capacitor', 'Flux Capacitor', ['palladium:lead_flux_capacitor'], [
    'A battery for powered suits and gadgets.',
], ['lead_circuit', 'flux_crystal'])
q('vib_circuit', 'Vibranium Circuit', ['palladium:vibranium_circuit'], [
    'The top tier.',
], ['vibranium'])
q('vib_capacitor', 'Vibranium Flux Capacitor', ['palladium:vibranium_flux_capacitor'], [
    'The biggest of the three capacitors.',
], ['vib_circuit'], kind='opt')
q('weave_boots', 'Vibranium Weave Boots', ['palladium:vibranium_weave_boots'], [
    'Boots woven with vibranium.',
], ['vibranium'], kind='opt')
q('herb', 'Heart-Shaped Herb', ['palladium:heart_shaped_herb'], [
    'Found, not crafted.',
], ['vibranium'], kind='opt')
q('extrapolator', 'Multiversal Extrapolator', ['palladium:multiversal_extrapolator'], [
    'The iterator\'s companion.',
], ['iterator'], kind='opt')

# ---------------------------------------------------------------- Powerborne
q('web_shooters', 'Web Shooters', ['powerborne:web_shooters'], [
    'The recipe viewer shows what they take. Powerborne\'s wiki (linked from the mod\'s page) covers how each hero\'s powers are gained.',
], ['spider'])
q('cap_suit', 'Captain America', ['powerborne:captain_america_suit'], [
    'The suit.',
], ['start'], kind='opt')
q('shield', 'Vibranium Shield', ['powerborne:vibranium_shield'], [
    'And the shield.',
], ['vibranium'])
q('uru', 'Uru', ['powerborne:uru_ingot*4'], [
    'The metal of Asgard\'s weapons.',
], ['start'])
q('stormbreaker', 'Stormbreaker', ['powerborne:stormbreaker'], [
    'Forged from uru.',
], ['uru'], kind='main')
q('superman', 'Superman', ['powerborne:superman_suit'], [
    'The suit.',
], ['start'], kind='opt')
q('sentry', 'Sentry', ['powerborne:sentry_suit'], [
    'The suit.',
], ['start'], kind='opt')
q('sun_totem', 'Sun Totem', ['powerborne:sun_totem'], [
    'A totem of the sun.',
], ['start'], kind='opt')
q('vials', 'Vials', ['powerborne:vial*4'], [
    'For the Genetic Chamber.',
], ['genetic'], kind='opt')
q('splicer', 'Quantum Splicer', ['powerborne:quantum_splicer'], [
    'The chamber\'s companion.',
], ['genetic'], kind='opt')

# ---------------------------------------------------------------- Moon Knight
q('lunar_stone', 'Lunar Stone', ['pantheonsent:lunar_stone'], [
    'From lunar shards.',
], ['moon_knight'])
q('ushabti', 'Khonshu\'s Ushabti', ['pantheonsent:khonshu_ushabti'], [
    'The broken one is found; the whole one is made.',
], ['moon_knight'])
q('lunar_totem', 'Lunar Totem', ['pantheonsent:lunar_totem'], [
    'Likewise.',
], ['lunar_stone'], kind='opt')
q('eye_of_horus', 'Eye of Horus', ['pantheonsent:eye_of_horus'], [
    'Likewise.',
], ['moon_knight'], kind='opt')
q('scarab', 'Scarab Compass', ['pantheonsent:scarab_compass'], [
    'Likewise.',
], ['moon_knight'], kind='opt')

# ---------------------------------------------------------------- Ben 10
q('galvan', 'Galvan Metal', ['alienevo:galvan_metal*4'], [
    'Made from galvan scrap.',
], ['start'])
q('machined', 'Machined Metal', ['alienevo:machined_metal*4', 'alienevo:machined_panel*2'], [
    'The plating of Galvan machines.',
], ['galvan'])
q('galvan_tools', 'Galvan Tools', ['alienevo:galvan_pickaxe'], [
    'A tool set in galvan metal.',
], ['galvan'], kind='opt')
q('upgrade_chip', 'Upgrade Chip', ['alienevo:upgrade_chip'], [
    'For the Omnitrix.',
], ['omnitrix'])
q('upgrade_crystal', 'Upgrade Crystal', ['alienevo:upgrade_crystal'], [
    'Sixteen colours of it.',
], ['omnitrix'], kind='opt')

# ---------------------------------------------------------------- Iron Man
q('im_circuits', 'Stark Circuits', ['satsu_iron_man_addon:basic_circuit', 'satsu_iron_man_addon:advanced_circuit'], [
    'The two circuits the armours are built from.',
], ['mark1'])
q('carbon', 'Carbon Mesh', ['satsu_iron_man_addon:carbon_mesh*4', 'satsu_iron_man_addon:high_density_carbon*2'], [
    'A material for the armours.',
], ['mark1'])
q('gantry', 'Gantry Station', ['satsu_iron_man_addon:gantry_station'], [
    'The armour\'s station.',
], ['im_circuits'])
q('mark2', 'Mark II', ['satsu_iron_man_addon:marks/mark_02/mark_02_black_suit'], [
    'The second armour.',
], ['arc', 'carbon', 'im_circuits'], kind='main')
q('mark3', 'Mark III', ['satsu_iron_man_addon:marks/mark_03/mark_03_black_suit'], [
    'Red and gold.',
], ['mark2'], kind='main')
q('mark5', 'Mark V', ['satsu_iron_man_addon:marks/mark_05/briefcase'], [
    'The one in the briefcase.',
], ['mark3'], kind='opt')
q('donut', 'A Donut', ['satsu_iron_man_addon:donuts/donut_chocolate'], [
    'Some things matter more than armour.',
], ['mark1'], kind='opt')

# ---------------------------------------------------------------- the Infinity Gauntlet
q('dwarf_star', 'Dwarf Star Alloy', ['infinity:dwarf_star_alloy_ingot*4'], [
    'The metal of the gauntlet. &eDwarf Star Meteors&r lie scattered across the Overworld: mine one for the raw material.',
], ['start'])
q('gauntlet', '&6Infinity Gauntlet', ['infinity:infinity_gauntlet'], [
    'Empty, it is a glove. The six stones are not crafted: each one sits in a structure, three in the Overworld and the End and three on planets of their own.',
    'Most of those structures have &efakes&r: the same building with nothing in it. An empty one means keep looking.',
], ['dwarf_star'], kind='main')
q('space_stone', 'Space Stone', ['infinity:tesseract'], [
    'Inside the &eTesseract&r, in the treasure room of a &eNorse Village&r in the Overworld.',
], ['gauntlet'])
q('time_stone', 'Time Stone', ['infinity:eye_of_agamotto'], [
    'Inside the &eEye of Agamotto&r, in a &eSanctum Sanctorum&r in the Overworld.',
], ['gauntlet'])
q('mind_stone', 'Mind Stone', ['infinity:scepter'], [
    'In the &eScepter&r, in the &eSanctuary&r. The Sanctuary is in the End.',
], ['gauntlet', 'worlds/end'])
q('power_stone', 'Power Stone', ['infinity:orb'], [
    'In the &eOrb&r, in a temple on &eMorag&r. Morag, Vormir and Svartelheim are planets of their own solar system and need a tier 4 rocket.',
], ['gauntlet', 'worlds/glacio'])
q('reality_stone', 'Reality Stone', ['infinity:aether'], [
    'The &eAether&r, in a chamber on &eSvartelheim&r. Malekith is in there with it.',
], ['gauntlet', 'worlds/glacio'])
q('soul_stone', 'Soul Stone', ['infinity:soul_stone'], [
    'On &eVormir&r, where the Soul Guardian waits. It has a price.',
], ['gauntlet', 'worlds/glacio'])
q('kree_ship', 'Crashed Kree Ship', ['infinity:cosmi-rod'], [
    'Wrecks in the plains of the Overworld. The &eCosmi-Rod&r is in the wreck.',
], ['start'], kind='opt')
q('temple', 'Infinity Temple', ['infinity:shards_of_existance'], [
    'Temples in the Overworld hold &eShards of Existence&r.',
], ['start'], kind='opt')
q('thanos', 'Dressed for It', ['infinity:thanos_mcu_chestplate'], [
    'The armour to go with the glove.',
], ['dwarf_star'], kind='opt')
