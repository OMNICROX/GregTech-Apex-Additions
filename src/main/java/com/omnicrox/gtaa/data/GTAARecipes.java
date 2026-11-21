package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/** Default recipes. Packs can override any of them with KubeJS (ids are gtapexadditions:...). */
public class GTAARecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        seedMachines(provider);
        fusion(provider);
        antimatter(provider);
        spaceElevator(provider);
        GTAASpaceMining.init(provider);
        GTAAFusion.init(provider);
        compactFusion(provider);
        GTAANaquadah.init(provider);
        GTAAComponentAssembly.init(provider);
        componentAssemblyLine(provider);
        GTAAQft.init(provider);
        GTAAPurification.init(provider);
        megaAndCrusher(provider);
        GTAACompatRecipes.init(provider);
        GTAASuitStation.init(provider);
        GTAASpacePumping.init(provider);
        extraModules(provider);
        drones(provider);
        ascensionGate(provider);
        GTAAGTNHRecipes.init(provider);
        GTAAHyperNaquadah.init(provider);
    }

    /** a gtna material if it's installed and has the given form */
    private static com.gregtechceu.gtceu.api.data.chemical.material.Material gtna(String id,
            com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey<?> key) {
        var m = com.gregtechceu.gtceu.api.GTCEuAPI.materialManager.getMaterial("gtna:" + id);
        return m == null || m.isNull() || !m.hasProperty(key) ? null : m;
    }

    /**
     * SLAM matter: Molten Superconductor Base UIV / UMV. GT Nexus Additions registers the fluids but nothing makes
     * them; GTAA makes them in the Electric Blast Furnace. 1 L of base pairs with 1 L of antimatter.
     */
    private static void slamMatter(Consumer<FinishedRecipe> provider) {
        var DUST = com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.DUST;
        var FLUID = com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.FLUID;
        var uiv = gtna("superconductor_uiv_base", FLUID);
        var umv = gtna("superconductor_umv_base", FLUID);
        if (uiv == null || umv == null) return;
        var infinity = gtna("infinity", DUST);
        var transcendent = gtna("transcendent_metal", DUST);

        // EBF: 3 items, 1 fluid in. Tritanium coils reach both (10,800 K + 100 K per voltage tier over MV)
        // UIV: the UHV superconductor base, upgraded with neutronium and infinity
        BLAST_RECIPES.recipeBuilder("gtapexadditions:superconductor_uiv_base")
                .inputItems(dust, GTMaterials.RutheniumTriniumAmericiumNeutronate, 6)
                .inputItems(dust, GTMaterials.Neutronium, 3)
                .inputItems(dust, infinity != null ? infinity : GTMaterials.Tritanium, 1)
                .outputFluids(uiv.getFluid(L * 10))
                .blastFurnaceTemp(10_800).duration(800).EUt(VA[UEV]).save(provider);

        // UMV: UIV base folded with transcendent metal
        BLAST_RECIPES.recipeBuilder("gtapexadditions:superconductor_umv_base")
                .inputFluids(uiv.getFluid(L * 8))
                .inputItems(dust, transcendent != null ? transcendent : GTMaterials.Naquadria, 1)
                .inputItems(dust, GTMaterials.Neutronium, 1)
                .outputFluids(umv.getFluid(L * 10))
                .blastFurnaceTemp(11_500).duration(800).EUt(VA[UIV]).save(provider);
    }

    /** Ascension Gate: built at MV so it can track the whole run, one chevron per tier. */
    private static void ascensionGate(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gate_base")
                .inputItems(plate, GTMaterials.Steel, 4).inputItems(frameGt, GTMaterials.Steel)
                .outputItems(GTAABlocks.GATE_BASE.asStack(2))
                .duration(100).EUt(VA[LV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gate_ring")
                .inputItems(plate, GTMaterials.Aluminium, 4).inputItems(frameGt, GTMaterials.Steel)
                .outputItems(GTAABlocks.GATE_RING.asStack(2))
                .duration(100).EUt(VA[MV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gate_chevron")
                .inputItems(GTAABlocks.GATE_RING.asStack())
                .inputItems(dust, GTMaterials.Glowstone, 2)
                .inputItems(GTItems.EMITTER_LV)
                .inputItems(CustomTags.MV_CIRCUITS)
                .outputItems(GTAABlocks.GATE_CHEVRON.asStack())
                .duration(200).EUt(VA[MV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:ascension_gate")
                .inputItems(GTMachines.HULL[MV].asStack())
                .inputItems(GTAABlocks.GATE_CHEVRON.asStack(4))
                .inputItems(GTItems.EMITTER_MV, 2)
                .inputItems(GTItems.SENSOR_MV, 2)
                .inputItems(CustomTags.MV_CIRCUITS, 4)
                .outputItems(GTAAMachines.ASCENSION_GATE.asStack())
                .duration(1200).EUt(VA[MV]).save(provider);
    }

    /** Seed Hatch (LV-LuV) and Seed Synthesizer (LV-UHV) for the Mystical Cultivation Array's GT seeds. */
    private static void seedMachines(Consumer<FinishedRecipe> provider) {
        var fieldGens = new com.tterrag.registrate.util.entry.ItemEntry<?>[] { null, GTItems.FIELD_GENERATOR_LV,
                GTItems.FIELD_GENERATOR_MV, GTItems.FIELD_GENERATOR_HV, GTItems.FIELD_GENERATOR_EV, GTItems.FIELD_GENERATOR_IV,
                GTItems.FIELD_GENERATOR_LuV, GTItems.FIELD_GENERATOR_ZPM, GTItems.FIELD_GENERATOR_UV, GTItems.FIELD_GENERATOR_UHV };
        var arms = new com.tterrag.registrate.util.entry.ItemEntry<?>[] { null, GTItems.ROBOT_ARM_LV, GTItems.ROBOT_ARM_MV,
                GTItems.ROBOT_ARM_HV, GTItems.ROBOT_ARM_EV, GTItems.ROBOT_ARM_IV, GTItems.ROBOT_ARM_LuV, GTItems.ROBOT_ARM_ZPM,
                GTItems.ROBOT_ARM_UV, GTItems.ROBOT_ARM_UHV };
        for (var def : GTAAMachines.SEED_HATCH) {
            if (def == null) continue;
            int t = def.getTier();
            var b = ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:seed_hatch_" + VN[t].toLowerCase())
                    .inputItems(com.gregtechceu.gtceu.common.data.GTMachines.ITEM_IMPORT_BUS[t].asStack())
                    .inputItems(CustomTags.CIRCUITS_ARRAY[t], 2)
                    .inputItems(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WHEAT_SEEDS, 16));
            if (t > LV) b.inputItems(GTAAMachines.SEED_HATCH[t - 1].asStack());
            b.outputItems(def.asStack()).duration(200).EUt(VA[t]).save(provider);
        }
        for (var def : GTAAMachines.SEED_SYNTHESIZER) {
            if (def == null) continue;
            int t = def.getTier();
            ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:seed_synthesizer_" + VN[t].toLowerCase())
                    .inputItems(com.gregtechceu.gtceu.common.data.GTMachines.HULL[t].asStack())
                    .inputItems(fieldGens[t].asStack())
                    .inputItems(arms[t].asStack(), 2)
                    .inputItems(CustomTags.CIRCUITS_ARRAY[t], 4)
                    .inputItems(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WHEAT_SEEDS, 32))
                    .outputItems(def.asStack()).duration(400).EUt(VA[t]).save(provider);
        }
    }

    private static void fusion(Consumer<FinishedRecipe> provider) {
        // --- MK IV (UHV) ---
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:fusion_casing_mk4")
                .inputItems(GTBlocks.FUSION_CASING_MK3.asStack())
                .inputItems(plateDense, GTMaterials.Neutronium, 2)
                .inputItems(GTItems.FIELD_GENERATOR_UV, 2)
                .inputItems(CustomTags.UHV_CIRCUITS)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 4))
                .outputItems(GTAABlocks.FUSION_CASING_MK4.asStack())
                .duration(400).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:fusion_coil_mk4")
                .inputItems(GTBlocks.FUSION_COIL.asStack())
                .inputItems(wireGtQuadruple, GTMaterials.RutheniumTriniumAmericiumNeutronate, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UV, 4)
                .inputItems(CustomTags.UHV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 8))
                .outputItems(GTAABlocks.FUSION_COIL_MK4.asStack())
                .duration(600).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:uhv_fusion_reactor")
                .inputItems(GTMultiMachines.FUSION_REACTOR[UV].asStack())
                .inputItems(GTAABlocks.FUSION_COIL_MK4.asStack(), 4)
                .inputItems(CustomTags.UHV_CIRCUITS, 8)
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(plateDense, GTMaterials.Neutronium, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .outputItems(GTAAMachines.FUSION_REACTOR[UHV].asStack())
                .duration(1200).EUt(VA[UHV]).save(provider);

        // --- MK V (UEV) ---
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:fusion_casing_mk5")
                .inputItems(GTAABlocks.FUSION_CASING_MK4.asStack())
                .inputItems(plateDense, GTMaterials.Neutronium, 6)
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 2)
                .inputItems(CustomTags.UEV_CIRCUITS)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 16))
                .outputItems(GTAABlocks.FUSION_CASING_MK5.asStack())
                .duration(400).EUt(VA[UHV]).save(provider);
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:fusion_coil_mk5")
                .inputItems(GTAABlocks.FUSION_COIL_MK4.asStack())
                .inputItems(wireGtHex, GTMaterials.RutheniumTriniumAmericiumNeutronate, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 32))
                .inputFluids(GTAAMaterials.METASTABLE_OGANESSON.getFluid(L * 4))
                .inputFluids(GTAAMaterials.pw(5, 4000))
                .outputItems(GTAABlocks.FUSION_COIL_MK5.asStack())
                .addData(GTAARecipeTypes.MODULE_TIER, 1)
                .duration(600).EUt(VA[UHV]).save(provider);
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:uev_fusion_reactor")
                .inputItems(GTAAMachines.FUSION_REACTOR[UHV].asStack())
                .inputItems(GTAABlocks.FUSION_COIL_MK5.asStack(), 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(GTItems.FIELD_GENERATOR_UEV, 4)
                .inputItems(plateDense, GTMaterials.Neutronium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .inputFluids(GTAAMaterials.METASTABLE_OGANESSON.getFluid(L * 16))
                .inputFluids(GTAAMaterials.pw(6, 8000))
                .outputItems(GTAAMachines.FUSION_REACTOR[UEV].asStack())
                .addData(GTAARecipeTypes.MODULE_TIER, 1)
                .duration(1200).EUt(VA[UEV]).save(provider);
    }

    private static void antimatter(Consumer<FinishedRecipe> provider) {
        // --- casings ---
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:magnetic_flux_casing")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(plate, GTMaterials.NaquadahAlloy, 6)
                .inputItems(rod, GTMaterials.NeodymiumMagnetic, 2)
                .outputItems(GTAABlocks.MAGNETIC_FLUX_CASING.asStack(4))
                .duration(100).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:gravity_stabilization_casing")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(plate, GTMaterials.Tritanium, 6)
                .inputItems(GTItems.GRAVI_STAR)
                .outputItems(GTAABlocks.GRAVITY_STABILIZATION_CASING.asStack(4))
                .duration(100).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:antimatter_containment_casing")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(plate, GTMaterials.Neutronium, 6)
                .inputItems(GTItems.FIELD_GENERATOR_UV)
                .outputItems(GTAABlocks.ANTIMATTER_CONTAINMENT_CASING.asStack(2))
                .duration(200).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:antimatter_annihilation_matrix")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(plateDense, GTMaterials.Neutronium, 2)
                .inputItems(GTItems.FIELD_GENERATOR_UV)
                .inputItems(GTItems.GRAVI_STAR)
                .outputItems(GTAABlocks.ANTIMATTER_ANNIHILATION_MATRIX.asStack())
                .duration(300).EUt(VA[UHV]).save(provider);

        // --- hatch + controllers ---
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:antimatter_hatch")
                .inputItems(GTMachines.FLUID_EXPORT_HATCH[UHV].asStack())
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(plateDense, GTMaterials.Neutronium, 8)
                .inputItems(CustomTags.UEV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 16))
                .inputFluids(GTAAMaterials.METASTABLE_OGANESSON.getFluid(L * 16))
                .inputFluids(GTAAMaterials.pw(6, 4000))
                .outputItems(GTAAMachines.ANTIMATTER_HATCH.asStack())
                .addData(GTAARecipeTypes.MODULE_TIER, 1)
                .duration(600).EUt(VA[UEV]).save(provider);
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:antimatter_forge")
                .inputItems(GTAABlocks.ANTIMATTER_CONTAINMENT_CASING.asStack(16))
                .inputItems(GTAABlocks.PROTOMATTER_ACTIVATION_COIL.asStack(8))
                .inputItems(GTItems.FIELD_GENERATOR_UEV, 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(GTItems.GRAVI_STAR, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .inputFluids(GTAAMaterials.METASTABLE_OGANESSON.getFluid(L * 64))
                .inputFluids(GTAAMaterials.pw(7, 16000))
                .outputItems(GTAAMachines.ANTIMATTER_FORGE.asStack())
                .addData(GTAARecipeTypes.MODULE_TIER, 1)
                .duration(2400).EUt(VA[UEV]).save(provider);
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:antimatter_generator")
                .inputItems(GTAABlocks.ANTIMATTER_ANNIHILATION_MATRIX.asStack(16))
                .inputItems(GTAABlocks.PROTOMATTER_ACTIVATION_COIL.asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_UEV, 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(GTItems.EMITTER_UEV, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .inputFluids(GTAAMaterials.METASTABLE_OGANESSON.getFluid(L * 64))
                .inputFluids(GTAAMaterials.pw(7, 16000))
                .outputItems(GTAAMachines.ANTIMATTER_GENERATOR.asStack())
                .addData(GTAARecipeTypes.MODULE_TIER, 1)
                .duration(2400).EUt(VA[UEV]).save(provider);

        slamMatter(provider);

        // --- fluids: MK IV / MK V fusion ---
        if (!GTAAMaterials.externalFluids()) {
            FUSION_RECIPES.recipeBuilder("gtapexadditions:spatially_enlarged_fluid")
                    .inputFluids(GTMaterials.Neutronium.getFluid(L), GTMaterials.Deuterium.getFluid(1000))
                    .outputFluids(GTAAMaterials.SPATIALLY_ENLARGED_FLUID.getFluid(L))
                    .duration(100).EUt(VA[UHV]).fusionStartEU(2_500_000_000L).save(provider);
            FUSION_RECIPES.recipeBuilder("gtapexadditions:tachyon_rich_temporal_fluid")
                    .inputFluids(GTMaterials.Naquadria.getFluid(L), GTMaterials.Americium.getFluid(L))
                    .outputFluids(GTAAMaterials.TACHYON_RICH_TEMPORAL_FLUID.getFluid(L))
                    .duration(100).EUt(VA[UHV]).fusionStartEU(3_000_000_000L).save(provider);
            FUSION_RECIPES.recipeBuilder("gtapexadditions:spacetime")
                    .inputFluids(GTAAMaterials.SPATIALLY_ENLARGED_FLUID.getFluid(L), GTAAMaterials.TACHYON_RICH_TEMPORAL_FLUID.getFluid(L))
                    .outputFluids(GTAAMaterials.SPACETIME.getFluid(L))
                    .duration(200).EUt(VA[UEV]).fusionStartEU(12_000_000_000L).save(provider);
        }
        // --- protomatter + the first antimatter ---
        var gtna = com.gregtechceu.gtceu.api.GTCEuAPI.materialManager;
        var dtr = GTAAMaterials.externalFluids() ? gtna.getMaterial("gtna:dimensionally_transcendent_residue") : null;
        var primordial = GTAAMaterials.externalFluids() ? gtna.getMaterial("gtna:primordial_matter") : null;
        var transcendent = GTAAMaterials.externalFluids() ? gtna.getMaterial("gtna:transcendent_metal") : null;
        if (dtr != null && primordial != null) {
            // GTNH (NewHorizonsCoreMod LaserEngraverRecipes): fluid-only, cleanroom, 10 s.
            // GTNH UMV / UXV are GTCEu UXV / OpV.
            LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:protomatter_from_dtr")
                    .inputFluids(dtr.getFluid(100))
                    .outputFluids(GTAAMaterials.PROTOMATTER.getFluid(100))
                    .duration(200).EUt(VA[UIV])
                    .cleanroom(com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType.CLEANROOM).save(provider);
            LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:protomatter_from_spacetime")
                    .inputFluids(GTAAMaterials.SPACETIME.getFluid(100))
                    .outputFluids(GTAAMaterials.PROTOMATTER.getFluid(1000))
                    .duration(200).EUt(VA[UXV])
                    .cleanroom(com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType.CLEANROOM).save(provider);
            LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:protomatter_from_primordial_matter")
                    .inputFluids(primordial.getFluid(100))
                    .outputFluids(GTAAMaterials.PROTOMATTER.getFluid(10000))
                    .duration(200).EUt(VA[OpV])
                    .cleanroom(com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType.CLEANROOM).save(provider);
            // GTNH bootstrap: superdense Hot Proto-Halkonite + 100 L protomatter -> 1 L antimatter, 10,000 s at UIV.
            // Proto-Halkonite doesn't exist here, gtna's Transcendent Metal stands in.
            var b = LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:antimatter_bootstrap");
            if (transcendent != null && !com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(plateDense, transcendent).isEmpty())
                b.inputItems(plateDense, transcendent, 8);
            else if (transcendent != null && !com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(ingot, transcendent).isEmpty())
                b.inputItems(ingot, transcendent, 64);
            else
                b.inputItems(plateDense, GTMaterials.Neutronium, 64);
            b.inputFluids(GTAAMaterials.PROTOMATTER.getFluid(100))
                    .outputFluids(GTAAMaterials.ANTIMATTER.getFluid(1))
                    .duration(10_000 * 20).EUt(VA[UIV])
                    .cleanroom(com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType.CLEANROOM).save(provider);
        } else {
            // without gtna: fusion stand-ins
            FUSION_RECIPES.recipeBuilder("gtapexadditions:protomatter")
                    .inputFluids(GTMaterials.Neutronium.getFluid(L), GTMaterials.Helium3.getFluid(1000))
                    .outputFluids(GTAAMaterials.PROTOMATTER.getFluid(1000))
                    .duration(100).EUt(VA[UEV]).fusionStartEU(10_000_000_000L).save(provider);
            FUSION_RECIPES.recipeBuilder("gtapexadditions:antimatter_seed")
                    .inputFluids(GTAAMaterials.PROTOMATTER.getFluid(1000), GTAAMaterials.SPACETIME.getFluid(L))
                    .outputFluids(GTAAMaterials.ANTIMATTER.getFluid(16))
                    .duration(400).EUt(VA[UEV]).fusionStartEU(15_000_000_000L).save(provider);
        }

        // activation stabilizers: depleted MkV / MkVI now come out of the Large Naquadah Reactor
    }

    private static void spaceElevator(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:space_elevator_base_casing")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(plate, GTMaterials.Tritanium, 6)
                .outputItems(GTAABlocks.SE_BASE_CASING.asStack(4))
                .duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:space_elevator_support_structure")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(plate, GTMaterials.NaquadahAlloy, 4)
                .inputItems(rodLong, GTMaterials.Neutronium, 2)
                .outputItems(GTAABlocks.SE_SUPPORT_STRUCTURE.asStack(4))
                .duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:space_elevator_internal_structure")
                .inputItems(frameGt, GTMaterials.Tritanium)
                .inputItems(plate, GTMaterials.Neutronium, 4)
                .inputItems(gear, GTMaterials.Tritanium, 2)
                .outputItems(GTAABlocks.SE_INTERNAL_STRUCTURE.asStack(4))
                .duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:ultra_high_strength_concrete_floor")
                .inputItems(plate, GTMaterials.Neutronium)
                .inputFluids(GTMaterials.Concrete.getFluid(L * 8))
                .outputItems(GTAABlocks.SE_CONCRETE_FLOOR.asStack(8))
                .duration(100).EUt(VA[UV]).save(provider);

        var motors = new com.tterrag.registrate.util.entry.BlockEntry<?>[] { GTAABlocks.SE_MOTOR_MK1, GTAABlocks.SE_MOTOR_MK2,
                GTAABlocks.SE_MOTOR_MK3, GTAABlocks.SE_MOTOR_MK4, GTAABlocks.SE_MOTOR_MK5 };
        var motorItems = java.util.List.of(GTItems.ELECTRIC_MOTOR_UV, GTItems.ELECTRIC_MOTOR_UHV, GTItems.ELECTRIC_MOTOR_UEV,
                GTItems.ELECTRIC_MOTOR_UIV, GTItems.ELECTRIC_MOTOR_UXV);
        var circuits = java.util.List.of(CustomTags.UV_CIRCUITS, CustomTags.UHV_CIRCUITS, CustomTags.UEV_CIRCUITS,
                CustomTags.UIV_CIRCUITS, CustomTags.UXV_CIRCUITS);
        int[] tiers = { UV, UHV, UEV, UIV, UXV };
        for (int i = 0; i < 5; i++) {
            ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:space_elevator_motor_mk" + (i + 1))
                    .inputItems(frameGt, GTMaterials.Neutronium)
                    .inputItems(motorItems.get(i), 4)
                    .inputItems(circuits.get(i), 2)
                    .inputItems(wireGtDouble, GTMaterials.RutheniumTriniumAmericiumNeutronate, 4)
                    .outputItems(motors[i].asStack())
                    .duration(400).EUt(VA[tiers[i]]).save(provider);
        }
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:space_elevator_cable")
                .inputItems(wireFine, GTMaterials.Neutronium, 64)
                .inputItems(plateDense, GTMaterials.Neutronium, 16)
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 32))
                .outputItems(GTAABlocks.SPACE_ELEVATOR_CABLE.asStack())
                .duration(1200).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:space_elevator")
                .inputItems(GTAABlocks.SE_BASE_CASING.asStack(16))
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(GTItems.SENSOR_UHV, 4)
                .inputItems(GTItems.EMITTER_UHV, 4)
                .inputItems(CustomTags.UHV_CIRCUITS, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .outputItems(GTAAMachines.SPACE_ELEVATOR.asStack())
                .duration(2400).EUt(VA[UHV]).save(provider);

        var modules = java.util.List.of(GTAAMachines.SPACE_ASSEMBLER_MK1, GTAAMachines.SPACE_ASSEMBLER_MK2, GTAAMachines.SPACE_ASSEMBLER_MK3);
        var arms = java.util.List.of(GTItems.ROBOT_ARM_UHV, GTItems.ROBOT_ARM_UIV, GTItems.ROBOT_ARM_UXV);
        var conveyors = java.util.List.of(GTItems.CONVEYOR_MODULE_UHV, GTItems.CONVEYOR_MODULE_UIV, GTItems.CONVEYOR_MODULE_UXV);
        var moduleCircuits = java.util.List.of(CustomTags.UHV_CIRCUITS, CustomTags.UIV_CIRCUITS, CustomTags.UXV_CIRCUITS);
        int[] moduleTiers = { UHV, UIV, UXV };
        for (int i = 0; i < 3; i++) {
            var b = ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:space_assembler_module_mk" + (i + 1))
                    .inputItems(GTAABlocks.SE_BASE_CASING.asStack(4))
                    .inputItems(arms.get(i), 4)
                    .inputItems(conveyors.get(i), 4)
                    .inputItems(moduleCircuits.get(i), 8);
            if (i > 0) b.inputItems(modules.get(i - 1).asStack());
            b.inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                    .outputItems(modules.get(i).asStack())
                    .duration(1200).EUt(VA[moduleTiers[i]]).save(provider);
        }

        // --- Space Assembler recipes: cheaper bulk versions of late-game parts ---
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:sa/fusion_coil_mk4")
                .inputItems(GTBlocks.FUSION_COIL.asStack(4))
                .inputItems(wireGtQuadruple, GTMaterials.RutheniumTriniumAmericiumNeutronate, 8)
                .inputItems(GTItems.FIELD_GENERATOR_UV, 8)
                .inputItems(CustomTags.UHV_CIRCUITS, 4)
                .outputItems(GTAABlocks.FUSION_COIL_MK4.asStack(4))
                .duration(600).EUt(VA[UHV]).save(provider);
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:sa/protomatter_activation_coil")
                .inputItems(frameGt, GTMaterials.Neutronium, 4)
                .inputItems(wireGtQuadruple, GTMaterials.RutheniumTriniumAmericiumNeutronate, 8)
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 2)
                .inputItems(CustomTags.UHV_CIRCUITS, 2)
                .outputItems(GTAABlocks.PROTOMATTER_ACTIVATION_COIL.asStack(4))
                .duration(400).EUt(VA[UHV]).save(provider);
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:sa/antimatter_annihilation_matrix")
                .inputItems(frameGt, GTMaterials.Neutronium, 4)
                .inputItems(plateDense, GTMaterials.Neutronium, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UV, 2)
                .inputItems(GTItems.GRAVI_STAR, 2)
                .outputItems(GTAABlocks.ANTIMATTER_ANNIHILATION_MATRIX.asStack(4))
                .duration(300).EUt(VA[UHV]).save(provider);
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:sa/antimatter_hatch")
                .inputItems(GTMachines.FLUID_EXPORT_HATCH[UHV].asStack(2))
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(plateDense, GTMaterials.Neutronium, 8)
                .inputItems(CustomTags.UEV_CIRCUITS, 2)
                .outputItems(GTAAMachines.ANTIMATTER_HATCH.asStack(2))
                .addData(GTAARecipeTypes.MODULE_TIER, 2)
                .duration(600).EUt(VA[UEV]).save(provider);
        for (int i = 2; i < 5; i++) {
            GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:sa/space_elevator_motor_mk" + (i + 1))
                    .inputItems(frameGt, GTMaterials.Neutronium, 4)
                    .inputItems(motorItems.get(i), 8)
                    .inputItems(circuits.get(i), 4)
                    .inputItems(wireGtDouble, GTMaterials.RutheniumTriniumAmericiumNeutronate, 8)
                    .outputItems(motors[i].asStack(4))
                    .addData(GTAARecipeTypes.MODULE_TIER, i - 1)
                    .duration(400).EUt(VA[tiers[i]]).save(provider);
        }
    }

    private static void drones(Consumer<FinishedRecipe> provider) {
        com.gregtechceu.gtceu.api.data.chemical.material.Material[] mats = { null, GTMaterials.Steel, GTMaterials.Steel,
                GTMaterials.Titanium, GTMaterials.Titanium, GTMaterials.TungstenSteel, GTMaterials.TungstenSteel,
                GTMaterials.Naquadah, GTMaterials.Naquadah, GTMaterials.NaquadahAlloy, GTMaterials.Neutronium,
                GTMaterials.Neutronium, GTMaterials.Neutronium, GTMaterials.Neutronium };
        var motors = java.util.List.of(GTItems.ELECTRIC_MOTOR_LV, GTItems.ELECTRIC_MOTOR_LV, GTItems.ELECTRIC_MOTOR_MV,
                GTItems.ELECTRIC_MOTOR_HV, GTItems.ELECTRIC_MOTOR_EV, GTItems.ELECTRIC_MOTOR_IV, GTItems.ELECTRIC_MOTOR_LuV,
                GTItems.ELECTRIC_MOTOR_ZPM, GTItems.ELECTRIC_MOTOR_UV, GTItems.ELECTRIC_MOTOR_UHV, GTItems.ELECTRIC_MOTOR_UEV,
                GTItems.ELECTRIC_MOTOR_UIV, GTItems.ELECTRIC_MOTOR_UXV, GTItems.ELECTRIC_MOTOR_OpV);
        var sensors = java.util.List.of(GTItems.SENSOR_LV, GTItems.SENSOR_LV, GTItems.SENSOR_MV, GTItems.SENSOR_HV,
                GTItems.SENSOR_EV, GTItems.SENSOR_IV, GTItems.SENSOR_LuV, GTItems.SENSOR_ZPM, GTItems.SENSOR_UV,
                GTItems.SENSOR_UHV, GTItems.SENSOR_UEV, GTItems.SENSOR_UIV, GTItems.SENSOR_UXV, GTItems.SENSOR_OpV);
        for (int tier = LV; tier <= OpV; tier++) {
            var b = ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:mining_drone_" + VN[tier].toLowerCase(java.util.Locale.ROOT))
                    .inputItems(plate, mats[tier], 6)
                    .inputItems(motors.get(tier), 4)
                    .inputItems(sensors.get(tier), 2)
                    .inputItems(CustomTags.CIRCUITS_ARRAY[tier], 2);
            if (tier > LV) b.inputItems(com.omnicrox.gtaa.common.data.GTAAItems.MINING_DRONES[tier - 1].asStack());
            b.outputItems(com.omnicrox.gtaa.common.data.GTAAItems.MINING_DRONES[tier].asStack())
                    .duration(400).EUt(VA[tier]).save(provider);
        }
        var minerModules = java.util.List.of(GTAAMachines.SPACE_MINING_MK1, GTAAMachines.SPACE_MINING_MK2, GTAAMachines.SPACE_MINING_MK3);
        int[] mt = { UV, UHV, UEV };
        var pumps = java.util.List.of(GTItems.ELECTRIC_PUMP_UV, GTItems.ELECTRIC_PUMP_UHV, GTItems.ELECTRIC_PUMP_UEV);
        var circ = java.util.List.of(CustomTags.UV_CIRCUITS, CustomTags.UHV_CIRCUITS, CustomTags.UEV_CIRCUITS);
        for (int i = 0; i < 3; i++) {
            var b = (i == 0 ? ASSEMBLER_RECIPES : GTAARecipeTypes.SPACE_ASSEMBLER)
                    .recipeBuilder("gtapexadditions:space_mining_module_mk" + (i + 1))
                    .inputItems(GTAABlocks.SE_BASE_CASING.asStack(4))
                    .inputItems(com.omnicrox.gtaa.common.data.GTAAItems.MINING_DRONES[mt[i]].asStack(2))
                    .inputItems(pumps.get(i), 4)
                    .inputItems(circ.get(i), 8);
            if (i > 0) b.inputItems(minerModules.get(i - 1).asStack()).addData(GTAARecipeTypes.MODULE_TIER, i);
            b.outputItems(minerModules.get(i).asStack()).duration(1200).EUt(VA[mt[i]]).save(provider);
        }
    }

    private static void extraModules(Consumer<FinishedRecipe> provider) {
        // Space Pumping Modules
        var pumpModules = java.util.List.of(GTAAMachines.SPACE_PUMP_MK1, GTAAMachines.SPACE_PUMP_MK2, GTAAMachines.SPACE_PUMP_MK3);
        int[] pt = { UHV, UEV, UXV };
        var pumps = java.util.List.of(GTItems.ELECTRIC_PUMP_UHV, GTItems.ELECTRIC_PUMP_UEV, GTItems.ELECTRIC_PUMP_UXV);
        var circ = java.util.List.of(CustomTags.UHV_CIRCUITS, CustomTags.UEV_CIRCUITS, CustomTags.UXV_CIRCUITS);
        var hatches = java.util.List.of(GTMachines.FLUID_EXPORT_HATCH[UHV], GTMachines.FLUID_EXPORT_HATCH[UEV], GTMachines.FLUID_EXPORT_HATCH[UXV]);
        for (int i = 0; i < 3; i++) {
            var b = (i == 0 ? ASSEMBLER_RECIPES : GTAARecipeTypes.SPACE_ASSEMBLER)
                    .recipeBuilder("gtapexadditions:space_pumping_module_mk" + (i + 1))
                    .inputItems(GTAABlocks.SE_BASE_CASING.asStack(4))
                    .inputItems(pumps.get(i), 8)
                    .inputItems(hatches.get(i).asStack(2))
                    .inputItems(circ.get(i), 8);
            if (i > 0) b.inputItems(pumpModules.get(i - 1).asStack()).addData(GTAARecipeTypes.MODULE_TIER, i);
            b.inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                    .outputItems(pumpModules.get(i).asStack()).duration(1200).EUt(VA[pt[i]]).save(provider);
        }

        // Resource Collection Module: a Mk-III miner and a Mk-III pump fused together
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:resource_collection_module")
                .inputItems(GTAAMachines.SPACE_MINING_MK3.asStack())
                .inputItems(GTAAMachines.SPACE_PUMP_MK3.asStack())
                .inputItems(GTItems.FIELD_GENERATOR_UIV, 4)
                .inputItems(GTItems.ROBOT_ARM_UIV, 8)
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .inputFluids(GTAAMaterials.pw(5, 8000))
                .outputItems(GTAAMachines.RESOURCE_COLLECTION_MODULE.asStack())
                .addData(GTAARecipeTypes.MODULE_TIER, 2)
                .duration(2400).EUt(VA[UIV]).save(provider);

        // Space Drill MK-123
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:space_drill_mk123")
                .inputItems(GTAAMachines.SPACE_MINING_MK3.asStack(4))
                .inputItems(com.omnicrox.gtaa.common.data.GTAAItems.MINING_DRONES[UXV].asStack(4))
                .inputItems(GTItems.ELECTRIC_MOTOR_UXV, 16)
                .inputItems(GTItems.FIELD_GENERATOR_UXV, 4)
                .inputItems(CustomTags.UXV_CIRCUITS, 16)
                .inputItems(plateDense, GTMaterials.Neutronium, 32)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 128))
                .inputFluids(GTAAMaterials.pw(5, 8000))
                .outputItems(GTAAMachines.SPACE_DRILL_MK123.asStack())
                .addData(GTAARecipeTypes.MODULE_TIER, 3)
                .duration(4800).EUt(VA[UXV]).save(provider);

        // Dyson Sphere Launch Site + swarm modules
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:dyson_sphere_launch_site")
                .inputItems(GTAABlocks.SE_BASE_CASING.asStack(16))
                .inputItems(GTItems.FIELD_GENERATOR_UIV, 8)
                .inputItems(GTItems.EMITTER_UIV, 8)
                .inputItems(GTItems.ELECTRIC_PISTON_UIV, 16)
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .inputFluids(GTAAMaterials.pw(8, 16000))
                .outputItems(GTAAMachines.DYSON_LAUNCH_SITE.asStack())
                .addData(GTAARecipeTypes.MODULE_TIER, 2)
                .duration(2400).EUt(VA[UIV]).save(provider);
        GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:dyson_swarm_module")
                .inputItems(foil, GTMaterials.Neutronium, 16)
                .inputItems(plate, GTMaterials.Tritanium, 8)
                .inputItems(GTItems.EMITTER_UHV, 2)
                .inputItems(GTItems.SENSOR_UHV, 2)
                .inputItems(CustomTags.UHV_CIRCUITS, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 4))
                .inputFluids(GTAAMaterials.pw(6, 2000))
                .outputItems(com.omnicrox.gtaa.common.data.GTAAItems.DYSON_SWARM_MODULE.asStack(4))
                .addData(GTAARecipeTypes.MODULE_TIER, 1)
                .duration(200).EUt(VA[UEV]).save(provider);
        GTAARecipeTypes.DYSON_LAUNCH.recipeBuilder("gtapexadditions:launch_swarm_module")
                .inputItems(com.omnicrox.gtaa.common.data.GTAAItems.DYSON_SWARM_MODULE.asStack())
                .inputFluids(GTMaterials.RocketFuel.getFluid(1000))
                .duration(200).EUt(VA[UEV]).save(provider);
    }

    private static void compactFusion(Consumer<FinishedRecipe> provider) {
        var plates = new com.gregtechceu.gtceu.api.data.chemical.material.Material[] { null,
                GTMaterials.NaquadahAlloy, GTMaterials.Duranium, GTMaterials.Tritanium, GTMaterials.Neutronium,
                GTAAMaterials.METASTABLE_OGANESSON };
        var wires = new com.gregtechceu.gtceu.api.data.chemical.material.Material[] { null,
                GTMaterials.IndiumTinBariumTitaniumCuprate, GTMaterials.UraniumRhodiumDinaquadide,
                GTMaterials.EnrichedNaquadahTriniumEuropiumDuranide, GTMaterials.RutheniumTriniumAmericiumNeutronate,
                GTMaterials.RutheniumTriniumAmericiumNeutronate };
        var circuits = java.util.List.of(CustomTags.LuV_CIRCUITS, CustomTags.LuV_CIRCUITS, CustomTags.ZPM_CIRCUITS,
                CustomTags.UV_CIRCUITS, CustomTags.UHV_CIRCUITS, CustomTags.UEV_CIRCUITS);
        var fieldGens = java.util.List.of(GTItems.FIELD_GENERATOR_LuV, GTItems.FIELD_GENERATOR_LuV, GTItems.FIELD_GENERATOR_ZPM,
                GTItems.FIELD_GENERATOR_UV, GTItems.FIELD_GENERATOR_UHV, GTItems.FIELD_GENERATOR_UEV);
        var fusionComputers = java.util.List.of(GTMultiMachines.FUSION_REACTOR[LuV], GTMultiMachines.FUSION_REACTOR[LuV],
                GTMultiMachines.FUSION_REACTOR[ZPM], GTMultiMachines.FUSION_REACTOR[UV],
                GTAAMachines.FUSION_REACTOR[UHV], GTAAMachines.FUSION_REACTOR[UEV]);
        for (int mk = 1; mk <= 5; mk++) {
            int tier = LuV + mk - 1;
            // coils: 560 per reactor, so 2 per craft and fairly cheap
            var coil = ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:compact_fusion_coil_mk" + mk);
            if (mk > 1) coil.inputItems(GTAABlocks.compactFusionCoil(mk - 1).asItem(), 2);
            coil.inputItems(plate, plates[mk], 4)
                    .inputItems(wireGtSingle, wires[mk], 4)
                    .inputItems(circuits.get(mk), 1)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L))
                    .outputItems(new net.minecraft.world.item.ItemStack(GTAABlocks.compactFusionCoil(mk), 2))
                    .duration(100).EUt(VA[tier]).save(provider);
            // controller: the normal fusion computer of the same tier, compressed
            var b = (mk == 5 ? GTAARecipeTypes.SPACE_ASSEMBLER : ASSEMBLY_LINE_RECIPES)
                    .recipeBuilder("gtapexadditions:compact_fusion_mk" + mk)
                    .inputItems(fusionComputers.get(mk).asStack())
                    .inputItems(new net.minecraft.world.item.ItemStack(GTAABlocks.compactFusionCoil(mk), 16))
                    .inputItems(fieldGens.get(mk), 4)
                    .inputItems(circuits.get(mk), 16)
                    .inputItems(plateDense, plates[mk], 8)
                    .inputItems(wireGtSingle, wires[mk], 32)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                    .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 16));
            if (mk == 5) b.addData(GTAARecipeTypes.MODULE_TIER, 1);
            b.outputItems(GTAAMachines.COMPACT_FUSION[mk - 1].asStack())
                    .duration(2400).EUt(VA[tier]).save(provider);
        }
    }

    private static void componentAssemblyLine(Consumer<FinishedRecipe> provider) {
        var plates = new com.gregtechceu.gtceu.api.data.chemical.material.Material[] { null, GTMaterials.Steel,
                GTMaterials.Aluminium, GTMaterials.StainlessSteel, GTMaterials.Titanium, GTMaterials.TungstenSteel,
                GTMaterials.RhodiumPlatedPalladium, GTMaterials.NaquadahAlloy, GTMaterials.Darmstadtium,
                GTMaterials.Neutronium, GTMaterials.Tritanium, GTAAMaterials.METASTABLE_OGANESSON,
                GTAAMaterials.METASTABLE_OGANESSON, GTAAMaterials.METASTABLE_OGANESSON };
        var arms = java.util.List.of(GTItems.ROBOT_ARM_LV, GTItems.ROBOT_ARM_LV, GTItems.ROBOT_ARM_MV, GTItems.ROBOT_ARM_HV,
                GTItems.ROBOT_ARM_EV, GTItems.ROBOT_ARM_IV, GTItems.ROBOT_ARM_LuV, GTItems.ROBOT_ARM_ZPM, GTItems.ROBOT_ARM_UV,
                GTItems.ROBOT_ARM_UHV, GTItems.ROBOT_ARM_UEV, GTItems.ROBOT_ARM_UIV, GTItems.ROBOT_ARM_UXV, GTItems.ROBOT_ARM_OpV);
        var conveyors = java.util.List.of(GTItems.CONVEYOR_MODULE_LV, GTItems.CONVEYOR_MODULE_LV, GTItems.CONVEYOR_MODULE_MV,
                GTItems.CONVEYOR_MODULE_HV, GTItems.CONVEYOR_MODULE_EV, GTItems.CONVEYOR_MODULE_IV, GTItems.CONVEYOR_MODULE_LuV,
                GTItems.CONVEYOR_MODULE_ZPM, GTItems.CONVEYOR_MODULE_UV, GTItems.CONVEYOR_MODULE_UHV, GTItems.CONVEYOR_MODULE_UEV,
                GTItems.CONVEYOR_MODULE_UIV, GTItems.CONVEYOR_MODULE_UXV, GTItems.CONVEYOR_MODULE_OpV);
        for (int t = LV; t <= OpV; t++) {
            ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:component_assembly_line_casing_" + VN[t].toLowerCase(java.util.Locale.ROOT))
                    .inputItems(plate, plates[t], 4)
                    .inputItems(arms.get(t), 2)
                    .inputItems(conveyors.get(t), 2)
                    .inputItems(CustomTags.CIRCUITS_ARRAY[t], 2)
                    .inputItems(frameGt, GTMaterials.TungstenSteel)
                    .outputItems(new net.minecraft.world.item.ItemStack(GTAABlocks.COAL_CASINGS.get(t).get(), 1))
                    .duration(400).EUt(VA[t]).save(provider);
        }
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:advanced_iridium_plated_casing")
                .inputItems(plate, GTMaterials.Iridium, 6).inputItems(frameGt, GTMaterials.Iridium)
                .outputItems(new net.minecraft.world.item.ItemStack(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get(), 2))
                .duration(100).EUt(VA[IV]).save(provider);
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:component_assembly_line")
                .inputItems(GTMultiMachines.ASSEMBLY_LINE.asStack(4))
                .inputItems(new net.minecraft.world.item.ItemStack(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get(), 16))
                .inputItems(GTItems.ROBOT_ARM_IV, 16)
                .inputItems(GTItems.CONVEYOR_MODULE_IV, 32)
                .inputItems(CustomTags.IV_CIRCUITS, 16)
                .inputItems(plateDense, GTMaterials.Iridium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64), GTMaterials.Lubricant.getFluid(4000))
                .inputFluids(GTAAMaterials.pw(3, 8000))
                .outputItems(GTAAMachines.COMPONENT_ASSEMBLY_LINE.asStack())
                .duration(2400).EUt(VA[IV]).save(provider);
    }

    private static void megaAndCrusher(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:mega_distillation_tower")
                .inputItems(GTMultiMachines.DISTILLATION_TOWER.asStack(16))
                .inputItems(GTItems.ELECTRIC_PUMP_ZPM, 16)
                .inputItems(CustomTags.ZPM_CIRCUITS, 16)
                .inputItems(GTItems.FIELD_GENERATOR_ZPM, 4)
                .inputItems(plateDense, GTMaterials.NaquadahAlloy, 16)
                .inputItems(pipeLargeFluid, GTMaterials.Polybenzimidazole, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .inputFluids(GTAAMaterials.pw(2, 8000))
                .outputItems(GTAAMachines.MEGA_DISTILLATION_TOWER.asStack())
                .duration(2400).EUt(VA[ZPM]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:structure_upgrader")
                .inputItems(GTItems.TERMINAL.asStack())
                .inputItems(GTItems.ROBOT_ARM_MV, 2)
                .inputItems(GTItems.CONVEYOR_MODULE_MV, 2)
                .inputItems(CustomTags.MV_CIRCUITS, 2)
                .inputItems(plate, GTMaterials.Aluminium, 4)
                .outputItems(com.omnicrox.gtaa.common.data.GTAAItems.STRUCTURE_UPGRADER.asStack())
                .duration(400).EUt(VA[MV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:extreme_entity_crusher")
                .inputItems(GTItems.ELECTRIC_MOTOR_MV, 4)
                .inputItems(CustomTags.MV_CIRCUITS, 4)
                .inputItems(net.minecraft.world.item.Items.IRON_SWORD, 4)
                .inputItems(GTBlocks.CASING_STEEL_SOLID.asItem(), 8)
                .inputItems(plate, GTMaterials.Steel, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 2))
                .outputItems(GTAAMachines.EXTREME_ENTITY_CRUSHER.asStack())
                .duration(400).EUt(VA[MV]).save(provider);

        // sigils for the crusher (not used up): Rare = every hostile infernal, Ultra / Infernal = better rank odds
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:elite_sigil")
                .inputItems(plate, GTMaterials.Gold, 4)
                .inputItems(net.minecraft.world.item.Items.BLAZE_ROD, 4)
                .inputItems(net.minecraft.world.item.Items.GHAST_TEAR, 1)
                .inputItems(CustomTags.MV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Redstone.getFluid(L * 4))
                .outputItems(com.omnicrox.gtaa.common.data.GTAAItems.ELITE_SIGIL.asStack())
                .duration(300).EUt(VA[MV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:ultra_sigil")
                .inputItems(com.omnicrox.gtaa.common.data.GTAAItems.ELITE_SIGIL.asStack())
                .inputItems(plate, GTMaterials.RoseGold, 4)
                .inputItems(net.minecraft.world.item.Items.WITHER_SKELETON_SKULL, 1)
                .inputItems(CustomTags.HV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Blaze.getFluid(L * 4))
                .outputItems(com.omnicrox.gtaa.common.data.GTAAItems.ULTRA_SIGIL.asStack())
                .duration(400).EUt(VA[HV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:infernal_sigil")
                .inputItems(com.omnicrox.gtaa.common.data.GTAAItems.ULTRA_SIGIL.asStack())
                .inputItems(plate, GTMaterials.Titanium, 4)
                .inputItems(net.minecraft.world.item.Items.NETHER_STAR, 1)
                .inputItems(CustomTags.EV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Blaze.getFluid(L * 8))
                .outputItems(com.omnicrox.gtaa.common.data.GTAAItems.INFERNAL_SIGIL.asStack())
                .duration(600).EUt(VA[EV]).save(provider);
    }
}
