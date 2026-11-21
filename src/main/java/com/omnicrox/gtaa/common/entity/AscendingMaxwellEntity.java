package com.omnicrox.gtaa.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/**
 * Maxwell, coming through the gate at the end of Apex Ascension: he spirals out of the event horizon, spinning,
 * flies a few blocks out, falls, and lands as a Placeable Maxwell block (or the Maxwell item without that mod).
 * The client draws him with Placeable Maxwell's model (AscendingMaxwellRenderer).
 */
public class AscendingMaxwellEntity extends Entity {

    public static final int FLIGHT_TICKS = 70;
    private static final EntityDataAccessor<Integer> FACING = SynchedEntityData.defineId(AscendingMaxwellEntity.class,
            EntityDataSerializers.INT);
    public static final ResourceLocation MAXWELL_BLOCK = new ResourceLocation("placeablemaxwell", "maxwell");

    private Vec3 origin = Vec3.ZERO;
    private int age;

    public AscendingMaxwellEntity(EntityType<? extends AscendingMaxwellEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public static AscendingMaxwellEntity create(Level level, Vec3 center, Direction facing) {
        AscendingMaxwellEntity e = new AscendingMaxwellEntity(GTAAEntities.ASCENDING_MAXWELL.get(), level);
        e.origin = center;
        e.entityData.set(FACING, facing.get2DDataValue());
        e.setPos(center.x, center.y - 0.3, center.z);
        return e;
    }

    public Direction facing() {
        return Direction.from2DDataValue(entityData.get(FACING));
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FACING, 0);
    }

    @Override
    public void tick() {
        super.tick();
        age++;
        if (level().isClientSide) {
            if (age < FLIGHT_TICKS)
                for (int i = 0; i < 3; i++)
                    level().addParticle(i == 0 ? ParticleTypes.END_ROD : ParticleTypes.WITCH, getX() + (random.nextDouble() - 0.5) * 0.6,
                            getY() + random.nextDouble() * 0.5, getZ() + (random.nextDouble() - 0.5) * 0.6, 0, 0.02, 0);
            return;
        }
        Direction f = facing();
        if (age <= FLIGHT_TICKS) {
            // spiral out of the horizon: 7 blocks forward, the spiral shrinking as he comes through
            double t = age / (double) FLIGHT_TICKS;
            double out = 7 * (1 - Math.pow(1 - t, 2));
            double r = 1.4 * (1 - t), a = age * 0.45;
            Direction side = f.getClockWise();
            double sx = Math.cos(a) * r, sy = Math.sin(a) * r;
            setPos(origin.x + f.getStepX() * out + side.getStepX() * sx, origin.y - 0.3 + sy - t * 0.8,
                    origin.z + f.getStepZ() * out + side.getStepZ() * sx);
            if (age == 1) level().playSound(null, blockPosition(), SoundEvents.CAT_AMBIENT, SoundSource.NEUTRAL, 2f, 1f);
            if (age % 14 == 0) level().playSound(null, blockPosition(), SoundEvents.CAT_PURREOW, SoundSource.NEUTRAL, 1f, 1.2f);
            return;
        }
        // fall and land
        noPhysics = false;
        setDeltaMovement(getDeltaMovement().add(0, -0.06, 0).scale(0.98));
        move(MoverType.SELF, getDeltaMovement());
        if (onGround() || age > FLIGHT_TICKS + 200) land();
    }

    private void land() {
        if (!(level() instanceof ServerLevel level)) return;
        BlockPos at = blockPosition();
        Block block = BuiltInRegistries.BLOCK.containsKey(MAXWELL_BLOCK) ? BuiltInRegistries.BLOCK.get(MAXWELL_BLOCK) : null;
        boolean placed = false;
        if (block != null && level.getBlockState(at).canBeReplaced()) {
            BlockState s = block.defaultBlockState();
            if (s.hasProperty(HorizontalDirectionalBlock.FACING)) s = s.setValue(HorizontalDirectionalBlock.FACING, facing().getOpposite());
            placed = level.setBlock(at, s, 3);
        }
        if (!placed) {
            ItemStack drop = block != null ? new ItemStack(block) : new ItemStack(com.omnicrox.gtaa.common.data.GTAAItems.MAXWELL.get());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), drop));
        }
        level.sendParticles(ParticleTypes.HEART, getX(), getY() + 0.5, getZ(), 10, 0.4, 0.3, 0.4, 0);
        level.playSound(null, at, SoundEvents.CAT_AMBIENT, SoundSource.NEUTRAL, 1.5f, 0.9f);
        discard();
    }

    public int age() {
        return age;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag t) {
        origin = new Vec3(t.getDouble("ox"), t.getDouble("oy"), t.getDouble("oz"));
        age = t.getInt("age");
        entityData.set(FACING, t.getInt("facing"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag t) {
        t.putDouble("ox", origin.x);
        t.putDouble("oy", origin.y);
        t.putDouble("oz", origin.z);
        t.putInt("age", age);
        t.putInt("facing", entityData.get(FACING));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
