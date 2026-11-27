package com.omnicrox.apexguide.layout;

import com.omnicrox.apexguide.document.LytRect;
import com.omnicrox.apexguide.document.block.AlignItems;
import com.omnicrox.apexguide.document.block.LytAxis;
import com.omnicrox.apexguide.document.block.LytBlock;
import java.util.List;
import org.joml.Vector2i;

public final class Layouts {
    private Layouts() {
    }

    public static LytRect verticalLayout(
            LayoutContext context,
            List<LytBlock> children,
            int x, int y, int availableWidth,
            int paddingLeft, int paddingTop, int paddingRight, int paddingBottom,
            int gap,
            AlignItems alignItems) {
        return verticalLayout(
                context, children, x, y, availableWidth, false, paddingLeft, paddingTop, paddingRight, paddingBottom,
                gap, alignItems);
    }

    /**
     * Lays out all children along the vertical axis, and returns the bounding box of the content area.
     */
    public static LytRect verticalLayout(
            LayoutContext context,
            List<LytBlock> children,
            int x, int y, int availableWidth,
            boolean fullWidth,
            int paddingLeft, int paddingTop, int paddingRight, int paddingBottom,
            int gap,
            AlignItems alignItems) {

        int contentWidth = 0;
        int contentHeight = 0;
        for (int iteration = 1; iteration <= 2; iteration++) {
            // Margins have been applied outside
            // Paddings need to be considered here
            var innerX = x + paddingLeft;
            var innerY = y + paddingTop;
            var innerWidth = availableWidth - paddingLeft - paddingRight;

            // Layout children vertically, without padding
            LytBlock previousBlock = null;
            contentWidth = paddingLeft;
            contentHeight = paddingTop;
            for (var child : children) {
                innerY = offsetIntoContentArea(LytAxis.VERTICAL, innerY, previousBlock, child);
                // Block width is the width available for the inner content area of the child
                var blockWidth = Math.max(1, innerWidth - child.getMarginLeft() - child.getMarginRight());
                var blockX = innerX + child.getMarginLeft();
                // Apex Guide: boxes (tables, recipes...) sit beside a floating block like GuideNH, or below it when
                // there isn't room. Paragraphs and lists wrap around floats on their own.
                if (!(child instanceof com.omnicrox.apexguide.document.block.LytParagraph)
                        && !(child instanceof com.omnicrox.apexguide.document.block.LytList)) {
                    context.clearFloatsAbove(innerY);
                    int left = Math.max(blockX, context.getLeftFloatRightEdge().orElse(blockX));
                    int right = Math.min(blockX + blockWidth, context.getRightFloatLeftEdge().orElse(blockX + blockWidth));
                    if (right - left < 160) {
                        var below = context.clearFloats(true, true);
                        if (below.isPresent()) {
                            innerY = Math.max(innerY, below.getAsInt());
                        }
                    } else {
                        blockWidth = Math.max(1, right - left - 4);
                        blockX = left;
                    }
                }
                var childBounds = child.layout(context, blockX, innerY, blockWidth);
                innerY += childBounds.height() + child.getMarginBottom() + gap;
                contentWidth = Math.max(contentWidth, childBounds.right() - x);
                contentHeight = Math.max(contentHeight, childBounds.bottom() - y);
                previousBlock = child;
            }

            // If content width exceeds available space it means one of the children had a fixed size exceeding
            // our available space. In that case, redo the layout again to give all other children that space we're
            // going
            // to use anyway
            if (contentWidth > availableWidth) {
                availableWidth = contentWidth;
                continue;
            }
            break;
        }

        if (fullWidth && contentWidth < availableWidth) {
            contentWidth = availableWidth;
        }

        // Align on the orthogonal axis
        alignChildren(LytAxis.HORIZONTAL, children, alignItems, x + paddingLeft, x + contentWidth);

        return new LytRect(
                x, y,
                contentWidth + paddingRight,
                contentHeight + paddingBottom);
    }

    @Deprecated(forRemoval = true)
    public static LytRect horizontalLayout(
            LayoutContext context,
            List<LytBlock> children,
            int x, int y, int availableWidth,
            int paddingLeft, int paddingTop, int paddingRight, int paddingBottom,
            int gap,
            AlignItems alignItems) {
        return horizontalLayout(
                context, children, x, y, availableWidth, false, paddingLeft, paddingTop, paddingRight, paddingBottom,
                gap, alignItems);
    }

    @Deprecated(forRemoval = true)
    public static LytRect horizontalLayout(
            LayoutContext context,
            List<LytBlock> children,
            int x, int y, int availableWidth, boolean fullWidth,
            int paddingLeft, int paddingTop, int paddingRight, int paddingBottom,
            int gap,
            AlignItems alignItems) {
        return horizontalLayout(
                context,
                children,
                x, y,
                availableWidth,
                fullWidth,
                paddingLeft, paddingTop, paddingRight, paddingBottom,
                gap,
                alignItems,
                true);
    }

    /**
     * Lays out all children along the horizontal axis, and returns the bounding box of the content area.
     */
    public static LytRect horizontalLayout(
            LayoutContext context,
            List<LytBlock> children,
            int x, int y, int availableWidth, boolean fullWidth,
            int paddingLeft, int paddingTop, int paddingRight, int paddingBottom,
            int gap,
            AlignItems alignItems,
            boolean wrap) {
        // Margins have been applied outside
        // Paddings need to be considered here
        var innerX = x + paddingLeft;
        var innerY = y + paddingTop;
        var innerWidth = availableWidth - paddingLeft - paddingRight;

        // Layout children horizontally, without padding
        LytBlock previousBlock = null;
        var contentWidth = paddingLeft;
        var contentHeight = paddingTop;
        var maxContentWidth = contentWidth;

        var itemsOnLine = 0;
        for (int i = 0; i < children.size(); i++) {
            var child = children.get(i);
            // Account for margins of the child, and margin collapsing
            innerX = offsetIntoContentArea(LytAxis.HORIZONTAL, innerX, previousBlock, child);
            var blockWidth = Math.max(1, innerWidth - contentWidth - child.getMarginLeft() - child.getMarginRight());
            var childBounds = child.layout(context, innerX, innerY + child.getMarginTop(), blockWidth);

            // Wrap, but not if we're the first item on the line
            if (wrap && childBounds.right() + child.getMarginRight() > x + innerWidth && itemsOnLine > 0) {
                innerX = x + paddingLeft;
                innerY = y + contentHeight + gap;
                previousBlock = null;
                itemsOnLine = 0;
                contentWidth = paddingLeft;
                i--;
                continue; // Redo on the new line
            }

            innerX += childBounds.width() + child.getMarginRight() + gap;
            contentWidth = Math.max(contentWidth, childBounds.right() - x);
            maxContentWidth = Math.max(maxContentWidth, contentWidth);
            contentHeight = Math.max(contentHeight, childBounds.bottom() - y);
            previousBlock = child;
            itemsOnLine++;
        }

        if (fullWidth && maxContentWidth < availableWidth) {
            maxContentWidth = availableWidth;
        }

        // Align on the orthogonal axis
        alignChildren(LytAxis.VERTICAL, children, alignItems, y + paddingTop, y + contentHeight);

        return new LytRect(
                x, y,
                maxContentWidth + paddingRight,
                contentHeight + paddingBottom);
    }

    private static void alignChildren(LytAxis axis, List<LytBlock> children, AlignItems alignItems, int start,
            int end) {
        var space = end - start;

        // Pass 2, align items
        for (var child : children) {
            var bounds = child.getBounds();
            var childSize = size(bounds, axis) + child.getMarginStart(axis) + child.getMarginEnd(axis);

            switch (alignItems) {
                case CENTER -> child.setLayoutPos(set(bounds.point(), axis, start + (space - childSize) / 2));
                case END -> child.setLayoutPos(set(bounds.point(), axis, end - childSize));
            }
        }
    }

    private static Vector2i set(Vector2i point, LytAxis axis, int pos) {
        return switch (axis) {
            case HORIZONTAL -> point.set(pos, point.y);
            case VERTICAL -> point.set(point.x, pos);
        };
    }

    private static int size(LytRect rect, LytAxis axis) {
        return switch (axis) {
            case HORIZONTAL -> rect.width();
            case VERTICAL -> rect.height();
        };
    }

    /**
     * Offsets position on the given axis into the content area of the child by adding the appropriate margin, while
     * accounting for potential collapsing of the margin with the previous block element.
     */
    private static int offsetIntoContentArea(LytAxis axis, int pos, LytBlock previousBlock, LytBlock child) {
        var previousMarginEnd = previousBlock != null ? previousBlock.getMarginEnd(axis) : 0;
        var childMarginStart = child.getMarginStart(axis);

        // Account for margins of the child, and margin collapsing
        if (previousMarginEnd > 0) {
            pos += Math.max(previousMarginEnd, childMarginStart) - previousMarginEnd;
        } else {
            pos += childMarginStart;
        }
        return pos;
    }
}
