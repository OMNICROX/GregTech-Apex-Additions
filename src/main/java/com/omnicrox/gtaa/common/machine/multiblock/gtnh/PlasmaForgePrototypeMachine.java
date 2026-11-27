package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Dimensionally Transcendent Matter Plasma Forge Prototype MK-II (Twist Space Technology). Runs the DTPF's recipes.
 * Built for long runs: after 24 hours of continuous running it uses 75% less catalyst fuel; once it stops the
 * discount drains to 0 within an hour. 16 parallels, coil heat like the DTPF.
 */
public class PlasmaForgePrototypeMachine extends CoilWorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PlasmaForgePrototypeMachine.class, CoilWorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final long FULL_TICKS = 24L * 72000L;
    public static final double MAX_DISCOUNT = 0.75;
    public static final int PARALLEL = 16;

    @Persisted @DescSynced private long runTicks;
    private TickableSubscription tick;

    public PlasmaForgePrototypeMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) tick = subscribeServerTick(tick, this::update);
    }

    private void update() {
        if (getRecipeLogic().isWorking()) runTicks = Math.min(FULL_TICKS, runTicks + 1);
        else if (runTicks > 0) runTicks = Math.max(0, runTicks - FULL_TICKS / 72000L); // 24 -> 0 in one hour
    }

    public double discount() {
        return MAX_DISCOUNT * Math.min(1.0, (double) runTicks / FULL_TICKS);
    }

    private static boolean isCatalyst(FluidStack s) {
        var key = BuiltInRegistries.FLUID.getKey(s.getFluid()).getPath();
        return key.contains("dimensionally_transcendent") && key.contains("catalyst");
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof PlasmaForgePrototypeMachine pf)) return ModifierFunction.NULL;
        double keep = 1.0 - pf.discount();
        ModifierFunction fuel = keep >= 1.0 ? ModifierFunction.IDENTITY : r -> {
            GTRecipe copy = r.copy();
            List<Content> list = copy.inputs.get(FluidRecipeCapability.CAP);
            if (list == null) return copy;
            List<Content> scaled = new ArrayList<>();
            for (Content c : list) {
                FluidIngredient ing = FluidRecipeCapability.CAP.of(c.content);
                FluidStack[] st = ing.getStacks();
                if (st.length > 0 && isCatalyst(st[0])) {
                    FluidIngredient red = ing.copy();
                    red.setAmount(Math.max(1, (int) Math.round(ing.getAmount() * keep)));
                    scaled.add(new Content(red, c.chance, c.maxChance, c.tierChanceBoost));
                } else scaled.add(c);
            }
            copy.inputs.put(FluidRecipeCapability.CAP, scaled);
            return copy;
        };
        int extra = 0;
        if (machine instanceof com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController mc)
            for (var part : mc.getParts())
                if (part instanceof com.gregtechceu.gtceu.api.capability.IParallelHatch h) extra += h.getCurrentParallel();
        int maxPar = PARALLEL + extra;
        ModifierFunction par = ApexModifiers.parallel(m -> maxPar, m -> 1.0, 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        GTRecipe afterPar = par.apply(recipe);
        ModifierFunction oc = GTRecipeModifiers.ebfOverclock(machine, afterPar);
        if (oc == ModifierFunction.NULL) return oc;
        return par.andThen(oc).andThen(fuel);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal(String.format("Catalyst discount: %.1f%% (%.1f h of continuous running)",
                100 * discount(), runTicks / 72000.0)).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
