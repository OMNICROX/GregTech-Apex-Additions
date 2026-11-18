package com.omnicrox.gtaa;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.omnicrox.gtaa.data.GTAARecipes;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

@GTAddon
public class GTAAAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTAA.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        GTAA.LOGGER.info("{} loaded", GTAA.NAME);
    }

    @Override
    public String addonModId() {
        return GTAA.MOD_ID;
    }

    @Override
    public boolean requiresHighTier() {
        return true; // UHV+ machines and hatches
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTAARecipes.init(provider);
    }
}
