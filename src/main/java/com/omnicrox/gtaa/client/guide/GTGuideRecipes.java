package com.omnicrox.gtaa.client.guide;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.omnicrox.apexguide.compiler.tags.RecipeTypeMappingSupplier;
import com.omnicrox.apexguide.document.block.LytBlock;
import com.omnicrox.apexguide.document.block.LytParagraph;
import com.omnicrox.apexguide.document.block.LytSlotGrid;
import com.omnicrox.apexguide.document.block.recipes.LytStandardRecipeBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/** GregTech recipes as guide recipe boxes: items in / out (cycling options), fluids, EU/t, time, tier */
public final class GTGuideRecipes {

    private GTGuideRecipes() {}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void collect(RecipeTypeMappingSupplier.RecipeTypeMappings mappings) {
        for (GTRecipeType type : GTRegistries.RECIPE_TYPES) {
            mappings.add((RecipeType) type, r -> box((GTRecipe) r));
        }
    }

    private static List<Ingredient> items(java.util.Map<?, List<Content>> map, List<String> notes, boolean in) {
        List<Ingredient> out = new ArrayList<>();
        if (map == null) return out;
        List<Content> list = map.get(ItemRecipeCapability.CAP);
        if (list == null) return out;
        for (Content c : list) {
            try {
                Ingredient ing = ItemRecipeCapability.CAP.of(c.content);
                out.add(ing);
                ItemStack[] st = ing.getItems();
                String name = st.length > 0 ? st[0].getHoverName().getString() : "?";
                if (in && c.chance == 0) notes.add(name + ": not consumed");
                else if (c.chance > 0 && c.chance < c.maxChance)
                    notes.add(name + ": " + FormattingUtil.formatNumbers(100.0 * c.chance / c.maxChance) + "% chance");
            } catch (Throwable ignored) {}
        }
        return out;
    }

    private static String fluids(java.util.Map<?, List<Content>> map) {
        if (map == null) return "";
        List<Content> list = map.get(FluidRecipeCapability.CAP);
        if (list == null || list.isEmpty()) return "";
        List<String> parts = new ArrayList<>();
        for (Content c : list) {
            try {
                FluidStack[] st = FluidRecipeCapability.CAP.of(c.content).getStacks();
                if (st.length == 0) continue;
                parts.add(FormattingUtil.formatNumbers(st[0].getAmount()) + " mB " + st[0].getDisplayName().getString());
            } catch (Throwable ignored) {}
        }
        return String.join(", ", parts);
    }

    private static LytSlotGrid grid(List<Ingredient> ings) {
        int n = Math.max(1, ings.size());
        int cols = Math.min(3, n);
        LytSlotGrid g = new LytSlotGrid(cols, (n + cols - 1) / cols);
        for (int i = 0; i < ings.size(); i++) g.setIngredient(i % cols, i / cols, ings.get(i));
        return g;
    }

    public static LytBlock box(GTRecipe r) {
        List<String> notes = new ArrayList<>();
        List<Ingredient> in = items(r.inputs, notes, true);
        List<Ingredient> out = items(r.outputs, notes, false);
        String fin = fluids(r.inputs), fout = fluids(r.outputs);
        var eu = RecipeHelper.getRealEUtWithIO(r);
        long eut = eu.getTotalEU();
        int tier = RecipeHelper.getRecipeEUtTier(r);
        StringBuilder info = new StringBuilder();
        if (eut > 0) info.append(eu.isInput() ? "Uses " : "Makes ").append(FormattingUtil.formatNumbers(eut)).append(" EU/t (")
                .append(GTValues.VNF[Math.max(0, Math.min(tier, GTValues.VNF.length - 1))].replaceAll("§.", "")).append(") · ");
        info.append(FormattingUtil.formatNumbers(r.duration / 20.0)).append(" s");
        ItemStack icon = ItemStack.EMPTY;
        try {
            icon = r.recipeType.getIconSupplier() != null ? r.recipeType.getIconSupplier().get() : ItemStack.EMPTY;
        } catch (Throwable ignored) {}
        var b = LytStandardRecipeBox.builder()
                .title(Component.translatable(r.recipeType.registryName.toLanguageKey()).getString())
                .icon(icon.isEmpty() ? new ItemStack(Items.FURNACE) : icon)
                .input(grid(in))
                .output(grid(out));
        if (!fin.isEmpty()) b.addBottom(LytParagraph.of("Fluids in: " + fin));
        if (!fout.isEmpty()) b.addBottom(LytParagraph.of("Fluids out: " + fout));
        b.addBottom(LytParagraph.of(info.toString()));
        for (String n : notes) b.addBottom(LytParagraph.of(n));
        return b.build(r);
    }
}
