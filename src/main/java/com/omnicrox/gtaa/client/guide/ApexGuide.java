package com.omnicrox.gtaa.client.guide;

import com.omnicrox.gtaa.GTAA;
import com.omnicrox.apexguide.Guide;
import com.omnicrox.apexguide.GuideItemSettings;
import com.omnicrox.apexguide.compiler.TagCompiler;
import com.omnicrox.apexguide.compiler.tags.RecipeTypeMappingSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * The Apex Guide: a GuideME book (the system behind AE2's guide, like GTNH's GuideNH). Pages are markdown in
 * assets/gtapexadditions/guides/gtapexadditions/guide/. GregTech recipes render in it (<Recipe id=...> and
 * <GTRecipesFor id=...>). Open it with the Apex Guide item, /apexguide open gtapexadditions:guide, or by holding G over
 * an item that has a page.
 */
public final class ApexGuide {

    private ApexGuide() {}

    public static final ResourceLocation ID = GTAA.id("guide");

    /** page: a path inside the guide ("gregtech/tools.md"), with an optional #anchor */
    public static void openPage(String page) {
        try {
            var guide = com.omnicrox.apexguide.Guides.getById(ID);
            if (guide == null) return;
            var anchor = com.omnicrox.apexguide.PageAnchor.parse(page.contains(":") ? page : GTAA.MOD_ID + ":" + page);
            com.omnicrox.apexguide.internal.GuideMEClient.openGuideAtAnchor(guide, anchor);
        } catch (RuntimeException e) {
            GTAA.LOGGER.warn("[Apex Guide] could not open page {}", page, e);
        }
    }

    public static void init() {
        Guide.builder(ID)
                .itemSettings(new GuideItemSettings(Optional.of(Component.literal("Apex Guide")),
                        List.of(Component.literal("Everything about Apex Ascension: what to do next,").withStyle(ChatFormatting.GRAY),
                                Component.literal("how machines work, every multiblock.").withStyle(ChatFormatting.GRAY)),
                        Optional.empty()))
                .extension(RecipeTypeMappingSupplier.EXTENSION_POINT, GTGuideRecipes::collect)
                .extension(TagCompiler.EXTENSION_POINT, new GTRecipesTag())
                .extension(TagCompiler.EXTENSION_POINT, new MultiblockTag())
                .extension(TagCompiler.EXTENSION_POINT, new QuestLinkTag())
                .build();
        // /apexpage <page>: opens a guide page. The quest book's "Read more in the Apex Guide" lines run it.
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener(
                (net.minecraftforge.client.event.RegisterClientCommandsEvent e) -> e.getDispatcher().register(
                        net.minecraft.commands.Commands.literal("apexpage").then(net.minecraft.commands.Commands
                                .argument("page", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String page = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "page");
                                    net.minecraft.client.Minecraft.getInstance().tell(() -> openPage(page));
                                    return 1;
                                }))));
    }
}
