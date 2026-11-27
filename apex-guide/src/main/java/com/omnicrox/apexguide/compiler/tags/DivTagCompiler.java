package com.omnicrox.apexguide.compiler.tags;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.TagCompiler;
import com.omnicrox.apexguide.document.block.LytBlockContainer;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxFlowElement;
import java.util.Set;

public class DivTagCompiler implements TagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("div");
    }

    @Override
    public void compileBlockContext(PageCompiler compiler, LytBlockContainer parent, MdxJsxFlowElement el) {
        compiler.compileBlockContext(el, parent);
    }
}
