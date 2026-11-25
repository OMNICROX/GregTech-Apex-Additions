package com.omnicrox.gtaa.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Sylvan Arbiter's leaves. They never decay and take the colour of the tier the Arbiter around them runs at:
 * grey-green unformed, then Manasteel blue, Terrasteel lime, Elementium pink, Alfsteel orange, Gaiasteel emerald.
 * The colour is worked out on the client from the Arbiters it can see (nothing in the world changes, so GregTech
 * never has to re-check the structure for it).
 */
public class ManaBloomLeavesBlock extends Block {

    public static final int[] COLORS = { 0x8FA88A, 0x4A9BFF, 0x7EE04A, 0xF27BD0, 0xFF9A2E, 0x1FD68A };
    /** client: Arbiter controller -> its tier, kept by FormedPartRefresh */
    public static final Map<BlockPos, Integer> ARBITERS = new ConcurrentHashMap<>();
    /** how far an Arbiter's leaves can be from its controller */
    public static final int REACH_XZ = 50, REACH_DOWN = 8, REACH_UP = 45;

    public ManaBloomLeavesBlock(Properties props) {
        super(props);
    }

    public static boolean inReach(BlockPos controller, BlockPos pos) {
        int dy = pos.getY() - controller.getY();
        return Math.abs(pos.getX() - controller.getX()) <= REACH_XZ && Math.abs(pos.getZ() - controller.getZ()) <= REACH_XZ &&
                dy >= -REACH_DOWN && dy <= REACH_UP;
    }

    public static int color(BlockPos pos) {
        if (pos == null) return COLORS[0];
        int best = 0;
        for (var e : ARBITERS.entrySet()) if (e.getValue() > best && inReach(e.getKey(), pos)) best = e.getValue();
        return COLORS[Math.max(0, Math.min(5, best))];
    }
}
