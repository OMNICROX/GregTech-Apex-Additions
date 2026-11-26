package com.omnicrox.gtaa.common.stargate;

import com.omnicrox.gtaa.api.GTAAFormat;
import com.omnicrox.gtaa.api.GTAAWireless;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The ascension charge, without a controller: the first Apex Stargate formed in a world (frame + Gate Base) charges
 * itself with 9.22E18 EU from the player who formed it - their gtna Nexus (where the Artificial Star sends its power)
 * and their GTM Things wireless network. At most 1/12000 of the total per tick, so 10 minutes at best. Full, the gate
 * opens one way, just once: event horizon, fireworks and credits, and every gate in the world wakes up.
 * The charge waits while the gate's chunk is unloaded, and is kept if the gate is broken and rebuilt in the same spot.
 */
public final class ApexGateCharge extends SavedData {

    public static final long REQUIRED = Long.MAX_VALUE;
    public static final long MAX_PER_TICK = REQUIRED / (20L * 60 * 10);
    public static final int INTRO_TICKS = 1200;
    private static final String ID = "gtapexadditions_apex_gate_charge";

    private String dim = "";
    private long pos;
    private int facing = Direction.NORTH.get3DDataValue();
    private UUID owner;
    private String ownerName = "";
    private long charge;
    private int intro = -1;
    private long[] horizon = new long[0];

    private transient long secondCharge, rate;
    private transient int celebrate;
    private static ServerBossEvent bar;

    //////////////////////////////////////
    // ***** saving *****
    //////////////////////////////////////

    static ApexGateCharge load(CompoundTag t) {
        ApexGateCharge d = new ApexGateCharge();
        d.dim = t.getString("dim");
        d.pos = t.getLong("pos");
        d.facing = t.getInt("facing");
        if (t.hasUUID("owner")) d.owner = t.getUUID("owner");
        d.ownerName = t.getString("ownerName");
        d.charge = t.getLong("charge");
        d.intro = t.contains("intro") ? t.getInt("intro") : -1;
        d.horizon = t.getLongArray("horizon");
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag t) {
        t.putString("dim", dim);
        t.putLong("pos", pos);
        t.putInt("facing", facing);
        if (owner != null) t.putUUID("owner", owner);
        t.putString("ownerName", ownerName);
        t.putLong("charge", charge);
        t.putInt("intro", intro);
        t.putLongArray("horizon", horizon);
        return t;
    }

    public static ApexGateCharge get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(ApexGateCharge::load, ApexGateCharge::new, ID);
    }

    //////////////////////////////////////
    // ***** the gate that charges *****
    //////////////////////////////////////

    /** called when a frame becomes a gate: the world's first gate (or the same spot again) starts charging */
    public static void onFormed(ServerLevel level, BlockPos base, Direction facing, Player player) {
        MinecraftServer server = level.getServer();
        if (ApexStargate.ascended(server)) return;
        ApexGateCharge d = get(server);
        String here = level.dimension().location().toString();
        boolean same = d.dim.equals(here) && d.pos == base.asLong();
        if (!d.dim.isEmpty() && !same && d.stillAGate(server)) {
            if (player != null) player.displayClientMessage(Component.literal(
                    "Another Apex Stargate is already charging - this one wakes up when that one ascends")
                    .withStyle(ChatFormatting.YELLOW), false);
            return;
        }
        if (!same) d.charge = 0;
        d.dim = here;
        d.pos = base.asLong();
        d.facing = facing.get3DDataValue();
        if (player != null) {
            d.owner = player.getUUID();
            d.ownerName = player.getGameProfile().getName();
            player.displayClientMessage(Component.literal("The Apex Stargate drinks from your wireless network: " +
                    "9.22E18 EU to open it").withStyle(ChatFormatting.LIGHT_PURPLE), false);
        }
        d.setDirty();
    }

    private ServerLevel level(MinecraftServer server) {
        if (dim.isEmpty()) return null;
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dim)));
    }

    private boolean stillAGate(MinecraftServer server) {
        ServerLevel l = level(server);
        BlockPos b = BlockPos.of(pos);
        return l != null && (!l.isLoaded(b) || ApexStargate.isGate(l, b));
    }

    public static void registerEvents() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) get(e.getServer()).tick(e.getServer());
        });
    }

    private void tick(MinecraftServer server) {
        if (dim.isEmpty()) return;
        ServerLevel level = level(server);
        BlockPos base = BlockPos.of(pos);
        if (level == null || !level.isLoaded(base)) return;
        if (celebrate > 0) celebrate(level, base);
        if (intro >= 0) {
            introTick(level, base);
            bar(level, base, server);
            return;
        }
        if (ApexStargate.ascended(server) || !ApexStargate.isGate(level, base)) {
            hideBar();
            return;
        }
        long need = Math.min(MAX_PER_TICK, REQUIRED - charge);
        long got = 0;
        if (need > 0 && owner != null) {
            got += GTAAWireless.drawNexusEU(level, owner, need);
            if (got < need) got += GTAAWireless.drawEU(owner, need - got);
        }
        if (got > 0) {
            charge = charge > REQUIRED - got ? REQUIRED : charge + got;
            setDirty();
        }
        secondCharge += got;
        if (server.getTickCount() % 20 == 0) {
            rate = secondCharge / 20;
            secondCharge = 0;
        }
        if (charge >= REQUIRED) startIntro(level, base, server);
        bar(level, base, server);
    }

    //////////////////////////////////////
    // ***** boss bar *****
    //////////////////////////////////////

    private void bar(ServerLevel level, BlockPos base, MinecraftServer server) {
        if (server.getTickCount() % 10 != 0) return;
        if (bar == null) bar = new ServerBossEvent(Component.literal("Ascension"), BossEvent.BossBarColor.BLUE,
                BossEvent.BossBarOverlay.NOTCHED_10);
        float frac = intro >= 0 ? 1f : (float) ((double) charge / REQUIRED);
        bar.setProgress(Math.max(0, Math.min(1, frac)));
        String text = intro >= 0 ? "✦ The gate is open ✦" : rate > 0
                ? String.format("Ascension charge: %.3f%%  (%s EU/t)", frac * 100, GTAAFormat.num(rate))
                : String.format("Ascension charge: %.3f%%  - waiting for %s's wireless power", frac * 100, ownerName);
        bar.setName(Component.literal(text).withStyle(ChatFormatting.AQUA));
        Vec3 c = ApexStargate.center(base);
        Set<ServerPlayer> near = new HashSet<>();
        for (ServerPlayer p : level.players()) if (p.position().distanceTo(c) < 48) near.add(p);
        for (ServerPlayer p : new ArrayList<>(bar.getPlayers())) if (!near.contains(p)) bar.removePlayer(p);
        for (ServerPlayer p : near) bar.addPlayer(p);
    }

    private static void hideBar() {
        if (bar != null && !bar.getPlayers().isEmpty()) bar.removeAllPlayers();
    }

    //////////////////////////////////////
    // ***** the one-way opening *****
    //////////////////////////////////////

    private void startIntro(ServerLevel level, BlockPos base, MinecraftServer server) {
        intro = 0;
        ApexStargate.setAscended(server, ownerName.isEmpty() ? "Someone" : ownerName);
        setDirty();
    }

    private void introTick(ServerLevel level, BlockPos base) {
        Direction f = Direction.from3DDataValue(facing);
        Vec3 c = ApexStargate.center(base);
        if (intro == 0) {
            List<Long> cells = new ArrayList<>();
            for (BlockPos p : ApexStargate.interior(base, f)) {
                if (level.getBlockState(p).isAir()) {
                    level.setBlock(p, GTAABlocks.EVENT_HORIZON.get().defaultBlockState(), 3);
                    cells.add(p.asLong());
                }
            }
            horizon = cells.stream().mapToLong(Long::longValue).toArray();
            level.playSound(null, base.above(3), SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 3f, 0.8f);
            level.playSound(null, base.above(3), SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 3f, 0.6f);
            level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("✦ " + ownerName +
                    " has opened the Apex Stargate. Every gate is awake. ✦").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        }
        if (intro < 40 && intro % 4 == 0)
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, c.x, c.y, c.z, 60, 1.5, 1.5, 1.5, 0.3);
        if (intro == 40) level.playSound(null, base.above(3), SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.6f, 1.6f);
        if (intro == 120) {
            celebrate = 200;
            level.playSound(null, base.above(3), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 3f, 1f);
            for (ServerPlayer p : level.players())
                if (p.position().distanceTo(c) <= 64) GTAANetwork.send(p, new GTAANetwork.AscensionPacket(true, com.omnicrox.gtaa.common.stargate.AscensionCredits.stats(p)));
        }
        if (++intro >= INTRO_TICKS) {
            for (long l : horizon) {
                BlockPos p = BlockPos.of(l);
                if (level.getBlockState(p).is(GTAABlocks.EVENT_HORIZON.get())) level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
            horizon = new long[0];
            intro = -1;
            dim = ""; // done: nothing charges any more
            hideBar();
            level.playSound(null, base.above(3), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 3f, 0.7f);
        }
        setDirty();
    }

    /** ten seconds of fireworks around the gate */
    private void celebrate(Level level, BlockPos base) {
        celebrate--;
        if (celebrate % 8 != 0) return;
        Direction f = Direction.from3DDataValue(facing);
        List<BlockPos> ring = new ArrayList<>();
        for (ApexStargate.Part p : ApexStargate.PARTS) ring.add(ApexStargate.partPos(base, f, p));
        BlockPos p = ring.get(level.random.nextInt(ring.size()));
        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fw = new CompoundTag();
        CompoundTag star = new CompoundTag();
        star.putByte("Type", (byte) level.random.nextInt(5));
        star.putIntArray("Colors", new int[] { 0x2E6FA0, 0x8FD8FF, 0xFFFFFF, 0x4A5058 });
        star.putBoolean("Twinkle", true);
        ListTag ex = new ListTag();
        ex.add(star);
        fw.put(FireworkRocketItem.TAG_EXPLOSIONS, ex);
        fw.putByte("Flight", (byte) 1);
        rocket.getOrCreateTag().put(FireworkRocketItem.TAG_FIREWORKS, fw);
        level.addFreshEntity(new FireworkRocketEntity(level, p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5, rocket));
    }

    /** right-click on the Gate Base before it's formed, or status text */
    public static Component status(MinecraftServer server) {
        ApexGateCharge d = get(server);
        if (ApexStargate.ascended(server)) return Component.literal("This world has ascended - every gate is awake").withStyle(ChatFormatting.AQUA);
        if (d.dim.isEmpty()) return Component.literal("No Apex Stargate is charging yet").withStyle(ChatFormatting.GRAY);
        return Component.literal(String.format("Ascension charge: %.4f%% (%s / 9.22E18 EU)",
                (double) d.charge / REQUIRED * 100, GTAAFormat.num(d.charge))).withStyle(ChatFormatting.AQUA);
    }
}
