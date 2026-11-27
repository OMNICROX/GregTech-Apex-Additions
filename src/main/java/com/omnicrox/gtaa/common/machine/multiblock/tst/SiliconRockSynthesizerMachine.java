package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Silicon Rock Synthesizer (Twist Space Technology "MegaStoneBreaker"): runs the Industrial Rock Breaker recipes.
 * Base parallel 4, x4 per voltage tier above LV (imperfect overclock worth of parallel), output x4; with at least a
 * bucket of water and a bucket of lava in the input hatches (not used up) the output is x1024 instead.
 */
public class SiliconRockSynthesizerMachine extends WorkableElectricMultiblockMachine {

    public SiliconRockSynthesizerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static int maxParallel(MetaMachine m) {
        int tier = Math.max(1, ApexModifiers.tier(m));
        return (int) Math.min(1 << 30, 4L << (2L * Math.min(14, tier - 1)));
    }

    /** true when the input hatches hold at least 1000 mB of water and 1000 mB of lava */
    public boolean boosted() {
        return ApexHatches.amount(this, Fluids.WATER) >= 1000 && ApexHatches.amount(this, Fluids.LAVA) >= 1000;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof SiliconRockSynthesizerMachine s)) return ModifierFunction.NULL;
        ModifierFunction par = ApexModifiers.parallel(SiliconRockSynthesizerMachine::maxParallel, m -> 1.0, 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        ModifierFunction out = ModifierFunction.builder().outputModifier(ContentModifier.multiplier(s.boosted() ? 1024 : 4)).build();
        ModifierFunction both = par.andThen(out);
        return both.andThen(GTRecipeModifiers.OC_NON_PERFECT.getModifier(machine, both.apply(recipe)));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.literal("Parallels: " + maxParallel(this)).withStyle(ChatFormatting.AQUA));
            textList.add(boosted() ? Component.literal("Water + lava: output x1024").withStyle(ChatFormatting.GOLD)
                    : Component.literal("Output x4 (add water and lava for x1024)").withStyle(ChatFormatting.GRAY));
        }
    }
}
