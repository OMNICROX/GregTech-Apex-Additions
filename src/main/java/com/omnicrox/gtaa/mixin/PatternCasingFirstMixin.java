package com.omnicrox.gtaa.mixin;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * "Casing or hatch" spots: GT's auto-builder (Terminal, Structure Upgrader) offers every hatch kind before the casing
 * once the casing isn't a limited predicate, so empty spots fill with input buses. This moves the plain casing to the
 * front of the limited list with no limits of its own: the structure check and the preview behave exactly as before,
 * but the builder now offers the casing first.
 */
@Mixin(value = FactoryBlockPattern.class, remap = false)
public abstract class PatternCasingFirstMixin {

    @Shadow @Final private Char2ObjectMap<TraceabilityPredicate> symbolMap;

    private static final java.util.Set<TraceabilityPredicate> gtaa$DONE =
            java.util.Collections.synchronizedSet(java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>()));

    @Inject(method = "build", at = @At("HEAD"), remap = false, require = 0)
    private void gtaa$casingFirst(CallbackInfoReturnable<?> cir) {
        if (!gtaa$enabled()) return;
        for (TraceabilityPredicate tp : symbolMap.values()) {
            if (tp == null || tp.isController || tp.limited.isEmpty() || tp.common.isEmpty()) continue;
            if (!gtaa$DONE.add(tp)) continue; // shared predicates (one var in several where()s / rebuilt patterns)
            List<SimplePredicate> casings = new ArrayList<>();
            for (SimplePredicate sp : tp.common) if (isPlainBlock(sp)) casings.add(sp);
            if (!casings.isEmpty()) {
                // casing stays in common, at the front (GT's preview fills free spots with the first common
                // predicate), and an unlimited copy leads the limited list (GT's builder tries limited first)
                tp.common.removeAll(casings);
                tp.common.addAll(0, casings);
                for (int i = casings.size() - 1; i >= 0; i--) tp.limited.add(0, copyOf(casings.get(i)));
            } else {
                gtaa$limitedCasingFills(tp);
            }
        }
    }

    private static SimplePredicate copyOf(SimplePredicate sp) {
        SimplePredicate copy = new SimplePredicate(sp.predicate, sp.candidates);
        copy.toolTips = sp.toolTips;
        return copy;
    }

    /**
     * "casing (at least N) or hatches with no limit": GT's preview fills every free spot with the first unlimited
     * hatch (340 item buses on a purification unit). An unlimited copy of the casing at the front of the common list
     * makes the preview and the builder fill with casing; the structure check tests the limited casing first, so its
     * count still works.
     */
    private static void gtaa$limitedCasingFills(TraceabilityPredicate tp) {
        for (SimplePredicate c : tp.common) if (plainCandidates(c)) return;
        boolean hatchFill = false;
        for (SimplePredicate c : tp.common) if (c.previewCount == -1) hatchFill = true;
        if (!hatchFill) return;
        for (SimplePredicate sp : tp.limited) {
            if (sp.maxCount > 0 || sp.maxLayerCount > 0 || !plainCandidates(sp)) continue;
            tp.common.add(0, copyOf(sp));
            return;
        }
    }

    private static boolean plainCandidates(SimplePredicate sp) {
        if (sp.candidates == null || sp.predicate == null) return false;
        BlockInfo[] infos;
        try {
            infos = sp.candidates.get();
        } catch (Throwable t) {
            return false;
        }
        if (infos == null || infos.length == 0) return false;
        for (BlockInfo info : infos) {
            Block b = info.getBlockState().getBlock();
            if (b == Blocks.AIR || b instanceof MetaMachineBlock) return false;
        }
        return true;
    }

    private static boolean isPlainBlock(SimplePredicate sp) {
        if (sp.candidates == null || sp.minCount > 0 || sp.maxCount > 0 || sp.previewCount > 0) return false;
        BlockInfo[] infos;
        try {
            infos = sp.candidates.get();
        } catch (Throwable t) {
            return false;
        }
        if (infos == null || infos.length == 0) return false;
        for (BlockInfo info : infos) {
            Block b = info.getBlockState().getBlock();
            if (b == Blocks.AIR || b instanceof MetaMachineBlock) return false;
        }
        return true;
    }

    private static boolean gtaa$enabled() {
        try {
            return com.omnicrox.gtaa.config.GTAACommonConfig.AUTOBUILD_CASING_FIRST.get();
        } catch (Throwable t) {
            return true; // config not loaded yet
        }
    }
}
