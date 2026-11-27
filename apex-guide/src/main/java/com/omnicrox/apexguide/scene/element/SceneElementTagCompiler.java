package com.omnicrox.apexguide.scene.element;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.document.LytErrorSink;
import com.omnicrox.apexguide.extensions.Extension;
import com.omnicrox.apexguide.extensions.ExtensionPoint;
import com.omnicrox.apexguide.internal.extensions.DefaultExtensions;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.scene.GuidebookScene;
import java.util.Set;

/**
 * Contributed by {@link DefaultExtensions}.
 */
public interface SceneElementTagCompiler extends Extension {
    ExtensionPoint<SceneElementTagCompiler> EXTENSION_POINT = new ExtensionPoint<>(SceneElementTagCompiler.class);

    Set<String> getTagNames();

    void compile(GuidebookScene scene, PageCompiler compiler, LytErrorSink errorSink, MdxJsxElementFields el);
}
