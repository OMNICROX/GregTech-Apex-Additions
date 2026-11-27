package com.omnicrox.gtaa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hostile Neural Networks: the Simulation Chamber's FE/t per data model, times hostileNetworks.simCostMultiplier
 * (gtapexadditions-common.toml). The chamber only trains models and makes predictions here.
 */
@Pseudo
@Mixin(targets = "dev.shadowsoffire.hostilenetworks.data.DataModel", remap = false)
public abstract class HnnSimCostMixin {

    @Inject(method = "simCost", at = @At("RETURN"), cancellable = true, require = 0)
    private void gtaa$cheaper(CallbackInfoReturnable<Integer> cir) {
        double m;
        try {
            m = com.omnicrox.gtaa.config.GTAACommonConfig.HNN_SIM_COST.get();
        } catch (Throwable t) {
            return;
        }
        if (m != 1.0) cir.setReturnValue((int) Math.max(1, Math.round(cir.getReturnValue() * m)));
    }
}
