package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.item.Item;

import static com.omnicrox.gtaa.GTAA.REGISTRATE;

public class GTAAItems {

    static {
        REGISTRATE.creativeModeTab(() -> GTAACreativeTab.MAIN);
    }

    /** Mining drones LV..OpV (index = GT voltage tier). Textures from GTNH-Intergalactic. */
    @SuppressWarnings("unchecked")
    public static final ItemEntry<Item>[] MINING_DRONES = new ItemEntry[GTValues.OpV + 1];

    static {
        for (int tier = GTValues.LV; tier <= GTValues.OpV; tier++) {
            String vn = GTValues.VN[tier].toLowerCase(java.util.Locale.ROOT);
            MINING_DRONES[tier] = REGISTRATE.item("mining_drone_" + vn, Item::new)
                    .lang(GTValues.VN[tier] + " Mining Drone")
                    .model(NonNullBiConsumer.noop())
                    .register();
        }
    }

    /** Launched by the Dyson Sphere Launch Site. */
    public static final ItemEntry<Item> DYSON_SWARM_MODULE = REGISTRATE.item("dyson_swarm_module", Item::new)
            .lang("Dyson Swarm Module")
            .model(NonNullBiConsumer.noop())
            .register();

    /** Quantum Force Transformer catalysts (GT++). Not consumed; the stack size sets the parallels. */
    public static final java.util.Map<String, ItemEntry<Item>> QFT_CATALYSTS = new java.util.LinkedHashMap<>();

    static {
        String[][] cats = { { "platinum_group", "Platinum Group Catalyst" }, { "plastic_polymer", "Plastic Polymer Catalyst" },
                { "rubber_polymer", "Rubber Polymer Catalyst" }, { "adhesion_promoter", "Adhesion Promoter Catalyst" },
                { "tita_tungsten_indium", "Tita-Tungsten Indium Catalyst" }, { "radioactivity", "Radioactivity Catalyst" },
                { "rare_earth_group", "Rare-Earth Group Catalyst" }, { "simple_naquadah", "Simple Naquadah Catalyst" },
                { "advanced_naquadah", "Advanced Naquadah Catalyst" }, { "raw_intelligence", "Raw Intelligence Catalyst" },
                { "ultimate_plastic", "Ultimate Plasticizer Catalyst" },
                { "biological_intelligence", "Biological Intelligence Catalyst" },
                { "temporal_harmony", "Temporal Harmonizer Catalyst" } };
        for (String[] c : cats) {
            QFT_CATALYSTS.put(c[0], REGISTRATE.item(c[0] + "_catalyst", Item::new)
                    .lang(c[1]).model(NonNullBiConsumer.noop()).register());
        }
    }

    public static final ItemEntry<Item> ACTIVATED_CARBON_FILTER_MESH = REGISTRATE.item("activated_carbon_filter_mesh", Item::new)
            .lang("Activated Carbon Filter Mesh").model(NonNullBiConsumer.noop()).register();
    public static final ItemEntry<com.omnicrox.gtaa.common.item.AngelRingItem> ANGEL_RING = REGISTRATE
            .item("angel_ring", com.omnicrox.gtaa.common.item.AngelRingItem::new)
            .lang("Angel Ring").model(NonNullBiConsumer.noop()).register();
    public static final ItemEntry<com.omnicrox.gtaa.common.item.StructureUpgrader> STRUCTURE_UPGRADER = REGISTRATE
            .item("structure_upgrader", com.omnicrox.gtaa.common.item.StructureUpgrader::new)
            .lang("Structure Upgrader").model(NonNullBiConsumer.noop()).register();
    public static final ItemEntry<com.omnicrox.gtaa.common.item.InfernalSigilItem> ELITE_SIGIL = sigil("elite_sigil", "Rare Sigil",
            com.omnicrox.gtaa.common.infernal.InfernalMobs.Rank.ELITE);
    public static final ItemEntry<com.omnicrox.gtaa.common.item.InfernalSigilItem> ULTRA_SIGIL = sigil("ultra_sigil", "Ultra Sigil",
            com.omnicrox.gtaa.common.infernal.InfernalMobs.Rank.ULTRA);
    public static final ItemEntry<com.omnicrox.gtaa.common.item.InfernalSigilItem> INFERNAL_SIGIL = sigil("infernal_sigil", "Infernal Sigil",
            com.omnicrox.gtaa.common.infernal.InfernalMobs.Rank.INFERNAL);

    private static ItemEntry<com.omnicrox.gtaa.common.item.InfernalSigilItem> sigil(String id, String name,
                                                                                    com.omnicrox.gtaa.common.infernal.InfernalMobs.Rank rank) {
        return REGISTRATE.item(id, p -> new com.omnicrox.gtaa.common.item.InfernalSigilItem(rank, p))
                .lang(name).model(NonNullBiConsumer.noop()).register();
    }
    public static final ItemEntry<Item> QUARK_CREATION_CATALYST = REGISTRATE.item("quark_creation_catalyst", Item::new)
            .lang("Quark Creation Catalyst").model(NonNullBiConsumer.noop()).register();

    /** the MAX circuit: GregTech has the tag but nothing in the pack made one (the MAX hatches need them) */
    public static final ItemEntry<Item> MAX_CIRCUIT = REGISTRATE
            .<Item>item("max_universal_circuit", p -> new Item(p.rarity(net.minecraft.world.item.Rarity.EPIC)))
            .lang("MAX Universal Circuit").tag(com.gregtechceu.gtceu.data.recipe.CustomTags.MAX_CIRCUITS)
            .model(NonNullBiConsumer.noop()).register();

    /** GT5-Unofficial's Nuclear Star (texture from GT5-Unofficial, LGPL-3.0) */
    public static final ItemEntry<Item> NUCLEAR_STAR = REGISTRATE
            .<Item>item("nuclear_star", p -> new Item(p.rarity(net.minecraft.world.item.Rarity.EPIC)) {
                @Override
                public void appendHoverText(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level level,
                                            java.util.List<net.minecraft.network.chat.Component> tooltip,
                                            net.minecraft.world.item.TooltipFlag flag) {
                    tooltip.add(net.minecraft.network.chat.Component.literal("§7By the powers of Greg, I command this star to be really hot."));
                }

                @Override
                public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
                    return true;
                }
            })
            .lang("Nuclear Star").model(NonNullBiConsumer.noop()).register();

    public static final ItemEntry<Item> SPIRE_LIGHTNING_ROD = REGISTRATE.item("spire_lightning_rod", Item::new)
            .lang("Spire Lightning Rod").model(NonNullBiConsumer.noop()).register();

    public static final ItemEntry<Item> BLACK_HOLE_SEED = REGISTRATE.item("black_hole_seed", Item::new)
            .lang("Black Hole Seed").model(NonNullBiConsumer.noop()).register();
    public static final ItemEntry<Item> BLACK_HOLE_COLLAPSER = REGISTRATE.item("black_hole_collapser", Item::new)
            .lang("Black Hole Collapser").model(NonNullBiConsumer.noop()).register();

    public static void init() {}
}
