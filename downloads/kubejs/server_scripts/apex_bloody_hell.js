// priority: -60
// Apex Ascension - Bloody Hell (GregTech Apex Additions, after Twist Space Technology).
// Every Blood Altar, Alchemy Table and Alchemy Array recipe becomes a Bloody Hell recipe:
//   altar  -> circuit 1, the altar's LE as Life Essence fluid, needs the recipe's altar tier
//   table  -> circuit 2, the table's LP as Life Essence, needs a Blood Orb of the recipe's tier in an input bus
//   array  -> circuit 11, 30,000 mB of Life Essence (Twist's Ritual of Binding cost)
// Duration: Life Essence / 10 ticks (Twist's soaking speed). No power.

const APEX_BH_SOAK = 10
const APEX_BH_LE = 'bloodmagic:life_essence_fluid'

ServerEvents.recipes(event => {
  if (!Platform.isLoaded('bloodmagic')) return
  let made = { altar: 0, table: 0, array: 0 }, skipped = 0
  let ing = v => {
    if (!v) return null
    if (Array.isArray(v)) v = v[0]
    if (!v) return null
    if (v.tag) return '#' + v.tag
    if (v.item) return v.nbt ? Item.of(v.item, typeof v.nbt === 'string' ? v.nbt : JSON.stringify(v.nbt)) : v.item
    return null
  }
  let out = o => {
    if (!o || !o.item) return null
    let count = o.count || 1
    return o.nbt ? Item.of(o.item, count, typeof o.nbt === 'string' ? o.nbt : JSON.stringify(o.nbt)) : `${count}x ${o.item}`
  }
  let add = (kind, id, inputs, output, le, circuit, altar, orb) => {
    if (!output || inputs.some(i => i == null) || !inputs.length) { skipped++; return }
    try {
      let r = event.recipes.gtapexadditions.bloody_hell(`apex:bloody_hell/${kind}/${id.replace(':', '/')}`)
        .itemInputs(inputs)
        .itemOutputs(output)
        .inputFluids(Fluid.of(APEX_BH_LE, Math.max(1, le)))
        .circuit(circuit)
        .duration(Math.max(1, Math.floor(le / APEX_BH_SOAK)))
      if (altar > 0) r.addData('bh_altar', altar)
      if (orb > 0) r.addData('bh_orb', orb)
      made[kind]++
    } catch (e) {
      skipped++
      console.warn(`[APEX-BH] ${id}: ${e}`)
    }
  }
  let parse = r => { try { return JSON.parse(String(r.json.toString())) } catch (e) { return null } }

  event.forEachRecipe({ type: 'bloodmagic:altar' }, r => {
    let j = parse(r); if (!j) return
    // Blood Magic gating (apex_blood_magic_gating.js): orbs soak a GregTech Raw Blood Orb here too
    let raw = j.output && global.APEX_BM_ORB_INPUTS ? global.APEX_BM_ORB_INPUTS[j.output.item] : null
    let input = raw && Item.exists(raw) ? raw : ing(j.input)
    add('altar', String(r.getId()), [input], out(j.output), j.altarSyphon || 0, 1, (j.upgradeLevel || 0) + 1, 0)
  })
  event.forEachRecipe({ type: 'bloodmagic:alchemytable' }, r => {
    let j = parse(r); if (!j) return
    add('table', String(r.getId()), (j.input || []).map(ing), out(j.output), (j.syphon || 0), 2, 0, j.upgradeLevel || 0)
  })
  event.forEachRecipe({ type: 'bloodmagic:array' }, r => {
    let j = parse(r); if (!j) return
    if (!j.output) return
    add('array', String(r.getId()), [ing(j.baseinput), ing(j.addedinput)], out(j.output), 30000, 11, 0, 0)
  })
  console.info(`[APEX-BH] Bloody Hell: ${made.altar} altar, ${made.table} alchemy table, ${made.array} array recipes` +
    (skipped ? `, ${skipped} skipped` : ''))
})
