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

    @Inject(method = "build", at = @At("HEAD"), remap = false, require = 0)
    private void gtaa$casingFirst(CallbackInfoReturnable<?> cir) {
        for (TraceabilityPredicate tp : symbolMap.values()) {
            if (tp == null || tp.isController || tp.limited.isEmpty() || tp.common.isEmpty()) continue;
            List<SimplePredicate> casings = new ArrayList<>();
            for (SimplePredicate sp : tp.common) if (isPlainBlock(sp)) casings.add(sp);
            if (casings.isEmpty()) continue;
            tp.common.removeAll(casings);
            tp.limited.addAll(0, casings);
        }
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
}
