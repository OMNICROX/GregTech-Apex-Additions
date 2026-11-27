package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Miracle Top (Twist Space Technology): circuit assembler with 2-16 rings. 128 parallels per ring,
 * speed 400% per ring, perfect overclocks from 8 rings.
 */
public class MiracleTopMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MiracleTopMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX = "MiracleTopRings";

    @DescSynced private int rings;

    public MiracleTopMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static final int MIN_RINGS = 2, MAX_RINGS = 16;
    private int matched = MIN_RINGS;

    /** Twist's structure: main piece, 0-14 middle pieces, end piece = 2-16 rings; the longest that matches wins */
    @Override
    public boolean checkPattern() {
        for (int r = MAX_RINGS; r >= MIN_RINGS; r--) {
            com.gregtechceu.gtceu.api.pattern.BlockPattern p =
                    com.omnicrox.gtaa.common.data.GTAAGTNHMachines.miracleTopPattern(getDefinition(), r);
            if (p != null && p.checkPatternAt(getMultiblockState(), false)) {
                matched = r;
                return true;
            }
        }
        return false;
    }

    @Override
    public com.gregtechceu.gtceu.api.pattern.BlockPattern getPattern() {
        return com.omnicrox.gtaa.common.data.GTAAGTNHMachines.miracleTopPattern(getDefinition(), matched);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        rings = matched;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof MiracleTopMachine mt)) return ModifierFunction.NULL;
        int r = Math.max(1, mt.rings);
        ModifierFunction par = ApexModifiers.parallel(m -> 128 * r, m -> 1.0 / (4.0 * r), 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        var oc = r >= 8 ? GTRecipeModifiers.OC_PERFECT : GTRecipeModifiers.OC_NON_PERFECT;
        return par.andThen(oc.getModifier(machine, par.apply(recipe)));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) textList.add(Component.literal(rings + " rings: " + 128 * rings + " parallels, " + 400 * rings +
                "% speed" + (rings >= 8 ? ", perfect overclocks" : "")).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
