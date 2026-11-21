package com.omnicrox.gtaa.common.infernal;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Infernal Mobs as it plays in GT New Horizons 2.9 (AtomicStryker's mod, GTNH fork 1.10.7), rebuilt for 1.20.1:
 *  - 1 in 20 hostile spawns gets modifiers; 1 in 10 of those rolls Ultra counts, 1 in 7 of those Infernal counts
 *    (2-5 / 5-10 / 8-15 modifiers). Blazes, ghasts, magma cubes and witches always do.
 *  - rank comes from the modifier count: 1-5 "Rare" (yellow), 6-10 "Ultra" (gold), 11+ "Infernal" (red)
 *  - max health = base x modifier count x 1.8
 *  - title "Rare &lt;prefix of a random modifier&gt; Zombie &lt;suffix of the next one&gt;", shown on a boss bar with the
 *    modifier list when you look at the mob (client: InfernalClient), plus a swirl of spell particles
 *  - killed by a player: 25 xp and one enchanted item from the rank's list per 5 modifiers
 * All 27 modifiers behave like the originals (cooldowns and numbers are GTNH's config values).
 */
public final class InfernalMobs {

    public static final String NBT = "gtapexadditions_infernal";
    private static final String ROLLED = "gtapexadditions_infernal_rolled";
    private static final UUID HEALTH_ID = UUID.fromString("6b1c9c84-37b0-4a5e-9a5b-5a3b7e0a6f11");
    private static final UUID OLD_SPEED_ID = UUID.fromString("6b1c9c84-37b0-4a5e-9a5b-5a3b7e0a6f12");
    private static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", "bosses"));
    private static final TagKey<EntityType<?>> BLACKLIST = TagKey.create(Registries.ENTITY_TYPE,
            new ResourceLocation("gtapexadditions", "infernal_blacklist"));

    // GTNH config values (ticks)
    private static final int ALCHEMIST_CD = 120, BLASTOFF_CD = 300, CLOAK_CD = 240, CLOAK_TIME = 200, EFFECT_TIME = 120,
            FIERY_SECS = 3, GHASTLY_CD = 120, GRAVITY_CD = 100, REGEN_CD = 10, SPRINT_CD = 100, STICKY_CD = 300,
            STORM_CD = 300, WEBBER_CD = 300, TELEPORT_CD = 300, RUST_ITEM_DAMAGE = 4;
    private static final double GRAVITY_RANGE = 40;
    private static final float TELEPORT_REFLECT = 0.75f, TELEPORT_REFLECT_MAX = 10f;

    public enum Rank {
        ELITE("Rare", "rareClass", ChatFormatting.YELLOW, 2),
        ULTRA("Ultra", "ultraClass", ChatFormatting.GOLD, 4),
        INFERNAL("Infernal", "infernalClass", ChatFormatting.RED, 8);

        public final String title, key;
        public final ChatFormatting color;
        /** rough loot weight, used by the Extreme Entity Crusher's infernal mode */
        public final int mult;

        Rank(String title, String key, ChatFormatting color, int mult) {
            this.title = title;
            this.key = key;
            this.color = color;
            this.mult = mult;
        }

        public static Rank of(int modCount) {
            return modCount <= 5 ? ELITE : modCount <= 10 ? ULTRA : INFERNAL;
        }

        public int min() {
            return switch (this) {
                case ELITE -> InfernalConfig.MIN_ELITE.get();
                case ULTRA -> InfernalConfig.MIN_ULTRA.get();
                case INFERNAL -> InfernalConfig.MIN_INFERNO.get();
            };
        }

        public int max() {
            return switch (this) {
                case ELITE -> InfernalConfig.MAX_ELITE.get();
                case ULTRA -> InfernalConfig.MAX_ULTRA.get();
                case INFERNAL -> InfernalConfig.MAX_INFERNO.get();
            };
        }

        public Component displayName() {
            return Component.translatableWithFallback("gtapexadditions.infernal." + key, title);
        }
    }

    /** One infernal mob: its modifier chain (in roll order), title words and per-modifier runtime state. */
    public static final class Infernal {
        private final List<InfernalModifier> mods;
        public final String prefix, suffix;
        final Map<InfernalModifier, Long> nextUse = new EnumMap<>(InfernalModifier.class);
        boolean healed, sprinting;
        double sprintX, sprintZ;
        int quicksandTicker;
        LivingEntity chokeTarget;
        int chokeAir = -999;

        public Infernal(List<InfernalModifier> mods, String prefix, String suffix) {
            this.mods = List.copyOf(mods);
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public List<InfernalModifier> mods() {
            return mods;
        }

        public Rank rank() {
            return Rank.of(mods.size());
        }

        public boolean has(InfernalModifier m) {
            return mods.contains(m);
        }

        boolean ready(InfernalModifier m, LivingEntity mob, int cooldown) {
            long now = mob.tickCount;
            if (now <= nextUse.getOrDefault(m, 0L)) return false;
            nextUse.put(m, now + cooldown);
            return true;
        }

        public Component title(LivingEntity mob) {
            return title(mods.size(), prefix, suffix, mob.getName());
        }

        public static Component title(int count, String prefix, String suffix, Component mobName) {
            Rank rank = Rank.of(count);
            MutableComponent c = Component.empty().withStyle(rank.color).append(rank.displayName());
            c.append(prefix.isEmpty() ? Component.literal(" ") : InfernalModifier.prefix(prefix));
            c.append(mobName);
            if (!suffix.isEmpty()) c.append(InfernalModifier.suffix(suffix));
            return c;
        }
    }

    private static final Map<LivingEntity, Infernal> ACTIVE = Collections.synchronizedMap(new WeakHashMap<>());
    private static boolean reflecting;

    private InfernalMobs() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(InfernalMobs.class);
    }

    public static Infernal get(LivingEntity e) {
        return ACTIVE.get(e);
    }

    // ------------------------------------------------------------------ spawning

    private static String id(Entity e) {
        return String.valueOf(EntityType.getKey(e.getType()));
    }

    private static boolean eligible(Mob mob, Level level) {
        if (!(mob instanceof Enemy) || mob instanceof OwnableEntity) return false;
        EntityType<?> type = mob.getType();
        if (type.is(BOSSES) || type.is(BLACKLIST) || InfernalConfig.MOB_BLACKLIST.get().contains(id(mob))) return false;
        return !InfernalConfig.DIMENSION_BLACKLIST.get().contains(level.dimension().location().toString());
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!InfernalConfig.FROM_SPAWNERS.get() && event.getSpawnType() == MobSpawnType.SPAWNER)
            event.getEntity().getPersistentData().putBoolean(ROLLED, true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob)) return;
        CompoundTag data = mob.getPersistentData();
        if (data.contains(NBT)) {
            CompoundTag t = data.getCompound(NBT);
            if (t.contains("rank")) { // pre-GTNH-parity format: strip the old buffs and reroll at the same rank
                Rank old = switch (t.getString("rank")) {
                    case "INFERNAL" -> Rank.INFERNAL;
                    case "ULTRA" -> Rank.ULTRA;
                    default -> Rank.ELITE;
                };
                data.remove(NBT);
                AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
                if (hp != null) hp.removeModifier(HEALTH_ID);
                AttributeInstance sp = mob.getAttribute(Attributes.MOVEMENT_SPEED);
                if (sp != null) sp.removeModifier(OLD_SPEED_ID);
                mob.setCustomName(null);
                make(mob, old);
                return;
            }
            Infernal inf = load(t);
            if (inf != null) ACTIVE.put(mob, inf);
            return;
        }
        if (data.getBoolean(ROLLED)) return;
        data.putBoolean(ROLLED, true);
        if (!InfernalConfig.ENABLED.get() || !eligible(mob, event.getLevel())) return;
        boolean forced = InfernalConfig.ALWAYS_INFERNAL.get().contains(id(mob));
        if (!forced && mob.getRandom().nextInt(InfernalConfig.ELITE_RARITY.get()) != 0) return;
        make(mob, (Rank) null);
    }

    /** Gives a mob modifiers. rank null = roll like a natural spawn. */
    public static Infernal make(Mob mob, Rank rank) {
        RandomSource r = mob.getRandom();
        if (rank == null) {
            rank = Rank.ELITE;
            if (r.nextInt(InfernalConfig.ULTRA_RARITY.get()) == 0) {
                rank = Rank.ULTRA;
                if (r.nextInt(InfernalConfig.INFERNO_RARITY.get()) == 0) rank = Rank.INFERNAL;
            }
        }
        int lo = Math.min(rank.min(), rank.max()), hi = Math.max(rank.min(), rank.max());
        int number = lo + r.nextInt(hi - lo + 1);
        List<InfernalModifier> pool = new ArrayList<>();
        for (InfernalModifier m : InfernalModifier.values()) if (enabled(m)) pool.add(m);
        List<InfernalModifier> chain = new ArrayList<>();
        while (number > 0 && !pool.isEmpty()) {
            InfernalModifier m = pool.remove(r.nextInt(pool.size()));
            if (!m.allowedOn(mob) || !chain.stream().allMatch(m::mixesWith)) continue;
            chain.add(m);
            number--;
        }
        return chain.isEmpty() ? null : make(mob, chain);
    }

    public static boolean enabled(InfernalModifier m) {
        for (String s : InfernalConfig.DISABLED_MODIFIERS.get())
            if (m.modName.equalsIgnoreCase(s) || m.name().equalsIgnoreCase(s)) return false;
        return true;
    }

    /** Gives a mob exactly these modifiers. */
    public static Infernal make(Mob mob, List<InfernalModifier> chain) {
        RandomSource r = mob.getRandom();
        // title: prefix from a random modifier, suffix from the one after it (wrapping), like MobModifier.getEntityDisplayName
        int pick = r.nextInt(chain.size());
        InfernalModifier pm = chain.get(pick);
        String prefix = pm.prefixes[r.nextInt(pm.prefixes.length)];
        String suffix = "";
        if (chain.size() > 1) {
            InfernalModifier sm = chain.get((pick + 1) % chain.size());
            suffix = sm.suffixes[r.nextInt(sm.suffixes.length)];
        }
        Infernal inf = new Infernal(chain, prefix, suffix);
        mob.getPersistentData().put(NBT, save(inf));
        // health: base x mods x factor
        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.removeModifier(HEALTH_ID);
            double base = mob.getMaxHealth();
            double target = base * chain.size() * InfernalConfig.HEALTH_FACTOR.get();
            hp.addPermanentModifier(new AttributeModifier(HEALTH_ID, "Infernal health", target - base,
                    AttributeModifier.Operation.ADDITION));
            mob.setHealth(mob.getMaxHealth());
        }
        ACTIVE.put(mob, inf);
        if (mob.level() instanceof ServerLevel)
            GTAANetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> mob), packet(mob, inf));
        return inf;
    }

    private static GTAANetwork.InfernalPacket packet(LivingEntity mob, Infernal inf) {
        List<String> names = new ArrayList<>();
        for (InfernalModifier m : inf.mods) names.add(m.modName);
        return new GTAANetwork.InfernalPacket(mob.getId(), names, inf.prefix, inf.suffix);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity e && event.getEntity() instanceof ServerPlayer p) {
            Infernal inf = ACTIVE.get(e);
            if (inf != null) GTAANetwork.send(p, packet(e, inf));
        }
    }

    public static Component title(LivingEntity mob, Infernal inf) {
        return inf.title(mob);
    }

    private static CompoundTag save(Infernal inf) {
        CompoundTag t = new CompoundTag();
        ListTag list = new ListTag();
        for (InfernalModifier m : inf.mods) list.add(StringTag.valueOf(m.modName));
        t.put("mods", list);
        t.putString("prefix", inf.prefix);
        t.putString("suffix", inf.suffix);
        return t;
    }

    private static Infernal load(CompoundTag t) {
        List<InfernalModifier> mods = new ArrayList<>();
        for (Tag s : t.getList("mods", Tag.TAG_STRING)) {
            InfernalModifier m = InfernalModifier.byName(s.getAsString());
            if (m != null && !mods.contains(m)) mods.add(m);
        }
        return mods.isEmpty() ? null : new Infernal(mods, t.getString("prefix"), t.getString("suffix"));
    }

    // ------------------------------------------------------------------ helpers

    private static boolean allowedTarget(Entity e) {
        return !(e instanceof FakePlayer);
    }

    private static boolean invulnerable(Entity e) {
        return e instanceof Player p && p.getAbilities().invulnerable;
    }

    /** melee hit straight from the attacker (not a projectile, not indirect) */
    private static boolean direct(DamageSource src) {
        return src.getEntity() != null && src.getDirectEntity() == src.getEntity() && !src.is(DamageTypeTags.IS_PROJECTILE);
    }

    private static float pitch(LivingEntity mob) {
        RandomSource r = mob.getRandom();
        return (r.nextFloat() - r.nextFloat()) * 0.2f + 1.0f;
    }

    private static void effect(LivingEntity e, net.minecraft.world.effect.MobEffect effect, int ticks) {
        e.addEffect(new MobEffectInstance(effect, ticks, 0));
    }

    private static void reflect(Entity target, DamageSource src, float amount) {
        if (reflecting || amount <= 0) return;
        reflecting = true;
        try {
            target.hurt(src, amount);
        } finally {
            reflecting = false;
        }
    }

    // ------------------------------------------------------------------ per-tick abilities

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity mob = event.getEntity();
        if (mob.level().isClientSide()) return;
        Infernal inf = ACTIVE.get(mob);
        if (inf == null || !mob.isAlive()) return;
        LivingEntity target = mob instanceof Mob m ? m.getTarget() : null;
        if (target != null && !target.isAlive()) target = null;
        ServerLevel level = (ServerLevel) mob.level();
        for (InfernalModifier m : inf.mods) {
            switch (m) {
                case ONE_UP -> {
                    if (!inf.healed && mob.getHealth() < mob.getMaxHealth() * 0.25f) {
                        mob.setHealth(mob.getMaxHealth());
                        level.playSound(null, mob, SoundEvents.PLAYER_LEVELUP, mob.getSoundSource(), 1f, 1f);
                        inf.healed = true;
                    }
                }
                case ALCHEMIST -> {
                    if (inf.ready(m, mob, ALCHEMIST_CD)) alchemist(mob, target);
                }
                case BLASTOFF -> {
                    if (target instanceof Player && !invulnerable(target)) blastoff(inf, mob, target);
                }
                case CHOKE -> choke(inf, mob, target);
                case CLOAKING -> {
                    if (target instanceof Player) cloak(inf, mob);
                }
                case GHASTLY -> {
                    if (inf.ready(m, mob, GHASTLY_CD)) ghastly(mob, target);
                }
                case GRAVITY -> {
                    if (target instanceof Player && !invulnerable(target)) gravity(inf, mob, target);
                }
                case QUICKSAND -> {
                    if (target != null && allowedTarget(target) && mob.hasLineOfSight(target) && ++inf.quicksandTicker >= 80) {
                        inf.quicksandTicker = 0;
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
                    }
                }
                case REGEN -> {
                    if (mob.getHealth() < mob.getMaxHealth() && inf.ready(m, mob, REGEN_CD)) mob.heal(1f);
                }
                case SPRINT -> {
                    if (target != null) {
                        if (inf.ready(m, mob, SPRINT_CD)) inf.sprinting = !inf.sprinting;
                        if (inf.sprinting) sprint(inf, mob);
                    }
                }
                case STORM -> {
                    if (target instanceof Player) storm(inf, mob, target, level);
                }
                case WEBBER -> {
                    if (target instanceof Player) webber(inf, mob, target);
                }
                default -> {}
            }
        }
    }

    private static void alchemist(LivingEntity mob, LivingEntity target) {
        if (target == null || !mob.hasLineOfSight(target) || mob.distanceTo(target) <= 2f) return;
        Vec3 tv = target.getDeltaMovement();
        double dx = target.getX() + tv.x - mob.getX();
        double dy = target.getEyeY() - 1.1 - mob.getY();
        double dz = target.getZ() + tv.z - mob.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        Potion potion = Potions.HARMING;
        if (dist >= 8 && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) potion = Potions.SLOWNESS;
        else if (target.getHealth() >= 8 && !target.hasEffect(MobEffects.POISON)) potion = Potions.POISON;
        else if (dist <= 3 && !target.hasEffect(MobEffects.WEAKNESS) && mob.getRandom().nextFloat() < 0.25f) potion = Potions.WEAKNESS;
        ThrownPotion thrown = new ThrownPotion(mob.level(), mob);
        thrown.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        thrown.setXRot(thrown.getXRot() + 20f);
        thrown.shoot(dx, dy + dist * 0.2, dz, 0.75f, 8f);
        mob.level().addFreshEntity(thrown);
    }

    private static void blastoff(Infernal inf, LivingEntity mob, LivingEntity target) {
        if (target == null || !mob.hasLineOfSight(target) || !inf.ready(InfernalModifier.BLASTOFF, mob, BLASTOFF_CD)) return;
        mob.level().playSound(null, mob, SoundEvents.SLIME_ATTACK, mob.getSoundSource(), 1f, pitch(mob));
        target.push(0, 1.1, 0);
        target.hurtMarked = true;
    }

    private static void choke(Infernal inf, LivingEntity mob, LivingEntity target) {
        if (target != inf.chokeTarget) {
            inf.chokeAir = -999;
            if (inf.chokeTarget != null) inf.chokeTarget.setAirSupply(inf.chokeTarget.getMaxAirSupply());
            inf.chokeTarget = target;
        }
        LivingEntity t = inf.chokeTarget;
        if (t == null || !mob.hasLineOfSight(t)) return;
        inf.chokeAir = inf.chokeAir == -999 ? t.getAirSupply() : Math.min(inf.chokeAir, t.getAirSupply());
        if (invulnerable(t)) return;
        inf.chokeAir--;
        if (inf.chokeAir < -19) {
            inf.chokeAir = 0;
            t.hurt(t.damageSources().drown(), 2f);
        }
        t.setAirSupply(inf.chokeAir);
    }

    private static void cloak(Infernal inf, LivingEntity mob) {
        if (inf.ready(InfernalModifier.CLOAKING, mob, CLOAK_CD))
            mob.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, CLOAK_TIME));
    }

    private static void ghastly(LivingEntity mob, LivingEntity target) {
        if (target == null || mob.distanceTo(target) <= 3f || !mob.hasLineOfSight(target)) return;
        Vec3 look = mob.getViewVector(1f);
        double sx = mob.getX() + look.x * 2, sy = mob.getY(0.5) + 0.5, sz = mob.getZ() + look.z * 2;
        double dx = target.getX() - sx, dy = target.getY(0.5) - sy, dz = target.getZ() - sz;
        mob.setYRot((float) (-Math.atan2(dx, dz) * 180.0 / Math.PI));
        mob.yBodyRot = mob.getYRot();
        mob.level().levelEvent(null, 1016, mob.blockPosition(), 0);
        LargeFireball ball = new LargeFireball(mob.level(), mob, dx, dy, dz, 1);
        ball.setPos(sx, sy, sz);
        mob.level().addFreshEntity(ball);
    }

    private static void gravity(Infernal inf, LivingEntity mob, LivingEntity target) {
        if (!mob.hasLineOfSight(target) || mob.distanceToSqr(target) >= GRAVITY_RANGE * GRAVITY_RANGE) return;
        if (!inf.ready(InfernalModifier.GRAVITY, mob, GRAVITY_CD)) return;
        double dx = target.getX() - mob.getX(), dz = target.getZ() - mob.getZ();
        while (dx * dx + dz * dz < 1.0E-4) {
            dx = (Math.random() - Math.random()) * 0.01;
            dz = (Math.random() - Math.random()) * 0.01;
        }
        mob.level().playSound(null, mob, SoundEvents.IRON_GOLEM_ATTACK, mob.getSoundSource(), 1f, pitch(mob));
        double n = Math.sqrt(dx * dx + dz * dz), power = 0.8;
        Vec3 v = target.getDeltaMovement().scale(0.5);
        double vy = Math.min(0.4, v.y + power);
        target.setDeltaMovement(v.x - dx / n * power, vy, v.z - dz / n * power);
        target.hasImpulse = true;
        target.hurtMarked = true;
    }

    private static void sprint(Infernal inf, LivingEntity mob) {
        Vec3 v = mob.getDeltaMovement();
        float rotMove = (float) (Math.atan2(v.x, v.z) * 180.0 / Math.PI);
        float rotLook = Mth.wrapDegrees(mob.getYRot());
        if (Math.abs(rotMove + rotLook) > 10f) rotLook -= 360f;
        if (Math.abs(rotMove + rotLook) > 10f) {
            inf.sprintX = v.x;
            inf.sprintZ = v.z;
        }
        if (Math.sqrt(v.x * v.x + v.z * v.z) < 0.3) {
            if (Math.sqrt(inf.sprintX * inf.sprintX + inf.sprintZ * inf.sprintZ) > 0.6 || !mob.onGround()) {
                inf.sprintX /= 1.55;
                inf.sprintZ /= 1.55;
            }
            inf.sprintX *= 1.5;
            inf.sprintZ *= 1.5;
            mob.setDeltaMovement(inf.sprintX, v.y, inf.sprintZ);
        }
    }

    private static void storm(Infernal inf, LivingEntity mob, LivingEntity target, ServerLevel level) {
        if (target.getVehicle() != null || !mob.hasLineOfSight(target) || mob.distanceTo(target) <= 3f) return;
        if (!level.canSeeSky(target.blockPosition())) return;
        if (!inf.ready(InfernalModifier.STORM, mob, STORM_CD)) return;
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) return;
        bolt.moveTo(target.getX(), target.getY() - 1, target.getZ());
        level.addFreshEntity(bolt);
    }

    private static void webber(Infernal inf, LivingEntity mob, LivingEntity target) {
        if (target == null || invulnerable(target)) return;
        if (!target.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || !mob.hasLineOfSight(target)) return;
        long now = mob.tickCount;
        if (now <= inf.nextUse.getOrDefault(InfernalModifier.WEBBER, 0L)) return;
        BlockPos feet = target.blockPosition();
        BlockPos at;
        if (target.level().getBlockState(feet.below()).isAir()) at = feet.below();
        else if (target.level().getBlockState(feet).isAir()) at = feet;
        else return;
        inf.nextUse.put(InfernalModifier.WEBBER, now + WEBBER_CD);
        target.level().setBlock(at, Blocks.COBWEB.defaultBlockState(), 3);
        mob.level().playSound(null, mob, SoundEvents.SPIDER_AMBIENT, mob.getSoundSource(), 1f, pitch(mob));
    }

    // ------------------------------------------------------------------ hurt / attack

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) return;
        DamageSource src = event.getSource();
        Entity attacker = src.getEntity();
        if (attacker == victim) return;
        Infernal inf = ACTIVE.get(victim);
        if (inf != null) event.setAmount(onHurt(inf, victim, src, event.getAmount()));
        if (attacker instanceof LivingEntity a && !src.is(net.minecraft.world.damagesource.DamageTypes.THORNS)) {
            Infernal ainf = ACTIVE.get(a);
            if (ainf != null) event.setAmount(onAttack(ainf, a, victim, src, event.getAmount()));
        }
    }

    /** the infernal mob is being hurt */
    private static float onHurt(Infernal inf, LivingEntity mob, DamageSource src, float damage) {
        Entity att = src.getEntity();
        LivingEntity la = att instanceof LivingEntity l ? l : null;
        for (InfernalModifier m : inf.mods) {
            switch (m) {
                case BLASTOFF -> {
                    if (la != null && !invulnerable(la)) blastoff(inf, mob, la);
                }
                case BULWARK -> damage = Math.max(damage * 0.5f, 1f);
                case CHOKE -> {
                    if (inf.chokeTarget != null && src.getDirectEntity() == inf.chokeTarget && inf.chokeAir != -999) {
                        inf.chokeAir += 60;
                        inf.chokeTarget.setAirSupply(inf.chokeAir);
                    }
                }
                case CLOAKING -> {
                    if (la != null) cloak(inf, mob);
                }
                case DARKNESS -> {
                    if (la != null && allowedTarget(la) && direct(src)) effect(la, MobEffects.BLINDNESS, EFFECT_TIME);
                }
                case ENDER, NINJA -> {
                    if (att != null && !reflecting && mob instanceof Mob mm && mob.tickCount > inf.nextUse.getOrDefault(m, 0L)
                            && teleport(mm, att, m == InfernalModifier.ENDER)) {
                        inf.nextUse.put(m, (long) mob.tickCount + TELEPORT_CD);
                        reflect(att, mob.damageSources().mobAttack(mob), Math.min(TELEPORT_REFLECT_MAX, damage * TELEPORT_REFLECT));
                        damage = 0;
                    }
                }
                case EXHAUST -> {
                    if (att instanceof Player p) p.causeFoodExhaustion(1f);
                }
                case FIERY -> {
                    if (la != null && direct(src)) {
                        la.setSecondsOnFire(FIERY_SECS);
                        mob.clearFire();
                    }
                }
                case GRAVITY -> {
                    if (la != null && !invulnerable(la)) gravity(inf, mob, la);
                }
                case POISONOUS -> {
                    if (la != null && allowedTarget(la) && !la.hasEffect(MobEffects.POISON) && direct(src))
                        effect(la, MobEffects.POISON, EFFECT_TIME);
                }
                case RUST -> {
                    if (att instanceof Player p && direct(src)) {
                        ItemStack held = p.getMainHandItem();
                        if (held.isDamageableItem()) held.hurtAndBreak(RUST_ITEM_DAMAGE, p, pl -> pl.broadcastBreakEvent(
                                net.minecraft.world.entity.EquipmentSlot.MAINHAND));
                    }
                }
                case SAPPER -> {
                    if (la != null && allowedTarget(la) && !la.hasEffect(MobEffects.HUNGER) && direct(src))
                        effect(la, MobEffects.HUNGER, EFFECT_TIME);
                }
                case STICKY -> {
                    if (att instanceof Player p && !p.isCreative() && direct(src) && !p.getMainHandItem().isEmpty()
                            && inf.ready(m, mob, STICKY_CD)) {
                        ItemEntity drop = p.drop(p.getInventory().removeItem(p.getInventory().selected, 1), false);
                        if (drop != null) {
                            drop.setPickUpDelay(50);
                            p.level().playSound(null, mob, SoundEvents.SLIME_ATTACK, mob.getSoundSource(), 1f, pitch(mob));
                        }
                    }
                }
                case VENGEANCE -> {
                    if (att != null && att != mob && !reflecting) {
                        float r = Math.max(Math.min(damage, mob.getHealth()) * 0.5f, 1f);
                        reflect(att, mob.damageSources().thorns(mob), r);
                    }
                }
                case WEAKNESS -> {
                    if (la != null && allowedTarget(la)) effect(la, MobEffects.WEAKNESS, EFFECT_TIME);
                }
                case WEBBER -> {
                    if (la != null) webber(inf, mob, la);
                }
                case WITHER -> {
                    if (la != null && allowedTarget(la) && direct(src)) effect(la, MobEffects.WITHER, EFFECT_TIME);
                }
                default -> {}
            }
        }
        return damage;
    }

    /** the infernal mob is hurting something */
    private static float onAttack(Infernal inf, LivingEntity mob, LivingEntity target, DamageSource src, float damage) {
        for (InfernalModifier m : inf.mods) {
            switch (m) {
                case BERSERK -> {
                    if (!reflecting) {
                        reflect(mob, mob.damageSources().generic(), damage);
                        damage *= 2f;
                    }
                }
                case DARKNESS -> {
                    if (allowedTarget(target)) effect(target, MobEffects.BLINDNESS, EFFECT_TIME);
                }
                case EXHAUST -> {
                    if (target instanceof Player p) p.causeFoodExhaustion(1f);
                }
                case FIERY -> target.setSecondsOnFire(FIERY_SECS);
                case LIFESTEAL -> {
                    if (mob.getHealth() < mob.getMaxHealth()) mob.heal(damage);
                }
                case POISONOUS -> {
                    if (allowedTarget(target) && !target.hasEffect(MobEffects.POISON)) effect(target, MobEffects.POISON, EFFECT_TIME);
                }
                case RUST -> {
                    if (target instanceof Player p) p.getInventory().hurtArmor(src, damage * 3f, Inventory.ALL_ARMOR_SLOTS);
                }
                case SAPPER -> {
                    if (allowedTarget(target) && !target.hasEffect(MobEffects.HUNGER)) effect(target, MobEffects.HUNGER, EFFECT_TIME);
                }
                case WEAKNESS -> {
                    if (allowedTarget(target)) effect(target, MobEffects.WEAKNESS, EFFECT_TIME);
                }
                case WITHER -> {
                    if (allowedTarget(target)) effect(target, MobEffects.WITHER, EFFECT_TIME);
                }
                default -> {}
            }
        }
        return damage;
    }

    /** Ender: 16 blocks through the attacker (portal particles). Ninja: 8 blocks, lands at the attacker with a bang. */
    private static boolean teleport(Mob mob, Entity toward, boolean ender) {
        Vec3 dir = new Vec3(mob.getX() - toward.getX(), mob.getY(0.5) - toward.getEyeY(), mob.getZ() - toward.getZ()).normalize();
        double dist = ender ? 16 : 8, spread = ender ? 8 : 4;
        int yr = ender ? 8 : 4;
        double ox = mob.getX(), oy = mob.getY(), oz = mob.getZ();
        RandomSource r = mob.getRandom();
        for (int i = 0; i < 4; i++) {
            double x = ox + (r.nextDouble() - 0.5) * spread - dir.x * dist;
            double y = oy + (r.nextInt(16) - yr) - dir.y * dist;
            double z = oz + (r.nextDouble() - 0.5) * spread - dir.z * dist;
            if (mob.randomTeleport(x, y, z, ender)) {
                if (!ender && mob.level() instanceof ServerLevel sl) {
                    sl.playSound(null, ox, oy, oz, SoundEvents.GENERIC_EXPLODE, mob.getSoundSource(), 2f,
                            (1f + (r.nextFloat() - r.nextFloat()) * 0.2f) * 0.7f);
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER, ox, oy, oz, 1, 0, 0, 0, 0);
                }
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ death and loot

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity mob = event.getEntity();
        Infernal inf = ACTIVE.get(mob);
        if (inf == null || mob.level().isClientSide()) return;
        if (inf.chokeTarget != null) {
            inf.chokeTarget.setAirSupply(inf.chokeTarget.getMaxAirSupply());
            inf.chokeTarget = null;
        }
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        LivingEntity mob = event.getEntity();
        Infernal inf = ACTIVE.remove(mob);
        if (inf == null || !(mob.level() instanceof ServerLevel level) || !event.isRecentlyHit()) return;
        ExperienceOrb.award(level, mob.position(), 25);
        int modStr = inf.mods.size();
        Rank rank = inf.rank();
        while (modStr > 0) {
            ItemStack stack = randomItem(rank, mob.getRandom(), Math.min(5, modStr));
            if (stack.isEmpty()) {
                modStr--;
                continue;
            }
            event.getDrops().add(new ItemEntity(level, mob.getX(), mob.getY(), mob.getZ(), stack));
            modStr -= 5;
        }
    }

    /** Modifier count for one mob: 1 in ultraRarity rolls Ultra counts, 1 in infernoRarity of those Infernal counts */
    public static int rollModCount(RandomSource r, int ultraRarity, int infernoRarity) {
        Rank rank = Rank.ELITE;
        if (r.nextInt(Math.max(1, ultraRarity)) == 0) {
            rank = Rank.ULTRA;
            if (r.nextInt(Math.max(1, infernoRarity)) == 0) rank = Rank.INFERNAL;
        }
        int lo = Math.min(rank.min(), rank.max()), hi = Math.max(rank.min(), rank.max());
        int enabled = 0;
        for (InfernalModifier m : InfernalModifier.values()) if (enabled(m)) enabled++;
        return Math.max(1, Math.min(enabled, lo + r.nextInt(hi - lo + 1)));
    }

    /** The items an infernal mob with this many modifiers drops when a player kills it (one per 5 modifiers) */
    public static List<ItemStack> infernalLoot(int mods, RandomSource r) {
        List<ItemStack> out = new ArrayList<>();
        Rank rank = Rank.of(mods);
        int modStr = mods;
        while (modStr > 0) {
            ItemStack stack = randomItem(rank, r, Math.min(5, modStr));
            if (stack.isEmpty()) {
                modStr--;
                continue;
            }
            out.add(stack);
            modStr -= 5;
        }
        return out;
    }

    /** Whether this entity type spawns infernal on its own every time (GTNH's always-infernal list) */
    public static boolean alwaysInfernal(EntityType<?> type) {
        return InfernalConfig.ALWAYS_INFERNAL.get().contains(String.valueOf(EntityType.getKey(type)));
    }

    private static List<? extends String> list(Rank rank) {
        return switch (rank) {
            case ELITE -> InfernalConfig.DROPS_ELITE.get();
            case ULTRA -> InfernalConfig.DROPS_ULTRA.get();
            case INFERNAL -> InfernalConfig.DROPS_INFERNAL.get();
        };
    }

    /** One random drop from the rank's list, enchanted with strength 1-5 (GTNH: (strength + 1) / 2 enchantments) */
    public static ItemStack randomItem(Rank rank, RandomSource r, int strength) {
        List<? extends String> l = list(rank);
        if (l.isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = parse(l.get(r.nextInt(l.size())));
        if (stack.isEmpty()) return stack;
        if (stack.is(Items.ENCHANTED_BOOK)) {
            List<net.minecraft.world.item.enchantment.Enchantment> all = new ArrayList<>();
            for (var e : BuiltInRegistries.ENCHANTMENT) if (e.isDiscoverable()) all.add(e);
            if (!all.isEmpty()) {
                var e = all.get(r.nextInt(all.size()));
                return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e,
                        Mth.nextInt(r, e.getMinLevel(), e.getMaxLevel())));
            }
            return stack;
        }
        int ench = stack.getItem().getEnchantmentValue();
        if (ench > 0 && stack.isEnchantable()) {
            List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(r, stack, ench, false);
            int remain = (strength + 1) / 2;
            for (EnchantmentInstance e : list) {
                if (remain-- <= 0) break;
                stack.enchant(e.enchantment, e.level);
            }
        }
        return stack;
    }

    private static ItemStack parse(String entry) {
        try {
            String s = entry.trim();
            int count = 1;
            int star = s.lastIndexOf('*');
            if (star > 0) {
                count = Integer.parseInt(s.substring(star + 1).trim());
                s = s.substring(0, star).trim();
            }
            if (s.startsWith("potion=")) {
                Potion p = BuiltInRegistries.POTION.get(new ResourceLocation(s.substring(7)));
                ItemStack st = PotionUtils.setPotion(new ItemStack(Items.POTION), p);
                st.setCount(Math.max(1, count));
                return st;
            }
            var item = BuiltInRegistries.ITEM.get(new ResourceLocation(s));
            if (item == Items.AIR) return ItemStack.EMPTY;
            return new ItemStack(item, Math.max(1, count));
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    /** One bonus drop for the Extreme Entity Crusher's infernal mode */
    public static ItemStack bonus(Rank rank, RandomSource r) {
        return randomItem(rank, r, rank == Rank.ELITE ? 3 : 5);
    }

    // ------------------------------------------------------------------ commands

    /**
     * /gtapexadditions infernal rare|ultra|infernal - the nearest mob within 10 blocks rolls that rank.
     * /gtapexadditions infernal mods &lt;names...&gt; - exact modifiers, e.g. "mods Choke Storm Vengeance". (ops)
     */
    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        var root = Commands.literal("gtapexadditions");
        var cmd = Commands.literal("infernal").requires(s -> s.hasPermission(2));
        for (Rank rank : Rank.values()) {
            String name = rank == Rank.ELITE ? "rare" : rank.name().toLowerCase(java.util.Locale.ROOT);
            cmd = cmd.then(Commands.literal(name).executes(ctx -> apply(ctx.getSource(), mob -> make(mob, rank))));
        }
        cmd = cmd.then(Commands.literal("elite").executes(ctx -> apply(ctx.getSource(), mob -> make(mob, Rank.ELITE))));
        cmd = cmd.then(Commands.literal("mods").then(Commands.argument("names", StringArgumentType.greedyString()).executes(ctx -> {
            List<InfernalModifier> chain = new ArrayList<>();
            for (String s : StringArgumentType.getString(ctx, "names").split("[ ,]+")) {
                InfernalModifier m = InfernalModifier.byName(s);
                if (m == null) {
                    ctx.getSource().sendFailure(Component.literal("Unknown modifier: " + s));
                    return 0;
                }
                if (!chain.contains(m)) chain.add(m);
            }
            return apply(ctx.getSource(), mob -> make(mob, chain));
        })));
        event.getDispatcher().register(root.then(cmd));
    }

    private static int apply(net.minecraft.commands.CommandSourceStack src, java.util.function.Function<Mob, Infernal> fn) {
        Mob best = null;
        double bd = 100;
        for (Mob m : src.getLevel().getEntitiesOfClass(Mob.class, new AABB(src.getPosition(), src.getPosition()).inflate(10))) {
            double d = m.distanceToSqr(src.getPosition());
            if (d < bd) {
                bd = d;
                best = m;
            }
        }
        if (best == null) {
            src.sendFailure(Component.literal("No mob within 10 blocks"));
            return 0;
        }
        best.getPersistentData().remove(NBT);
        Infernal inf = fn.apply(best);
        if (inf == null) {
            src.sendFailure(Component.literal("No modifiers could roll on that mob"));
            return 0;
        }
        Component name = inf.title(best);
        StringBuilder mods = new StringBuilder();
        for (InfernalModifier m : inf.mods()) mods.append(m.modName).append(' ');
        src.sendSuccess(() -> Component.literal("Made infernal: ").append(name)
                .append(Component.literal(" [" + mods.toString().trim() + "]").withStyle(ChatFormatting.GRAY)), true);
        return 1;
    }

    /** Area helper for commands / tests. */
    public static List<LivingEntity> near(ServerLevel level, Vec3 pos, double r) {
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos).inflate(r)))
            if (ACTIVE.containsKey(e)) out.add(e);
        return out;
    }
}
