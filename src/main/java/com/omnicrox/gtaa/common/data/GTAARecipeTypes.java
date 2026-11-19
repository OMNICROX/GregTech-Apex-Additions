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
    public static GTRecipeType COMPONENT_ASSEMBLY_LINE;
    public static GTRecipeType QUANTUM_FORCE_TRANSFORMER;
    /** index 1..8 */
    public static final GTRecipeType[] PURIFICATION = new GTRecipeType[9];
    public static GTRecipeType PURIFICATION_PLANT;
    public static GTRecipeType ENTITY_CRUSHER;

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
                .addDataInfo(d -> "Base output: " + com.gregtechceu.gtceu.utils.FormattingUtil.formatNumbers(d.getLong("lnr_output")) + " EU/t")
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
        ENTITY_CRUSHER = register("extreme_entity_crusher", "multiblock", "Extreme Entity Crusher")
                .setMaxIOSize(0, 0, 0, 0).setEUIO(IO.IN);
    }
}
