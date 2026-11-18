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

    private static GTRecipeType register(String name, String group) {
        var type = new GTRecipeType(GTAA.id(name), group);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, type.registryName, type);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, type.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(type.registryName, type);
        return type;
    }

    public static void init() {
        SPACE_ASSEMBLER = register("space_assembler", "multiblock")
                .setMaxIOSize(16, 1, 4, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ASSEMBLER, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .setMaxTooltips(4);
        SPACE_MINING = register("space_mining", "multiblock")
                .setMaxIOSize(2, 9, 1, 0)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
                .setMaxTooltips(4);
    }
}
