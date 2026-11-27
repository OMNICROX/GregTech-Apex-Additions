package com.omnicrox.gtaa.client.preview;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Layout, drawing and mouse handling of the preview inside a recipe page (local coordinates, 0,0 = top left).
 * Input arrives through {@link PreviewInput}, because NEI Rewrite does not pass mouse input to JEI categories.
 */
public final class PreviewPanel {

    public static final int W = 170, H = 212;
    public static final int SX = 0, SY = 12, SW = 170, SH = 136;
    public static final int ROW_LAYER = 151, ROW_TIER = 162, TRACK_X = 60, TRACK_W = 108;
    public static final int SLOTS_Y = 176;
    private static final int BTN = 11, BTN_X = SX + SW - BTN - 2;

    private PreviewPanel() {}

    private static final java.util.Set<String> LOGGED = new java.util.HashSet<>();

    // ---------------- drawing ----------------
    public static void draw(MultiblockPreview p, GuiGraphics g, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        String name = p.def.getItem().getDescription().getString();
        g.drawString(font, font.plainSubstrByWidth(name, 150), 1, 2, 0xFF303030, false);
        // scene frame
        g.fill(SX, SY, SX + SW, SY + SH, 0x30000000);
        g.renderOutline(SX, SY, SW, SH, 0xFF555555);
        if (p.error() != null) {
            g.drawWordWrap(font, Component.literal(p.error()).withStyle(ChatFormatting.RED), SX + 4, SY + 4, SW - 8, 0xFFFF5555);
            return;
        }
        BlockPos hovered = null;
        try {
            hovered = p.render(g, SX + 1, SY + 1, SW - 2, SH - 2, mouseX, mouseY);
        } catch (Throwable t) {
            g.drawString(font, "Preview failed: " + t.getClass().getSimpleName(), SX + 4, SY + 4, 0xFFFF5555, false);
            if (LOGGED.add(p.def.getId().toString()))
                com.omnicrox.gtaa.GTAA.LOGGER.error("[Apex preview] {} failed to render", p.def.getId(), t);
        }
        if (p.isCompiling())
            g.drawString(font, "Loading " + (int) (p.compileProgress() * 100) + "%", SX + 4, SY + SH - 11, 0xFFFFFFFF, true);
        // buttons
        button(g, font, BTN_X, SY + 2, "H", p.hatches ? 0xFF2E7D32 : 0xFF555555, mouseX, mouseY);
        button(g, font, BTN_X, SY + 2 + BTN + 2, "R", 0xFF555555, mouseX, mouseY);
        button(g, font, BTN_X, SY + 2 + 2 * (BTN + 2), "?", 0xFF1565C0, mouseX, mouseY);
        if (p.pages() > 1) {
            int by = SY + SH - BTN - 2;
            button(g, font, SX + 2, by, "<", 0xFF555555, mouseX, mouseY);
            String t = (p.page + 1) + "/" + p.pages();
            g.drawString(font, t, SX + 2 + BTN + 3, by + 2, 0xFFFFFFFF, true);
            button(g, font, SX + 2 + BTN + 6 + font.width(t), by, ">", 0xFF555555, mouseX, mouseY);
        }
        // sliders
        String ly = p.layer == 0 ? "All" : p.layer + "/" + p.layers();
        slider(g, font, ROW_LAYER, "Layer: " + ly, p.layer, p.layers());
        if (p.maxTier() > 1) {
            String tt = p.tier < 0 ? "Shown" : (p.tier + 1) + "/" + p.maxTier();
            slider(g, font, ROW_TIER, "Tier: " + tt, p.tier + 1, p.maxTier());
        }
        // tooltips
        List<Component> tip = null;
        if (hovered != null) tip = p.tooltip(hovered);
        if (in(mouseX, mouseY, BTN_X, SY + 2, BTN, BTN))
            tip = List.of(Component.literal(p.hatches ? "Hatches shown (click: show casings instead)" : "Casings in place of hatches (click: show hatches)"));
        else if (in(mouseX, mouseY, BTN_X, SY + 2 + BTN + 2, BTN, BTN)) tip = List.of(Component.literal("Reset the view"));
        else if (in(mouseX, mouseY, BTN_X, SY + 2 + 2 * (BTN + 2), BTN, BTN)) tip = help();
        if (tip != null && !tip.isEmpty()) g.renderTooltip(font, tip, Optional.empty(), mouseX, mouseY);
    }

    private static List<Component> help() {
        List<Component> l = new ArrayList<>();
        l.add(Component.literal("Multiblock preview").withStyle(ChatFormatting.GOLD));
        l.add(Component.literal("Left-drag: rotate").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Right-drag: move").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Scroll: zoom (on a slider: step it)").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Layer: show one layer at a time").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Tier: swap coils and other tiered blocks").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("H: hatches or plain casings").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Hover a block to see what that spot accepts").withStyle(ChatFormatting.GRAY));
        return l;
    }

    private static void button(GuiGraphics g, Font font, int x, int y, String label, int color, int mx, int my) {
        boolean hover = in(mx, my, x, y, BTN, BTN);
        g.fill(x, y, x + BTN, y + BTN, hover ? (color | 0xFF000000) + 0x00202020 : color);
        g.renderOutline(x, y, BTN, BTN, 0xFF000000);
        g.drawString(font, label, x + (BTN - font.width(label)) / 2 + 1, y + 2, 0xFFFFFFFF, false);
    }

    private static void slider(GuiGraphics g, Font font, int y, String label, int value, int max) {
        g.drawString(font, label, 1, y, 0xFF303030, false);
        g.fill(TRACK_X, y + 3, TRACK_X + TRACK_W, y + 5, 0xFF777777);
        int kx = max <= 0 ? TRACK_X : TRACK_X + Math.round((float) value / max * (TRACK_W - 4));
        g.fill(kx, y, kx + 4, y + 8, 0xFF303030);
        g.fill(kx + 1, y + 1, kx + 3, y + 7, 0xFFDDDDDD);
    }

    static boolean in(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    // ---------------- input (local coordinates) ----------------
    enum Drag { NONE, ROTATE, PAN, LAYER, TIER }

    static Drag press(MultiblockPreview p, double mx, double my, int button) {
        if (p.error() != null) return Drag.NONE;
        if (button == 0 && in(mx, my, BTN_X, SY + 2, BTN, BTN)) {
            p.hatches = !p.hatches;
            p.markDirty();
            return Drag.NONE;
        }
        if (button == 0 && in(mx, my, BTN_X, SY + 2 + BTN + 2, BTN, BTN)) {
            p.resetView();
            return Drag.NONE;
        }
        if (in(mx, my, BTN_X, SY + 2 + 2 * (BTN + 2), BTN, BTN)) return Drag.NONE;
        if (p.pages() > 1 && button == 0) {
            int by = SY + SH - BTN - 2;
            Font font = Minecraft.getInstance().font;
            String t = (p.page + 1) + "/" + p.pages();
            if (in(mx, my, SX + 2, by, BTN, BTN)) {
                p.page = Math.floorMod(p.page - 1, p.pages());
                p.layer = 0;
                p.markDirty();
                return Drag.NONE;
            }
            if (in(mx, my, SX + 2 + BTN + 6 + font.width(t), by, BTN, BTN)) {
                p.page = Math.floorMod(p.page + 1, p.pages());
                p.layer = 0;
                p.markDirty();
                return Drag.NONE;
            }
        }
        if (in(mx, my, SX, SY, SW, SH)) return button == 1 ? Drag.PAN : Drag.ROTATE;
        if (in(mx, my, 0, ROW_LAYER - 1, W, 10)) {
            setSlider(p, Drag.LAYER, mx);
            return Drag.LAYER;
        }
        if (p.maxTier() > 1 && in(mx, my, 0, ROW_TIER - 1, W, 10)) {
            setSlider(p, Drag.TIER, mx);
            return Drag.TIER;
        }
        return null; // not ours
    }

    static void drag(MultiblockPreview p, Drag d, double mx, double dx, double dy) {
        switch (d) {
            case ROTATE -> {
                p.yaw += (float) dx * 1.6f;
                p.pitch = Math.max(-89, Math.min(89, p.pitch + (float) dy * 1.6f));
            }
            case PAN -> {
                double rad = Math.toRadians(p.yaw);
                float k = 0.06f * p.zoom * Math.max(1, p.layers() / 8f);
                p.pan.add((float) (-Math.sin(rad) * dx * k), (float) (dy * k), (float) (Math.cos(rad) * dx * k));
            }
            case LAYER, TIER -> setSlider(p, d, mx);
            default -> {}
        }
    }

    static boolean scroll(MultiblockPreview p, double mx, double my, double delta) {
        if (in(mx, my, SX, SY, SW, SH)) {
            p.zoom = (float) Math.max(0.05, Math.min(6, p.zoom * (delta > 0 ? 0.88 : 1.0 / 0.88)));
            return true;
        }
        int step = delta > 0 ? 1 : -1;
        if (in(mx, my, 0, ROW_LAYER - 1, W, 10)) {
            p.setLayer(p.layer + step);
            return true;
        }
        if (p.maxTier() > 1 && in(mx, my, 0, ROW_TIER - 1, W, 10)) {
            setTier(p, p.tier + step);
            return true;
        }
        return in(mx, my, 0, 0, W, SLOTS_Y);
    }

    private static void setSlider(MultiblockPreview p, Drag d, double mx) {
        int max = d == Drag.LAYER ? p.layers() : p.maxTier();
        int v = (int) Math.round((mx - TRACK_X - 2) / (TRACK_W - 4) * max);
        v = Math.max(0, Math.min(max, v));
        if (d == Drag.LAYER) p.setLayer(v);
        else setTier(p, v - 1);
    }

    private static void setTier(MultiblockPreview p, int t) {
        t = Math.max(-1, Math.min(p.maxTier() - 1, t));
        if (t == p.tier) return;
        p.tier = t;
        p.markDirty();
    }
}
