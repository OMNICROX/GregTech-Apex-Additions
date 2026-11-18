package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import com.omnicrox.gtaa.common.machine.part.AntimatterHatchPartMachine;
import com.omnicrox.gtaa.common.machine.multiblock.AntimatterMachineHelper.StoredFluid;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Semi-Stable Antimatter Stabilization Sequencer (SASS / Antimatter Forge).
 * Logic ported 1:1 from GoodGenerator's AntimatterForge (GT5-Unofficial / GT New Horizons):
 * protomatter becomes antimatter across 16 Antimatter Hatches, once per second, with a passive containment
 * cost, an active cost per cycle, hatch balancing losses, a random (possibly negative) gain per hatch and
 * four kinds of stabilizer fluid.
 */
public class AntimatterForgeMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AntimatterForgeMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int MAGNETIC = 0, GRAVITY = 1, CONTAINMENT = 2, ACTIVATION = 3;
    private static final float[] MODIFIER_STEPS = { -0.1f, -0.05f, 0.05f, 0.05f };
    public static final long BASE_CONSUMPTION = 10_000_000L;
    private static final int ACTIVE_BASE_MULT = 10_000;
    private static final float ACTIVE_BASE_EXP = 1.5f;
    private static final float BASE_SKEW = 0.2f;
    private static final double DECAY = 0.99d;
    private static final int GROWTH = 1000;
    private static final int CYCLE_TICKS = 20;

    private record Upgrade(int id, int tier) {}
    private static Map<Fluid, Upgrade> UPGRADES;

    private final float[] modifiers = { 0f, 0f, 0f, 0f };
    private final int[] consumptions = { 0, 0, 0, 0 };
    private final Random random = new Random();

    @Persisted private long rollingCost;
    @Persisted private float savedMagneticModifier;
    @Persisted @DescSynced private long guiAntimatter;
    @Persisted @DescSynced private long guiChange;
    @Persisted @DescSynced private long guiPassive;
    @Persisted @DescSynced private long guiActive;
    @Persisted @DescSynced private String lastStop = "";
    @DescSynced private final String[] activeStabilizers = { "", "", "", "" };

    private TickableSubscription tickSub;

    public AntimatterForgeMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private static synchronized Map<Fluid, Upgrade> upgrades() {
        if (UPGRADES != null) return UPGRADES;
        Map<Fluid, Upgrade> map = new HashMap<>();
        put(map, AntimatterMachineHelper.fluidOf("gtna:tengam_purified"), MAGNETIC, 1);
        put(map, AntimatterMachineHelper.fluidOf(GTAAMaterials.TACHYON_RICH_TEMPORAL_FLUID), MAGNETIC, 2);
        put(map, AntimatterMachineHelper.fluidOf("gtna:magmatter"), MAGNETIC, 3);
        put(map, AntimatterMachineHelper.fluidOf(GTAAMaterials.SPACETIME), GRAVITY, 1);
        put(map, AntimatterMachineHelper.fluidOf(GTAAMaterials.SPATIALLY_ENLARGED_FLUID), GRAVITY, 2);
        put(map, AntimatterMachineHelper.fluidOf("gtna:eternity"), GRAVITY, 3);
        put(map, AntimatterMachineHelper.fluidOf("gtna:shirabon"), CONTAINMENT, 1);
        put(map, AntimatterMachineHelper.fluidOf("gtna:magnetohydrodynamically_constrained_star_matter"), CONTAINMENT, 2);
        put(map, AntimatterMachineHelper.fluidOf(GTAAMaterials.NAQUADAH_FUEL_MKV_DEPLETED), ACTIVATION, 1);
        put(map, AntimatterMachineHelper.fluidOf(GTAAMaterials.NAQUADAH_FUEL_MKVI_DEPLETED), ACTIVATION, 2);
        UPGRADES = map;
        return map;
    }

    private static void put(Map<Fluid, Upgrade> map, Fluid fluid, int id, int tier) {
        if (fluid != null) map.put(fluid, new Upgrade(id, tier));
    }

    //////////////////////////////////////
    // ***** Lifecycle *****
    //////////////////////////////////////

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        modifiers[MAGNETIC] = savedMagneticModifier;
        if (!isRemote()) tickSub = subscribeServerTick(tickSub, this::antimatterTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tickSub != null) {
            tickSub.unsubscribe();
            tickSub = null;
        }
    }

    private List<AntimatterHatchPartMachine> hatches() {
        List<AntimatterHatchPartMachine> list = new ArrayList<>(16);
        for (IMultiPart part : getParts()) {
            if (part instanceof AntimatterHatchPartMachine h) list.add(h);
        }
        return list;
    }

    private boolean drainEnergy(long amount) {
        if (energyContainer == null || amount <= 0) return amount <= 0;
        if (energyContainer.getEnergyStored() < amount) return false;
        energyContainer.removeEnergy(amount);
        return true;
    }

    private void antimatterTick() {
        if (!isFormed()) return;
        List<AntimatterHatchPartMachine> hatches = hatches();
        long total = 0;
        for (AntimatterHatchPartMachine h : hatches) total += h.getAmount();
        if (!hatches.isEmpty()) drainEnergy(containmentCost(total));
        if (!getRecipeLogic().isWorkingEnabled()) {
            guiAntimatter = total;
            return;
        }
        if (getOffsetTimer() % CYCLE_TICKS == 0) cycle(hatches);
    }

    //////////////////////////////////////
    // ***** One cycle (GTNH checkProcessing) *****
    //////////////////////////////////////

    private void cycle(List<AntimatterHatchPartMachine> hatches) {
        if (hatches.isEmpty() || GTAAMaterials.ANTIMATTER == null) return;
        long total = 0, min = Long.MAX_VALUE;
        for (AntimatterHatchPartMachine h : hatches) {
            long a = h.getAmount();
            total += a;
            min = Math.min(min, a);
        }
        // hatches above the lowest one lose half the difference
        long ratioLosses = 0;
        for (AntimatterHatchPartMachine h : hatches) {
            long a = h.getAmount();
            if (a > min) ratioLosses -= h.drainAntimatter((int) ((a - min) * 0.5));
        }

        // stabilizer fluids
        int sqrt = (int) Math.ceil(Math.sqrt(total));
        consumptions[MAGNETIC] = sqrt;
        consumptions[GRAVITY] = sqrt;
        consumptions[CONTAINMENT] = (int) Math.ceil(Math.pow(total, 2.0 / 7.0));
        consumptions[ACTIVATION] = (int) Math.ceil(Math.pow(total, 1.0 / 3.0));
        FluidStack[] chosen = new FluidStack[4];
        for (int i = 0; i < 4; i++) {
            modifiers[i] = 0f;
            activeStabilizers[i] = "";
        }
        List<StoredFluid> inputs = AntimatterMachineHelper.inputFluids(this);
        Map<Fluid, Upgrade> map = upgrades();
        for (StoredFluid f : inputs) {
            Upgrade up = map.get(f.stack().getFluid());
            if (up == null || f.stack().getAmount() < consumptions[up.id()]) continue;
            chosen[up.id()] = f.stack();
            modifiers[up.id()] = MODIFIER_STEPS[up.id()] * up.tier();
            activeStabilizers[up.id()] = f.stack().getDisplayName().getString();
        }
        savedMagneticModifier = modifiers[MAGNETIC];

        long energyCost = activeCost(total);
        if (!drainEnergy(energyCost)) {
            decimate(hatches);
            stop("Not enough energy for the cycle (" + FormattingUtil.formatNumbers(energyCost) + " EU)");
            return;
        }
        for (int i = 0; i < 4; i++) {
            if (chosen[i] != null && consumptions[i] > 0) {
                AntimatterMachineHelper.deplete(inputs, chosen[i].getFluid(), consumptions[i]);
            }
        }

        long change = distribute(hatches, total);
        if (!AntimatterMachineHelper.deplete(AntimatterMachineHelper.inputFluids(this),
                GTAAMaterials.PROTOMATTER.getFluid(), Math.abs(change))) {
            decimate(hatches);
            stop("Ran out of Protomatter");
            return;
        }
        lastStop = "";
        guiChange = ratioLosses + change;
        guiAntimatter = total;
    }

    /** Passive drain per tick: 10M + (rolling cost)^(1.5 - M). Rolling cost decays 1%/tick when empty. */
    private long containmentCost(long antimatter) {
        if (antimatter == 0) {
            rollingCost = (long) (rollingCost * DECAY);
            if (rollingCost < 100) rollingCost = 0;
        } else if (rollingCost <= antimatter * GROWTH) {
            rollingCost = antimatter * GROWTH;
        } else {
            rollingCost = (long) (rollingCost * DECAY);
        }
        long value = BASE_CONSUMPTION + (long) Math.pow(rollingCost, 1.5 + modifiers[MAGNETIC]);
        guiPassive = value;
        return value;
    }

    /** Active cost per cycle: (antimatter * 10,000)^(1.5 - G). */
    private long activeCost(long antimatter) {
        long value = (long) Math.pow((double) antimatter * ACTIVE_BASE_MULT, ACTIVE_BASE_EXP + modifiers[GRAVITY]);
        guiActive = value;
        return value;
    }

    private void decimate(List<AntimatterHatchPartMachine> hatches) {
        for (AntimatterHatchPartMachine h : hatches) h.drainAntimatter((int) Math.floor(h.getAmount() * 0.1));
    }

    /** Base gain per hatch (1/16) * antimatter^(0.5 + C), times a normal roll with mean 0.2 + A. */
    private long distribute(List<AntimatterHatchPartMachine> hatches, long total) {
        double coeff = Math.pow(total, 0.5 + modifiers[CONTAINMENT]);
        long difference = 0;
        for (AntimatterHatchPartMachine h : hatches) {
            int change = (int) Math.ceil((random.nextGaussian() + BASE_SKEW + modifiers[ACTIVATION]) * (coeff / 16));
            difference += change;
            if (change > 0) h.fillAntimatter(change);
            else if (change < 0) h.drainAntimatter(-change);
        }
        return difference;
    }

    private void stop(String reason) {
        lastStop = reason;
        getRecipeLogic().setWorkingEnabled(false);
    }

    //////////////////////////////////////
    // ***** GUI *****
    //////////////////////////////////////

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(getRecipeLogic().isWorkingEnabled(), getRecipeLogic().isWorkingEnabled() && isFormed())
                .addCustom(t -> {
                    t.add(Component.literal("Antimatter: " + FormattingUtil.formatNumbers(guiAntimatter) + " L")
                            .withStyle(ChatFormatting.LIGHT_PURPLE));
                    t.add(Component.literal("Last change: " + (guiChange >= 0 ? "+" : "") +
                            FormattingUtil.formatNumbers(guiChange) + " L/cycle")
                            .withStyle(guiChange >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED));
                    t.add(Component.literal("Passive: " + FormattingUtil.formatNumbers(guiPassive) + " EU/t")
                            .withStyle(ChatFormatting.YELLOW));
                    t.add(Component.literal("Active: " + FormattingUtil.formatNumbers(guiActive / CYCLE_TICKS) + " EU/t")
                            .withStyle(ChatFormatting.YELLOW));
                    String[] names = { "Magnetic", "Gravity", "Containment", "Activation" };
                    for (int i = 0; i < 4; i++) {
                        if (!activeStabilizers[i].isEmpty())
                            t.add(Component.literal(names[i] + ": " + activeStabilizers[i]).withStyle(ChatFormatting.AQUA));
                    }
                    if (!lastStop.isEmpty()) t.add(Component.literal("Stopped: " + lastStop).withStyle(ChatFormatting.RED));
                });
    }
}
