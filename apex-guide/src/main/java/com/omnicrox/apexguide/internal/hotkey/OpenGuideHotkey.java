package com.omnicrox.apexguide.internal.hotkey;

import com.google.common.base.Strings;
import com.mojang.blaze3d.platform.InputConstants;
import com.omnicrox.apexguide.Guide;
import com.omnicrox.apexguide.PageAnchor;
import com.omnicrox.apexguide.indices.ItemIndex;
import com.omnicrox.apexguide.internal.GuideMEClient;
import com.omnicrox.apexguide.internal.GuideRegistry;
import com.omnicrox.apexguide.internal.GuidebookText;
import com.omnicrox.apexguide.internal.screen.GuideScreen;
import com.omnicrox.apexguide.ui.GuideUiHost;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

/**
 * Adds a "Hold X to show guide" tooltip
 */
public final class OpenGuideHotkey {
    private static final KeyMapping OPEN_GUIDE_MAPPING = new KeyMapping(
            "key.apexguide.guide", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_G,
            "key.apexguide.category");

    /** GuideNH: G in the world opens the guide's home page. */
    private static final KeyMapping OPEN_HOME_MAPPING = new KeyMapping(
            "key.apexguide.home", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_G,
            "key.apexguide.category");

    public static KeyMapping getHomeHotkey() {
        return OPEN_HOME_MAPPING;
    }

    private static final int TICKS_TO_OPEN = 10;

    private static boolean newTick = true;

    // The previous item the tooltip was being shown for
    private static ResourceLocation previousItemId;
    private static final List<FoundPage> guidebookPages = new ArrayList<>();
    // Full ticks since the button was held (reduces slowly when not held)
    private static int ticksKeyHeld;
    // Is the key to open currently held
    private static boolean holding;

    private OpenGuideHotkey() {
    }

    private record FoundPage(Guide guide, PageAnchor page) {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(
                (ItemTooltipEvent evt) -> {
                    // Ignore events fired for anything but the current local player,
                    // for example while building the search tree for the creative menu
                    if (evt.getEntity() != Minecraft.getInstance().player) {
                        return;
                    }
                    // Also ignore any events not on the render thread, since EMI for example might
                    // try to index tooltips off-thread
                    if (!Minecraft.getInstance().isSameThread()) {
                        return;
                    }
                    handleTooltip(evt.getItemStack(), evt.getFlags(), evt.getToolTip());
                });
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent evt) -> {
            if (evt.phase == TickEvent.Phase.END) {
                newTick = true;
                while (OPEN_HOME_MAPPING.consumeClick()) {
                    var mc = Minecraft.getInstance();
                    var guides = com.omnicrox.apexguide.Guides.getAll();
                    if (mc.screen == null && mc.player != null && !guides.isEmpty()) {
                        com.omnicrox.apexguide.internal.GuideMEClient.openGuideAtAnchor(guides.iterator().next(),
                                PageAnchor.page(com.omnicrox.apexguide.internal.screen.GuideScreen.HOME_PAGE_ID));
                    }
                }
            }
        });
    }

    private static void handleTooltip(ItemStack itemStack, TooltipFlag tooltipFlag, List<Component> lines) {
        // Player didn't bind the key
        if (!isKeyBound()) {
            holding = false;
            ticksKeyHeld = 0;
            return;
        }

        // This should only update once per client-tick
        if (newTick) {
            newTick = false;
            update(itemStack);
        }

        if (guidebookPages.isEmpty()) {
            return;
        }

        var guide = guidebookPages.get(0).guide();
        var pageAnchor = guidebookPages.get(0).page();

        // Don't do anything if we're already on the target page
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof GuideScreen guideScreen
                && guideScreen.getGuide() == guide
                && guideScreen.getCurrentPageId().equals(pageAnchor.pageId())) {
            return;
        }

        // Compute the progress value between [0,1]
        float progress = ticksKeyHeld;
        if (holding) {
            progress += minecraft.getDeltaFrameTime();
        } else {
            progress -= minecraft.getDeltaFrameTime();
        }
        progress /= (float) TICKS_TO_OPEN;
        var component = makeProgressBar(Mth.clamp(progress, 0, 1));
        // It may happen that we're the only line
        if (lines.isEmpty()) {
            lines.add(component);
        } else {
            lines.add(1, component);
        }
    }

    private static Component makeProgressBar(float progress) {
        var minecraft = Minecraft.getInstance();

        var holdW = GuidebookText.HoldToShow
                .text(getHotkey().getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY))
                .withStyle(ChatFormatting.DARK_GRAY);

        var fontRenderer = minecraft.font;
        var charWidth = fontRenderer.width("|");
        var tipWidth = fontRenderer.width(holdW);

        var total = tipWidth / charWidth;
        var current = (int) (progress * total);

        if (progress > 0) {
            var result = Component.literal(Strings.repeat("|", current)).withStyle(ChatFormatting.GRAY);
            if (progress < 1)
                result = result.append(
                        Component.literal(Strings.repeat("|", total - current)).withStyle(ChatFormatting.DARK_GRAY));
            return result;
        }

        return holdW;
    }

    private static void update(ItemStack itemStack) {
        var itemId = itemStack.getItemHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

        if (!Objects.equals(itemId, previousItemId)) {
            previousItemId = itemId;
            guidebookPages.clear();
            ticksKeyHeld = 0;

            if (itemId == null) {
                return;
            }

            for (var guide : GuideRegistry.getAll()) {
                if (!guide.isAvailableToOpenHotkey()) {
                    continue;
                }

                var itemIndex = guide.getIndex(ItemIndex.class);
                var page = itemIndex.get(itemId);
                if (page != null) {
                    guidebookPages.add(new FoundPage(guide, page));
                }
            }
        }

        // Bump the ticks the key was held
        holding = isKeyHeld();
        if (holding) {
            if (ticksKeyHeld < TICKS_TO_OPEN && ++ticksKeyHeld == TICKS_TO_OPEN) {
                if (!guidebookPages.isEmpty()) {
                    var foundPage = guidebookPages.get(0);
                    var guide = foundPage.guide();

                    if (Minecraft.getInstance().screen instanceof GuideUiHost uiHost && uiHost.getGuide() == guide) {
                        uiHost.navigateTo(foundPage.page());
                    } else {
                        GuideMEClient.openGuideAtAnchor(guide, foundPage.page());
                    }
                    // Reset the ticks held immediately to avoid reopening another page if
                    // our cursors lands on an item
                    ticksKeyHeld = 0;
                    holding = false;
                }
            } else if (ticksKeyHeld > TICKS_TO_OPEN) {
                ticksKeyHeld = TICKS_TO_OPEN;
            }
        } else {
            ticksKeyHeld = Math.max(0, ticksKeyHeld - 2);
        }
    }

    /**
     * This circumvents any current UI key handling.
     */
    private static boolean isKeyHeld() {
        int keyCode = getHotkey().getKey().getValue();
        var window = Minecraft.getInstance().getWindow().getWindow();

        return InputConstants.isKeyDown(window, keyCode);
    }

    private static boolean isKeyBound() {
        return !OPEN_GUIDE_MAPPING.isUnbound();
    }

    public static KeyMapping getHotkey() {
        return OPEN_GUIDE_MAPPING;
    }
}
