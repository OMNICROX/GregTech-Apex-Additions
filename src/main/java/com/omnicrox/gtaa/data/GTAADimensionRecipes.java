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
        bloodOrbs(p);
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

    /**
     * Blood Magic tier gating (GT New Horizons, NHCore's ScriptBloodMagic): Blood Orbs are soaked from Raw Orbs made in
     * GregTech, one voltage tier per orb (Weak LV ... Archmage IV). apex_blood_magic_gating.js swaps the altar inputs and
     * moves Blood Magic's orb crafting (runes, ritual stones...) into the assembler at the orb's tier.
     */
    public static void bloodOrbs(Consumer<FinishedRecipe> p) {
        var orbs = com.omnicrox.gtaa.common.data.GTAAItems.RAW_BLOOD_ORBS;
        var circuits = new net.minecraft.tags.TagKey[] { com.gregtechceu.gtceu.data.recipe.CustomTags.LV_CIRCUITS,
                com.gregtechceu.gtceu.data.recipe.CustomTags.MV_CIRCUITS, com.gregtechceu.gtceu.data.recipe.CustomTags.HV_CIRCUITS,
                com.gregtechceu.gtceu.data.recipe.CustomTags.EV_CIRCUITS, com.gregtechceu.gtceu.data.recipe.CustomTags.IV_CIRCUITS };
        Material[] plates = { GTMaterials.Steel, GTMaterials.Aluminium, GTMaterials.StainlessSteel, GTMaterials.Titanium,
                GTMaterials.TungstenSteel };
        int[] tiers = { LV, MV, HV, EV, IV };
        // the core each Blood Magic orb is soaked from: diamond, redstone block, gold block, weak blood shard, dungeon metal
        for (int i = 0; i < 5; i++) {
            GTRecipeBuilder b = ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:bloodmagic/raw_blood_orb_" + (i + 1));
            switch (i) {
                case 0 -> b.inputItems(gem, GTMaterials.Diamond, 1).inputItems(plate, GTMaterials.Redstone, 4);
                case 1 -> b.inputItems(block, GTMaterials.Redstone, 1).inputItems(plate, GTMaterials.Ruby, 2);
                case 2 -> b.inputItems(block, GTMaterials.Gold, 1).inputItems(plate, GTMaterials.Emerald, 2);
                case 3 -> b.inputItems(IN).inputItems(plate, GTMaterials.Diamond, 2);
                default -> b.inputItems(IN).inputItems(plate, GTMaterials.NetherStar, 1);
            }
            @SuppressWarnings("unchecked") net.minecraft.tags.TagKey<Item> c = (net.minecraft.tags.TagKey<Item>) circuits[i];
            b.inputItems(c, 2).inputItems(plate, plates[i], 4).inputFluids(GTMaterials.Glowstone.getFluid(144 * (i + 1)))
                    .outputItems(orbs[i].asStack()).duration(200 + 100 * i).EUt(VA[tiers[i]]);
            if (i == 3) b.save(GTAACompatRecipes.needing(p, "bloodmagic", Map.of(IN, "bloodmagic:weakbloodshard")));
            else if (i == 4) b.save(GTAACompatRecipes.needing(p, "bloodmagic", Map.of(IN, "bloodmagic:dungeon_metal")));
            else b.save(GTAACompatRecipes.needing(p, "bloodmagic", Map.of()));
        }
    }
}
