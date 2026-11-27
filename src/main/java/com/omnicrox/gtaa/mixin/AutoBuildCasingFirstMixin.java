package com.omnicrox.gtaa.mixin;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Survival auto-build takes the first inventory item that fits a spot, so hatches in the inventory went into casing
 * spots. Now the casing in the inventory is used first when the spot also takes a casing.
 */
@Mixin(value = BlockPattern.class, remap = false)
public abstract class AutoBuildCasingFirstMixin {

    private static final ThreadLocal<Boolean> GTAA_INNER = ThreadLocal.withInitial(() -> false);

    @Inject(method = "getMatchStackWithHandler", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void gtaa$casingFirst(List<ItemStack> candidates, LazyOptional<IItemHandler> cap,
                                         CallbackInfoReturnable<IntObjectPair<IItemHandler>> cir) {
        if (GTAA_INNER.get() || candidates == null || candidates.size() < 2 || !gtaa$enabled()) return;
        List<ItemStack> casings = new ArrayList<>();
        for (ItemStack s : candidates)
            if (s.getItem() instanceof BlockItem bi && !(bi.getBlock() instanceof MetaMachineBlock)) casings.add(s);
        if (casings.isEmpty() || casings.size() == candidates.size()) return;
        GTAA_INNER.set(true);
        try {
            IntObjectPair<IItemHandler> r = BlockPatternInvoker.gtaa$match(casings, cap);
            if (r == null) r = BlockPatternInvoker.gtaa$match(candidates, cap);
            cir.setReturnValue(r);
        } finally {
            GTAA_INNER.set(false);
        }
    }

    private static boolean gtaa$enabled() {
        try {
            return com.omnicrox.gtaa.config.GTAACommonConfig.AUTOBUILD_CASING_FIRST.get();
        } catch (Throwable t) {
            return true; // config not loaded yet
        }
    }
}
