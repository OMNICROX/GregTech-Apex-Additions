"""Tier 4 - EV: titanium machines, nichrome and tungstensteel, nanoprocessors, the Alloy Blast Smelter, big engines,
the End, the Undergarden and Mars. Gates follow the pack's recipes."""
from fw import Chapter

C = Chapter('ev', '&5Tier 4 - EV', 'gtceu:ev_machine_hull', '', 5, '2,048 EU/t: extreme voltage', 'ev', 'pick_ev', xp_base=40)
C.derive = False
q = C.q

q('start', '&5&lTier 4: EV', ['gtceu:ev_machine_hull'], [
    'Welcome to &5EV&r: 2,048 EU/t per amp.',
    'Single machines matter less from here. This chapter is about three metals and one circuit:',
    '&e1.&r &eNeodymium&r for motors and &eplatinum&r for sensors, coils and SMD diodes.',
    '&e2.&r &eNichrome coils&r (3,600 K) and &etungstensteel&r, the IV metal.',
    '&e3.&r &eNanoprocessors&r on epoxy boards.',
    '> You will leave the Overworld for most of it: the End, the Undergarden and Mars.',
], ['hv/ev'], kind='hub', guide='progression/04-ev.md')

# ---------------------------------------------------------------- components
q('neodymium', 'Neodymium', ['gtceu:neodymium_ingot*8'], [
    'EV and IV motors use a &emagnetic neodymium rod&r (HV Polarizer). Neodymium comes from &ebastnasite&r and &emonazite&r: the Nether has some, the Undergarden has whole veins.',
    '> The electromagnetic separator pulls extra neodymium out of their purified dusts.',
], ['start'], guide='lines/rare-earths.md')
q('motor', '&eEV Motor', ['gtceu:ev_electric_motor'], [
    'Kanthal wire, titanium rods, a magnetic neodymium rod and aluminium cable.',
], ['neodymium'], kind='main')
q('pump', 'EV Pump', ['gtceu:ev_electric_pump'], [
    'A titanium fluid pipe, a stainless steel rotor and &estyrene-butadiene rubber&r rings. Plain rubber no longer works here.',
], ['motor'])
q('conveyor', 'EV Conveyor', ['gtceu:ev_conveyor_module'], [
    'Two motors in &esilicone rubber&r sheets.',
], ['motor'])
q('piston', 'EV Piston and Robot Arm', ['gtceu:ev_electric_piston', 'gtceu:ev_robot_arm'], [
    'Titanium plates, rods and a small gear; the arm adds two motors and an EV circuit.',
], ['motor'])
q('platinum', 'First Platinum', ['gtceu:platinum_ingot*8'], [
    'EV emitters and sensors need platinum rods, the EV voltage coil and SMD diodes need fine platinum wire.',
    'Early sources: &eSheldonite&r (Cooperite) ore in the End, or bathing crushed &enickel&r ore in mercury (70% platinum dust).',
    '> The full platinum group line is an IV job. For now you only need the metal itself.',
], ['start'], guide='gregtech/ore-processing.md')
q('emitter', 'Emitters and Sensors', ['gtceu:ev_emitter', 'gtceu:ev_sensor'], [
    'Platinum rods, a &eQuantum Eye&r and EV circuits.',
], ['platinum'])
q('wither', 'The Wither', ['minecraft:nether_star'], [
    'EV field generators are built around a &eNether Star&r, and IV and LuV ones around stars bathed in radon. You will need many.',
    '> The Extreme Entity Crusher does not farm bosses. Build a Wither killer, or fight.',
], ['start'])
q('field_gen', '&eEV Field Generator', ['gtceu:ev_field_generator'], [
    'A Nether Star, double titanium plates, EV circuits and quadruple uranium triplatinum wire.',
], ['wither', 'emitter'], kind='main')

# ---------------------------------------------------------------- power
q('mpic', 'MPIC Chips', ['gtceu:mpic_chip*8'], [
    'Power ICs for EV: engraved wafers cut at EV.',
], ['start'])
q('energy_hatch', '&eEV Energy Hatch', ['gtceu:ev_energy_input_hatch*2'], [
    'An EV hull, aluminium cable, an MPIC chip and an &eExtreme Voltage Coil&r (a magnetic neodymium rod wound in fine platinum wire).',
    'Two of them run &9IV recipes&r in a multiblock.',
], ['mpic', 'platinum', 'neodymium'], kind='main', guide='gregtech/oc/tier-skipping.md')
q('lce', '&eLarge Combustion Engine', ['gtceu:large_combustion_engine'], [
    'An EV hull, pistons, motors, titanium gears and an &9IV circuit&r. Burns diesel-type fuels with lubricant for 2,048 EU/t.',
    'Feed it oxygen and it makes three times the power from twice the fuel. Cetane-boosted diesel is the best fuel.',
], ['piston', 'mainframe'], kind='main', guide='gregtech/power/generation-and-consumption.md')
q('gas_turbine', 'Large Gas Turbine', ['gtceu:gas_large_turbine'], [
    'The rotor-based turbine for refinery gas, methane and LPG. Stainless steel gears and EV circuits.',
], ['start'], kind='opt', guide='gtmultis/large_turbines.md')
q('lapotron', 'Lapotron Crystal', ['gtceu:lapotron_crystal'], [
    '25M EU in one slot. Lapotron crystals also fill the EV Lapotronic Capacitors of the Power Substation.',
], ['start'], kind='opt', guide='gregtech/power/batteries.md')

# ---------------------------------------------------------------- coils and alloys
q('nichrome', 'Nichrome', ['gtceu:nichrome_ingot*16'], [
    'Nickel and chrome, 4:1. It melts at 2,700 K, so kanthal coils are just enough.',
], ['start'])
q('nichrome_coils', '&eNichrome Coils', ['gtceu:nichrome_coil_block*16'], [
    'Nichrome wire, stainless steel foil and molten aluminium. The blast furnace reaches &e3,600 K&r.',
], ['nichrome'], kind='main', guide='gregtech/ebf-and-coils.md')
q('abs', '&e&lAlloy Blast Smelter', ['gtceu:alloy_blast_smelter'], [
    'An EV Alloy Smelter wrapped in tantalum carbide plates, with EV circuits. It takes coils, like the EBF.',
    'It melts the &odusts&r of an alloy straight into molten alloy, skipping the mixer. Cast the result into ingots in the Vacuum Freezer with an ingot mold.',
    '> Most alloys from here on are meant to be made this way.',
], ['motor', 'nichrome_coils'], kind='hub', guide='gtmultis/alloy_blast_smelter.md')
q('end', '&dThe End', ['dim:minecraft:the_end'], [
    'GregTech\'s End veins hold &escheelite and tungstate&r (tungsten), &esheldonite&r (platinum and palladium), &enaquadah&r, chromite and pitchblende.',
    'Kill the dragon first, or mine around it.',
], ['start'], kind='main', guide='dimensions/the-end.md')
q('tungsten', 'Tungsten', ['gtceu:tungsten_dust*16'], [
    'Scheelite or tungstate dust with hydrochloric acid gives &etungstic acid&r; the electrolyzer splits it into tungsten.',
    'Mars has tungsten veins too.',
], ['end'], guide='lines/tungsten.md')
q('tungstensteel', '&9&lTungstensteel', ['gtceu:tungsten_steel_ingot*16'], [
    'Tungsten and steel, 1:1. It needs 3,000 K: nichrome coils, in the Alloy Blast Smelter or the EBF, then the Vacuum Freezer.',
    'The metal of IV: hulls, casings, rotors and pipes.',
], ['tungsten', 'abs'], kind='main', guide='lines/tungsten.md')
q('ti_glass', 'Titanium Glass', ['gtapexadditions:titanium_reinforced_borosilicate_glass*8'], [
    'The EV tier of multiblock glass.',
], ['start'], kind='opt', guide='gregtech/glass-tiers.md')

# ---------------------------------------------------------------- circuits
q('mainframe', '&eMicroprocessor Mainframe', ['gtceu:micro_processor_mainframe*2'], [
    'The top of the microprocessor line: an &9IV circuit&r from an aluminium frame, supercomputers, inductors, capacitors and RAM. An HV circuit assembler recipe.',
    'The Large Combustion Engine, the EV scanner and the large miner all ask for IV circuits.',
], ['start'], kind='main', guide='gregtech/circuits.md')
q('epoxy_board', 'Epoxy Circuit Boards', ['gtceu:epoxy_printed_circuit_board*8'], [
    'An epoxy sheet with gold foil, printed with electrum foil.',
], ['start'])
q('nano_cpu', 'Nano CPU Chips', ['gtceu:nano_cpu_chip*8'], [
    'A CPU wafer, &eraw carbon fibers&r and molten glowstone in an &5EV&r chemical reactor, then the cutter. Carbon fibers are spun from carbon dust and liquid plastic in the autoclave.',
], ['start'])
q('nano', '&eNanoprocessor', ['gtceu:nano_processor*8'], [
    'An epoxy board, a nano CPU chip and SMD parts, in an EV circuit assembler inside the Cleanroom. An &6HV&r circuit, eight per craft.',
], ['epoxy_board', 'nano_cpu'], kind='main', guide='gregtech/circuits.md')
q('nano_assembly', 'Nanoprocessor Assembly', ['gtceu:nano_processor_assembly*4'], [
    'The &5EV&r circuit of the nano line. Much cheaper than microprocessor supercomputers.',
], ['nano'])
q('nano_computer', 'Nanoprocessor Supercomputer', ['gtceu:nano_processor_computer*2'], [
    'An &9IV&r circuit. It needs &eNOR memory chips&r and SMD diodes.',
], ['nano_assembly'])

# ---------------------------------------------------------------- multiblocks
q('implosion', 'Implosion Compressor', ['gtceu:implosion_compressor'], [
    'Compresses gem dust into gems with explosives: TNT, dynamite or industrial TNT. Industrial diamonds, nether star dust back into stars, and many plates need it.',
], ['start'], kind='opt', guide='gtmultis/implosion_compressor.md')
q('miner', 'Large Miner', ['gtceu:ev_large_miner'], [
    'A multiblock miner that digs out a whole area and crushes the ore as it goes. Give it drilling fluid.',
], ['mainframe', 'conveyor'], kind='opt', guide='gtmultis/drilling.md')
q('gcym', 'Large Machines', ['gtceu:large_centrifuge'], [
    'The GCYM large machines are multiblock versions of the single-block machines, with parallels from a &eParallel Hatch&r. Each needs its own alloy casing from the Alloy Blast Smelter.',
], ['abs'], kind='opt', guide='gtmultis/gcym.md')

# ---------------------------------------------------------------- worlds and other mods
q('undergarden', '&2The Undergarden', ['dim:undergarden:undergarden'], [
    'A dark world reached with the Undergarden catalyst. Its veins hold &euranium and thorium&r, &erare earths&r, tantalite and cinnabar.',
], ['start'], kind='opt', guide='dimensions/undergarden.md')
q('mars', '&cMars', ['ad_astra:tier_2_rocket'], [
    'The Tier 2 Rocket is made of desh from the Moon. Mars has tungsten veins, and ostrum for the next rocket.',
    'The &eGravity Normalizer&r (EV) needs Aether gravitite.',
], ['motor'], kind='opt', guide='dimensions/space.md')
q('blood_orb', 'Master Blood Orb', ['gtapexadditions:raw_blood_orb_4'], [
    'The EV raw orb, built around a weak blood shard. See the &4Magic&r chapter.',
], ['start'], kind='opt', guide='magic/blood-magic.md')
q('quantum_ring', 'Quantum Network Bridge', ['ae2:quantum_ring*8', 'ae2:quantum_link'], [
    'AE2\'s quantum bridge, spatial storage and the big crafting accelerators are EV assembler recipes. See the &bAE2&r chapter.',
], ['start'], kind='opt', guide='automation/ae2.md')

q('iv', '&9&lOn to IV', ['gtceu:iv_machine_hull'], [
    'Tungstensteel plates, &ePTFE&r sheets and platinum cable: the &9IV Machine Hull&r.',
    'IV is 8,192 EU/t. It brings the platinum group, the Assembly Line and the first wave of Apex\'s own multiblocks. Open &9Tier 5 - IV&r.',
], ['tungstensteel', 'platinum'], kind='hub', bag='ev')


# ================================================================ more to do in EV (2026-11-28)
def also(key, *deps):
    x = next(x for x in C.quests if x.key == 'ev/' + key)
    x.deps += [d if '/' in d else 'ev/' + d for d in deps if (d if '/' in d else 'ev/' + d) not in x.deps]


q('ev_cables', 'EV Cables', ['gtceu:aluminium_single_cable*16'], [
    '&eAluminium&r carries EV. Gold and electrum stop at HV.',
], ['start'])
q('sensor', 'EV Sensor', ['gtceu:ev_sensor'], [
    'The pair to the emitter, as in every tier.',
], ['emitter'])
q('robot_arm', 'EV Robot Arm', ['gtceu:ev_robot_arm'], [
    'Motors, a piston, a circuit. The assembler upgrades of this tier want several.',
], ['motor', 'piston'])
q('ti_casing', 'Stable Titanium Casing', ['gtceu:stable_machine_casing*16'], [
    'Titanium\'s casing. Several EV multiblocks are built of it: look at a controller\'s preview before you craft a stack of the wrong one.',
], ['start'])
q('ti_boiler', 'Titanium Large Boiler', ['gtceu:titanium_large_boiler'], [
    'The third large boiler. Worth it only if steam is still part of your power.',
], ['ti_casing'], kind='opt')
q('ti_drum', 'Titanium Drum', ['gtceu:titanium_drum'], [
    'Bigger again.',
], ['start'], kind='opt')
q('superconductor', 'A Wire That Loses Nothing', ['gtceu:uranium_triplatinum_single_wire*8'], [
    'Every tier has a &esuperconductor&r: a wire with no loss over any distance and no insulation. This is EV\'s.',
    'They are expensive now and ordinary later: from LuV on, the best machines ask for them by name.',
], ['platinum'], kind='opt')
q('big_buses', 'Bigger Buses', ['gtceu:ev_input_bus', 'gtceu:ev_input_hatch'], [
    'A bus or hatch of a higher tier has more slots or a bigger tank. A multiblock that keeps stopping for input wants one.',
], ['start'], kind='opt')
q('large_macerator', 'Large Maceration Tower', ['gtceu:large_maceration_tower'], [
    'The large machines are the answer to "I need eight of these": one multiblock, &eparallel hatches&r, many recipes at once.',
], ['gcym'])
q('large_mixer', 'Large Mixer', ['gtceu:large_mixer'], [
    'More fluid and item inputs than any single mixer: the alloys ahead have six and more ingredients.',
], ['gcym'])
q('large_electrolyzer', 'Large Electrolyzer', ['gtceu:large_electrolyzer'], [
    'Bulk electrolysis. Bauxite, salt water and clay stop being a queue.',
], ['gcym'], kind='opt')
q('large_bath', 'Large Chemical Bath', ['gtceu:large_chemical_bath'], [
    'Ore washing and chemical bathing in one multiblock.',
], ['gcym'], kind='opt')
q('large_sifter', 'Large Sifting Funnel', ['gtceu:large_sifting_funnel'], [
    'Every gem ore you own, sifted at once.',
], ['gcym'], kind='opt')
q('ev_rig', 'A Deeper Rig', ['gtceu:ev_fluid_drilling_rig'], [
    'A higher tier of drilling rig pumps more per cycle from the same fluid vein.',
], ['pump'], kind='opt')
q('data_orb', 'Data Orb', ['gtceu:data_orb'], [
    'Holds what a scanner reads. You will meet it again when assembly line research starts.',
], ['mainframe'], kind='opt')
q('nanomuscle', 'NanoMuscle Suit', ['gtceu:nanomuscle_chestplate'], [
    'Powered armour: it takes damage out of its battery instead of out of you.',
], ['lapotron'], kind='opt')
