package com.omnicrox.gtaa.common.machine.multiblock.elevator;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Base of every Space Elevator module. Port of GTNH-Intergalactic's TileEntityModuleBase (MIT):
 * no energy hatch - the module has its own EU buffer that the Space Elevator fills. It only works while connected
 * to a running elevator with a high enough motor tier.
 */
public class SpaceElevatorModuleMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SpaceElevatorModuleMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public record Stats(int voltageTier, int moduleTier, int neededMotorTier, int maxParallels, float speedBonus,
                        long bufferSize) {}

    protected final Stats stats;
    @Persisted protected final NotifiableEnergyContainer buffer;
    @DescSynced protected boolean connected;

    public SpaceElevatorModuleMachine(IMachineBlockEntity holder, Stats stats, Object... args) {
        super(holder, args);
        this.stats = stats;
        this.buffer = NotifiableEnergyContainer.receiverContainer(this, stats.bufferSize(),
                GTValues.V[stats.voltageTier()], stats.maxParallels());
        this.buffer.setCapabilityValidator(Objects::isNull); // only the elevator can charge it
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public Stats getStats() {
        return stats;
    }

    public int getNeededMotorTier() {
        return stats.neededMotorTier();
    }

    public void connect(SpaceElevatorMachine elevator) {
        connected = true;
    }

    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    /** @return how much EU was actually accepted */
    public long increaseStoredEU(long max) {
        long space = buffer.getEnergyCapacity() - buffer.getEnergyStored();
        long add = Math.min(space, max);
        if (add > 0) buffer.changeEnergy(add);
        return Math.max(0, add);
    }

    /**
     * GT processing logic with voltage V[tier] and amperage = max parallels (no amperage overclock),
     * then the module's speed bonus. Recipes can require a module tier via "module_tier".
     */
    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof SpaceElevatorModuleMachine module) || !module.connected) return ModifierFunction.NULL;
        Stats s = module.stats;
        if (recipe.data.getInt(GTAARecipeTypes.MODULE_TIER) > s.moduleTier()) return ModifierFunction.NULL;
        long voltage = GTValues.V[s.voltageTier()];
        var euIO = RecipeHelper.getRealEUtWithIO(recipe);
        long eut = Math.max(1, euIO.isInput() ? euIO.stack().getTotalEU() : 0);
        if (eut > voltage) return ModifierFunction.NULL;
        int maxByEnergy = (int) Math.min(s.maxParallels(), (voltage * s.maxParallels()) / eut);
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, Math.max(1, maxByEnergy));
        if (parallels <= 0) return ModifierFunction.NULL;
        // non-perfect overclocks against a single amp of the module voltage
        int ocs = 0;
        long ocEut = eut;
        int duration = recipe.duration;
        while (ocEut * 4 <= voltage && duration > 1) {
            ocEut *= 4;
            duration = Math.max(1, duration / 2);
            ocs++;
        }
        double durationMult = (double) duration / recipe.duration / s.speedBonus();
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier((double) parallels * Math.pow(4, ocs))
                .durationMultiplier(durationMult)
                .parallels(parallels)
                .build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(getRecipeLogic().isWorkingEnabled(), getRecipeLogic().isActive())
                .addCustom(t -> {
                    t.add(Component.literal(connected ? "Connected to a Space Elevator" : "Not connected to a Space Elevator")
                            .withStyle(connected ? ChatFormatting.GREEN : ChatFormatting.RED));
                    t.add(Component.literal("Stored: " + FormattingUtil.formatNumbers(buffer.getEnergyStored()) + " / " +
                            FormattingUtil.formatNumbers(buffer.getEnergyCapacity()) + " EU").withStyle(ChatFormatting.YELLOW));
                    t.add(Component.literal("Runs at " + GTValues.VNF[stats.voltageTier()] + "§r with up to " +
                            stats.maxParallels() + " parallels"));
                })
                .addWorkingStatusLine()
                .addProgressLine(getRecipeLogic());
    }
}
