package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IRotorHolderMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Spinmatron-2737 (GT5-Unofficial "MTESpinmatron"), standard mode: centrifuge, 4 parallels per total turbine tier
 * (the harvest level of every rotor in its rotor holders), 300% speed, 70% EU. Burns recipe tier x 10 mB of Diesel a
 * second (GTNH: Kerosene, which this pack does not have).
 */
public class SpinmatronMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SpinmatronMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @DescSynced private int turbineTier;
    @DescSynced private boolean dry;
    private int runningTier;
    private TickableSubscription tick;

    public SpinmatronMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int turbineTier() {
        int sum = 0;
        for (IMultiPart part : getParts()) {
            if (!(part instanceof IRotorHolderMachine holder) || !holder.hasRotor()) continue;
            var mat = holder.getRotorMaterial();
            int level = 1;
            try {
                if (mat != null && mat.hasProperty(PropertyKey.TOOL)) level = Math.max(1, mat.getProperty(PropertyKey.TOOL).getHarvestLevel());
            } catch (Throwable ignored) {}
            sum += level;
        }
        return sum;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) tick = subscribeServerTick(tick, () -> {
            if (getOffsetTimer() % 20 != 0) return;
            turbineTier = turbineTier();
            if (!getRecipeLogic().isWorking()) return;
            dry = !ApexHatches.drain(this, GTMaterials.Diesel.getFluid(Math.max(1, runningTier) * 10));
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
        if (!(machine instanceof SpinmatronMachine s)) return ModifierFunction.NULL;
        int tier = com.gregtechceu.gtceu.utils.GTUtil.getTierByVoltage(Math.max(1, RecipeHelper.getRealEUtWithIO(recipe).stack().getTotalEU()));
        if (!ApexHatches.has(s, GTMaterials.Diesel.getFluid(Math.max(1, tier) * 10)))
            return ModifierFunction.cancel(Component.literal("Needs " + Math.max(1, tier) * 10 + " mB/s of Diesel"));
        s.runningTier = tier;
        int max = Math.max(1, 4 * s.turbineTier());
        ModifierFunction par = ApexModifiers.parallel(m -> max, m -> 1.0 / 3, 0.7).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        GTRecipe scaled = par.apply(recipe);
        return scaled == null ? ModifierFunction.NULL : par.andThen(GTRecipeModifiers.OC_NON_PERFECT_SUBTICK.getModifier(machine, scaled));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Total turbine tier " + turbineTier + "  |  " + Math.max(1, 4 * turbineTier) + " parallels")
                .withStyle(ChatFormatting.AQUA));
        if (dry) textList.add(Component.literal("Out of Diesel").withStyle(ChatFormatting.RED));
    }
}
