package com.omnicrox.gtaa.common.machine.multiblock.suit;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.CalculationStrategy;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEStockingBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.machine.multiblock.SuitFabricationStationMachine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Future;

/**
 * "Fabricate" for a station with an ME Input Bus (only loaded when AE2 is installed): sets the bus to pull the
 * suit's parts from the network, and asks the network's crafting CPUs for any part that is short and has a
 * pattern. The job is submitted once AE2 finishes planning it.
 */
public final class SuitStationME {

    private SuitStationME() {}

    private record Pending(Future<ICraftingPlan> plan, IActionSource source, String name) {}

    private static final Map<SuitFabricationStationMachine, List<Pending>> PENDING = new WeakHashMap<>();
    private static Field itemHandlerField;

    /** Returns the status line, or null when the station has no ME Input Bus. */
    public static String fabricate(SuitFabricationStationMachine m, ServerPlayer player, GTRecipe recipe, int circuit) {
        MEInputBusPartMachine bus = null;
        for (IMultiPart part : m.getParts()) if (part instanceof MEInputBusPartMachine b) { bus = b; break; }
        if (bus == null) return null;
        IGrid grid = bus.getMainNode().getGrid();
        if (grid == null || !bus.isOnline()) return "§cME Input Bus is offline";

        KeyCounter stock = grid.getStorageService().getCachedInventory();
        ICraftingService crafting = grid.getCraftingService();
        IActionSource source = IActionSource.ofPlayer(player);
        List<GenericStack> configs = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        int requested = 0;

        for (Content c : recipe.inputs.getOrDefault(ItemRecipeCapability.CAP, List.of())) {
            if (c.content instanceof IntCircuitIngredient || c.chance == 0) continue;
            Ingredient ing = ItemRecipeCapability.CAP.of(c.content);
            long amount = c.content instanceof SizedIngredient s ? s.getAmount() : 1;
            AEItemKey best = null;
            long have = 0;
            for (var e : stock) {
                if (e.getKey() instanceof AEItemKey k && e.getLongValue() > have && ing.test(k.toStack())) {
                    best = k;
                    have = e.getLongValue();
                }
            }
            if (have < amount) {
                AEItemKey craft = best != null && crafting.isCraftable(best) ? best : null;
                if (craft == null) {
                    for (AEKey k : crafting.getCraftables(k -> k instanceof AEItemKey ik && ing.test(ik.toStack()))) {
                        craft = (AEItemKey) k;
                        break;
                    }
                }
                if (craft != null) {
                    long n = amount - have;
                    Future<ICraftingPlan> plan = crafting.beginCraftingCalculation(m.getLevel(), () -> source, craft, n,
                            CalculationStrategy.REPORT_MISSING_ITEMS);
                    PENDING.computeIfAbsent(m, x -> new ArrayList<>()).add(new Pending(plan, source,
                            n + "x " + craft.toStack().getHoverName().getString()));
                    requested++;
                    if (best == null) best = craft;
                } else {
                    var items = ing.getItems();
                    missing.add((amount - have) + "x " + (items.length > 0 ? items[0].getHoverName().getString() : "?"));
                }
            }
            if (best == null) {
                var items = ing.getItems();
                if (items.length > 0) best = AEItemKey.of(items[0]);
            }
            if (best != null) configs.add(new GenericStack(best, amount));
        }

        boolean autoPull = bus instanceof MEStockingBusPartMachine s && s.isAutoPull();
        if (!autoPull) configure(bus, configs);
        if (circuit > 0) bus.getCircuitInventory().setStackInSlot(0, IntCircuitBehaviour.stack(circuit));

        if (!missing.isEmpty())
            return "§cNo pattern for: " + String.join(", ", missing.subList(0, Math.min(3, missing.size()))) +
                    (missing.size() > 3 ? " +" + (missing.size() - 3) : "");
        if (requested > 0) return "§ePlanning " + requested + " autocraft" + (requested > 1 ? "s" : "") + "...";
        return "§aAll parts in ME - fabricating";
    }

    private static void configure(MEInputBusPartMachine bus, List<GenericStack> configs) {
        try {
            if (itemHandlerField == null) {
                itemHandlerField = MEInputBusPartMachine.class.getDeclaredField("aeItemHandler");
                itemHandlerField.setAccessible(true);
            }
            ExportOnlyAEItemList list = (ExportOnlyAEItemList) itemHandlerField.get(bus);
            ExportOnlyAEItemSlot[] slots = list.getInventory();
            for (int i = 0; i < slots.length; i++) slots[i].setConfig(i < configs.size() ? configs.get(i) : null);
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[GTAA] couldn't configure ME Input Bus: {}", t.toString());
        }
    }

    /** Called every second by the station: submits finished crafting plans. */
    public static void tick(SuitFabricationStationMachine m) {
        List<Pending> list = PENDING.get(m);
        if (list == null || list.isEmpty()) return;
        MEInputBusPartMachine bus = null;
        for (IMultiPart part : m.getParts()) if (part instanceof MEInputBusPartMachine b) { bus = b; break; }
        IGrid grid = bus == null ? null : bus.getMainNode().getGrid();
        int started = 0;
        List<String> failed = new ArrayList<>();
        for (Iterator<Pending> it = list.iterator(); it.hasNext(); ) {
            Pending p = it.next();
            if (!p.plan.isDone()) continue;
            it.remove();
            if (grid == null) continue;
            try {
                ICraftingPlan plan = p.plan.get();
                if (!plan.missingItems().isEmpty()) {
                    failed.add(p.name);
                    continue;
                }
                var result = grid.getCraftingService().submitJob(plan, null, null, true, p.source);
                if (result.successful()) started++;
                else failed.add(p.name + " (" + result.errorCode() + ")");
            } catch (Exception e) {
                failed.add(p.name);
            }
        }
        if (!failed.isEmpty()) m.setFabStatus("§cCan't autocraft: " + String.join(", ", failed));
        else if (started > 0) m.setFabStatus("§aAutocrafting " + started + " part" + (started > 1 ? "s" : "") +
                " - the station starts when they arrive");
    }
}
