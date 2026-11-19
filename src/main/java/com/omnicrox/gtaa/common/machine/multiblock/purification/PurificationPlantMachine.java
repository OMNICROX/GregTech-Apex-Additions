package com.omnicrox.gtaa.common.machine.multiblock.purification;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Water Purification Plant. Port of GT5-Unofficial MTEPurificationPlant (LGPL-3.0), simplified:
 * purification units within 32 blocks link to the plant automatically and only run while it powers them.
 */
public class PurificationPlantMachine extends WorkableElectricMultiblockMachine {

    public static final int RANGE = 32;
    private static final Set<PurificationPlantMachine> PLANTS = Collections.newSetFromMap(new WeakHashMap<>());

    private final Set<PurificationUnitMachine> units = Collections.newSetFromMap(new WeakHashMap<>());
    private TickableSubscription tick;

    public PurificationPlantMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static PurificationPlantMachine findNearby(Level level, net.minecraft.core.BlockPos pos) {
        PurificationPlantMachine best = null;
        double bestD = Double.MAX_VALUE;
        synchronized (PLANTS) {
            for (PurificationPlantMachine p : PLANTS) {
                if (p.getLevel() != level || !p.isFormed() || p.isInValid()) continue;
                double d = p.getPos().distSqr(pos);
                if (d <= RANGE * RANGE && d < bestD) {
                    best = p;
                    bestD = d;
                }
            }
        }
        return best;
    }

    public void link(PurificationUnitMachine unit) {
        units.add(unit);
    }

    public void unlink(PurificationUnitMachine unit) {
        units.remove(unit);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) {
            synchronized (PLANTS) {
                PLANTS.add(this);
            }
            tick = subscribeServerTick(tick, this::distribute);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        synchronized (PLANTS) {
            PLANTS.remove(this);
        }
        for (PurificationUnitMachine u : new ArrayList<>(units)) u.unlinkFromPlant();
        units.clear();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        synchronized (PLANTS) {
            PLANTS.remove(this);
        }
    }

    private void distribute() {
        if (getOffsetTimer() % 20 != 0 || !isWorkingEnabled()) return;
        var energy = getEnergyContainer();
        if (energy == null || units.isEmpty()) return;
        List<PurificationUnitMachine> list = new ArrayList<>();
        for (PurificationUnitMachine u : units) if (u.isFormed() && !u.isInValid()) list.add(u);
        if (list.isEmpty()) return;
        long share = energy.getEnergyStored() / list.size();
        for (PurificationUnitMachine u : list) {
            long given = u.charge(Math.min(share, energy.getEnergyStored()));
            if (given > 0) energy.removeEnergy(given);
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Linked purification units: " + units.size()).withStyle(ChatFormatting.AQUA));
        for (PurificationUnitMachine u : units) {
            textList.add(Component.literal(" - Grade " + u.getGrade() + " unit at " + u.getPos().toShortString())
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
