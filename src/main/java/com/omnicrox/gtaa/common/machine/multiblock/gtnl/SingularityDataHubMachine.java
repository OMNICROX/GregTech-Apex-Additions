package com.omnicrox.gtaa.common.machine.multiblock.gtnl;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IDropSaveMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.util.ApexFormat;
import com.omnicrox.apexlib.compat.ApexWireless;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * Singularity Data Hub (GT-Not-Leisure): a vault with no limit on types or amounts. While it runs (1/20 of UHV, from
 * energy hatches, or from the wireless network when there are none) it pulls everything from its input buses and
 * hatches, and it is mounted as storage on the ME network its controller is connected to. Broken, it keeps its
 * contents (saved next to the world, like GT-Not-Leisure does).
 *
 * This file is subject to the additional terms in LICENSE-ADDITIONAL-TERMS.md (GT-Not-Leisure).
 */
public class SingularityDataHubMachine extends WorkableElectricMultiblockMachine
        implements IGridConnectedMachine, IStorageProvider, IDropSaveMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SingularityDataHubMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final long EUT = GTValues.VA[GTValues.UHV] / 20;

    @Persisted
    protected final GridNodeHolder nodeHolder = new GridNodeHolder(this);
    @DescSynced protected boolean isOnline;
    @DescSynced private boolean running;
    @DescSynced private boolean wireless;
    @DescSynced private long itemTypes, fluidTypes, itemTotal, fluidTotal;
    @Persisted private UUID vaultId;

    private final KeyCounter store = new KeyCounter();
    private final Vault vault = new Vault();
    private TickableSubscription tick;

    public SingularityDataHubMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        getMainNode().addService(IStorageProvider.class, this);
        getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    // ---------------- ME ----------------
    @Override
    public IManagedGridNode getMainNode() {
        return nodeHolder.getMainNode();
    }

    @Override
    public boolean isOnline() {
        return isOnline;
    }

    @Override
    public void setOnline(boolean online) {
        this.isOnline = online;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        IGridConnectedMachine.super.onMainNodeStateChanged(reason);
        IStorageProvider.requestUpdate(getMainNode());
    }

    @Override
    public void mountInventories(IStorageMounts mounts) {
        mounts.mount(vault, 0);
    }

    /** the stored contents as ME storage; locked while the hub is not running */
    private final class Vault implements MEStorage {

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (!running || amount <= 0 || !(what instanceof AEItemKey || what instanceof AEFluidKey)) return 0;
            if (mode == Actionable.MODULATE) add(what, amount);
            return amount;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (!running || amount <= 0) return 0;
            long have = store.get(what);
            long got = Math.min(have, amount);
            if (got > 0 && mode == Actionable.MODULATE) {
                store.remove(what, got);
                if (store.get(what) <= 0) store.removeZeros();
                changed();
            }
            return got;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            if (running) out.addAll(store);
        }

        @Override
        public Component getDescription() {
            return getDefinition().getItem().getDescription();
        }
    }

    private void add(AEKey key, long amount) {
        long have = store.get(key);
        store.set(key, have > Long.MAX_VALUE - amount ? Long.MAX_VALUE : have + amount);
        changed();
    }

    private void changed() {
        markDirty();
        long it = 0, ft = 0, ia = 0, fa = 0;
        for (var e : store) {
            if (e.getKey() instanceof AEItemKey) {
                it++;
                ia = sat(ia, e.getLongValue());
            } else {
                ft++;
                fa = sat(fa, e.getLongValue());
            }
        }
        itemTypes = it;
        fluidTypes = ft;
        itemTotal = ia;
        fluidTotal = fa;
    }

    private static long sat(long a, long b) {
        return a > Long.MAX_VALUE - b ? Long.MAX_VALUE : a + b;
    }

    // ---------------- running ----------------
    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) tick = subscribeServerTick(tick, this::work);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        setRunning(false);
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    private void setRunning(boolean r) {
        if (running == r) return;
        running = r;
        IStorageProvider.requestUpdate(getMainNode());
    }

    private IEnergyContainer energy() {
        List<IEnergyContainer> list = new java.util.ArrayList<>();
        for (IMultiPart part : getParts())
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(EURecipeCapability.CAP))
                    if (h instanceof IEnergyContainer c) list.add(c);
            }
        return new EnergyContainerList(list);
    }

    private void work() {
        if (!isWorkingEnabled()) {
            setRunning(false);
            return;
        }
        if (getOffsetTimer() % 20 != 0) {
            // power every tick
            if (running && !pay()) setRunning(false);
            return;
        }
        boolean paid = pay();
        setRunning(paid);
        if (!paid) return;
        for (NotifiableItemStackHandler bus : ApexHatches.itemHandlers(this, IO.IN))
            for (int i = 0; i < bus.getSlots(); i++) {
                ItemStack s = bus.getStackInSlot(i);
                if (s.isEmpty()) continue;
                ItemStack got = bus.extractItemInternal(i, s.getCount(), false);
                if (!got.isEmpty()) add(AEItemKey.of(got), got.getCount());
            }
        for (NotifiableFluidTank tank : ApexHatches.fluidTanks(this, IO.IN))
            for (int i = 0; i < tank.getTanks(); i++) {
                FluidStack f = tank.getFluidInTank(i);
                if (f.isEmpty()) continue;
                FluidStack got = tank.drainInternal(f.copy(), IFluidHandler.FluidAction.EXECUTE);
                if (!got.isEmpty()) add(AEFluidKey.of(got), got.getAmount());
            }
    }

    private boolean pay() {
        IEnergyContainer e = energy();
        wireless = e.getEnergyCapacity() <= 0;
        if (wireless) return ApexWireless.drawEU(getOwnerUUID(), EUT) >= EUT;
        if (e.getEnergyStored() < EUT) return false;
        e.removeEnergy(EUT);
        return true;
    }

    // ---------------- saving ----------------
    private static final String KEY = "Vault";

    private ListTag writeStore() {
        ListTag list = new ListTag();
        for (var e : store) {
            CompoundTag t = e.getKey().toTagGeneric();
            t.putLong("#", e.getLongValue());
            list.add(t);
        }
        return list;
    }

    private void readStore(ListTag list) {
        store.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag t = list.getCompound(i);
            AEKey k = AEKey.fromTagGeneric(t);
            long n = t.getLong("#");
            if (k != null && n > 0) store.add(k, n);
        }
        changed();
    }

    @Override
    public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
        if (!forDrop) tag.put(KEY, writeStore());
    }

    @Override
    public void loadCustomPersistedData(@NotNull CompoundTag tag) {
        super.loadCustomPersistedData(tag);
        if (tag.contains(KEY, Tag.TAG_LIST)) readStore(tag.getList(KEY, Tag.TAG_COMPOUND));
    }

    private static File vaultFile(UUID id) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;
        File dir = server.getWorldPath(LevelResource.ROOT).resolve("data").toFile();
        if (!dir.exists() && !dir.mkdirs()) return null;
        return new File(dir, "gtaa_singularity_vault_" + id + ".dat");
    }

    /** the contents go to a file next to the world and the controller item only carries its id (GT-Not-Leisure) */
    @Override
    public void saveToItem(CompoundTag tag) {
        if (store.isEmpty()) return;
        UUID id = UUID.randomUUID();
        File f = vaultFile(id);
        if (f == null) return;
        CompoundTag root = new CompoundTag();
        root.put(KEY, writeStore());
        try {
            NbtIo.writeCompressed(root, f);
            tag.putUUID("VaultId", id);
            tag.putLong("VaultItems", itemTypes);
            tag.putLong("VaultFluids", fluidTypes);
        } catch (Exception ignored) {}
    }

    @Override
    public void loadFromItem(CompoundTag tag) {
        if (!tag.hasUUID("VaultId")) return;
        File f = vaultFile(tag.getUUID("VaultId"));
        if (f == null || !f.exists()) return;
        try {
            CompoundTag root = NbtIo.readCompressed(f);
            readStore(root.getList(KEY, Tag.TAG_COMPOUND));
            if (!f.delete()) f.deleteOnExit();
        } catch (Exception ignored) {}
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal(running ? "Online: mounted as ME storage" : "Offline: needs " +
                ApexFormat.num(EUT) + " EU/t" + (wireless ? " from the wireless network" : ""))
                .withStyle(running ? ChatFormatting.GREEN : ChatFormatting.RED));
        if (!isOnline) textList.add(Component.literal("Connect the controller to an ME network").withStyle(ChatFormatting.YELLOW));
        textList.add(Component.literal("Items: " + ApexFormat.num(itemTypes) + " types, " + ApexFormat.num(itemTotal))
                .withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("Fluids: " + ApexFormat.num(fluidTypes) + " types, " + ApexFormat.num(fluidTotal) + " mB")
                .withStyle(ChatFormatting.AQUA));
    }
}
