package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Integrated Assembly Matrix (Twist Space Technology "TST_IntegratedAssemblyMatrix"): assembly line recipes (still
 * needs its data stick in a Data Access Hatch), 128 parallels at 1.5x time. Silver nanites in an input bus (up to 256)
 * bring the time down to 1x; gold nanites (up to 1024) to 0.5x and double the parallels. Nanites are not used up.
 */
public class IntegratedAssemblyMatrixMachine extends WorkableElectricMultiblockMachine {

    public static final int PARALLEL = 128, MAX_SILVER = 256, MAX_GOLD = 1024;

    public IntegratedAssemblyMatrixMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private long count(String path) {
        long n = 0;
        for (var bus : ApexHatches.itemHandlers(this, IO.IN))
            for (int i = 0; i < bus.getSlots(); i++) {
                var s = bus.getStackInSlot(i);
                if (s.isEmpty()) continue;
                var id = ForgeRegistries.ITEMS.getKey(s.getItem());
                if (id != null && id.getPath().equals(path)) n += s.getCount();
            }
        return n;
    }

    public double speed() {
        long gold = Math.min(MAX_GOLD, count("gold_nanites"));
        if (gold > 0) return 1.0 - 0.5 * gold / MAX_GOLD;
        long silver = Math.min(MAX_SILVER, count("silver_nanites"));
        return 1.5 - 0.5 * silver / MAX_SILVER;
    }

    public int parallel() {
        long gold = Math.min(MAX_GOLD, count("gold_nanites"));
        return (int) (PARALLEL * (1 + (double) gold / MAX_GOLD));
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof IntegratedAssemblyMatrixMachine m)) return ModifierFunction.NULL;
        ModifierFunction par = ApexModifiers.parallel(x -> m.parallel(), x -> m.speed(), 1.0).getModifier(machine, recipe);
        if (par == ModifierFunction.NULL) return par;
        GTRecipe scaled = par.apply(recipe);
        return scaled == null ? ModifierFunction.NULL : par.andThen(GTRecipeModifiers.OC_NON_PERFECT_SUBTICK.getModifier(machine, scaled));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal(String.format("Parallels: %d  |  Time: %.2fx", parallel(), speed())).withStyle(ChatFormatting.AQUA));
    }
}
