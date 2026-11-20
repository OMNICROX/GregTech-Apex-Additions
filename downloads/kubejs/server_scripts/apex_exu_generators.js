// priority: -90
// Apex Ascension 1.2 - Extra Utilities Reborn generators off (same call as Cyclic's: free FE doesn't fit GT
// progression). Angel Block, Angel Ring parts and the rest of ExU stay. Delete this file to bring them back.
const APEX_EXU_GEN = /^extrautilitiesreborn:.*(generator|_mill|solar_panel|lunar_panel)/

ServerEvents.recipes(event => {
  event.remove({ output: APEX_EXU_GEN })
  console.info('[APEX-EXU] Extra Utilities Reborn generator recipes removed')
})
