package com.omnicrox.gtaa.integration.jei;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.client.preview.MultiblockPreview;
import com.omnicrox.gtaa.client.preview.PreviewInput;
import com.omnicrox.gtaa.client.preview.PreviewPanel;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** GT New Horizons-style multiblock structure page, replacing GregTech's static JEI multiblock page */
public class MultiblockPreviewCategory implements IRecipeCategory<MultiblockPreviewCategory.Recipe> {

    public static final RecipeType<Recipe> TYPE = RecipeType.create(GTAA.MOD_ID, "multiblock_preview", Recipe.class);

    public record Recipe(MultiblockMachineDefinition def, List<ItemStack> parts) {}

    private final IDrawable background, icon, slot;

    private static final mezz.jei.api.ingredients.IIngredientRenderer<ItemStack> NOTHING = new mezz.jei.api.ingredients.IIngredientRenderer<>() {
        @Override
        public void render(GuiGraphics g, ItemStack ingredient) {}

        @Override
        @SuppressWarnings("removal")
        public List<Component> getTooltip(ItemStack ingredient, net.minecraft.world.item.TooltipFlag flag) {
            return List.of();
        }

        @Override
        public int getWidth() {
            return 1;
        }

        @Override
        public int getHeight() {
            return 1;
        }
    };

    public MultiblockPreviewCategory(IGuiHelper gui) {
        background = gui.createBlankDrawable(PreviewPanel.W, PreviewPanel.H);
        icon = gui.createDrawableItemStack(com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.ELECTRIC_BLAST_FURNACE.asStack());
        slot = gui.getSlotDrawable();
    }

    static Recipe recipe(MultiblockMachineDefinition def) {
        List<ItemStack> parts;
        try {
            var shapes = def.getMatchingShapes();
            parts = shapes.isEmpty() ? List.of(def.asStack()) : MultiblockPreview.parts(shapes.get(0), def.get().self());
        } catch (Throwable t) {
            parts = List.of(def.asStack());
        }
        return new Recipe(def, parts);
    }

    @Override
    public RecipeType<Recipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Multiblock Structure");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public int getWidth() {
        return PreviewPanel.W;
    }

    @Override
    public int getHeight() {
        return PreviewPanel.H;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, PreviewPanel.W - 16, 0).addItemStack(recipe.def.asStack());
        // the panel draws the part list itself (counts follow the shape / tier); JEI still finds this page from any part
        // (an invisible-ingredient list isn't a slot to NEI, so this is a real slot that draws nothing, 1x1 px)
        builder.addSlot(RecipeIngredientRole.INPUT, PreviewPanel.W - 18, 0).addItemStacks(new ArrayList<>(recipe.parts))
                .setCustomRenderer(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, NOTHING);
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView slots, GuiGraphics g, double mouseX, double mouseY) {
        MultiblockPreview p = MultiblockPreview.of(recipe.def);
        PreviewInput.drawn(p, g);
        PreviewPanel.draw(p, g, (int) mouseX, (int) mouseY);
    }

    @Override
    public ResourceLocation getRegistryName(Recipe recipe) {
        return recipe.def.getId();
    }
}
