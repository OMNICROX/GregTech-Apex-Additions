package com.omnicrox.gtaa.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The multiblock preview gets the mouse wheel before the screen (NEI Rewrite would otherwise turn the page).
 * Both names: "onScroll" in the dev run, its SRG name "m_91526_" in the game (no refmap, remap = false).
 */
@Mixin(value = MouseHandler.class, remap = false)
public abstract class MouseHandlerScrollMixin {

    @Inject(method = { "onScroll", "m_91526_" }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtaa$previewScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (window != mc.getWindow().getWindow() || mc.screen == null) return;
        try {
            double delta = (mc.options.discreteMouseScroll().get() ? Math.signum(yOffset) : yOffset)
                    * mc.options.mouseWheelSensitivity().get();
            if (delta != 0 && com.omnicrox.gtaa.client.preview.PreviewInput.scroll(delta)) ci.cancel();
        } catch (Throwable ignored) {}
    }
}
