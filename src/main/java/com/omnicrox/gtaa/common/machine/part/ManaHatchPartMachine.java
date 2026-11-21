package com.omnicrox.gtaa.common.machine.part;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Mana Hatch for the Mana Nexus. Botania sees it as a mana receiver: aim Mana Spreaders at it or put a Spark on it
 * (see BotaniaCompat). It buffers up to one Mana Pool's worth; the Nexus pulls it into its own storage every tick.
 */
public class ManaHatchPartMachine extends MultiblockPartMachine implements com.gregtechceu.gtceu.api.machine.feature.IInteractedMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ManaHatchPartMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);
    public static final long CAPACITY = 1_000_000;

    @Persisted @DescSynced private long mana;

    public ManaHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public long getMana() {
        return mana;
    }

    public boolean isFull() {
        return mana >= CAPACITY;
    }

    public long space() {
        return Math.max(0, CAPACITY - mana);
    }

    /** Botania hands mana in (negative = a spark taking it out) */
    public void receive(long amount) {
        mana = Math.max(0, Math.min(CAPACITY, mana + amount));
    }

    /** the Nexus takes up to max */
    public long extract(long max) {
        long take = Math.min(max, mana);
        mana -= take;
        return take;
    }

    @Override
    public InteractionResult onUse(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
                                   net.minecraft.core.BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide && player.getItemInHand(hand).isEmpty()) {
            player.displayClientMessage(Component.literal("Mana Hatch: " + String.format("%,d", mana) + " / " +
                    String.format("%,d", CAPACITY) + " mana"), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
