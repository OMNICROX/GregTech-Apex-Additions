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
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("C~C", "C#C", "CCC")
                    .aisle("CCC", "CCC", "CCC")
                    .where('~', controller(blocks(definition.get())))
                    .where('C', io(blocks(GTBlocks.CASING_STEEL_SOLID.get()), 12))
                    .where('#', air())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/solid/machine_casing_solid_steel"),
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
            .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
            .pattern(definition -> {
                var c = blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get());
                return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                        .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                        .aisle("CC~CC", "CXXXC", "CXXXC", "CXXXC", "CCCCC")
                        .aisle("CGGGC", "GXXXG", "GXXXG", "GXXXG", "CGGGC")
                        .aisle("CCCCC", "CXXXC", "CXXXC", "CXXXC", "CCCCC")
                        .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                        .where('~', controller(blocks(definition.get())))
                        .where('C', io(c, 40))
                        .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.FUSION_GLASS.get(), GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()))
                        .where('X', ElectricImplosionCompressorMachine.containmentPredicate())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/solid/machine_casing_robust_tungstensteel"),
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
            .pattern(definition -> {
                var d = blocks(GTAABlocks.EXTREME_DENSITY_CASING.get());
                return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                        .aisle("DDDDDDD", "DDDDDDD", "DDDDDDD", "DDDDDDD", "DDDDDDD", "DDDDDDD", "DDDDDDD")
                        .aisle("DDD~DDD", "DRRRRRD", "DR###RD", "DR###RD", "DR###RD", "DRRRRRD", "DDDDDDD")
                        .aisle("DDHHHDD", "D#####D", "H#####H", "H#####H", "H#####H", "D#####D", "DDHHHDD")
                        .aisle("DDDDDDD", "DRRRRRD", "DR###RD", "DR###RD", "DR###RD", "DRRRRRD", "DDDDDDD")
                        .aisle("DDDDDDD", "DDDDDDD", "DDHHHDD", "DDHHHDD", "DDHHHDD", "DDDDDDD", "DDDDDDD")
                        .where('~', controller(blocks(definition.get())))
                        .where('D', io(d, 80))
                        .where('R', blocks(GTAABlocks.RADIATION_ABSORBENT_CASING.get()))
                        .where('H', blocks(GTAABlocks.HAWKING_GLASS.get()))
                        .where('#', air())
                        .build();
            })
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
                    Component.literal("§b16 parallels§7; coil heat and overclocks like the Electric Blast Furnace"),
                    Component.literal("§8After Twist Space Technology (GTNH addon)"))
            .appearanceBlock(GTBlocks.FUSION_CASING_MK3)
            .pattern(definition -> {
                var c = blocks(GTBlocks.FUSION_CASING_MK3.get());
                return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                        .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                        .aisle("CCC~CCC", "CKKKKKC", "CK###KC", "CK###KC", "CK###KC", "CKKKKKC", "CCCCCCC")
                        .aisle("CGGGGGC", "GKKKKKG", "GK###KG", "GK###KG", "GK###KG", "GKKKKKG", "CGGGGGC")
                        .aisle("CCCCCCC", "CKKKKKC", "CK###KC", "CK###KC", "CK###KC", "CKKKKKC", "CCCCCCC")
                        .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                        .where('~', controller(blocks(definition.get())))
                        .where('C', io(c, 80))
                        .where('K', heatingCoils())
                        .where('G', blocks(GTBlocks.FUSION_GLASS.get()))
                        .where('#', air())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/fusion/fusion_casing_mk3"),
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
                var glass = blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.CASING_TEMPERED_GLASS.get(), GTBlocks.FUSION_GLASS.get());
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
            .appearanceBlock(GTBlocks.FUSION_CASING_MK2)
            .pattern(definition -> {
                var c = blocks(GTBlocks.FUSION_CASING_MK2.get());
                return FactoryBlockPattern.start(RelativeDirection.BACK, RelativeDirection.UP, RelativeDirection.RIGHT)
                        .aisle("CCCCC", "CCCCC", "~CCCC", "CCCCC", "CCCCC")
                        .aisle("PPPPP", "P###P", "P###P", "P###P", "PPRPP").setRepeatable(2, 16)
                        .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                        .where('~', controller(blocks(definition.get())))
                        .where('C', io(c, 20))
                        .where('P', blocks(GTBlocks.FUSION_CASING_MK3.get()))
                        .where('R', ApexModifiers.counted(MiracleTopMachine.CTX, () -> new Block[] { GTBlocks.FUSION_COIL.get() }))
                        .where('#', air())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/fusion/fusion_casing_mk2"),
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
            .pattern(definition -> {
                var m = blocks(GTAABlocks.NANOCHIP_MESH_INTERFACE_CASING.get());
                return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                        .aisle("MMMMMMM", "MMMMMMM", "MMMMMMM", "MMMMMMM", "MMMMMMM", "MMMMMMM", "MMMMMMM")
                        .aisle("RMM~MMR", "MXXXXXM", "MXXXXXM", "MXXXXXM", "MXXXXXM", "MXXXXXM", "RMMMMMR")
                        .aisle("RGGGGGR", "GXXXXXG", "GXXXXXG", "GXXXXXG", "GXXXXXG", "GXXXXXG", "RGGGGGR")
                        .aisle("RMMMMMR", "MXXXXXM", "MXXXXXM", "MXXXXXM", "MXXXXXM", "MXXXXXM", "RMMMMMR")
                        .aisle("RRRRRRR", "RMMMMMR", "RMSSSMR", "RMSSSMR", "RMSSSMR", "RMMMMMR", "RRRRRRR")
                        .where('~', controller(blocks(definition.get())))
                        .where('M', io(m, 60).or(abilities(PartAbility.DATA_ACCESS).setMaxGlobalLimited(2)))
                        .where('R', blocks(GTAABlocks.NANOCHIP_REINFORCEMENT_CASING.get()))
                        .where('X', blocks(GTAABlocks.NANOCHIP_COMPUTATIONAL_MATRIX_CASING.get()))
                        .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.FUSION_GLASS.get()))
                        .where('S', NanochipAssemblyComplexMachine.moduleSlot())
                        .build();
            })
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
            .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
            .pattern(definition -> {
                var c = blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get());
                return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                        .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                        .aisle("CCC~CCC", "CFFFFFC", "CFGGGFC", "CFFFFFC", "CCCCCCC")
                        .aisle("CCCCCCC", "CF###FC", "CG###GC", "CF###FC", "CCCCCCC")
                        .aisle("CCCCCCC", "CF###FC", "CG###GC", "CF###FC", "CCCCCCC")
                        .aisle("CCCCCCC", "CFFFFFC", "CFGGGFC", "CFFFFFC", "CCCCCCC")
                        .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                        .where('~', controller(blocks(definition.get())))
                        .where('C', io(c, 80))
                        .where('F', frames(com.gregtechceu.gtceu.common.data.GTMaterials.TungstenSteel))
                        .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                        .where('#', air())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/solid/machine_casing_robust_tungstensteel"),
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
            .pattern(definition -> {
                var c = blocks(GTAABlocks.PUR_NAQUADAH_CASING.get());
                return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                        .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                        .aisle("CCC~CCC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC")
                        .aisle("CCCCCCC", "CG###GC", "CG###GC", "CG###GC", "CG###GC", "CG###GC", "CCCCCCC")
                        .aisle("CCCCCCC", "CG###GC", "CG#P#GC", "CG#P#GC", "CG#P#GC", "CG###GC", "CCCCCCC")
                        .aisle("CCCCCCC", "CG###GC", "CG###GC", "CG###GC", "CG###GC", "CG###GC", "CCCCCCC")
                        .aisle("CCCCCCC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC")
                        .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                        .where('~', controller(blocks(definition.get())))
                        .where('C', io(c, 120))
                        .where('G', blocks(GTAABlocks.PUR_STERILE_CASING.get(), GTBlocks.FUSION_GLASS.get()))
                        .where('P', blocks(GTAABlocks.PUR_PLASMA_HEATING_CASING.get()))
                        .where('#', air())
                        .build();
            })
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
                        .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                        .where('#', air())
                        .build();
            })
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/hpca/high_power_casing"),
                    gt("block/multiblock/assembly_line")))
            .register();


    // ======================= GT-Not-Leisure ports =======================
    // Contains code from GT-Not-Leisure (https://github.com/GTNotLeisure/GT-Not-Leisure, GPL-3.0 with the additional
    // terms in LICENSE-ADDITIONAL-TERMS.md). Project authors include HFstudio, LineBroke and other contributors.
    // Each machine keeps its original author line.
    public static final String GTNL_NLXCJH = "§7Add Mod: §bGT Not Leisure§7: §6§l§n年轮新城计划 by 咸到老时变成鱼";
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
                    .where('A', blocks(modBlock("gtca:heat_resistant_trinium_plated_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
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
                    .where('A', blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.FUSION_GLASS.get(), GTBlocks.CASING_TEMPERED_GLASS.get()))
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
                    .where('A', blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.FUSION_GLASS.get(), GTBlocks.CASING_TEMPERED_GLASS.get()))
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
                    .where('A', blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.FUSION_GLASS.get(), GTBlocks.CASING_TEMPERED_GLASS.get()))
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
                    .where('A', blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.FUSION_GLASS.get(), GTBlocks.CASING_TEMPERED_GLASS.get()))
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
                    .where('H', blocks(GTBlocks.FUSION_GLASS.get()))
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
            .model(createWorkableCasingMachineModel(gt("block/casings/fusion/fusion_casing_mk3"),
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
                    .where('A', blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.FUSION_GLASS.get(), GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('B', blocks(modBlock("gtna:dimensionally_transcendent_casing", GTBlocks.FUSION_CASING_MK3::get)))
                    .where('C', blocks(modBlock("gtna:hollow_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('D', blocks(modBlock("gtna:molecular_coil", GTBlocks.SUPERCONDUCTING_COIL::get)))
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
                    .where('I', blocks(GTBlocks.FUSION_CASING_MK3.get()))
                    .where('J', blocks(modBlock("gtna:dyson_receiver_casing", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('K', blocks(modBlock("gtna:dyson_control_casing", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('L', blocks(modBlock("gtna:dyson_control_toroid", GTBlocks.HIGH_POWER_CASING::get)))
                    .where('M', blocks(GTBlocks.FUSION_GLASS.get()))
                    .where('N', blocks(modBlock("gtna:infinity_frame", () -> modBlock("gtceu:neutronium_frame", GTBlocks.FUSION_COIL::get))))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/fusion/fusion_casing_mk3"),
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
                    .where('D', blocks(GTBlocks.CASING_LAMINATED_GLASS.get(), GTBlocks.FUSION_GLASS.get(), GTBlocks.CASING_TEMPERED_GLASS.get()))
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
                    .where('E', blocks(modBlock("gtca:heat_resistant_trinium_plated_casing", GTBlocks.CASING_TUNGSTENSTEEL_ROBUST::get)))
                    .where('F', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                    .where('G', blocks(GTBlocks.FUSION_GLASS.get(), GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .where('H', blocks(net.minecraft.world.level.block.Blocks.GLOWSTONE))
                    .where('I', blocks(net.minecraft.world.level.block.Blocks.FARMLAND))
                    .where('J', blocks(net.minecraft.world.level.block.Blocks.WATER))
                    .where(' ', any())
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(gt("block/casings/gcym/watertight_casing"),
                    gt("block/multiblock/implosion_compressor")))
            .register();

}
