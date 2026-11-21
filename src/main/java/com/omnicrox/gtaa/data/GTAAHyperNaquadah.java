package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import com.omnicrox.gtaa.common.machine.multiblock.naquadah.FuelRefiningComplexMachine;
import com.omnicrox.gtaa.common.machine.multiblock.naquadah.HyperNaquadahReactorMachine;
import com.omnicrox.gtaa.common.machine.multiblock.naquadah.NaquadahFuelRefineryMachine;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.omnicrox.gtaa.common.data.GTAAMaterials.*;

/**
 * The longer naquadah line, after GT Not Leisure (GTNH addon): Hyper / Advanced Hyper Naquadah Reactor reactions and
 * the Fuel Refining Complex. GTNH-only materials are taken from GT Nexus Additions when it has them; a recipe whose
 * ingredients can't all be found is skipped (and logged).
 */
public final class GTAAHyperNaquadah {

    private GTAAHyperNaquadah() {}

    private static final String ID = "gtapexadditions:hyper_naquadah/";
    private static final List<String> SKIPPED = new ArrayList<>();

    private static Material gtna(String id) {
        Material m = GTCEuAPI.materialManager.getMaterial("gtna:" + id);
        return m == null || m.isNull() ? null : m;
    }

    private static FluidStack fluid(Material m, int amount) {
        if (m == null) return null;
        try {
            FluidStack s = m.getFluid(amount);
            return s == null || s.isEmpty() ? null : s;
        } catch (Throwable t) {
            return null;
        }
    }

    private static FluidStack plasma(Material m, int amount) {
        if (m == null) return null;
        try {
            FluidStack s = m.getFluid(FluidStorageKeys.PLASMA, amount);
            return s == null || s.isEmpty() ? null : s;
        } catch (Throwable t) {
            return null;
        }
    }

    private static FluidStack first(FluidStack... options) {
        for (FluidStack s : options) if (s != null) return s;
        return null;
    }

    private static boolean ok(String name, Object... parts) {
        for (Object o : parts) if (o == null) {
            SKIPPED.add(name);
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------------ reactor reactions

    private static void reaction(Consumer<FinishedRecipe> provider, String name, int tier, int mk, int fuelAmount,
                                 FluidStack catalyst, long power, int ticks) {
        FluidStack fuel = fluid(NAQUADAH_FUEL[mk], fuelAmount);
        FluidStack spent = fluid(NAQUADAH_FUEL_DEPLETED[mk], fuelAmount);
        if (!ok("reaction " + name, fuel, spent, catalyst)) return;
        GTAARecipeTypes.HYPER_NAQUADAH_REACTOR.recipeBuilder(ID + "reaction/" + name)
                .inputFluids(fuel, catalyst).outputFluids(spent)
                .duration(ticks)
                .addData(HyperNaquadahReactorMachine.POWER, power)
                .addData(HyperNaquadahReactorMachine.TIER, tier)
                .save(provider);
    }

    private static void reactions(Consumer<FinishedRecipe> provider) {
        // GT Not Leisure's table. GTNH tiers by index: UMV -> GTCEu UXV (12), UXV -> OpV (13)
        long max = VA[MAX];
        reaction(provider, "mk1_hydrogen", 1, 1, 16, fluid(Hydrogen, 1600), VA[UV], 1000);
        reaction(provider, "mk1_oxygen_plasma", 1, 1, 100, plasma(Oxygen, 40), VA[UEV], 10000);
        reaction(provider, "mk2_hydrogen", 1, 2, 16, fluid(Hydrogen, 1600), VA[UEV], 1000);
        reaction(provider, "mk2_nitrogen_plasma", 1, 2, 125, plasma(Nitrogen, 80), VA[UXV], 10000);
        reaction(provider, "mk3_iron_plasma", 1, 3, 16, plasma(Iron, 40), VA[UXV], 1400);
        reaction(provider, "mk3_radon_plasma", 1, 3, 125, first(plasma(Radon, 100), plasma(Helium, 400)), max, 14000);
        reaction(provider, "mk4_residue", 1, 4, 16, fluid(gtna("dimensionally_transcendent_residue"), 100), VA[OpV], 1800);
        reaction(provider, "mk4_excited_dtec", 1, 4, 150, fluid(gtna("excited_dtec"), 150), max * 4, 18000);
        reaction(provider, "mk5_shirabon", 2, 5, 16, fluid(gtna("shirabon"), 36), max, 2000);
        reaction(provider, "mk5_excited_dtsc", 2, 5, 175, fluid(gtna("excited_dtsc"), 80), max * 16, 20000);
        reaction(provider, "mk6_protomatter", 2, 6, 16, fluid(PROTOMATTER, 200), max * 4, 2200);
        reaction(provider, "mk6_quark_gluon_plasma", 2, 6, 175, fluid(gtna("quark_gluon_plasma"), 4), max * 64, 22000);
    }

    // ------------------------------------------------------------------ Fuel Refining Complex

    private static GTRecipeBuilder frc(String name, int heat, int fieldTier) {
        return GTAARecipeTypes.FUEL_REFINING_COMPLEX.recipeBuilder(ID + "refining/" + name)
                .addData(FuelRefiningComplexMachine.HEAT, heat)
                .addData(NaquadahFuelRefineryMachine.COIL_TIER, fieldTier);
    }

    private static void refining(Consumer<FinishedRecipe> provider) {
        FluidStack asc750 = fluid(ATOMIC_SEPARATION_CATALYST, 750);
        // MkI: UHV
        FluidStack lA = fluid(LIGHT_NAQUADAH_FUEL, 2875), hA = fluid(HEAVY_NAQUADAH_FUEL, 1375);
        if (ok("refining mk1", lA, hA, asc750, fluid(Duranium, 144), fluid(NAQUADAH_FUEL[1], 1250)))
            frc("mk1", 10_800, 1).inputFluids(lA, hA, asc750, fluid(Duranium, 144))
                    .outputFluids(fluid(NAQUADAH_FUEL[1], 1250)).duration(100).EUt(VA[UHV]).save(provider);
        // MkII: UEV
        FluidStack am = first(plasma(Americium, 288), fluid(Americium, 288));
        if (ok("refining mk2", lA, hA, asc750, fluid(NAQUADAH_GAS, 5000), am, fluid(NAQUADAH_FUEL[2], 1250)))
            frc("mk2", 11_500, 1).inputItems(GTItems.GRAVI_STAR, 2)
                    .inputFluids(lA, hA, asc750, fluid(NAQUADAH_GAS, 5000), am)
                    .outputFluids(fluid(NAQUADAH_FUEL[2], 1250)).duration(120).EUt(VA[UEV]).save(provider);
        // MkIII: UIV (GTNH Chronomatic Glass -> Trinium, as the Naquadah Fuel Refinery does)
        if (ok("refining mk3", fluid(URANIUM_FUEL_EXCITED, 1500), fluid(PLUTONIUM_FUEL_EXCITED, 750), fluid(NAQUADAH_FUEL[3], 1000)))
            frc("mk3", 12_600, 2).inputItems(dust, EXTREMELY_UNSTABLE_NAQUADAH, 32)
                    .inputFluids(fluid(LIGHT_NAQUADAH_FUEL, 11500), fluid(HEAVY_NAQUADAH_FUEL, 5500), fluid(ATOMIC_SEPARATION_CATALYST, 3000),
                            fluid(Trinium, 5184), fluid(URANIUM_FUEL_EXCITED, 1500), fluid(PLUTONIUM_FUEL_EXCITED, 750))
                    .outputFluids(fluid(NAQUADAH_FUEL[3], 1000)).duration(140).EUt(VA[UIV]).save(provider);
        // MkIV: UXV (GTNH UMV). Awakened Draconium -> Neutronium blocks
        FluidStack orundum = fluid(gtna("orundum"), 9216), hypogen = fluid(gtna("hypogen"), 480);
        if (ok("refining mk4", orundum, hypogen, fluid(NAQUADAH_FUEL[4], 1000)))
            frc("mk4", 13_500, 3).inputItems(block, Neutronium, 64)
                    .inputFluids(fluid(LIGHT_NAQUADAH_FUEL, 46000), fluid(HEAVY_NAQUADAH_FUEL, 22000), fluid(ATOMIC_SEPARATION_CATALYST, 12000),
                            fluid(Trinium, 19440), orundum, hypogen)
                    .outputFluids(fluid(NAQUADAH_FUEL[4], 1000)).duration(160).EUt(VA[UXV]).save(provider);
        // MkV: OpV (GTNH UXV). Hot Proto-Halkonite plates -> Six-Phased Copper plates
        Material transcendent = gtna("transcendent_metal"), sixPhased = gtna("six_phased_copper");
        FluidStack infinity = fluid(gtna("infinity"), 2304);
        boolean rods = transcendent != null && !ChemicalHelper.get(rod, transcendent).isEmpty();
        boolean plates = sixPhased != null && !ChemicalHelper.get(plate, sixPhased).isEmpty();
        if (ok("refining mk5", rods ? "" : null, plates ? "" : null, infinity, fluid(SPACETIME, 144), fluid(NAQUADAH_FUEL[5], 1000)))
            frc("mk5", 13_500, 4).inputItems(rod, transcendent, 20).inputItems(plate, sixPhased, 16)
                    .inputFluids(fluid(LIGHT_NAQUADAH_FUEL, 184000), fluid(HEAVY_NAQUADAH_FUEL, 88000), fluid(ATOMIC_SEPARATION_CATALYST, 48000),
                            infinity, fluid(SPACETIME, 144))
                    .outputFluids(fluid(NAQUADAH_FUEL[5], 1000)).duration(180).EUt(VA[OpV]).save(provider);
        // MkVI: MAX
        FluidStack celestial = fluid(gtna("celestial_tungsten"), 5760), mellion = fluid(gtna("mellion"), 4608),
                shirabon = fluid(gtna("shirabon"), 288), rawStar = fluid(gtna("raw_star_matter"), 80),
                universium = fluid(gtna("universium"), 18);
        if (ok("refining mk6", celestial, mellion, shirabon, rawStar, fluid(NAQUADAH_FUEL[6], 1500))) {
            frc("mk6", 13_500, 4).circuitMeta(1)
                    .inputFluids(fluid(LIGHT_NAQUADAH_FUEL, 368000), fluid(HEAVY_NAQUADAH_FUEL, 176000), fluid(ATOMIC_SEPARATION_CATALYST, 96000),
                            celestial, mellion, shirabon, rawStar)
                    .outputFluids(fluid(NAQUADAH_FUEL[6], 1500)).duration(200).EUt(VA[MAX]).save(provider);
            if (universium != null)
                frc("mk6_universium", 13_500, 4).circuitMeta(2)
                        .inputFluids(fluid(LIGHT_NAQUADAH_FUEL, 368000), fluid(HEAVY_NAQUADAH_FUEL, 176000), fluid(ATOMIC_SEPARATION_CATALYST, 96000),
                                celestial, mellion, shirabon, rawStar, universium)
                        .outputFluids(fluid(NAQUADAH_FUEL[6], 3750)).duration(100).EUt(VA[MAX]).save(provider);
        }
    }

    // ------------------------------------------------------------------ controllers

    private static void machines(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(ID + "hyper_naquadah_reactor")
                .inputItems(GTAAMachines.LARGE_NAQUADAH_REACTOR.asStack())
                .inputItems(new ItemStack(GTAABlocks.FUSION_COIL_MK4.get(), 8))
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(CustomTags.UHV_CIRCUITS, 8)
                .inputItems(plateDense, Neutronium, 8)
                .inputItems(new ItemStack(GTAABlocks.FIELD_RESTRICTION_GLASS.get(), 16))
                .inputFluids(SolderingAlloy.getFluid(L * 32), Naquadria.getFluid(L * 16))
                .outputItems(GTAAMachines.HYPER_NAQUADAH_REACTOR.asStack())
                .duration(2400).EUt(VA[UHV]).save(provider);
        var adv = ASSEMBLY_LINE_RECIPES.recipeBuilder(ID + "advanced_hyper_naquadah_reactor")
                .inputItems(GTAAMachines.HYPER_NAQUADAH_REACTOR.asStack())
                .inputItems(new ItemStack(GTAABlocks.FUSION_COIL_MK5.get(), 16))
                .inputItems(GTItems.FIELD_GENERATOR_UIV, 4)
                .inputItems(CustomTags.UIV_CIRCUITS, 8)
                .inputItems(plateDense, Neutronium, 16)
                .inputFluids(SolderingAlloy.getFluid(L * 64));
        if (SPACETIME != null) adv.inputFluids(SPACETIME.getFluid(L * 16));
        adv.outputItems(GTAAMachines.ADVANCED_HYPER_NAQUADAH_REACTOR.asStack())
                .duration(3600).EUt(VA[UIV]).save(provider);
        ASSEMBLY_LINE_RECIPES.recipeBuilder(ID + "fuel_refining_complex")
                .inputItems(GTAAMachines.NAQUADAH_FUEL_REFINERY.asStack())
                .inputItems(GTBlocks.COIL_TRITANIUM.asStack(16))
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 2)
                .inputItems(CustomTags.UHV_CIRCUITS, 4)
                .inputItems(plateDense, NaquadahAlloy, 8)
                .inputItems(new ItemStack(GTAABlocks.FIELD_RESTRICTION_GLASS.get(), 16))
                .inputFluids(SolderingAlloy.getFluid(L * 32))
                .outputItems(GTAAMachines.FUEL_REFINING_COMPLEX.asStack())
                .duration(2400).EUt(VA[UHV]).save(provider);
        // Lightning Spire
        ASSEMBLER_RECIPES.recipeBuilder(ID + "spire_lightning_rod")
                .inputItems(Items.LIGHTNING_ROD)
                .inputItems(rodLong, TungstenSteel, 2)
                .inputItems(foil, Platinum, 4)
                .outputItems(GTAAItems.SPIRE_LIGHTNING_ROD.asStack())
                .duration(200).EUt(VA[IV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(ID + "lightning_spire")
                .inputItems(com.gregtechceu.gtceu.common.data.GTMachines.HULL[LuV].asStack())
                .inputItems(GTAAItems.SPIRE_LIGHTNING_ROD.asStack(8))
                .inputItems(GTItems.FIELD_GENERATOR_LuV, 2)
                .inputItems(CustomTags.LuV_CIRCUITS, 4)
                .inputItems(frameGt, TungstenSteel, 16)
                .inputFluids(SolderingAlloy.getFluid(L * 8))
                .outputItems(GTAAMachines.LIGHTNING_SPIRE.asStack())
                .duration(1200).EUt(VA[LuV]).save(provider);
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        SKIPPED.clear();
        reactions(provider);
        refining(provider);
        machines(provider);
        if (!SKIPPED.isEmpty()) GTAA.LOGGER.info("[GTAA] Hyper naquadah: skipped (missing materials) {}", SKIPPED);
    }
}
