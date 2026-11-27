package com.omnicrox.apexlib.multiblock;

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
 * Big structures kept as JSON (too large for Java constants), read from {@code /<namespace>/structures/<file>.json}.
 * Layout is GTNH StructureLib's: layers front-to-back, rows top-to-bottom, characters left-to-right; '~' is the
 * controller. Porting a GTNH machine is copying its {@code shapeMain} arrays into a JSON file.
 */
public final class ApexStructures {

    private static final Map<String, String[][]> CACHE = new HashMap<>();

    private ApexStructures() {}

    public static synchronized String[][] get(String namespace, String file, String key) {
        return CACHE.computeIfAbsent(namespace + ":" + file + "#" + key, k -> {
            String path = "/" + namespace + "/structures/" + file + ".json";
            try (InputStream in = ApexStructures.class.getResourceAsStream(path)) {
                if (in == null) throw new IllegalStateException("missing structure file " + path);
                JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
                JsonArray layers = root.getAsJsonArray(key);
                if (layers == null) throw new IllegalStateException("no shape '" + key + "' in " + path);
                String[][] out = new String[layers.size()][];
                for (int z = 0; z < layers.size(); z++) {
                    JsonArray rows = layers.get(z).getAsJsonArray();
                    out[z] = new String[rows.size()];
                    for (int y = 0; y < rows.size(); y++) out[z][y] = rows.get(y).getAsString();
                }
                return out;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load structure " + path + "#" + key, e);
            }
        });
    }

    /** A pattern builder pre-filled with the shape; callers add the {@code where} clauses. */
    public static FactoryBlockPattern pattern(String namespace, String file, String key) {
        FactoryBlockPattern pattern = FactoryBlockPattern.start(RelativeDirection.LEFT, RelativeDirection.DOWN,
                RelativeDirection.BACK);
        for (String[] layer : get(namespace, file, key)) pattern.aisle(layer);
        return pattern;
    }

    /** The same from a GTNH-style {@code String[][]} written in Java. */
    public static FactoryBlockPattern pattern(String[][] shape) {
        FactoryBlockPattern pattern = FactoryBlockPattern.start(RelativeDirection.LEFT, RelativeDirection.DOWN,
                RelativeDirection.BACK);
        for (String[] layer : shape) pattern.aisle(layer);
        return pattern;
    }
}
