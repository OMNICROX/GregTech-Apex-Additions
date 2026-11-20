// priority: -200
// Apex Ascension - GT long-recipe duration curve + matching power curve
//
// TIME: recipes up to THRESHOLD are untouched. Longer ones are squashed on a log curve:
//   newTime = T * (1 + K * ln(oldTime / T))
//   15 min -> 13 min | 30 min -> 18 min | 1 h -> 23 min | 3 h -> 32 min | 12.5 h (45000 s) -> 42 min
//
// POWER: the more time a recipe loses, the more power it pulls.
//   ampMult = ceil((oldTime / newTime) ^ EU_EXP)
// It raises AMPERAGE, not voltage, so the recipe stays in its original tier
// (a UV recipe stays UV, it just needs more amps: more hatches / 4A / 16A / laser hatches).
// EU_EXP = 1.0 keeps total energy per craft about the same (a bit more, from rounding up).
//   30 min recipe  -> x2 amps
//   1 h recipe     -> x3 amps
//   3 h recipe     -> x6 amps
//   12.5 h recipe  -> x18 amps
//
// Generator recipes (anything that OUTPUTS EU) are never touched, so fuel values stay the same.
// Covers every GTCEu-format recipe: gtceu, gtna, gtca, extendedfeatures, etc.

const ApexDurJsonObject = Java.loadClass('com.google.gson.JsonObject')

const APEX_DUR = {
  THRESHOLD_TICKS: 600 * 20, // 10 minutes; nothing at or below this changes
  K: 0.75,                   // time squash strength (lower = shorter recipes)
  EU_EXP: 1.0,               // power curve (1.0 = same total energy, 0.5 = gentler, 0 = no power change)
  MAX_AMP_MULT: 64,          // safety cap on the amp multiplier
  SKIP_TYPES: [              // recipe type ids to leave alone, e.g. 'gtceu:fusion_reactor'
  ],
  LOG_EACH: false            // true = log every changed recipe
}

ServerEvents.recipes(event => {
  let changed = 0, failed = 0, powered = 0, biggest = null
  const T = APEX_DUR.THRESHOLD_TICKS

  const euEntries = (json, key) => {
    if (!json.has(key)) return null
    const m = json.get(key)
    if (!m.isJsonObject() || !m.getAsJsonObject().has('eu')) return null
    return m.getAsJsonObject().get('eu').getAsJsonArray()
  }

  event.forEachRecipe({}, r => {
    const json = r.json
    if (!json || !json.has('duration')) return
    if (!(json.has('tickInputs') || json.has('tickOutputs') || json.has('inputs'))) return
    const type = String(r.type)
    if (APEX_DUR.SKIP_TYPES.indexOf(type) >= 0) return
    if (euEntries(json, 'tickOutputs') || euEntries(json, 'outputs')) return // generator: skip

    let old
    try { old = json.get('duration').getAsInt() } catch (e) { return }
    if (old <= T) return

    const nd = Math.max(T, Math.round(T * (1 + APEX_DUR.K * Math.log(old / T))))
    const ampMult = Math.min(APEX_DUR.MAX_AMP_MULT, Math.max(1, Math.ceil(Math.pow(old / nd, APEX_DUR.EU_EXP))))

    try {
      // power: multiply amperage of every EU/t input, keep voltage (= keep tier)
      let euNote = ''
      const eu = euEntries(json, 'tickInputs')
      if (eu && ampMult > 1) {
        for (let i = 0; i < eu.size(); i++) {
          const entry = eu.get(i).getAsJsonObject()
          const c = entry.get('content')
          let volt, amps
          if (c.isJsonPrimitive()) { volt = c.getAsLong(); amps = 1 }
          else {
            const o = c.getAsJsonObject()
            volt = o.has('voltage') ? o.get('voltage').getAsLong() : 0
            amps = o.has('amperage') ? o.get('amperage').getAsLong() : 1
          }
          if (volt <= 0) continue
          const stack = new ApexDurJsonObject()
          stack.addProperty('voltage', volt)
          stack.addProperty('amperage', amps * ampMult)
          entry.add('content', stack)
          euNote = ` | ${volt} EU/t x${amps}A -> x${amps * ampMult}A`
        }
        if (euNote) powered++
      }
      // time
      r.set('duration', nd)
      r.changed = true
      changed++
      if (!biggest || old > biggest.old) biggest = { id: String(r.getId()), old: old, nd: nd, amp: ampMult }
      if (APEX_DUR.LOG_EACH) console.info(`[APEX-DUR] ${r.getId()} (${type}): ${old / 20}s -> ${nd / 20}s${euNote}`)
    } catch (e) {
      failed++
      console.warn(`[APEX-DUR] could not change ${r.getId()}: ${e}`)
    }
  })

  console.info(`[APEX-DUR] squashed ${changed} long GT recipes (${powered} got more amps, ${failed} failed)`)
  if (biggest) console.info(`[APEX-DUR] longest: ${biggest.id} ${biggest.old / 20}s -> ${biggest.nd / 20}s, x${biggest.amp} amps`)
})

