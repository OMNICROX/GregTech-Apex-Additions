// priority: -150
// Apex Ascension 1.2 - Tinkers' Avaritia (tinkers_avaritia) is the one Avaritia <-> Tinkers integration.
//  - Its Infinity and Neutron materials melt from the GregTech ingots: apex_gt_neutronium.js rewrites every recipe
//    that names avaritia:infinity_ingot / avaritia:neutron_ingot to gtna:infinity_ingot / gtceu:neutronium_ingot.
//  - Avaritia Integration keeps its machines and other compat, but its own four Tinkers materials are turned off
//    (kubejs/data/avaritia_integration/tinkering/materials/definition) and its Tinkers recipes removed here.
//  - tic3nh's Neutronium and the old cc_tic Infinity material are retired so one ingot = one Tinkers material.
ServerEvents.recipes(event => {
  let ids = [
    /^avaritia_integration:/, /^apex:gt_unify\/avaritia_integration\//
  ]
  let gone = []
  ids.forEach(re => event.forEachRecipe({ id: re }, r => {
    let type = ''
    try { type = String(r.json.get('type').getAsString()) } catch (e) { type = '' }
    if (type.indexOf('tconstruct:') === 0) gone.push(String(r.getId()))
  }))
  gone.forEach(id => event.remove({ id: id }))
  ;['tic3nh:smeltery/melting/neutronium', 'tic3nh:smeltery/casting/material/neutronium'].forEach(id => event.remove({ id: id }))
  event.remove({ id: /^cc_tic:/ })
  console.info(`[APEX-TIC-AVARITIA] Tinkers Avaritia is the Avaritia material set; removed ${gone.length} Avaritia Integration Tinkers recipes and the tic3nh / cc_tic overlaps`)
})
