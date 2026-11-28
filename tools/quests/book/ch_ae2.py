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


# ================================================================ more to do in AE2 and automation (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'ae2/' + key)
    x.deps += [d if '/' in d else 'ae2/' + d for d in deps if (d if '/' in d else 'ae2/' + d) not in x.deps]


q('meteor', 'Meteorites', ['ae2:meteorite_compass'], [
    'The compass points at the nearest meteorite. Inside is sky stone, certus quartz and a mysterious cube that breaks into the four inscriber presses.',
], ['start'])
q('presses', 'The Presses', ['ae2:silicon_press'], [
    'Silicon, logic, calculation, engineering. Copy a press in the inscriber with a block of iron, so you never have to find another.',
], ['meteor'])
q('charger', 'Charger', ['ae2:charger'], [
    'Charges certus quartz crystals. Charged certus, redstone and nether quartz dropped in water grow fluix crystals.',
], ['start'])
q('fluix', 'Fluix', ['ae2:fluix_crystal*8'], [
    'The purple crystal every cable and most machines are made of.',
], ['charger'])
q('processors', 'Processors', ['ae2:logic_processor*4', 'ae2:calculation_processor*4', 'ae2:engineering_processor*4'], [
    'Press a circuit from gold, certus or diamond, press silicon, then press the two together over redstone.',
    'Three inscribers in a row with hoppers between them do it without you.',
], ['inscriber', 'presses'])
also('network', 'processors')
q('cable', 'Cable', ['ae2:fluix_glass_cable*16'], [
    'Glass cable carries &e8 channels&r. Every device on the network that stores, moves or crafts uses one.',
], ['fluix'])
also('network', 'cable')
q('drive', 'ME Drive', ['ae2:drive'], [
    'Ten storage cells in one block.',
], ['network'])
also('cells', 'drive')
q('terminal', 'Terminal', ['ae2:terminal'], [
    'Everything in the network, in one searchable window.',
], ['network'])
also('crafting_terminal', 'terminal')
q('energy_cell', 'Energy Cell', ['ae2:energy_cell'], [
    'A buffer for the network. Big crafting jobs draw power in bursts; without a buffer the network browns out and restarts.',
], ['network'])
q('export_bus', 'Export Bus', ['ae2:export_bus'], [
    'Pushes the items you list out of the network into what it touches.',
], ['buses'])
q('storage_bus', 'Storage Bus', ['ae2:storage_bus'], [
    'Makes the inventory it touches part of the network: a super chest, a drawer, a quantum tank. The network sees it and uses it, nothing is moved.',
], ['buses'])
q('level_emitter', 'Level Emitter', ['ae2:level_emitter'], [
    'A redstone signal when the network has more (or less) than a number of an item. A machine controller cover reads it: "keep 256 of this in stock" without autocrafting.',
], ['buses'], kind='opt')
q('fluid_cell', 'Fluid Cells', ['ae2:fluid_storage_cell_1k'], [
    'Fluids are stored exactly like items, in their own cells.',
], ['cells'], kind='opt')
q('dense_cable', 'Smart and Dense Cable', ['ae2:fluix_smart_cable*8', 'ae2:fluix_covered_dense_cable*8'], [
    'Dense cable carries &e32 channels&r. Smart cable shows how many channels it is carrying.',
], ['controller'])
q('p2p', 'P2P Tunnels', ['ae2:me_p2p_tunnel*2'], [
    'A pair of tunnels carries a whole dense cable through one channel of another network. This is how a big base gets channels where it needs them.',
], ['dense_cable'], kind='opt')
q('cards', 'Upgrade Cards', ['ae2:speed_card*4'], [
    'Cards go into buses, interfaces and assemblers. Speed cards first.',
], ['buses'], kind='opt')
q('pattern_terminal', 'Pattern Encoding Terminal', ['ae2:pattern_encoding_terminal', 'ae2:blank_pattern*16'], [
    'A pattern is a recipe on a card. For a GregTech recipe use the &eprocessing&r mode: list what goes in and what comes out.',
    'The + button of the recipe viewer fills it in for you.',
], ['autocraft'])
q('assembler', 'Molecular Assembler', ['ae2:molecular_assembler'], [
    'Does crafting-table patterns. Put it against a pattern provider.',
], ['autocraft'])
q('cpu', 'Crafting CPU', ['ae2:crafting_unit', 'ae2:1k_crafting_storage'], [
    'Every running job needs a crafting CPU of its own: any block of crafting units with at least one storage in it.',
], ['autocraft'])
q('coprocessor', 'Co-Processors', ['ae2:crafting_accelerator*4'], [
    'Each co-processor in a CPU lets the job hand out one more task at a time.',
], ['cpu'], kind='opt')
q('wap', 'Wireless Access Point', ['ae2:wireless_access_point'], [
    'Gives wireless terminals their range. Boosters in it extend the range and the power draw.',
], ['controller'])
also('wireless', 'wap')
q('singularity', 'Singularity', ['ae2:condenser', 'ae2:singularity'], [
    'A &eMatter Condenser&r eats items and, with a storage component in it, turns 256,000 of them into a singularity.',
    'A singularity blown up next to ender dust becomes an entangled pair: one for each end of a quantum bridge.',
], ['controller'])
also('quantum', 'singularity')
q('ex_drive', 'Extended Drive', ['expatternprovider:ex_drive'], [
    'Twenty cells instead of ten.',
], ['ex_provider'], kind='opt')
q('ex_assembler', 'Extended Molecular Assembler', ['expatternprovider:ex_molecular_assembler'], [
    'Several crafts at a time in one block.',
], ['ex_provider'], kind='opt')
q('adv_provider', 'Advanced Pattern Provider', ['advanced_ae:adv_pattern_provider'], [
    'A pattern provider that can send each ingredient to a chosen side of the machine: the answer to machines that want items on one face and fluids on another.',
], ['adv_ae'])
q('reaction_chamber', 'Reaction Chamber', ['advanced_ae:reaction_chamber'], [
    'Advanced AE\'s own machine, for its quantum alloy and the parts built from it.',
], ['adv_ae'], kind='opt')
q('bulk_cell', 'Bulk Cell', ['megacells:bulk_item_cell'], [
    'One item type, without limit. The cell for cobblestone.',
], ['mega'], kind='opt')
q('data_model', 'Data Models', ['hostilenetworks:blank_data_model*4'], [
    'Use a blank model on a mob to bind it, then keep it in the Deep Learner while you kill that mob. The model levels up.',
], ['learner'])
also('sim', 'data_model')
q('loot_fab', 'Loot Fabricator', ['hostilenetworks:loot_fabricator'], [
    'Turns the predictions a Simulation Chamber makes into the mob\'s drops, the one you pick.',
], ['sim'])
