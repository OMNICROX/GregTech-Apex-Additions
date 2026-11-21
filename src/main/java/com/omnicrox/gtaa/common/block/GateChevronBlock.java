package com.omnicrox.gtaa.common.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Ascension Gate chevron: lights up when the gate locks it. */
public class GateChevronBlock extends Block {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public GateChevronBlock(Properties props) {
        super(props.lightLevel(s -> s.getValue(LIT) ? 12 : 0));
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState();
    }
}
