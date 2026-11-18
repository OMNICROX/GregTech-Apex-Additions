package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;

/**
 * Fusion Reactor MK IV (UHV) and MK V (UEV).
 * Ported from GT++ MTEAdvFusionMk4 / MTEAdvFusionMk5 (GT5-Unofficial, LGPL-3.0):
 * same energy buffer per energy hatch as GTNH (MK IV 320M EU, MK V 1.28B EU), perfect fusion overclocks,
 * running the normal fusion recipe map. Logic otherwise reuses GTCEu's FusionReactorMachine.
 */
public class AdvancedFusionReactorMachine extends FusionReactorMachine {

    public AdvancedFusionReactorMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    /** EU buffer added per energy hatch (GTNH values). */
    public static long capacityPerHatch(int tier) {
        if (tier >= GTValues.UEV) return 640_010_000L * 16 / 8; // 1,280,020,000
        return 640_010_000L * 4 / 8;                            //   320,005,000
    }

    public static long totalCapacity(int tier, int hatches) {
        return capacityPerHatch(tier) * Math.min(16, hatches);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        // GTCEu sizes the buffer as hatches * 2^(tier-LuV) * 10M; recover the hatch count and apply GTNH's value
        long gtceuPerHatch = FusionReactorMachine.calculateEnergyStorageFactor(getTier(), 1);
        int hatches = (int) (energyContainer.getEnergyCapacity() / Math.max(1, gtceuPerHatch));
        energyContainer.resetBasicInfo(totalCapacity(getTier(), hatches), 0, 0, 0, 0);
    }
}
