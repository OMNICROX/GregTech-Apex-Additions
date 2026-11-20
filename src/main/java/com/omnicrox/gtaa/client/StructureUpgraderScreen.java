package com.omnicrox.gtaa.client;

import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure Upgrader screen: every block family and hatch of the multiblock with its current tier and a button
 * per upgrade. Green = you have enough blocks, grey = you don't (still clickable: it swaps as many as you have).
 */
public class StructureUpgraderScreen extends Screen {

    private static final int W = 300, ROW_H = 30, GOLD = 0xFFFFC94A, CYAN = 0xFF5FF3FF, TEXT = 0xFFE6DDF7,
            MUTED = 0xFF9A8CB8, EDGE = 0xFF6B47B8, EDGE_DIM = 0xFF3A2766;

    private final GTAANetwork.UpgraderScreenPacket data;
    private final List<Btn> buttons = new ArrayList<>();
    private int left, top, height2, scroll, maxScroll;
    private int[] rowTop = new int[0], rowHeight = new int[0];

    private record Btn(GTAANetwork.Choice choice, int row, int x, int line, int w) {}

    public static void open(GTAANetwork.UpgraderScreenPacket data) {
        Minecraft.getInstance().setScreen(new StructureUpgraderScreen(data));
    }

    StructureUpgraderScreen(GTAANetwork.UpgraderScreenPacket data) {
        super(Component.literal("Structure Upgrader"));
        this.data = data;
    }

    @Override
    protected void init() {
        buttons.clear();
        int n = data.rows().size();
        rowTop = new int[n];
        rowHeight = new int[n];
        int y = 0;
        for (int r = 0; r < n; r++) {
            int x = 26, line = 0;
            for (GTAANetwork.Choice c : data.rows().get(r).choices()) {
                int w = Math.min(W - 34, font.width(c.label()) + 10);
                if (x + w > W - 8 && x > 26) {
                    line++;
                    x = 26;
                }
                buttons.add(new Btn(c, r, x, line, w));
                x += w + 3;
            }
            rowTop[r] = y;
            rowHeight[r] = 17 + (line + 1) * 13;
            y += rowHeight[r] + 3;
        }
        height2 = Math.min(height - 40, 30 + Math.max(30, y) + 24);
        left = (width - W) / 2;
        top = (height - height2) / 2;
        maxScroll = Math.max(0, y - (height2 - 54));
    }

    private int rowY(int r) {
        return top + 30 + rowTop[r] - scroll;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        // frame: GT steel edge, violet glass inside
        g.fill(left - 2, top - 2, left + W + 2, top + height2 + 2, 0xFF2B2B33);
        g.fill(left, top, left + W, top + height2, 0xF0120A1E);
        g.renderOutline(left, top, W, height2, EDGE);
        long t = minecraft.level == null ? 0 : minecraft.level.getGameTime();
        int pulse = (int) (40 + 30 * Math.sin((t + pt) / 10.0));
        g.fill(left + 4, top + 16, left + W - 4, top + 17, (pulse << 24) | 0x5FF3FF);
        g.drawString(font, "✦ STRUCTURE UPGRADER", left + 6, top + 5, GOLD, true);
        String m = font.plainSubstrByWidth(data.machine(), W - 150);
        g.drawString(font, m, left + W - 6 - font.width(m), top + 5, TEXT, false);

        g.enableScissor(left + 2, top + 20, left + W - 2, top + height2 - 22);
        Btn hovered = null;
        for (int r = 0; r < data.rows().size(); r++) {
            int y = rowY(r);
            int rh = rowHeight[r] - 3;
            if (y + rh < top + 20 || y > top + height2 - 22) continue;
            GTAANetwork.Row row = data.rows().get(r);
            g.fill(left + 4, y, left + W - 4, y + rh, 0xC01E1232);
            g.renderOutline(left + 4, y, W - 8, rh, EDGE_DIM);
            ItemStack icon = stack(row.itemId());
            if (!icon.isEmpty()) g.renderItem(icon, left + 7, y + 5);
            g.drawString(font, row.title(), left + 26, y + 3, CYAN, false);
            g.drawString(font, font.plainSubstrByWidth(row.current(), W - 40 - font.width(row.title())),
                    left + 30 + font.width(row.title()), y + 3, MUTED, false);
            for (Btn b : buttons) {
                if (b.row != r) continue;
                int bx = left + b.x, by = y + 14 + b.line * 13;
                boolean hover = mx >= bx && mx < bx + b.w && my >= by && my < by + 11 && my > top + 20 && my < top + height2 - 22;
                if (hover) hovered = b;
                int fill = b.choice.ok() ? (hover ? 0xFF2F6B3A : 0xFF1F4A28) : (hover ? 0xFF3A3448 : 0xFF2A2436);
                g.fill(bx, by, bx + b.w, by + 11, fill);
                g.renderOutline(bx, by, b.w, 11, b.choice.ok() ? 0xFF5FD06B : EDGE_DIM);
                g.drawString(font, b.choice.label(), bx + 5, by + 2, b.choice.ok() ? 0xFFDFFFE2 : MUTED, false);
            }
        }
        g.disableScissor();
        if (data.rows().isEmpty())
            g.drawString(font, "Nothing to upgrade here.", left + 8, top + 34, MUTED, false);
        if (maxScroll > 0) {
            int track = height2 - 46, bar = Math.max(12, track * track / (track + maxScroll));
            int by = top + 22 + (track - bar) * scroll / maxScroll;
            g.fill(left + W - 4, by, left + W - 2, by + bar, EDGE);
        }
        g.drawString(font, font.plainSubstrByWidth(data.footer(), W - 12), left + 6, top + height2 - 14, MUTED, false);
        super.render(g, mx, my, pt);
        if (hovered != null) g.renderTooltip(font, List.of(Component.literal(hovered.choice.hover()).getVisualOrderText()), mx, my);
    }

    private static ItemStack stack(String id) {
        if (id == null || id.isEmpty()) return ItemStack.EMPTY;
        try {
            return new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(id)));
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (my > top + 20 && my < top + height2 - 22) {
            for (Btn b : buttons) {
                int bx = left + b.x, by = rowY(b.row) + 14 + b.line * 13;
                if (mx >= bx && mx < bx + b.w && my >= by && my < by + 11) {
                    String cmd = b.choice.command();
                    if (cmd.startsWith("/")) cmd = cmd.substring(1);
                    minecraft.player.connection.sendCommand(cmd);
                    onClose();
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        scroll = Math.max(0, Math.min(maxScroll, scroll - (int) (delta * 12)));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
