package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * General Ore Processing Factory (Twist Space Technology "TST_OreProcessingFactory"): runs the Integrated Ore Processor
 * recipes (GTNA's ore_processing list). No overclocks: every run takes 6.4 s and processes as many ores as the power
 * allows. Uses 3200 mB of Lubricant every 12.8 s while it works.
 */
public class OreProcessingFactoryMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            OreProcessingFactoryMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final int RUN_TICKS = 128, LUBE_TICKS = 256, LUBE_MB = 3200;

    @DescSynced private boolean dry;
    private TickableSubscription tick;

    public OreProcessingFactoryMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) tick = subscribeServerTick(tick, () -> {
            if (getOffsetTimer() % LUBE_TICKS != 0 || !getRecipeLogic().isWorking()) return;
            dry = !ApexHatches.drain(this, GTMaterials.Lubricant.getFluid(LUBE_MB));
            if (dry) getRecipeLogic().interruptRecipe();
        });
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (machine instanceof OreProcessingFactoryMachine m && !m.dry && !ApexHatches.has(m, GTMaterials.Lubricant.getFluid(1)))
            return ModifierFunction.cancel(Component.literal("Needs Lubricant"));
        ModifierFunction par = ApexModifiers.parallel(x -> Integer.MAX_VALUE, x -> 1.0, 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        double fix = RUN_TICKS / (double) Math.max(1, recipe.duration);
        return par.andThen(ModifierFunction.builder().durationMultiplier(fix).build());
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed() && dry) textList.add(Component.literal("Out of Lubricant (3200 mB every 12.8 s)").withStyle(ChatFormatting.RED));
    }
}
