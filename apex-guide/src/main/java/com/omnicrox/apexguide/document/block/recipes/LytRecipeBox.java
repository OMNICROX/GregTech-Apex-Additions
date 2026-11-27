package com.omnicrox.apexguide.document.block.recipes;

import com.omnicrox.apexguide.document.block.LytBox;
import com.omnicrox.apexguide.siteexport.ExportableResourceProvider;
import com.omnicrox.apexguide.siteexport.ResourceExporter;
import net.minecraft.world.item.crafting.Recipe;

@Deprecated(forRemoval = true)
public abstract class LytRecipeBox extends LytBox implements ExportableResourceProvider {
    private final Recipe<?> recipe;

    public LytRecipeBox(Recipe<?> recipe) {
        this.recipe = recipe;
    }

    @Override
    public void exportResources(ResourceExporter exporter) {
        exporter.referenceRecipe(this.recipe);
    }
}
