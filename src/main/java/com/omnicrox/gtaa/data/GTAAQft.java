package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import com.omnicrox.gtaa.common.machine.multiblock.QuantumForceTransformerMachine;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/** Quantum Force Transformer recipes, adapted from GT++ RecipeLoaderChemicalSkips to GTCEu's processing lines. */
public final class GTAAQft {

    private GTAAQft() {}

    private static final int STACK = 64 * 144;

    private static GTRecipeBuilder qft(String name, String catalyst, int tier, int focus, long eut) {
        return GTAARecipeTypes.QUANTUM_FORCE_TRANSFORMER.recipeBuilder("gtapexadditions:qft/" + name)
                .addData(QuantumForceTransformerMachine.TIER, tier)
                .addData(QuantumForceTransformerMachine.FOCUS, focus)
                .addData(QuantumForceTransformerMachine.CATALYST, "gtapexadditions:" + catalyst + "_catalyst")
                .duration(400).EUt(eut);
    }

    private static void dusts(GTRecipeBuilder b, Material... mats) {
        int n = 0;
        for (Material m : mats) {
            if (m == null || ChemicalHelper.get(dust, m).isEmpty() || n >= 6) continue;
            b.outputItems(dust, m, 64);
            n++;
        }
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        // platline
        var b = qft("platline", "platinum_group", 1, 1, VA[UV]).inputItems(dust, PlatinumGroupSludge, 32);
        dusts(b, Platinum, Palladium, Iridium, Osmium, Rhodium, Ruthenium);
        b.save(provider);
        // early plastics
        qft("early_plastics", "plastic_polymer", 1, 1, VA[ZPM])
                .inputItems(dust, Carbon, 64)
                .inputFluids(Hydrogen.getFluid(16000), Chlorine.getFluid(16000), Fluorine.getFluid(16000), Oxygen.getFluid(16000))
                .outputFluids(Polyethylene.getFluid(4 * STACK), PolyvinylChloride.getFluid(2 * STACK),
                        Polytetrafluoroethylene.getFluid(2 * STACK), Epoxy.getFluid(STACK), Polybenzimidazole.getFluid(STACK))
                .save(provider);
        // rubbers
        qft("rubbers", "rubber_polymer", 1, 1, VA[ZPM])
                .inputItems(dust, Sulfur, 32).inputItems(dust, Carbon, 32)
                .inputFluids(Hydrogen.getFluid(16000), Oxygen.getFluid(16000))
                .outputFluids(SiliconeRubber.getFluid(STACK), StyreneButadieneRubber.getFluid(STACK),
                        PolyphenyleneSulfide.getFluid(2 * STACK), Rubber.getFluid(4 * STACK))
                .save(provider);
        // glue and solder
        qft("glue_solder", "adhesion_promoter", 1, 1, VA[UV])
                .inputItems(dust, Carbon, 32).inputItems(dust, Bismuth, 32)
                .inputFluids(Oxygen.getFluid(10000), Hydrogen.getFluid(10000))
                .outputFluids(Glue.getFluid(32000), SolderingAlloy.getFluid(2 * STACK))
                .save(provider);
        // titanium / tungsten / indium
        b = qft("titanium_tungsten_indium", "tita_tungsten_indium", 1, 1, VA[UV])
                .inputItems(dust, Lead, 16).inputItems(dust, Bauxite, 32).inputItems(dust, Tungstate, 16);
        dusts(b, Titanium, TungstenSteel, TungstenCarbide, Indium);
        b.save(provider);
        b = qft("titanium_tungsten_tantalum", "tita_tungsten_indium", 1, 1, VA[UV])
                .inputItems(dust, Rutile, 32).inputItems(dust, Scheelite, 16).inputItems(dust, Ilmenite, 16);
        dusts(b, Titanium, TungstenSteel, Tantalum, Rhenium, Niobium, Hafnium);
        b.save(provider);
        // radioactive
        b = qft("radioactive", "radioactivity", 1, 1, VA[UV])
                .inputItems(dust, Thorium, 32).inputItems(dust, Uranium238, 32);
        dusts(b, Thorium, Uranium238, Uranium235, Plutonium239, Plutonium241);
        b.save(provider);
        // rare earths (monaline)
        b = qft("monaline", "rare_earth_group", 2, 1, VA[UHV]).inputItems(dust, Monazite, 32);
        dusts(b, Cerium, Gadolinium, Samarium, Lanthanum, Neodymium, Europium);
        b.save(provider);
        b = qft("bastnasite", "rare_earth_group", 2, 1, VA[UHV]).inputItems(dust, Bastnasite, 32);
        dusts(b, Cerium, Lanthanum, Neodymium, Yttrium, Promethium, Lutetium);
        b.save(provider);
        // naquadah lines
        b = qft("simple_naquadah", "simple_naquadah", 2, 2, VA[UHV]).inputItems(dust, Naquadah, 32)
                .inputFluids(FluoroantimonicAcid.getFluid(8000));
        dusts(b, NaquadahEnriched, Naquadria, Trinium, Duranium, Indium, Gallium);
        b.save(provider);
        b = qft("advanced_naquadah", "advanced_naquadah", 3, 2, VA[UEV]).inputItems(dust, Naquadah, 64)
                .inputItems(dust, GTAAMaterials.EXTREMELY_UNSTABLE_NAQUADAH, 8)
                .inputFluids(FluoroantimonicAcid.getFluid(16000));
        dusts(b, Naquadria, Trinium, Tritanium, Neutronium, GTAAMaterials.ATOMIC_SEPARATION_CATALYST, Darmstadtium);
        b.save(provider);
        // late plastics
        qft("ultimate_plastics", "ultimate_plastic", 3, 2, VA[UEV])
                .inputItems(dust, Carbon, 64).inputItems(dust, Silicon, 32)
                .inputFluids(Hydrogen.getFluid(32000), Fluorine.getFluid(32000), Oxygen.getFluid(32000), Nitrogen.getFluid(32000))
                .outputFluids(Polybenzimidazole.getFluid(8 * STACK), Polytetrafluoroethylene.getFluid(8 * STACK),
                        Epoxy.getFluid(8 * STACK), SiliconeRubber.getFluid(8 * STACK))
                .save(provider);

        // ---------------- catalysts (one-time, never used up) ----------------
        Object[][] cats = {
                { "platinum_group", new Material[] { Platinum, Palladium, Iridium, Osmium }, UV },
                { "plastic_polymer", new Material[] { Polyethylene, PolyvinylChloride, Polytetrafluoroethylene, Carbon }, ZPM },
                { "rubber_polymer", new Material[] { Rubber, SiliconeRubber, StyreneButadieneRubber, Sulfur }, ZPM },
                { "adhesion_promoter", new Material[] { Bismuth, Tin, Lead, Carbon }, UV },
                { "tita_tungsten_indium", new Material[] { Titanium, Tungsten, Indium, Tantalum }, UV },
                { "radioactivity", new Material[] { Uranium235, Plutonium241, Thorium, Lead }, UV },
                { "rare_earth_group", new Material[] { Cerium, Neodymium, Samarium, Europium }, UHV },
                { "simple_naquadah", new Material[] { Naquadah, NaquadahEnriched, Naquadria, Trinium }, UHV },
                { "advanced_naquadah", new Material[] { Naquadria, Trinium, Tritanium, Neutronium }, UEV },
                { "ultimate_plastic", new Material[] { Polybenzimidazole, Polytetrafluoroethylene, Epoxy, Neutronium }, UEV } };
        for (Object[] c : cats) {
            String name = (String) c[0];
            Material[] mats = (Material[]) c[1];
            int tier = (int) c[2];
            var rb = ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:qft_catalyst/" + name);
            for (Material m : mats) {
                if (!ChemicalHelper.get(dust, m).isEmpty()) rb.inputItems(dust, m, 16);
                else rb.inputFluids(m.getFluid(16 * 144));
            }
            rb.inputItems(CustomTags.CIRCUITS_ARRAY[tier], 2)
                    .outputItems(GTAAItems.QFT_CATALYSTS.get(name).asStack())
                    .duration(400).EUt(VA[tier]).save(provider);
        }

        // ---------------- casings ----------------
        var manip = GTAABlocks.qftManipulators();
        var shield = GTAABlocks.qftShieldings();
        Material[] tierPlate = { Neutronium, Tritanium, GTAAMaterials.METASTABLE_OGANESSON, GTAAMaterials.METASTABLE_OGANESSON };
        var gens = java.util.List.of(GTItems.FIELD_GENERATOR_UHV, GTItems.FIELD_GENERATOR_UEV, GTItems.FIELD_GENERATOR_UIV,
                GTItems.FIELD_GENERATOR_UXV);
        var emitters = java.util.List.of(GTItems.EMITTER_UHV, GTItems.EMITTER_UEV, GTItems.EMITTER_UIV, GTItems.EMITTER_UXV);
        for (int t = 0; t < 4; t++) {
            var m = ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:qft_manipulator_t" + (t + 1));
            if (t > 0) m.inputItems(manip[t - 1].asItem());
            m.inputItems(plateDense, tierPlate[t], 2).inputItems(gens.get(t)).inputItems(CustomTags.CIRCUITS_ARRAY[UHV + t], 2)
                    .outputItems(new ItemStack(manip[t], 1)).duration(400).EUt(VA[UHV + t]).save(provider);
            var s = ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:qft_shielding_t" + (t + 1));
            if (t > 0) s.inputItems(shield[t - 1].asItem());
            s.inputItems(plateDense, tierPlate[t], 2).inputItems(emitters.get(t)).inputItems(CustomTags.CIRCUITS_ARRAY[UHV + t], 2)
                    .outputItems(new ItemStack(shield[t], 1)).duration(400).EUt(VA[UHV + t]).save(provider);
        }
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:qft_containment_casing")
                .inputItems(plate, Neutronium, 6).inputItems(frameGt, NaquadahAlloy)
                .outputItems(new ItemStack(GTAABlocks.QFT_CONTAINMENT_CASING.get(), 2)).duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:qft_coil")
                .inputItems(wireGtQuadruple, RutheniumTriniumAmericiumNeutronate, 4).inputItems(frameGt, Neutronium)
                .inputItems(plate, Tritanium, 4)
                .outputItems(new ItemStack(GTAABlocks.QFT_COIL.get(), 1)).duration(200).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:force_field_glass")
                .inputItems(com.gregtechceu.gtceu.common.data.GTBlocks.FUSION_GLASS.asItem(), 2)
                .inputItems(plate, Tritanium, 2).inputItems(GTItems.FIELD_GENERATOR_UV)
                .outputItems(new ItemStack(GTAABlocks.FORCE_FIELD_GLASS.get(), 2)).duration(200).EUt(VA[UHV]).save(provider);
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:quantum_force_transformer")
                .inputItems(com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.LARGE_CHEMICAL_REACTOR.asStack(4))
                .inputItems(new ItemStack(manip[0], 4)).inputItems(new ItemStack(shield[0], 4))
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 8).inputItems(GTItems.EMITTER_UHV, 8)
                .inputItems(CustomTags.UHV_CIRCUITS, 16).inputItems(plateDense, Neutronium, 16)
                .inputFluids(SolderingAlloy.getFluid(144 * 64), Polybenzimidazole.getFluid(144 * 32))
                .inputFluids(GTAAMaterials.pw(4, 8000))
                .outputItems(GTAAMachines.QUANTUM_FORCE_TRANSFORMER.asStack())
                .duration(4800).EUt(VA[UHV]).save(provider);
    }
}
