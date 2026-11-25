package com.omnicrox.gtaa.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Rolling credits for the Ascension: the player's stats drift up the screen. */
public class AscensionCreditsScreen extends Screen {

    private static final int GOLD = 0xFFFFC94A, PINK = 0xFFFF9AD5, TEXT = 0xFFE6DDF7, MUTED = 0xFF9A8CB8;
    private final List<String> lines;
    private final boolean celebrate;
    private float time;

    public AscensionCreditsScreen(List<String> lines, boolean celebrate) {
        super(Component.literal("Ascended"));
        this.lines = lines;
        this.celebrate = celebrate;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        time += pt;
        g.fill(0, 0, width, height, 0xE0100818);
        int cx = width / 2;
        float scroll = celebrate ? Math.max(0, height - time * 0.9f) : 0;
        int y = (int) (height / 5f + scroll);
        g.pose().pushPose();
        g.pose().translate(cx, y, 0);
        g.pose().scale(3f, 3f, 1f);
        String title = "ASCENDED";
        g.drawString(font, title, -font.width(title) / 2, 0, GOLD, true);
        g.pose().popPose();
        y += 40;
        String sub = "The Ascension Gate is open.";
        g.drawString(font, sub, cx - font.width(sub) / 2, y, PINK, true);
        y += 24;
        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i);
            boolean last = i == lines.size() - 1;
            int c = i == 0 ? GOLD : last ? PINK : TEXT;
            if (last) y += 10;
            g.drawString(font, l, cx - font.width(l) / 2, y, c, true);
            y += 14;
        }
        String hint = "Esc to close";
        g.drawString(font, hint, cx - font.width(hint) / 2, height - 14, MUTED, false);
        super.render(g, mx, my, pt);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
