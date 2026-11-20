package com.omnicrox.gtaa.common.machine.multiblock.suit;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.omnicrox.gtaa.common.machine.multiblock.SuitFabricationStationMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Suit Picker tab of the Suit Fabrication Station: browse and search the suits, see the suit turning on the holo
 * pad with its hero kit and parts, pick it and press Fabricate. GregTech frame, arcane-violet inside.
 */
public class SuitPickerTab implements IFancyUIProvider {

    // palette: GT steel frame, violet arcane glass, gold and cyan accents
    static final int BG = 0xF0120A1E, PANEL = 0xC01E1232, EDGE = 0xFF6B47B8, EDGE_DIM = 0xFF3A2766;
    static final int GOLD = 0xFFFFC94A, CYAN = 0xFF5FF3FF, TEXT = 0xFFE6DDF7, MUTED = 0xFF9A8CB8;
    static final int[] TIER_COLOR = { 0xFF9A9A9A, 0xFFC0C0C0, 0xFF55E6FF, 0xFFD27BFF, 0xFFFFC94A };
    static final String[] ROMAN = { "", "I", "II", "III", "IV" };

    static final int W = 300, H = 200, LIST_X = 6, LIST_Y = 44, LIST_W = 110, LIST_H = 150, ROW_H = 13;

    private final SuitFabricationStationMachine machine;
    private final List<EntryRow> rows = new ArrayList<>();
    private DraggableScrollableWidgetGroup list;
    private String search = "";
    private int tierFilter = 0;

    public SuitPickerTab(SuitFabricationStationMachine machine) {
        this.machine = machine;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(Items.NETHERITE_CHESTPLATE);
    }

    @Override
    public Component getTitle() {
        return Component.literal("Suit Picker");
    }

    @Override
    public boolean hasPlayerInventory() {
        return false;
    }

    SuitCatalog.Entry selected() {
        return SuitCatalog.get(machine.getSelectedSuit());
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget widget) {
        WidgetGroup root = new WidgetGroup(0, 0, W, H);
        root.setBackground(new GuiTextureGroup(GuiTextures.BACKGROUND_INVERSE, new ColorRectTexture(BG)));

        root.addWidget(new Header(4, 3, W - 8, 12));

        TextFieldWidget field = new TextFieldWidget(LIST_X, 17, LIST_W, 11, () -> search, s -> {
            search = s == null ? "" : s;
            relayout();
        });
        field.setBackground(new GuiTextureGroup(new ColorRectTexture(0xFF0B0614), new ColorBorderTexture(1, EDGE_DIM)));
        field.setHoverTooltips("Search suits, kits or powers");
        root.addWidget(field);

        for (int t = 0; t <= 4; t++) {
            final int tier = t;
            ButtonWidget b = new ButtonWidget(LIST_X + t * 22, 31, 21, 11, cd -> {
                tierFilter = tier;
                relayout();
            }) {
                @Override
                public void drawInBackground(GuiGraphics g, int mx, int my, float pt) {
                    int x = getPosition().x, y = getPosition().y;
                    boolean on = tierFilter == tier;
                    g.fill(x, y, x + 21, y + 11, on ? 0xFF3B2470 : (isMouseOverElement(mx, my) ? 0xFF2A1A4A : 0xFF1A1030));
                    g.renderOutline(x, y, 21, 11, on ? TIER_COLOR[tier] : EDGE_DIM);
                    String s = tier == 0 ? "All" : ROMAN[tier];
                    Font f = Minecraft.getInstance().font;
                    g.drawString(f, s, x + 11 - f.width(s) / 2, y + 2, on ? TIER_COLOR[tier] : MUTED, false);
                }
            };
            b.setHoverTooltips(tier == 0 ? "All tiers" : "Tier " + tier + ": " + SuitFabricationStationMachine.TIER_NAMES[tier]);
            root.addWidget(b);
        }

        list = new DraggableScrollableWidgetGroup(LIST_X, LIST_Y, LIST_W, LIST_H);
        list.setBackground(new GuiTextureGroup(new ColorRectTexture(0xFF0B0614), new ColorBorderTexture(1, EDGE_DIM)));
        list.setYScrollBarWidth(3);
        list.setYBarStyle(null, new ColorRectTexture(EDGE));
        rows.clear();
        for (SuitCatalog.Entry e : SuitCatalog.all()) {
            EntryRow row = new EntryRow(e);
            rows.add(row);
            list.addWidget(row);
        }
        relayout();
        root.addWidget(list);

        root.addWidget(new Preview(120, 17, 80, 120));
        root.addWidget(new Info(120, 139, 176, 26));
        root.addWidget(new Powers(204, 17, 92, 66));
        root.addWidget(new Parts(204, 85, 92, 52));

        ButtonWidget fab = new ButtonWidget(204, 139 + 28, 92, 16, null);
        fab.setButtonTexture(GuiTextures.BUTTON, new TextTexture("§l✦ FABRICATE ✦", GOLD).setDropShadow(true));
        fab.setHoverTexture(new GuiTextureGroup(GuiTextures.BUTTON, new ColorRectTexture(0x406B47B8)),
                new TextTexture("§l✦ FABRICATE ✦", CYAN).setDropShadow(true));
        fab.setHoverTooltips("Set the ME Input Bus to this suit's parts",
                "and autocraft any part that is short");
        fab.setOnPressCallback(cd -> {
            if (!cd.isRemote && fab.getGui() != null && fab.getGui().entityPlayer instanceof ServerPlayer sp)
                machine.fabricate(sp);
        });
        root.addWidget(fab);

        ButtonWidget clear = new ButtonWidget(120, 139 + 28, 80, 16, cd -> {
            if (!cd.isRemote) machine.select("");
        });
        clear.setButtonTexture(GuiTextures.BUTTON, new TextTexture("Any suit", TEXT));
        clear.setHoverTooltips("Unpick: the station makes any suit", "whose parts are in its buses");
        root.addWidget(clear);

        root.addWidget(new Status(120, 186, 176, 10));
        return root;
    }

    private void relayout() {
        if (list == null) return;
        String q = search.toLowerCase(Locale.ROOT).trim();
        int y = 1;
        for (EntryRow r : rows) {
            SuitCatalog.Entry e = r.entry;
            boolean show = (tierFilter == 0 || e.tier() == tierFilter) && (q.isEmpty() || matches(e, q));
            r.setVisible(show);
            r.setActive(show);
            if (show) {
                r.setSelfPosition(1, y);
                y += ROW_H;
            } else r.setSelfPosition(1, -1000);
        }
        list.setScrollYOffset(0);
        list.computeMax();
    }

    private static boolean matches(SuitCatalog.Entry e, String q) {
        if (e.name().toLowerCase(Locale.ROOT).contains(q) || e.kit().toLowerCase(Locale.ROOT).contains(q)) return true;
        for (String a : e.abilities()) if (a.toLowerCase(Locale.ROOT).contains(q)) return true;
        return false;
    }

    // ---- widgets -----------------------------------------------------------------------------------------

    static void text(GuiGraphics g, String s, int x, int y, int color, float scale) {
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1);
        g.drawString(Minecraft.getInstance().font, s, 0, 0, color, false);
        g.pose().popPose();
    }

    static String fit(String s, int width, float scale) {
        Font f = Minecraft.getInstance().font;
        int max = (int) (width / scale);
        if (f.width(s) <= max) return s;
        return f.plainSubstrByWidth(s, max - f.width("..")) + "..";
    }

    static void panel(GuiGraphics g, int x, int y, int w, int h, String title) {
        g.fill(x, y, x + w, y + h, PANEL);
        g.renderOutline(x, y, w, h, EDGE_DIM);
        g.fill(x + 1, y + 1, x + w - 1, y + 2, 0x606B47B8);
        if (title != null) text(g, title, x + 3, y + 3, GOLD, 0.75f);
    }

    private class Header extends Widget {
        Header(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        @Override
        public void drawInBackground(GuiGraphics g, int mx, int my, float pt) {
            int x = getPosition().x, y = getPosition().y, w = getSize().width;
            long t = Minecraft.getInstance().level == null ? 0 : Minecraft.getInstance().level.getGameTime();
            int pulse = (int) (40 + 30 * Math.sin((t + pt) / 10.0));
            g.fill(x, y + 10, x + w, y + 11, (pulse << 24) | 0x5FF3FF);
            g.drawString(Minecraft.getInstance().font, "✦ SUIT FABRICATION", x + 2, y + 1, GOLD, true);
            int tier = machine.getStationTier();
            String s = tier > 0 ? SuitFabricationStationMachine.TIER_NAMES[tier] + " · " + ROMAN[tier] : "not formed";
            Font f = Minecraft.getInstance().font;
            g.drawString(f, s, x + w - f.width(s) - 2, y + 1, tier > 0 ? TIER_COLOR[tier] : 0xFFFF5555, false);
        }
    }

    private class EntryRow extends ButtonWidget {
        final SuitCatalog.Entry entry;

        EntryRow(SuitCatalog.Entry e) {
            super(1, 0, LIST_W - 6, ROW_H - 1, (ClickData cd) -> {});
            this.entry = e;
            setOnPressCallback(cd -> {
                if (!cd.isRemote) machine.select(entry.suit());
            });
            setHoverTooltips(e.name(), "Tier " + e.tier() + " · " + e.kit());
        }

        @Override
        public void drawInBackground(GuiGraphics g, int mx, int my, float pt) {
            int x = getPosition().x, y = getPosition().y, w = getSize().width, h = getSize().height;
            boolean sel = entry.suit().equals(machine.getSelectedSuit());
            boolean locked = entry.tier() > machine.getStationTier();
            boolean hover = isMouseOverElement(mx, my);
            if (sel) {
                g.fill(x, y, x + w, y + h, 0xFF3B2470);
                g.renderOutline(x, y, w, h, CYAN);
            } else if (hover) g.fill(x, y, x + w, y + h, 0xFF24163E);
            g.fill(x + 2, y + 3, x + 4, y + h - 3, TIER_COLOR[entry.tier()]);
            String name = fit(entry.name(), w - 10, 0.8f);
            text(g, name, x + 7, y + 3, locked ? 0xFF6E6380 : (sel ? CYAN : TEXT), 0.8f);
        }
    }

    private class Preview extends Widget {
        private float dragYaw;
        private double lastX;
        private boolean dragging;

        Preview(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        @Override
        public boolean mouseClicked(double mx, double my, int button) {
            if (isMouseOverElement(mx, my)) {
                dragging = true;
                lastX = mx;
                return true;
            }
            return super.mouseClicked(mx, my, button);
        }

        @Override
        public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
            if (dragging) {
                dragYaw += (float) (mx - lastX) * 0.04f;
                lastX = mx;
                return true;
            }
            return super.mouseDragged(mx, my, button, dx, dy);
        }

        @Override
        public boolean mouseReleased(double mx, double my, int button) {
            dragging = false;
            return super.mouseReleased(mx, my, button);
        }

        @Override
        public void drawInBackground(GuiGraphics g, int mx, int my, float pt) {
            int x = getPosition().x, y = getPosition().y, w = getSize().width, h = getSize().height;
            g.fillGradient(x, y, x + w, y + h, 0xFF1A0F33, 0xFF06030C);
            g.renderOutline(x, y, w, h, EDGE);
            long t = Minecraft.getInstance().level == null ? 0 : Minecraft.getInstance().level.getGameTime();
            float time = t + pt;
            // holo pad + scan line
            int cx = x + w / 2, pad = y + h - 14;
            for (int i = 0; i < 3; i++) {
                int rw = 30 - i * 7;
                g.fill(cx - rw, pad + i, cx + rw, pad + i + 1, (0x90 - i * 0x20) << 24 | 0x5FF3FF);
            }
            int scan = (int) ((time * 1.5) % (h - 20));
            g.fill(x + 3, pad - scan, x + w - 3, pad - scan + 1, 0x305FF3FF);
            SuitCatalog.Entry e = selected();
            if (e == null) {
                Font f = Minecraft.getInstance().font;
                String s = "Pick a suit";
                text(g, s, cx - (int) (f.width(s) * 0.4f), y + h / 2 - 4, MUTED, 0.8f);
                return;
            }
            float yaw = dragging ? dragYaw : dragYaw + time * 0.03f;
            if (!dragging) dragYaw = yaw - time * 0.03f;
            com.omnicrox.gtaa.client.SuitPreviewClient.renderInGui(g, cx, pad, 44, yaw, e.armor());
            text(g, "drag to turn", x + 3, y + h - 7, 0x806B47B8, 0.6f);
        }
    }

    private class Info extends Widget {
        Info(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        @Override
        public void drawInBackground(GuiGraphics g, int mx, int my, float pt) {
            int x = getPosition().x, y = getPosition().y;
            SuitCatalog.Entry e = selected();
            if (e == null) {
                text(g, "Station makes: any suit", x, y + 2, MUTED, 0.85f);
                return;
            }
            g.drawString(Minecraft.getInstance().font, fit(e.name(), 176, 1f), x, y, CYAN, true);
            boolean locked = e.tier() > machine.getStationTier();
            text(g, "Tier " + ROMAN[e.tier()] + " · " + SuitFabricationStationMachine.TIER_NAMES[e.tier()] +
                    (locked ? "  §c(upgrade casings)" : ""), x, y + 11, TIER_COLOR[e.tier()], 0.8f);
            text(g, "Kit: " + e.kit(), x, y + 19, GOLD, 0.8f);
        }
    }

    private class Powers extends Widget {
        Powers(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        @Override
        public void drawInBackground(GuiGraphics g, int mx, int my, float pt) {
            int x = getPosition().x, y = getPosition().y, w = getSize().width, h = getSize().height;
            panel(g, x, y, w, h, "HERO KIT");
            SuitCatalog.Entry e = selected();
            if (e == null) return;
            int line = 0;
            for (String a : e.abilities()) {
                if (line >= 7) break;
                text(g, "• " + fit(a, w - 8, 0.7f), x + 3, y + 11 + line * 7, TEXT, 0.7f);
                line++;
            }
            if (e.abilities().size() > 7) text(g, "+" + (e.abilities().size() - 7) + " more", x + w - 30, y + 3, MUTED, 0.6f);
        }

        @Override
        public void drawInForeground(GuiGraphics g, int mx, int my, float pt) {
            SuitCatalog.Entry e = selected();
            if (e == null || !isMouseOverElement(mx, my) || e.abilities().size() <= 7) return;
            List<Component> lines = new ArrayList<>();
            for (String a : e.abilities()) lines.add(Component.literal(a));
            g.renderComponentTooltip(Minecraft.getInstance().font, lines, mx, my);
        }
    }

    private class Parts extends Widget {
        Parts(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        private List<ItemStack> stacks() {
            SuitCatalog.Entry e = selected();
            return e == null ? List.of() : com.omnicrox.gtaa.client.SuitPreviewClient.parts(e);
        }

        @Override
        public void drawInBackground(GuiGraphics g, int mx, int my, float pt) {
            int x = getPosition().x, y = getPosition().y, w = getSize().width, h = getSize().height;
            panel(g, x, y, w, h, "PARTS");
            List<ItemStack> s = stacks();
            Font f = Minecraft.getInstance().font;
            for (int i = 0; i < Math.min(s.size(), 10); i++) {
                int ix = x + 3 + (i % 5) * 18, iy = y + 11 + (i / 5) * 19;
                g.fill(ix - 1, iy - 1, ix + 17, iy + 17, 0x60000000);
                g.renderItem(s.get(i), ix, iy);
                g.renderItemDecorations(f, s.get(i), ix, iy);
            }
        }

        @Override
        public void drawInForeground(GuiGraphics g, int mx, int my, float pt) {
            int x = getPosition().x, y = getPosition().y;
            List<ItemStack> s = stacks();
            for (int i = 0; i < Math.min(s.size(), 10); i++) {
                int ix = x + 3 + (i % 5) * 18, iy = y + 11 + (i / 5) * 19;
                if (mx >= ix && mx < ix + 16 && my >= iy && my < iy + 16) {
                    g.renderTooltip(Minecraft.getInstance().font, s.get(i), mx, my);
                    return;
                }
            }
        }
    }

    private class Status extends Widget {
        Status(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        @Override
        public void drawInBackground(GuiGraphics g, int mx, int my, float pt) {
            String s = machine.getFabStatus();
            if (s == null || s.isEmpty()) s = "§8Pick a suit, then Fabricate";
            text(g, fit(s, getSize().width, 0.75f), getPosition().x, getPosition().y, TEXT, 0.75f);
        }
    }
}
