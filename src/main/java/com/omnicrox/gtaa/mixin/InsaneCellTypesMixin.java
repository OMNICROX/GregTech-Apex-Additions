package com.omnicrox.gtaa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * InsaneAE: its storage cells hold up to millions of item types each, which makes every other kind of storage
 * pointless. Here a cell holds insaneAE.typesPerCell types (gtapexadditions-common.toml, 1 by default): a huge amount
 * of one thing, like GT New Horizons' singularity cells. Storing many types at that scale is the Singularity Data
 * Hub's job. 0 leaves the mod's own numbers.
 */
@Pseudo
@Mixin(targets = "jp.main.taikun.insaneae.registries.ModCells", remap = false)
public abstract class InsaneCellTypesMixin {

    @Inject(method = "totalTypes", at = @At("HEAD"), cancellable = true, require = 0)
    private static void gtaa$fewTypes(CallbackInfoReturnable<Integer> cir) {
        int n;
        try {
            n = com.omnicrox.gtaa.config.GTAACommonConfig.INSANE_CELL_TYPES.get();
        } catch (Throwable t) {
            n = 1;   // asked before the config is read: the default
        }
        if (n > 0) cir.setReturnValue(n);
    }
}
