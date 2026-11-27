package com.omnicrox.gtaa.common.glass;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * GT New Horizons-style glass tiers. A multi's glass slots take any tiered glass; the lowest glass in the structure
 * is its glass tier, and (gtapexadditions-common.toml: glassTierLimitsEnergyHatches) an energy hatch above that tier
 * stops it, like BartWorks' borosilicate glass in GTNH.
 */
public final class GTAAGlass {

    private GTAAGlass() {}

    public static final String CTX = "ApexGlassTier";
    private static Map<Block, Integer> tiers;
    private static List<List<Block>> family;

    /** block -> voltage tier: the borosilicate line (HV..UXV) plus GregTech's own glass */
    public static synchronized Map<Block, Integer> tiers() {
        if (tiers == null) {
            tiers = new IdentityHashMap<>();
            for (int i = 0; i < GTAABlocks.TIERED_GLASS.length; i++) tiers.put(GTAABlocks.TIERED_GLASS[i].get(), GTValues.HV + i);
            tiers.put(GTBlocks.CASING_TEMPERED_GLASS.get(), GTValues.HV);
            tiers.put(GTBlocks.CASING_LAMINATED_GLASS.get(), GTValues.IV);
            tiers.put(GTBlocks.FUSION_GLASS.get(), GTValues.LuV);
        }
        return tiers;
    }

    public static int tierOf(Block b) {
        Integer t = tiers().get(b);
        return t == null ? -1 : t;
    }

    /** one list per tier, lowest first (the borosilicate block first, so previews show it) */
    public static synchronized List<List<Block>> family() {
        if (family == null) {
            family = new ArrayList<>();
            for (int t = GTValues.HV; t < GTValues.HV + GTAABlocks.TIERED_GLASS.length; t++) {
                List<Block> l = new ArrayList<>();
                l.add(GTAABlocks.TIERED_GLASS[t - GTValues.HV].get());
                for (var e : tiers().entrySet()) if (e.getValue() == t && !l.contains(e.getKey())) l.add(e.getKey());
                family.add(l);
            }
        }
        return family;
    }

    /** a glass slot: any tiered glass, the lowest tier found goes into the match context */
    public static TraceabilityPredicate tiered() {
        return new TraceabilityPredicate(s -> {
            int t = tierOf(s.getBlockState().getBlock());
            if (t < 0) return false;
            int[] min = s.getMatchContext().getOrPut(CTX, new int[] { Integer.MAX_VALUE });
            min[0] = Math.min(min[0], t);
            return true;
        }, () -> family().stream().flatMap(List::stream)
                .map(b -> BlockInfo.fromBlockState(b.defaultBlockState())).toArray(BlockInfo[]::new));
    }

    /** the glass tier of a formed structure, or -1 if it has no tiered glass */
    public static int structureTier(com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController c) {
        Object o = c.getMultiblockState().getMatchContext().get(CTX);
        return o instanceof int[] a && a[0] != Integer.MAX_VALUE ? a[0] : -1;
    }
}
