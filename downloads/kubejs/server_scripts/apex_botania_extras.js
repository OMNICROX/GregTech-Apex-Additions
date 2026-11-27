// priority: -60
// Apex Ascension - extra Botania recipes (GTNH style). Runs before apex_mana_nexus.js (-70), so the Alfheim
// Teleportation Array picks these up too.
ServerEvents.recipes(event => {
  // Manasteel needs steel, not iron, so Botania starts once you have steel instead of right away
  event.remove({ id: 'botania:mana_infusion/manasteel' })
  event.remove({ id: 'botania:mana_infusion/manasteel_block' })
  event.custom({ type: 'botania:mana_infusion', input: { tag: 'forge:ingots/steel' },
    output: { item: 'botania:manasteel_ingot' }, mana: 3000 }).id('apex:botania/manasteel_from_steel')
  event.custom({ type: 'botania:mana_infusion', input: { tag: 'forge:storage_blocks/steel' },
    output: { item: 'botania:manasteel_block' }, mana: 27000 }).id('apex:botania/manasteel_block_from_steel')
  console.info('[APEX-BOTANIA-EXTRAS] manasteel now infuses from steel only')
})
