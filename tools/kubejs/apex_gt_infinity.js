// priority: -120
// Apex Ascension - Infinity like GTNH: the ingot is crafted (Avaritia Extreme Crafting -> GregTech Infinity Ingot),
// but every GregTech recipe that shapes or uses Infinity runs at UV. Infinity parts come from the
// Fluid Solidifier: molten Infinity + mold, 491,520 EU/t (UV), 7.5 s.
const APEX_INF = {
  UV: 491520,
  SOLIDIFY_TICKS: 150,
  // crafting-table part recipes GT generates (hammer + ingots etc.) are removed for these
  PART_RE: /^gtna:infinity_(plate|double_plate|dense_plate|rod|long_rod|gear|small_gear|bolt|screw|ring|spring|small_spring|foil|fine_wire|frame|round)$/
}

ServerEvents.recipes(event => {
  // 1) no hand-made infinity parts
  let hand = 0
  ;['minecraft:crafting_shaped', 'minecraft:crafting_shapeless'].forEach(type => {
    event.forEachRecipe({ type: type }, r => {
      let res = r.json.get('result')
      if (!res) return
      let out = res.isJsonObject() ? String(res.getAsJsonObject().get('item')) : String(res.getAsString())
      if (APEX_INF.PART_RE.test(out.replace(/"/g, ''))) { event.remove({ id: r.getId() }); hand++ }
    })
  })

  // 2) every GT recipe touching Infinity runs at least at UV; solidifier recipes take 7.5 s
  let uv = 0, solid = 0
  event.forEachRecipe({}, r => {
    let json = r.json
    if (!json || !json.has('duration') || !json.has('tickInputs')) return
    let s = String(json.toString())
    if (s.indexOf('"gtna:infinity') < 0) return
    let ti = json.get('tickInputs').getAsJsonObject()
    if (!ti.has('eu')) return
    let eu = ti.get('eu').getAsJsonArray()
    let changed = false
    for (let i = 0; i < eu.size(); i++) {
      let e = eu.get(i).getAsJsonObject(), c = e.get('content')
      let volt, amps = 1
      if (c.isJsonPrimitive()) volt = c.getAsLong()
      else { const o = c.getAsJsonObject(); volt = o.has('voltage') ? o.get('voltage').getAsLong() : 0; amps = o.has('amperage') ? o.get('amperage').getAsLong() : 1 }
      if (volt < APEX_INF.UV) {
        let st = new ApexInfJsonObject(); st.addProperty('voltage', APEX_INF.UV); st.addProperty('amperage', amps)
        e.add('content', st); changed = true
      }
    }
    let type = ''
    try { type = String(r.json.get('type').getAsString()) } catch (e) { type = '' }
    if (type === 'gtceu:fluid_solidifier' && s.indexOf('"gtna:infinity"') >= 0) { r.set('duration', APEX_INF.SOLIDIFY_TICKS); solid++; changed = true }
    else if (changed) r.set('duration', json.get('duration').getAsInt())
    if (changed) { r.changed = true; uv++ }
  })
  console.info(`[APEX-INF] ${hand} hand part recipes removed, ${uv} GT recipes raised to UV (${solid} solidifier)`)
})

const ApexInfJsonObject = Java.loadClass('com.google.gson.JsonObject')
