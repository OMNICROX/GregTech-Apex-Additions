package com.omnicrox.gtaa.common.machine.multiblock.gtnl;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.compat.ApexWireless;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.util.ApexFormat;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import com.omnicrox.gtaa.common.machine.multiblock.QuantumForceTransformerMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Real Annihilation Generator (GT-Not-Leisure "RealArtificialStar"). Every 5 s it annihilates all the antimatter in
 * its input hatches (no matter needed) straight into the owner's wireless EU network: 1e12 EU per mB, times
 * (1 + (time + spacetime + stabilisation field tiers) / 9), times up to 2 for continuous running (+1% a cycle,
 * lost when a cycle finds no antimatter).
 *
 * This file is subject to the additional terms in LICENSE-ADDITIONAL-TERMS.md (GT-Not-Leisure).
 */
public class RealAnnihilationGeneratorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            RealAnnihilationGeneratorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX_TIME = "RAGTime", CTX_SPACE = "RAGSpace", CTX_STAB = "RAGStab";
    public static final double EU_PER_MB = 1e12;
    public static final int CYCLE = 100;

    @Persisted @DescSynced private int streak;
    @DescSynced private int tierSum;
    @DescSynced private long lastEU;
    @DescSynced private String problem = "";
    private TickableSubscription tick;

    public RealAnnihilationGeneratorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private static Block[] tiers(String prefix) {
        Block[] out = new Block[9];
        for (int i = 0; i < 9; i++) {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("gtna", prefix + i));
            out[i] = b == null ? Blocks.BEDROCK : b;
        }
        return out;
    }

    public static TraceabilityPredicate timeFields() {
        return QuantumForceTransformerMachine.tiered(CTX_TIME, () -> tiers("time_acceleration_field_generator_tier_"), "Time field generators");
    }

    public static TraceabilityPredicate spaceFields() {
        return QuantumForceTransformerMachine.tiered(CTX_SPACE, () -> tiers("spacetime_compression_field_generator_tier_"), "Spacetime field generators");
    }

    public static TraceabilityPredicate stabFields() {
        return QuantumForceTransformerMachine.tiered(CTX_STAB, () -> tiers("stabilisation_field_generator_tier_"), "Stabilisation field generators");
    }

    private int ctx(String key) {
        Object o = getMultiblockState().getMatchContext().get(key);
        return o instanceof Integer i ? i : 0;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        tierSum = ctx(CTX_TIME) + ctx(CTX_SPACE) + ctx(CTX_STAB);
        if (!isRemote()) tick = subscribeServerTick(tick, this::cycle);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    private void cycle() {
        if (getOffsetTimer() % CYCLE != 0 || !isWorkingEnabled()) return;
        var am = GTAAMaterials.ANTIMATTER == null ? null : GTAAMaterials.ANTIMATTER.getFluid();
        if (am == null) return;
        long mb = 0;
        for (var t : ApexHatches.fluidTanks(this, IO.IN))
            mb += t.drainInternal(new FluidStack(am, Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE).getAmount();
        if (mb <= 0) {
            streak = 0;
            lastEU = 0;
            problem = "No antimatter in the input hatches";
            return;
        }
        streak = Math.min(100, streak + 1);
        double eu = mb * EU_PER_MB * (1 + tierSum / 9.0) * (1 + streak / 100.0);
        long out = eu >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) eu;
        if (!ApexWireless.isAvailable()) problem = "Wireless EU network not available";
        else if (getOwnerUUID() == null) problem = "This controller has no owner: break and place it yourself";
        else {
            lastEU = ApexWireless.addEU(getOwnerUUID(), out, this, true);
            problem = "";
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Field tiers " + tierSum + " / 27  |  continuous x" + String.format("%.2f", 1 + streak / 100.0))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        textList.add(Component.literal("Last cycle: " + ApexFormat.num(lastEU) + " EU to the wireless network").withStyle(ChatFormatting.AQUA));
        if (!problem.isEmpty()) textList.add(Component.literal(problem).withStyle(ChatFormatting.RED));
    }
}
