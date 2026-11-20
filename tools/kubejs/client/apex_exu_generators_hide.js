// Apex Ascension 1.2 - hide the disabled Extra Utilities Reborn generators from JEI
JEIEvents.hideItems(event => {
  event.hide(/^extrautilitiesreborn:.*(generator|_mill|solar_panel|lunar_panel)/)
})
