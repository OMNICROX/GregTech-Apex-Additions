package com.omnicrox.apexguide.internal.screen;

import com.omnicrox.apexguide.color.SymbolicColor;
import com.omnicrox.apexguide.internal.GuideME;
import com.omnicrox.apexguide.internal.GuideMEClient;
import com.omnicrox.apexguide.internal.GuidebookText;
import com.omnicrox.apexguide.internal.util.Blitter;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

/**
 * Button found in the toolbar at the top of {@link GuideScreen}.
 */
public class GuideIconButton extends Button {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;

    private Role role;
    private boolean highlighted;

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    /** Draws a toolbar icon from the GuideNH button atlas, tinted with an ARGB color. */
    public static void drawIcon(GuiGraphics guiGraphics, Role role, int x, int y, int argb) {
        drawIcon(guiGraphics, role, x, y, 16, argb);
    }

    public static void drawIcon(GuiGraphics guiGraphics, Role role, int x, int y, int size, int argb) {
        Blitter.texture(GuideME.makeId("textures/guide/buttons.png"), 256, 256)
                .src(role.iconSrcX, role.iconSrcY, 16, 16)
                .dest(x, y, size, size)
                .colorArgb(argb)
                .blit(guiGraphics);
    }

    public GuideIconButton(int x, int y, Role role, Runnable callback) {
        this(x, y, role, btn -> callback.run());
    }

    public GuideIconButton(int x, int y, Role role, Consumer<GuideIconButton> callback) {
        super(
                x,
                y,
                WIDTH,
                HEIGHT,
                role.actionText,
                btn -> callback.accept((GuideIconButton) btn),
                Supplier::get);
        this.role = role;
        setTooltip(Tooltip.create(getMessage()));
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        setMessage(role.actionText);
        setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        drawIcon(guiGraphics, role, getX(), getY(), ApexTheme.iconColor(isActive(), isHovered(), highlighted));
    }

    public enum Role {
        BACK(GuidebookText.HistoryGoBack.text(), 0, 0),
        FORWARD(GuidebookText.HistoryGoForward.text(), 16, 0),
        CLOSE(GuidebookText.Close.text(), 32, 0),
        SEARCH(GuidebookText.Search.text(), 48, 0),
        HIDE_ANNOTATIONS(GuidebookText.HideAnnotations.text(), 0, 16),
        SHOW_ANNOTATIONS(GuidebookText.ShowAnnotations.text(), 16, 16),
        ZOOM_OUT(GuidebookText.ZoomOut.text(), 32, 16),
        ZOOM_IN(GuidebookText.ZoomIn.text(), 48, 16),
        RESET_VIEW(GuidebookText.ResetView.text(), 0, 32),
        OPEN_FULL_WIDTH_VIEW(GuidebookText.FullWidthView.text(), 16, 32),
        CLOSE_FULL_WIDTH_VIEW(GuidebookText.CloseFullWidthView.text(), 32, 32),
        HOMEPAGE(GuidebookText.HomePage.text(), 16, 80),
        BOOKMARK(GuidebookText.Bookmark.text(), 128, 0),
        BOOKMARKED(GuidebookText.Bookmarked.text(), 144, 0),
        NAVIGATION_PIN(GuidebookText.NavigationPin.text(), 0, 80),
        TOGGLE_GRID(GuidebookText.ToggleGrid.text(), 16, 64);

        final Component actionText;

        final int iconSrcX;
        final int iconSrcY;

        Role(Component actionText, int iconSrcX, int iconSrcY) {
            this.actionText = actionText;
            this.iconSrcX = iconSrcX;
            this.iconSrcY = iconSrcY;
        }
    }
}
