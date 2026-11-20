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
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.google.common.base.Suppliers;
import com.omnicrox.gtaa.api.GTAAStructures;
import com.omnicrox.gtaa.common.machine.multiblock.elevator.SpaceElevatorMachine;
import com.omnicrox.gtaa.common.machine.multiblock.elevator.SpaceElevatorModuleMachine;
import com.omnicrox.gtaa.common.machine.multiblock.elevator.SpaceElevatorPredicates;
import com.omnicrox.gtaa.common.machine.multiblock.elevator.DysonLaunchSiteMachine;
import java.util.function.Supplier;
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
                    Component.literal("\u00a77Use a screwdriver to toggle the antimatter render"),
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
                    GTAA.id("block/multiblock/antimatter"))
                    .andThen(b -> b.addDynamicRenderer(() -> new com.omnicrox.gtaa.client.AntimatterCoreRender())))
            .hasBER(true)
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
                    Component.literal("\u00a77Switch the power destination to your wireless network with a screwdriver"),
                    Component.literal("\u00a77Wireless mode requires \u00a76Molten Superconductor Base UMV\u00a77 to work"),
                    Component.literal("\u00a77Wireless mode is still limited by hatch capacity"),
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

    //////////////////////////////////////
    // ***** Space Elevator (GTNH-Intergalactic port) *****
    //////////////////////////////////////

    private static final java.util.List<MultiblockMachineDefinition> SE_MODULES = new java.util.ArrayList<>();

    private static Block[] moduleBlocks() {
        return SE_MODULES.stream().map(MultiblockMachineDefinition::getBlock).toArray(Block[]::new);
    }

    private static FactoryBlockPattern elevatorShape(String key, MultiblockMachineDefinition definition) {
        TraceabilityPredicate base = blocks(GTAABlocks.SE_BASE_CASING.get());
        return GTAAStructures.pattern("space_elevator", key)
                .where('~', controller(blocks(definition.getBlock())))
                .where('E', blocks(GTAABlocks.SE_SUPPORT_STRUCTURE.get()))
                .where('B', SpaceElevatorPredicates.cable())
                .where('X', blocks(GTAABlocks.SE_BASE_CASING.get())
                        .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1))
                        .or(abilities(PartAbility.IMPORT_FLUIDS, PartAbility.COMPUTATION_DATA_RECEPTION)))
                .where('M', SpaceElevatorPredicates.moduleIO())
                .where('H', blocks(neutroniumFrame()))
                .where('F', blocks(GTAABlocks.SE_INTERNAL_STRUCTURE.get()))
                .where('C', SpaceElevatorPredicates.motors())
                .where('A', blocks(GTAABlocks.SE_CONCRETE_FLOOR.get()))
                .where('D', base)
                .where('I', SpaceElevatorPredicates.moduleSlot(GTAAMachines::moduleBlocks))
                .where('-', air())
                .where(' ', any());
    }

    private static Block neutroniumFrame() {
        Block b = ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.Neutronium);
        return b != null && b != Blocks.AIR ? b : GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get();
    }

    public static final MultiblockMachineDefinition SPACE_ELEVATOR = REGISTRATE
            .multiblock("space_elevator", SpaceElevatorMachine::new)
            .langValue("Space Elevator")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .tooltips(
                    Component.literal("Machine Type: \u00a7eSpace Elevator"),
                    Component.literal("\u00a7oElevates to space"),
                    Component.literal("Can be equipped with up to \u00a7a12\u00a77 Space Elevator Modules"),
                    Component.literal("\u00a7fMotor Tier\u00a77 limits module Tier and amount of possible modules"),
                    Component.literal("Each module has its own space for IO, except energy"),
                    Component.literal("Energy is supplied to this controller and gets distributed"),
                    Component.literal("Starting from \u00a7dMotor Mk-III\u00a77 the structure can be expanded to fit \u00a7a24\u00a77 modules"),
                    Component.literal("Max module amount is \u00a7a6\u00a77/\u00a799\u00a7912\u00a77/\u00a7d15\u00a77/\u00a7618\u00a77/\u00a7c24\u00a77 for \u00a7aMk-I\u00a77/\u00a79Mk-II\u00a77/\u00a7dMk-III\u00a77/\u00a76Mk-IV\u00a77/\u00a7cMk-V\u00a77 motors correspondingly"),
                    Component.literal("Disabling this controller also pauses all modules"),
                    Component.literal("Use a screwdriver on the controller to switch to the extended structure"),
                    Component.literal("\u00a7aSupports Multi-Amp and Laser Hatches!"),
                    Component.literal("\u00a78Ported from GT New Horizons (GTNH-Intergalactic)"))
            .appearanceBlock(GTAABlocks.SE_BASE_CASING)
            .pattern(definition -> elevatorShape("main", definition).build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/space_elevator/base_casing"),
                    GTAA.id("block/multiblock/space_elevator")))
            .register();

    public static final Supplier<BlockPattern> SPACE_ELEVATOR_EXTENDED_PATTERN = Suppliers.memoize(
            () -> elevatorShape("extended", SPACE_ELEVATOR).build());

    private static long moduleBuffer(int voltageTier, int multiplier) {
        return 160_008_000L * (2L << (voltageTier - 7)) * multiplier;
    }

    private static MultiblockMachineDefinition seModule(String id, String name, SpaceElevatorModuleMachine.Stats stats,
                                                        com.gregtechceu.gtceu.api.recipe.GTRecipeType type,
                                                        Component... tooltips) {
        return seModule(id, name, stats, h -> new SpaceElevatorModuleMachine(h, stats),
                new com.gregtechceu.gtceu.api.recipe.GTRecipeType[] { type }, new PartAbility[0], null, tooltips);
    }

    private static MultiblockMachineDefinition seModule(String id, String name, SpaceElevatorModuleMachine.Stats stats,
                                                        java.util.function.Function<com.gregtechceu.gtceu.api.machine.IMachineBlockEntity, ? extends com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine> factory,
                                                        com.gregtechceu.gtceu.api.recipe.GTRecipeType[] types,
                                                        PartAbility[] extraAbilities, String credit,
                                                        Component... tooltips) {
        PartAbility[] abilities = java.util.stream.Stream.concat(java.util.stream.Stream.of(
                PartAbility.IMPORT_ITEMS, PartAbility.EXPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                PartAbility.COMPUTATION_DATA_RECEPTION), java.util.stream.Stream.of(extraAbilities)).toArray(PartAbility[]::new);
        var def = REGISTRATE.multiblock(id, factory::apply)
                .langValue(name)
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeTypes(types)
                .recipeModifiers(SpaceElevatorModuleMachine::recipeModifier)
                .tooltips(tooltips)
                .tooltips(Component.literal("\u00a77Capacity: \u00a79" + FormattingUtil.formatNumbers(stats.bufferSize()) + " EU"),
                        Component.literal(credit != null ? credit : "\u00a78Ported from GT New Horizons (GTNH-Intergalactic)"))
                .appearanceBlock(GTAABlocks.SE_BASE_CASING)
                .pattern(definition -> FactoryBlockPattern.start(RelativeDirection.LEFT, RelativeDirection.DOWN, RelativeDirection.BACK)
                        .aisle("H", "~", "H", "H", "H")
                        .aisle("H", "H", "H", "H", "H")
                        .where('~', controller(blocks(definition.getBlock())))
                        .where('H', blocks(GTAABlocks.SE_BASE_CASING.get())
                                .or(abilities(abilities)))
                        .build())
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(createWorkableCasingMachineModel(GTAA.id("block/space_elevator/base_casing"),
                        GTAA.id("block/multiblock/space_elevator")))
                .register();
        SE_MODULES.add(def);
        return def;
    }

    public static final MultiblockMachineDefinition SPACE_ASSEMBLER_MK1 = seModule("space_assembler_module_mk1",
            "Space Assembler Module Mk-I",
            new SpaceElevatorModuleMachine.Stats(UHV, 1, 1, 4, 1.0f, moduleBuffer(UHV, 4)),
            GTAARecipeTypes.SPACE_ASSEMBLER,
            Component.literal("Machine Type: \u00a7eSpace Elevator Module, SPASS"),
            Component.literal("Module that adds the Space Assembler to the Space Elevator"),
            Component.literal("\u00a7d\u00a7lWhy do I need to assemble stuff in space again?"),
            Component.literal("Runs at " + VNF[UHV] + "\u00a77 with up to \u00a764\u00a77 parallels"),
            Component.literal("Needs at least \u00a7aMotor Mk-I\u00a77 to work"));

    public static final MultiblockMachineDefinition SPACE_ASSEMBLER_MK2 = seModule("space_assembler_module_mk2",
            "Space Assembler Module Mk-II",
            new SpaceElevatorModuleMachine.Stats(UIV, 2, 3, 16, 1.5f, moduleBuffer(UIV, 16)),
            GTAARecipeTypes.SPACE_ASSEMBLER,
            Component.literal("Machine Type: \u00a7eSpace Elevator Module, SPASS"),
            Component.literal("Module that adds the Space Assembler to the Space Elevator"),
            Component.literal("\u00a7d\u00a7lIs this really necessary?"),
            Component.literal("Runs at " + VNF[UIV] + "\u00a77 at \u00a7a150%\u00a77 speed with up to \u00a7616\u00a77 parallels"),
            Component.literal("Needs at least \u00a7dMotor Mk-III\u00a77 to work"));

    public static final MultiblockMachineDefinition SPACE_ASSEMBLER_MK3 = seModule("space_assembler_module_mk3",
            "Space Assembler Module Mk-III",
            new SpaceElevatorModuleMachine.Stats(UXV, 3, 5, 64, 2.0f, moduleBuffer(UXV, 64)),
            GTAARecipeTypes.SPACE_ASSEMBLER,
            Component.literal("Machine Type: \u00a7eSpace Elevator Module, SPASS"),
            Component.literal("Module that adds the Space Assembler to the Space Elevator"),
            Component.literal("\u00a7d\u00a7lI guess we'll never know."),
            Component.literal("Runs at " + VNF[UXV] + "\u00a77 at \u00a7a200%\u00a77 speed with up to \u00a7664\u00a77 parallels"),
            Component.literal("Needs at least \u00a7cMotor Mk-V\u00a77 to work"));

    private static final Component[] MINING_COMMON = {
            Component.literal("\u00a77Doesn't use an energy hatch, instead receives energy from the Space Elevator"),
            Component.literal("\u00a77Requires a plasma to operate (\u00a7e825L Helium\u00a77 / \u00a73550L Nitrogen\u00a77 / \u00a79375L Oxygen\u00a77 / \u00a7f250L Iron\u00a77 / \u00a7c150L Nickel\u00a77)."),
            Component.literal("\u00a77Higher tier plasmas increase the size of mined asteroids and reduce mining operation time."),
            Component.literal("\u00a77Needs to be fed with computation to work."),
            Component.literal("\u00a77Computation is consumed per parallel"),
            Component.literal("\u00a77Mining Drones must be placed in a regular input bus (they are not used up)."),
            Component.literal("\u00a77Pick the asteroid with a \u00a7eProgrammed Circuit\u00a77 (see JEI)."),
    };

    private static Component[] miningTooltips(String quote, int tier, int parallels, String motor) {
        java.util.List<Component> list = new java.util.ArrayList<>();
        list.add(Component.literal("\u00a77Machine Type: \u00a7eSpace Elevator Module"));
        list.add(Component.literal("\u00a77Module that adds Space Mining Operations to the Space Elevator"));
        list.add(Component.literal("\u00a7d\u00a7l" + quote));
        list.addAll(java.util.List.of(MINING_COMMON));
        list.add(Component.literal("\u00a77Runs at " + VNF[tier] + "\u00a77 with up to \u00a76" + parallels + "\u00a77 parallels"));
        list.add(Component.literal("\u00a77Needs at least " + motor + "\u00a77 to work"));
        return list.toArray(Component[]::new);
    }

    public static final MultiblockMachineDefinition SPACE_MINING_MK1 = seModule("space_mining_module_mk1",
            "Space Mining Module Mk-I",
            new SpaceElevatorModuleMachine.Stats(UV, 1, 1, 2, 1.0f, moduleBuffer(UV, 1)),
            GTAARecipeTypes.SPACE_MINING,
            miningTooltips("Great treasures beyond your imagination await!", UV, 2, "\u00a7aMotor Mk-I"));

    public static final MultiblockMachineDefinition SPACE_MINING_MK2 = seModule("space_mining_module_mk2",
            "Space Mining Module Mk-II",
            new SpaceElevatorModuleMachine.Stats(UHV, 2, 2, 4, 1.0f, moduleBuffer(UHV, 1)),
            GTAARecipeTypes.SPACE_MINING,
            miningTooltips("Does this violate drone rights?", UHV, 4, "\u00a79Motor Mk-II"));

    public static final MultiblockMachineDefinition SPACE_MINING_MK3 = seModule("space_mining_module_mk3",
            "Space Mining Module Mk-III",
            new SpaceElevatorModuleMachine.Stats(UEV, 3, 3, 8, 1.0f, moduleBuffer(UEV, 1)),
            GTAARecipeTypes.SPACE_MINING,
            miningTooltips("This definitely violates drone rights.", UEV, 8, "\u00a7dMotor Mk-III"));

    // ---------------- Space Pumping ----------------
    private static Component[] pumpTooltips(String quote, int tier, int parallels, String motor, String gases) {
        return new Component[] {
                Component.literal("\u00a77Machine Type: \u00a7eSpace Elevator Module"),
                Component.literal("\u00a77Module that adds Space Pumping Operations to the Space Elevator"),
                Component.literal("\u00a7d\u00a7l" + quote),
                Component.literal("\u00a77Doesn't use an energy hatch, instead receives energy from the Space Elevator"),
                Component.literal("\u00a77Pumps gases from other planets. Pick the gas with a \u00a7eProgrammed Circuit\u00a77 (see JEI)."),
                Component.literal("\u00a77Gases: " + gases),
                Component.literal("\u00a77Runs at " + VNF[tier] + "\u00a77 with up to \u00a76" + parallels + "\u00a77 parallels"),
                Component.literal("\u00a77Needs at least " + motor + "\u00a77 to work") };
    }

    public static final MultiblockMachineDefinition SPACE_PUMP_MK1 = seModule("space_pumping_module_mk1",
            "Space Pumping Module Mk-I",
            new SpaceElevatorModuleMachine.Stats(UHV, 1, 1, 4, 1.0f, moduleBuffer(UHV, 4)),
            GTAARecipeTypes.SPACE_PUMPING,
            pumpTooltips("Pumping clouds of other worlds", UHV, 4, "\u00a7aMotor Mk-I",
                    "Hydrogen, Helium, Deuterium, Methane, Nitrogen"));

    public static final MultiblockMachineDefinition SPACE_PUMP_MK2 = seModule("space_pumping_module_mk2",
            "Space Pumping Module Mk-II",
            new SpaceElevatorModuleMachine.Stats(UEV, 2, 2, 16, 1.0f, moduleBuffer(UEV, 16)),
            GTAARecipeTypes.SPACE_PUMPING,
            pumpTooltips("Gas giants are just big fuel tanks", UEV, 16, "\u00a79Motor Mk-II",
                    "+ Helium-3, Tritium, Neon, Argon, Krypton, Xenon, Radon, Oganesson"));

    public static final MultiblockMachineDefinition SPACE_PUMP_MK3 = seModule("space_pumping_module_mk3",
            "Space Pumping Module Mk-III",
            new SpaceElevatorModuleMachine.Stats(UXV, 3, 4, 64, 1.0f, moduleBuffer(UXV, 64)),
            GTAARecipeTypes.SPACE_PUMPING,
            pumpTooltips("Is there anything left to pump?", UXV, 64, "\u00a76Motor Mk-IV",
                    "+ Fluorine, Oxygen, Liquid Helium, Spatially Enlarged Fluid"));

    // ---------------- our versions of third-party modules (idea credited, no code copied) ----------------
    public static final MultiblockMachineDefinition RESOURCE_COLLECTION_MODULE = seModule("resource_collection_module",
            "Resource Collection Module",
            new SpaceElevatorModuleMachine.Stats(UIV, 3, 4, 32, 1.5f, moduleBuffer(UIV, 32)),
            h -> new SpaceElevatorModuleMachine(h, new SpaceElevatorModuleMachine.Stats(UIV, 3, 4, 32, 1.5f, moduleBuffer(UIV, 32))),
            new com.gregtechceu.gtceu.api.recipe.GTRecipeType[] { GTAARecipeTypes.SPACE_MINING, GTAARecipeTypes.SPACE_PUMPING },
            new PartAbility[0], "\u00a78Idea from GT: Not Leisure (GTNL); GTAA's own implementation",
            Component.literal("\u00a77Machine Type: \u00a7eSpace Elevator Module, Space Mining / Space Pumping"),
            Component.literal("\u00a77Mines asteroids and pumps planetary gases in one module"),
            Component.literal("\u00a7d\u00a7lWhy build two when one will do?"),
            Component.literal("\u00a77Doesn't use an energy hatch, instead receives energy from the Space Elevator"),
            Component.literal("\u00a77Runs every Space Mining (Mk-III) and Space Pumping (Mk-III) recipe"),
            Component.literal("\u00a77Switch between mining and pumping in the controller GUI"),
            Component.literal("\u00a77Runs at " + VNF[UIV] + "\u00a77 at \u00a7a150%\u00a77 speed with up to \u00a7632\u00a77 parallels"),
            Component.literal("\u00a77Needs at least \u00a76Motor Mk-IV\u00a77 to work"));

    public static final MultiblockMachineDefinition SPACE_DRILL_MK123 = seModule("space_drill_mk123",
            "Space Drill MK-123",
            new SpaceElevatorModuleMachine.Stats(UXV, 3, 5, 256, 2.0f, moduleBuffer(UXV, 256)),
            h -> new SpaceElevatorModuleMachine(h, new SpaceElevatorModuleMachine.Stats(UXV, 3, 5, 256, 2.0f, moduleBuffer(UXV, 256))),
            new com.gregtechceu.gtceu.api.recipe.GTRecipeType[] { GTAARecipeTypes.SPACE_MINING },
            new PartAbility[0], "\u00a78Idea from 123Technology; GTAA's own implementation",
            Component.literal("\u00a77Machine Type: \u00a7eSpace Elevator Module, Space Mining"),
            Component.literal("\u00a77Drills straight through whole asteroids"),
            Component.literal("\u00a7d\u00a7lOne, two, three... gone."),
            Component.literal("\u00a77Doesn't use an energy hatch, instead receives energy from the Space Elevator"),
            Component.literal("\u00a77Runs every Space Mining recipe, including Mk-III asteroids"),
            Component.literal("\u00a77Runs at " + VNF[UXV] + "\u00a77 at \u00a7a200%\u00a77 speed with up to \u00a76256\u00a77 parallels"),
            Component.literal("\u00a77Needs at least \u00a7cMotor Mk-V\u00a77 to work"));

    public static final MultiblockMachineDefinition DYSON_LAUNCH_SITE = seModule("dyson_sphere_launch_site",
            "Dyson Sphere Launch Site",
            DysonLaunchSiteMachine.STATS,
            DysonLaunchSiteMachine::new,
            new com.gregtechceu.gtceu.api.recipe.GTRecipeType[] { GTAARecipeTypes.DYSON_LAUNCH },
            new PartAbility[] { PartAbility.OUTPUT_ENERGY, PartAbility.OUTPUT_LASER },
            "\u00a78Idea from Twist Space Technology (TST); GTAA's own implementation",
            Component.literal("\u00a77Machine Type: \u00a7eSpace Elevator Module, Generator"),
            Component.literal("\u00a77Launches Dyson Swarm Modules into orbit around the sun"),
            Component.literal("\u00a7d\u00a7lHarness the power of a star"),
            Component.literal("\u00a77Launching uses energy from the Space Elevator"),
            Component.literal("\u00a77Every module in orbit makes \u00a7e" + FormattingUtil.formatNumbers(DysonLaunchSiteMachine.EU_PER_MODULE) + " EU/t\u00a77 through Dynamo or Laser Source hatches"),
            Component.literal("\u00a77Modules in orbit slowly break: \u00a7c0.1%\u00a77 of the swarm is lost every second"),
            Component.literal("\u00a77At most \u00a76" + FormattingUtil.formatNumbers(DysonLaunchSiteMachine.MAX_MODULES) + "\u00a77 modules in orbit"),
            Component.literal("\u00a77Runs at " + VNF[UIV] + "\u00a77 with up to \u00a7664\u00a77 parallel launches"),
            Component.literal("\u00a77Needs at least \u00a76Motor Mk-IV\u00a77 to work"));

    // ---------------- Compact Fusion Computers (GoodGenerator) ----------------
    /** Compact Fusion MK-n uses Fusion Machine Casing MK n (GTAA adds MK IV / MK V). */
    private static Block compactFusionCasing(int mk) {
        return switch (mk) {
            case 1 -> GTBlocks.FUSION_CASING.get();
            case 2 -> GTBlocks.FUSION_CASING_MK2.get();
            case 3 -> GTBlocks.FUSION_CASING_MK3.get();
            case 4 -> GTAABlocks.FUSION_CASING_MK4.get();
            default -> GTAABlocks.FUSION_CASING_MK5.get();
        };
    }

    private static net.minecraft.resources.ResourceLocation compactFusionCasingTexture(int mk) {
        return switch (mk) {
            case 1 -> new net.minecraft.resources.ResourceLocation("gtceu", "block/casings/fusion/fusion_casing");
            case 2 -> new net.minecraft.resources.ResourceLocation("gtceu", "block/casings/fusion/fusion_casing_mk2");
            case 3 -> new net.minecraft.resources.ResourceLocation("gtceu", "block/casings/fusion/fusion_casing_mk3");
            case 4 -> GTAA.id("block/casing/fusion_casing_mk4");
            default -> GTAA.id("block/casing/fusion_casing_mk5");
        };
    }

    /** First frame material that has a frame block: GTNH uses Naquadah Alloy, Duranium, Neutronium, Infinity Catalyst, Infinity. */
    private static com.gregtechceu.gtceu.api.data.chemical.material.Material compactFusionFrame(int mk) {
        java.util.List<com.gregtechceu.gtceu.api.data.chemical.material.Material> options = new java.util.ArrayList<>();
        switch (mk) {
            case 1 -> options.add(GTMaterials.NaquadahAlloy);
            case 2 -> options.add(GTMaterials.Duranium);
            case 3 -> options.add(GTMaterials.Neutronium);
            case 4 -> options.add(GTMaterials.Tritanium);
            default -> {
                var inf = com.gregtechceu.gtceu.api.GTCEuAPI.materialManager.getMaterial("gtna:infinity");
                if (inf != null) options.add(inf);
            }
        }
        options.add(GTMaterials.Neutronium);
        options.add(GTMaterials.TungstenSteel);
        for (var m : options) {
            if (com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.getBlock(
                    com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt, m) != null) return m;
        }
        return GTMaterials.Steel;
    }

    private static MultiblockMachineDefinition compactFusion(int mk) {
        int tier = LuV + mk - 1;
        String roman = com.omnicrox.gtaa.common.machine.multiblock.CompactFusionMachine.roman(mk);
        long capacity = com.omnicrox.gtaa.common.machine.multiblock.CompactFusionMachine.capableStartup(mk);
        String oc = mk >= 4 ? "4/4" : "2/2";
        return REGISTRATE.multiblock("compact_fusion_mk" + mk,
                        h -> new com.omnicrox.gtaa.common.machine.multiblock.CompactFusionMachine(h, tier))
                .langValue("Compact Fusion Computer MK-" + roman)
                .rotationState(RotationState.ALL)
                .recipeType(GTRecipeTypes.FUSION_RECIPES)
                .recipeModifiers(DEFAULT_ENVIRONMENT_REQUIREMENT,
                        com.omnicrox.gtaa.common.machine.multiblock.CompactFusionMachine::recipeModifier, BATCH_MODE)
                .tooltips(
                        Component.literal("\u00a77Machine Type: \u00a7eFusion Reactor"),
                        Component.literal("\u00a7664\u00a77 + (Machine Tier - Recipe Tier) * \u00a7664\u00a77 Parallels"),
                        Component.literal("\u00a7a" + FormattingUtil.formatNumbers(V[tier] * 2L * mk) + "\u00a77 EU/t and \u00a7e" +
                                FormattingUtil.formatNumbers(capacity / 32) + "\u00a77 EU capacity per \u00a7bEnergy Hatch"),
                        Component.literal("\u00a77The total combined EU capacity must be greater than the recipe start cost to ignite"),
                        Component.literal("\u00a77Can only run " + VNF[tier] + "\u00a77 recipes and below"),
                        Component.literal("\u00a77Up to \u00a7e32\u00a77 Energy Hatches (" + VNF[tier] + "\u00a77 or better): \u00a7e" +
                                FormattingUtil.formatNumbers(capacity) + "\u00a77 EU total"),
                        Component.literal("\u00a7dPerforms " + oc + " Perfect Overclocks"),
                        Component.literal("\u00a78Ported from GT New Horizons (GoodGenerator)"))
                .appearanceBlock(() -> compactFusionCasing(mk))
                .pattern(definition -> {
                    var casing = blocks(compactFusionCasing(mk));
                    var glass = blocks(FUSION_GLASS.get());
                    Block[] energy = java.util.stream.Stream.concat(
                            PartAbility.INPUT_ENERGY.getBlockRange(tier, MAX).stream(),
                            PartAbility.INPUT_LASER.getBlockRange(tier, MAX).stream()).toArray(Block[]::new);
                    return GTAAStructures.pattern("compact_fusion", "compact_fusion")
                            .where('~', controller(blocks(definition.get())))
                            .where('C', casing)
                            .where('D', casing)
                            .where('H', blocks(GTAABlocks.compactFusionCoil(mk)))
                            .where('F', frames(compactFusionFrame(mk)))
                            .where('B', glass)
                            .where('I', glass.or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(16))
                                    .or(abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(16))
                                    .or(abilities(PartAbility.IMPORT_ITEMS)))
                            .where('E', casing.or(blocks(energy).setMinGlobalLimited(1).setMaxGlobalLimited(32).setPreviewCount(32)))
                            .where(' ', any())
                            .build();
                })
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(createWorkableCasingMachineModel(compactFusionCasingTexture(mk),
                        GTAA.id("block/multiblock/compact_fusion_mk" + mk)))
                .register();
    }

    public static final MultiblockMachineDefinition[] COMPACT_FUSION = {
            compactFusion(1), compactFusion(2), compactFusion(3), compactFusion(4), compactFusion(5) };

    // ---------------- Naquadah fuel line (GoodGenerator) ----------------
    public static final MultiblockMachineDefinition LARGE_NAQUADAH_REACTOR = REGISTRATE
            .multiblock("large_naquadah_reactor", com.omnicrox.gtaa.common.machine.multiblock.naquadah.LargeNaquadahReactorMachine::new)
            .langValue("Large Naquadah Reactor")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.NAQUADAH_REACTOR)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.naquadah.LargeNaquadahReactorMachine::recipeModifier)
            .generator(true)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eNaquadah Reactor"),
                    Component.literal("\u00a77Generates power from Liquid Nuclear Fuel or Liquid Naquadah Fuel"),
                    Component.literal("\u00a77Requires \u00a7b2400 L/s Liquid Air\u00a77 to run, otherwise \u00a7cit voids the fuel"),
                    SEPARATOR,
                    Component.literal("\u00a77Optionally supply a \u00a79Coolant\u00a77 to increase \u00a7aEfficiency\u00a77:"),
                    Component.literal("\u00a7920 L/s\u00a77 of \u00a79Tachyon Rich Temporal Fluid\u00a77 : \u00a7a500%"),
                    Component.literal("\u00a791000 L/s\u00a77 of \u00a79Liquid Helium\u00a77 : \u00a7a275%"),
                    Component.literal("\u00a791000 L/s\u00a77 of \u00a79Liquid Nitrogen\u00a77 : \u00a7a150%"),
                    SEPARATOR,
                    Component.literal("\u00a77Optionally supply a \u00a73Booster\u00a77 to increase \u00a7aOutput Power\u00a77 and \u00a7aFuel Usage\u00a77:"),
                    Component.literal("\u00a73180 L/s\u00a77 of \u00a73Molten Caesium\u00a77 : \u00a7a2x"),
                    Component.literal("\u00a73180 L/s\u00a77 of \u00a73Molten Uranium-235\u00a77 : \u00a7a3x"),
                    Component.literal("\u00a7320 L/s\u00a77 of \u00a73Molten Naquadah\u00a77 : \u00a7a4x"),
                    Component.literal("\u00a7320 L/s\u00a77 of \u00a73Molten Atomic Separation Catalyst\u00a77 : \u00a7a16x"),
                    Component.literal("\u00a7320 L/s\u00a77 of \u00a73Spatially Enlarged Fluid\u00a77 : \u00a7a64x"),
                    Component.literal("\u00a77Needs one Dynamo Hatch or Laser Source Hatch"),
                    Component.literal("\u00a78Ported from GT New Horizons (GoodGenerator)"))
            .appearanceBlock(GTAABlocks.NAQUADAH_REACTOR_CASING)
            .pattern(definition -> GTAAStructures.pattern("naquadah", "large_naquadah_reactor")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTAABlocks.NAQUADAH_REACTOR_CASING.get()).setMinGlobalLimited(130)
                            .or(abilities(PartAbility.OUTPUT_ENERGY, PartAbility.OUTPUT_LASER).setExactLimit(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1))
                            .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                    .where('B', blocks(GTAABlocks.FIELD_RESTRICTION_CASING.get()))
                    .where('C', blocks(GTAABlocks.RADIATION_PROOF_STEEL_FRAME.get()))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/naquadah_reactor_casing"),
                    GTAA.id("block/multiblock/naquadah_reactor")))
            .register();

    public static final MultiblockMachineDefinition NAQUADAH_FUEL_REFINERY = REGISTRATE
            .multiblock("naquadah_fuel_refinery", com.omnicrox.gtaa.common.machine.multiblock.naquadah.NaquadahFuelRefineryMachine::new)
            .langValue("Naquadah Fuel Refinery")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.NAQUADAH_FUEL_REFINERY)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.naquadah.NaquadahFuelRefineryMachine::recipeModifier)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eNaquadah Fuel Refinery"),
                    Component.literal("\u00a77Produces Naquadah Fuels"),
                    Component.literal("\u00a77\u00a764\u00a77 parallels per \u00a7bField Restriction Coil\u00a77 tier"),
                    SEPARATOR,
                    Component.literal("\u00a7dPerforms 1 4/4 Perfect Overclock\u00a77 per \u00a7bCoil\u00a77 above \u00a7bRecipe\u00a77 Tier"),
                    Component.literal("\u00a77All Field Restriction Coils must be the same tier"),
                    Component.literal("\u00a78Ported from GT New Horizons (GoodGenerator)"))
            .appearanceBlock(GTAABlocks.NAQUADAH_FUEL_REFINERY_CASING)
            .pattern(definition -> GTAAStructures.pattern("naquadah", "naquadah_fuel_refinery")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTAABlocks.NAQUADAH_FUEL_REFINERY_CASING.get()).setMinGlobalLimited(470)
                            .or(autoAbilities(definition.getRecipeTypes()))
                            .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                    .where('B', com.omnicrox.gtaa.common.machine.multiblock.naquadah.NaquadahFuelRefineryMachine.coils())
                    .where('C', blocks(GTAABlocks.FIELD_RESTRICTION_GLASS.get()))
                    .where('E', blocks(GTAABlocks.EUROPIUM_RADIATION_PROOF_CASING.get()))
                    .where('F', blocks(GTAABlocks.RADIATION_PROOF_STEEL_FRAME.get()))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/naquadah_fuel_refinery_casing"),
                    new net.minecraft.resources.ResourceLocation("gtceu", "block/multiblock/large_chemical_reactor")))
            .register();

    // ---------------- Component Assembly Line (GoodGenerator) ----------------
    public static final MultiblockMachineDefinition COMPONENT_ASSEMBLY_LINE = REGISTRATE
            .multiblock("component_assembly_line", com.omnicrox.gtaa.common.machine.multiblock.ComponentAssemblyLineMachine::new)
            .langValue("Component Assembly Line")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.COMPONENT_ASSEMBLY_LINE)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.ComponentAssemblyLineMachine::recipeModifier)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eComponent Assembler"),
                    Component.literal("\u00a77Assembles basic components (motors, pumps, etc.) in large batches"),
                    Component.literal("\u00a77The \u00a7bComponent Assembly Line Casing\u00a77 limits the recipes the machine can perform"),
                    Component.literal("\u00a77Using a higher tier casing than required \u00a7ahalves the recipe time\u00a77 per tier"),
                    Component.literal("\u00a77Normal overclocks, no tier-skip limit"),
                    Component.literal("\u00a78Ported from GT New Horizons (GoodGenerator)"))
            .appearanceBlock(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING)
            .pattern(definition -> {
                var h = blocks(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get());
                var glass = blocks(GTBlocks.CASING_TEMPERED_GLASS.get(), GTBlocks.CASING_LAMINATED_GLASS.get(),
                        FUSION_GLASS.get(), GTBlocks.CLEANROOM_GLASS.get());
                return GTAAStructures.pattern("component_assembly_line", "component_assembly_line")
                        .where('~', controller(blocks(definition.get())))
                        .where('H', h)
                        .where('A', glass)
                        .where('C', blocks(GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX.get()))
                        .where('D', blocks(GTBlocks.CASING_ASSEMBLY_CONTROL.get()))
                        .where('G', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                        .where('E', blocks(GTBlocks.CASING_GRATE.get()))
                        .where('B', com.omnicrox.gtaa.common.machine.multiblock.ComponentAssemblyLineMachine.casings())
                        .where('J', h.or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1)))
                        .where('N', frames(GTMaterials.TungstenSteel).or(abilities(PartAbility.IMPORT_ITEMS)))
                        .where('n', frames(GTMaterials.TungstenSteel))
                        .where('K', h.or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1)))
                        .where('L', h.or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1)))
                        .where('I', h.or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                        .where('M', h.or(abilities(PartAbility.IMPORT_FLUIDS)))
                        .where(' ', any())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/advanced_iridium_plated_casing"),
                    GTAA.id("block/multiblock/component_assembly_line")))
            .register();

    // ---------------- Quantum Force Transformer (GT++) ----------------
    public static final MultiblockMachineDefinition QUANTUM_FORCE_TRANSFORMER = REGISTRATE
            .multiblock("quantum_force_transformer", com.omnicrox.gtaa.common.machine.multiblock.QuantumForceTransformerMachine::new)
            .langValue("Quantum Force Transformer")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.QUANTUM_FORCE_TRANSFORMER)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.QuantumForceTransformerMachine::recipeModifier)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eQuantum Force Transformer"),
                    Component.literal("\u00a77Allows complex processing lines to be performed instantly in one step"),
                    Component.literal("\u00a77Every recipe requires a catalyst in an input bus; it is not consumed"),
                    Component.literal("\u00a77The number of catalysts sets the \u00a76maximum parallels"),
                    Component.literal("\u00a77Each output has an equal chance; higher focus tiers make the boosts stronger"),
                    Component.literal("\u00a7bNeptunium Plasma\u00a77 + a \u00a7eProgrammed Circuit\u00a77 focuses one output (circuit 0 = first output)"),
                    Component.literal("\u00a7bFermium Plasma\u00a77 raises the chance of every output"),
                    Component.literal("\u00a77Plasma use: focus tier \u00d7 4 \u00d7 \u221aparallels L/s; running out voids the run"),
                    Component.literal("\u00a77Manipulators set the crafting tier, Shielding Cores set the focus tier"),
                    Component.literal("\u00a78Ported from GT New Horizons (GT++)"))
            .appearanceBlock(GTAABlocks.QFT_CONTAINMENT_CASING)
            .pattern(definition -> {
                var casing = blocks(GTAABlocks.QFT_CONTAINMENT_CASING.get());
                return GTAAStructures.pattern("quantum_force_transformer", "quantum_force_transformer")
                        .where('~', controller(blocks(definition.get())))
                        .where('A', com.omnicrox.gtaa.common.machine.multiblock.QuantumForceTransformerMachine.tiered(
                                "QFTCraftTier", GTAABlocks::qftManipulators, "Manipulators"))
                        .where('B', com.omnicrox.gtaa.common.machine.multiblock.QuantumForceTransformerMachine.tiered(
                                "QFTFocusTier", GTAABlocks::qftShieldings, "Shielding Cores"))
                        .where('C', blocks(GTAABlocks.QFT_COIL.get()))
                        .where('D', casing)
                        .where('E', blocks(GTAABlocks.FORCE_FIELD_GLASS.get()))
                        .where('H', casing.or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1))
                                .or(abilities(PartAbility.IMPORT_FLUIDS))
                                .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1)))
                        .where('T', casing.or(abilities(PartAbility.EXPORT_ITEMS)).or(abilities(PartAbility.EXPORT_FLUIDS)))
                        .where(' ', any())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/qft_containment_casing"),
                    GTAA.id("block/multiblock/quantum_force_transformer")))
            .register();

    // ---------------- Water Purification Plant (GT5-Unofficial) ----------------
    private static TraceabilityPredicate waterOrAir() {
        return new TraceabilityPredicate(st -> st.getBlockState().isAir() ||
                st.getBlockState().getFluidState().is(net.minecraft.tags.FluidTags.WATER),
                () -> new com.lowdragmc.lowdraglib.utils.BlockInfo[] {
                        com.lowdragmc.lowdraglib.utils.BlockInfo.fromBlockState(net.minecraft.world.level.block.Blocks.WATER.defaultBlockState()) });
    }

    private static TraceabilityPredicate frameOr(com.gregtechceu.gtceu.api.data.chemical.material.Material... mats) {
        for (var m : mats) {
            if (m != null && com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.getBlock(
                    com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt, m) != null) return frames(m);
        }
        return frames(GTMaterials.Steel);
    }

    public static final MultiblockMachineDefinition PURIFICATION_PLANT = REGISTRATE
            .multiblock("purification_plant", com.omnicrox.gtaa.common.machine.multiblock.purification.PurificationPlantMachine::new)
            .langValue("Water Purification Plant")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.PURIFICATION_PLANT)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eWater Purification Plant"),
                    Component.literal("\u00a77Main controller for the purification units"),
                    Component.literal("\u00a77Every purification unit within \u00a7e32 blocks\u00a77 links to it automatically"),
                    Component.literal("\u00a77Powers all linked units from its own energy hatches"),
                    Component.literal("\u00a77Purified water grades 1-8 are made in the eight units"),
                    Component.literal("\u00a78Ported from GT New Horizons"))
            .appearanceBlock(GTAABlocks.PUR_INDUSTRIAL_CASING)
            .pattern(definition -> GTAAStructures.pattern("purification", "plant")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTAABlocks.PUR_CONCRETE.get()))
                    .where('B', blocks(GTAABlocks.PUR_INDUSTRIAL_CASING.get()))
                    .where('C', blocks(GTAABlocks.PUR_STERILE_CASING.get()))
                    .where('D', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('F', waterOrAir())
                    .where('G', frameOr(GTMaterials.Tungsten))
                    .where('H', blocks(GTAABlocks.PUR_INDUSTRIAL_CASING.get())
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1))
                            .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/pur_industrial_casing"),
                    GTAA.id("block/multiblock/purification_plant")))
            .register();

    private static MultiblockMachineDefinition purUnit(int grade, String id, String name, String key, java.util.function.Supplier<? extends Block> appearance,
                                                       String texture, String overlay,
                                                       java.util.function.Function<MultiblockMachineDefinition, com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern> where,
                                                       Component... extra) {
        java.util.List<Component> tips = new java.util.ArrayList<>();
        tips.add(Component.literal("\u00a77Machine Type: \u00a7ePurification Unit (Grade " + grade + ")"));
        tips.addAll(java.util.List.of(extra));
        tips.add(Component.literal("\u00a77Must be within \u00a7e32 blocks\u00a77 of a Water Purification Plant, which powers it"));
        tips.add(Component.literal("\u00a77Water boost: keep some Grade " + (grade + 1) + " water in the input for \u00a7a+15%\u00a77 success"));
        tips.add(Component.literal("\u00a77Up to \u00a7664\u00a77 parallels"));
        tips.add(Component.literal("\u00a78Ported from GT New Horizons"));
        return REGISTRATE.multiblock(id, h -> new com.omnicrox.gtaa.common.machine.multiblock.purification.PurificationUnitMachine(h, grade))
                .langValue(name)
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(GTAARecipeTypes.PURIFICATION[grade])
                .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.purification.PurificationUnitMachine::recipeModifier)
                .tooltips(tips.toArray(Component[]::new))
                .appearanceBlock(appearance)
                .pattern(definition -> where.apply(definition).where(' ', any()).build())
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(createWorkableCasingMachineModel(GTAA.id("block/casing/" + texture), overlay.contains(":") ?
                        new net.minecraft.resources.ResourceLocation(overlay) : GTAA.id(overlay)))
                .register();
    }

    private static final String LCR = "gtceu:block/multiblock/large_chemical_reactor";

    private static TraceabilityPredicate fluidIO(Block casing) {
        return blocks(casing).or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1))
                .or(abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1))
                .or(abilities(PartAbility.IMPORT_ITEMS)).or(abilities(PartAbility.EXPORT_ITEMS));
    }

    public static final MultiblockMachineDefinition PURIFICATION_CLARIFIER = purUnit(1, "purification_unit_clarifier",
            "Clarifier Purification Unit", "clarifier", GTAABlocks.PUR_STERILE_CASING, "pur_sterile_casing", LCR,
            d -> GTAAStructures.pattern("purification", "clarifier")
                    .where('~', controller(blocks(d.get())))
                    .where('H', fluidIO(GTAABlocks.PUR_STERILE_CASING.get()))
                    .where('A', blocks(GTAABlocks.PUR_STERILE_CASING.get()))
                    .where('B', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('C', frameOr(GTMaterials.Iridium))
                    .where('D', frameOr(GTMaterials.DamascusSteel, GTMaterials.Steel))
                    .where('W', waterOrAir())
                    .where('F', blocks(GTBlocks.CASING_GRATE.get())),
            Component.literal("\u00a77Filters water through Activated Carbon Filter Meshes (used up 1 per cycle)"),
            Component.literal("\u00a77Base success chance \u00a7a70%"));

    public static final MultiblockMachineDefinition PURIFICATION_OZONATION = purUnit(2, "purification_unit_ozonation",
            "Ozonation Purification Unit", "ozonation", GTAABlocks.PUR_OZONE_CASING, "pur_ozone_casing", LCR,
            d -> GTAAStructures.pattern("purification", "ozonation")
                    .where('~', controller(blocks(d.get())))
                    .where('A', fluidIO(GTAABlocks.PUR_OZONE_CASING.get()))
                    .where('B', blocks(GTAABlocks.PUR_PRESSURE_CASING.get()))
                    .where('C', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('D', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('E', frameOr(GTMaterials.TungstenSteel))
                    .where('O', blocks(GTAABlocks.PUR_PRESSURE_CASING.get()).or(abilities(PartAbility.IMPORT_FLUIDS))),
            Component.literal("\u00a77Uses Ozone: \u00a7e128k / 256k / 512k / 1M L\u00a77 per cycle for \u00a7a20 / 40 / 60 / 80%"));

    public static final MultiblockMachineDefinition PURIFICATION_FLOCCULATION = purUnit(3, "purification_unit_flocculation",
            "Flocculation Purification Unit", "flocculation", GTAABlocks.PUR_FLOCCULATION_CASING, "pur_flocculation_casing", LCR,
            d -> GTAAStructures.pattern("purification", "flocculation")
                    .where('~', controller(blocks(d.get())))
                    .where('A', blocks(GTBlocks.CASING_GRATE.get()))
                    .where('B', fluidIO(GTAABlocks.PUR_FLOCCULATION_CASING.get()))
                    .where('C', blocks(GTAABlocks.PUR_STERILE_CASING.get()))
                    .where('D', blocks(GTAABlocks.PUR_INDUSTRIAL_CASING.get()))
                    .where('E', frameOr(GTMaterials.Neutronium))
                    .where('W', waterOrAir())
                    .where('G', blocks(GTBlocks.CASING_TEMPERED_GLASS.get())),
            Component.literal("\u00a77Uses \u00a7e100k L\u00a77 Polyaluminium Chloride per cycle for \u00a7a90%\u00a77 (10% without)"));

    public static final MultiblockMachineDefinition PURIFICATION_PH = purUnit(4, "purification_unit_ph_adjustment",
            "pH Neutralization Purification Unit", "ph_adjustment", GTAABlocks.PUR_NAQUADAH_CASING, "pur_naquadah_casing", LCR,
            d -> GTAAStructures.pattern("purification", "ph_adjustment")
                    .where('~', controller(blocks(d.get())))
                    .where('A', blocks(GTAABlocks.PUR_CORROSION_CASING.get()))
                    .where('B', blocks(GTAABlocks.PUR_NAQUADAH_CASING.get()))
                    .where('E', frameOr(GTMaterials.NaquadahAlloy))
                    .where('G', blocks(GTAABlocks.PUR_PH_GLASS.get()))
                    .where('H', blocks(GTAABlocks.PUR_PH_GLASS.get()))
                    .where('I', fluidIO(GTAABlocks.PUR_NAQUADAH_CASING.get()))
                    .where('R', blocks(GTAABlocks.PUR_NAQUADAH_CASING.get()))
                    .where('X', blocks(GTAABlocks.PUR_NAQUADAH_CASING.get()).or(abilities(PartAbility.IMPORT_ITEMS)))
                    .where('Y', blocks(GTAABlocks.PUR_NAQUADAH_CASING.get()).or(abilities(PartAbility.IMPORT_FLUIDS))),
            Component.literal("\u00a77Balances the water with Sodium Hydroxide and Hydrochloric Acid for \u00a7a95%"));

    public static final MultiblockMachineDefinition PURIFICATION_PLASMA = purUnit(5, "purification_unit_plasma_heater",
            "Extreme Temperature Fluctuation Purification Unit", "plasma_heater", GTAABlocks.PUR_PLASMA_HEATING_CASING,
            "pur_plasma_heating_casing", LCR,
            d -> GTAAStructures.pattern("purification", "plasma_heater")
                    .where('~', controller(blocks(d.get())))
                    .where('A', blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                    .where('B', fluidIO(GTAABlocks.PUR_PLASMA_HEATING_CASING.get()))
                    .where('D', blocks(GTAABlocks.PUR_STERILE_CASING.get()))
                    .where('E', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('F', any())
                    .where('G', frameOr(GTMaterials.NaquadahAlloy))
                    .where('K', blocks(GTAABlocks.PUR_STERILE_CASING.get()).or(abilities(PartAbility.IMPORT_FLUIDS)))
                    .where('P', blocks(GTAABlocks.PUR_PLASMA_HEATING_CASING.get()).or(abilities(PartAbility.IMPORT_FLUIDS))),
            Component.literal("\u00a77Heats with Helium Plasma and cools with Liquid Helium for \u00a7a95%"));

    public static final MultiblockMachineDefinition PURIFICATION_UV = purUnit(6, "purification_unit_uv_treatment",
            "High Energy Laser Purification Unit", "uv_treatment", GTAABlocks.PUR_NAQUADRIA_CASING, "pur_naquadria_casing", LCR,
            d -> GTAAStructures.pattern("purification", "uv_treatment")
                    .where('~', controller(blocks(d.get())))
                    .where('A', blocks(GTAABlocks.PUR_NAQUADRIA_CASING.get()))
                    .where('B', blocks(GTAABlocks.PUR_UV_GLASS.get()))
                    .where('C', blocks(GTAABlocks.PUR_UV_EMITTER.get()))
                    .where('D', frameOr(GTMaterials.Tritanium, GTMaterials.Neutronium))
                    .where('L', blocks(GTAABlocks.PUR_NAQUADRIA_CASING.get()).or(abilities(PartAbility.IMPORT_ITEMS)))
                    .where('I', blocks(GTAABlocks.PUR_NAQUADRIA_CASING.get()))
                    .where('H', fluidIO(GTAABlocks.PUR_NAQUADRIA_CASING.get())),
            Component.literal("\u00a77Needs all 9 purification lenses in an input bus (not used up) for \u00a7a95%"));

    public static final MultiblockMachineDefinition PURIFICATION_DEGASSER = purUnit(7, "purification_unit_degasser",
            "Residual Decontaminant Degasser Purification Unit", "degasser", GTAABlocks.PUR_PLASMA_HEATING_CASING,
            "pur_plasma_heating_casing", LCR,
            d -> GTAAStructures.pattern("purification", "degasser")
                    .where('~', controller(blocks(d.get())))
                    .where('A', fluidIO(GTAABlocks.PUR_PLASMA_HEATING_CASING.get()))
                    .where('B', blocks(GTAABlocks.PUR_INFINITY_GLASS.get()))
                    .where('C', frameOr(GTMaterials.Neutronium)),
            Component.literal("\u00a77Flushes out residual gases with noble gases and a superconductor for \u00a7a95%"));

    public static final MultiblockMachineDefinition PURIFICATION_BARYONIC = purUnit(8, "purification_unit_baryonic_perfection",
            "Absolute Baryonic Perfection Purification Unit", "baryonic_perfection", GTAABlocks.PUR_QUARK_CONTAINMENT,
            "pur_quark_containment", "block/multiblock/baryonic_perfection",
            d -> GTAAStructures.pattern("purification", "baryonic_perfection")
                    .where('~', controller(blocks(d.get())))
                    .where('A', fluidIO(GTAABlocks.PUR_QUARK_CONTAINMENT.get()))
                    .where('B', blocks(GTAABlocks.PUR_QUARK_PIPE.get()))
                    .where('C', blocks(GTAABlocks.PUR_QUARK_RELEASE.get()))
                    .where('D', blocks(GTAABlocks.PUR_QUARK_GLASS.get()))
                    .where('E', frameOr(GTMaterials.Neutronium)),
            Component.literal("\u00a77Uses Quark Creation Catalysts and Molten Neutronium for \u00a7a95%"));

    // ---------------- Mega Distillation Tower (BartWorks) ----------------
    public static final MultiblockMachineDefinition MEGA_DISTILLATION_TOWER = REGISTRATE
            .multiblock("mega_distillation_tower", com.gregtechceu.gtceu.common.machine.multiblock.electric.DistillationTowerMachine::new)
            .langValue("Mega Distillation Tower")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DISTILLATION_RECIPES)
            .recipeModifiers((m, r) -> {
                int p = com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic.getParallelAmount(m, r, 256);
                if (p <= 1) return com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction.IDENTITY;
                return com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction.builder()
                        .modifyAllContents(com.gregtechceu.gtceu.api.recipe.content.ContentModifier.multiplier(p))
                        .eutMultiplier(p).parallels(p).build();
            }, com.gregtechceu.gtceu.common.data.GTRecipeModifiers.OC_NON_PERFECT_SUBTICK, BATCH_MODE)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eDistillation Tower"),
                    Component.literal("\u00a77Up to \u00a76256\u00a77 parallels"),
                    Component.literal("\u00a77One fluid output hatch per layer, 2 to 13 layers"),
                    Component.literal("\u00a78Ported from GT New Horizons (BartWorks)"))
            .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
            .pattern(definition -> {
                TraceabilityPredicate out = abilities(PartAbility.EXPORT_FLUIDS_1X, PartAbility.EXPORT_FLUIDS).setMaxLayerLimited(1);
                var casing = blocks(GTBlocks.CASING_STAINLESS_CLEAN.get());
                return FactoryBlockPattern.start(com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.RIGHT,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.BACK,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.UP)
                        .aisle("YYYYYYYSYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYY")
                        .aisle("ZZZZZZZZZZZZZZZ", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "Z#############Z", "ZZZZZZZZZZZZZZZ")
                        .aisle("XXXXXXXXXXXXXXX", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "X#############X", "XXXXXXXXXXXXXXX").setRepeatable(0, 11)
                        .aisle("XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX")
                        .where('S', controller(blocks(definition.getBlock())))
                        .where('Y', casing.or(abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(1))
                                .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1))
                                .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1))
                                .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                        .where('Z', casing.or(out))
                        .where('X', casing.or(out))
                        .where('#', air())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new net.minecraft.resources.ResourceLocation("gtceu", "block/casings/solid/machine_casing_clean_stainless_steel"),
                    new net.minecraft.resources.ResourceLocation("gtceu", "block/multiblock/distillation_tower")))
            .register();

    // ---------------- Extreme Entity Crusher (KubaTech) ----------------
    public static final MultiblockMachineDefinition EXTREME_ENTITY_CRUSHER = REGISTRATE
            .multiblock("extreme_entity_crusher", com.omnicrox.gtaa.common.machine.multiblock.ExtremeEntityCrusherMachine::new)
            .langValue("Extreme Entity Crusher")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.ENTITY_CRUSHER)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eMob Farm"),
                    Component.literal("\u00a77Put a \u00a7eSpawn Egg\u00a77 in an input bus to choose the mob (not used up)"),
                    Component.literal("\u00a77A sword in an input bus adds its Looting level (max 4)"),
                    Component.literal("\u00a77Rolls the mob's loot every 5 seconds"),
                    Component.literal("\u00a77Uses \u00a7e64 EU/t per point of mob health\u00a77 (at least MV) per mob"),
                    Component.literal("\u00a77Extra power runs more mobs at once, up to \u00a76256"),
                    Component.literal("\u00a7cBosses can't be crushed"),
                    Component.literal("\u00a78Idea from GT New Horizons (KubaTech)"))
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> {
                var casing = blocks(GTBlocks.CASING_STEEL_SOLID.get());
                var glass = blocks(GTBlocks.CASING_TEMPERED_GLASS.get(), GTBlocks.CASING_LAMINATED_GLASS.get(),
                        net.minecraft.world.level.block.Blocks.GLASS);
                return FactoryBlockPattern.start(com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.RIGHT,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.BACK,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.UP)
                        .aisle("CCSCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                        .aisle("fgggf", "gsssg", "gsssg", "gsssg", "fgggf")
                        .aisle("fgggf", "g###g", "g###g", "g###g", "fgggf").setRepeatable(4)
                        .aisle("ccccc", "ccccc", "ccccc", "ccccc", "ccccc")
                        .where('S', controller(blocks(definition.getBlock())))
                        .where('C', casing.or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1))
                                .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1))
                                .or(abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                                .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                        .where('c', casing)
                        .where('g', glass)
                        .where('f', frames(GTMaterials.Steel))
                        .where('s', any())
                        .where('#', air())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new net.minecraft.resources.ResourceLocation("gtceu", "block/casings/solid/machine_casing_solid_steel"),
                    new net.minecraft.resources.ResourceLocation("gtceu", "block/multiblock/distillation_tower")))
            .register();

    // ---------------- Draconic Fusion Crafter (KubaTech) ----------------
    public static final MultiblockMachineDefinition DRACONIC_FUSION_CRAFTER = REGISTRATE
            .multiblock("draconic_fusion_crafter", com.omnicrox.gtaa.common.machine.multiblock.DraconicFusionCrafterMachine::new)
            .langValue("Draconic Fusion Crafter")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.DRACONIC_FUSION)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.DraconicFusionCrafterMachine::recipeModifier,
                    com.gregtechceu.gtceu.common.data.GTRecipeModifiers.PARALLEL_HATCH, com.gregtechceu.gtceu.common.data.GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eFusion Crafter"),
                    Component.literal("\u00a77Runs every \u00a7dDraconic Evolution\u00a77 fusion crafting recipe, no injectors needed"),
                    Component.literal("\u00a77Core casings set the highest recipe tier: Draconium, Wyvern, Draconic, Chaotic"),
                    Component.literal("\u00a77Each core tier above the recipe halves its time"),
                    Component.literal("\u00a77Chaotic cores need \u00a7eFusion Machine Casing MK II"),
                    Component.literal("\u00a77Accepts a Parallel Hatch"),
                    Component.literal("\u00a78Ported from GT New Horizons (KubaTech)"))
            .appearanceBlock(GTAABlocks.DEFC_CASING)
            .pattern(definition -> {
                var casing = blocks(GTAABlocks.DEFC_CASING.get());
                return FactoryBlockPattern.start(com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.RIGHT,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.BACK,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.UP)
                        .aisle("NN~NN", "NNNNN", "NNNNN", "NNNNN", "NNNNN")
                        .aisle("     ", "  F  ", " FfF ", "  F  ", "     ")
                        .aisle("     ", "  F  ", " FfF ", "  F  ", "     ")
                        .aisle("RRRRR", "R F R", "RFfFR", "R F R", "RRRRR")
                        .aisle("     ", "  F  ", " FfF ", "  F  ", "     ")
                        .aisle("     ", "  F  ", " FfF ", "  F  ", "     ")
                        .aisle("RRRRR", "R F R", "RFfFR", "R F R", "RRRRR")
                        .aisle("     ", "  F  ", " FfF ", "  F  ", "     ")
                        .aisle("     ", "  F  ", " FfF ", "  F  ", "     ")
                        .aisle("nnnnn", "nnnnn", "nnnnn", "nnnnn", "nnnnn")
                        .where('~', controller(blocks(definition.get())))
                        .where('N', casing.or(autoAbilities(definition.getRecipeTypes()))
                                .or(abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1))
                                .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                        .where('n', casing)
                        .where('f', blocks(GTBlocks.FUSION_COIL.get()))
                        .where('F', com.omnicrox.gtaa.common.machine.multiblock.DraconicFusionCrafterMachine.fusionCasings())
                        .where('R', com.omnicrox.gtaa.common.machine.multiblock.DraconicFusionCrafterMachine.cores())
                        .where(' ', any())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/defc_0"),
                    new net.minecraft.resources.ResourceLocation("gtceu", "block/multiblock/fusion_reactor")))
            .register();

    // ---------------- Mystical Cultivation Array (KubaTech EIG) ----------------
    public static final MultiblockMachineDefinition MYSTICAL_CULTIVATION_ARRAY = REGISTRATE
            .multiblock("mystical_cultivation_array", com.omnicrox.gtaa.common.machine.multiblock.MysticalCultivationArrayMachine::new)
            .langValue("Mystical Cultivation Array")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.CULTIVATION)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eCrop Farm"),
                    Component.literal("\u00a77Put seeds in an input bus: each seed is one crop and is never used up"),
                    Component.literal("\u00a77Works with \u00a7dMystical Agriculture\u00a77 seeds and any normal crop"),
                    Component.literal("\u00a77Every harvest gives \u00a7e4x\u00a77 a normal crop's drops on dirt or farmland"),
                    Component.literal("\u00a77Mystical Agriculture farmland raises it: Inferium 5x, Prudentium 6x, Tertium 7x,"),
                    Component.literal("\u00a77Imperium 8x, Supremium 10x, Insanium 12x (all soil the same kind)"),
                    Component.literal("\u00a77Glass limits the voltage: Glass HV, Tempered IV, Laminated LuV, Fusion Glass any"),
                    Component.literal("\u00a77Harvests every 5 s at LV, twice as fast each tier, down to 0.25 s at LuV"),
                    Component.literal("\u00a77Room for 64 crops at LV, doubling each tier (max 8192)"),
                    Component.literal("\u00a77Uses 1 L of water per 4 crops each second"),
                    Component.literal("\u00a78Based on the GT New Horizons Extreme Industrial Greenhouse"))
            .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
            .pattern(definition -> {
                var casing = blocks(GTBlocks.CASING_STAINLESS_CLEAN.get());
                var glass = com.omnicrox.gtaa.common.machine.multiblock.MysticalCultivationArrayMachine.glass();
                return FactoryBlockPattern.start(com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.RIGHT,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.BACK,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.UP)
                        .aisle("aaa~aaa", "aaaaaaa", "aaaaaaa", "aaaaaaa", "aaaaaaa", "aaaaaaa", "aaaaaaa", "aaaaaaa", "aaaaaaa")
                        .aisle("aaaaaaa", "awdddwa", "awdddwa", "awdddwa", "awdddwa", "awdddwa", "awdddwa", "awdddwa", "aaaaaaa")
                        .aisle("fgggggf", "g     g", "g     g", "g     g", "f     f", "g     g", "g     g", "g     g", "fgggggf")
                        .aisle("fgggggf", "g     g", "g     g", "g     g", "f     f", "g     g", "g     g", "g     g", "fgggggf")
                        .aisle("fgggggf", "g     g", "g     g", "g     g", "f     f", "g     g", "g     g", "g     g", "fgggggf")
                        .aisle(" fgggf ", " g   g ", " g   g ", " g   g ", " flllf ", " g   g ", " g   g ", " g   g ", " fgggf ")
                        .aisle("  fff  ", "  ggg  ", "  ggg  ", "  ggg  ", "  fff  ", "  ggg  ", "  ggg  ", "  ggg  ", "  fff  ")
                        .where('~', controller(blocks(definition.get())))
                        .where('a', casing.or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1))
                                .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1))
                                .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1))
                                .or(abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                                .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                        .where('g', glass)
                        .where('f', frames(GTMaterials.StainlessSteel))
                        .where('l', blocks(net.minecraft.world.level.block.Blocks.REDSTONE_LAMP))
                        .where('d', com.omnicrox.gtaa.common.machine.multiblock.MysticalCultivationArrayMachine.soil())
                        .where('w', waterOrAir())
                        .where(' ', any())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new net.minecraft.resources.ResourceLocation("gtceu", "block/casings/solid/machine_casing_clean_stainless_steel"),
                    new net.minecraft.resources.ResourceLocation("gtceu", "block/multiblock/large_chemical_reactor")))
            .register();

    // ---------------- Suit Fabrication Station ----------------
    public static final MultiblockMachineDefinition SUIT_FABRICATION_STATION = REGISTRATE
            .multiblock("suit_fabrication_station", com.omnicrox.gtaa.common.machine.multiblock.SuitFabricationStationMachine::new)
            .langValue("Suit Fabrication Station")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.SUIT_FABRICATION)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.SuitFabricationStationMachine::recipeModifier,
                    com.gregtechceu.gtceu.common.data.GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .tooltips(
                    Component.literal("\u00a77Machine Type: \u00a7eHero Suit Fabricator"),
                    Component.literal("\u00a77Makes hero suits; every suit comes with its own \u00a7dhero kit\u00a77 of powers"),
                    Component.literal("\u00a77Casing tier sets which suits it can make:"),
                    Component.literal("\u00a77 1 Steel Booth (MV), 2 Showcase Pod (EV),"),
                    Component.literal("\u00a77 3 Holo Chamber (LuV), 4 Ascension Forge (UHV)"),
                    Component.literal("\u00a77Same building for every tier: swap the casings to upgrade"),
                    Component.literal("\u00a77Use an ME Input Bus + a pattern to autocraft missing parts"))
            .appearanceBlock(GTAABlocks.SUIT_CASING_T1)
            .pattern(definition -> {
                var casing = com.omnicrox.gtaa.common.machine.multiblock.SuitFabricationStationMachine.casings();
                var glass = blocks(GTBlocks.CASING_TEMPERED_GLASS.get(), GTBlocks.CASING_LAMINATED_GLASS.get(),
                        GTBlocks.FUSION_GLASS.get(), net.minecraft.world.level.block.Blocks.GLASS);
                var monitor = blocks(GTAABlocks.SUIT_MONITOR.get());
                return FactoryBlockPattern.start(com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.RIGHT,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.BACK,
                                com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.UP)
                        .aisle("CC~CC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                        .aisle("cgggc", "c###c", "c#p#c", "c###c", "ccccc")
                        .aisle("cgggc", "c###c", "c###c", "c###c", "ccccc")
                        .aisle("cgggc", "c###c", "c###c", "c###c", "ccccc")
                        .aisle("cmmmc", "m###m", "m###m", "m###m", "cmmmc")
                        .aisle("ccccc", "ccccc", "ccLcc", "ccccc", "ccccc")
                        .where('~', controller(blocks(definition.get())))
                        .where('C', casing.or(autoAbilities(definition.getRecipeTypes()))
                                .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1)))
                        .where('c', casing)
                        .where('g', glass)
                        .where('m', monitor)
                        .where('p', any())
                        .where('L', blocks(net.minecraft.world.level.block.Blocks.REDSTONE_LAMP, net.minecraft.world.level.block.Blocks.GLOWSTONE,
                                net.minecraft.world.level.block.Blocks.SEA_LANTERN))
                        .where('#', air())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/suit_casing_t1"),
                    new net.minecraft.resources.ResourceLocation("gtceu", "block/multiblock/large_chemical_reactor"))
                    .andThen(b -> b.addDynamicRenderer(() -> new com.omnicrox.gtaa.client.SuitHologramRender())))
            .hasBER(true)
            .register();

    public static void init() {}
}
