
package com.omnicrox.apexguide.scene;

import com.omnicrox.apexguide.color.SymbolicColor;
import com.omnicrox.apexguide.compiler.IndexingContext;
import com.omnicrox.apexguide.compiler.IndexingSink;
import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.tags.BlockTagCompiler;
import com.omnicrox.apexguide.compiler.tags.MdxAttrs;
import com.omnicrox.apexguide.document.block.LytBlockContainer;
import com.omnicrox.apexguide.extensions.Extension;
import com.omnicrox.apexguide.extensions.ExtensionCollection;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.libs.mdast.model.MdAstNode;
import com.omnicrox.apexguide.scene.element.SceneElementTagCompiler;
import com.omnicrox.apexguide.scene.level.GuidebookLevel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SceneTagCompiler extends BlockTagCompiler implements Extension {
    public static final String TAG_NAME = "GameScene";

    private final Map<String, SceneElementTagCompiler> elementTagCompilers = new HashMap<>();

    @Override
    public Set<String> getTagNames() {
        return Set.of(TAG_NAME);
    }

    @Override
    protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el) {
        var padding = MdxAttrs.getInt(compiler, parent, el, "padding", 5);
        var zoom = MdxAttrs.getFloat(compiler, parent, el, "zoom", 1.0f);
        var background = MdxAttrs.getColor(compiler, parent, el, "background", SymbolicColor.SCENE_BACKGROUND);

        var level = new GuidebookLevel();
        var cameraSettings = new CameraSettings();
        cameraSettings.setZoom(zoom);

        var scene = new GuidebookScene(level, cameraSettings);

        for (var child : el.children()) {
            if (child instanceof MdxJsxElementFields childEl) {
                var childTagName = childEl.name();
                var childCompiler = elementTagCompilers.get(childTagName);
                if (childCompiler == null) {
                    parent.appendError(compiler, "Unknown scene element", child);
                } else {
                    childCompiler.compile(scene, compiler, parent, childEl);
                }
            }
        }

        scene.getCameraSettings().setRotationCenter(scene.getWorldCenter());
        scene.centerScene();

        var lytScene = new LytGuidebookScene(compiler.getExtensions());
        lytScene.setScene(scene);
        lytScene.setBackground(background);
        lytScene.setPadding(padding);
        if (MdxAttrs.getBoolean(compiler, parent, el, "interactive", false)) {
            lytScene.setInteractive(true);
        }
        if (MdxAttrs.getBoolean(compiler, parent, el, "fullWidth", false)) {
            lytScene.setFullWidth(true);
        }
        lytScene.setSourceNode((MdAstNode) el);

        parent.append(lytScene);
    }

    @Override
    public void onExtensionsBuilt(ExtensionCollection extensions) {
        for (var sceneElementTag : extensions.get(SceneElementTagCompiler.EXTENSION_POINT)) {
            for (var tagName : sceneElementTag.getTagNames()) {
                // This explicitly allows for overrides
                elementTagCompilers.put(tagName, sceneElementTag);
            }
        }
    }

    @Override
    public void index(IndexingContext indexer, MdxJsxElementFields el, IndexingSink sink) {
    }
}
