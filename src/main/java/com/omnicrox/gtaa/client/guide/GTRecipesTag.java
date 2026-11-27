package com.omnicrox.gtaa.client.guide;

import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.tags.BlockTagCompiler;
import com.omnicrox.apexguide.compiler.tags.MdxAttrs;
import com.omnicrox.apexguide.document.block.LytBlockContainer;
import com.omnicrox.apexguide.document.block.LytParagraph;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@code <GTRecipesFor id="gtceu:lv_electric_motor" limit="2" />}: GregTech recipes that make an item (cheapest first).
 * {@code <GTRecipesUsing id="..." />}: recipes that use it. GuideME's own RecipeFor can't see GT outputs.
 */
public class GTRecipesTag extends BlockTagCompiler {

    @Override
    public Set<String> getTagNames() {
        return Set.of("GTRecipesFor", "GTRecipesUsing");
    }

    private static boolean has(Map<?, List<Content>> map, Item item) {
        if (map == null) return false;
        List<Content> list = map.get(ItemRecipeCapability.CAP);
        if (list == null) return false;
        for (Content c : list) {
            try {
                for (ItemStack s : ItemRecipeCapability.CAP.of(c.content).getItems()) if (s.getItem() == item) return true;
            } catch (Throwable ignored) {}
        }
        return false;
    }

    @Override
    protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            parent.appendError(compiler, "Recipes show in-game", el);
            return;
        }
        Item item = MdxAttrs.getRequiredItem(compiler, parent, el, "id");
        if (item == null) return;
        int limit = MdxAttrs.getInt(compiler, parent, el, "limit", 2);
        String type = el.getAttributeString("type", null);
        boolean using = "GTRecipesUsing".equals(el.name());
        List<GTRecipe> found = new ArrayList<>();
        for (Recipe<?> r : level.getRecipeManager().getRecipes()) {
            if (!(r instanceof GTRecipe g)) continue;
            if (type != null && !g.recipeType.registryName.toString().equals(type) && !g.recipeType.registryName.getPath().equals(type)) continue;
            if (has(using ? g.inputs : g.outputs, item)) found.add(g);
        }
        found.sort(Comparator.comparingLong(g -> RecipeHelper.getRealEUtWithIO(g).getTotalEU()));
        if (found.isEmpty()) {
            String fb = el.getAttributeString("fallbackText", null);
            parent.append(LytParagraph.of(fb != null ? fb : "No GregTech recipe found."));
            return;
        }
        for (int i = 0; i < Math.min(limit, found.size()); i++) parent.append(GTGuideRecipes.box(found.get(i)));
        if (found.size() > limit)
            parent.append(LytParagraph.of("+" + (found.size() - limit) + " more in JEI (" + (using ? "U" : "R") + ")."));
    }
}
