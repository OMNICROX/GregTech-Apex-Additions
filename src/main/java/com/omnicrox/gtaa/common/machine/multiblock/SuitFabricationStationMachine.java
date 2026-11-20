package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.machine.multiblock.suit.SuitCatalog;
import com.omnicrox.gtaa.common.machine.multiblock.suit.SuitPickerTab;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Suit Fabrication Station: makes hero suits (with their Apex hero kit) from parts. One footprint, four casing
 * tiers - Steel Booth (MV), Showcase Pod (EV), Holo Chamber (LuV), Ascension Forge (UHV) - and the casing tier
 * decides which suits it can make. The Suit Picker tab picks a suit (the station then only makes that one) and
 * its Fabricate button sets an ME Input Bus to pull the parts and autocrafts whatever is short.
 */
public class SuitFabricationStationMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SuitFabricationStationMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX = "SuitStationTier";
    public static final String[] TIER_NAMES = { "", "Steel Booth", "Showcase Pod", "Holo Chamber", "Ascension Forge" };

    @DescSynced private int stationTier;
    /** Suit picked in the Suit Picker; empty = make any suit whose parts are in the buses. */
    @Persisted @DescSynced private String selectedSuit = "";
    /** Suit shown on the hologram pad: the picked one, or the last one made. */
    @Persisted @DescSynced private String displaySuit = "";
    @DescSynced private String fabStatus = "";

    private TickableSubscription meTick;

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

    public String getSelectedSuit() {
        return selectedSuit;
    }

    public String getDisplaySuit() {
        return selectedSuit.isEmpty() ? displaySuit : selectedSuit;
    }

    public String getFabStatus() {
        return fabStatus;
    }

    public void setFabStatus(String status) {
        fabStatus = status == null ? "" : status;
    }

    /** Booth floor centre (where the suit hologram stands), relative to the controller. */
    public BlockPos padOffset() {
        var back = getFrontFacing().getOpposite();
        return BlockPos.ZERO.relative(back, 2).above();
    }

    public void select(String suit) {
        if (suit == null || suit.equals(selectedSuit)) suit = "";
        selectedSuit = suit;
        fabStatus = suit.isEmpty() ? "" : "§7Picked - press Fabricate";
        getRecipeLogic().markLastRecipeDirty();
    }

    public void fabricate(ServerPlayer player) {
        SuitCatalog.Entry e = SuitCatalog.get(selectedSuit);
        if (e == null) {
            fabStatus = "§cPick a suit first";
            return;
        }
        if (!isFormed()) {
            fabStatus = "§cStation isn't formed";
            return;
        }
        if (e.tier() > stationTier) {
            fabStatus = "§cNeeds a " + TIER_NAMES[e.tier()] + " (tier " + e.tier() + " casings)";
            return;
        }
        GTRecipe recipe = getLevel().getRecipeManager().byKey(e.recipeId())
                .filter(r -> r instanceof GTRecipe).map(r -> (GTRecipe) r).orElse(null);
        if (recipe == null) {
            fabStatus = "§cNo recipe for this suit";
            return;
        }
        String status = null;
        if (ModList.get().isLoaded("ae2")) status = com.omnicrox.gtaa.common.machine.multiblock.suit.SuitStationME
                .fabricate(this, player, recipe, e.circuit());
        if (status == null) status = "§eNo ME Input Bus - put the parts in an input bus" +
                (e.circuit() > 0 ? " (circuit " + e.circuit() + ")" : "");
        fabStatus = status;
        getRecipeLogic().markLastRecipeDirty();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object t = getMultiblockState().getMatchContext().get(CTX);
        stationTier = t instanceof Integer i ? i : 0;
        if (meTick == null && ModList.get().isLoaded("ae2")) meTick = subscribeServerTick(() -> {
            if (getOffsetTimer() % 20 == 0) com.omnicrox.gtaa.common.machine.multiblock.suit.SuitStationME.tick(this);
        });
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        stationTier = 0;
        if (meTick != null) {
            unsubscribe(meTick);
            meTick = null;
        }
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe != null) {
            SuitCatalog.Entry e = SuitCatalog.byRecipe(recipe.getId());
            if (e != null) {
                displaySuit = e.suit();
                if (e.suit().equals(selectedSuit)) fabStatus = "§aFabricating " + e.name();
            }
        }
        return super.beforeWorking(recipe);
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof SuitFabricationStationMachine m)) return ModifierFunction.NULL;
        int need = Math.max(1, recipe.data.getInt("station_tier"));
        if (need > m.stationTier) return ModifierFunction.cancel(Component.literal(
                "Needs a " + TIER_NAMES[Math.min(4, need)] + " (tier " + need + " casings)"));
        if (!m.selectedSuit.isEmpty()) {
            SuitCatalog.Entry e = SuitCatalog.byRecipe(recipe.getId());
            if (e == null || !e.suit().equals(m.selectedSuit))
                return ModifierFunction.cancel(Component.literal("Another suit is picked in the Suit Picker"));
        }
        return ModifierFunction.IDENTITY;
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        super.attachSideTabs(sideTabs);
        sideTabs.attachSubTab(new SuitPickerTab(this));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Station: " + TIER_NAMES[Math.max(0, Math.min(4, stationTier))] + " (tier " + stationTier + ")")
                .withStyle(ChatFormatting.GOLD));
        SuitCatalog.Entry e = SuitCatalog.get(selectedSuit);
        textList.add(Component.literal("Picked suit: " + (e == null ? "any (open the Suit Picker tab)" : e.name()))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
