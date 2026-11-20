// Apex Ascension - GregTech ore veins for other mods' ores (v1.2 prep).
// GregTech already turns vanilla ores into veins (gtceu.yaml: removeVanillaOreGen). This file does the same for the
// modded ores that have no GregTech material: their scattered worldgen is removed (kubejs/data/apex/forge/biome_modifier)
// and they spawn here as big GregTech-style veins instead, using the mods' own ore blocks so their recipes keep working.
// Duplicate metals (silver, lead, sapphire, extra lapis) are not re-added: GregTech's own veins cover them.
// Only applies to newly generated chunks.

GTCEuServerEvents.oreVeins(event => {
  let B = id => () => Block.getBlock(id)
  let add = (id, mods, fn) => {
    if (!mods.every(m => Platform.isLoaded(m))) { console.info(`[APEX-ORES] skipped ${id} (mod missing)`); return }
    try { event.add(`apex:${id}`, fn); console.info(`[APEX-ORES] added ${id}`) }
    catch (e) { console.error(`[APEX-ORES] failed ${id}: ${e}`) }
  }

  // ---------------- Overworld ----------------
  add('inferium_vein', ['mysticalagriculture'], v => {
    v.weight(50).clusterSize(32).density(0.3).layer('stone').heightRangeUniform(0, 96)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(4).block(B('mysticalagriculture:inferium_ore')).size(2, 4))
      .layer(l => l.weight(1).block(B('mysticalagriculture:prosperity_ore')).size(1, 2))
      .layer(l => l.weight(1).mat(GTMaterials.Coal).size(1, 2))))
    v.surfaceIndicatorGenerator(i => i.surfaceRock(GTMaterials.Coal).placement('above'))
  })
  add('inferium_vein_deep', ['mysticalagriculture'], v => {
    v.weight(30).clusterSize(32).density(0.3).layer('deepslate').heightRangeUniform(-60, 0)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(3).block(B('mysticalagriculture:deepslate_inferium_ore')).size(2, 4))
      .layer(l => l.weight(2).block(B('mysticalagriculture:deepslate_prosperity_ore')).size(1, 3))))
  })
  add('prosperity_vein', ['mysticalagriculture'], v => {
    v.weight(30).clusterSize(28).density(0.25).layer('stone').heightRangeUniform(-10, 64)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(3).block(B('mysticalagriculture:prosperity_ore')).size(2, 4))
      .layer(l => l.weight(1).mat(GTMaterials.Lapis).size(1, 2))
      .layer(l => l.weight(1).mat(GTMaterials.Quartzite).size(1, 1))))
    v.surfaceIndicatorGenerator(i => i.surfaceRock(GTMaterials.Lapis).placement('above'))
  })
  add('uraninite_vein', ['powah'], v => {
    v.weight(25).clusterSize(36).density(0.25).layer('deepslate').heightRangeUniform(-55, 0)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(3).block(B('powah:deepslate_uraninite_ore')).size(2, 3))
      .layer(l => l.weight(2).block(B('powah:deepslate_uraninite_ore_poor')).size(1, 3))
      .layer(l => l.weight(1).block(B('powah:deepslate_uraninite_ore_dense')).size(1, 1))
      .layer(l => l.weight(2).mat(GTMaterials.Uraninite).size(1, 2))
      .layer(l => l.weight(1).mat(GTMaterials.Pitchblende).size(1, 1))))
    v.surfaceIndicatorGenerator(i => i.surfaceRock(GTMaterials.Uraninite).placement('above'))
  })
  add('draconium_vein', ['draconicevolution'], v => {
    v.weight(15).clusterSize(24).density(0.2).layer('deepslate').heightRangeUniform(-64, -32)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(3).block(B('draconicevolution:deepslate_draconium_ore')).size(1, 3))
      .layer(l => l.weight(2).mat(GTMaterials.Graphite).size(1, 2))
      .layer(l => l.weight(1).mat(GTMaterials.Diamond).size(1, 1))))
  })
  add('chlorophyll_vein', ['tinkers_thinking'], v => {
    v.weight(25).clusterSize(28).density(0.25).layer('stone').heightRangeUniform(10, 80)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(3).block(B('tinkers_thinking:chlorophyll_ore')).size(2, 4))
      .layer(l => l.weight(1).mat(GTMaterials.Malachite).size(1, 2))))
    v.surfaceIndicatorGenerator(i => i.surfaceRock(GTMaterials.Malachite).placement('above'))
  })

  // ---------------- Nether ----------------
  add('cobalt_ardite_vein', ['tconstruct'], v => {
    v.weight(50).clusterSize(36).density(0.25).layer('netherrack').heightRangeUniform(10, 117)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => {
      p.layer(l => l.weight(4).block(B('tconstruct:cobalt_ore')).size(2, 4))
      if (Platform.isLoaded('tic3nh')) p.layer(l => l.weight(2).block(B('tic3nh:ardite_ore')).size(1, 3))
      if (Platform.isLoaded('tinkers_thinking')) p.layer(l => l.weight(1).block(B('tinkers_thinking:ardite_ore')).size(1, 2))
      p.layer(l => l.weight(1).mat(GTMaterials.Cobaltite).size(1, 2))
    }))
    v.surfaceIndicatorGenerator(i => i.surfaceRock(GTMaterials.Cobaltite).placement('above'))
  })
  add('nether_draconium_vein', ['draconicevolution'], v => {
    v.weight(20).clusterSize(28).density(0.2).layer('netherrack').heightRangeUniform(5, 120)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(3).block(B('draconicevolution:nether_draconium_ore')).size(1, 3))
      .layer(l => l.weight(2).mat(GTMaterials.NetherQuartz).size(1, 2))))
  })

  // ---------------- End ----------------
  add('end_draconium_vein', ['draconicevolution'], v => {
    v.weight(40).clusterSize(36).density(0.25).layer('endstone').heightRangeUniform(10, 80)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(4).block(B('draconicevolution:end_draconium_ore')).size(2, 4))
      .layer(l => l.weight(1).mat(GTMaterials.Naquadah).size(1, 1))))
  })
  add('zith_vein', ['tinkers_thinking'], v => {
    v.weight(25).clusterSize(28).density(0.25).layer('endstone').heightRangeUniform(10, 80)
    v.layeredVeinGenerator(g => g.buildLayerPattern(p => p
      .layer(l => l.weight(3).block(B('tinkers_thinking:zith_ore')).size(2, 4))
      .layer(l => l.weight(1).mat(GTMaterials.Scheelite).size(1, 1))))
  })
})
