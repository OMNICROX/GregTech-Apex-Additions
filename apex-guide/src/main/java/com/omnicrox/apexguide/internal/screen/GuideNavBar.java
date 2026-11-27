package com.omnicrox.apexguide.internal.screen;

import com.omnicrox.apexguide.Guide;
import com.omnicrox.apexguide.internal.GuideMEClient;
import com.omnicrox.apexguide.internal.GuidebookText;
import com.omnicrox.apexguide.internal.home.GuideUserData;
import com.omnicrox.apexguide.navigation.NavigationNode;
import com.omnicrox.apexguide.navigation.NavigationTree;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

/**
 * Collapsible navigation tree on the left side of the guide window.
 * <p>
 * Layout, metrics and behavior follow GuideNH's GuideNavBar (LGPL-3.0, HFstudio): a 10px strip with an arrow while
 * closed, opening as an overlay on hover; a title row with a pin button; 12px rows with expand arrows, 9px icons, a
 * highlighted current page and a bookmark toggle on hover; sticky parent rows while scrolling; and a title filter.
 */
public class GuideNavBar extends AbstractWidget {
    public static final int WIDTH_CLOSED = 10;
    public static final int MIN_OPEN_WIDTH = 110;
    public static final int OPEN_WIDTH_SCREEN_PERCENT = 18;
    public static final int TITLE_H = 16;
    public static final int ROW_H = 12;
    public static final int INDENT = 8;
    public static final int ICON_SIZE = 9;
    private static final int ACTION_SLOT_W = 12;
    private static final int SCROLLBAR_W = 4;

    private final GuideScreen screen;
    private final Guide guide;

    private NavigationTree navTree;
    private final List<Row> allRows = new ArrayList<>();
    private final List<Row> rows = new ArrayList<>();
    private final Set<ResourceLocation> expanded = new HashSet<>();
    private static final Set<ResourceLocation> REMEMBERED_EXPANDED = new HashSet<>();

    private int openWidth = 150;
    private boolean pinned;
    private boolean hoverOpen;
    private int scrollY;
    private boolean draggingScrollbar;
    private int scrollGrab;

    @Nullable
    private EditBox filterField;
    private String filter = "";

    public GuideNavBar(GuideScreen screen, Guide guide) {
        super(0, 0, WIDTH_CLOSED, 100, Component.literal("Navigation"));
        this.screen = screen;
        this.guide = guide;
        this.pinned = GuideUserData.isNavPinned();
        this.expanded.addAll(REMEMBERED_EXPANDED);
    }

    // ------------------------------------------------------------------ layout

    public void setBounds(int x, int y, int height, int openWidth) {
        setX(x);
        setY(y);
        this.height = height;
        this.openWidth = Math.max(WIDTH_CLOSED, openWidth);
        this.width = isOpen() ? this.openWidth : WIDTH_CLOSED;
        clampScroll();
    }

    public boolean isOpen() {
        return pinned || hoverOpen || (filterField != null && filterField.isFocused());
    }

    public boolean isPinned() {
        return pinned;
    }

    /** Horizontal space the document must leave free for the navigation. */
    public int reservedWidth() {
        return pinned ? openWidth : WIDTH_CLOSED;
    }

    public boolean hasRows() {
        ensureRows();
        return !allRows.isEmpty();
    }

    private void ensureRows() {
        var current = guide.getNavigationTree();
        if (current == navTree) {
            return;
        }
        navTree = current;
        allRows.clear();
        for (var root : navTree.getRootNodes()) {
            addRows(root, null, 0);
        }
        rebuildVisibleRows();
    }

    private void addRows(NavigationNode node, @Nullable Row parent, int depth) {
        var row = new Row(node, parent, depth);
        allRows.add(row);
        for (var child : node.children()) {
            addRows(child, row, depth + 1);
        }
    }

    private void rebuildVisibleRows() {
        rows.clear();
        var query = filter.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            for (var row : allRows) {
                if (row.isVisible()) {
                    rows.add(row);
                }
            }
        } else {
            // Show matches together with the chain of parents that leads to them
            var keep = new HashSet<Row>();
            for (var row : allRows) {
                if (row.node.title().toLowerCase(Locale.ROOT).contains(query)) {
                    for (var r = row; r != null; r = r.parent) {
                        keep.add(r);
                    }
                }
            }
            for (var row : allRows) {
                if (keep.contains(row)) {
                    rows.add(row);
                }
            }
        }
        clampScroll();
    }

    public void expandParentsOf(@Nullable ResourceLocation pageId) {
        ensureRows();
        if (pageId == null) {
            return;
        }
        for (var row : allRows) {
            if (pageId.equals(row.node.pageId())) {
                for (var p = row.parent; p != null; p = p.parent) {
                    setExpanded(p, true);
                }
            }
        }
        rebuildVisibleRows();
        scrollToPage(pageId);
    }

    private void scrollToPage(ResourceLocation pageId) {
        for (int i = 0; i < rows.size(); i++) {
            if (pageId.equals(rows.get(i).node.pageId())) {
                int rowTop = i * ROW_H;
                int body = bodyHeight();
                if (rowTop < scrollY || rowTop + ROW_H > scrollY + body) {
                    scrollY = rowTop - body / 3;
                    clampScroll();
                }
                return;
            }
        }
    }

    private void setExpanded(Row row, boolean value) {
        var key = row.key();
        if (value) {
            expanded.add(key);
            REMEMBERED_EXPANDED.add(key);
        } else {
            expanded.remove(key);
            REMEMBERED_EXPANDED.remove(key);
        }
    }

    private int bodyY() {
        return getY() + TITLE_H;
    }

    private int bodyHeight() {
        return Math.max(0, height - TITLE_H);
    }

    private int maxScroll() {
        return Math.max(0, rows.size() * ROW_H - bodyHeight());
    }

    private void clampScroll() {
        scrollY = Mth.clamp(scrollY, 0, maxScroll());
    }

    private boolean hasScrollbar() {
        return maxScroll() > 0;
    }

    private int rowRight() {
        return getX() + width - 1 - (hasScrollbar() ? SCROLLBAR_W + 1 : 0);
    }

    private int pinX() {
        return getX() + width - 1 - 16 - 1;
    }

    // ------------------------------------------------------------------ rendering

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        ensureRows();
        active = !allRows.isEmpty();
        if (allRows.isEmpty()) {
            return;
        }

        // Hover-open (GuideNH opens the bar as an overlay while the mouse is over it)
        boolean inside = mouseX >= getX() && mouseX < getX() + (isOpen() ? openWidth : WIDTH_CLOSED)
                && mouseY >= getY() && mouseY < getY() + height;
        if (!pinned) {
            if (inside && !hoverOpen) {
                hoverOpen = true;
            } else if (!inside && hoverOpen && !draggingScrollbar) {
                hoverOpen = false;
            }
        }
        width = isOpen() ? openWidth : WIDTH_CLOSED;

        var pose = g.pose();
        pose.pushPose();
        if (!pinned && isOpen()) {
            pose.translate(0, 0, 300); // overlay above the document
        }

        int x = getX();
        int y = getY();
        g.fillGradient(x, y, x + width, y + height, ApexTheme.NAV_BG_TOP, ApexTheme.NAV_BG_BOTTOM);
        g.fill(x + width - 1, y, x + width, y + height, ApexTheme.SEPARATOR);

        if (!isOpen()) {
            drawArrow(g, x + width / 2 - 2, y + height / 2 - 3, true, ApexTheme.NAV_ARROW_CLOSED);
            pose.popPose();
            return;
        }

        renderTitle(g, mouseX, mouseY);

        int bodyY = bodyY();
        int bodyBottom = bodyY + bodyHeight();
        g.enableScissor(x, bodyY, x + width - 1, bodyBottom);
        var font = Minecraft.getInstance().font;
        var currentPage = screen.getCurrentPageIdOrNull();

        int first = Math.max(0, scrollY / ROW_H);
        var sticky = computeSticky(first);
        for (int i = first; i < rows.size(); i++) {
            int rowY = bodyY + i * ROW_H - scrollY;
            if (rowY >= bodyBottom) {
                break;
            }
            var row = rows.get(i);
            if (sticky.contains(row)) {
                continue;
            }
            renderRow(g, font, row, rowY, mouseX, mouseY, currentPage, false);
        }
        // Sticky parents: the chain of ancestors of the first visible row stays pinned to the top
        for (int s = 0; s < sticky.size(); s++) {
            var row = sticky.get(s);
            int stickyY = bodyY + s * ROW_H;
            // Push up when the subtree of this parent scrolls out
            int lastIdx = subtreeEnd(row);
            int limit = bodyY + lastIdx * ROW_H - scrollY;
            stickyY = Math.min(stickyY, limit - ROW_H * (sticky.size() - s));
            renderRow(g, font, row, stickyY, mouseX, mouseY, currentPage, true);
        }
        g.disableScissor();

        renderScrollbar(g, mouseX, mouseY);
        pose.popPose();
    }

    private List<Row> computeSticky(int firstVisible) {
        var result = new ArrayList<Row>();
        if (firstVisible >= rows.size() || scrollY == 0) {
            return result;
        }
        var anchor = rows.get(firstVisible);
        for (var p = anchor.parent; p != null; p = p.parent) {
            if (rows.contains(p)) {
                result.add(0, p);
            }
        }
        return result;
    }

    /** Index (exclusive) of the last visible row belonging to the subtree of a row. */
    private int subtreeEnd(Row row) {
        int idx = rows.indexOf(row);
        int i = idx + 1;
        while (i < rows.size() && rows.get(i).depth > row.depth) {
            i++;
        }
        return i;
    }

    private void renderTitle(GuiGraphics g, int mouseX, int mouseY) {
        var font = Minecraft.getInstance().font;
        int x = getX();
        int y = getY();
        int borderX = x + width - 1;
        g.fill(x, y, borderX, y + TITLE_H, ApexTheme.NAV_TITLE_BG);
        g.fill(x, y + TITLE_H - 1, borderX, y + TITLE_H, ApexTheme.SEPARATOR);

        int pinX = pinX();
        boolean pinHovered = isInside(mouseX, mouseY, pinX, y, 16, 16);
        GuideIconButton.drawIcon(g, GuideIconButton.Role.NAVIGATION_PIN, pinX, y,
                ApexTheme.iconColor(true, pinHovered, pinned));

        int titleX = x + 6;
        int titleW = pinX - 1 - titleX;
        if (filterField != null) {
            filterField.setX(titleX);
            filterField.setY(y + (TITLE_H - font.lineHeight) / 2 + 1);
            filterField.setWidth(titleW);
            filterField.render(g, mouseX, mouseY, 0);
            if (filterField.getValue().isEmpty() && !filterField.isFocused()) {
                g.drawString(font, GuidebookText.NavigationFilter.text(), titleX, filterField.getY(),
                        ApexTheme.TEXT_DIM, false);
            }
            return;
        }
        var title = GuidebookText.NavigationTitle.text().getString();
        boolean hovered = isInside(mouseX, mouseY, titleX, y, titleW, TITLE_H);
        g.drawString(font, font.plainSubstrByWidth(title, titleW), titleX, y + (TITLE_H - font.lineHeight) / 2 + 1,
                hovered ? ApexTheme.ACCENT_SOFT : ApexTheme.TEXT_TITLE, false);
    }

    private void renderRow(GuiGraphics g, Font font, Row row, int rowY, int mouseX, int mouseY,
            @Nullable ResourceLocation currentPage, boolean sticky) {
        int x = getX();
        int rowRight = rowRight();
        boolean hovered = !draggingScrollbar && mouseX >= x && mouseX < rowRight
                && mouseY >= Math.max(rowY, bodyY()) && mouseY < rowY + ROW_H
                && mouseY < bodyY() + bodyHeight();
        boolean current = currentPage != null && currentPage.equals(row.node.pageId());
        var pageId = row.node.hasPage() ? row.node.pageId() : null;
        boolean bookmarked = pageId != null && GuideUserData.isBookmarked(guide.getId(), pageId);
        boolean bookmarkVisible = pageId != null && (hovered || bookmarked);

        if (sticky) {
            g.fill(x, rowY, rowRight, rowY + ROW_H, ApexTheme.NAV_STICKY_BG);
            g.fill(x, rowY + ROW_H - 1, rowRight, rowY + ROW_H, ApexTheme.NAV_STICKY_LINE);
        }
        if (current) {
            g.fill(x, rowY, rowRight, rowY + ROW_H, ApexTheme.NAV_ROW_CURRENT);
        } else if (hovered) {
            g.fill(x, rowY, rowRight, rowY + ROW_H, ApexTheme.NAV_ROW_HOVER);
        }

        int rowX = x + 2 + row.depth * 3;
        if (row.hasChildren()) {
            drawArrow(g, rowX, rowY + 2, !row.isExpanded(), ApexTheme.NAV_ARROW);
        }
        int textX = rowX + INDENT;
        if (!row.node.icon().isEmpty()) {
            var pose = g.pose();
            pose.pushPose();
            pose.translate(textX, rowY + (ROW_H - ICON_SIZE) / 2f, 0);
            pose.scale(ICON_SIZE / 16f, ICON_SIZE / 16f, 1);
            g.renderItem(row.node.icon(), 0, 0);
            pose.popPose();
            textX += ICON_SIZE + 2;
        }

        int textRight = rowRight - 1 - (bookmarkVisible ? ACTION_SLOT_W : 0);
        int maxW = textRight - textX;
        if (maxW > 0) {
            var title = row.node.title();
            if (font.width(title) > maxW) {
                title = font.plainSubstrByWidth(title, Math.max(0, maxW - font.width("…"))) + "…";
            }
            g.drawString(font, title, textX, rowY + 2, ApexTheme.rowTextColor(current, hovered), false);
        }

        if (bookmarkVisible) {
            int bx = rowRight - ACTION_SLOT_W + 1;
            boolean bHovered = hovered && mouseX >= bx;
            GuideIconButton.drawIcon(g, bookmarked ? GuideIconButton.Role.BOOKMARKED : GuideIconButton.Role.BOOKMARK,
                    bx, rowY + 1, 10, ApexTheme.iconColor(true, bHovered, bookmarked));
        }
    }

    private void renderScrollbar(GuiGraphics g, int mouseX, int mouseY) {
        if (!hasScrollbar()) {
            return;
        }
        int x = getX() + width - 1 - SCROLLBAR_W;
        int top = bodyY();
        int h = bodyHeight();
        int thumbH = thumbHeight();
        int thumbY = top + (int) ((long) scrollY * (h - thumbH) / Math.max(1, maxScroll()));
        boolean hovered = draggingScrollbar || isInside(mouseX, mouseY, x, top, SCROLLBAR_W, h);
        g.fill(x, top, x + SCROLLBAR_W, top + h, ApexTheme.SCROLL_TRACK);
        g.fill(x, thumbY, x + SCROLLBAR_W, thumbY + thumbH,
                hovered ? ApexTheme.SCROLL_THUMB_HOVER : ApexTheme.SCROLL_THUMB);
    }

    private int thumbHeight() {
        int h = bodyHeight();
        int content = Math.max(1, rows.size() * ROW_H);
        return Math.max(16, h * h / Math.max(h, content));
    }

    /** Small filled triangle, pointing right (collapsed) or down (expanded), like GuideNH's drawArrow. */
    static void drawArrow(GuiGraphics g, int x, int y, boolean pointRight, int color) {
        if (pointRight) {
            for (int i = 0; i < 4; i++) {
                g.fill(x + i, y + i, x + i + 1, y + 7 - i, color);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                g.fill(x + i, y + 1 + i, x + 7 - i, y + 2 + i, color);
            }
        }
    }

    private static boolean isInside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return active && visible && isInside(mouseX, mouseY, getX(), getY(), width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || !isOpen()) {
            if (filterField != null && filterField.isFocused() && !isMouseOver(mouseX, mouseY)) {
                filterField.setFocused(false);
            }
            return false;
        }
        int y = getY();
        // Title row: pin button / filter
        if (mouseY < y + TITLE_H) {
            if (isInside(mouseX, mouseY, pinX(), y, 16, 16)) {
                pinned = !pinned;
                hoverOpen = !pinned;
                GuideUserData.setNavPinned(pinned);
                playClick();
                screen.onNavigationLayoutChanged();
                return true;
            }
            openFilter();
            if (filterField != null) {
                filterField.mouseClicked(mouseX, mouseY, button);
                filterField.setFocused(true);
            }
            return true;
        }
        // Scrollbar
        if (hasScrollbar() && mouseX >= getX() + width - 1 - SCROLLBAR_W) {
            int thumbH = thumbHeight();
            int thumbY = bodyY() + (int) ((long) scrollY * (bodyHeight() - thumbH) / Math.max(1, maxScroll()));
            draggingScrollbar = true;
            scrollGrab = (mouseY >= thumbY && mouseY < thumbY + thumbH) ? (int) mouseY - thumbY : thumbH / 2;
            dragScrollbarTo(mouseY);
            return true;
        }
        var row = pickRow(mouseX, mouseY);
        if (row == null) {
            return true;
        }
        var pageId = row.node.hasPage() ? row.node.pageId() : null;
        // Bookmark toggle
        if (pageId != null && mouseX >= rowRight() - ACTION_SLOT_W + 1) {
            GuideUserData.toggleBookmark(guide.getId(), pageId);
            playClick();
            return true;
        }
        int rowX = getX() + 2 + row.depth * 3;
        boolean onArrow = row.hasChildren() && mouseX < rowX + INDENT;
        if (onArrow || (row.hasChildren() && pageId == null)) {
            setExpanded(row, !row.isExpanded());
            rebuildVisibleRows();
            playClick();
            return true;
        }
        if (row.hasChildren() && !row.isExpanded()) {
            setExpanded(row, true);
            rebuildVisibleRows();
        }
        if (pageId != null) {
            playClick();
            screen.navigateTo(pageId);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingScrollbar) {
            draggingScrollbar = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar) {
            dragScrollbarTo(mouseY);
            return true;
        }
        return false;
    }

    private void dragScrollbarTo(double mouseY) {
        int thumbH = thumbHeight();
        int travel = Math.max(1, bodyHeight() - thumbH);
        int pos = (int) mouseY - scrollGrab - bodyY();
        scrollY = (int) ((long) pos * maxScroll() / travel);
        clampScroll();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isOpen() || !isMouseOver(mouseX, mouseY)) {
            return false;
        }
        scrollY -= (int) Math.round(delta * ROW_H * 2);
        clampScroll();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (filterField != null && filterField.isFocused()) {
            if (keyCode == 256) { // escape clears the filter
                closeFilter();
                return true;
            }
            return filterField.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (filterField != null && filterField.isFocused()) {
            return filterField.charTyped(codePoint, modifiers);
        }
        return false;
    }

    public boolean isFilterFocused() {
        return filterField != null && filterField.isFocused();
    }

    private void openFilter() {
        if (filterField != null) {
            return;
        }
        var font = Minecraft.getInstance().font;
        filterField = new EditBox(font, getX() + 6, getY() + 4, Math.max(10, pinX() - getX() - 7), font.lineHeight,
                Component.empty());
        filterField.setBordered(false);
        filterField.setMaxLength(128);
        filterField.setTextColor(0xFFFFFF);
        filterField.setResponder(value -> {
            filter = value;
            scrollY = 0;
            rebuildVisibleRows();
        });
    }

    private void closeFilter() {
        filterField = null;
        filter = "";
        rebuildVisibleRows();
    }

    @Nullable
    private Row pickRow(double mouseX, double mouseY) {
        int bodyY = bodyY();
        if (mouseY < bodyY || mouseY >= bodyY + bodyHeight()) {
            return null;
        }
        // Sticky rows sit on top of the regular ones
        int first = Math.max(0, scrollY / ROW_H);
        var sticky = computeSticky(first);
        int stickyIdx = (int) ((mouseY - bodyY) / ROW_H);
        if (stickyIdx < sticky.size()) {
            return sticky.get(stickyIdx);
        }
        int idx = (int) ((mouseY - bodyY + scrollY) / ROW_H);
        return idx >= 0 && idx < rows.size() ? rows.get(idx) : null;
    }

    private void playClick() {
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(GuideMEClient.GUIDE_CLICK_EVENT, 1.0F));
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    private class Row {
        final NavigationNode node;
        @Nullable
        final Row parent;
        final int depth;

        Row(NavigationNode node, @Nullable Row parent, int depth) {
            this.node = node;
            this.parent = parent;
            this.depth = depth;
        }

        ResourceLocation key() {
            return node.pageId() != null ? node.pageId()
                    : new ResourceLocation("apexguide", "nav/" + Integer.toHexString(node.title().hashCode()));
        }

        boolean hasChildren() {
            return !node.children().isEmpty();
        }

        boolean isExpanded() {
            return !filter.isBlank() || expanded.contains(key());
        }

        boolean isVisible() {
            for (var p = parent; p != null; p = p.parent) {
                if (!p.isExpanded()) {
                    return false;
                }
            }
            return true;
        }
    }
}
