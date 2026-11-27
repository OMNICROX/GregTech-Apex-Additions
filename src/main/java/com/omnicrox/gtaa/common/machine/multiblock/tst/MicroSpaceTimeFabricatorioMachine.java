package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Micro SpaceTime Fabricatorio (Twist Space Technology "TST_MicroSpaceTimeFabricatorio"): 16 parallels. A
 * Stabilisation Field Generator (field tier 3+) enables perfect overclocks; every tier above that divides the time.
 */
public class MicroSpaceTimeFabricatorioMachine extends CrystallineInfinitierMachine {

    public MicroSpaceTimeFabricatorioMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public int maxParallel() {
        return 16;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof MicroSpaceTimeFabricatorioMachine m)) return ModifierFunction.NULL;
        double speed = 1.0 / Math.max(1, m.fieldTier - 2);
        ModifierFunction par = ApexModifiers.parallel(x -> 16, x -> speed, 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        GTRecipe scaled = par.apply(recipe);
        if (scaled == null) return ModifierFunction.NULL;
        var oc = m.fieldTier >= 3 ? GTRecipeModifiers.OC_PERFECT_SUBTICK : GTRecipeModifiers.OC_NON_PERFECT_SUBTICK;
        return par.andThen(oc.getModifier(machine, scaled));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed() && fieldTier > 3)
            textList.add(Component.literal("Time / " + (fieldTier - 2)).withStyle(ChatFormatting.GOLD));
    }
}
