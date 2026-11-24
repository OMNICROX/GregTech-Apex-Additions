package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.api.GTAAWireless;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import com.omnicrox.gtaa.common.machine.multiblock.AntimatterMachineHelper.StoredFluid;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shielded Lagrangian Annihilation Matrix (SLAM / Antimatter Generator).
 * Logic ported from GoodGenerator's AntimatterGenerator (GT5-Unofficial / GT New Horizons):
 * every 5 seconds it annihilates ALL fluid in its input hatches; with antimatter + exactly one kind of matter it
 * makes A * antimatter^E EU (A = 1e12 EU/L) scaled by min(AM/M, M/AM). Anything else is voided.
 * Matter: Molten Copper (E 1.00), Molten Superconductor Base UIV (1.02) / UMV (1.03).
 * Wireless mode (screwdriver, needs GTM Things and UMV base): EU goes to the owner's wireless network instead of
 * the hatches, still capped at what the hatches could hold.
 */
public class AntimatterGeneratorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AntimatterGeneratorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final double ANTIMATTER_FUEL_VALUE = 1e12;
    public static final int CYCLE_TICKS = 100;

    // last cycle, for the UI (the SLAM works in 5-second bursts, so these stay up between them)
    @Persisted @DescSynced private long euLastCycle;
    @Persisted @DescSynced private long euVoidedLastCycle;
    /** EU of the current burst still waiting to go into the hatches (released over the 5 s, not in one tick) */
    @Persisted private long pending;
    @Persisted @DescSynced private long sentLastCycle;
    @Persisted @DescSynced private long sentThisCycle;
    @Persisted @DescSynced private long antimatterLastCycle;
    @Persisted @DescSynced private long matterLastCycle;
    @Persisted @DescSynced private String matterNameLastCycle = "";
    @Persisted @DescSynced private float boostLastCycle;
    @Persisted @DescSynced private float efficiency;
    @Persisted @DescSynced private float avgEfficiency;
    @Persisted @DescSynced private String problem = "";
    @Persisted @DescSynced private boolean wireless = false;
    // live, refreshed every second
    @DescSynced private long antimatterStored;
    @DescSynced private long matterStored;
    @DescSynced private String matterStoredName = "";
    @DescSynced private int fluidKinds;
    @DescSynced private long hatchCapacity;
    @DescSynced private long hatchStored;
    @DescSynced private int hatchCount;
    @DescSynced private int inputHatches;
    @DescSynced private String wirelessStored = "";
    @DescSynced private int ticksToCycle;

    private final List<Float> history = new ArrayList<>();
    private int historyIndex;
    private TickableSubscription tickSub;

    public AntimatterGeneratorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public boolean isWireless() {
        return wireless;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) tickSub = subscribeServerTick(tickSub, this::antimatterTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tickSub != null) {
            tickSub.unsubscribe();
            tickSub = null;
        }
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player player, InteractionHand hand, Direction gridSide, BlockHitResult hitResult) {
        if (isRemote()) return InteractionResult.SUCCESS;
        if (!GTAAWireless.isAvailable()) {
            player.displayClientMessage(Component.literal("Wireless mode needs GTM Things installed"), true);
            return InteractionResult.CONSUME;
        }
        wireless = !wireless;
        player.displayClientMessage(Component.literal(wireless ?
                "SLAM: wireless mode ON (needs Molten Superconductor Base UMV as matter)" :
                "SLAM: wireless mode OFF (power goes to the dynamo / laser hatches)")
                .withStyle(wireless ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY), true);
        return InteractionResult.CONSUME;
    }

    //////////////////////////////////////
    // ***** logic *****
    //////////////////////////////////////

    private void antimatterTick() {
        if (!isFormed()) return;
        long t = getOffsetTimer();
        ticksToCycle = (int) (CYCLE_TICKS - t % CYCLE_TICKS);
        if (t % 20 == 0) refreshLive();
        if (pending > 0) release();
        if (!getRecipeLogic().isWorkingEnabled()) return;
        if (t % CYCLE_TICKS == 0) cycle();
    }

    /** push as much of the burst as the hatches take this tick; they empty themselves every tick, so it spreads out */
    private void release() {
        List<IEnergyContainer> outs = outputs();
        long left = pending;
        for (int i = 0; i < outs.size() && left > 0; i++) {
            IEnergyContainer c = outs.get(i);
            long share = left / (outs.size() - i) + (left % (outs.size() - i) > 0 ? 1 : 0);
            long space = c.getEnergyCapacity() - c.getEnergyStored();
            long put = Math.min(space, share);
            if (put > 0) left -= c.changeEnergy(put);
        }
        sentThisCycle = sat(sentThisCycle, pending - left);
        pending = left;
    }

    private static Fluid antimatterFluid() {
        return GTAAMaterials.ANTIMATTER == null ? null : GTAAMaterials.ANTIMATTER.getFluid();
    }

    private List<IEnergyContainer> outputs() {
        List<IEnergyContainer> out = new ArrayList<>();
        for (IRecipeHandler<?> h : getCapabilitiesFlat(IO.OUT, EURecipeCapability.CAP))
            if (h instanceof IEnergyContainer c) out.add(c);
        return out;
    }

    /** total by fluid, in the order found */
    private static Map<Fluid, Long> byFluid(List<StoredFluid> inputs) {
        Map<Fluid, Long> m = new LinkedHashMap<>();
        for (StoredFluid f : inputs) m.merge(f.stack().getFluid(), (long) f.stack().getAmount(), Long::sum);
        return m;
    }

    private static String name(Fluid f) {
        return f == null ? "" : new FluidStack(f, 1).getDisplayName().getString();
    }

    private void refreshLive() {
        Fluid am = antimatterFluid();
        Map<Fluid, Long> fluids = byFluid(AntimatterMachineHelper.inputFluids(this));
        fluidKinds = fluids.size();
        antimatterStored = am == null ? 0 : fluids.getOrDefault(am, 0L);
        matterStored = 0;
        matterStoredName = "";
        for (var e : fluids.entrySet()) {
            if (e.getKey() == am) continue;
            matterStored += e.getValue();
            matterStoredName = matterStoredName.isEmpty() ? name(e.getKey()) : matterStoredName + ", " + name(e.getKey());
        }
        List<IEnergyContainer> outs = outputs();
        hatchCount = outs.size();
        long cap = 0, stored = 0;
        for (IEnergyContainer c : outs) {
            cap = sat(cap, c.getEnergyCapacity());
            stored = sat(stored, c.getEnergyStored());
        }
        hatchCapacity = cap;
        hatchStored = stored;
        int in = 0;
        for (var part : getParts())
            for (var list : part.getRecipeHandlers())
                if (list.isValid(IO.IN) && !list.getCapability(com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability.CAP).isEmpty()) {
                    in++;
                    break;
                }
        inputHatches = in;
        wirelessStored = wireless ? GTAAWireless.storedText(getOwnerUUID()) : "";
    }

    private static long sat(long a, long b) {
        long r = a + b;
        return r < 0 ? Long.MAX_VALUE : r;
    }

    private void cycle() {
        Fluid am = antimatterFluid();
        if (am == null) return;
        // whatever of the last burst the hatches couldn't take in 5 s is gone
        long undelivered = pending;
        pending = 0;
        sentLastCycle = sentThisCycle;
        sentThisCycle = 0;
        if (undelivered > 0) {
            euVoidedLastCycle = undelivered;
            problem = "The hatches couldn't take a whole burst in 5 s: " + com.omnicrox.gtaa.api.GTAAFormat.num(undelivered) +
                    " EU voided. Add more / bigger dynamo or laser hatches";
        }
        List<StoredFluid> inputs = AntimatterMachineHelper.inputFluids(this);
        if (inputs.isEmpty()) return; // nothing in: keep showing the last burst
        Map<Fluid, Long> fluids = byFluid(inputs);
        // everything is annihilated, valid or not
        for (StoredFluid f : inputs) AntimatterMachineHelper.drainAll(f);

        long antimatter = fluids.getOrDefault(am, 0L);
        Fluid matter = null;
        long matterAmount = 0;
        for (var e : fluids.entrySet()) if (e.getKey() != am) { matter = e.getKey(); matterAmount = e.getValue(); }
        antimatterLastCycle = antimatter;
        matterLastCycle = matterAmount;
        matterNameLastCycle = name(matter);
        euLastCycle = 0;
        if (undelivered == 0) euVoidedLastCycle = 0;
        boostLastCycle = 0;

        if (antimatter <= 0) fail("No antimatter: everything in the hatches was voided");
        else if (matter == null) fail("No matter: add Molten Copper or a Molten Superconductor Base (UIV / UMV)");
        else if (fluids.size() > 2) fail("More than one kind of matter: everything was voided");
        else if (modifierFor(matter) == 0f) fail(name(matter) + " isn't valid matter and was voided");
        else createEU(antimatter, matter, matterAmount);
        refreshLive();
    }

    private void fail(String why) {
        problem = why;
        updateEfficiency(0f);
    }

    public static float modifierFor(Fluid fluid) {
        if (fluid == null) return 0f;
        if (fluid == GTMaterials.Copper.getFluid()) return 1.00f;
        if (fluid == AntimatterMachineHelper.fluidOf("gtna:superconductor_uiv_base")) return 1.02f;
        if (fluid == AntimatterMachineHelper.fluidOf("gtna:superconductor_umv_base")) return 1.03f;
        return 0f;
    }

    private void createEU(long antimatter, Fluid matter, long matterAmount) {
        float modifier = modifierFor(matter);
        boostLastCycle = modifier;
        float eff = Math.min((float) antimatter / matterAmount, (float) matterAmount / antimatter);
        updateEfficiency(eff);
        double raw = Math.pow(antimatter, modifier) * ANTIMATTER_FUEL_VALUE * eff;
        long eu = raw >= Long.MAX_VALUE || Double.isNaN(raw) ? Long.MAX_VALUE : (long) raw;
        euLastCycle = eu;
        problem = eff < 0.999f ? "Antimatter and matter aren't 1:1, so efficiency dropped" : "";

        List<IEnergyContainer> outs = outputs();
        if (outs.isEmpty()) {
            euVoidedLastCycle = eu;
            problem = "No dynamo / laser source hatch: all of it was voided";
            return;
        }
        if (wireless) {
            if (!GTAAWireless.isAvailable()) {
                problem = "Wireless mode needs GTM Things; sent to the hatches instead";
            } else if (getOwnerUUID() == null) {
                problem = "This controller has no owner (placed by a machine?): break and place it yourself. Sent to the hatches instead";
            } else if (modifier < 1.03f) {
                problem = "Wireless mode needs Molten Superconductor Base UMV; sent to the hatches instead";
            } else {
                long cap = 0;
                for (IEnergyContainer c : outs) cap = sat(cap, c.getEnergyCapacity());
                long sent = GTAAWireless.addEU(getOwnerUUID(), Math.min(eu, cap), this, true);
                euVoidedLastCycle = eu - sent;
                if (eu > cap) problem = "Over the hatches' capacity: the rest was voided (wireless is still capped by hatches)";
                return;
            }
        }
        // released into the hatches over the next 5 s (see release())
        pending = eu;
        release();
    }

    private void updateEfficiency(float value) {
        efficiency = value;
        if (history.size() < 10) history.add(value);
        else {
            history.set(historyIndex, value);
            historyIndex = (historyIndex + 1) % 10;
        }
        float sum = 0;
        for (float f : history) sum += f;
        avgEfficiency = history.isEmpty() ? 0 : sum / history.size();
    }

    //////////////////////////////////////
    // ***** UI *****
    //////////////////////////////////////

    private static Component line(String label, String value, ChatFormatting color) {
        return Component.literal(label).withStyle(ChatFormatting.GRAY).append(Component.literal(value).withStyle(color));
    }

    private static String eu(long v) {
        return com.omnicrox.gtaa.api.GTAAFormat.num(v) + " EU";
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(getRecipeLogic().isWorkingEnabled(), getRecipeLogic().isWorkingEnabled() && isFormed())
                .addCustom(t -> {
                    if (!isFormed()) return;
                    t.add(line("Next burst in ", String.format("%.1f s", ticksToCycle / 20f), ChatFormatting.WHITE));

                    t.add(Component.literal("Last burst").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE));
                    t.add(line(" Generated: ", eu(euLastCycle), ChatFormatting.GREEN));
                    t.add(line(" Average: ", com.omnicrox.gtaa.api.GTAAFormat.num(euLastCycle / CYCLE_TICKS) + " EU/t", ChatFormatting.GREEN));
                    t.add(line(" Used: ", com.omnicrox.gtaa.api.GTAAFormat.num(antimatterLastCycle) + " L antimatter + " +
                            com.omnicrox.gtaa.api.GTAAFormat.num(matterLastCycle) + " L " +
                            (matterNameLastCycle.isEmpty() ? "matter" : matterNameLastCycle), ChatFormatting.AQUA));
                    if (boostLastCycle > 0) t.add(line(" Boost: ", "x^" + boostLastCycle, ChatFormatting.YELLOW));
                    t.add(line(" Efficiency: ", Math.round(efficiency * 100) + "% (avg " + Math.round(avgEfficiency * 100) + "%)",
                            efficiency >= 0.999f ? ChatFormatting.GREEN : ChatFormatting.RED));
                    if (!wireless) t.add(line(" Delivered: ", eu(sentThisCycle) + " (" + com.omnicrox.gtaa.api.GTAAFormat.num(sentThisCycle / CYCLE_TICKS) + " EU/t)", ChatFormatting.GREEN));
                    if (euVoidedLastCycle > 0) t.add(line(" Voided: ", eu(euVoidedLastCycle), ChatFormatting.RED));

                    t.add(Component.literal("Inputs").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE));
                    t.add(line(" Fluid hatches: ", String.valueOf(inputHatches), ChatFormatting.WHITE));
                    t.add(line(" Antimatter: ", com.omnicrox.gtaa.api.GTAAFormat.num(antimatterStored) + " L", ChatFormatting.AQUA));
                    t.add(line(" Matter: ", com.omnicrox.gtaa.api.GTAAFormat.num(matterStored) + " L" +
                            (matterStoredName.isEmpty() ? "" : " (" + matterStoredName + ")"), ChatFormatting.AQUA));
                    if (fluidKinds > 2) t.add(Component.literal(" More than 2 fluids: the next burst will be voided!")
                            .withStyle(ChatFormatting.RED));

                    t.add(Component.literal("Output").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE));
                    t.add(line(" Destination: ", wireless ? "Wireless network" : "Dynamo / laser hatches",
                            wireless ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.WHITE));
                    t.add(line(" Hatches: ", hatchCount + ", hold " + eu(hatchCapacity) + " (" + eu(hatchStored) + " stored)",
                            ChatFormatting.WHITE));
                    if (!wireless && pending > 0) t.add(line(" Releasing: ", eu(pending) + " still to go", ChatFormatting.YELLOW));
                    if (wireless && euLastCycle > hatchCapacity && hatchCount > 0)
                        t.add(Component.literal(" Last burst was bigger than the hatches can hold").withStyle(ChatFormatting.RED));
                    if (wireless && !wirelessStored.isEmpty()) t.add(line(" Network: ", wirelessStored + " EU", ChatFormatting.LIGHT_PURPLE));
                    t.add(Component.literal(" Screwdriver the controller: switch hatches / wireless").withStyle(ChatFormatting.DARK_GRAY));

                    if (!problem.isEmpty()) t.add(Component.literal(problem).withStyle(ChatFormatting.RED));
                });
    }
}
