package com.omnicrox.gtaa.common.block;

import com.omnicrox.gtaa.common.stargate.ApexStargate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Ascension Gate Base: goes where the gate's base goes. With the whole frame around it (9 chevrons, 14 rings),
 * right-clicking it turns the frame into the Warpgate, like Stargate Journey's classic gate base. The world's first
 * one then charges itself from the player's wireless network (ApexGateCharge).
 */
public class GateBaseBlock extends Block {

    public GateBaseBlock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!ApexStargate.available()) {
            player.displayClientMessage(Component.literal("Stargate Journey isn't installed").withStyle(ChatFormatting.RED), true);
            return InteractionResult.CONSUME;
        }
        var facing = ApexStargate.findFrame(level, pos, player.getDirection().getOpposite());
        if (facing.isEmpty()) {
            player.displayClientMessage(Component.literal("The frame isn't complete: 9 Ascension Gate Chevrons and 14 Rings in the gate's shape")
                    .withStyle(ChatFormatting.YELLOW), true);
            player.displayClientMessage(com.omnicrox.gtaa.common.stargate.ApexGateCharge.status(level.getServer()), false);
            return InteractionResult.CONSUME;
        }
        if (ApexStargate.form((ServerLevel) level, pos, facing.get(), player))
            com.omnicrox.gtaa.common.stargate.ApexGateCharge.onFormed((ServerLevel) level, pos, facing.get(), player);
        return InteractionResult.CONSUME;
    }
}
