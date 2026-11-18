package com.omnicrox.gtaa.common.machine.multiblock.elevator;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.block.SpaceElevatorCableBlockEntity;
import com.omnicrox.gtaa.common.data.GTAAMachines;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Space Elevator. Port of GTNH-Intergalactic's TileEntitySpaceElevator (MIT):
 * motor tier limits module count (6/12/15/18/24) and module tier; energy goes into this controller and is shared
 * out to the modules every second; pausing it pauses every module. From Motor MK-III the base can be extended
 * (screwdriver) to fit 24 modules.
 */
public class SpaceElevatorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SpaceElevatorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int[] MODULE_SLOTS = { 6, 12, 15, 18, 24 };
    private static final int CHARGE_INTERVAL = 20;
    private static final int CLIMB_INTERVAL = 2000;

    @Persisted @DescSynced private boolean extensionEnabled;
    @DescSynced private int motorTier;
    @DescSynced private int moduleCount;
    @DescSynced private boolean tooManyModules;
    private final List<BlockPos> modulePositions = new ArrayList<>();
    private BlockPos cablePos;
    private TickableSubscription tickSub;

    public SpaceElevatorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static int moduleSlots(int motorTier) {
        return motorTier < 1 || motorTier > MODULE_SLOTS.length ? 0 : MODULE_SLOTS[motorTier - 1];
    }

    public int getMotorTier() {
        return motorTier;
    }

    @Override
    public BlockPattern getPattern() {
        return extensionEnabled ? GTAAMachines.SPACE_ELEVATOR_EXTENDED_PATTERN.get() : super.getPattern();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var ctx = getMultiblockState().getMatchContext();
        motorTier = ctx.getOrDefault(SpaceElevatorPredicates.CTX_MOTOR, 0);
        LongSet modules = ctx.getOrDefault(SpaceElevatorPredicates.CTX_MODULES, new LongOpenHashSet());
        modulePositions.clear();
        for (long l : modules) modulePositions.add(BlockPos.of(l));
        moduleCount = modulePositions.size();
        tooManyModules = moduleCount > moduleSlots(motorTier) || (extensionEnabled && motorTier < 3 && moduleCount > 12);
        cablePos = ctx.getOrDefault(SpaceElevatorPredicates.CTX_CABLE, null);
        if (cable() != null) cable().setShouldRender(!tooManyModules);
        if (!isRemote()) tickSub = subscribeServerTick(tickSub, this::elevatorTick);
    }

    @Override
    public void onStructureInvalid() {
        if (cable() != null) cable().setShouldRender(false);
        forEachModule(SpaceElevatorModuleMachine::disconnect);
        super.onStructureInvalid();
        if (tickSub != null) {
            tickSub.unsubscribe();
            tickSub = null;
        }
    }

    private SpaceElevatorCableBlockEntity cable() {
        if (cablePos == null || getLevel() == null) return null;
        return getLevel().getBlockEntity(cablePos) instanceof SpaceElevatorCableBlockEntity be ? be : null;
    }

    private void forEachModule(java.util.function.Consumer<SpaceElevatorModuleMachine> action) {
        if (getLevel() == null) return;
        for (BlockPos pos : modulePositions) {
            if (MetaMachine.getMachine(getLevel(), pos) instanceof SpaceElevatorModuleMachine m) action.accept(m);
        }
    }

    private void elevatorTick() {
        if (!isFormed()) return;
        if (!getRecipeLogic().isWorkingEnabled() || tooManyModules) {
            forEachModule(SpaceElevatorModuleMachine::disconnect);
            return;
        }
        long timer = getOffsetTimer();
        SpaceElevatorCableBlockEntity cable = cable();
        if (cable != null && timer % CLIMB_INTERVAL == 0 && !cable.isAnimating()) {
            cable.start(SpaceElevatorCableBlockEntity.ANIM_DELIVER);
        }
        if (timer % CHARGE_INTERVAL != 0) return;

        List<SpaceElevatorModuleMachine> chargeable = new ArrayList<>();
        forEachModule(m -> {
            if (m.getNeededMotorTier() > motorTier) {
                m.disconnect();
                return;
            }
            m.connect(this);
            if (m.isFormed()) chargeable.add(m);
        });
        if (chargeable.isEmpty() || energyContainer == null) return;
        long share = energyContainer.getEnergyStored() / chargeable.size();
        for (SpaceElevatorModuleMachine m : chargeable) {
            long available = energyContainer.getEnergyStored();
            if (available <= 0) break;
            long given = m.increaseStoredEU(Math.min(share, available));
            if (given > 0) energyContainer.removeEnergy(given);
        }
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player player, InteractionHand hand, Direction gridSide,
                                                   BlockHitResult hitResult) {
        if (isRemote()) return InteractionResult.SUCCESS;
        extensionEnabled = !extensionEnabled;
        player.displayClientMessage(Component.literal(extensionEnabled ?
                "Extended structure enabled (needs Motor MK-III+, fits 24 modules)" :
                "Extended structure disabled"), true);
        getMultiblockState().setError(null);
        onStructureInvalid();
        return InteractionResult.CONSUME;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(getRecipeLogic().isWorkingEnabled(), getRecipeLogic().isWorkingEnabled() && isFormed())
                .addEnergyUsageLine(energyContainer)
                .addCustom(t -> {
                    t.add(Component.literal("Motor Tier: MK-" + roman(motorTier)).withStyle(ChatFormatting.AQUA));
                    t.add(Component.literal("Modules: " + moduleCount + " / " + moduleSlots(motorTier))
                            .withStyle(tooManyModules ? ChatFormatting.RED : ChatFormatting.GREEN));
                    if (tooManyModules) t.add(Component.literal("Too many modules for this motor tier!")
                            .withStyle(ChatFormatting.RED));
                    t.add(Component.literal("Extended structure: " + (extensionEnabled ? "on" : "off") +
                            " (screwdriver)").withStyle(ChatFormatting.GRAY));
                });
    }

    private static String roman(int i) {
        return switch (i) { case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V"; default -> "?"; };
    }
}
