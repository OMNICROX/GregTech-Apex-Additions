package com.omnicrox.apexguide.document;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.libs.unist.UnistNode;

public interface LytErrorSink {
    void appendError(PageCompiler compiler, String text, UnistNode node);
}
