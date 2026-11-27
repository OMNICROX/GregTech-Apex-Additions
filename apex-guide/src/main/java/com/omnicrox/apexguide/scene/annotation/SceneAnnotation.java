package com.omnicrox.apexguide.scene.annotation;

import com.omnicrox.apexguide.document.block.LytBlock;
import com.omnicrox.apexguide.document.interaction.ContentTooltip;
import com.omnicrox.apexguide.document.interaction.GuideTooltip;
import com.omnicrox.apexguide.document.interaction.TextTooltip;
import com.omnicrox.apexguide.siteexport.ExportableResourceProvider;
import com.omnicrox.apexguide.siteexport.ResourceExporter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * An annotation to show additional information to the user about content in a {@link com.omnicrox.apexguide.scene.GuidebookScene}.
 */
public abstract class SceneAnnotation implements ExportableResourceProvider {
    @Nullable
    private GuideTooltip tooltip;

    private boolean hovered;

    public @Nullable GuideTooltip getTooltip() {
        return tooltip;
    }

    public void setTooltip(@Nullable GuideTooltip tooltip) {
        this.tooltip = tooltip;
    }

    public void setTooltipContent(LytBlock block) {
        this.tooltip = new ContentTooltip(block);
    }

    public void setTooltipContent(Component component) {
        this.tooltip = new TextTooltip(component);
    }

    public boolean hasTooltip() {
        return tooltip != null;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    @Override
    public void exportResources(ResourceExporter exporter) {
        if (tooltip != null) {
            tooltip.exportResources(exporter);
        }
    }
}
