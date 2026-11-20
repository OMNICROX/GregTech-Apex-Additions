// Apex Ascension - harder Ultimate Stew and Cosmic Meatballs.
// Adds Farmer's Delight crops and meat from modded passive mobs to Avaritia's own recipes
// (it edits whatever recipe Avaritia ships, so the recipe size/station stays the same).
// Items that aren't in the pack are skipped automatically.
const CC_STEW_EXTRA = [
  'farmersdelight:cabbage', 'farmersdelight:tomato', 'farmersdelight:onion', 'farmersdelight:rice',
  'undergarden:underbeans', 'undergarden:gloomgourd', 'alexsmobs:banana'
]
const CC_MEAT_EXTRA = [
  'farmersdelight:ham', 'alexsmobs:kangaroo_meat', 'alexsmobs:moose_ribs', 'alexsmobs:raw_catfish',
  'alexsmobs:lobster_tail', 'alexsmobs:blobfish', 'alexsmobs:flying_fish',
  'twilightforest:raw_venison', 'twilightforest:raw_meef',
  'undergarden:raw_dweller_meat', 'undergarden:raw_gloomper_leg'
]

const CcFoodJsonParser = Java.loadClass('com.google.gson.JsonParser')
// Adds a recipe from raw JSON with a fixed id. GT-format recipes go through GT's own builder: event.custom()
// on a GT recipe type can't take an id and registers the recipe twice (kjs/<out> and <type>/<out>).
function ccFoodAdd(event, json, id) {
  let t = String(json.type || '').split(':')
  if (json.duration !== undefined && (json.inputs || json.tickInputs) && t.length === 2) {
    try {
      // GT puts the type in front of the path itself
      let gid = String(id).replace(`:${t[1]}/`, ':')
      let b = event.recipes[t[0]][t[1]](gid)
      b.merge(CcFoodJsonParser.parseString(JSON.stringify(json)).getAsJsonObject())
      return
    } catch (e) { console.warn(`GT builder failed for ${id}, using custom: ${e}`) }
  }
  let r = event.custom(json)
  try { r.id(id) } catch (e) { }
}

ServerEvents.recipes(event => {
  let exists = id => { try { return !Item.of(id).isEmpty() } catch (e) { return false } }

  let targets = [
    { label: 'Ultimate Stew', item: 'avaritia:ultimate_stew', extra: CC_STEW_EXTRA },
    { label: 'Cosmic Meatballs', item: 'avaritia:cosmic_meatballs', extra: CC_MEAT_EXTRA }
  ]

  targets.forEach(t => {
    if (!t.item) { console.warn(`[CRAZYCRAFT-FOOD] no Avaritia item found for ${t.label}`); return }
    let found = []
    event.forEachRecipe({ output: t.item }, r => found.push(r))
    if (found.length === 0) { console.warn(`[CRAZYCRAFT-FOOD] no recipe makes ${t.item}`); return }

    found.forEach(r => {
      let json = JSON.parse(String(r.json.toString()))
      if (!Array.isArray(json.ingredients)) {
        console.warn(`[CRAZYCRAFT-FOOD] ${r.getId()} (${json.type}) has no ingredient list, left unchanged`)
        return
      }
      let have = JSON.stringify(json.ingredients)
      let add = t.extra.filter(exists).filter(id => have.indexOf(`"${id}"`) < 0)
      if (json.ingredients.length + add.length > 81) {
        console.warn(`[CRAZYCRAFT-FOOD] ${r.getId()} would exceed 81 ingredients, left unchanged`)
        return
      }
      add.forEach(id => json.ingredients.push({ item: id }))
      // GregTech Neutronium replaces Avaritia's (see apex_gt_neutronium.js)
      let swap = { 'avaritia:neutron_pile': 'gtceu:tiny_neutronium_dust', 'avaritia:neutron_nugget': 'gtceu:neutronium_nugget', 'avaritia:neutronium_ingot': 'gtceu:neutronium_ingot', 'avaritia:neutron_ingot': 'gtceu:neutronium_ingot' }
      json.ingredients.forEach(ing => { if (ing && swap[ing.item] && exists(swap[ing.item])) ing.item = swap[ing.item] })
      let oldId = String(r.getId())
      event.remove({ id: oldId })
      ccFoodAdd(event, json, `crazycraft:avaritia_food/${t.item.split(':')[1]}`)
      console.info(`[CRAZYCRAFT-FOOD] ${t.label}: added ${add.length} ingredients (${add.join(', ')}) to ${oldId}`)
    })
  })
})
