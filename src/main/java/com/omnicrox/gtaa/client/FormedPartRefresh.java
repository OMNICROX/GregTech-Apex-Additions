package com.omnicrox.gtaa.client;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.omnicrox.gtaa.GTAA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Hatches on a formed multi take the controller's casing texture, but only if the controller is loaded when the
 * hatch's chunk is drawn. Our multis are big, so the controller often sits in another chunk that arrives later, and
 * the hatches keep their own hull (teal UEV hatches on the SASS...) until something redraws them. When a chunk with
 * one of our controllers loads, this redraws every hatch around it that belongs to it.
 */
@Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FormedPartRefresh {

    private FormedPartRefresh() {}

    private static final int CHUNK_RADIUS = 3;
    private static final ArrayDeque<ChunkPos> PENDING = new ArrayDeque<>();

    @SubscribeEvent
    public static void chunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ClientLevel) || !(event.getChunk() instanceof LevelChunk chunk)) return;
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (be instanceof IMachineBlockEntity mbe && mbe.getMetaMachine() instanceof IMultiController
                    && GTAA.MOD_ID.equals(mbe.getDefinition().getId().getNamespace())) {
                synchronized (PENDING) {
                    PENDING.add(chunk.getPos());
                }
                return;
            }
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        List<ChunkPos> todo;
        synchronized (PENDING) {
            if (PENDING.isEmpty()) return;
            todo = new ArrayList<>(PENDING);
            PENDING.clear();
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        for (ChunkPos center : todo) {
            List<BlockPos> controllers = new ArrayList<>();
            LevelChunk own = level.getChunkSource().getChunk(center.x, center.z, false);
            if (own == null) continue;
            for (BlockEntity be : own.getBlockEntities().values())
                if (be instanceof IMachineBlockEntity mbe && mbe.getMetaMachine() instanceof IMultiController)
                    controllers.add(be.getBlockPos());
            if (controllers.isEmpty()) continue;
            for (int dx = -CHUNK_RADIUS; dx <= CHUNK_RADIUS; dx++) {
                for (int dz = -CHUNK_RADIUS; dz <= CHUNK_RADIUS; dz++) {
                    LevelChunk c = level.getChunkSource().getChunk(center.x + dx, center.z + dz, false);
                    if (c == null) continue;
                    for (BlockEntity be : c.getBlockEntities().values()) {
                        if (!(be instanceof IMachineBlockEntity mbe) || !(mbe.getMetaMachine() instanceof IMultiPart part))
                            continue;
                        boolean ours = false;
                        for (IMultiController ctrl : part.getControllers())
                            if (controllers.contains(ctrl.self().getPos())) ours = true;
                        if (!ours) continue;
                        BlockPos p = be.getBlockPos();
                        Minecraft.getInstance().levelRenderer.setBlocksDirty(p.getX(), p.getY(), p.getZ(), p.getX(), p.getY(), p.getZ());
                    }
                }
            }
        }
    }
}
