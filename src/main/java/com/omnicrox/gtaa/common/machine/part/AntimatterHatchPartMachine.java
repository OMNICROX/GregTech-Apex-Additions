package com.omnicrox.gtaa.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Antimatter Hatch: 16,384,000 L output hatch locked to Semi-Stable Antimatter.
 * Port of GoodGenerator's AntimatterOutputHatch (GT5-Unofficial).
 */
public class AntimatterHatchPartMachine extends FluidHatchPartMachine {

    public static final int CAPACITY = 16_384_000;

    public AntimatterHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.UEV, IO.OUT, CAPACITY, 1);
    }

    @Override
    protected NotifiableFluidTank createTank(int initialCapacity, int slots, Object... args) {
        return new NotifiableFluidTank(this, 1, CAPACITY, IO.OUT)
                .setFilter(fs -> GTAAMaterials.ANTIMATTER != null && fs.getFluid() == GTAAMaterials.ANTIMATTER.getFluid());
    }

    public long getAmount() {
        return tank.getFluidInTank(0).getAmount();
    }

    public void fillAntimatter(int amount) {
        if (amount > 0) tank.fillInternal(GTAAMaterials.ANTIMATTER.getFluid(amount), IFluidHandler.FluidAction.EXECUTE);
    }

    public int drainAntimatter(int amount) {
        if (amount <= 0) return 0;
        FluidStack drained = tank.drainInternal(amount, IFluidHandler.FluidAction.EXECUTE);
        return drained.getAmount();
    }
}
