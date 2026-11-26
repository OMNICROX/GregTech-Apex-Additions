"""Reward tables: a 'pick one' choice list and a random loot bag per tier. Useful materials, never progression skips."""
from fw import table_hex, exists


class Flt(float):
    pass


MISSING = []

# choice lists: the player picks one entry
PICKS = {
    'pick_stone': ('Stone Age Supplies', 'minecraft:chest', [
        ('minecraft:iron_ingot', 12), ('minecraft:copper_ingot', 16), ('gtceu:tin_ingot', 8), ('minecraft:coal', 24),
        ('minecraft:torch', 32), ('minecraft:cooked_beef', 12), ('minecraft:leather', 8), ('minecraft:clay_ball', 32),
        ('minecraft:flint', 12), ('minecraft:string', 16)]),
    'pick_steam': ('Steam Age Supplies', 'gtceu:bronze_crate', [
        ('gtceu:bronze_ingot', 16), ('gtceu:steel_ingot', 6), ('gtceu:coke_gem', 24), ('gtceu:wrought_iron_ingot', 12),
        ('gtceu:sticky_resin', 12), ('minecraft:redstone', 24), ('minecraft:glass', 32), ('gtceu:silver_ingot', 6),
        ('gtceu:nickel_ingot', 6), ('gtceu:lead_ingot', 12), ('gtceu:rubber_plate', 6), ('minecraft:diamond', 1)]),
    'pick_lv': ('LV Supplies', 'gtceu:steel_crate', [
        ('gtceu:steel_ingot', 16), ('gtceu:red_alloy_ingot', 12), ('gtceu:tin_single_cable', 12), ('gtceu:rubber_plate', 12),
        ('gtceu:vacuum_tube', 4), ('gtceu:resistor', 4), ('gtceu:lead_ingot', 16), ('gtceu:silver_ingot', 8),
        ('gtceu:electrum_ingot', 6), ('gtceu:invar_ingot', 12), ('gtceu:sulfur_dust', 16), ('minecraft:diamond', 2)]),
    'pick_mv': ('MV Supplies', 'gtceu:aluminium_crate', [
        ('gtceu:aluminium_ingot', 16), ('gtceu:cupronickel_ingot', 16), ('gtceu:silicon_dust', 16), ('gtceu:electrum_ingot', 12),
        ('gtceu:good_electronic_circuit', 2), ('gtceu:diode', 8), ('gtceu:gallium_arsenide_dust', 2), ('gtceu:silicone_rubber_plate', 8),
        ('gtceu:polyethylene_plate', 8), ('gtceu:chromium_dust', 4), ('gtceu:manganese_dust', 4), ('minecraft:emerald', 4)]),
}

# loot bags: one random entry
BAGS = {
    'stone': ('Stone Age Bag', 'minecraft:bundle', [
        ('minecraft:iron_ingot', 16, 10), ('gtceu:tin_ingot', 12, 8), ('minecraft:copper_ingot', 16, 8), ('minecraft:coal', 32, 10),
        ('minecraft:bread', 16, 6), ('minecraft:torch', 32, 6), ('minecraft:flint', 8, 6), ('minecraft:clay_ball', 32, 6),
        ('gtceu:bronze_ingot', 8, 4), ('minecraft:cooked_beef', 16, 6), ('minecraft:redstone', 16, 4), ('gtceu:sticky_resin', 8, 4)]),
    'steam': ('Steam Age Bag', 'gtceu:bronze_crate', [
        ('gtceu:bronze_ingot', 24, 10), ('gtceu:steel_ingot', 8, 6), ('gtceu:coke_gem', 24, 8), ('gtceu:wrought_iron_ingot', 12, 6),
        ('gtceu:sticky_resin', 16, 6), ('minecraft:redstone', 32, 6), ('minecraft:glass', 32, 5), ('gtceu:bronze_plate', 16, 6),
        ('gtceu:red_alloy_ingot', 8, 4), ('gtceu:rubber_plate', 8, 4), ('gtceu:creosote_bucket', 1, 3), ('gtceu:compressed_fireclay', 16, 3),
        ('minecraft:diamond', 2, 2)]),
    'lv': ('LV Bag', 'gtceu:steel_crate', [
        ('gtceu:steel_ingot', 24, 10), ('gtceu:red_alloy_ingot', 16, 6), ('gtceu:tin_single_cable', 16, 6), ('gtceu:basic_electronic_circuit', 2, 4),
        ('gtceu:vacuum_tube', 8, 5), ('gtceu:resistor', 8, 5), ('gtceu:lv_electric_motor', 2, 3), ('gtceu:lv_electric_pump', 1, 2),
        ('gtceu:lv_conveyor_module', 1, 2), ('gtceu:rubber_plate', 16, 6), ('gtceu:lead_ingot', 16, 5), ('gtceu:silver_ingot', 8, 4),
        ('gtceu:lv_sodium_battery', 1, 2), ('gtceu:invar_ingot', 16, 4), ('gtceu:electrum_ingot', 8, 4)]),
    'mv': ('MV Bag', 'gtceu:aluminium_crate', [
        ('gtceu:aluminium_ingot', 24, 10), ('gtceu:cupronickel_ingot', 16, 6), ('gtceu:silicon_dust', 24, 6), ('gtceu:electrum_ingot', 12, 5),
        ('gtceu:good_electronic_circuit', 2, 4), ('gtceu:diode', 12, 5), ('gtceu:mv_electric_motor', 2, 3), ('gtceu:mv_electric_pump', 1, 2),
        ('gtceu:mv_conveyor_module', 1, 2), ('gtceu:silicone_rubber_plate', 8, 4), ('gtceu:polyethylene_plate', 8, 4), ('gtceu:gallium_arsenide_dust', 2, 3),
        ('gtceu:chromium_dust', 6, 3), ('gtceu:manganese_dust', 6, 3), ('gtceu:silicon_wafer', 4, 2), ('minecraft:emerald', 6, 3)]),
}


def tables():
    out = {}
    i = 0
    for key, (title, icon, items) in PICKS.items():
        rew = []
        for it, n in items:
            if not exists(it): MISSING.append(('pick', key, it)); continue
            rew.append({'item': it, 'count': n, 'weight': Flt(1)} if n > 1 else {'item': it, 'weight': Flt(1)})
        out[key] = {'id': table_hex(key), 'title': title, 'icon': icon if exists(icon) else 'minecraft:chest', 'loot_size': 1,
                    'order_index': i, 'rewards': rew, 'use_title': True}
        i += 1
    for key, (title, icon, items) in BAGS.items():
        rew = []
        for it, n, w in items:
            if not exists(it): MISSING.append(('bag', key, it)); continue
            rew.append({'item': it, 'count': n, 'weight': Flt(w)} if n > 1 else {'item': it, 'weight': Flt(w)})
        out[key] = {'id': table_hex(key), 'title': title, 'icon': icon if exists(icon) else 'minecraft:chest', 'loot_size': 1,
                    'order_index': i, 'rewards': rew, 'use_title': True}
        i += 1
    return out
