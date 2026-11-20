// =====================================================================
// Apex Ascension - endgame gating            (KubeJS 6 / Forge 1.20.1)
//
// The strongest tech is locked behind boss drops ("keys"), and the keys
// stay rare drops so they cannot be farmed around (ProjectE was removed in Apex Ascension v1.1).
//
// Item IDs differ between Avaritia ports, so each Avaritia item is looked
// up by its expected ID first, then by a name pattern inside the
// "avaritia:" namespace. Anything still missing is reported as one
// warning that lists the Avaritia IDs that DO exist.
// (Plain ES5-style JS on purpose: KubeJS's engine rejects some modern syntax.)
// =====================================================================

ServerEvents.recipes(function (event) {
  function has(id) {
    try { return Item.exists(id) } catch (e) { return false }
  }

  var all = []
  try {
    var list = Item.getTypeList()
    var n = list.size ? list.size() : list.length
    for (var i = 0; i < n; i++) all.push(String(list.get ? list.get(i) : list[i]))
  } catch (e) {
    console.warn('[CRAZYCRAFT-GATE] could not list items: ' + e)
  }

  var avaritiaIds = []
  for (var a = 0; a < all.length; a++) {
    if (all[a].indexOf('avaritia:') === 0) avaritiaIds.push(all[a])
  }

  // expected id first, then first avaritia item whose name matches the pattern
  function findAvaritia(preferred, re) {
    if (has(preferred)) return preferred
    for (var k = 0; k < avaritiaIds.length; k++) {
      if (re.test(avaritiaIds[k])) return avaritiaIds[k]
    }
    return null
  }

  function firstOf(fallback, list) {
    for (var q = 0; q < list.length; q++) {
      if (has(list[q])) return list[q]
    }
    return fallback
  }

  // ---- boss keys (fall back to a Nether Star if a mod item is missing) ----
  var STAR   = 'minecraft:nether_star'                                                  // Wither
    var SOUL   = firstOf(STAR, ['bosses_of_mass_destruction:soul_star'])                 // Lich
  var THORN  = firstOf(STAR, ['bosses_of_mass_destruction:void_thorn'])                // Void Blossom
  var EYE    = firstOf(STAR, ['bosses_of_mass_destruction:blazing_eye'])               // Gauntlet
  var BLOCK  = 'minecraft:netherite_block'

  var missing = []
  function gate(label, preferred, re, pattern, keys) {
    var out = findAvaritia(preferred, re)
    if (!out) { missing.push(label); return }
    event.remove({ output: out })
    event.shaped(out, pattern, keys).id('crazycraft:gate/' + out.replace(':', '_'))
    console.info('[CRAZYCRAFT-GATE] gated ' + out + (out === preferred ? '' : ' (matched by name for ' + label + ')'))
  }

  // ---- Tier 1 (Wither): Avaritia's crafting station ----
  gate('extreme crafting table', 'avaritia:extreme_crafting_table', /extreme/,
    ['SDS', 'DCD', 'SDS'],
    { S: STAR, D: 'minecraft:diamond_block', C: 'minecraft:crafting_table' })

  // ---- Tier 2 (Void Blossom / Gauntlet): Avaritia compressor ----
  // Neutron Collector removed: GregTech Neutronium replaces Avaritia's (apex_gt_neutronium.js)

  gate('neutronium compressor', firstOf('avaritia:neutronium_compressor', ['avaritia:neutron_compressor', 'avaritia:neutronium_compressor']), /^avaritia:neutron(ium)?_compressor$/,
    ['TNE', 'NPN', 'ENT'],
    { T: THORN, E: EYE, N: BLOCK, P: 'minecraft:piston' })

  // ---- Infinity Catalyst: NOT replaced. Avaritia's own Extreme Crafting recipe (the full singularity chain)
  //      stays, so the catalyst still needs every singularity / the Eternal Singularity. ----

  if (missing.length > 0) {
    var hint = []
    for (var h = 0; h < avaritiaIds.length; h++) {
      if (/(craft|compress|collect|catalyst|neutron|infinity)/.test(avaritiaIds[h])) hint.push(avaritiaIds[h])
    }
    console.warn('[CRAZYCRAFT-GATE] not gated (no matching item): ' + missing.join(', ') +
      ' | avaritia ids seen: ' + (hint.length ? hint.join(', ') : '(none - is Avaritia loaded?)'))
  }

  // ---- Infinity Stone mod (namespace "infinity"): lock stone crafting (gauntlets and the gauntlet cast keep their original recipes) ----
  var locked = 0
  for (var j = 0; j < all.length; j++) {
    var id = all[j]
    if (id.indexOf('avaritia:') === 0) continue
    if (/infinity/.test(id) && /stone/.test(id) && !/gauntlet|glove/.test(id)) {
      event.remove({ output: id })
      console.info('[CRAZYCRAFT-GATE] locked crafting for ' + id)
      locked++
    }
  }
  console.info('[CRAZYCRAFT-GATE] done, ' + all.length + ' items scanned, ' + locked + ' infinity-stone items locked')
})
