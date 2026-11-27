package com.omnicrox.apexlib.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

/** Reading and draining a multiblock's hatches and buses outside of recipe matching. */
public final class ApexHatches {

    private ApexHatches() {}

    public static List<NotifiableFluidTank> fluidTanks(IMultiController machine, IO io) {
        List<NotifiableFluidTank> out = new ArrayList<>();
        for (IMultiPart part : machine.getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != io) continue;
                for (var h : handler.getCapability(FluidRecipeCapability.CAP))
                    if (h instanceof NotifiableFluidTank t) out.add(t);
            }
        }
        return out;
    }

    public static List<NotifiableItemStackHandler> itemHandlers(IMultiController machine, IO io) {
        List<NotifiableItemStackHandler> out = new ArrayList<>();
        for (IMultiPart part : machine.getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != io) continue;
                for (var h : handler.getCapability(ItemRecipeCapability.CAP))
                    if (h instanceof NotifiableItemStackHandler t) out.add(t);
            }
        }
        return out;
    }

    /** mB of the fluid in the machine's input hatches (simulated, nothing drained) */
    public static long amount(IMultiController machine, Fluid fluid) {
        long n = 0;
        for (var t : fluidTanks(machine, IO.IN))
            n += t.drainInternal(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE).getAmount();
        return n;
    }

    public static boolean has(IMultiController machine, FluidStack stack) {
        return !stack.isEmpty() && amount(machine, stack.getFluid()) >= stack.getAmount();
    }

    /** drains the whole stack from the input hatches, or nothing if there isn't enough */
    public static boolean drain(IMultiController machine, FluidStack stack) {
        if (!has(machine, stack)) return false;
        int left = stack.getAmount();
        for (var t : fluidTanks(machine, IO.IN)) {
            left -= t.drainInternal(new FluidStack(stack, left), IFluidHandler.FluidAction.EXECUTE).getAmount();
            if (left <= 0) break;
        }
        return true;
    }
}
