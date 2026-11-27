package com.omnicrox.gtaa.client.preview;

import com.omnicrox.gtaa.GTAA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Where each preview was drawn this frame, so the mouse wheel can reach it: NEI Rewrite never gives JEI categories
 * the scroll, so {@code MouseHandlerScrollMixin} asks here first (and keeps the scroll from NEI when we used it).
 * Clicks and drags are read by {@link PreviewPanel} itself, every frame, like BlockRenderer6343 does.
 */
@Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PreviewInput {

    private PreviewInput() {}

    /** scroll in a region's local coordinates; true = used */
    public interface Scroll {
        boolean scroll(double x, double y, double delta);
    }

    private record Region(MultiblockPreview preview, Matrix4f inverse, long time, int w, int h, Scroll handler) {}

    private static final List<Region> REGIONS = new ArrayList<>();
    private static final long FRESH_NS = 250_000_000L;

    public static void drawn(MultiblockPreview p, GuiGraphics g) {
        long now = System.nanoTime();
        REGIONS.removeIf(r -> now - r.time > FRESH_NS || r.preview == p);
        REGIONS.add(new Region(p, new Matrix4f(g.pose().last().pose()).invert(), now, PreviewPanel.W,
                PreviewPanel.SLOTS_Y, (x, y, d) -> PreviewPanel.scroll(p, x, y, d)));
    }

    /** a preview drawn with other chrome (the Apex Guide scene) */
    public static void drawn(MultiblockPreview p, GuiGraphics g, int w, int h, Scroll handler) {
        long now = System.nanoTime();
        REGIONS.removeIf(r -> now - r.time > FRESH_NS || r.preview == p);
        REGIONS.add(new Region(p, new Matrix4f(g.pose().last().pose()).invert(), now, w, h, handler));
    }

    /** from the mouse handler: window scroll; true = a preview used it */
    public static boolean scroll(double delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null || REGIONS.isEmpty()) return false;
        double gx = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double gy = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();
        long now = System.nanoTime();
        for (int i = REGIONS.size() - 1; i >= 0; i--) {
            Region r = REGIONS.get(i);
            if (now - r.time > FRESH_NS) continue;
            Vector4f v = new Vector4f((float) gx, (float) gy, 0, 1).mul(r.inverse);
            if (!PreviewPanel.in(v.x, v.y, 0, 0, r.w, r.h)) continue;
            try {
                return r.handler.scroll(v.x, v.y, delta);
            } catch (Throwable t) {
                return false;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void closed(ScreenEvent.Closing e) {
        REGIONS.clear();
    }
}
