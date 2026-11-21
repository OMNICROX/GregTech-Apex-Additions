// priority: -85
// Apex Ascension - Botania gated by GregTech voltage (GregTech Apex Additions)
//   pre-LV : Pure Daisy, Petal Apothecary, Mana Pool, Spreaders, generating flowers, Manasteel (natural)
//   LV     : Runic Altar, Mechanical Daisy / Apothecary
//   MV     : Terrestrial Agglomeration Plate, Mechanical Mana Pool / Runic Altar
//   HV     : Elven Gateway Core, Mana Battery, Alfheim Market, Mechanical Brewery
//   EV     : Gaia Pylon, Industrial Agglomeration Factory, Mana Infuser (Mythic Botany)
//   IV     : Alfsteel Pylon, Elven Altar, Dreaming Mana Pool (Botanic Additions)
//   LuV    : Gaia Agglomeration Plate, Mana Tesseract, Terra Catalyst, Gaiasteel Pylon
// The item keeps its own ingredients and is made in a GT assembler with that tier's circuits and parts.
// Exploits removed: Orechid / Orechid Ignem (stone -> ores skips GT veins), Elven Mana Fluxfield (mana -> FE).
// Also the Mana Nexus' own blocks (they need Botania items, so they live here instead of the GTAA jar).

const APEX_BOT_TIERS = {
  lv:  { eut: 30,    pump: 'gtceu:lv_electric_pump',  plate: '#forge:plates/steel' },
  mv:  { eut: 120,   pump: 'gtceu:mv_electric_pump',  plate: '#forge:plates/aluminium' },
  hv:  { eut: 480,   pump: 'gtceu:hv_electric_pump',  plate: '#forge:plates/stainless_steel' },
  ev:  { eut: 1920,  pump: 'gtceu:ev_electric_pump',  plate: '#forge:plates/titanium' },
  iv:  { eut: 7680,  pump: 'gtceu:iv_electric_pump',  plate: '#forge:plates/tungsten_steel' },
  luv: { eut: 30720, pump: 'gtceu:luv_electric_pump', plate: '#forge:plates/rhodium_plated_palladium' }
}

// output -> [tier, extra: 'plates' | 'pump' | 'hull' | 'field', seconds]
const APEX_BOT_GATED = {
  'botania:runic_altar':                             ['lv',  ['plates'], 15],
  'botanicalmachinery:mechanical_daisy':             ['lv',  ['hull'], 20],
  'botanicalmachinery:mechanical_apothecary':        ['lv',  ['hull', 'pump'], 20],
  'botania:terra_plate':                             ['mv',  ['plates'], 30],
  'botanicalmachinery:mechanical_mana_pool':         ['mv',  ['hull', 'pump'], 30],
  'botanicalmachinery:mechanical_runic_altar':       ['mv',  ['hull'], 30],
  'botania:alfheim_portal':                          ['hv',  ['plates', 'field'], 40],
  'botanicalmachinery:mana_battery':                 ['hv',  ['hull'], 40],
  'botanicalmachinery:alfheim_market':               ['hv',  ['hull'], 40],
  'botanicalmachinery:mechanical_brewery':           ['hv',  ['hull', 'pump'], 40],
  'botania:gaia_pylon':                              ['ev',  ['plates', 'field'], 50],
  'botanicalmachinery:industrial_agglomeration_factory': ['ev', ['hull', 'field'], 60],
  'mythicbotany:mana_infuser':                       ['ev',  ['hull', 'field'], 60],
  'mythicbotany:alfsteel_pylon':                     ['iv',  ['plates', 'field'], 60],
  'botanicadds:elven_altar':                         ['iv',  ['hull', 'field'], 60],
  'botanicadds:dreaming_pool':                       ['iv',  ['hull', 'pump'], 60],
  'botanicadds:gaia_plate':                          ['luv', ['plates', 'field'], 80],
  'botanicadds:mana_tesseract':                      ['luv', ['hull', 'field'], 80],
  'botanicadds:terra_catalyst':                      ['luv', ['field'], 80],
  'botanicadds:gaiasteel_pylon':                     ['luv', ['plates', 'field'], 80]
}

// Botanic Additions adds its crafting recipes through HammerLib, out of KubeJS' reach; GTAA removes them
// (BotanicAdditionsCompat) and these are their ingredients for the GT versions
const APEX_BOT_HAMMERLIB = {
  'botanicadds:mana_tesseract':  { 'botanicadds:rune_tp': 1, 'botanicadds:dreamrock': 1, 'botania:terrasteel_ingot': 1, 'botania:red_string': 1 },
  'botanicadds:terra_catalyst':  { 'botania:shimmerrock': 4, 'botania:life_essence': 1, 'botania:terrasteel_ingot': 3, 'botania:alchemy_catalyst': 1 },
  'botanicadds:gaia_plate':      { 'botanicadds:elven_lapis_block': 3, 'botanicadds:rune_energy': 1, 'botania:terra_plate': 1, 'botanicadds:rune_tp': 1, 'botanicadds:gaia_shard': 3 },
  'botanicadds:gaiasteel_pylon': { 'botanicadds:gaiasteel_nugget': 2, 'botania:glimmering_dreamwood': 2, 'botania:natura_pylon': 1 },
  'botanicadds:elven_altar':     { 'botanicadds:dreamrock': 7, 'botania:dragonstone': 1, 'botania:runic_altar': 1 },
  'botanicadds:dreaming_pool':   { 'botania:terrasteel_nugget': 3, 'botanicadds:dreamrock': 5, 'botania:mana_pool': 1 }
}

const APEX_BOT_REMOVED = ['botania:orechid', 'botania:floating_orechid', 'botania:orechid_ignem', 'botania:floating_orechid_ignem',
  'botanicadds:elven_fluxfield']

ServerEvents.recipes(event => {
  let has = id => { try { return Item.exists(id) } catch (e) { return false } }
  let gated = 0, missing = []

  Object.keys(APEX_BOT_GATED).forEach(out => {
    if (!has(out)) { missing.push(out); return }
    let spec = APEX_BOT_GATED[out], t = APEX_BOT_TIERS[spec[0]]
    let counts = {}, found = false, outCount = 1
    event.forEachRecipe({ output: out }, r => {
      if (found) return
      let j
      try { j = JSON.parse(String(r.json.toString())) } catch (e) { return }
      let type = String(j.type || '')
      if (type.indexOf('crafting_shaped') >= 0 && j.pattern && j.key) {
        let n = {}
        j.pattern.join('').split('').forEach(ch => { if (ch !== ' ') n[ch] = (n[ch] || 0) + 1 })
        Object.keys(j.key).forEach(k => {
          let v = j.key[k]
          if (Array.isArray(v)) v = v[0]
          let id = v.item ? v.item : '#' + v.tag
          counts[id] = (counts[id] || 0) + (n[k] || 0)
        })
        found = true
      } else if (type.indexOf('crafting_shapeless') >= 0 && j.ingredients) {
        j.ingredients.forEach(v => {
          if (Array.isArray(v)) v = v[0]
          let id = v.item ? v.item : '#' + v.tag
          counts[id] = (counts[id] || 0) + 1
        })
        found = true
      }
      if (found && j.result && j.result.count) outCount = j.result.count
    })
    if (!found && APEX_BOT_HAMMERLIB[out]) {
      let m = APEX_BOT_HAMMERLIB[out]
      Object.keys(m).forEach(id => { counts[id] = m[id] })
      found = Object.keys(m).every(id => has(id))
    }
    if (!found) { missing.push(out + ' (no crafting recipe visible)'); return }
    let inputs = Object.keys(counts).filter(id => counts[id] > 0).map(id => `${counts[id]}x ${id}`)
    spec[1].forEach(x => {
      if (x === 'plates') inputs.push(`4x ${t.plate}`)
      if (x === 'pump') inputs.push(`1x ${t.pump}`)
      if (x === 'hull') inputs.push(`1x gtceu:${spec[0]}_machine_hull`)
      if (x === 'field') inputs.push(`1x gtceu:${spec[0]}_field_generator`)
    })
    inputs.push(`2x #gtceu:circuits/${spec[0]}`)
    event.remove({ output: out, type: 'minecraft:crafting_shaped' })
    event.remove({ output: out, type: 'minecraft:crafting_shapeless' })
    event.recipes.gtceu.assembler(`apex:botania/${out.replace(':', '/')}`)
      .itemInputs(inputs).itemOutputs(`${outCount}x ${out}`).duration(spec[2] * 20).EUt(t.eut)
    gated++
  })

  APEX_BOT_REMOVED.forEach(id => event.remove({ output: id }))
  event.remove({ type: 'botania:orechid' })
  event.remove({ type: 'botania:orechid_ignem' })

  // ---- Mana Nexus blocks ----
  let nexus = 0
  let gt = (id, inputs, output, eut, secs) => {
    if (inputs.some(i => { let m = i.match(/^\d+x ([^#].*)$/); return m && !has(m[1]) }) || !has(output.replace(/^\d+x /, ''))) return
    event.recipes.gtceu.assembler(`apex:mana_nexus/${id}`).itemInputs(inputs).itemOutputs(output).duration(secs * 20).EUt(eut)
    nexus++
  }
  gt('casing', ['4x botania:livingrock_bricks', '1x botania:manasteel_ingot', '1x #forge:plates/steel'], '2x gtapexadditions:mana_nexus_casing', 30, 5)
  gt('mana_hatch', ['1x gtceu:lv_machine_hull', '1x botania:mana_pool', '1x botania:mana_spreader', '2x #gtceu:circuits/lv'], '1x gtapexadditions:mana_hatch', 30, 10)
  gt('controller', ['1x gtceu:lv_machine_hull', '1x botania:mana_pool', '1x botania:runic_altar', '4x botania:mana_glass', '4x #gtceu:circuits/lv'], '1x gtapexadditions:mana_nexus', 30, 30)
  gt('core_t1', ['1x botania:manasteel_block', '1x botania:mana_pearl', '2x #gtceu:circuits/lv'], '1x gtapexadditions:mana_core_t1', 30, 10)
  gt('core_t2', ['1x botania:terrasteel_block', '1x gtapexadditions:mana_core_t1', '2x #gtceu:circuits/mv'], '1x gtapexadditions:mana_core_t2', 120, 15)
  gt('core_t3', ['1x botania:elementium_block', '1x botania:pixie_dust', '1x gtapexadditions:mana_core_t2', '2x #gtceu:circuits/ev'], '1x gtapexadditions:mana_core_t3', 1920, 20)
  gt('core_t4', ['1x mythicbotany:alfsteel_block', '1x gtapexadditions:mana_core_t3', '2x #gtceu:circuits/iv'], '1x gtapexadditions:mana_core_t4', 7680, 30)
  gt('core_t5', ['1x botanicadds:gaiasteel_block', '1x botania:life_essence', '1x gtapexadditions:mana_core_t4', '2x #gtceu:circuits/luv'], '1x gtapexadditions:mana_core_t5', 30720, 40)

  console.info(`[APEX-BOTANIA] ${gated} Botania items moved to GT assemblers, ${nexus} Mana Nexus recipes, Orechid / Fluxfield removed` +
    (missing.length ? `; skipped: ${missing.join(', ')}` : ''))
})
