package com.omnicrox.apexguide.document.block.recipes;

import com.omnicrox.apexguide.document.DefaultStyles;
import com.omnicrox.apexguide.document.LytRect;
import com.omnicrox.apexguide.document.block.LytSlot;
import com.omnicrox.apexguide.internal.GuidebookText;
import com.omnicrox.apexguide.internal.util.Platform;
import com.omnicrox.apexguide.layout.LayoutContext;
import com.omnicrox.apexguide.render.GuiAssets;
import com.omnicrox.apexguide.render.RenderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Blocks;

@Deprecated(forRemoval = true)
public class LytCookingRecipe extends LytRecipeBox {
    private final ItemStack icon;

    private final Component title;

    private final AbstractCookingRecipe recipe;

    private final LytSlot inputSlot;

    private final LytSlot resultSlot;

    public LytCookingRecipe(ItemStack icon,
            Component title,
            AbstractCookingRecipe holder) {
        super(holder);
        this.icon = icon;
        this.title = title;
        this.recipe = holder;
        setPadding(5);
        paddingTop = 15;

        append(inputSlot = new LytSlot(recipe.getIngredients().get(0)));
        append(resultSlot = new LytSlot(recipe.getResultItem(Platform.getClientRegistryAccess())));
    }

    @Override
    protected LytRect computeBoxLayout(LayoutContext context, int x, int y, int availableWidth) {
        var inputBounds = inputSlot.layout(
                context,
                x,
                y,
                availableWidth);

        var resultBounds = resultSlot.layout(
                context,
                inputBounds.right() + 28,
                y,
                availableWidth);
        return LytRect.union(inputBounds, resultBounds);
    }

    @Override
    public void render(RenderContext context) {
        context.renderPanel(getBounds());

        context.renderItem(
                icon,
                bounds.x() + paddingLeft,
                bounds.y() + 4,
                8,
                8);
        context.renderText(
                title.getString(),
                DefaultStyles.CRAFTING_RECIPE_TYPE.mergeWith(DefaultStyles.BASE_STYLE),
                bounds.x() + paddingLeft + 10,
                bounds.y() + 4);

        context.fillIcon(
                new LytRect(bounds.right() - 25 - 24, bounds.y() + 10 + (bounds.height() - 27) / 2, 24, 17),
                GuiAssets.ARROW);

        super.render(context);
    }

    public static LytCookingRecipe createSmelting(SmeltingRecipe recipe) {
        return new LytCookingRecipe(
                Blocks.FURNACE.asItem().getDefaultInstance(),
                GuidebookText.Smelting.text(),
                recipe);
    }

    public static LytCookingRecipe createBlasting(BlastingRecipe recipe) {
        return new LytCookingRecipe(
                Blocks.BLAST_FURNACE.asItem().getDefaultInstance(),
                GuidebookText.Blasting.text(),
                recipe);
    }
}
