package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAAItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * Lightning Spire, after Twist Space Technology's (GTNH addon). Stores up to 512 Spire Lightning Rods (fed through an
 * input bus). Every 12.8 s it calls down lightning: each rod makes 28,000,000 EU into an internal buffer
 * (280,000,000 EU per rod), costing 128 L of coolant and 72 L of molten iron per rod. The buffer drains into the
 * dynamo / laser hatches. Weather doesn't matter. Breaking the controller drops the stored rods.
 */
public class LightningSpireMachine extends WorkableElectricMultiblockMachine
        implements com.gregtechceu.gtceu.api.machine.feature.IMachineLife {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            LightningSpireMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int MAX_RODS = 512;
    public static final long EU_PER_ROD = 28_000_000L;
    public static final long BUFFER_PER_ROD = 280_000_000L;
    public static final int STRIKE_TICKS = 256;
    public static final int COOLANT = 128;
    public static final int IRON_PER_ROD = 72;

    @Persisted @DescSynced private int rods;
    @Persisted @DescSynced private long stored;
    @Persisted @DescSynced private boolean visuals = true;
    @DescSynced private String problem = "";
    @DescSynced private int ticksToStrike;
    @DescSynced private long lastOut;
    private IEnergyContainer outputs = new EnergyContainerList(new ArrayList<>());
    private TickableSubscription tick;

    public LightningSpireMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    /** Cryotheum if some mod has it, otherwise liquid helium */
    public static FluidStack coolant(int amount) {
        for (var key : ForgeRegistries.FLUIDS.getKeys()) {
            if (key.getPath().equals("cryotheum")) {
                Fluid f = ForgeRegistries.FLUIDS.getValue(key);
                if (f != null) return new FluidStack(f, amount);
            }
        }
        try {
            return GTMaterials.Helium.getFluid(FluidStorageKeys.LIQUID, amount);
        } catch (Throwable t) {
            return GTMaterials.Nitrogen.getFluid(FluidStorageKeys.LIQUID, amount);
        }
    }

    public static FluidStack iron(int amount) {
        return GTMaterials.Iron.getFluid(amount);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        List<IEnergyContainer> list = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var h : part.getRecipeHandlers())
                if (h.getHandlerIO() == IO.OUT)
                    for (var c : h.getCapability(EURecipeCapability.CAP)) if (c instanceof IEnergyContainer e) list.add(e);
        outputs = new EnergyContainerList(list);
        if (!isRemote()) tick = subscribeServerTick(tick, this::spireTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        outputs = new EnergyContainerList(new ArrayList<>());
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    @Override
    public void onMachineRemoved() {
        if (getLevel() != null && !isRemote() && rods > 0) {
            int left = rods;
            while (left > 0) {
                int n = Math.min(64, left);
                Block.popResource(getLevel(), getPos(), new ItemStack(GTAAItems.SPIRE_LIGHTNING_ROD.get(), n));
                left -= n;
            }
            rods = 0;
        }
    }

    private void absorbRods() {
        if (rods >= MAX_RODS) return;
        for (IMultiPart part : getParts())
            for (var h : part.getRecipeHandlers())
                if (h.getHandlerIO() == IO.IN)
                    for (var c : h.getCapability(ItemRecipeCapability.CAP))
                        if (c instanceof NotifiableItemStackHandler inv)
                            for (int i = 0; i < inv.getSlots() && rods < MAX_RODS; i++)
                                if (inv.getStackInSlot(i).is(GTAAItems.SPIRE_LIGHTNING_ROD.get()))
                                    rods += inv.extractItemInternal(i, MAX_RODS - rods, false).getCount();
    }

    private List<IFluidHandler> tanks() {
        List<IFluidHandler> l = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var h : part.getRecipeHandlers())
                if (h.getHandlerIO() == IO.IN)
                    for (var c : h.getCapability(FluidRecipeCapability.CAP)) if (c instanceof NotifiableFluidTank t) l.add(t);
        return l;
    }

    private boolean drain(FluidStack want, boolean simulate) {
        int left = want.getAmount();
        for (IFluidHandler t : tanks()) {
            left -= ((com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank) t).drainInternal(new FluidStack(want, left), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE).getAmount();
            if (left <= 0) return true;
        }
        return left <= 0;
    }

    private void spireTick() {
        if (!isFormed()) return;
        long t = getOffsetTimer();
        if (t % 20 == 0) absorbRods();
        ticksToStrike = (int) (STRIKE_TICKS - t % STRIKE_TICKS);
        if (getRecipeLogic().isWorkingEnabled() && t % STRIKE_TICKS == 0) strike();
        if (stored > 0) {
            long put = outputs.changeEnergy(stored);
            stored -= put;
            lastOut = put;
        } else lastOut = 0;
    }

    private void strike() {
        if (rods <= 0) {
            problem = "No Spire Lightning Rods inside (feed them through an input bus)";
            return;
        }
        FluidStack cool = coolant(COOLANT), fe = iron(IRON_PER_ROD * rods);
        if (!drain(cool, true) || !drain(fe, true)) {
            problem = "Each strike needs " + COOLANT + " L " + cool.getDisplayName().getString() + " and " +
                    FormattingUtil.formatNumbers(fe.getAmount()) + " L Molten Iron";
            return;
        }
        drain(cool, false);
        drain(fe, false);
        problem = "";
        stored = Math.min(stored + rods * EU_PER_ROD, rods * BUFFER_PER_ROD);
        if (visuals && getLevel() instanceof ServerLevel level) {
            BlockPos top = topPos();
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt != null) {
                bolt.moveTo(top.getX() + 0.5, top.getY() + 1, top.getZ() + 0.5);
                bolt.setVisualOnly(true);
                level.addFreshEntity(bolt);
            }
        }
    }

    private BlockPos topPos() {
        BlockPos best = getPos();
        if (getLevel() == null) return best;
        for (BlockPos p : getMultiblockState().getCache())
            if (getLevel().getBlockState(p).is(Blocks.LIGHTNING_ROD) && p.getY() > best.getY()) best = p.immutable();
        return best;
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player player, InteractionHand hand, Direction gridSide, BlockHitResult hitResult) {
        if (isRemote()) return InteractionResult.SUCCESS;
        visuals = !visuals;
        player.displayClientMessage(Component.literal("Lightning visuals " + (visuals ? "on" : "off")), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Lightning Rods: " + rods + " / " + MAX_RODS).withStyle(ChatFormatting.YELLOW));
        textList.add(Component.literal("Per strike: " + FormattingUtil.formatNumbers(rods * EU_PER_ROD) + " EU (avg " +
                FormattingUtil.formatNumbers(rods * EU_PER_ROD / STRIKE_TICKS) + " EU/t)").withStyle(ChatFormatting.GOLD));
        textList.add(Component.literal("Buffer: " + FormattingUtil.formatNumbers(stored) + " / " +
                FormattingUtil.formatNumbers(rods * BUFFER_PER_ROD) + " EU").withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("Output: " + FormattingUtil.formatNumbers(lastOut) + " EU/t").withStyle(ChatFormatting.GREEN));
        textList.add(Component.literal("Next strike in " + String.format("%.1f s", ticksToStrike / 20f)).withStyle(ChatFormatting.WHITE));
        textList.add(Component.literal("Needs " + COOLANT + " L " + coolant(1).getDisplayName().getString() + " + " +
                FormattingUtil.formatNumbers((long) IRON_PER_ROD * rods) + " L Molten Iron per strike").withStyle(ChatFormatting.GRAY));
        if (!problem.isEmpty()) textList.add(Component.literal(problem).withStyle(ChatFormatting.RED));
    }
}
