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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hatches on a formed multi take the controller's casing texture when they're drawn, but the hatch is often redrawn
 * before the client has heard that its controller formed (or before the controller's chunk has loaded), and nothing
 * redraws it afterwards, so it keeps its own hull colour until F3+A. This watches every multi controller near the
 * player and, whenever one forms, unforms or comes into view, redraws its hatches (again a second later, in case the
 * sync was late). Works for every GregTech multi, not just ours.
 */
@Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FormedPartRefresh {

    private FormedPartRefresh() {}

    private static final int SCAN_EVERY = 10, PART_CHUNK_RADIUS = 3, MAX_SCAN_RADIUS = 10;
    private static final Map<BlockPos, Boolean> FORMED = new HashMap<>();
    /** controller -> tick to redraw its hatches again */
    private static final Map<BlockPos, Long> AGAIN = new HashMap<>();
    private static ClientLevel lastLevel;
    private static long ticks;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) {
            FORMED.clear();
            AGAIN.clear();
            com.omnicrox.gtaa.common.block.ManaBloomLeavesBlock.ARBITERS.clear();
            lastLevel = null;
            return;
        }
        if (level != lastLevel) {
            FORMED.clear();
            AGAIN.clear();
            com.omnicrox.gtaa.common.block.ManaBloomLeavesBlock.ARBITERS.clear();
            lastLevel = level;
        }
        ticks++;
        if (!AGAIN.isEmpty()) {
            List<BlockPos> due = new ArrayList<>();
            AGAIN.forEach((p, t) -> { if (t <= ticks) due.add(p); });
            for (BlockPos p : due) {
                AGAIN.remove(p);
                redrawParts(level, p);
            }
        }
        if (ticks % SCAN_EVERY != 0) return;

        int r = Math.min(MAX_SCAN_RADIUS, mc.options.getEffectiveRenderDistance());
        ChunkPos center = mc.player.chunkPosition();
        Set<BlockPos> seen = new HashSet<>();
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                LevelChunk c = level.getChunkSource().getChunk(center.x + dx, center.z + dz, false);
                if (c == null) continue;
                for (BlockEntity be : c.getBlockEntities().values()) {
                    if (!(be instanceof IMachineBlockEntity mbe) || !(mbe.getMetaMachine() instanceof IMultiController ctrl))
                        continue;
                    BlockPos p = be.getBlockPos().immutable();
                    seen.add(p);
                    boolean formed = ctrl.isFormed();
                    if (ctrl instanceof com.omnicrox.gtaa.common.machine.multiblock.ManaNexusMachine arbiter)
                        arbiterTier(mc, p, formed ? arbiter.tier() : 0);
                    Boolean before = FORMED.put(p, formed);
                    if (before == null ? formed : before != formed) {
                        redrawParts(level, p);
                        AGAIN.put(p, ticks + 20);
                    }
                }
            }
        }
        FORMED.keySet().retainAll(seen);
        var arbiters = com.omnicrox.gtaa.common.block.ManaBloomLeavesBlock.ARBITERS;
        for (BlockPos gone : new ArrayList<>(arbiters.keySet()))
            if (!seen.contains(gone)) {
                arbiters.remove(gone);
                redrawLeaves(mc, gone);
            }
    }

    /** the Sylvan Arbiter's leaves are tinted by its tier: redraw them when it changes */
    private static void arbiterTier(Minecraft mc, BlockPos p, int tier) {
        Integer before = com.omnicrox.gtaa.common.block.ManaBloomLeavesBlock.ARBITERS.put(p, tier);
        if (before == null || before != tier) redrawLeaves(mc, p);
    }

    private static void redrawLeaves(Minecraft mc, BlockPos p) {
        int r = com.omnicrox.gtaa.common.block.ManaBloomLeavesBlock.REACH_XZ;
        mc.levelRenderer.setBlocksDirty(p.getX() - r, p.getY() - com.omnicrox.gtaa.common.block.ManaBloomLeavesBlock.REACH_DOWN,
                p.getZ() - r, p.getX() + r, p.getY() + com.omnicrox.gtaa.common.block.ManaBloomLeavesBlock.REACH_UP, p.getZ() + r);
    }

    private static void redrawParts(ClientLevel level, BlockPos controller) {
        Minecraft mc = Minecraft.getInstance();
        ChunkPos cp = new ChunkPos(controller);
        for (int dx = -PART_CHUNK_RADIUS; dx <= PART_CHUNK_RADIUS; dx++) {
            for (int dz = -PART_CHUNK_RADIUS; dz <= PART_CHUNK_RADIUS; dz++) {
                LevelChunk c = level.getChunkSource().getChunk(cp.x + dx, cp.z + dz, false);
                if (c == null) continue;
                for (BlockEntity be : c.getBlockEntities().values()) {
                    if (!(be instanceof IMachineBlockEntity mbe) || !(mbe.getMetaMachine() instanceof IMultiPart part))
                        continue;
                    boolean belongs = part.getControllers().isEmpty();
                    for (IMultiController ctrl : part.getControllers())
                        if (ctrl.self().getPos().equals(controller)) belongs = true;
                    if (!belongs) continue;
                    BlockPos p = be.getBlockPos();
                    mc.levelRenderer.setBlocksDirty(p.getX(), p.getY(), p.getZ(), p.getX(), p.getY(), p.getZ());
                }
            }
        }
    }
}
