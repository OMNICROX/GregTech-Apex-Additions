package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.omnicrox.gtaa.api.GTAAStructures;
import com.omnicrox.gtaa.common.machine.multiblock.AntimatterForgeMachine;
import com.omnicrox.gtaa.common.machine.multiblock.AntimatterGeneratorMachine;
import com.omnicrox.gtaa.common.machine.part.AntimatterHatchPartMachine;
import net.minecraft.world.level.block.Blocks;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderHelper;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.machine.multiblock.AdvancedFusionReactorMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.FUSION_GLASS;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.BATCH_MODE;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.DEFAULT_ENVIRONMENT_REQUIREMENT;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.registerTieredMultis;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.OVERLAY_FLUID_HATCH_OUTPUT;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static com.omnicrox.gtaa.GTAA.REGISTRATE;

public class GTAAMachines {

    static {
        REGISTRATE.creativeModeTab(() -> GTAACreativeTab.MAIN);
    }

    public static Block fusionCasing(int tier) {
        return tier >= UEV ? GTAABlocks.FUSION_CASING_MK5.get() : GTAABlocks.FUSION_CASING_MK4.get();
    }

    public static Block fusionCoil(int tier) {
        return tier >= UEV ? GTAABlocks.FUSION_COIL_MK5.get() : GTAABlocks.FUSION_COIL_MK4.get();
    }

    /** uhv_fusion_reactor (MK IV) and uev_fusion_reactor (MK V). Same ring as GTCEu's MK I-III. */
    public static final MultiblockMachineDefinition[] FUSION_REACTOR = registerTieredMultis(REGISTRATE,
            "fusion_reactor", AdvancedFusionReactorMachine::new, (tier, builder) -> builder
                    .rotationState(RotationState.ALL)
                    .langValue("Fusion Reactor Computer MK %s".formatted(tier >= UEV ? "V" : "IV"))
                    .recipeType(GTRecipeTypes.FUSION_RECIPES)
                    .recipeModifiers(DEFAULT_ENVIRONMENT_REQUIREMENT, FusionReactorMachine::recipeModifier, BATCH_MODE)
                    .tooltips(
                            Component.literal("\u00a77Runs fusion recipes up to " + VNF[tier] + "§7, with perfect overclocks"),
                            Component.literal("\u00a77§e" + FormattingUtil.formatNumbers(
                                    AdvancedFusionReactorMachine.capacityPerHatch(tier)) +
                                    "§7 EU buffer per Energy Hatch (max 16: §e" +
                                    FormattingUtil.formatNumbers(AdvancedFusionReactorMachine.totalCapacity(tier, 16)) +
                                    "§7 EU)"),
                            Component.literal("\u00a77§8Ported from GT New Horizons (GT++)"))
                    .appearanceBlock(() -> fusionCasing(tier))
                    .pattern(definition -> {
                        var casing = blocks(fusionCasing(tier));
                        return FactoryBlockPattern.start()
                                .aisle("###############", "######OGO######", "###############")
                                .aisle("######ICI######", "####GGAAAGG####", "######ICI######")
                                .aisle("####CC###CC####", "###EAAOGOAAE###", "####CC###CC####")
                                .aisle("###C#######C###", "##EKEG###GEKE##", "###C#######C###")
                                .aisle("##C#########C##", "#GAE#######EAG#", "##C#########C##")
                                .aisle("##C#########C##", "#GAG#######GAG#", "##C#########C##")
                                .aisle("#I###########I#", "OAO#########OAO", "#I###########I#")
                                .aisle("#C###########C#", "GAG#########GAG", "#C###########C#")
                                .aisle("#I###########I#", "OAO#########OAO", "#I###########I#")
                                .aisle("##C#########C##", "#GAG#######GAG#", "##C#########C##")
                                .aisle("##C#########C##", "#GAE#######EAG#", "##C#########C##")
                                .aisle("###C#######C###", "##EKEG###GEKE##", "###C#######C###")
                                .aisle("####CC###CC####", "###EAAOGOAAE###", "####CC###CC####")
                                .aisle("######ICI######", "####GGAAAGG####", "######ICI######")
                                .aisle("###############", "######OSO######", "###############")
                                .where('S', controller(blocks(definition.get())))
                                .where('G', blocks(FUSION_GLASS.get()).or(casing))
                                .where('E', casing.or(
                                        blocks(PartAbility.INPUT_ENERGY.getBlockRange(tier, MAX).toArray(Block[]::new))
                                                .setMinGlobalLimited(1).setPreviewCount(16)))
                                .where('C', casing)
                                .where('K', blocks(fusionCoil(tier)))
                                .where('O', casing.or(abilities(PartAbility.EXPORT_FLUIDS)))
                                .where('A', air())
                                .where('I', casing.or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(2)))
                                .where('#', any())
                                .build();
                    })
                    .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                    .model(createWorkableCasingMachineModel(
                            GTAA.id("block/casing/fusion_casing_mk" + (tier >= UEV ? 5 : 4)),
                            GTCEu.id("block/multiblock/fusion_reactor"))
                            .andThen(b -> b.addDynamicRenderer(DynamicRenderHelper::createFusionRingRender)))
                    .hasBER(true)
                    .register(),
            UHV, UEV);

    //////////////////////////////////////
    // ***** Antimatter (SASS / SLAM) *****
    //////////////////////////////////////

    public static final MachineDefinition ANTIMATTER_HATCH = REGISTRATE
            .machine("antimatter_hatch", AntimatterHatchPartMachine::new)
            .langValue("Antimatter Hatch")
            .tier(UEV)
            .rotationState(RotationState.ALL)
            .colorOverlayTieredHullModel(OVERLAY_FLUID_HATCH_OUTPUT, null, "overlay_pipe_out_emissive")
            .tooltips(Component.literal("\u00a77Holds \u00a7d" + FormattingUtil.formatNumbers(AntimatterHatchPartMachine.CAPACITY) +
                            " L\u00a77 of Semi-Stable Antimatter"),
                    Component.literal("\u00a77The SASS needs exactly 16, around its inner ring"))
            .register();

    private static final Component SEPARATOR = Component.literal("\u00a78\u00a7m                                                            ");

    private static Block naquadriaFrame() {
        for (Material m : new Material[] { GTMaterials.Naquadria, GTMaterials.NaquadahAlloy, GTMaterials.Neutronium }) {
            Block b = ChemicalHelper.getBlock(TagPrefix.frameGt, m);
            if (b != null && b != Blocks.AIR) return b;
        }
        return GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get();
    }

    public static final MultiblockMachineDefinition ANTIMATTER_FORGE = REGISTRATE
            .multiblock("antimatter_forge", AntimatterForgeMachine::new)
            .langValue("Semi-Stable Antimatter Stabilization Sequencer")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eAntimatter Forge, SSASS"),
                    Component.literal("\u00a7dDimensions not included!"),
                    Component.literal("\u00a77Converts protomatter into antimatter"),
                    Component.literal("\u00a77Passively consumes 10,000,000 + (\u00a7bAntimatter\u00a77 \u00d7 1000)^(1.5 - \u00a7aM\u00a77) EU/t"),
                    Component.literal("\u00a77The consumption decays by 1% every tick when empty"),
                    Component.literal("\u00a77Actively uses (\u00a7bAntimatter\u00a77 \u00d7 10,000)^(1.5 - \u00a75G\u00a77) EU per cycle to produce antimatter"),
                    SEPARATOR,
                    Component.literal("\u00a77Cycles every second"),
                    Component.literal("\u00a77Every cycle, the lowest amount of antimatter in the 16 antimatter hatches is recorded"),
                    Component.literal("\u00a77All hatches with more than the lowest amount \u00a7close half the difference!"),
                    Component.literal("\u00a7cVoids 10% of antimatter\u00a77 if it runs out of energy/protomatter during a cycle"),
                    Component.literal("\u00a77Base production per hatch is (1/16) \u00d7 (\u00a7bAntimatter\u00a77^(0.5 + \u00a76C\u00a77)) of antimatter each cycle"),
                    Component.literal("\u00a77Each hatch multiplies the base production per hatch with a random number pulled from"),
                    Component.literal("\u00a77a normal distribution with a mean of 0.2 + \u00a7bA\u00a77 and a variance of 1"),
                    Component.literal("\u00a77The total gain of antimatter can be negative!"),
                    SEPARATOR,
                    Component.literal("\u00a77Can be supplied with stabilization fluids to improve antimatter generation"),
                    Component.literal("\u00a77Each stabilization can only use one of the fluids at a time"),
                    Component.literal("\u00a7aMagnetic Stabilization\u00a77 (Consumes \u00a7bAntimatter\u00a77^0.5 L of fluid per cycle)"),
                    Component.literal("\u00a771. Molten Purified Tengam = \u00a7a0.1"),
                    Component.literal("\u00a772. Tachyon Rich Temporal Fluid = \u00a7a0.2"),
                    Component.literal("\u00a773. Molten Magmatter = \u00a7a0.3"),
                    Component.literal("\u00a75Gravity Stabilization\u00a77 (Consumes \u00a7bAntimatter\u00a77^0.5 L of fluid per cycle)"),
                    Component.literal("\u00a771. Molten SpaceTime = \u00a750.05"),
                    Component.literal("\u00a772. Spatially Enlarged Fluid = \u00a750.10"),
                    Component.literal("\u00a773. Molten Eternity = \u00a750.15"),
                    Component.literal("\u00a76Containment Stabilization\u00a77 (Consumes \u00a7bAntimatter\u00a77^(2/7) L of fluid per cycle)"),
                    Component.literal("\u00a771. Molten Shirabon = \u00a760.05"),
                    Component.literal("\u00a772. Molten Magnetohydrodynamically Constrained Star Matter = \u00a760.10"),
                    Component.literal("\u00a7bActivation Stabilization\u00a77 (Consumes \u00a7bAntimatter\u00a77^(1/3) L of fluid per cycle)"),
                    Component.literal("\u00a771. Naquadah Based Liquid Fuel MkV (Depleted) = \u00a7b0.05"),
                    Component.literal("\u00a772. Naquadah Based Liquid Fuel MkVI (Depleted) = \u00a7b0.10"),
                    SEPARATOR,
                    Component.literal("\u00a78Ported from GT New Horizons (GoodGenerator)"))
            .appearanceBlock(GTAABlocks.MAGNETIC_FLUX_CASING)
            .pattern(definition -> GTAAStructures.pattern("antimatter", "antimatter_forge")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTAABlocks.ANTIMATTER_CONTAINMENT_CASING.get()))
                    .where('B', blocks(GTAABlocks.PROTOMATTER_ACTIVATION_COIL.get()))
                    .where('C', blocks(GTAABlocks.GRAVITY_STABILIZATION_CASING.get()))
                    .where('D', blocks(GTAABlocks.MAGNETIC_FLUX_CASING.get()))
                    .where('E', blocks(ANTIMATTER_HATCH.getBlock()))
                    .where('F', blocks(GTAABlocks.GRAVITY_STABILIZATION_CASING.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1)))
                    .where('H', blocks(GTAABlocks.GRAVITY_STABILIZATION_CASING.get())
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/antimatter_machine_casing"),
                    GTAA.id("block/multiblock/antimatter")))
            .register();

    public static final MultiblockMachineDefinition ANTIMATTER_GENERATOR = REGISTRATE
            .multiblock("antimatter_generator", AntimatterGeneratorMachine::new)
            .langValue("Shielded Lagrangian Annihilation Matrix")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .generator(true)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eAntimatter Generator, SLAM"),
                    Component.literal("\u00a77Annihilating Antimatter like it's 2205!"),
                    SEPARATOR,
                    Component.literal("\u00a77Generates energy by reacting \u00a7bSemi-Stable Antimatter\u00a77 with \u00a76Matter"),
                    Component.literal("\u00a77Annihilation uses an equal amount of \u00a7bAntimatter\u00a77 and \u00a76Matter"),
                    Component.literal("\u00a77Consumes \u00a76all inputs\u00a77 every processing cycle"),
                    Component.literal("\u00a7cVoids any invalid fluid!"),
                    Component.literal("\u00a77A cycle lasts 1 tick; \u00a76inputs\u00a77 are automatically checked every 5 seconds"),
                    Component.literal("\u00a77An imbalance between \u00a7bAntimatter\u00a77 and \u00a76Matter\u00a77 \u00a7cdecreases efficiency!"),
                    Component.literal("\u00a77Efficiency formula: Min(\u00a7bAntimatter\u00a77/\u00a76Matter\u00a77, \u00a76Matter\u00a77/\u00a7bAntimatter\u00a77)"),
                    Component.literal("\u00a77Any excess EU generated will be \u00a7cvoided!"),
                    Component.literal("\u00a77Cannot produce more than \u00a7a9.22e18\u00a77 EU per cycle"),
                    SEPARATOR,
                    Component.literal("\u00a77Energy production formula: \u00a7aA\u00a77 \u00d7 (\u00a7bAntimatter\u00a77 ^ \u00a76E\u00a77) EU/Cycle"),
                    Component.literal("\u00a7aAntimatter energy\u00a77 base value: \u00a7a1.00e12\u00a77 EU/L"),
                    Component.literal("\u00a76Energy production boost\u00a77:"),
                    Component.literal("\u00a771. Molten Copper: \u00a761.00"),
                    Component.literal("\u00a772. Molten Superconductor Base UIV: \u00a761.02"),
                    Component.literal("\u00a773. Molten Superconductor Base UMV: \u00a761.03"),
                    SEPARATOR,
                    Component.literal("\u00a78Ported from GT New Horizons (GoodGenerator)"))
            .appearanceBlock(GTAABlocks.MAGNETIC_FLUX_CASING)
            .pattern(definition -> GTAAStructures.pattern("antimatter", "antimatter_generator")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTBlocks.FUSION_GLASS.get()))
                    .where('B', blocks(GTAABlocks.PROTOMATTER_ACTIVATION_COIL.get()))
                    .where('C', blocks(GTAABlocks.GRAVITY_STABILIZATION_CASING.get()))
                    .where('D', blocks(GTAABlocks.MAGNETIC_FLUX_CASING.get()))
                    .where('E', blocks(GTBlocks.FILTER_CASING.get()))
                    .where('F', blocks(naquadriaFrame()))
                    .where('G', blocks(GTAABlocks.ANTIMATTER_ANNIHILATION_MATRIX.get()))
                    .where('H', blocks(GTAABlocks.GRAVITY_STABILIZATION_CASING.get())
                            .or(abilities(PartAbility.OUTPUT_LASER, PartAbility.OUTPUT_ENERGY).setMinGlobalLimited(1)))
                    .where('I', blocks(GTAABlocks.MAGNETIC_FLUX_CASING.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/antimatter_machine_casing"),
                    GTAA.id("block/multiblock/antimatter")))
            .register();

    public static void init() {}
}
