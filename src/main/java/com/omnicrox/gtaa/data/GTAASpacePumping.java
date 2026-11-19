package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

/** Gases for the Space Pumping Module (idea from GTNH-Intergalactic). One gas per programmed circuit. */
public final class GTAASpacePumping {

    private GTAASpacePumping() {}

    private static int circuit;

    private static void gas(Consumer<FinishedRecipe> provider, int moduleTier, int eut, FluidStack out) {
        if (out == null || out.isEmpty()) return;
        int c = ++circuit;
        String name = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(out.getFluid()).getPath();
        GTAARecipeTypes.SPACE_PUMPING.recipeBuilder(GTAA.id("gas/" + name).toString())
                .circuitMeta(c)
                .outputFluids(out)
                .duration(400).EUt(eut)
                .addData(GTAARecipeTypes.MODULE_TIER, moduleTier)
                .save(provider);
    }

    private static FluidStack f(Material m, int amount) {
        try {
            return m == null ? null : m.getFluid(amount);
        } catch (Throwable t) {
            return null;
        }
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        circuit = 0;
        // Mk-I
        gas(provider, 1, VA[UHV], f(Hydrogen, 256_000));
        gas(provider, 1, VA[UHV], f(Helium, 256_000));
        gas(provider, 1, VA[UHV], f(Deuterium, 128_000));
        gas(provider, 1, VA[UHV], f(Methane, 256_000));
        gas(provider, 1, VA[UHV], f(Nitrogen, 256_000));
        // Mk-II
        gas(provider, 2, VA[UEV], f(Helium3, 128_000));
        gas(provider, 2, VA[UEV], f(Tritium, 64_000));
        gas(provider, 2, VA[UEV], f(Neon, 64_000));
        gas(provider, 2, VA[UEV], f(Argon, 64_000));
        gas(provider, 2, VA[UEV], f(Krypton, 32_000));
        gas(provider, 2, VA[UEV], f(Xenon, 32_000));
        gas(provider, 2, VA[UEV], f(Radon, 16_000));
        // Mk-III
        gas(provider, 3, VA[UXV], f(Fluorine, 256_000));
        gas(provider, 3, VA[UXV], f(Oxygen, 512_000));
        try {
            gas(provider, 3, VA[UXV], Helium.getFluid(FluidStorageKeys.LIQUID, 256_000));
        } catch (Throwable ignored) {}
        gas(provider, 3, VA[UXV], f(GTAAMaterials.SPATIALLY_ENLARGED_FLUID, 1_000));
    }
}
