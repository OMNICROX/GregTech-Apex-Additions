package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAGTNHMachines;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/** Recipes for the GTNH-port multis and their parts. */
public final class GTAAGTNHRecipes {

    private GTAAGTNHRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        rockBreaker(provider);
        superWater(provider);
        controllers(provider);
        tstControllers(provider);
        parts(provider);
    }

    private static void rockBreaker(Consumer<FinishedRecipe> provider) {
        var t = GTAARecipeTypes.INDUSTRIAL_ROCK_BREAKER;
        t.recipeBuilder("gtapexadditions:irb_cobblestone").circuitMeta(1).outputItems(Items.COBBLESTONE)
                .duration(16).EUt(VA[LV]).save(provider);
        t.recipeBuilder("gtapexadditions:irb_stone").circuitMeta(2).outputItems(Items.STONE)
                .duration(16).EUt(VA[LV]).save(provider);
        t.recipeBuilder("gtapexadditions:irb_obsidian").circuitMeta(3).inputItems(dust, GTMaterials.Redstone)
                .outputItems(Items.OBSIDIAN).duration(128).EUt(VA[MV]).save(provider);
        t.recipeBuilder("gtapexadditions:irb_basalt").circuitMeta(4).outputItems(Items.BASALT)
                .duration(16).EUt(VA[MV]).save(provider);
        t.recipeBuilder("gtapexadditions:irb_deepslate").circuitMeta(5).outputItems(Items.COBBLED_DEEPSLATE)
                .duration(16).EUt(VA[MV]).save(provider);
        t.recipeBuilder("gtapexadditions:irb_netherrack").circuitMeta(6).notConsumable(Items.NETHERRACK)
                .outputItems(Items.NETHERRACK).duration(16).EUt(VA[MV]).save(provider);
        t.recipeBuilder("gtapexadditions:irb_end_stone").circuitMeta(7).notConsumable(Items.END_STONE)
                .outputItems(Items.END_STONE).duration(16).EUt(VA[HV]).save(provider);
    }

    private static void controllers(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:industrial_rock_breaker")
                .inputItems(GTMachines.ROCK_CRUSHER[MV].asStack(2))
                .inputItems(GTItems.ELECTRIC_PISTON_MV, 4)
                .inputItems(CustomTags.MV_CIRCUITS, 2)
                .inputItems(GTBlocks.CASING_STEEL_SOLID.asItem(), 4)
                .inputItems(gear, GTMaterials.Steel, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 2))
                .outputItems(GTAAGTNHMachines.INDUSTRIAL_ROCK_BREAKER.asStack())
                .duration(400).EUt(VA[MV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:circuit_assembly_line")
                .inputItems(GTMultiMachines.ASSEMBLY_LINE.asStack())
                .inputItems(GTMachines.CIRCUIT_ASSEMBLER[LuV].asStack(2))
                .inputItems(GTItems.ROBOT_ARM_LuV, 4)
                .inputItems(GTItems.CONVEYOR_MODULE_LuV, 4)
                .inputItems(CustomTags.LuV_CIRCUITS, 4)
                .inputItems(plateDense, GTMaterials.HSSS, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                .outputItems(GTAAGTNHMachines.CIRCUIT_ASSEMBLY_LINE.asStack())
                .duration(1200).EUt(VA[LuV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:electric_implosion_compressor")
                .inputItems(GTMultiMachines.IMPLOSION_COMPRESSOR.asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_UV, 4)
                .inputItems(GTItems.EMITTER_UV, 4)
                .inputItems(CustomTags.UV_CIRCUITS, 4)
                .inputItems(plateDense, GTMaterials.Neutronium, 4)
                .inputItems(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.asItem(), 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .outputItems(GTAAGTNHMachines.ELECTRIC_IMPLOSION_COMPRESSOR.asStack())
                .duration(1600).EUt(VA[UV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:nanochip_assembly_complex")
                .inputItems(GTMachines.CIRCUIT_ASSEMBLER[UV].asStack(4))
                .inputItems(GTItems.ROBOT_ARM_UV, 8)
                .inputItems(GTItems.SENSOR_UV, 4)
                .inputItems(CustomTags.UHV_CIRCUITS, 4)
                .inputItems(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING.asItem(), 16)
                .inputItems(plateDense, GTMaterials.Neutronium, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .outputItems(GTAAGTNHMachines.NANOCHIP_ASSEMBLY_COMPLEX.asStack())
                .duration(2400).EUt(VA[UV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:miracle_top")
                .inputItems(GTMachines.CIRCUIT_ASSEMBLER[UV].asStack(8))
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(GTItems.EMITTER_UHV, 8)
                .inputItems(CustomTags.UHV_CIRCUITS, 8)
                .inputItems(GTBlocks.FUSION_COIL.asItem(), 16)
                .inputItems(GTItems.GRAVI_STAR, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 128))
                .outputItems(GTAAGTNHMachines.MIRACLE_TOP.asStack())
                .duration(3200).EUt(VA[UHV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:black_hole_compressor")
                .inputItems(GTMachines.COMPRESSOR[UV].asStack(8))
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 8)
                .inputItems(GTItems.GRAVI_STAR, 8)
                .inputItems(CustomTags.UHV_CIRCUITS, 8)
                .inputItems(GTAABlocks.EXTREME_DENSITY_CASING.asItem(), 32)
                .inputItems(plateDense, GTMaterials.Neutronium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 128))
                .outputItems(GTAAGTNHMachines.BLACK_HOLE_COMPRESSOR.asStack())
                .duration(3200).EUt(VA[UHV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:plasma_forge_prototype_mk2")
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 16)
                .inputItems(GTItems.EMITTER_UHV, 16)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(GTBlocks.FUSION_CASING_MK3.asItem(), 64)
                .inputItems(GTItems.QUANTUM_STAR, 16)
                .inputItems(plateDense, GTMaterials.Neutronium, 32)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 256))
                .outputItems(GTAAGTNHMachines.PLASMA_FORGE_PROTOTYPE.asStack())
                .duration(6000).EUt(VA[UEV]).save(provider);
    }

    private static void parts(Consumer<FinishedRecipe> provider) {
        // Nanochip casings and modules
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:nanochip_mesh_interface_casing")
                .inputItems(plate, GTMaterials.Naquadria, 6).inputItems(frameGt, GTMaterials.NaquadahAlloy)
                .inputItems(CustomTags.ZPM_CIRCUITS).circuitMeta(6)
                .outputItems(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING.asStack(2)).duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:nanochip_reinforcement_casing")
                .inputItems(plate, GTMaterials.Tritanium, 6).inputItems(frameGt, GTMaterials.NaquadahAlloy).circuitMeta(6)
                .outputItems(GTAABlocks.NANOCHIP_REINFORCEMENT_CASING.asStack(2)).duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:nanochip_computational_matrix_casing")
                .inputItems(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING.asItem()).inputItems(CustomTags.LuV_CIRCUITS, 2)
                .inputItems(wireFine, GTMaterials.Europium, 8).circuitMeta(7)
                .outputItems(GTAABlocks.NANOCHIP_COMPUTATIONAL_MATRIX_CASING.asStack()).duration(100).EUt(VA[UV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:nanochip_firewall_projection_casing")
                .inputItems(GTAABlocks.NANOCHIP_REINFORCEMENT_CASING.asItem()).inputItems(GTItems.FIELD_GENERATOR_ZPM)
                .circuitMeta(8)
                .outputItems(GTAABlocks.NANOCHIP_FIREWALL_PROJECTION_CASING.asStack()).duration(100).EUt(VA[UV]).save(provider);
        Object[][] moduleParts = {
                { GTMachines.CIRCUIT_ASSEMBLER[UV].asStack(), CustomTags.UV_CIRCUITS },   // assembly matrix
                { GTItems.ROBOT_ARM_UV.asStack(2), CustomTags.ZPM_CIRCUITS },              // part processor
                { GTMachines.CIRCUIT_ASSEMBLER[ZPM].asStack(), CustomTags.ZPM_CIRCUITS },  // board processor
                { GTMachines.LASER_ENGRAVER[UV].asStack(), CustomTags.UV_CIRCUITS },        // etching array
                { GTMachines.CUTTER[UV].asStack(), CustomTags.ZPM_CIRCUITS },               // cutting chamber
                { GTMachines.WIREMILL[UV].asStack(), CustomTags.ZPM_CIRCUITS },             // wire tracer
                { GTItems.EMITTER_UV.asStack(4), CustomTags.UV_CIRCUITS },                  // optical organizer
                { GTItems.SENSOR_UV.asStack(4), CustomTags.UV_CIRCUITS },                   // biological coordination
                { GTMachines.PACKER[UV].asStack(), CustomTags.ZPM_CIRCUITS } };             // encasement wrapper
        for (int i = 0; i < GTAABlocks.NAC_MODULES.length; i++) {
            ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:nac_module_" + GTAABlocks.NAC_MODULE_IDS[i][0])
                    .inputItems(GTAABlocks.NANOCHIP_COMPUTATIONAL_MATRIX_CASING.asItem())
                    .inputItems((net.minecraft.world.item.ItemStack) moduleParts[i][0])
                    .inputItems((net.minecraft.tags.TagKey<net.minecraft.world.item.Item>) moduleParts[i][1], 2)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 8))
                    .outputItems(GTAABlocks.NAC_MODULES[i].asStack()).duration(400).EUt(VA[UV]).save(provider);
        }
        // Black hole compressor
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:extreme_density_casing")
                .inputItems(plate, GTMaterials.Neutronium, 6).inputItems(frameGt, GTMaterials.Tritanium).circuitMeta(6)
                .outputItems(GTAABlocks.EXTREME_DENSITY_CASING.asStack(2)).duration(100).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:radiation_absorbent_casing")
                .inputItems(plate, GTMaterials.Lead, 6).inputItems(plate, GTMaterials.Neutronium, 2)
                .inputItems(frameGt, GTMaterials.Tritanium).circuitMeta(6)
                .outputItems(GTAABlocks.RADIATION_ABSORBENT_CASING.asStack(2)).duration(100).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:hawking_glass")
                .inputItems(GTBlocks.FUSION_GLASS.asItem(), 2).inputItems(GTItems.GRAVI_STAR).circuitMeta(6)
                .outputItems(GTAABlocks.HAWKING_GLASS.asStack(2)).duration(100).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:black_hole_seed")
                .inputItems(GTItems.GRAVI_STAR, 4).inputItems(dust, GTMaterials.Neutronium, 16)
                .inputItems(CustomTags.UHV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 8))
                .outputItems(GTAAItems.BLACK_HOLE_SEED.asStack()).duration(1200).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:black_hole_collapser")
                .inputItems(GTItems.GRAVI_STAR, 1).inputItems(plate, GTMaterials.Neutronium, 8)
                .inputItems(CustomTags.UV_CIRCUITS, 2)
                .outputItems(GTAAItems.BLACK_HOLE_COLLAPSER.asStack()).duration(400).EUt(VA[UV]).save(provider);
    }
    private static void superWater(Consumer<FinishedRecipe> provider) {
        for (int g = 1; g <= 8; g++) {
            GTAARecipeTypes.SUPER_WATER_PURIFIER.recipeBuilder("gtapexadditions:super_water_purifier/grade_" + g)
                    .circuitMeta(g)
                    .inputFluids(GTMaterials.DistilledWater.getFluid(1000))
                    .outputFluids(com.omnicrox.gtaa.common.data.GTAAMaterials.pw(g, 1000))
                    .duration(400).EUt(VA[UHV]).save(provider);
        }
    }

    /** controllers of the Twist Space Technology ports */
    private static void tstControllers(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:silicon_rock_synthesizer")
                .inputItems(GTAAGTNHMachines.INDUSTRIAL_ROCK_BREAKER.asStack(4))
                .inputItems(GTItems.ELECTRIC_PISTON_IV, 8)
                .inputItems(GTItems.ELECTRIC_PUMP_IV, 4)
                .inputItems(CustomTags.IV_CIRCUITS, 4)
                .inputItems(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.asItem(), 16)
                .inputItems(frameGt, GTMaterials.TungstenSteel, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                .outputItems(GTAAGTNHMachines.SILICON_ROCK_SYNTHESIZER.asStack())
                .duration(1200).EUt(VA[IV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:super_water_purifier")
                .inputItems(com.omnicrox.gtaa.common.data.GTAAMachines.PURIFICATION_PLANT.asStack(1))
                .inputItems(GTItems.ELECTRIC_PUMP_UHV, 8)
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(CustomTags.UHV_CIRCUITS, 8)
                .inputItems(GTAABlocks.PUR_NAQUADAH_CASING.asItem(), 32)
                .inputItems(GTAABlocks.PUR_PLASMA_HEATING_CASING.asItem(), 3)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .outputItems(GTAAGTNHMachines.SUPER_WATER_PURIFIER.asStack())
                .duration(2400).EUt(VA[UHV]).save(provider);

        // Exothermic Hearth (GT5-Unofficial: ZPM assembly line; molten Blaze stands in for Pyrotheum)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:exothermic_hearth")
                .inputItems(GTMultiMachines.ELECTRIC_BLAST_FURNACE.asStack(64))
                .inputItems(com.gregtechceu.gtceu.common.data.machines.GCYMMachines.BLAST_ALLOY_SMELTER.asStack(8))
                .inputItems(CustomTags.UV_CIRCUITS, 8)
                .inputItems(GTItems.ELECTRIC_PUMP_ZPM, 8)
                .inputItems(GTItems.FLUID_REGULATOR_ZPM, 8)
                .inputItems(wireGtDouble, GTMaterials.UraniumRhodiumDinaquadide, 16)
                .inputItems(plateDense, GTMaterials.Invar, 8)
                .inputItems(plateDense, GTMaterials.Cupronickel, 8)
                .inputFluids(GTMaterials.Blaze.getFluid(64000))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 10))
                .inputFluids(GTMaterials.Lubricant.getFluid(16000))
                .outputItems(GTAAGTNHMachines.EXOTHERMIC_HEARTH.asStack())
                .duration(1200).EUt(VA[ZPM] / 2).save(provider);

        // General Ore Processing Factory (TST)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:ore_processing_factory")
                .inputItems(GTMachines.MACERATOR[IV].asStack(16))
                .inputItems(GTMachines.ORE_WASHER[IV].asStack(16))
                .inputItems(GTMachines.THERMAL_CENTRIFUGE[IV].asStack(16))
                .inputItems(GTMachines.CENTRIFUGE[IV].asStack(16))
                .inputItems(GTMachines.SIFTER[IV].asStack(16))
                .inputItems(GTItems.CONVEYOR_MODULE_LuV, 16)
                .inputItems(CustomTags.LuV_CIRCUITS, 16)
                .inputItems(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.asStack(64))
                .inputFluids(GTMaterials.Lubricant.getFluid(32000))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .outputItems(GTAAGTNHMachines.ORE_PROCESSING_FACTORY.asStack())
                .duration(2400).EUt(VA[LuV]).save(provider);
        // Integrated Assembly Matrix (TST)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:integrated_assembly_matrix")
                .inputItems(GTMultiMachines.ASSEMBLY_LINE.asStack(16))
                .inputItems(GTItems.ROBOT_ARM_UV, 16)
                .inputItems(GTItems.CONVEYOR_MODULE_UV, 16)
                .inputItems(GTItems.FIELD_GENERATOR_UV, 8)
                .inputItems(CustomTags.UHV_CIRCUITS, 8)
                .inputItems(GTBlocks.HIGH_POWER_CASING.asStack(64))
                .inputItems(GTBlocks.FUSION_GLASS.asStack(64))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 64))
                .outputItems(GTAAGTNHMachines.INTEGRATED_ASSEMBLY_MATRIX.asStack())
                .duration(2400).EUt(VA[UV]).save(provider);
        // Nether Interface (TST)
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:nether_interface")
                .inputItems(GTMachines.HULL[IV].asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_IV, 4)
                .inputItems(GTItems.EMITTER_IV, 4)
                .inputItems(CustomTags.IV_CIRCUITS, 8)
                .inputItems(net.minecraft.world.item.Items.OBSIDIAN, 64)
                .inputItems(net.minecraft.world.item.Items.NETHERITE_INGOT, 1)
                .inputItems(GTBlocks.CASING_TITANIUM_STABLE.asStack(32))
                .outputItems(GTAAGTNHMachines.NETHER_INTERFACE.asStack())
                .duration(1200).EUt(VA[IV]).save(provider);
        // what the Nether Interface brings back: 16 buckets of distilled water, 1A IV, a minute
        GTAARecipeTypes.NETHER_INTERFACE.recipeBuilder("gtapexadditions:nether_interface/hell")
                .inputFluids(GTMaterials.DistilledWater.getFluid(16000))
                .outputItems(net.minecraft.world.item.Items.NETHERRACK, 64)
                .outputItems(net.minecraft.world.item.Items.QUARTZ, 32)
                .outputItems(net.minecraft.world.item.Items.GLOWSTONE_DUST, 32)
                .chancedOutput(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.ANCIENT_DEBRIS, 2), 4900, 0)
                .chancedOutput(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.NETHERITE_SCRAP, 4), 3000, 0)
                .chancedOutput(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BLAZE_ROD, 16), 5000, 0)
                .chancedOutput(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.MAGMA_CREAM, 16), 5000, 0)
                .chancedOutput(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.NETHER_WART, 16), 5000, 0)
                .chancedOutput(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GHAST_TEAR, 4), 1000, 0)
                .outputFluids(GTMaterials.Lava.getFluid(16000))
                .duration(1200).EUt(VA[IV]).save(provider);

        // Spinmatron-2737 (GT5-Unofficial)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:spinmatron")
                .inputItems(com.gregtechceu.gtceu.common.data.machines.GCYMMachines.LARGE_CENTRIFUGE.asStack(16))
                .inputItems(GTMachines.CENTRIFUGE[ZPM].asStack(16))
                .inputItems(GTItems.ELECTRIC_MOTOR_UV, 16)
                .inputItems(CustomTags.UV_CIRCUITS, 8)
                .inputItems(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_VIBRATION_SAFE.asStack(64))
                .inputItems(block, GTMaterials.NaquadahAlloy, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .inputFluids(GTMaterials.Lubricant.getFluid(16000))
                .outputItems(GTAAGTNHMachines.SPINMATRON.asStack())
                .duration(2400).EUt(VA[UV]).save(provider);

        // Eden Garden (GT-Not-Leisure)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:eden_garden")
                .inputItems(GTMachines.HULL[ZPM].asStack(4))
                .inputItems(GTItems.ELECTRIC_PUMP_ZPM, 16)
                .inputItems(GTItems.ROBOT_ARM_ZPM, 8)
                .inputItems(CustomTags.UV_CIRCUITS, 8)
                .inputItems(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_WATERTIGHT.asStack(64))
                .inputItems(net.minecraft.world.item.Items.GLOWSTONE, 64)
                .inputItems(net.minecraft.world.item.Items.WHEAT_SEEDS, 64)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .inputFluids(GTMaterials.Water.getFluid(64000))
                .outputItems(GTAAGTNHMachines.EDEN_GARDEN.asStack())
                .duration(2400).EUt(VA[ZPM]).save(provider);

        // Extreme Craft Center: TST uses molecular assemblers + extreme auto crafters + a molecular transformer
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:extreme_craft_center")
                .inputItems(appeng.core.definitions.AEBlocks.MOLECULAR_ASSEMBLER.stack(16))
                .inputItems(appeng.core.definitions.AEBlocks.PATTERN_PROVIDER.stack(4))
                .inputItems(GTMachines.ASSEMBLER[IV].asStack(2))
                .inputItems(GTItems.ROBOT_ARM_IV, 4)
                .inputItems(CustomTags.IV_CIRCUITS, 4)
                .inputItems(GTBlocks.HIGH_POWER_CASING.asItem(), 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                .outputItems(GTAAGTNHMachines.EXTREME_CRAFT_CENTER.asStack())
                .duration(1200).EUt(VA[IV]).save(provider);
    }

}
