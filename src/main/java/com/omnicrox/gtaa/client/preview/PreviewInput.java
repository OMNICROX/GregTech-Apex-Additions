package com.omnicrox.gtaa.client.preview;

import com.omnicrox.gtaa.GTAA;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * NEI Rewrite draws JEI categories but never passes them the mouse, so the preview listens to the screen's mouse
 * events itself: every frame each drawn preview records where it is on screen, and clicks, drags and scrolls over it
 * are handled here (and kept from the screen underneath). Works the same under plain JEI.
 */
@Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PreviewInput {

    private PreviewInput() {}

    private record Region(MultiblockPreview preview, Matrix4f inverse, long time) {}

    private static final List<Region> REGIONS = new ArrayList<>();
    private static Region active;
    private static PreviewPanel.Drag drag = PreviewPanel.Drag.NONE;
    private static final long FRESH_NS = 250_000_000L;

    /** called by the category every time it draws a preview */
    public static void drawn(MultiblockPreview p, GuiGraphics g) {
        long now = System.nanoTime();
        REGIONS.removeIf(r -> now - r.time > FRESH_NS || r.preview == p);
        REGIONS.add(new Region(p, new Matrix4f(g.pose().last().pose()).invert(), now));
    }

    private static double[] local(Region r, double mx, double my) {
        Vector4f v = new Vector4f((float) mx, (float) my, 0, 1).mul(r.inverse);
        return new double[] { v.x, v.y };
    }

    private static Region at(double mx, double my) {
        long now = System.nanoTime();
        for (int i = REGIONS.size() - 1; i >= 0; i--) {
            Region r = REGIONS.get(i);
            if (now - r.time > FRESH_NS) continue;
            double[] l = local(r, mx, my);
            if (PreviewPanel.in(l[0], l[1], 0, 0, PreviewPanel.W, PreviewPanel.SLOTS_Y)) return r;
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void press(ScreenEvent.MouseButtonPressed.Pre e) {
        Region r = at(e.getMouseX(), e.getMouseY());
        if (r == null) return;
        double[] l = local(r, e.getMouseX(), e.getMouseY());
        try {
            PreviewPanel.Drag d = PreviewPanel.press(r.preview, l[0], l[1], e.getButton());
            if (d == null) return;
            active = r;
            drag = d;
            e.setCanceled(true);
        } catch (Throwable ignored) {}
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void dragged(ScreenEvent.MouseDragged.Pre e) {
        if (active == null || drag == PreviewPanel.Drag.NONE) return;
        double[] l = local(active, e.getMouseX(), e.getMouseY());
        // drag deltas in the preview's own scale
        double s = active.inverse.m00();
        try {
            PreviewPanel.drag(active.preview, drag, l[0], e.getDragX() * s, e.getDragY() * s);
        } catch (Throwable ignored) {}
        e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void released(ScreenEvent.MouseButtonReleased.Pre e) {
        if (active == null) return;
        active = null;
        boolean was = drag != PreviewPanel.Drag.NONE;
        drag = PreviewPanel.Drag.NONE;
        if (was) e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void scrolled(ScreenEvent.MouseScrolled.Pre e) {
        Region r = at(e.getMouseX(), e.getMouseY());
        if (r == null) return;
        double[] l = local(r, e.getMouseX(), e.getMouseY());
        try {
            if (PreviewPanel.scroll(r.preview, l[0], l[1], e.getScrollDelta())) e.setCanceled(true);
        } catch (Throwable ignored) {}
    }

    @SubscribeEvent
    public static void closed(ScreenEvent.Closing e) {
        REGIONS.clear();
        active = null;
        drag = PreviewPanel.Drag.NONE;
    }
}
