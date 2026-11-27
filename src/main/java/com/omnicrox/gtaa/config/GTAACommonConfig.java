package com.omnicrox.gtaa.config;

import net.minecraftforge.common.ForgeConfigSpec;

/** config/gtapexadditions-common.toml */
public final class GTAACommonConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue AUTOBUILD_CASING_FIRST;
    public static final ForgeConfigSpec.BooleanValue GLASS_TIER_LIMITS_ENERGY;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("multiblocks");
        AUTOBUILD_CASING_FIRST = b.comment("GregTech's auto-builder (Terminal, Structure Upgrader) puts casings, not hatches,",
                "in spots that take either. Applies to every mod's multiblocks; patterns built before a change keep the old order.")
                .define("autoBuildCasingFirst", true);
        GLASS_TIER_LIMITS_ENERGY = b.comment("GT New Horizons' glass rule: a multiblock built with tiered glass can't run with an energy",
                "hatch above its lowest glass tier (Borosilicate = HV ... Universium = UXV; tempered = HV, laminated = IV, fusion = LuV).")
                .define("glassTierLimitsEnergyHatches", true);
        b.pop();
        SPEC = b.build();
    }

    private GTAACommonConfig() {}
}
