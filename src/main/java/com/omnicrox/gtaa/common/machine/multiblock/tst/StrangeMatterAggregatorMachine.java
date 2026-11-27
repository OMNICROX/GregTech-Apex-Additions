package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.omnicrox.apexlib.compat.ApexWireless;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.util.ApexFormat;
import com.omnicrox.gtaa.common.data.GTAAGTNHMachines;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strange Matter Aggregator (Twist Space Technology, by Nxer), adapted: Twist's machine makes Antimatter Fuel Rods
 * for its Dyson Sphere line; here it breeds Semi-Stable Antimatter for the Shielded Lagrangian Annihilation Matrix.
 * Main section, 3-8 rings of SpaceTime Oscillators / Constraintors / Mergers (at least one of each; the ring blocks are
 * the Forge of the Gods field generators: Spacetime Compression = Oscillator, Stabilisation = Constraintor,
 * Time Acceleration = Merger; generator tiers 0-2 / 3-5 / 6-8 = ring tier 1 / 2 / 3), end section.
 * Twist's formulas, with 1 Antimatter Fuel Rod = 1 L of antimatter and the antimatter input counted in mB.
 */
public class StrangeMatterAggregatorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StrangeMatterAggregatorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int MIN_RINGS = 3, MAX_RINGS = 8, BLOCKS_PER_RING = 48;
    public static final String CTX = "SMARings";
    public static final String[] FAMILIES = { "spacetime_compression_field_generator", "stabilisation_field_generator",
            "time_acceleration_field_generator" };
    private static final int[] RECIPE_TIME = { 120 * 20, 40 * 20, 10 * 20 };
    private static final int[] MAX_POINTS = { 24, 144, 384 };
    private static final double[] AUX_RATE = { 1.0, 0.99, 0.90 };
    private static final String[] MAINTENANCE = { "gtna:space_time", "gtna:universium",
            "gtna:magnetohydrodynamically_constrained_star_matter" };
    private static final int[] MAINTENANCE_USE = { 576, 96, 16 };
    private static final int[] FLUID_FACTOR = { 1000, 50, 1 };
    private static final int[] ROD_FACTOR = { 256, 1024, 16384 };
    private static final String[][] BYPRODUCTS = { { "gtna:infinity", "gtna:hypogen" },
            { "gtna:space_time", "gtna:shirabon" }, { "gtna:universium" } };
    public static final long BASE_EUT = GTValues.VA[GTValues.OpV] * 16L;

    @DescSynced private int rings;
    @DescSynced private int[] pieces = new int[3];
    @DescSynced private int[] tiers = new int[3];
    @Persisted @DescSynced private int points;
    @Persisted @DescSynced private int progress, duration;
    @Persisted private long eut;
    @Persisted private long pendingAntimatter;
    @Persisted private long pendingByproduct;
    @Persisted private int pendingMaintenanceTier;
    @DescSynced private boolean wireless;
    @DescSynced private String status = "";
    @DescSynced private long lastOutput;
    private int matched = MIN_RINGS;
    private TickableSubscription tick;

    public StrangeMatterAggregatorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    // ---------------- structure ----------------
    @Override
    public boolean checkPattern() {
        for (int r = MAX_RINGS; r >= MIN_RINGS; r--) {
            BlockPattern p = GTAAGTNHMachines.strangeMatterAggregatorPattern(getDefinition(), r);
            if (p != null && p.checkPatternAt(getMultiblockState(), false)) {
                matched = r;
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockPattern getPattern() {
        return GTAAGTNHMachines.strangeMatterAggregatorPattern(getDefinition(), matched);
    }

    private static final Map<Block, int[]> RING_BLOCKS = new HashMap<>();

    private static Map<Block, int[]> ringBlocks() {
        if (RING_BLOCKS.isEmpty())
            for (int f = 0; f < 3; f++)
                for (int t = 0; t <= 8; t++) {
                    Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("gtna", FAMILIES[f] + "_tier_" + t));
                    if (b != null && b != net.minecraft.world.level.block.Blocks.AIR) RING_BLOCKS.put(b, new int[] { f, t / 3 + 1 });
                }
        return RING_BLOCKS;
    }

    /** a ring block: any of the 27 generators; counts blocks per family and keeps the lowest tier */
    public static TraceabilityPredicate ringPredicate() {
        return new TraceabilityPredicate(s -> {
            int[] ft = ringBlocks().get(s.getBlockState().getBlock());
            if (ft == null) return false;
            int[] acc = s.getMatchContext().getOrPut(CTX, new int[] { 0, 0, 0, 9, 9, 9 });
            acc[ft[0]]++;
            acc[3 + ft[0]] = Math.min(acc[3 + ft[0]], ft[1]);
            return true;
        }, () -> ringBlocks().keySet().stream().map(b -> BlockInfo.fromBlockState(b.defaultBlockState())).toArray(BlockInfo[]::new));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        rings = matched;
        Object o = getMultiblockState().getMatchContext().get(CTX);
        if (o instanceof int[] acc) {
            for (int f = 0; f < 3; f++) {
                pieces[f] = acc[f] / BLOCKS_PER_RING;
                tiers[f] = pieces[f] > 0 ? acc[3 + f] : 0;
            }
        }
        if (!isRemote()) tick = subscribeServerTick(tick, this::work);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        points = 0;
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    private boolean ringsOk() {
        return pieces[0] > 0 && pieces[1] > 0 && pieces[2] > 0;
    }

    // ---------------- Twist's parameters ----------------
    private int recipeTime() {
        return tiers[1] * RECIPE_TIME[Math.max(0, tiers[0] - 1)];
    }

    private double inputFactor() {
        return 1d + 255d / Math.sqrt(pieces[0]);
    }

    private long power() {
        return (long) (tiers[2] * Math.pow(pieces[2], 0.7) * BASE_EUT);
    }

    private int maxPoints() {
        return MAX_POINTS[Math.max(0, tiers[2] - 1)];
    }

    private double auxRate() {
        return AUX_RATE[Math.max(0, tiers[1] - 1)];
    }

    private int maintenanceUse(int mt) {
        return (int) Math.max(1, MAINTENANCE_USE[mt] * Math.pow(tiers[0] * pieces[0], 0.8) / pieces[1]);
    }

    private long constrainerFactor(int mt) {
        return (long) tiers[0] * pieces[0] * tiers[1] * pieces[1] * tiers[2] * pieces[2] * ROD_FACTOR[mt];
    }

    private static Fluid fluid(String id) {
        Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(id));
        return f == null ? Fluids.EMPTY : f;
    }

    private static Item item(String id, Item fallback) {
        Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return i == null || i == Items.AIR ? fallback : i;
    }

    // ---------------- running ----------------
    private IEnergyContainer energy() {
        List<IEnergyContainer> list = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(EURecipeCapability.CAP))
                    if (h instanceof IEnergyContainer c) list.add(c);
            }
        return new EnergyContainerList(list);
    }

    private void work() {
        if (!isWorkingEnabled()) {
            status = "Paused";
            return;
        }
        if (duration > 0) {
            if (!wireless) {
                IEnergyContainer e = energy();
                if (e.getEnergyStored() < eut) {
                    status = "Not enough energy: the run was lost";
                    duration = progress = 0;
                    pendingAntimatter = pendingByproduct = 0;
                    points = 0;
                    return;
                }
                e.removeEnergy(eut);
            }
            if (++progress < duration) return;
            finish();
            duration = progress = 0;
        }
        if (getOffsetTimer() % 20 != 0 && points == 0) return;
        if (!ringsOk()) {
            status = "Needs at least one Oscillator, Constraintor and Merger ring";
            return;
        }
        start();
    }

    private void start() {
        // maintenance fluid: the highest tier present
        int mt = -1;
        for (int i = 2; i >= 0; i--)
            if (fluid(MAINTENANCE[i]) != Fluids.EMPTY && ApexHatches.amount(this, fluid(MAINTENANCE[i])) >= maintenanceUse(i)) {
                mt = i;
                break;
            }
        if (mt < 0) {
            status = "Needs SpaceTime / Universium / Magnetohydrodynamically Constrained Star Matter";
            points = 0;
            return;
        }
        Fluid antimatter = GTAAMaterials.ANTIMATTER.getFluid();
        Fluid plasma = fluid("gtceu:hydrogen_plasma");
        Item frame = item("gtca:stellar_alloy_frame", Items.NETHERITE_BLOCK);
        Item tesseract = item("infinity:empty_tesseract", com.gregtechceu.gtceu.common.data.GTItems.GRAVI_STAR.get());
        Item constrainer = GTAAItems.NUCLEAR_STAR.get();
        Item core = item("gtca:exquisite_strange_crystal_gem", Items.AIR);
        long am = ApexHatches.amount(this, antimatter), hp = ApexHatches.amount(this, plasma);
        long frames = count(frame), tess = count(tesseract), cons = count(constrainer), cores = core == Items.AIR ? 0 : count(core);
        if (cons <= 0) {
            status = "Needs Nuclear Stars (annihilation constrainers)";
            points = 0;
            return;
        }
        double inputFactor = inputFactor();
        double plasmaPer = inputFactor * FLUID_FACTOR[mt];
        long rodPer = 1L + points;
        double parallel = (double) cons * constrainerFactor(mt) / rodPer;
        parallel = Math.min(parallel, am / inputFactor);
        parallel = Math.min(parallel, hp / plasmaPer);
        parallel = Math.min(parallel, tess * ROD_FACTOR[mt] / auxRate());
        parallel = Math.min(parallel, (double) ((long) (frames / auxRate()) / rodPer));
        long par = (long) parallel;
        if (par < 1) {
            status = "Needs antimatter, hydrogen plasma, stellar alloy frames and tesseracts";
            points = 0;
            return;
        }
        // power: wireless runs pay the whole run up front
        IEnergyContainer e = energy();
        wireless = e.getEnergyCapacity() <= 0;
        long p = power();
        int time = Math.max(1, recipeTime());
        if (wireless) {
            long total = p * time;
            if (ApexWireless.drawEU(getOwnerUUID(), total) < total) {
                status = "Not enough wireless EU (" + ApexFormat.num(total) + " a run)";
                points = 0;
                return;
            }
        } else if (e.getInputVoltage() * Math.max(1, e.getInputAmperage()) < p) {
            status = "Needs " + ApexFormat.num(p) + " EU/t";
            points = 0;
            return;
        }
        long rods = par * rodPer;
        long useCons = (long) Math.max(1, Math.ceil((double) rods / constrainerFactor(mt)));
        ApexHatches.drain(this, new FluidStack(fluid(MAINTENANCE[mt]), maintenanceUse(mt)));
        drainLong(antimatter, Math.max(1, (long) (par * inputFactor)));
        drainLong(plasma, (long) Math.max(1, par * plasmaPer));
        take(tesseract, (long) Math.max(1, par * auxRate() / ROD_FACTOR[mt]));
        take(frame, (long) Math.max(1, auxRate() * rods));
        take(constrainer, useCons);
        long byproduct = (long) 144 * pieces[2] * points;
        if (cores > 0) {
            take(core, Math.min(cores, useCons));
            byproduct *= 2;
        }
        pendingAntimatter = rods * 1000;
        pendingByproduct = byproduct;
        pendingMaintenanceTier = mt;
        eut = wireless ? 0 : p;
        duration = time;
        progress = 0;
        status = "Running: " + ApexFormat.num(par) + " parallels, " + ApexFormat.num(rods) + " L of antimatter";
    }

    private void finish() {
        Fluid antimatter = GTAAMaterials.ANTIMATTER.getFluid();
        fillLong(antimatter, pendingAntimatter);
        lastOutput = pendingAntimatter;
        if (points > 0 && pendingByproduct > 0)
            for (String id : BYPRODUCTS[pendingMaintenanceTier]) fillLong(fluid(id), pendingByproduct);
        pendingAntimatter = pendingByproduct = 0;
        points = Math.min(maxPoints(), points + 1);
    }

    private long count(Item item) {
        long n = 0;
        for (NotifiableItemStackHandler bus : ApexHatches.itemHandlers(this, IO.IN))
            for (int i = 0; i < bus.getSlots(); i++) {
                ItemStack s = bus.getStackInSlot(i);
                if (s.is(item)) n += s.getCount();
            }
        return n;
    }

    private void take(Item item, long n) {
        for (NotifiableItemStackHandler bus : ApexHatches.itemHandlers(this, IO.IN))
            for (int i = 0; i < bus.getSlots() && n > 0; i++) {
                ItemStack s = bus.getStackInSlot(i);
                if (!s.is(item)) continue;
                n -= bus.extractItemInternal(i, (int) Math.min(n, s.getCount()), false).getCount();
            }
    }

    private void drainLong(Fluid f, long amount) {
        for (NotifiableFluidTank tank : ApexHatches.fluidTanks(this, IO.IN))
            for (int i = 0; i < tank.getTanks() && amount > 0; i++) {
                FluidStack s = tank.getFluidInTank(i);
                if (s.isEmpty() || s.getFluid() != f) continue;
                int want = (int) Math.min(amount, s.getAmount());
                amount -= tank.drainInternal(new FluidStack(f, want), IFluidHandler.FluidAction.EXECUTE).getAmount();
            }
    }

    private void fillLong(Fluid f, long amount) {
        if (f == Fluids.EMPTY) return;
        for (NotifiableFluidTank tank : ApexHatches.fluidTanks(this, IO.OUT)) {
            while (amount > 0) {
                int put = tank.fillInternal(new FluidStack(f, (int) Math.min(Integer.MAX_VALUE, amount)), IFluidHandler.FluidAction.EXECUTE);
                if (put <= 0) break;
                amount -= put;
            }
            if (amount <= 0) return;
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        String[] names = { "Oscillator", "Constraintor", "Merger" };
        for (int f = 0; f < 3; f++)
            textList.add(Component.literal(names[f] + " rings: " + pieces[f] + (pieces[f] > 0 ? " (tier " + tiers[f] + ")" : ""))
                    .withStyle(pieces[f] > 0 ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.RED));
        if (ringsOk()) {
            textList.add(Component.literal("Run: " + ApexFormat.num(recipeTime() / 20) + " s at " + ApexFormat.num(power()) +
                    " EU/t" + (wireless ? " (wireless)" : "")).withStyle(ChatFormatting.AQUA));
            textList.add(Component.literal("Antimatter per parallel: " + String.format("%.1f", inputFactor()) + " mB in, " +
                    (1 + points) + " L out").withStyle(ChatFormatting.DARK_PURPLE));
            textList.add(Component.literal("Consecutive runs: " + points + " / " + maxPoints()).withStyle(ChatFormatting.GOLD));
        }
        if (!status.isEmpty()) textList.add(Component.literal(status).withStyle(ChatFormatting.YELLOW));
        if (lastOutput > 0) textList.add(Component.literal("Last run: " + ApexFormat.num(lastOutput / 1000) + " L of antimatter")
                .withStyle(ChatFormatting.GREEN));
    }
}
