"""AE2 and Automation: Applied Energistics by tier, its add-ons, GregTech's ME hatches, and mob automation."""
from fw import Chapter

C = Chapter('ae2', '&bAE2 and Automation', 'ae2:controller', 'side', 22, 'Storage, autocrafting and mob farms', None, None, xp_base=25)
C.derive = False
q = C.q

q('start', '&b&lApplied Energistics', ['ae2:certus_quartz_crystal*8', 'ae2:charger'], [
    'AE2 works as it always does: hold &eG&r over any AE2 item for its own guide. What changes is &owhen&r: its machines are built in the GregTech assembler, at a tier.',
    'Certus quartz is a GregTech Nether vein. Charge it in the Charger.',
], ['lv/assembler'], kind='hub', guide='automation/ae2.md')

# ---------------------------------------------------------------- LV
q('inscriber', 'Inscriber', ['ae2:inscriber', 'ae2:fluix_crystal*8'], [
    'Presses come from meteorites. Fluix is charged certus, redstone and nether quartz in water.',
], ['start'])
q('network', '&7A First Network', ['ae2:energy_acceptor', 'ae2:drive', 'ae2:terminal'], [
    '&7LV.&r GregTech cables power the Energy Acceptor directly. Without a controller a network has 8 channels, enough for a drive and a terminal.',
], ['inscriber'], kind='main')
q('cells', '1k Storage', ['ae2:item_storage_cell_1k*4'], [
    'LV gets 1k cells only. One cell holds 63 types of item.',
], ['network'])
q('buses', 'Buses', ['ae2:import_bus', 'ae2:export_bus', 'ae2:storage_bus'], [
    'Import, export and storage buses are LV. A storage bus on a chest or drawer makes it part of the network.',
], ['network'])
q('crafting_terminal', 'Crafting Terminal', ['ae2:crafting_terminal'], [
    'A terminal with a crafting grid that pulls from the network.',
], ['network'], kind='opt')

# ---------------------------------------------------------------- MV
q('controller', '&bController', ['ae2:controller'], [
    '&bMV.&r 32 channels per face with dense cable. From here the network can grow.',
], ['network', 'mv/good_circuit'], kind='main')
q('autocraft', '&e&lAutocrafting', ['ae2:pattern_provider', 'ae2:molecular_assembler', 'ae2:crafting_unit'], [
    '&bMV.&r Pattern providers, molecular assemblers and a crafting CPU. A pattern provider on a GregTech machine sends it the ingredients; pipe or pull the results back.',
    '> Set programmed circuits on the machine, not in the pattern.',
], ['controller'], kind='hub', guide='gregtech/programmed-circuits.md')
q('bigger_cells', '4k and 16k Cells', ['ae2:item_storage_cell_4k', 'ae2:item_storage_cell_16k'], [
    'MV.',
], ['controller'])
q('interface', 'Interfaces and P2P', ['ae2:interface', 'ae2:me_p2p_tunnel'], [
    'Interfaces keep a stock of items in a machine or hand them to another network. P2P tunnels carry channels, items, fluids and even EU.',
], ['controller'])
q('subnet', 'Subnets', ['check'], [
    'A second small network that the main one sees through a storage bus on an interface. Use one to keep a wall of drives, or a processing line, off your main channels.',
], ['interface'], kind='tip', guide='automation/ae2.md')
q('requester', 'ME Requester', ['merequester:requester'], [
    'Keeps a set amount of an item in stock by ordering crafts on its own.',
], ['autocraft'], kind='opt')

# ---------------------------------------------------------------- HV+
q('me_hatches', '&6ME Hatches for GregTech', ['gtceu:me_input_bus', 'gtceu:me_output_bus', 'gtceu:me_input_hatch', 'gtceu:me_output_hatch'], [
    'Buses and hatches that sit on your network: a multiblock pulls its inputs straight from storage and returns outputs to it. Check JEI for the tier of each.',
], ['autocraft', 'hv/ic'], kind='main', guide='gregtech/multiblock-basics.md')
q('pattern_buffer', 'ME Pattern Buffer', ['gtceu:me_pattern_buffer'], [
    '&dLuV&r, from the Assembly Line. A pattern provider and input bus in one hatch. Each pattern keeps its own ingredients apart, so one multiblock runs many recipes without mixing them.',
], ['me_hatches'], kind='opt')
q('ex_provider', 'Extended Pattern Provider', ['expatternprovider:ex_pattern_provider'], [
    'ExtendedAE: 36 patterns in one block, and many more tools. HV with the rest of AE2\'s default tier.',
], ['me_hatches'], kind='opt')
q('wireless', 'Wireless Terminal', ['ae2wtlib:wireless_universal_terminal'], [
    'One terminal for crafting, patterns and more, anywhere in range of a wireless access point.',
], ['me_hatches'], kind='opt')
q('quantum', '&5Quantum Bridge and Spatial', ['ae2:quantum_ring*8', 'ae2:quantum_link'], [
    '&5EV.&r Join two networks across any distance or dimension. Spatial storage and the big crafting accelerators are EV too.',
], ['me_hatches', 'ev/start'], kind='main')
q('craft_center', '&9Extreme Craft Center', ['gtapexadditions:extreme_craft_center'], [
    '&9IV.&r Crafting patterns run inside a GregTech multiblock with pattern magnification.',
], ['quantum', 'iv/start'], kind='opt', guide='multiblocks/extreme_craft_center.md')
q('adv_ae', '&9Advanced AE', ['advanced_ae:quantum_core', 'advanced_ae:quantum_crafter'], [
    '&9IV.&r The quantum computer is a multiblock crafting CPU with huge storage and many co-processors.',
], ['quantum', 'iv/start'], kind='opt')
q('mega', '&cMEGA Cells', ['megacells:cell_component_1m', 'megacells:mega_crafting_unit'], [
    '&cZPM&r for 1M to 16M, &3UV&r for 64M and 256M.',
], ['adv_ae', 'zpm/start'], kind='main')
q('data_hub', '&4Singularity Data Hub', ['gtapexadditions:singularity_data_hub'], [
    '&4UHV.&r Bulk storage as a GregTech multiblock on the network.',
], ['mega', 'uhv/start'], kind='opt', guide='multiblocks/singularity_data_hub.md')

# ---------------------------------------------------------------- mobs
q('learner', '&7Hostile Neural Networks', ['hostilenetworks:deep_learner', 'hostilenetworks:blank_data_model'], [
    'Attune a blank model to a mob, carry it in the Deep Learner and kill that mob to train it. Hand-crafted at LV.',
], ['start'], kind='main', guide='automation/hostile-neural-networks.md')
q('sim', '&bSimulation Chamber', ['hostilenetworks:sim_chamber', 'hostilenetworks:prediction_matrix*16'], [
    'An &bMV&r assembler recipe. It runs a trained model on GregTech power and makes &epredictions&r; a GregTech extractor turns predictions into drops.',
], ['learner', 'mv/good_circuit'], kind='main', guide='automation/hostile-neural-networks.md')
q('crusher', '&bExtreme Entity Crusher', ['gtapexadditions:extreme_entity_crusher'], [
    'An &bMV&r multiblock mob farm. Put a spawn egg or a data model in its input bus and it rolls the mob\'s loot many times a cycle. Bosses are refused.',
], ['sim'], kind='hub', guide='multiblocks/extreme_entity_crusher.md')
q('rock_breaker', 'Industrial Rock Breaker', ['gtapexadditions:industrial_rock_breaker'], [
    'Cobblestone, stone, obsidian and basalt from lava and water, picked by programmed circuit. MV.',
], ['start', 'mv/good_circuit'], kind='opt', guide='multiblocks/industrial_rock_breaker.md')
q('upgrader', 'Structure Upgrader', ['gtapexadditions:structure_upgrader'], [
    'Builds and upgrades multiblocks from your inventory or your ME network: point it at a controller. MV.',
], ['start', 'mv/robot_arm'], kind='opt', guide='items/structure-upgrader.md')
