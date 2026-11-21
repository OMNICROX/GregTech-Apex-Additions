// priority: -95
// Apex Ascension - Avaritia's Infinity Catalyst needs Botania's top materials (GregTech Apex Additions)

// ---- Avaritia's endgame needs Botania ----
// The Infinity Catalyst keeps its singularities and also takes Botania's top materials. Runs before the GT unify
// script (-100) and does the same neutronium / infinity swap itself (unify can't see recipes added here).
// (The Neutron Compressor's Terrasteel block is in crazycraft_endgame_gating.js.)
const APEX_CATALYST_BOTANIA = [['botania:life_essence', 4], ['botania:terrasteel_block', 2], ['mythicbotany:alfsteel_ingot', 4],
  ['botanicadds:gaiasteel_ingot', 4]]

ServerEvents.recipes(event => {
  let has = id => { try { return Item.exists(id) } catch (e) { return false } }
  let extra = APEX_CATALYST_BOTANIA.filter(e => has(e[0]))
  if (!extra.length) return
  let done = 0
  let todo = []
  // Avaritia's catalyst type isn't known to KubeJS' filters, so match on the recipe JSON itself
  event.forEachRecipe({}, r => {
    let s = String(r.json.toString())
    if (s.indexOf('"avaritia:infinity_catalyst"') < 0) return
    let j
    try { j = JSON.parse(s) } catch (e) { return }
    if (j.type !== 'avaritia:infinity_catalyst' || !Array.isArray(j.ingredients)) return
    todo.push({ id: String(r.getId()), j: j })
  })
  let swap = { 'avaritia:neutron_ingot': 'gtceu:neutronium_ingot', 'avaritia:infinity_ingot': 'gtna:infinity_ingot' }
  todo.forEach(t => {
    t.j.ingredients.forEach(x => { if (x && x.item && swap[x.item] && has(swap[x.item])) x.item = swap[x.item] })
    extra.forEach(e => { for (let i = 0; i < e[1]; i++) t.j.ingredients.push({ item: e[0] }) })
    event.remove({ id: t.id })
    event.custom(t.j).id(`apex:botania_catalyst/${t.id.replace(':', '/')}`)
    done++
  })
  console.info(`[APEX-BOTANIA] Infinity Catalyst now needs Botania (${extra.map(e => e[1] + 'x ' + e[0]).join(', ')}): ${done} recipes`)
})

