package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Mass Fabricator Genesis (Twist Space Technology, by Nxer): makes UU-Matter out of energy. Twist's modular Parallel
 * Controllers are GregTech parallel hatches here, and like Twist's they add up (any number of them). Perfect overclocks.
 */
public class MassFabricatorGenesisMachine extends WorkableElectricMultiblockMachine {

    public MassFabricatorGenesisMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public int totalParallel() {
        long p = 0;
        for (IMultiPart part : getParts())
            if (part instanceof IParallelHatch h) p += h.getCurrentParallel();
        return (int) Math.max(1, Math.min(Integer.MAX_VALUE, p));
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof MassFabricatorGenesisMachine m)) return ModifierFunction.NULL;
        int max = m.totalParallel();
        ModifierFunction par = ApexModifiers.parallel(x -> max, x -> 1.0, 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        return par.andThen(GTRecipeModifiers.OC_PERFECT_SUBTICK.getModifier(machine, par.apply(recipe)));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) textList.add(Component.literal("Parallel hatches: up to " + totalParallel() + " parallels")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
