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

    // Compact Fusion Computers - coil textures from GoodGenerator (GT5-Unofficial)
    public static final BlockEntry<Block> COMPACT_FUSION_COIL_MK1 = casing("compact_fusion_coil_mk1", "Compact Fusion Coil");
    public static final BlockEntry<Block> COMPACT_FUSION_COIL_MK2 = casing("compact_fusion_coil_mk2", "Advanced Compact Fusion Coil");
    public static final BlockEntry<Block> COMPACT_FUSION_COIL_MK3 = casing("compact_fusion_coil_mk3", "Compact Fusion Coil Mk-II Prototype");
    public static final BlockEntry<Block> COMPACT_FUSION_COIL_MK4 = casing("compact_fusion_coil_mk4", "Compact Fusion Coil Mk-II");
    public static final BlockEntry<Block> COMPACT_FUSION_COIL_MK5 = casing("compact_fusion_coil_mk5", "Compact Fusion Coil Mk-II Finaltype");

    // Naquadah fuel line - textures from GoodGenerator / GT5-Unofficial
    public static final BlockEntry<Block> NAQUADAH_REACTOR_CASING = casing("naquadah_reactor_casing", "Naquadah Reactor Casing");
    public static final BlockEntry<Block> FIELD_RESTRICTION_CASING = casing("field_restriction_casing", "Field Restriction Casing");
    public static final BlockEntry<Block> NAQUADAH_FUEL_REFINERY_CASING = casing("naquadah_fuel_refinery_casing", "Naquadah Fuel Refinery Casing");
    public static final BlockEntry<Block> EUROPIUM_RADIATION_PROOF_CASING = casing("europium_radiation_proof_casing", "Europium Reinforced Radiation Proof Machine Casing");
    public static final BlockEntry<Block> FIELD_RESTRICTION_GLASS = glass("field_restriction_glass", "Field Restriction Glass");
    public static final BlockEntry<Block> RADIATION_PROOF_STEEL_FRAME = glass("radiation_proof_steel_frame", "Radiation Proof Steel Frame Box");
    public static final BlockEntry<Block> FIELD_RESTRICTION_COIL_T1 = casing("field_restriction_coil_t1", "Field Restriction Coil T1");
    public static final BlockEntry<Block> FIELD_RESTRICTION_COIL_T2 = casing("field_restriction_coil_t2", "Field Restriction Coil T2");
    public static final BlockEntry<Block> FIELD_RESTRICTION_COIL_T3 = casing("field_restriction_coil_t3", "Field Restriction Coil T3");
    public static final BlockEntry<Block> FIELD_RESTRICTION_COIL_T4 = casing("field_restriction_coil_t4", "Field Restriction Coil T4");

    public static Block[] fieldRestrictionCoils() {
        return new Block[] { FIELD_RESTRICTION_COIL_T1.get(), FIELD_RESTRICTION_COIL_T2.get(),
                FIELD_RESTRICTION_COIL_T3.get(), FIELD_RESTRICTION_COIL_T4.get() };
    }

    public static int fieldRestrictionCoilTier(Block b) {
        Block[] coils = fieldRestrictionCoils();
        for (int i = 0; i < coils.length; i++) if (coils[i] == b) return i + 1;
        return 0;
    }

    // Component Assembly Line - textures from GoodGenerator / GT5-Unofficial
    public static final BlockEntry<Block> ADVANCED_IRIDIUM_PLATED_CASING = casing("advanced_iridium_plated_casing", "Advanced Iridium Plated Machine Casing");
    /** index = voltage tier (LV=1 .. OpV=13) */
    public static final java.util.List<BlockEntry<Block>> COAL_CASINGS = coalCasings();

    private static java.util.List<BlockEntry<Block>> coalCasings() {
        java.util.List<BlockEntry<Block>> list = new java.util.ArrayList<>();
        list.add(null);
        for (int t = com.gregtechceu.gtceu.api.GTValues.LV; t <= com.gregtechceu.gtceu.api.GTValues.OpV; t++) {
            String vn = com.gregtechceu.gtceu.api.GTValues.VN[t];
            list.add(casing("component_assembly_line_casing_" + vn.toLowerCase(java.util.Locale.ROOT),
                    "Component Assembly Line Casing (" + vn + ")"));
        }
        return list;
    }

    public static int coalCasingTier(Block b) {
        for (int t = 1; t < COAL_CASINGS.size(); t++) if (COAL_CASINGS.get(t).get() == b) return t;
        return 0;
    }

    // Quantum Force Transformer - textures from GT++ (GT5-Unofficial)
    public static final BlockEntry<Block> QFT_MANIPULATOR_T1 = casing("qft_manipulator_t1", "Neutron Pulse Manipulator");
    public static final BlockEntry<Block> QFT_MANIPULATOR_T2 = casing("qft_manipulator_t2", "Cosmic Fabric Manipulator");
    public static final BlockEntry<Block> QFT_MANIPULATOR_T3 = casing("qft_manipulator_t3", "Infinity Infused Manipulator");
    public static final BlockEntry<Block> QFT_MANIPULATOR_T4 = casing("qft_manipulator_t4", "SpaceTime Continuum Ripper");
    public static final BlockEntry<Block> QFT_SHIELDING_T1 = casing("qft_shielding_t1", "Neutron Shielding Core");
    public static final BlockEntry<Block> QFT_SHIELDING_T2 = casing("qft_shielding_t2", "Cosmic Fabric Shielding Core");
    public static final BlockEntry<Block> QFT_SHIELDING_T3 = casing("qft_shielding_t3", "Infinity Infused Shielding Core");
    public static final BlockEntry<Block> QFT_SHIELDING_T4 = casing("qft_shielding_t4", "SpaceTime Bending Core");
    public static final BlockEntry<Block> FORCE_FIELD_GLASS = glass("force_field_glass", "Force Field Glass");
    public static final BlockEntry<Block> QFT_COIL = casing("qft_coil", "Quantum Force Conductor");
    public static final BlockEntry<Block> QFT_CONTAINMENT_CASING = casing("qft_containment_casing", "Particle Containment Casing");

    public static Block[] qftManipulators() {
        return new Block[] { QFT_MANIPULATOR_T1.get(), QFT_MANIPULATOR_T2.get(), QFT_MANIPULATOR_T3.get(), QFT_MANIPULATOR_T4.get() };
    }

    public static Block[] qftShieldings() {
        return new Block[] { QFT_SHIELDING_T1.get(), QFT_SHIELDING_T2.get(), QFT_SHIELDING_T3.get(), QFT_SHIELDING_T4.get() };
    }

    // Purification Plant - textures from GT5-Unofficial
    public static final BlockEntry<Block> PUR_CONCRETE = casing("pur_concrete", "Industrial Strength Concrete");
    public static final BlockEntry<Block> PUR_INDUSTRIAL_CASING = casing("pur_industrial_casing", "Industrial Water Plant Casing");
    public static final BlockEntry<Block> PUR_STERILE_CASING = casing("pur_sterile_casing", "Reinforced Sterile Water Plant Casing");
    public static final BlockEntry<Block> PUR_FLOCCULATION_CASING = casing("pur_flocculation_casing", "Reinforced Sterile Water Plant Casing (Flocculation)");
    public static final BlockEntry<Block> PUR_NAQUADAH_CASING = casing("pur_naquadah_casing", "Naquadah Reinforced Water Plant Casing");
    public static final BlockEntry<Block> PUR_CORROSION_CASING = casing("pur_corrosion_casing", "Extreme Corrosion Resistant Casing");
    public static final BlockEntry<Block> PUR_PRESSURE_CASING = casing("pur_pressure_casing", "High Pressure Resistant Casing");
    public static final BlockEntry<Block> PUR_OZONE_CASING = casing("pur_ozone_casing", "Ozonation Casing");
    public static final BlockEntry<Block> PUR_PLASMA_HEATING_CASING = casing("pur_plasma_heating_casing", "Plasma Heating Casing");
    public static final BlockEntry<Block> PUR_NAQUADRIA_CASING = casing("pur_naquadria_casing", "Naquadria-Reinforced Water Plant Casing");
    public static final BlockEntry<Block> PUR_UV_EMITTER = casing("pur_uv_emitter", "UV Backlight Sterilizer Casing");
    public static final BlockEntry<Block> PUR_QUARK_PIPE = casing("pur_quark_pipe", "Quark Exclusion Casing");
    public static final BlockEntry<Block> PUR_QUARK_RELEASE = casing("pur_quark_release", "Quark Release Chamber");
    public static final BlockEntry<Block> PUR_QUARK_CONTAINMENT = casing("pur_quark_containment", "Femtometer-Calibrated Particle Beam Casing");
    public static final BlockEntry<Block> PUR_PH_GLASS = glass("pur_ph_glass", "pH-Resistant Glass");
    public static final BlockEntry<Block> PUR_UV_GLASS = glass("pur_uv_glass", "Neutronium-Coated UV-Resistant Glass");
    public static final BlockEntry<Block> PUR_INFINITY_GLASS = glass("pur_infinity_glass", "Omni-Purpose Infinity Fused Glass");
    public static final BlockEntry<Block> PUR_QUARK_GLASS = glass("pur_quark_glass", "Quark Containment Glass");

    public static Block compactFusionCoil(int mk) {
        return switch (mk) {
            case 1 -> COMPACT_FUSION_COIL_MK1.get();
            case 2 -> COMPACT_FUSION_COIL_MK2.get();
            case 3 -> COMPACT_FUSION_COIL_MK3.get();
            case 4 -> COMPACT_FUSION_COIL_MK4.get();
            default -> COMPACT_FUSION_COIL_MK5.get();
        };
    }

    // Antimatter (SASS / SLAM) - textures from GoodGenerator (GT5-Unofficial)
    public static final BlockEntry<Block> MAGNETIC_FLUX_CASING = casing("magnetic_flux_casing", "Magnetic Flux Casing");
    public static final BlockEntry<Block> GRAVITY_STABILIZATION_CASING = casing("gravity_stabilization_casing", "Gravity Stabilization Casing");
    // see-through texture: must be glass, or the game culls the blocks behind it and you see through the world
    public static final BlockEntry<Block> ANTIMATTER_CONTAINMENT_CASING = glass("antimatter_containment_casing", "Antimatter Containment Casing");
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

    // ---------------- Draconic Fusion Crafter (KubaTech DEFC) ----------------
    public static final BlockEntry<Block> DEFC_CASING = casing("defc_casing", "Naquadah Alloy Fusion Casing");
    public static final BlockEntry<Block> DEFC_CORE_T1 = casing("defc_core_t1", "Draconium Fusion Casing");
    public static final BlockEntry<Block> DEFC_CORE_T2 = casing("defc_core_t2", "Wyvern Fusion Casing");
    public static final BlockEntry<Block> DEFC_CORE_T3 = casing("defc_core_t3", "Awakened Draconium Fusion Casing");
    public static final BlockEntry<Block> DEFC_CORE_T4 = casing("defc_core_t4", "Chaotic Fusion Casing");

    public static Block[] defcCores() {
        return new Block[] { DEFC_CORE_T1.get(), DEFC_CORE_T2.get(), DEFC_CORE_T3.get(), DEFC_CORE_T4.get() };
    }

    // ---------------- Suit Fabrication Station ----------------
    // Mana Nexus - GTAA's own textures
    public static final BlockEntry<Block> MANA_NEXUS_CASING = casing("mana_nexus_casing", "Livingrock Nexus Casing");
    public static final BlockEntry<Block> MANA_CORE_T1 = casing("mana_core_t1", "Manasteel Mana Core");
    public static final BlockEntry<Block> MANA_CORE_T2 = casing("mana_core_t2", "Terrasteel Mana Core");
    public static final BlockEntry<Block> MANA_CORE_T3 = casing("mana_core_t3", "Elementium Mana Core");
    public static final BlockEntry<Block> MANA_CORE_T4 = casing("mana_core_t4", "Alfsteel Mana Core");
    public static final BlockEntry<Block> MANA_CORE_T5 = casing("mana_core_t5", "Gaiasteel Mana Core");

    public static Block[] manaCores() {
        return new Block[] { MANA_CORE_T1.get(), MANA_CORE_T2.get(), MANA_CORE_T3.get(), MANA_CORE_T4.get(), MANA_CORE_T5.get() };
    }

    public static final BlockEntry<Block> SUIT_CASING_T1 = casing("suit_casing_t1", "Steel Booth Casing");
    public static final BlockEntry<Block> SUIT_CASING_T2 = casing("suit_casing_t2", "Showcase Pod Casing");
    public static final BlockEntry<Block> SUIT_CASING_T3 = casing("suit_casing_t3", "Holo Chamber Casing");
    public static final BlockEntry<Block> SUIT_CASING_T4 = casing("suit_casing_t4", "Ascension Forge Casing");
    public static final BlockEntry<Block> SUIT_MONITOR = casing("suit_monitor", "Suit Display Monitor");

    public static Block[] suitCasings() {
        return new Block[] { SUIT_CASING_T1.get(), SUIT_CASING_T2.get(), SUIT_CASING_T3.get(), SUIT_CASING_T4.get() };
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

    /** see-through casing (glass / frame boxes) */
    public static BlockEntry<Block> glass(String name, String lang) {
        // GlassBlock: faces between two of the same glass are skipped, light passes through
        return REGISTRATE.<Block>block(name, p -> new net.minecraft.world.level.block.GlassBlock(p))
                .initialProperties(() -> Blocks.GLASS)
                .properties(p -> p.strength(5.0f, 10.0f).noOcclusion().isValidSpawn((s, l, pos, e) -> false)
                        .isSuffocating((s, l, pos) -> false).isViewBlocking((s, l, pos) -> false))
                .addLayer(() -> RenderType::cutout)
                .blockstate(NonNullBiConsumer.noop())
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .lang(lang)
                .item(BlockItem::new).model(NonNullBiConsumer.noop()).build()
                .register();
    }

    public static void init() {}
}
