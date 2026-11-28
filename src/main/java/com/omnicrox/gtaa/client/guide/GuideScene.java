package com.omnicrox.gtaa.client.guide;

import com.omnicrox.apexguide.internal.screen.ApexTheme;
import com.omnicrox.apexguide.internal.screen.GuideIconButton;
import com.omnicrox.gtaa.client.preview.MultiblockPreview;
import com.omnicrox.gtaa.client.preview.PreviewInput;
import com.omnicrox.gtaa.config.GTAAClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The 3D multiblock scene inside the Apex Guide, drawn like GuideNH's GameScene: a dark framed viewport, a column of
 * icon buttons on its right (zoom in, zoom out, reset, next shape, hatch spots, block list) and slim sliders under it
 * (tier, layer). The structure itself comes from {@link MultiblockPreview} (GTCEu patterns), the same data as JEI.
 */
final class GuideScene {

    static final int BAR = 20;          // icon column width (16 px icon + gaps)
    static final int ROW = 14;          // slider row height
    private static final int SLIDER_H = 10;

    private enum Target { NONE, ROTATE, ZOOM, PAN, TIER, LAYER }
    private enum Tool { ZOOM_IN, ZOOM_OUT, RESET, SHAPE, HATCHES, BLOCKS }


    private GuideScene() {}

    static int sliderRows(MultiblockPreview p) {
        return (p.maxTier() > 1 ? 1 : 0) + 1;
    }

    static int totalHeight(MultiblockPreview p, int h) {
        return h + 4 + sliderRows(p) * ROW;
    }

    private static boolean in(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    private static List<Tool> tools(MultiblockPreview p) {
        List<Tool> t = new ArrayList<>(List.of(Tool.ZOOM_IN, Tool.ZOOM_OUT, Tool.RESET));
        if (p.pages() > 1) t.add(Tool.SHAPE);
        t.add(Tool.HATCHES);
        t.add(Tool.BLOCKS);
        return t;
    }

    private static GuideIconButton.Role role(Tool t) {
        return switch (t) {
            case ZOOM_IN -> GuideIconButton.Role.ZOOM_IN;
            case ZOOM_OUT -> GuideIconButton.Role.ZOOM_OUT;
            case RESET -> GuideIconButton.Role.RESET_VIEW;
            case SHAPE -> GuideIconButton.Role.SCENE_CROP;
            case HATCHES -> GuideIconButton.Role.TOGGLE_GRID;
            case BLOCKS -> GuideIconButton.Role.TOGGLE_BLOCK_STATS;
        };
    }

    private static List<Component> toolTip(Tool t, MultiblockPreview p, GuideState st) {
        return switch (t) {
            case ZOOM_IN -> List.of(Component.literal("Zoom in"));
            case ZOOM_OUT -> List.of(Component.literal("Zoom out"));
            case RESET -> List.of(Component.literal("Reset view"));
            case SHAPE -> List.of(Component.literal("Next shape (" + (p.page + 1) + " of " + p.pages() + ")"),
                    Component.literal("Sizes and variants of this multiblock").withStyle(ChatFormatting.GRAY));
            case HATCHES -> List.of(Component.literal(p.highlightHatches ? "Hide hatch spots" : "Show where hatches can go"));
            case BLOCKS -> List.of(Component.literal(st.showBlocks ? "Hide block list" : "Show the blocks you need"));
        };
    }

    private static int toolY(int i) {
        return i * 19;
    }

    /** per-scene toggles that GuideNH keeps on the scene, not the structure */
    static final class GuideState {
        boolean showBlocks;
        // mouse state is per scene: a page with two scenes must not have them reset each other's drags
        Target target = Target.NONE;
        final boolean[] wasDown = new boolean[3];
        double lastX, lastY;
        long lastFrame;
    }

    static void draw(MultiblockPreview p, GuideState st, GuiGraphics g, int w, int h) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        Matrix4f inv = new Matrix4f(g.pose().last().pose()).invert();
        double gx = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double gy = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();
        Vector4f lm = new Vector4f((float) gx, (float) gy, 0, 1).mul(inv);
        double mx = lm.x, my = lm.y;
        int imx = (int) Math.floor(mx), imy = (int) Math.floor(my);
        PreviewInput.drawn(p, g, w + BAR, totalHeight(p, h), (lx, ly, delta) -> scroll(p, w, h, lx, ly, delta));
        handleMouse(p, st, mc, w, h, mx, my);

        // viewport (GuideNH: near-black panel with a thin border)
        g.fill(0, 0, w, h, 0xFF0B0B10);
        g.renderOutline(0, 0, w, h, 0xFF3A3A48);
        List<Component> tip = null;
        if (p.error() != null) {
            g.drawWordWrap(font, Component.literal(p.error()), 4, 4, w - 8, 0xFFFF5555);
        } else {
            BlockPos hovered = null;
            try {
                hovered = p.render(g, 1, 1, w - 2, h - 2, imx, imy);
            } catch (Throwable t) {
                g.drawString(font, "Preview failed: " + t.getClass().getSimpleName(), 4, 4, 0xFFFF5555, false);
            }
            if (p.isCompiling())
                g.drawString(font, "Loading " + (int) (p.compileProgress() * 100) + "%", 4, h - 12, ApexTheme.TEXT_MUTED, false);
            if (hovered != null && st.target == Target.NONE && !st.showBlocks) tip = p.tooltip(hovered);
        }

        // what the selected spot accepts
        List<ItemStack> cands = p.selected == null ? List.of() : p.candidates(p.selected);
        if (!cands.isEmpty() && !st.showBlocks) {
            int n = Math.min(cands.size(), (w - 8) / 18 * 2), cols = Math.max(1, (w - 8) / 18);
            int rows = (n + cols - 1) / cols, py = h - 4 - rows * 18 - 11;
            g.fill(1, py, w - 1, h - 1, 0xD0101014);
            g.drawString(font, "Accepts:", 4, py + 2, ApexTheme.TEXT_MUTED, false);
            for (int i = 0; i < n; i++) {
                int ix = 4 + (i % cols) * 18, iy = py + 11 + (i / cols) * 18;
                g.renderItem(cands.get(i), ix, iy);
                if (in(mx, my, ix, iy, 16, 16)) tip = List.of(cands.get(i).getHoverName());
            }
        }

        // block list (GuideNH block stats)
        if (st.showBlocks) {
            List<ItemStack> parts = p.currentParts();
            g.fill(1, 1, w - 1, h - 1, 0xE0101014);
            g.drawString(font, "Blocks needed", 5, 5, ApexTheme.TEXT_TITLE, false);
            int cols = Math.max(1, (w - 10) / 20);
            for (int i = 0; i < parts.size(); i++) {
                int ix = 5 + (i % cols) * 20, iy = 17 + (i / cols) * 20;
                if (iy + 16 > h - 2) break;
                ItemStack s = parts.get(i);
                g.renderItem(s, ix, iy);
                g.renderItemDecorations(font, s, ix, iy);
                if (in(mx, my, ix, iy, 16, 16)) {
                    List<Component> l = new ArrayList<>(net.minecraft.client.gui.screens.Screen.getTooltipFromItem(mc, s));
                    l.add(Component.literal("Needed: " + s.getCount()).withStyle(ChatFormatting.YELLOW));
                    tip = l;
                }
            }
        }

        // icon column on the right
        List<Tool> tools = tools(p);
        for (int i = 0; i < tools.size(); i++) {
            Tool t = tools.get(i);
            int ty = toolY(i);
            boolean hov = in(mx, my, w + 3, ty, 16, 16);
            boolean active = (t == Tool.HATCHES && p.highlightHatches) || (t == Tool.BLOCKS && st.showBlocks);
            GuideIconButton.drawIcon(g, role(t), w + 3, ty, ApexTheme.iconColor(true, hov, active));
            if (hov) tip = toolTip(t, p, st);
        }

        // sliders under the viewport
        int row = 0;
        if (p.maxTier() > 1) {
            slider(g, font, h + 4 + ROW * row++, w, "Tier: " + (p.tier < 0 ? "Shown" : String.valueOf(p.tier + 1)),
                    p.tier + 1, p.maxTier(), imx, imy);
        }
        slider(g, font, h + 4 + ROW * row, w, "Layer: " + (p.layer == 0 ? "All" : String.valueOf(p.layer)),
                p.layer, p.layers(), imx, imy);

        if (tip != null && !tip.isEmpty()) g.renderTooltip(font, tip, Optional.empty(), imx, imy);
    }

    /** GuideNH slider: a dark track, an accent fill up to the white thumb, the label centered on it */
    private static void slider(GuiGraphics g, Font font, int y, int w, String label, int value, int max, int mx, int my) {
        int ty = y + SLIDER_H / 2 - 2;
        g.fill(0, ty, w, ty + 4, 0xFF242430);
        int tx = max <= 0 ? 0 : (int) Math.round((double) value / max * (w - 4));
        g.fill(0, ty, tx + 2, ty + 4, 0xFF1E8EB8);
        boolean hov = in(mx, my, 0, y, w, SLIDER_H);
        g.fill(tx, y, tx + 4, y + SLIDER_H, hov ? 0xFFFFFFFF : 0xFFDDDDDD);
        g.drawCenteredString(font, label, w / 2, y + 1, 0xFFFFFFFF);
    }

    private static void handleMouse(MultiblockPreview p, GuideState st, Minecraft mc, int w, int h, double mx, double my) {
        long win = mc.getWindow().getWindow();
        boolean[] down = new boolean[3];
        for (int b = 0; b < 3; b++) down[b] = GLFW.glfwGetMouseButton(win, b) == GLFW.GLFW_PRESS;
        long now = System.nanoTime();
        if (now - st.lastFrame > 200_000_000L) {
            st.target = Target.NONE;
            System.arraycopy(down, 0, st.wasDown, 0, 3);
            st.lastX = mx;
            st.lastY = my;
        }
        st.lastFrame = now;
        double dx = mx - st.lastX, dy = my - st.lastY;
        boolean inScene = in(mx, my, 0, 0, w, h);
        boolean inPanel = in(mx, my, 0, 0, w + BAR, totalHeight(p, h));
        if (st.target == Target.NONE && inPanel) {
            if (down[0] && !st.wasDown[0]) leftPress(p, st, w, h, mx, my, inScene);
            else if (down[1] && !st.wasDown[1] && inScene) {
                st.target = Target.PAN;
                p.selected = p.hoveredBlock();
            } else if (down[2] && !st.wasDown[2] && inScene) st.target = Target.PAN;
        }
        switch (st.target) {
            case ROTATE -> {
                if (!down[0]) st.target = Target.NONE;
                else {
                    float sp = GTAAClientConfig.PREVIEW_ROTATE_SPEED.get().floatValue();
                    p.rotH = (p.rotH + (float) dx * (GTAAClientConfig.PREVIEW_INVERT_X.get() ? -sp : sp) + 360) % 360;
                    p.rotV = (float) Math.max(-89.9, Math.min(89.9, p.rotV + dy * (GTAAClientConfig.PREVIEW_INVERT_Y.get() ? -sp : sp)));
                }
            }
            case PAN -> {
                if (!down[1] && !down[2]) st.target = Target.NONE;
                else p.pan(dx, dy);
            }
            case TIER, LAYER -> {
                if (!down[0]) st.target = Target.NONE;
                else setSlider(p, st.target, w, mx);
            }
            default -> {}
        }
        System.arraycopy(down, 0, st.wasDown, 0, 3);
        st.lastX = mx;
        st.lastY = my;
    }

    private static void leftPress(MultiblockPreview p, GuideState st, int w, int h, double mx, double my, boolean inScene) {
        List<Tool> tools = tools(p);
        for (int i = 0; i < tools.size(); i++) {
            if (!in(mx, my, w + 3, toolY(i), 16, 16)) continue;
            switch (tools.get(i)) {
                case ZOOM_IN -> zoom(p, 1.5);
                case ZOOM_OUT -> zoom(p, -1.5);
                case RESET -> p.resetView();
                case SHAPE -> {
                    p.page = (p.page + 1) % p.pages();
                    p.layer = 0;
                    p.markDirty();
                }
                case HATCHES -> p.highlightHatches = !p.highlightHatches;
                case BLOCKS -> st.showBlocks = !st.showBlocks;
            }
            click();
            return;
        }
        int row = 0;
        if (p.maxTier() > 1) {
            if (in(mx, my, 0, h + 4, w, SLIDER_H)) {
                st.target = Target.TIER;
                setSlider(p, st.target, w, mx);
                return;
            }
            row++;
        }
        if (in(mx, my, 0, h + 4 + ROW * row, w, SLIDER_H)) {
            st.target = Target.LAYER;
            setSlider(p, st.target, w, mx);
            return;
        }
        if (inScene && !st.showBlocks) st.target = Target.ROTATE;
    }

    private static void zoom(MultiblockPreview p, double steps) {
        double step = GTAAClientConfig.PREVIEW_ZOOM_SPEED.get();
        p.distance = (float) Math.max(3, Math.min(999, p.distance - steps * step));
    }

    private static void setSlider(MultiblockPreview p, Target t, int w, double mx) {
        int max = t == Target.LAYER ? p.layers() : p.maxTier();
        int v = (int) Math.round(mx / Math.max(1, w - 4) * max);
        v = Math.max(0, Math.min(max, v));
        if (t == Target.LAYER) p.setLayer(v);
        else {
            int tier = Math.max(-1, Math.min(p.maxTier() - 1, v - 1));
            if (tier != p.tier) {
                p.tier = tier;
                p.markDirty();
            }
        }
    }

    /** mouse wheel in local coordinates: zoom over the viewport, step over a slider; true = used */
    static boolean scroll(MultiblockPreview p, int w, int h, double mx, double my, double delta) {
        if (in(mx, my, 0, 0, w, h)) {
            zoom(p, delta);
            return true;
        }
        int step = delta > 0 ? 1 : -1, row = 0;
        if (p.maxTier() > 1) {
            if (in(mx, my, 0, h + 4, w, ROW)) {
                int tier = Math.max(-1, Math.min(p.maxTier() - 1, p.tier + step));
                if (tier != p.tier) {
                    p.tier = tier;
                    p.markDirty();
                }
                return true;
            }
            row++;
        }
        if (in(mx, my, 0, h + 4 + ROW * row, w, ROW)) {
            p.setLayer(p.layer + step);
            return true;
        }
        return false;
    }

    private static void click() {
        Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1f));
    }
}
