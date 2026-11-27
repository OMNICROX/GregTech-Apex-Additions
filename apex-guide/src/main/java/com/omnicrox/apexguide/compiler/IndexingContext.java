package com.omnicrox.apexguide.compiler;

import com.omnicrox.apexguide.PageCollection;
import com.omnicrox.apexguide.extensions.Extension;
import com.omnicrox.apexguide.extensions.ExtensionCollection;
import com.omnicrox.apexguide.extensions.ExtensionPoint;
import com.omnicrox.apexguide.indices.PageIndex;
import com.omnicrox.apexguide.libs.mdast.model.MdAstAnyContent;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * The context used during search indexing of custom tags {@link TagCompiler}.
 */
@ApiStatus.NonExtendable
public interface IndexingContext {
    ExtensionCollection getExtensions();

    default <T extends Extension> List<T> getExtensions(ExtensionPoint<T> extensionPoint) {
        return getExtensions().get(extensionPoint);
    }

    /**
     * Get the current page id.
     */
    ResourceLocation getPageId();

    PageCollection getPageCollection();

    default void indexContent(List<? extends MdAstAnyContent> children, IndexingSink sink) {
        for (var child : children) {
            indexContent(child, sink);
        }
    }

    void indexContent(MdAstAnyContent content, IndexingSink sink);

    default byte @Nullable [] loadAsset(ResourceLocation imageId) {
        return getPageCollection().loadAsset(imageId);
    }

    default <T extends PageIndex> T getIndex(Class<T> clazz) {
        return getPageCollection().getIndex(clazz);
    }
}
