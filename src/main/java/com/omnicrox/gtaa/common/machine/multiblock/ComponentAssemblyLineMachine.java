package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Component Assembly Line (CoAL). Port of GoodGenerator's MTEComponentAssemblyLine (GT5-Unofficial, LGPL-3.0):
 * makes 64 components at a time from bulk materials; the casing tier sets the highest component tier,
 * and every casing tier above the recipe halves the time. Normal overclocks on top, no tier-skip limit.
 */
public class ComponentAssemblyLineMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ComponentAssemblyLineMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX_CASING = "CoALCasingTier";
    public static final String CASING_TIER = "coal_casing_tier";

    @DescSynced private int casingTier;

    public ComponentAssemblyLineMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static TraceabilityPredicate casings() {
        return new TraceabilityPredicate(s -> {
            int tier = GTAABlocks.coalCasingTier(s.getBlockState().getBlock());
            if (tier == 0) return false;
            Object current = s.getMatchContext().getOrPut(CTX_CASING, tier);
            if (!current.equals(tier)) {
                s.setError(new PatternStringError("Component Assembly Line Casings must all be the same tier"));
                return false;
            }
            return true;
        }, () -> GTAABlocks.COAL_CASINGS.stream().skip(1)
                .map(b -> BlockInfo.fromBlockState(b.get().defaultBlockState())).toArray(BlockInfo[]::new));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object t = getMultiblockState().getMatchContext().get(CTX_CASING);
        casingTier = t instanceof Integer i ? i : 0;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        casingTier = 0;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof ComponentAssemblyLineMachine coal)) return ModifierFunction.NULL;
        int need = recipe.data.getInt(CASING_TIER);
        if (need > coal.casingTier) {
            return ModifierFunction.cancel(Component.literal("Needs a " + GTValues.VN[Math.max(1, need)] +
                    " Component Assembly Line Casing"));
        }
        double speed = Math.pow(2, -(coal.casingTier - need));
        var euIO = RecipeHelper.getRealEUtWithIO(recipe);
        long eut = Math.max(1, euIO.isInput() ? euIO.stack().getTotalEU() : 0);
        long voltage = coal.getMaxVoltage();
        if (eut > voltage) return ModifierFunction.NULL;
        double eutMult = 1, durMult = speed;
        while (eut * eutMult * 4 <= voltage && recipe.duration * durMult / 2 >= 1) {
            eutMult *= 4;
            durMult /= 2;
        }
        return ModifierFunction.builder().eutMultiplier(eutMult).durationMultiplier(durMult).build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) textList.add(Component.literal("Casing tier: " +
                (casingTier > 0 ? GTValues.VNF[casingTier] : "none")).withStyle(ChatFormatting.AQUA));
    }
}
