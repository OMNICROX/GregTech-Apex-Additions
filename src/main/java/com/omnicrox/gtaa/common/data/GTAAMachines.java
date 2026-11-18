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
                            Component.literal("Runs fusion recipes up to " + VNF[tier] + "§7, with perfect overclocks"),
                            Component.literal("§e" + FormattingUtil.formatNumbers(
                                    AdvancedFusionReactorMachine.capacityPerHatch(tier)) +
                                    "§7 EU buffer per Energy Hatch (max 16: §e" +
                                    FormattingUtil.formatNumbers(AdvancedFusionReactorMachine.totalCapacity(tier, 16)) +
                                    "§7 EU)"),
                            Component.literal("§8Ported from GT New Horizons (GT++)"))
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
            .tooltips(Component.literal("Holds \u00a7d" + FormattingUtil.formatNumbers(AntimatterHatchPartMachine.CAPACITY) +
                            " L\u00a77 of Semi-Stable Antimatter"),
                    Component.literal("The SASS needs exactly 16, around its inner ring"))
            .register();

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
                    Component.literal("\u00a7eAntimatter Forge, SASS\u00a77: converts Protomatter into Antimatter"),
                    Component.literal("Passively uses 10,000,000 + (Antimatter \u00d7 1000)^(1.5 - M) EU/t"),
                    Component.literal("Uses (Antimatter \u00d7 10,000)^(1.5 - G) EU per cycle; cycles every second"),
                    Component.literal("Hatches above the lowest lose half the difference each cycle"),
                    Component.literal("\u00a7cLoses 10% of its antimatter if it runs out of EU or Protomatter"),
                    Component.literal("Gain per hatch: (1/16) \u00d7 Antimatter^(0.5 + C) \u00d7 N(0.2 + A, 1) - can be negative!"),
                    Component.literal("Needs a little antimatter to start (from a MK V fusion seed recipe)"),
                    Component.literal("Stabilizers: Magnetic (M), Gravity (G), Containment (C), Activation (A)"),
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
                    Component.literal("\u00a7eAntimatter Generator, SLAM\u00a77: annihilating antimatter like it's 2205!"),
                    Component.literal("Reacts Semi-Stable Antimatter with an equal amount of Matter"),
                    Component.literal("Consumes all inputs every cycle; checks inputs every 5 seconds"),
                    Component.literal("\u00a7cVoids any invalid fluid!"),
                    Component.literal("Efficiency: min(Antimatter / Matter, Matter / Antimatter)"),
                    Component.literal("EU per cycle: 1e12 \u00d7 Antimatter^E; excess EU is voided"),
                    Component.literal("E: Molten Copper 1.00, Superconductor Base UIV 1.02, UMV 1.03"),
                    Component.literal("Outputs through Dynamo or Laser Source Hatches"),
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
