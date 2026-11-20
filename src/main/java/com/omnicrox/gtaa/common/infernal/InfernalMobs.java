package com.omnicrox.gtaa.common.infernal;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Infernal mobs for Apex Ascension: a clean-room take on the classic Infernal Mobs idea (GT New Horizons has
 * a fork of it). A small share of hostile spawns become Elite / Ultra / Infernal with 2-3 / 4-6 / 7-9 random
 * modifiers, x2 / x4 / x8 health, and their loot rolled 2 / 4 / 8 extra times plus a bonus drop. That makes
 * farms around them (an infernal witch farm for early redstone and glowstone) worth building.
 */
public final class InfernalMobs {

    public static final String NBT = "gtapexadditions_infernal";
    private static final String ROLLED = "gtapexadditions_infernal_rolled";
    private static final UUID HEALTH_ID = UUID.fromString("6b1c9c84-37b0-4a5e-9a5b-5a3b7e0a6f11");
    private static final UUID SPEED_ID = UUID.fromString("6b1c9c84-37b0-4a5e-9a5b-5a3b7e0a6f12");
    private static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", "bosses"));
    private static final TagKey<EntityType<?>> BLACKLIST = TagKey.create(Registries.ENTITY_TYPE,
            new ResourceLocation("gtapexadditions", "infernal_blacklist"));

    public enum Rank {
        ELITE("Elite", ChatFormatting.YELLOW, 2, 2, 3),
        ULTRA("Ultra", ChatFormatting.GOLD, 4, 4, 6),
        INFERNAL("Infernal", ChatFormatting.DARK_RED, 8, 7, 9);

        public final String title;
        public final ChatFormatting color;
        public final int mult, min, max;

        Rank(String title, ChatFormatting color, int mult, int min, int max) {
            this.title = title;
            this.color = color;
            this.mult = mult;
            this.min = min;
            this.max = max;
        }
    }

    public record Infernal(Rank rank, EnumSet<InfernalModifier> mods) {
        public boolean has(InfernalModifier m) {
            return mods.contains(m);
        }
    }

    private static final Map<LivingEntity, Infernal> ACTIVE = Collections.synchronizedMap(new WeakHashMap<>());

    private InfernalMobs() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(InfernalMobs.class);
    }

    public static Infernal get(LivingEntity e) {
        return ACTIVE.get(e);
    }

    // ------------------------------------------------------------------ spawning

    private static boolean eligible(Mob mob, Level level) {
        if (!(mob instanceof Enemy)) return false;
        EntityType<?> type = mob.getType();
        if (type.is(BOSSES) || type.is(BLACKLIST) || type == EntityType.WITHER || type == EntityType.ENDER_DRAGON ||
                type == EntityType.WARDEN || type == EntityType.ELDER_GUARDIAN) return false;
        String id = String.valueOf(EntityType.getKey(type));
        if (InfernalConfig.MOB_BLACKLIST.get().contains(id)) return false;
        return !InfernalConfig.DIMENSION_BLACKLIST.get().contains(level.dimension().location().toString());
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!InfernalConfig.FROM_SPAWNERS.get() && event.getSpawnType() == MobSpawnType.SPAWNER) {
            event.getEntity().getPersistentData().putBoolean(ROLLED, true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob)) return;
        CompoundTag data = mob.getPersistentData();
        if (data.contains(NBT)) {
            Infernal inf = load(data.getCompound(NBT));
            if (inf != null) ACTIVE.put(mob, inf);
            return;
        }
        if (data.getBoolean(ROLLED)) return;
        data.putBoolean(ROLLED, true);
        if (!InfernalConfig.ENABLED.get() || !eligible(mob, event.getLevel())) return;
        RandomSource r = mob.getRandom();
        if (r.nextInt(InfernalConfig.CHANCE.get()) != 0) return;
        make(mob, null);
    }

    /** Turns a mob infernal. rank null = roll one. */
    public static Infernal make(Mob mob, Rank rank) {
        RandomSource r = mob.getRandom();
        if (rank == null) {
            int roll = r.nextInt(100);
            int inf = InfernalConfig.INFERNAL_PERCENT.get();
            rank = roll < inf ? Rank.INFERNAL : roll < inf + InfernalConfig.ULTRA_PERCENT.get() ? Rank.ULTRA : Rank.ELITE;
        }
        List<InfernalModifier> pool = new ArrayList<>();
        for (InfernalModifier m : InfernalModifier.values())
            if (!InfernalConfig.DISABLED_MODIFIERS.get().contains(m.id())) pool.add(m);
        Collections.shuffle(pool, new java.util.Random(r.nextLong()));
        int n = Math.min(pool.size(), rank.min + r.nextInt(rank.max - rank.min + 1));
        EnumSet<InfernalModifier> mods = EnumSet.noneOf(InfernalModifier.class);
        mods.addAll(pool.subList(0, n));
        Infernal inf = new Infernal(rank, mods);
        mob.getPersistentData().put(NBT, save(inf));
        apply(mob, inf);
        ACTIVE.put(mob, inf);
        return inf;
    }

    private static void apply(Mob mob, Infernal inf) {
        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null && hp.getModifier(HEALTH_ID) == null) {
            hp.addPermanentModifier(new AttributeModifier(HEALTH_ID, "Infernal health", inf.rank.mult - 1,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
            mob.setHealth(mob.getMaxHealth());
        }
        if (inf.has(InfernalModifier.SWIFT)) {
            AttributeInstance sp = mob.getAttribute(Attributes.MOVEMENT_SPEED);
            if (sp != null && sp.getModifier(SPEED_ID) == null)
                sp.addPermanentModifier(new AttributeModifier(SPEED_ID, "Infernal speed", 0.4,
                        AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (inf.has(InfernalModifier.FIERY)) mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false));
        mob.setPersistenceRequired();
        mob.setCustomName(title(mob, inf));
    }

    public static Component title(LivingEntity mob, Infernal inf) {
        MutableComponent c = Component.empty();
        int shown = 0;
        for (InfernalModifier m : inf.mods) {
            if (shown++ >= 3) break;
            c.append(Component.literal(m.title + " ").withStyle(m.color));
        }
        c.append(Component.literal(inf.rank.title + " ").withStyle(inf.rank.color, ChatFormatting.BOLD));
        c.append(mob.getType().getDescription().copy().withStyle(inf.rank.color));
        if (inf.mods.size() > 3) c.append(Component.literal(" +" + (inf.mods.size() - 3)).withStyle(ChatFormatting.GRAY));
        return c;
    }

    private static CompoundTag save(Infernal inf) {
        CompoundTag t = new CompoundTag();
        t.putString("rank", inf.rank.name());
        ListTag list = new ListTag();
        for (InfernalModifier m : inf.mods) list.add(StringTag.valueOf(m.name()));
        t.put("mods", list);
        return t;
    }

    private static Infernal load(CompoundTag t) {
        try {
            Rank rank = Rank.valueOf(t.getString("rank"));
            EnumSet<InfernalModifier> mods = EnumSet.noneOf(InfernalModifier.class);
            for (Tag s : t.getList("mods", Tag.TAG_STRING)) {
                try {
                    mods.add(InfernalModifier.valueOf(s.getAsString()));
                } catch (IllegalArgumentException ignored) {}
            }
            return new Infernal(rank, mods);
        } catch (Exception e) {
            return null;
        }
    }

    // ------------------------------------------------------------------ behaviour

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity mob = event.getEntity();
        if (mob.level().isClientSide() || mob.tickCount % 20 != 0) return;
        Infernal inf = ACTIVE.get(mob);
        if (inf == null || !mob.isAlive()) return;
        ServerLevel level = (ServerLevel) mob.level();
        level.sendParticles(inf.rank == Rank.INFERNAL ? ParticleTypes.SOUL_FIRE_FLAME : inf.rank == Rank.ULTRA ?
                ParticleTypes.FLAME : ParticleTypes.ENCHANT, mob.getX(), mob.getY() + mob.getBbHeight() * 0.6, mob.getZ(),
                4, 0.3, 0.4, 0.3, 0.01);
        LivingEntity target = mob instanceof Mob m ? m.getTarget() : null;
        if (inf.has(InfernalModifier.REGENERATING) && mob.getHealth() < mob.getMaxHealth())
            mob.heal(Math.max(1, mob.getMaxHealth() * 0.02f));
        if (inf.has(InfernalModifier.CLOAKING) && mob.getRandom().nextInt(4) == 0)
            mob.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, false));
        if (inf.has(InfernalModifier.MAGNETIC)) {
            for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(8))) {
                Vec3 pull = mob.position().subtract(item.position()).normalize().scale(0.4);
                item.setDeltaMovement(item.getDeltaMovement().add(pull));
            }
        }
        if (target == null || !target.isAlive() || target.distanceTo(mob) > 16) return;
        if (inf.has(InfernalModifier.GRAVITY) && mob.getRandom().nextInt(3) == 0 && target.distanceTo(mob) > 3) {
            Vec3 pull = mob.position().subtract(target.position()).normalize().scale(0.9);
            target.setDeltaMovement(target.getDeltaMovement().add(pull.x, 0.3, pull.z));
            target.hurtMarked = true;
        }
        if (inf.has(InfernalModifier.STORM) && mob.getRandom().nextInt(8) == 0 && level.canSeeSky(target.blockPosition())) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt != null) {
                bolt.moveTo(target.position());
                bolt.setVisualOnly(false);
                level.addFreshEntity(bolt);
            }
        }
        if (inf.has(InfernalModifier.IRRADIATED) && target.distanceTo(mob) < 5) {
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
        if (inf.has(InfernalModifier.ENDER) && mob.getRandom().nextInt(10) == 0 && target.distanceTo(mob) > 6) {
            Vec3 behind = target.position().add(target.getLookAngle().scale(-2));
            mob.teleportTo(behind.x, target.getY(), behind.z);
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) return;
        DamageSource src = event.getSource();
        Entity attackerEntity = src.getEntity();

        // infernal attacking
        if (attackerEntity instanceof LivingEntity attacker) {
            Infernal inf = ACTIVE.get(attacker);
            if (inf != null && src.getDirectEntity() == attacker) {
                float dmg = event.getAmount();
                if (inf.has(InfernalModifier.BERSERK)) event.setAmount(dmg *= 2);
                if (inf.has(InfernalModifier.VAMPIRIC)) attacker.heal(dmg * 0.5f);
                if (inf.has(InfernalModifier.BLASTOFF) && attacker.getRandom().nextInt(3) == 0) {
                    victim.setDeltaMovement(victim.getDeltaMovement().add(0, 1.1, 0));
                    victim.hurtMarked = true;
                }
                if (inf.has(InfernalModifier.STICKY)) victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3));
                if (inf.has(InfernalModifier.WITHERING)) victim.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
                if (inf.has(InfernalModifier.POISONOUS)) victim.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                if (inf.has(InfernalModifier.FIERY)) victim.setSecondsOnFire(5);
                if (inf.has(InfernalModifier.FREEZING)) {
                    victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
                    victim.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 1));
                    victim.setTicksFrozen(Math.min(victim.getTicksRequiredToFreeze() + 40, victim.getTicksFrozen() + 60));
                }
                if (inf.has(InfernalModifier.BLINDING)) victim.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                if (inf.has(InfernalModifier.RUSTING) && victim instanceof Player p) {
                    ItemStack held = p.getMainHandItem();
                    if (held.isDamageableItem()) held.hurtAndBreak(4, p, pl -> {});
                }
            }
        }

        // infernal being hit
        Infernal inf = ACTIVE.get(victim);
        if (inf == null) return;
        if (inf.has(InfernalModifier.ARMORED)) event.setAmount(event.getAmount() * 0.5f);
        if (inf.has(InfernalModifier.BERSERK)) event.setAmount(event.getAmount() * 1.25f);
        if (inf.has(InfernalModifier.THORNY) && attackerEntity instanceof LivingEntity a && a != victim &&
                src.getDirectEntity() == a)
            a.hurt(victim.damageSources().thorns(victim), event.getAmount() * 0.3f);
        if (inf.has(InfernalModifier.ENDER) && victim.getRandom().nextInt(4) == 0 && victim instanceof Mob m) {
            for (int i = 0; i < 8; i++) {
                double x = m.getX() + (m.getRandom().nextDouble() - 0.5) * 12;
                double z = m.getZ() + (m.getRandom().nextDouble() - 0.5) * 12;
                if (m.randomTeleport(x, m.getY() + m.getRandom().nextInt(5) - 2, z, true)) break;
            }
        }
    }

    // ------------------------------------------------------------------ death and loot

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity mob = event.getEntity();
        Infernal inf = ACTIVE.get(mob);
        if (inf == null || mob.level().isClientSide()) return;
        if (inf.has(InfernalModifier.UNSTABLE))
            mob.level().explode(mob, mob.getX(), mob.getY(), mob.getZ(), 2.5f, Level.ExplosionInteraction.NONE);
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        LivingEntity mob = event.getEntity();
        Infernal inf = ACTIVE.get(mob);
        if (inf == null || !(mob.level() instanceof ServerLevel level)) return;
        // extra rolls of the mob's own loot
        try {
            LootTable table = level.getServer().getLootData().getLootTable(mob.getLootTable());
            LootParams.Builder b = new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, mob)
                    .withParameter(LootContextParams.ORIGIN, mob.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, event.getSource())
                    .withOptionalParameter(LootContextParams.KILLER_ENTITY, event.getSource().getEntity())
                    .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, event.getSource().getDirectEntity());
            if (event.getSource().getEntity() instanceof Player p)
                b = b.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, p).withLuck(p.getLuck());
            LootParams params = b.create(LootContextParamSets.ENTITY);
            for (int i = 0; i < inf.rank.mult; i++)
                table.getRandomItems(params, stack -> event.getDrops().add(new ItemEntity(level, mob.getX(), mob.getY(), mob.getZ(), stack)));
        } catch (Exception ignored) {}
        // bonus drop
        ItemStack bonus = bonus(inf.rank, mob.getRandom());
        if (!bonus.isEmpty()) event.getDrops().add(new ItemEntity(level, mob.getX(), mob.getY(), mob.getZ(), bonus));
    }

    private static ItemStack bonus(Rank rank, RandomSource r) {
        ItemStack[] pool = switch (rank) {
            case ELITE -> new ItemStack[] { new ItemStack(Items.IRON_INGOT, 4), new ItemStack(Items.GOLD_INGOT, 3),
                    new ItemStack(Items.REDSTONE, 12), new ItemStack(Items.LAPIS_LAZULI, 8), new ItemStack(Items.GLOWSTONE_DUST, 8),
                    new ItemStack(Items.ENDER_PEARL, 2), new ItemStack(Items.EMERALD, 2), new ItemStack(Items.EXPERIENCE_BOTTLE, 4) };
            case ULTRA -> new ItemStack[] { new ItemStack(Items.DIAMOND, 2), new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.BLAZE_ROD, 4), new ItemStack(Items.GOLDEN_APPLE, 2), new ItemStack(Items.GHAST_TEAR, 2),
                    new ItemStack(Items.NAME_TAG), new ItemStack(Items.EXPERIENCE_BOTTLE, 12) };
            case INFERNAL -> new ItemStack[] { new ItemStack(Items.DIAMOND, 6), new ItemStack(Items.NETHERITE_SCRAP, 2),
                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), new ItemStack(Items.TOTEM_OF_UNDYING),
                    new ItemStack(Items.EXPERIENCE_BOTTLE, 32) };
        };
        return pool[r.nextInt(pool.length)].copy();
    }

    @SubscribeEvent
    public static void onXp(LivingExperienceDropEvent event) {
        Infernal inf = ACTIVE.get(event.getEntity());
        if (inf != null) event.setDroppedExperience(event.getDroppedExperience() * (inf.rank.mult + 1));
    }

    /** /gtapexadditions infernal [elite|ultra|infernal]: makes the nearest mob within 10 blocks infernal (ops). */
    @SubscribeEvent
    public static void onCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
        var root = net.minecraft.commands.Commands.literal("gtapexadditions");
        var cmd = net.minecraft.commands.Commands.literal("infernal").requires(s -> s.hasPermission(2));
        for (Rank rank : Rank.values()) {
            cmd = cmd.then(net.minecraft.commands.Commands.literal(rank.name().toLowerCase(java.util.Locale.ROOT)).executes(ctx -> {
                var src = ctx.getSource();
                Mob best = null;
                double bd = 100;
                for (Mob m : src.getLevel().getEntitiesOfClass(Mob.class, new AABB(src.getPosition(), src.getPosition()).inflate(10))) {
                    double d = m.distanceToSqr(src.getPosition());
                    if (d < bd) { bd = d; best = m; }
                }
                if (best == null) {
                    src.sendFailure(Component.literal("No mob within 10 blocks"));
                    return 0;
                }
                best.getPersistentData().remove(NBT);
                Infernal inf = make(best, rank);
                Component name = title(best, inf);
                src.sendSuccess(() -> Component.literal("Made infernal: ").append(name), true);
                com.omnicrox.gtaa.GTAA.LOGGER.info("Made infernal: {} {}", inf.rank(), inf.mods());
                return 1;
            }));
        }
        event.getDispatcher().register(root.then(cmd));
    }

    /** Area helper for commands / tests. */
    public static List<LivingEntity> near(ServerLevel level, Vec3 pos, double r) {
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos).inflate(r)))
            if (ACTIVE.containsKey(e)) out.add(e);
        return out;
    }
}
