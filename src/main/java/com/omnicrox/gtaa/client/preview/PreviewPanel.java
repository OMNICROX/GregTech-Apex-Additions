package com.omnicrox.gtaa.client.preview;

import com.omnicrox.gtaa.config.GTAAClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
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
 * The preview page, laid out and driven like GT New Horizons' BlockRenderer6343 NEI multiblock page:
 * left-drag rotates, right-drag zooms, middle-drag moves, scroll zooms, right-click a block to see what that spot
 * accepts, Tier and Layer sliders under the scene, small buttons on the right.
 * Like BlockRenderer6343 it reads the mouse buttons itself every frame, because NEI Rewrite never passes mouse input
 * to JEI categories; scrolling comes in through {@link PreviewInput}.
 */
public final class PreviewPanel {

    public static final int W = 170, H = 216;
    public static final int SX = 0, SY = 12, SW = 170, SH = 138;
    static final int UNDER = SY + SH + 2, ROW = 13;
    static final int SLIDER_W = 121, SLIDER_H = 12, BTN_W = 20, BTN_H = 12, BTN_RIGHT = 148, BTN_GAP = 22;
    public static final int SLOTS_Y = UNDER + 2 * ROW + 2;

    private PreviewPanel() {}

    private static final java.util.Set<String> LOGGED = new java.util.HashSet<>();

    // ---------------- per-frame mouse state (one preview is handled at a time) ----------------
    enum Target { NONE, ROTATE, ZOOM, PAN, TIER, LAYER }

    private static MultiblockPreview owner;
    private static Target target = Target.NONE;
    private static final boolean[] wasDown = new boolean[3];
    /** set by the JEI plugin: (stack, uses?) opens JEI's recipes (false) or uses (true) page for a part */
    public static java.util.function.BiConsumer<ItemStack, Boolean> SHOW_RECIPES;
    private static ItemStack hoveredPart = ItemStack.EMPTY;
    private static double lastX, lastY;
    private static long lastFrame;

    /** vanilla-looking slider, drawn only (we drive it ourselves) */
    private static final class Slider extends AbstractSliderButton {
        Slider() {
            super(0, 0, SLIDER_W, SLIDER_H, Component.empty(), 0);
        }

        void set(int x, int y, String text, double frac) {
            setX(x);
            setY(y);
            value = Math.max(0, Math.min(1, frac));
            setMessage(Component.literal(text));
        }

        @Override
        protected void updateMessage() {}

        @Override
        protected void applyValue() {}
    }

    private static final Slider SLIDER = new Slider();
    private static final Button BUTTON = Button.builder(Component.empty(), b -> {}).bounds(0, 0, BTN_W, BTN_H).build();

    private static void drawButton(GuiGraphics g, int x, int y, String label, boolean on, int mx, int my) {
        BUTTON.setX(x);
        BUTTON.setY(y);
        BUTTON.setMessage(Component.literal(on ? "§a" + label : label));
        BUTTON.render(g, mx, my, 0);
    }

    private static void drawSlider(GuiGraphics g, int y, String text, int value, int min, int max, int mx, int my) {
        SLIDER.set(0, y, text, max <= min ? 0 : (double) (value - min) / (max - min));
        SLIDER.render(g, mx, my, 0);
    }

    private static final Button SMALL = Button.builder(Component.empty(), b -> {}).bounds(0, 0, 10, BTN_H).build();

    private static void drawSmall(GuiGraphics g, int x, int y, String label, int mx, int my) {
        SMALL.setX(x);
        SMALL.setY(y);
        SMALL.setMessage(Component.literal(label));
        SMALL.render(g, mx, my, 0);
    }

    static boolean in(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    private static int btnX(int i) {
        return BTN_RIGHT - (i % 2) * BTN_GAP;
    }

    private static int btnY(int i) {
        return UNDER + (i / 2) * ROW;
    }

    // ---------------- drawing + input ----------------
    public static void draw(MultiblockPreview p, GuiGraphics g, int ignoredMouseX, int ignoredMouseY) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        // mouse in this page's coordinates, read straight from the window (NEI's own mouse values can't be trusted)
        Matrix4f inv = new Matrix4f(g.pose().last().pose()).invert();
        double gx = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double gy = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();
        Vector4f lm = new Vector4f((float) gx, (float) gy, 0, 1).mul(inv);
        double mx = lm.x, my = lm.y;
        int imx = (int) Math.floor(mx), imy = (int) Math.floor(my);
        handleMouse(p, mc, mx, my);

        String name = p.def.getItem().getDescription().getString();
        g.drawString(font, font.plainSubstrByWidth(name, 150), 1, 2, 0xFF303030, false);
        g.fill(SX, SY, SX + SW, SY + SH, 0x30000000);
        g.renderOutline(SX, SY, SW, SH, 0xFF555555);
        if (p.error() != null) {
            g.drawWordWrap(font, Component.literal(p.error()).withStyle(ChatFormatting.RED), SX + 4, SY + 4, SW - 8, 0xFFFF5555);
            return;
        }
        BlockPos hovered = null;
        try {
            hovered = p.render(g, SX + 1, SY + 1, SW - 2, SH - 2, imx, imy);
        } catch (Throwable t) {
            g.drawString(font, "Preview failed: " + t.getClass().getSimpleName(), SX + 4, SY + 4, 0xFFFF5555, false);
            if (LOGGED.add(p.def.getId().toString()))
                com.omnicrox.gtaa.GTAA.LOGGER.error("[Apex preview] {} failed to render", p.def.getId(), t);
        }
        if (p.isCompiling())
            g.drawString(font, "Loading " + (int) (p.compileProgress() * 100) + "%", SX + 4, SY + SH - 11, 0xFFFFFFFF, true);

        // what the selected spot accepts (BlockRenderer6343 shows it in place of the ingredients)
        List<ItemStack> cands = p.selected == null ? List.of() : p.candidates(p.selected);
        ItemStack hoveredCandidate = ItemStack.EMPTY;
        if (!cands.isEmpty()) {
            int n = Math.min(cands.size(), 18), rows = (n + 8) / 9;
            int py = SY + SH - 2 - rows * 18 - 10;
            g.fill(SX + 1, py, SX + SW - 1, SY + SH - 1, 0xC0101010);
            g.drawString(font, "Accepts (right-click empty space to close):", SX + 3, py + 2, 0xFFBBBBBB, false);
            for (int i = 0; i < n; i++) {
                int ix = SX + 3 + (i % 9) * 18, iy = py + 11 + (i / 9) * 18;
                g.renderItem(cands.get(i), ix, iy);
                if (in(mx, my, ix, iy, 16, 16)) hoveredCandidate = cands.get(i);
            }
        }

        // sliders and buttons (BlockRenderer6343 layout)
        int row = 0;
        if (p.maxTier() > 1) {
            drawSlider(g, UNDER + ROW * row++, "Tier: " + (p.tier < 0 ? "Shown" : (p.tier + 1) + ""), p.tier + 1, 0, p.maxTier(), imx, imy);
        }
        drawSlider(g, UNDER + ROW * row, "Layer: " + (p.layer == 0 ? "All" : p.layer + ""), p.layer, 0, p.layers(), imx, imy);
        drawButton(g, btnX(0), btnY(0), "H", p.highlightHatches, imx, imy);
        drawButton(g, btnX(1), btnY(1), "R", false, imx, imy);
        drawButton(g, btnX(2), btnY(2), "?", false, imx, imy);
        if (p.pages() > 1) {
            drawSmall(g, btnX(3), btnY(3), "<", imx, imy);
            drawSmall(g, btnX(3) + 10, btnY(3), ">", imx, imy);
            String sh = "Shape " + (p.page + 1) + "/" + p.pages();
            g.drawString(font, sh, SX + SW - 4 - font.width(sh), SY + 3, 0xFFFFFFFF, true);
        }

        // the blocks this shape / tier needs (re-counted whenever the shape or tier changes)
        List<ItemStack> parts = p.currentParts();
        hoveredPart = ItemStack.EMPTY;
        int n = Math.min(18, parts.size());
        for (int i = 0; i < n; i++) {
            int ix = 1 + (i % 9) * 18, iy = SLOTS_Y + 1 + (i / 9) * 18;
            ItemStack st = parts.get(i);
            if (i == 17 && parts.size() > 18) st = parts.get(17 + (int) ((System.currentTimeMillis() / 1000) % (parts.size() - 17)));
            g.fill(ix - 1, iy - 1, ix + 17, iy + 17, 0xFF8B8B8B);
            g.fill(ix - 1, iy - 1, ix + 17, iy, 0xFF373737);
            g.fill(ix - 1, iy - 1, ix, iy + 17, 0xFF373737);
            g.fill(ix, iy + 16, ix + 17, iy + 17, 0xFFFFFFFF);
            g.fill(ix + 16, iy, ix + 17, iy + 17, 0xFFFFFFFF);
            g.renderItem(st, ix, iy);
            g.renderItemDecorations(font, st, ix, iy);
            if (in(mx, my, ix, iy, 16, 16)) {
                hoveredPart = st;
                g.fill(ix, iy, ix + 16, iy + 16, 0x80FFFFFF);
            }
        }

        // tooltips
        List<Component> tip = null;
        if (!hoveredPart.isEmpty()) {
            tip = new ArrayList<>(net.minecraft.client.gui.screens.Screen.getTooltipFromItem(mc, hoveredPart));
            if (hoveredPart.getCount() > 1) tip.add(Component.literal("Needed: " + hoveredPart.getCount()).withStyle(ChatFormatting.YELLOW));
            if (SHOW_RECIPES != null) tip.add(Component.literal("Click: recipes, right-click: uses").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (!hoveredCandidate.isEmpty()) tip = List.of(hoveredCandidate.getHoverName());
        else if (hovered != null && target == Target.NONE) tip = p.tooltip(hovered);
        if (in(mx, my, btnX(0), btnY(0), BTN_W, BTN_H)) tip = List.of(Component.literal("Toggle hatch position highlight"));
        else if (in(mx, my, btnX(1), btnY(1), BTN_W, BTN_H)) tip = List.of(Component.literal("Reset the view"));
        else if (in(mx, my, btnX(2), btnY(2), BTN_W, BTN_H)) tip = help();
        else if (p.pages() > 1 && in(mx, my, btnX(3), btnY(3), BTN_W, BTN_H))
            tip = List.of(Component.literal("Shape " + (p.page + 1) + " of " + p.pages() + " (sizes / variants of this multi)"),
                    Component.literal("< previous, > next").withStyle(ChatFormatting.GRAY));
        if (tip != null && !tip.isEmpty()) g.renderTooltip(font, tip, Optional.empty(), imx, imy);
    }

    private static List<Component> help() {
        List<Component> l = new ArrayList<>();
        l.add(Component.literal("Multiblock Structure").withStyle(ChatFormatting.GOLD));
        l.add(Component.literal("Left-drag: rotate").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Right-drag or scroll: zoom").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Middle-drag: move").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Right-click a block: what that spot accepts").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("Tier / Layer: drag, or scroll over them").withStyle(ChatFormatting.GRAY));
        l.add(Component.literal("H: highlight where hatches can go").withStyle(ChatFormatting.GRAY));
        return l;
    }

    private static void handleMouse(MultiblockPreview p, Minecraft mc, double mx, double my) {
        long win = mc.getWindow().getWindow();
        boolean[] down = new boolean[3];
        for (int b = 0; b < 3; b++) down[b] = GLFW.glfwGetMouseButton(win, b) == GLFW.GLFW_PRESS;
        long now = System.nanoTime();
        // a different preview, or this one wasn't drawn for a while: start fresh
        if (owner != p || now - lastFrame > 200_000_000L) {
            if (owner != p) target = Target.NONE;
            System.arraycopy(down, 0, wasDown, 0, 3);
            lastX = mx;
            lastY = my;
            owner = p;
        }
        lastFrame = now;
        double dx = mx - lastX, dy = my - lastY;
        boolean inScene = in(mx, my, SX, SY, SW, SH);

        if (target == Target.NONE && !hoveredPart.isEmpty() && SHOW_RECIPES != null && my >= SLOTS_Y
                && ((down[0] && !wasDown[0]) || (down[1] && !wasDown[1]))) {
            ItemStack st = hoveredPart.copyWithCount(1);
            boolean uses = down[1];
            System.arraycopy(down, 0, wasDown, 0, 3);
            Minecraft.getInstance().tell(() -> SHOW_RECIPES.accept(st, uses));
            return;
        }
        if (target == Target.NONE) {
            if (down[0] && !wasDown[0]) leftPress(p, mx, my, inScene);
            else if (down[1] && !wasDown[1] && inScene) {
                target = Target.ZOOM;
                // right-click selects the block (BlockRenderer6343), empty space clears it
                p.selected = p.hoveredBlock();
            } else if (down[2] && !wasDown[2] && inScene) target = Target.PAN;
        }
        switch (target) {
            case ROTATE -> {
                if (!down[0]) target = Target.NONE;
                else {
                    float sp = GTAAClientConfig.PREVIEW_ROTATE_SPEED.get().floatValue();
                    p.rotH = (p.rotH + (float) dx * (GTAAClientConfig.PREVIEW_INVERT_X.get() ? -sp : sp) + 360) % 360;
                    p.rotV = (float) Math.max(-89.9, Math.min(89.9, p.rotV + dy * (GTAAClientConfig.PREVIEW_INVERT_Y.get() ? -sp : sp)));
                }
            }
            case ZOOM -> {
                if (!down[1]) target = Target.NONE;
                else if (Math.abs(dy) > 1) p.distance = (float) Math.max(3, Math.min(999, p.distance + (dy > 0 ? 0.15 : -0.15) * Math.abs(dy)));
            }
            case PAN -> {
                if (!down[2]) target = Target.NONE;
                else p.pan(dx, dy);
            }
            case TIER, LAYER -> {
                if (!down[0]) target = Target.NONE;
                else setSlider(p, target, mx);
            }
            default -> {}
        }
        System.arraycopy(down, 0, wasDown, 0, 3);
        lastX = mx;
        lastY = my;
    }

    private static void leftPress(MultiblockPreview p, double mx, double my, boolean inScene) {
        if (in(mx, my, btnX(0), btnY(0), BTN_W, BTN_H)) {
            p.highlightHatches = !p.highlightHatches;
            click();
            return;
        }
        if (in(mx, my, btnX(1), btnY(1), BTN_W, BTN_H)) {
            p.resetView();
            click();
            return;
        }
        if (in(mx, my, btnX(2), btnY(2), BTN_W, BTN_H)) return;
        if (p.pages() > 1 && in(mx, my, btnX(3), btnY(3), BTN_W, BTN_H)) {
            p.page = Math.floorMod(p.page + (mx < btnX(3) + 10 ? -1 : 1), p.pages());
            p.layer = 0;
            p.markDirty();
            click();
            return;
        }
        int row = 0;
        if (p.maxTier() > 1) {
            if (in(mx, my, 0, UNDER + ROW * row, SLIDER_W, SLIDER_H)) {
                target = Target.TIER;
                setSlider(p, target, mx);
                return;
            }
            row++;
        }
        if (in(mx, my, 0, UNDER + ROW * row, SLIDER_W, SLIDER_H)) {
            target = Target.LAYER;
            setSlider(p, target, mx);
            return;
        }
        if (inScene) target = Target.ROTATE;
    }

    private static void click() {
        Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1f));
    }

    private static void setSlider(MultiblockPreview p, Target t, double mx) {
        int max = t == Target.LAYER ? p.layers() : p.maxTier();
        int v = (int) Math.round((mx - 4) / (SLIDER_W - 8) * max);
        v = Math.max(0, Math.min(max, v));
        if (t == Target.LAYER) p.setLayer(v);
        else setTier(p, v - 1);
    }

    private static void setTier(MultiblockPreview p, int t) {
        t = Math.max(-1, Math.min(p.maxTier() - 1, t));
        if (t == p.tier) return;
        p.tier = t;
        p.markDirty();
    }

    /** scroll over the page (local coordinates); true = used */
    static boolean scroll(MultiblockPreview p, double mx, double my, double delta) {
        if (in(mx, my, SX, SY, SW, SH)) {
            double step = GTAAClientConfig.PREVIEW_ZOOM_SPEED.get();
            p.distance = (float) Math.max(3, Math.min(999, p.distance - delta * step));
            return true;
        }
        int step = delta > 0 ? 1 : -1, row = 0;
        if (p.maxTier() > 1) {
            if (in(mx, my, 0, UNDER, SLIDER_W, SLIDER_H)) {
                setTier(p, p.tier + step);
                return true;
            }
            row++;
        }
        if (in(mx, my, 0, UNDER + ROW * row, SLIDER_W, SLIDER_H)) {
            p.setLayer(p.layer + step);
            return true;
        }
        return in(mx, my, 0, 0, W, SLOTS_Y);
    }
}
