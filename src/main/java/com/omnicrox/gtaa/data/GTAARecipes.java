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
import com.omnicrox.gtaa.common.data.GTAAMachines;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/** Default recipes. Packs can override any of them with KubeJS (ids are gtapexadditions:...). */
public class GTAARecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        fusion(provider);
        antimatter(provider);
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
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:fusion_coil_mk5")
                .inputItems(GTAABlocks.FUSION_COIL_MK4.asStack())
                .inputItems(wireGtHex, GTMaterials.RutheniumTriniumAmericiumNeutronate, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 32))
                .outputItems(GTAABlocks.FUSION_COIL_MK5.asStack())
                .duration(600).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:uev_fusion_reactor")
                .inputItems(GTAAMachines.FUSION_REACTOR[UHV].asStack())
                .inputItems(GTAABlocks.FUSION_COIL_MK5.asStack(), 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(GTItems.FIELD_GENERATOR_UEV, 4)
                .inputItems(plateDense, GTMaterials.Neutronium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .outputItems(GTAAMachines.FUSION_REACTOR[UEV].asStack())
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
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:protomatter_activation_coil")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(wireGtQuadruple, GTMaterials.RutheniumTriniumAmericiumNeutronate, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UHV)
                .inputItems(CustomTags.UHV_CIRCUITS)
                .outputItems(GTAABlocks.PROTOMATTER_ACTIVATION_COIL.asStack())
                .duration(400).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:antimatter_annihilation_matrix")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(plateDense, GTMaterials.Neutronium, 2)
                .inputItems(GTItems.FIELD_GENERATOR_UV)
                .inputItems(GTItems.GRAVI_STAR)
                .outputItems(GTAABlocks.ANTIMATTER_ANNIHILATION_MATRIX.asStack())
                .duration(300).EUt(VA[UHV]).save(provider);

        // --- hatch + controllers ---
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:antimatter_hatch")
                .inputItems(GTMachines.FLUID_EXPORT_HATCH[UHV].asStack())
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(plateDense, GTMaterials.Neutronium, 8)
                .inputItems(CustomTags.UEV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(L * 16))
                .outputItems(GTAAMachines.ANTIMATTER_HATCH.asStack())
                .duration(600).EUt(VA[UEV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:antimatter_forge")
                .inputItems(GTAABlocks.ANTIMATTER_CONTAINMENT_CASING.asStack(16))
                .inputItems(GTAABlocks.PROTOMATTER_ACTIVATION_COIL.asStack(8))
                .inputItems(GTItems.FIELD_GENERATOR_UEV, 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(GTItems.GRAVI_STAR, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .outputItems(GTAAMachines.ANTIMATTER_FORGE.asStack())
                .duration(2400).EUt(VA[UEV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:antimatter_generator")
                .inputItems(GTAABlocks.ANTIMATTER_ANNIHILATION_MATRIX.asStack(16))
                .inputItems(GTAABlocks.PROTOMATTER_ACTIVATION_COIL.asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_UEV, 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(GTItems.EMITTER_UEV, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .outputItems(GTAAMachines.ANTIMATTER_GENERATOR.asStack())
                .duration(2400).EUt(VA[UEV]).save(provider);

        // --- fluids: MK IV / MK V fusion ---
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
        FUSION_RECIPES.recipeBuilder("gtapexadditions:protomatter")
                .inputFluids(GTMaterials.Neutronium.getFluid(L), GTMaterials.Helium3.getFluid(1000))
                .outputFluids(GTAAMaterials.PROTOMATTER.getFluid(1000))
                .duration(100).EUt(VA[UEV]).fusionStartEU(10_000_000_000L).save(provider);
        FUSION_RECIPES.recipeBuilder("gtapexadditions:antimatter_seed")
                .inputFluids(GTAAMaterials.PROTOMATTER.getFluid(1000), GTAAMaterials.SPACETIME.getFluid(L))
                .outputFluids(GTAAMaterials.ANTIMATTER.getFluid(16))
                .duration(400).EUt(VA[UEV]).fusionStartEU(15_000_000_000L).save(provider);

        // --- activation stabilizers (placeholder until the Naquadah reactor line exists) ---
        MIXER_RECIPES.recipeBuilder("gtapexadditions:naquadah_fuel_mkv_depleted")
                .inputItems(dust, GTMaterials.Naquadria, 4)
                .inputItems(dust, GTMaterials.NaquadahEnriched, 4)
                .inputFluids(GTMaterials.Radon.getFluid(1000))
                .outputFluids(GTAAMaterials.NAQUADAH_FUEL_MKV_DEPLETED.getFluid(1000))
                .duration(400).EUt(VA[UHV]).save(provider);
        MIXER_RECIPES.recipeBuilder("gtapexadditions:naquadah_fuel_mkvi_depleted")
                .inputItems(dust, GTMaterials.Neutronium, 1)
                .inputFluids(GTAAMaterials.NAQUADAH_FUEL_MKV_DEPLETED.getFluid(1000))
                .outputFluids(GTAAMaterials.NAQUADAH_FUEL_MKVI_DEPLETED.getFluid(1000))
                .duration(400).EUt(VA[UEV]).save(provider);
    }
}
