package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.Locale;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

/**
 * Asteroids for the Space Mining Module (idea and mechanics from GTNH-Intergalactic).
 * Mk-I: Overworld ores (better yields than the Electric Void Miner). Mk-II: adds Nether/End ores.
 * Mk-III: exotic asteroids only this module can reach.
 */
public final class GTAASpaceMining {

    private GTAASpaceMining() {}

    private static int circuitCounter = 0;

    private record Plasma(Material material, int amount) {}

    private static final Plasma[] PLASMAS = {
            new Plasma(Helium, 825), new Plasma(Nitrogen, 550), new Plasma(Oxygen, 375),
            new Plasma(Iron, 250), new Plasma(Nickel, 150) };

    /** name, module tier, min drone tier, EU/t, CWU/t, base seconds, base ore count, materials + chances (x100 %) */
    private static void asteroid(Consumer<FinishedRecipe> provider, String name, int moduleTier, int droneTier,
                                 int eut, int cwut, int seconds, int count, Object... matsAndChances) {
        int circuit = ++circuitCounter;
        for (int p = 0; p < PLASMAS.length; p++) {
            Plasma plasma = PLASMAS[p];
            FluidStack fluid;
            try {
                fluid = plasma.material().getFluid(FluidStorageKeys.PLASMA, plasma.amount());
            } catch (Throwable t) {
                continue;
            }
            if (fluid == null || fluid.isEmpty()) continue;
            int amount = (int) Math.round(count * (1 + 0.25 * p));
            int duration = (int) Math.round(seconds * 20 * (1 - 0.15 * p));
            GTRecipeBuilder b = GTAARecipeTypes.SPACE_MINING.recipeBuilder(GTAA.id("asteroid/" + name + "_" +
                    plasma.material().getName()).toString())
                    .notConsumable(Ingredient.of(ItemTags.create(GTAA.id("mining_drones/at_least_" +
                            VN[droneTier].toLowerCase(Locale.ROOT)))))
                    .circuitMeta(circuit)
                    .inputFluids(fluid)
                    .duration(Math.max(20, duration)).EUt(eut).CWUt(cwut)
                    .addData(GTAARecipeTypes.MODULE_TIER, moduleTier);
            for (int i = 0; i + 1 < matsAndChances.length; i += 2) {
                Material m = (Material) matsAndChances[i];
                int chance = (int) matsAndChances[i + 1];
                ItemStack out = m.hasProperty(PropertyKey.ORE) ? ChemicalHelper.get(TagPrefix.rawOre, m, amount) :
                        ChemicalHelper.get(TagPrefix.dust, m, Math.max(1, amount / 4));
                if (out.isEmpty()) continue;
                b.chancedOutput(out, chance * 100, 0);
            }
            b.save(provider);
        }
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        circuitCounter = 0;
        // --- Mk-I: Overworld ---
        asteroid(provider, "coal", 1, LV, VA[UV], 16, 10, 32, Coal, 70, Graphite, 20, Diamond, 10);
        asteroid(provider, "iron", 1, LV, VA[UV], 16, 10, 32, Iron, 40, Magnetite, 20, Pyrite, 15, Gold, 10, VanadiumMagnetite, 15);
        asteroid(provider, "copper", 1, MV, VA[UV], 16, 10, 32, Copper, 30, Chalcopyrite, 30, Tetrahedrite, 20, Malachite, 20);
        asteroid(provider, "tin", 1, MV, VA[UV], 16, 10, 32, Tin, 30, Cassiterite, 70);
        asteroid(provider, "lead_silver", 1, HV, VA[UV], 16, 10, 32, Galena, 40, Lead, 20, Silver, 20, Stibnite, 20);
        asteroid(provider, "nickel", 1, HV, VA[UV], 16, 10, 32, Garnierite, 30, Nickel, 20, Pentlandite, 30, Cobaltite, 20);
        asteroid(provider, "redstone", 1, HV, VA[UV], 16, 10, 32, Redstone, 50, Ruby, 25, Cinnabar, 25);
        asteroid(provider, "lapis", 1, HV, VA[UV], 16, 10, 32, Lapis, 40, Lazurite, 25, Sodalite, 25, Calcite, 10);
        asteroid(provider, "aluminium", 1, EV, VA[UV], 32, 12, 32, Bauxite, 50, Ilmenite, 30, Aluminium, 20);
        asteroid(provider, "salt", 1, LV, VA[UV], 8, 8, 32, Salt, 40, RockSalt, 40, Lepidolite, 20);
        asteroid(provider, "beryllium", 1, EV, VA[UV], 32, 12, 24, Beryllium, 40, Emerald, 40, Thorium, 20);
        asteroid(provider, "molybdenum", 1, EV, VA[UV], 32, 12, 24, Molybdenite, 30, Wulfenite, 30, Powellite, 40);
        asteroid(provider, "uranium", 1, IV, VA[UV], 32, 14, 24, Uraninite, 50, Pitchblende, 50);
        asteroid(provider, "rare_earth", 1, IV, VA[UV], 32, 14, 24, Bastnasite, 40, Monazite, 40, Neodymium, 20);

        // --- Mk-II: Nether / End ---
        asteroid(provider, "nether_quartz", 2, IV, VA[UHV], 64, 12, 32, NetherQuartz, 60, Quartzite, 20, Barite, 20);
        asteroid(provider, "sulfur", 2, IV, VA[UHV], 64, 12, 32, Sulfur, 50, Sphalerite, 30, Pyrite, 20);
        asteroid(provider, "tungsten", 2, LuV, VA[UHV], 64, 14, 24, Tungstate, 50, Scheelite, 50);
        asteroid(provider, "platinum", 2, LuV, VA[UHV], 96, 16, 16, Cooperite, 60, Palladium, 40);
        asteroid(provider, "chrome", 2, LuV, VA[UHV], 64, 14, 24, Chromite, 50, Pyrolusite, 30, Tantalite, 20);
        asteroid(provider, "naquadah", 2, ZPM, VA[UHV], 128, 18, 16, Naquadah, 70, Plutonium239, 30);

        // --- Mk-III: exotic (only here) ---
        asteroid(provider, "exotic", 3, UHV, VA[UEV], 256, 20, 16, Naquadria, 30, Trinium, 30, Tritanium, 20, Osmiridium, 15, Neutronium, 5);
        asteroid(provider, "stellar_core", 3, UEV, VA[UEV], 512, 30, 8, Neutronium, 20, Tritanium, 30, Naquadria, 30, Iridium, 20);
    }
}
