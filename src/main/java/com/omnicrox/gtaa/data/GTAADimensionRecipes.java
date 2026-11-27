package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/**
 * GregTech processing for the Aether, Twilight Forest and Undergarden's own materials, so what you bring back from a
 * world goes into GregTech (the worlds' exclusive ore veins are in GTAADimensionOres; the Twilight bosses' trophies
 * build the Library of Ruina; Aether gravitite goes into flight gear).
 */
public final class GTAADimensionRecipes {

    private GTAADimensionRecipes() {}

    private static final Item IN = Items.BARRIER;

    private static void run(Consumer<FinishedRecipe> p, String mod, String id, String input,
                            com.gregtechceu.gtceu.api.recipe.GTRecipeType type, int tier, int ticks,
                            UnaryOperator<GTRecipeBuilder> outputs) {
        outputs.apply(type.recipeBuilder("gtapexadditions:" + mod + "/" + id).inputItems(IN))
                .duration(ticks).EUt(VA[tier]).save(GTAACompatRecipes.needing(p, mod, Map.of(IN, input)));
    }

    private static UnaryOperator<GTRecipeBuilder> dust(Material m, int n) {
        return b -> b.outputItems(dust, m, n);
    }

    public static void init(Consumer<FinishedRecipe> p) {
        // ---------------- Aether ----------------
        run(p, "aether", "zanite", "aether:zanite_gemstone", CENTRIFUGE_RECIPES, MV, 200,
                b -> b.outputItems(dust, GTMaterials.Sapphire, 1).outputItems(dustSmall, GTMaterials.Titanium, 1));
        run(p, "aether", "ambrosium", "aether:ambrosium_shard", CENTRIFUGE_RECIPES, LV, 100,
                b -> b.outputItems(dust, GTMaterials.Glowstone, 1).outputItems(dustSmall, GTMaterials.Sulfur, 1));
        run(p, "aether", "holystone", "aether:holystone", MACERATOR_RECIPES, LV, 60, dust(GTMaterials.Stone, 1));
        // ---------------- Twilight Forest ----------------
        run(p, "twilightforest", "ironwood", "twilightforest:ironwood_ingot", MACERATOR_RECIPES, LV, 100,
                b -> b.outputItems(dust, GTMaterials.Iron, 1).outputItems(dustSmall, GTMaterials.Gold, 1));
        run(p, "twilightforest", "knightmetal", "twilightforest:knightmetal_ingot", MACERATOR_RECIPES, MV, 140,
                dust(GTMaterials.Steel, 1));
        run(p, "twilightforest", "steeleaf", "twilightforest:steeleaf_ingot", CENTRIFUGE_RECIPES, MV, 160,
                b -> b.outputItems(dustSmall, GTMaterials.Steel, 2).outputItems(GTItems.PLANT_BALL.asStack()));
        run(p, "twilightforest", "carminite", "twilightforest:carminite", CENTRIFUGE_RECIPES, HV, 200,
                b -> b.outputItems(dust, GTMaterials.Redstone, 2).outputItems(dustSmall, GTMaterials.Ruby, 1));
        run(p, "twilightforest", "fiery_ingot", "twilightforest:fiery_ingot", MACERATOR_RECIPES, HV, 200,
                b -> b.outputItems(dust, GTMaterials.Iron, 1).outputItems(dust, GTMaterials.Blaze, 1));
        // ---------------- Undergarden ----------------
        run(p, "undergarden", "utherium", "undergarden:utherium_crystal", CENTRIFUGE_RECIPES, EV, 300,
                b -> b.outputItems(dustSmall, GTMaterials.Uranium238, 2).outputItems(dustSmall, GTMaterials.Thorium, 1));
        run(p, "undergarden", "utheric_shard", "undergarden:utheric_shard", CENTRIFUGE_RECIPES, EV, 120,
                b -> b.outputItems(dustTiny, GTMaterials.Uranium238, 2).outputItems(dustTiny, GTMaterials.Thorium, 1));
        run(p, "undergarden", "regalium", "undergarden:regalium_crystal", CENTRIFUGE_RECIPES, HV, 200,
                b -> b.outputItems(dust, GTMaterials.Gold, 1).outputItems(dustSmall, GTMaterials.Diamond, 1));
        run(p, "undergarden", "cloggrum", "undergarden:cloggrum_ingot", MACERATOR_RECIPES, MV, 100,
                b -> b.outputItems(dust, GTMaterials.Iron, 1).outputItems(dustSmall, GTMaterials.Tin, 1));
        run(p, "undergarden", "froststeel", "undergarden:froststeel_ingot", MACERATOR_RECIPES, HV, 140, dust(GTMaterials.Steel, 1));
        run(p, "undergarden", "depthrock", "undergarden:depthrock", MACERATOR_RECIPES, LV, 60, dust(GTMaterials.Stone, 1));
    }
}
