package com.omnicrox.gtaa.common.machine.multiblock.gtnl;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.util.ApexFormat;
import com.omnicrox.gtaa.common.machine.part.ManaHatchPartMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Alfheim Teleportation Array (GT-Not-Leisure "TeleportationArrayToAlfheim"). Runs every Botania altar recipe (the
 * mana_nexus recipe list) with unlimited parallels and perfect overclocks. Mana comes as liquid mana (1 mB = 100 mana)
 * from the input hatches or from Mana Hatches; the portal itself costs 100 mB of liquid mana a second while it runs.
 * An Everlasting Guilty Pool or an Asgard Dandelion in an input bus makes the mana unlimited.
 *
 * This file is subject to the additional terms in LICENSE-ADDITIONAL-TERMS.md (GT-Not-Leisure).
 */
public class AlfheimTeleportationArrayMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AlfheimTeleportationArrayMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final String MANA = "mana";
    public static final long MANA_PER_MB = 100;
    public static final long UPKEEP_MB = 100;
    public static final long CAPACITY = 100_000_000_000L;
    private static final Set<ResourceLocation> INFINITE = Set.of(new ResourceLocation("botania", "creative_pool"),
            new ResourceLocation("avaritia_integration", "asgard_dandelion"),
            new ResourceLocation("avaritia_integration", "asgard_dandelion_floating"));

    @Persisted @DescSynced
    private long storedMana;
    @DescSynced
    private boolean infinite;
    @DescSynced
    private boolean starved;

    private Fluid liquidMana;
    private TickableSubscription tick;

    public AlfheimTeleportationArrayMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) {
            infinite = hasInfiniteSource();
            tick = subscribeServerTick(tick, this::arrayTick);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    private Fluid liquidMana() {
        if (liquidMana == null) {
            Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("manafluid", "mana"));
            liquidMana = f == null ? Fluids.EMPTY : f;
        }
        return liquidMana;
    }

    private boolean hasInfiniteSource() {
        for (NotifiableItemStackHandler bus : ApexHatches.itemHandlers(this, IO.IN))
            for (int i = 0; i < bus.getSlots(); i++) {
                ItemStack s = bus.getStackInSlot(i);
                if (!s.isEmpty() && INFINITE.contains(ForgeRegistries.ITEMS.getKey(s.getItem()))) return true;
            }
        return false;
    }

    private void arrayTick() {
        pullMana();
        if (getOffsetTimer() % 20 != 0) return;
        infinite = hasInfiniteSource();
        breadCheck();
        // the portal's own upkeep, paid every second while it works
        if (getRecipeLogic().isWorking() && !infinite) {
            long cost = UPKEEP_MB * MANA_PER_MB;
            if (storedMana >= cost) {
                storedMana -= cost;
                starved = false;
            } else {
                starved = true;
                getRecipeLogic().interruptRecipe();
            }
        }
    }

    /** takes liquid mana from the input hatches and the Mana Hatches into the internal tank */
    private void pullMana() {
        long space = CAPACITY - storedMana;
        if (space <= 0) return;
        for (IMultiPart part : getParts()) {
            if (space <= 0) return;
            if (part instanceof ManaHatchPartMachine hatch) {
                long got = hatch.extract(space);
                storedMana += got;
                space -= got;
            }
        }
        Fluid mana = liquidMana();
        if (mana == Fluids.EMPTY || space < MANA_PER_MB) return;
        for (NotifiableFluidTank tank : ApexHatches.fluidTanks(this, IO.IN)) {
            for (int i = 0; i < tank.getTanks() && space >= MANA_PER_MB; i++) {
                FluidStack f = tank.getFluidInTank(i);
                if (f.isEmpty() || f.getFluid() != mana) continue;
                int mb = (int) Math.min(f.getAmount(), space / MANA_PER_MB);
                FluidStack got = tank.drainInternal(new FluidStack(f, mb), IFluidHandler.FluidAction.EXECUTE);
                storedMana += got.getAmount() * MANA_PER_MB;
                space -= got.getAmount() * MANA_PER_MB;
            }
        }
    }

    /** GT-Not-Leisure's portal does not take kindly to bread. Blocks are never broken. */
    private void breadCheck() {
        int bread = 0;
        for (NotifiableItemStackHandler bus : ApexHatches.itemHandlers(this, IO.IN))
            for (int i = 0; i < bus.getSlots(); i++) {
                ItemStack s = bus.getStackInSlot(i);
                if (s.is(Items.BREAD)) bread += bus.extractItemInternal(i, s.getCount(), false).getCount();
            }
        if (bread == 0 || getLevel() == null) return;
        var p = getPos();
        getLevel().playSound(null, p, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 2f, 0.6f);
        getLevel().explode(null, p.getX() + 0.5, p.getY() + 1.5, p.getZ() + 0.5,
                Math.min(8f, 2f + bread / 8f), Level.ExplosionInteraction.NONE);
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof AlfheimTeleportationArrayMachine array)) return ModifierFunction.NULL;
        long mana = recipe.data.getLong(MANA);
        int max = Integer.MAX_VALUE;
        if (mana > 0 && !array.infinite) {
            long affordable = array.storedMana / mana;
            if (affordable <= 0) return ModifierFunction.cancel(Component.literal("Needs " + ApexFormat.num(mana) + " mana"));
            max = (int) Math.min(max, affordable);
        }
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, max);
        if (parallels <= 0) return ModifierFunction.NULL;
        ModifierFunction par = parallels == 1 ? ModifierFunction.IDENTITY : ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels)).parallels(parallels).build();
        return par.andThen(GTRecipeModifiers.OC_PERFECT_SUBTICK.getModifier(machine, par.apply(recipe)));
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe != null && !infinite) {
            long need = recipe.data.getLong(MANA) * Math.max(1, recipe.getTotalRuns());
            if (need > storedMana) return false;
            storedMana -= need;
        }
        return super.beforeWorking(recipe);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(infinite ? Component.literal("Mana: unlimited").withStyle(ChatFormatting.LIGHT_PURPLE) :
                Component.literal("Mana: " + ApexFormat.num(storedMana) + " / " + ApexFormat.num(CAPACITY))
                        .withStyle(ChatFormatting.AQUA));
        if (starved) textList.add(Component.literal("Out of mana: the portal needs 100 mB of liquid mana a second")
                .withStyle(ChatFormatting.RED));
    }
}
