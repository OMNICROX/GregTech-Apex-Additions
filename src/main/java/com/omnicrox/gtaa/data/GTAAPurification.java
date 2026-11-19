package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import com.omnicrox.gtaa.common.machine.multiblock.purification.PurificationUnitMachine;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.omnicrox.gtaa.common.data.GTAAMaterials.*;

/** Water purification recipes (GT5-Unofficial PurifiedWaterRecipes), adapted to the simplified units. */
public final class GTAAPurification {

    private GTAAPurification() {}

    private static final int CYCLE = 120 * 20;

    private static GTRecipeBuilder unit(int grade, String name, int chance, long eut) {
        return GTAARecipeTypes.PURIFICATION[grade].recipeBuilder("gtapexadditions:purification/" + name)
                .addData(PurificationUnitMachine.CHANCE, chance)
                .duration(CYCLE).EUt(eut);
    }

    private static void casing(Consumer<FinishedRecipe> provider, Block block, Material plate, Material frame, int tier) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:" + net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(block).getPath())
                .inputItems(com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate, plate, 6).inputItems(frameGt, frame)
                .outputItems(new ItemStack(block, 2)).duration(100).EUt(VA[tier]).save(provider);
    }

    private static com.gregtechceu.gtceu.api.data.tag.TagPrefix plate(Material m) {
        return plate;
    }

    private static void glass(Consumer<FinishedRecipe> provider, Block block, Material mat, int tier) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:" + net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(block).getPath())
                .inputItems(GTBlocks.CASING_LAMINATED_GLASS.asItem(), 2).inputItems(com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate, mat, 2)
                .outputItems(new ItemStack(block, 2)).duration(100).EUt(VA[tier]).save(provider);
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        // ---------------- consumables ----------------
        CHEMICAL_RECIPES.recipeBuilder("gtapexadditions:purification/pre_activated_carbon")
                .inputItems(dust, Carbon).inputFluids(PhosphoricAcid.getFluid(1000))
                .outputItems(dust, PRE_ACTIVATED_CARBON).duration(100).EUt(VA[LuV]).save(provider);
        FURNACE_RECIPES.recipeBuilder("gtapexadditions:purification/dirty_activated_carbon")
                .inputItems(dust, PRE_ACTIVATED_CARBON).outputItems(dust, DIRTY_ACTIVATED_CARBON)
                .duration(200).EUt(VA[EV]).save(provider);
        CHEMICAL_RECIPES.recipeBuilder("gtapexadditions:purification/activated_carbon")
                .inputItems(dust, DIRTY_ACTIVATED_CARBON).inputFluids(Water.getFluid(1000))
                .outputItems(dust, ACTIVATED_CARBON).outputFluids(PhosphoricAcid.getFluid(1000))
                .duration(40).EUt(VA[IV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:purification/activated_carbon_filter_mesh")
                .inputItems(dust, ACTIVATED_CARBON, 64).inputItems(foil, Zinc, 16)
                .outputItems(GTAAItems.ACTIVATED_CARBON_FILTER_MESH.asStack())
                .duration(200).EUt(VA[IV]).save(provider);
        LASER_ENGRAVER_RECIPES.recipeBuilder("gtapexadditions:purification/ozone")
                .notConsumable(lens, Glass).inputFluids(Air.getFluid(10000))
                .outputFluids(OZONE.getFluid(2000)).duration(20).EUt(VA[LuV]).save(provider);
        CHEMICAL_RECIPES.recipeBuilder("gtapexadditions:purification/polyaluminium_chloride")
                .inputItems(dust, Aluminium, 2).inputFluids(HydrochloricAcid.getFluid(3000))
                .outputFluids(POLYALUMINIUM_CHLORIDE.getFluid(1000), Water.getFluid(3000))
                .duration(80).EUt(VA[EV]).save(provider);
        CENTRIFUGE_RECIPES.recipeBuilder("gtapexadditions:purification/flocculation_waste")
                .inputFluids(FLOCCULATION_WASTE.getFluid(10000))
                .outputItems(dust, Aluminium, 2).outputFluids(HydrochloricAcid.getFluid(3000))
                .duration(20).EUt(VA[EV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:purification/quark_creation_catalyst")
                .inputItems(plate, Neutronium, 4).inputItems(GTItems.EMITTER_UHV).inputItems(CustomTags.UHV_CIRCUITS)
                .inputFluids(SolderingAlloy.getFluid(144))
                .outputItems(GTAAItems.QUARK_CREATION_CATALYST.asStack(4))
                .duration(400).EUt(VA[UHV]).save(provider);

        // ---------------- the eight grades ----------------
        unit(1, "grade_1", 70, VA[LuV])
                .inputItems(GTAAItems.ACTIVATED_CARBON_FILTER_MESH.asStack())
                .inputFluids(Water.getFluid(1000))
                .outputFluids(PURIFIED_WATER[1].getFluid(900))
                .chancedOutput(dust, Stone, 1, 5000, 0)
                .chancedOutput(nugget, Gold, 1, 1000, 0)
                .save(provider);
        int[] ozone = { 128_000, 256_000, 512_000, 1_000_000 };
        for (int t = 0; t < 4; t++) {
            unit(2, "grade_2_" + (t + 1), 20 * (t + 1), VA[LuV])
                    .circuitMeta(t + 1)
                    .inputFluids(PURIFIED_WATER[1].getFluid(1000), OZONE.getFluid(ozone[t]))
                    .outputFluids(PURIFIED_WATER[2].getFluid(900))
                    .chancedOutput(dust, Manganese, 1, 2500, 0).chancedOutput(dust, Iron, 1, 2500, 0)
                    .chancedOutput(dust, Sulfur, 1, 2500, 0)
                    .save(provider);
        }
        unit(3, "grade_3", 90, VA[ZPM])
                .circuitMeta(1)
                .inputFluids(PURIFIED_WATER[2].getFluid(1000), POLYALUMINIUM_CHLORIDE.getFluid(100_000))
                .outputFluids(PURIFIED_WATER[3].getFluid(900), FLOCCULATION_WASTE.getFluid(100_000))
                .save(provider);
        unit(3, "grade_3_no_pac", 10, VA[ZPM])
                .circuitMeta(2)
                .inputFluids(PURIFIED_WATER[2].getFluid(1000))
                .outputFluids(PURIFIED_WATER[3].getFluid(900))
                .save(provider);
        unit(4, "grade_4", 95, VA[ZPM])
                .inputItems(dust, SodiumHydroxide, 64)
                .inputFluids(PURIFIED_WATER[3].getFluid(1000), HydrochloricAcid.getFluid(1000))
                .outputFluids(PURIFIED_WATER[4].getFluid(900))
                .save(provider);
        unit(5, "grade_5", 95, VA[UV])
                .inputFluids(PURIFIED_WATER[4].getFluid(1000), Helium.getFluid(FluidStorageKeys.PLASMA, 100),
                        Helium.getFluid(FluidStorageKeys.LIQUID, 1000))
                .outputFluids(PURIFIED_WATER[5].getFluid(900))
                .save(provider);
        var uv = unit(6, "grade_6", 95, VA[UV]);
        for (Material m : new Material[] { Glass, Diamond, Ruby, Emerald, Sapphire, GreenSapphire, Topaz, BlueTopaz, Amethyst })
            uv.notConsumable(lens, m);
        uv.inputFluids(PURIFIED_WATER[5].getFluid(1000)).outputFluids(PURIFIED_WATER[6].getFluid(900)).save(provider);
        unit(7, "grade_7", 95, VA[UHV])
                .notConsumable(wireGtSingle, RutheniumTriniumAmericiumNeutronate)
                .inputFluids(PURIFIED_WATER[6].getFluid(1000), Helium.getFluid(10_000), Neon.getFluid(5_000),
                        Krypton.getFluid(2_500))
                .outputFluids(PURIFIED_WATER[7].getFluid(900))
                .save(provider);
        unit(8, "grade_8", 95, VA[UEV])
                .inputItems(GTAAItems.QUARK_CREATION_CATALYST.asStack())
                .inputFluids(PURIFIED_WATER[7].getFluid(1000), Neutronium.getFluid(144))
                .outputFluids(PURIFIED_WATER[8].getFluid(900))
                .chancedOutput(GTAAItems.QUARK_CREATION_CATALYST.asStack(), 5000, 0)
                .save(provider);

        // ---------------- blocks ----------------
        casing(provider, GTAABlocks.PUR_CONCRETE.get(), Steel, Steel, HV);
        casing(provider, GTAABlocks.PUR_INDUSTRIAL_CASING.get(), StainlessSteel, Tungsten, IV);
        casing(provider, GTAABlocks.PUR_STERILE_CASING.get(), Polybenzimidazole, Iridium, LuV);
        casing(provider, GTAABlocks.PUR_FLOCCULATION_CASING.get(), Polytetrafluoroethylene, Iridium, ZPM);
        casing(provider, GTAABlocks.PUR_NAQUADAH_CASING.get(), Naquadah, NaquadahAlloy, ZPM);
        casing(provider, GTAABlocks.PUR_CORROSION_CASING.get(), Polytetrafluoroethylene, NaquadahAlloy, ZPM);
        casing(provider, GTAABlocks.PUR_PRESSURE_CASING.get(), TungstenSteel, TungstenSteel, LuV);
        casing(provider, GTAABlocks.PUR_OZONE_CASING.get(), Titanium, TungstenSteel, LuV);
        casing(provider, GTAABlocks.PUR_PLASMA_HEATING_CASING.get(), Darmstadtium, NaquadahAlloy, UV);
        casing(provider, GTAABlocks.PUR_NAQUADRIA_CASING.get(), Naquadria, Neutronium, UV);
        casing(provider, GTAABlocks.PUR_UV_EMITTER.get(), Duranium, Neutronium, UV);
        casing(provider, GTAABlocks.PUR_QUARK_PIPE.get(), Neutronium, Neutronium, UEV);
        casing(provider, GTAABlocks.PUR_QUARK_RELEASE.get(), Tritanium, Neutronium, UEV);
        casing(provider, GTAABlocks.PUR_QUARK_CONTAINMENT.get(), Neutronium, Tritanium, UEV);
        glass(provider, GTAABlocks.PUR_PH_GLASS.get(), Polytetrafluoroethylene, ZPM);
        glass(provider, GTAABlocks.PUR_UV_GLASS.get(), Neutronium, UV);
        glass(provider, GTAABlocks.PUR_INFINITY_GLASS.get(), Tritanium, UHV);
        glass(provider, GTAABlocks.PUR_QUARK_GLASS.get(), com.omnicrox.gtaa.common.data.GTAAMaterials.METASTABLE_OGANESSON, UEV);

        // ---------------- controllers ----------------
        Object[][] ctrl = {
                { GTAAMachines.PURIFICATION_PLANT, LuV, GTAABlocks.PUR_INDUSTRIAL_CASING.get() },
                { GTAAMachines.PURIFICATION_CLARIFIER, LuV, GTAABlocks.PUR_STERILE_CASING.get() },
                { GTAAMachines.PURIFICATION_OZONATION, LuV, GTAABlocks.PUR_OZONE_CASING.get() },
                { GTAAMachines.PURIFICATION_FLOCCULATION, ZPM, GTAABlocks.PUR_FLOCCULATION_CASING.get() },
                { GTAAMachines.PURIFICATION_PH, ZPM, GTAABlocks.PUR_NAQUADAH_CASING.get() },
                { GTAAMachines.PURIFICATION_PLASMA, UV, GTAABlocks.PUR_PLASMA_HEATING_CASING.get() },
                { GTAAMachines.PURIFICATION_UV, UV, GTAABlocks.PUR_NAQUADRIA_CASING.get() },
                { GTAAMachines.PURIFICATION_DEGASSER, UHV, GTAABlocks.PUR_PLASMA_HEATING_CASING.get() },
                { GTAAMachines.PURIFICATION_BARYONIC, UEV, GTAABlocks.PUR_QUARK_CONTAINMENT.get() } };
        var pumps = java.util.List.of(GTItems.ELECTRIC_PUMP_LuV, GTItems.ELECTRIC_PUMP_ZPM, GTItems.ELECTRIC_PUMP_UV,
                GTItems.ELECTRIC_PUMP_UHV, GTItems.ELECTRIC_PUMP_UEV);
        for (Object[] c : ctrl) {
            var def = (com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition) c[0];
            int tier = (int) c[1];
            ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:" + def.getId().getPath())
                    .inputItems(new ItemStack((Block) c[2], 16))
                    .inputItems(pumps.get(tier - LuV), 8)
                    .inputItems(CustomTags.CIRCUITS_ARRAY[tier], 8)
                    .inputItems(GTItems.FIELD_GENERATOR_LuV.get(), 2)
                    .inputItems(plateDense, tier >= UV ? Neutronium : NaquadahAlloy, 8)
                    .inputFluids(SolderingAlloy.getFluid(144 * 16))
                    .outputItems(def.asStack())
                    .duration(1200).EUt(VA[tier]).save(provider);
        }
    }
}
