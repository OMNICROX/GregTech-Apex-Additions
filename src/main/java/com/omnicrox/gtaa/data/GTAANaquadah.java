package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import com.omnicrox.gtaa.common.machine.multiblock.naquadah.LargeNaquadahReactorMachine;
import com.omnicrox.gtaa.common.machine.multiblock.naquadah.NaquadahFuelRefineryMachine;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.omnicrox.gtaa.common.data.GTAAMaterials.*;

/**
 * Naquadah fuel line, following GoodGenerator (GTNH). Materials GTCEu doesn't have are swapped for close ones
 * (noted inline). Fuel values and burn times are GTNH's.
 */
public final class GTAANaquadah {

    private GTAANaquadah() {}

    private static final String ID = "gtapexadditions:naquadah/";

    private static Material ext(String id, Material fallback) {
        Material m = GTCEuAPI.materialManager.getMaterial(id);
        return m != null ? m : fallback;
    }

    private static boolean hasDust(Material m) {
        return m != null && !ChemicalHelper.get(dust, m).isEmpty();
    }

    private static void fuel(Consumer<FinishedRecipe> provider, String name, Material in, Material out, long eut, int ticks) {
        if (in == null || out == null) return;
        GTAARecipeTypes.NAQUADAH_REACTOR.recipeBuilder(ID + "fuel/" + name)
                .inputFluids(in.getFluid(1)).outputFluids(out.getFluid(1))
                .duration(ticks)
                .addData(LargeNaquadahReactorMachine.BASIC_OUTPUT, eut)
                .save(provider);
    }

    private static GTRecipeBuilder nfr(String name, int coil) {
        return GTAARecipeTypes.NAQUADAH_FUEL_REFINERY.recipeBuilder(ID + "refinery/" + name)
                .addData(NaquadahFuelRefineryMachine.COIL_TIER, coil);
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        // ---------------- Large Naquadah Reactor fuels (GTNH values) ----------------
        fuel(provider, "uranium", URANIUM_FUEL_EXCITED, URANIUM_FUEL_DEPLETED, 12_960, 100);
        fuel(provider, "thorium", THORIUM_FUEL_EXCITED, THORIUM_FUEL_DEPLETED, 2_200, 500);
        fuel(provider, "plutonium", PLUTONIUM_FUEL_EXCITED, PLUTONIUM_FUEL_DEPLETED, 32_400, 150);
        long[] nqEut = { 0, 975_000L, 2_300_000L, 9_511_000L, 88_540_000L, 399_576_000L, 2_077_795_200L };
        int[] nqTicks = { 0, 60, 70, 80, 100, 160, 240 };
        for (int i = 1; i <= 6; i++)
            fuel(provider, "naquadah_mk" + i, NAQUADAH_FUEL[i], NAQUADAH_FUEL_DEPLETED[i], nqEut[i], nqTicks[i]);

        // ---------------- liquid nuclear fuels ----------------
        MIXER_RECIPES.recipeBuilder(ID + "uranium_fuel")
                .inputItems(dust, Uranium235, 2).inputItems(dust, Naquadah, 1)
                .inputFluids(Radon.getFluid(1000))
                .outputFluids(URANIUM_FUEL.getFluid(1000))
                .duration(200).EUt(VA[LuV] / 2).save(provider);
        FUSION_RECIPES.recipeBuilder(ID + "uranium_fuel_excited")
                .inputFluids(URANIUM_FUEL.getFluid(10), Hydrogen.getFluid(100))
                .outputFluids(URANIUM_FUEL_EXCITED.getFluid(10))
                .duration(40).EUt(VA[IV]).fusionStartEU(200_000_000L).save(provider);
        MIXER_RECIPES.recipeBuilder(ID + "thorium_fuel")
                .inputItems(dust, Thorium, 16).inputItems(dust, Lithium, 4)
                .inputFluids(Mercury.getFluid(1000))
                .outputFluids(THORIUM_FUEL.getFluid(4000))
                .duration(3000).EUt(VA[HV] / 2).save(provider);
        MIXER_RECIPES.recipeBuilder(ID + "thorium_fuel_excited")
                .inputFluids(THORIUM_FUEL.getFluid(1000), Helium.getFluid(FluidStorageKeys.PLASMA, 250))
                .outputFluids(THORIUM_FUEL_EXCITED.getFluid(1000))
                .duration(120).EUt(VA[IV] / 2).save(provider);
        MIXER_RECIPES.recipeBuilder(ID + "plutonium_fuel")
                .inputItems(dust, Plutonium241, 4).inputItems(dust, Naquadah, 2)
                .inputFluids(Radon.getFluid(1000))
                .outputFluids(PLUTONIUM_FUEL.getFluid(1000))
                .duration(360).EUt(VA[LuV]).save(provider);
        FUSION_RECIPES.recipeBuilder(ID + "plutonium_fuel_excited")
                .inputFluids(Lutetium.getFluid(16), PLUTONIUM_FUEL.getFluid(20))
                .outputFluids(PLUTONIUM_FUEL_EXCITED.getFluid(20))
                .duration(20).EUt(VA[LuV] / 2).fusionStartEU(220_000_000L).save(provider);

        // ---------------- naquadah solution -> light / heavy fuel ----------------
        LARGE_CHEMICAL_RECIPES.recipeBuilder(ID + "naquadah_solution")
                .inputItems(dust, Naquadah, 1)
                .inputFluids(Ammonia.getFluid(1000))
                .outputFluids(NAQUADAH_SOLUTION.getFluid(1000))
                .duration(200).EUt(VA[IV]).save(provider);
        DISTILLATION_RECIPES.recipeBuilder(ID + "naquadah_solution_distillation")
                .inputFluids(NAQUADAH_SOLUTION.getFluid(1000))
                .outputFluids(NAQUADAH_ASPHALT.getFluid(100), HEAVY_NAQUADAH_FUEL.getFluid(250),
                        LIGHT_NAQUADAH_FUEL.getFluid(500), Water.getFluid(500), NAQUADAH_GAS.getFluid(3000))
                .duration(600).EUt(VA[EV]).save(provider);
        DISTILLATION_RECIPES.recipeBuilder(ID + "naquadah_asphalt_cracking")
                .inputFluids(NAQUADAH_ASPHALT.getFluid(1000))
                .outputFluids(LIGHT_NAQUADAH_FUEL.getFluid(600), HEAVY_NAQUADAH_FUEL.getFluid(350), Radon.getFluid(500))
                .duration(600).EUt(VA[IV]).save(provider);
        GAS_TURBINE_FUELS.recipeBuilder(ID + "naquadah_gas")
                .inputFluids(NAQUADAH_GAS.getFluid(1))
                .duration(5).EUt(-V[EV]).save(provider);

        // ---------------- MkI / MkII ----------------
        FUSION_RECIPES.recipeBuilder(ID + "mk1")
                .inputFluids(LIGHT_NAQUADAH_FUEL.getFluid(780), HEAVY_NAQUADAH_FUEL.getFluid(360))
                .outputFluids(NAQUADAH_FUEL[1].getFluid(100))
                .duration(500).EUt(VA[LuV]).fusionStartEU(320_000_000L).save(provider);
        LARGE_CHEMICAL_RECIPES.recipeBuilder(ID + "mk2")
                .inputItems(dust, NetherStar, 4)
                .inputItems(dust, Electrum, 32) // GTNH: Fluxed Electrum
                .inputFluids(NAQUADAH_FUEL[1].getFluid(100), NAQUADAH_GAS.getFluid(1500))
                .outputFluids(NAQUADAH_FUEL[2].getFluid(100))
                .duration(500).EUt(VA[UHV] / 2).save(provider);

        // ---------------- refinery inputs ----------------
        BLAST_RECIPES.recipeBuilder(ID + "extremely_unstable_naquadah")
                .inputItems(dust, Naquadria, 32)
                .inputFluids(FluoroantimonicAcid.getFluid(4000))
                .outputItems(dust, EXTREMELY_UNSTABLE_NAQUADAH, 17)
                .blastFurnaceTemp(8_000)
                .duration(3600).EUt(VA[IV] / 2).save(provider);
        BLAST_RECIPES.recipeBuilder(ID + "atomic_separation_catalyst")
                .inputItems(dust, Naquadria, 8).inputItems(dust, Trinium, 4).inputItems(dust, Plutonium241, 4)
                .outputItems(dust, ATOMIC_SEPARATION_CATALYST, 16)
                .blastFurnaceTemp(10_000)
                .duration(600).EUt(VA[UV]).save(provider);

        // ---------------- Naquadah Fuel Refinery (coil tier T1..T4) ----------------
        // GTNH Tiberium -> Naquadria, High Density Uranium/Plutonium -> U-235 / Pu-241 dust
        nfr("mk3", 1)
                .inputItems(dust, EXTREMELY_UNSTABLE_NAQUADAH, 4).inputItems(dust, Naquadria, 27)
                .inputItems(dust, Uranium235, 8).inputItems(dust, Plutonium241, 4)
                .inputFluids(HEAVY_NAQUADAH_FUEL.getFluid(800), LIGHT_NAQUADAH_FUEL.getFluid(1000))
                .outputFluids(NAQUADAH_FUEL[3].getFluid(100))
                .duration(100).EUt(1_100_000).save(provider);
        nfr("mk3_alt", 1) // GTNH Chronomatic Glass -> Trinium
                .inputItems(dust, EXTREMELY_UNSTABLE_NAQUADAH, 8).inputItems(dust, Trinium, 9)
                .inputItems(dust, Uranium235, 8).inputItems(dust, Plutonium241, 4)
                .inputFluids(HEAVY_NAQUADAH_FUEL.getFluid(800), LIGHT_NAQUADAH_FUEL.getFluid(1000))
                .outputFluids(NAQUADAH_FUEL[3].getFluid(200))
                .duration(100).EUt(2_100_000).save(provider);
        nfr("mk4", 2) // GTNH Awakened Draconium -> Neutronium, Orundum -> Tritanium
                .inputItems(dust, EXTREMELY_UNSTABLE_NAQUADAH, 27).inputItems(dust, NetherStar, 64)
                .inputItems(dust, Neutronium, 16).inputItems(dust, Tritanium, 32)
                .inputFluids(NAQUADAH_FUEL[3].getFluid(2000), Praseodymium.getFluid(9216))
                .outputFluids(NAQUADAH_FUEL[4].getFluid(250))
                .duration(160).EUt(46_000_000).save(provider);
        nfr("mk4_one_step", 4) // GTNH Hypogen -> Metastable Oganesson, Chronomatic Glass -> SpaceTime
                .inputItems(dust, EXTREMELY_UNSTABLE_NAQUADAH, 54).inputItems(dust, Tritanium, 32)
                .inputItems(dust, Uranium235, 40).inputItems(dust, Plutonium241, 20)
                .inputFluids(HEAVY_NAQUADAH_FUEL.getFluid(4000), LIGHT_NAQUADAH_FUEL.getFluid(5000),
                        METASTABLE_OGANESSON.getFluid(120), SPACETIME.getFluid(720))
                .outputFluids(NAQUADAH_FUEL[4].getFluid(250))
                .duration(2).EUt(350_000_000).save(provider);
        Material infinity = ext("gtna:infinity", null);
        var mk5 = nfr("mk5", 2);
        if (hasDust(infinity)) mk5.inputItems(dust, infinity, 8);
        else mk5.inputItems(dust, Neutronium, 32);
        mk5.inputItems(dust, ATOMIC_SEPARATION_CATALYST, 32)
                .inputFluids(NAQUADAH_FUEL[4].getFluid(2000), Naquadria.getFluid(250)) // GTNH Heavy Radox -> Naquadria
                .outputFluids(NAQUADAH_FUEL[5].getFluid(500))
                .duration(200).EUt(100_000_000).save(provider);
        Material transcendent = ext("gtna:transcendent_metal", null);
        var mk5alt = nfr("mk5_alt", 3);
        if (hasDust(transcendent)) mk5alt.inputItems(dust, transcendent, 16);
        else mk5alt.inputItems(dust, Tritanium, 48);
        mk5alt.inputItems(dust, ATOMIC_SEPARATION_CATALYST, 48)
                .inputFluids(NAQUADAH_FUEL[4].getFluid(2000), Naquadria.getFluid(250), METASTABLE_OGANESSON.getFluid(288))
                .outputFluids(NAQUADAH_FUEL[5].getFluid(750))
                .duration(50).EUt(300_000_000).save(provider);
        nfr("mk6", 3) // GTNH Astral Titanium -> Neutronium, Shirabon -> SpaceTime
                .inputItems(dust, Neutronium, 64).inputItems(dust, Tritanium, 32)
                .inputFluids(NAQUADAH_FUEL[5].getFluid(2000), SPACETIME.getFluid(360))
                .outputFluids(NAQUADAH_FUEL[6].getFluid(750))
                .duration(240).EUt(320_000_000).save(provider);
        nfr("mk6_alt", 4) // GTNH Celestial Tungsten -> Metastable Oganesson
                .inputItems(dust, METASTABLE_OGANESSON, 64).inputItems(dust, Tritanium, 48)
                .inputFluids(NAQUADAH_FUEL[5].getFluid(2000), SPACETIME.getFluid(360))
                .outputFluids(NAQUADAH_FUEL[6].getFluid(1250))
                .duration(60).EUt(VA[UXV]).save(provider);

        // ---------------- reprocessing depleted fuel (MkV / MkVI go to the SASS instead) ----------------
        CENTRIFUGE_RECIPES.recipeBuilder(ID + "depleted_uranium")
                .inputFluids(URANIUM_FUEL_DEPLETED.getFluid(1000))
                .outputItems(dust, Uranium238, 1).outputItems(dust, Lead, 1).outputFluids(Radon.getFluid(100))
                .duration(200).EUt(VA[EV]).save(provider);
        CENTRIFUGE_RECIPES.recipeBuilder(ID + "depleted_thorium")
                .inputFluids(THORIUM_FUEL_DEPLETED.getFluid(1000))
                .outputItems(dust, Thorium, 2).outputItems(dust, Lead, 1).outputFluids(Mercury.getFluid(250))
                .duration(200).EUt(VA[EV]).save(provider);
        CENTRIFUGE_RECIPES.recipeBuilder(ID + "depleted_plutonium")
                .inputFluids(PLUTONIUM_FUEL_DEPLETED.getFluid(1000))
                .outputItems(dust, Plutonium241, 1).outputItems(dust, Lead, 1)
                .duration(200).EUt(VA[IV]).save(provider);
        for (int i = 1; i <= 4; i++) {
            CENTRIFUGE_RECIPES.recipeBuilder(ID + "depleted_mk" + i)
                    .inputFluids(NAQUADAH_FUEL_DEPLETED[i].getFluid(1000))
                    .outputItems(dust, Naquadah, 2 * i).outputItems(dust, Naquadria, i).outputItems(dust, Lead, 2)
                    .duration(400).EUt(VA[LuV + i - 1]).save(provider);
        }

        // ---------------- blocks ----------------
        ASSEMBLER_RECIPES.recipeBuilder(ID + "naquadah_reactor_casing")
                .inputItems(plate, Naquadah, 6).inputItems(frameGt, TungstenSteel)
                .outputItems(new ItemStack(GTAABlocks.NAQUADAH_REACTOR_CASING.get(), 2))
                .duration(100).EUt(VA[IV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(ID + "field_restriction_casing")
                .inputItems(plate, NaquadahAlloy, 4).inputItems(GTItems.FIELD_GENERATOR_IV)
                .inputItems(frameGt, TungstenSteel)
                .outputItems(new ItemStack(GTAABlocks.FIELD_RESTRICTION_CASING.get(), 2))
                .duration(200).EUt(VA[IV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(ID + "radiation_proof_steel_frame")
                .inputItems(frameGt, TungstenSteel).inputItems(plate, Lead, 4)
                .outputItems(new ItemStack(GTAABlocks.RADIATION_PROOF_STEEL_FRAME.get(), 1))
                .duration(100).EUt(VA[EV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(ID + "naquadah_fuel_refinery_casing")
                .inputItems(plate, Plutonium241, 6).inputItems(frameGt, NaquadahAlloy)
                .outputItems(new ItemStack(GTAABlocks.NAQUADAH_FUEL_REFINERY_CASING.get(), 2))
                .duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(ID + "europium_radiation_proof_casing")
                .inputItems(plate, Europium, 6).inputItems(frameGt, NaquadahAlloy)
                .outputItems(new ItemStack(GTAABlocks.EUROPIUM_RADIATION_PROOF_CASING.get(), 2))
                .duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(ID + "field_restriction_glass")
                .inputItems(GTBlocks.FUSION_GLASS.asItem()).inputItems(plate, NaquadahAlloy, 4)
                .outputItems(new ItemStack(GTAABlocks.FIELD_RESTRICTION_GLASS.get(), 1))
                .duration(100).EUt(VA[UV]).save(provider);
        var coilWire = new Material[] { UraniumRhodiumDinaquadide, EnrichedNaquadahTriniumEuropiumDuranide,
                RutheniumTriniumAmericiumNeutronate, RutheniumTriniumAmericiumNeutronate };
        var coilPlate = new Material[] { NaquadahAlloy, Neutronium, Tritanium, METASTABLE_OGANESSON };
        var coilGen = java.util.List.of(GTItems.FIELD_GENERATOR_UV, GTItems.FIELD_GENERATOR_UHV,
                GTItems.FIELD_GENERATOR_UEV, GTItems.FIELD_GENERATOR_UIV);
        var coilCirc = java.util.List.of(CustomTags.UV_CIRCUITS, CustomTags.UHV_CIRCUITS, CustomTags.UEV_CIRCUITS,
                CustomTags.UIV_CIRCUITS);
        var coils = GTAABlocks.fieldRestrictionCoils();
        for (int t = 0; t < 4; t++) {
            var b = ASSEMBLER_RECIPES.recipeBuilder(ID + "field_restriction_coil_t" + (t + 1));
            if (t > 0) b.inputItems(coils[t - 1].asItem());
            b.inputItems(plateDense, coilPlate[t], 2).inputItems(wireGtSingle, coilWire[t], 8)
                    .inputItems(coilGen.get(t)).inputItems(coilCirc.get(t), 2)
                    .outputItems(new ItemStack(coils[t], 1))
                    .duration(400).EUt(VA[UV + t]).save(provider);
        }

        // ---------------- controllers ----------------
        ASSEMBLY_LINE_RECIPES.recipeBuilder(ID + "large_naquadah_reactor")
                .inputItems(new ItemStack(GTAABlocks.NAQUADAH_REACTOR_CASING.get(), 8))
                .inputItems(new ItemStack(GTAABlocks.FIELD_RESTRICTION_CASING.get(), 8))
                .inputItems(GTItems.FIELD_GENERATOR_IV, 4)
                .inputItems(CustomTags.IV_CIRCUITS, 8)
                .inputItems(plateDense, Naquadah, 8)
                .inputItems(wireGtQuadruple, Naquadah, 8)
                .inputFluids(SolderingAlloy.getFluid(L * 16))
                .outputItems(GTAAMachines.LARGE_NAQUADAH_REACTOR.asStack())
                .duration(1200).EUt(VA[IV]).save(provider);
        ASSEMBLY_LINE_RECIPES.recipeBuilder(ID + "naquadah_fuel_refinery")
                .inputItems(new ItemStack(GTAABlocks.NAQUADAH_FUEL_REFINERY_CASING.get(), 16))
                .inputItems(new ItemStack(coils[0], 8))
                .inputItems(GTItems.FIELD_GENERATOR_UV, 4)
                .inputItems(CustomTags.UV_CIRCUITS, 16)
                .inputItems(wireGtSingle, UraniumRhodiumDinaquadide, 32)
                .inputItems(plateDense, NaquadahAlloy, 16)
                .inputFluids(SolderingAlloy.getFluid(L * 32), Polybenzimidazole.getFluid(L * 16))
                .outputItems(GTAAMachines.NAQUADAH_FUEL_REFINERY.asStack())
                .duration(2400).EUt(VA[UV]).save(provider);
    }
}
