package com.omnicrox.apexguide.internal.screen;

/**
 * Colors and metrics of the guide window. Values follow GuideNH's dark theme (LGPL-3.0, HFstudio).
 */
public final class ApexTheme {
    private ApexTheme() {
    }

    public static final int PANEL_MARGIN = 20;
    public static final int PANEL_PADDING = 8;
    public static final int TOOLBAR_H = 18;
    public static final int TOOLBAR_GAP = 3;
    public static final int WINDOW_PERCENT = 90;

    public static final int PANEL_BG = 0xE0101010;
    public static final int PANEL_BORDER = 0xFF5A5A5A;
    public static final int SEPARATOR = 0xFF2A2A2A;
    public static final int BACKGROUND_DIM = 0x34101018;
    public static final int ACCENT = 0xFF00CAF2;
    public static final int ACCENT_SOFT = 0xFF5EA8FF;

    public static final int TEXT_TITLE = 0xFFE8E8E8;
    public static final int TEXT_MUTED = 0xFFAAAAAA;
    public static final int TEXT_DIM = 0xFF666666;

    public static final int NAV_BG_TOP = 0xE0151515;
    public static final int NAV_BG_BOTTOM = 0xE0101010;
    public static final int NAV_TITLE_BG = 0xD0202020;
    public static final int NAV_ROW_CURRENT = 0x40FFFFFF;
    public static final int NAV_ROW_HOVER = 0x20FFFFFF;
    public static final int NAV_ARROW = 0xFFCCCCCC;
    public static final int NAV_ARROW_CLOSED = 0xFF888888;
    public static final int NAV_STICKY_BG = 0xF0181818;
    public static final int NAV_STICKY_LINE = 0x802A2A2A;

    public static final int HOME_PANEL = 0xA6181A20;
    public static final int HOME_ROW = 0x661E232B;
    public static final int HOME_ROW_HOVER = 0x88303946;
    public static final int HOME_TITLE = 0xFFE5E9F0;
    public static final int HOME_SUMMARY = 0xFF9AA3B2;

    public static final int SCROLL_TRACK = 0x22262D38;
    public static final int SCROLL_THUMB = 0xFF4A5566;
    public static final int SCROLL_THUMB_HOVER = 0xFF6A7A90;

    /** The window rectangle: 90% of the screen centered with a margin, or the whole screen in full-width mode. */
    public static com.omnicrox.apexguide.document.LytRect panelRect(int width, int height, boolean full) {
        int margin = full ? 0 : PANEL_MARGIN;
        int availW = Math.max(100, width - margin * 2);
        int availH = Math.max(100, height - margin * 2);
        int panelW = full ? availW : Math.min(availW, Math.max(100, width * WINDOW_PERCENT / 100));
        int panelH = full ? availH : Math.min(availH, Math.max(100, height * WINDOW_PERCENT / 100));
        int panelX = full ? 0 : margin + (availW - panelW) / 2;
        int panelY = full ? 0 : margin + (availH - panelH) / 2;
        return new com.omnicrox.apexguide.document.LytRect(panelX, panelY, panelW, panelH);
    }

    public static void drawPanel(net.minecraft.client.gui.GuiGraphics g, int screenW, int screenH,
            com.omnicrox.apexguide.document.LytRect r) {
        g.fill(0, 0, screenW, screenH, BACKGROUND_DIM);
        g.fill(r.x(), r.y(), r.right(), r.bottom(), PANEL_BG);
        g.fill(r.x(), r.y(), r.right(), r.y() + 1, PANEL_BORDER);
        g.fill(r.x(), r.bottom() - 1, r.right(), r.bottom(), PANEL_BORDER);
        g.fill(r.x(), r.y(), r.x() + 1, r.bottom(), PANEL_BORDER);
        g.fill(r.right() - 1, r.y(), r.right(), r.bottom(), PANEL_BORDER);
    }

    public static int iconColor(boolean enabled, boolean hovered, boolean active) {
        if (!enabled) {
            return 0x60FFFFFF;
        }
        if (active || hovered) {
            return ACCENT;
        }
        return 0xC0FFFFFF;
    }

    public static int rowTextColor(boolean current, boolean hovered) {
        if (current) {
            return 0xFFFFFFFF;
        }
        if (hovered) {
            return ACCENT;
        }
        return 0xFFD0D0D0;
    }
}
