package com.omnicrox.gtaa.api;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.omnicrox.gtaa.GTAA;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recipe type for the Draconic Fusion Crafter. Draconic Evolution's own fusion crafting recipes are pulled in as
 * proxy recipes every time recipes load (so datapack / KubeJS changes to them carry over). DE is read through
 * reflection, so GTAA has no hard dependency on it.
 * <p>
 * DE tech level -> casing tier / voltage / time: Draconium 1 LuV 20 s, Wyvern 2 ZPM 40 s, Draconic 3 UV 80 s,
 * Chaotic 4 UHV 160 s.
 */
public class DefcRecipeType extends GTRecipeType {

    public static final ResourceLocation DE_FUSION = new ResourceLocation("draconicevolution", "fusion_crafting");
    public static final String TIER_KEY = "defc_tier";
    private static final int[] VOLTAGE = { GTValues.LuV, GTValues.ZPM, GTValues.UV, GTValues.UHV };
    private static final int[] DURATION = { 400, 800, 1600, 3200 };

    public DefcRecipeType(ResourceLocation id, String group) {
        super(id, group);
    }

    /** called in common setup, once DE's recipe type is registered */
    public void linkProxy() {
        RecipeType<?> type = ForgeRegistries.RECIPE_TYPES.getValue(DE_FUSION);
        if (type != null && !proxyRecipes.containsKey(type)) {
            proxyRecipes.put(type, new ArrayList<>());
            GTAA.LOGGER.info("Draconic Fusion Crafter linked to Draconic Evolution fusion recipes");
        }
    }

    @Override
    public GTRecipe toGTrecipe(ResourceLocation id, Recipe<?> recipe) {
        try {
            ClassLoader cl = recipe.getClass().getClassLoader();
            Class<?> fr = Class.forName("com.brandon3055.draconicevolution.api.crafting.IFusionRecipe", false, cl);
            Class<?> fi = Class.forName("com.brandon3055.draconicevolution.api.crafting.IFusionRecipe$IFusionIngredient", false, cl);
            if (!fr.isInstance(recipe)) return super.toGTrecipe(id, recipe);
            Ingredient catalyst = (Ingredient) fr.getMethod("getCatalyst").invoke(recipe);
            List<?> ingredients = (List<?>) fr.getMethod("fusionIngredients").invoke(recipe);
            Enum<?> level = (Enum<?>) fr.getMethod("getRecipeTier").invoke(recipe);
            Method get = fi.getMethod("get");
            Method consume = fi.getMethod("consume");
            int tier = Math.max(1, Math.min(4, level.ordinal() + 1));

            var builder = recipeBuilder(id);
            builder.inputItems(catalyst);
            // merge identical ingredients so the recipe reads (and matches) as stacks
            Map<String, Ingredient> keys = new LinkedHashMap<>();
            Map<String, Integer> counts = new LinkedHashMap<>();
            for (Object o : ingredients) {
                Ingredient ing = (Ingredient) get.invoke(o);
                if (!(boolean) consume.invoke(o)) {
                    builder.notConsumable(ing);
                    continue;
                }
                String key = ing.toJson().toString();
                keys.putIfAbsent(key, ing);
                counts.merge(key, 1, Integer::sum);
            }
            keys.forEach((k, ing) -> builder.inputItems(ing, counts.get(k)));
            builder.outputItems(recipe.getResultItem(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)));
            builder.EUt(GTValues.VA[VOLTAGE[tier - 1]]).duration(DURATION[tier - 1]).addData(TIER_KEY, tier);
            return GTRecipeSerializer.SERIALIZER.fromJson(id, builder.build().serializeRecipe());
        } catch (Throwable t) {
            GTAA.LOGGER.warn("Could not convert fusion recipe {}: {}", id, t.toString());
            return super.toGTrecipe(id, recipe);
        }
    }
}
