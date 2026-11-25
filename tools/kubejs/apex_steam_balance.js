// priority: -170
// Apex Ascension 1.3 - steam balance
// Steam competes with LV but shouldn't skip ahead of it.
//  - Steam Lava Maker: an LV assembler part (10 LV circuits + its steam parts), and it makes a tenth of the lava,
//    so it can't undercut the scarce-lava gating.
//  - Dimensionally Transcendent Steam Oven (524,288 parallel): an MV assembler part instead of 16 furnaces in a
//    compressor, so it arrives mid-MV, not on day two.
// (The Large Steam Solar Boiler is tuned in config/gtna.yaml: 900 mB per cell.)

ServerEvents.recipes(event => {
  let gt = event.recipes.gtceu

  // Steam Lava Maker
  event.remove({ output: 'gtna:steam_lava_maker' })
  gt.assembler('apex:steam/steam_lava_maker')
    .itemInputs('10x #gtceu:circuits/lv', 'gtna:stronze_wrapped_casing', '2x gtna:hydraulic_motor',
      '2x gtna:stronze_normal_fluid_pipe', '2x gtna:breel_normal_fluid_pipe', '2x gtna:precision_steam_component')
    .inputFluids(Fluid.of('gtceu:soldering_alloy', 288))
    .itemOutputs('gtna:steam_lava_maker')
    .duration(600).EUt(30)

  let lava = 0, todo = []
  let shrink = (node, f) => {
    if (Array.isArray(node)) { node.forEach(n => shrink(n, f)); return }
    if (!node || typeof node !== 'object') return
    Object.keys(node).forEach(k => {
      if (k === 'amount' && typeof node[k] === 'number') node[k] = Math.max(1, Math.floor(node[k] / f))
      else shrink(node[k], f)
    })
  }
  event.forEachRecipe({ type: 'gtna:lava_maker' }, r => {
    let j
    try { j = JSON.parse(String(r.json)) } catch (e) { return }
    ;['outputs', 'tickOutputs'].forEach(k => { if (j[k] && j[k].fluid) shrink(j[k].fluid, 10) })
    todo.push({ id: String(r.getId()), json: j })
  })
  todo.forEach(t => {
    try {
      event.remove({ id: t.id })
      event.custom(t.json) // GT recipe objects have no .id() here; KubeJS names it
      lava++
    } catch (e) { console.warn('[APEX-STEAM] lava ' + t.id + ': ' + e) }
  })

  // Dimensionally Transcendent Steam Oven
  event.remove({ output: 'gtna:dimensionally_transcendent_steam_oven' })
  gt.assembler('apex:steam/dimensionally_transcendent_steam_oven')
    .itemInputs('16x gtna:large_steam_furnace', '4x #gtceu:circuits/mv', '4x gtceu:aluminium_frame',
      '8x gtceu:double_steel_plate', '2x gtceu:mv_electric_pump', '4x gtna:precision_steam_component')
    .inputFluids(Fluid.of('gtceu:soldering_alloy', 1152))
    .itemOutputs('gtna:dimensionally_transcendent_steam_oven')
    .duration(1200).EUt(120)

  console.info(`[APEX-STEAM] lava maker LV assembler + ${lava} lava recipes cut 10x; DT Steam Oven moved to MV assembler`)
})
