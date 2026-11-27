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
        List<ItemStack> parts = recipe.parts;
        int slots = Math.min(18, parts.size());
        for (int i = 0; i < slots; i++) {
            int x = 1 + (i % 9) * 18, y = PreviewPanel.SLOTS_Y + 1 + (i / 9) * 18;
            var b = builder.addSlot(RecipeIngredientRole.INPUT, x, y).setBackground(slot, -1, -1);
            if (i == 17 && parts.size() > 18) b.addItemStacks(new ArrayList<>(parts.subList(17, parts.size())));
            else b.addItemStack(parts.get(i));
        }
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
