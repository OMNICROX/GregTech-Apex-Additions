package com.omnicrox.gtaa.common.personal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** GT New Horizons' Personal Space portal: takes you to your own world, and inside it, back to where you came from */
public class PersonalPortalBlock extends Block {

    public PersonalPortalBlock(Properties p) {
        super(p);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!level.isClientSide && player instanceof ServerPlayer sp) PersonalSpace.usePortal(sp, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
