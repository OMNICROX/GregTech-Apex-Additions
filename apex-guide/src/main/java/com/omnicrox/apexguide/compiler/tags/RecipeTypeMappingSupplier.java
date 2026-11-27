package com.omnicrox.apexguide.compiler.tags;

import com.omnicrox.apexguide.document.block.LytBlock;
import com.omnicrox.apexguide.extensions.Extension;
import com.omnicrox.apexguide.extensions.ExtensionPoint;
import java.util.function.Function;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Allows mods to register mappings between recipe type and their custom recipe blocks for use in {@code <RecipeFor/>}
 * and similar tags.
 * <p/>
 * **NOTE:** In addition to being an extension point, implementations of this interface are also retrieved through the
 * Java Service-Loader to enable use of mod recipes cross-guide. Specific instances registered through
 * {@link com.omnicrox.apexguide.GuideBuilder#extension} will have higher priority than instances discovered through service-loader.
 */
public interface RecipeTypeMappingSupplier extends Extension {
    ExtensionPoint<RecipeTypeMappingSupplier> EXTENSION_POINT = new ExtensionPoint<>(RecipeTypeMappingSupplier.class);

    void collect(RecipeTypeMappings mappings);

    interface RecipeTypeMappings {
        <T extends Recipe<C>, C extends Container> void add(
                RecipeType<T> recipeType,
                Function<? super T, LytBlock> factory);
    }
}
