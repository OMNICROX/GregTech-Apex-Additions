package com.omnicrox.gtaa.common.infernal;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class InfernalConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.IntValue CHANCE;
    public static final ForgeConfigSpec.IntValue ULTRA_PERCENT;
    public static final ForgeConfigSpec.IntValue INFERNAL_PERCENT;
    public static final ForgeConfigSpec.BooleanValue FROM_SPAWNERS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISABLED_MODIFIERS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Infernal mobs: rare hostile mobs with random modifiers, more health and much better loot").push("infernal");
        ENABLED = b.define("enabled", true);
        CHANCE = b.comment("1 in this many hostile mob spawns becomes infernal").defineInRange("chance", 30, 1, 100000);
        ULTRA_PERCENT = b.comment("Percent of infernal mobs that are Ultra (4-6 modifiers) instead of Elite (2-3)")
                .defineInRange("ultraPercent", 18, 0, 100);
        INFERNAL_PERCENT = b.comment("Percent of infernal mobs that are full Infernal (7-9 modifiers)")
                .defineInRange("infernalPercent", 4, 0, 100);
        FROM_SPAWNERS = b.comment("Can mobs from spawners become infernal").define("fromSpawners", true);
        MOB_BLACKLIST = b.comment("Entity ids that never become infernal (the tag forge:bosses is always excluded)")
                .defineListAllowEmpty("mobBlacklist", List.of("minecraft:creeper"), o -> o instanceof String);
        DIMENSION_BLACKLIST = b.defineListAllowEmpty("dimensionBlacklist", List.of(), o -> o instanceof String);
        DISABLED_MODIFIERS = b.comment("Modifier ids to turn off, e.g. \"ender\"")
                .defineListAllowEmpty("disabledModifiers", List.of(), o -> o instanceof String);
        b.pop();
        SPEC = b.build();
    }

    private InfernalConfig() {}
}
