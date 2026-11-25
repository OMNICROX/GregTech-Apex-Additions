package com.omnicrox.gtaa.client;

import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure Upgrader screen. A status section says what the machine can't run and what would fix it; then every
 * block family and hatch gets a NEEDED / OPTIONAL / MAXED badge, a line on what it does in this machine, and one
 * button per tier. Button tags: "+12" = unlocks 12 recipes, "★" = recommended, "↓" = downgrade.
 * Green buttons = you carry enough blocks (grey ones still swap as many as you have). Hover for details.
 */
public class StructureUpgraderScreen extends Screen {

    private static final int W = 340, LINE = 10, BTN_H = 12, GOLD = 0xFFFFC94A, CYAN = 0xFF5FF3FF, TEXT = 0xFFE6DDF7,
            MUTED = 0xFF9A8CB8, EDGE = 0xFF6B47B8, EDGE_DIM = 0xFF3A2766, RED = 0xFFFF6B6B, GREEN = 0xFF7CE38B;

    private final GTAANetwork.UpgraderScreenPacket data;
    private final List<Btn> buttons = new ArrayList<>();
    private final List<List<FormattedCharSequence>> infoLines = new ArrayList<>(), verdictLines = new ArrayList<>();
    private int left, top, height2, scroll, maxScroll;
    private int[] rowTop = new int[0], rowHeight = new int[0], btnTop = new int[0];

    private record Btn(GTAANetwork.Choice choice, int row, int x, int line, int w) {}

    public static void open(GTAANetwork.UpgraderScreenPacket data) {
        Minecraft.getInstance().setScreen(new StructureUpgraderScreen(data));
    }

    StructureUpgraderScreen(GTAANetwork.UpgraderScreenPacket data) {
        super(Component.literal("Structure Upgrader"));
        this.data = data;
    }

    private String buttonText(GTAANetwork.Choice c) {
        return c.tag().isEmpty() ? c.label() : c.label() + "  " + c.tag();
    }

    @Override
    protected void init() {
        buttons.clear();
        infoLines.clear();
        verdictLines.clear();
        int n = data.rows().size();
        rowTop = new int[n];
        rowHeight = new int[n];
        btnTop = new int[n];
        int textW = W - 34;
        int y = 0;
        for (int r = 0; r < n; r++) {
            GTAANetwork.Row row = data.rows().get(r);
            boolean status = row.title().equals("Status");
            List<FormattedCharSequence> info = new ArrayList<>();
            if (status) for (String s : row.info()) info.addAll(font.split(FormattedText.of(s), textW));
            List<FormattedCharSequence> verdict = row.verdictText().isEmpty() || !(status || row.verdict() == 2) ? List.of() :
                    font.split(FormattedText.of(row.verdictText()), textW);
            infoLines.add(info);
            verdictLines.add(verdict);
            int h = 13 + (info.size() + verdict.size()) * LINE;
            btnTop[r] = h;
            int x = 26, line = 0;
            for (GTAANetwork.Choice c : row.choices()) {
                int w = Math.min(W - 34, font.width(buttonText(c)) + 10);
                if (x + w > W - 8 && x > 26) {
                    line++;
                    x = 26;
                }
                buttons.add(new Btn(c, r, x, line, w));
                x += w + 3;
            }
            if (!row.choices().isEmpty()) h += (line + 1) * (BTN_H + 2) + 2;
            rowTop[r] = y;
            rowHeight[r] = h + 2;
            y += rowHeight[r] + 2;
        }
        height2 = Math.min(height - 20, 30 + Math.max(30, y) + 24);
        left = (width - W) / 2;
        top = (height - height2) / 2;
        maxScroll = Math.max(0, y - (height2 - 54));
        scroll = Math.min(scroll, maxScroll);
    }

    private int rowY(int r) {
        return top + 30 + rowTop[r] - scroll;
    }

    private static int badgeColor(int verdict) {
        return verdict == 2 ? RED : verdict == 1 ? GREEN : GOLD;
    }

    private static int badgeBg(int verdict) {
        return verdict == 2 ? 0xFF4A1418 : verdict == 1 ? 0xFF173A1F : 0xFF3A2E10;
    }

    private static String badge(int verdict) {
        return verdict == 2 ? "NEEDED" : verdict == 1 ? "OPTIONAL" : "MAXED";
    }

    private int tagColor(String tag) {
        if (tag.startsWith("★")) return GOLD;
        if (tag.startsWith("↓")) return RED;
        if (tag.startsWith("+")) return GREEN;
        return MUTED;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        g.fill(left - 2, top - 2, left + W + 2, top + height2 + 2, 0xFF2B2B33);
        g.fill(left, top, left + W, top + height2, 0xF0120A1E);
        g.renderOutline(left, top, W, height2, EDGE);
        long t = minecraft.level == null ? 0 : minecraft.level.getGameTime();
        int pulse = (int) (40 + 30 * Math.sin((t + pt) / 10.0));
        g.fill(left + 4, top + 16, left + W - 4, top + 17, (pulse << 24) | 0x5FF3FF);
        g.drawString(font, "✦ STRUCTURE UPGRADER", left + 6, top + 5, GOLD, true);
        String m = font.plainSubstrByWidth(data.machine(), W - 150);
        g.drawString(font, m, left + W - 6 - font.width(m), top + 5, TEXT, false);

        int clipTop = top + 20, clipBottom = top + height2 - 22;
        g.enableScissor(left + 2, clipTop, left + W - 2, clipBottom);
        Btn hovered = null;
        int hoverRow = -1;
        for (int r = 0; r < data.rows().size(); r++) {
            int y = rowY(r);
            int rh = rowHeight[r];
            if (y + rh < clipTop || y > clipBottom) continue;
            GTAANetwork.Row row = data.rows().get(r);
            boolean status = row.choices().isEmpty() && row.itemId().isEmpty() && row.title().equals("Status");
            g.fill(left + 4, y, left + W - 4, y + rh, status ? 0xC0241A10 : 0xC01E1232);
            g.renderOutline(left + 4, y, W - 8, rh, row.verdict() == 2 ? 0xFF8A3040 : EDGE_DIM);
            ItemStack icon = stack(row.itemId());
            if (!icon.isEmpty()) g.renderItem(icon, left + 7, y + 4);
            g.drawString(font, row.title(), left + 26, y + 3, status ? GOLD : CYAN, false);
            if (mx >= left + 4 && mx < left + W - 4 && my >= y && my < y + 12 && my > clipTop && my < clipBottom) hoverRow = r;
            String b = badge(row.verdict());
            int bw = font.width(b) + 6;
            if (!row.verdictText().isEmpty()) {
                g.fill(left + W - 10 - bw, y + 2, left + W - 10, y + 12, badgeBg(row.verdict()));
                g.drawString(font, b, left + W - 7 - bw, y + 3, badgeColor(row.verdict()), false);
            }
            int maxCur = W - 50 - font.width(row.title()) - bw;
            g.drawString(font, font.plainSubstrByWidth(row.current(), Math.max(0, maxCur)),
                    left + 30 + font.width(row.title()), y + 3, MUTED, false);
            int ly = y + 13;
            for (FormattedCharSequence line : infoLines.get(r)) {
                g.drawString(font, line, left + 26, ly, TEXT, false);
                ly += LINE;
            }
            for (FormattedCharSequence line : verdictLines.get(r)) {
                g.drawString(font, line, left + 26, ly, badgeColor(row.verdict()), false);
                ly += LINE;
            }
            for (Btn btn : buttons) {
                if (btn.row != r) continue;
                int bx = left + btn.x, by = y + btnTop[r] + 2 + btn.line * (BTN_H + 2);
                boolean hover = mx >= bx && mx < bx + btn.w && my >= by && my < by + BTN_H && my > clipTop && my < clipBottom;
                if (hover) hovered = btn;
                boolean ok = btn.choice.ok();
                int fill = ok ? (hover ? 0xFF2F6B3A : 0xFF1F4A28) : (hover ? 0xFF3A3448 : 0xFF2A2436);
                g.fill(bx, by, bx + btn.w, by + BTN_H, fill);
                g.renderOutline(bx, by, btn.w, BTN_H, ok ? 0xFF5FD06B : EDGE_DIM);
                g.drawString(font, btn.choice.label(), bx + 5, by + 2, ok ? 0xFFDFFFE2 : MUTED, false);
                if (!btn.choice.tag().isEmpty())
                    g.drawString(font, btn.choice.tag(), bx + 5 + font.width(btn.choice.label() + "  "), by + 2,
                            tagColor(btn.choice.tag()), false);
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
        if (hovered == null && hoverRow >= 0) {
            GTAANetwork.Row row = data.rows().get(hoverRow);
            List<FormattedCharSequence> tip = new ArrayList<>();
            for (String s : row.info()) tip.addAll(font.split(FormattedText.of(s), 240));
            if (!row.verdictText().isEmpty()) tip.addAll(font.split(FormattedText.of(row.verdictText()), 240));
            if (!tip.isEmpty()) g.renderTooltip(font, tip, mx, my);
        }
        if (hovered != null) {
            List<FormattedCharSequence> tip = new ArrayList<>();
            for (String s : hovered.choice.hover().split("\n")) tip.addAll(font.split(FormattedText.of(s), 220));
            g.renderTooltip(font, tip, mx, my);
        }
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
                int bx = left + b.x, by = rowY(b.row) + btnTop[b.row] + 2 + b.line * (BTN_H + 2);
                if (mx >= bx && mx < bx + b.w && my >= by && my < by + BTN_H) {
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
