package com.omnicrox.gtaa.common.infernal;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/** Defaults are GT New Horizons 2.9's InfernalMobs.cfg. */
public final class InfernalConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.IntValue ELITE_RARITY, ULTRA_RARITY, INFERNO_RARITY;
    public static final ForgeConfigSpec.IntValue MIN_ELITE, MAX_ELITE, MIN_ULTRA, MAX_ULTRA, MIN_INFERNO, MAX_INFERNO;
    public static final ForgeConfigSpec.DoubleValue HEALTH_FACTOR;
    public static final ForgeConfigSpec.BooleanValue FROM_SPAWNERS;
    public static final ForgeConfigSpec.BooleanValue OVERLAY;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_BLACKLIST, ALWAYS_INFERNAL, DIMENSION_BLACKLIST,
            DISABLED_MODIFIERS, DROPS_ELITE, DROPS_ULTRA, DROPS_INFERNAL;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Infernal Mobs, matching the GT New Horizons fork: rare hostile mobs with random modifiers, a",
                "boss bar, much more health and enchanted loot").push("infernal");
        ENABLED = b.define("enabled", true);
        ELITE_RARITY = b.comment("1 in this many hostile mob spawns gets modifiers").defineInRange("eliteRarity", 20, 1, 1000000);
        ULTRA_RARITY = b.comment("1 in this many of those rolls Ultra modifier counts").defineInRange("ultraRarity", 10, 1, 1000000);
        INFERNO_RARITY = b.comment("1 in this many Ultras rolls Infernal modifier counts").defineInRange("infernoRarity", 7, 1, 1000000);
        MIN_ELITE = b.defineInRange("minEliteModifiers", 2, 1, 27);
        MAX_ELITE = b.defineInRange("maxEliteModifiers", 5, 1, 27);
        MIN_ULTRA = b.defineInRange("minUltraModifiers", 5, 1, 27);
        MAX_ULTRA = b.defineInRange("maxUltraModifiers", 10, 1, 27);
        MIN_INFERNO = b.defineInRange("minInfernoModifiers", 8, 1, 27);
        MAX_INFERNO = b.defineInRange("maxInfernoModifiers", 15, 1, 27);
        HEALTH_FACTOR = b.comment("Max health = base max health x modifier count x this")
                .defineInRange("mobHealthFactor", 1.8, 0.01, 1000);
        FROM_SPAWNERS = b.comment("Can mobs from spawners become infernal").define("fromSpawners", true);
        OVERLAY = b.comment("Client: show the boss bar when looking at an infernal mob").define("bossBarOverlay", true);
        MOB_BLACKLIST = b.comment("Entity ids that never get modifiers (the forge:bosses tag is always excluded)")
                .defineListAllowEmpty("mobBlacklist", List.of("minecraft:wither", "minecraft:ender_dragon", "minecraft:warden",
                        "minecraft:elder_guardian", "twilightforest:naga", "twilightforest:lich", "twilightforest:lich_minion",
                        "twilightforest:hydra", "twilightforest:ur_ghast", "twilightforest:snow_queen", "twilightforest:minoshroom",
                        "twilightforest:knight_phantom", "twilightforest:alpha_yeti", "twilightforest:armored_giant",
                        "twilightforest:upper_goblin_knight", "twilightforest:maze_slime", "twilightforest:minotaur"),
                        o -> o instanceof String);
        ALWAYS_INFERNAL = b.comment("Entity ids that always get modifiers (GTNH: blazes, ghasts, magma cubes, witches, druids...)")
                .defineListAllowEmpty("alwaysInfernal", List.of("minecraft:blaze", "minecraft:ghast", "minecraft:magma_cube",
                        "minecraft:witch", "twilightforest:skeleton_druid", "twilightforest:death_tome"), o -> o instanceof String);
        DIMENSION_BLACKLIST = b.defineListAllowEmpty("dimensionBlacklist", List.of(), o -> o instanceof String);
        DISABLED_MODIFIERS = b.comment("Modifiers that never roll (GTNH turns these five off)")
                .defineListAllowEmpty("disabledModifiers", List.of("Ender", "Lifesteal", "Ninja", "Rust", "Sticky"), o -> o instanceof String);
        b.comment("Drop lists: one entry is picked per 5 modifiers (at least one). Entry = item id (or potion=<potion id>), optionally *count.",
                "Tools / armour get random enchantments, enchanted books a random enchantment. Rank by modifier count:",
                "Rare 1-5, Ultra 6-10, Infernal 11+").push("drops");
        DROPS_ELITE = b.defineListAllowEmpty("rare", List.of("potion=strong_regeneration",
                "potion=strong_healing", "minecraft:iron_axe", "minecraft:iron_sword", "minecraft:iron_hoe",
                "minecraft:iron_helmet", "minecraft:iron_chestplate", "minecraft:iron_leggings", "minecraft:iron_boots",
                "minecraft:piston", "minecraft:dispenser", "minecraft:sticky_piston", "minecraft:dropper", "minecraft:iron_ingot*2",
                "minecraft:gold_ingot", "minecraft:golden_helmet", "minecraft:golden_chestplate", "minecraft:golden_leggings",
                "minecraft:golden_boots", "minecraft:glistering_melon_slice", "minecraft:magma_cream", "minecraft:bow",
                "minecraft:chainmail_helmet", "minecraft:chainmail_chestplate", "minecraft:chainmail_leggings",
                "minecraft:chainmail_boots"), o -> o instanceof String);
        DROPS_ULTRA = b.defineListAllowEmpty("ultra", List.of("potion=strong_regeneration*2",
                "potion=strong_healing*2", "minecraft:ender_pearl", "minecraft:diamond", "minecraft:blaze_powder*2",
                "minecraft:golden_axe", "minecraft:golden_sword", "minecraft:golden_hoe", "minecraft:chainmail_helmet",
                "minecraft:chainmail_chestplate", "minecraft:chainmail_leggings", "minecraft:chainmail_boots", "minecraft:glowstone",
                "minecraft:iron_door", "minecraft:glistering_melon_slice*2", "minecraft:magma_cream*2", "minecraft:fire_charge",
                "minecraft:enchanted_book", "minecraft:bow", "minecraft:golden_apple", "minecraft:ghast_tear"), o -> o instanceof String);
        DROPS_INFERNAL = b.defineListAllowEmpty("infernal", List.of("potion=strong_regeneration*3",
                "potion=strong_healing*3", "minecraft:blaze_powder*3", "minecraft:diamond*2", "minecraft:ender_pearl*2",
                "minecraft:diamond_axe", "minecraft:diamond_sword", "minecraft:diamond_hoe", "minecraft:diamond_helmet",
                "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots", "minecraft:gold_block",
                "minecraft:diamond_block", "minecraft:emerald_block", "minecraft:enchanted_book", "minecraft:ghast_tear*2"),
                o -> o instanceof String);
        b.pop();
        b.pop();
        SPEC = b.build();
    }

    private InfernalConfig() {}
}
