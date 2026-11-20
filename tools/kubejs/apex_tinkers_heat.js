// priority: -80
// Apex Ascension 1.2 - Tinkers heat tiers
// Smeltery heat follows GregTech progression. A fuel's temperature is the hottest recipe it can melt;
// its rate is how fast it melts (hotter fuel = faster).
//   Lava (1000)            : metals up to MV (no Electric Blast Furnace needed, or EBF up to 1800 K)
//   Blazing Blood (1500)   : HV - IV metals (EBF 1801 - 3600 K)
//   GT plasmas (3000-9000) : LuV and up (EBF above 3600 K), each plasma hotter and faster
// Melting, alloying and part-casting temperatures of every Tinkers metal that is also a GregTech material
// are raised to match its GT blast temperature. Temperatures are only ever raised, never lowered.

const APEX_HEAT = {
  // [fluid, temperature, rate, mB per burn, burn ticks]
  FUELS: [
    ['gtceu:helium_plasma', 3000, 25, 5, 200],
    ['gtceu:oxygen_plasma', 4000, 30, 5, 200],
    ['gtceu:nitrogen_plasma', 4500, 35, 5, 200],
    ['gtceu:argon_plasma', 5500, 40, 5, 200],
    ['gtceu:iron_plasma', 7000, 50, 5, 200],
    ['gtceu:nickel_plasma', 9000, 60, 5, 200]
  ],
  // Tinkers name -> GT material name, where they differ
  RENAME: { aluminum: 'aluminium' },
  // GT blast temperature (K) -> Tinkers temperature; first row whose limit is >= blast temp
  STEPS: [[1800, 0], [3600, 1400], [4500, 2800], [5400, 3800], [7200, 4400], [9000, 5400], [10800, 6800], [1e9, 8800]]
}

const apexHeatFor = (() => {
  let mm = null
  try { mm = Java.loadClass('com.gregtechceu.gtceu.api.GTCEuAPI').materialManager } catch (e) { console.warn('[APEX-HEAT] GT material manager not found: ' + e) }
  let cache = {}
  return name => {
    if (name in cache) return cache[name]
    let t = 0
    let mat = null
    let blast = 0
    try { mat = mm ? mm.getMaterial('gtceu:' + (APEX_HEAT.RENAME[name] || name)) : null } catch (e) { mat = null }
    try { blast = mat ? mat.getBlastTemperature() : 0 } catch (e) { blast = 0 }
    if (blast > 0) for (let i = 0; i < APEX_HEAT.STEPS.length; i++) if (blast <= APEX_HEAT.STEPS[i][0]) { t = APEX_HEAT.STEPS[i][1]; break }
    cache[name] = t
    return t
  }
})()

// "forge:molten_iron" / "tconstruct:molten_iron" -> "iron"
const apexMetalOf = res => {
  if (!res) return null
  let id = res.tag || res.fluid || res.name
  if (!id) return null
  let m = String(id).match(/molten_([a-z0-9_]+)$/)
  return m ? m[1] : null
}

const ApexHeatJsonParser = Java.loadClass('com.google.gson.JsonParser')
// Adds a recipe from raw JSON with a fixed id. GT-format recipes go through GT's own builder: event.custom()
// on a GT recipe type can't take an id and registers the recipe twice (kjs/<out> and <type>/<out>).
function apexHeatAdd(event, json, id) {
  let t = String(json.type || '').split(':')
  if (json.duration !== undefined && (json.inputs || json.tickInputs) && t.length === 2) {
    try {
      // GT puts the type in front of the path itself
      let gid = String(id).replace(`:${t[1]}/`, ':')
      let b = event.recipes[t[0]][t[1]](gid)
      b.merge(ApexHeatJsonParser.parseString(JSON.stringify(json)).getAsJsonObject())
      return
    } catch (e) { console.warn(`GT builder failed for ${id}, using custom: ${e}`) }
  }
  let r = event.custom(json)
  try { r.id(id) } catch (e) { }
}

ServerEvents.recipes(event => {
  // ---- plasma fuels ----
  let fuels = 0
  APEX_HEAT.FUELS.forEach(f => {
    let ok = false
    try { ok = !Fluid.of(f[0], 1).isEmpty() } catch (e) { ok = false }
    if (!ok) { console.info(`[APEX-HEAT] no fluid ${f[0]}, fuel skipped`); return }
    event.custom({ type: 'tconstruct:melting_fuel', fluid: { fluid: f[0], amount: f[3] }, duration: f[4], temperature: f[1], rate: f[2] })
      .id(`apex:tinkers_heat/fuel/${f[0].split(':')[1]}`)
    fuels++
  })

  // ---- raise melting / alloy / casting temperatures ----
  let raised = 0
  let bump = (type, pick) => {
    event.forEachRecipe({ type: type }, r => {
      let j
      try { j = JSON.parse(String(r.json)) } catch (e) { return }
      let metal = pick(j)
      if (!metal) return
      let t = apexHeatFor(metal)
      if (!t || (j.temperature || 0) >= t) return
      j.temperature = t
      let id = String(r.getId())
      event.remove({ id: id })
      apexHeatAdd(event, j, id.indexOf('apex:') === 0 ? id + '_heat' : 'apex:tinkers_heat/' + id.replace(':', '/'))
      raised++
    })
  }
  bump('tconstruct:melting', j => apexMetalOf(j.result))
  bump('tconstruct:alloy', j => apexMetalOf(j.result))
  bump('tconstruct:material_fluid', j => apexMetalOf(j.fluid))
  bump('tconstruct:material_melting', j => apexMetalOf(j.result))
  console.info(`[APEX-HEAT] ${fuels} plasma fuels added, ${raised} Tinkers recipes moved to a hotter tier`)
})
