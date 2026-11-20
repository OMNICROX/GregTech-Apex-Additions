// priority: -100
// Apex Ascension 1.2 - Cyclic is the "IC2" of this pack: its machines stay, its power generators go
// (GT power only). Items are hidden in client_scripts/apex_v12_cleanup_hide.js.
const APEX_CYCLIC_GENERATORS = [
  'cyclic:generator_fuel', 'cyclic:generator_solar', 'cyclic:generator_food',
  'cyclic:generator_item', 'cyclic:generator_fluid', 'cyclic:peat_farm'
]

ServerEvents.recipes(event => {
  let n = 0
  APEX_CYCLIC_GENERATORS.forEach(id => {
    if (!Item.exists(id)) return
    event.remove({ output: id })
    n++
  })
  console.info(`[APEX-1.2] removed ${n} Cyclic generator recipes`)
})
