package com.omnicrox.apexguide.scene.annotation;

import com.omnicrox.apexguide.color.ConstantColor;
import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.tags.MdxAttrs;
import com.omnicrox.apexguide.document.LytErrorSink;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.scene.GuidebookScene;
import java.util.Set;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class DiamondAnnotationElementCompiler extends AnnotationTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("DiamondAnnotation");
    }

    @Override
    protected @Nullable SceneAnnotation createAnnotation(PageCompiler compiler, LytErrorSink errorSink,
            MdxJsxElementFields el) {
        return createAnnotation(null, compiler, errorSink, el, BlockPos.ZERO);
    }

    @Override
    protected @Nullable SceneAnnotation createAnnotation(GuidebookScene scene, PageCompiler compiler,
            LytErrorSink errorSink, MdxJsxElementFields el, BlockPos instancePosition) {
        var pos = MdxAttrs.getVector3(compiler, errorSink, el, "pos", new Vector3f());
        var color = MdxAttrs.getColor(compiler, errorSink, el, "color", ConstantColor.WHITE);

        pos.add(instancePosition.getX(), instancePosition.getY(), instancePosition.getZ());

        return new DiamondAnnotation(pos, color);
    }
}
