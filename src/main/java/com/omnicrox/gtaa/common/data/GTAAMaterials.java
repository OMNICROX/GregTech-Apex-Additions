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
    // naquadah fuel line (GoodGenerator, MIT)
    public static Material URANIUM_FUEL, URANIUM_FUEL_EXCITED, URANIUM_FUEL_DEPLETED;
    public static Material THORIUM_FUEL, THORIUM_FUEL_EXCITED, THORIUM_FUEL_DEPLETED;
    public static Material PLUTONIUM_FUEL, PLUTONIUM_FUEL_EXCITED, PLUTONIUM_FUEL_DEPLETED;
    public static Material NAQUADAH_SOLUTION, LIGHT_NAQUADAH_FUEL, HEAVY_NAQUADAH_FUEL, NAQUADAH_GAS, NAQUADAH_ASPHALT;
    public static Material EXTREMELY_UNSTABLE_NAQUADAH, ATOMIC_SEPARATION_CATALYST;
    /** index 1..6 = Mk I..VI */
    public static final Material[] NAQUADAH_FUEL = new Material[7];
    public static final Material[] NAQUADAH_FUEL_DEPLETED = new Material[7];
    // purification plant (GT5-Unofficial)
    /** index 1..8 */
    public static final Material[] PURIFIED_WATER = new Material[9];
    public static Material OZONE, POLYALUMINIUM_CHLORIDE, FLOCCULATION_WASTE, ACTIVATED_CARBON, PRE_ACTIVATED_CARBON, DIRTY_ACTIVATED_CARBON;
    public static Material NAQUADAH_FUEL_MKV_DEPLETED;
    public static Material NAQUADAH_FUEL_MKVI_DEPLETED;

    /** true when the three shared fluids belong to gtna. */
    public static boolean externalFluids() {
        return ModList.get().isLoaded("gtna");
    }

    /** true when GT Community Additions supplies the purified water grades. */
    public static boolean externalWater() {
        return ModList.get().isLoaded("gtca");
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
        // GoodGenerator (MIT): made in MK IV fusion, cheap in MK V. gtna has its own; use that one when present.
        if (!externalFluids()) METASTABLE_OGANESSON = new Material.Builder(GTAA.id("metastable_oganesson"))
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

        URANIUM_FUEL = fluid("uranium_based_liquid_fuel", "Uranium Based Liquid Fuel", 0x00FF00, 300, false);
        URANIUM_FUEL_EXCITED = fluid("uranium_based_liquid_fuel_excited", "Uranium Based Liquid Fuel (Excited State)", 0x00FF00, 6_000, false);
        URANIUM_FUEL_DEPLETED = fluid("uranium_based_liquid_fuel_depleted", "Uranium Based Liquid Fuel (Depleted)", 0x6E8C6E, 300, false);
        THORIUM_FUEL = fluid("thorium_based_liquid_fuel", "Thorium Based Liquid Fuel", 0x50003C, 300, false);
        THORIUM_FUEL_EXCITED = fluid("thorium_based_liquid_fuel_excited", "Thorium Based Liquid Fuel (Excited State)", 0x50003C, 6_000, false);
        THORIUM_FUEL_DEPLETED = fluid("thorium_based_liquid_fuel_depleted", "Thorium Based Liquid Fuel (Depleted)", 0x3C2836, 300, false);
        PLUTONIUM_FUEL = fluid("plutonium_based_liquid_fuel", "Plutonium Based Liquid Fuel", 0xEF1515, 300, false);
        PLUTONIUM_FUEL_EXCITED = fluid("plutonium_based_liquid_fuel_excited", "Plutonium Based Liquid Fuel (Excited State)", 0xEF1515, 6_000, false);
        PLUTONIUM_FUEL_DEPLETED = fluid("plutonium_based_liquid_fuel_depleted", "Plutonium Based Liquid Fuel (Depleted)", 0x7A3232, 300, false);
        NAQUADAH_SOLUTION = fluid("naquadah_solution", "Naquadah Solution", 0x3D3D3D, 300, false);
        LIGHT_NAQUADAH_FUEL = fluid("light_naquadah_fuel", "Light Naquadah Fuel", 0x92E05A, 300, false);
        HEAVY_NAQUADAH_FUEL = fluid("heavy_naquadah_fuel", "Heavy Naquadah Fuel", 0x3A7A2B, 300, false);
        NAQUADAH_GAS = new Material.Builder(GTAA.id("naquadah_gas")).langValue("Naquadah Gas")
                .gas(new FluidBuilder()).color(0x575757).flags(MaterialFlags.DISABLE_DECOMPOSITION).buildAndRegister();
        NAQUADAH_ASPHALT = fluid("naquadah_asphalt", "Naquadah Asphalt", 0x1E1E1E, 300, false);
        EXTREMELY_UNSTABLE_NAQUADAH = new Material.Builder(GTAA.id("extremely_unstable_naquadah"))
                .langValue("Extremely Unstable Naquadah").dust().color(0x1A2D1A)
                .iconSet(com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.SHINY)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION).buildAndRegister();
        if (!externalFluids()) ATOMIC_SEPARATION_CATALYST = new Material.Builder(GTAA.id("atomic_separation_catalyst"))
                .langValue("Atomic Separation Catalyst").dust().liquid(new FluidBuilder().temperature(5_000))
                .color(0xE85AB5).iconSet(com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.SHINY)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION).buildAndRegister();
        String[] mk = { "", "mki", "mkii", "mkiii", "mkiv", "mkv", "mkvi" };
        String[] mkName = { "", "MkI", "MkII", "MkIII", "MkIV", "MkV", "MkVI" };
        int[] colors = { 0, 0x3E9100, 0x2B7A00, 0x1A6400, 0x0F4F00, 0x063B00, 0x002A00 };
        for (int i = 1; i <= 6; i++) {
            NAQUADAH_FUEL[i] = fluid("naquadah_based_liquid_fuel_" + mk[i], "Naquadah Based Liquid Fuel " + mkName[i],
                    colors[i], 300 + 1000 * i, false);
            if (i <= 4) NAQUADAH_FUEL_DEPLETED[i] = fluid("naquadah_based_liquid_fuel_" + mk[i] + "_depleted",
                    "Naquadah Based Liquid Fuel " + mkName[i] + " (Depleted)", 0x333333 + 0x050505 * i, 300, false);
        }
        String[] grade = { "", "Filtered", "Ozonated", "Flocculated", "pH Neutralized", "Extreme-Temperature Treated",
                "Ultraviolet Treated Electrically Neutral", "Degassed Decontaminant-Free", "Subatomically Perfect" };
        int[] gcol = { 0, 0x3E6FE0, 0x3A8CE0, 0x37A6E0, 0x34C0E0, 0x31D8E0, 0x2EE0C8, 0x2BE0A8, 0xE0E0FF };
        if (!externalWater()) for (int g = 1; g <= 8; g++) PURIFIED_WATER[g] = fluid("grade_" + g + "_purified_water",
                "Grade " + g + " Purified Water (" + grade[g] + ")", gcol[g], 300, false);
        OZONE = new Material.Builder(GTAA.id("ozone")).langValue("Ozone").gas(new FluidBuilder()).color(0x80C0FF)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION).buildAndRegister();
        POLYALUMINIUM_CHLORIDE = fluid("polyaluminium_chloride", "Polyaluminium Chloride", 0xD0D0B0, 300, false);
        FLOCCULATION_WASTE = fluid("flocculation_waste", "Flocculation Waste Liquid", 0x6A6A40, 300, false);
        ACTIVATED_CARBON = GTMaterials.ActivatedCarbon; // GTCEu's own
        PRE_ACTIVATED_CARBON = new Material.Builder(GTAA.id("pre_activated_carbon")).langValue("Pre-Activated Carbon").dust()
                .color(0x3A3A2A).flags(MaterialFlags.DISABLE_DECOMPOSITION).buildAndRegister();
        DIRTY_ACTIVATED_CARBON = new Material.Builder(GTAA.id("dirty_activated_carbon")).langValue("Dirty Activated Carbon").dust()
                .color(0x4A3A2A).flags(MaterialFlags.DISABLE_DECOMPOSITION).buildAndRegister();
        NAQUADAH_FUEL_DEPLETED[5] = NAQUADAH_FUEL_MKV_DEPLETED;
        NAQUADAH_FUEL_DEPLETED[6] = NAQUADAH_FUEL_MKVI_DEPLETED;
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
                GTMaterials.Iridium, GTMaterials.Beryllium, GTMaterials.Praseodymium, GTMaterials.Caesium,
                GTMaterials.Uranium235, GTMaterials.Naquadah, GTMaterials.Naquadria })
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

    private static Material external(String id) {
        Material m = GTCEuAPI.materialManager.getMaterial(id);
        if (m == null) GTAA.LOGGER.error("[GTAA] expected material {} was not found", id);
        return m;
    }

    private static void ensureDust(Material m) {
        if (m != null && !m.hasProperty(com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.DUST))
            m.setProperty(com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.DUST,
                    new com.gregtechceu.gtceu.api.data.chemical.material.properties.DustProperty());
    }

    /** Called after every addon registered its materials. */
    public static void resolveExternal() {
        var L = com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys.LIQUID;
        if (externalWater()) {
            for (int g = 1; g <= 8; g++) {
                PURIFIED_WATER[g] = external("gtca:purified_water_grade_" + g);
                if (PURIFIED_WATER[g] != null) ensureFluid(PURIFIED_WATER[g], L);
            }
        }
        ensureDust(ACTIVATED_CARBON);
        if (!externalFluids()) return;
        // gtna's versions replace GTAA's; make sure they have the forms GTAA's recipes use
        METASTABLE_OGANESSON = external("gtna:metastable_oganesson");
        if (METASTABLE_OGANESSON != null) {
            ensureDust(METASTABLE_OGANESSON);
            if (!METASTABLE_OGANESSON.hasProperty(com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.INGOT))
                METASTABLE_OGANESSON.setProperty(com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.INGOT,
                        new com.gregtechceu.gtceu.api.data.chemical.material.properties.IngotProperty());
            METASTABLE_OGANESSON.addFlags(MaterialFlags.GENERATE_PLATE);
            ensureFluid(METASTABLE_OGANESSON, L);
        }
        ATOMIC_SEPARATION_CATALYST = external("gtna:atomic_separation_catalyst");
        if (ATOMIC_SEPARATION_CATALYST != null) {
            ensureDust(ATOMIC_SEPARATION_CATALYST);
            ensureFluid(ATOMIC_SEPARATION_CATALYST, L);
        }
        TACHYON_RICH_TEMPORAL_FLUID = GTCEuAPI.materialManager.getMaterial("gtna:temporal_fluid");
        SPACETIME = GTCEuAPI.materialManager.getMaterial("gtna:space_time");
        SPATIALLY_ENLARGED_FLUID = GTCEuAPI.materialManager.getMaterial("gtna:spatial_fluid");
        if (SPACETIME == null || SPATIALLY_ENLARGED_FLUID == null || TACHYON_RICH_TEMPORAL_FLUID == null)
            GTAA.LOGGER.error("[GTAA] gtna is loaded but its SpaceTime/Spatial/Temporal fluids were not found");
    }
}
