package com.omnicrox.gtaa.common.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Black Hole Compressor "neutronium compressor" mode: every Avaritia Neutronium Compressor recipe (singularities,
 * neutronium...) as a GT recipe - same input count, 10 s base time, UV power. Read from the loaded Re-Avaritia
 * recipes by reflection so there is no hard dependency.
 */
public enum NeutroniumCompressionLogic implements GTRecipeType.ICustomRecipeLogic {

    INSTANCE;

    private List<GTRecipe> cache = List.of();
    private RecipeManager cachedFor;
    private static RecipeManager lastSeen;

    public synchronized List<GTRecipe> recipes(RecipeManager rm) {
        if (rm == null) return cache;
        lastSeen = rm;
        if (rm == cachedFor) return cache;
        List<GTRecipe> out = new ArrayList<>();
        for (Recipe<?> r : rm.getRecipes()) {
            Class<?> c = r.getClass();
            if (!c.getName().startsWith("committee.nova.mods.avaritia") || !c.getSimpleName().equals("CompressorRecipe")) continue;
            try {
                Method in = c.getMethod("getInput"), count = c.getMethod("getInputCount"), res = c.getMethod("getResultItem");
                Ingredient ing = (Ingredient) in.invoke(r);
                int n = (Integer) count.invoke(r);
                ItemStack result = ((ItemStack) res.invoke(r)).copy();
                if (ing.isEmpty() || result.isEmpty() || n <= 0) continue;
                out.add(GTAARecipeTypes.NEUTRONIUM_COMPRESSION.recipeBuilder(r.getId().getNamespace() + "_" +
                                r.getId().getPath().replace('/', '_'))
                        .inputItems(ing, n).outputItems(result).duration(200).EUt(GTValues.VA[GTValues.UV]).buildRawRecipe());
            } catch (Throwable ignored) {}
        }
        cache = out;
        cachedFor = rm;
        return cache;
    }

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        Level level = holder instanceof MetaMachine m ? m.getLevel() : null;
        if (level == null) return null;
        for (GTRecipe r : recipes(level.getRecipeManager()))
            if (RecipeHelper.matchRecipe(holder, r).isSuccess()) return r;
        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        for (GTRecipe r : recipes(lastSeen)) GTAARecipeTypes.NEUTRONIUM_COMPRESSION.addToMainCategory(r);
    }
}
