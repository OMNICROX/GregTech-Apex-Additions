package com.omnicrox.gtaa.client;

import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Client side of the Ascension: the credits screen and a minute of pink sky-glow. */
public final class AscensionClient {

    private AscensionClient() {}

    public static final int GLOW_TICKS = 1200;
    private static int glow;
    private static boolean registered;

    public static void show(GTAANetwork.AscensionPacket packet) {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(AscensionClient.class);
            registered = true;
        }
        if (packet.celebrate()) glow = GLOW_TICKS;
        Minecraft.getInstance().setScreen(new AscensionCreditsScreen(packet.lines(), packet.celebrate()));
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END && glow > 0 && !Minecraft.getInstance().isPaused()) glow--;
    }

    @SubscribeEvent
    public static void fog(ViewportEvent.ComputeFogColor e) {
        if (glow <= 0) return;
        // fade in over 2 s, hold, fade out over the last 10 s
        float f = Math.min(1f, Math.min((GLOW_TICKS - glow) / 40f, glow / 200f)) * 0.7f;
        e.setRed(e.getRed() + (1.0f - e.getRed()) * f);
        e.setGreen(e.getGreen() + (0.55f - e.getGreen()) * f);
        e.setBlue(e.getBlue() + (0.8f - e.getBlue()) * f);
    }
}
