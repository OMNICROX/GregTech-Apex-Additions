package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/**
 * Fusion recipes for MK IV (UHV, 5.12B start) and MK V (UEV, 20.48B start), following GTNH
 * (GT5-Unofficial FusionReactorRecipes, GT++ and GoodGenerator). Recipes over 640M need MK IV,
 * recipes at 6B need MK V. A few plasma feeders GTCEu lacks are added at MK II / MK III.
 */
public final class GTAAFusion {

    private GTAAFusion() {}

    private static final FluidStorageKey L = FluidStorageKeys.LIQUID, P = FluidStorageKeys.PLASMA;

    private static FluidStack f(Material m, FluidStorageKey key, int amount) {
        try {
            FluidStack s = m.getFluid(key, amount);
            return s == null || s.isEmpty() ? null : s;
        } catch (Throwable t) {
            return null;
        }
    }

    private static FluidStack f(Material m, int amount) {
        try {
            FluidStack s = m.getFluid(amount);
            return s == null || s.isEmpty() ? null : s;
        } catch (Throwable t) {
            return null;
        }
    }

    private static void fusion(Consumer<FinishedRecipe> provider, String id, FluidStack a, FluidStack b, FluidStack out,
                               int duration, long eut, long start) {
        if (a == null || b == null || out == null) return;
        FUSION_RECIPES.recipeBuilder("gtapexadditions:fusion/" + id)
                .inputFluids(a, b).outputFluids(out)
                .duration(duration).EUt(eut).fusionStartEU(start).save(provider);
    }

    private static void plasmaFuel(Consumer<FinishedRecipe> provider, Material m, int duration, long eut) {
        FluidStack in = f(m, P, 1);
        if (in == null) return;
        var b = PLASMA_GENERATOR_FUELS.recipeBuilder("gtapexadditions:" + m.getName()).inputFluids(in);
        FluidStack back = f(m, L, 1);
        if (back == null) back = f(m, FluidStorageKeys.GAS, 1);
        if (back != null) b.outputFluids(back);
        b.duration(duration).EUt(-eut).save(provider);
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        // ---- feeders (MK II / MK III): plasmas GTCEu has no recipe for ----
        // GT: New Age ships the same radon and thorium plasma recipes
        boolean gtna = net.minecraftforge.fml.ModList.get().isLoaded("gtna");
        if (!gtna) fusion(provider, "radon_plasma", f(Iridium, L, 144), f(Fluorine, 500), f(Radon, P, 144), 32, VA[LuV], 450_000_000L);
        // GT Community Additions already has this one
        if (!net.minecraftforge.fml.ModList.get().isLoaded("gtca"))
            fusion(provider, "calcium_plasma", f(Magnesium, L, 128), f(Oxygen, 128), f(Calcium, P, 16), 128, VA[IV], 120_000_000L);
        fusion(provider, "boron_plasma", f(Beryllium, L, 16), f(Hydrogen, 125), f(Boron, P, 16), 32, VA[LuV], 300_000_000L);
        fusion(provider, "copper_plasma", f(Cobalt, L, 144), f(Helium, 250), f(Copper, P, 144), 32, VA[ZPM], 480_000_000L);
        fusion(provider, "xenon_plasma", f(Antimony, L, 144), f(Lithium, L, 144), f(Xenon, P, 144), 32, VA[ZPM], 500_000_000L);

        // ---- MK IV (UHV, > 640M start) ----
        fusion(provider, "neptunium_plasma", f(Radon, P, 100), f(Nitrogen, P, 100), f(Neptunium, P, 100), 600, VA[UHV], 1_000_000_000L);
        fusion(provider, "fermium_plasma", f(Americium, P, 100), f(Boron, P, 100), f(Fermium, P, 100), 600, VA[UHV], 1_000_000_000L);
        fusion(provider, "californium", f(Curium, L, 144), f(Helium, P, 144), f(Californium, L, 144), 32, 196_608, 750_000_000L);
        fusion(provider, "flerovium", f(Plutonium241, L, 144), f(Calcium, P, 144), f(Flerovium, L, 144), 160, 196_608, 1_000_000_000L);
        fusion(provider, "bromine_plasma", f(Manganese, L, 144), f(Neon, 500), f(Bromine, P, 144), 32, 196_608, 1_000_000_000L);
        fusion(provider, "metastable_oganesson", f(NaquadahEnriched, L, 144), f(Oganesson, L, 250),
                f(GTAAMaterials.METASTABLE_OGANESSON, L, 36), 600, VA[UV], 1_000_000_000L);

        // ---- MK V (UEV, 6B start) ----
        fusion(provider, "plutonium241_plasma", f(Lutetium, L, 576), f(Vanadium, L, 576), f(Plutonium241, P, 576), 4, VA[UEV] / 2, 6_000_000_000L);
        fusion(provider, "lead_plasma", f(Tellurium, L, 576), f(Zinc, L, 576), f(Lead, P, 576), 4, VA[UEV] / 2, 6_000_000_000L);
        if (!gtna) fusion(provider, "thorium_plasma", f(Osmium, L, 576), f(Silicon, L, 576), f(Thorium, P, 576), 4, VA[UEV] / 2, 6_000_000_000L);
        fusion(provider, "neptunium_plasma_mk5", f(Xenon, P, 576), f(Yttrium, L, 576), f(Neptunium, P, 576), 32, VA[UEV], 6_000_000_000L);
        fusion(provider, "fermium_plasma_mk5", f(Americium, P, 576), f(Rubidium, L, 576), f(Fermium, P, 576), 32, VA[UEV], 6_000_000_000L);
        fusion(provider, "metastable_oganesson_mk5", f(Copper, P, 576), f(Oganesson, L, 1000),
                f(GTAAMaterials.METASTABLE_OGANESSON, L, 576), 100, VA[UEV], 6_000_000_000L);

        // ---- plasma generator fuels (EU per mB = duration * eut) ----
        plasmaFuel(provider, Bromine, 64, V[IV]);        //   524k
        plasmaFuel(provider, Neptunium, 96, V[IV]);      //   786k
        plasmaFuel(provider, Fermium, 112, V[IV]);       //   918k
        plasmaFuel(provider, Plutonium241, 128, V[IV]);  // 1.05M
        plasmaFuel(provider, Lead, 160, V[IV]);          // 1.31M
        plasmaFuel(provider, Thorium, 192, V[IV]);       // 1.57M
        plasmaFuel(provider, Radon, 40, V[IV]);
        plasmaFuel(provider, Calcium, 24, V[IV]);
        plasmaFuel(provider, Boron, 24, V[IV]);
        plasmaFuel(provider, Copper, 32, V[IV]);
        plasmaFuel(provider, Xenon, 40, V[IV]);
    }
}
