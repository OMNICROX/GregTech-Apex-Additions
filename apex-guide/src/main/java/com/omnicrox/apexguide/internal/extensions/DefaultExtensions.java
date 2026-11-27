package com.omnicrox.apexguide.internal.extensions;

import com.omnicrox.apexguide.compiler.TagCompiler;
import com.omnicrox.apexguide.compiler.tags.ATagCompiler;
import com.omnicrox.apexguide.compiler.tags.BoxFlowDirection;
import com.omnicrox.apexguide.compiler.tags.BoxTagCompiler;
import com.omnicrox.apexguide.compiler.tags.BreakCompiler;
import com.omnicrox.apexguide.compiler.tags.CategoryIndexCompiler;
import com.omnicrox.apexguide.compiler.tags.ColorTagCompiler;
import com.omnicrox.apexguide.compiler.tags.CommandLinkCompiler;
import com.omnicrox.apexguide.compiler.tags.DivTagCompiler;
import com.omnicrox.apexguide.compiler.tags.FloatingImageCompiler;
import com.omnicrox.apexguide.compiler.tags.ItemGridCompiler;
import com.omnicrox.apexguide.compiler.tags.ItemLinkCompiler;
import com.omnicrox.apexguide.compiler.tags.KeyBindTagCompiler;
import com.omnicrox.apexguide.compiler.tags.PlayerNameTagCompiler;
import com.omnicrox.apexguide.compiler.tags.RecipeCompiler;
import com.omnicrox.apexguide.compiler.tags.RecipeTypeMappingSupplier;
import com.omnicrox.apexguide.compiler.tags.SubPagesCompiler;
import com.omnicrox.apexguide.extensions.Extension;
import com.omnicrox.apexguide.extensions.ExtensionCollection;
import com.omnicrox.apexguide.extensions.ExtensionPoint;
import com.omnicrox.apexguide.scene.BlockImageTagCompiler;
import com.omnicrox.apexguide.scene.ItemImageTagCompiler;
import com.omnicrox.apexguide.scene.SceneTagCompiler;
import com.omnicrox.apexguide.scene.annotation.BlockAnnotationElementCompiler;
import com.omnicrox.apexguide.scene.annotation.BlockAnnotationTemplateElementCompiler;
import com.omnicrox.apexguide.scene.annotation.BoxAnnotationElementCompiler;
import com.omnicrox.apexguide.scene.annotation.DiamondAnnotationElementCompiler;
import com.omnicrox.apexguide.scene.annotation.LineAnnotationElementCompiler;
import com.omnicrox.apexguide.scene.element.EntityElementCompiler;
import com.omnicrox.apexguide.scene.element.ImportStructureElementCompiler;
import com.omnicrox.apexguide.scene.element.IsometricCameraElementCompiler;
import com.omnicrox.apexguide.scene.element.SceneBlockElementCompiler;
import com.omnicrox.apexguide.scene.element.SceneElementTagCompiler;
import com.omnicrox.apexguide.scene.element.SceneRemoveBlocksElementCompiler;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.world.item.crafting.RecipeType;

public final class DefaultExtensions {
    private static final List<Registration<?>> EXTENSIONS = List.of(
            new Registration<>(TagCompiler.EXTENSION_POINT, DefaultExtensions::tagCompilers),
            new Registration<>(SceneElementTagCompiler.EXTENSION_POINT, DefaultExtensions::sceneElementTagCompilers),
            new Registration<>(RecipeTypeMappingSupplier.EXTENSION_POINT, DefaultExtensions::vanillaRecipeTypes));

    private DefaultExtensions() {
    }

    public static void addAll(ExtensionCollection.Builder builder, Set<ExtensionPoint<?>> disabledExtensionPoints) {
        for (var registration : EXTENSIONS) {
            add(builder, disabledExtensionPoints, registration);
        }
    }

    private static <T extends Extension> void add(ExtensionCollection.Builder builder,
            Set<ExtensionPoint<?>> disabledExtensionPoints, Registration<T> registration) {
        if (disabledExtensionPoints.contains(registration.extensionPoint)) {
            return;
        }

        for (var extension : registration.factory.get()) {
            builder.add(registration.extensionPoint, extension);
        }
    }

    private static List<TagCompiler> tagCompilers() {
        return List.of(
                new DivTagCompiler(),
                new ATagCompiler(),
                new ColorTagCompiler(),
                new ItemLinkCompiler(),
                new FloatingImageCompiler(),
                new BreakCompiler(),
                new RecipeCompiler(),
                new ItemGridCompiler(),
                new CategoryIndexCompiler(),
                new BlockImageTagCompiler(),
                new ItemImageTagCompiler(),
                new BoxTagCompiler(BoxFlowDirection.ROW),
                new BoxTagCompiler(BoxFlowDirection.COLUMN),
                new SceneTagCompiler(),
                new SubPagesCompiler(),
                new CommandLinkCompiler(),
                new PlayerNameTagCompiler(),
                new KeyBindTagCompiler());
    }

    private static List<SceneElementTagCompiler> sceneElementTagCompilers() {
        return List.of(
                new EntityElementCompiler(),
                new SceneBlockElementCompiler(),
                new ImportStructureElementCompiler(),
                new IsometricCameraElementCompiler(),
                new BlockAnnotationElementCompiler(),
                new BoxAnnotationElementCompiler(),
                new LineAnnotationElementCompiler(),
                new DiamondAnnotationElementCompiler(),
                new BlockAnnotationTemplateElementCompiler(),
                new SceneRemoveBlocksElementCompiler());
    }

    private static List<RecipeTypeMappingSupplier> vanillaRecipeTypes() {
        return List.of(
                mappings -> {
                    mappings.add(RecipeType.CRAFTING, VanillaRecipes::createCrafting);
                    mappings.add(RecipeType.BLASTING, VanillaRecipes::createBlasting);
                    mappings.add(RecipeType.SMELTING, VanillaRecipes::createSmelting);
                    mappings.add(RecipeType.SMITHING, VanillaRecipes::createSmithing);
                });
    }

    private record Registration<T extends Extension>(ExtensionPoint<T> extensionPoint,
            Supplier<Collection<T>> factory) {
    }
}
