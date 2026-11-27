package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.machine.part.ManaHatchPartMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Mana Nexus: stores mana and does every Botania altar craft (mana pool infusion, runic altar, terra plate, elven
 * trade, pure daisy, petal apothecary, Mythic Botany's Mana Infuser and rune rituals) with it.
 * <ul>
 * <li>ten Mana Cores, all the same kind, set the tier: Manasteel 1, Terrasteel 2, Elementium 3, Alfsteel 4,
 * Gaiasteel 5. Tier sets storage, parallels and which crafts it can do.</li>
 * <li>mana comes in through Mana Hatches (spreaders and sparks) and as liquid mana (Mana Fluid, 1 mB = 100 mana)
 * in its fluid input hatches.</li>
 * <li>a craft takes its mana when it starts; voltage sets speed like any GT machine.</li>
 * </ul>
 */
public class ManaNexusMachine extends WorkableElectricMultiblockMachine implements com.omnicrox.apexlib.multiblock.IStagedMulti {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ManaNexusMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final String CTX_CORE = "ManaNexusCore";
    public static final String MANA = "mana", TIER = "nexus_tier";
    public static final long[] CAPACITY = { 0, 10_000_000L, 50_000_000L, 250_000_000L, 1_000_000_000L, 2_000_000_000L };
    public static final int[] PARALLELS = { 0, 1, 2, 4, 8, 16 };
    public static final String[] CORE_NAMES = { "", "Manasteel", "Terrasteel", "Elementium", "Alfsteel", "Gaiasteel" };
    public static final String[] TIER_CRAFTS = { "",
            "mana pool infusion, runic altar, pure daisy, petal apothecary",
            "+ terrestrial agglomeration, elven trade",
            "+ Mana Infuser (Mythic Botany)",
            "+ rune rituals (Mythic Botany)",
            "everything, 2B mana, 16 at once" };
    public static final int MANA_PER_MB = 100;

    @Persisted @DescSynced private long storedMana;
    @DescSynced private int coreTier;
    @DescSynced private int stage;
    private int trying;
    private static final com.gregtechceu.gtceu.api.pattern.BlockPattern[] STAGE_PATTERNS =
            new com.gregtechceu.gtceu.api.pattern.BlockPattern[ManaNexusShapes.STAGES + 1];
    private TickableSubscription tick;
    private Fluid liquidMana;

    public ManaNexusMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate cores() {
        return QuantumForceTransformerMachine.tiered(CTX_CORE, GTAABlocks::manaCores, "Mana Cores");
    }

    public int getCoreTier() {
        return coreTier;
    }

    /** the tier everything runs at: the lower of the growth stage and the Mana Cores */
    public int tier() {
        return Math.max(0, Math.min(5, Math.min(coreTier, stage)));
    }

    // ---- growth stages ----

    @Override
    public int stageCount() {
        return ManaNexusShapes.STAGES;
    }

    @Override
    public int currentStage() {
        return isFormed() ? stage : 0;
    }

    @Override
    public com.gregtechceu.gtceu.api.pattern.BlockPattern stagePattern(int st) {
        int i = Math.max(1, Math.min(ManaNexusShapes.STAGES, st));
        if (STAGE_PATTERNS[i] == null)
            STAGE_PATTERNS[i] = com.omnicrox.gtaa.common.data.GTAAMachines.nexusStage(
                    com.omnicrox.gtaa.common.data.GTAAMachines.MANA_NEXUS, i).build();
        return STAGE_PATTERNS[i];
    }

    @Override
    public String stageName(int st) {
        return ManaNexusShapes.STAGE_NAMES[Math.max(1, Math.min(5, st))];
    }

    @Override
    public String stageAdds(int st) {
        return ManaNexusShapes.STAGE_ADDS[Math.max(1, Math.min(5, st))];
    }

    @Override
    public com.gregtechceu.gtceu.api.pattern.BlockPattern getPattern() {
        return stagePattern(trying > 0 ? trying : Math.max(1, stage));
    }

    /** the biggest stage that's built wins */
    @Override
    public boolean checkPattern() {
        for (int st = ManaNexusShapes.STAGES; st >= 1; st--) {
            trying = st;
            boolean ok = stagePattern(st).checkPatternAt(getMultiblockState(), false);
            if (ok) {
                stage = st;
                trying = 0;
                return true;
            }
        }
        trying = 0;
        return false;
    }

    public long getStoredMana() {
        return storedMana;
    }

    public long capacity() {
        return CAPACITY[tier()];
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Object t = getMultiblockState().getMatchContext().get(CTX_CORE);
        coreTier = t instanceof Integer i ? i : 0;
        if (!isRemote()) tick = subscribeServerTick(tick, this::pullMana);
    }



    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        coreTier = 0;
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    private Fluid liquidMana() {
        if (liquidMana == null) {
            Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("manafluid", "mana"));
            liquidMana = f == null ? net.minecraft.world.level.material.Fluids.EMPTY : f;
        }
        return liquidMana;
    }

    /** every tick: empty the Mana Hatches and the liquid mana in the input hatches into storage */
    private void pullMana() {
        long space = capacity() - storedMana;
        if (space <= 0) return;
        for (IMultiPart part : getParts()) {
            if (space <= 0) break;
            if (part instanceof ManaHatchPartMachine hatch) {
                long got = hatch.extract(space);
                storedMana += got;
                space -= got;
            }
        }
        Fluid mana = liquidMana();
        if (mana == net.minecraft.world.level.material.Fluids.EMPTY || space < MANA_PER_MB) return;
        for (IMultiPart part : getParts())
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(FluidRecipeCapability.CAP)) {
                    if (!(h instanceof NotifiableFluidTank tank)) continue;
                    for (int i = 0; i < tank.getTanks() && space >= MANA_PER_MB; i++) {
                        FluidStack f = tank.getFluidInTank(i);
                        if (f.isEmpty() || f.getFluid() != mana) continue;
                        int mb = (int) Math.min(f.getAmount(), space / MANA_PER_MB);
                        FluidStack got = tank.drainInternal(new FluidStack(f, mb), IFluidHandler.FluidAction.EXECUTE);
                        storedMana += (long) got.getAmount() * MANA_PER_MB;
                        space -= (long) got.getAmount() * MANA_PER_MB;
                    }
                }
            }
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof ManaNexusMachine nexus)) return ModifierFunction.NULL;
        int need = Math.max(1, recipe.data.getInt(TIER));
        if (need > nexus.coreTier) return ModifierFunction.cancel(Component.literal(
                "Needs " + CORE_NAMES[Math.min(5, need)] + " Mana Cores"));
        if (need > nexus.stage) return ModifierFunction.cancel(Component.literal(
                "Needs the " + ManaNexusShapes.STAGE_NAMES[Math.min(5, need)] + " stage"));
        long mana = recipe.data.getLong(MANA);
        int max = PARALLELS[Math.max(1, nexus.tier())];
        if (mana > 0) {
            long affordable = nexus.storedMana / mana;
            if (affordable <= 0) return ModifierFunction.cancel(Component.literal("Needs " + com.omnicrox.apexlib.util.ApexFormat.num(mana) + " mana"));
            max = (int) Math.min(max, affordable);
        }
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, max);
        if (parallels <= 0) return ModifierFunction.NULL;
        if (parallels == 1) return ModifierFunction.IDENTITY;
        return ModifierFunction.builder().modifyAllContents(ContentModifier.multiplier(parallels)).parallels(parallels).build();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe != null) {
            long need = recipe.data.getLong(MANA) * Math.max(1, recipe.getTotalRuns());
            if (need > storedMana) return false;
            storedMana -= need;
        }
        return super.beforeWorking(recipe);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        int t = tier();
        textList.add(Component.literal("Mana " + com.omnicrox.apexlib.util.ApexFormat.num(storedMana) + " / " +
                com.omnicrox.apexlib.util.ApexFormat.num(capacity())).withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("Tier " + t + "  |  " + ManaNexusShapes.STAGE_NAMES[Math.max(1, stage)] + " stage, " +
                CORE_NAMES[Math.max(0, Math.min(5, coreTier))] + " cores  |  " + PARALLELS[Math.max(1, t)] + "x").withStyle(ChatFormatting.LIGHT_PURPLE));
        if (stage != coreTier) textList.add(Component.literal(stage < coreTier
                ? "Grow it to the " + ManaNexusShapes.STAGE_NAMES[Math.min(5, stage + 1)] + " stage to use your cores"
                : "Better cores would raise the tier").withStyle(ChatFormatting.YELLOW));
    }

    /** for the Structure Upgrader */
    public static List<String> coreInfo(int tier) {
        List<String> l = new ArrayList<>();
        int t = Math.max(1, Math.min(5, tier));
        l.add(com.omnicrox.apexlib.util.ApexFormat.num(CAPACITY[t]) + " mana, " + PARALLELS[t] + " crafts at once; " + TIER_CRAFTS[t]);
        return l;
    }
}
