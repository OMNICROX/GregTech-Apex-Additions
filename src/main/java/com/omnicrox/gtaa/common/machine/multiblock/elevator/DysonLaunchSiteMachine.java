package com.omnicrox.gtaa.common.machine.multiblock.elevator;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Dyson Sphere Launch Site. The idea comes from Twist Space Technology (GPL-3); this is GTAA's own implementation,
 * no code was copied. Each launch recipe puts Dyson Swarm Modules into orbit; every module in orbit makes power
 * that leaves through Dynamo / Laser Source hatches. The swarm slowly decays.
 */
public class DysonLaunchSiteMachine extends SpaceElevatorModuleMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DysonLaunchSiteMachine.class, SpaceElevatorModuleMachine.MANAGED_FIELD_HOLDER);

    public static final Stats STATS = new Stats(GTValues.UIV, 2, 4, 64, 1.0f,
            160_008_000L * (2L << (GTValues.UIV - 7)) * 64);
    /** EU/t made by one module in orbit (one amp of ZPM). */
    public static final long EU_PER_MODULE = GTValues.V[GTValues.ZPM];
    public static final long MAX_MODULES = 1_000_000L;
    /** fraction of the swarm lost every second */
    public static final double DECAY_PER_SECOND = 0.001;

    @Persisted @DescSynced private long modules;
    @Persisted private double decayRemainder;
    @DescSynced private long lastOutput;

    private IEnergyContainer outputs = new EnergyContainerList(new ArrayList<>());
    private final ConditionalSubscriptionHandler generator;

    public DysonLaunchSiteMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, STATS, args);
        this.generator = new ConditionalSubscriptionHandler(this, this::generate, () -> isFormed() && modules > 0);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        List<IEnergyContainer> list = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() == IO.OUT) {
                    for (var h : handler.getCapability(EURecipeCapability.CAP)) {
                        if (h instanceof IEnergyContainer c) list.add(c);
                    }
                }
            }
        }
        outputs = new EnergyContainerList(list);
        generator.updateSubscription();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        outputs = new EnergyContainerList(new ArrayList<>());
        generator.updateSubscription();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        generator.updateSubscription();
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        var recipe = getRecipeLogic().getLastRecipe();
        int launched = recipe == null ? 1 : Math.max(1, recipe.parallels);
        modules = Math.min(MAX_MODULES, modules + launched);
        generator.updateSubscription();
    }

    private void generate() {
        long want = modules * EU_PER_MODULE;
        long added = want > 0 ? outputs.changeEnergy(want) : 0;
        lastOutput = added;
        if (getOffsetTimer() % 20 == 0) {
            decayRemainder += modules * DECAY_PER_SECOND;
            long lost = (long) decayRemainder;
            if (lost > 0) {
                decayRemainder -= lost;
                modules = Math.max(0, modules - lost);
                if (modules == 0) generator.updateSubscription();
            }
        }
    }

    public long getModules() {
        return modules;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Dyson Swarm Modules in orbit: " + com.omnicrox.gtaa.api.GTAAFormat.num(modules))
                .withStyle(ChatFormatting.GOLD));
        textList.add(Component.literal("Swarm output: " + com.omnicrox.gtaa.api.GTAAFormat.num(modules * EU_PER_MODULE) + " EU/t")
                .withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("Sent to hatches: " + com.omnicrox.gtaa.api.GTAAFormat.num(lastOutput) + " EU/t")
                .withStyle(ChatFormatting.GRAY));
    }
}
