package com.omnicrox.gtaa.config;

import net.minecraftforge.common.ForgeConfigSpec;

/** config/gtapexadditions-common.toml */
public final class GTAACommonConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue AUTOBUILD_CASING_FIRST;
    public static final ForgeConfigSpec.BooleanValue GLASS_TIER_LIMITS_ENERGY;
    public static final ForgeConfigSpec.BooleanValue PERSONAL_SPACE;
    public static final ForgeConfigSpec.BooleanValue MOVE_OVERWORLD_VEINS;

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
        b.push("dimensionOres");
        MOVE_OVERWORLD_VEINS = b.comment("GT New Horizons: the sapphire and olivine veins only spawn in the Twilight Forest (not the Overworld).",
                "The Aether, Undergarden and Twilight veins are added either way. New chunks only; needs a restart.")
                .define("moveOverworldVeins", true);
        b.pop();
        b.push("personalSpace");
        PERSONAL_SPACE = b.comment("Personal Space Portals (GT New Horizons): each player can make one world of their own.",
                "Needs the Infiniverse mod. Off by default: GTNA has its own personal dimension. The portal has no recipe",
                "while this is off (give it with /give); turning it off keeps the worlds on disk but the portals stop working.")
                .define("enabled", false);
        b.pop();
        SPEC = b.build();
    }

    private GTAACommonConfig() {}
}
