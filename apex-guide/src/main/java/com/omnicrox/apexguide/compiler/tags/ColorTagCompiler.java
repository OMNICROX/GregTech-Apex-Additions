package com.omnicrox.apexguide.compiler.tags;

import com.omnicrox.apexguide.color.ColorValue;
import com.omnicrox.apexguide.color.SymbolicColorResolver;
import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.document.flow.LytFlowParent;
import com.omnicrox.apexguide.document.flow.LytFlowSpan;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.style.TextStyle;
import java.util.Set;

public class ColorTagCompiler extends FlowTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("Color");
    }

    @Override
    protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el) {
        var id = MdxAttrs.getString(compiler, parent, el, "id", null);
        ColorValue color;
        if (id != null) {
            color = SymbolicColorResolver.resolve(compiler, id);
            if (color == null) {
                parent.appendError(compiler, "Cannot resolve symbolic color", el);
                return;
            }
        } else if (el.hasAttribute("color")) {
            color = MdxAttrs.getColor(compiler, parent, el, "color", null);
            if (color == null) {
                parent.appendError(compiler, "Malformed color value", el);
                return;
            }
        } else {
            parent.appendError(compiler, "Must either specify 'id' or 'color' attribute", el);
            return;
        }

        var span = new LytFlowSpan();
        span.setStyle(TextStyle.builder().color(color).build());
        parent.append(span);
        compiler.compileFlowContext(el.children(), span);
    }
}
