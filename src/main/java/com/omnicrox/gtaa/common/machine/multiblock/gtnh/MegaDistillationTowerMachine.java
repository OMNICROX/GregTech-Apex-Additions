package com.omnicrox.gtaa.common.machine.multiblock.gtnh;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.DistillationTowerMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Mega Distillation Tower with GT5-Unofficial's structure: a base, 1-5 middle layers (6 blocks tall, two output
 * hatches each) and a top. GTCEu's tower gives one output per block of height and voids the gaps; here the outputs go
 * to the fluid output hatches in height order, gaps skipped, so the 1st distillate is the lowest hatch and so on.
 */
public class MegaDistillationTowerMachine extends DistillationTowerMachine
        implements com.omnicrox.apexlib.multiblock.IStagedMulti {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MegaDistillationTowerMachine.class, com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final int MAX_LAYERS = 5;

    @DescSynced private int layers = 1;

    public MegaDistillationTowerMachine(IMachineBlockEntity holder) {
        super(holder, 1);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    /** tries the tallest tower first; the one that matches decides the layer count */
    @Override
    public boolean checkPattern() {
        for (int l = MAX_LAYERS; l >= 1; l--) {
            BlockPattern p = GTAAMachines.megaDistillationPattern(getDefinition(), l);
            if (p != null && p.checkPatternAt(getMultiblockState(), false)) {
                layers = l;
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockPattern getPattern() {
        return GTAAMachines.megaDistillationPattern(getDefinition(), layers);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isFormed()) return;
        List<IMultiPart> hatches = new ArrayList<>();
        for (IMultiPart part : getParts())
            if (PartAbility.EXPORT_FLUIDS.isApplicable(part.self().getBlockState().getBlock())) hatches.add(part);
        hatches.sort(Comparator.comparingInt(p -> p.self().getPos().getY()));
        List<IFluidHandler> outs = new ArrayList<>();
        for (IMultiPart part : hatches)
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.OUT) continue;
                handler.getCapability(FluidRecipeCapability.CAP).stream().filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast).findFirst().ifPresent(outs::add);
                break;
            }
        try {
            var f = DistillationTowerMachine.class.getDeclaredField("fluidOutputs");
            f.setAccessible(true);
            f.set(this, new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(outs));
            var v = DistillationTowerMachine.class.getDeclaredField("firstValid");
            v.setAccessible(true);
            v.set(this, outs.isEmpty() ? null : outs.get(0));
        } catch (ReflectiveOperationException e) {
            com.omnicrox.gtaa.GTAA.LOGGER.error("[GTAA] Mega Distillation Tower could not set its outputs", e);
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) textList.add(Component.literal(layers + " / " + MAX_LAYERS + " layers").withStyle(ChatFormatting.AQUA));
    }

    // ---- Structure Upgrader: 1-5 layers ----
    @Override public int stageCount() { return MAX_LAYERS; }
    @Override public int currentStage() { return isFormed() ? layers : 0; }
    @Override public com.gregtechceu.gtceu.api.pattern.BlockPattern stagePattern(int stage) {
        return com.omnicrox.gtaa.common.data.GTAAMachines.megaDistillationPattern(getDefinition(), stage);
    }
    @Override public String stageName(int stage) { return stage + (stage == 1 ? " layer" : " layers"); }
    @Override public String stageAdds(int stage) { return "two more output layers"; }
}
