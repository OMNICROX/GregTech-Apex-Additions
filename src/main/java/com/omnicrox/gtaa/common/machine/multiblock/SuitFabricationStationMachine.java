package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Suit Fabrication Station: makes hero suits (with their Apex hero kit) from parts. One footprint, four casing
 * tiers - Steel Booth (MV), Showcase Pod (EV), Holo Chamber (LuV), Ascension Forge (UHV) - and the casing tier
 * decides which suits it can make. Hook it to an ME system with an ME input bus and encode the suit as a pattern
 * to have missing parts autocrafted.
 */
public class SuitFabricationStationMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SuitFabricationStationMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX = "SuitStationTier";
    public static final String[] TIER_NAMES = { "", "Steel Booth", "Showcase Pod", "Holo Chamber", "Ascension Forge" };

    @DescSynced private int stationTier;

    public SuitFabricationStationMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static TraceabilityPredicate casings() {
        return QuantumForceTransformerMachine.tiered(CTX, GTAABlocks::suitCasings, "Suit station casings");
    }

    public int getStationTier() {
        return stationTier;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object t = getMultiblockState().getMatchContext().get(CTX);
        stationTier = t instanceof Integer i ? i : 0;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        stationTier = 0;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof SuitFabricationStationMachine m)) return ModifierFunction.NULL;
        int need = Math.max(1, recipe.data.getInt("station_tier"));
        if (need > m.stationTier) return ModifierFunction.cancel(Component.literal(
                "Needs a " + TIER_NAMES[Math.min(4, need)] + " (tier " + need + " casings)"));
        return ModifierFunction.IDENTITY;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Station: " + TIER_NAMES[Math.max(0, Math.min(4, stationTier))] + " (tier " + stationTier + ")")
                .withStyle(ChatFormatting.GOLD));
    }
}
