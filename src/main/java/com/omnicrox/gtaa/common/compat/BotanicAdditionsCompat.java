package com.omnicrox.gtaa.common.compat;

import com.omnicrox.gtaa.GTAA;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Botanic Additions adds its crafting recipes through HammerLib's RegisterRecipesEvent, where KubeJS can't see them.
 * The Botania gating moves these items to GT assemblers (apex_botania.js), so their crafting-table recipes are
 * taken out here. Pure reflection: no compile dependency on HammerLib.
 */
public final class BotanicAdditionsCompat {

    private BotanicAdditionsCompat() {}

    public static final Set<String> GATED = Set.of("botanicadds:elven_altar", "botanicadds:dreaming_pool",
            "botanicadds:gaia_plate", "botanicadds:mana_tesseract", "botanicadds:terra_catalyst", "botanicadds:gaiasteel_pylon");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void init(IEventBus modBus) {
        Class<? extends Event> cls;
        try {
            cls = (Class<? extends Event>) Class.forName("org.zeith.hammerlib.event.recipe.RegisterRecipesEvent");
        } catch (ClassNotFoundException e) {
            return;
        }
        modBus.addListener(EventPriority.LOWEST, false, (Class) cls, (java.util.function.Consumer<Event>) BotanicAdditionsCompat::onRecipes);
    }

    private static void onRecipes(Event event) {
        try {
            Method get = event.getClass().getMethod("getRecipes");
            Method remove = event.getClass().getMethod("removeRecipe", ResourceLocation.class);
            List<Recipe<?>> hit = new ArrayList<>();
            ((Stream<Recipe<?>>) get.invoke(event)).forEach(r -> {
                ItemStack out;
                try {
                    out = r.getResultItem(RegistryAccess.EMPTY);
                } catch (Exception e) {
                    return;
                }
                if (out.isEmpty()) return;
                if (!GATED.contains(BuiltInRegistries.ITEM.getKey(out.getItem()).toString())) return;
                // recipes that take the item itself (re-attuning a Mana Tesseract) stay
                for (Ingredient ing : r.getIngredients()) if (ing.test(out)) return;
                hit.add(r);
            });
            for (Recipe<?> r : hit) {
                StringBuilder sb = new StringBuilder();
                for (Ingredient ing : r.getIngredients()) {
                    ItemStack[] items = ing.getItems();
                    sb.append(items.length == 0 ? "-" : BuiltInRegistries.ITEM.getKey(items[0].getItem())).append(' ');
                }
                GTAA.LOGGER.info("[GTAA] Botania gating: removed crafting recipe {} ({})", r.getId(), sb.toString().trim());
                remove.invoke(event, r.getId());
            }
        } catch (Exception e) {
            GTAA.LOGGER.warn("[GTAA] Botanic Additions recipe gating failed: {}", e.toString());
        }
    }
}
