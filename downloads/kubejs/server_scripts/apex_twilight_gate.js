// Apex Ascension - the Twilight Forest opens at HV.
// The portal is lit by throwing an item from the twilightforest:portal/activator tag into the pool (a diamond by
// default). Now it takes an HV Field Generator, so the Twilight Forest (and the Library of Ruina's trophies) is an
// HV-era trip. Delete this file to go back to diamonds.
ServerEvents.tags('item', event => {
  event.removeAll('twilightforest:portal/activator')
  event.add('twilightforest:portal/activator', 'gtceu:hv_field_generator')
  console.info('[APEX] Twilight Forest portal: lit with an HV Field Generator')
})
