// priority: -90
// Apex Ascension 1.2 - Space Elevator progression (GregTech Apex Additions)
//  1. GT Community Additions' space elevator, miners, pumps and assemblers are disabled (GTAA's replace them)
//  2. UIV / UXV / OpV field generators, emitters and sensors move from the Assembly Line to the Space Assembler
//  3. GT: New Age capstones (Eye of Harmony, Forge of the Gods, DTPF, Transcendent Plasma Mixer) move to the Space Assembler
// The moved recipes keep their inputs, outputs and time. The EU/t is capped at the module's voltage so the
// required module tier can always run them.

const APEX_SE = {
  GTCA_DISABLED: [
    'gtca:space_elevator', 'gtca:space_miner_mki', 'gtca:space_miner_mkii', 'gtca:space_miner_mkiii',
    'gtca:space_pump_mki', 'gtca:space_pump_mkii', 'gtca:space_pump_mkiii',
    'gtca:space_assembler_mki', 'gtca:space_assembler_mkii', 'gtca:space_assembler_mkiii'
  ],
  // output id -> Space Assembler module tier
  MOVE: {
    'gtceu:uiv_field_generator': 2, 'gtceu:uiv_emitter': 2, 'gtceu:uiv_sensor': 2,
    'gtceu:uxv_field_generator': 3, 'gtceu:uxv_emitter': 3, 'gtceu:uxv_sensor': 3,
    'gtceu:opv_field_generator': 3, 'gtceu:opv_emitter': 3, 'gtceu:opv_sensor': 3,
    'gtna:dimensionally_transcendent_plasma_forge': 2, 'gtna:transcendent_plasma_mixer': 2,
    'gtna:eye_of_harmony': 3, 'gtna:forge_of_gods': 3
  },
  // highest EU/t each module tier runs at (VA of UIV / UXV)
  MAX_EUT: { 1: 491520, 2: 7864320, 3: 31457280 },
  MAX_ITEMS: 16,
  MAX_FLUIDS: 4
}

const ApexSeJsonParser = Java.loadClass('com.google.gson.JsonParser')
// Adds a recipe from raw JSON with a fixed id. GT-format recipes go through GT's own builder: event.custom()
// on a GT recipe type can't take an id and registers the recipe twice (kjs/<out> and <type>/<out>).
function apexSeAdd(event, json, id) {
  let t = String(json.type || '').split(':')
  if (json.duration !== undefined && (json.inputs || json.tickInputs) && t.length === 2) {
    try {
      // GT puts the type in front of the path itself
      let gid = String(id).replace(`:${t[1]}/`, ':')
      let b = event.recipes[t[0]][t[1]](gid)
      b.merge(ApexSeJsonParser.parseString(JSON.stringify(json)).getAsJsonObject())
      return
    } catch (e) { console.warn(`GT builder failed for ${id}, using custom: ${e}`) }
  }
  let r = event.custom(json)
  try { r.id(id) } catch (e) { }
}

ServerEvents.recipes(event => {
  let disabled = 0
  APEX_SE.GTCA_DISABLED.forEach(id => {
    if (!Item.exists(id)) return
    event.remove({ output: id })
    disabled++
  })
  console.info(`[APEX-SE] disabled ${disabled} gtca space elevator machines`)

  let moved = 0
  Object.keys(APEX_SE.MOVE).forEach(out => {
    if (!Item.exists(out)) { console.info(`[APEX-SE] not in pack (skipped): ${out}`); return }
    let tier = APEX_SE.MOVE[out]
    let found = 0
    // GTAA itself ships Space Assembler recipes for most of these: then only the old recipes go
    let own = false
    event.forEachRecipe({ output: out, type: 'gtapexadditions:space_assembler' }, r => { own = true })
    event.forEachRecipe({ output: out }, r => {
      let type = ''
      try { type = String(r.json.get('type').getAsString()) } catch (e) { type = '' }
      if (type.indexOf('gtapexadditions:') === 0 || type.indexOf('minecraft:') === 0) return
      let j
      try { j = JSON.parse(String(r.json)) } catch (e) { return }
      if (!j.inputs || !j.tickInputs) return // not a GT-format recipe
      let items = (j.inputs && j.inputs.item) || []
      let fluids = (j.inputs && j.inputs.fluid) || []
      if (items.length > APEX_SE.MAX_ITEMS || fluids.length > APEX_SE.MAX_FLUIDS) {
        console.warn(`[APEX-SE] too many inputs, left as is: ${r.getId()}`)
        return
      }
      let eu = (j.tickInputs && j.tickInputs.eu) || []
      eu.forEach(e => {
        let c = e.content
        let cap = APEX_SE.MAX_EUT[tier]
        if (typeof c === 'number') { if (c > cap) e.content = cap }
        else if (c && c.voltage !== undefined) {
          let total = c.voltage * (c.amperage || 1)
          if (total > cap) e.content = { voltage: cap, amperage: 1 }
        }
      })
      let nj = {
        type: 'gtapexadditions:space_assembler',
        duration: j.duration,
        inputs: { item: items, fluid: fluids },
        outputs: j.outputs,
        tickInputs: j.tickInputs || {},
        data: { module_tier: tier }
      }
      ;['inputChanceLogics', 'outputChanceLogics'].forEach(k => { if (j[k]) nj[k] = j[k] })
      event.remove({ id: r.getId() })
      found++
      if (own) return
      apexSeAdd(event, nj, `apex:space_assembler/${String(r.getId()).replace(':', '/')}`)
    })
    if (found === 0) console.warn(`[APEX-SE] no GT recipe found to move for ${out}`)
    moved += found
  })
  console.info(`[APEX-SE] moved ${moved} recipes to the Space Assembler (GTAA's own recipe kept where it has one)`)
})
