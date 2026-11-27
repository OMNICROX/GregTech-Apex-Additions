package com.omnicrox.apexguide.scene;

import com.omnicrox.apexguide.extensions.Extension;
import com.omnicrox.apexguide.extensions.ExtensionPoint;
import com.omnicrox.apexguide.scene.annotation.SceneAnnotation;
import com.omnicrox.apexguide.scene.level.GuidebookLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a way to generate a {@link SceneAnnotation} on the fly if no explicit annotation could be found under the
 * mouse.
 */
public interface ImplicitAnnotationStrategy extends Extension {
    ExtensionPoint<ImplicitAnnotationStrategy> EXTENSION_POINT = new ExtensionPoint<>(ImplicitAnnotationStrategy.class);

    @Nullable
    SceneAnnotation getAnnotation(GuidebookLevel level, BlockState blockState, BlockHitResult blockHitResult);
}
