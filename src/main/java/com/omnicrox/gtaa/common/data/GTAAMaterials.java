package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.omnicrox.gtaa.GTAA;

/** Fluids for the antimatter line (SASS / SLAM). Names follow GTNH. */
public class GTAAMaterials {

    public static Material PROTOMATTER;
    public static Material ANTIMATTER;
    public static Material TACHYON_RICH_TEMPORAL_FLUID;
    public static Material SPACETIME;
    public static Material SPATIALLY_ENLARGED_FLUID;
    public static Material NAQUADAH_FUEL_MKV_DEPLETED;
    public static Material NAQUADAH_FUEL_MKVI_DEPLETED;

    private static Material fluid(String id, String lang, int color, int temperature) {
        return new Material.Builder(GTAA.id(id))
                .langValue(lang)
                .liquid(new FluidBuilder().temperature(temperature))
                .color(color)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }

    public static void init() {
        PROTOMATTER = fluid("protomatter", "Protomatter", 0x8EF8FF, 1);
        ANTIMATTER = fluid("antimatter", "Semi-Stable Antimatter", 0x2A0B47, 1);
        TACHYON_RICH_TEMPORAL_FLUID = fluid("tachyon_rich_temporal_fluid", "Tachyon Rich Temporal Fluid", 0x7D5BE0, 1);
        SPACETIME = fluid("spacetime", "Molten SpaceTime", 0x15123A, 100_000_000);
        SPATIALLY_ENLARGED_FLUID = fluid("spatially_enlarged_fluid", "Spatially Enlarged Fluid", 0x2E9BD6, 1);
        NAQUADAH_FUEL_MKV_DEPLETED = fluid("naquadah_based_liquid_fuel_mkv_depleted",
                "Naquadah Based Liquid Fuel MkV (Depleted)", 0x2E3A2E, 300);
        NAQUADAH_FUEL_MKVI_DEPLETED = fluid("naquadah_based_liquid_fuel_mkvi_depleted",
                "Naquadah Based Liquid Fuel MkVI (Depleted)", 0x233023, 300);
    }
}
