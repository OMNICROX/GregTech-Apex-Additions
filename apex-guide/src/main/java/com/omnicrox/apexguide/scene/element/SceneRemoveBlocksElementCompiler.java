package com.omnicrox.apexguide.scene.element;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.tags.MdxAttrs;
import com.omnicrox.apexguide.document.LytErrorSink;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.scene.GuidebookScene;
import java.util.Set;
import net.minecraft.world.level.block.Blocks;

/**
 * Removes block states from the scene.
 */
public class SceneRemoveBlocksElementCompiler implements SceneElementTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("RemoveBlocks");
    }

    @Override
    public void compile(GuidebookScene scene,
            PageCompiler compiler,
            LytErrorSink errorSink,
            MdxJsxElementFields el) {
        var predicate = MdxAttrs.getRequiredBlockStatePredicate(compiler, errorSink, el, "id");
        if (predicate == null) {
            return;
        }

        var level = scene.getLevel();
        var it = level.getFilledBlocks().iterator();
        while (it.hasNext()) {
            var pos = it.next();
            if (predicate.test(level.getBlockState(pos))) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);
            }
        }
    }
}
