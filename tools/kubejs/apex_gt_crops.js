// priority: -60
// Apex Ascension - GregTech crops for the Mystical Cultivation Array (CropsNH-style). GregTech Apex Additions.
//
// The crops themselves are Mystical Customization files (config/mysticalcustomization/crops/gt_*.json); their ids
// live under mysticalagriculture: (mysticalagriculture:gt_steel_seeds / _essence).
//
// Seeds: only from the Seed Synthesizer. At the crop's tier a seed costs 32 blocks of the material, 4 of that tier's
//   circuits, its field generator and a Prosperity Seed Base. Every Synthesizer tier above that halves the material
//   (1/2, 1/4, then 1/8 as the floor); those take 2 circuits of the Synthesizer's own tier and no field generator.
// Essence: 24 essence -> 1 dust in a GregTech extractor. Nothing else turns these essences into anything, so output
//   grows exactly with the number of seeds planted.
// (Generated list: tools/gt_crops.py; [material, tier, ingot|gem, has a block])

const APEX_GT_CROPS = [
  ["almandine", "lv", "gem", true],
  ["andradite", "lv", "gem", true],
  ["antimony", "lv", "ingot", true],
  ["apatite", "lv", "gem", true],
  ["bismuth", "lv", "ingot", true],
  ["brass", "lv", "ingot", true],
  ["bronze", "lv", "ingot", true],
  ["cinnabar", "lv", "gem", true],
  ["cobalt", "lv", "ingot", true],
  ["cobalt_brass", "lv", "ingot", true],
  ["electrum", "lv", "ingot", true],
  ["grossular", "lv", "gem", true],
  ["invar", "lv", "ingot", true],
  ["lazurite", "lv", "gem", true],
  ["lead", "lv", "ingot", true],
  ["malachite", "lv", "gem", true],
  ["nickel", "lv", "ingot", true],
  ["olivine", "lv", "gem", true],
  ["potin", "lv", "ingot", true],
  ["pyrope", "lv", "gem", true],
  ["quartzite", "lv", "gem", true],
  ["realgar", "lv", "gem", true],
  ["red_alloy", "lv", "ingot", true],
  ["red_garnet", "lv", "gem", true],
  ["silver", "lv", "ingot", true],
  ["sodalite", "lv", "gem", true],
  ["soldering_alloy", "lv", "ingot", true],
  ["spessartine", "lv", "gem", true],
  ["tin", "lv", "ingot", true],
  ["uvarovite", "lv", "gem", true],
  ["wrought_iron", "lv", "ingot", true],
  ["yellow_garnet", "lv", "gem", true],
  ["zinc", "lv", "ingot", true],
  ["aluminium", "mv", "ingot", true],
  ["battery_alloy", "mv", "ingot", true],
  ["beryllium", "mv", "ingot", true],
  ["bismuth_bronze", "mv", "ingot", true],
  ["black_steel", "mv", "ingot", true],
  ["blue_alloy", "mv", "ingot", true],
  ["blue_topaz", "mv", "gem", true],
  ["chromium", "mv", "ingot", true],
  ["cupronickel", "mv", "ingot", true],
  ["damascus_steel", "mv", "ingot", true],
  ["gallium", "mv", "ingot", true],
  ["germanium", "mv", "ingot", true],
  ["green_sapphire", "mv", "gem", true],
  ["magnalium", "mv", "ingot", true],
  ["manganese", "mv", "ingot", true],
  ["neodymium", "mv", "ingot", true],
  ["opal", "mv", "gem", true],
  ["rose_gold", "mv", "ingot", true],
  ["ruby", "mv", "gem", true],
  ["rutile", "mv", "gem", true],
  ["sapphire", "mv", "gem", true],
  ["steel", "mv", "ingot", true],
  ["sterling_silver", "mv", "ingot", true],
  ["topaz", "mv", "gem", true],
  ["yttrium", "mv", "ingot", true],
  ["black_bronze", "hv", "ingot", true],
  ["gadolinium", "hv", "ingot", true],
  ["gallium_arsenide", "hv", "ingot", true],
  ["hsla_steel", "hv", "ingot", true],
  ["indium", "hv", "ingot", true],
  ["kanthal", "hv", "ingot", true],
  ["molybdenum", "hv", "ingot", true],
  ["monazite", "hv", "gem", true],
  ["nichrome", "hv", "ingot", true],
  ["nickel_zinc_ferrite", "hv", "ingot", true],
  ["samarium", "hv", "ingot", true],
  ["silicon", "hv", "ingot", true],
  ["stainless_steel", "hv", "ingot", true],
  ["tellurium", "hv", "ingot", true],
  ["titanium", "hv", "ingot", true],
  ["vanadium", "hv", "ingot", true],
  ["vanadium_steel", "hv", "ingot", true],
  ["molybdenum_disilicide", "ev", "ingot", true],
  ["niobium", "ev", "ingot", true],
  ["palladium", "ev", "ingot", true],
  ["platinum", "ev", "ingot", true],
  ["rtm_alloy", "ev", "ingot", true],
  ["tantalum", "ev", "ingot", true],
  ["thorium", "ev", "ingot", true],
  ["titanium_carbide", "ev", "ingot", true],
  ["tungsten", "ev", "ingot", true],
  ["tungsten_carbide", "ev", "ingot", true],
  ["tungsten_steel", "ev", "ingot", true],
  ["uranium", "ev", "ingot", true],
  ["hastelloy_c_276", "iv", "ingot", true],
  ["hastelloy_x", "iv", "ingot", true],
  ["holmium", "iv", "ingot", true],
  ["hssg", "iv", "ingot", true],
  ["incoloy_ma_956", "iv", "ingot", true],
  ["indium_gallium_phosphide", "iv", "ingot", true],
  ["iridium", "iv", "ingot", true],
  ["maraging_steel_300", "iv", "ingot", true],
  ["niobium_titanium", "iv", "ingot", true],
  ["osmiridium", "iv", "ingot", true],
  ["osmium", "iv", "ingot", true],
  ["plutonium", "iv", "ingot", true],
  ["rhodium", "iv", "ingot", true],
  ["ruthenium", "iv", "ingot", true],
  ["stellite_100", "iv", "ingot", true],
  ["tantalum_carbide", "iv", "ingot", true],
  ["thulium", "iv", "ingot", true],
  ["titanium_tungsten_carbide", "iv", "ingot", true],
  ["ultimet", "iv", "ingot", true],
  ["uranium_235", "iv", "ingot", true],
  ["watertight_steel", "iv", "ingot", true],
  ["zeron_100", "iv", "ingot", true],
  ["europium", "luv", "ingot", true],
  ["hsse", "luv", "ingot", true],
  ["hsss", "luv", "ingot", true],
  ["naquadah", "luv", "ingot", true],
  ["plutonium_241", "luv", "ingot", true],
  ["rhodium_plated_palladium", "luv", "ingot", true],
  ["ruridit", "luv", "ingot", true],
  ["americium", "zpm", "ingot", true],
  ["duranium", "zpm", "ingot", true],
  ["enriched_naquadah", "zpm", "ingot", true],
  ["naquadah_alloy", "zpm", "ingot", true],
  ["trinium", "zpm", "ingot", true],
  ["darmstadtium", "uv", "ingot", true],
  ["naquadria", "uv", "ingot", true],
  ["tritanium", "uv", "ingot", true],
  ["neutronium", "uhv", "ingot", true]
]

const APEX_CROP_TIERS = ['ulv', 'lv', 'mv', 'hv', 'ev', 'iv', 'luv', 'zpm', 'uv', 'uhv']
const APEX_CROP_VA = [8, 30, 120, 480, 1920, 7680, 30720, 122880, 491520, 1966080]
const APEX_CROP_BLOCKS = 32
const APEX_CROP_ESSENCE_PER_DUST = 24

ServerEvents.recipes(event => {
  let has = id => { try { return Item.exists(id) } catch (e) { return false } }
  let seedsMade = 0, essences = 0, missing = []
  let base = has('mysticalagriculture:prosperity_seed_base') ? 'mysticalagriculture:prosperity_seed_base' : 'minecraft:wheat_seeds'

  APEX_GT_CROPS.forEach(c => {
    let mat = c[0], unlock = APEX_CROP_TIERS.indexOf(c[1]), form = c[2], block = c[3]
    let seeds = `mysticalagriculture:gt_${mat}_seeds`, essence = `mysticalagriculture:gt_${mat}_essence`
    if (!has(seeds) || !has(essence)) { missing.push(mat); return }

    // nothing but the Synthesizer makes these seeds, nothing but the extractor uses the essence
    event.remove({ output: seeds })
    event.remove({ input: essence })

    for (let k = 0; k <= 3; k++) {
      let t = unlock + k
      if (t > 9) break
      let blocks = APEX_CROP_BLOCKS >> k
      let inputs = []
      let blockId = `gtceu:${mat}_block`
      if (block && has(blockId)) inputs.push(`${blocks}x ${blockId}`)
      else inputs.push(`${blocks * 9}x gtceu:${mat}_${form}`)
      // the cheaper versions take two circuits of the machine's own tier (also keeps the versions apart for GT)
      inputs.push(k === 0 ? `4x #gtceu:circuits/${APEX_CROP_TIERS[unlock]}` : `2x #gtceu:circuits/${APEX_CROP_TIERS[t]}`)
      if (k === 0) inputs.push(`1x gtceu:${APEX_CROP_TIERS[unlock]}_field_generator`)
      inputs.push(`1x ${base}`)
      event.recipes.gtapexadditions.seed_synthesizer(`apex:gt_seeds/${mat}_${APEX_CROP_TIERS[t]}`)
        .itemInputs(inputs)
        .itemOutputs(seeds)
        .duration(60 * 20)
        .EUt(APEX_CROP_VA[t])
      seedsMade++
    }

    let dust = `gtceu:${mat}_dust`
    if (has(dust)) {
      event.recipes.gtceu.extractor(`apex:gt_essence/${mat}`)
        .itemInputs(`${APEX_CROP_ESSENCE_PER_DUST}x ${essence}`)
        .itemOutputs(dust)
        .duration(40)
        .EUt(APEX_CROP_VA[Math.max(1, unlock - 1)])
      essences++
    }
  })
  console.info(`[APEX-CROPS] ${APEX_GT_CROPS.length - missing.length} GT crops: ${seedsMade} Seed Synthesizer recipes, ` +
    `${essences} essence -> dust recipes` + (missing.length ? `; not loaded (check config/mysticalcustomization): ${missing.join(', ')}` : ''))
})
