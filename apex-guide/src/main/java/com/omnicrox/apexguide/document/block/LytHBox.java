package com.omnicrox.apexguide.document.block;

import com.omnicrox.apexguide.document.LytRect;
import com.omnicrox.apexguide.layout.LayoutContext;
import com.omnicrox.apexguide.layout.Layouts;

/**
 * Lays out its children vertically.
 */
public class LytHBox extends LytAxisBox {
    private boolean wrap = true;

    @Override
    protected LytRect computeBoxLayout(LayoutContext context, int x, int y, int availableWidth) {
        // Padding is applied through the parent
        return Layouts.horizontalLayout(context,
                children,
                x,
                y,
                availableWidth,
                isFullWidth(),
                0,
                0,
                0,
                0,
                getGap(),
                getAlignItems(),
                wrap);
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }
}
