package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.data.recipe.CustomTags;
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
