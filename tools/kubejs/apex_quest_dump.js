// priority: -1000
// Apex Ascension: one-off dump for building the quest book. Writes kubejs/apex_quest_recipes.json and
// kubejs/apex_quest_items.json, then can be disabled. Reads only; changes nothing.

const APEX_QD = { recipes: [], tags: {}, failed: 0, raw: [] }
const OUT_KEYS = { result: 1, results: 1, output: 1, outputs: 1, tickOutputs: 1, output_item: 1, outputItems: 1, item_output: 1, byproduct: 1, byproducts: 1, secondaries: 1 }

ServerEvents.recipes(event => {
  let walk = (node, out, acc) => {
    if (node === null || node === undefined) return
    if (Array.isArray(node)) { node.forEach(n => walk(n, out, acc)); return }
    if (typeof node !== 'object') return
    Object.keys(node).forEach(k => {
      let v = node[k], o = out || !!OUT_KEYS[k]
      if (typeof v === 'string' && v.indexOf(':') > 0) {
        // KubeJS-made GT recipes keep ingredients as strings like "4x ae2:controller" or "#forge:plates/steel"
        let s = v.replace(/^\d+x /, '')
        let isId = /^#?[a-z0-9_.-]+:[a-z0-9_\/.-]+$/.test(s)
        if (k === 'item' || k === 'id' || ((k === 'content' || k === 'ingredient' || k === 'value') && isId)) {
          if (s.charAt(0) === '#') { if (!o) acc.t.push(s.substring(1)) } else (o ? acc.o : acc.i).push(s)
        }
        else if (k === 'tag') { if (!o) acc.t.push(v) }
        else if (k === 'fluid') (o ? acc.fo : acc.fi).push(v)
      } else if (k === 'eu' && !o && Array.isArray(v)) {
        v.forEach(c => { if (c && typeof c.content === 'number') acc.eu = Math.max(acc.eu, c.content) })
      } else walk(v, o, acc)
    })
  }
  event.forEachRecipe({}, r => {
    try {
      let j = JSON.parse(String(r.json))
      let acc = { i: [], o: [], t: [], fi: [], fo: [], eu: 0 }
      walk(j, false, acc)
      if (typeof j.result === 'string' && j.result.indexOf(':') > 0) acc.o.push(j.result)
      if (APEX_QD.raw.length < 40 && String(r.getId()).indexOf('kjs') >= 0) APEX_QD.raw.push(j)
      if (!acc.o.length && !acc.fo.length) return
      APEX_QD.recipes.push({ id: String(r.getId()), t: String(r.getType()), i: acc.i, t2: acc.t, o: acc.o, fi: acc.fi, fo: acc.fo, eu: acc.eu, d: j.duration || 0 })
      acc.t.forEach(t => { APEX_QD.tags[t] = 1 })
    } catch (e) { APEX_QD.failed++ }
  })
  console.info(`[APEX-QUESTDUMP] ${APEX_QD.recipes.length} recipes read, ${APEX_QD.failed} failed`)
})

ServerEvents.loaded(event => {
  let tags = {}
  Object.keys(APEX_QD.tags).forEach(t => {
    try { tags[t] = Ingredient.of('#' + t).getItemIds().toArray().map(x => String(x)) } catch (e) {}
  })
  JsonIO.write('kubejs/apex_quest_recipes.json', { recipes: APEX_QD.recipes, tags: tags, raw: APEX_QD.raw })
  let items = []
  Ingredient.all.getItemIds().forEach(id => {
    let name = String(id)
    try { name = String(Item.of(id).getHoverName().getString()) } catch (e) {}
    items.push([String(id), name])
  })
  JsonIO.write('kubejs/apex_quest_items.json', { items: items })
  console.info(`[APEX-QUESTDUMP] wrote ${APEX_QD.recipes.length} recipes, ${Object.keys(tags).length} tags, ${items.length} items`)
})
