package com.omnicrox.gtaa.common.stargate;

import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The Apex Stargate: Stargate Journey's Pegasus gate in More Gates' Warpgate skin, built from GTAA's gate frame.
 * The frame has the gate's exact shape (Stargate Journey's 24 ring positions): the Ascension Gate Base where the
 * gate's base goes, 9 Ascension Gate Chevrons and 14 Ascension Gate Rings. When it's complete it turns into the
 * real gate, like Stargate Journey's classic gate. Gates stay sealed (no dialing) until a world's first gate is
 * charged with 9.22 Qn EU by an Ascension Gate controller. No compile dependency on Stargate Journey.
 */
public final class ApexStargate {

    private ApexStargate() {}

    public static final ResourceLocation PEGASUS_GATE = new ResourceLocation("sgjourney", "pegasus_stargate");
    public static final ResourceLocation PEGASUS_RING = new ResourceLocation("sgjourney", "pegasus_ring");
    public static final ResourceLocation WARPGATE = new ResourceLocation("moregates", "warpgate");

    public enum Kind { BASE, RING, CHEVRON }

    /** Stargate Journey's StargatePart: name, width (to the facing's clockwise side), height, what the frame needs */
    public record Part(String name, int width, int height, Kind kind) {}

    public static final List<Part> PARTS = List.of(
            new Part("base", 0, 0, Kind.BASE), new Part("left", 1, 0, Kind.CHEVRON), new Part("left2", 2, 0, Kind.RING),
            new Part("left2_above", 2, 1, Kind.RING), new Part("left3_above", 3, 1, Kind.CHEVRON),
            new Part("left3_above2", 3, 2, Kind.RING), new Part("left3_above3", 3, 3, Kind.RING),
            new Part("left3_above4", 3, 4, Kind.CHEVRON), new Part("left3_above5", 3, 5, Kind.RING),
            new Part("left2_above5", 2, 5, Kind.RING), new Part("left2_above6", 2, 6, Kind.CHEVRON),
            new Part("left_above6", 1, 6, Kind.RING), new Part("above6", 0, 6, Kind.CHEVRON),
            new Part("right_above6", -1, 6, Kind.RING), new Part("right2_above6", -2, 6, Kind.CHEVRON),
            new Part("right2_above5", -2, 5, Kind.RING), new Part("right3_above5", -3, 5, Kind.RING),
            new Part("right3_above4", -3, 4, Kind.CHEVRON), new Part("right3_above3", -3, 3, Kind.RING),
            new Part("right3_above2", -3, 2, Kind.RING), new Part("right3_above", -3, 1, Kind.CHEVRON),
            new Part("right2_above", -2, 1, Kind.RING), new Part("right2", -2, 0, Kind.RING),
            new Part("right", -1, 0, Kind.CHEVRON));

    public static boolean available() {
        return ModList.get().isLoaded("sgjourney") && BuiltInRegistries.BLOCK.containsKey(PEGASUS_GATE);
    }

    public static BlockPos partPos(BlockPos base, Direction facing, Part p) {
        return base.relative(facing.getClockWise(), p.width()).above(p.height());
    }

    /** the air inside the ring (for the intro's event horizon) */
    public static List<BlockPos> interior(BlockPos base, Direction facing) {
        List<BlockPos> out = new ArrayList<>();
        for (int h = 1; h <= 5; h++) {
            int span = h == 1 || h == 5 ? 1 : 2;
            for (int w = -span; w <= span; w++) out.add(base.relative(facing.getClockWise(), w).above(h));
        }
        return out;
    }

    public static Vec3 center(BlockPos base) {
        return Vec3.atCenterOf(base.above(3));
    }

    private static Block frameBlock(Kind k) {
        return switch (k) {
            case BASE -> GTAABlocks.GATE_BASE.get();
            case RING -> GTAABlocks.GATE_RING.get();
            case CHEVRON -> GTAABlocks.GATE_CHEVRON.get();
        };
    }

    public static boolean frameComplete(Level level, BlockPos base, Direction facing) {
        for (Part p : PARTS) if (!level.getBlockState(partPos(base, facing, p)).is(frameBlock(p.kind()))) return false;
        return true;
    }

    public static boolean isGate(Level level, BlockPos base) {
        return BuiltInRegistries.BLOCK.getKey(level.getBlockState(base).getBlock()).equals(PEGASUS_GATE);
    }

    /** a complete frame around this base, facing the given way or the other way round (both axes are tried) */
    public static Optional<Direction> findFrame(Level level, BlockPos base, Direction preferred) {
        for (Direction d : new Direction[] { preferred, preferred.getClockWise() })
            if (frameComplete(level, base, d)) return Optional.of(d);
        return Optional.empty();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static BlockState with(BlockState s, String prop, String value) {
        Property p = s.getBlock().getStateDefinition().getProperty(prop);
        if (p == null) return s;
        Optional v = p.getValue(value);
        return v.isPresent() ? s.setValue(p, (Comparable) v.get()) : s;
    }

    /** Turns a complete frame into the Warpgate. @return false if Stargate Journey isn't there */
    public static boolean form(ServerLevel level, BlockPos base, Direction facing, LivingEntity placer) {
        if (!available()) return false;
        Block gate = BuiltInRegistries.BLOCK.get(PEGASUS_GATE);
        for (Part p : PARTS) level.setBlock(partPos(base, facing, p), Blocks.AIR.defaultBlockState(), 2 | 16);
        BlockState state = with(with(gate.defaultBlockState(), "facing", facing.getSerializedName()), "orientation", "regular");
        level.setBlock(base, state, 3);
        // Stargate Journey places the ring parts and sets the gate up when it's placed
        gate.setPlacedBy(level, base, level.getBlockState(base), placer, new ItemStack(gate));
        BlockEntity be = level.getBlockEntity(base);
        if (be != null) {
            try {
                // what Stargate Journey does for a gate it places itself: address, network, symbols
                be.getClass().getMethod("displayID").invoke(be);
                be.getClass().getMethod("generate").invoke(be);
            } catch (Throwable t) {
                GTAA.LOGGER.warn("[GTAA] could not add the gate to the Stargate network: {}", t.toString());
            }
            try {
                Method m = be.getClass().getMethod("setVariant", ResourceLocation.class);
                m.invoke(be, WARPGATE);
            } catch (Throwable t) {
                GTAA.LOGGER.warn("[GTAA] could not set the Warpgate variant: {}", t.toString());
            }
            be.setChanged();
            level.sendBlockUpdated(base, state, level.getBlockState(base), 3);
        }
        level.playSound(null, base.above(3), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 2f, 0.7f);
        level.playSound(null, base.above(3), SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.5f, 0.6f);
        return true;
    }

    //////////////////////////////////////
    // ***** world state: has anyone ascended? *****
    //////////////////////////////////////

    public static final class Data extends SavedData {
        public boolean ascended;
        public String by = "";

        static Data load(CompoundTag t) {
            Data d = new Data();
            d.ascended = t.getBoolean("ascended");
            d.by = t.getString("by");
            return d;
        }

        @Override
        public CompoundTag save(CompoundTag t) {
            t.putBoolean("ascended", ascended);
            t.putString("by", by);
            return t;
        }
    }

    public static Data data(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(Data::load, Data::new, "gtapexadditions_apex_gate");
    }

    public static boolean ascended(MinecraftServer server) {
        return data(server).ascended;
    }

    public static void setAscended(MinecraftServer server, String by) {
        Data d = data(server);
        d.ascended = true;
        d.by = by;
        d.setDirty();
    }

    //////////////////////////////////////
    // ***** sealed gates *****
    //////////////////////////////////////

    /** Gates refuse to dial until the world's first Apex Stargate has been charged (Stargate Journey's Dial event). */
    @SuppressWarnings("unchecked")
    public static void registerEvents() {
        if (!ModList.get().isLoaded("sgjourney")) return;
        try {
            Class<? extends Event> dial = (Class<? extends Event>) Class.forName(
                    "net.povstalec.sgjourney.common.events.custom.StargateEvent$Dial");
            Method getServer = dial.getMethod("getServer"), getStargate = dial.getMethod("getStargate");
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, false, (Class<Event>) dial, (Event e) -> {
                try {
                    MinecraftServer server = (MinecraftServer) getServer.invoke(e);
                    if (server == null || ascended(server) || !e.isCancelable()) return;
                    e.setCanceled(true);
                    Object sg = getStargate.invoke(e);
                    Vec3 pos = (Vec3) sg.getClass().getMethod("getPosition").invoke(sg);
                    ServerLevel level = (ServerLevel) sg.getClass().getMethod("getLevel").invoke(sg);
                    if (level == null || pos == null) return;
                    for (ServerPlayer p : level.players())
                        if (p.position().distanceTo(pos) < 24)
                            p.displayClientMessage(Component.literal("The gate is sealed. Charge it with 9.22E18 EU from its Ascension Gate controller.")
                                    .withStyle(ChatFormatting.LIGHT_PURPLE), true);
                } catch (Throwable ignored) {}
            });
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[GTAA] Stargate Journey found but its dial event changed: {}", t.toString());
        }
    }
}
