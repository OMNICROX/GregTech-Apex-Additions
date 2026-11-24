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

import com.omnicrox.gtaa.common.infernal.InfernalConfig;
import com.omnicrox.gtaa.common.infernal.InfernalMobs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Enemy;
import java.util.HashMap;
import java.util.Map;
import com.omnicrox.gtaa.common.item.InfernalSigilItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Extreme Entity Crusher. Port of KubaTech's MTEExtremeEntityCrusher idea (GTNH, LGPL-3.0), simplified:
 * a spawn egg in an input bus picks the mob; every cycle the mob's loot table is rolled (with the looting level
 * of a sword in an input bus), many times in parallel. Power scales with the mob's health. Bosses are refused.
 * A Hostile Neural Networks data model works like an egg; its tier adds bonus loot rolls (its accuracy).
 * Infernals: every hostile mob crushed has the world's 1 in 20 chance to be infernal (always for blazes, witches...)
 * and then drops the infernal loot for its modifier count. A Rare Sigil makes every mob infernal; Ultra / Infernal
 * Sigils also raise the odds of the higher ranks (Rare drops still happen).
 */
public class ExtremeEntityCrusherMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ExtremeEntityCrusherMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final int CYCLE = 100;
    public static final int MAX_PARALLEL = 256;
    public static final int MAX_LOOTING = 4;
    public static final int EU_PER_HEALTH = 4;

    @Persisted private double cycleProgress;
    @DescSynced private String mobName = "";
    @DescSynced private int lastParallel;
    @DescSynced private long lastEut;
    @DescSynced private int infernalRank = -1;
    @DescSynced private int modelTier = -1;
    @DescSynced private int lastInfernals;
    @DescSynced private long lastNeed;
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

    // ---- Hostile Neural Networks data models: hostilenetworks:data_model {data_model:{id:"ns:path", data:N}}
    private static final Map<ResourceLocation, EntityType<?>> HNN_CACHE = new HashMap<>();
    private static final int[] HNN_TIER_DATA = { 0, 6, 54, 354, 1254 };
    private static final float[] HNN_ACCURACY = { 0f, 0.05f, 0.22f, 0.65f, 0.995f };
    private static final String[] HNN_TIER_NAME = { "Faulty", "Basic", "Advanced", "Superior", "Self Aware" };

    private static boolean isDataModel(ItemStack s) {
        var key = BuiltInRegistries.ITEM.getKey(s.getItem());
        return key.getNamespace().equals("hostilenetworks") && key.getPath().equals("data_model");
    }

    private static int hnnTier(ItemStack s) {
        CompoundTag t = s.getTagElement("data_model");
        int data = t == null ? 0 : t.getInt("data");
        int tier = 0;
        for (int i = 0; i < HNN_TIER_DATA.length; i++) if (data >= HNN_TIER_DATA[i]) tier = i;
        return tier;
    }

    private static EntityType<?> hnnEntity(ServerLevel level, ItemStack s) {
        CompoundTag t = s.getTagElement("data_model");
        if (t == null || !t.contains("id")) return null;
        ResourceLocation id = ResourceLocation.tryParse(t.getString("id"));
        if (id == null) return null;
        return HNN_CACHE.computeIfAbsent(id, k -> {
            try {
                var res = level.getServer().getResourceManager()
                        .getResource(new ResourceLocation(k.getNamespace(), "data_models/" + k.getPath() + ".json"));
                if (res.isEmpty()) return null;
                try (var reader = res.get().openAsReader()) {
                    var json = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
                    return BuiltInRegistries.ENTITY_TYPE.getOptional(new ResourceLocation(json.get("entity").getAsString())).orElse(null);
                }
            } catch (Exception e) {
                return null;
            }
        });
    }

    /** sigil rank -> {ultra rarity, inferno rarity}; no sigil = the natural spawn odds and only a 1 in 20 chance */
    private static int[] odds(InfernalMobs.Rank sigil) {
        if (sigil == null || sigil == InfernalMobs.Rank.ELITE)
            return new int[] { InfernalConfig.ULTRA_RARITY.get(), InfernalConfig.INFERNO_RARITY.get() };
        return sigil == InfernalMobs.Rank.ULTRA ? new int[] { 3, 7 } : new int[] { 2, 3 };
    }

    private void work() {
        if (!(getLevel() instanceof ServerLevel level) || !isWorkingEnabled()) return;
        EntityType<?> type = null;
        ItemStack weapon = ItemStack.EMPTY;
        InfernalMobs.Rank rank = null;
        int modelTier = -1;
        for (NotifiableItemStackHandler inv : handlers(IO.IN)) {
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack s = inv.getStackInSlot(i);
                if (s.isEmpty()) continue;
                if (type == null && s.getItem() instanceof SpawnEggItem egg) type = egg.getType(s.getTag());
                if (type == null && isDataModel(s)) {
                    type = hnnEntity(level, s);
                    if (type != null) modelTier = hnnTier(s);
                }
                if (weapon.isEmpty() && s.getItem() instanceof SwordItem) weapon = s;
                if (s.getItem() instanceof InfernalSigilItem sigil && (rank == null || sigil.rank.ordinal() > rank.ordinal()))
                    rank = sigil.rank;
            }
        }
        infernalRank = rank == null ? -1 : rank.ordinal();
        this.modelTier = modelTier;
        if (type == null || isBoss(type)) {
            mobName = type == null ? "" : "Bosses can't be crushed";
            cycleProgress = 0;
            return;
        }
        var probe = type.create(level);
        if (!(probe instanceof LivingEntity living)) {
            mobName = "Not a mob";
            return;
        }
        mobName = type.getDescription().getString();
        boolean hostile = probe instanceof Enemy;
        boolean forced = InfernalMobs.alwaysInfernal(type);
        float health = Math.max(1, living.getMaxHealth());
        // a sigil makes every mob infernal, and infernals have (modifiers x 1.8) times the health: budget for the average
        float healthMult = 1f;
        if (hostile && (rank != null || forced)) healthMult = rank == null || rank == InfernalMobs.Rank.ELITE ? 6.5f :
                rank == InfernalMobs.Rank.ULTRA ? 9f : 12f;
        long baseEut = Math.max(GTValues.VA[GTValues.LV], (long) (health * healthMult * EU_PER_HEALTH));
        IEnergyContainer energy = energy();
        long budget = energy.getInputVoltage() * Math.max(1, energy.getInputAmperage());
        int parallel = (int) Math.max(1, Math.min(MAX_PARALLEL, budget / baseEut));
        long need = baseEut * parallel;
        // not enough power for even one mob: run slower instead of not at all
        long eut = Math.min(need, Math.max(1, budget));
        if (energy.getEnergyStored() < eut) {
            lastEut = 0;
            return;
        }
        energy.removeEnergy(eut);
        lastEut = eut;
        lastNeed = need;
        lastParallel = parallel;
        cycleProgress += (double) eut / need;
        if (cycleProgress < CYCLE) return;
        cycleProgress = 0;

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
        float bonus = modelTier < 0 ? 0f : HNN_ACCURACY[modelTier];
        int[] odds = odds(rank);
        int infernals = 0;
        for (int i = 0; i < parallel; i++) {
            table.getRandomItems(params, drops::add);
            if (bonus > 0 && level.random.nextFloat() < bonus) table.getRandomItems(params, drops::add);
            if (!hostile || !InfernalConfig.ENABLED.get()) continue;
            boolean infernal = rank != null || forced || level.random.nextInt(InfernalConfig.ELITE_RARITY.get()) == 0;
            if (!infernal) continue;
            infernals++;
            drops.addAll(InfernalMobs.infernalLoot(InfernalMobs.rollModCount(level.random, odds[0], odds[1]), level.random));
        }
        lastInfernals = infernals;
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
        if (lastEut > 0 && lastEut < (long) lastNeed) textList.add(Component.literal("Underpowered: running at " +
                Math.round(100.0 * lastEut / lastNeed) + "% speed").withStyle(ChatFormatting.RED));
        textList.add(Component.literal("Mob: " + (mobName.isEmpty() ? "put a spawn egg or data model in an input bus" : mobName))
                .withStyle(ChatFormatting.GOLD));
        if (modelTier >= 0) textList.add(Component.literal("Data model: " + HNN_TIER_NAME[modelTier] + " (+" +
                Math.round(HNN_ACCURACY[modelTier] * 100) + "% loot rolls)").withStyle(ChatFormatting.AQUA));
        if (infernalRank >= 0) {
            var r = InfernalMobs.Rank.values()[infernalRank];
            String odds = switch (r) {
                case ELITE -> "every mob infernal";
                case ULTRA -> "every mob infernal, Ultra 1 in 3";
                case INFERNAL -> "every mob infernal, Ultra 1 in 2, Infernal 1 in 6";
            };
            textList.add(Component.literal(r.title + " Sigil: " + odds).withStyle(r.color));
        }
        if (lastEut > 0) textList.add(Component.literal("Using " + com.omnicrox.gtaa.api.GTAAFormat.num(lastEut) + " EU/t, " +
                lastParallel + " mobs per cycle" + (lastInfernals > 0 ? ", " + lastInfernals + " infernal last cycle" : ""))
                .withStyle(ChatFormatting.AQUA));
    }
}
