package com.omnicrox.gtaa.common.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Electric Implosion Compressor recipes: every Implosion Compressor recipe (GTCEu's, KubeJS', other mods') with
 * its explosives (TNT, dynamite, industrial TNT, powderbarrels) taken out, built on the fly from the loaded recipes.
 */
public enum ElectricImplosionLogic implements GTRecipeType.ICustomRecipeLogic {

    INSTANCE;

    private List<GTRecipe> cache = List.of();
    private int cacheKey = -1;

    private static List<GTRecipe> all(GTRecipeType type) {
        List<GTRecipe> out = new ArrayList<>();
        for (Set<GTRecipe> set : type.getCategoryMap().values()) out.addAll(set);
        return out;
    }

    private static boolean explosive(Object content) {
        try {
            Ingredient ing = ItemRecipeCapability.CAP.of(content);
            ItemStack[] probes = { new ItemStack(Items.TNT), GTItems.DYNAMITE.asStack(), GTBlocks.INDUSTRIAL_TNT.asStack(),
                    GTBlocks.POWDERBARREL.asStack() };
            for (ItemStack p : probes) if (ing.test(p)) return true;
        } catch (Throwable ignored) {}
        return false;
    }

    public synchronized List<GTRecipe> recipes() {
        List<GTRecipe> src = all(GTRecipeTypes.IMPLOSION_RECIPES);
        int key = src.size() * 31 + System.identityHashCode(GTRecipeTypes.IMPLOSION_RECIPES.getCategoryMap());
        if (key == cacheKey) return cache;
        Map<String, GTRecipe> unique = new LinkedHashMap<>();
        for (GTRecipe r : src) {
            Map<RecipeCapability<?>, List<Content>> inputs = new HashMap<>();
            for (var e : r.inputs.entrySet()) {
                List<Content> list = new ArrayList<>();
                for (Content c : e.getValue())
                    if (e.getKey() != ItemRecipeCapability.CAP || !explosive(c.content)) list.add(c);
                inputs.put(e.getKey(), list);
            }
            GTRecipe copy = new GTRecipe(GTAARecipeTypes.ELECTRIC_IMPLOSION, r.id, inputs, new HashMap<>(r.outputs),
                    new HashMap<>(r.tickInputs), new HashMap<>(r.tickOutputs), new HashMap<>(r.inputChanceLogics),
                    new HashMap<>(r.outputChanceLogics), new HashMap<>(r.tickInputChanceLogics),
                    new HashMap<>(r.tickOutputChanceLogics), new ArrayList<>(r.conditions), new ArrayList<>(r.ingredientActions),
                    r.data.copy(), r.duration, GTAARecipeTypes.ELECTRIC_IMPLOSION.getCategory(), r.groupColor);
            // the same compression with different explosives collapses to one recipe
            unique.putIfAbsent(String.valueOf(RecipeHelper.getInputItems(copy)) + RecipeHelper.getOutputItems(copy), copy);
        }
        cache = new ArrayList<>(unique.values());
        cacheKey = key;
        return cache;
    }

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        for (GTRecipe r : recipes())
            if (RecipeHelper.matchRecipe(holder, r).isSuccess() && RecipeHelper.matchTickRecipe(holder, r).isSuccess()) return r;
        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        for (GTRecipe r : recipes()) GTAARecipeTypes.ELECTRIC_IMPLOSION.addToMainCategory(r);
    }
}
