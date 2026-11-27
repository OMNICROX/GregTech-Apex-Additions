// priority: 10
// Apex Ascension - Blood Magic tier gating (after GT New Horizons / NHCore's ScriptBloodMagic).
//  1. Blood Orbs: the altar soaks a Raw Blood Orb (GregTech assembler, GTAA) instead of a diamond / redstone block /
//     gold block / weak blood shard / dungeon metal. Weak LV, Apprentice MV, Magician HV, Master EV, Archmage IV.
//  2. Blood Magic's crafting that needs a Blood Orb (runes, ritual stones, incense altar...) moves into the GregTech
//     assembler at the orb's tier; the orb stays in the machine (not consumed). Runes without an orb follow their
//     tier (speed / capacity / displacement MV, the tier-2 runes IV), the Blood Altar itself is LV.
// Turn it off: set APEX_BM_GATING to false and /reload.
const APEX_BM_GATING = true

// orb output -> raw orb input (read by apex_bloody_hell.js too, so the Bloody Hell soaks the same)
global.APEX_BM_ORB_INPUTS = APEX_BM_GATING ? {
  'bloodmagic:weakbloodorb': 'gtapexadditions:raw_blood_orb_1',
  'bloodmagic:apprenticebloodorb': 'gtapexadditions:raw_blood_orb_2',
  'bloodmagic:magicianbloodorb': 'gtapexadditions:raw_blood_orb_3',
  'bloodmagic:masterbloodorb': 'gtapexadditions:raw_blood_orb_4',
  'bloodmagic:archmagebloodorb': 'gtapexadditions:raw_blood_orb_5'
} : {}

ServerEvents.recipes(event => {
  if (!APEX_BM_GATING || !Platform.isLoaded('bloodmagic')) return
  const VA = [8, 30, 120, 480, 1920, 7680]
  const ORBS = ['bloodmagic:weakbloodorb', 'bloodmagic:apprenticebloodorb', 'bloodmagic:magicianbloodorb',
    'bloodmagic:masterbloodorb', 'bloodmagic:archmagebloodorb']
  let parse = r => { try { return JSON.parse(String(r.json.toString())) } catch (e) { return null } }
  let altar = 0, moved = 0, skipped = 0

  // 1. orbs from raw orbs
  event.forEachRecipe({ type: 'bloodmagic:altar' }, r => {
    let j = parse(r); if (!j || !j.output) return
    let raw = global.APEX_BM_ORB_INPUTS[j.output.item]
    if (!raw || !Item.exists(raw)) return
    j.input = { item: raw }
    event.remove({ id: r.getId() })
    event.custom(j).id(`apex:bloodmagic/altar/${j.output.item.split(':')[1]}`)
    altar++
  })

  // 2. orb crafting into the assembler
  let tierFor = (j, id) => {
    let orb = 0
    Object.values(j.key || {}).forEach(v => { if (v && v.orb_tier) orb = Math.max(orb, v.orb_tier) })
    if (orb > 0) return orb
    let out = j.result ? j.result.item : ''
    if (/rune2$/.test(out)) return 5
    if (/rune$/.test(out)) return 2
    if (out === 'bloodmagic:altar') return 1
    return 0
  }
  event.forEachRecipe({ mod: 'bloodmagic', type: 'minecraft:crafting_shaped' }, r => {
    let j = parse(r); if (!j || !j.result || !j.pattern) return
    let tier = tierFor(j, String(r.getId()))
    if (tier <= 0) return
    let counts = {}, orbTier = 0, bad = false
    j.pattern.join('').split('').forEach(ch => {
      if (ch === ' ') return
      let v = j.key[ch]
      if (!v) { bad = true; return }
      if (v.orb_tier) { orbTier = Math.max(orbTier, v.orb_tier); return }
      let k = v.tag ? '#' + v.tag : v.item
      if (!k) { bad = true; return }
      counts[k] = (counts[k] || 0) + 1
    })
    if (bad) { skipped++; return }
    let inputs = Object.keys(counts).map(k => `${counts[k]}x ${k}`)
    if (inputs.length > 9) { skipped++; return }
    let out = `${j.result.count || 1}x ${j.result.item}`
    try {
      let g = event.recipes.gtceu.assembler(`apex:bloodmagic/${String(r.getId()).split(':')[1].replace(/\//g, '_')}`)
        .itemInputs(inputs).itemOutputs(out).duration(100 + 50 * tier).EUt(VA[tier])
      if (orbTier > 0) g.notConsumable(Ingredient.of(ORBS.slice(orbTier - 1)))
      event.remove({ id: r.getId() })
      moved++
    } catch (e) {
      skipped++
      console.warn(`[APEX-BM] ${r.getId()}: ${e}`)
    }
  })
  console.info(`[APEX-BM] Blood Magic gating: ${altar} orb altar recipes, ${moved} crafting recipes in the assembler` +
    (skipped ? `, ${skipped} skipped` : ''))
})
