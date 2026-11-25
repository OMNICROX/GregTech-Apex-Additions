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
    private static final Item S1 = Items.BARRIER, S2 = Items.STRUCTURE_VOID, S3 = Items.LIGHT, S4 = Items.JIGSAW,
            S5 = Items.COMMAND_BLOCK, S6 = Items.CHAIN_COMMAND_BLOCK, S7 = Items.REPEATING_COMMAND_BLOCK,
            S8 = Items.DEBUG_STICK, S9 = Items.KNOWLEDGE_BOOK, S10 = Items.COMMAND_BLOCK_MINECART;

    static Consumer<FinishedRecipe> needing(Consumer<FinishedRecipe> provider, String modid, Map<Item, String> swap) {
        return needing(provider, new String[] { modid }, swap);
    }

    static Consumer<FinishedRecipe> needing(Consumer<FinishedRecipe> provider, String[] modids, Map<Item, String> swap) {
        return fr -> {
            String text = fr.serializeRecipe().toString();
            for (var e : swap.entrySet())
                text = text.replace("\"" + BuiltInRegistries.ITEM.getKey(e.getKey()) + "\"", "\"" + e.getValue() + "\"");
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            JsonArray conditions = new JsonArray();
            for (String modid : modids) {
                JsonObject loaded = new JsonObject();
                loaded.addProperty("type", "forge:mod_loaded");
                loaded.addProperty("modid", modid);
                conditions.add(loaded);
            }
            json.add("conditions", conditions);
            provider.accept(GTAAComponentAssembly.wrap(fr.getId(), json));
        };
    }

    public static void init(Consumer<FinishedRecipe> provider) {
        draconic(provider);
        cultivation(provider);
        cultivationGuide(provider);
        chaos(provider);
        angelRing(provider);
        aether(provider);
        godforge(provider);
        apexGate(provider);
        manaBloom(provider);
        maxwellCats(provider);
    }

    /**
     * Forge of the Gods (GT Nexus Additions): it boots on Star Fuel (Re-Avaritia's item, see ForgeOfGodsStarFuelMixin),
     * made here from Neutronium Blocks, and Star Fuel also melts into Raw Star Matter, the Forge's second running fuel,
     * which nothing else made.
     */
    private static void godforge(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:godforge/star_fuel")
                .inputItems(net.minecraft.world.item.Item.byBlock(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.getBlock(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.block, GTMaterials.Neutronium)))
                .inputItems(S1)
                .outputItems(S2)
                .duration(200).EUt(VA[UHV])
                .save(needing(provider, "avaritia", Map.of(S1, "avaritia:refined_coal_block", S2, "avaritia:star_fuel")));
        var rawStar = com.gregtechceu.gtceu.api.GTCEuAPI.materialManager.getMaterial("gtna:raw_star_matter");
        if (rawStar != null && !rawStar.isNull() && rawStar.hasProperty(com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.FLUID))
            EXTRACTOR_RECIPES.recipeBuilder("gtapexadditions:godforge/raw_star_matter")
                    .inputItems(S1)
                    .outputFluids(rawStar.getFluid(250))
                    .duration(100).EUt(VA[UEV])
                    .save(needing(provider, "avaritia", Map.of(S1, "avaritia:star_fuel")));
    }

    /**
     * Aether tie-ins: aerclouds centrifuge into helium (golden ones into helium-3, an early fusion fuel), and
     * enchanted gravitite doubles Gravi Star output.
     */
    private static void aether(Consumer<FinishedRecipe> provider) {
        CENTRIFUGE_RECIPES.recipeBuilder("gtapexadditions:aether/cold_aercloud")
                .inputItems(S1)
                .outputFluids(GTMaterials.Helium.getFluid(250))
                .duration(100).EUt(VA[MV]).save(needing(provider, "aether", Map.of(S1, "aether:cold_aercloud")));
        CENTRIFUGE_RECIPES.recipeBuilder("gtapexadditions:aether/blue_aercloud")
                .inputItems(S1)
                .outputFluids(GTMaterials.Helium.getFluid(500))
                .duration(100).EUt(VA[MV]).save(needing(provider, "aether", Map.of(S1, "aether:blue_aercloud")));
        CENTRIFUGE_RECIPES.recipeBuilder("gtapexadditions:aether/golden_aercloud")
                .inputItems(S1)
                .outputFluids(GTMaterials.Helium3.getFluid(100), GTMaterials.Helium.getFluid(250))
                .duration(200).EUt(VA[HV]).save(needing(provider, "aether", Map.of(S1, "aether:golden_aercloud")));
        AUTOCLAVE_RECIPES.recipeBuilder("gtapexadditions:aether/gravi_star")
                .inputItems(Items.NETHER_STAR)
                .inputItems(S1, 4)
                .inputFluids(GTMaterials.Tritanium.getFluid(L))
                .outputItems(GTItems.GRAVI_STAR, 2)
                .duration(480).EUt(VA[UV]).save(needing(provider, "aether", Map.of(S1, "aether:enchanted_gravitite")));
    }

    /** Angel Ring: flight earned in the Aether (Enchanted Gravitite, Golden Feather) plus the ExU Angel Block. */
    private static void angelRing(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:angel_ring")
                .inputItems(S1, 4)
                .inputItems(S2)
                .inputItems(S3)
                .inputItems(foil, GTMaterials.Gold, 8)
                .inputItems(GTItems.EMITTER_HV, 2)
                .inputItems(CustomTags.HV_CIRCUITS, 2)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 2))
                .outputItems(com.omnicrox.gtaa.common.data.GTAAItems.ANGEL_RING.asStack())
                .duration(600).EUt(VA[HV])
                .save(needing(provider, new String[] { "aether", "extrautilitiesreborn" }, Map.of(
                        S1, "aether:enchanted_gravitite", S2, "aether:golden_feather",
                        S3, "extrautilitiesreborn:angel_block")));
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
        // LV: you'll want crops early
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:mystical_cultivation_array")
                .inputItems(GTBlocks.CASING_STEEL_SOLID.asStack(4))
                .inputItems(GTItems.ELECTRIC_PUMP_LV, 2)
                .inputItems(GTItems.ROBOT_ARM_LV, 2)
                .inputItems(CustomTags.LV_CIRCUITS, 4)
                .inputItems(S3, 4)
                .inputItems(S4, 8)
                .inputFluids(GTMaterials.Tin.getFluid(L * 4))
                .outputItems(GTAAMachines.MYSTICAL_CULTIVATION_ARRAY.asStack())
                .duration(600).EUt(VA[LV])
                .save(needing(provider, "mysticalagriculture", Map.of(
                        S3, "mysticalagriculture:inferium_ingot", S4, "mysticalagriculture:inferium_essence")));
    }

    /**
     * Apex Stargate frame: the final gate. 14 rings (UHV), 9 chevrons (UEV, each built on a ring) and the base (UIV).
     * The world's first finished gate then charges itself with 9.22E18 EU (ApexGateCharge).
     */
    private static void apexGate(Consumer<FinishedRecipe> provider) {
        String[] mods = { "gtna", "draconicevolution" };
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:apex_gate/ring")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(plateDense, GTMaterials.Neutronium, 2)
                .inputItems(S1, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UHV)
                .inputItems(CustomTags.UHV_CIRCUITS, 2)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 4))
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 4))
                .outputItems(GTAABlocks.GATE_RING.asStack())
                .duration(600).EUt(VA[UHV])
                .save(needing(provider, mods, Map.of(S1, "gtna:infinity_ingot")));
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:apex_gate/chevron")
                .inputItems(GTAABlocks.GATE_RING.asStack())
                .inputItems(GTItems.EMITTER_UEV, 2)
                .inputItems(GTItems.SENSOR_UEV)
                .inputItems(CustomTags.UEV_CIRCUITS, 2)
                .inputItems(S1, 8)
                .inputItems(Items.NETHER_STAR, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 8))
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 8))
                .outputItems(GTAABlocks.GATE_CHEVRON.asStack())
                .duration(900).EUt(VA[UEV])
                .save(needing(provider, mods, Map.of(S1, "gtna:infinity_ingot")));
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:apex_gate/base")
                .inputItems(frameGt, GTMaterials.Neutronium, 4)
                .inputItems(S1, 4)
                .inputItems(S2, 2)
                .inputItems(S3, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UIV, 4)
                .inputItems(CustomTags.UIV_CIRCUITS, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 16))
                .outputItems(GTAABlocks.GATE_BASE.asStack())
                .duration(2400).EUt(VA[UIV])
                .save(needing(provider, mods, Map.of(S1, "gtna:infinity_block", S2, "draconicevolution:chaotic_core",
                        S3, "gtna:magmatter_ingot")));
    }

    /**
     * The other final goal (like Twist Space Technology's): Placeable Maxwell's cats. Mars, Poomba, Valenok and
     * Vasilisa are one per tier from UHV to UXV; Maxwell himself needs all four at OpV.
     */
    private static void maxwellCats(Consumer<FinishedRecipe> provider) {
        String[] mods = { "placeablemaxwell", "gtna", "avaritia", "draconicevolution" };
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:maxwell/mars")
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 4)
                .inputItems(GTItems.ROBOT_ARM_UHV, 4)
                .inputItems(GTItems.ELECTRIC_MOTOR_UHV, 4)
                .inputItems(CustomTags.UHV_CIRCUITS, 8)
                .inputItems(S1, 16)
                .inputItems(Items.NETHER_STAR, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 32))
                .outputItems(S10)
                .duration(6000).EUt(VA[UHV])
                .save(needing(provider, mods, Map.of(S1, "gtna:infinity_ingot", S10, "placeablemaxwell:mars")));
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:maxwell/poomba")
                .inputItems(GTItems.FIELD_GENERATOR_UEV, 4)
                .inputItems(GTItems.EMITTER_UEV, 4)
                .inputItems(GTItems.SENSOR_UEV, 4)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(S1, 4)
                .inputItems(S2)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 64))
                .outputItems(S10)
                .duration(8000).EUt(VA[UEV])
                .save(needing(provider, mods, Map.of(S1, "gtna:infinity_block", S2, "draconicevolution:chaotic_core",
                        S10, "placeablemaxwell:poomba")));
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:maxwell/valenok")
                .inputItems(GTItems.FIELD_GENERATOR_UIV, 4)
                .inputItems(GTItems.ROBOT_ARM_UIV, 4)
                .inputItems(CustomTags.UIV_CIRCUITS, 8)
                .inputItems(S1, 4)
                .inputItems(S2)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 64))
                .outputItems(S10)
                .duration(10000).EUt(VA[UIV])
                .save(needing(provider, mods, Map.of(S1, "gtna:magmatter_ingot", S2, "avaritia:eternal_singularity",
                        S10, "placeablemaxwell:valenok")));
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:maxwell/vasilisa")
                .inputItems(GTItems.FIELD_GENERATOR_UXV, 4)
                .inputItems(GTItems.EMITTER_UXV, 4)
                .inputItems(CustomTags.UXV_CIRCUITS, 8)
                .inputItems(S1, 16)
                .inputItems(S2, 4)
                .inputItems(S3, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 96))
                .outputItems(S10)
                .duration(12000).EUt(VA[UXV])
                .save(needing(provider, mods, Map.of(S1, "gtna:magmatter_ingot", S2, "avaritia:eternal_singularity",
                        S3, "avaritia:infinity_ingot", S10, "placeablemaxwell:vasilisa")));
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:maxwell/maxwell")
                .inputItems(S5)
                .inputItems(S6)
                .inputItems(S7)
                .inputItems(S8)
                .inputItems(GTItems.FIELD_GENERATOR_OpV, 8)
                .inputItems(CustomTags.OpV_CIRCUITS, 16)
                .inputItems(S1, 4)
                .inputItems(S2, 16)
                .inputItems(S3, 4)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 144))
                .outputItems(S10)
                .duration(20000).EUt(VA[OpV])
                .save(needing(provider, mods, Map.of(S5, "placeablemaxwell:mars", S6, "placeablemaxwell:poomba",
                        S7, "placeablemaxwell:valenok", S8, "placeablemaxwell:vasilisa", S1, "gtna:magmatter_block",
                        S2, "avaritia:eternal_singularity", S3, "avaritia:infinity_catalyst", S10, "placeablemaxwell:maxwell")));
    }

    /** Mana Bloom Leaves: the Great Mana Tree's canopy in the Mana Nexus */
    private static void manaBloom(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:mana_bloom_leaves")
                .inputItems(net.minecraft.tags.ItemTags.LEAVES, 32)
                .inputItems(S1, 1)
                .inputItems(S2, 2)
                .outputItems(GTAABlocks.MANA_BLOOM_LEAVES.asStack(32))
                .duration(160).EUt(VA[LV])
                .save(needing(provider, "botania", Map.of(S1, "botania:mana_powder", S2, "botania:pink_petal")));
    }
}
