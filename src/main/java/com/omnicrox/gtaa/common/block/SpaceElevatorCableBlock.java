package com.omnicrox.gtaa.common.block;

import com.omnicrox.gtaa.common.data.GTAABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** The Space Elevator Cable: renders the cable to the sky (and its climber) while the elevator is formed. */
public class SpaceElevatorCableBlock extends Block implements EntityBlock {

    public SpaceElevatorCableBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return GTAABlocks.SPACE_ELEVATOR_CABLE_BE.create(pos, state);
    }
}
