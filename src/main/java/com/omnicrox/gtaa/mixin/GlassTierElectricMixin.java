package com.omnicrox.gtaa.mixin;

import com.omnicrox.gtaa.common.glass.GlassTierBlocked;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.omnicrox.gtaa.common.glass.GTAAGlass;
import com.omnicrox.gtaa.config.GTAACommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * GT New Horizons' glass rule: a multi with tiered glass in it can't take an energy hatch above the glass tier. A
 * blocked multi gets no voltage and starts no recipes until the glass is upgraded or the hatch swapped down.
 * Toggle: gtapexadditions-common.toml, multiblocks.glassTierLimitsEnergyHatches.
 */
@Mixin(value = WorkableElectricMultiblockMachine.class, remap = false)
public abstract class GlassTierElectricMixin implements GlassTierBlocked {

    @Shadow protected EnergyContainerList energyContainer;

    @Unique private int gtaa$glassTier = -1;
    @Unique private int gtaa$hatchTier = -1;

    @Override
    public boolean gtaa$glassBlocked() {
        return gtaa$glassTier >= 0 && gtaa$hatchTier > gtaa$glassTier && GTAACommonConfig.GLASS_TIER_LIMITS_ENERGY.get();
    }

    @Inject(method = "onStructureFormed", at = @At("TAIL"))
    private void gtaa$checkGlass(CallbackInfo ci) {
        var self = (WorkableElectricMultiblockMachine) (Object) this;
        gtaa$glassTier = GTAAGlass.structureTier(self);
        long v = energyContainer == null ? 0 : energyContainer.getHighestInputVoltage();
        gtaa$hatchTier = v <= 0 ? -1 : GTUtil.getTierByVoltage(v);
    }

    @Inject(method = "onStructureInvalid", at = @At("TAIL"), require = 0)
    private void gtaa$clearGlass(CallbackInfo ci) {
        gtaa$glassTier = -1;
        gtaa$hatchTier = -1;
    }

    @Inject(method = "getMaxVoltage", at = @At("RETURN"), cancellable = true)
    private void gtaa$noVoltage(CallbackInfoReturnable<Long> cir) {
        if (gtaa$glassBlocked()) cir.setReturnValue(0L);
    }

    @Inject(method = "getOverclockVoltage", at = @At("RETURN"), cancellable = true)
    private void gtaa$noOcVoltage(CallbackInfoReturnable<Long> cir) {
        if (gtaa$glassBlocked()) cir.setReturnValue(0L);
    }

    @Inject(method = "addDisplayText", at = @At("TAIL"))
    private void gtaa$glassLine(List<Component> text, CallbackInfo ci) {
        if (gtaa$glassTier < 0) return;
        text.add(Component.literal("Glass tier: " + GTValues.VNF[gtaa$glassTier]).withStyle(ChatFormatting.GRAY));
        if (gtaa$glassBlocked())
            text.add(Component.literal("Energy hatch (" + GTValues.VNF[gtaa$hatchTier] + "§c) is above the glass tier: upgrade the glass")
                    .withStyle(ChatFormatting.RED));
    }
}
