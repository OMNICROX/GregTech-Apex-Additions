// priority: -100
// Apex Ascension - GregTech versions of Avaritia's neutronium and infinity.
//   Neutronium: GregTech's replaces Avaritia's completely (Avaritia neutronium + Neutron Collector can't be made).
//   Infinity:   the Infinity Ingot is still crafted (Extreme Crafting), but it now makes GregTech (gtna) Infinity,
//               so the same metal goes through GT machines. Parts from it are UV fluid-solidifier work (apex_gt_infinity.js).
// Every recipe that mentions an Avaritia id is rewritten as raw JSON, so Extreme Crafting recipes work too.
const APEX_SWAP = {
  'avaritia:neutron_pile': 'gtceu:tiny_neutronium_dust',
  'avaritia:neutron_nugget': 'gtceu:neutronium_nugget',
  'avaritia:neutronium_ingot': 'gtceu:neutronium_ingot',
  'avaritia:neutronium_block': 'gtceu:neutronium_block',
  'avaritia:neutron_ingot': 'gtceu:neutronium_ingot',   // Re-Avaritia names
  'avaritia:neutron': 'gtceu:neutronium_block',
  'avaritia:infinity': 'gtna:infinity_block',
  'avaritia:infinity_ingot': 'gtna:infinity_ingot',
  'avaritia:infinity_nugget': 'gtna:infinity_nugget',
  'avaritia:infinity_block': 'gtna:infinity_block'
}
// these Avaritia items can no longer be made at all (GT has its own sources)
const APEX_NO_OUTPUT = ['avaritia:neutron_pile', 'avaritia:neutron_nugget', 'avaritia:neutronium_ingot',
  'avaritia:neutronium_block', 'avaritia:neutron_collector', 'avaritia:neutron_ingot', 'avaritia:neutron',
  'avaritia:dense_neutron_collector', 'avaritia:denser_neutron_collector', 'avaritia:densest_neutron_collector']
// Avaritia's own ingot<->nugget<->block conversions: GT generates these for its materials already
const APEX_DROP_IDS = ['avaritia:infinity', 'avaritia:infinity_block', 'avaritia:un_infinity_block', 'avaritia:infinity_nugget',
  'avaritia:infinity_ingot_from_nuggets', 'avaritia:un_infinity_ingot']

const ApexUnifyJsonParser = Java.loadClass('com.google.gson.JsonParser')
// Adds a recipe from raw JSON with a fixed id. GT-format recipes go through GT's own builder: event.custom()
// on a GT recipe type can't take an id and registers the recipe twice (kjs/<out> and <type>/<out>).
function apexUnifyAdd(event, json, id) {
  let t = String(json.type || '').split(':')
  if (json.duration !== undefined && (json.inputs || json.tickInputs) && t.length === 2) {
    try {
      // GT puts the type in front of the path itself
      let gid = String(id).replace(`:${t[1]}/`, ':')
      let b = event.recipes[t[0]][t[1]](gid)
      b.merge(ApexUnifyJsonParser.parseString(JSON.stringify(json)).getAsJsonObject())
      return
    } catch (e) { console.warn(`GT builder failed for ${id}, using custom: ${e}`) }
  }
  let r = event.custom(json)
  try { r.id(id) } catch (e) { }
}

ServerEvents.recipes(event => {
  let has = id => { try { return Item.exists(id) } catch (e) { return false } }
  let swap = {}
  Object.keys(APEX_SWAP).forEach(av => { if (has(APEX_SWAP[av])) swap[av] = APEX_SWAP[av]; else console.warn(`[APEX-UNIFY] missing ${APEX_SWAP[av]}, ${av} left alone`) })
  let avIds = Object.keys(swap)

  APEX_NO_OUTPUT.forEach(av => event.remove({ output: av }))
  APEX_DROP_IDS.forEach(id => event.remove({ id: id }))

  let todo = []
  event.forEachRecipe({}, r => {
    let id = String(r.getId())
    if (APEX_DROP_IDS.indexOf(id) >= 0) return
    let s = String(r.json.toString())
    if (avIds.some(av => s.indexOf(`"${av}"`) >= 0)) todo.push({ id: id, s: s })
  })
  let recycled = 0
  todo.forEach(t => {
    let s = t.s
    avIds.forEach(av => { s = s.split(`"${av}"`).join(`"${swap[av]}"`) })
    try {
      let json = JSON.parse(s)
      event.remove({ id: t.id })
      // GT's own recycling (macerator, arc furnace, extractor) of Avaritia ingots: GT already has the same
      // recipes for its own neutronium / infinity, so the Avaritia copies just go
      if (json.duration !== undefined && json.inputs && /^gtceu:(macerator|arc_furnace|liquefaction_furnace|extractor|fluid_extractor)\//.test(t.id)) { recycled++; return }
      apexUnifyAdd(event, json, `apex:gt_unify/${t.id.replace(':', '/')}`)
    } catch (e) { console.error(`[APEX-UNIFY] failed ${t.id}: ${e}`) }
  })
  console.info(`[APEX-UNIFY] ${todo.length - recycled} recipes now use GregTech Neutronium / Infinity (${recycled} duplicate GT recycling recipes dropped)`)
})
