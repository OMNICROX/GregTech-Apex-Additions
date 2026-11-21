package com.omnicrox.gtaa.common.compat;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.machine.part.ManaHatchPartMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.ManaSpark;
import vazkii.botania.api.mana.spark.SparkAttachable;

import java.util.List;

/**
 * Botania side of the Mana Hatch, only loaded when Botania is installed. GT machine block entities fall back to
 * Forge's attached capabilities, so the hatch shows up to Botania as a mana receiver (spreader bursts) and a spark
 * attachable (Mana Sparks).
 */
public final class BotaniaCompat {

    private BotaniaCompat() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, BotaniaCompat::attach);
    }

    private static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof MetaMachineBlockEntity be)
            event.addCapability(GTAA.id("mana_hatch"), new Provider(be));
    }

    private static final class Provider implements ICapabilityProvider {
        private final MetaMachineBlockEntity be;
        private LazyOptional<Receiver> opt = LazyOptional.empty();
        private ManaHatchPartMachine bound;

        Provider(MetaMachineBlockEntity be) {
            this.be = be;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap != BotaniaForgeCapabilities.MANA_RECEIVER && cap != BotaniaForgeCapabilities.SPARK_ATTACHABLE)
                return LazyOptional.empty();
            if (!(be.getMetaMachine() instanceof ManaHatchPartMachine hatch)) return LazyOptional.empty();
            if (bound != hatch || !opt.isPresent()) {
                bound = hatch;
                Receiver r = new Receiver(hatch);
                opt = LazyOptional.of(() -> r);
            }
            return opt.cast();
        }
    }

    private record Receiver(ManaHatchPartMachine hatch) implements ManaReceiver, SparkAttachable {
        @Override
        public Level getManaReceiverLevel() {
            return hatch.getLevel();
        }

        @Override
        public BlockPos getManaReceiverPos() {
            return hatch.getPos();
        }

        @Override
        public int getCurrentMana() {
            return (int) Math.min(Integer.MAX_VALUE, hatch.getMana());
        }

        @Override
        public boolean isFull() {
            return hatch.isFull();
        }

        @Override
        public void receiveMana(int mana) {
            hatch.receive(mana);
        }

        @Override
        public boolean canReceiveManaFromBursts() {
            return true;
        }

        @Override
        public boolean canAttachSpark(ItemStack stack) {
            return true;
        }

        @Override
        public int getAvailableSpaceForMana() {
            return (int) Math.min(Integer.MAX_VALUE, hatch.space());
        }

        @Override
        public ManaSpark getAttachedSpark() {
            Level level = hatch.getLevel();
            if (level == null) return null;
            BlockPos up = hatch.getPos().above();
            List<net.minecraft.world.entity.Entity> list = level.getEntitiesOfClass(net.minecraft.world.entity.Entity.class,
                    new AABB(up), e -> e instanceof ManaSpark);
            return list.isEmpty() ? null : (ManaSpark) list.get(0);
        }

        @Override
        public boolean areIncomingTranfersDone() {
            return hatch.isFull();
        }
    }
}
