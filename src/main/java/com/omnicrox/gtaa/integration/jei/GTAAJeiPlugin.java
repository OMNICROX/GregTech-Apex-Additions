package com.omnicrox.gtaa.integration.jei;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.omnicrox.gtaa.GTAA;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class GTAAJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return GTAA.id("jei_plugin");
    }

    private static boolean on() {
        try {
            return com.omnicrox.gtaa.config.GTAAClientConfig.PREVIEW_ENABLED.get();
        } catch (Throwable t) {
            return true;
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (!on()) return;
        registration.addRecipeCategories(new MultiblockPreviewCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!on()) return;
        List<MultiblockPreviewCategory.Recipe> recipes = new ArrayList<>();
        for (var def : GTRegistries.MACHINES.values())
            if (def instanceof MultiblockMachineDefinition m) recipes.add(MultiblockPreviewCategory.recipe(m));
        registration.addRecipes(MultiblockPreviewCategory.TYPE, recipes);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        com.omnicrox.gtaa.client.preview.PreviewPanel.SHOW_RECIPES = (stack, uses) -> {
            try {
                var focus = runtime.getJeiHelpers().getFocusFactory().createFocus(
                        uses ? mezz.jei.api.recipe.RecipeIngredientRole.INPUT : mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT,
                        mezz.jei.api.constants.VanillaTypes.ITEM_STACK, stack);
                runtime.getRecipesGui().show(focus);
            } catch (Throwable ignored) {}
        };
        if (!on() || !com.omnicrox.gtaa.config.GTAAClientConfig.PREVIEW_HIDE_GREGTECH.get()) return;
        // GregTech's own (static under NEI) multiblock page goes away
        try {
            runtime.getRecipeManager().hideRecipeCategory(
                    com.gregtechceu.gtceu.integration.jei.multipage.MultiblockInfoCategory.RECIPE_TYPE);
        } catch (Throwable t) {
            try {
                var type = runtime.getRecipeManager().getRecipeType(new ResourceLocation("gtceu", "multiblock_info"));
                type.ifPresent(rt -> runtime.getRecipeManager().hideRecipeCategory((RecipeType<?>) rt));
            } catch (Throwable ignored) {}
        }
        com.omnicrox.gtaa.client.preview.MultiblockPreview.releaseAll();
    }
}
