// priority: -80
// Apex Ascension 1.2.1 - GT Community Additions fixes (no mod files are changed: CurseForge sees the official jars).
//
// GTNA and GTCA both register Inconel-625, Hastelloy-N, Tantalloy-61, End Steel, Stellar Alloy and more under the same
// names, and the two copies split the recipes between them: GTCA's alloys could not be made at all, and some of GTNA's
// hot ingots had no vacuum freezer recipe or no mixer recipe. That left COMET, both PCB Factories and the IsaMill
// impossible to build.
//  1. every recipe that asks for a GTCA item that GTNA also has now accepts either copy (shared #apex:gtca_shared tags)
//  2. the missing vacuum freezer and mixer recipes are added (components from the GTCA / GTNA sources)
//  3. Ohriharukon and Germanite (stellar alloy, quantum alloy) get an End vein; they had no worldgen

const APEX_GTCA_SHARED = ["celestial_tungsten_block", "celestial_tungsten_bolt", "celestial_tungsten_dust", "celestial_tungsten_frame", "celestial_tungsten_plate", "celestial_tungsten_rod", "celestial_tungsten_screw", "dark_steel_block", "dark_steel_gear", "dark_steel_nugget", "dark_steel_plate", "dark_steel_rod", "double_celestial_tungsten_plate", "double_dark_steel_plate", "double_eglin_steel_plate", "double_end_steel_plate", "double_hastelloy_n_plate", "double_inconel_625_plate", "double_nitinol_60_plate", "double_stellar_alloy_plate", "eglin_steel_block", "eglin_steel_plate", "end_steel_frame", "end_steel_rod", "gold_nanites", "hastelloy_n_block", "hastelloy_n_dust", "hastelloy_n_ingot", "hastelloy_n_plate", "hastelloy_n_rod", "hot_end_steel_ingot", "hot_hastelloy_n_ingot", "hot_inconel_625_ingot", "hot_inconel_792_ingot", "hot_tantalloy_61_ingot", "inconel_625_block", "inconel_625_bolt", "inconel_625_dust", "inconel_625_gear", "inconel_625_ingot", "inconel_625_nugget", "inconel_625_plate", "inconel_625_rod", "inconel_625_screw", "inconel_792_block", "inconel_792_dust", "inconel_792_ingot", "inconel_792_nugget", "nitinol_60_block", "nitinol_60_bolt", "nitinol_60_dust", "nitinol_60_gear", "nitinol_60_plate", "nitinol_60_rod", "nitinol_60_screw", "silver_nanites", "small_celestial_tungsten_dust", "small_dark_steel_dust", "small_eglin_steel_dust", "small_end_steel_dust", "small_hastelloy_n_dust", "small_inconel_625_dust", "small_inconel_792_dust", "small_nitinol_60_dust", "small_stellar_alloy_dust", "small_tantalloy_61_dust", "stellar_alloy_block", "stellar_alloy_dust", "stellar_alloy_frame", "stellar_alloy_gear", "stellar_alloy_ingot", "stellar_alloy_nugget", "stellar_alloy_plate", "stellar_alloy_rod", "tantalloy_61_block", "tantalloy_61_bolt", "tantalloy_61_nugget", "tantalloy_61_rod", "tantalloy_61_screw", "tiny_celestial_tungsten_dust", "tiny_dark_steel_dust", "tiny_end_steel_dust", "tiny_inconel_625_dust", "tiny_inconel_792_dust", "tiny_nitinol_60_dust", "tiny_stellar_alloy_dust", "tiny_tantalloy_61_dust"]

ServerEvents.tags('item', event => {
  let n = 0
  APEX_GTCA_SHARED.forEach(p => {
    let a = 'gtca:' + p, b = 'gtna:' + p
    if (!Item.exists(a) || !Item.exists(b)) return
    event.add('apex:gtca_shared/' + p, [a, b])
    n++
  })
  console.info(`[APEX-GTCA] ${n} shared tags`)
})

ServerEvents.recipes(event => {
  let n = 0
  APEX_GTCA_SHARED.forEach(p => {
    let a = 'gtca:' + p, b = 'gtna:' + p
    if (!Item.exists(a) || !Item.exists(b)) return
    event.replaceInput({ input: a }, a, '#apex:gtca_shared/' + p)
    n++
  })

  // GTNA hot ingots that had no way to cool
  ;[['end_steel', 300], ['hastelloy_n', 220], ['inconel_625', 300], ['tantalloy_61', 440]].forEach(([m, t]) => {
    if (!Item.exists(`gtna:hot_${m}_ingot`)) return
    event.recipes.gtceu.vacuum_freezer(`apex:gtca_fix/freeze_${m}`)
      .itemInputs(`gtna:hot_${m}_ingot`).itemOutputs(`gtna:${m}_ingot`)
      .duration(t).EUt(GTValues.VA[GTValues.HV])
  })

  // alloy dusts that had no mixer recipe: [output, tier, [count, input]...]
  let MIX = [
    ['gtna:inconel_625_dust', GTValues.EV, [[3, 'gtceu:nickel_dust'], [7, 'gtceu:chromium_dust'], [10, 'gtceu:molybdenum_dust'], [10, 'gtceu:invar_dust'], [13, 'gtceu:nichrome_dust']]],
    ['gtna:hastelloy_n_dust', GTValues.EV, [[2, 'gtceu:yttrium_dust'], [4, 'gtceu:molybdenum_dust'], [2, 'gtceu:chromium_dust'], [2, 'gtceu:titanium_dust'], [25, 'gtceu:nickel_dust']]],
    ['gtna:inconel_792_dust', GTValues.EV, [[2, 'gtceu:nickel_dust'], [1, 'gtceu:niobium_dust'], [2, 'gtceu:aluminium_dust'], [1, 'gtceu:nichrome_dust']]],
    ['gtca:hastealloy_276_dust', GTValues.EV, [[1, 'gtceu:cobalt_dust'], [8, 'gtceu:molybdenum_dust'], [1, 'gtceu:tungsten_dust'], [1, 'gtceu:copper_dust'], [7, 'gtceu:chromium_dust'], [32, 'gtceu:nickel_dust']]],
    // seven inputs: needs the Large Mixing Vessel, which every UV base has
    ['gtca:quantum_alloy_dust', GTValues.UV, [[3, 'gtca:stellite_79_dust'], [1, 'gtca:silicon_carbide_dust'], [1, 'gtceu:gallium_dust'], [1, 'gtceu:americium_dust'], [1, 'gtceu:palladium_dust'], [1, 'gtceu:bismuth_dust'], [1, 'gtceu:germanium_dust']]],
  ]
  MIX.forEach(([out, tier, ins]) => {
    if (!Item.exists(out) || !ins.every(i => Item.exists(i[1]))) { console.warn(`[APEX-GTCA] skipped mixer for ${out}`); return }
    let total = ins.reduce((s, i) => s + i[0], 0)
    event.recipes.gtceu.mixer(`apex:gtca_fix/mix_${out.split(':')[1]}`)
      .itemInputs(ins.map(i => `${i[0]}x ${i[1]}`)).circuit(total > 9 ? 7 : 3)
      .itemOutputs(`${Math.min(total, 64)}x ${out}`)
      .duration(Math.min(total, 64) * 40).EUt(GTValues.VA[tier])
  })
  console.info(`[APEX-GTCA] ${n} recipes accept either alloy copy; freezer and mixer recipes added`)
})

GTCEuServerEvents.oreVeins(event => {
  if (!Platform.isLoaded('gtca')) return
  let B = id => () => Block.getBlock(id)
  if (!Item.exists('gtca:endstone_ohriharukon_ore') || !Item.exists('gtca:endstone_germanite_ore')) return
  event.add('apex:gtca_exotic_vein', v => {
    v.weight(20).clusterSize(30).density(0.25).layer('endstone').heightRangeUniform(10, 70)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(3).block(B('gtca:endstone_ohriharukon_ore')).size(1, 3))
      .layer(l => l.weight(2).block(B('gtca:endstone_germanite_ore')).size(1, 2))))
  })
})
