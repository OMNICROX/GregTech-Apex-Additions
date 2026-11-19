package com.omnicrox.gtaa.common.machine.multiblock.naquadah;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Naquadah Fuel Refinery. Port of GoodGenerator's MTEFuelRefineFactory (GT5-Unofficial, LGPL-3.0):
 * Field Restriction Coils T1-T4 set the tier; recipes need a minimum coil tier; 4 parallels per coil tier;
 * one 4/4 perfect overclock per coil tier above the recipe.
 */
public class NaquadahFuelRefineryMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            NaquadahFuelRefineryMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX_COIL = "NFRCoilTier";
    public static final String COIL_TIER = "nfr_coil_tier";

    @DescSynced private int coilTier;

    public NaquadahFuelRefineryMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static TraceabilityPredicate coils() {
        return new TraceabilityPredicate(s -> {
            int tier = GTAABlocks.fieldRestrictionCoilTier(s.getBlockState().getBlock());
            if (tier == 0) return false;
            Object current = s.getMatchContext().getOrPut(CTX_COIL, tier);
            if (!current.equals(tier)) {
                s.setError(new PatternStringError("Field Restriction Coils must all be the same tier"));
                return false;
            }
            return true;
        }, () -> Arrays.stream(GTAABlocks.fieldRestrictionCoils())
                .map(b -> BlockInfo.fromBlockState(b.defaultBlockState())).toArray(BlockInfo[]::new));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object t = getMultiblockState().getMatchContext().get(CTX_COIL);
        coilTier = t instanceof Integer i ? i : 1;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        coilTier = 0;
    }

    public int getCoilTier() {
        return coilTier;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof NaquadahFuelRefineryMachine nfr)) return ModifierFunction.NULL;
        int need = recipe.data.getInt(COIL_TIER);
        if (need > nfr.coilTier) {
            return ModifierFunction.cancel(Component.literal("Needs Field Restriction Coil T" + need));
        }
        var euIO = RecipeHelper.getRealEUtWithIO(recipe);
        long eut = Math.max(1, euIO.isInput() ? euIO.stack().getTotalEU() : 0);
        var energy = nfr.getEnergyContainer();
        long budget = energy == null ? 0 : energy.getInputVoltage() * energy.getInputAmperage();
        if (budget < eut) return ModifierFunction.NULL;
        int maxPara = 4 * Math.max(1, nfr.coilTier);
        int byEnergy = (int) Math.max(1, Math.min(maxPara, budget / eut));
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, byEnergy);
        if (parallels <= 0) return ModifierFunction.NULL;
        int maxOc = Math.max(0, nfr.coilTier - need);
        int ocs = 0;
        double eutMult = parallels, durMult = 1;
        while (ocs < maxOc && eut * eutMult * 4 <= budget && recipe.duration * durMult / 4 >= 1) {
            eutMult *= 4;
            durMult /= 4;
            ocs++;
        }
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier(eutMult).durationMultiplier(durMult).parallels(parallels).build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) textList.add(Component.literal("Field Restriction Coil: T" + coilTier + " (" +
                4 * Math.max(1, coilTier) + " parallels)").withStyle(ChatFormatting.AQUA));
    }
}
