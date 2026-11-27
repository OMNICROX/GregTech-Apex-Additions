package com.omnicrox.apexguide.compiler.tags;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.document.flow.LytFlowBreak;
import com.omnicrox.apexguide.document.flow.LytFlowParent;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.libs.mdast.model.MdAstNode;
import java.util.Set;

public class BreakCompiler extends FlowTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("br");
    }

    @Override
    protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el) {
        var br = new LytFlowBreak();
        var clear = el.getAttributeString("clear", "none");
        switch (clear) {
            case "left" -> br.setClearLeft(true);
            case "right" -> br.setClearRight(true);
            case "all" -> {
                br.setClearLeft(true);
                br.setClearRight(true);
            }
            case "none" -> {
            }
            default -> parent.append(compiler.createErrorFlowContent("Invalid 'clear' attribute", (MdAstNode) el));
        }

        parent.append(br);
    }
}
