package com.omnicrox.apexcore.recipe;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.omnicrox.apexcore.ApexCore;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CUTTER_RECIPES;

/**
 * Was kubejs/server_scripts/apex_purified_water.js. Cutting naquadah boules needs Grade 3 purified water and
 * neutronium boules Grade 5 (GTCA's purified water when installed, otherwise GTAA's). The plain, water and distilled
 * water cuts are removed.
 * <p>
 * The script's bug this replaces: with GTCA installed it removed GTCEu's cuts and expected GTCA to add its own,
 * which GTCA does not, so naquadah and neutronium wafers (and every UEV+ circuit after them) could not be made.
 */
public final class WaferRecipes {

    private WaferRecipes() {}

    private static final String[] BOULES = { "naquadah", "neutronium" };

    public static void remove(Consumer<ResourceLocation> consumer) {
        for (String b : BOULES)
            for (String suffix : new String[] { "", "_water", "_distilled_water" })
                consumer.accept(new ResourceLocation("gtceu", "cutter/cut_" + b + "_boule" + suffix));
    }

    /** GTCA's grade when it is installed, otherwise GTAA's; null if neither mod provides purified water */
    static Material purifiedWater(int grade) {
        Material m = GTCEuAPI.materialManager.getMaterial("gtca:purified_water_grade_" + grade);
        if (m == null || m.isNull()) m = GTCEuAPI.materialManager.getMaterial("gtapexadditions:grade_" + grade + "_purified_water");
        return m == null || m.isNull() ? null : m;
    }

    public static void add(Consumer<FinishedRecipe> provider) {
        Material g3 = purifiedWater(3), g5 = purifiedWater(5);
        if (g3 == null || g5 == null) {
            ApexCore.LOGGER.warn("[ApexCore] no purified water found; naquadah and neutronium boules keep GTCEu's cuts");
            return;
        }
        // same items, cleanroom, duration and voltage as GTCEu's CircuitRecipes, purified water instead of lubricant
        CUTTER_RECIPES.recipeBuilder(ApexCore.MOD_ID + ":cut_naquadah_boule_purified_water")
                .inputItems(GTItems.NAQUADAH_BOULE)
                .inputFluids(g3.getFluid(1000))
                .outputItems(GTItems.NAQUADAH_WAFER, 64)
                .cleanroom(CleanroomType.CLEANROOM)
                .duration(1600).EUt(VA[EV]).save(provider);
        CUTTER_RECIPES.recipeBuilder(ApexCore.MOD_ID + ":cut_neutronium_boule_purified_water")
                .inputItems(GTItems.NEUTRONIUM_BOULE)
                .inputFluids(g5.getFluid(1000))
                .outputItems(GTItems.NEUTRONIUM_WAFER, 64)
                .outputItems(GTItems.NEUTRONIUM_WAFER, 32)
                .cleanroom(CleanroomType.CLEANROOM)
                .duration(2400).EUt(VA[IV]).save(provider);
    }
}
