package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

/** Hatch helpers shared by the GTNH-port multis. */
final class GTNHMultiHelper {

    private GTNHMultiHelper() {}

    static List<NotifiableFluidTank> tanks(MultiblockControllerMachine m) {
        List<NotifiableFluidTank> l = new ArrayList<>();
        for (IMultiPart part : m.getParts())
            for (var h : part.getRecipeHandlers())
                if (h.getHandlerIO() == IO.IN)
                    for (var c : h.getCapability(FluidRecipeCapability.CAP)) if (c instanceof NotifiableFluidTank t) l.add(t);
        return l;
    }

    static List<NotifiableItemStackHandler> buses(MultiblockControllerMachine m) {
        List<NotifiableItemStackHandler> l = new ArrayList<>();
        for (IMultiPart part : m.getParts())
            for (var h : part.getRecipeHandlers())
                if (h.getHandlerIO() == IO.IN)
                    for (var c : h.getCapability(ItemRecipeCapability.CAP)) if (c instanceof NotifiableItemStackHandler t) l.add(t);
        return l;
    }

    /** drains exactly want (all or nothing) from the input hatches */
    static boolean drain(MultiblockControllerMachine m, FluidStack want, boolean simulate) {
        if (want == null || want.isEmpty()) return false;
        int left = want.getAmount();
        List<NotifiableFluidTank> tanks = tanks(m);
        for (NotifiableFluidTank t : tanks) {
            left -= t.drainInternal(new FluidStack(want, left), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (left <= 0) break;
        }
        if (left > 0) return false;
        if (simulate) return true;
        left = want.getAmount();
        for (NotifiableFluidTank t : tanks) {
            left -= t.drainInternal(new FluidStack(want, left), IFluidHandler.FluidAction.EXECUTE).getAmount();
            if (left <= 0) break;
        }
        return true;
    }

    /** takes one of the item from the input buses */
    static boolean consume(MultiblockControllerMachine m, Item item) {
        for (NotifiableItemStackHandler inv : buses(m))
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack s = inv.getStackInSlot(i);
                if (!s.isEmpty() && s.is(item)) {
                    inv.extractItemInternal(i, 1, false);
                    return true;
                }
            }
        return false;
    }
}
