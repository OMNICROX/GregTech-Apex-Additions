package com.omnicrox.apexguide.document.block;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.document.LytErrorSink;
import com.omnicrox.apexguide.libs.unist.UnistNode;

public interface LytBlockContainer extends LytErrorSink {
    void append(LytBlock node);

    @Override
    default void appendError(PageCompiler compiler, String text, UnistNode node) {
        append(compiler.createErrorBlock(text, node));
    }
}
