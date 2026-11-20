// priority: -85
// Apex Ascension 1.2 - Tinkers tool parts from GregTech machines
// For every Tinkers metal that is also a GregTech material, every castable part can be made:
//   Fluid Solidifier : molten GT metal (144 mB per ingot of part cost) + the gold cast (not used up). Fast.
//   Extruder         : the ingots + the gold cast (not used up). Slower and uses more power.
// Voltage follows the metal's GT blast temperature, like the heat tiers in apex_tinkers_heat.js.

const APEX_PARTS = {
  RENAME: { aluminum: 'aluminium' },
  EUT: [30, 120, 480, 1920, 7680, 30720, 122880, 491520, 1966080],
  // GT blast temperature (K) -> GT voltage tier index (no blast furnace needed = LV)
  tierOf: b => b <= 0 ? 0 : b <= 1800 ? 1 : b <= 2700 ? 2 : b <= 3600 ? 3 : b <= 4500 ? 5 : b <= 5400 ? 6 : b <= 7200 ? 7 : 8
}

ServerEvents.recipes(event => {
  let mm = null
  try { mm = Java.loadClass('com.gregtechceu.gtceu.api.GTCEuAPI').materialManager } catch (e) { console.warn('[APEX-PARTS] GT material manager not found'); return }

  let FLUIDS = Java.loadClass('net.minecraftforge.registries.ForgeRegistries').FLUIDS
  let INGOT = Java.loadClass('com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey').INGOT
  // material id -> { metal, temperature }
  let mats = {}
  event.forEachRecipe({ type: 'tconstruct:material_fluid' }, r => {
    let j
    try { j = JSON.parse(String(r.json)) } catch (e) { return }
    let tag = j.fluid && (j.fluid.tag || j.fluid.fluid)
    let m = tag ? String(tag).match(/molten_([a-z0-9_]+)$/) : null
    if (!m || !j.output) return
    let out = String(j.output)
    if (mats[out]) return
    mats[out] = { metal: m[1], temperature: j.temperature || 800 }
  })

  // part item -> cost (gold-cast table recipes)
  let parts = {}
  event.forEachRecipe({ type: 'tconstruct:table_casting_material' }, r => {
    let j
    try { j = JSON.parse(String(r.json)) } catch (e) { return }
    let cast = j.cast && j.cast.tag ? String(j.cast.tag) : ''
    if (cast.indexOf('casts/multi_use/') < 0) return
    let result = String(j.result && (j.result.item || j.result))
    if (parts[result]) return
    let castItem = result + '_cast'
    if (!Item.exists(castItem)) return
    parts[result] = { cost: j.item_cost || 1, cast: castItem }
  })

  let made = 0
  Object.keys(mats).forEach(matId => {
    let info = mats[matId]
    let gt = null
    try { gt = mm.getMaterial('gtceu:' + (APEX_PARTS.RENAME[info.metal] || info.metal)) } catch (e) { gt = null }
    if (!gt) return
    let fluid = null
    let fl = null
    let key = null
    try { fl = gt.getFluid() } catch (e) { fl = null }
    try { key = fl ? FLUIDS.getKey(fl) : null } catch (e) { key = null }
    if (key && String(key) !== 'minecraft:empty') fluid = String(key)
    let metal = false
    try { metal = gt.hasProperty(INGOT) } catch (e) { metal = false }
    if (!metal) return
    let ingotTag = 'forge:ingots/' + (APEX_PARTS.RENAME[info.metal] || info.metal)
    let blast = 0
    try { blast = gt.getBlastTemperature() } catch (e) { blast = 0 }
    let tier = APEX_PARTS.tierOf(blast)
    let hasIngots = false
    try { hasIngots = !Ingredient.of('#' + ingotTag).isEmpty() } catch (e) { hasIngots = false }
    if (matId.indexOf('#') >= 0) return
    let safe = matId.replace(/[^a-z0-9_]/g, '_')
    Object.keys(parts).forEach(part => {
      let p = parts[part]
      let out = Item.of(part, `{Material:"${matId}"}`)
      let pname = part.replace(':', '/') // full id: two mods can both have a hammer_head
      if (fluid) {
        event.recipes.gtceu.fluid_solidifier(`apex:tinkers_parts/solidify/${safe}/${pname}`)
          .notConsumable(p.cast)
          .inputFluids(Fluid.of(fluid, 144 * p.cost))
          .itemOutputs(out)
          .duration(40 * p.cost)
          .EUt(APEX_PARTS.EUT[tier])
        made++
      }
      if (!hasIngots) return
      event.recipes.gtceu.extruder(`apex:tinkers_parts/extrude/${safe}/${pname}`)
        .notConsumable(p.cast)
        .itemInputs(`${p.cost}x #${ingotTag}`)
        .itemOutputs(out)
        .duration(120 * p.cost)
        .EUt(APEX_PARTS.EUT[Math.min(tier + 1, APEX_PARTS.EUT.length - 1)])
      made++
    })
  })
  console.info(`[APEX-PARTS] ${made} GregTech recipes for ${Object.keys(parts).length} Tinkers parts`)
})
