// priority: -150
// Apex Ascension 1.2 - AE2 tier gating
// Moves AE2-family crafting-table recipes into the GregTech Assembler at a set voltage tier.
// The machine's voltage is the gate: an LV Assembler can't run MV recipes.
//   LV  : network basics, 1k storage, buses, terminals. NO autocrafting.
//   MV  : 4k/16k, autocrafting (crafting CPU, pattern provider, molecular assembler, interface), P2P
//   HV  : everything not listed (the default)
//   EV  : quantum link/ring, spatial, big crafting accelerators, assembler matrix
//   IV+ : AdvancedAE quantum computer + quantum armor, very large cells
//   ZPM : MEGA Cells (1M-16M cells, MEGA crafting CPU, MEGA interface / pattern provider)
//   UV  : MEGA 64M/256M; InsaneAE basics (G-size cells, insane processor, interface, hyper cable)
//   UHV / UEV / UIV : InsaneAE T / P / E-size cells and crafting storage, quantum CPU, top energy cells
//   InsaneAE's machine acceleration cards (Turbo / Overclock / Hypersonic / Warp) are disabled.
// Inputs are the original recipe's ingredients (same counts) + soldering alloy.
// Recipes that can't be converted (NBT ingredients, >9 different inputs) stay handcrafted and are logged.

const APEX_AE = {
  NAMESPACES: ['ae2', 'expatternprovider', 'extendedae_plus', 'advanced_ae', 'ae2things', 'appflux',
               'merequester', 'extendedterminal', 'ae2wtlib', 'pccard', 'myotus', 'megacells', 'insaneae'],
  EUT: { lv: 30, mv: 120, hv: 480, ev: 1920, iv: 7680, luv: 30720, zpm: 122880, uv: 491520,
         uhv: 1966080, uev: 7864320, uiv: 31457280 },
  SOLDER: { lv: 72, mv: 144, hv: 288, ev: 576, iv: 1152, luv: 2304, zpm: 4608, uv: 9216,
            uhv: 18432, uev: 36864, uiv: 73728 },
  // checked in order after TIERS; first match wins
  TIER_RULES: [
    [/^megacells:.*(64m|256m)/, 'uv'],
    [/^megacells:/, 'zpm'],
    [/^insaneae:.*_(1|4|16|64|256)t(_|$)/, 'uhv'],
    [/^insaneae:.*_(1|4|16|64|256)p(_|$)/, 'uev'],
    [/^insaneae:.*_(1|4|8)e(_|$)/, 'uiv'],
    [/^insaneae:(1|4|16|64|256)t_crafting_storage$/, 'uhv'],
    [/^insaneae:(1|4|16|64|256)p_crafting_storage$/, 'uev'],
    [/^insaneae:(1|4|8)e_crafting_storage$/, 'uiv'],
    [/^insaneae:(1|2)g_crafting_accelerator$/, 'uev'],
    [/^insaneae:(16|64|256)m_crafting_accelerator$/, 'uhv'],
    [/^insaneae:(quantum_cpu|quantum_acceleration_card|task_fusion_card)$/, 'uhv'],
    [/^insaneae:big_integer_cpu$/, 'uev'],
    [/^insaneae:(collapsar|singularity|pulsar|quasar)_energy_cell$/, 'uhv'],
    [/^insaneae:(nova|supernova|hypernova|galactic|cosmic)_energy_cell$/, 'uev'],
    [/^insaneae:/, 'uv']
  ],
  // recipes removed outright (items hidden in client_scripts/apex_ae2_hide.js)
  DISABLED: ['insaneae:turbo_card', 'insaneae:overclock_card', 'insaneae:hypersonic_card', 'insaneae:warp_card'],
  DEFAULT_TIER: 'hv',

  // exact ids -> tier
  TIERS: {
    lv: [
      'ae2:controller', 'ae2:drive', 'ae2:chest', 'ae2:terminal', 'ae2:crafting_terminal',
      'ae2:cell_component_1k', 'ae2:item_cell_housing', 'ae2:fluid_cell_housing',
      'ae2:energy_acceptor', 'ae2:cable_energy_acceptor', 'ae2:energy_cell', 'ae2:cell_workbench',
      'ae2:inscriber', 'ae2:charger', 'ae2:growth_accelerator', 'ae2:crank', 'ae2:vibration_chamber',
      'ae2:import_bus', 'ae2:export_bus', 'ae2:storage_bus', 'ae2:level_emitter', 'ae2:energy_level_emitter',
      'ae2:toggle_bus', 'ae2:inverted_toggle_bus', 'ae2:annihilation_plane', 'ae2:formation_plane',
      'ae2:fluix_glass_cable', 'ae2:fluix_covered_cable', 'ae2:fluix_smart_cable', 'ae2:quartz_fiber',
      'ae2:cable_anchor', 'ae2:quartz_glass', 'ae2:quartz_vibrant_glass', 'ae2:quartz_fixture',
      'ae2:annihilation_core', 'ae2:formation_core', 'ae2:basic_card', 'ae2:capacity_card',
      'ae2:redstone_card', 'ae2:fuzzy_card', 'ae2:inverter_card', 'ae2:void_card', 'ae2:view_cell',
      'ae2:network_tool', 'ae2:memory_card', 'ae2:monitor', 'ae2:semi_dark_monitor', 'ae2:dark_monitor',
      'ae2:storage_monitor', 'ae2:conversion_monitor', 'ae2:sky_stone_tank', 'ae2:light_detector',
      'ae2things:disk_housing', 'appflux:fe_cell_housing', 'appflux:core_1k',
      'appflux:flux_accessor', 'appflux:part_flux_accessor', 'appflux:insulating_resin',
      'extendedterminal:basic_terminal'
    ],
    mv: [
      'ae2:cell_component_4k', 'ae2:cell_component_16k', 'appflux:core_4k', 'appflux:core_16k',
      'ae2:crafting_unit', 'ae2:1k_crafting_storage', 'ae2:4k_crafting_storage', 'ae2:16k_crafting_storage',
      'ae2:crafting_accelerator', 'ae2:crafting_monitor', 'ae2:molecular_assembler',
      'ae2:pattern_provider', 'ae2:cable_pattern_provider', 'ae2:blank_pattern',
      'ae2:pattern_encoding_terminal', 'ae2:pattern_access_terminal',
      'ae2:interface', 'ae2:cable_interface', 'ae2:me_p2p_tunnel',
      'ae2:crafting_card', 'ae2:advanced_card', 'ae2:speed_card', 'ae2:energy_card',
      'ae2:equal_distribution_card', 'ae2:dense_energy_cell',
      'merequester:requester', 'merequester:requester_terminal', 'pccard:card_programmed_circuit',
      'extendedterminal:advanced_terminal', 'extendedae_plus:4x_crafting_accelerator'
    ],
    ev: [
      'ae2:quantum_link', 'ae2:quantum_ring', 'ae2:spatial_io_port', 'ae2:spatial_pylon', 'ae2:spatial_anchor',
      'ae2:spatial_cell_component_2', 'ae2:spatial_cell_component_16', 'ae2:spatial_cell_component_128',
      'ae2wtlib:quantum_bridge_card', 'appflux:core_1m', 'appflux:core_4m',
      'expatternprovider:assembler_matrix_frame', 'expatternprovider:assembler_matrix_wall',
      'expatternprovider:assembler_matrix_glass', 'expatternprovider:assembler_matrix_crafter',
      'expatternprovider:assembler_matrix_pattern', 'expatternprovider:assembler_matrix_speed',
      'extendedae_plus:16x_crafting_accelerator', 'extendedae_plus:64x_crafting_accelerator',
      'extendedae_plus:spatial_core', 'extendedterminal:epic_terminal', 'extendedterminal:end_terminal',
      'extendedterminal:sculk_terminal', 'extendedterminal:wireless_epic_terminal'
    ],
    iv: [
      'advanced_ae:quantum_core', 'advanced_ae:quantum_structure', 'advanced_ae:quantum_unit',
      'advanced_ae:quantum_storage_128', 'advanced_ae:quantum_storage_256', 'advanced_ae:quantum_storage_component',
      'advanced_ae:quantum_accelerator', 'advanced_ae:quantum_multi_threader', 'advanced_ae:data_entangler',
      'advanced_ae:quantum_crafter', 'advanced_ae:quantum_crafter_terminal', 'advanced_ae:wireless_quantum_crafter_terminal',
      'advanced_ae:quantum_helmet', 'advanced_ae:quantum_chestplate', 'advanced_ae:quantum_leggings',
      'advanced_ae:quantum_boots', 'advanced_ae:quantum_upgrade_base',
      'appflux:core_16m', 'extendedae_plus:256x_crafting_accelerator',
      'extendedae_plus:assembler_matrix_crafter_plus', 'extendedae_plus:assembler_matrix_pattern_plus',
      'extendedae_plus:assembler_matrix_speed_plus', 'extendedae_plus:assembler_matrix_hybrid_plus',
      'extendedae_plus:assembler_matrix_upload_core', 'extendedae_plus:super_assembler_matrix_frame',
      'extendedae_plus:super_assembler_matrix_wall', 'extendedae_plus:ultimate_super_assembler_matrix_builder',
      'extendedterminal:extreme_terminal'
    ],
    luv: [
      'appflux:core_64m', 'expatternprovider:infinity_cell', 'extendedae_plus:1024x_crafting_accelerator',
      'extendedae_plus:infinity_core', 'extendedterminal:neo_extreme_terminal', 'extendedterminal:ultimate_terminal'
    ],
    zpm: [
      'appflux:core_256m', 'extendedterminal:legendary_terminal', 'extendedterminal:united_terminal',
      'extendedterminal:wireless_legendary_terminal', 'extendedterminal:wireless_united_terminal'
    ],
    uv: [
      'extendedae_plus:infinity_biginteger_cell'
    ]
  },

  // regex on the path: these stay handcrafted (decoration, dyed variants, tools, cells assembled from gated components)
  KEEP_HANDCRAFT: [
    /^(white|orange|magenta|light_blue|yellow|lime|pink|gray|light_gray|cyan|purple|blue|brown|green|red|black)_/,
    /paint_ball$/, /(^|_)quartz_(block|slab|stairs|wall|bricks?|pillar)/, /^(chiseled|cut|smooth)_quartz/,
    /^quartz_(brick|pillar)/, /sky_stone_(brick|small_brick|slab|stairs|wall|chest)/, /^smooth_sky_stone/,
    /^fluix_(block|slab|stairs|wall|crystal|pearl|upgrade_smithing_template)$/, /^certus_quartz_crystal$/,
    /^(certus|nether)_quartz_(axe|hoe|pickaxe|shovel|sword|cutting_knife|wrench)$/, /^silicon(_block)?$/,
    /storage_cell_\d+[kmgtpe]?$/, /^bulk_item_cell$/, /^sky_steel_block$/, /^portable_.*cell/, /^fe_\d+[km]_(portable_)?cell$/, /^disk_drive_\d+k$/,
    /^quantum_alloy(_block|_slab|_stairs|_wall)?$/, /^(charged_)?ender_pearl(_block)?$/, /^tiny_tnt$/,
    /^not_so_mysterious_cube$/, /^me_packing_tape$/, /^fishbig$/, /^xbai$/, /^c-h716$/
  ],
  LOG_EACH: false
}

ServerEvents.recipes(event => {
  let tierOf = {}
  Object.keys(APEX_AE.TIERS).forEach(t => APEX_AE.TIERS[t].forEach(id => tierOf[id] = t))
  let nsOk = ns => APEX_AE.NAMESPACES.indexOf(ns) >= 0
  let keep = path => APEX_AE.KEEP_HANDCRAFT.some(re => re.test(path))
  let ruleTier = id => {
    for (let i = 0; i < APEX_AE.TIER_RULES.length; i++) if (APEX_AE.TIER_RULES[i][0].test(id)) return APEX_AE.TIER_RULES[i][1]
    return null
  }
  APEX_AE.DISABLED.forEach(id => { if (Item.exists(id)) event.remove({ output: id }) })

  let ingStr = i => {
    if (Array.isArray(i)) return i.length ? ingStr(i[0]) : null
    if (!i || typeof i !== 'object') return null
    if (i.nbt || (i.type && i.type !== 'forge:item')) return null
    if (i.item) return i.item
    if (i.tag) return '#' + i.tag
    return null
  }

  let todo = []
  let skipped = 0
  ;['minecraft:crafting_shaped', 'minecraft:crafting_shapeless'].forEach(type => {
    event.forEachRecipe({ type: type }, r => {
      let j
      try { j = JSON.parse(String(r.json)) } catch (e) { return }
      let res = j.result
      let out = res && (typeof res === 'string' ? res : res.item)
      if (!out || out.indexOf(':') < 0) return
      let [ns, path] = out.split(':')
      if (!nsOk(ns) || keep(path) || APEX_AE.DISABLED.indexOf(out) >= 0) return
      if (res.nbt) { skipped++; return }

      let counts = {}
      let bad = false
      let add = s => { if (s === null) { bad = true; return } counts[s] = (counts[s] || 0) + 1 }
      if (j.pattern) {
        j.pattern.join('').split('').forEach(ch => { if (ch !== ' ') add(ingStr(j.key[ch])) })
      } else {
        (j.ingredients || []).forEach(i => add(ingStr(i)))
      }
      let keys = Object.keys(counts)
      let total = keys.reduce((a, k) => a + counts[k], 0)
      if (total === 1 && !bad) return // 1-item conversions (part <-> block, cleaning) stay handcrafted
      if (bad || keys.length === 0 || keys.length > 9) {
        skipped++
        console.info(`[APEX-AE2] kept handcrafted (can't convert): ${r.getId()} -> ${out}`)
        return
      }
      todo.push({ id: String(r.getId()), out: out, count: (res.count || 1), counts: counts,
                  tier: tierOf[out] || ruleTier(out) || APEX_AE.DEFAULT_TIER, n: j.pattern ? j.pattern.join('').replace(/ /g, '').length : (j.ingredients || []).length })
    })
  })

  let perTier = {}
  todo.forEach(t => {
    event.remove({ id: t.id })
    let inputs = Object.keys(t.counts).map(k => `${t.counts[k]}x ${k}`)
    event.recipes.gtceu.assembler(`apex:ae2_tiers/${t.id.replace(':', '/')}`)
      .itemInputs(inputs)
      .inputFluids(Fluid.of('gtceu:soldering_alloy', APEX_AE.SOLDER[t.tier]))
      .itemOutputs(`${t.count}x ${t.out}`)
      .duration(100 + 20 * t.n)
      .EUt(APEX_AE.EUT[t.tier])
    perTier[t.tier] = (perTier[t.tier] || 0) + 1
    if (APEX_AE.LOG_EACH) console.info(`[APEX-AE2] ${t.out} -> ${t.tier.toUpperCase()} assembler`)
  })
  console.info(`[APEX-AE2] moved ${todo.length} AE recipes into the assembler ${JSON.stringify(perTier)}; ${skipped} left handcrafted`)
})
