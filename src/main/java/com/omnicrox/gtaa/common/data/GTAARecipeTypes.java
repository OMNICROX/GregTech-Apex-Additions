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
    }
}
