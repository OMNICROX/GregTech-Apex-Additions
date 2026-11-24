package com.omnicrox.gtaa.common.machine.multiblock.naquadah;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Fuel Refining Complex, after GT Not Leisure's (GTNH addon): the bulk route to naquadah fuel. Light and heavy
 * naquadah fuel plus atomic separation catalyst and the tier's extras refine straight into Naquadah Fuel MkI-MkVI.
 * Heating coils give heat (coil temperature + 100 K per voltage tier above MV, like the EBF); Field Restriction
 * Coils set the fuel tier it can make. Every 900 K over a recipe's heat is one perfect overclock; parallels =
 * 4 per Field Restriction tier.
 */
public class FuelRefiningComplexMachine extends CoilWorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            FuelRefiningComplexMachine.class, CoilWorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String HEAT = "frc_heat";

    @DescSynced private int fieldTier;
    @DescSynced private int heat;

    public FuelRefiningComplexMachine(IMachineBlockEntity holder, Object... args) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object t = getMultiblockState().getMatchContext().get(NaquadahFuelRefineryMachine.CTX_COIL);
        fieldTier = t instanceof Integer i ? i : 1;
        heat = currentHeat();
    }

    private int currentHeat() {
        int coil = getCoilType() == null ? 0 : getCoilType().getCoilTemperature();
        int tier = getEnergyContainer() == null ? 0 : com.gregtechceu.gtceu.utils.GTUtil.getTierByVoltage(getEnergyContainer().getInputVoltage());
        return coil + 100 * Math.max(0, tier - GTValues.MV);
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof FuelRefiningComplexMachine m)) return ModifierFunction.NULL;
        int needField = recipe.data.getInt(NaquadahFuelRefineryMachine.COIL_TIER);
        if (needField > m.fieldTier) return ModifierFunction.cancel(Component.literal("Needs Field Restriction Coil T" + needField));
        m.heat = m.currentHeat();
        int needHeat = recipe.data.getInt(HEAT);
        if (needHeat > m.heat) return ModifierFunction.cancel(Component.literal("Needs " + needHeat + " K (have " + m.heat + " K)"));
        var euIO = RecipeHelper.getRealEUtWithIO(recipe);
        long eut = Math.max(1, euIO.isInput() ? euIO.stack().getTotalEU() : 0);
        var energy = m.getEnergyContainer();
        long budget = energy == null ? 0 : energy.getInputVoltage() * energy.getInputAmperage();
        if (budget < eut) return ModifierFunction.NULL;
        int maxPara = 4 * Math.max(1, m.fieldTier);
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, (int) Math.max(1, Math.min(maxPara, budget / eut)));
        if (parallels <= 0) return ModifierFunction.NULL;
        int perfect = Math.max(0, (m.heat - needHeat) / 900);
        double eutMult = parallels, durMult = 1;
        int ocs = 0;
        while (eut * eutMult * 4 <= budget && recipe.duration * durMult / 4 >= 1) {
            eutMult *= 4;
            durMult /= ocs < perfect ? 4 : 2;
            ocs++;
        }
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier(eutMult).durationMultiplier(durMult).parallels(parallels).build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        heat = currentHeat();
        textList.add(Component.literal("Heat: " + com.omnicrox.gtaa.api.GTAAFormat.num(heat) + " K").withStyle(ChatFormatting.RED));
        textList.add(Component.literal("Field Restriction Coil: T" + fieldTier + " (" + 4 * Math.max(1, fieldTier) + " parallels)")
                .withStyle(ChatFormatting.AQUA));
    }
}
