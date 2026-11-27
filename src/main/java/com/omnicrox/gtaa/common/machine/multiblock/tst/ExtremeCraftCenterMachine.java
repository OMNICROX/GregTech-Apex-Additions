package com.omnicrox.gtaa.common.machine.multiblock.tst;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StorageHelper;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.crafting.pattern.AESmithingTablePattern;
import appeng.crafting.pattern.AEStonecuttingPattern;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IMachineModifyDrops;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Extreme Craft Center (Twist Space Technology "TST_MegaCraftingCenter"). An ME crafting provider for crafting-table,
 * smithing and stonecutting patterns: no power, no assemblers, every push finishes within a second. Patterns go into the
 * input buses and are pulled inside; each pattern runs {@code magnification} crafts per push. Results go straight into
 * the ME network.
 */
public class ExtremeCraftCenterMachine extends WorkableElectricMultiblockMachine
        implements IGridConnectedMachine, ICraftingProvider, IMachineModifyDrops {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ExtremeCraftCenterMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int PATTERN_SLOTS = 1024;
    public static final int CYCLE_TICKS = 20;
    public static final int[] MAGNIFICATIONS = { 1, 4, 16, 64, 256, 1024, 4096, 16384, 65536, 262144, 1048576 };

    @Persisted
    protected final GridNodeHolder nodeHolder = new GridNodeHolder(this);
    @Persisted
    protected final CustomItemStackHandler patterns = new CustomItemStackHandler(PATTERN_SLOTS);
    @Persisted @DescSynced
    protected int magnification = 64;
    @DescSynced
    protected boolean isOnline;
    @DescSynced
    protected int patternCount;
    @DescSynced
    protected int waitingStacks;

    protected final IActionSource actionSource = IActionSource.ofMachine(() -> nodeHolder.getMainNode().getNode());
    private final List<IPatternDetails> details = new ArrayList<>();
    private final KeyCounter pending = new KeyCounter();
    private TickableSubscription tickSub;

    public ExtremeCraftCenterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        getMainNode().addService(ICraftingProvider.class, this);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    // ---------------- ME node ----------------

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
        ICraftingProvider.requestUpdate(getMainNode());
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        getMainNode().setExposedOnSides(EnumSet.of(newFacing));
    }

    // ---------------- lifecycle ----------------

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) rebuildPatterns();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) {
            tickSub = subscribeServerTick(tickSub, this::tick);
            ICraftingProvider.requestUpdate(getMainNode());
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tickSub != null) {
            tickSub.unsubscribe();
            tickSub = null;
        }
        if (!isRemote()) ICraftingProvider.requestUpdate(getMainNode());
    }

    @Override
    public void onDrops(List<ItemStack> drops) {
        for (int i = 0; i < patterns.getSlots(); i++) {
            ItemStack s = patterns.getStackInSlot(i);
            if (!s.isEmpty()) drops.add(s.copy());
            patterns.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    private void tick() {
        if (getOffsetTimer() % CYCLE_TICKS != 0) return;
        absorbPatterns();
        flushOutputs();
        waitingStacks = pending.size();
    }

    // ---------------- patterns ----------------

    public static boolean accepted(IPatternDetails d) {
        return d instanceof AECraftingPattern || d instanceof AESmithingTablePattern ||
                d instanceof AEStonecuttingPattern;
    }

    protected void rebuildPatterns() {
        details.clear();
        if (getLevel() == null) return;
        for (int i = 0; i < patterns.getSlots(); i++) {
            ItemStack s = patterns.getStackInSlot(i);
            if (s.isEmpty()) continue;
            IPatternDetails d = PatternDetailsHelper.decodePattern(s, getLevel());
            if (d != null && accepted(d)) details.add(new ScaledPattern(d, magnification));
        }
        patternCount = details.size();
        ICraftingProvider.requestUpdate(getMainNode());
    }

    /** Pulls valid patterns out of the input buses; anything else goes to the output buses. */
    private void absorbPatterns() {
        boolean changed = false;
        for (NotifiableItemStackHandler in : ApexHatches.itemHandlers(this, IO.IN)) {
            for (int slot = 0; slot < in.getSlots(); slot++) {
                ItemStack s = in.getStackInSlot(slot);
                if (s.isEmpty()) continue;
                IPatternDetails d = PatternDetailsHelper.isEncodedPattern(s) ?
                        PatternDetailsHelper.decodePattern(s, getLevel()) : null;
                if (d != null && accepted(d)) {
                    while (!in.getStackInSlot(slot).isEmpty()) {
                        int free = freePatternSlot();
                        if (free < 0) break;
                        patterns.setStackInSlot(free, in.extractItemInternal(slot, 1, false));
                        changed = true;
                    }
                } else {
                    ItemStack left = toOutputs(s.copy());
                    int moved = s.getCount() - left.getCount();
                    if (moved > 0) in.extractItemInternal(slot, moved, false);
                }
            }
        }
        if (changed) rebuildPatterns();
    }

    private int freePatternSlot() {
        for (int i = 0; i < patterns.getSlots(); i++) if (patterns.getStackInSlot(i).isEmpty()) return i;
        return -1;
    }

    /** puts the stack into the output buses and returns what did not fit */
    private ItemStack toOutputs(ItemStack stack) {
        for (NotifiableItemStackHandler out : ApexHatches.itemHandlers(this, IO.OUT)) {
            for (int slot = 0; slot < out.getSlots() && !stack.isEmpty(); slot++)
                stack = out.insertItemInternal(slot, stack, false);
            if (stack.isEmpty()) break;
        }
        return stack;
    }

    private void ejectPatterns() {
        for (int i = 0; i < patterns.getSlots(); i++) {
            ItemStack s = patterns.getStackInSlot(i);
            if (s.isEmpty()) continue;
            patterns.setStackInSlot(i, toOutputs(s.copy()));
        }
        rebuildPatterns();
    }

    private boolean hasBase(IPatternDetails base) {
        for (IPatternDetails d : details) if (d instanceof ScaledPattern s && s.base().equals(base)) return true;
        return false;
    }

    // ---------------- crafting provider ----------------

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return isFormed() && isWorkingEnabled() ? details : List.of();
    }

    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] inputs) {
        if (!isFormed() || !isWorkingEnabled()) return false;
        // a job planned before the magnification changed still carries the old pattern: accept it at its own scale
        if (!(pattern instanceof ScaledPattern sp) || !hasBase(sp.base())) return false;
        for (GenericStack out : sp.getOutputs()) pending.add(out.what(), out.amount());
        IPatternDetails.IInput[] in = sp.getInputs();
        for (int i = 0; i < in.length && i < inputs.length; i++) {
            for (Object2LongMap.Entry<AEKey> e : inputs[i]) {
                AEKey rest = in[i].getRemainingKey(e.getKey());
                if (rest != null) pending.add(rest, e.getLongValue());
            }
        }
        return true;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    private void flushOutputs() {
        if (pending.isEmpty()) return;
        IGrid grid = getMainNode().getGrid();
        if (grid == null) return;
        var storage = grid.getStorageService().getInventory();
        var energy = grid.getEnergyService();
        List<Object2LongMap.Entry<AEKey>> entries = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> e : pending) entries.add(e);
        for (Object2LongMap.Entry<AEKey> e : entries) {
            AEKey key = e.getKey();
            long amount = e.getLongValue();
            if (amount <= 0) continue;
            long inserted = StorageHelper.poweredInsert(energy, storage, key, amount, actionSource);
            if (inserted > 0) pending.remove(key, inserted);
        }
        pending.removeZeros();
    }

    // ---------------- saving ----------------

    @Override
    public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
        ListTag list = new ListTag();
        for (Object2LongMap.Entry<AEKey> e : pending)
            if (e.getLongValue() > 0) list.add(GenericStack.writeTag(new GenericStack(e.getKey(), e.getLongValue())));
        tag.put("pendingOutputs", list);
    }

    @Override
    public void loadCustomPersistedData(@NotNull CompoundTag tag) {
        super.loadCustomPersistedData(tag);
        pending.clear();
        for (Tag t : tag.getList("pendingOutputs", Tag.TAG_COMPOUND)) {
            GenericStack s = GenericStack.readTag((CompoundTag) t);
            if (s != null) pending.add(s.what(), s.amount());
        }
    }

    // ---------------- interaction and display ----------------

    @Override
    protected InteractionResult onScrewdriverClick(Player player, InteractionHand hand, Direction gridSide,
                                                   BlockHitResult hitResult) {
        if (isRemote()) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) {
            ejectPatterns();
            player.displayClientMessage(Component.literal(patternCount == 0 ? "Patterns moved to the output buses" :
                    patternCount + " patterns did not fit in the output buses"), true);
        } else {
            int next = MAGNIFICATIONS[0];
            for (int i = 0; i < MAGNIFICATIONS.length; i++)
                if (MAGNIFICATIONS[i] == magnification) next = MAGNIFICATIONS[(i + 1) % MAGNIFICATIONS.length];
            magnification = next;
            rebuildPatterns();
            player.displayClientMessage(Component.literal("Pattern magnification: x" +
                    FormattingUtil.formatNumbers(magnification)), true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(isOnline ? Component.literal("ME network: connected").withStyle(ChatFormatting.GREEN) :
                Component.literal("ME network: not connected (cable to the controller face)").withStyle(ChatFormatting.RED));
        textList.add(Component.literal("Patterns: " + patternCount + " / " + PATTERN_SLOTS).withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("Magnification: x" + FormattingUtil.formatNumbers(magnification))
                .withStyle(ChatFormatting.GOLD));
        if (waitingStacks > 0)
            textList.add(Component.literal("Waiting for ME space: " + waitingStacks + " item types")
                    .withStyle(ChatFormatting.YELLOW));
        textList.add(Component.literal("Screwdriver: change magnification. Sneak: take patterns out")
                .withStyle(ChatFormatting.GRAY));
    }
}
