package com.omnicrox.gtaa.client.guide;

import com.omnicrox.gtaa.GTAA;
import guideme.Guide;
import guideme.GuideItemSettings;
import guideme.compiler.TagCompiler;
import guideme.compiler.tags.RecipeTypeMappingSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * The Apex Guide: a GuideME book (the system behind AE2's guide, like GTNH's GuideNH). Pages are markdown in
 * assets/gtapexadditions/guides/gtapexadditions/guide/. GregTech recipes render in it (<Recipe id=...> and
 * <GTRecipesFor id=...>). Open it with the Apex Guide item, /guideme open gtapexadditions:guide, or by holding G over
 * an item that has a page.
 */
public final class ApexGuide {

    private ApexGuide() {}

    public static final ResourceLocation ID = GTAA.id("guide");

    public static void init() {
        Guide.builder(ID)
                .itemSettings(new GuideItemSettings(Optional.of(Component.literal("Apex Guide")),
                        List.of(Component.literal("Everything about Apex Ascension: what to do next,").withStyle(ChatFormatting.GRAY),
                                Component.literal("how machines work, every multiblock.").withStyle(ChatFormatting.GRAY)),
                        Optional.empty()))
                .extension(RecipeTypeMappingSupplier.EXTENSION_POINT, GTGuideRecipes::collect)
                .extension(TagCompiler.EXTENSION_POINT, new GTRecipesTag())
                .extension(TagCompiler.EXTENSION_POINT, new MultiblockTag())
                .build();
    }
}
