package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.machine.GTAAModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Circuit Assembly Line (GT5-Unofficial). Two modes, screwdriver to switch:
 *  - Circuit Assembly Line: imprinted to one circuit, permanently (the first circuit it makes). Runs only that
 *    circuit's recipes with 4 parallels per slice (5-16 slices = 20-64) and perfect overclocks
 *  - Circuit Assembler: any circuit assembler recipe up to (energy hatch tier - 1), perfect overclocks
 */
public class CircuitAssemblyLineMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CircuitAssemblyLineMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX = "CALSlices";

    @Persisted @DescSynced private boolean assemblerMode;
    @Persisted @DescSynced private String imprint = "";
    @DescSynced private int slices;

    public CircuitAssemblyLineMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        slices = GTAAModifiers.count(this, CTX);
    }

    private static String output(GTRecipe r) {
        List<ItemStack> outs = RecipeHelper.getOutputItems(r);
        return outs.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(outs.get(0).getItem()).toString();
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof CircuitAssemblyLineMachine cal)) return ModifierFunction.NULL;
        if (cal.assemblerMode) {
            if (RecipeHelper.getRecipeEUtTier(recipe) > Math.max(1, cal.getTier() - 1))
                return ModifierFunction.cancel(Component.literal("Circuit Assembler mode: recipe tier is at most energy hatch tier - 1"));
            return GTRecipeModifiers.OC_PERFECT.getModifier(machine, recipe);
        }
        String out = output(recipe);
        if (!cal.imprint.isEmpty() && !cal.imprint.equals(out))
            return ModifierFunction.cancel(Component.literal("Imprinted for another circuit"));
        int max = 4 * Math.max(1, cal.slices);
        ModifierFunction par = GTAAModifiers.parallel(m -> max, m -> 1.0, 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        return par.andThen(GTRecipeModifiers.OC_PERFECT.getModifier(machine, par.apply(recipe)));
    }

    @Override
    public boolean beforeWorking(GTRecipe recipe) {
        if (!super.beforeWorking(recipe)) return false;
        if (!assemblerMode && imprint.isEmpty() && recipe != null) imprint = output(recipe);
        return true;
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player player, InteractionHand hand, Direction side, BlockHitResult hit) {
        if (isRemote()) return InteractionResult.SUCCESS;
        assemblerMode = !assemblerMode;
        player.sendSystemMessage(Component.literal("Mode: " + (assemblerMode ? "Circuit Assembler" : "Circuit Assembly Line")));
        return InteractionResult.CONSUME;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Mode: " + (assemblerMode ? "Circuit Assembler" : "Circuit Assembly Line") +
                " (screwdriver to switch)").withStyle(ChatFormatting.GOLD));
        if (!assemblerMode) {
            var item = imprint.isEmpty() ? null : BuiltInRegistries.ITEM.get(new net.minecraft.resources.ResourceLocation(imprint));
            textList.add(Component.literal("Imprint: ").append(item == null ? Component.literal("none - the first circuit made sets it")
                    : new ItemStack(item).getHoverName()).withStyle(ChatFormatting.AQUA));
            textList.add(Component.literal(slices + " slices: " + 4 * slices + " parallels").withStyle(ChatFormatting.AQUA));
        }
    }
}
