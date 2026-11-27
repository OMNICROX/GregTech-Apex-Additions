package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import com.omnicrox.gtaa.common.machine.multiblock.gtnh.BlackHoleCompressorMachine;
import com.omnicrox.gtaa.common.machine.multiblock.gtnh.CircuitAssemblyLineMachine;
import com.omnicrox.gtaa.common.machine.multiblock.gtnh.ElectricImplosionCompressorMachine;
import com.omnicrox.gtaa.common.machine.multiblock.gtnh.MiracleTopMachine;
import com.omnicrox.gtaa.common.machine.multiblock.gtnh.NanochipAssemblyComplexMachine;
import com.omnicrox.gtaa.common.machine.multiblock.gtnh.PlasmaForgePrototypeMachine;
import com.omnicrox.gtaa.common.machine.multiblock.tst.SiliconRockSynthesizerMachine;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static com.omnicrox.gtaa.GTAA.REGISTRATE;

/** Non-power multis ported from GT New Horizons 2.9 and its addons (Twist Space Technology). */
public final class GTAAGTNHMachines {

    private GTAAGTNHMachines() {}

    private static ResourceLocation gt(String path) {
        return new ResourceLocation("gtceu", path);
    }

    private static TraceabilityPredicate io(TraceabilityPredicate casing, int minCasing) {
        return casing.setMinGlobalLimited(minCasing)
                .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1))
                .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1).setPreviewCount(1))
                .or(abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1));
    }

    // ---------------- Industrial Rock Breaker (GT++) ----------------
    public static final MultiblockMachineDefinition INDUSTRIAL_ROCK_BREAKER = REGISTRATE
            .multiblock("industrial_rock_breaker", WorkableElectricMultiblockMachine::new)
            .langValue("Industrial Rock Breaker")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.INDUSTRIAL_ROCK_BREAKER)
            .recipeModifiers(ApexModifiers.parallel(m -> 8 * Math.max(1, ApexModifiers.tier(m)), m -> 1.0 / 3.0, 1.0),
                    GTRecipeModifiers.OC_NON_PERFECT)
            .tooltips(
                    Component.literal("§7Machine Type: §eRock Breaker"),
                    Component.literal("§7Use a §eProgrammed Circuit§7 to pick the block:"),
                    Component.literal("§61§7 Cobblestone, §62§7 Stone, §63§7 Obsidian (uses Redstone), §64§7 Basalt"),
                    Component.literal("§65§7 Deepslate, §66§7 Netherrack, §67§7 End Stone (needs one of the block)"),
                    Component.literal("§b8 parallels§7 per voltage tier, §a200% faster"),
                    Component.literal("§8Ported from GT New Horizons (GT++)"))
            .appearanceBlock(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gt5u", "industrial_rock_breaker")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', frames(com.gregtechceu.gtceu.common.data.GTMaterials.Tungsten))
                    .where('B', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('C', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()).setMinGlobalLimited(30)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.MUFFLER).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('D', blocks(net.minecraft.world.level.block.Blocks.LAVA).or(air()))
                    .where('E', blocks(net.minecraft.world.level.block.Blocks.WATER).or(air()))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/gcym/high_temperature_smelting_casing"),
                    GTAA.id("block/multiblock/industrial_rock_breaker")))
            .register();

    // ---------------- Electric Implosion Compressor (GT5-Unofficial) ----------------
    public static final MultiblockMachineDefinition ELECTRIC_IMPLOSION_COMPRESSOR = REGISTRATE
            .multiblock("electric_implosion_compressor", ElectricImplosionCompressorMachine::new)
            .langValue("Electric Implosion Compressor")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.ELECTRIC_IMPLOSION)
            .recipeModifiers(ApexModifiers.parallel(ElectricImplosionCompressorMachine::maxParallel, m -> 1.0, 1.0),
                    GTRecipeModifiers.OC_NON_PERFECT)
            .tooltips(
                    Component.literal("§7Machine Type: §eImplosion Compressor"),
                    Component.literal("§7Runs every Implosion Compressor recipe §awithout explosives"),
                    Component.literal("§7The 27 containment blocks set the parallels:"),
                    Component.literal("§fNeutronium§7 1, §cInfinity§7 4, §8Transcendent Metal§7 16,"),
                    Component.literal("§5Spacetime§7 64, §3Universium§7 256"),
                    Component.literal("§8Ported from GT New Horizons"))
            .appearanceBlock(GTAABlocks.PUR_NAQUADAH_CASING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gt5u", "electric_implosion_compressor")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('B', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()))
                    .where('C', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('D', frames(com.gregtechceu.gtceu.common.data.GTMaterials.Naquadah))
                    .where('E', com.omnicrox.gtaa.common.machine.multiblock.gtnh.ElectricImplosionCompressorMachine.containmentPredicate())
                    .where('F', blocks(GTAABlocks.PUR_NAQUADAH_CASING.get()).setMinGlobalLimited(150).or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/pur_naquadah_casing"),
                    gt("block/multiblock/implosion_compressor")))
            .register();

    // ---------------- Black Hole Compressor (GT5-Unofficial 2.9) ----------------
    public static final MultiblockMachineDefinition BLACK_HOLE_COMPRESSOR = REGISTRATE
            .multiblock("black_hole_compressor", BlackHoleCompressorMachine::new)
            .langValue("Pseudostable Black Hole Containment Field")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.COMPRESSOR_RECIPES, GTAARecipeTypes.NEUTRONIUM_COMPRESSION)
            .recipeModifiers(BlackHoleCompressorMachine::recipeModifier)
            .tooltips(
                    Component.literal("§7Machine Type: §eCompressor, Neutronium Compressor"),
                    Component.literal("§dUses the immense power of the event horizon to compress things"),
                    Component.literal("§7Insert a §eBlack Hole Seed§7 to open a black hole at §b100§7 stability"),
                    Component.literal("§7Stability decays §b1/s§7; at §c0§7 it voids outputs and closes after a minute"),
                    Component.literal("§7Feed §5Spacetime§7 to halt decay: 1 L/s, doubling every 30 s it is held"),
                    Component.literal("§7A §eBlack Hole Collapser§7 closes it (open a new one to reset)"),
                    Component.literal("§7Neutronium Compressor mode (Avaritia singularities) needs an open black hole"),
                    Component.literal("§b8 parallels§7 per voltage tier, §a500% speed§7, §a70% EU"),
                    Component.literal("§7Parallels §bx2 / x4§7 below 50 / 20 stability (compressor uncapped below 20)"),
                    Component.literal("§8Ported from GT New Horizons"))
            .appearanceBlock(GTAABlocks.EXTREME_DENSITY_CASING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gt5u", "black_hole_compressor")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTAABlocks.HAWKING_GLASS.get()))
                    .where('B', blocks(GTAABlocks.EXTREME_DENSITY_CASING.get()).setMinGlobalLimited(600).or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('C', blocks(GTAABlocks.RADIATION_ABSORBENT_CASING.get()))
                    .where('D', frames(com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahAlloy))
                    .where('E', blocks(GTAABlocks.RADIATION_ABSORBENT_CASING.get()).or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/extreme_density_casing"),
                    GTAA.id("block/multiblock/black_hole_compressor")))
            .register();

    // ---------------- DTPF Prototype MK-II (Twist Space Technology) ----------------
    private static GTRecipeType plasmaForge() {
        for (String id : new String[] { "gtceu:plasma_forge", "gtna:plasma_forge" }) {
            GTRecipeType t = GTRegistries.RECIPE_TYPES.get(new ResourceLocation(id));
            if (t != null) return t;
        }
        return GTRecipeTypes.BLAST_RECIPES;
    }

    public static final MultiblockMachineDefinition PLASMA_FORGE_PROTOTYPE = REGISTRATE
            .multiblock("plasma_forge_prototype_mk2", PlasmaForgePrototypeMachine::new)
            .langValue("Dimensionally Transcendent Matter Plasma Forge Prototype MK-II")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(plasmaForge())
            .recipeModifiers(PlasmaForgePrototypeMachine::recipeModifier)
            .tooltips(
                    Component.literal("§7Machine Type: §ePlasma Forge"),
                    Component.literal("§9§oIf the facts don't fit the theory, change the facts."),
                    Component.literal("§7The prototype of the Dimensionally Transcendent Plasma Forge; runs its recipes"),
                    Component.literal("§7Better at continuous long-term operation and mass production:"),
                    Component.literal("§7after §c24 h§7 of continuous running it uses §b75%§7 less catalyst fuel"),
                    Component.literal("§7Once stopped, the discount falls back to §b0%§7 within §c1 h"),
                    Component.literal("§b16 parallels§7 (a Parallel Hatch adds more); coil heat and overclocks like the Electric Blast Furnace"),
                    Component.literal("§7Twist Space Technology's full 65x43x65 structure: 11,520 coils"),
                    Component.literal("§8After Twist Space Technology (GTNH addon)"))
            .appearanceBlock(() -> modBlock("gtna:infinite_spacetime_energy_boundary_casing", GTBlocks.FUSION_CASING_MK3::get))
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "plasma_forge_mk2")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', heatingCoils())
                    .where('C', blocks(GTAABlocks.MAGNETIC_FLUX_CASING.get()))
                    .where('D', blocks(modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('E', blocks(GTAABlocks.FORCE_FIELD_GLASS.get()))
                    .where('F', blocks(modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get))
                            .or(abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1)))
                    .where('B', blocks(modBlock("gtna:infinite_spacetime_energy_boundary_casing", GTBlocks.FUSION_CASING_MK3::get)).setMinGlobalLimited(3000).or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/eye_of_harmony/boundary"),
                    gt("block/multiblock/fusion_reactor")))
            .register();

    // ---------------- Circuit Assembly Line (GT5-Unofficial) ----------------
    public static final MultiblockMachineDefinition CIRCUIT_ASSEMBLY_LINE = REGISTRATE
            .multiblock("circuit_assembly_line", CircuitAssemblyLineMachine::new)
            .langValue("Circuit Assembly Line")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES)
            .recipeModifiers(CircuitAssemblyLineMachine::recipeModifier)
            .tooltips(
                    Component.literal("§7Machine Type: §eCircuit Assembler"),
                    Component.literal("§6Circuit Assembly Line§7 mode:"),
                    Component.literal("§7- Imprinted by the §efirst circuit§7 it makes; §cimprints are permanent"),
                    Component.literal("§7- Only makes that circuit, §b4 parallels§7 per slice (5-16 slices)"),
                    Component.literal("§6Circuit Assembler§7 mode: any circuit, recipe tier up to energy hatch tier - 1"),
                    Component.literal("§7Screwdriver the controller to change mode"),
                    Component.literal("§aPerforms 4/4 perfect overclocks"),
                    Component.literal("§7Break and replace the controller to clear the imprint"),
                    Component.literal("§8Ported from GT New Horizons"))
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> {
                var c = blocks(GTBlocks.CASING_STEEL_SOLID.get());
                var glass = com.omnicrox.gtaa.common.glass.GTAAGlass.tiered();
                var energy = c.setMinGlobalLimited(10).or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER)
                        .setMinGlobalLimited(1).setMaxGlobalLimited(2).setPreviewCount(1))
                        .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                        .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1).setPreviewCount(1));
                return FactoryBlockPattern.start(RelativeDirection.BACK, RelativeDirection.UP, RelativeDirection.RIGHT)
                        .aisle("CIC", "~AG", "CTC")
                        .aisle("CIC", "GAG", "CTC").setRepeatable(3, 14)
                        .aisle("COC", "GAG", "CTC")
                        .where('~', controller(blocks(definition.get())))
                        .where('C', energy)
                        .where('I', abilities(PartAbility.IMPORT_ITEMS).or(blocks(GTBlocks.CASING_STEEL_SOLID.get())))
                        .where('O', abilities(PartAbility.EXPORT_ITEMS))
                        .where('A', blocks(GTBlocks.CASING_ASSEMBLY_LINE.get()))
                        .where('T', ApexModifiers.counted(CircuitAssemblyLineMachine.CTX,
                                () -> new Block[] { GTBlocks.CASING_ASSEMBLY_CONTROL.get() }))
                        .where('G', glass)
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/solid/machine_casing_solid_steel"),
                    gt("block/multiblock/assembly_line")))
            .register();

    // ---------------- Miracle Top (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition MIRACLE_TOP = REGISTRATE
            .multiblock("miracle_top", MiracleTopMachine::new)
            .langValue("Miracle Top")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES)
            .recipeModifiers(MiracleTopMachine::recipeModifier)
            .tooltips(
                    Component.literal("§7Machine Type: §eCircuit Assembler"),
                    Component.literal("§dI never think about the future because it will come sooner or later."),
                    Component.literal("§7A ring section and a conveying section; §62-16§7 rings"),
                    Component.literal("§b128 parallels§7 per ring, §a400% speed§7 per ring"),
                    Component.literal("§aPerfect overclocks§7 with 8 or more rings"),
                    Component.literal("§8After Twist Space Technology (GTNH addon)"))
            .appearanceBlock(() -> modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get))
            .pattern(definition -> miracleTopPattern(definition, 2))
            .shapeInfos(definition -> variantShapes(miracleTopPattern(definition, 2), miracleTopPattern(definition, 8),
                    miracleTopPattern(definition, 16)))
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/casings/molecular_casing"),
                    gt("block/multiblock/gcym/large_circuit_assembler")))
            .register();

    // ---------------- Nanochip Assembly Complex (GT5-Unofficial 2.9) ----------------
    public static final MultiblockMachineDefinition NANOCHIP_ASSEMBLY_COMPLEX = REGISTRATE
            .multiblock("nanochip_assembly_complex", NanochipAssemblyComplexMachine::new)
            .langValue("Nanochip Assembly Complex")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES, GTRecipeTypes.ASSEMBLY_LINE_RECIPES)
            .recipeModifiers(NanochipAssemblyComplexMachine::recipeModifier)
            .tooltips(
                    Component.literal("§7Machine Type: §eNanochip Assembly Complex"),
                    Component.literal("§7Handles all your circuit assembling needs: every circuit recipe from the"),
                    Component.literal("§7 Circuit Assembler and the Assembly Line (circuits only)"),
                    Component.literal("§7Install §dmodules§7 in the 3x3 bay on the roof (Firewall Casing = empty):"),
                    Component.literal("§7 Assembly Matrix (required), Board Processor, Cutting Chamber, Wire Tracer,"),
                    Component.literal("§7 Etching Array, Biological Coordination, Optical Organizer, Encasement Wrapper,"),
                    Component.literal("§7 Part Processor - each unlocks a circuit family (see the controller)"),
                    Component.literal("§b32 parallels§7 per installed module, §aperfect overclocks"),
                    Component.literal("§8Ported from GT New Horizons 2.9 (condensed)"))
            .appearanceBlock(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gt5u", "nanochip_assembly_complex")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTAABlocks.NANOCHIP_FIREWALL_PROJECTION_CASING.get()))
                    .where('B', blocks(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING.get()))
                    .where('C', blocks(GTAABlocks.NANOCHIP_REINFORCEMENT_CASING.get()))
                    .where('J', blocks(GTAABlocks.NANOCHIP_COMPUTATIONAL_MATRIX_CASING.get()))
                    .where('D', frames(com.gregtechceu.gtceu.common.data.GTMaterials.Naquadah))
                    .where('E', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('F', com.omnicrox.gtaa.common.machine.multiblock.gtnh.NanochipAssemblyComplexMachine.moduleSlot()
                            .or(blocks(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING.get())))
                    .where('H', blocks(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING.get())
                            .or(abilities(PartAbility.DATA_ACCESS, PartAbility.OPTICAL_DATA_RECEPTION).setMaxGlobalLimited(2).setPreviewCount(1)).or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('I', blocks(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING.get()))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/nanochip_mesh_interface_casing"),
                    GTAA.id("block/multiblock/nanochip_assembly_complex")))
            .register();

    public static void init() {}
    // ---------------- Silicon Rock Synthesizer (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition SILICON_ROCK_SYNTHESIZER = REGISTRATE
            .multiblock("silicon_rock_synthesizer", SiliconRockSynthesizerMachine::new)
            .langValue("Silicon Rock Synthesizer")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.INDUSTRIAL_ROCK_BREAKER)
            .recipeModifiers(SiliconRockSynthesizerMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Rock Breaker")
                    .flavor("Hey, I heard you come from a sky island?")
                    .info("Runs every §eIndustrial Rock Breaker§7 recipe (circuit picks the block)")
                    .parallel("4, x4 per voltage tier above LV")
                    .bonus("Output x4; with 1000 mB water and 1000 mB lava in the hatches, x1024")
                    .info("The water and lava are not used up")
                    .portedFrom("Twist Space Technology (GTNH addon)").build())
            .appearanceBlock(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_SECURE_MACERATION)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "silicon_rock_synthesizer")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()))
                    .where('B', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('C', blocks(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get()))
                    .where('D', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('E', blocks(modBlock("gtca:cosmic_neutronium_frame", () -> modBlock("gtceu:neutronium_frame", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get))))
                    .where('F', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_SECURE_MACERATION.get()))
                    .where('G', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_STRESS_PROOF.get()))
                    .where('H', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_SHOCK_PROOF.get()))
                    .where('I', blocks(modBlock("gtca:pressure_resistant_wall", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('J', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_SECURE_MACERATION.get())
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('L', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_SECURE_MACERATION.get()).or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1)))
                    .where('W', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_SECURE_MACERATION.get()).or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/gcym/secure_maceration_casing"),
                    GTAA.id("block/multiblock/industrial_rock_breaker")))
            .register();

    // ---------------- Super Water Purifier (Twist Space Technology) ----------------
    public static int superWaterParallel(com.gregtechceu.gtceu.api.machine.MetaMachine m) {
        int over = Math.max(0, ApexModifiers.tier(m) - com.gregtechceu.gtceu.api.GTValues.UHV);
        return (int) Math.min(2_000_000L, 256L << (2L * Math.min(10, over)));
    }

    public static final MultiblockMachineDefinition SUPER_WATER_PURIFIER = REGISTRATE
            .multiblock("super_water_purifier", WorkableElectricMultiblockMachine::new)
            .langValue("Super Water Purifier")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.SUPER_WATER_PURIFIER)
            .recipeModifiers(ApexModifiers.parallel(GTAAGTNHMachines::superWaterParallel, m -> 1.0, 1.0),
                    GTRecipeModifiers.OC_NON_PERFECT)
            .tooltips(
                    Component.literal("§7Machine Type: §ePerfect Water Creator"),
                    Component.literal("§7Makes every grade of §bpurified water§7 in one machine, no units needed"),
                    Component.literal("§7A §eProgrammed Circuit§7 picks the grade (1-8)"),
                    Component.literal("§b256 parallels§7 at UHV, x4 per tier above, up to §b2,000,000"),
                    Component.literal("§8Ported from Twist Space Technology (GTNH addon)"))
            .appearanceBlock(GTAABlocks.PUR_NAQUADAH_CASING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "super_water_purifier")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTAABlocks.PUR_NAQUADAH_CASING.get()).setMinGlobalLimited(40)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('B', blocks(GTAABlocks.PUR_STERILE_CASING.get()))
                    .where('C', blocks(GTAABlocks.PUR_PLASMA_HEATING_CASING.get()))
                    .where('D', blocks(modBlock("gtna:six_phased_copper_frame", () -> modBlock("gtceu:naquadah_alloy_frame", GTBlocks.FUSION_CASING_MK3::get))))
                    .where('E', blocks(modBlock("gtapexadditions:pur_quark_containment", GTBlocks.FUSION_COIL::get)))
                    .where('F', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/pur_naquadah_casing"),
                    gt("block/multiblock/distillation_tower")))
            .register();


    // ---------------- Extreme Craft Center (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition EXTREME_CRAFT_CENTER = REGISTRATE
            .multiblock("extreme_craft_center", com.omnicrox.gtaa.common.machine.multiblock.tst.ExtremeCraftCenterMachine::new)
            .langValue("Extreme Craft Center")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Molecular Assembler | Extreme Crafting Table")
                    .flavor("Goodbye, all crafting lag.")
                    .info("An §eME crafting provider§7: connect an ME cable to the controller face")
                    .info("Put §eencoded crafting, smithing or stonecutting patterns§7 into an input bus;")
                    .info("they are pulled inside (up to 1024). Other items go to the output bus")
                    .info("Uses §ano EU§7 and no assemblers; results go straight into the ME network")
                    .bonus("Every pattern crafts §6x magnification§7 per push (x1 up to x1,048,576)")
                    .info("Screwdriver: change magnification. Sneak + screwdriver: take patterns out")
                    .warning("Processing patterns are not accepted")
                    .portedFrom("Twist Space Technology (GTNH addon)").build())
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .pattern(definition -> {
                var c = blocks(GTBlocks.HIGH_POWER_CASING.get());
                var edge = c.setMinGlobalLimited(40)
                        .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                        .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1));
                String[] cap = { "CCCCCCC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC" };
                String[] mid = { "CGGGGGC", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "CGGGGGC" };
                String[] ctl = { "CGG~GGC", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "CGGGGGC" };
                return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                        .aisle(cap).aisle(mid).aisle(mid).aisle(ctl).aisle(mid).aisle(mid).aisle(cap)
                        .where('~', controller(blocks(definition.get())))
                        .where('C', edge)
                        .where('G', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                        .where('#', air())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/hpca/high_power_casing"),
                    gt("block/multiblock/assembly_line")))
            .register();



    // ---------------- structures that come in several lengths (GTNH's repeated pieces) ----------------
    private static final java.util.Map<String, com.gregtechceu.gtceu.api.pattern.BlockPattern> VARIANTS = new java.util.concurrent.ConcurrentHashMap<>();

    /** the preview pages of a multi with several structure lengths */
    public static java.util.List<com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo> variantShapes(
            com.gregtechceu.gtceu.api.pattern.BlockPattern... patterns) {
        java.util.List<com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo> out = new java.util.ArrayList<>();
        for (var bp : patterns) {
            int[] reps = new int[bp.aisleRepetitions.length];
            java.util.Arrays.fill(reps, 1);
            out.add(new com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo(bp.getPreview(reps)));
        }
        return out;
    }

    /** Twist Space Technology's Miracle Top with 2-16 rings */
    public static com.gregtechceu.gtceu.api.pattern.BlockPattern miracleTopPattern(
            com.gregtechceu.gtceu.api.machine.MachineDefinition md, int rings) {
        MultiblockMachineDefinition definition = (MultiblockMachineDefinition) md;
        return VARIANTS.computeIfAbsent("miracle_top_" + rings, k -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "miracle_top_" + rings)
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('M', blocks(modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('H', blocks(modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get)).or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('B', ApexModifiers.counted(com.omnicrox.gtaa.common.machine.multiblock.gtnh.MiracleTopMachine.CTX,
                            () -> new Block[] { modBlock("gtna:molecular_coil", GTBlocks.FUSION_COIL::get) }))
                    .where('C', blocks(modBlock("gtna:hollow_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('D', blocks(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get()))
                    .where('E', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where(' ', any())
                    .build());
    }

    // ======================= GT-Not-Leisure ports =======================
    // Contains code from GT-Not-Leisure (https://github.com/GTNotLeisure/GT-Not-Leisure, GPL-3.0 with the additional
    // terms in LICENSE-ADDITIONAL-TERMS.md). Project authors include HFstudio, LineBroke and other contributors.
    // Each machine keeps its original author line.
    public static final String GTNL_NLXCJH = "§7Add Mod: §bGT Not Leisure§7: §6§l§n年轮新城计划 by 咸到老时变成鱼";
    public static final String GTNL_SRP = "§7Add Mod: §bGT Not Leisure§7: §bStructural Replacement Plan";
    public static final String GTNL_EDEN = "§7Add Mod: §bGT Not Leisure §7X §6伊甸生态园 by 荻角";
    public static final String GTNL_QYZG = "§7Add Mod: §bGT Not Leisure§7: §6§o犰狳重工 GT-Odyssey";

    /** a block of another mod, or the fallback when that mod is missing */
    private static Block modBlock(String id, java.util.function.Supplier<Block> fallback) {
        Block b = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
        return b == null || b == net.minecraft.world.level.block.Blocks.AIR ? fallback.get() : b;
    }

    // ---------------- Alfheim Teleportation Array (GT-Not-Leisure) ----------------
    public static final MultiblockMachineDefinition ALFHEIM_TELEPORTATION_ARRAY = REGISTRATE
            .multiblock("alfheim_teleportation_array",
                    com.omnicrox.gtaa.common.machine.multiblock.gtnl.AlfheimTeleportationArrayMachine::new)
            .langValue("Alfheim Teleportation Array")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.MANA_NEXUS)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.gtnl.AlfheimTeleportationArrayMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips
                    .of("Portal to Alfheim / Nature Spirit Array / Mana Infusion / Rune Altar")
                    .parallel("2147483647")
                    .flavor("Legend says this array can stably transport large amounts of materials.")
                    .info("Runs every §dBotania§7 altar craft: mana pool, runic altar, terra plate, elven trade,")
                    .info(" pure daisy, petal apothecary, Mythic Botany infuser and rune rituals (elven trades: circuit 3)")
                    .info("Needs §bliquid mana§7 (1 mB = 100 mana) in an input hatch, or Mana Hatches")
                    .info("Consumes §b100 mB§7 of liquid mana per second to operate")
                    .bonus("Put an Everlasting Guilty Pool or an Asgard Dandelion in an input bus for unlimited mana")
                    .info("§bMachine does not lose efficiency when overclocked")
                    .info("§aSupports Multi-Amp and Laser Hatches!")
                    .portedFrom("GT-Not-Leisure (GTNH addon)")
                    .author(GTNL_NLXCJH).build())
            .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gtnl", "alfheim")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTBlocks.CASING_TITANIUM_STABLE.get()))
                    .where('B', blocks(modBlock("gtna:pressure_containment_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('C', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('D', blocks(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get()))
                    .where('E', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                            .setMinGlobalLimited(300)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(GTAAMachines.MANA_HATCH_ABILITY).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('F', blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where('G', blocks(modBlock("botania:elf_glass", GTBlocks.CASING_LAMINATED_GLASS::get)))
                    .where('H', blocks(modBlock("botania:mana_glass", GTBlocks.CASING_LAMINATED_GLASS::get)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/solid/machine_casing_robust_tungstensteel"),
                    gt("block/multiblock/fusion_reactor")))
            .register();


    // ---------------- Draconic Fusion Crafting (GT-Not-Leisure) ----------------
    public static final MultiblockMachineDefinition DRACONIC_FUSION_CRAFTING = REGISTRATE
            .multiblock("draconic_fusion_crafting",
                    com.omnicrox.gtaa.common.machine.multiblock.gtnl.DraconicFusionCraftingMachine::new)
            .langValue("Draconic Fusion Crafting")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.DRACONIC_FUSION)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.gtnl.DraconicFusionCraftingMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Fusion Crafting")
                    .info("§68 Parallels§7 (a Parallel Hatch can raise it)")
                    .info("§a200% Speed")
                    .info("§b50% EU Usage")
                    .info("Enables §cPerfect Overclock§7 at machine casing tier 4 (Chaotic) or higher")
                    .info("Core casings set the highest recipe tier; each tier above the recipe halves its time")
                    .info("§aSupports Multi-Amp and Laser Hatches!")
                    .portedFrom("GT-Not-Leisure (GTNH addon)")
                    .author(GTNL_NLXCJH).build())
            .appearanceBlock(GTAABlocks.RADIATION_ABSORBENT_CASING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gtnl", "draconic_fusion")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                    .where('B', blocks(GTAABlocks.PUR_PRESSURE_CASING.get()))
                    .where('C', blocks(GTAABlocks.RADIATION_ABSORBENT_CASING.get()).setMinGlobalLimited(25)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('D', com.omnicrox.gtaa.common.machine.multiblock.DraconicFusionCrafterMachine.cores())
                    .where('E', blocks(modBlock("gtna:electron_permeable_amprosium_coated_glass",
                            () -> modBlock("gtca:neutronium_fused_glass", GTBlocks.FUSION_GLASS::get))))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/radiation_absorbent_casing"),
                    gt("block/multiblock/fusion_reactor")))
            .register();


    // ---------------- Matter Fabricator (GT-Not-Leisure) ----------------
    public static final MultiblockMachineDefinition MATTER_FABRICATOR = REGISTRATE
            .multiblock("matter_fabricator", WorkableElectricMultiblockMachine::new)
            .langValue("Matter Fabricator")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.MATTER_FABRICATOR)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Matter Fabricator")
                    .info("§68 Parallels§7 (a Parallel Hatch can raise it)")
                    .info("§a167% Speed")
                    .info("§b80% EU Usage")
                    .info("Turns §eany ingot or gem§7 into matter (a block counts as 9):")
                    .info(" circuit 1: 640 §eMatter Balls§7, circuit 2: 2000 mB of §dUU Matter Amplifier")
                    .info("§aSupports Multi-Amp and Laser Hatches!")
                    .portedFrom("GT-Not-Leisure (GTNH addon)")
                    .author(GTNL_QYZG).build())
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gtnl", "matter_fabricator")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('B', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()))
                    .where('C', blocks(GTBlocks.MACHINE_CASING_ZPM.get()))
                    .where('D', blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                    .where('E', blocks(GTBlocks.FILTER_CASING.get()))
                    .where('F', blocks(GTBlocks.FUSION_CASING.get()))
                    .where('G', blocks(GTBlocks.HIGH_POWER_CASING.get()).setMinGlobalLimited(100)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('H', frames(com.gregtechceu.gtceu.common.data.GTMaterials.Naquadria))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/hpca/high_power_casing"),
                    gt("block/multiblock/fusion_reactor")))
            .register();


    // ---------------- Exothermic Hearth (GT5-Unofficial) ----------------
    public static final MultiblockMachineDefinition EXOTHERMIC_HEARTH = REGISTRATE
            .multiblock("exothermic_hearth", com.omnicrox.gtaa.common.machine.multiblock.gtnh.ExothermicHearthMachine::new)
            .langValue("Exothermic Hearth")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.gtnh.ExothermicHearthMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Blast Furnace, ExH, MEBF, MBF")
                    .parallel("256")
                    .info("§b-5% EU Usage§7 per 900 K above the recipe requirement")
                    .flavor("Never one...")
                    .info("Increases Heat by §c+100 K§7 for every Voltage Tier past MV")
                    .info("Every §c1,800 K§7 over the recipe requirement grants 1 §dPerfect Overclock")
                    .bonus("While active, the machine heats up and multiplies its parallels up to 2x")
                    .info("Takes §d30 minutes§7 of constant running to reach the maximum multiplier")
                    .info("While not running, the machine rapidly cools down")
                    .info("Optionally, supply §6250-500 mB/s of Molten Blaze§7 to speed up heating by §66x")
                    .info("§aSupports Multi-Amp and Laser Hatches!")
                    .portedFrom("GT5-Unofficial (GT New Horizons)").build())
            .appearanceBlock(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gt5u", "exothermic_hearth")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('B', blocks(GTBlocks.CASING_INVAR_HEATPROOF.get()))
                    .where('C', blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                    .where('D', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('E', heatingCoils())
                    .where('F', blocks(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get()))
                    .where('G', frames(com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahAlloy))
                    .where('H', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()))
                    .where('I', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get())
                            .setMinGlobalLimited(1500)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('J', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.HEAT_VENT.get()))
                    .where('K', abilities(PartAbility.MUFFLER))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/gcym/high_temperature_smelting_casing"),
                    gt("block/multiblock/electric_blast_furnace")))
            .register();


    // ---------------- Crystalline Infinitier (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition CRYSTALLINE_INFINITIER = REGISTRATE
            .multiblock("crystalline_infinitier", com.omnicrox.gtaa.common.machine.multiblock.tst.CrystallineInfinitierMachine::new)
            .langValue("Crystalline Infinitier")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.AUTOCLAVE_RECIPES, GTRecipeTypes.CHEMICAL_BATH_RECIPES)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.tst.CrystallineInfinitierMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Autoclave | Chemical Bath")
                    .flavor("They're here. Grow and multiply without end.")
                    .info("With Gravitation Tech as a medium, we can control growth of crystalline cells more conveniently.")
                    .info("Higher field generator tier and voltage tier mean more parallels")
                    .info("Higher field generator tier means lower energy cost (EU / tier)")
                    .info("A §eStabilisation Field Generator§7 enables §dPerfect Overclock")
                    .bonus("Extra +300% speed in Autoclave mode. Extra +1500% speed in Chemical Bath mode.")
                    .info("Change the mode in the controller GUI")
                    .portedFrom("Twist Space Technology (GTNH addon)")
                    .author("§7Author: §cNxer").build())
            .appearanceBlock(GTAABlocks.EUROPIUM_RADIATION_PROOF_CASING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "crystalline_infinitier")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('B', blocks(GTAABlocks.EUROPIUM_RADIATION_PROOF_CASING.get()).setMinGlobalLimited(1000)
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('C', blocks(modBlock("gtna:molecular_casing", GTBlocks.HIGH_POWER_CASING::get))
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1)))
                    .where('D', com.omnicrox.gtaa.common.machine.multiblock.tst.CrystallineInfinitierMachine.fieldGenerators())
                    .where('E', blocks(modBlock("gtna:hollow_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('F', blocks(modBlock("gtna:molecular_coil", GTBlocks.SUPERCONDUCTING_COIL::get)))
                    .where('G', frames(com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahAlloy))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/europium_radiation_proof_casing"),
                    gt("block/multiblock/implosion_compressor")))
            .register();


    /** a recipe type from another GTCEu addon (GTNA...), or the fallback when that mod is missing */
    private static GTRecipeType extType(String id, GTRecipeType fallback) {
        GTRecipeType t = GTRegistries.RECIPE_TYPES.get(new ResourceLocation(id));
        return t == null ? fallback : t;
    }

    private static final String TST_NXER = "§7Author: §cNxer§7, added by §aTwist Space Technology";

    // ---------------- General Ore Processing Factory (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition ORE_PROCESSING_FACTORY = REGISTRATE
            .multiblock("ore_processing_factory", com.omnicrox.gtaa.common.machine.multiblock.tst.OreProcessingFactoryMachine::new)
            .langValue("General Ore Processing Factory")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(extType("gtna:ore_processing", GTRecipeTypes.MACERATOR_RECIPES))
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.tst.OreProcessingFactoryMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Ore Processor")
                    .flavor("Engineering is the art of directing the great sources of power in nature for the use and convenience of man.")
                    .info("The ores will line up and go in through the entrance and out through the exit.")
                    .info("Runs every §eIntegrated Ore Processor§7 recipe")
                    .info("This machine will not overclock. Progress time is always §66.4s§7.")
                    .info("It will process as many inputs as possible at once, if power allows.")
                    .info("Consumes §63200 mB Lubricant§7 every §612.8s§7.")
                    .portedFrom("Twist Space Technology (GTNH addon)").author(TST_NXER).build())
            .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "ore_processing_factory")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('B', blocks(GTBlocks.CASING_TITANIUM_GEARBOX.get()))
                    .where('C', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                    .where('D', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('E', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()))
                    .where('F', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                    .where('G', blocks(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get()))
                    .where('H', blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where('I', blocks(modBlock("gtna:molecular_coil", GTBlocks.SUPERCONDUCTING_COIL::get)))
                    .where('J', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('K', blocks(GTBlocks.HIGH_POWER_CASING.get())
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('L', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('M', frames(com.gregtechceu.gtceu.common.data.GTMaterials.TungstenSteel))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/solid/machine_casing_robust_tungstensteel"),
                    gt("block/multiblock/large_chemical_reactor")))
            .register();

    // ---------------- Integrated Assembly Matrix (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition INTEGRATED_ASSEMBLY_MATRIX = REGISTRATE
            .multiblock("integrated_assembly_matrix", com.omnicrox.gtaa.common.machine.multiblock.tst.IntegratedAssemblyMatrixMachine::new)
            .langValue("Integrated Assembly Matrix")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.ASSEMBLY_LINE_RECIPES)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.tst.IntegratedAssemblyMatrixMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Assembly Line")
                    .flavor("Imagination can take you anywhere")
                    .info("The built-in integrated logistics system allows you to no longer worry about external logistics design.")
                    .info("Spatial isolation technology brings higher batch production capabilities: §b128 parallels")
                    .info("The cost is §c1.5§7 times the base time.")
                    .info("Still need to use the data access hatch to provide data stick.")
                    .info("Put silver or gold nanites in an input bus to increase the running speed (not used up):")
                    .info("Up to §f256 Silver Nanites§7 increase the running speed to 100% of normal speed.")
                    .info("Or up to §e1024 Gold Nanites§7 increase the running speed to 200% of normal speed,")
                    .info("And §edouble§7 the parallelism.")
                    .portedFrom("Twist Space Technology (GTNH addon)").author(TST_NXER).build())
            .appearanceBlock(GTBlocks.HIGH_POWER_CASING)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "integrated_assembly_matrix")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('B', blocks(GTBlocks.CASING_ASSEMBLY_LINE.get()))
                    .where('C', blocks(GTBlocks.CASING_ASSEMBLY_CONTROL.get()))
                    .where('D', blocks(GTAABlocks.SE_BASE_CASING.get()))
                    .where('E', blocks(GTBlocks.HIGH_POWER_CASING.get()).setMinGlobalLimited(150)
                            .or(abilities(PartAbility.DATA_ACCESS, PartAbility.OPTICAL_DATA_RECEPTION).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('F', blocks(modBlock("gtna:molecular_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('G', blocks(modBlock("gtna:containment_field_generator", GTBlocks.FUSION_COIL::get)))
                    .where('H', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/hpca/high_power_casing"),
                    gt("block/multiblock/assembly_line")))
            .register();

    // ---------------- Nether Interface (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition NETHER_INTERFACE = REGISTRATE
            .multiblock("nether_interface", WorkableElectricMultiblockMachine::new)
            .langValue("Nether Interface")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.NETHER_INTERFACE)
            .recipeModifiers(ApexModifiers.parallel(m -> 64, m -> 1.0, 1.0))
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Otherworld Teleporter")
                    .flavor("The imprisoned souls of an ancient civilization now serve you.")
                    .info("Build portals and transport the dirty but useful resources of Hell back to you.")
                    .info("Machine takes §e1A IV§7 per parallel, up to §b64 parallels§7, one run a minute")
                    .info("Brings back ancient debris, netherite scrap, quartz, glowstone, blaze rods and more")
                    .portedFrom("Twist Space Technology (GTNH addon)").author(TST_NXER).build())
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "nether_interface")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(80)
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('B', frames(com.gregtechceu.gtceu.common.data.GTMaterials.BlackSteel))
                    .where('C', blocks(GTBlocks.CASING_TITANIUM_STABLE.get()))
                    .where('D', blocks(net.minecraft.world.level.block.Blocks.OBSIDIAN))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/solid/machine_casing_solid_steel"),
                    gt("block/multiblock/implosion_compressor")))
            .register();


    // ---------------- Micro SpaceTime Fabricatorio (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition MICRO_SPACETIME_FABRICATORIO = REGISTRATE
            .multiblock("micro_spacetime_fabricatorio", com.omnicrox.gtaa.common.machine.multiblock.tst.MicroSpaceTimeFabricatorioMachine::new)
            .langValue("Micro SpaceTime Fabricatorio")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.MICRO_SPACETIME)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.tst.MicroSpaceTimeFabricatorioMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Artificial SpaceTime Fabricator")
                    .flavor("A little start of Genesis.")
                    .info("Activate the primordial singularity to begin the creation process of the universe, of course, within controllable limits.")
                    .parallel("16")
                    .info("A §eStabilisation Field Generator§7 enables perfect overclock,")
                    .info(" and further upgrades can significantly increase the running speed.")
                    .info("Makes Quantum Anomalies and Nuclear Stars in bulk")
                    .portedFrom("Twist Space Technology (GTNH addon)").author(TST_NXER).build())
            .appearanceBlock(() -> modBlock("gtna:dimensionally_transcendent_casing", GTBlocks.FUSION_CASING_MK3::get))
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "micro_spacetime_fabricatorio")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(modBlock("gtna:dimensionally_transcendent_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('E', blocks(modBlock("gtna:dimensionally_transcendent_casing", GTBlocks.FUSION_CASING_MK3::get))
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1)))
                    .where('B', blocks(GTBlocks.FUSION_CASING_MK3.get()))
                    .where('F', blocks(GTBlocks.FUSION_CASING_MK3.get())
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('C', blocks(modBlock("gtna:transcendentally_amplified_magnetic_confinement_casing", GTBlocks.FUSION_COIL::get)))
                    .where('D', com.omnicrox.gtaa.common.machine.multiblock.tst.CrystallineInfinitierMachine.fieldGenerators())
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/casings/dimensionally_transcendent_casing"),
                    gt("block/multiblock/fusion_reactor")))
            .register();


    // ---------------- Divine Isa Forge (123Technology) ----------------
    /** 123Technology's speed: time / (V ^ (glass / 2 + 1)) ^ (1/3), with the voltage tier standing in for glass */
    public static double isaForgeSpeed(com.gregtechceu.gtceu.api.machine.MetaMachine m) {
        int v = Math.max(1, ApexModifiers.tier(m));
        return 1.0 / Math.cbrt(Math.pow(v, v / 2.0 + 1));
    }

    public static final MultiblockMachineDefinition DIVINE_ISA_FORGE = REGISTRATE
            .multiblock("divine_isa_forge", WorkableElectricMultiblockMachine::new)
            .langValue("Divine Isa Forge")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(extType("gtceu:isamill", GTRecipeTypes.MACERATOR_RECIPES),
                    extType("gtna:isa_mill", GTRecipeTypes.MACERATOR_RECIPES), GTAARecipeTypes.SPACE_ASSEMBLER)
            .recipeModifiers(ApexModifiers.parallel(m -> Integer.MAX_VALUE, GTAAGTNHMachines::isaForgeSpeed, 1.0),
                    GTRecipeModifiers.OC_PERFECT_SUBTICK)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips
                    .of("Laodeng's Ultimate Creation - Divine Isa Machine (IsaMill & Space Assembler)")
                    .flavor("'I rejected the Blood Magic's favor and the Bee's sweetness'")
                    .info("§dIsa Lord Certified")
                    .info("Unimaginable speed and parallel.")
                    .info("Time = recipe time x ( 1 / (Voltage Tier^[(Voltage Tier / 2)+1])^(1/3))")
                    .info("Runs both IsaMill recipe lists and the Space Assembler; switch in the controller GUI")
                    .info("§aSupports Multi-Amp and Laser Hatches!")
                    .portedFrom("123Technology (GTNH addon)")
                    .author("§aAdded by: 123Technology - Make ISA great again!").build())
            .appearanceBlock(() -> modBlock("gtna:molecular_casing", GTBlocks.HIGH_POWER_CASING::get))
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("ote", "divine_isa_forge")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('B', blocks(modBlock("gtna:infinite_spacetime_energy_boundary_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('C', blocks(modBlock("gtna:hollow_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('D', blocks(GTAABlocks.MAGNETIC_FLUX_CASING.get()))
                    .where('E', blocks(modBlock("gtna:molecular_casing", GTBlocks.HIGH_POWER_CASING::get)).setMinGlobalLimited(3000)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.MUFFLER).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('F', blocks(modBlock("gtna:spacetime_compression_field_generator_tier_8", GTBlocks.FUSION_COIL::get)))
                    .where('G', blocks(modBlock("gtna:stabilisation_field_generator_tier_8", GTBlocks.FUSION_COIL::get)))
                    .where('H', blocks(modBlock("gtna:time_acceleration_field_generator_tier_8", GTBlocks.FUSION_COIL::get)))
                    .where('I', blocks(modBlock("gtca:isa_mill_gearbox", () -> modBlock("gtca:isa_mill_casing", GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX::get))))
                    .where('J', blocks(modBlock("gtna:dyson_receiver_casing", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('K', blocks(modBlock("gtna:dyson_control_casing", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('L', blocks(modBlock("gtna:dyson_control_toroid", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('M', blocks(GTAABlocks.FORCE_FIELD_GLASS.get()))
                    .where('N', blocks(modBlock("gtna:infinity_frame", () -> modBlock("gtceu:neutronium_frame", GTBlocks.FUSION_COIL::get))))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/casings/molecular_casing"),
                    gt("block/multiblock/fusion_reactor")))
            .register();


    // ---------------- Spinmatron-2737 (GT5-Unofficial) ----------------
    public static final MultiblockMachineDefinition SPINMATRON = REGISTRATE
            .multiblock("spinmatron", com.omnicrox.gtaa.common.machine.multiblock.gtnh.SpinmatronMachine::new)
            .langValue("Spinmatron-2737")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.CENTRIFUGE_RECIPES)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.gtnh.SpinmatronMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Centrifuge")
                    .info("§64 Parallels§7 per §fTotal Turbine Tier§7 (the rotors in its rotor holders)")
                    .info("§a300% Speed")
                    .info("§b70% EU Usage")
                    .info("Requires §e(Recipe Tier x 10 mB/s)§7 of §6Diesel§7 to run")
                    .info("Upgrade blocks: Naquadah Alloy, Neutronium or Infinity (block + frame)")
                    .info("§aSupports Multi-Amp and Laser Hatches!")
                    .flavor("Maahes guides the way...")
                    .portedFrom("GT5-Unofficial (GT New Horizons)").build())
            .appearanceBlock(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_VIBRATION_SAFE)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gt5u", "spinmatron")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_VIBRATION_SAFE.get()).setMinGlobalLimited(500)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('B', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('C', blocks(GTBlocks.CASING_GRATE.get()))
                    .where('D', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('E', blocks(modBlock("gtca:isa_mill_gearbox", GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX::get)))
                    .where('F', blocks(GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX.get()))
                    .where('G', blocks(modBlock("gtna:supercritical_turbine_casing", GTBlocks.CASING_TUNGSTENSTEEL_TURBINE::get)))
                    .where('H', abilities(PartAbility.ROTOR_HOLDER))
                    .where('a', blocks(modBlock("gtceu:naquadah_alloy_block", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get),
                            modBlock("gtceu:neutronium_block", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get),
                            modBlock("gtna:infinity_block", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('b', blocks(modBlock("gtceu:naquadah_alloy_frame", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get),
                            modBlock("gtceu:neutronium_frame", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get),
                            modBlock("gtna:infinity_frame", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/gcym/vibration_safe_casing"),
                    gt("block/multiblock/large_centrifuge")))
            .register();


    // ---------------- Eden Garden (GT-Not-Leisure) ----------------
    public static final MultiblockMachineDefinition EDEN_GARDEN = REGISTRATE
            .multiblock("eden_garden", WorkableElectricMultiblockMachine::new)
            .langValue("Eden Garden")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(extType("gtna:greenhouse", extType("gtceu:green_house", GTRecipeTypes.MACERATOR_RECIPES)))
            .recipeModifiers(ApexModifiers.parallel(m -> 64 * Math.max(1, ApexModifiers.tier(m)), m -> 0.01, 1.0),
                    GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Crop Farm")
                    .flavor("God said: \"Let the land produce vegetation and various plants.\"")
                    .flavor("And the earth brought forth vegetation, bearing all kinds of fruits and crops.")
                    .flavor("The earth was filled with abundant seeds, and all the land was full of life.")
                    .info("Runs every §aGreenhouse§7 recipe §a100 times faster§7, 64 parallels per voltage tier")
                    .info("No fertilizer or herbicide required!")
                    .info("§aSupports Multi-Amp and Laser Hatches!")
                    .portedFrom("GT-Not-Leisure (GTNH addon)").author(GTNL_EDEN).build())
            .appearanceBlock(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_WATERTIGHT)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gtnl", "eden_garden")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                    .where('B', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_WATERTIGHT.get()).setMinGlobalLimited(1200)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('C', blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                    .where('D', blocks(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get()))
                    .where('E', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                    .where('F', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                    .where('G', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('H', blocks(net.minecraft.world.level.block.Blocks.GLOWSTONE))
                    .where('I', blocks(net.minecraft.world.level.block.Blocks.FARMLAND, net.minecraft.world.level.block.Blocks.DIRT,
                            net.minecraft.world.level.block.Blocks.GRASS_BLOCK, net.minecraft.world.level.block.Blocks.COARSE_DIRT))
                    .where('J', blocks(net.minecraft.world.level.block.Blocks.WATER, net.minecraft.world.level.block.Blocks.WATER_CAULDRON))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/gcym/watertight_casing"),
                    gt("block/multiblock/implosion_compressor")))
            .register();


    // ---------------- Real Annihilation Generator (GT-Not-Leisure) ----------------
    public static final MultiblockMachineDefinition REAL_ANNIHILATION_GENERATOR = REGISTRATE
            .multiblock("real_annihilation_generator", com.omnicrox.gtaa.common.machine.multiblock.gtnl.RealAnnihilationGeneratorMachine::new)
            .langValue("Real · Annihilation Generator")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Real · Annihilation Generator")
                    .flavor("You only need to let the proton and antiproton beams pass quietly from both ends")
                    .flavor("Through the magnetic field into the containment sphere, easy and pleasant!")
                    .info("Its simple appearance is due to the elegance of the theory.")
                    .info("Every 5 s it annihilates all the §dAntimatter§7 in its input hatches: §b1e12 EU§7 per mB")
                    .info("The actual energy production is affected by three levels of blocks")
                    .info(" (time, spacetime and stabilisation field generators): x(1 + tiers / 9)")
                    .info("Maintaining continuous operation will also increase energy output (up to x2).")
                    .info("Energy will be output directly to the wireless EU network.")
                    .portedFrom("GT-Not-Leisure (GTNH addon)").author(GTNL_SRP).build())
            .appearanceBlock(() -> modBlock("gtna:dimensionally_transcendent_casing", GTBlocks.FUSION_CASING_MK3::get))
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gtnl", "annihilation_generator")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', com.omnicrox.gtaa.common.machine.multiblock.gtnl.RealAnnihilationGeneratorMachine.timeFields())
                    .where('B', blocks(modBlock("gtna:accelerator_protection_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('C', blocks(GTAABlocks.COMPACT_FUSION_COIL_MK5.get()))
                    .where('D', blocks(modBlock("gtna:dimensionally_transcendent_casing", GTBlocks.FUSION_CASING_MK3::get))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1)))
                    .where('E', com.omnicrox.gtaa.common.machine.multiblock.gtnl.RealAnnihilationGeneratorMachine.spaceFields())
                    .where('F', blocks(modBlock("gtna:infinite_spacetime_energy_boundary_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('G', blocks(modBlock("gtna:naquadah_alloy_casing", GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING::get)))
                    .where('H', com.omnicrox.gtaa.common.machine.multiblock.gtnl.RealAnnihilationGeneratorMachine.stabFields())
                    .where('I', blocks(modBlock("gtna:dyson_receiver_casing", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('J', blocks(modBlock("gtna:dyson_control_casing", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('K', blocks(modBlock("gtna:dyson_control_toroid", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('L', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/casings/dimensionally_transcendent_casing"),
                    gt("block/multiblock/fusion_reactor")))
            .register();


    // ---------------- Library of Ruina (GT-Not-Leisure) ----------------
    public static final MultiblockMachineDefinition LIBRARY_OF_RUINA = REGISTRATE
            .multiblock("library_of_ruina", WorkableElectricMultiblockMachine::new)
            .langValue("§eLibrary of Ruina")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.LIBRARY_OF_RUINA)
            .recipeModifiers(ApexModifiers.parallel(m -> {
                        int max = 8;
                        if (m instanceof com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController c)
                            for (var part : c.getParts())
                                if (part instanceof com.gregtechceu.gtceu.api.capability.IParallelHatch h)
                                    max = Math.max(max, h.getCurrentParallel());
                        return max;
                    }, m -> 0.6, 0.8),
                    GTRecipeModifiers.OC_PERFECT_SUBTICK)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Eternal Twilight")
                    .info("§68 Parallels§7 (a Parallel Hatch can raise it)")
                    .info("§a167% Speed")
                    .info("§b80% EU Usage")
                    .flavor("Broke the eternal night of the Twilight Forest")
                    .flavor("Things turned into books")
                    .flavor("The library will last forever")
                    .flavor("Radiance, solitude, endless")
                    .flavor("Filled with rare books")
                    .flavor("Useless, indestructible, hidden")
                    .flavor("Here, may you find the book you desire.")
                    .info("Put a §eTwilight Forest boss trophy§7 in an input bus: it brings back that boss's loot")
                    .info(" (the trophy is not used up)")
                    .info("§bMachine does not lose efficiency when overclocked")
                    .info("§aSupports Multi-Amp and Laser Hatches!")
                    .portedFrom("GT-Not-Leisure (GTNH addon)").author(GTNL_NLXCJH).build())
            .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gtnl", "library_of_ruina")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_ATOMIC.get()))
                    .where('B', blocks(GTBlocks.CASING_TITANIUM_STABLE.get()))
                    .where('C', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                    .where('D', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()).setMinGlobalLimited(850)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('E', blocks(com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_STRESS_PROOF.get()))
                    .where('F', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('G', blocks(GTBlocks.COIL_HSSG.get()))
                    .where('H', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('I', blocks(net.minecraft.world.level.block.Blocks.WATER,
                            modBlock("twilightforest:twilight_portal", () -> net.minecraft.world.level.block.Blocks.WATER)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/solid/machine_casing_robust_tungstensteel"),
                    gt("block/multiblock/implosion_compressor")))
            .register();

    // ======================= Blood Magic (Twist Space Technology) =======================
    private static final String TST_TASKEREN = "§7Authors: §cTaskeren§7, §6Goderium§7, added by §aTwist Space Technology";
    private static final String TST_TOTTO = "§7Author: §bTotto§7, added by §aTwist Space Technology";

    private static Block bm(String path, java.util.function.Supplier<Block> fallback) {
        return modBlock("bloodmagic:" + path, fallback);
    }

    private static final String[] BM_RUNES = { "blankrune", "sacrificerune", "selfsacrificerune", "dislocationrune",
            "altarcapacityrune", "bettercapacityrune", "orbcapacityrune", "accelerationrune", "chargingrune",
            "sacrificerune2", "selfsacrificerune2", "dislocationrune2", "altarcapacityrune2", "bettercapacityrune2",
            "orbcapacityrune2", "accelerationrune2", "chargingrune2" };

    /** any glass block (Twist: any glass, without a glass tier) */
    private static TraceabilityPredicate anyGlass() {
        return new TraceabilityPredicate(s -> s.getBlockState().is(net.minecraftforge.common.Tags.Blocks.GLASS),
                () -> new com.lowdragmc.lowdraglib.utils.BlockInfo[] {
                        com.lowdragmc.lowdraglib.utils.BlockInfo.fromBlockState(GTBlocks.CASING_TEMPERED_GLASS.get().defaultBlockState()),
                        com.lowdragmc.lowdraglib.utils.BlockInfo.fromBlockState(GTBlocks.FUSION_GLASS.get().defaultBlockState()) });
    }

    /** a Bloody Hell pool block: empty or Life Essence; records the position */
    private static TraceabilityPredicate bloodPool() {
        return new TraceabilityPredicate(s -> {
            var st = s.getBlockState();
            Block le = bm("life_essence_block", () -> net.minecraft.world.level.block.Blocks.AIR);
            if (!(st.isAir() || st.getBlock() == le || st.canBeReplaced())) return false;
            java.util.Set<Long> set = s.getMatchContext().getOrPut(
                    com.omnicrox.gtaa.common.machine.multiblock.tst.BloodyHellMachine.CTX_BLOOD, new java.util.HashSet<Long>());
            set.add(s.getPos().asLong());
            return true;
        }, () -> new com.lowdragmc.lowdraglib.utils.BlockInfo[] { com.lowdragmc.lowdraglib.utils.BlockInfo.fromBlockState(
                bm("life_essence_block", () -> net.minecraft.world.level.block.Blocks.AIR).defaultBlockState()) });
    }

    /** Twist's Bloody Hell, structure tiers 1-6 */
    public static com.gregtechceu.gtceu.api.pattern.BlockPattern bloodyHellPattern(
            com.gregtechceu.gtceu.api.machine.MachineDefinition md, int tier) {
        MultiblockMachineDefinition definition = (MultiblockMachineDefinition) md;
        return VARIANTS.computeIfAbsent("bloody_hell_" + tier, k -> {
            Block brick = GTAABlocks.GORE_CASING.get();
            java.util.List<Block> runes = new java.util.ArrayList<>();
            for (String r : BM_RUNES) {
                Block b = bm(r, () -> null);
                if (b != null) runes.add(b);
            }
            if (runes.isEmpty()) runes.add(net.minecraft.world.level.block.Blocks.RED_NETHER_BRICKS);
            return com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "bloody_hell_" + tier)
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(GTAABlocks.REINFORCED_STONE_BRICK_CASING.get()))
                    .where('B', blocks(brick))
                    .where('C', blocks(GTAABlocks.ICHOR_DRACONIC_BLOCK.get()))
                    .where('D', blocks(GTAABlocks.BLOODY_ICHORIUM_BLOCK.get()))
                    .where('E', blocks(GTAABlocks.BLOODY_THAUMIUM_BLOCK.get()))
                    .where('F', blocks(GTAABlocks.BLOODY_VOID_BLOCK.get()))
                    .where('G', ApexModifiers.counted(com.omnicrox.gtaa.common.machine.multiblock.tst.BloodyHellMachine.CTX_SPEED,
                                    () -> new Block[] { bm("speedrune", () -> net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK) })
                            .or(ApexModifiers.counted(com.omnicrox.gtaa.common.machine.multiblock.tst.BloodyHellMachine.CTX_SPEED2,
                                    () -> new Block[] { bm("speedrune2", () -> net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK) }))
                            .or(blocks(runes.toArray(new Block[0]))))
                    .where('H', blocks(bm("largebloodstonebrick", () -> net.minecraft.world.level.block.Blocks.RED_NETHER_BRICKS)))
                    .where('I', blocks(bm("dungeon_metal_steadfast", () -> net.minecraft.world.level.block.Blocks.NETHERITE_BLOCK)))
                    .where('J', blocks(bm("dungeon_eye", () -> net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN)))
                    .where('K', blocks(net.minecraft.world.level.block.Blocks.GLOWSTONE))
                    .where('L', blocks(net.minecraft.world.level.block.Blocks.DIAMOND_BLOCK))
                    .where('M', blocks(net.minecraft.world.level.block.Blocks.BEACON))
                    .where('N', blocks(brick)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1)))
                    .where('Z', bloodPool())
                    .where(' ', any())
                    .build();
        });
    }

    // ---------------- Bloody Hell (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition BLOODY_HELL = REGISTRATE
            .multiblock("bloody_hell", com.omnicrox.gtaa.common.machine.multiblock.tst.BloodyHellMachine::new)
            .langValue("Bloody Hell")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.BLOODY_HELL)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.tst.BloodyHellMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Altar of Blood / Alchemy Table / Alchemy Array")
                    .flavor("After years of researching, you finally find the last piece to mass-produce the Blood Magic things!")
                    .flavor("Researches show that the speed of soaking ritual is highly related to the greatness of the altar and the runes it used.")
                    .flavor("And there is a small text on the corner said, \"Speed Runes can be the key.\"")
                    .flavor("Also a weird stranger told you that Armok splashes Blood everywhere in the machine.")
                    .info("Runs every §cBlood Altar§7, §cAlchemy Table§7 and §cAlchemy Array§7 recipe with §cLife Essence§7")
                    .info(" from the input hatches; no power needed")
                    .info("§66 structure tiers§7; tier N counts as a tier N+1 altar")
                    .info("§b(tier)^3 parallels§7; speed +50% per tier above 2, and up to §a+3500%§7 from Speed Runes")
                    .info(" (a Reinforced Speed Rune counts as 3)")
                    .info("Alchemy Table recipes need a §dBlood Orb§7 of their tier in an input bus (not used up)")
                    .info("Tiers 3-6: the §4blood pool§7 fills itself from the input hatches (1000 mB of Life Essence a block)")
                    .portedFrom("Twist Space Technology (GTNH addon)").author(TST_TASKEREN).build())
            .appearanceBlock(GTAABlocks.GORE_CASING)
            .pattern(definition -> bloodyHellPattern(definition, 1))
            .shapeInfos(definition -> variantShapes(bloodyHellPattern(definition, 1), bloodyHellPattern(definition, 2),
                    bloodyHellPattern(definition, 3), bloodyHellPattern(definition, 4), bloodyHellPattern(definition, 5),
                    bloodyHellPattern(definition, 6)))
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(GTAA.id("block/casing/gore_casing"),
                    gt("block/multiblock/implosion_compressor")))
            .register();

    private static TraceabilityPredicate lmmHatches(Block casing, boolean energyMin) {
        return blocks(casing)
                .or(abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(energyMin ? 1 : 0).setPreviewCount(1))
                .or(abilities(PartAbility.MAINTENANCE).setMaxGlobalLimited(1).setPreviewCount(1));
    }

    /** Twist's Laser Meteor Miner, tiers 1-3 */
    public static com.gregtechceu.gtceu.api.pattern.BlockPattern laserMeteorMinerPattern(
            com.gregtechceu.gtceu.api.machine.MachineDefinition md, int tier) {
        MultiblockMachineDefinition definition = (MultiblockMachineDefinition) md;
        return VARIANTS.computeIfAbsent("laser_meteor_miner_" + tier, k -> {
            Block robust = GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get();
            Block neutronium = modBlock("gtna:neutronium_stable_casing", () -> robust);
            Block support = modBlock("gtna:high_strength_support_mechanical_casing", () -> robust);
            Block solar = GTBlocks.CASING_STAINLESS_CLEAN.get();
            return com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "laser_meteor_miner_" + tier)
                    .where('~', controller(blocks(definition.get())))
                    .where('A', anyGlass())
                    .where('B', blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                    .where('C', blocks(GTBlocks.FUSION_COIL.get()))
                    .where('D', blocks(neutronium))
                    .where('E', blocks(modBlock("gtna:iridium_casing", () -> robust)))
                    .where('F', blocks(modBlock("gtceu:trinium_coil_block", () -> robust)))
                    .where('G', frames(com.gregtechceu.gtceu.common.data.GTMaterials.Neutronium))
                    .where('H', frames(com.gregtechceu.gtceu.common.data.GTMaterials.Trinium))
                    .where('I', blocks(modBlock("gtceu:naquadah_coil_block", () -> robust)))
                    .where('J', frames(com.gregtechceu.gtceu.common.data.GTMaterials.StainlessSteel))
                    .where('K', blocks(solar))
                    .where('L', blocks(GTBlocks.CASING_TITANIUM_STABLE.get()))
                    .where('M', blocks(GTAABlocks.ADVANCED_IRIDIUM_PLATED_CASING.get()))
                    .where('N', blocks(support))
                    .where('O', blocks(modBlock("gtca:borsilicate_reinforced_iridium_glass", GTBlocks.FUSION_GLASS::get)))
                    .where('P', blocks(modBlock("gtapexadditions:compact_fusion_coil_mk3", GTBlocks.FUSION_COIL::get)))
                    .where('Q', com.omnicrox.gtaa.common.glass.GTAAGlass.tiered())
                    .where('R', blocks(GTAABlocks.BLOODY_ICHORIUM_BLOCK.get()))
                    .where('S', blocks(GTAABlocks.BLOODY_THAUMIUM_BLOCK.get()))
                    .where('U', blocks(modBlock("gtca:radiant_proof_reinforced_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('V', lmmHatches(support, true))
                    .where('W', lmmHatches(solar, true))
                    .where('X', lmmHatches(neutronium, true))
                    .where('Y', blocks(solar).or(abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1).setPreviewCount(1)))
                    .where('Z', blocks(net.minecraft.world.level.block.Blocks.BEACON))
                    .where(' ', any())
                    .build();
        });
    }

    // ---------------- Laser Meteor Miner (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition LASER_METEOR_MINER = REGISTRATE
            .multiblock("laser_meteor_miner", com.omnicrox.gtaa.common.machine.multiblock.tst.LaserMeteorMinerMachine::new)
            .langValue("Laser Meteor Miner")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.LASER_METEOR_MINER)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Meteor Miner")
                    .flavor("Who needs a pickaxe when you have a laser?")
                    .info("Mines the §cBlood Magic§7 meteor called down by the §cMark of the Falling Tower§7:")
                    .info(" put the ritual's Master Ritual Stone in the laser's column below the machine, and a block")
                    .info(" §e48 blocks above the top of the machine§7, right over the beacon, to catch the meteor")
                    .info("§63 tiers§7: one block (fortune of the pickaxe in the first input bus), one row (Fortune III)")
                    .info(" or one layer (Fortune IV) per cycle")
                    .info("Cycles take §62 s§7 at §bMV§7 and overclock (x4 power, x1.41 speed) with a 16x voltage margin")
                    .warning("Everything within 40 blocks of the meteor center is mined, not only the meteor")
                    .portedFrom("Twist Space Technology (GTNH addon)").author(TST_TOTTO).build())
            .appearanceBlock(() -> modBlock("gtna:high_strength_support_mechanical_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get))
            .pattern(definition -> laserMeteorMinerPattern(definition, 1))
            .shapeInfos(definition -> variantShapes(laserMeteorMinerPattern(definition, 1),
                    laserMeteorMinerPattern(definition, 2), laserMeteorMinerPattern(definition, 3)))
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/casings/high_strength_support_mechanical_casing"),
                    gt("block/multiblock/large_miner")))
            .register();

    // ---------------- Singularity Data Hub (GT-Not-Leisure) ----------------
    public static final MultiblockMachineDefinition SINGULARITY_DATA_HUB = REGISTRATE
            .multiblock("singularity_data_hub", com.omnicrox.gtaa.common.machine.multiblock.gtnl.SingularityDataHubMachine::new)
            .langValue("Singularity Data Hub")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.SINGULARITY_DATA_HUB)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Item / Fluid Vault")
                    .flavor("Infinite storage at your fingertips.")
                    .info("Stores §eunlimited types and amounts§7 of items and fluids")
                    .info("While running it pulls everything out of its input buses and hatches")
                    .info("Connect the §bcontroller§7 to an ME network: the hub is mounted as storage on it")
                    .info("Uses §61/20 of UHV§7 (" + com.omnicrox.apexlib.util.ApexFormat.num(com.gregtechceu.gtceu.api.GTValues.VA[com.gregtechceu.gtceu.api.GTValues.UHV] / 20) +
                            " EU/t); with no energy hatch it draws from the wireless network")
                    .info("The storage is locked while the hub is not running")
                    .info("Broken, the controller keeps its contents")
                    .portedFrom("GT-Not-Leisure (GTNH addon)").author(GTNL_NLXCJH).build())
            .appearanceBlock(() -> modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get))
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("gtnl", "singularity_data_hub")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(modBlock("gtna:t5_me_storage_core", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('B', blocks(modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get)).setMinGlobalLimited(100)
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setPreviewCount(1)))
                    .where('C', blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where('D', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                    .where('E', blocks(modBlock("gtna:neutronium_stable_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('F', blocks(modBlock("gtna:hollow_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('G', blocks(modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('H', blocks(modBlock("gtna:containment_field_generator", GTBlocks.FUSION_COIL::get)))
                    .where('I', blocks(modBlock("gtna:force_field_glass", GTBlocks.FUSION_GLASS::get)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/casings/molecular_casing"),
                    gt("block/multiblock/data_bank")))
            .register();

    // ---------------- Strange Matter Aggregator (Twist Space Technology, adapted) ----------------
    private static TraceabilityPredicate tstBoundary() {
        return blocks(modBlock("gtna:infinite_spacetime_energy_boundary_casing", GTBlocks.FUSION_CASING_MK3::get));
    }

    public static com.gregtechceu.gtceu.api.pattern.BlockPattern strangeMatterAggregatorPattern(
            com.gregtechceu.gtceu.api.machine.MachineDefinition md, int rings) {
        MultiblockMachineDefinition definition = (MultiblockMachineDefinition) md;
        return VARIANTS.computeIfAbsent("strange_matter_aggregator_" + rings, k -> {
            Block boundary = modBlock("gtna:infinite_spacetime_energy_boundary_casing", GTBlocks.FUSION_CASING_MK3::get);
            return com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "strange_matter_aggregator_" + rings)
                    .where('~', controller(blocks(definition.get())))
                    .where('A', blocks(modBlock("gtna:reinforced_temporal_structure_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('B', blocks(modBlock("gtna:reinforced_spatial_structure_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('C', blocks(boundary))
                    .where('D', blocks(GTAABlocks.GRAVITY_STABILIZATION_CASING.get()))
                    .where('E', blocks(GTAABlocks.MAGNETIC_FLUX_CASING.get()))
                    .where('F', blocks(modBlock("gtna:containment_field_generator", GTBlocks.FUSION_COIL::get)))
                    .where('G', blocks(GTAABlocks.FORCE_FIELD_GLASS.get()))
                    .where('H', blocks(boundary)
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setPreviewCount(1)))
                    .where('I', blocks(boundary).or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1)))
                    .where('J', blocks(boundary).or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1)))
                    .where('K', blocks(boundary).or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1)))
                    .where('R', com.omnicrox.gtaa.common.machine.multiblock.tst.StrangeMatterAggregatorMachine.ringPredicate())
                    .where(' ', any())
                    .build();
        });
    }

    public static final MultiblockMachineDefinition STRANGE_MATTER_AGGREGATOR = REGISTRATE
            .multiblock("strange_matter_aggregator", com.omnicrox.gtaa.common.machine.multiblock.tst.StrangeMatterAggregatorMachine::new)
            .langValue("Strange Matter Aggregator")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.STRANGE_MATTER_AGGREGATOR)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Antimatter Breeder")
                    .flavor("Matter is just energy that forgot how to move.")
                    .info("Breeds §5Semi-Stable Antimatter§7 for the Shielded Lagrangian Annihilation Matrix")
                    .info("Each parallel: §d(256 / sqrt(Oscillator rings)) mB§7 of antimatter + hydrogen plasma in,")
                    .info(" §d1 L + 1 L per consecutive run§7 out (up to 24 / 144 / 384 runs by Merger tier)")
                    .info("Uses a §6maintenance fluid§7 every run: SpaceTime, Universium or Magnetohydrodynamically")
                    .info(" Constrained Star Matter (better ones: more rods per Nuclear Star, less plasma, byproducts)")
                    .info("Also eats §eStellar Alloy Frames§7 (1 per L), §eTesseracts§7 and §eNuclear Stars§7;")
                    .info(" an §eExquisite Strange Crystal§7 doubles the byproducts")
                    .info("§63-8 rings§7 behind the main section, at least one of each kind, ring blocks = the Forge")
                    .info(" of the Gods field generators (Spacetime Compression = Oscillator, Stabilisation =")
                    .info(" Constraintor, Time Acceleration = Merger; generator tier 0-2/3-5/6-8 = ring tier 1/2/3)")
                    .info("No energy hatch: the run's power comes from the wireless network")
                    .warning("Adapted: Twist's machine makes fuel rods for its Dyson Sphere line")
                    .portedFrom("Twist Space Technology (GTNH addon)").author(TST_NXER).build())
            .appearanceBlock(() -> modBlock("gtna:infinite_spacetime_energy_boundary_casing", GTBlocks.FUSION_CASING_MK3::get))
            .pattern(definition -> strangeMatterAggregatorPattern(definition, 3))
            .shapeInfos(definition -> variantShapes(strangeMatterAggregatorPattern(definition, 3),
                    strangeMatterAggregatorPattern(definition, 5), strangeMatterAggregatorPattern(definition, 8)))
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/eye_of_harmony/boundary"),
                    gt("block/multiblock/fusion_reactor")))
            .register();

    // ---------------- Mass Fabricator Genesis (Twist Space Technology) ----------------
    public static final MultiblockMachineDefinition MASS_FABRICATOR_GENESIS = REGISTRATE
            .multiblock("mass_fabricator_genesis", com.omnicrox.gtaa.common.machine.multiblock.tst.MassFabricatorGenesisMachine::new)
            .langValue("Mass Fabricator Genesis")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTAARecipeTypes.MASS_FABRICATOR_GENESIS)
            .recipeModifiers(com.omnicrox.gtaa.common.machine.multiblock.tst.MassFabricatorGenesisMachine::recipeModifier)
            .tooltips(com.omnicrox.apexlib.tooltip.ApexTooltips.of("Mass Fabricator")
                    .flavor("Even though the future seems far away, it is actually beginning right now.")
                    .flavor("Energy - Mass : Interface.")
                    .info("Makes §dUU-Matter§7 out of energy")
                    .info("Parallels come from §bParallel Hatches§7: use as many as you like, they add up")
                    .info("§aPerfect overclocks")
                    .portedFrom("Twist Space Technology (GTNH addon)").author(TST_NXER).build())
            .appearanceBlock(() -> modBlock("gtna:infinite_spacetime_energy_boundary_casing", GTBlocks.FUSION_CASING_MK3::get))
            .pattern(definition -> com.omnicrox.gtaa.api.GTAAStructures.pattern("tst", "mass_fabricator_genesis")
                    .where('~', controller(blocks(definition.get())))
                    .where('A', tstBoundary())
                    .where('B', blocks(GTAABlocks.GRAVITY_STABILIZATION_CASING.get()))
                    .where('C', blocks(GTAABlocks.MAGNETIC_FLUX_CASING.get()))
                    .where('D', blocks(modBlock("gtna:molecular_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('E', blocks(modBlock("gtna:hyper_mechanical_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('F', blocks(modBlock("gtna:containment_field_generator", GTBlocks.FUSION_COIL::get)))
                    .where('G', blocks(GTAABlocks.FORCE_FIELD_GLASS.get()))
                    .where('H', tstBoundary()
                            .or(abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                            .or(abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1))
                            .or(abilities(PartAbility.PARALLEL_HATCH).setPreviewCount(1)))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(new ResourceLocation("gtna", "block/eye_of_harmony/boundary"),
                    gt("block/multiblock/fusion_reactor")))
            .register();

}
