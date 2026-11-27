package com.omnicrox.apexguide.libs.mdast;

import com.omnicrox.apexguide.libs.mdast.model.MdAstRoot;
import com.omnicrox.apexguide.libs.micromark.Micromark;

public final class MdAst {
    private MdAst() {
    }

    public static MdAstRoot fromMarkdown(String markdown, MdastOptions options) {
        var evts = Micromark.parseAndPostprocess(markdown, options);
        return new MdastCompiler(options).compile(evts);
    }
}
