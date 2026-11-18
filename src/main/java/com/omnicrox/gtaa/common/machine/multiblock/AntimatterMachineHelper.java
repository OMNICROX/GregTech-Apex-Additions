package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

/** Shared helpers for the antimatter machines. */
final class AntimatterMachineHelper {

    private AntimatterMachineHelper() {}

    /** A fluid sitting in one slot of one input hatch. */
    record StoredFluid(NotifiableFluidTank tank, int slot, FluidStack stack) {}

    static List<StoredFluid> inputFluids(IMultiController controller) {
        List<StoredFluid> out = new ArrayList<>();
        for (IMultiPart part : controller.getParts()) {
            for (RecipeHandlerList list : part.getRecipeHandlers()) {
                if (!list.isValid(IO.IN)) continue;
                for (Object handler : list.getCapability(FluidRecipeCapability.CAP)) {
                    if (!(handler instanceof NotifiableFluidTank tank)) continue;
                    if (tank.getHandlerIO() != IO.IN) continue;
                    for (int i = 0; i < tank.getTanks(); i++) {
                        FluidStack fs = tank.getFluidInTank(i);
                        if (!fs.isEmpty()) out.add(new StoredFluid(tank, i, fs));
                    }
                }
            }
        }
        return out;
    }

    static long amountOf(List<StoredFluid> fluids, Fluid fluid) {
        long sum = 0;
        for (StoredFluid f : fluids) if (f.stack().getFluid() == fluid) sum += f.stack().getAmount();
        return sum;
    }

    /** Drains exactly {@code amount} of {@code fluid}, or nothing if there isn't enough. */
    static boolean deplete(List<StoredFluid> fluids, Fluid fluid, long amount) {
        if (amount <= 0) return true;
        if (amountOf(fluids, fluid) < amount) return false;
        long left = amount;
        for (StoredFluid f : fluids) {
            if (left <= 0) break;
            if (f.stack().getFluid() != fluid) continue;
            int take = (int) Math.min(left, f.stack().getAmount());
            FluidStack drained = f.tank().drainInternal(new FluidStack(fluid, take), IFluidHandler.FluidAction.EXECUTE);
            left -= drained.getAmount();
        }
        return left <= 0;
    }

    static void drainAll(StoredFluid f) {
        f.tank().drainInternal(f.stack().copy(), IFluidHandler.FluidAction.EXECUTE);
    }

    /** Fluid of a material registered by any mod (e.g. "gtna:magmatter"), or null if that mod isn't installed. */
    static Fluid fluidOf(String materialId) {
        try {
            Material m = GTCEuAPI.materialManager.getMaterial(materialId);
            return m == null || m.isNull() ? null : m.getFluid();
        } catch (Throwable t) {
            return null;
        }
    }

    static Fluid fluidOf(Material m) {
        return m == null ? null : m.getFluid();
    }
}
