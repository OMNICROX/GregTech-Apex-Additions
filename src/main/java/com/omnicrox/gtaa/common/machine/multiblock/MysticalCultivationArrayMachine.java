package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mystical Cultivation Array. Based on KubaTech's Extreme Industrial Greenhouse (GTNH, LGPL-3.0), made for
 * Mystical Agriculture (works with any crop seed):
 * <ul>
 * <li>seeds in input buses are planted (one crop per seed, not used up); each harvest gives 4x a normal crop's
 * drops</li>
 * <li>a harvest every 5 s at LV, twice as fast per voltage tier, down to 0.25 s at LuV</li>
 * <li>room for 64 crops at LV, doubling per tier (max 8192)</li>
 * <li>water: a quarter litre per crop per second</li>
 * </ul>
 */
public class MysticalCultivationArrayMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MysticalCultivationArrayMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final int YIELD = 4;
    public static final int MAX_CROPS = 8192;
    private static final int SAMPLES = 64;

    @Persisted private int progress;
    @DescSynced private int crops, types, cycle, tier;
    @DescSynced private long lastEut;
    @DescSynced private String status = "";
    private TickableSubscription tick;
    private final Map<Item, Map<Item, Double>> dropCache = new HashMap<>();
    private final Map<Item, Double> carry = new HashMap<>();

    public static final String CTX_SOIL = "MCASoil";
    public static final String CTX_GLASS = "MCAGlass";
    /** soil tier -> yield (plain dirt / farmland, then the Mystical Agriculture farmlands) */
    public static final int[] SOIL_YIELD = { 4, 5, 6, 7, 8, 10, 12 };
    public static final String[][] SOILS = {
            { "minecraft:farmland", "minecraft:dirt" },
            { "mysticalagriculture:inferium_farmland" }, { "mysticalagriculture:prudentium_farmland" },
            { "mysticalagriculture:tertium_farmland" }, { "mysticalagriculture:imperium_farmland" },
            { "mysticalagriculture:supremium_farmland" }, { "mysticalagradditions:insanium_farmland" } };
    /** glass tier -> highest voltage tier the array runs at */
    public static final int[] GLASS_CAP = { GTValues.HV, GTValues.IV, GTValues.LuV, GTValues.MAX };
    public static final String[] GLASS_NAMES = { "Glass", "Tempered Glass", "Laminated Glass", "Fusion Glass" };

    @DescSynced private int soilTier = 1, glassTier = 1;

    public MysticalCultivationArrayMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    /** the recipes in JEI are only a guide; the array farms through its own loop, never through recipe logic */
    @Override
    protected com.gregtechceu.gtceu.api.machine.trait.RecipeLogic createRecipeLogic(Object... args) {
        return new com.gregtechceu.gtceu.api.machine.trait.RecipeLogic(this) {
            @Override
            public void serverTick() {}

            @Override
            public void findAndHandleRecipe() {}
        };
    }

    private static Map<net.minecraft.world.level.block.Block, Integer> tierMap(String[][] ids) {
        Map<net.minecraft.world.level.block.Block, Integer> map = new LinkedHashMap<>();
        for (int t = 0; t < ids.length; t++)
            for (String id : ids[t]) {
                var b = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValue(new net.minecraft.resources.ResourceLocation(id));
                if (b != null && b != net.minecraft.world.level.block.Blocks.AIR) map.put(b, t + 1);
            }
        return map;
    }

    private static Map<net.minecraft.world.level.block.Block, Integer> glassMap() {
        Map<net.minecraft.world.level.block.Block, Integer> map = new LinkedHashMap<>();
        map.put(net.minecraft.world.level.block.Blocks.GLASS, 1);
        map.put(com.gregtechceu.gtceu.common.data.GTBlocks.CASING_TEMPERED_GLASS.get(), 2);
        map.put(com.gregtechceu.gtceu.common.data.GTBlocks.CASING_LAMINATED_GLASS.get(), 3);
        map.put(com.gregtechceu.gtceu.common.data.GTBlocks.FUSION_GLASS.get(), 4);
        return map;
    }

    private static com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate tiered(String ctx,
            java.util.function.Supplier<Map<net.minecraft.world.level.block.Block, Integer>> tiers, String what) {
        return new com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate(st -> {
            Integer t = tiers.get().get(st.getBlockState().getBlock());
            if (t == null) return false;
            Object cur = st.getMatchContext().getOrPut(ctx, t);
            if (!cur.equals(t)) {
                st.setError(new com.gregtechceu.gtceu.api.pattern.error.PatternStringError(what + " must all be the same tier"));
                return false;
            }
            return true;
        }, () -> tiers.get().keySet().stream()
                .map(b -> com.lowdragmc.lowdraglib.utils.BlockInfo.fromBlockState(b.defaultBlockState()))
                .toArray(com.lowdragmc.lowdraglib.utils.BlockInfo[]::new));
    }

    public static com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate soil() {
        return tiered(CTX_SOIL, () -> tierMap(SOILS), "Soil");
    }

    public static com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate glass() {
        return tiered(CTX_GLASS, MysticalCultivationArrayMachine::glassMap, "Glass");
    }

    public int soilYield() {
        return SOIL_YIELD[Math.max(0, Math.min(SOIL_YIELD.length - 1, soilTier - 1))];
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        dropCache.clear();
        Object so = getMultiblockState().getMatchContext().get(CTX_SOIL);
        Object gl = getMultiblockState().getMatchContext().get(CTX_GLASS);
        soilTier = so instanceof Integer i ? i : 1;
        glassTier = gl instanceof Integer i ? i : 1;
        if (!isRemote()) tick = subscribeServerTick(tick, this::work);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    public static int maxCrops(int tier) {
        return Math.min(MAX_CROPS, 64 << Math.max(0, tier - GTValues.LV));
    }

    public static int cycleTicks(int tier) {
        return Math.max(5, 100 >> Math.max(0, Math.min(tier, GTValues.LuV) - GTValues.LV));
    }

    private static int tierOf(long voltage) {
        for (int t = 0; t < GTValues.V.length; t++) if (GTValues.V[t] >= voltage) return t;
        return GTValues.MAX;
    }

    private List<NotifiableItemStackHandler> items(IO io) {
        List<NotifiableItemStackHandler> out = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var handler : part.getRecipeHandlers())
                if (handler.getHandlerIO() == io)
                    for (var h : handler.getCapability(ItemRecipeCapability.CAP))
                        if (h instanceof NotifiableItemStackHandler inv) out.add(inv);
        return out;
    }

    private List<NotifiableFluidTank> tanks() {
        List<NotifiableFluidTank> out = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var handler : part.getRecipeHandlers())
                if (handler.getHandlerIO() == IO.IN)
                    for (var h : handler.getCapability(FluidRecipeCapability.CAP))
                        if (h instanceof NotifiableFluidTank t) out.add(t);
        return out;
    }

    private IEnergyContainer energy() {
        List<IEnergyContainer> list = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var handler : part.getRecipeHandlers())
                if (handler.getHandlerIO() == IO.IN)
                    for (var h : handler.getCapability(EURecipeCapability.CAP))
                        if (h instanceof IEnergyContainer c) list.add(c);
        return new EnergyContainerList(list);
    }

    private static CropBlock crop(ItemStack s) {
        return s.getItem() instanceof BlockItem bi && bi.getBlock() instanceof CropBlock c ? c : null;
    }

    /** average drops of one fully grown crop (the seed itself is left out) */
    private Map<Item, Double> drops(ServerLevel level, Item seed, CropBlock crop) {
        return dropCache.computeIfAbsent(seed, k -> {
            Map<Item, Double> avg = new LinkedHashMap<>();
            BlockState grown = crop.getStateForAge(crop.getMaxAge());
            for (int i = 0; i < SAMPLES; i++) {
                LootParams.Builder b = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(getPos()))
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
                List<ItemStack> list;
                try {
                    list = grown.getDrops(b);
                } catch (Throwable t) {
                    list = List.of();
                }
                for (ItemStack d : list)
                    if (!d.isEmpty() && d.getItem() != seed) avg.merge(d.getItem(), d.getCount() / (double) SAMPLES, Double::sum);
            }
            return avg;
        });
    }

    private long drainWater(long amount, boolean simulate) {
        long left = amount;
        for (NotifiableFluidTank t : tanks()) {
            for (int i = 0; i < t.getTanks() && left > 0; i++) {
                FluidStack f = t.getFluidInTank(i);
                if (f.isEmpty() || !f.getFluid().is(FluidTags.WATER)) continue;
                int take = (int) Math.min(left, f.getAmount());
                FluidStack got = t.drainInternal(new FluidStack(f, take),
                        simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                left -= got.getAmount();
            }
        }
        return amount - left;
    }

    private void work() {
        if (!(getLevel() instanceof ServerLevel level) || !isWorkingEnabled()) return;
        IEnergyContainer energy = energy();
        tier = Math.min(tierOf(energy.getInputVoltage()), GLASS_CAP[Math.max(0, Math.min(3, glassTier - 1))]);
        if (tier < GTValues.LV) {
            status = "Needs at least an LV energy hatch";
            return;
        }
        int room = maxCrops(tier);
        cycle = cycleTicks(tier);

        // plant: one crop per seed, up to the room this tier allows
        Map<Item, Integer> planted = new LinkedHashMap<>();
        Map<Item, CropBlock> blocks = new HashMap<>();
        int total = 0;
        for (NotifiableItemStackHandler inv : items(IO.IN)) {
            for (int i = 0; i < inv.getSlots() && total < room; i++) {
                ItemStack s = inv.getStackInSlot(i);
                CropBlock c = crop(s);
                if (c == null) continue;
                int n = Math.min(s.getCount(), room - total);
                planted.merge(s.getItem(), n, Integer::sum);
                blocks.put(s.getItem(), c);
                total += n;
            }
        }
        crops = total;
        types = planted.size();
        if (total == 0) {
            status = "Put seeds in an input bus";
            lastEut = 0;
            return;
        }
        long eut = Math.max(GTValues.VA[GTValues.LV], GTValues.VA[tier] * total / room);
        if (energy.getEnergyStored() < eut) {
            status = "Not enough energy";
            lastEut = 0;
            return;
        }
        if (++progress < cycle) {
            energy.removeEnergy(eut);
            lastEut = eut;
            status = "";
            return;
        }
        long water = Math.max(1, (long) Math.ceil(total * cycle / 80.0));
        if (drainWater(water, true) < water) {
            status = "Needs " + water + " L of water per harvest";
            progress = cycle;
            return;
        }
        drainWater(water, false);
        energy.removeEnergy(eut);
        lastEut = eut;
        status = "";
        progress = 0;

        List<ItemStack> out = new ArrayList<>();
        planted.forEach((seed, count) -> drops(level, seed, blocks.get(seed)).forEach((item, avg) -> {
            double amount = avg * count * soilYield() + carry.getOrDefault(item, 0.0);
            long whole = (long) amount;
            carry.put(item, amount - whole);
            while (whole > 0) {
                int n = (int) Math.min(whole, item.getMaxStackSize(item.getDefaultInstance()));
                out.add(new ItemStack(item, n));
                whole -= n;
            }
        }));
        List<NotifiableItemStackHandler> outs = items(IO.OUT);
        for (ItemStack drop : out) {
            ItemStack left = drop;
            for (NotifiableItemStackHandler inv : outs) {
                for (int i = 0; i < inv.getSlots() && !left.isEmpty(); i++) left = inv.insertItemInternal(i, left, false);
                if (left.isEmpty()) break;
            }
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Crops: " + crops + " / " + maxCrops(tier) + " (" + types + " kinds)")
                .withStyle(ChatFormatting.GREEN));
        textList.add(Component.literal("Harvest every " + cycle / 20.0 + " s, " + soilYield() + "x yield")
                .withStyle(ChatFormatting.GREEN));
        if (lastEut > 0) textList.add(Component.literal("Using " + FormattingUtil.formatNumbers(lastEut) + " EU/t")
                .withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("Soil tier " + soilTier + ", " + GLASS_NAMES[Math.max(0, Math.min(3, glassTier - 1))] +
                " (runs up to " + GTValues.VNF[GLASS_CAP[Math.max(0, Math.min(3, glassTier - 1))]] + ChatFormatting.GRAY + ")")
                .withStyle(ChatFormatting.GRAY));
        if (!status.isEmpty()) textList.add(Component.literal(status).withStyle(ChatFormatting.RED));
    }
}
