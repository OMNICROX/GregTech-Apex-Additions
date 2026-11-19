package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.omnicrox.gtaa.GTAA;
import net.minecraftforge.fml.ModList;

/**
 * Fluids for the antimatter line (SASS / SLAM). Names follow GTNH.
 * Molten SpaceTime, Spatially Enlarged Fluid and Tachyon Rich Temporal Fluid come from GT: New Age (gtna)
 * when it is installed; otherwise GTAA registers its own copies.
 */
public class GTAAMaterials {

    public static Material PROTOMATTER;
    public static Material ANTIMATTER;
    public static Material TACHYON_RICH_TEMPORAL_FLUID;
    public static Material SPACETIME;
    public static Material SPATIALLY_ENLARGED_FLUID;
    public static Material METASTABLE_OGANESSON;
    public static Material NAQUADAH_FUEL_MKV_DEPLETED;
    public static Material NAQUADAH_FUEL_MKVI_DEPLETED;

    /** true when the three shared fluids belong to gtna. */
    public static boolean externalFluids() {
        return ModList.get().isLoaded("gtna");
    }

    private static Material fluid(String id, String lang, int color, int temperature, boolean texture) {
        FluidBuilder fb = new FluidBuilder().temperature(temperature);
        if (texture) fb.customStill().disableColor();
        return new Material.Builder(GTAA.id(id))
                .langValue(lang)
                .liquid(fb)
                .color(color)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }

    public static void init() {
        PROTOMATTER = fluid("protomatter", "Protomatter", 0x8EF8FF, 1, true);
        ANTIMATTER = fluid("antimatter", "Semi-Stable Antimatter", 0x2A0B47, 1, true);
        if (!externalFluids()) {
            TACHYON_RICH_TEMPORAL_FLUID = fluid("temporal_fluid", "Tachyon Rich Temporal Fluid", 0x7D5BE0, 1, true);
            SPACETIME = fluid("space_time", "Molten SpaceTime", 0x15123A, 100_000_000, false);
            SPATIALLY_ENLARGED_FLUID = fluid("spatial_fluid", "Spatially Enlarged Fluid", 0x2E9BD6, 1, true);
        }
        // GoodGenerator (MIT): made in MK IV fusion, cheap in MK V
        METASTABLE_OGANESSON = new Material.Builder(GTAA.id("metastable_oganesson"))
                .langValue("Metastable Oganesson")
                .ingot().liquid(new FluidBuilder().temperature(10_380))
                .color(0xE61C24).secondaryColor(0x5A0A0A)
                .iconSet(com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.SHINY)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION, MaterialFlags.GENERATE_PLATE)
                .blastTemp(10_380, com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty.GasTier.HIGHEST)
                .buildAndRegister();
        NAQUADAH_FUEL_MKV_DEPLETED = fluid("naquadah_based_liquid_fuel_mkv_depleted",
                "Naquadah Based Liquid Fuel MkV (Depleted)", 0x2E3A2E, 300, false);
        NAQUADAH_FUEL_MKVI_DEPLETED = fluid("naquadah_based_liquid_fuel_mkvi_depleted",
                "Naquadah Based Liquid Fuel MkVI (Depleted)", 0x233023, 300, false);
    }

    /** Fluid forms GTCEu doesn't have but the MK IV / MK V fusion recipes need (added in PostMaterialEvent). */
    public static void addFusionFluids() {
        var L = com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys.LIQUID;
        var P = com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys.PLASMA;
        for (Material m : new Material[] { GTMaterials.Curium, GTMaterials.Californium, GTMaterials.Flerovium,
                GTMaterials.Plutonium241, GTMaterials.Oganesson, GTMaterials.Yttrium, GTMaterials.Rubidium,
                GTMaterials.Lutetium, GTMaterials.Vanadium, GTMaterials.Tellurium, GTMaterials.Zinc,
                GTMaterials.Osmium, GTMaterials.Silicon, GTMaterials.Manganese, GTMaterials.NaquadahEnriched,
                GTMaterials.Antimony, GTMaterials.Lithium, GTMaterials.Cobalt, GTMaterials.Magnesium,
                GTMaterials.Iridium, GTMaterials.Beryllium })
            ensureFluid(m, L);
        for (Material m : new Material[] { GTMaterials.Radon, GTMaterials.Neptunium, GTMaterials.Boron,
                GTMaterials.Fermium, GTMaterials.Xenon, GTMaterials.Calcium, GTMaterials.Copper, GTMaterials.Bromine,
                GTMaterials.Plutonium241, GTMaterials.Lead, GTMaterials.Thorium })
            ensureFluid(m, P);
    }

    private static void ensureFluid(Material m, com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey key) {
        try {
            var prop = m.getProperty(com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.FLUID);
            if (prop != null && (prop.get(key) != null || prop.getQueuedBuilder(key) != null)) return;
            FluidBuilder b = new FluidBuilder();
            if (key == com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys.LIQUID) {
                var blast = m.getProperty(com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.BLAST);
                if (blast != null) b.temperature(Math.max(300, blast.getBlastTemperature()));
            }
            if (prop == null) {
                m.setProperty(com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.FLUID,
                        new com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty(key, b));
            } else {
                prop.enqueueRegistration(key, b);
            }
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[GTAA] could not add {} to {}: {}", key, m.getName(), t.toString());
        }
    }

    /** Called after every addon registered its materials. */
    public static void resolveExternal() {
        if (!externalFluids()) return;
        TACHYON_RICH_TEMPORAL_FLUID = GTCEuAPI.materialManager.getMaterial("gtna:temporal_fluid");
        SPACETIME = GTCEuAPI.materialManager.getMaterial("gtna:space_time");
        SPATIALLY_ENLARGED_FLUID = GTCEuAPI.materialManager.getMaterial("gtna:spatial_fluid");
        if (SPACETIME == null || SPATIALLY_ENLARGED_FLUID == null || TACHYON_RICH_TEMPORAL_FLUID == null)
            GTAA.LOGGER.error("[GTAA] gtna is loaded but its SpaceTime/Spatial/Temporal fluids were not found");
    }
}
