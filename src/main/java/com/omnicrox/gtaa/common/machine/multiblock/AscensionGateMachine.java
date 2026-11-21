package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.omnicrox.gtaa.api.GTAAWireless;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.entity.AscendingMaxwellEntity;
import com.omnicrox.gtaa.common.item.MaxwellItem;
import com.omnicrox.gtaa.common.network.GTAANetwork;
import com.omnicrox.gtaa.common.stargate.ApexStargate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Ascension Gate: the end of Apex Ascension. A pedestal (this controller, energy / laser hatches) under the Apex
 * Stargate's frame. Complete the frame and it becomes the Retrogate (Stargate Journey's Pegasus gate in More Gates'
 * Retrogate skin); every gate in the world stays sealed until this one is charged with 9.22 Qn EU (Long.MAX_VALUE,
 * the most a GregTech number can hold). Power comes from the hatches and the owner's wireless networks (GTM Things
 * and gtna's Nexus, where the Artificial Star sends its power), at most ~768 T EU/t so it takes 10 minutes at best.
 * Fully charged, the gate opens one way, just once: Maxwell comes through, the credits roll, and every gate wakes.
 */
public class AscensionGateMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AscensionGateMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final long REQUIRED = Long.MAX_VALUE;
    /** fastest charge: 10 minutes */
    public static final long MAX_PER_TICK = REQUIRED / (20L * 60 * 10);
    public static final int INTRO_TICKS = 1200;

    @Persisted @DescSynced private long charge;
    @Persisted @DescSynced private boolean ascendedHere;
    @Persisted @DescSynced private String ascendedBy = "";
    @Persisted private int intro = -1;
    @Persisted private long[] horizonCells = new long[0];
    @DescSynced private long rate;
    @DescSynced private boolean gateFormed;
    @DescSynced private boolean worldAscended;
    @DescSynced private String status = "";
    private long secondCharge;
    private int celebrateTicks;
    private TickableSubscription tick;
    private ServerBossEvent bar;

    public AscensionGateMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    //////////////////////////////////////
    // ***** structure *****
    //////////////////////////////////////

    /** frame block, or the formed gate's block in its place */
    public static TraceabilityPredicate gatePart(java.util.function.Supplier<Block> frame, ResourceLocation formed) {
        return new TraceabilityPredicate(s -> {
            Block b = s.getBlockState().getBlock();
            return b == frame.get() || BuiltInRegistries.BLOCK.getKey(b).equals(formed);
        }, () -> new BlockInfo[] { BlockInfo.fromBlockState(frame.get().defaultBlockState()) });
    }

    private BlockPos base() {
        return getPos().above();
    }

    private Direction facing() {
        return getFrontFacing().getAxis().isHorizontal() ? getFrontFacing() : Direction.NORTH;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) tick = subscribeServerTick(tick, this::gateTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
        if (bar != null) bar.removeAllPlayers();
    }

    //////////////////////////////////////
    // ***** charging *****
    //////////////////////////////////////

    private void gateTick() {
        if (!(getLevel() instanceof ServerLevel level)) return;
        if (celebrateTicks > 0) celebrate(level);
        if (intro >= 0) {
            introTick(level);
            updateBar(level);
            return;
        }
        worldAscended = ApexStargate.ascended(level.getServer());
        gateFormed = ApexStargate.isGate(level, base());
        if (!gateFormed) {
            if (getOffsetTimer() % 20 == 0 && ApexStargate.frameComplete(level, base(), facing())) {
                if (ApexStargate.form(level, base(), facing(), null)) gateFormed = true;
                else status = "Stargate Journey isn't installed";
            } else status = "Complete the frame above the pedestal";
            updateBar(level);
            return;
        }
        if (worldAscended && !ascendedHere) {
            status = "This world has already ascended - every gate is awake";
            updateBar(level);
            return;
        }
        if (ascendedHere) {
            status = "";
            updateBar(level);
            return;
        }
        if (charge >= REQUIRED) {
            startIntro(level);
            updateBar(level);
            return;
        }
        long need = Math.min(MAX_PER_TICK, REQUIRED - charge);
        long got = 0;
        if (need > 0 && energyContainer != null) {
            long take = Math.min(need, energyContainer.getEnergyStored());
            if (take > 0) got += energyContainer.removeEnergy(take);
        }
        UUID owner = getOwnerUUID();
        if (got < need && owner != null) got += GTAAWireless.drawNexusEU(level, owner, need - got);
        if (got < need && owner != null) got += GTAAWireless.drawEU(owner, need - got);
        charge = charge > REQUIRED - got ? REQUIRED : charge + got;
        secondCharge += got;
        if (getOffsetTimer() % 20 == 0) {
            rate = secondCharge / 20;
            secondCharge = 0;
        }
        status = got > 0 ? "" : "Waiting for power (hatches, or the owner's wireless network)";
        if (charge >= REQUIRED) startIntro(level);
        updateBar(level);
    }

    private void updateBar(ServerLevel level) {
        boolean show = gateFormed && !ascendedHere && !worldAscended || intro >= 0;
        if (bar == null) {
            bar = new ServerBossEvent(Component.literal("Ascension"), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);
        }
        if (getOffsetTimer() % 10 != 0) return;
        if (!show) {
            bar.removeAllPlayers();
            return;
        }
        float frac = intro >= 0 ? 1f : (float) ((double) charge / REQUIRED);
        bar.setProgress(Math.max(0, Math.min(1, frac)));
        bar.setName(Component.literal(intro >= 0 ? "✦ The gate is open ✦" :
                String.format("Ascension charge: %.3f%%", frac * 100)).withStyle(ChatFormatting.LIGHT_PURPLE));
        Vec3 c = ApexStargate.center(base());
        Set<ServerPlayer> near = new HashSet<>();
        for (ServerPlayer p : level.players()) if (p.position().distanceTo(c) < 48) near.add(p);
        for (ServerPlayer p : new ArrayList<>(bar.getPlayers())) if (!near.contains(p)) bar.removePlayer(p);
        for (ServerPlayer p : near) bar.addPlayer(p);
    }

    //////////////////////////////////////
    // ***** the one-way opening *****
    //////////////////////////////////////

    private String ownerName(ServerLevel level) {
        UUID owner = getOwnerUUID();
        ServerPlayer p = owner == null ? null : level.getServer().getPlayerList().getPlayer(owner);
        if (p != null) return p.getGameProfile().getName();
        var near = level.getNearestPlayer(getPos().getX(), getPos().getY(), getPos().getZ(), 64, false);
        return near != null ? near.getGameProfile().getName() : "Someone";
    }

    private void startIntro(ServerLevel level) {
        intro = 0;
        ascendedHere = true;
        ascendedBy = ownerName(level);
        ApexStargate.setAscended(level.getServer(), ascendedBy);
    }

    /** the Maxwell open: horizon, Maxwell through the gate, credits, then it closes after a minute */
    private void introTick(ServerLevel level) {
        BlockPos base = base();
        Vec3 c = ApexStargate.center(base);
        if (intro == 0) {
            List<Long> cells = new ArrayList<>();
            for (BlockPos p : ApexStargate.interior(base, facing())) {
                if (level.getBlockState(p).isAir()) {
                    level.setBlock(p, GTAABlocks.EVENT_HORIZON.get().defaultBlockState(), 3);
                    cells.add(p.asLong());
                }
            }
            horizonCells = cells.stream().mapToLong(Long::longValue).toArray();
            level.playSound(null, base.above(3), SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 3f, 0.8f);
            level.playSound(null, base.above(3), SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 3f, 0.6f);
            level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("✦ " + ascendedBy +
                    " has opened the Apex Stargate. ✦").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD), false);
        }
        if (intro < 40 && intro % 4 == 0)
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, c.x, c.y, c.z, 60, 1.5, 1.5, 1.5, 0.3);
        if (intro == 40) {
            Direction f = facing();
            Vec3 start = c.add(f.getStepX() * 0.3, 0, f.getStepZ() * 0.3);
            level.addFreshEntity(AscendingMaxwellEntity.create(level, start, f));
            level.playSound(null, base.above(3), SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.6f, 1.6f);
        }
        if (intro == 120) {
            celebrateTicks = 200;
            level.playSound(null, base.above(3), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 3f, 1f);
            for (ServerPlayer p : level.players()) {
                if (p.position().distanceTo(c) > 64) continue;
                GTAANetwork.send(p, new GTAANetwork.AscensionPacket(true, MaxwellItem.stats(p)));
                if (p.getGameProfile().getName().equals(ascendedBy)) {
                    ItemStack prize = new ItemStack(GTAAItems.MAXWELL.get());
                    if (!p.getInventory().add(prize)) p.drop(prize, false);
                }
            }
        }
        if (++intro >= INTRO_TICKS) {
            for (long l : horizonCells) {
                BlockPos p = BlockPos.of(l);
                if (level.getBlockState(p).is(GTAABlocks.EVENT_HORIZON.get())) level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
            horizonCells = new long[0];
            intro = -1;
            level.playSound(null, base.above(3), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 3f, 0.7f);
        }
    }

    /** ten seconds of fireworks around the gate */
    private void celebrate(ServerLevel level) {
        celebrateTicks--;
        if (celebrateTicks % 8 != 0) return;
        List<BlockPos> ring = new ArrayList<>();
        for (ApexStargate.Part p : ApexStargate.PARTS) ring.add(ApexStargate.partPos(base(), facing(), p));
        BlockPos p = ring.get(level.random.nextInt(ring.size()));
        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fw = new CompoundTag();
        CompoundTag star = new CompoundTag();
        star.putByte("Type", (byte) level.random.nextInt(5));
        star.putIntArray("Colors", new int[] { 0x7A2CFF, 0x00C8FF, 0xFF6E14, 0xC77DFF });
        star.putBoolean("Twinkle", true);
        ListTag ex = new ListTag();
        ex.add(star);
        fw.put(FireworkRocketItem.TAG_EXPLOSIONS, ex);
        fw.putByte("Flight", (byte) 1);
        rocket.getOrCreateTag().put(FireworkRocketItem.TAG_FIREWORKS, fw);
        level.addFreshEntity(new FireworkRocketEntity(level, p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5, rocket));
    }

    /** for tests: fill the charge */
    public void chargeForTest() {
        charge = REQUIRED;
    }

    public long getCharge() {
        return charge;
    }

    public boolean isIntroRunning() {
        return intro >= 0;
    }

    //////////////////////////////////////
    // ***** display *****
    //////////////////////////////////////

    private static String big(long v) {
        if (v < 1_000_000_000_000L) return FormattingUtil.formatNumbers(v);
        String[] units = { "T", "Qa", "Qn" };
        double d = v / 1e12;
        int u = 0;
        while (d >= 1000 && u < units.length - 1) {
            d /= 1000;
            u++;
        }
        return String.format("%.2f %s", d, units[u]);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        if (ascendedHere) {
            textList.add(Component.literal("✦ " + ascendedBy + " ascended here ✦").withStyle(ChatFormatting.LIGHT_PURPLE));
            textList.add(Component.literal("The Retrogate is awake: dial anywhere").withStyle(ChatFormatting.GRAY));
            return;
        }
        if (!gateFormed) {
            textList.add(Component.literal("Build the Apex Stargate frame on the pedestal:").withStyle(ChatFormatting.GOLD));
            textList.add(Component.literal(" Ascension Gate Base in the middle, 9 Chevrons, 14 Rings").withStyle(ChatFormatting.WHITE));
        } else if (!worldAscended) {
            double frac = (double) charge / REQUIRED;
            textList.add(Component.literal(String.format("Ascension charge: %.4f%%", frac * 100)).withStyle(ChatFormatting.LIGHT_PURPLE));
            textList.add(Component.literal(big(charge) + " / 9.22 Qn EU").withStyle(ChatFormatting.AQUA));
            if (rate > 0) {
                long secs = (REQUIRED - charge) / Math.max(1, rate * 20);
                textList.add(Component.literal("Charging at " + big(rate) + " EU/t, " + (secs / 60) + " min " + (secs % 60) + " s left")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
        if (!status.isEmpty()) textList.add(Component.literal(status).withStyle(ChatFormatting.YELLOW));
    }
}
