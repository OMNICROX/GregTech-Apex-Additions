package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.api.DefcRecipeType;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Draconic Fusion Crafter. Port of KubaTech's MTEDEFusionCrafter (GTNH, LGPL-3.0): runs Draconic Evolution
 * fusion crafting recipes. The core casings set the highest recipe tier; every core tier above the recipe halves
 * its time. Chaotic cores need Fusion Machine Casing MK II.
 */
public class DraconicFusionCrafterMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DraconicFusionCrafterMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX_CORE = "DEFCCore";
    public static final String CTX_FUSION = "DEFCFusion";

    @DescSynced protected int coreTier;
    @DescSynced private int fusionTier;

    public DraconicFusionCrafterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static TraceabilityPredicate cores() {
        return QuantumForceTransformerMachine.tiered(CTX_CORE, GTAABlocks::defcCores, "Fusion core casings");
    }

    public static TraceabilityPredicate fusionCasings() {
        return QuantumForceTransformerMachine.tiered(CTX_FUSION,
                () -> new Block[] { GTBlocks.FUSION_CASING.get(), GTBlocks.FUSION_CASING_MK2.get() },
                "Fusion machine casings");
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object c = getMultiblockState().getMatchContext().get(CTX_CORE);
        Object f = getMultiblockState().getMatchContext().get(CTX_FUSION);
        coreTier = c instanceof Integer i ? i : 0;
        fusionTier = f instanceof Integer i ? i : 0;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        coreTier = fusionTier = 0;
    }

    /** chaotic cores only count with MK II fusion casings */
    public int effectiveTier() {
        return coreTier >= 4 && fusionTier < 2 ? 3 : coreTier;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof DraconicFusionCrafterMachine m)) return ModifierFunction.NULL;
        int need = Math.max(1, recipe.data.getInt(DefcRecipeType.TIER_KEY));
        int have = m.effectiveTier();
        if (need > have) return ModifierFunction.cancel(Component.literal("Needs tier " + need + " fusion core casings"));
        if (have == need) return ModifierFunction.IDENTITY;
        return ModifierFunction.builder().durationMultiplier(Math.pow(0.5, have - need)).build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Core tier: " + effectiveTier() +
                (coreTier >= 4 && fusionTier < 2 ? " (Chaotic cores need Fusion Machine Casing MK II)" : ""))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
