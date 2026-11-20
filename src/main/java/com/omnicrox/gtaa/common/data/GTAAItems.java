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
    public static final ItemEntry<Item> QUARK_CREATION_CATALYST = REGISTRATE.item("quark_creation_catalyst", Item::new)
            .lang("Quark Creation Catalyst").model(NonNullBiConsumer.noop()).register();

    public static void init() {}
}
