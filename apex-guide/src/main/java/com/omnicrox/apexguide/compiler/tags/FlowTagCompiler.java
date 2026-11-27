package com.omnicrox.apexguide.compiler.tags;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.TagCompiler;
import com.omnicrox.apexguide.document.block.LytBlockContainer;
import com.omnicrox.apexguide.document.block.LytParagraph;
import com.omnicrox.apexguide.document.flow.LytFlowParent;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxFlowElement;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxTextElement;

/**
 * Compiler base-class for tag compilers that compile flow content but allow the flow content to be used in block
 * context by wrapping it in a paragraph.
 */
public abstract class FlowTagCompiler implements TagCompiler {
    protected abstract void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el);

    @Override
    public void compileFlowContext(PageCompiler compiler, LytFlowParent parent, MdxJsxTextElement el) {
        compile(compiler, parent, el);
    }

    @Override
    public final void compileBlockContext(PageCompiler compiler, LytBlockContainer parent, MdxJsxFlowElement el) {
        var paragraph = new LytParagraph();
        compile(compiler, paragraph, el);
        parent.append(paragraph);
    }
}
