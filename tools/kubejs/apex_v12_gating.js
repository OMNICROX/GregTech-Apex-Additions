// priority: -100
// Apex Ascension 1.2 - GT progression: multiblock cleanup, Eye of Wood HV gate, scarce lava
// (Some multis are turned off in config/gtna.yaml and config/extendedfeatures.yaml instead;
//  these are the ones those configs can't toggle.)

// ---------- 1. Overlapping / tier-skipping multiblocks: no controller recipe ----------
const APEX_DISABLED_MULTIS = [
  // gtna: 64x-parallel EBF / freezer shortcuts, steam-age mob farm + oil
  'gtna:blaze_blast_furnace', 'gtna:cold_ice_freezer', 'gtna:leap_forward_one_blast_furnace',
  'gtna:steam_elevator_entity_crusher_module_i', 'gtna:steam_elevator_entity_crusher_module_ii',
  'gtna:steam_elevator_entity_crusher_module_iii',
  'gtna:steam_elevator_oil_drill_module_i', 'gtna:steam_elevator_oil_drill_module_ii',
  'gtna:steam_elevator_oil_drill_module_iii',
  // gtca: duplicates of gtna / GCYM machines
  'gtca:nano_forge', 'gtca:green_house', 'gtca:shd_turbine', 'gtca:mega_lcr',
  'gtca:isamill', 'gtca:isa_mill_airintake', 'gtca:flotation_cell_regulator',
  // extendedfeatures: greenhouse duplicate the config can't turn off
  'extendedfeatures:advanced_conservatory'
]

const ApexGateJsonParser = Java.loadClass('com.google.gson.JsonParser')
// Adds a recipe from raw JSON with a fixed id. GT-format recipes go through GT's own builder: event.custom()
// on a GT recipe type can't take an id and registers the recipe twice (kjs/<out> and <type>/<out>).
function apexGateAdd(event, json, id) {
  let t = String(json.type || '').split(':')
  if (json.duration !== undefined && (json.inputs || json.tickInputs) && t.length === 2) {
    try {
      // GT puts the type in front of the path itself
      let gid = String(id).replace(`:${t[1]}/`, ':')
      let b = event.recipes[t[0]][t[1]](gid)
      b.merge(ApexGateJsonParser.parseString(JSON.stringify(json)).getAsJsonObject())
      return
    } catch (e) { console.warn(`GT builder failed for ${id}, using custom: ${e}`) }
  }
  let r = event.custom(json)
  try { r.id(id) } catch (e) { }
}

ServerEvents.recipes(event => {
  let removed = 0
  APEX_DISABLED_MULTIS.forEach(id => {
    if (!Item.exists(id)) { console.warn(`[APEX-1.2] multi not found (skipped): ${id}`); return }
    event.remove({ output: id })
    removed++
  })
  // GTSE: every machine duplicates another mod or skips tiers. Prefer removing the mod;
  // if it's still installed, none of its items can be crafted.
  if (Platform.isLoaded('gtse')) {
    event.remove({ output: /^gtse:/ })
    console.info('[APEX-1.2] gtse is installed: all of its recipes removed')
  }
  console.info(`[APEX-1.2] disabled ${removed} overlapping multiblock controllers`)

  // ---------- 2. Eye of Wood: LV -> HV ----------
  // gtna crafts the Eye of Wood core in an LV assembler from 64 LV emitters, field generators and circuits.
  // Same recipe, but with 16 HV parts at HV voltage.
  let core = 'gtna:primitive_mans_spacetime_distortion_device'
  let eowDone = false
  event.forEachRecipe({ output: core, type: 'gtceu:assembler' }, r => {
    let j = JSON.parse(String(r.json))
    let items = (j.inputs && j.inputs.item) || []
    items.forEach(e => {
      let c = e.content || {}
      let ing = c.ingredient || c
      let swap = s => s.replace('gtceu:lv_emitter', 'gtceu:hv_emitter')
                         .replace('gtceu:lv_field_generator', 'gtceu:hv_field_generator')
                         .replace('gtceu:circuits/lv', 'gtceu:circuits/hv')
      if (ing.item) ing.item = swap(ing.item)
      if (ing.tag) ing.tag = swap(ing.tag)
      if (c.count === 64) c.count = 16
    })
    let eu = (j.tickInputs && j.tickInputs.eu) || []
    eu.forEach(e => { e.content = 480 })
    event.remove({ id: r.getId() })
    if (eowDone) return // gtna can register the core recipe twice: remove all, add one
    apexGateAdd(event, j, 'apex:gt_gates/eye_of_wood_core_hv')
    eowDone = true
  })
  console.info(`[APEX-1.2] Eye of Wood core moved to HV: ${eowDone}`)

  // ---------- 3. Lava is scarce ----------
  // Mystical Agriculture: no more 8 fire essence -> lava bucket. Fire essence melts in a GT Fluid Extractor instead.
  event.remove({ output: 'minecraft:lava_bucket', mod: 'mysticalagriculture' })
  if (Item.exists('mysticalagriculture:fire_essence')) {
    try {
      event.recipes.gtceu.fluid_extractor('apex:lava/fire_essence')
        .itemInputs('mysticalagriculture:fire_essence')
        .outputFluids('minecraft:lava 50')
        .duration(80).EUt(30)
    } catch (e) { console.warn(`[APEX-1.2] fire essence extractor recipe failed: ${e}`) }
  }
  // Cyclic Melter: obsidian + stone -> 1000 lava is an infinite loop with a GT Rock Breaker making obsidian
  event.remove({ id: 'cyclic:melter/melter_obslava' })
  event.remove({ id: 'cyclic:melter/melter_obslava1' })
  console.info('[APEX-1.2] lava sources limited (MA fire essence -> 50 mB in extractor; Cyclic obsidian melting removed)')
})
