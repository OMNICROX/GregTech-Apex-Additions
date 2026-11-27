package com.omnicrox.apexguide.internal.home;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Per-player guide state that survives restarts: bookmarks, recently visited pages and the navigation pin. Stored in
 * {@code config/apexguide/userdata.json}. Ported in spirit from GuideNH's GuideBookmarkStore / GuideScreenHomeHistory
 * (LGPL-3.0, HFstudio).
 */
public final class GuideUserData {
    private static final Logger LOG = LoggerFactory.getLogger(GuideUserData.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int HISTORY_LIMIT = 20;

    private static final Map<ResourceLocation, Set<ResourceLocation>> BOOKMARKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<ResourceLocation>> HISTORY = new LinkedHashMap<>();
    private static boolean navPinned = true;
    private static boolean loaded;
    private static int version;

    private GuideUserData() {
    }

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("apexguide").resolve("userdata.json");
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        var path = file();
        if (!Files.isRegularFile(path)) {
            return;
        }
        try {
            var root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            if (root.has("navPinned")) {
                navPinned = root.get("navPinned").getAsBoolean();
            }
            readMap(root, "bookmarks", (guide, page) -> BOOKMARKS.computeIfAbsent(guide, k -> new LinkedHashSet<>())
                    .add(page));
            readMap(root, "history", (guide, page) -> HISTORY.computeIfAbsent(guide, k -> new ArrayList<>()).add(page));
        } catch (Exception e) {
            LOG.warn("Failed to read {}", path, e);
        }
    }

    private interface PairConsumer {
        void accept(ResourceLocation guide, ResourceLocation page);
    }

    private static void readMap(JsonObject root, String key, PairConsumer consumer) {
        if (!root.has(key)) {
            return;
        }
        for (var entry : root.getAsJsonObject(key).entrySet()) {
            var guide = ResourceLocation.tryParse(entry.getKey());
            if (guide == null) {
                continue;
            }
            for (var el : entry.getValue().getAsJsonArray()) {
                var page = ResourceLocation.tryParse(el.getAsString());
                if (page != null) {
                    consumer.accept(guide, page);
                }
            }
        }
    }

    private static void save() {
        version++;
        var root = new JsonObject();
        root.addProperty("navPinned", navPinned);
        root.add("bookmarks", writeMap(BOOKMARKS));
        root.add("history", writeMap(HISTORY));
        var path = file();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.warn("Failed to write {}", path, e);
        }
    }

    private static JsonObject writeMap(Map<ResourceLocation, ? extends java.util.Collection<ResourceLocation>> map) {
        var obj = new JsonObject();
        for (var entry : map.entrySet()) {
            var arr = new JsonArray();
            entry.getValue().forEach(id -> arr.add(id.toString()));
            obj.add(entry.getKey().toString(), arr);
        }
        return obj;
    }

    /** Changes whenever bookmarks or history change; lets views cache derived data. */
    public static int version() {
        ensureLoaded();
        return version;
    }

    public static boolean isBookmarked(ResourceLocation guide, ResourceLocation page) {
        ensureLoaded();
        var set = BOOKMARKS.get(guide);
        return set != null && set.contains(page);
    }

    public static void toggleBookmark(ResourceLocation guide, ResourceLocation page) {
        ensureLoaded();
        var set = BOOKMARKS.computeIfAbsent(guide, k -> new LinkedHashSet<>());
        if (!set.remove(page)) {
            set.add(page);
        }
        save();
    }

    public static List<ResourceLocation> bookmarks(ResourceLocation guide) {
        ensureLoaded();
        return List.copyOf(BOOKMARKS.getOrDefault(guide, Collections.emptySet()));
    }

    public static void recordVisit(ResourceLocation guide, ResourceLocation page) {
        ensureLoaded();
        var list = HISTORY.computeIfAbsent(guide, k -> new ArrayList<>());
        if (!list.isEmpty() && list.get(0).equals(page)) {
            return;
        }
        list.remove(page);
        list.add(0, page);
        while (list.size() > HISTORY_LIMIT) {
            list.remove(list.size() - 1);
        }
        save();
    }

    public static List<ResourceLocation> history(ResourceLocation guide) {
        ensureLoaded();
        return List.copyOf(HISTORY.getOrDefault(guide, Collections.emptyList()));
    }

    public static boolean isNavPinned() {
        ensureLoaded();
        return navPinned;
    }

    public static void setNavPinned(boolean pinned) {
        ensureLoaded();
        if (navPinned != pinned) {
            navPinned = pinned;
            save();
        }
    }
}
