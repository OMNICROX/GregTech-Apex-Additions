package com.omnicrox.apexguide.internal.home;

import com.mojang.blaze3d.systems.RenderSystem;
import com.omnicrox.apexguide.Guide;
import com.omnicrox.apexguide.compiler.ParsedGuidePage;
import com.omnicrox.apexguide.internal.GuidebookText;
import com.omnicrox.apexguide.internal.screen.ApexTheme;
import com.omnicrox.apexguide.navigation.NavigationNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The guide's home page: logo, "Recommended" on the left, "Bookmarks" and "History" stacked on the right.
 * <p>
 * Layout and colors are ported from GuideNH's HomePageLayout / HomePageController (LGPL-3.0, HFstudio).
 */
public class HomePageView {
    private static final int SECTION_PADDING = 8;
    private static final int TITLE_GAP = 8;
    private static final int ROW_HEIGHT = 32;
    private static final int ROW_GAP = 4;
    private static final int ICON_GAP = 6;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_GAP = 4;
    private static final int PANEL_GAP = 12;
    private static final int LOGO_PANEL_GAP = 6;
    private static final int SUMMARY_LENGTH = 160;
    private static final Pattern MARKUP = Pattern.compile("<[^>]*>|\\[([^\\]]*)]\\([^)]*\\)|[*_`#>]");

    public record Entry(ResourceLocation pageId, String title, String summary, ItemStack icon) {
    }

    private enum Kind {
        RECOMMENDED, BOOKMARKS, HISTORY
    }

    private record Rect(int x, int y, int w, int h) {
        boolean contains(double mx, double my) {
            return mx >= x && my >= y && mx < x + w && my < y + h;
        }
    }

    private final Guide guide;
    private final int[] scroll = new int[3];
    private final Rect[] rects = new Rect[3];
    private final List<List<Entry>> sections = new ArrayList<>(List.of(List.of(), List.of(), List.of()));
    private Rect logoRect = new Rect(0, 0, 0, 0);
    private int cachedVersion = Integer.MIN_VALUE;
    private Object cachedTree;

    public HomePageView(Guide guide) {
        this.guide = guide;
    }

    // ------------------------------------------------------------------ data

    private void refresh() {
        var tree = guide.getNavigationTree();
        int version = GuideUserData.version();
        if (version == cachedVersion && tree == cachedTree) {
            return;
        }
        cachedVersion = version;
        cachedTree = tree;
        sections.set(0, recommended());
        sections.set(1, toEntries(GuideUserData.bookmarks(guide.getId())));
        sections.set(2, toEntries(GuideUserData.history(guide.getId())));
    }

    /**
     * Pages whose frontmatter has {@code recommend: <n>} (highest first); without any, the top level of the
     * navigation tree.
     */
    private List<Entry> recommended() {
        record Scored(Entry entry, int score) {
        }
        var scored = new ArrayList<Scored>();
        for (var page : guide.getPages()) {
            var value = page.getFrontmatter().additionalProperties().get("recommend");
            if (value instanceof Number n) {
                var entry = toEntry(page.getId());
                if (entry != null) {
                    scored.add(new Scored(entry, n.intValue()));
                }
            }
        }
        if (!scored.isEmpty()) {
            scored.sort(Comparator.comparingInt(Scored::score).reversed()
                    .thenComparing(s -> s.entry().title(), String.CASE_INSENSITIVE_ORDER));
            return scored.stream().map(Scored::entry).toList();
        }
        var result = new ArrayList<Entry>();
        for (var node : guide.getNavigationTree().getRootNodes()) {
            var id = firstPage(node);
            if (id != null) {
                result.add(new Entry(id, node.title(), summaryOf(guide.getParsedPage(id)), node.icon()));
            }
        }
        return result;
    }

    @Nullable
    private static ResourceLocation firstPage(NavigationNode node) {
        if (node.hasPage() && node.pageId() != null) {
            return node.pageId();
        }
        for (var child : node.children()) {
            var id = firstPage(child);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    private List<Entry> toEntries(List<ResourceLocation> ids) {
        var result = new ArrayList<Entry>();
        for (var id : ids) {
            var entry = toEntry(id);
            if (entry != null) {
                result.add(entry);
            }
        }
        return result;
    }

    @Nullable
    private Entry toEntry(ResourceLocation id) {
        var page = guide.getParsedPage(id);
        if (page == null) {
            return null;
        }
        var node = guide.getNavigationTree().getNodeById(id);
        var title = node != null ? node.title() : titleFromSource(page);
        var icon = node != null ? node.icon() : ItemStack.EMPTY;
        return new Entry(id, title, summaryOf(page), icon);
    }

    private static String titleFromSource(ParsedGuidePage page) {
        for (var line : page.getSource().split("\n")) {
            if (line.startsWith("# ")) {
                return line.substring(2).trim();
            }
        }
        return page.getId().getPath();
    }

    /** {@code summary:} from the frontmatter, else the first paragraph of plain text in the page. */
    static String summaryOf(@Nullable ParsedGuidePage page) {
        if (page == null) {
            return "";
        }
        var explicit = page.getFrontmatter().additionalProperties().get("summary");
        if (explicit instanceof String s) {
            return s;
        }
        var src = page.getSource();
        if (src.startsWith("---")) {
            int end = src.indexOf("\n---", 3);
            if (end > 0) {
                src = src.substring(end + 4);
            }
        }
        var sb = new StringBuilder();
        for (var line : src.split("\n")) {
            var t = line.trim();
            if (t.isEmpty()) {
                if (sb.length() > 0) {
                    break;
                }
                continue;
            }
            if (t.startsWith("#") || t.startsWith("<") || t.startsWith("|") || t.startsWith("!") || t.startsWith("-")
                    || t.startsWith("```")) {
                if (sb.length() > 0) {
                    break;
                }
                continue;
            }
            sb.append(t).append(' ');
            if (sb.length() > SUMMARY_LENGTH) {
                break;
            }
        }
        var text = MARKUP.matcher(sb.toString()).replaceAll("$1").replaceAll("\\s+", " ").trim();
        return text.length() > SUMMARY_LENGTH ? text.substring(0, SUMMARY_LENGTH) + "…" : text;
    }

    // ------------------------------------------------------------------ layout

    private void layout(int contentX, int contentY, int contentW, int contentH, Font font) {
        int shellW = Math.max(220, Math.round(contentW * 0.8f));
        int shellH = Mth.clamp(Math.round(contentH * 0.85f), Math.min(120, contentH), Math.max(1, contentH));
        int shellX = contentX + Math.max(0, (contentW - shellW) / 2);
        int shellY = contentY + Math.max(0, (contentH - shellH) / 2);
        int panelW = Math.max(100, (shellW - PANEL_GAP) / 2);
        int columnsX = shellX + Math.max(0, (shellW - (panelW * 2 + PANEL_GAP)) / 2);
        int logoH = Mth.clamp(shellH / 5, 24, 56);
        logoRect = new Rect(columnsX, shellY, panelW, logoH);
        int panelsY = shellY + logoH + LOGO_PANEL_GAP;
        int available = Math.max(52 * 2 + PANEL_GAP, shellY + shellH - panelsY);
        int half = Math.max(52, (available - PANEL_GAP) / 2);
        rects[0] = new Rect(columnsX, panelsY, panelW, half * 2 + PANEL_GAP);
        rects[1] = new Rect(columnsX + panelW + PANEL_GAP, panelsY, panelW, half);
        rects[2] = new Rect(columnsX + panelW + PANEL_GAP, panelsY + half + PANEL_GAP, panelW, half);
    }

    // ------------------------------------------------------------------ render

    public void render(GuiGraphics g, int contentX, int contentY, int contentW, int contentH, int mouseX,
            int mouseY) {
        refresh();
        var font = Minecraft.getInstance().font;
        layout(contentX, contentY, contentW, contentH, font);
        drawLogo(g, font);
        var titles = new String[] { GuidebookText.HomeRecommended.text().getString(),
                GuidebookText.Bookmarks.text().getString(), GuidebookText.HomeHistory.text().getString() };
        for (int i = 0; i < 3; i++) {
            drawSection(g, font, i, titles[i], mouseX, mouseY);
        }
    }

    /** Text logo: "APEX" in gold over "GUIDE", scaled to fit, until the pack ships a logo image. */
    private void drawLogo(GuiGraphics g, Font font) {
        var title = "APEX GUIDE";
        float scale = Math.max(1f, Math.min(logoRect.h() / 12f, logoRect.w() / (float) font.width(title)));
        scale = (float) Math.floor(scale);
        int tw = Math.round(font.width(title) * scale);
        var pose = g.pose();
        pose.pushPose();
        pose.translate(logoRect.x() + (logoRect.w() - tw) / 2f, logoRect.y() + (logoRect.h() - 8 * scale) / 2f, 0);
        pose.scale(scale, scale, 1);
        g.drawString(font, "APEX", 0, 0, 0xFFF2B33D, true);
        g.drawString(font, " GUIDE", font.width("APEX"), 0, 0xFF9FD7E8, true);
        pose.popPose();
    }

    private void drawSection(GuiGraphics g, Font font, int idx, String title, int mouseX, int mouseY) {
        var r = rects[idx];
        g.fill(r.x(), r.y(), r.x() + r.w(), r.y() + r.h(), ApexTheme.HOME_PANEL);
        g.drawString(font, title, r.x() + SECTION_PADDING, r.y() + SECTION_PADDING, ApexTheme.HOME_TITLE, false);

        var entries = sections.get(idx);
        if (entries.isEmpty()) {
            var empty = GuidebookText.HomeEmpty.text().getString();
            if (font.width(empty) > r.w() - 8) {
                empty = font.plainSubstrByWidth(empty, Math.max(0, r.w() - 8));
            }
            g.drawString(font, empty, r.x() + (r.w() - font.width(empty)) / 2, r.y() + (r.h() - font.lineHeight) / 2,
                    ApexTheme.HOME_SUMMARY, false);
            return;
        }
        int cx = contentX(r);
        int cy = contentY(r);
        int visibleH = visibleH(r);
        int rowW = rowW(r);
        int contentH = entries.size() * ROW_HEIGHT + (entries.size() - 1) * ROW_GAP;
        int maxScroll = Math.max(0, contentH - visibleH);
        scroll[idx] = Mth.clamp(scroll[idx], 0, maxScroll);

        g.enableScissor(cx, cy, cx + rowW, cy + visibleH);
        for (int i = 0; i < entries.size(); i++) {
            int rowY = cy + i * (ROW_HEIGHT + ROW_GAP) - scroll[idx];
            if (rowY + ROW_HEIGHT < cy || rowY > cy + visibleH) {
                continue;
            }
            drawRow(g, font, entries.get(i), cx, rowY, rowW, mouseX, mouseY, cy, visibleH);
        }
        g.disableScissor();

        if (maxScroll > 0) {
            int x = r.x() + r.w() - SECTION_PADDING - SCROLLBAR_WIDTH;
            int thumbH = Math.max(18, visibleH * visibleH / Math.max(visibleH, contentH));
            int thumbY = cy + (int) ((long) scroll[idx] * (visibleH - thumbH) / maxScroll);
            g.fill(x, cy, x + SCROLLBAR_WIDTH, cy + visibleH, ApexTheme.SCROLL_TRACK);
            g.fill(x, thumbY, x + SCROLLBAR_WIDTH, thumbY + thumbH, ApexTheme.SCROLL_THUMB_HOVER);
        }
    }

    private void drawRow(GuiGraphics g, Font font, Entry entry, int x, int y, int w, int mouseX, int mouseY,
            int clipY, int clipH) {
        boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + ROW_HEIGHT
                && mouseY >= clipY && mouseY < clipY + clipH;
        g.fill(x, y, x + w, y + ROW_HEIGHT, hovered ? ApexTheme.HOME_ROW_HOVER : ApexTheme.HOME_ROW);
        int textX = x + 4;
        if (!entry.icon().isEmpty()) {
            int size = 21;
            var pose = g.pose();
            pose.pushPose();
            pose.translate(x + 4, y + (ROW_HEIGHT - size) / 2f, 0);
            pose.scale(size / 16f, size / 16f, 1);
            g.renderItem(entry.icon(), 0, 0);
            pose.popPose();
            RenderSystem.enableBlend();
            textX += size + ICON_GAP;
        }
        int textW = x + w - 4 - textX;
        drawScaled(g, font, entry.title(), textX, y + 4, textW, ApexTheme.HOME_TITLE, 1.12f);
        drawScaled(g, font, entry.summary(), textX, y + 18, textW, ApexTheme.HOME_SUMMARY, 0.85f);
    }

    private static void drawScaled(GuiGraphics g, Font font, String text, int x, int y, int maxW, int color,
            float scale) {
        if (text.isEmpty() || maxW <= 0) {
            return;
        }
        int unscaledW = Math.max(1, Math.round(maxW / scale));
        if (font.width(text) > unscaledW) {
            text = font.plainSubstrByWidth(text, Math.max(0, unscaledW - font.width("..."))) + "...";
        }
        var pose = g.pose();
        pose.pushPose();
        pose.translate(x, y, 50);
        pose.scale(scale, scale, 1);
        g.drawString(font, text, 0, 0, color, false);
        pose.popPose();
    }

    private static int contentX(Rect r) {
        return r.x() + SECTION_PADDING;
    }

    private static int contentY(Rect r) {
        return r.y() + SECTION_PADDING + 9 + TITLE_GAP;
    }

    private static int visibleH(Rect r) {
        return Math.max(0, r.y() + r.h() - SECTION_PADDING - contentY(r));
    }

    private static int rowW(Rect r) {
        return Math.max(30, r.x() + r.w() - SECTION_PADDING - SCROLLBAR_WIDTH - SCROLLBAR_GAP - contentX(r));
    }

    // ------------------------------------------------------------------ input

    /** Returns the page that was clicked, if any. */
    @Nullable
    public ResourceLocation mouseClicked(double mouseX, double mouseY) {
        for (int idx = 0; idx < 3; idx++) {
            var r = rects[idx];
            if (r == null || !r.contains(mouseX, mouseY)) {
                continue;
            }
            int cx = contentX(r);
            int cy = contentY(r);
            if (mouseX < cx || mouseX >= cx + rowW(r) || mouseY < cy || mouseY >= cy + visibleH(r)) {
                return null;
            }
            int rel = (int) mouseY - cy + scroll[idx];
            int i = rel / (ROW_HEIGHT + ROW_GAP);
            if (rel % (ROW_HEIGHT + ROW_GAP) < ROW_HEIGHT && i >= 0 && i < sections.get(idx).size()) {
                return sections.get(idx).get(i).pageId();
            }
        }
        return null;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (int idx = 0; idx < 3; idx++) {
            if (rects[idx] != null && rects[idx].contains(mouseX, mouseY)) {
                scroll[idx] -= (int) Math.round(delta * (ROW_HEIGHT - 4));
                if (scroll[idx] < 0) {
                    scroll[idx] = 0;
                }
                return true;
            }
        }
        return false;
    }
}
