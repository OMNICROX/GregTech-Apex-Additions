package com.omnicrox.gtaa.client.guide;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.tags.FlowTagCompiler;
import com.omnicrox.apexguide.compiler.tags.MdxAttrs;
import com.omnicrox.apexguide.document.LytRect;
import com.omnicrox.apexguide.document.block.LytBlock;
import com.omnicrox.apexguide.document.flow.InlineBlockAlignment;
import com.omnicrox.apexguide.document.flow.LytFlowInlineBlock;
import com.omnicrox.apexguide.document.flow.LytFlowParent;
import com.omnicrox.apexguide.layout.LayoutContext;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.apexguide.render.RenderContext;
import com.omnicrox.gtaa.client.preview.MultiblockPreview;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * {@code <Multiblock id="gtceu:electric_blast_furnace" />}: the interactive 3D structure in a guide page, styled like
 * GuideNH's scenes (floats right of the text by default; {@code align="left"} or {@code align="none"} to change,
 * {@code width} / {@code height} for the viewport size).
 */
public class MultiblockTag extends FlowTagCompiler {

    @Override
    public Set<String> getTagNames() {
        return Set.of("Multiblock");
    }

    @Override
    protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el) {
        ResourceLocation id = MdxAttrs.getRequiredId(compiler, parent, el, "id");
        if (id == null) return;
        var def = GTRegistries.MACHINES.get(id);
        if (!(def instanceof MultiblockMachineDefinition mb)) {
            parent.append(compiler.createErrorFlowContent("Not a multiblock: " + id, el));
            return;
        }
        int w = (int) MdxAttrs.getFloat(compiler, parent, el, "width", 220);
        int h = (int) MdxAttrs.getFloat(compiler, parent, el, "height", 200);
        var block = new LytMultiblock(mb, Math.max(80, w), Math.max(60, h));
        var inline = new LytFlowInlineBlock();
        inline.setBlock(block);
        switch (el.getAttributeString("align", "right")) {
            case "left" -> {
                inline.setAlignment(InlineBlockAlignment.FLOAT_LEFT);
                block.setMarginRight(8);
            }
            case "none" -> inline.setAlignment(InlineBlockAlignment.INLINE);
            default -> {
                inline.setAlignment(InlineBlockAlignment.FLOAT_RIGHT);
                block.setMarginLeft(8);
            }
        }
        block.setMarginBottom(6);
        parent.append(inline);
    }

    static final class LytMultiblock extends LytBlock {
        private final MultiblockMachineDefinition def;
        private final int w, h;
        private final GuideScene.GuideState state = new GuideScene.GuideState();

        LytMultiblock(MultiblockMachineDefinition def, int w, int h) {
            this.def = def;
            this.w = w;
            this.h = h;
        }

        @Override
        protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth) {
            int width = Math.min(w, Math.max(80, availableWidth - GuideScene.BAR));
            return new LytRect(x, y, width + GuideScene.BAR, GuideScene.totalHeight(MultiblockPreview.of(def), h));
        }

        @Override
        protected void onLayoutMoved(int dx, int dy) {}

        @Override
        public void renderBatch(RenderContext context, MultiBufferSource buffers) {}

        @Override
        public void render(RenderContext context) {
            var g = context.guiGraphics();
            MultiblockPreview p = MultiblockPreview.of(def);
            g.pose().pushPose();
            g.pose().translate(bounds.x(), bounds.y(), 0);
            try {
                GuideScene.draw(p, state, g, bounds.width() - GuideScene.BAR, h);
            } finally {
                g.pose().popPose();
            }
        }
    }
}
