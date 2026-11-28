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
    'pick_hv': ('HV Supplies', 'gtceu:stainless_steel_crate', [
        ('gtceu:stainless_steel_ingot', 16), ('gtceu:kanthal_ingot', 8), ('gtceu:electrum_ingot', 16), ('gtceu:polyethylene_plate', 16),
        ('gtceu:advanced_integrated_circuit', 2), ('gtceu:silicon_wafer', 8), ('gtceu:chromium_ingot', 8), ('minecraft:gold_ingot', 16),
        ('gtceu:polytetrafluoroethylene_plate', 8), ('minecraft:ender_pearl', 8), ('gtceu:black_steel_ingot', 8), ('minecraft:diamond', 4)]),
    'pick_ev': ('EV Supplies', 'gtceu:titanium_crate', [
        ('gtceu:titanium_ingot', 16), ('gtceu:nichrome_ingot', 8), ('gtceu:tungsten_steel_ingot', 8), ('gtceu:platinum_ingot', 4),
        ('gtceu:micro_processor_computer', 2), ('gtceu:epoxy_plate', 16), ('gtceu:neodymium_ingot', 8), ('gtceu:aluminium_ingot', 32),
        ('gtceu:vanadium_steel_ingot', 8), ('gtceu:silicone_rubber_plate', 16), ('gtceu:nano_cpu_chip', 2), ('minecraft:nether_star', 1)]),
    'pick_iv': ('IV Supplies', 'gtceu:tungstensteel_crate', [
        ('gtceu:tungsten_steel_ingot', 16), ('gtceu:tungsten_ingot', 16), ('gtceu:platinum_ingot', 8), ('gtceu:palladium_ingot', 6),
        ('gtceu:nano_processor_computer', 2), ('gtceu:polytetrafluoroethylene_plate', 32), ('gtceu:iridium_ingot', 2), ('gtceu:rhodium_ingot', 2),
        ('gtceu:hssg_ingot', 8), ('gtceu:niobium_titanium_ingot', 8), ('gtceu:quantum_eye', 4), ('minecraft:nether_star', 2)]),
    'pick_luv': ('LuV Supplies', 'gtceu:tungstensteel_crate', [
        ('gtceu:rhodium_plated_palladium_ingot', 8), ('gtceu:hsss_ingot', 8), ('gtceu:iridium_ingot', 8), ('gtceu:osmium_ingot', 4),
        ('gtceu:naquadah_ingot', 8), ('gtceu:niobium_titanium_ingot', 16), ('gtceu:quantum_processor_computer', 1), ('gtceu:polybenzimidazole_plate', 16),
        ('gtceu:quantum_star', 1), ('gtceu:samarium_ingot', 4), ('gtceu:ruridit_ingot', 8), ('gtceu:indium_ingot', 4)]),
    'pick_zpm': ('ZPM Supplies', 'gtceu:tungstensteel_crate', [
        ('gtceu:naquadah_alloy_ingot', 16), ('gtceu:osmiridium_ingot', 8), ('gtceu:europium_ingot', 4), ('gtceu:enriched_naquadah_ingot', 8),
        ('gtceu:trinium_ingot', 4), ('gtceu:vanadium_gallium_ingot', 16), ('gtceu:crystal_processor_computer', 1), ('gtceu:polybenzimidazole_plate', 32),
        ('gtceu:quantum_star', 2), ('gtceu:naquadria_ingot', 4), ('gtceu:duranium_ingot', 8), ('gtceu:iridium_ingot', 16)]),
    'pick_uv': ('UV Supplies', 'gtceu:tungstensteel_crate', [
        ('gtceu:darmstadtium_ingot', 8), ('gtceu:tritanium_ingot', 8), ('gtceu:americium_ingot', 4), ('gtceu:naquadria_ingot', 8),
        ('gtceu:yttrium_barium_cuprate_ingot', 16), ('gtceu:europium_ingot', 8), ('gtceu:wetware_processor_assembly', 1), ('gtceu:trinium_ingot', 8),
        ('gtceu:gravi_star', 1), ('gtceu:neutronium_ingot', 2), ('gtceu:osmiridium_ingot', 16), ('gtceu:naquadah_alloy_ingot', 32)]),
    'pick_uhv': ('UHV Supplies', 'gtceu:tungstensteel_crate', [
        ('gtceu:neutronium_ingot', 8), ('gtceu:tritanium_ingot', 16), ('gtceu:americium_ingot', 8), ('gtceu:darmstadtium_ingot', 16),
        ('gtceu:gravi_star', 2), ('gtceu:europium_ingot', 16), ('gtceu:wetware_processor_computer', 1), ('gtceu:naquadria_ingot', 16),
        ('avaritia:neutron_ingot', 2), ('gtceu:trinium_ingot', 16)]),
    'pick_end': ('Endgame Supplies', 'gtceu:tungstensteel_crate', [
        ('gtceu:neutronium_ingot', 16), ('gtceu:gravi_star', 4), ('gtceu:tritanium_ingot', 32), ('gtceu:americium_ingot', 16),
        ('gtceu:darmstadtium_ingot', 32), ('gtceu:wetware_processor_mainframe', 1), ('avaritia:neutron_ingot', 4), ('gtceu:europium_ingot', 32)]),
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


for _t in ('hv', 'ev', 'iv', 'luv', 'zpm', 'uv', 'uhv', 'end'):
    _n, _ic, _it = PICKS['pick_' + _t]
    BAGS[_t] = (_n.replace('Supplies', 'Bag'), _ic, [(i, n * 2 if n < 16 else n, 10 - min(j, 7)) for j, (i, n) in enumerate(_it)])


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
