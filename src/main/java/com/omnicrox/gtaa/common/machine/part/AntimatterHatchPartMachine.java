package com.omnicrox.gtaa.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Antimatter Hatch: 16,384,000 L output hatch locked to Semi-Stable Antimatter.
 * Port of GoodGenerator's AntimatterOutputHatch (GT5-Unofficial).
 * <p>
 * Keep level: nothing outside the forge can take the hatch below it (auto-output to the front, pipes, ME import
 * buses, interfaces...), so the forge keeps a working amount of antimatter and only the surplus leaves. All 16
 * hatches even out every cycle, so their output stays balanced. Screwdriver cycles the level (shift = back).
 */
public class AntimatterHatchPartMachine extends FluidHatchPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AntimatterHatchPartMachine.class, FluidHatchPartMachine.MANAGED_FIELD_HOLDER);

    public static final int CAPACITY = 16_384_000;
    public static final int[] KEEP_LEVELS = { 0, 10_000, 100_000, 250_000, 500_000, 1_000_000, 2_000_000, 4_000_000,
            8_000_000, 12_000_000, 16_000_000 };
    /** default: keep 1,000,000 L in each hatch */
    public static final int DEFAULT_KEEP = 5;

    @Persisted @DescSynced private int keepIndex = DEFAULT_KEEP;

    public AntimatterHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.UEV, IO.OUT, CAPACITY, 1);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int keepLevel() {
        return KEEP_LEVELS[Math.max(0, Math.min(KEEP_LEVELS.length - 1, keepIndex))];
    }

    @Override
    protected NotifiableFluidTank createTank(int initialCapacity, int slots, Object... args) {
        return new NotifiableFluidTank(this, 1, CAPACITY, IO.OUT) {

            /** what may leave the hatch from outside: everything above the keep level */
            private int surplus() {
                return Math.max(0, getFluidInTank(0).getAmount() - keepLevel());
            }

            @Override
            public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
                return super.drain(Math.min(maxDrain, surplus()), action);
            }

            @Override
            public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
                if (resource.isEmpty()) return FluidStack.EMPTY;
                return super.drain(new FluidStack(resource, Math.min(resource.getAmount(), surplus())), action);
            }
        }.setFilter(fs -> GTAAMaterials.ANTIMATTER != null && fs.getFluid() == GTAAMaterials.ANTIMATTER.getFluid());
    }

    public long getAmount() {
        return tank.getFluidInTank(0).getAmount();
    }

    public void fillAntimatter(int amount) {
        if (amount > 0) tank.fillInternal(GTAAMaterials.ANTIMATTER.getFluid(amount), IFluidHandler.FluidAction.EXECUTE);
    }

    public int drainAntimatter(int amount) {
        if (amount <= 0) return 0;
        FluidStack drained = tank.drainInternal(amount, IFluidHandler.FluidAction.EXECUTE);
        return drained.getAmount();
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player player, InteractionHand hand, Direction side, BlockHitResult hit) {
        if (isRemote()) return InteractionResult.SUCCESS;
        int n = KEEP_LEVELS.length;
        keepIndex = ((keepIndex + (player.isShiftKeyDown() ? -1 : 1)) % n + n) % n;
        // every hatch of the forge gets the same level, or the higher ones would lose antimatter evening out
        int synced = 1;
        for (var controller : getControllers())
            for (var part : controller.getParts())
                if (part instanceof AntimatterHatchPartMachine h && h != this) {
                    h.keepIndex = keepIndex;
                    synced++;
                }
        player.displayClientMessage(Component.literal("Keeps " + com.omnicrox.gtaa.api.GTAAFormat.num(keepLevel()) +
                " L of antimatter in each hatch (" + synced + " hatches); only the surplus leaves").withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return InteractionResult.CONSUME;
    }
}
