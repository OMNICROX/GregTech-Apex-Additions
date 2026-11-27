"""Which GuideNH DefaultGuide pages are imported, and where they go in the Apex Guide."""
SRC = '/home/claude/defaultguide/assets'
# source path (relative to assets/<mod>/guidenh/_en_us) -> target path in the Apex Guide
GT = {
    'gtnh_basics/multiblocks.md': 'gregtech/multiblock-basics.md',
    'power/power_index.md': 'gregtech/power/power-index.md',
    'power/voltage_tiers.md': 'gregtech/power/voltage-tiers.md',
    'power/generation_and_consumption.md': 'gregtech/power/generation-and-consumption.md',
    'power/cable.md': 'gregtech/power/cables.md',
    'power/cable_loss.md': 'gregtech/power/cable-loss.md',
    'power/output_loss.md': 'gregtech/power/output-loss.md',
    'power/distribution.md': 'gregtech/power/distribution.md',
    'power/enet.md': 'gregtech/power/enet.md',
    'tierskipping_overcloking_parallels/T_O_P_index.md': 'gregtech/oc/oc-index.md',
    'tierskipping_overcloking_parallels/overclocking.md': 'gregtech/oc/overclocking-rules.md',
    'tierskipping_overcloking_parallels/parallels.md': 'gregtech/oc/parallels.md',
    'tierskipping_overcloking_parallels/tierskipping.md': 'gregtech/oc/tier-skipping.md',
    'singleblock/singleblock_index.md': 'gregtech/singleblocks/singleblocks-index.md',
    'singleblock/rock_breaker.md': 'gregtech/singleblocks/rock-breaker.md',
    'singleblock/brewery.md': 'gregtech/singleblocks/brewery.md',
    'singleblock/bio_lab.md': 'gregtech/singleblocks/bio-lab.md',
    'multiblocks/multiblocks_index.md': 'gtmultis/gtmultis-index.md',
}
for src, dst in {
    'active_transformer': 'active_transformer', 'alloy_blast_smelter': 'alloy_blast_smelter',
    'assembly_line': 'assembly_line', 'bricked_blast_furnace': 'primitive_blast_furnace',
    'cleanroom': 'cleanroom', 'coke_oven': 'coke_oven', 'data_bank': 'data_bank',
    'distillation_tower': 'distillation_tower', 'electric_blast_furnace': 'electric_blast_furnace',
    'industrial_coke_oven': 'industrial_coke_oven', 'large_chemical_reactor': 'large_chemical_reactor',
    'lapotronic_supercapacitor': 'power_substation', 'mega_alloy_blast_smelter': 'mega_alloy_blast_smelter',
    'pyrolyse_oven': 'pyrolyse_oven', 'quantum_computer': 'hpca', 'research_station': 'research_station',
    'vacuum_freezer': 'vacuum_freezer', 'big_barrel_brewery': 'large_brewing_vat',
    'industrial_mixing_machine': 'large_mixing_vessel', 'hyper_intensity_laser_engraver': 'large_engraving_laser',
    'exxonmobil_chemical_plant': 'chemical_plant', 'eye_of_harmony': 'eye_of_harmony',
    'steam_grinder': 'large_steam_crusher', 'steam_blender': 'large_steam_mixer',
    'steam_presser': 'large_steam_forming_press', 'steam_squasher': 'large_steam_compressor',
    'steam_seperator': 'large_steam_centrifuge', 'steam_hearth': 'large_steam_furnace',
    'steam_fuser': 'large_steam_alloy_smelter', 'steam_purifier': 'large_steam_ore_washer',
}.items():
    GT[f'multiblocks/{src}.md'] = f'gtmultis/{dst}.md'
