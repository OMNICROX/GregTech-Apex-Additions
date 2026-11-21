package com.omnicrox.gtaa.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The open Ascension Gate's shimmering surface. Walk-through, glowing, and it gives anyone stepping in a little
 * sparkle. Placed and removed by the gate itself; it drops nothing.
 */
public class EventHorizonBlock extends Block {

    public EventHorizonBlock(Properties props) {
        super(props.noCollission().noOcclusion().lightLevel(s -> 15).strength(-1.0f, 3600000.0f).noLootTable());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState neighbor, net.minecraft.core.Direction dir) {
        return neighbor.is(this) || super.skipRendering(state, neighbor, dir);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level instanceof ServerLevel sl && entity instanceof Player && level.getGameTime() % 4 == 0)
            sl.sendParticles(ParticleTypes.END_ROD, entity.getX(), entity.getY() + 1, entity.getZ(), 6, 0.3, 0.6, 0.3, 0.02);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0)
            level.addParticle(ParticleTypes.PORTAL, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(),
                    pos.getZ() + 0.5, 0, 0, 0);
    }
}
