// priority: -70
// Apex Ascension - Mana Nexus (GregTech Apex Additions): every Botania altar craft as a Mana Nexus recipe.
// Reads the recipes Botania and its addons actually have, so new / changed recipes follow automatically.
//   tier 1 (Manasteel cores): mana pool infusion, runic altar, pure daisy, petal apothecary
//   tier 2 (Terrasteel):      terrestrial agglomeration, elven trade
//   tier 3 (Elementium):      Mythic Botany Mana Infuser
//   tier 4 (Alfsteel):        Mythic Botany rune rituals         tier 5 (Gaiasteel): most storage and parallels
// (Botanic Additions registers its altar recipes through HammerLib after KubeJS, so they stay on its own blocks.)
// Runes are kept (not used up) like on the runic altar; mana pool catalysts are kept too.
// Elven trades need programmed circuit 3 (their inputs overlap with pool infusions).

const APEX_NEXUS_EUT = { infusion: 30, runic: 30, daisy: 30, apothecary: 30, terra: 120, elven: 480, infuser: 1920, ritual: 7680, ba: 7680, gaia: 30720 }

ServerEvents.recipes(event => {
  let has = id => { try { return Item.exists(id) } catch (e) { return false } }
  let made = {}, skipped = 0

  // Botania ingredient JSON -> 'Nx id' / 'Nx #tag'; arrays use their first option
  let ing = j => {
    if (Array.isArray(j)) j = j[0]
    if (!j) return null
    if (j.item) return has(j.item) ? { k: j.item, n: j.count || 1 } : null
    if (j.tag) return { k: '#' + j.tag, n: j.count || 1 }
    return null
  }
  let isRune = k => /rune/.test(k) && !/^#/.test(k)
  let out = j => {
    if (!j) return null
    if (Array.isArray(j)) j = j[0]
    let id = j.item || j.name
    return id && has(id) ? `${j.count || 1}x ${id}` : null
  }
  let blockItem = j => {
    if (!j) return null
    if (j.type === 'block' || j.block) return has(j.block) ? `1x ${j.block}` : null
    if (j.type === 'tag' || j.tag) return `1x #${j.tag}`
    if (j.name) return has(j.name) ? `1x ${j.name}` : null
    return null
  }

  let add = (kind, src, tier, inputs, keep, outputs, mana, extra) => {
    if (!outputs.length || outputs.some(o => !o) || inputs.some(i => !i)) { skipped++; return }
    // merge repeats (the runic altar lists the same ingredient several times)
    let merged = {}, order = []
    inputs.forEach(i => { if (!merged[i.k]) { merged[i.k] = 0; order.push(i.k) } merged[i.k] += i.n })
    let consumed = [], kept = keep.slice()
    order.forEach(k => { if (isRune(k)) kept.push(k); else consumed.push(`${merged[k]}x ${k}`) })
    if (!consumed.length && !kept.length) { skipped++; return }
    let dur = Math.min(2400, 100 + Math.floor((mana || 0) / 5000))
    if (kind === 'daisy') dur = 200
    if (kind === 'elven') dur = 40
    let r = event.recipes.gtapexadditions.mana_nexus(`apex:${kind}/${src.replace(':', '/')}`)
    if (consumed.length) r.itemInputs(consumed)
    kept.filter((k, i, a) => a.indexOf(k) === i).forEach(k => r.notConsumable(k))
    r.itemOutputs(outputs).duration(dur).EUt(APEX_NEXUS_EUT[kind] || 30)
    if (extra) extra(r)
    // elven trades share inputs with pool infusions: circuit 3 picks the trade
    if (kind === 'elven') r.circuit(3)
    r.addData('mana', Math.floor(mana || 0)).addData('nexus_tier', tier)
    made[kind] = (made[kind] || 0) + 1
  }

  let each = (type, fn) => event.forEachRecipe({ type: type }, r => {
    let j
    try { j = JSON.parse(String(r.json.toString())) } catch (e) { return }
    try { fn(j, String(r.getId())) } catch (e) { skipped++ }
  })

  each('botania:mana_infusion', (j, id) => {
    let keep = []
    if (j.catalyst) { let c = blockItem(j.catalyst); if (c) keep.push(c.replace(/^1x /, '')); else { skipped++; return } }
    add('infusion', id, 1, [ing(j.input)], keep, [out(j.output)], j.mana)
  })
  each('botania:runic_altar', (j, id) => add('runic', id, 1, j.ingredients.map(ing), [], [out(j.output)], j.mana))
  each('botania:terra_plate', (j, id) => add('terra', id, 2, j.ingredients.map(ing), [], [out(j.result || j.output)], j.mana))
  each('botania:elven_trade', (j, id) => add('elven', id, 2, j.ingredients.map(ing), [], (j.output || []).map(out),
    500 * j.ingredients.length))
  let daisy = (j, id) => {
    let i = blockItem(j.input), o = blockItem(j.output)
    if (!i || !o) { skipped++; return }
    add('daisy', id, 1, [{ k: i.replace(/^1x /, ''), n: 1 }], [], [o], 0)
  }
  each('botania:pure_daisy', daisy)
  each('botania:state_copying_pure_daisy', daisy)
  each('botania:petal_apothecary', (j, id) => {
    let ins = j.ingredients.map(ing)
    if (j.reagent) ins.push(ing(j.reagent))
    add('apothecary', id, 1, ins, [], [out(j.output)], 0, r => r.inputFluids('minecraft:water 1000'))
  })
  each('mythicbotany:infuser', (j, id) => add('infuser', id, 3, j.ingredients.map(ing), [], [out(j.output)], j.mana))
  each('mythicbotany:rune_ritual', (j, id) => {
    let ins = (j.inputs || []).map(ing)
    if (j.center) ins.push(ing(j.center))
    let keep = []
    ;(j.runes || []).forEach(rn => {
      let x = ing(rn.rune)
      if (!x) return
      if (rn.consume) ins.push(x); else keep.push(x.k)
    })
    add('ritual', id, 4, ins, keep, (j.outputs || []).map(out), j.mana)
  })
  // Botanic Additions (only when its recipes are visible to KubeJS)
  ;['botanicadds:gaia_plate', 'botanicadds:elven_altar', 'botanicadds:dreaming_pool'].forEach(t => each(t, (j, id) => {
    let gaia = /gaia/.test(t)
    add(gaia ? 'gaia' : 'ba', id, gaia ? 5 : 4, (j.ingredients || [j.input]).map(ing), [], [out(j.result || j.output)], j.mana)
  }))

  console.info(`[APEX-NEXUS] Mana Nexus recipes: ${JSON.stringify(made)}; skipped ${skipped} (missing items, block-only inputs)`)
})
