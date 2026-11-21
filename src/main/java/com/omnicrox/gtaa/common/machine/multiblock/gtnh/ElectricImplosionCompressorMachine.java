package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Electric Implosion Compressor (GT5-Unofficial): implosion recipes without explosives. The containment block
 * (Neutronium / Infinity / Transcendent Metal / Spacetime / Universium) sets the parallels: 1 / 4 / 16 / 64 / 256.
 */
public class ElectricImplosionCompressorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ElectricImplosionCompressorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX = "EICContainment";
    public static final String[] NAMES = { "Neutronium", "Infinity", "Transcendent Metal", "Spacetime", "Universium" };
    private static final String[] MATS = { "gtceu:neutronium", "gtna:infinity", "gtna:transcendent_metal", "gtna:space_time",
            "gtna:universium" };

    @DescSynced private int containment;

    public ElectricImplosionCompressorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    static Material mat(String id) {
        try {
            Material m = GTCEuAPI.materialManager.getMaterial(id);
            if (m != null && !m.isNull()) return m;
        } catch (Throwable ignored) {}
        return null;
    }

    /** containment block per tier (index 0 = tier 1), null when that material isn't in the pack */
    public static Block[] containmentBlocks() {
        Block[] out = new Block[MATS.length];
        for (int i = 0; i < MATS.length; i++) {
            Material m = i == 0 ? GTMaterials.Neutronium : mat(MATS[i]);
            if (m == null) continue;
            try {
                out[i] = ChemicalHelper.getBlock(TagPrefix.block, m);
            } catch (Throwable ignored) {}
        }
        return out;
    }

    public static TraceabilityPredicate containmentPredicate() {
        return new TraceabilityPredicate(s -> {
            Block[] arr = containmentBlocks();
            int tier = 0;
            for (int i = 0; i < arr.length; i++) if (arr[i] != null && arr[i] == s.getBlockState().getBlock()) tier = i + 1;
            if (tier == 0) return false;
            Object cur = s.getMatchContext().getOrPut(CTX, tier);
            if (!cur.equals(tier)) {
                s.setError(new PatternStringError("Containment blocks must all be the same material"));
                return false;
            }
            return true;
        }, () -> {
            List<BlockInfo> l = new ArrayList<>();
            for (Block b : containmentBlocks()) if (b != null) l.add(BlockInfo.fromBlockState(b.defaultBlockState()));
            return l.toArray(BlockInfo[]::new);
        });
    }

    public static int maxParallel(MetaMachine m) {
        return m instanceof ElectricImplosionCompressorMachine e && e.containment > 0 ? 1 << (2 * (e.containment - 1)) : 1;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object o = getMultiblockState().getMatchContext().get(CTX);
        containment = o instanceof Integer i ? i : 0;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        containment = 0;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed() && containment > 0)
            textList.add(Component.literal("Containment: " + NAMES[containment - 1] + " (" + maxParallel(this) + " parallels)")
                    .withStyle(ChatFormatting.AQUA));
    }
}
