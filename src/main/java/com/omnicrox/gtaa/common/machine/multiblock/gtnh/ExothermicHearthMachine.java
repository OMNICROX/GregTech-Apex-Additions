package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Exothermic Hearth (GT5-Unofficial "MTEExothermicHearth"): a mega blast furnace. 256 parallels, growing to 512 over
 * 30 minutes of constant running (6x faster while it is fed 250-500 mB/s of molten Blaze, this pack's stand-in for
 * Blazing Pyrotheum); it cools back down when idle. Normal EBF heat rules: +100 K per voltage tier above MV, 5% less
 * EU per 900 K above the recipe and a perfect overclock per 1800 K above it.
 */
public class ExothermicHearthMachine extends CoilWorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ExothermicHearthMachine.class, CoilWorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int BASE_PARALLEL = 256;
    private static final float MAX_MULT = 2f;
    private static final int HEATUP_MINUTES = 30, INTERVAL = 100, PYRO_SPEEDUP = 6, PYRO_DRAIN = 250;
    private static final float STEP = (MAX_MULT - 1) / (HEATUP_MINUTES * 60f * 20 / INTERVAL);

    @Persisted @DescSynced
    private float multiplier = 1f;
    @DescSynced
    private boolean pyro;
    private TickableSubscription tick;

    public ExothermicHearthMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int maxParallel() {
        return (int) Math.floor(multiplier * BASE_PARALLEL);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) tick = subscribeServerTick(tick, this::heatTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    /** drains this second's blaze if there is enough of it; false (and nothing drained) otherwise */
    private boolean drainBlaze(int mb) {
        var blaze = GTMaterials.Blaze.getFluid(mb);
        long have = 0;
        List<NotifiableFluidTank> tanks = ApexHatches.fluidTanks(this, IO.IN);
        for (var t : tanks)
            have += t.drainInternal(new FluidStack(blaze, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE).getAmount();
        if (have < mb) return false;
        int left = mb;
        for (var t : tanks) {
            left -= t.drainInternal(new FluidStack(blaze, left), IFluidHandler.FluidAction.EXECUTE).getAmount();
            if (left <= 0) break;
        }
        return true;
    }

    private void heatTick() {
        long t = getOffsetTimer();
        if (getRecipeLogic().isWorking()) {
            if (t % 20 == 0) pyro = drainBlaze((int) Math.floor(PYRO_DRAIN * multiplier));
            if (t % INTERVAL == 0 && multiplier < MAX_MULT)
                multiplier = Math.min(MAX_MULT, multiplier + (pyro ? STEP * PYRO_SPEEDUP : STEP));
        } else if (t % 20 == 0 && multiplier > 1) {
            pyro = false;
            multiplier = Math.max(1f, multiplier - STEP);
        }
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof ExothermicHearthMachine h)) return ModifierFunction.NULL;
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, h.maxParallel());
        if (parallels <= 0) return ModifierFunction.NULL;
        ModifierFunction par = parallels == 1 ? ModifierFunction.IDENTITY : ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels)).parallels(parallels).build();
        GTRecipe scaled = par.apply(recipe);
        if (scaled == null) return ModifierFunction.NULL;
        return par.andThen(GTRecipeModifiers.ebfOverclock(machine, scaled));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Parallels: " + maxParallel() + String.format(" (x%.2f heat-up)", multiplier))
                .withStyle(ChatFormatting.AQUA));
        if (pyro) textList.add(Component.literal("Molten Blaze: heating up 6x faster").withStyle(ChatFormatting.GOLD));
    }
}
