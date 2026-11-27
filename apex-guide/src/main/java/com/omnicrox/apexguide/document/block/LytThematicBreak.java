package com.omnicrox.apexguide.document.block;

import com.omnicrox.apexguide.color.SymbolicColor;
import com.omnicrox.apexguide.document.LytRect;
import com.omnicrox.apexguide.layout.LayoutContext;
import com.omnicrox.apexguide.render.RenderContext;
import net.minecraft.client.renderer.MultiBufferSource;

public class LytThematicBreak extends LytBlock {
    @Override
    public LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth) {
        return new LytRect(x, y, availableWidth, 6);
    }

    @Override
    protected void onLayoutMoved(int deltaX, int deltaY) {
    }

    @Override
    public void renderBatch(RenderContext context, MultiBufferSource buffers) {
    }

    @Override
    public void render(RenderContext context) {
        var line = bounds.withHeight(2).centerVerticallyIn(bounds);

        context.fillRect(line, SymbolicColor.THEMATIC_BREAK);
    }
}
