package com.omnicrox.gtaa.common.machine.multiblock.tst;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.apexlib.multiblock.ApexHatches;
import com.omnicrox.apexlib.multiblock.ApexModifiers;
import com.omnicrox.apexlib.util.ApexFormat;
import com.omnicrox.gtaa.common.data.GTAAGTNHMachines;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bloody Hell (Twist Space Technology, by Taskeren and Goderium): mass-produces Blood Magic. Runs every Blood Altar,
 * Alchemy Table and Alchemy Array recipe (converted by apex_bloody_hell.js) with Life Essence from the input hatches.
 * Six structure tiers; parallels = tier^3; speed from the tier and the Speed Runes. Tiers 3-6 have a pool that must be
 * filled with Life Essence (done automatically from the input hatches) before anything runs.
 */
public class BloodyHellMachine extends WorkableElectricMultiblockMachine
        implements com.omnicrox.apexlib.multiblock.IStagedMulti {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BloodyHellMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final String CTX_SPEED = "BloodyHellSpeedRunes", CTX_SPEED2 = "BloodyHellSpeedRunes2",
            CTX_BLOOD = "BloodyHellPool";
    public static final String ALTAR = "bh_altar", ORB = "bh_orb";
    public static final int MAX_TIER = 6;
    private static final Map<ResourceLocation, Integer> ORBS = Map.of(
            new ResourceLocation("bloodmagic", "weakbloodorb"), 1,
            new ResourceLocation("bloodmagic", "apprenticebloodorb"), 2,
            new ResourceLocation("bloodmagic", "magicianbloodorb"), 3,
            new ResourceLocation("bloodmagic", "masterbloodorb"), 4,
            new ResourceLocation("bloodmagic", "archmagebloodorb"), 5);

    @DescSynced private int tier;
    @DescSynced private int speedRunes, tbSpeedRunes;
    @DescSynced private int poolSize, poolMissing;
    @DescSynced private int orbTier;
    @Persisted @DescSynced private boolean bloodFilled;

    private int matched = 1;
    private final List<BlockPos> pool = new ArrayList<>();
    private TickableSubscription tick;
    private Fluid lifeEssence;
    private Block lifeEssenceBlock;

    public BloodyHellMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    // ---------------- structure: tiers 1-6, the highest that matches wins ----------------
    @Override
    public boolean checkPattern() {
        for (int t = MAX_TIER; t >= 1; t--) {
            BlockPattern p = GTAAGTNHMachines.bloodyHellPattern(getDefinition(), t);
            if (p != null && p.checkPatternAt(getMultiblockState(), false)) {
                matched = t;
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockPattern getPattern() {
        return GTAAGTNHMachines.bloodyHellPattern(getDefinition(), matched);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        tier = matched;
        speedRunes = ApexModifiers.count(this, CTX_SPEED);
        tbSpeedRunes = ApexModifiers.count(this, CTX_SPEED2);
        pool.clear();
        Object o = getMultiblockState().getMatchContext().get(CTX_BLOOD);
        if (tier >= 3 && o instanceof Set<?> set)
            for (Object p : set) if (p instanceof Long l) pool.add(BlockPos.of(l));
        poolSize = pool.size();
        if (!isRemote()) tick = subscribeServerTick(tick, this::bloodTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        tier = 0;
        pool.clear();
        poolSize = 0;
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    public static Fluid lifeEssence() {
        Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("bloodmagic", "life_essence_fluid"));
        return f == null ? Fluids.EMPTY : f;
    }

    private Fluid le() {
        if (lifeEssence == null) lifeEssence = lifeEssence();
        return lifeEssence;
    }

    private Block leBlock() {
        if (lifeEssenceBlock == null) {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("bloodmagic", "life_essence_block"));
            lifeEssenceBlock = b == null ? Blocks.AIR : b;
        }
        return lifeEssenceBlock;
    }

    private boolean isBlood(BlockState s) {
        return s.getBlock() == leBlock() && s.getFluidState().isSource();
    }

    /** every second: the orb in the input buses, and the Life Essence pool (filled from the input hatches) */
    private void bloodTick() {
        if (getOffsetTimer() % 20 != 0) return;
        orbTier = findOrbTier();
        if (pool.isEmpty()) {
            bloodFilled = true;
            poolMissing = 0;
            return;
        }
        Level level = getLevel();
        if (level == null || le() == Fluids.EMPTY || leBlock() == Blocks.AIR) return;
        List<BlockPos> missing = new ArrayList<>();
        for (BlockPos p : pool) {
            if (!level.isLoaded(p)) return;
            if (!isBlood(level.getBlockState(p))) missing.add(p);
        }
        poolMissing = missing.size();
        if (missing.isEmpty()) {
            bloodFilled = true;
            return;
        }
        bloodFilled = false;
        // fill the pool from the input hatches, 1000 mB of Life Essence per block, up to 64 blocks a second (a big pool
        // needs far more than one hatch holds, so it fills over time instead of waiting for all of it at once)
        BlockState blood = leBlock().defaultBlockState();
        int placed = 0;
        for (BlockPos p : missing) {
            if (placed >= 64) break;
            BlockState s = level.getBlockState(p);
            if (!s.isAir() && !s.canBeReplaced() && !s.liquid()) continue;
            if (!ApexHatches.drain(this, new FluidStack(le(), 1000))) break;
            level.setBlock(p, blood, Block.UPDATE_ALL);
            placed++;
        }
        poolMissing = missing.size() - placed;
        bloodFilled = poolMissing == 0;
    }

    private int findOrbTier() {
        int best = 0;
        for (NotifiableItemStackHandler bus : ApexHatches.itemHandlers(this, IO.IN))
            for (int i = 0; i < bus.getSlots(); i++) {
                ItemStack s = bus.getStackInSlot(i);
                if (s.isEmpty()) continue;
                Integer t = ORBS.get(ForgeRegistries.ITEMS.getKey(s.getItem()));
                if (t != null) best = Math.max(best, t);
            }
        return best;
    }

    /** the altar this machine counts as: structure tier + 1 (Twist), capped at Blood Magic's tier 6 */
    public int altarTier() {
        return Math.min(6, tier + 1);
    }

    public double tierSpeedBonus() {
        return (tier - 2) * 0.5;
    }

    /** Taskeren's curve: 35 x (1 - e^(-runes / 500)); a Reinforced Speed Rune counts as 3 */
    public double runeSpeedBonus() {
        return 35 * (1 - Math.exp(-(speedRunes + 3.0 * tbSpeedRunes) / 500.0));
    }

    public double durationMultiplier() {
        return 1.0 / ((1.0 + tierSpeedBonus()) * (1.0 + runeSpeedBonus()));
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof BloodyHellMachine bh) || bh.tier <= 0) return ModifierFunction.NULL;
        if (!bh.bloodFilled) return ModifierFunction.cancel(Component.literal("Fill the blood pool with Life Essence"));
        int altar = recipe.data.getInt(ALTAR);
        if (altar > bh.altarTier())
            return ModifierFunction.cancel(Component.literal("Needs a tier " + altar + " altar (structure tier " + (altar - 1) + ")"));
        int orb = recipe.data.getInt(ORB);
        if (orb > bh.orbTier) return ModifierFunction.cancel(Component.literal("Needs a tier " + orb + " Blood Orb in an input bus"));
        int t = bh.tier;
        return ApexModifiers.parallel(m -> t * t * t, m -> bh.durationMultiplier(), 1.0).getModifier(machine, recipe);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Structure tier " + tier + " (altar tier " + altarTier() + "): " +
                tier * tier * tier + " parallels").withStyle(ChatFormatting.DARK_RED));
        textList.add(Component.literal("Speed Runes: " + speedRunes + ", Reinforced: " + tbSpeedRunes + " - " +
                ApexFormat.num(Math.round(100 / durationMultiplier())) + "% speed").withStyle(ChatFormatting.RED));
        textList.add(Component.literal("Blood Orb: " + (orbTier == 0 ? "none" : "tier " + orbTier))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        if (poolSize > 0) textList.add(bloodFilled ?
                Component.literal("Blood pool full (" + poolSize + " blocks)").withStyle(ChatFormatting.DARK_RED) :
                Component.literal("Blood pool: needs " + ApexFormat.num(poolMissing * 1000L) +
                        " mB of Life Essence in an input hatch").withStyle(ChatFormatting.YELLOW));
    }

    // ---- Structure Upgrader: the six tiers ----
    @Override public int stageCount() { return MAX_TIER; }
    @Override public int currentStage() { return isFormed() ? tier : 0; }
    @Override public BlockPattern stagePattern(int stage) { return GTAAGTNHMachines.bloodyHellPattern(getDefinition(), stage); }
    @Override public String stageName(int stage) { return "Tier " + stage; }
    @Override public String stageAdds(int stage) {
        return stage * stage * stage + " parallels, altar tier " + Math.min(6, stage + 1) + (stage == 3 ? ", the blood pool" : "");
    }
}
