package com.omnicrox.apexguide.scene;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.tags.BlockTagCompiler;
import com.omnicrox.apexguide.compiler.tags.MdxAttrs;
import com.omnicrox.apexguide.document.block.LytBlockContainer;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.libs.mdast.model.MdAstNode;
import com.omnicrox.apexguide.scene.level.GuidebookLevel;
import java.util.Set;
import net.minecraft.core.BlockPos;

/**
 * Handles tags like <code>&lt;BlockImage id="mod:blockid" /&gt;</code> and renders a 3D block image in its place.
 */
public class BlockImageTagCompiler extends BlockTagCompiler {

    public static final String TAG_NAME = "BlockImage";

    @Override
    public Set<String> getTagNames() {
        return Set.of(TAG_NAME);
    }

    @Override
    protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el) {
        var pair = MdxAttrs.getRequiredBlockAndId(compiler, parent, el, "id");
        if (pair == null) {
            return;
        }

        var scale = MdxAttrs.getFloat(compiler, parent, el, "scale", 1.0f);
        var perspective = MdxAttrs.getEnum(compiler, parent, el, "perspective", PerspectivePreset.ISOMETRIC_NORTH_EAST);
        if (perspective == null) {
            return;
        }

        var state = pair.getRight().defaultBlockState();
        state = MdxAttrs.applyBlockStateProperties(compiler, parent, el, state);

        var level = new GuidebookLevel();
        var cameraSettings = new CameraSettings();
        cameraSettings.setZoom(scale);
        cameraSettings.setPerspectivePreset(perspective);

        var scene = new GuidebookScene(level, cameraSettings);
        level.setBlockAndUpdate(BlockPos.ZERO, state);
        scene.centerScene();

        var lytScene = new LytGuidebookScene(compiler.getExtensions());
        lytScene.setScene(scene);
        lytScene.setInteractive(false);
        lytScene.setSourceNode((MdAstNode) el);
        parent.append(lytScene);
    }

}
