// priority: -90
// Apex Ascension 1.2 - purified water in high-tier wafers (GregTech Apex Additions)
// Cutting Naquadah boules needs Grade 3 purified water, Neutronium boules Grade 5 (the GTCA grades when GTCA is
// installed, otherwise GTAA's own). The water / distilled water / lubricant variants collapse into one recipe.

const APEX_PW_BOULES = { 'gtceu:naquadah_boule': 3, 'gtceu:neutronium_boule': 5 }
const APEX_PW_FLUIDS = ['minecraft:water', 'gtceu:distilled_water', 'gtceu:lubricant']
const APEX_PW_TAGS = ['minecraft:water', 'forge:water', 'forge:distilled_water', 'forge:lubricant']

function apexPwId(grade) {
  return Platform.isLoaded('gtca') ? `gtca:purified_water_grade_${grade}` : `gtapexadditions:grade_${grade}_purified_water`
}

// swaps every fluid id in the recipe json for the purified water; returns how many it changed
function apexPwSwap(node, to) {
  let n = 0
  if (node == null || typeof node !== 'object') return 0
  if (Array.isArray(node)) { node.forEach(c => { n += apexPwSwap(c, to) }); return n }
  if (typeof node.tag === 'string' && APEX_PW_TAGS.indexOf(node.tag) >= 0) {
    delete node.tag
    node.fluid = to
    return 1
  }
  Object.keys(node).forEach(k => {
    let v = node[k]
    if ((k === 'fluid' || k === 'value') && typeof v === 'string' && APEX_PW_FLUIDS.indexOf(v) >= 0) { node[k] = to; n++ }
    else n += apexPwSwap(v, to)
  })
  return n
}

ServerEvents.recipes(event => {
  let made = 0
  Object.keys(APEX_PW_BOULES).forEach(boule => {
    let grade = APEX_PW_BOULES[boule]
    let done = false
    event.forEachRecipe({ type: 'gtceu:cutter' }, r => {
      let text = String(r.json.toString())
      if (text.indexOf('"' + boule + '"') < 0) return
      let id = String(r.getId())
      let json = JSON.parse(text)
      event.remove({ id: id })
      if (done) return
      if (apexPwSwap(json.inputs, apexPwId(grade)) > 0) {
        event.custom(json).id(`gtapexadditions:apex_pw/${boule.split(':')[1]}`)
        done = true
        made++
      }
    })
  })
  console.info(`[APEX-PW] ${made} boule cutting recipes now need purified water`)
})
