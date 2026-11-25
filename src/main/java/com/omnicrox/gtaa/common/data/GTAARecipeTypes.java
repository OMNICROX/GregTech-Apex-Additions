package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.omnicrox.gtaa.GTAA;
import net.minecraft.core.registries.BuiltInRegistries;

public class GTAARecipeTypes {

    /** Recipe data key: minimum Space Assembler module tier (1-3). */
    public static final String MODULE_TIER = "module_tier";

    public static GTRecipeType SPACE_ASSEMBLER;
    public static GTRecipeType SPACE_MINING;
    public static GTRecipeType SPACE_PUMPING;
    public static GTRecipeType DYSON_LAUNCH;
    public static GTRecipeType NAQUADAH_FUEL_REFINERY;
    public static GTRecipeType NAQUADAH_REACTOR;
    public static GTRecipeType HYPER_NAQUADAH_REACTOR;
    public static GTRecipeType FUEL_REFINING_COMPLEX;
    public static GTRecipeType COMPONENT_ASSEMBLY_LINE;
    public static GTRecipeType QUANTUM_FORCE_TRANSFORMER;
    /** index 1..8 */
    public static final GTRecipeType[] PURIFICATION = new GTRecipeType[9];
    public static GTRecipeType PURIFICATION_PLANT;
    public static GTRecipeType ENTITY_CRUSHER;
    public static GTRecipeType INDUSTRIAL_ROCK_BREAKER;
    public static GTRecipeType ELECTRIC_IMPLOSION;
    public static GTRecipeType NEUTRONIUM_COMPRESSION;
    public static GTRecipeType CULTIVATION;
    public static GTRecipeType SUIT_FABRICATION;
    public static GTRecipeType SEED_SYNTHESIS;
    public static GTRecipeType MANA_NEXUS;
    public static com.omnicrox.gtaa.api.DefcRecipeType DRACONIC_FUSION;

    private static GTRecipeType register(String name, String group, String english) {
        GTAA.REGISTRATE.addRawLang(GTAA.MOD_ID + "." + name, english);
        var type = new GTRecipeType(GTAA.id(name), group);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, type.registryName, type);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, type.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(type.registryName, type);
        return type;
    }

    public static void init() {
        SPACE_ASSEMBLER = register("space_assembler", "multiblock", "Space Assembler")
                .setMaxIOSize(16, 1, 4, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ASSEMBLER, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .setMaxTooltips(4);
        SPACE_MINING = register("space_mining", "multiblock", "Space Mining")
                .setMaxIOSize(2, 9, 1, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .setMaxTooltips(4);
        SPACE_PUMPING = register("space_pumping", "multiblock", "Space Pumping")
                .setMaxIOSize(1, 0, 0, 1)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .setMaxTooltips(4);
        DYSON_LAUNCH = register("dyson_launch", "multiblock", "Dyson Swarm Launch")
                .setMaxIOSize(2, 0, 1, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .setMaxTooltips(4);
        NAQUADAH_FUEL_REFINERY = register("naquadah_fuel_refinery", "multiblock", "Naquadah Fuel Refinery")
                .setMaxIOSize(4, 0, 4, 1)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addDataInfo(d -> "Needs Field Restriction Coil T" + d.getInt("nfr_coil_tier"))
                .setMaxTooltips(4);
        NAQUADAH_REACTOR = register("naquadah_reactor", "multiblock", "Large Naquadah Reactor")
                .setMaxIOSize(0, 0, 1, 1)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addDataInfo(d -> "Base output: " + com.omnicrox.gtaa.api.GTAAFormat.num(d.getLong("lnr_output")) + " EU/t")
                .setMaxTooltips(4);
        HYPER_NAQUADAH_REACTOR = register("hyper_naquadah_reactor", "multiblock", "Hyper Naquadah Reaction")
                .setMaxIOSize(0, 0, 2, 1)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addDataInfo(d -> "Output: " + com.omnicrox.gtaa.api.GTAAFormat.num(d.getLong("hnr_power")) + " EU/t per parallel")
                .addDataInfo(d -> d.getInt("hnr_tier") >= 2 ? "Needs the Advanced Hyper Naquadah Reactor" : "Hyper or Advanced Hyper Naquadah Reactor")
                .setMaxTooltips(4);
        FUEL_REFINING_COMPLEX = register("fuel_refining_complex", "multiblock", "Fuel Refining Complex")
                .setMaxIOSize(3, 0, 8, 1)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addDataInfo(d -> "Heat: " + com.omnicrox.gtaa.api.GTAAFormat.num(d.getInt("frc_heat")) + " K")
                .addDataInfo(d -> "Needs Field Restriction Coil T" + d.getInt("nfr_coil_tier"))
                .setMaxTooltips(4);
        COMPONENT_ASSEMBLY_LINE = register("component_assembly_line", "multiblock", "Component Assembly Line")
                .setMaxIOSize(16, 1, 6, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addDataInfo(d -> "Casing tier: " + com.gregtechceu.gtceu.api.GTValues.VN[Math.max(1, Math.min(13, d.getInt("coal_casing_tier")))])
                .setMaxTooltips(4);
        QUANTUM_FORCE_TRANSFORMER = register("quantum_force_transformer", "multiblock", "Quantum Force Transformer")
                .setMaxIOSize(6, 6, 6, 6)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addDataInfo(d -> "Manipulator tier " + d.getInt("qft_tier") + ", focus tier " + d.getInt("qft_focus"))
                .addDataInfo(d -> "Catalyst: " + d.getString("qft_catalyst").replace("gtapexadditions:", ""))
                .setMaxTooltips(6);
        String[] unit = { "", "Clarifier", "Ozonation", "Flocculation", "pH Adjustment", "Extreme Temperature Fluctuation",
                "High Energy Laser Purification", "Residual Decontaminant Degasser", "Absolute Baryonic Perfection" };
        for (int g = 1; g <= 8; g++) {
            PURIFICATION[g] = register("purification_grade_" + g, "multiblock", "Purification: " + unit[g])
                    .setMaxIOSize(9, 4, 4, 2)
                    .setEUIO(IO.IN)
                    .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                    .addDataInfo(d -> "Base success chance: " + d.getInt("pur_chance") + "%")
                    .setMaxTooltips(4);
        }
        PURIFICATION_PLANT = register("purification_plant", "multiblock", "Water Purification Plant")
                .setMaxIOSize(0, 0, 0, 0).setEUIO(IO.IN);
        INDUSTRIAL_ROCK_BREAKER = register("industrial_rock_breaker", "multiblock", "Industrial Rock Breaker")
                .setMaxIOSize(2, 1, 0, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, ProgressTexture.FillDirection.LEFT_TO_RIGHT);
        ELECTRIC_IMPLOSION = register("electric_implosion", "multiblock", "Electric Implosion Compressor")
                .setMaxIOSize(6, 2, 1, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addCustomRecipeLogic(com.omnicrox.gtaa.common.recipe.ElectricImplosionLogic.INSTANCE);
        NEUTRONIUM_COMPRESSION = register("neutronium_compression", "multiblock", "Neutronium Compressor")
                .setMaxIOSize(1, 1, 0, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_COMPRESS, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addCustomRecipeLogic(com.omnicrox.gtaa.common.recipe.NeutroniumCompressionLogic.INSTANCE);
        ENTITY_CRUSHER = register("extreme_entity_crusher", "multiblock", "Extreme Entity Crusher")
                .setMaxIOSize(0, 0, 0, 0).setEUIO(IO.IN);
        CULTIVATION = register("mystical_cultivation_array", "multiblock", "Mystical Cultivation Array")
                .setMaxIOSize(1, 2, 1, 0).setEUIO(IO.IN)
                .addDataInfo(data -> "Shown for plain soil; better farmland multiplies it")
                .addDataInfo(data -> "Seeds aren't used up; real drops follow the crop");
        MANA_NEXUS = register("mana_nexus", "multiblock", "Sylvan Arbiter")
                .setMaxIOSize(16, 4, 1, 0).setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addDataInfo(data -> data.getLong("mana") > 0 ? "Mana: " + com.omnicrox.gtaa.api.GTAAFormat.num(data.getLong("mana")) : "")
                .addDataInfo(data -> "Mana Cores: " + com.omnicrox.gtaa.common.machine.multiblock.ManaNexusMachine.CORE_NAMES[
                        Math.max(1, Math.min(5, data.getInt("nexus_tier")))] + " or better");
        SEED_SYNTHESIS = register("seed_synthesizer", "electric", "Seed Synthesizer")
                .setMaxIOSize(9, 1, 0, 0).setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .setSound(com.gregtechceu.gtceu.common.data.GTSoundEntries.SCIENCE);
        SUIT_FABRICATION = register("suit_fabrication", "multiblock", "Suit Fabrication")
                .setMaxIOSize(16, 4, 1, 0).setEUIO(IO.IN)
                .addDataInfo(data -> "Station tier: " + data.getInt("station_tier"))
                .addDataInfo(data -> data.getString("suit_kit").isEmpty() ? "" : "Kit: " + data.getString("suit_kit"))
                .addDataInfo(data -> data.getString("suit_origin").isEmpty() ? "" : "Power: " + data.getString("suit_origin"));
        GTAA.REGISTRATE.addRawLang(GTAA.MOD_ID + ".draconic_fusion_crafter", "Draconic Fusion Crafting");
        DRACONIC_FUSION = new com.omnicrox.gtaa.api.DefcRecipeType(GTAA.id("draconic_fusion_crafter"), "multiblock");
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, DRACONIC_FUSION.registryName, DRACONIC_FUSION);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, DRACONIC_FUSION.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(DRACONIC_FUSION.registryName, DRACONIC_FUSION);
        DRACONIC_FUSION.setMaxIOSize(18, 1, 0, 0).setEUIO(IO.IN)
                .setProgressBar(com.gregtechceu.gtceu.api.gui.GuiTextures.PROGRESS_BAR_FUSION,
                        com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .addDataInfo(data -> "Fusion casing tier: " + data.getInt(com.omnicrox.gtaa.api.DefcRecipeType.TIER_KEY));
    }
}
