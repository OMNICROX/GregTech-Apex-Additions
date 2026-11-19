package com.omnicrox.gtaa.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.data.recipe.misc.ComponentRecipes;
import com.omnicrox.gtaa.GTAA;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Component Assembly Line recipes, generated from GTCEu's own component recipes using GoodGenerator's
 * CoAL conversion rules: every input x48, output 64; single..octal wires / cables become 16x,
 * plates become dense plates, rods become long rods, small gears become gears (rounded down).
 */
public final class GTAAComponentAssembly {

    private GTAAComponentAssembly() {}

    private static final Pattern COMPONENT = Pattern.compile(
            "gtceu:(lv|mv|hv|ev|iv|luv|zpm|uv|uhv|uev|uiv|uxv|opv)_(electric_motor|electric_piston|electric_pump|conveyor_module|robot_arm|emitter|sensor|field_generator)");
    private static final Pattern WIRE = Pattern.compile("(.+)_(single|double|quadruple|octal)_(wire|cable)");
    private static final int MULT = 48, OUT = 64, DURATION = 48 * 20;

    private record Candidate(ResourceLocation id, JsonObject json, boolean machine) {}

    public static void init(Consumer<FinishedRecipe> provider) {
        Map<String, List<Candidate>> byOutput = new LinkedHashMap<>();
        try {
            ComponentRecipes.init(fr -> {
                JsonObject j;
                try {
                    j = fr.serializeRecipe();
                } catch (Throwable t) {
                    return;
                }
                String out = outputId(j);
                if (out == null || !COMPONENT.matcher(out).matches()) return;
                String type = j.has("type") ? j.get("type").getAsString() : "";
                boolean machine = type.equals("gtceu:assembler") || type.equals("gtceu:assembly_line");
                boolean crafting = type.equals("minecraft:crafting_shaped");
                if (!machine && !crafting) return;
                byOutput.computeIfAbsent(out, k -> new ArrayList<>()).add(new Candidate(fr.getId(), j, machine));
            });
        } catch (Throwable t) {
            GTAA.LOGGER.error("[GTAA] could not read GTCEu component recipes for the Component Assembly Line", t);
            return;
        }
        int made = 0;
        for (var e : byOutput.entrySet()) {
            List<Candidate> list = e.getValue();
            boolean anyMachine = list.stream().anyMatch(Candidate::machine);
            Matcher m = COMPONENT.matcher(e.getKey());
            if (!m.matches()) continue;
            int tier = Arrays.asList(GTValues.VN).stream().map(s -> s.toLowerCase(Locale.ROOT)).toList().indexOf(m.group(1));
            int n = 0;
            for (Candidate c : list) {
                if (anyMachine && !c.machine()) continue;
                JsonObject recipe = c.machine() ? fromMachine(c.json(), e.getKey(), tier) : fromCrafting(c.json(), e.getKey(), tier);
                if (recipe == null) continue;
                String path = e.getKey().substring("gtceu:".length()) + (n == 0 ? "" : "_" + n);
                n++;
                provider.accept(wrap(GTAA.id("component_assembly_line/" + path), recipe));
                made++;
            }
        }
        GTAA.LOGGER.info("[GTAA] generated {} Component Assembly Line recipes", made);
    }

    @Nullable
    private static String outputId(JsonObject j) {
        try {
            if (j.has("result")) {
                JsonElement r = j.get("result");
                return r.isJsonObject() ? r.getAsJsonObject().get("item").getAsString() : r.getAsString();
            }
            if (j.has("outputs")) {
                JsonArray items = j.getAsJsonObject("outputs").getAsJsonArray("item");
                if (items == null || items.isEmpty()) return null;
                JsonObject content = items.get(0).getAsJsonObject().getAsJsonObject("content");
                JsonObject ing = content.has("ingredient") ? content.getAsJsonObject("ingredient") : content;
                return ing.has("item") ? ing.get("item").getAsString() : null;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    // ---------------- conversion ----------------

    private record Converted(JsonObject ingredient, int count) {}

    private static Converted convert(JsonObject ing, int count) {
        if (ing.has("tag")) {
            String tag = ing.get("tag").getAsString();
            Converted c = convertTag(tag, count, "forge:plates/", TagPrefix.plateDense, "forge:dense_plates/", 9);
            if (c == null) c = convertTag(tag, count, "forge:small_gears/", TagPrefix.gear, "forge:gears/", 4);
            if (c == null && !tag.startsWith("forge:rods/long/"))
                c = convertTag(tag, count, "forge:rods/", TagPrefix.rodLong, "forge:rods/long/", 2);
            if (c != null) return c;
        } else if (ing.has("item")) {
            String item = ing.get("item").getAsString();
            Matcher w = WIRE.matcher(item);
            if (w.matches()) {
                int per = switch (w.group(2)) {
                    case "single" -> 1;
                    case "double" -> 2;
                    case "quadruple" -> 4;
                    default -> 8;
                };
                String hex = w.group(1) + "_hex_" + w.group(3);
                if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(hex))) {
                    JsonObject o = new JsonObject();
                    o.addProperty("item", hex);
                    return new Converted(o, Math.max(1, count * MULT * per / 16));
                }
            }
        }
        return new Converted(ing, count * MULT);
    }

    @Nullable
    private static Converted convertTag(String tag, int count, String from, TagPrefix to, String toTag, int ratio) {
        if (!tag.startsWith(from)) return null;
        String matName = tag.substring(from.length());
        Material mat = GTCEuAPI.materialManager.getMaterial("gtceu:" + matName);
        if (mat == null) mat = GTCEuAPI.materialManager.getMaterial(matName);
        if (mat == null || ChemicalHelper.get(to, mat).isEmpty()) return null;
        JsonObject o = new JsonObject();
        o.addProperty("tag", toTag + matName);
        return new Converted(o, Math.max(1, count * MULT / ratio));
    }

    private static JsonObject sized(JsonObject ingredient, int count) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "gtceu:sized");
        c.addProperty("count", count);
        c.add("ingredient", ingredient);
        JsonObject entry = new JsonObject();
        entry.add("content", c);
        return entry;
    }

    private static JsonObject base(String output, int tier, JsonElement eu, JsonArray items, JsonArray fluids) {
        JsonObject r = new JsonObject();
        r.addProperty("type", GTAA.MOD_ID + ":component_assembly_line");
        r.addProperty("category", GTAA.MOD_ID + ":component_assembly_line");
        r.addProperty("duration", DURATION);
        JsonObject data = new JsonObject();
        data.addProperty("coal_casing_tier", tier);
        r.add("data", data);
        JsonObject tick = new JsonObject();
        JsonArray euArr = new JsonArray();
        JsonObject euEntry = new JsonObject();
        euEntry.add("content", eu);
        euArr.add(euEntry);
        tick.add("eu", euArr);
        r.add("tickInputs", tick);
        JsonObject inputs = new JsonObject();
        inputs.add("item", items);
        if (fluids != null && !fluids.isEmpty()) inputs.add("fluid", fluids);
        r.add("inputs", inputs);
        JsonObject outItem = new JsonObject();
        outItem.addProperty("item", output);
        JsonObject outputs = new JsonObject();
        JsonArray outArr = new JsonArray();
        outArr.add(sized(outItem, OUT));
        outputs.add("item", outArr);
        r.add("outputs", outputs);
        return r;
    }

    @Nullable
    private static JsonObject fromCrafting(JsonObject j, String output, int tier) {
        try {
            JsonObject key = j.getAsJsonObject("key");
            Map<Character, Integer> counts = new LinkedHashMap<>();
            for (JsonElement row : j.getAsJsonArray("pattern"))
                for (char ch : row.getAsString().toCharArray()) if (ch != ' ') counts.merge(ch, 1, Integer::sum);
            Map<String, Converted> merged = new LinkedHashMap<>();
            for (var e : counts.entrySet()) {
                JsonElement ingEl = key.get(String.valueOf(e.getKey()));
                if (ingEl == null) continue;
                JsonObject ing = ingEl.isJsonArray() ? ingEl.getAsJsonArray().get(0).getAsJsonObject() : ingEl.getAsJsonObject();
                if (ing.has("type") && !ing.get("type").getAsString().equals("forge:item")) {
                    // tool / nbt ingredient: crafting tools are not consumed, skip
                    if (ing.get("type").getAsString().contains("tool")) continue;
                }
                if (ing.has("item") && ing.get("item").getAsString().contains("_wrench")) continue;
                Converted c = convert(ing, e.getValue());
                merged.merge(c.ingredient().toString(), c, (a, b) -> new Converted(a.ingredient(), a.count() + b.count()));
            }
            JsonArray items = new JsonArray();
            for (Converted c : merged.values()) items.add(sized(c.ingredient(), c.count()));
            if (items.isEmpty() || items.size() > 16) return null;
            long eut = GTValues.VA[Math.max(GTValues.ULV, tier - 1)];
            return base(output, tier, new com.google.gson.JsonPrimitive(eut), items, null);
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    private static JsonObject fromMachine(JsonObject j, String output, int tier) {
        try {
            JsonObject inputs = j.getAsJsonObject("inputs");
            JsonArray items = new JsonArray();
            Map<String, Converted> merged = new LinkedHashMap<>();
            for (JsonElement el : inputs.getAsJsonArray("item")) {
                JsonObject content = el.getAsJsonObject().getAsJsonObject("content");
                int count = content.has("count") ? content.get("count").getAsInt() : 1;
                JsonObject ing = content.has("ingredient") ? content.getAsJsonObject("ingredient") : content;
                if (el.getAsJsonObject().has("chance") && el.getAsJsonObject().get("chance").getAsInt() == 0) continue;
                Converted c = convert(ing, count);
                merged.merge(c.ingredient().toString(), c, (a, b) -> new Converted(a.ingredient(), a.count() + b.count()));
            }
            for (Converted c : merged.values()) items.add(sized(c.ingredient(), c.count()));
            JsonArray fluids = new JsonArray();
            if (inputs.has("fluid")) {
                for (JsonElement el : inputs.getAsJsonArray("fluid")) {
                    JsonObject copy = el.getAsJsonObject().deepCopy();
                    JsonObject content = copy.getAsJsonObject("content");
                    if (content != null && content.has("amount"))
                        content.addProperty("amount", content.get("amount").getAsLong() * MULT);
                    fluids.add(copy);
                }
            }
            if (items.isEmpty() || items.size() > 16 || fluids.size() > 6) return null;
            JsonElement eu = j.getAsJsonObject("tickInputs").getAsJsonArray("eu").get(0).getAsJsonObject().get("content");
            return base(output, tier, eu.deepCopy(), items, fluids);
        } catch (Throwable t) {
            return null;
        }
    }

    static FinishedRecipe wrap(ResourceLocation id, JsonObject json) {
        return new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject out) {
                for (String k : json.keySet()) if (!k.equals("type")) out.add(k, json.get(k));
            }

            @Override
            public JsonObject serializeRecipe() {
                return json.deepCopy();
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return GTRecipeSerializer.SERIALIZER;
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        };
    }
}
