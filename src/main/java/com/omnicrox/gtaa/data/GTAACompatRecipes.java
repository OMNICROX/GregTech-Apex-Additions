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
        alfheimArray(provider);
        draconicFusionCrafting(provider);
        matterFabricator(provider);
        godforgeStarter(provider);
        missingSources(provider);
        topCircuits(provider);
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
        // the pedestal under the gate (at least 4) and its controller: the last things to build
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:apex_gate/pedestal")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(S1, 6)
                .inputItems(GTItems.FIELD_GENERATOR_UXV)
                .inputItems(CustomTags.UXV_CIRCUITS, 2)
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 4))
                .outputItems(GTAABlocks.GATE_PEDESTAL.asStack())
                .duration(600).EUt(VA[UXV])
                .save(needing(provider, mods, Map.of(S1, "gtna:magmatter_ingot")));
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:apex_gate/controller")
                .inputItems(frameGt, GTMaterials.Neutronium, 4)
                .inputItems(S1)
                .inputItems(S2)
                .inputItems(S3, 2)
                .inputItems(GTItems.FIELD_GENERATOR_OpV, 4)
                .inputItems(GTItems.EMITTER_OpV, 2)
                .inputItems(GTItems.SENSOR_OpV, 2)
                .inputItems(CustomTags.OpV_CIRCUITS, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 64))
                .outputItems(GTAAMachines.ASCENSION_GATE.asStack())
                .duration(6000).EUt(VA[OpV])
                .save(needing(provider, new String[] { "gtna", "draconicevolution", "avaritia" }, Map.of(
                        S1, "gtna:magmatter_block", S2, "avaritia:eternal_singularity", S3, "draconicevolution:chaotic_core")));
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
    /**
     * The Forge of the Gods chain had no start: Celestial Tungsten Plasma only came from the Forge's own Plasma module,
     * and building the Forge needs Creon, which needs that plasma. Two starter routes, both slower than the Godforge:
     * the Dimensionally Transcendent Plasma Forge (UIV, 1 min) and Fusion Reactor MK V (UEV, 5 min, 10B EU to start).
     */
    private static void godforgeStarter(Consumer<FinishedRecipe> provider) {
        var mat = com.gregtechceu.gtceu.api.GTCEuAPI.materialManager.getMaterial("gtna:celestial_tungsten");
        if (mat == null || mat.isNull()) return;
        var plasma = mat.getFluid(com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys.PLASMA, 144);
        var molten = mat.getFluid(144);
        var dtpf = com.gregtechceu.gtceu.api.registry.GTRegistries.RECIPE_TYPES.get(
                new net.minecraft.resources.ResourceLocation("gtna", "plasma_forge"));
        if (dtpf != null && plasma != null && !plasma.isEmpty())
            dtpf.recipeBuilder("gtapexadditions:godforge_starter/celestial_tungsten_plasma")
                    .inputItems(dust, mat)
                    .inputFluids(GTMaterials.Neutronium.getFluid(144))
                    .outputFluids(plasma)
                    .duration(1200).EUt(VA[UIV]).blastFurnaceTemp(10800)
                    .save(provider);
        if (plasma != null && !plasma.isEmpty() && molten != null && !molten.isEmpty())
            FUSION_RECIPES.recipeBuilder("gtapexadditions:godforge_starter/celestial_tungsten_plasma_fusion")
                    .inputFluids(molten)
                    .inputFluids(GTMaterials.Helium3.getFluid(500))
                    .outputFluids(plasma)
                    .duration(6000).EUt(VA[UEV]).fusionStartEU(10_000_000_000L)
                    .save(provider);
    }

    /** sources for the elements GTAA gave dust forms (Rhenium, Erbium, Praseodymium) */
    private static void missingSources(Consumer<FinishedRecipe> provider) {
        CHEMICAL_RECIPES.recipeBuilder("gtapexadditions:missing/rhenium_from_molybdenite")
                .inputItems(dust, GTMaterials.Molybdenite, 8)
                .inputFluids(GTMaterials.Oxygen.getFluid(1000))
                .outputItems(dust, GTMaterials.Rhenium)
                .outputItems(dust, GTMaterials.Molybdenum, 4)
                .outputFluids(GTMaterials.SulfurDioxide.getFluid(1000))
                .duration(400).EUt(VA[IV]).save(provider);
        ELECTROLYZER_RECIPES.recipeBuilder("gtapexadditions:missing/rare_earth_split")
                .inputItems(dust, GTMaterials.RareEarth, 8)
                .outputItems(dust, GTMaterials.Erbium)
                .outputItems(dust, GTMaterials.Praseodymium)
                .outputItems(dust, GTMaterials.Neodymium, 2)
                .outputItems(dust, GTMaterials.Lanthanum, 2)
                .duration(600).EUt(VA[EV]).save(provider);
    }

    /**
     * There was no real OpV or MAX circuit (the only one was Extended Features' Universal Circuit, made from itself).
     * OpV: Space Assembler from UXV circuits and UXV parts; MAX: from OpV circuits and OpV parts.
     */
    private static void topCircuits(Consumer<FinishedRecipe> provider) {
        Item opv = BuiltInRegistries.ITEM.get(new net.minecraft.resources.ResourceLocation("extendedfeatures", "opv_universal_circuit"));
        if (opv != Items.AIR)
            com.omnicrox.gtaa.common.data.GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:circuits/opv")
                    .inputItems(CustomTags.UXV_CIRCUITS, 2)
                    .inputItems(GTItems.EMITTER_UXV.asStack(2))
                    .inputItems(GTItems.SENSOR_UXV.asStack(2))
                    .inputItems(foil, GTMaterials.Neutronium, 16)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(2304))
                    .outputItems(new net.minecraft.world.item.ItemStack(opv))
                    .duration(1200).EUt(VA[UXV]).save(provider);
        // nothing in the pack made a MAX circuit (extendedfeatures stops at OpV): GTAA's own, for the MAX hatches
        com.omnicrox.gtaa.common.data.GTAARecipeTypes.SPACE_ASSEMBLER.recipeBuilder("gtapexadditions:circuits/max")
                    .inputItems(CustomTags.OpV_CIRCUITS, 2)
                    .inputItems(GTItems.EMITTER_OpV.asStack(2))
                    .inputItems(GTItems.SENSOR_OpV.asStack(2))
                    .inputItems(foil, GTMaterials.Neutronium, 64)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(4608))
                    .outputItems(com.omnicrox.gtaa.common.data.GTAAItems.MAX_CIRCUIT.asStack())
                    .duration(2400).EUt(VA[OpV]).save(provider);
    }


    /** GT-Not-Leisure's Alfheim Teleportation Array, brought down to IV for this pack (GTNL: ZPM assembly line) */
    private static void alfheimArray(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:alfheim_teleportation_array")
                .inputItems(com.gregtechceu.gtceu.common.data.GTMachines.HULL[IV].asStack(4))
                .inputItems(S1, 4)
                .inputItems(S2, 4)
                .inputItems(S3, 4)
                .inputItems(S4, 4)
                .inputItems(S5, 8)
                .inputItems(GTItems.SENSOR_IV, 4)
                .inputItems(GTItems.FIELD_GENERATOR_IV, 4)
                .inputItems(CustomTags.IV_CIRCUITS, 8)
                .inputItems(GTBlocks.HIGH_POWER_CASING.asStack(16))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                .outputItems(com.omnicrox.gtaa.common.data.GTAAGTNHMachines.ALFHEIM_TELEPORTATION_ARRAY.asStack())
                .duration(1200).EUt(VA[IV])
                .save(needing(provider, "botania", Map.of(
                        S1, "botania:alfheim_portal", S2, "botania:runic_altar", S3, "botania:mana_pool",
                        S4, "botania:natura_pylon", S5, "botania:mana_spreader")));
    }

    /** GT-Not-Leisure's Draconic Fusion Crafting: their UMV recipe brought to UHV for this pack */
    private static void draconicFusionCrafting(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:draconic_fusion_crafting")
                .inputItems(GTAAMachines.DRACONIC_FUSION_CRAFTER.asStack())
                .inputItems(plateDense, GTMaterials.Neutronium, 4)
                .inputItems(GTItems.EMITTER_UHV, 16)
                .inputItems(GTItems.FIELD_GENERATOR_UHV, 16)
                .inputItems(S1, 32)
                .inputItems(CustomTags.UHV_CIRCUITS, 16)
                .inputItems(S2)
                .inputItems(S3)
                .inputItems(GTAABlocks.RADIATION_ABSORBENT_CASING.asStack(32))
                .inputFluids(GTMaterials.Neutronium.getFluid(L * 64))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 128))
                .outputItems(com.omnicrox.gtaa.common.data.GTAAGTNHMachines.DRACONIC_FUSION_CRAFTING.asStack())
                .duration(2400).EUt(VA[UHV])
                .save(needing(provider, "draconicevolution", Map.of(S1, "draconicevolution:draconic_core",
                        S2, "draconicevolution:dragon_heart", S3, "draconicevolution:chaos_shard")));
    }

    /** GT-Not-Leisure's Matter Fabricator (ZPM) */
    private static void matterFabricator(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder("gtapexadditions:matter_fabricator")
                .inputItems(com.gregtechceu.gtceu.common.data.GTMachines.HULL[ZPM].asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_ZPM, 8)
                .inputItems(GTItems.ELECTRIC_PUMP_ZPM, 8)
                .inputItems(CustomTags.ZPM_CIRCUITS, 8)
                .inputItems(GTBlocks.SUPERCONDUCTING_COIL.asStack(16))
                .inputItems(GTBlocks.FUSION_CASING.asStack(8))
                .inputItems(frameGt, GTMaterials.Naquadria, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .outputItems(com.omnicrox.gtaa.common.data.GTAAGTNHMachines.MATTER_FABRICATOR.asStack())
                .duration(1600).EUt(VA[ZPM])
                .save(provider);
    }
}
