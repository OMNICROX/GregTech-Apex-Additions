package com.omnicrox.gtaa.client.guide;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.omnicrox.gtaa.client.preview.MultiblockPreview;
import com.omnicrox.gtaa.client.preview.PreviewInput;
import com.omnicrox.gtaa.client.preview.PreviewPanel;
import guideme.compiler.PageCompiler;
import guideme.compiler.tags.BlockTagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.LytRect;
import guideme.document.block.LytBlock;
import guideme.document.block.LytBlockContainer;
import guideme.layout.LayoutContext;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import guideme.render.RenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

/**
 * {@code <Multiblock id="gtceu:electric_blast_furnace" />}: the same interactive 3D structure as the JEI multiblock page
 * (layers, tiers, hatch spots, block list), inside a guide page. Like GTNH's GuideNH structure previews.
 */
public class MultiblockTag extends BlockTagCompiler {

    @Override
    public Set<String> getTagNames() {
        return Set.of("Multiblock");
    }

    @Override
    protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el) {
        ResourceLocation id = MdxAttrs.getRequiredId(compiler, parent, el, "id");
        if (id == null) return;
        var def = GTRegistries.MACHINES.get(id);
        if (!(def instanceof MultiblockMachineDefinition mb)) {
            parent.appendError(compiler, "Not a multiblock: " + id, el);
            return;
        }
        parent.append(new LytMultiblock(mb));
    }

    static final class LytMultiblock extends LytBlock {
        private final MultiblockMachineDefinition def;

        LytMultiblock(MultiblockMachineDefinition def) {
            this.def = def;
        }

        @Override
        protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth) {
            return new LytRect(x + 3, y + 3, PreviewPanel.W, PreviewPanel.H + 3);
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
            // JEI-style light panel so the preview's dark labels read on the guide's dark page
            g.fill(-3, -3, PreviewPanel.W + 3, PreviewPanel.H + 3, 0xFF8B8B8B);
            g.fill(-2, -2, PreviewPanel.W + 2, PreviewPanel.H + 2, 0xFFC6C6C6);
            try {
                PreviewInput.drawn(p, g);
                PreviewPanel.draw(p, g, 0, 0);
            } finally {
                g.pose().popPose();
            }
        }
    }
}
