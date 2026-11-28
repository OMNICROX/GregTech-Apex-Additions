package com.omnicrox.gtaa.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Commands run by clicking text (the quest book's "Read more in the Apex Guide" lines) go straight to the server and
 * never reach client commands. This catches /apexpage there and opens the guide page on the client.
 * Both names: "sendUnsignedCommand" in the dev run, its SRG name "m_246979_" in the game (no refmap, remap = false).
 */
@Mixin(value = ClientPacketListener.class, remap = false)
public abstract class ClientCommandClickMixin {

    @Inject(method = { "sendUnsignedCommand", "m_246979_" }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void gtaa$apexPage(String command, CallbackInfoReturnable<Boolean> cir) {
        if (command == null || !command.startsWith("apexpage ")) return;
        String page = command.substring("apexpage ".length()).trim();
        try {
            if (net.minecraftforge.fml.ModList.get().isLoaded("apexguide")) {
                net.minecraft.client.Minecraft.getInstance().tell(() -> com.omnicrox.gtaa.client.guide.ApexGuide.openPage(page));
            }
        } catch (Throwable ignored) {}
        cir.setReturnValue(true);
    }
}
