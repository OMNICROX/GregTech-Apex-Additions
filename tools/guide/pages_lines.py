from pages_core import page

page('lines/lines-index.md', 'Processing Lines', '''
# Processing Lines

The big chemical and material chains, step by step. JEI has every recipe; these pages tell you the order.

<SubPages />
''', pos=35, icon='gtceu:large_chemical_reactor')

def line(path, title, pos, icon, body):
    page('lines/' + path, title, body, parent='lines/lines-index.md', pos=pos, icon=icon)

line('rubber-and-plastics.md', 'Rubber and Plastics', 10, 'gtceu:lv_chemical_reactor', '''
# Rubber and Plastics

1. **Rubber**: sticky resin from rubber trees -> raw rubber pulp (extractor) -> rubber (with sulfur in a chemical reactor
   for bulk). Insulates LV-HV cables.
2. **Polyethylene** (MV): ethylene (from ethanol + sulfuric acid, or oil) + oxygen in a chemical reactor. Circuit boards
   and cable insulation from MV.
3. **Polytetrafluoroethylene** (HV-EV) and **polybenzimidazole** (IV+) for better boards and parts.
''')

line('oil.md', 'Oil Processing', 20, 'gtceu:distillation_tower', '''
# Oil Processing

1. Find oil with a prospector (fluid mode) and pump it, or make it from oil sands / bedrock fluid drills.
2. **Desulfurize** in a chemical reactor (hydrogen) and **distill** in the <ItemLink id="gtceu:distillation_tower" />.
3. Light fuel + heavy fuel -> **diesel**; with tetranitromethane -> **cetane-boosted diesel** (best LCE fuel).
4. Naphtha / gases -> ethylene, propene, benzene for plastics. Ad Astra rockets burn GregTech rocket fuel.
''')

line('titanium.md', 'Titanium', 30, 'gtceu:ev_machine_hull', '''
# Titanium (HV -> EV)

1. Rutile / ilmenite / bauxite byproducts -> **rutile dust**.
2. Rutile + carbon + chlorine -> **titanium tetrachloride** (chemical reactor).
3. Titanium tetrachloride + **magnesium** -> titanium sponge / hot titanium in the EBF; magnesium chloride gets
   electrolyzed back to magnesium + chlorine (loop it).
4. Cool hot ingots in the vacuum freezer. Magnesium comes from magnesite / olivine (Twilight Forest) or salt water.
''')

line('tungsten.md', 'Tungsten and Tungstensteel', 40, 'gtceu:iv_machine_hull', '''
# Tungsten and Tungstensteel (EV -> IV)

1. Scheelite / tungstate (End, Mars) -> tungstic acid with hydrochloric acid -> tungsten trioxide -> tungsten (EBF with hydrogen).
2. **Tungstensteel** = tungsten + steel (mixer) -> EBF at 3,000 K (Nichrome coils) -> vacuum freezer.
''')

line('platinum.md', 'Platinum Group', 50, 'gtceu:iv_machine_hull', '''
# Platinum Group (IV)

1. Platinum group sludge from sheldonite / cooperite (End) and from many ores' byproducts (nickel, copper, chalcopyrite...).
2. Sludge + aqua regia -> **platinum** and **palladium** salts -> metal; the residue gives **iridium**, **osmium**,
   **rhodium** and **ruthenium** through further chemistry.
3. Rhodium-plated palladium (LuV parts, LuV glass), iridium (ZPM), osmium (UV).
''')

line('rare-earths.md', 'Rare Earths and Nuclear', 60, 'gtceu:ev_chemical_reactor', '''
# Rare Earths and Nuclear (EV)

* **Rare earths** (bastnasite, monazite, neodymium) come from the Nether and the **Undergarden**: neodymium for magnets,
  lanthanum / cerium / yttrium / europium for superconductors and LuV+ parts.
* **Uranium and thorium** (pitchblende, uraninite: End, Undergarden): enriched uranium, plutonium; uranium-238
  centrifuges from Undergarden utherium.
''')

line('fusion.md', 'Fusion', 70, 'gtceu:luv_fusion_reactor', '''
# Fusion (LuV -> UV)

1. <ItemLink id="gtceu:luv_fusion_reactor" /> (MK-I), <ItemLink id="gtceu:zpm_fusion_reactor" /> (MK-II),
   <ItemLink id="gtceu:uv_fusion_reactor" /> (MK-III); Apex adds MK-IV (UHV) and MK-V (UEV) and the GoodGenerator
   Compact Fusion Computers MK-I to MK-V (parallels).
2. Each recipe needs a start-up energy (EU stored in the reactor): more energy hatches = more storage.
3. **Plasmas** (helium, oxygen, nitrogen, nickel, iron...) burn in the <ItemLink id="gtceu:plasma_large_turbine" />.
4. Fusion makes europium, americium, naquadria, duranium, tritanium and other late metals.
''')

line('naquadah.md', 'Naquadah', 80, 'gtceu:naquadah_coil_block', '''
# Naquadah (LuV+)

1. Naquadah ore (End, planets) -> naquadah oxide mixture -> **naquadah**, **enriched naquadah**, **naquadria** through
   acid chemistry.
2. Naquadah alloy and naquadah coils (7,200 K) for ZPM-UV.
3. **Naquadah fuel** (GoodGenerator): the [Naquadah Fuel Refinery](../multiblocks/naquadah_fuel_refinery.md) and
   [Fuel Refining Complex](../multiblocks/fuel_refining_complex.md) make fuel MkI-MkVI for the
   [Large Naquadah Reactor](../multiblocks/large_naquadah_reactor.md).
''')

line('antimatter.md', 'Antimatter', 90, 'gtapexadditions:antimatter_forge', '''
# Antimatter (UEV+)

1. Protomatter -> **semi-stable antimatter** in the [SASS](../multiblocks/antimatter_forge.md) (with an Antimatter Hatch).
2. Burn it with matter in the [SLAM](../multiblocks/antimatter_generator.md) for enormous power, or wireless with the
   [Real Annihilation Generator](../multiblocks/real_annihilation_generator.md).
3. The [Strange Matter Aggregator](../multiblocks/strange_matter_aggregator.md) breeds more antimatter at UIV.
''')

line('draconic-evolution.md', 'Draconic Evolution', 100, 'draconicevolution:draconium_ingot', '''
# Draconic Evolution

Draconic Evolution's crafting moves into GregTech assemblers: base items at IV, **Wyvern** at LuV, **Draconic** at ZPM,
**Chaotic** at UV. Draconium is smelted in the Electric Blast Furnace. Its free generator is removed.
[Draconic Fusion Crafting](../multiblocks/draconic_fusion_crafting.md) (LuV) does fusion crafting without injectors.
''')

line('avaritia.md', 'Avaritia Endgame', 110, 'avaritia:extreme_crafting_table', '''
# Avaritia Endgame

* **Extreme Crafting Table**: Nether Stars (the Wither), diamond blocks and a crafting table.
* **Neutronium Compressor**: a Void Thorn (Void Blossom) and a Blazing Eye (Gauntlet), from Bosses of Mass Destruction,
  netherite blocks and a Botania Terrasteel block. Avaritia's own neutronium is replaced by GregTech's.
* **Infinity**: the ingot is extreme-crafted but is GregTech Infinity; shaping it is UV work.
* **Infinity Catalyst**: every singularity (the Eternal Singularity) plus Botania's top materials.
* The Infinity Stones' crafting is locked.
''')

line('heroes.md', 'Hero Suits', 120, 'minecraft:iron_chestplate', '''
# Hero Suits

Palladium-based hero mods (Iron Man, Omnitrix, superhero packs) are in. Their basic circuits and machines move into
GregTech assemblers (HV / EV); the suits themselves are crafted at their own tailoring benches.
(A new Apex suit station is planned.)
''')
