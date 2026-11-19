package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/**
 * Recipes that use items from other mods (Draconic Evolution, Mystical Agriculture). Those mods aren't in the
 * dev environment, so the recipes are built with stand-in vanilla items that are swapped for the real ids in the
 * JSON, and the recipe gets a forge:mod_loaded condition.
 */
public final class GTAACompatRecipes {

    private GTAACompatRecipes() {}

    // stand-ins, never used in real recipes
    private static final Item S1 = Items.BARRIER, S2 = Items.STRUCTURE_VOID, S3 = Items.LIGHT, S4 = Items.JIGSAW;

    static Consumer<FinishedRecipe> needing(Consumer<FinishedRecipe> provider, String modid, Map<Item, String> swap) {
        return fr -> {
            String text = fr.serializeRecipe().toString();
            for (var e : swap.entrySet())
                text = text.replace("\"" + BuiltInRegistries.ITEM.getKey(e.getKey()) + "\"", "\"" + e.getValue() + "\"");
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            JsonArray conditions = new JsonArray();
            JsonObject loaded = new JsonObject();
            loaded.addProperty("type", "forge:mod_loaded");
            loaded.addProperty("modid", modid);
            conditions.add(loaded);
            json.add("conditions", conditions);
            provider.accept(GTAAComponentAssembly.wrap(fr.getId(), json));
        };
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        draconic(provider);
        cultivation(provider);
        cultivationGuide(provider);
        chaos(provider);
    }

    /**
     * JEI guide for the Mystical Cultivation Array: one entry per crop seed (vanilla crops and every enabled
     * Mystical Agriculture / Agradditions crop). Built when recipes load, so it follows the crops in the pack.
     */
    private static void cultivationGuide(Consumer<FinishedRecipe> provider) {
        Map<Item, Object[]> vanilla = Map.of(
                Items.WHEAT_SEEDS, new Object[] { Items.WHEAT, 1 },
                Items.CARROT, new Object[] { Items.CARROT, 3 },
                Items.POTATO, new Object[] { Items.POTATO, 3 },
                Items.BEETROOT_SEEDS, new Object[] { Items.BEETROOT, 1 });
        int made = 0;
        for (Item seed : BuiltInRegistries.ITEM) {
            if (!(seed instanceof net.minecraft.world.item.BlockItem bi) ||
                    !(bi.getBlock() instanceof net.minecraft.world.level.block.CropBlock crop)) continue;
            Item product = null;
            int amount = 1;
            Object[] v = vanilla.get(seed);
            if (v != null) {
                product = (Item) v[0];
                amount = (int) v[1];
            } else {
                try {
                    Object c = crop.getClass().getMethod("getCrop").invoke(crop);
                    if (!(boolean) c.getClass().getMethod("isEnabled").invoke(c)) continue;
                    product = (Item) c.getClass().getMethod("getEssenceItem").invoke(c);
                } catch (Throwable ignored) {}
            }
            if (product == null || product == Items.AIR) continue;
            var id = BuiltInRegistries.ITEM.getKey(seed);
            com.omnicrox.gtaa.common.data.GTAARecipeTypes.CULTIVATION
                    .recipeBuilder("gtapexadditions:cultivation/" + id.getNamespace() + "/" + id.getPath())
                    .notConsumable(seed)
                    .inputFluids(GTMaterials.Water.getFluid(25))
                    .outputItems(new net.minecraft.world.item.ItemStack(product, amount * 4))
                    .duration(100).EUt(VA[LV]).save(provider);
            made++;
        }
        com.omnicrox.gtaa.GTAA.LOGGER.info("[GTAA] Mystical Cultivation Array guide: {} crops", made);
    }

    /**
     * Chaos Shards without fighting the Chaos Guardian every time: an assembly line bootstrap from a Dragon Heart,
     * and a UEV replicator that grows one shard into two.
     */
    private static void chaos(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:chaos_shard")
                .inputItems(S1)
                .inputItems(S2, 16)
                .inputItems(plateDense, GTMaterials.Neutronium, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UV, 2)
                .inputItems(CustomTags.UHV_CIRCUITS, 2)
                .inputItems(Items.NETHER_STAR, 4)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 8))
                .inputFluids(GTMaterials.Radon.getFluid(com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys.PLASMA, 1000))
                .outputItems(S3)
                .duration(1200).EUt(VA[UHV])
                .save(needing(provider, "draconicevolution", Map.of(S1, "draconicevolution:dragon_heart",
                        S2, "draconicevolution:awakened_draconium_ingot", S3, "draconicevolution:chaos_shard")));
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:chaos_shard_replication")
                .inputItems(S1)
                .inputItems(S2, 8)
                .inputItems(Items.NETHER_STAR, 2)
                .inputItems(CustomTags.UEV_CIRCUITS, 1)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 4))
                .inputFluids(GTMaterials.Radon.getFluid(com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys.PLASMA, 500))
                .outputItems(S1, 2)
                .duration(600).EUt(VA[UEV])
                .save(needing(provider, "draconicevolution", Map.of(
                        S1, "draconicevolution:chaos_shard", S2, "draconicevolution:awakened_draconium_ingot")));
    }

    private static void draconic(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:defc_casing")
                .inputItems(plate, GTMaterials.NaquadahAlloy, 6)
                .inputItems(frameGt, GTMaterials.TungstenSteel)
                .circuitMeta(6)
                .outputItems(GTAABlocks.DEFC_CASING.asStack(2))
                .duration(100).EUt(VA[LuV]).save(provider);

        String[][] cores = {
                { "draconicevolution:draconium_ingot", "draconicevolution:draconium_core" },
                { "draconicevolution:draconium_block", "draconicevolution:wyvern_core" },
                { "draconicevolution:awakened_draconium_ingot", "draconicevolution:awakened_core" },
                { "draconicevolution:large_chaos_frag", "draconicevolution:chaotic_core" } };
        var blocks = new com.tterrag.registrate.util.entry.BlockEntry<?>[] { GTAABlocks.DEFC_CORE_T1,
                GTAABlocks.DEFC_CORE_T2, GTAABlocks.DEFC_CORE_T3, GTAABlocks.DEFC_CORE_T4 };
        int[] volt = { LuV, ZPM, UV, UHV };
        for (int i = 0; i < 4; i++) {
            ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:defc_core_t" + (i + 1))
                    .inputItems(GTAABlocks.DEFC_CASING.asStack())
                    .inputItems(S1, 4)
                    .inputItems(S2)
                    .inputItems(CustomTags.CIRCUITS_ARRAY[volt[i]], 1)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 2 * (i + 1)))
                    .outputItems(blocks[i].asStack())
                    .duration(200 * (i + 1)).EUt(VA[volt[i]])
                    .save(needing(provider, "draconicevolution", Map.of(S1, cores[i][0], S2, cores[i][1])));
        }

        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:draconic_fusion_crafter")
                .inputItems(S1)
                .inputItems(S2, 4)
                .inputItems(GTBlocks.FUSION_COIL.asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_LuV, 2)
                .inputItems(GTItems.ROBOT_ARM_LuV, 4)
                .inputItems(CustomTags.LuV_CIRCUITS, 4)
                .inputItems(plateDouble, GTMaterials.NaquadahAlloy, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                .outputItems(GTAAMachines.DRACONIC_FUSION_CRAFTER.asStack())
                .duration(1200).EUt(VA[LuV])
                .save(needing(provider, "draconicevolution", Map.of(
                        S1, "draconicevolution:crafting_core", S2, "draconicevolution:basic_crafting_injector")));
    }

    private static void cultivation(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:mystical_cultivation_array")
                .inputItems(GTBlocks.CASING_STAINLESS_CLEAN.asStack(4))
                .inputItems(GTItems.ELECTRIC_PUMP_EV, 2)
                .inputItems(GTItems.ROBOT_ARM_EV, 2)
                .inputItems(CustomTags.EV_CIRCUITS, 4)
                .inputItems(S3, 4)
                .inputItems(S4, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 4))
                .outputItems(GTAAMachines.MYSTICAL_CULTIVATION_ARRAY.asStack())
                .duration(600).EUt(VA[EV])
                .save(needing(provider, "mysticalagriculture", Map.of(
                        S3, "mysticalagriculture:prudentium_ingot", S4, "mysticalagriculture:prudentium_essence")));
    }
}
