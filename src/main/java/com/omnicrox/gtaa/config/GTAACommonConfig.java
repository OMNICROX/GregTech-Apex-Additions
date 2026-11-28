package com.omnicrox.gtaa.config;

import net.minecraftforge.common.ForgeConfigSpec;

/** config/gtapexadditions-common.toml */
public final class GTAACommonConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue AUTOBUILD_CASING_FIRST;
    public static final ForgeConfigSpec.BooleanValue GLASS_TIER_LIMITS_ENERGY;
    public static final ForgeConfigSpec.BooleanValue PERSONAL_SPACE;
    public static final ForgeConfigSpec.BooleanValue MOVE_OVERWORLD_VEINS;
    public static final ForgeConfigSpec.BooleanValue HNN_EXTRACTOR;
    public static final ForgeConfigSpec.BooleanValue GIVE_GUIDE;
    public static final ForgeConfigSpec.BooleanValue MAXWELL_MAKER;
    public static final ForgeConfigSpec.IntValue INSANE_CELL_TYPES;
    public static final ForgeConfigSpec.DoubleValue HNN_SIM_COST;

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
        b.push("guide");
        GIVE_GUIDE = b.comment("Give every player the Apex Guide the first time they join a world.").define("giveOnFirstJoin", true);
        b.pop();
        b.push("hostileNetworks");
        HNN_EXTRACTOR = b.comment("A Hostile Neural Networks prediction in a GregTech Extractor gives every drop its data model lists.")
                .define("extractorPredictions", true);
        HNN_SIM_COST = b.comment("Simulation Chamber power (FE/t per data model) is multiplied by this. 0.125 = an eighth of HNN's cost.")
                .defineInRange("simCostMultiplier", 0.125, 0.0, 10.0);
        b.pop();
        b.push("insaneAE");
        INSANE_CELL_TYPES = b.comment("How many different items (or fluids) one InsaneAE storage cell holds. 1 = one type per cell, like",
                "GT New Horizons' singularity cells; storing many types at that scale is the Singularity Data Hub's job.",
                "0 = leave InsaneAE's own numbers (up to millions of types per cell). Needs a restart.")
                .defineInRange("typesPerCell", 1, 0, 63);
        b.pop();
        b.push("maxwell");
        MAXWELL_MAKER = b.comment("The Maxwell Maker: an LV multiblock that makes Maxwell at 8 EU/t in 16,000,000 seconds (about 185 days).",
                "Off: the machine has no recipes (its controller can still be made). Needs a restart or /reload.")
                .define("maxwellMaker", true);
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
