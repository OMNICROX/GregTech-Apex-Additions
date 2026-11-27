package com.omnicrox.gtaa.common.machine.multiblock.gtnl;

import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.omnicrox.gtaa.common.machine.multiblock.DraconicFusionCrafterMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Draconic Fusion Crafting (GT-Not-Leisure "DraconicFusionCrafting"): the large fusion crafter. 8 parallels (more with
 * a Parallel Hatch), 200% speed, 50% EU, and perfect overclocks with Chaotic (tier 4) core casings. Core tiers above a
 * recipe still halve its time, like the KubaTech crafter.
 *
 * This file is subject to the additional terms in LICENSE-ADDITIONAL-TERMS.md (GT-Not-Leisure).
 */
public class DraconicFusionCraftingMachine extends DraconicFusionCrafterMachine {

    public static final int BASE_PARALLEL = 8;

    public DraconicFusionCraftingMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    /** no separate fusion casing check: the cores alone set the tier */
    @Override
    public int effectiveTier() {
        return coreTier;
    }

    public int maxParallel() {
        int max = BASE_PARALLEL;
        for (IMultiPart part : getParts())
            if (part instanceof IParallelHatch hatch) max = Math.max(max, hatch.getCurrentParallel());
        return max;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof DraconicFusionCraftingMachine m)) return ModifierFunction.NULL;
        ModifierFunction tier = DraconicFusionCrafterMachine.recipeModifier(machine, recipe);
        if (tier == ModifierFunction.NULL) return tier;
        GTRecipe tiered = tier.apply(recipe);
        if (tiered == null) return tier;
        int parallels = ParallelLogic.getParallelAmount(machine, tiered, m.maxParallel());
        if (parallels <= 0) return ModifierFunction.NULL;
        ModifierFunction bonus = ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels)).parallels(parallels)
                .durationMultiplier(0.5).eutMultiplier(0.5).build();
        ModifierFunction both = tier.andThen(bonus);
        var oc = m.coreTier >= 4 ? GTRecipeModifiers.OC_PERFECT_SUBTICK : GTRecipeModifiers.OC_NON_PERFECT_SUBTICK;
        return both.andThen(oc.getModifier(machine, both.apply(recipe)));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Parallels: " + maxParallel()).withStyle(ChatFormatting.AQUA));
        if (coreTier >= 4) textList.add(Component.literal("Perfect overclocks").withStyle(ChatFormatting.RED));
    }
}
