"""GTNH-style loot bags: one reward table per tier, full of that tier's materials. Never progression skips."""
from core import *

# (item, count, weight)
BAGS = {
    'stone': ('Stone Age Bag', 'minecraft:bundle', [
        ('minecraft:iron_ingot', 16, 10), ('gtceu:tin_ingot', 12, 8), ('minecraft:copper_ingot', 16, 8), ('minecraft:coal', 32, 10),
        ('minecraft:bread', 16, 6), ('minecraft:torch', 32, 6), ('minecraft:flint', 8, 6), ('minecraft:clay_ball', 32, 6),
        ('gtceu:bronze_ingot', 8, 4), ('minecraft:cooked_beef', 16, 6), ('minecraft:redstone', 16, 4), ('gtceu:sticky_resin', 8, 4)]),
    'steam': ('Steam Age Bag', 'gtceu:bronze_crate', [
        ('gtceu:bronze_ingot', 24, 10), ('gtceu:steel_ingot', 8, 6), ('gtceu:coke_gem', 24, 8), ('gtceu:wrought_iron_ingot', 12, 6),
        ('gtceu:sticky_resin', 16, 6), ('minecraft:redstone', 32, 6), ('minecraft:glass', 32, 5), ('gtceu:bronze_plate', 16, 6),
        ('gtceu:red_alloy_ingot', 8, 4), ('gtceu:rubber_plate', 8, 4), ('gtceu:creosote_bucket', 1, 3), ('gtceu:compressed_fireclay', 16, 3)]),
    'lv': ('LV Bag', 'gtceu:steel_crate', [
        ('gtceu:steel_ingot', 24, 10), ('gtceu:red_alloy_ingot', 16, 6), ('gtceu:tin_single_cable', 16, 6), ('gtceu:basic_electronic_circuit', 4, 5),
        ('gtceu:vacuum_tube', 8, 5), ('gtceu:resistor', 8, 5), ('gtceu:lv_electric_motor', 2, 3), ('gtceu:lv_electric_pump', 1, 2),
        ('gtceu:lv_conveyor_module', 1, 2), ('gtceu:rubber_plate', 16, 6), ('gtceu:lead_ingot', 16, 5), ('gtceu:silver_ingot', 8, 4),
        ('gtceu:lv_sodium_battery', 1, 2), ('gtceu:invar_ingot', 16, 4), ('gtceu:electrum_ingot', 8, 4)]),
    'mv': ('MV Bag', 'gtceu:aluminium_crate', [
        ('gtceu:aluminium_ingot', 24, 10), ('gtceu:electrum_ingot', 16, 6), ('gtceu:cupronickel_ingot', 16, 5), ('gtceu:annealed_copper_ingot', 16, 5),
        ('gtceu:good_electronic_circuit', 4, 5), ('gtceu:mv_electric_motor', 2, 3), ('gtceu:mv_electric_pump', 1, 2), ('gtceu:mv_robot_arm', 1, 1),
        ('gtceu:polyethylene_plate', 16, 6), ('gtceu:silicon_ingot', 8, 4), ('gtceu:epoxy_plate', 8, 3), ('gtceu:mv_lithium_battery', 1, 2),
        ('gtceu:kanthal_coil_block', 2, 1), ('gtceu:vanadium_steel_ingot', 8, 3)]),
    'hv': ('HV Bag', 'gtceu:stainless_steel_crate', [
        ('gtceu:stainless_steel_ingot', 24, 10), ('gtceu:chromium_ingot', 12, 5), ('gtceu:kanthal_ingot', 12, 5), ('gtceu:micro_processor_assembly', 2, 4),
        ('gtceu:hv_electric_motor', 2, 3), ('gtceu:hv_electric_pump', 1, 2), ('gtceu:hv_robot_arm', 1, 1), ('gtceu:polytetrafluoroethylene_plate', 8, 4),
        ('gtceu:titanium_ingot', 4, 3), ('gtceu:energy_crystal', 1, 2), ('gtceu:quantum_star', 1, 1), ('gtceu:nichrome_coil_block', 2, 1),
        ('gtceu:hv_lithium_battery', 1, 2), ('gtceu:silver_ingot', 16, 4)]),
    'ev': ('EV Bag', 'gtceu:titanium_crate', [
        ('gtceu:titanium_ingot', 24, 10), ('gtceu:platinum_ingot', 12, 5), ('gtceu:nichrome_ingot', 12, 5), ('gtceu:vanadium_steel_ingot', 12, 4),
        ('gtceu:quantum_processor', 2, 4), ('gtceu:ev_electric_motor', 2, 3), ('gtceu:ev_electric_pump', 1, 2), ('gtceu:ev_robot_arm', 1, 1),
        ('gtceu:lapotron_crystal', 1, 2), ('gtceu:tungsten_ingot', 4, 3), ('gtceu:rtm_alloy_coil_block', 2, 1), ('gtceu:polytetrafluoroethylene_plate', 16, 4)]),
    'iv': ('IV Bag', 'gtceu:tungstensteel_crate', [
        ('gtceu:tungsten_steel_ingot', 24, 10), ('gtceu:tungsten_carbide_ingot', 12, 5), ('gtceu:hssg_ingot', 12, 5), ('gtceu:tungsten_ingot', 16, 5),
        ('gtceu:quantum_processor_assembly', 2, 4), ('gtceu:iv_electric_motor', 2, 3), ('gtceu:iv_electric_pump', 1, 2), ('gtceu:iv_robot_arm', 1, 1),
        ('gtceu:iridium_ingot', 4, 3), ('gtceu:hssg_coil_block', 2, 1), ('gtceu:lapotron_crystal', 2, 2)]),
    'luv': ('LuV Bag', 'gtceu:titanium_crate', [
        ('gtceu:rhodium_plated_palladium_ingot', 16, 8), ('gtceu:hsse_ingot', 16, 6), ('gtceu:iridium_ingot', 12, 5), ('gtceu:naquadah_ingot', 8, 4),
        ('gtceu:crystal_processor_assembly', 2, 4), ('gtceu:luv_electric_motor', 2, 3), ('gtceu:luv_electric_pump', 1, 2), ('gtceu:luv_robot_arm', 1, 1),
        ('gtceu:europium_ingot', 2, 2), ('gtceu:enriched_naquadah_ingot', 4, 3), ('gtceu:naquadah_coil_block', 2, 1)]),
    'zpm': ('ZPM Bag', 'gtceu:tungstensteel_crate', [
        ('gtceu:naquadah_alloy_ingot', 16, 8), ('gtceu:osmiridium_ingot', 16, 6), ('gtceu:darmstadtium_ingot', 8, 4), ('gtceu:trinium_ingot', 8, 4),
        ('gtceu:wetware_processor_assembly', 2, 4), ('gtceu:zpm_electric_motor', 2, 3), ('gtceu:zpm_electric_pump', 1, 2), ('gtceu:zpm_robot_arm', 1, 1),
        ('gtceu:enriched_naquadah_ingot', 12, 4), ('gtceu:trinium_coil_block', 2, 1), ('gtceu:gravi_star', 1, 1)]),
    'uv': ('UV Bag', 'gtceu:tungstensteel_crate', [
        ('gtceu:naquadria_ingot', 8, 6), ('gtceu:duranium_ingot', 12, 6), ('gtceu:darmstadtium_ingot', 16, 6), ('gtceu:americium_ingot', 8, 4),
        ('gtceu:wetware_processor_computer', 1, 4), ('gtceu:uv_electric_motor', 2, 3), ('gtceu:uv_electric_pump', 1, 2), ('gtceu:uv_robot_arm', 1, 1),
        ('gtceu:neutronium_ingot', 2, 2), ('gtceu:tritanium_coil_block', 1, 1), ('gtceu:gravi_star', 2, 2)]),
    'uhv': ('UHV Bag', 'gtceu:tungstensteel_crate', [
        ('gtceu:neutronium_ingot', 16, 8), ('gtceu:tritanium_ingot', 12, 6), ('gtceu:naquadria_ingot', 16, 5), ('gtceu:wetware_processor_mainframe', 1, 3),
        ('gtceu:uhv_electric_motor', 2, 3), ('gtceu:uhv_electric_pump', 1, 2), ('gtceu:uhv_robot_arm', 1, 1), ('gtceu:gravi_star', 4, 3),
        ('minecraft:nether_star', 4, 3)]),
    'uev': ('UEV Bag', 'gtceu:tungstensteel_crate', [
        ('gtceu:neutronium_ingot', 32, 8), ('gtna:infinity_ingot', 4, 4), ('gtna:optical_computer', 1, 3), ('gtceu:uev_electric_motor', 2, 3),
        ('gtceu:uev_electric_pump', 1, 2), ('gtceu:uev_robot_arm', 1, 1), ('minecraft:nether_star', 8, 4), ('gtceu:gravi_star', 8, 3)]),
    'uiv': ('UIV Bag', 'gtceu:tungstensteel_crate', [
        ('gtna:infinity_ingot', 16, 8), ('gtceu:neutronium_ingot', 64, 6), ('gtna:optical_mainframe', 1, 3), ('gtceu:uiv_electric_motor', 2, 3),
        ('gtceu:uiv_electric_pump', 1, 2), ('gtceu:uiv_robot_arm', 1, 1), ('minecraft:nether_star', 16, 4)]),
    'uxv': ('UXV Bag', 'gtceu:tungstensteel_crate', [
        ('gtna:infinity_ingot', 32, 8), ('gtna:magmatter_ingot', 2, 3), ('gtna:exotic_computer', 1, 3), ('gtceu:uxv_electric_motor', 2, 3),
        ('gtceu:uxv_electric_pump', 1, 2), ('gtceu:uxv_robot_arm', 1, 1), ('gtna:transcendent_metal_ingot', 8, 4), ('gtna:shirabon_ingot', 4, 3)]),
    'opv': ('OpV Bag', 'gtceu:tungstensteel_crate', [
        ('gtna:magmatter_ingot', 8, 6), ('gtna:infinity_ingot', 64, 6), ('gtna:exotic_mainframe', 1, 3), ('gtceu:opv_electric_motor', 2, 3),
        ('gtceu:opv_electric_pump', 1, 2), ('gtceu:opv_robot_arm', 1, 1), ('gtna:creon_ingot', 8, 3), ('gtna:mellion_ingot', 8, 3)]),
    'max': ('Ascended Bag', 'minecraft:nether_star', [
        ('gtna:magmatter_ingot', 64, 6), ('gtna:infinity_ingot', 64, 6), ('extendedfeatures:opv_universal_circuit', 4, 3), ('gtna:creon_ingot', 64, 3),
        ('gtna:mellion_ingot', 64, 3), ('minecraft:nether_star', 64, 4)]),
}
ORDER = list(BAGS)
TIER_BAG = ['steam', 'lv', 'mv', 'hv', 'ev', 'iv', 'luv', 'zpm', 'uv', 'uhv', 'uev', 'uiv', 'uxv', 'opv', 'max']   # bag for a GT tier number


def bag_for_tier(t):
    return 'stone' if t < 0 else TIER_BAG[min(t, 14)]


def tables():
    out = {}
    for i, (key, (title, icon, items)) in enumerate(BAGS.items()):
        rew = []
        for it, n, w in items:
            if not exists(it):
                MISSING.add(it)
                continue
            d = {'item': it, 'weight': Flt(w)} if n == 1 else {'count': n, 'item': it, 'weight': Flt(w)}
            rew.append(d)
        out[key] = {'id': table_hex(key), 'title': title, 'icon': icon if exists(icon) else 'minecraft:chest',
                    'loot_size': 1, 'order_index': i, 'rewards': rew}
    return out


class Flt(float):
    pass
