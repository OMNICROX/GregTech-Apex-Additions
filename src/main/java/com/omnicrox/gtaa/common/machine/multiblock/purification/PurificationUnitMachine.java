package com.omnicrox.gtaa.common.machine.multiblock.purification;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A purification unit (grade 1-8). Port of GT5-Unofficial's MTEPurificationUnitBase (LGPL-3.0), simplified:
 * <ul>
 * <li>needs a Water Purification Plant within 32 blocks; the plant powers it (no energy hatch on the unit)</li>
 * <li>the purified water output has the recipe's success chance</li>
 * <li>water boost: if the next grade of water is in an input hatch, 10% of the input amount of it is used
 * and the success chance rises by 15%</li>
 * <li>up to 64 parallels</li>
 * </ul>
 */
public class PurificationUnitMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PurificationUnitMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CHANCE = "pur_chance";
    public static final int MAX_PARALLEL = 64;

    private final int grade;
    @Persisted protected final NotifiableEnergyContainer buffer;
    @DescSynced private boolean linked;
    @DescSynced private int lastChance;
    private PurificationPlantMachine plant;

    public PurificationUnitMachine(IMachineBlockEntity holder, int grade, Object... args) {
        super(holder, args);
        this.grade = grade;
        long v = GTValues.V[GTValues.LuV + Math.min(4, grade / 2)];
        this.buffer = NotifiableEnergyContainer.receiverContainer(this, v * 64 * 200, v, 64);
        this.buffer.setCapabilityValidator(Objects::isNull);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getGrade() {
        return grade;
    }

    public long charge(long max) {
        long space = buffer.getEnergyCapacity() - buffer.getEnergyStored();
        long add = Math.min(space, max);
        if (add > 0) buffer.changeEnergy(add);
        return Math.max(0, add);
    }

    void unlinkFromPlant() {
        plant = null;
        linked = false;
    }

    private void tryLink() {
        if (isRemote()) return;
        if (plant != null && (plant.isInValid() || !plant.isFormed())) unlinkFromPlant();
        if (plant == null) {
            plant = PurificationPlantMachine.findNearby(getLevel(), getPos());
            if (plant != null) plant.link(this);
        }
        linked = plant != null;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        tryLink();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (plant != null) plant.unlink(this);
        unlinkFromPlant();
    }

    private List<IFluidHandler> inputTanks() {
        List<IFluidHandler> out = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(FluidRecipeCapability.CAP)) {
                    if (h instanceof NotifiableFluidTank t) out.add(t);
                }
            }
        }
        return out;
    }

    private boolean drain(FluidStack stack, boolean simulate) {
        int left = stack.getAmount();
        for (IFluidHandler t : inputTanks()) {
            left -= t.drain(new FluidStack(stack, left), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (left <= 0) break;
        }
        if (left > 0) return false;
        if (simulate) return true;
        left = stack.getAmount();
        for (IFluidHandler t : inputTanks()) {
            left -= t.drain(new FluidStack(stack, left), IFluidHandler.FluidAction.EXECUTE).getAmount();
            if (left <= 0) break;
        }
        return true;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof PurificationUnitMachine unit)) return ModifierFunction.NULL;
        unit.tryLink();
        if (!unit.linked) return ModifierFunction.cancel(Component.literal("Not linked to a Water Purification Plant"));
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, MAX_PARALLEL);
        if (parallels <= 0) return ModifierFunction.NULL;
        int chance = recipe.data.getInt(CHANCE);
        // water boost with the next grade
        if (unit.grade < 8 && GTAAMaterials.PURIFIED_WATER[unit.grade + 1] != null) {
            FluidStack boost = GTAAMaterials.PURIFIED_WATER[unit.grade + 1].getFluid(100 * parallels);
            if (unit.drain(boost, true)) {
                unit.drain(boost, false);
                chance = Math.min(100, chance + 15);
            }
        }
        unit.lastChance = chance;
        final int p = parallels, ch = chance;
        final var targetWater = GTAAMaterials.PURIFIED_WATER[unit.grade];
        return r -> {
            GTRecipe copy = r.copy(ContentModifier.multiplier(p), false);
            Map<RecipeCapability<?>, List<Content>> outs = new LinkedHashMap<>();
            for (var e : copy.outputs.entrySet()) {
                List<Content> list = new ArrayList<>();
                for (Content c : e.getValue()) {
                    boolean isWater = e.getKey() == FluidRecipeCapability.CAP && targetWater != null &&
                            c.content instanceof com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient fi &&
                            fi.getStacks().length > 0 && fi.getStacks()[0].getFluid() == targetWater.getFluid();
                    list.add(isWater ? new Content(c.content, ch * 100, 10000, 0) : c);
                }
                outs.put(e.getKey(), list);
            }
            copy.outputs.clear();
            copy.outputs.putAll(outs);
            copy.parallels = p;
            return copy;
        };
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal(linked ? "Linked to a Water Purification Plant" : "Not linked: build a Water Purification Plant within 32 blocks")
                .withStyle(linked ? ChatFormatting.GREEN : ChatFormatting.RED));
        textList.add(Component.literal("Stored: " + FormattingUtil.formatNumbers(buffer.getEnergyStored()) + " EU")
                .withStyle(ChatFormatting.YELLOW));
        if (lastChance > 0) textList.add(Component.literal("Success chance: " + lastChance + "%").withStyle(ChatFormatting.AQUA));
    }
}
