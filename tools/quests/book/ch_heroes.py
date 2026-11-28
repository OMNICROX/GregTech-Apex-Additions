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
