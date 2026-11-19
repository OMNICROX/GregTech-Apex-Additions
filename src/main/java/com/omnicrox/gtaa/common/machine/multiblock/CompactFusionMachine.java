package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Compact Fusion Computer MK-I .. MK-V. Port of GoodGenerator's MTELargeFusionComputer1-5
 * (GT5-Unofficial, LGPL-3.0):
 * <ul>
 * <li>runs the normal fusion recipes with 64 parallels, multiplied for recipes far below its tier</li>
 * <li>up to 32 energy hatches, each adds 1/32 of the startup buffer</li>
 * <li>fusion overclocks (x2 speed for x2 EU; MK-IV and MK-V x4 / x4), one per fusion tier above the recipe</li>
 * </ul>
 * Heat / startup handling reuses GTCEu's {@link FusionReactorMachine}.
 */
public class CompactFusionMachine extends FusionReactorMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CompactFusionMachine.class, FusionReactorMachine.MANAGED_FIELD_HOLDER);

    public static final int MAX_ENERGY_HATCHES = 32;
    public static final int BASE_PARALLEL = 64;

    private final int mk;
    @DescSynced private int lastParallel;

    public CompactFusionMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        this.mk = tier - GTValues.LuV + 1;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getMk() {
        return mk;
    }

    /** GTNH capableStartupCanonical */
    public static long capableStartup(int mk) {
        return switch (mk) {
            case 1 -> 160_000_000L;
            case 2 -> 320_000_000L;
            case 3 -> 640_000_000L;
            case 4 -> 5_120_000_000L;
            default -> 20_480_000_000L;
        };
    }

    /** Fusion tier (MK) a recipe needs, from its startup energy (GTNH FusionSpecialValueFormatter). */
    public static int recipeMk(long startEU, long eut) {
        int byEnergy;
        if (startEU <= 160_000_000L) byEnergy = 1;
        else if (startEU <= 320_000_000L) byEnergy = 2;
        else if (startEU <= 640_000_000L) byEnergy = 3;
        else if (startEU <= 5_120_000_000L) byEnergy = 4;
        else byEnergy = 5;
        int byVoltage = Math.max(1, GTValues.LuV <= 0 ? 1 : RecipeHelperTier.tier(eut) - GTValues.LuV + 1);
        return Math.max(byEnergy, byVoltage);
    }

    /** GTNH extraPara: extra parallel multiplier for low-energy recipes. */
    public static int extraPara(int mk, long startEU) {
        int recipeMk = recipeMk(startEU, 0);
        return Math.max(1, mk - recipeMk + 1);
    }

    public static double ocFactor(int mk) {
        return mk >= 4 ? 4.0 : 2.0;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        int hatches = 0;
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() == IO.OUT) continue;
                for (var h : handler.getCapability(EURecipeCapability.CAP)) {
                    if (h instanceof IEnergyContainer) hatches++;
                }
            }
        }
        energyContainer.resetBasicInfo(
                capableStartup(mk) * Math.min(MAX_ENERGY_HATCHES, hatches) / MAX_ENERGY_HATCHES, 0, 0, 0, 0);
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof CompactFusionMachine cf)) return ModifierFunction.NULL;
        // startup / heat checks and energy draw from GTCEu (also rejects recipes above this voltage)
        ModifierFunction base = FusionReactorMachine.recipeModifier(machine, recipe);
        if (base == ModifierFunction.NULL) return base;
        GTRecipe checked = base.apply(recipe);
        if (checked == null) return base;

        long startEU = recipe.data.getLong("eu_to_start");
        var euIO = RecipeHelper.getRealEUtWithIO(recipe);
        long eut = Math.max(1, euIO.isInput() ? euIO.stack().getTotalEU() : 0);
        long voltage = GTValues.V[cf.getTier()];
        int maxPara = BASE_PARALLEL * extraPara(cf.mk, startEU);
        // energy budget: GTNH gives each of the 32 hatch slots V * 64 * extraPara(MK-I recipe) / 32
        long budget = voltage * BASE_PARALLEL * extraPara(cf.mk, 0);
        long input = cf.inputEnergyContainers == null ? 0 :
                cf.inputEnergyContainers.getInputVoltage() * cf.inputEnergyContainers.getInputAmperage();
        budget = Math.min(budget, Math.max(input, voltage));
        int byEnergy = (int) Math.max(1, Math.min(maxPara, budget / eut));
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, byEnergy);
        if (parallels <= 0) return ModifierFunction.NULL;

        // fusion overclocks: one per MK above the recipe, only while the energy budget allows
        int maxOc = Math.max(0, cf.mk - recipeMk(startEU, eut));
        double f = ocFactor(cf.mk);
        int ocs = 0;
        double eutMult = parallels;
        double durMult = 1;
        while (ocs < maxOc && eut * eutMult * f <= budget && recipe.duration * durMult / f >= 1) {
            eutMult *= f;
            durMult /= f;
            ocs++;
        }
        cf.lastParallel = parallels;
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier(eutMult)
                .durationMultiplier(durMult)
                .parallels(parallels)
                .build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Compact Fusion MK-" + roman(mk) + " | Parallels: " + lastParallel)
                .withStyle(ChatFormatting.AQUA));
    }

    public static String roman(int mk) {
        return switch (mk) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            default -> "V";
        };
    }

    /** voltage tier of an EU/t value */
    private static final class RecipeHelperTier {
        static int tier(long eut) {
            if (eut <= 0) return 0;
            for (int t = 0; t < GTValues.V.length; t++) if (GTValues.V[t] >= eut) return t;
            return GTValues.MAX;
        }
    }
}
