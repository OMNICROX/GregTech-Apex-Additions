package com.omnicrox.apexlib.multiblock;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/** Shared recipe modifiers and structure predicates for Apex GTNH-style multiblocks. */
public final class ApexModifiers {

    private ApexModifiers() {}

    /** Voltage tier of the machine's energy hatches (0 when unformed) */
    public static int tier(MetaMachine m) {
        return m instanceof WorkableElectricMultiblockMachine w ? Math.max(0, w.getTier()) : 0;
    }

    /**
     * Parallel + speed modifier: up to max(machine) parallels (limited by inputs, output space and power), the
     * duration multiplied by speed(machine) and EU/t by eu. Put an overclock modifier after it.
     */
    public static RecipeModifier parallel(ToIntFunction<MetaMachine> max, ToDoubleFunction<MetaMachine> speed, double eu) {
        return (machine, recipe) -> {
            int cap = Math.max(1, max.applyAsInt(machine));
            // never more parallels than the hatches can power (the overclock step refuses recipes above the voltage)
            if (machine instanceof WorkableElectricMultiblockMachine w) {
                var euIO = com.gregtechceu.gtceu.api.recipe.RecipeHelper.getRealEUtWithIO(recipe);
                long eut = euIO.isInput() ? euIO.stack().getTotalEU() : 0;
                if (eut > 0) {
                    long byEU = (long) (w.getOverclockVoltage() / (eut * Math.max(eu, 1e-9)));
                    if (byEU < 1) return ModifierFunction.NULL;
                    cap = (int) Math.min(cap, byEU);
                }
            }
            int p = cap == 1 ? 1 : ParallelLogic.getParallelAmount(machine, recipe, cap);
            if (p <= 0) return ModifierFunction.NULL;
            var b = ModifierFunction.builder().durationMultiplier(speed.applyAsDouble(machine)).eutMultiplier(p * eu);
            if (p > 1) b.modifyAllContents(ContentModifier.multiplier(p)).parallels(p);
            return b.build();
        };
    }

    /** Matches any of the blocks and records each matched position in a set in the match context (to count them) */
    public static TraceabilityPredicate counted(String ctx, Supplier<Block[]> blocks) {
        return new TraceabilityPredicate(s -> {
            Block b = s.getBlockState().getBlock();
            for (Block x : blocks.get()) {
                if (x == b) {
                    Set<Long> set = s.getMatchContext().getOrPut(ctx, new HashSet<Long>());
                    set.add(s.getPos().asLong());
                    return true;
                }
            }
            return false;
        }, () -> Arrays.stream(blocks.get()).map(x -> BlockInfo.fromBlockState(x.defaultBlockState())).toArray(BlockInfo[]::new));
    }

    /** How many positions a counted() predicate matched in the last structure check */
    public static int count(com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController c, String ctx) {
        Object o = c.getMultiblockState().getMatchContext().get(ctx);
        return o instanceof Set<?> s ? s.size() : 0;
    }
}
