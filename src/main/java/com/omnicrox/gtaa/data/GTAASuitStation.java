package com.omnicrox.gtaa.data;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/**
 * Suit Fabrication Station recipes. The suit list (parts, armor pieces, station tier, kit) is generated from
 * FSang's Heroes by tools/hero_kits.py into gtapexadditions/suit_station.json; recipes are built when recipes
 * load, only for suits whose items exist in the pack.
 */
public final class GTAASuitStation {

    private GTAASuitStation() {}

    // tier 1..4: voltage, seconds, circuit tag, plate material
    private static final int[] VOLT = { MV, EV, LuV, UHV };
    private static final int[] SECONDS = { 30, 60, 120, 240 };

    public static void init(Consumer<FinishedRecipe> provider) {
        machines(provider);
        JsonArray suits;
        try (InputStream in = GTAA.class.getResourceAsStream("/gtapexadditions/suit_station.json")) {
            if (in == null) return;
            suits = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonArray();
        } catch (Exception e) {
            GTAA.LOGGER.warn("Suit station list unreadable: {}", e.toString());
            return;
        }
        int made = 0;
        for (JsonElement el : suits) {
            JsonObject s = el.getAsJsonObject();
            if (suit(provider, s)) made++;
        }
        if (made > 0) GTAA.LOGGER.info("[GTAA] Suit Fabrication Station: {} suits", made);
    }

    private static Item item(String id) {
        Item it = BuiltInRegistries.ITEM.get(new ResourceLocation(id));
        return it == Items.AIR ? null : it;
    }

    private static boolean suit(Consumer<FinishedRecipe> provider, JsonObject s) {
        String suit = s.get("suit").getAsString();
        int tier = Math.max(1, Math.min(4, s.get("tier").getAsInt()));
        var builder = GTAARecipeTypes.SUIT_FABRICATION.recipeBuilder(GTAA.id("suit/" + suit.replace(':', '/')));
        for (JsonElement ie : s.getAsJsonArray("ingredients")) {
            JsonObject i = ie.getAsJsonObject();
            int count = i.get("count").getAsInt();
            if (i.has("item")) {
                Item it = item(i.get("item").getAsString());
                if (it == null) return false;
                builder.inputItems(new ItemStack(it, count));
            } else if (i.has("tag")) {
                builder.inputItems(TagKey.create(Registries.ITEM, new ResourceLocation(i.get("tag").getAsString())), count);
            } else return false;
        }
        for (JsonElement ae : s.getAsJsonArray("armor")) {
            Item it = item(ae.getAsString());
            if (it == null) return false;
            builder.outputItems(new ItemStack(it));
        }
        switch (tier) {
            case 1 -> builder.inputItems(CustomTags.MV_CIRCUITS, 1).inputItems(plate, GTMaterials.Steel, 4);
            case 2 -> builder.inputItems(CustomTags.EV_CIRCUITS, 2).inputItems(plate, GTMaterials.Titanium, 4);
            case 3 -> builder.inputItems(CustomTags.LuV_CIRCUITS, 2).inputItems(plate, GTMaterials.RhodiumPlatedPalladium, 4);
            default -> builder.inputItems(CustomTags.UHV_CIRCUITS, 2).inputItems(plate, GTMaterials.Neutronium, 4);
        }
        if (s.has("circuit")) builder.circuitMeta(s.get("circuit").getAsInt());
        builder.addData("station_tier", tier)
                .addData("suit_kit", s.has("kit") ? s.get("kit").getAsString() : "")
                .addData("suit_origin", s.has("origin") && !s.get("origin").isJsonNull() ? s.get("origin").getAsString() : "");
        builder.duration(SECONDS[tier - 1] * 20).EUt(VA[VOLT[tier - 1]]).save(provider);
        return true;
    }

    private static void machines(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:suit_fabrication_station")
                .inputItems(GTAABlocks.SUIT_CASING_T1.asStack(4))
                .inputItems(GTItems.ROBOT_ARM_MV, 2)
                .inputItems(GTItems.SENSOR_MV, 1)
                .inputItems(CustomTags.MV_CIRCUITS, 4)
                .inputItems(Items.LOOM)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 4))
                .outputItems(GTAAMachines.SUIT_FABRICATION_STATION.asStack())
                .duration(600).EUt(VA[MV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:suit_casing_t1")
                .inputItems(plate, GTMaterials.Steel, 4).inputItems(plate, GTMaterials.Aluminium, 2)
                .inputItems(frameGt, GTMaterials.Steel).circuitMeta(13).outputItems(GTAABlocks.SUIT_CASING_T1.asStack(2))
                .duration(100).EUt(VA[MV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:suit_casing_t2")
                .inputItems(GTAABlocks.SUIT_CASING_T1.asStack()).inputItems(plate, GTMaterials.Titanium, 4)
                .inputItems(GTItems.EMITTER_EV).outputItems(GTAABlocks.SUIT_CASING_T2.asStack())
                .duration(200).EUt(VA[EV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:suit_casing_t3")
                .inputItems(GTAABlocks.SUIT_CASING_T2.asStack()).inputItems(plate, GTMaterials.RhodiumPlatedPalladium, 4)
                .inputItems(GTItems.FIELD_GENERATOR_LuV).outputItems(GTAABlocks.SUIT_CASING_T3.asStack())
                .duration(300).EUt(VA[LuV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:suit_casing_t4")
                .inputItems(GTAABlocks.SUIT_CASING_T3.asStack()).inputItems(plate, GTMaterials.Neutronium, 4)
                .inputItems(GTItems.FIELD_GENERATOR_UHV).outputItems(GTAABlocks.SUIT_CASING_T4.asStack())
                .duration(400).EUt(VA[UHV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder("gtapexadditions:suit_monitor")
                .inputItems(GTItems.COVER_SCREEN).inputItems(plate, GTMaterials.Aluminium, 2)
                .inputItems(CustomTags.MV_CIRCUITS, 1).outputItems(GTAABlocks.SUIT_MONITOR.asStack(2))
                .duration(100).EUt(VA[MV]).save(provider);
    }
}
