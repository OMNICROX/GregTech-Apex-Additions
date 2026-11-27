package com.omnicrox.apexguide.compiler.tags;

import com.omnicrox.apexguide.compiler.IndexingContext;
import com.omnicrox.apexguide.compiler.IndexingSink;
import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.document.block.LytBlockContainer;
import com.omnicrox.apexguide.document.block.LytItemGrid;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import java.util.Set;

public class ItemGridCompiler extends BlockTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("ItemGrid");
    }

    @Override
    protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el) {
        var itemGrid = new LytItemGrid();

        // We expect children to only contain ItemIcon elements
        for (var childNode : el.children()) {
            if (childNode instanceof MdxJsxElementFields jsxChild && "ItemIcon".equals(jsxChild.name())) {
                var stack = MdxAttrs.getRequiredItemStack(compiler, parent, jsxChild);
                if (stack != null) {
                    itemGrid.addItem(stack);
                }

                continue;
            }
            parent.appendError(compiler, "Unsupported child-element in ItemGrid", childNode);
        }

        parent.append(itemGrid);
    }

    @Override
    public void index(IndexingContext indexer, MdxJsxElementFields el, IndexingSink sink) {
    }
}
