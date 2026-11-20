// priority: -90
// Apex Ascension 1.2 - Ad Astra gated by GregTech voltage tiers (GregTech Apex Additions)
//   Moon (tier 1 rocket, steel)        HV
//   Mars (tier 2, desh)                EV
//   Venus / Mercury (tier 3, ostrum)   IV
//   Glacio (tier 4, calorite)          LuV
//   Jupiter + moons (tier 5 rocket)    ZPM   (Ad Astra: Rocketed + Ad Astra Extra)
//   Saturn (tier 6)                    UV
//   Uranus, Neptune (tier 7)           UHV
// Rocket parts, the launch pad, the NASA workbench and the main machines move from the crafting table to GT
// assemblers: the original parts plus that tier's circuits, pumps and plates. Rockets themselves are still put
// together in the NASA workbench. Fuel comes from GregTech: oil refining is removed and GT rocket fuel works as
// rocket fuel.

const APEX_AA_TIERS = {
  hv:  { eut: 480,   circuit: '#gtceu:circuits/hv',  pump: 'gtceu:hv_electric_pump',  motor: 'gtceu:hv_electric_motor',  plate: '#forge:plates/stainless_steel' },
  ev:  { eut: 1920,  circuit: '#gtceu:circuits/ev',  pump: 'gtceu:ev_electric_pump',  motor: 'gtceu:ev_electric_motor',  plate: '#forge:plates/titanium' },
  iv:  { eut: 7680,  circuit: '#gtceu:circuits/iv',  pump: 'gtceu:iv_electric_pump',  motor: 'gtceu:iv_electric_motor',  plate: '#forge:plates/tungsten_steel' },
  luv: { eut: 30720, circuit: '#gtceu:circuits/luv', pump: 'gtceu:luv_electric_pump', motor: 'gtceu:luv_electric_motor', plate: '#forge:plates/rhodium_plated_palladium' },
  mv:  { eut: 120,   circuit: '#gtceu:circuits/mv',  pump: 'gtceu:mv_electric_pump',  motor: 'gtceu:mv_electric_motor',  plate: '#forge:plates/steel' }
}

// output -> [tier, extra GT parts: 'pump' | 'motor' | 'plates' | 'hull' | 'arm', seconds]
const APEX_AA_GATED = {
  'ad_astra:steel_engine':    ['hv',  ['pump', 'plates'], 20],
  'ad_astra:steel_tank':      ['hv',  ['pump', 'plates'], 20],
  'ad_astra:rocket_nose_cone':['hv',  ['plates'], 15],
  'ad_astra:rocket_fin':      ['hv',  ['plates'], 10],
  'ad_astra:launch_pad':      ['hv',  ['plates'], 15],
  'ad_astra:nasa_workbench':  ['hv',  ['hull', 'arm'], 30],
  'ad_astra:desh_engine':     ['ev',  ['pump', 'plates'], 30],
  'ad_astra:desh_tank':       ['ev',  ['pump', 'plates'], 30],
  'ad_astra:tier_1_rover':    ['ev',  ['motor'], 30],
  'ad_astra:ostrum_engine':   ['iv',  ['pump', 'plates'], 40],
  'ad_astra:ostrum_tank':     ['iv',  ['pump', 'plates'], 40],
  'ad_astra:calorite_engine': ['luv', ['pump', 'plates'], 50],
  'ad_astra:calorite_tank':   ['luv', ['pump', 'plates'], 50],
  'ad_astra:jet_suit_helmet': ['iv',  ['motor'], 40],
  'ad_astra:jet_suit':        ['iv',  ['motor'], 40],
  'ad_astra:jet_suit_pants':  ['iv',  ['motor'], 40],
  'ad_astra:jet_suit_boots':  ['iv',  ['motor'], 40],
  'ad_astra:oxygen_loader':   ['mv',  ['hull', 'pump'], 15],
  'ad_astra:fuel_refinery':   ['mv',  ['hull', 'pump'], 15],
  'ad_astra:oxygen_distributor': ['hv', ['hull', 'pump'], 20],
  'ad_astra:gravity_normalizer': ['ev', ['hull', 'motor'], 30],
  'ad_astra:cryo_freezer':    ['hv',  ['hull', 'pump'], 20],
  'ad_astra:energizer':       ['ev',  ['hull'], 30]
}

// free power that skips GT progression, same call as Cyclic and ExU
const APEX_AA_REMOVED = ['ad_astra:coal_generator']

// tier 5-7 rockets (Ad Astra: Rocketed): previous rocket + calorite parts + that voltage's GT parts
const APEX_AA_ROCKETS = [
  ['ad_astra_rocketed:tier_5_rocket', 'ad_astra:tier_4_rocket',          'zpm', 'naquadah_alloy', 122880],
  ['ad_astra_rocketed:tier_6_rocket', 'ad_astra_rocketed:tier_5_rocket', 'uv',  'tritanium',      491520],
  ['ad_astra_rocketed:tier_7_rocket', 'ad_astra_rocketed:tier_6_rocket', 'uhv', 'neutronium',     1966080]
]

ServerEvents.tags('item', event => {
  // Avaritia's infinity armor works in space (same as the Re-Avaritia space patch, for AvaritiaNeo)
  const inf = ['avaritia:infinity_helmet', 'avaritia:infinity_chestplate', 'avaritia:infinity_leggings',
    'avaritia:infinity_pants', 'avaritia:infinity_boots'].filter(id => Item.exists(id))
  ;['ad_astra:space_resistant_armor', 'ad_astra:oxygen_supplying_armor', 'ad_astra:heat_resistant_armor',
    'ad_astra:freeze_resistant_armor'].forEach(tag => inf.forEach(id => event.add(tag, id)))
})

ServerEvents.tags('fluid', event => {
  // GT rocket fuel fuels Ad Astra rockets (all tiers accept #ad_astra:fuel)
  event.add('ad_astra:fuel', 'gtceu:rocket_fuel')
})

ServerEvents.recipes(event => {
  let gated = 0
  let missing = []
  Object.keys(APEX_AA_GATED).forEach(out => {
    if (!Item.exists(out)) { missing.push(out); return }
    let spec = APEX_AA_GATED[out]
    let t = APEX_AA_TIERS[spec[0]]
    let counts = {}
    let found = false
    event.forEachRecipe({ output: out, type: 'minecraft:crafting_shaped' }, r => {
      if (found) return
      let j = JSON.parse(String(r.json.toString()))
      let n = {}
      j.pattern.join('').split('').forEach(ch => { if (ch !== ' ') n[ch] = (n[ch] || 0) + 1 })
      Object.keys(j.key).forEach(k => {
        let v = j.key[k]
        if (Array.isArray(v)) v = v[0]
        let id = v.item ? v.item : '#' + v.tag
        counts[id] = (counts[id] || 0) + (n[k] || 0)
      })
      found = true
    })
    if (!found) { missing.push(out + ' (no crafting recipe)'); return }
    let inputs = Object.keys(counts).filter(id => counts[id] > 0).map(id => `${counts[id]}x ${id}`)
    spec[1].forEach(x => {
      if (x === 'pump') inputs.push(`2x ${t.pump}`)
      if (x === 'motor') inputs.push(`2x ${t.motor}`)
      if (x === 'plates') inputs.push(`4x ${t.plate}`)
      if (x === 'hull') inputs.push(`1x gtceu:${spec[0]}_machine_hull`)
      if (x === 'arm') inputs.push(`2x gtceu:${spec[0]}_robot_arm`)
    })
    inputs.push(`2x ${t.circuit}`)
    event.remove({ output: out, not: { mod: 'gtceu' } })
    event.recipes.gtceu.assembler(`apex:ad_astra/${out.split(':')[1]}`)
      .itemInputs(inputs)
      .itemOutputs(out)
      .duration(spec[2] * 20)
      .EUt(t.eut)
    gated++
  })
  let rockets = 0
  APEX_AA_ROCKETS.forEach(r => {
    if (!Item.exists(r[0]) || !Item.exists(r[1])) return
    event.remove({ output: r[0], not: { mod: 'gtceu' } })
    event.recipes.gtceu.assembler(`apex:ad_astra/${r[0].split(':')[1]}`)
      .itemInputs([`1x ${r[1]}`, '2x ad_astra:calorite_engine', '2x ad_astra:calorite_tank',
        `8x #forge:plates/${r[3]}`, `2x gtceu:${r[2]}_electric_pump`, `2x gtceu:${r[2]}_field_generator`,
        `4x #gtceu:circuits/${r[2]}`])
      .itemOutputs(r[0])
      .duration(60 * 20)
      .EUt(r[4])
    rockets++
  })
  APEX_AA_REMOVED.forEach(id => event.remove({ output: id }))
  // rocket fuel comes from GregTech now
  event.remove({ id: 'ad_astra:refining/fuel_from_refining_oil' })
  console.info(`[APEX-ADASTRA] ${gated} Ad Astra items and ${rockets} high-tier rockets moved to GT assemblers` +
    (missing.length ? `; skipped: ${missing.join(', ')}` : ''))
})
