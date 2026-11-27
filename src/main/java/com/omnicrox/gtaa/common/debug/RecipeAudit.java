package com.omnicrox.gtaa.common.debug;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.omnicrox.gtaa.GTAA;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * /gtapexadditions audit: writes gtaa_audit/ in the game folder. used_not_made.txt = items some recipe needs that no
 * recipe makes (worldgen, loot and mob drops are not known here, so those show up too); ore_materials.txt = GT
 * materials with an ore that no vein places; veins.txt = every vein's dimensions and materials; dimensions.txt.
 */
@Mod.EventBusSubscriber(modid = GTAA.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RecipeAudit {

    private RecipeAudit() {}

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gtapexadditions").then(Commands.literal("audit")
                .requires(s -> s.hasPermission(2)).executes(ctx -> run(ctx.getSource()))));
    }

    private static int run(CommandSourceStack src) {
        MinecraftServer server = src.getServer();
        Set<Item> made = new HashSet<>(), used = new HashSet<>();
        Map<Item, Set<String>> usedBy = new HashMap<>();
        int recipes = 0;
        for (Recipe<?> r : server.getRecipeManager().getRecipes()) {
            recipes++;
            String id = r.getId().toString();
            try {
                if (r instanceof GTRecipe g) {
                    collect(g.outputs, made, null, id);
                    collect(g.tickOutputs, made, null, id);
                    collect(g.inputs, used, usedBy, id);
                    collect(g.tickInputs, used, usedBy, id);
                    continue;
                }
                ItemStack out = r.getResultItem(server.registryAccess());
                if (out != null && !out.isEmpty()) made.add(out.getItem());
                for (Ingredient ing : r.getIngredients())
                    for (ItemStack s : ing.getItems()) {
                        used.add(s.getItem());
                        usedBy.computeIfAbsent(s.getItem(), k -> new TreeSet<>()).add(id);
                    }
            } catch (Throwable ignored) {}
        }
        // loot tables (block drops, mob drops, chests) count as a source
        Set<Item> loot = new HashSet<>();
        try {
            var lootData = server.getLootData();
            var gson = net.minecraft.world.level.storage.loot.LootDataType.TABLE.parser();
            java.util.regex.Pattern name = java.util.regex.Pattern.compile("\"name\"\\s*:\\s*\"([a-z0-9_.\\-]+:[a-z0-9_./\\-]+)\"");
            for (ResourceLocation id : lootData.getKeys(net.minecraft.world.level.storage.loot.LootDataType.TABLE)) {
                try {
                    var table = lootData.getElement(new net.minecraft.world.level.storage.loot.LootDataId<>(
                            net.minecraft.world.level.storage.loot.LootDataType.TABLE, id));
                    if (table == null) continue;
                    var m = name.matcher(gson.toJsonTree(table).toString());
                    while (m.find()) {
                        Item it = BuiltInRegistries.ITEM.get(new ResourceLocation(m.group(1)));
                        if (it != null && it != net.minecraft.world.item.Items.AIR) loot.add(it);
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[audit] loot tables not read", t);
        }
        Set<Item> have = new HashSet<>(made);
        have.addAll(loot);
        // recipes with an ingredient that nothing can give (no option is made by a recipe or dropped by loot)
        Map<String, Set<String>> blocked = new TreeMap<>();
        for (Recipe<?> r : server.getRecipeManager().getRecipes()) {
            String id = r.getId().toString();
            try {
                List<List<Item>> groups = new ArrayList<>();
                if (r instanceof GTRecipe g) {
                    groups(g.inputs, groups);
                    groups(g.tickInputs, groups);
                } else for (Ingredient ing : r.getIngredients()) {
                    List<Item> l = new ArrayList<>();
                    for (ItemStack st : ing.getItems()) l.add(st.getItem());
                    groups.add(l);
                }
                for (List<Item> grp : groups) {
                    if (grp.isEmpty() || grp.stream().anyMatch(have::contains)) continue;
                    String key = grp.stream().map(i -> BuiltInRegistries.ITEM.getKey(i).toString()).distinct().limit(4)
                            .reduce((a, b) -> a + " | " + b).orElse("?");
                    blocked.computeIfAbsent(key, k -> new TreeSet<>()).add(id);
                }
            } catch (Throwable ignored) {}
        }
        Path dir = server.getServerDirectory().toPath().resolve("gtaa_audit");
        try {
            Files.createDirectories(dir);
            // items used but never made, by mod
            Map<String, List<String>> byMod = new TreeMap<>();
            for (Item it : used) {
                if (made.contains(it)) continue;
                ResourceLocation k = BuiltInRegistries.ITEM.getKey(it);
                if (k.getNamespace().equals("minecraft")) continue;
                Set<String> ids = usedBy.getOrDefault(it, Set.of());
                String ex = ids.stream().limit(3).reduce((a, b) -> a + ", " + b).orElse("");
                byMod.computeIfAbsent(k.getNamespace(), n -> new ArrayList<>()).add(k + "\t" + ids.size() + "\t" + ex);
            }
            StringBuilder sb = new StringBuilder("# item\trecipes using it\texamples (" + recipes + " recipes scanned)\n");
            byMod.forEach((mod, l) -> {
                Collections.sort(l);
                sb.append("\n## ").append(mod).append(" (").append(l.size()).append(")\n");
                l.forEach(x -> sb.append(x).append('\n'));
            });
            Files.writeString(dir.resolve("used_not_made.txt"), sb);
            StringBuilder bl = new StringBuilder("# ingredient nothing gives (recipe outputs + loot tables)\trecipes blocked\texamples\n");
            blocked.entrySet().stream().sorted((a, b) -> b.getValue().size() - a.getValue().size()).forEach(e -> bl.append(e.getKey())
                    .append('\t').append(e.getValue().size()).append('\t')
                    .append(e.getValue().stream().limit(4).reduce((a, b) -> a + ", " + b).orElse("")).append('\n'));
            Files.writeString(dir.resolve("blocked_recipes.txt"), bl);
            // items with no recipe at all, for our namespaces
            StringBuilder nr = new StringBuilder();
            for (Item it : BuiltInRegistries.ITEM) {
                ResourceLocation k = BuiltInRegistries.ITEM.getKey(it);
                String ns = k.getNamespace();
                if (!(ns.startsWith("gt") || ns.equals("extendedfeatures") || ns.equals("modern_manipulator"))) continue;
                if (!made.contains(it)) nr.append(k).append(used.contains(it) ? "\tUSED" : "").append('\n');
            }
            Files.writeString(dir.resolve("gt_no_recipe.txt"), nr);
            // veins
            Set<String> veinMats = new HashSet<>();
            StringBuilder vs = new StringBuilder();
            for (var e : GTRegistries.ORE_VEINS.entries()) {
                var def = e.getValue();
                List<String> mats = new ArrayList<>();
                try {
                    for (var m : def.veinGenerator().getAllMaterials()) { mats.add(m.getName()); veinMats.add(m.getName()); }
                    for (var b : def.veinGenerator().getAllBlocks()) mats.add(BuiltInRegistries.BLOCK.getKey(b.getBlock()).toString());
                } catch (Throwable ignored) {}
                vs.append(e.getKey()).append('\t').append(def.dimensionFilter()).append('\t')
                        .append(def.layer() == null ? "?" : def.layer().getSerializedName()).append('\t').append(mats).append('\n');
            }
            Files.writeString(dir.resolve("veins.txt"), vs);
            StringBuilder om = new StringBuilder();
            for (var m : GTCEuAPI.materialManager.getRegisteredMaterials())
                if (m.hasProperty(PropertyKey.ORE) && !veinMats.contains(m.getName())) om.append(m.getResourceLocation()).append('\n');
            Files.writeString(dir.resolve("ore_materials_without_vein.txt"), om);
            StringBuilder ds = new StringBuilder();
            for (var k : server.levelKeys()) ds.append(k.location()).append('\n');
            Files.writeString(dir.resolve("dimensions.txt"), ds);
        } catch (IOException e) {
            src.sendFailure(Component.literal("Audit failed: " + e));
            return 0;
        }
        src.sendSuccess(() -> Component.literal("Audit written to " + dir), false);
        return 1;
    }

    private static void groups(Map<?, List<Content>> map, List<List<Item>> out) {
        if (map == null) return;
        List<Content> list = map.get(ItemRecipeCapability.CAP);
        if (list == null) return;
        for (Content c : list) {
            try {
                List<Item> l = new ArrayList<>();
                for (ItemStack s : ItemRecipeCapability.CAP.of(c.content).getItems()) l.add(s.getItem());
                out.add(l);
            } catch (Throwable ignored) {}
        }
    }

    private static void collect(Map<?, List<Content>> map, Set<Item> into, Map<Item, Set<String>> by, String id) {
        if (map == null) return;
        List<Content> list = map.get(ItemRecipeCapability.CAP);
        if (list == null) return;
        for (Content c : list) {
            try {
                Ingredient ing = ItemRecipeCapability.CAP.of(c.content);
                for (ItemStack s : ing.getItems()) {
                    into.add(s.getItem());
                    if (by != null) by.computeIfAbsent(s.getItem(), k -> new TreeSet<>()).add(id);
                }
            } catch (Throwable ignored) {}
        }
    }
}
