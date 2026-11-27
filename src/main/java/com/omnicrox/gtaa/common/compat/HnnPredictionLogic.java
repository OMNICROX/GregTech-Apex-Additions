package com.omnicrox.gtaa.common.compat;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.config.GTAACommonConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gregtechceu.gtceu.api.GTValues.*;

/**
 * Hostile Neural Networks predictions in the GregTech Extractor: one prediction gives every drop its model lists
 * (what the Loot Fabricator would let you pick one of). The Simulation Chamber only trains models and makes
 * predictions; the Loot Fabricator has no recipe (apex_v13_integration.js). Reflection only, so GTAA loads without HNN.
 * Toggle: gtapexadditions-common.toml, hostileNetworks.extractorPredictions.
 */
public enum HnnPredictionLogic implements GTRecipeType.ICustomRecipeLogic {

    INSTANCE;

    private static Item prediction;
    private static Method getStoredModel, isBound, get, fabDrops, getValues, getKey;

    public static void register() {
        if (!ModList.get().isLoaded("hostilenetworks")) return;
        try {
            Class<?> dmi = Class.forName("dev.shadowsoffire.hostilenetworks.item.DataModelItem");
            getStoredModel = dmi.getMethod("getStoredModel", ItemStack.class);
            Class<?> holder = getStoredModel.getReturnType();
            isBound = holder.getMethod("isBound");
            get = holder.getMethod("get");
            fabDrops = Class.forName("dev.shadowsoffire.hostilenetworks.data.DataModel").getMethod("fabDrops");
            Class<?> reg = Class.forName("dev.shadowsoffire.hostilenetworks.data.DataModelRegistry");
            Object inst = reg.getField("INSTANCE").get(null);
            getValues = inst.getClass().getMethod("getValues");
            for (Method m : inst.getClass().getMethods())
                if (m.getName().equals("getKey") && m.getParameterCount() == 1 && m.getParameterTypes()[0] != net.minecraft.world.item.ItemStack.class) getKey = m;
            GTRecipeTypes.EXTRACTOR_RECIPES.addCustomRecipeLogic(INSTANCE);
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[HNN] predictions in the Extractor are off: {}", t.toString());
        }
    }

    private static Item prediction() {
        if (prediction == null) prediction = BuiltInRegistries.ITEM.get(new ResourceLocation("hostilenetworks", "prediction"));
        return prediction;
    }

    @SuppressWarnings("unchecked")
    private static List<ItemStack> drops(ItemStack predictionStack) {
        try {
            Object h = getStoredModel.invoke(null, predictionStack);
            if (!(Boolean) isBound.invoke(h)) return List.of();
            return (List<ItemStack>) fabDrops.invoke(get.invoke(h));
        } catch (Throwable t) {
            return List.of();
        }
    }

    private static GTRecipe recipe(String id, ItemStack in, List<ItemStack> drops) {
        GTRecipeBuilder b = GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(GTAA.id("hnn_prediction/" + id))
                .inputItems(in.copyWithCount(1));
        for (ItemStack d : drops) if (!d.isEmpty()) b.outputItems(d.copy());
        return b.duration(80).EUt(VA[MV]).buildRawRecipe();
    }

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        if (!enabled()) return null;
        for (RecipeHandlerList rhl : holder.getCapabilitiesForIO(IO.IN)) {
            if (!rhl.hasCapability(ItemRecipeCapability.CAP)) continue;
            for (IRecipeHandler<?> h : rhl.getCapability(ItemRecipeCapability.CAP)) {
                for (Object o : h.getContents()) {
                    if (!(o instanceof ItemStack s) || s.isEmpty() || s.getItem() != prediction()) continue;
                    List<ItemStack> d = drops(s);
                    if (d.isEmpty()) continue;
                    return recipe("run", s, d);
                }
            }
        }
        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        if (!enabled()) return;
        try {
            Object inst = Class.forName("dev.shadowsoffire.hostilenetworks.data.DataModelRegistry").getField("INSTANCE").get(null);
            for (Object model : (Collection<?>) getValues.invoke(inst)) {
                ResourceLocation key = (ResourceLocation) getKey.invoke(inst, model);
                ItemStack p = new ItemStack(prediction());
                Class.forName("dev.shadowsoffire.hostilenetworks.item.DataModelItem")
                        .getMethod("setStoredModel", ItemStack.class, ResourceLocation.class).invoke(null, p, key);
                @SuppressWarnings("unchecked") List<ItemStack> d = new ArrayList<>((List<ItemStack>) fabDrops.invoke(model));
                if (d.isEmpty()) continue;
                GTRecipe r = recipe(key.getNamespace() + "/" + key.getPath(), p, d);
                GTRecipeTypes.EXTRACTOR_RECIPES.addToMainCategory(r);
            }
        } catch (Throwable t) {
            GTAA.LOGGER.debug("[HNN] no prediction showcase recipes: {}", t.toString());
        }
    }

    private static boolean enabled() {
        try {
            return GTAACommonConfig.HNN_EXTRACTOR.get();
        } catch (Throwable t) {
            return true;
        }
    }
}
