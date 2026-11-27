package com.omnicrox.apexguide.document.block;

import com.omnicrox.apexguide.document.LytRect;
import com.omnicrox.apexguide.layout.LayoutContext;
import com.omnicrox.apexguide.layout.Layouts;

/**
 * Lays out its children vertically.
 */
public class LytVBox extends LytAxisBox {
    @Override
    protected LytRect computeBoxLayout(LayoutContext context, int x, int y, int availableWidth) {
        // Padding is applied through the parent
        return Layouts.verticalLayout(context,
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
                getAlignItems());
    }
}
