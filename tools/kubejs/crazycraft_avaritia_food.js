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

ServerEvents.recipes(event => {
  const exists = id => { try { return !Item.of(id).isEmpty() } catch (e) { return false } }

  const targets = [
    { label: 'Ultimate Stew', item: 'avaritia:ultimate_stew', extra: CC_STEW_EXTRA },
    { label: 'Cosmic Meatballs', item: 'avaritia:cosmic_meatballs', extra: CC_MEAT_EXTRA }
  ]

  targets.forEach(t => {
    if (!t.item) { console.warn(`[CRAZYCRAFT-FOOD] no Avaritia item found for ${t.label}`); return }
    const found = []
    event.forEachRecipe({ output: t.item }, r => found.push(r))
    if (found.length === 0) { console.warn(`[CRAZYCRAFT-FOOD] no recipe makes ${t.item}`); return }

    found.forEach(r => {
      const json = JSON.parse(String(r.json.toString()))
      if (!Array.isArray(json.ingredients)) {
        console.warn(`[CRAZYCRAFT-FOOD] ${r.getId()} (${json.type}) has no ingredient list, left unchanged`)
        return
      }
      const have = JSON.stringify(json.ingredients)
      const add = t.extra.filter(exists).filter(id => have.indexOf(`"${id}"`) < 0)
      if (json.ingredients.length + add.length > 81) {
        console.warn(`[CRAZYCRAFT-FOOD] ${r.getId()} would exceed 81 ingredients, left unchanged`)
        return
      }
      add.forEach(id => json.ingredients.push({ item: id }))
      // GregTech Neutronium replaces Avaritia's (see apex_gt_neutronium.js)
      const swap = { 'avaritia:neutron_pile': 'gtceu:tiny_neutronium_dust', 'avaritia:neutron_nugget': 'gtceu:neutronium_nugget', 'avaritia:neutronium_ingot': 'gtceu:neutronium_ingot', 'avaritia:neutron_ingot': 'gtceu:neutronium_ingot' }
      json.ingredients.forEach(ing => { if (ing && swap[ing.item] && exists(swap[ing.item])) ing.item = swap[ing.item] })
      const oldId = String(r.getId())
      event.remove({ id: oldId })
      event.custom(json).id(`crazycraft:avaritia_food/${t.item.split(':')[1]}`)
      console.info(`[CRAZYCRAFT-FOOD] ${t.label}: added ${add.length} ingredients (${add.join(', ')}) to ${oldId}`)
    })
  })
})
