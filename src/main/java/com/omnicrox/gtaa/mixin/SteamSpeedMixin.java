package com.omnicrox.gtaa.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Apex balance: high-pressure single-block steam machines keep up with LV. An LV machine overclocks a ULV recipe once
 * (half the time); an HP steam machine now does the same, but pays 3x the steam for it, so steam is as fast as LV
 * and only less efficient. LV-tier recipes and low-pressure machines are unchanged.
 */
@Mixin(value = SimpleSteamMachine.class, remap = false)
public abstract class SteamSpeedMixin {

    @Inject(method = "recipeModifier", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private static void gtaa$hpSpeed(MetaMachine machine, GTRecipe recipe, CallbackInfoReturnable<ModifierFunction> cir) {
        ModifierFunction base = cir.getReturnValue();
        if (base == null || base == ModifierFunction.NULL) return;
        if (!(machine instanceof SimpleSteamMachine steam) || !steam.isHighPressure()) return;
        if (RecipeHelper.getRecipeEUtTier(recipe) > 0) return;
        cir.setReturnValue(base.andThen(ModifierFunction.builder().durationMultiplier(0.5).eutMultiplier(3).build()));
    }
}
