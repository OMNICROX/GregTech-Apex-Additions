package com.omnicrox.gtaa.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.infernal.InfernalMobs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * The Apex Ascension Suit: the pack's final suit, made in an Ascension Forge from Hero Cores (each one a tier-IV
 * hero suit broken down). Every piece is strong on its own; the full set adds flight, immunity to fire, falls,
 * drowning, wither and poison, night vision, regeneration, and +50% damage against infernal mobs.
 */
public class ApexSuitItem extends ArmorItem {

    public static final ArmorMaterial MATERIAL = new ArmorMaterial() {
        private final int[] defense = { 6, 12, 16, 8 }; // boots, legs, chest, helmet

        @Override
        public int getDurabilityForType(Type type) {
            return 0;
        }

        @Override
        public int getDefenseForType(Type type) {
            return switch (type) {
                case BOOTS -> defense[0];
                case LEGGINGS -> defense[1];
                case CHESTPLATE -> defense[2];
                case HELMET -> defense[3];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return 30;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_NETHERITE;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public String getName() {
            return GTAA.MOD_ID + ":apex";
        }

        @Override
        public float getToughness() {
            return 6f;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.5f;
        }
    };

    private static final UUID[] PIECE_UUID = {
            UUID.fromString("6a2b1c0e-3d4f-4a1b-9c7e-2f5a8b0d1e01"), UUID.fromString("6a2b1c0e-3d4f-4a1b-9c7e-2f5a8b0d1e02"),
            UUID.fromString("6a2b1c0e-3d4f-4a1b-9c7e-2f5a8b0d1e03"), UUID.fromString("6a2b1c0e-3d4f-4a1b-9c7e-2f5a8b0d1e04") };

    private final Multimap<Attribute, AttributeModifier> extra;

    public ApexSuitItem(Type type, Properties props) {
        super(MATERIAL, type, props.stacksTo(1).rarity(Rarity.EPIC).fireResistant());
        UUID id = PIECE_UUID[type.ordinal()];
        var b = ImmutableMultimap.<Attribute, AttributeModifier>builder();
        b.putAll(super.getDefaultAttributeModifiers(type.getSlot()));
        switch (type) {
            case HELMET -> b.put(Attributes.MAX_HEALTH, new AttributeModifier(id, "Apex", 10, AttributeModifier.Operation.ADDITION));
            case CHESTPLATE -> {
                b.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(id, "Apex", 8, AttributeModifier.Operation.ADDITION));
                b.put(Attributes.ATTACK_SPEED, new AttributeModifier(id, "Apex", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
            }
            case LEGGINGS -> {
                b.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, "Apex", 0.35, AttributeModifier.Operation.MULTIPLY_BASE));
                b.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(id, "Apex", 2, AttributeModifier.Operation.ADDITION));
                b.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(id, "Apex", 2, AttributeModifier.Operation.ADDITION));
            }
            case BOOTS -> {
                b.put(ForgeMod.STEP_HEIGHT_ADDITION.get(), new AttributeModifier(id, "Apex", 1, AttributeModifier.Operation.ADDITION));
                b.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(id, "Apex", 1, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
        extra = b.build();
    }

    public static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(ApexSuitItem.class);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == getEquipmentSlot() ? extra : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return GTAA.MOD_ID + ":textures/models/armor/apex_layer_" + (slot == EquipmentSlot.LEGS ? 2 : 1) +
                (type == null ? "" : "_" + type) + ".png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Apex Ascension Suit").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Full set:").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal(" Flight, regeneration, night vision").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(" Immune to fire, falls, drowning, wither, poison").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(" +50% damage to infernal mobs").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Unbreakable").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static boolean fullSet(LivingEntity e) {
        return e.getItemBySlot(EquipmentSlot.HEAD).is(GTAAItems.APEX_HELMET.get()) &&
                e.getItemBySlot(EquipmentSlot.CHEST).is(GTAAItems.APEX_CHESTPLATE.get()) &&
                e.getItemBySlot(EquipmentSlot.LEGS).is(GTAAItems.APEX_LEGGINGS.get()) &&
                e.getItemBySlot(EquipmentSlot.FEET).is(GTAAItems.APEX_BOOTS.get());
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
        Player p = event.player;
        if (p.tickCount % 20 != 0 || !fullSet(p)) return;
        p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, true, false));
        p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false));
        p.removeEffect(MobEffects.WITHER);
        p.removeEffect(MobEffects.POISON);
        if (p.isOnFire()) p.clearFire();
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (fullSet(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        var src = event.getSource();
        if ((src.is(DamageTypeTags.IS_FIRE) || src.is(DamageTypeTags.IS_DROWNING) || src.is(DamageTypeTags.IS_FALL))
                && fullSet(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker && fullSet(attacker) &&
                InfernalMobs.get(event.getEntity()) != null)
            event.setAmount(event.getAmount() * 1.5f);
    }
}
