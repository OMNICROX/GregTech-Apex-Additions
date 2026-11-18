package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;

/** Default recipes. Packs can override any of them with KubeJS (ids are gtapexadditions:...). */
public class GTAARecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        fusion(provider);
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
}
