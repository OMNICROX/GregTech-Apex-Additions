// Apex Ascension - Hostile Neural Networks progression.
//  * Data models are attuned with DIMENSION blanks: right-click a mob with the blank for the dimension you are in.
//    Bosses need the Boss blank; the Master blank works on anything. (HNN's own blank is disabled.)
//  * Boss models drop shards: 9 shards = 1 fragment, 9 fragments = the real item (81 simulations per boss item).
const APEX_BLANKS = {
  'kubejs:overworld_data_model': { dims: ['minecraft:overworld'], label: 'the Overworld' },
  'kubejs:nether_data_model': { dims: ['minecraft:the_nether'], label: 'the Nether' },
  'kubejs:end_data_model': { dims: ['minecraft:the_end'], label: 'the End' },
  'kubejs:twilight_data_model': { dims: ['twilightforest:twilight_forest'], label: 'the Twilight Forest' },
  'kubejs:undergarden_data_model': { dims: ['undergarden:undergarden'], label: 'the Undergarden' },
  'kubejs:horizon_data_model': { dims: ['eoh:aecora', 'eoh:astralis'], label: 'the Echoes of Horizons dimensions' },
  'kubejs:boss_data_model': { boss: true, label: 'bosses' },
  'kubejs:master_data_model': { any: true, label: 'any mob' }
}
const APEX_BOSSES = [
  'minecraft:wither', 'minecraft:ender_dragon', 'minecraft:elder_guardian', 'minecraft:warden',
  'twilightforest:naga', 'twilightforest:lich', 'twilightforest:hydra', 'twilightforest:ur_ghast', 'twilightforest:snow_queen',
  'twilightforest:alpha_yeti', 'twilightforest:minoshroom', 'twilightforest:knight_phantom',
  'bosses_of_mass_destruction:lich', 'bosses_of_mass_destruction:void_blossom', 'bosses_of_mass_destruction:gauntlet', 'bosses_of_mass_destruction:obsidilith',
  'iceandfire:fire_dragon', 'iceandfire:ice_dragon', 'iceandfire:lightning_dragon', 'iceandfire:hydra', 'iceandfire:dread_lich',
  'iceandfire:sea_serpent', 'iceandfire:cyclops', 'iceandfire:gorgon', 'iceandfire:myrmex_queen',
  'alexsmobs:void_worm', 'undergarden:forgotten_guardian',
  'threateningly_mobs:the_earthloong', 'threateningly_mobs:terra_dragon_re', 'threateningly_mobs:the_inferno', 'threateningly_mobs:the_regalhart',
  'threateningly_mobs:tide_specter', 'threateningly_mobs:lich_re', 'threateningly_mobs:hydra', 'threateningly_mobs:sandworm',
  'threateningly_mobs:ice_brood_mother', 'threateningly_mobs:solscarab_maximus', 'threateningly_mobs:executioner',
  'threateningly_mobs:dungeon_guardian', 'threateningly_mobs:moon_priest', 'threateningly_mobs:titan_rabbit_re', 'threateningly_mobs:flower_of_dragon',
  'eoh:axol_chief', 'eoh:junkpig_cyclops', 'eoh:triton_sentinel', 'eoh:withered_dracopouik', 'eoh:axol_whale'
]

ItemEvents.entityInteracted(event => {
  let cfg = APEX_BLANKS[String(event.item.id)]
  if (!cfg) return
  event.cancel()
  let player = event.player, target = event.target
  let EntityType = Java.loadClass('net.minecraft.world.entity.EntityType')
  let DMR = Java.loadClass('dev.shadowsoffire.hostilenetworks.data.DataModelRegistry').INSTANCE
  let typeId = String(EntityType.getKey(target.getType()))
  let dim = String(event.level.dimension)
  let isBoss = APEX_BOSSES.indexOf(typeId) >= 0

  if (!cfg.any) {
    if (isBoss && !cfg.boss) { player.tell(Text.red('This is a boss - you need a Boss Blank Data Model.')); return }
    if (!isBoss && cfg.boss) { player.tell(Text.red('The Boss Blank Data Model only works on bosses.')); return }
    if (!cfg.boss && cfg.dims.indexOf(dim) < 0) { player.tell(Text.red(`This blank only works in ${cfg.label}.`)); return }
  }
  let model = DMR.getForEntity(target.getType())
  if (!model) { player.tell(Text.red('This mob has no data model.')); return }
  let key = String(DMR.getKey(model))
  let stack = Item.of('hostilenetworks:data_model', { data_model: { id: key } })
  if (event.item.count <= 1) player.setItemInHand(event.hand, stack)
  else { event.item.shrink(1); player.give(stack) }
  player.tell(Text.gold('Built a data model: ').append(model.name()))
})

ServerEvents.recipes(event => {
  // HNN's own blank can no longer be made (it would skip the dimension blanks)
  event.remove({ output: 'hostilenetworks:blank_data_model' })

  let has = id => { try { return Item.exists(id) } catch (e) { return false } }
  let ow = 'kubejs:overworld_data_model'
  event.shaped(ow, ['LPL', 'RSR', 'LGL'], { L: 'minecraft:clay_ball', P: 'minecraft:repeater', R: '#forge:dusts/redstone', S: 'minecraft:smooth_stone', G: '#forge:ingots/gold' }).id('apex:hnn/overworld_data_model')
  event.shaped('kubejs:nether_data_model', ['QBQ', 'WOW', 'QBQ'], { Q: 'minecraft:quartz', B: 'minecraft:blaze_rod', W: 'minecraft:nether_wart', O: ow }).id('apex:hnn/nether_data_model')
  event.shaped('kubejs:end_data_model', ['EPE', 'SOS', 'EPE'], { E: 'minecraft:ender_pearl', P: 'minecraft:popped_chorus_fruit', S: 'minecraft:shulker_shell', O: ow }).id('apex:hnn/end_data_model')
  if (has('twilightforest:naga_scale')) event.shaped('kubejs:twilight_data_model', ['NIN', 'TOT', 'NIN'], { N: 'twilightforest:naga_scale', I: 'twilightforest:ironwood_ingot', T: 'twilightforest:steeleaf_ingot', O: ow }).id('apex:hnn/twilight_data_model')
  if (has('undergarden:cloggrum_ingot')) event.shaped('kubejs:undergarden_data_model', ['CRC', 'FOF', 'CRC'], { C: 'undergarden:cloggrum_ingot', R: 'undergarden:regalium_crystal', F: 'undergarden:froststeel_ingot', O: ow }).id('apex:hnn/undergarden_data_model')
  if (has('eoh:pearl')) event.shaped('kubejs:horizon_data_model', ['PLP', 'SOS', 'PLP'], { P: 'eoh:pearl', L: 'eoh:luriell_crystal_shard', S: 'eoh:solvanite_crystal_shard', O: ow }).id('apex:hnn/horizon_data_model')
  event.shaped('kubejs:boss_data_model', ['DND', 'BOB', 'DTD'], { D: 'minecraft:diamond_block', N: 'minecraft:nether_star', B: 'minecraft:dragon_breath', T: 'minecraft:totem_of_undying', O: ow }).id('apex:hnn/boss_data_model')
  event.shaped('kubejs:master_data_model', ['ONE', 'TSU', 'HBD'], {
    O: ow, N: 'kubejs:nether_data_model', E: 'kubejs:end_data_model', T: 'kubejs:twilight_data_model', U: 'kubejs:undergarden_data_model',
    H: 'kubejs:horizon_data_model', B: 'kubejs:boss_data_model', S: 'minecraft:nether_star', D: 'minecraft:diamond_block'
  }).id('apex:hnn/master_data_model')

  // shards -> fragments -> boss items (9 x 9 = 81 shards)
  let SHARDS = {
    nether_star: 'minecraft:nether_star', dragon_egg: 'minecraft:dragon_egg',
    ancient_anima: 'bosses_of_mass_destruction:ancient_anima', void_thorn: 'bosses_of_mass_destruction:void_thorn',
    blazing_eye: 'bosses_of_mass_destruction:blazing_eye', obsidian_heart: 'bosses_of_mass_destruction:obsidian_heart'
  }
  Object.keys(SHARDS).forEach(k => {
    if (!has(SHARDS[k])) return
    event.shapeless(`kubejs:${k}_fragment`, Array(9).fill(`kubejs:${k}_shard`)).id(`apex:hnn/${k}_fragment`)
    event.shapeless(SHARDS[k], Array(9).fill(`kubejs:${k}_fragment`)).id(`apex:hnn/${k}_from_fragments`)
  })
})
