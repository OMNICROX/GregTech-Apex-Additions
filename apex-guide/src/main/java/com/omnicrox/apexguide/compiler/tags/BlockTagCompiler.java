package com.omnicrox.apexguide.compiler.tags;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.TagCompiler;
import com.omnicrox.apexguide.document.block.LytBlockContainer;
import com.omnicrox.apexguide.document.flow.InlineBlockAlignment;
import com.omnicrox.apexguide.document.flow.LytFlowInlineBlock;
import com.omnicrox.apexguide.document.flow.LytFlowParent;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxFlowElement;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxTextElement;

/**
 * Compiler base-class for tag compilers that compile block content but allow the block content to be used in flow
 * context by wrapping it in an inline block.
 */
public abstract class BlockTagCompiler implements TagCompiler {
    protected abstract void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el);

    @Override
    public final void compileFlowContext(PageCompiler compiler, LytFlowParent parent, MdxJsxTextElement el) {
        compile(compiler, node -> {
            var alignmentAttr = el.getAttributeString("float", "none");
            var alignment = switch (alignmentAttr) {
                case "left" -> InlineBlockAlignment.FLOAT_LEFT;
                case "right" -> InlineBlockAlignment.FLOAT_RIGHT;
                default -> InlineBlockAlignment.INLINE;
            };

            var inlineBlock = new LytFlowInlineBlock();
            inlineBlock.setBlock(node);
            inlineBlock.setAlignment(alignment);
            parent.append(inlineBlock);
        }, el);
    }

    @Override
    public final void compileBlockContext(PageCompiler compiler, LytBlockContainer parent, MdxJsxFlowElement el) {
        compile(compiler, parent, el);
    }
}
