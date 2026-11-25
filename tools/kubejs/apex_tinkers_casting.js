// priority: -90
// Apex Ascension 1.3 - Tinkers casting costs
// Casting plates, gears and rods on a Casting Table was cheaper than GregTech (a plate for 1 ingot, while early GT
// hammers 2 ingots into 1 plate), which made the first GT machines pointless. The molds stay, they just cost more
// molten metal than GregTech: plates 2 ingots, gears 6, small gears 2, rods 1. Ingots, nuggets and blocks are untouched.

const APEX_CAST = {
  // item path ending -> metal needed, in ingots (90 mB each)
  COST: [[/_small_gear$|^small_gear_/, 2], [/_gear$|^gear_/, 6], [/_plate$|^plate_/, 2], [/_rod$|^rod_/, 1]],
  INGOT: 90
}

ServerEvents.recipes(event => {
  let changed = 0, skipped = 0
  let costFor = path => {
    for (let i = 0; i < APEX_CAST.COST.length; i++) if (APEX_CAST.COST[i][0].test(path)) return APEX_CAST.COST[i][1]
    return 0
  }
  let todo = []
  event.forEachRecipe({ type: 'tconstruct:casting_table' }, r => {
    let j
    try { j = JSON.parse(String(r.json)) } catch (e) { return }
    let res = j.result
    let out = typeof res === 'string' ? res : res && (res.item || res.tag)
    if (!out || out.indexOf(':') < 0) return
    let ingots = costFor(out.split(':')[1])
    if (!ingots || !j.fluid) return
    let need = ingots * APEX_CAST.INGOT
    let fl = Array.isArray(j.fluid) ? j.fluid : [j.fluid]
    let raise = false
    fl.forEach(f => { if (typeof f === 'object' && f.amount && f.amount < need) raise = true })
    if (!raise) return
    let old = fl[0].amount
    fl.forEach(f => { if (typeof f === 'object' && f.amount) f.amount = need })
    if (j.cooling_time) j.cooling_time = Math.round(j.cooling_time * need / Math.max(1, old))
    todo.push({ id: String(r.getId()), json: j })
  })
  todo.forEach(t => {
    try {
      event.remove({ id: t.id })
      event.custom(t.json).id(t.id.indexOf('apex:') === 0 ? t.id + '_cost' : 'apex:tinkers_casting/' + t.id.replace(':', '/'))
      changed++
    } catch (e) { skipped++ }
  })
  console.info(`[APEX-CAST] ${changed} Tinkers casting recipes (plates, gears, rods) now cost at least as much as GregTech; ${skipped} skipped`)
})
