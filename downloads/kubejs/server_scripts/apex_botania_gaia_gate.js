// priority: -80
// Apex Ascension - post-Gaia Botania needs the Alfheim Teleportation Array (GregTech Apex Additions, IV).
// Runs after apex_mana_nexus.js (-70), which already copied every altar / apothecary / infuser recipe onto the Array.
// Here: crafting-table recipes that use Gaia Spirit or Gaia Spirit Ingots become Array recipes, and the original
// recipes of every kind that use them are removed, so after the Gaia Guardian fight the Array is the way forward.
// GregTech machine recipes, Avaritia and Mystical Agriculture recipes are left alone.

const APEX_GAIA_INPUTS = ['botania:life_essence', 'botania:gaia_ingot']
const APEX_GAIA_KEEP_TYPES = /^(gtceu|gtapexadditions|avaritia|mysticalagriculture|mysticalagradditions|cucumber):/
const APEX_GAIA_MANA = 250000

ServerEvents.recipes(event => {
  let moved = 0, removed = 0, kept = 0
  let usesGaia = j => APEX_GAIA_INPUTS.some(id => j.indexOf(`"${id}"`) >= 0)
  let ingId = v => {
    if (Array.isArray(v)) v = v[0]
    if (!v) return null
    if (v.item) return v.item
    if (v.tag) return '#' + v.tag
    return null
  }

  let toRemove = []
  event.forEachRecipe({}, r => {
    let type = String(r.getType())
    if (APEX_GAIA_KEEP_TYPES.test(type)) return
    let text
    try { text = String(r.json.toString()) } catch (e) { return }
    if (!usesGaia(text)) return
    let id = String(r.getId())
    let j
    try { j = JSON.parse(text) } catch (e) { return }

    if (type === 'minecraft:crafting_shaped' || type === 'minecraft:crafting_shapeless') {
      let counts = {}, order = []
      let add = k => { if (!k) return; if (!counts[k]) { counts[k] = 0; order.push(k) } counts[k]++ }
      if (type === 'minecraft:crafting_shaped') {
        (j.pattern || []).forEach(row => row.split('').forEach(c => { if (c !== ' ') add(ingId(j.key[c])) }))
      } else (j.ingredients || []).forEach(i => add(ingId(i)))
      let res = j.result || {}
      let out = res.item
      if (!out || !order.length) { kept++; return }
      let r2 = event.recipes.gtapexadditions.mana_nexus(`apex:gaia_gate/${id.replace(':', '/')}`)
        .itemInputs(order.map(k => `${counts[k]}x ${k}`))
        .itemOutputs(`${res.count || 1}x ${out}`)
        .duration(400).EUt(7680)
      r2.addData('mana', APEX_GAIA_MANA).addData('nexus_tier', 5)
      moved++
    }
    // other kinds (petal apothecary, Mythic Botany infuser, mana infusion...) already have an Array copy
    toRemove.push(id)
  })
  toRemove.forEach(id => { event.remove({ id: id }); removed++ })
  console.info(`[APEX-GAIA] ${moved} crafting recipes moved to the Alfheim Teleportation Array, ${removed} originals removed` +
    (kept ? `, ${kept} left as they were` : ''))
})
