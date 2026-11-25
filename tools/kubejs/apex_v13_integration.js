// priority: -160
// Apex Ascension 1.3 - mod integration pass (from the full recipe audit)
// Same method as the AE2 tiers: a mod's crafting-table recipes move into the GregTech Assembler at a voltage
// tier, keeping their original ingredients (same counts) + soldering alloy. The machine's voltage is the gate.
// Decoration, food, tools of a mod's own metal, 1-item conversions and NBT recipes stay handcrafted.
// Also: free power blocks removed, mod metals that GregTech already has fold into GT's, and space / Draconic
// metals smelt in the Electric Blast Furnace instead of a furnace.

const APEX_V13 = {
  EUT: { lv: 30, mv: 120, hv: 480, ev: 1920, iv: 7680, luv: 30720, zpm: 122880, uv: 491520 },
  SOLDER: { lv: 72, mv: 144, hv: 288, ev: 576, iv: 1152, luv: 2304, zpm: 4608, uv: 9216 },

  // namespace -> { def: default tier or 'keep', rules: [[regex on the item path, tier | 'keep' | 'remove'], ...] }
  // first matching rule wins
  MODS: {
    ironchest: { def: 'lv', rules: [[/obsidian|crystal/, 'mv'], [/netherite/, 'hv'], [/diamond/, 'lv'], [/(^|_)(copper|iron|gold)(_|$)/, 'keep']] },
    ironfurnaces: { def: 'lv', rules: [[/^(iron|copper|gold)_furnace$|^upgrade_(iron|iron2|copper|gold)$/, 'keep'],
      [/augment_generator/, 'remove'], [/crystal|netherite|obsidian2/, 'mv'], [/million|rainbow/, 'hv'], [/^item_/, 'keep']] },
    pipez: { def: 'lv', rules: [[/energy_pipe|universal_pipe|improved_upgrade/, 'mv'], [/advanced_upgrade/, 'hv'], [/ultimate_upgrade/, 'ev']] },
    sfm: { def: 'lv', rules: [[/water_tank|xp_goop/, 'keep'], [/tough/, 'mv']] },
    sophisticatedbackpacks: { def: 'lv', rules: [[/^backpack$/, 'keep'], [/stack_upgrade_tier_2|^advanced_(feeding|pickup|filter|magnet|refill|restock|deposit|compacting|void|jukebox|alchemy|tool_swapper|pump|xp_pump|anvil)/, 'mv'],
      [/stack_upgrade_tier_3|^advanced_/, 'hv'], [/stack_upgrade_tier_4|inception|everlasting|infinity/, 'ev'], [/stack_upgrade_omega/, 'iv']] },
    sophisticatedstorage: { def: 'lv', rules: [[/storage_connector|_trim$|^(barrel|chest|shulker_box|limited_barrel_\d)$|^decoration_table$|^storage_io$|^storage_input$|^storage_output$/, 'keep'],
      [/^(basic|copper)_to_(copper|iron)_tier_upgrade$/, 'lv'], [/_to_gold_tier_upgrade$/, 'mv'], [/_to_diamond_tier_upgrade$/, 'hv'], [/_to_netherite_tier_upgrade$/, 'ev'],
      [/stack_upgrade_tier_2|^advanced_/, 'mv'], [/stack_upgrade_tier_3/, 'hv'], [/stack_upgrade_tier_4/, 'ev'], [/stack_upgrade_tier_5|omega/, 'iv']] },
    constructionwand: { def: 'lv', rules: [[/core_angel|core_destruction|core_exchange|energy_upgrade/, 'mv']] },
    itemcollectors: { def: 'lv', rules: [[/advanced/, 'mv']] },
    hostilenetworks: { def: 'mv', rules: [[/deep_learner|prediction_matrix/, 'lv']] },
    cyclic: { def: 'mv', rules: [
      [/^(amethyst|copper|crystal|emerald|netherite|sandstone|gold|diamond|obsidian)_(axe|hoe|pickaxe|shovel|sword|helmet|chestplate|leggings|boots)$/, 'keep'],
      [/^(apple|carrot|cookie|chorus_spectral)_|^apple_|^compressed_cobblestone$|_bars$|^cask$|^carbon_paper$|^cloud$|^clock$|^blockstate_data$|^battery_clay$/, 'keep'],
      [/charm_ultimate|anvil_void|antigravity|tank_cask|teleport|dark_glass_connected/, 'ev'],
      [/^charm_|^anvil|^chorus_flight|^ender|crafter|collector|^user$|placer|^wireless|beacon|disenchanter|uncrafter|experience_pylon|forester|harvester|miner|dropper|^spawner|ender_shelf|conveyor/, 'hv']] },
    securitycraft: { def: 'keep', rules: [[/sentry|^ims$|camera|block_pocket|retinal|portable_radar|inventory_scanner|protecto|rift_stabilizer|projector|universal_block_remover|block_change_detector/, 'hv'],
      [/keycard|keypad|_module$|reader|scanner|laser_block|alarm|cage_trap|motion|trophy_system|username_logger|universal_(owner|key)_changer|universal_block_modifier|secure_trading_station/, 'mv']] },
    easy_villagers: { def: 'mv', rules: [[/iron_farm|auto_trader/, 'hv']] },
    travelanchors: { def: 'hv', rules: [] },
    torchmaster: { def: 'mv', rules: [[/frozen_pearl/, 'keep']] },
    waystones: { def: 'mv', rules: [[/warp_dust|attuned_shard|crumbling/, 'keep'], [/warp_plate|sharestone/, 'hv']] },
    buildinggadgets2: { def: 'hv', rules: [[/copy_paste|template_manager/, 'ev']] },
    palladium: { def: 'keep', rules: [[/quartz_(circuit|flux_capacitor)/, 'hv'], [/vibranium_(circuit|flux_capacitor)|multiversal|suit_stand/, 'ev']] },
    fsang: { def: 'keep', rules: [[/circuit|motherboard|^ai_/, 'ev']] },
    satsu_iron_man_addon: { def: 'keep', rules: [[/basic_circuit/, 'hv'], [/arc_reactor|armored_construct/, 'ev']] },
    alienevo: { def: 'keep', rules: [[/omnitrix_workbench|machined_metal|machine_scroll/, 'hv'], [/prototype_omnitrix|upgrade_chip/, 'ev']] },
    extrautilitiesreborn: { def: 'mv', rules: [
      [/compressed|glass$|_glass|^(iron|golden|wood)_spikes$|^border_stone$|chandelier|^moon_stone$|^ender_shard$|^eye_of_redstone$|^kikoku$|_ingot$|^block_of_/, 'keep'],
      [/angel_block|drum|golden_bag|diamond_spikes|enchanter|ender_thermic_pump|portable_globe/, 'ev'],
      [/bedrock|quarry|world_hole|destruction_pickaxe|erosion_shovel|precision_shears|etheric_sword|healing_axe|fire_axe/, 'iv'],
      [/machine_block|electrical_furnace|mechanical_(miner|user)|trash_can|chicken_wing_ring|golden_feather/, 'hv']] },
    corewithstuff: { def: 'keep', rules: [[/^(solar_generator|generator_base|alloy_smelter)$/, 'remove'],
      [/basic_circuit|basic_wire|energy_cable|wrench|charger/, 'hv'], [/advanced_wire|super_circuit|fabricator|airlock_projector|gravity_core|anchor_core|player_sensor|hologram_stand/, 'ev']] },
    draconicevolution: { def: 'iv', rules: [
      [/^(awakened_)?draconium_(block|ingot|nugget)$|^infused_obsidian$|^structure_block$/, 'keep'],
      [/^generator$/, 'remove'],
      [/^wyvern_|^basic_(relay|io|wireless)_crystal/, 'luv'], [/^draconic_|^item_draconic/, 'zpm'], [/^(chaotic_|awakened_|item_chaotic)|chaos_shard/, 'uv']] },
    draconicadditions: { def: 'luv', rules: [[/^inert_potato|^infused_potato|^hermal$/, 'keep'], [/^draconic_|item_draconic/, 'zpm'], [/chaos|chaotic/, 'uv']] }
  },

  // free power that skips GT progression
  REMOVE: ['corewithstuff:solar_generator', 'corewithstuff:generator_base', 'corewithstuff:alloy_smelter',
           'draconicevolution:generator', 'ironfurnaces:augment_generator'],

  // [mod item, GT tag that replaces it in every recipe, GT item it converts into]
  METALS: [
    ['chaospersists:ingottitanium', 'forge:ingots/titanium', 'gtceu:titanium_ingot'],
    ['chaospersists:ingoturanium', 'forge:ingots/uranium', 'gtceu:uranium_ingot'],
    ['chaospersists:ruby', 'forge:gems/ruby', 'gtceu:ruby_gem'],
    ['corewithstuff:lead_ingot', 'forge:ingots/lead', 'gtceu:lead_ingot'],
    ['corewithstuff:silver_ingot', 'forge:ingots/silver', 'gtceu:silver_ingot'],
    ['corewithstuff:steel_ingot', 'forge:ingots/steel', 'gtceu:steel_ingot'],
    ['corewithstuff:titanium_ingot', 'forge:ingots/titanium', 'gtceu:titanium_ingot'],
    ['palladium:titanium_ingot', 'forge:ingots/titanium', 'gtceu:titanium_ingot'],
    ['palladium:lead_ingot', 'forge:ingots/lead', 'gtceu:lead_ingot'],
    ['eoh:cobalt_ingot', 'forge:ingots/cobalt', 'gtceu:cobalt_ingot'],
    ['gravecore:palladium_ingot', 'forge:ingots/palladium', 'gtceu:palladium_ingot'],
    ['fsang:titanium_ingot', 'forge:ingots/titanium', 'gtceu:titanium_ingot'],
    ['infinity:titanium_ingot', 'forge:ingots/titanium', 'gtceu:titanium_ingot'],
    ['infinity:steel_ingot', 'forge:ingots/steel', 'gtceu:steel_ingot'],
    ['satsu_iron_man_addon:steel_ingot', 'forge:ingots/steel', 'gtceu:steel_ingot'],
    ['iceandfire:silver_ingot', 'forge:ingots/silver', 'gtceu:silver_ingot']
  ],

  // [ingot, raw/dust inputs, voltage, EU/t, blast temp K, seconds]
  BLAST: [
    ['ad_astra:desh_ingot', ['ad_astra:raw_desh'], 'ev', 1920, 2700, 20],
    ['ad_astra:ostrum_ingot', ['ad_astra:raw_ostrum'], 'iv', 7680, 3600, 25],
    ['ad_astra:calorite_ingot', ['ad_astra:raw_calorite'], 'luv', 30720, 4500, 30],
    ['draconicevolution:draconium_ingot', ['draconicevolution:draconium_dust'], 'iv', 7680, 4500, 30]
  ],

  KEEP_PATH: [/(stairs|slab|_wall$|fence|_gate$|door|button|pressure_plate|planks|_log$|_wood$|bricks?$|tiles?$|pillar|carpet|pane|polished|chiseled|smooth|^cut_|cobbled|mossy|cracked|_sign$|banner|_bed$|candle|concrete|terracotta|wool|_lamp$|lantern|ladder|_boat$|bookshelf)/,
              /^(white|orange|magenta|light_blue|yellow|lime|pink|gray|light_gray|cyan|purple|blue|brown|green|red|black)_/],
  LOG_EACH: false
}

ServerEvents.recipes(event => {
  let has = id => { try { return Item.exists(id) } catch (e) { return false } }

  // ---------- 1. free power ----------
  let removed = 0
  APEX_V13.REMOVE.forEach(id => { if (has(id)) { event.remove({ output: id }); removed++ } })
  // Maxwell and his friends are the second final goal: only GTAA's assembly line recipes make them
  event.remove({ mod: 'placeablemaxwell' })

  // ---------- 2. metals GregTech already has ----------
  let metals = 0
  APEX_V13.METALS.forEach(m => {
    if (!has(m[0]) || !has(m[2])) return
    event.replaceInput({ not: { output: m[0] } }, m[0], '#' + m[1])
    event.shapeless(m[2], [m[0]]).id(`apex:v13/unify/${m[0].replace(':', '_')}`)
    metals++
  })

  // ---------- 3. space and Draconic metals: Electric Blast Furnace ----------
  let blast = 0
  APEX_V13.BLAST.forEach(b => {
    if (!has(b[0])) return
    let ins = b[1].filter(has)
    if (!ins.length) return
    ;['minecraft:smelting', 'minecraft:blasting'].forEach(t => event.remove({ type: t, output: b[0] }))
    ins.forEach((inp, i) => {
      event.recipes.gtceu.electric_blast_furnace(`apex:v13/blast/${b[0].split(':')[1]}_${i}`)
        .itemInputs(inp).itemOutputs(b[0]).duration(b[5] * 20).EUt(b[3]).blastFurnaceTemp(b[4])
    })
    blast++
  })

  // ---------- 4. crafting table -> GT Assembler at a tier ----------
  let tierFor = out => {
    let [ns, path] = out.split(':')
    let cfg = APEX_V13.MODS[ns]
    if (!cfg) return null
    if (APEX_V13.KEEP_PATH.some(re => re.test(path))) return 'keep'
    for (let i = 0; i < cfg.rules.length; i++) if (cfg.rules[i][0].test(path)) return cfg.rules[i][1]
    return cfg.def
  }
  let ingStr = i => {
    if (Array.isArray(i)) return i.length ? ingStr(i[0]) : null
    if (!i || typeof i !== 'object') return null
    if (i.nbt || (i.type && i.type !== 'forge:item')) return null
    if (i.item) return i.item
    if (i.tag) return '#' + i.tag
    return null
  }
  let todo = [], skipped = 0, removedByRule = 0
  ;['minecraft:crafting_shaped', 'minecraft:crafting_shapeless'].forEach(type => {
    event.forEachRecipe({ type: type }, r => {
      let j
      try { j = JSON.parse(String(r.json)) } catch (e) { return }
      let res = j.result
      let out = res && (typeof res === 'string' ? res : res.item)
      if (!out || out.indexOf(':') < 0) return
      let tier = tierFor(out)
      if (!tier || tier === 'keep') return
      if (tier === 'remove') { event.remove({ id: r.getId() }); removedByRule++; return }
      if (res.nbt) { skipped++; return }
      let counts = {}, bad = false
      let add = s => { if (s === null) { bad = true; return } counts[s] = (counts[s] || 0) + 1 }
      if (j.pattern) j.pattern.join('').split('').forEach(ch => { if (ch !== ' ') add(ingStr(j.key[ch])) })
      else (j.ingredients || []).forEach(i => add(ingStr(i)))
      let keys = Object.keys(counts)
      let total = keys.reduce((a, k) => a + counts[k], 0)
      if (total === 1 && !bad) return
      if (bad || keys.length === 0 || keys.length > 9) { skipped++; return }
      todo.push({ id: String(r.getId()), out: out, count: res.count || 1, counts: counts, tier: tier, n: total })
    })
  })
  let perTier = {}, perMod = {}
  todo.forEach(t => {
    event.remove({ id: t.id })
    event.recipes.gtceu.assembler(`apex:v13/${t.id.replace(':', '/')}`)
      .itemInputs(Object.keys(t.counts).map(k => `${t.counts[k]}x ${k}`))
      .inputFluids(Fluid.of('gtceu:soldering_alloy', APEX_V13.SOLDER[t.tier]))
      .itemOutputs(`${t.count}x ${t.out}`)
      .duration(100 + 20 * t.n)
      .EUt(APEX_V13.EUT[t.tier])
    perTier[t.tier] = (perTier[t.tier] || 0) + 1
    let ns = t.out.split(':')[0]
    perMod[ns] = (perMod[ns] || 0) + 1
    if (APEX_V13.LOG_EACH) console.info(`[APEX-1.3] ${t.out} -> ${t.tier.toUpperCase()} assembler`)
  })
  console.info(`[APEX-1.3] ${todo.length} recipes moved into the assembler ${JSON.stringify(perTier)} by mod ${JSON.stringify(perMod)}; ` +
    `${skipped} left handcrafted (NBT / not convertible); ${removed + removedByRule} power recipes removed; ${metals} metals folded into GT; ${blast} metals now need the EBF`)
})
