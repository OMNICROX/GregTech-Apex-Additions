package com.omnicrox.apexguide.internal.screen;

import com.omnicrox.apexguide.Guide;
import com.omnicrox.apexguide.GuidePage;
import com.omnicrox.apexguide.PageAnchor;
import com.omnicrox.apexguide.PageCollection;
import com.omnicrox.apexguide.compiler.AnchorIndexer;
import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.ParsedGuidePage;
import com.omnicrox.apexguide.document.LytRect;
import com.omnicrox.apexguide.document.block.LytDocument;
import com.omnicrox.apexguide.document.block.LytHeading;
import com.omnicrox.apexguide.document.flow.LytFlowAnchor;
import com.omnicrox.apexguide.internal.GuideME;
import com.omnicrox.apexguide.internal.GuideMEClient;
import com.omnicrox.apexguide.internal.GuidebookText;
import com.omnicrox.apexguide.internal.home.GuideUserData;
import com.omnicrox.apexguide.internal.home.HomePageView;
import com.omnicrox.apexguide.render.GuidePageTexture;
import com.omnicrox.apexguide.render.RenderContext;
import com.omnicrox.apexguide.ui.GuideUiHost;
import com.omnicrox.apexguide.ui.UiPoint;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The guide window. Its chrome follows GuideNH's GuideScreen (LGPL-3.0, HFstudio): a dark panel at 90% of the
 * screen (or full screen), a toolbar with the page title and search / home / back / forward / width / close
 * buttons, a collapsible navigation tree on the left, a bottom bar naming where the content came from, and a home
 * page with recommended pages, bookmarks and history.
 */
public class GuideScreen extends DocumentScreen implements GuideUiHost {
    private static final Logger LOG = LoggerFactory.getLogger(GuideScreen.class);

    /** Built-in page id of the home page. */
    public static final ResourceLocation HOME_PAGE_ID = GuideME.makeId("home");

    private final Guide guide;
    @Nullable
    private GuidePage currentPage;
    private boolean home;
    private String pageTitle = "";

    private final GuideNavBar navbar;
    private final HomePageView homeView;
    private final List<GuideIconButton> rightButtons = new ArrayList<>();
    private final GuideIconButton searchButton;
    private final GuideIconButton homeButton;
    private final GuideIconButton backButton;
    private final GuideIconButton forwardButton;
    private final GuideIconButton fullWidthButton;
    private final GuideIconButton bookmarkButton;
    private final GuideIconButton closeButton;

    private LytRect panel = LytRect.empty();
    private LytRect content = LytRect.empty();

    @Nullable
    private Screen returnToOnClose;
    @Nullable
    private String pendingScrollToAnchor;

    private GuideScreen(Guide guide, PageAnchor anchor) {
        super(Component.literal("Apex Guide"));
        this.guide = guide;
        this.navbar = new GuideNavBar(this, guide);
        this.homeView = new HomePageView(guide);

        searchButton = new GuideIconButton(0, 0, GuideIconButton.Role.SEARCH,
                () -> GuideNavigation.navigateTo(guide, PageAnchor.page(GuideSearchScreen.PAGE_ID)));
        homeButton = new GuideIconButton(0, 0, GuideIconButton.Role.HOMEPAGE,
                () -> navigateTo(HOME_PAGE_ID));
        backButton = new GuideIconButton(0, 0, GuideIconButton.Role.BACK,
                () -> GuideNavigation.navigateBack(guide));
        forwardButton = new GuideIconButton(0, 0, GuideIconButton.Role.FORWARD,
                () -> GuideNavigation.navigateForward(guide));
        fullWidthButton = new GuideIconButton(0, 0, GuideIconButton.Role.OPEN_FULL_WIDTH_VIEW, () -> {
            GuideMEClient.instance().setFullWidthLayout(!GuideMEClient.instance().isFullWidthLayout());
            rebuildLayout();
        });
        bookmarkButton = new GuideIconButton(0, 0, GuideIconButton.Role.BOOKMARK, () -> {
            if (!home && currentPage != null) {
                GuideUserData.toggleBookmark(guide.getId(), currentPage.id());
            }
        });
        closeButton = new GuideIconButton(0, 0, GuideIconButton.Role.CLOSE, this::onClose);

        loadPageAndScrollTo(anchor);
    }

    /** Opens and resets history. Uses per-guide history by default. */
    public static GuideScreen openNew(Guide guide, PageAnchor anchor) {
        return openNew(guide, anchor, GlobalInMemoryHistory.get(guide));
    }

    /** Opens and resets history. */
    public static GuideScreen openNew(Guide guide, PageAnchor anchor, GuideScreenHistory history) {
        history.push(anchor);
        return new GuideScreen(guide, anchor);
    }

    // ------------------------------------------------------------------ layout

    @Override
    protected void init() {
        super.init();
        rightButtons.clear();
        rightButtons.add(bookmarkButton);
        rightButtons.add(searchButton);
        rightButtons.add(homeButton);
        rightButtons.add(backButton);
        rightButtons.add(forwardButton);
        rightButtons.add(fullWidthButton);
        rightButtons.add(closeButton);
        addRenderableWidget(navbar);
        rightButtons.forEach(this::addRenderableWidget);
        rebuildLayout();
    }

    private void rebuildLayout() {
        panel = ApexTheme.panelRect(width, height, GuideMEClient.instance().isFullWidthLayout());
        int panelX = panel.x();
        int panelY = panel.y();
        int panelW = panel.width();
        int panelH = panel.height();
        screenRect = panel;

        // Toolbar buttons, right aligned
        int bx = panel.right() - 4;
        int by = panelY + (ApexTheme.TOOLBAR_H - GuideIconButton.HEIGHT) / 2;
        for (int i = rightButtons.size() - 1; i >= 0; i--) {
            var b = rightButtons.get(i);
            bx -= GuideIconButton.WIDTH;
            b.setX(bx);
            b.setY(by);
            bx -= ApexTheme.TOOLBAR_GAP;
        }

        int bottomBar = hasBottomBar() ? ApexTheme.TOOLBAR_H : 0;
        int navOpen = Math.max(GuideNavBar.MIN_OPEN_WIDTH, width * GuideNavBar.OPEN_WIDTH_SCREEN_PERCENT / 100);
        navOpen = Math.min(navOpen, Math.max(GuideNavBar.WIDTH_CLOSED, panelW - ApexTheme.PANEL_PADDING * 2 - 40));
        int navY = panelY + ApexTheme.TOOLBAR_H + 1;
        navbar.setBounds(panelX + 1, navY, Math.max(20, panelH - ApexTheme.TOOLBAR_H - 2 - bottomBar), navOpen);

        int navReserved = navbar.hasRows() ? navbar.reservedWidth() : 0;
        int contentX = panelX + 1 + navReserved + ApexTheme.PANEL_PADDING;
        int contentY = panelY + ApexTheme.TOOLBAR_H + 4;
        int contentW = Math.max(20, panel.right() - ApexTheme.PANEL_PADDING - contentX);
        int contentH = Math.max(20, panel.bottom() - bottomBar - 2 - contentY);
        content = new LytRect(contentX, contentY, contentW, contentH);
        setDocumentRect(content);
        updateDocumentLayout();
    }

    /** Called by the navigation bar when it gets pinned or unpinned. */
    void onNavigationLayoutChanged() {
        rebuildLayout();
    }

    private boolean hasBottomBar() {
        return !home && currentPage != null;
    }

    @Override
    public void tick() {
        super.tick();
        var history = GlobalInMemoryHistory.get(guide);
        backButton.active = history.peekBack().isPresent();
        forwardButton.active = history.peekForward().isPresent();
        homeButton.setHighlighted(home);
        bookmarkButton.visible = !home && currentPage != null;
        if (bookmarkButton.visible) {
            boolean marked = GuideUserData.isBookmarked(guide.getId(), currentPage.id());
            bookmarkButton.setRole(marked ? GuideIconButton.Role.BOOKMARKED : GuideIconButton.Role.BOOKMARK);
            bookmarkButton.setHighlighted(marked);
        }
        fullWidthButton.setRole(GuideMEClient.instance().isFullWidthLayout()
                ? GuideIconButton.Role.CLOSE_FULL_WIDTH_VIEW
                : GuideIconButton.Role.OPEN_FULL_WIDTH_VIEW);
        processPendingScrollTo();
    }

    // ------------------------------------------------------------------ rendering

    @Override
    public void scaledRender(GuiGraphics g, RenderContext context, int mouseX, int mouseY, float partialTick) {
        var font = Minecraft.getInstance().font;
        // Dim the world behind the window
        ApexTheme.drawPanel(g, width, height, panel);

        // Mouse over the open overlay navigation should not hover the document
        int docMouseX = navbar.isOpen() && navbar.isMouseOver(mouseX, mouseY) ? Integer.MIN_VALUE : mouseX;
        int docMouseY = docMouseX == Integer.MIN_VALUE ? Integer.MIN_VALUE : mouseY;

        if (home) {
            homeView.render(g, content.x(), content.y(), content.width(), content.height(), docMouseX, docMouseY);
        } else {
            renderDocument(context);
        }

        var pose = g.pose();
        pose.pushPose();
        pose.translate(0, 0, 200);

        // Toolbar: title on the left, separator below
        g.fill(panel.x() + 1, panel.y() + 1, panel.right() - 1, panel.y() + ApexTheme.TOOLBAR_H, ApexTheme.PANEL_BG);
        g.fill(panel.x() + 1, panel.y() + ApexTheme.TOOLBAR_H, panel.right() - 1, panel.y() + ApexTheme.TOOLBAR_H + 1,
                ApexTheme.SEPARATOR);
        drawTitle(g, font);

        if (hasBottomBar()) {
            drawBottomBar(g, font);
        }

        super.scaledRender(g, context, mouseX, mouseY, partialTick);
        pose.popPose();

        if (!home) {
            renderDocumentTooltip(g, docMouseX, docMouseY, partialTick);
        }
    }

    private void drawTitle(GuiGraphics g, Font font) {
        int left = panel.x() + ApexTheme.PANEL_PADDING;
        int firstButton = rightButtons.stream().filter(b -> b.visible).mapToInt(GuideIconButton::getX).min()
                .orElse(panel.right());
        int maxW = firstButton - 6 - left;
        float scale = 1.25f;
        var title = home ? GuidebookText.HomePage.text().getString() : pageTitle;
        int unscaled = Math.round(maxW / scale);
        if (font.width(title) > unscaled) {
            title = font.plainSubstrByWidth(title, Math.max(0, unscaled - font.width("..."))) + "...";
        }
        var pose = g.pose();
        pose.pushPose();
        pose.translate(left, panel.y() + (ApexTheme.TOOLBAR_H - 8 * scale) / 2f + 0.5f, 0);
        pose.scale(scale, scale, 1);
        g.drawString(font, Component.literal(title).withStyle(s -> s.withBold(true)), 0, 0, 0xFFFFFFFF, false);
        pose.popPose();
    }

    private void drawBottomBar(GuiGraphics g, Font font) {
        int barY = panel.bottom() - ApexTheme.TOOLBAR_H;
        g.fill(panel.x() + 1, barY, panel.right() - 1, panel.bottom() - 1, ApexTheme.PANEL_BG);
        g.fill(panel.x() + 1, barY, panel.right() - 1, barY + 1, ApexTheme.SEPARATOR);
        var source = getSourceName();
        if (source == null) {
            return;
        }
        var text = Component.literal(GuidebookText.ContentFrom.text().getString() + " ")
                .append(Component.literal(source).withStyle(s -> s.withItalic(true)));
        int textX = panel.right() - 8 - font.width(text);
        g.drawString(font, text, textX, barY + (ApexTheme.TOOLBAR_H - font.lineHeight) / 2 + 1,
                ApexTheme.TEXT_MUTED, false);
    }

    private static void drawBorder(GuiGraphics g, LytRect r, int color) {
        g.fill(r.x(), r.y(), r.right(), r.y() + 1, color);
        g.fill(r.x(), r.bottom() - 1, r.right(), r.bottom(), color);
        g.fill(r.x(), r.y(), r.x() + 1, r.bottom(), color);
        g.fill(r.right() - 1, r.y(), r.right(), r.bottom(), color);
    }

    /** Resource pack or mod that supplied the page; falls back to the guide's own mod. */
    @Nullable
    private String getSourceName() {
        if (currentPage == null) {
            return null;
        }
        var pack = Minecraft.getInstance().getResourcePackRepository().getPack(currentPage.sourcePack());
        if (pack != null && !pack.getId().equals("mod_resources")) {
            return pack.getDescription().getString();
        }
        return ModList.get().getModContainerById(currentPage.id().getNamespace())
                .map(ModContainer::getModInfo)
                .map(IModInfo::getDisplayName)
                .orElse(currentPage.id().getNamespace());
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        // Drawn in scaledRender
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean scaledMouseClicked(double mouseX, double mouseY, int button) {
        // Overlay navigation gets the click first
        if (navbar.isOpen() && navbar.mouseClicked(mouseX, mouseY, button)) {
            setFocused(navbar);
            return true;
        }
        if (home && button == 0 && content.contains((int) mouseX, (int) mouseY)) {
            var target = homeView.mouseClicked(mouseX, mouseY);
            if (target != null) {
                navigateTo(target);
                return true;
            }
        }
        return super.scaledMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean scaledMouseScrolled(double mouseX, double mouseY, double delta) {
        if (navbar.isOpen() && navbar.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        if (home) {
            return homeView.mouseScrolled(mouseX, mouseY, delta);
        }
        return super.scaledMouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (navbar.isFilterFocused()) {
            return navbar.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (navbar.isFilterFocused()) {
            return navbar.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected boolean documentClicked(UiPoint documentPoint, int button) {
        if (button == 3) {
            GuideNavigation.navigateBack(guide);
            return true;
        } else if (button == 4) {
            GuideNavigation.navigateForward(guide);
            return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ navigation

    @Override
    public void navigateTo(ResourceLocation pageId) {
        navigateTo(PageAnchor.page(pageId));
    }

    @Override
    public void navigateTo(PageAnchor anchor) {
        GuideNavigation.navigateTo(guide, anchor);
    }

    void loadPageAndScrollTo(PageAnchor anchor) {
        loadPage(anchor.pageId());
        setDocumentScrollY(0);
        updateDocumentLayout();
        pendingScrollToAnchor = anchor.anchor();
    }

    @Override
    public void reloadPage() {
        loadPage(home || currentPage == null ? HOME_PAGE_ID : currentPage.id());
        updateDocumentLayout();
    }

    private void loadPage(ResourceLocation pageId) {
        GuidePageTexture.releaseUsedTextures();
        if (HOME_PAGE_ID.equals(pageId)) {
            home = true;
            currentPage = null;
            pageTitle = "";
            if (minecraft != null) {
                rebuildLayout();
            }
            return;
        }
        home = false;
        var page = guide.getParsedPage(pageId);
        if (page == null) {
            page = buildNotFoundPage(pageId);
        } else {
            GuideUserData.recordVisit(guide.getId(), pageId);
        }
        currentPage = PageCompiler.compile(guide, guide.getExtensions(), page);
        pageTitle = extractPageTitle(currentPage);
        navbar.expandParentsOf(pageId);
        if (minecraft != null) {
            rebuildLayout();
        }
    }

    /** Pulls the leading level-1 heading out of the page and uses it as the toolbar title. */
    private String extractPageTitle(GuidePage page) {
        for (var block : page.document().getBlocks()) {
            if (block instanceof LytHeading heading) {
                if (heading.getDepth() == 1) {
                    page.document().removeChild(heading);
                    return flowText(heading.getContent());
                }
                break;
            }
        }
        var node = guide.getNavigationTree().getNodeById(page.id());
        return node != null ? node.title() : page.id().getPath();
    }

    private static String flowText(Iterable<com.omnicrox.apexguide.document.flow.LytFlowContent> content) {
        var sb = new StringBuilder();
        for (var c : content) {
            if (c instanceof com.omnicrox.apexguide.document.flow.LytFlowText text) {
                sb.append(text.getText());
            } else if (c instanceof com.omnicrox.apexguide.document.flow.LytFlowSpan parent) {
                sb.append(flowText(parent.getChildren()));
            }
        }
        return sb.toString().trim();
    }

    private ParsedGuidePage buildNotFoundPage(ResourceLocation pageId) {
        String pageSource = "# Page not Found\n\nPage \"" + pageId + "\" could not be found.";
        return PageCompiler.parse(pageId.getNamespace(), "en_us", pageId, pageSource);
    }

    private void processPendingScrollTo() {
        if (pendingScrollToAnchor == null || currentPage == null) {
            return;
        }
        var anchor = pendingScrollToAnchor;
        pendingScrollToAnchor = null;
        var targetAnchor = new AnchorIndexer(currentPage.document()).get(anchor);
        if (targetAnchor == null) {
            LOG.warn("Failed to find anchor {} in page {}", anchor, currentPage);
            return;
        }
        if (targetAnchor.flowContent() instanceof LytFlowAnchor flowAnchor && flowAnchor.getLayoutY().isPresent()) {
            setDocumentScrollY(flowAnchor.getLayoutY().getAsInt());
        } else {
            setDocumentScrollY(targetAnchor.blockNode().getBounds().y());
        }
    }

    public void scrollToAnchor(@Nullable String anchor) {
        pendingScrollToAnchor = anchor;
        if (anchor == null) {
            setDocumentScrollY(0);
        }
    }

    @Override
    public void removed() {
        super.removed();
        GuidePageTexture.releaseUsedTextures();
    }

    public void setReturnToOnClose(@Nullable Screen screen) {
        this.returnToOnClose = screen;
    }

    public @Nullable Screen getReturnToOnClose() {
        return returnToOnClose;
    }

    @Override
    protected @Nullable LytDocument getDocument() {
        return home || currentPage == null ? null : currentPage.document();
    }

    @Override
    public PageCollection getGuide() {
        return guide;
    }

    public ResourceLocation getCurrentPageId() {
        return home || currentPage == null ? HOME_PAGE_ID : currentPage.id();
    }

    @Nullable
    ResourceLocation getCurrentPageIdOrNull() {
        return home || currentPage == null ? null : currentPage.id();
    }

    @Override
    public void onClose() {
        if (minecraft != null && minecraft.screen == this && this.returnToOnClose != null) {
            minecraft.setScreen(this.returnToOnClose);
            this.returnToOnClose = null;
            return;
        }
        super.onClose();
    }
}
