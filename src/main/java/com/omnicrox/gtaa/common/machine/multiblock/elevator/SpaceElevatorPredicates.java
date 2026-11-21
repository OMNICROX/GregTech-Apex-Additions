package com.omnicrox.gtaa.common.machine.multiblock.elevator;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/** Pattern predicates for the Space Elevator (GTNH-Intergalactic port). */
public final class SpaceElevatorPredicates {

    public static final String CTX_MOTOR = "SEMotorTier";
    public static final String CTX_MODULES = "SEModules";
    public static final String CTX_CABLE = "SECable";
    /** every module slot (base casing or module), so modules placed later are found without a re-form */
    public static final String CTX_SLOTS = "SESlots";

    private SpaceElevatorPredicates() {}

    private static boolean isBase(BlockState state) {
        return state.is(GTAABlocks.SE_BASE_CASING.get());
    }

    /** Motors: all the same tier; the tier is stored in the match context. */
    public static TraceabilityPredicate motors() {
        return new TraceabilityPredicate(s -> {
            int tier = GTAABlocks.motorTier(s.getBlockState().getBlock());
            if (tier == 0) return false;
            Object current = s.getMatchContext().getOrPut(CTX_MOTOR, tier);
            if (!current.equals(tier)) {
                s.setError(new PatternStringError("Space Elevator Motors must all be the same tier"));
                return false;
            }
            return true;
        }, () -> new BlockInfo[] {
                BlockInfo.fromBlockState(GTAABlocks.SE_MOTOR_MK1.get().defaultBlockState()),
                BlockInfo.fromBlockState(GTAABlocks.SE_MOTOR_MK2.get().defaultBlockState()),
                BlockInfo.fromBlockState(GTAABlocks.SE_MOTOR_MK3.get().defaultBlockState()),
                BlockInfo.fromBlockState(GTAABlocks.SE_MOTOR_MK4.get().defaultBlockState()),
                BlockInfo.fromBlockState(GTAABlocks.SE_MOTOR_MK5.get().defaultBlockState()) });
    }

    /** Module slot: base casing, or a Space Elevator module controller (position recorded). */
    public static TraceabilityPredicate moduleSlot(Supplier<Block[]> moduleBlocks) {
        return new TraceabilityPredicate(s -> {
            boolean base = isBase(s.getBlockState());
            boolean module = !base && MetaMachine.getMachine(s.getWorld(), s.getPos()) instanceof SpaceElevatorModuleMachine;
            if (base || module) s.getMatchContext().getOrCreate(CTX_SLOTS, LongOpenHashSet::new).add(s.getPos().asLong());
            if (base) return true;
            if (module) {
                s.getMatchContext().getOrCreate(CTX_MODULES, LongOpenHashSet::new).add(s.getPos().asLong());
                return true;
            }
            return false;
        }, () -> {
            Block[] modules = moduleBlocks.get();
            BlockInfo[] infos = new BlockInfo[modules.length + 1];
            infos[0] = BlockInfo.fromBlockState(GTAABlocks.SE_BASE_CASING.get().defaultBlockState());
            for (int i = 0; i < modules.length; i++) infos[i + 1] = BlockInfo.fromBlockState(modules[i].defaultBlockState());
            return infos;
        });
    }

    /** Module IO space: base casing or any hatch (the hatch belongs to the module, not the elevator). */
    public static TraceabilityPredicate moduleIO() {
        return new TraceabilityPredicate(s -> {
            BlockState state = s.getBlockState();
            if (isBase(state)) return true;
            return state.getBlock() instanceof MetaMachineBlock &&
                    MetaMachine.getMachine(s.getWorld(), s.getPos()) instanceof IMultiPart;
        }, () -> new BlockInfo[] { BlockInfo.fromBlockState(GTAABlocks.SE_BASE_CASING.get().defaultBlockState()) });
    }

    /** The cable: must see the sky. */
    public static TraceabilityPredicate cable() {
        return new TraceabilityPredicate(s -> {
            if (!s.getBlockState().is(GTAABlocks.SPACE_ELEVATOR_CABLE.get())) return false;
            if (!s.getWorld().canSeeSky(s.getPos().above())) {
                s.setError(new PatternStringError("The Space Elevator Cable needs to see the sky"));
                return false;
            }
            s.getMatchContext().set(CTX_CABLE, s.getPos().immutable());
            return true;
        }, () -> new BlockInfo[] { BlockInfo.fromBlockState(GTAABlocks.SPACE_ELEVATOR_CABLE.get().defaultBlockState()) });
    }
}
