package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.omnicrox.gtaa.common.block.SpaceElevatorCableBlock;
import com.omnicrox.gtaa.common.block.SpaceElevatorCableBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.omnicrox.gtaa.GTAA.REGISTRATE;

public class GTAABlocks {

    static {
        REGISTRATE.creativeModeTab(() -> GTAACreativeTab.MAIN);
    }

    // Fusion MK IV / MK V (textures from GT5-Unofficial / GT++, LGPL-3.0)
    public static final BlockEntry<Block> FUSION_CASING_MK4 = casing("fusion_casing_mk4", "Fusion Machine Casing MK IV");
    public static final BlockEntry<Block> FUSION_COIL_MK4 = casing("fusion_coil_mk4", "Advanced Fusion Coil");
    public static final BlockEntry<Block> FUSION_CASING_MK5 = casing("fusion_casing_mk5", "Fusion Machine Casing MK V");
    public static final BlockEntry<Block> FUSION_COIL_MK5 = casing("fusion_coil_mk5", "Ultimate Fusion Coil");

    // Antimatter (SASS / SLAM) - textures from GoodGenerator (GT5-Unofficial)
    public static final BlockEntry<Block> MAGNETIC_FLUX_CASING = casing("magnetic_flux_casing", "Magnetic Flux Casing");
    public static final BlockEntry<Block> GRAVITY_STABILIZATION_CASING = casing("gravity_stabilization_casing", "Gravity Stabilization Casing");
    public static final BlockEntry<Block> ANTIMATTER_CONTAINMENT_CASING = casing("antimatter_containment_casing", "Antimatter Containment Casing");
    public static final BlockEntry<Block> PROTOMATTER_ACTIVATION_COIL = casing("protomatter_activation_coil", "Protomatter Activation Coil");
    public static final BlockEntry<Block> ANTIMATTER_ANNIHILATION_MATRIX = casing("antimatter_annihilation_matrix", "Antimatter Annihilation Matrix");

    // Space Elevator - textures from GTNH-Intergalactic (MIT)
    public static final BlockEntry<Block> SE_BASE_CASING = casing("space_elevator_base_casing", "Space Elevator Base Casing");
    public static final BlockEntry<Block> SE_SUPPORT_STRUCTURE = casing("space_elevator_support_structure", "Space Elevator Support Structure");
    public static final BlockEntry<Block> SE_INTERNAL_STRUCTURE = casing("space_elevator_internal_structure", "Space Elevator Internal Structure");
    public static final BlockEntry<Block> SE_CONCRETE_FLOOR = casing("ultra_high_strength_concrete_floor", "Ultra High Strength Concrete Floor");
    public static final BlockEntry<Block> SE_MOTOR_MK1 = casing("space_elevator_motor_mk1", "Space Elevator Motor MK-I");
    public static final BlockEntry<Block> SE_MOTOR_MK2 = casing("space_elevator_motor_mk2", "Space Elevator Motor MK-II");
    public static final BlockEntry<Block> SE_MOTOR_MK3 = casing("space_elevator_motor_mk3", "Space Elevator Motor MK-III");
    public static final BlockEntry<Block> SE_MOTOR_MK4 = casing("space_elevator_motor_mk4", "Space Elevator Motor MK-IV");
    public static final BlockEntry<Block> SE_MOTOR_MK5 = casing("space_elevator_motor_mk5", "Space Elevator Motor MK-V");

    public static final BlockEntry<SpaceElevatorCableBlock> SPACE_ELEVATOR_CABLE = REGISTRATE
            .block("space_elevator_cable", SpaceElevatorCableBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.strength(5.0f, 10.0f).noOcclusion())
            .addLayer(() -> RenderType::cutout)
            .blockstate(NonNullBiConsumer.noop())
            .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
            .lang("Space Elevator Cable")
            .item(BlockItem::new).model(NonNullBiConsumer.noop()).build()
            .register();

    public static final BlockEntityEntry<SpaceElevatorCableBlockEntity> SPACE_ELEVATOR_CABLE_BE = REGISTRATE
            .blockEntity("space_elevator_cable", SpaceElevatorCableBlockEntity::new)
            .validBlocks(SPACE_ELEVATOR_CABLE)
            .renderer(() -> com.omnicrox.gtaa.client.SpaceElevatorCableRenderer::new)
            .register();

    /** Motor tier for a block (1-5), or 0 if it isn't a motor. */
    public static int motorTier(Block block) {
        if (block == SE_MOTOR_MK1.get()) return 1;
        if (block == SE_MOTOR_MK2.get()) return 2;
        if (block == SE_MOTOR_MK3.get()) return 3;
        if (block == SE_MOTOR_MK4.get()) return 4;
        if (block == SE_MOTOR_MK5.get()) return 5;
        return 0;
    }

    /** Plain casing. Blockstate / models / loot are static files in resources (no datagen needed). */
    public static BlockEntry<Block> casing(String name, String lang) {
        return REGISTRATE.block(name, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.strength(5.0f, 10.0f).isValidSpawn((s, l, pos, e) -> false))
                .addLayer(() -> RenderType::solid)
                .blockstate(NonNullBiConsumer.noop())
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .lang(lang)
                .item(BlockItem::new).model(NonNullBiConsumer.noop()).build()
                .register();
    }

    public static void init() {}
}
