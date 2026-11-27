package com.omnicrox.apexcore;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.omnicrox.apexcore.recipe.WaferRecipes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

@GTAddon
public class ApexCoreAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return ApexCore.REGISTRATE;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return ApexCore.MOD_ID;
    }

    @Override
    public void removeRecipes(Consumer<ResourceLocation> consumer) {
        WaferRecipes.remove(consumer);
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        WaferRecipes.add(provider);
    }
}
