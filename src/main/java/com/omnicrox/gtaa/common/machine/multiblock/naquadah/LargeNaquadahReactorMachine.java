package com.omnicrox.gtaa.common.machine.multiblock.naquadah;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Large Naquadah Reactor. Port of GoodGenerator's MTEMultiNqGenerator (now GT5-Unofficial
 * MTELargeNaquadahReactor, LGPL-3.0). Burns 1 L of liquid nuclear / naquadah fuel per run for its base EU/t,
 * needs 2400 L/s Liquid Air, optional coolant raises efficiency, optional booster multiplies output and fuel use.
 */
public class LargeNaquadahReactorMachine extends com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            LargeNaquadahReactorMachine.class, com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String BASIC_OUTPUT = "lnr_output";
    public static final int LIQUID_AIR_PER_SECOND = 2400;

    public record Boost(FluidStack fluid, int value) {}

    @Persisted private long basicOutput;
    @Persisted private int times = 1;
    @DescSynced private long trueOutput;
    @DescSynced private int trueEff;
    private IEnergyContainer outputs = new EnergyContainerList(new ArrayList<>());

    public LargeNaquadahReactorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private static FluidStack fs(com.gregtechceu.gtceu.api.data.chemical.material.Material m, int amt) {
        if (m == null) return FluidStack.EMPTY;
        try {
            FluidStack s = m.getFluid(amt);
            return s == null ? FluidStack.EMPTY : s;
        } catch (Throwable t) {
            return FluidStack.EMPTY;
        }
    }

    /** GTNH coolants: amount per second -> efficiency % */
    public static List<Boost> coolants() {
        List<Boost> l = new ArrayList<>();
        l.add(new Boost(fs(GTAAMaterials.TACHYON_RICH_TEMPORAL_FLUID, 20), 500));
        try {
            l.add(new Boost(GTMaterials.Helium.getFluid(FluidStorageKeys.LIQUID, 1000), 275));
        } catch (Throwable ignored) {}
        try {
            l.add(new Boost(GTMaterials.Nitrogen.getFluid(FluidStorageKeys.LIQUID, 1000), 150));
        } catch (Throwable ignored) {}
        l.add(new Boost(fs(GTMaterials.Ice, 1000), 105));
        l.removeIf(b -> b.fluid() == null || b.fluid().isEmpty());
        return l;
    }

    /** GTNH boosters: amount per second -> output and fuel multiplier */
    public static List<Boost> boosters() {
        List<Boost> l = new ArrayList<>();
        l.add(new Boost(fs(GTAAMaterials.SPATIALLY_ENLARGED_FLUID, 20), 64));
        l.add(new Boost(fs(GTAAMaterials.ATOMIC_SEPARATION_CATALYST, 20), 16));
        l.add(new Boost(fs(GTMaterials.Naquadah, 20), 4));
        l.add(new Boost(fs(GTMaterials.Uranium235, 180), 3));
        l.add(new Boost(fs(GTMaterials.Caesium, 180), 2));
        l.removeIf(b -> b.fluid() == null || b.fluid().isEmpty());
        return l;
    }

    private List<IFluidHandler> inputTanks() {
        List<IFluidHandler> tanks = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(FluidRecipeCapability.CAP)) {
                    if (h instanceof NotifiableFluidTank t) tanks.add(t);
                }
            }
        }
        return tanks;
    }

    /** drain exactly `stack` from the input hatches; simulate first */
    private boolean drain(FluidStack stack, boolean simulate) {
        if (stack == null || stack.isEmpty()) return false;
        int left = stack.getAmount();
        List<IFluidHandler> tanks = inputTanks();
        for (IFluidHandler t : tanks) {
            FluidStack d = ((com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank) t).drainInternal(new FluidStack(stack, left), IFluidHandler.FluidAction.SIMULATE);
            left -= d.getAmount();
            if (left <= 0) break;
        }
        if (left > 0) return false;
        if (simulate) return true;
        left = stack.getAmount();
        for (IFluidHandler t : tanks) {
            FluidStack d = ((com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank) t).drainInternal(new FluidStack(stack, left), IFluidHandler.FluidAction.EXECUTE);
            left -= d.getAmount();
            if (left <= 0) break;
        }
        return true;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        List<IEnergyContainer> list = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.OUT) continue;
                for (var h : handler.getCapability(EURecipeCapability.CAP)) {
                    if (h instanceof IEnergyContainer c) list.add(c);
                }
            }
        }
        outputs = new EnergyContainerList(list);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        outputs = new EnergyContainerList(new ArrayList<>());
        trueOutput = 0;
    }

    /** booster check at recipe start: parallel = booster multiplier (GTNH "times") */
    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof LargeNaquadahReactorMachine lnr)) return ModifierFunction.NULL;
        int t = 1;
        for (Boost b : boosters()) {
            if (lnr.drain(b.fluid(), true)) {
                t = b.value();
                break;
            }
        }
        lnr.times = t;
        lnr.basicOutput = recipe.data.getLong(BASIC_OUTPUT);
        if (t == 1) return ModifierFunction.IDENTITY;
        return ModifierFunction.builder().modifyAllContents(ContentModifier.multiplier(t)).parallels(t).build();
    }

    @Override
    public boolean onWorking() {
        boolean ok = super.onWorking();
        if (getOffsetTimer() % 20 == 0) {
            FluidStack air = fs(GTMaterials.LiquidAir, LIQUID_AIR_PER_SECOND);
            if (!drain(air, false)) {
                // GTNH: without liquid air the fuel is wasted
                trueOutput = 0;
                trueEff = 0;
                return ok;
            }
            int eff = 100;
            for (Boost c : coolants()) {
                if (drain(c.fluid(), false)) {
                    eff = c.value();
                    break;
                }
            }
            int time = 1;
            if (times > 1) {
                for (Boost b : boosters()) {
                    if (b.value() == times) {
                        if (drain(b.fluid(), false)) time = times;
                        break;
                    }
                }
            }
            trueEff = eff;
            trueOutput = basicOutput * eff * time / 100;
        }
        if (trueOutput > 0) outputs.changeEnergy(trueOutput);
        return ok;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Generating: " + FormattingUtil.formatNumbers(trueOutput) + " EU/t")
                .withStyle(ChatFormatting.GOLD));
        textList.add(Component.literal("Efficiency: " + trueEff + "%  |  Booster: x" + times)
                .withStyle(ChatFormatting.AQUA));
    }
}
