package com.omnicrox.gtaa.mixin;

import com.omnicrox.gtaa.common.glass.GlassTierBlocked;

import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** no recipe starts while the glass tier blocks the multi (covers machines that override getMaxVoltage) */
@Mixin(value = WorkableMultiblockMachine.class, remap = false)
public abstract class GlassTierWorkingMixin {

    @Inject(method = "beforeWorking", at = @At("HEAD"), cancellable = true)
    private void gtaa$glassGate(GTRecipe recipe, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof GlassTierBlocked b && b.gtaa$glassBlocked()) cir.setReturnValue(false);
    }
}
