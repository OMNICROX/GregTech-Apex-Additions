package com.omnicrox.gtaa.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SpaceElevatorCableBlockEntity extends BlockEntity {

    public static final float MAX_HEIGHT = 250f;
    public static final int STANDBY_TICKS = 200;
    public static final int ANIM_NONE = 0, ANIM_DELIVER = 1, ANIM_FORMATION = 2;

    private boolean shouldRender;
    private long animationStart = -1;
    private int animation = ANIM_NONE;

    public SpaceElevatorCableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public long getAnimationStart() {
        return animationStart;
    }

    public int getAnimation() {
        return animation;
    }

    public void setShouldRender(boolean value) {
        if (value == shouldRender) return;
        shouldRender = value;
        if (value) start(ANIM_FORMATION);
        else sync();
    }

    /** True while a climb animation is still playing. */
    public boolean isAnimating() {
        if (level == null || animation == ANIM_NONE) return false;
        long t = level.getGameTime() - animationStart;
        return t < (animation == ANIM_DELIVER ? 2 * MAX_HEIGHT + STANDBY_TICKS : MAX_HEIGHT);
    }

    public void start(int type) {
        if (level == null) return;
        animation = type;
        animationStart = level.getGameTime();
        sync();
    }

    /** Climber height above its rest point (GTNH: 1 block/tick, 250 blocks, 10 s pause at the top). */
    public float climberHeight(float partialTick) {
        if (level == null || animation == ANIM_NONE) return 0f;
        float t = level.getGameTime() - animationStart + partialTick;
        if (animation == ANIM_FORMATION) return Math.max(0f, MAX_HEIGHT - t);
        if (t < MAX_HEIGHT) return t;
        if (t < MAX_HEIGHT + STANDBY_TICKS) return MAX_HEIGHT;
        return Math.max(0f, 2 * MAX_HEIGHT + STANDBY_TICKS - t);
    }

    /** Climber rotation in degrees (0.5 deg/tick while moving). */
    public float climberRotation(float partialTick) {
        if (level == null || animation == ANIM_NONE) return 0f;
        float t = level.getGameTime() - animationStart + partialTick;
        float moving = animation == ANIM_FORMATION ? Math.min(t, MAX_HEIGHT)
                : Math.min(t, MAX_HEIGHT) + Math.max(0f, Math.min(t - MAX_HEIGHT - STANDBY_TICKS, MAX_HEIGHT));
        return (moving * 0.5f) % 360f;
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("render", shouldRender);
        tag.putLong("anim", animationStart);
        tag.putInt("animType", animation);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        shouldRender = tag.getBoolean("render");
        animationStart = tag.getLong("anim");
        animation = tag.getInt("animType");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-8, 0, -8), worldPosition.offset(9, 520, 9));
    }
}
