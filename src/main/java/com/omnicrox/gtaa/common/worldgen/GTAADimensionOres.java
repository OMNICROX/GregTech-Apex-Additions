package com.omnicrox.gtaa.common.worldgen;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconType;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.GTLayerPattern;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.IWorldGenLayer;
import com.gregtechceu.gtceu.api.data.worldgen.SimpleWorldGenLayer;
import com.gregtechceu.gtceu.api.data.worldgen.WorldGeneratorUtils;
import com.gregtechceu.gtceu.api.data.worldgen.generator.indicators.SurfaceIndicatorGenerator;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.block.OreBlock;
import com.gregtechceu.gtceu.common.data.GTOres;
import com.omnicrox.gtaa.GTAA;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.loading.LoadingModList;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

/**
 * GT New Horizons-style dimension ores: some metals come from one world only, so you go there for them.
 * Aether (holystone): aluminium (bauxite), mica, lithium, topaz. Undergarden (depthrock): uranium, rare earths,
 * tantalum. Twilight Forest: GTNH's Twilight set (sapphire and olivine move there from the Overworld; galena, nickel,
 * molybdenum veins too). New GregTech ore blocks are made for holystone and depthrock, like GTNA does for Ad Astra's
 * planet stones. Overworld vein moves: gtapexadditions-common.toml, dimensionOres.moveOverworldVeins.
 */
public final class GTAADimensionOres {

    private GTAADimensionOres() {}

    public enum Realm {
        AETHER("aether", "Aether", new ResourceLocation("aether", "the_aether"), new ResourceLocation("aether", "holystone"),
                new ResourceLocation("aether", "block/natural/holystone")),
        UNDERGARDEN("undergarden", "Undergarden", new ResourceLocation("undergarden", "undergarden"),
                new ResourceLocation("undergarden", "depthrock"), new ResourceLocation("undergarden", "block/depthrock"));

        final String mod, english;
        final ResourceKey<Level> dim;
        final ResourceLocation stone, texture;
        final TagKey<Block> replaceables;
        TagPrefix prefix;
        SimpleWorldGenLayer layer;

        Realm(String mod, String english, ResourceLocation dim, ResourceLocation stone, ResourceLocation texture) {
            this.mod = mod;
            this.english = english;
            this.dim = ResourceKey.create(Registries.DIMENSION, dim);
            this.stone = stone;
            this.texture = texture;
            this.replaceables = BlockTags.create(GTAA.id(name().toLowerCase() + "_ore_replaceables"));
        }

        boolean loaded() {
            return LoadingModList.get() != null && LoadingModList.get().getModFileById(mod) != null;
        }

        Set<Material> materials() {
            return switch (this) {
                case AETHER -> Set.of(Bauxite, Ilmenite, Aluminium, Kyanite, Mica, Pollucite, Lepidolite, Spodumene, Lithium,
                        Topaz, BlueTopaz, Chalcocite);
                case UNDERGARDEN -> Set.of(Pitchblende, Uraninite, Thorium, Bastnasite, Monazite, Neodymium, Tantalite,
                        Pyrolusite, Pyrochlore, Cinnabar, Redstone, Ruby);
            };
        }

        /** the stone as worldgen places it (the Aether's natural blocks carry double_drops=true) */
        BlockState naturalStone() {
            Block b = BuiltInRegistries.BLOCK.get(stone);
            if (b == null || b == Blocks.AIR) return Blocks.AIR.defaultBlockState();
            BlockState s = b.defaultBlockState();
            Property<?> p = b.getStateDefinition().getProperty("double_drops");
            if (p instanceof BooleanProperty bp) s = s.setValue(bp, true);
            return s;
        }
    }

    public static final ResourceKey<Level> TWILIGHT = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation("twilightforest", "twilight_forest"));
    private static SimpleWorldGenLayer twilightLayer;

    private static boolean loaded(String mod) {
        return LoadingModList.get() != null && LoadingModList.get().getModFileById(mod) != null;
    }

    private static final class RealmOrePrefix extends TagPrefix {
        private final Realm realm;

        RealmOrePrefix(Realm realm) {
            super(realm.name().toLowerCase() + "_" + realm.stone.getPath());
            this.realm = realm;
        }

        @Override
        public boolean isIgnored(Material material) {
            return super.isIgnored(material) || !realm.materials().contains(material);
        }
    }

    // ---------------- IGTAddon hooks ----------------
    public static void registerTagPrefixes() {
        for (Realm r : Realm.values()) {
            if (!r.loaded()) continue;
            r.prefix = new RealmOrePrefix(r).defaultTagPath("ores/%s").prefixOnlyTagPath("ores_in_ground/%s")
                    .unformattedTagPath("ores").materialIconType(MaterialIconType.ore)
                    .miningToolTag(BlockTags.MINEABLE_WITH_PICKAXE).unificationEnabled(true)
                    .blockConstructor(OreBlock::new).generationCondition(TagPrefix.Conditions.hasOreProperty)
                    .langValue(r.english + " %s Ore")
                    .registerOre(r::naturalStone, null,
                            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0f, 3.0f),
                            r.texture);
        }
    }

    public static void registerWorldgenLayers() {
        for (Realm r : Realm.values()) {
            if (!r.loaded()) continue;
            TagKey<Block> tag = r.replaceables;
            r.layer = new SimpleWorldGenLayer("gtaa_" + r.name().toLowerCase(), () -> new TagMatchTest(tag), Set.of(r.dim.location()));
            WorldGeneratorUtils.WORLD_GEN_LAYERS.put(r.layer.getSerializedName(), r.layer);
        }
        if (loaded("twilightforest")) {
            twilightLayer = new SimpleWorldGenLayer("gtaa_twilight", () -> new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                    Set.of(TWILIGHT.location()));
            WorldGeneratorUtils.WORLD_GEN_LAYERS.put(twilightLayer.getSerializedName(), twilightLayer);
        }
    }

    public static void registerOreVeins() {
        if (Realm.AETHER.layer != null) {
            Realm a = Realm.AETHER;
            layered(a, "aether_bauxite_vein", 60, 10, 170, Bauxite, new Object[][] { { Bauxite, 3 }, { Ilmenite, 2 }, { Aluminium, 1 } });
            layered(a, "aether_mica_vein", 40, 10, 170, Mica, new Object[][] { { Kyanite, 3 }, { Mica, 2 }, { Pollucite, 1 } });
            layered(a, "aether_lithium_vein", 35, 10, 170, Lepidolite, new Object[][] { { Lepidolite, 3 }, { Spodumene, 2 }, { Lithium, 1 } });
            layered(a, "aether_topaz_vein", 25, 10, 170, Topaz, new Object[][] { { BlueTopaz, 3 }, { Topaz, 2 }, { Chalcocite, 1 } });
        }
        if (Realm.UNDERGARDEN.layer != null) {
            Realm u = Realm.UNDERGARDEN;
            layered(u, "undergarden_uranium_vein", 40, -60, 100, Pitchblende, new Object[][] { { Pitchblende, 3 }, { Uraninite, 2 }, { Thorium, 1 } });
            layered(u, "undergarden_rare_earth_vein", 40, -60, 100, Bastnasite, new Object[][] { { Bastnasite, 3 }, { Monazite, 2 }, { Neodymium, 1 } });
            layered(u, "undergarden_tantalite_vein", 35, -60, 100, Tantalite, new Object[][] { { Tantalite, 3 }, { Pyrolusite, 2 }, { Pyrochlore, 1 } });
            layered(u, "undergarden_cinnabar_vein", 30, -60, 100, Cinnabar, new Object[][] { { Cinnabar, 3 }, { Redstone, 2 }, { Ruby, 1 } });
        }
        if (twilightLayer != null) {
            twilight("twilight_sapphire_vein", 50, Sapphire, new Object[][] { { Almandine, 3 }, { Pyrope, 2 }, { Sapphire, 2 }, { GreenSapphire, 1 } });
            twilight("twilight_galena_vein", 50, Galena, new Object[][] { { Galena, 3 }, { Silver, 2 }, { Lead, 1 } });
            twilight("twilight_olivine_vein", 40, Olivine, new Object[][] { { Bentonite, 3 }, { Magnesite, 2 }, { Olivine, 2 }, { GlauconiteSand, 1 } });
            twilight("twilight_nickel_vein", 40, Nickel, new Object[][] { { Garnierite, 3 }, { Nickel, 2 }, { Cobaltite, 2 }, { Pentlandite, 1 } });
            twilight("twilight_molybdenum_vein", 30, Molybdenum, new Object[][] { { Wulfenite, 3 }, { Molybdenite, 2 }, { Molybdenum, 1 }, { Powellite, 1 } });
            if (moveOverworld()) {
                // GTNH: sapphire and olivine are Twilight Forest veins
                for (String v : new String[] { "sapphire_vein", "olivine_vein" }) {
                    GTOreDefinition d = GTRegistries.ORE_VEINS.get(new ResourceLocation("gtceu", v));
                    if (d != null) d.layer(IWorldGenLayer.NOWHERE).dimensions(Set.of());
                }
            }
        }
    }

    private static boolean moveOverworld() {
        try {
            return com.omnicrox.gtaa.config.GTAACommonConfig.MOVE_OVERWORLD_VEINS.get();
        } catch (Throwable t) {
            return true; // config not loaded yet
        }
    }

    private static void layered(Realm r, String name, int weight, int minY, int maxY, Material indicator, Object[][] layers) {
        vein(name, r.layer, Set.of(r.dim), weight, minY, maxY, indicator, layers, new RuleTest[] { new TagMatchTest(r.replaceables) });
    }

    private static void twilight(String name, int weight, Material indicator, Object[][] layers) {
        vein(name, twilightLayer, Set.of(TWILIGHT), weight, -50, 70, indicator, layers,
                new RuleTest[] { new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES) });
    }

    private static void vein(String name, IWorldGenLayer layer, Set<ResourceKey<Level>> dims, int weight, int minY, int maxY,
                             Material indicator, Object[][] layers, RuleTest[] rules) {
        Consumer<GTOreDefinition> cfg = v -> v.clusterSize(UniformInt.of(32, 40)).density(0.3f).weight(weight).layer(layer)
                .dimensions(dims).heightRangeUniform(minY, maxY)
                .layeredVeinGenerator(g -> g.withLayerPattern(() -> {
                    GTLayerPattern.Builder b = GTLayerPattern.builder(rules);
                    for (Object[] l : layers) {
                        Material m = (Material) l[0];
                        if (!m.hasProperty(PropertyKey.ORE)) continue;
                        int w = (Integer) l[1];
                        b.layer(x -> x.weight(w).mat(m).size(1, w + 1));
                    }
                    return b.build();
                }))
                .surfaceIndicatorGenerator(i -> i.surfaceRock(indicator).placement(SurfaceIndicatorGenerator.IndicatorPlacement.ABOVE));
        GTOres.create(GTAA.id(name), cfg);
    }

    @SuppressWarnings("unused")
    private static Supplier<BlockState> unused() {
        return null;
    }
}
