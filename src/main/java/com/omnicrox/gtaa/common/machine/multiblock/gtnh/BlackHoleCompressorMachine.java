package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.data.GTAARecipeTypes;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Pseudostable Black Hole Containment Field (GT5-Unofficial 2.9's Black Hole Compressor).
 *  - Compressor and Neutronium Compressor (Avaritia singularities) modes; the second needs an open black hole
 *  - 8 parallels per voltage tier, 500% speed, 70% EU; parallels x2 / x4 below 50 / 20 stability
 *  - a Black Hole Seed opens the black hole at 100 stability; it loses 1 per second unless fed spacetime
 *    (1 L/s, doubling for every 30 s it has been held); at 0 it is unstable, voids outputs and closes after a minute
 *  - a Black Hole Collapser closes it
 */
public class BlackHoleCompressorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BlackHoleCompressorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted @DescSynced private boolean open;
    @Persisted @DescSynced private float stability;
    @Persisted private int heldSeconds;
    @Persisted @DescSynced private int unstableSeconds;
    @DescSynced private boolean fedLastSecond;
    private TickableSubscription tick;

    public BlackHoleCompressorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) tick = subscribeServerTick(tick, this::update);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    private static Material spacetime() {
        Material m = ElectricImplosionCompressorMachine.mat("gtna:space_time");
        return m != null ? m : GTMaterials.Neutronium;
    }

    private void update() {
        if (getOffsetTimer() % 20 != 0) return;
        if (!open) {
            if (GTNHMultiHelper.consume(this, GTAAItems.BLACK_HOLE_SEED.get())) {
                open = true;
                stability = 100;
                heldSeconds = 0;
                unstableSeconds = 0;
            }
            return;
        }
        if (GTNHMultiHelper.consume(this, GTAAItems.BLACK_HOLE_COLLAPSER.get())) {
            close();
            return;
        }
        fedLastSecond = false;
        if (stability > 0) {
            Material st = spacetime();
            int cost = 1 << Math.min(20, heldSeconds / 30);
            FluidStack want = st.getFluid(cost);
            if (want != null && !want.isEmpty() && GTNHMultiHelper.drain(this, want, false)) {
                heldSeconds++;
                fedLastSecond = true;
            } else {
                stability = Math.max(0, stability - 1);
            }
        } else if (++unstableSeconds >= 60) {
            close();
        }
    }

    private void close() {
        open = false;
        stability = 0;
        heldSeconds = 0;
        unstableSeconds = 0;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof BlackHoleCompressorMachine bh)) return ModifierFunction.NULL;
        boolean neutronium = recipe.recipeType == GTAARecipeTypes.NEUTRONIUM_COMPRESSION;
        if (neutronium && !bh.open) return ModifierFunction.cancel(Component.literal("Requires an active black hole"));
        int mult = !bh.open ? 1 : bh.stability < 20 ? 4 : bh.stability < 50 ? 2 : 1;
        int max = 8 * Math.max(1, ApexModifiers.tier(machine)) * mult;
        if (bh.open && bh.stability < 20 && !neutronium) max = 1 << 16; // uncapped
        final int cap = max;
        ModifierFunction par = ApexModifiers.parallel(m -> cap, m -> 0.2, 0.7).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        ModifierFunction oc = GTRecipeModifiers.OC_NON_PERFECT.getModifier(machine, par.apply(recipe));
        ModifierFunction result = par.andThen(oc);
        if (bh.open && bh.stability <= 0) {
            return result.andThen(r -> {
                GTRecipe copy = r.copy();
                copy.outputs.clear();
                return copy;
            });
        }
        return result;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        if (!open) {
            textList.add(Component.literal("No black hole: insert a Black Hole Seed").withStyle(ChatFormatting.GRAY));
            return;
        }
        ChatFormatting c = stability <= 0 ? ChatFormatting.DARK_RED : stability < 20 ? ChatFormatting.RED :
                stability < 50 ? ChatFormatting.GOLD : ChatFormatting.LIGHT_PURPLE;
        textList.add(Component.literal("Black hole stability: " + Math.round(stability) + (stability <= 0 ?
                " - UNSTABLE, voiding outputs, closing in " + (60 - unstableSeconds) + " s" : "")).withStyle(c));
        textList.add(Component.literal(fedLastSecond ? "Decay halted by spacetime (" + (1 << Math.min(20, heldSeconds / 30)) +
                " L/s)" : "Decaying 1/s - feed spacetime to halt it").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
