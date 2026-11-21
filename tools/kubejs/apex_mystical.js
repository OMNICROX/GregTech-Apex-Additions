// Apex Ascension - Mystical Agriculture: essence recipes for the custom crops (config/mysticalcustomization/crops).
ServerEvents.recipes(event => {
  let has = id => { try { return Item.exists(id) } catch (e) { return false } }
  // [crop, output, count, full 3x3?]
  let CROPS = [
    ['cloggrum', 'undergarden:cloggrum_ingot', 4], ['solvanite', 'eoh:solvanite_crystal', 4],
    ['froststeel', 'undergarden:froststeel_ingot', 2], ['utherium', 'undergarden:utherium_crystal', 2],
    ['ardite', 'tic3nh:ardite_ingot', 2], ['luriell', 'eoh:luriell_crystal', 2], ['crimsolite', 'eoh:crimsolite_crystal', 2],
    ['regalium', 'undergarden:regalium_crystal', 1], ['dragon_bone', 'iceandfire:dragonbone', 1],
    ['fire_dragonsteel', 'iceandfire:dragonsteel_fire_ingot', 1, true], ['ice_dragonsteel', 'iceandfire:dragonsteel_ice_ingot', 1, true],
    ['lightning_dragonsteel', 'iceandfire:dragonsteel_lightning_ingot', 1, true]
  ]
  CROPS.forEach(c => {
    let ess = `mysticalagriculture:${c[0]}_essence` // Mystical Customization crops live under mysticalagriculture:
    let out = c[1]
    if (!has(out) && c[0] === 'ardite') out = 'tinkers_thinking:ardite_ingot'
    if (!has(ess) || !has(out)) { console.warn(`[APEX-MA] skipped ${c[0]}`); return }
    event.shaped(Item.of(out, c[2]), c[3] ? ['EEE', 'EEE', 'EEE'] : ['EEE', 'E E', 'EEE'], { E: ess }).id(`apex:mystical/${c[0]}`)
  })
})
