package com.omnicrox.apexguide;

import com.omnicrox.apexguide.compiler.ParsedGuidePage;
import com.omnicrox.apexguide.indices.PageIndex;
import com.omnicrox.apexguide.navigation.NavigationTree;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface PageCollection {
    <T extends PageIndex> T getIndex(Class<T> indexClass);

    Collection<ParsedGuidePage> getPages();

    @Nullable
    ParsedGuidePage getParsedPage(ResourceLocation id);

    @Nullable
    GuidePage getPage(ResourceLocation id);

    byte @Nullable [] loadAsset(ResourceLocation id);

    NavigationTree getNavigationTree();

    boolean pageExists(ResourceLocation pageId);
}
