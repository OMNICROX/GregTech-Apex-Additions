// priority: -60
// Apex Ascension - extra Botania recipes (GTNH style). Runs before apex_mana_nexus.js (-70), so the Alfheim
// Teleportation Array picks these up too.
ServerEvents.recipes(event => {
  // GregTech steel infuses into Manasteel as well as iron does (GTNH has steel here)
  event.custom({ type: 'botania:mana_infusion', input: { tag: 'forge:ingots/steel' },
    output: { item: 'botania:manasteel_ingot' }, mana: 3000 }).id('apex:botania/manasteel_from_steel')
  event.custom({ type: 'botania:mana_infusion', input: { tag: 'forge:storage_blocks/steel' },
    output: { item: 'botania:manasteel_block' }, mana: 27000 }).id('apex:botania/manasteel_block_from_steel')
  console.info('[APEX-BOTANIA-EXTRAS] steel -> manasteel infusions added')
})
