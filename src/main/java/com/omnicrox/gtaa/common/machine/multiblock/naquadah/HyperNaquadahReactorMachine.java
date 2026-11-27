package com.omnicrox.gtaa.common.machine.multiblock.naquadah;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Hyper / Advanced Hyper Naquadah Reactor, after GT Not Leisure's (GTNH addon) Naquadah Reactor family, tuned for
 * Apex. Each recipe burns naquadah fuel with a catalyst for a fixed EU/t per parallel. Fixed parallels; feeding the
 * boost fluid (naquadah gas / SpaceTime) at recipe start quadruples the parallels and runs it faster and hotter.
 * Burning fuel with nowhere to put the power stalls nothing: the reactor runs and the power that doesn't fit is lost.
 */
public class HyperNaquadahReactorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            HyperNaquadahReactorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String POWER = "hnr_power";
    public static final String TIER = "hnr_tier";

    /** reactor tier 1 (Hyper) / 2 (Advanced Hyper) */
    public record Spec(int tier, int parallels, int boostedParallels, Supplier<FluidStack> boost, int boostPowerMult,
                       int boostSpeedMult) {}

    private final Spec spec;
    @Persisted private long powerPerParallel;
    @Persisted private int parallels;
    @Persisted private boolean boosted;
    @DescSynced private long output;
    @DescSynced private long lastVoided;
    private IEnergyContainer outputs = new EnergyContainerList(new ArrayList<>());

    public HyperNaquadahReactorMachine(IMachineBlockEntity holder, Spec spec) {
        super(holder);
        this.spec = spec;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public Spec getSpec() {
        return spec;
    }

    private List<IFluidHandler> tanks() {
        List<IFluidHandler> l = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var h : part.getRecipeHandlers())
                if (h.getHandlerIO() == IO.IN)
                    for (var c : h.getCapability(FluidRecipeCapability.CAP)) if (c instanceof NotifiableFluidTank t) l.add(t);
        return l;
    }

    private boolean drain(FluidStack want, boolean simulate) {
        if (want == null || want.isEmpty()) return false;
        int left = want.getAmount();
        for (IFluidHandler t : tanks()) {
            left -= ((com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank) t).drainInternal(new FluidStack(want, left), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (left <= 0) break;
        }
        if (left > 0) return false;
        if (simulate) return true;
        left = want.getAmount();
        for (IFluidHandler t : tanks()) {
            left -= ((com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank) t).drainInternal(new FluidStack(want, left), IFluidHandler.FluidAction.EXECUTE).getAmount();
            if (left <= 0) break;
        }
        return true;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        List<IEnergyContainer> list = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var h : part.getRecipeHandlers())
                if (h.getHandlerIO() == IO.OUT)
                    for (var c : h.getCapability(EURecipeCapability.CAP)) if (c instanceof IEnergyContainer e) list.add(e);
        outputs = new EnergyContainerList(list);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        outputs = new EnergyContainerList(new ArrayList<>());
        output = 0;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof HyperNaquadahReactorMachine r)) return ModifierFunction.NULL;
        if (recipe.data.getInt(TIER) > r.spec.tier())
            return ModifierFunction.cancel(Component.literal("Needs the Advanced Hyper Naquadah Reactor"));
        FluidStack boost = r.spec.boost().get();
        boolean boosted = boost != null && !boost.isEmpty() && r.drain(boost, true);
        int max = boosted ? r.spec.boostedParallels() : r.spec.parallels();
        int p = ParallelLogic.getParallelAmount(machine, recipe, max);
        if (p <= 0) return ModifierFunction.NULL;
        r.boosted = boosted;
        r.parallels = p;
        r.powerPerParallel = recipe.data.getLong(POWER) * (boosted ? r.spec.boostPowerMult() : 1);
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(p))
                .durationMultiplier(boosted ? 1.0 / r.spec.boostSpeedMult() : 1.0)
                .parallels(p).build();
    }

    @Override
    public boolean onWorking() {
        boolean ok = super.onWorking();
        if (boosted && getOffsetTimer() % 20 == 0) {
            FluidStack boost = spec.boost().get();
            if (!drain(boost, false)) {
                // boost ran out mid-run: back to the plain rate
                boosted = false;
                powerPerParallel /= Math.max(1, spec.boostPowerMult());
            }
        }
        long want = powerPerParallel * parallels;
        if (want < 0 || (parallels != 0 && want / parallels != powerPerParallel)) want = Long.MAX_VALUE;
        output = want;
        long put = want > 0 ? outputs.changeEnergy(want) : 0;
        lastVoided = Math.max(0, want - put);
        return ok;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        output = 0;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Generating: " + com.omnicrox.apexlib.util.ApexFormat.num(output) + " EU/t").withStyle(ChatFormatting.GOLD));
        textList.add(Component.literal("Parallels: " + parallels + " (max " + spec.parallels() + ", " + spec.boostedParallels() + " boosted)")
                .withStyle(ChatFormatting.AQUA));
        FluidStack b = spec.boost().get();
        textList.add(Component.literal("Boost: " + (boosted ? "ON" : "off") + (b == null || b.isEmpty() ? "" :
                " (" + b.getAmount() + " L/s " + b.getDisplayName().getString() + ": x" + spec.boostPowerMult() + " power, x" +
                        spec.boostSpeedMult() + " speed)")).withStyle(boosted ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY));
        if (lastVoided > 0) textList.add(Component.literal("Output hatches full: " + com.omnicrox.apexlib.util.ApexFormat.num(lastVoided) + " EU/t lost")
                .withStyle(ChatFormatting.RED));
    }
}
