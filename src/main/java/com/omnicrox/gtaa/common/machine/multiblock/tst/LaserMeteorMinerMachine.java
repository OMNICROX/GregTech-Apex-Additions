package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.util.ApexFormat;
import com.omnicrox.gtaa.common.data.GTAAGTNHMachines;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * Laser Meteor Miner (Twist Space Technology, by Totto). Mines the Blood Magic meteor that the Mark of the Falling
 * Tower drops on a block placed {@value #DISTANCE} blocks above the top of the machine, right over the laser. Tier 1
 * mines one block per cycle (fortune from the pickaxe in the first input bus), tier 2 a row (fortune III), tier 3 a
 * whole layer (fortune IV). Cycles take 2 s at MV and overclock x4 power / x1.41 speed, with a 16x voltage margin.
 */
public class LaserMeteorMinerMachine extends WorkableElectricMultiblockMachine
        implements com.omnicrox.apexlib.multiblock.IStagedMulti {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            LaserMeteorMinerMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int DISTANCE = 48, MAX_RADIUS = 40, BASE_TICKS = 40, POLL_TICKS = 200;
    private static final int MAX_AIR_CHECKS = 4096;
    private static final long BASE_EUT = GTValues.VA[GTValues.MV];

    @DescSynced private int tier;
    @Persisted private int xs, ys, zs, xd, yd, zd;
    @Persisted @DescSynced private int radius = MAX_RADIUS;
    @Persisted @DescSynced private boolean finished = true;
    @Persisted private boolean started;
    @Persisted private double progress;
    @DescSynced private boolean waiting;
    @DescSynced private long lastEut;
    @DescSynced private int fortune;
    @DescSynced private long mined;
    @DescSynced private String status = "";
    private int matched = 1;
    private final List<ItemStack> pending = new ArrayList<>();
    private TickableSubscription tick;

    public LaserMeteorMinerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean checkPattern() {
        for (int t = 3; t >= 1; t--) {
            BlockPattern p = GTAAGTNHMachines.laserMeteorMinerPattern(getDefinition(), t);
            if (p != null && p.checkPatternAt(getMultiblockState(), false)) {
                matched = t;
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockPattern getPattern() {
        return GTAAGTNHMachines.laserMeteorMinerPattern(getDefinition(), matched);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (tier != matched) started = false;
        tier = matched;
        BlockPos c = meteorCenter();
        if (c.getX() != xs || c.getY() != ys || c.getZ() != zs) started = false;
        xs = c.getX();
        ys = c.getY();
        zs = c.getZ();
        if (!isRemote()) tick = subscribeServerTick(tick, this::work);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    /**
     * Twist: the meteor center is {@value #DISTANCE} blocks above the top of the multi, over the laser column
     * (2 blocks behind the controller for tier 1, 6 for tiers 2 and 3).
     */
    private BlockPos meteorCenter() {
        Direction back = getFrontFacing().getOpposite();
        int behind = tier == 1 ? 2 : 6;
        BlockPos p = getPos().relative(back, behind);
        return new BlockPos(p.getX(), getPos().getY() + DISTANCE + (tier == 1 ? 13 : 15), p.getZ());
    }

    private IEnergyContainer energy() {
        List<IEnergyContainer> list = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(EURecipeCapability.CAP))
                    if (h instanceof IEnergyContainer c) list.add(c);
            }
        return new EnergyContainerList(list);
    }

    private void work() {
        if (!(getLevel() instanceof ServerLevel level) || !isWorkingEnabled()) {
            lastEut = 0;
            return;
        }
        if (!pending.isEmpty() && !flush()) {
            status = "Output buses are full";
            lastEut = 0;
            return;
        }
        if (!areaLoaded(level, started && !finished ? radius : MAX_RADIUS)) {
            status = "The meteor area is not loaded";
            lastEut = 0;
            return;
        }
        if (!started || finished) {
            // wait for a meteor: Twist checks the block above the center every 10 s
            waiting = true;
            lastEut = 0;
            if (getOffsetTimer() % POLL_TICKS != 0 && started) {
                status = "Waiting for a meteor";
                return;
            }
            started = true;
            if (level.isEmptyBlock(new BlockPos(xs, ys + 1, zs))) {
                finished = true;
                status = "Waiting for a meteor";
                return;
            }
            findRadius(level);
            xd = xs - radius;
            yd = ys - radius;
            zd = zs - radius;
            finished = false;
            mined = 0;
            waiting = false;
        }
        // the cycle: 2 s at MV, overclocked with a 16x voltage margin
        IEnergyContainer energy = energy();
        long budget = energy.getInputVoltage() * Math.max(1, energy.getInputAmperage()) / 16;
        int ocs = 0;
        while (ocs < 30 && BASE_EUT * Math.pow(4, ocs + 1) <= budget && ticks(ocs + 1) >= 1 && ticks(ocs) > 1) ocs++;
        long eut = (long) (BASE_EUT * Math.pow(4, ocs));
        int duration = ticks(ocs);
        if (energy.getEnergyStored() < eut) {
            status = "Not enough energy";
            lastEut = 0;
            return;
        }
        energy.removeEnergy(eut);
        lastEut = eut;
        status = "Mining";
        progress += 1;
        if (progress < duration) return;
        progress = 0;
        fortune = fortuneLevel();
        switch (tier) {
            case 1 -> mineSingle(level);
            case 2 -> mineRow(level);
            default -> mineLayer(level);
        }
        flush();
    }

    private static int ticks(int ocs) {
        return Math.max(1, (int) Math.ceil(BASE_TICKS / Math.pow(Math.sqrt(2), ocs)));
    }

    private boolean areaLoaded(ServerLevel level, int r) {
        return level.hasChunksAt(xs - r, ys - r, zs - r, xs + r, ys + r, zs + r);
    }

    private void findRadius(ServerLevel level) {
        radius = MAX_RADIUS;
        int delta = 0;
        for (int z = zs - MAX_RADIUS; delta < MAX_RADIUS - 1; z++) {
            if (!level.isEmptyBlock(new BlockPos(xs, ys, z))) break;
            delta++;
        }
        radius -= delta;
    }

    private void mineSingle(ServerLevel level) {
        int checks = 0;
        while (true) {
            if (zd == zs - radius && level.isEmptyBlock(new BlockPos(xd, yd, zs))) nextColumn();
            else if (level.isEmptyBlock(new BlockPos(xd, yd, zd))) nextBlock();
            else break;
            if (finished || ++checks >= MAX_AIR_CHECKS) return;
        }
        mine(level, new BlockPos(xd, yd, zd));
        nextBlock();
    }

    private void mineRow(ServerLevel level) {
        while (level.isEmptyBlock(new BlockPos(xd, yd, zs))) {
            nextColumn();
            if (finished) return;
        }
        for (int z = zs - radius; z <= zs + radius; z++) {
            BlockPos p = new BlockPos(xd, yd, z);
            if (!level.isEmptyBlock(p)) mine(level, p);
        }
        nextColumn();
    }

    private void mineLayer(ServerLevel level) {
        while (yd <= ys + radius && level.isEmptyBlock(new BlockPos(xs, yd, zs))) yd++;
        if (yd > ys + radius) {
            finished = true;
            return;
        }
        for (int x = xs - radius; x <= xs + radius; x++)
            for (int z = zs - radius; z <= zs + radius; z++) {
                BlockPos p = new BlockPos(x, yd, z);
                if (!level.isEmptyBlock(p)) mine(level, p);
            }
        yd++;
        if (yd > ys + radius) finished = true;
    }

    private void nextBlock() {
        if (zd <= zs + radius) zd++;
        else {
            zd = zs - radius;
            nextColumn();
        }
    }

    private void nextColumn() {
        if (xd <= xs + radius) xd++;
        else if (yd <= ys + radius) {
            xd = xs - radius;
            yd++;
        } else finished = true;
    }

    private void mine(ServerLevel level, BlockPos p) {
        // the block that caught the meteor stays where it is, and so do block entities
        if (p.getX() == xs && p.getY() == ys && p.getZ() == zs) return;
        BlockState s = level.getBlockState(p);
        if (s.getDestroySpeed(level, p) < 0 || level.getBlockEntity(p) != null) return;
        ItemStack tool = new ItemStack(Items.NETHERITE_PICKAXE);
        if (fortune > 0) tool.enchant(Enchantments.BLOCK_FORTUNE, fortune);
        var fake = FakePlayerFactory.getMinecraft(level);
        pending.addAll(Block.getDrops(s, level, p, null, fake, tool));
        level.removeBlock(p, false);
        mined++;
    }

    /** Twist: tier 1 takes the fortune of its pickaxe (Terra Shatterer counts as III), tiers 2 and 3 have III and IV */
    private int fortuneLevel() {
        if (tier == 2) return 3;
        if (tier >= 3) return 4;
        var buses = ApexHatches.itemHandlers(this, IO.IN);
        if (buses.isEmpty()) return 0;
        NotifiableItemStackHandler bus = buses.get(0);
        ItemStack s = bus.getSlots() > 0 ? bus.getStackInSlot(0) : ItemStack.EMPTY;
        if (s.isEmpty()) return 0;
        int f = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, s);
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(s.getItem());
        if (id != null && id.toString().equals("botania:terra_pick")) f = Math.max(f, 3);
        return Math.min(f, 5);
    }

    /** moves the mined items into the output buses; false while some are left */
    private boolean flush() {
        List<NotifiableItemStackHandler> outs = ApexHatches.itemHandlers(this, IO.OUT);
        List<ItemStack> left = new ArrayList<>();
        for (ItemStack drop : pending) {
            ItemStack rest = drop;
            for (NotifiableItemStackHandler inv : outs) {
                for (int i = 0; i < inv.getSlots() && !rest.isEmpty(); i++) rest = inv.insertItemInternal(i, rest, false);
                if (rest.isEmpty()) break;
            }
            if (!rest.isEmpty()) left.add(rest);
        }
        pending.clear();
        pending.addAll(left);
        return pending.isEmpty();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Tier " + tier + ": " + (tier == 1 ? "one block" : tier == 2 ? "one row" : "one layer") +
                " per cycle, fortune " + fortune).withStyle(ChatFormatting.GOLD));
        textList.add(Component.literal("Meteor center: " + xs + ", " + ys + ", " + zs).withStyle(ChatFormatting.GRAY));
        if (!status.isEmpty()) textList.add(Component.literal(status + (status.equals("Mining") ? " (radius " + radius +
                ", " + ApexFormat.num(mined) + " blocks)" : "")).withStyle(status.equals("Mining") ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        if (lastEut > 0) textList.add(Component.literal("Using " + ApexFormat.num(lastEut) + " EU/t").withStyle(ChatFormatting.AQUA));
    }

    // ---- Structure Upgrader: the three tiers ----
    @Override public int stageCount() { return 3; }
    @Override public int currentStage() { return isFormed() ? tier : 0; }
    @Override public BlockPattern stagePattern(int stage) { return GTAAGTNHMachines.laserMeteorMinerPattern(getDefinition(), stage); }
    @Override public String stageName(int stage) { return "Tier " + stage; }
    @Override public String stageAdds(int stage) {
        return stage == 1 ? "one block a cycle" : stage == 2 ? "one row a cycle, Fortune III" : "one layer a cycle, Fortune IV";
    }
}
