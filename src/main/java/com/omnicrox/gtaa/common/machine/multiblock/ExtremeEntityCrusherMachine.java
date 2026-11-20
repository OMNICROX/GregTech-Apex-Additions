package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.omnicrox.gtaa.common.infernal.InfernalMobs;
import com.omnicrox.gtaa.common.item.InfernalSigilItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Extreme Entity Crusher. Port of KubaTech's MTEExtremeEntityCrusher idea (GTNH, LGPL-3.0), simplified:
 * a spawn egg in an input bus picks the mob; every cycle the mob's loot table is rolled (with the looting level
 * of a sword in an input bus), many times in parallel. Power scales with the mob's health. Bosses are refused.
 * Infernal mode: an Elite / Ultra / Infernal Sigil in an input bus crushes that rank of the mob instead - its
 * health (and power) times the rank, loot rolled 3 / 5 / 9 times, and a quarter of them drop an infernal bonus.
 */
public class ExtremeEntityCrusherMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ExtremeEntityCrusherMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final int CYCLE = 100;
    public static final int MAX_PARALLEL = 256;
    public static final int MAX_LOOTING = 4;

    @Persisted private int progress;
    @DescSynced private String mobName = "";
    @DescSynced private int lastParallel;
    @DescSynced private long lastEut;
    @DescSynced private int infernalRank = -1;
    private TickableSubscription tick;

    public ExtremeEntityCrusherMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
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

    private List<NotifiableItemStackHandler> handlers(IO io) {
        List<NotifiableItemStackHandler> out = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != io) continue;
                for (var h : handler.getCapability(ItemRecipeCapability.CAP))
                    if (h instanceof NotifiableItemStackHandler inv) out.add(inv);
            }
        }
        return out;
    }

    private IEnergyContainer energy() {
        List<IEnergyContainer> list = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            for (var handler : part.getRecipeHandlers()) {
                if (handler.getHandlerIO() != IO.IN) continue;
                for (var h : handler.getCapability(EURecipeCapability.CAP))
                    if (h instanceof IEnergyContainer c) list.add(c);
            }
        }
        return new EnergyContainerList(list);
    }

    private static boolean isBoss(EntityType<?> type) {
        return type == EntityType.WITHER || type == EntityType.ENDER_DRAGON || type == EntityType.WARDEN ||
                type.getTags().anyMatch(t -> t.location().getPath().equals("bosses"));
    }

    private void work() {
        if (!(getLevel() instanceof ServerLevel level) || !isWorkingEnabled()) return;
        EntityType<?> type = null;
        ItemStack weapon = ItemStack.EMPTY;
        InfernalMobs.Rank rank = null;
        for (NotifiableItemStackHandler inv : handlers(IO.IN)) {
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack s = inv.getStackInSlot(i);
                if (type == null && s.getItem() instanceof SpawnEggItem egg) type = egg.getType(s.getTag());
                if (weapon.isEmpty() && s.getItem() instanceof SwordItem) weapon = s;
                if (s.getItem() instanceof InfernalSigilItem sigil && (rank == null || sigil.rank.ordinal() > rank.ordinal()))
                    rank = sigil.rank;
            }
        }
        infernalRank = rank == null ? -1 : rank.ordinal();
        if (type == null || isBoss(type)) {
            mobName = type == null ? "" : "Bosses can't be crushed";
            progress = 0;
            return;
        }
        var probe = type.create(level);
        if (!(probe instanceof LivingEntity living)) {
            mobName = "Not a mob";
            return;
        }
        mobName = type.getDescription().getString();
        float health = Math.max(1, living.getMaxHealth());
        // infernal mode: the mob has the rank's health, so it costs that much more to crush
        if (rank != null) health *= rank.mult;
        long baseEut = Math.max(GTValues.VA[rank == null ? GTValues.MV : GTValues.HV + rank.ordinal()], (long) (health * 64));
        IEnergyContainer energy = energy();
        long budget = energy.getInputVoltage() * Math.max(1, energy.getInputAmperage());
        int parallel = (int) Math.max(1, Math.min(MAX_PARALLEL, budget / baseEut));
        long eut = baseEut * parallel;
        if (energy.getEnergyStored() < eut) {
            lastEut = 0;
            return;
        }
        energy.removeEnergy(eut);
        lastEut = eut;
        lastParallel = parallel;
        if (++progress < CYCLE) return;
        progress = 0;

        var fake = FakePlayerFactory.getMinecraft(level);
        fake.setItemInHand(InteractionHand.MAIN_HAND, weapon.copy());
        int looting = Math.min(MAX_LOOTING, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, weapon));
        living.setPos(Vec3.atCenterOf(getPos()));
        LootTable table = level.getServer().getLootData().getLootTable(type.getDefaultLootTable());
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, living)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(getPos()))
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(fake))
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, fake)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, fake)
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, fake)
                .withLuck(looting)
                .create(LootContextParamSets.ENTITY);
        List<ItemStack> drops = new ArrayList<>();
        int rolls = rank == null ? 1 : 1 + rank.mult;
        for (int i = 0; i < parallel * rolls; i++) table.getRandomItems(params, drops::add);
        if (rank != null) {
            // bonus drop for one in four infernals, like the ones that spawn in the world
            for (int i = 0; i < parallel; i++)
                if (level.random.nextInt(4) == 0) drops.add(InfernalMobs.bonus(rank, level.random));
        }
        if (living instanceof Mob) {
            // equipment-free mobs only; GT doesn't simulate armor drops
        }
        fake.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        List<NotifiableItemStackHandler> outs = handlers(IO.OUT);
        for (ItemStack drop : drops) {
            ItemStack left = drop;
            for (NotifiableItemStackHandler inv : outs) {
                for (int i = 0; i < inv.getSlots() && !left.isEmpty(); i++) left = inv.insertItemInternal(i, left, false);
                if (left.isEmpty()) break;
            }
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.literal("Mob: " + (mobName.isEmpty() ? "put a spawn egg in an input bus" : mobName))
                .withStyle(ChatFormatting.GOLD));
        if (infernalRank >= 0) {
            var r = InfernalMobs.Rank.values()[infernalRank];
            textList.add(Component.literal("Infernal mode: " + r.title + " (loot x" + (1 + r.mult) + ", bonus drops)")
                    .withStyle(r.color));
        }
        if (lastEut > 0) textList.add(Component.literal("Using " + FormattingUtil.formatNumbers(lastEut) + " EU/t, " +
                lastParallel + " mobs per cycle").withStyle(ChatFormatting.AQUA));
    }
}
