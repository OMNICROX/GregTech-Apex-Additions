package com.omnicrox.gtaa.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Big structures are kept as JSON (too large for Java constants). Layout is GTNH StructureLib's:
 * layers front-to-back, rows top-to-bottom, characters left-to-right. '~' is the controller.
 */
public final class GTAAStructures {

    private static final Map<String, String[][]> CACHE = new HashMap<>();

    private GTAAStructures() {}

    public static synchronized String[][] get(String file, String key) {
        return CACHE.computeIfAbsent(file + "#" + key, k -> {
            try (InputStream in = GTAAStructures.class.getResourceAsStream("/gtapexadditions/structures/" + file + ".json")) {
                if (in == null) throw new IllegalStateException("missing structure file " + file);
                JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
                JsonArray layers = root.getAsJsonArray(key);
                String[][] out = new String[layers.size()][];
                for (int z = 0; z < layers.size(); z++) {
                    JsonArray rows = layers.get(z).getAsJsonArray();
                    out[z] = new String[rows.size()];
                    for (int y = 0; y < rows.size(); y++) out[z][y] = rows.get(y).getAsString();
                }
                return out;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load structure " + file + "#" + key, e);
            }
        });
    }

    /** A pattern builder pre-filled with the shape; callers add the {@code where} clauses. */
    public static FactoryBlockPattern pattern(String file, String key) {
        FactoryBlockPattern pattern = FactoryBlockPattern.start(RelativeDirection.LEFT, RelativeDirection.DOWN,
                RelativeDirection.BACK);
        for (String[] layer : get(file, key)) pattern.aisle(layer);
        return pattern;
    }
}
