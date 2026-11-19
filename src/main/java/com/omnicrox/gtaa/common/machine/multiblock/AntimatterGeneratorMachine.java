package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import com.omnicrox.gtaa.common.machine.multiblock.AntimatterMachineHelper.StoredFluid;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

/**
 * Shielded Lagrangian Annihilation Matrix (SLAM / Antimatter Generator).
 * Logic ported from GoodGenerator's AntimatterGenerator (GT5-Unofficial / GT New Horizons):
 * every 5 seconds it annihilates ALL fluid in its input hatches; with exactly antimatter + one matter fluid it
 * makes A * antimatter^E EU (A = 1e12 EU/L) scaled by min(AM/M, M/AM). Invalid fluids are voided.
 */
public class AntimatterGeneratorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AntimatterGeneratorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final double ANTIMATTER_FUEL_VALUE = 1e12;
    private static final int CYCLE_TICKS = 100;

    @Persisted @DescSynced private long euLastCycle;
    @Persisted @DescSynced private float efficiency;
    @Persisted @DescSynced private float avgEfficiency;
    private final List<Float> history = new ArrayList<>();
    private int historyIndex;
    private TickableSubscription tickSub;

    public AntimatterGeneratorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) tickSub = subscribeServerTick(tickSub, this::antimatterTick);
    }

    @Persisted @DescSynced private boolean wireless = false;

    public boolean isWireless() {
        return wireless;
    }

    @Override
    protected net.minecraft.world.InteractionResult onScrewdriverClick(net.minecraft.world.entity.player.Player player,
                                                                         net.minecraft.world.InteractionHand hand,
                                                                         net.minecraft.core.Direction gridSide,
                                                                         net.minecraft.world.phys.BlockHitResult hitResult) {
        if (isRemote()) return net.minecraft.world.InteractionResult.SUCCESS;
        if (!com.omnicrox.gtaa.api.GTAAWireless.isAvailable()) {
            player.displayClientMessage(Component.literal("Wireless mode needs GTM Things installed"), true);
            return net.minecraft.world.InteractionResult.CONSUME;
        }
        wireless = !wireless;
        player.displayClientMessage(Component.literal(wireless ? "Wireless mode enabled" : "Wireless mode disabled"), true);
        if (wireless) player.displayClientMessage(Component.literal(
                "Wireless mode needs a UMV superconductor base catalyst; EU goes to your wireless network"), false);
        return net.minecraft.world.InteractionResult.CONSUME;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tickSub != null) {
            tickSub.unsubscribe();
            tickSub = null;
        }
    }

    private void antimatterTick() {
        if (!isFormed() || !getRecipeLogic().isWorkingEnabled()) return;
        if (getOffsetTimer() % CYCLE_TICKS == 0) cycle();
    }

    private void cycle() {
        if (GTAAMaterials.ANTIMATTER == null) return;
        Fluid antimatterFluid = GTAAMaterials.ANTIMATTER.getFluid();
        List<StoredFluid> inputs = AntimatterMachineHelper.inputFluids(this);
        long antimatter = 0;
        StoredFluid catalyst = null;
        long catalystAmount = 0;
        int count = 0;
        for (StoredFluid f : inputs) {
            if (f.stack().getFluid() == antimatterFluid) antimatter += f.stack().getAmount();
            else {
                catalyst = f;
                catalystAmount = f.stack().getAmount();
            }
            count++;
        }
        Fluid catalystFluid = catalyst == null ? null : catalyst.stack().getFluid();
        // everything is annihilated, valid or not
        for (StoredFluid f : inputs) AntimatterMachineHelper.drainAll(f);

        if (count == 2 && antimatter > 0 && catalystFluid != null) {
            createEU(antimatter, catalystFluid, catalystAmount);
        } else if (count > 0) {
            euLastCycle = 0;
            updateEfficiency(0f);
        }
    }

    private static float modifierFor(Fluid fluid) {
        if (fluid == GTMaterials.Copper.getFluid()) return 1.00f;
        if (fluid == AntimatterMachineHelper.fluidOf("gtna:superconductor_uiv_base")) return 1.02f;
        if (fluid == AntimatterMachineHelper.fluidOf("gtna:superconductor_umv_base")) return 1.03f;
        return 0f;
    }

    private void createEU(long antimatter, Fluid catalyst, long catalystAmount) {
        float modifier = modifierFor(catalyst);
        if (modifier == 0f) {
            euLastCycle = 0;
            updateEfficiency(0f);
            return;
        }
        float eff = Math.min((float) antimatter / catalystAmount, (float) catalystAmount / antimatter);
        updateEfficiency(eff);
        double raw = Math.pow(antimatter, modifier) * ANTIMATTER_FUEL_VALUE * eff;
        long eu = raw >= Long.MAX_VALUE || Double.isNaN(raw) ? Long.MAX_VALUE : (long) raw;
        euLastCycle = eu;

        // split across the dynamo / laser source hatches; anything they can't hold is voided
        List<IEnergyContainer> outputs = new ArrayList<>();
        for (IRecipeHandler<?> h : getCapabilitiesFlat(IO.OUT, EURecipeCapability.CAP)) {
            if (h instanceof IEnergyContainer c) outputs.add(c);
        }
        if (outputs.isEmpty()) return;
        // GTNH wireless mode: with a UMV-grade catalyst the EU goes to the owner's wireless network instead,
        // clamped to what the hatches could hold so wireless can't bypass them
        if (wireless && modifier >= 1.03f && com.omnicrox.gtaa.api.GTAAWireless.isAvailable()) {
            long cap = 0;
            for (IEnergyContainer c : outputs) {
                cap += c.getEnergyCapacity();
                if (cap < 0) { cap = Long.MAX_VALUE; break; }
            }
            long sent = Math.min(eu, cap);
            euLastCycle = sent;
            com.omnicrox.gtaa.api.GTAAWireless.addEU(getOwnerUUID(), sent, this);
            return;
        }
        long perHatch = eu / outputs.size();
        for (IEnergyContainer c : outputs) {
            long space = c.getEnergyCapacity() - c.getEnergyStored();
            if (space > 0) c.changeEnergy(Math.min(space, perHatch));
        }
    }

    private void updateEfficiency(float value) {
        efficiency = value;
        if (history.size() < 10) history.add(value);
        else {
            history.set(historyIndex, value);
            historyIndex = (historyIndex + 1) % 10;
        }
        float sum = 0;
        for (float f : history) sum += f;
        avgEfficiency = history.isEmpty() ? 0 : sum / history.size();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(getRecipeLogic().isWorkingEnabled(), getRecipeLogic().isWorkingEnabled() && isFormed())
                .addCustom(t -> {
                    t.add(Component.literal("Last cycle: " + FormattingUtil.formatNumbers(euLastCycle) + " EU")
                            .withStyle(ChatFormatting.GOLD));
                    t.add(Component.literal("Efficiency: " + Math.round(efficiency * 100) + "% (avg " +
                            Math.round(avgEfficiency * 100) + "%)").withStyle(ChatFormatting.AQUA));
                    t.add(Component.literal("Power destination: " + (wireless ? "Wireless network" : "Hatches"))
                            .withStyle(wireless ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY));
                });
    }
}
