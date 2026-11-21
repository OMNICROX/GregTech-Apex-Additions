package com.omnicrox.gtaa.client;

import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.infernal.InfernalConfig;
import com.omnicrox.gtaa.common.infernal.InfernalMobs;
import com.omnicrox.gtaa.common.infernal.InfernalModifier;
import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client half of Infernal Mobs, matching the original's InfernalMobsClient + RendererBossGlow:
 *  - look at an infernal mob (up to 32 blocks) and a boss bar appears at the top of the screen with its title in the
 *    rank colour and its modifiers listed underneath, 5 per line; it stays 3 s after you look away and hides while a
 *    real boss bar is showing
 *  - every infernal mob in range trails "mobSpell" swirls (random dark colours), 100 a second
 */
public final class InfernalClient {

    private InfernalClient() {}

    public record Data(List<String> mods, String prefix, String suffix) {}

    private static final Map<Integer, Data> DATA = new ConcurrentHashMap<>();
    private static final ResourceLocation BARS = new ResourceLocation("textures/gui/bars.png");
    private static final double RANGE = 32;
    private static int targetId = -1;
    private static long lastSeen;
    private static Field bossEvents;
    private static boolean bossEventsTried;

    public static void receive(GTAANetwork.InfernalPacket p) {
        DATA.put(p.entityId(), new Data(p.mods(), p.prefix(), p.suffix()));
    }

    public static Data get(Entity e) {
        return e == null ? null : DATA.get(e.getId());
    }

    @Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModEvents {

        @SubscribeEvent
        public static void overlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), "infernal_boss_bar",
                    (gui, g, partialTick, width, height) -> render(g, width));
        }
    }

    @Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeEvents {

        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.isPaused()) return;
            Entity cam = mc.getCameraEntity();
            if (cam == null || DATA.isEmpty()) return;
            // look target
            Entity looked = lookedAt(mc, cam);
            if (looked != null && DATA.containsKey(looked.getId())) {
                targetId = looked.getId();
                lastSeen = System.currentTimeMillis();
            }
            // particles: 5 per tick per mob in range (the original spawned one every 10 ms)
            RandomSource r = mc.level.random;
            for (Integer id : DATA.keySet()) {
                Entity e = mc.level.getEntity(id);
                if (!(e instanceof LivingEntity le) || !le.isAlive() || e.distanceToSqr(cam) > RANGE * RANGE || e.isInvisible()) continue;
                AABB bb = e.getBoundingBox();
                for (int i = 0; i < 5; i++) {
                    double x = bb.minX + r.nextDouble() * (bb.maxX - bb.minX);
                    double y = bb.minY + r.nextDouble() * (bb.maxY - bb.minY);
                    double z = bb.minZ + r.nextDouble() * (bb.maxZ - bb.minZ);
                    double c = r.nextDouble();
                    mc.level.addParticle(ParticleTypes.ENTITY_EFFECT, x, y, z, Math.max(0, (r.nextDouble() - 0.5) * 2), 0,
                            Math.max(0, (c - 0.5) * 2));
                }
            }
        }

        @SubscribeEvent
        public static void leave(EntityLeaveLevelEvent event) {
            if (event.getLevel().isClientSide()) DATA.remove(event.getEntity().getId());
        }

        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
            DATA.clear();
            targetId = -1;
        }
    }

    private static Entity lookedAt(Minecraft mc, Entity cam) {
        Vec3 eye = cam.getEyePosition(1f);
        Vec3 look = cam.getViewVector(1f);
        Vec3 end = eye.add(look.scale(RANGE));
        HitResult block = mc.level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cam));
        if (block.getType() != HitResult.Type.MISS) end = block.getLocation();
        AABB box = cam.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(cam, eye, end, box,
                e -> !e.isSpectator() && e instanceof LivingEntity, eye.distanceToSqr(end));
        return hit == null ? null : hit.getEntity();
    }

    private static boolean vanillaBossBarShowing(Minecraft mc) {
        if (!bossEventsTried) {
            bossEventsTried = true;
            for (String name : new String[] { "f_93699_", "events" }) {
                try {
                    bossEvents = ObfuscationReflectionHelper.findField(BossHealthOverlay.class, name);
                    break;
                } catch (Throwable ignored) {}
            }
        }
        if (bossEvents == null) return false;
        try {
            return !((Map<?, ?>) bossEvents.get(mc.gui.getBossOverlay())).isEmpty();
        } catch (Throwable t) {
            return false;
        }
    }

    private static void render(GuiGraphics g, int width) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.options.hideGui || targetId < 0 || !InfernalConfig.OVERLAY.get()) return;
        if (System.currentTimeMillis() - lastSeen > 3000) return;
        Entity e = mc.level.getEntity(targetId);
        Data d = DATA.get(targetId);
        if (!(e instanceof LivingEntity mob) || !mob.isAlive() || d == null) return;
        if (vanillaBossBarShowing(mc)) return;
        Font font = mc.font;
        Component title = InfernalMobs.Infernal.title(d.mods().size(), d.prefix(), d.suffix(), mob.getName());
        int x = width / 2 - 91, y = 12;
        float frac = mob.getMaxHealth() <= 0 ? 0 : Math.min(1f, mob.getHealth() / mob.getMaxHealth());
        g.blit(BARS, x, y, 0, 0, 182, 5);
        int fill = (int) (frac * 183f);
        if (fill > 0) g.blit(BARS, x, y, 0, 5, Math.min(182, fill), 5);
        g.drawString(font, title, width / 2 - font.width(title) / 2, y - 10, 0x2F96EB, true);
        // modifier list: 5 names per line, up to 3 lines (the last takes the rest)
        List<MutableComponent> lines = new ArrayList<>();
        MutableComponent line = Component.empty();
        int j = 0;
        for (String m : d.mods()) {
            InfernalModifier mod = InfernalModifier.byName(m);
            line.append(" ").append(mod != null ? mod.displayName() : Component.literal(m));
            if (++j % 5 == 0 && lines.size() < 2) {
                lines.add(line);
                line = Component.empty();
            }
        }
        if (!line.getSiblings().isEmpty()) lines.add(line);
        int ly = y + 7;
        for (MutableComponent l : lines) {
            g.drawString(font, l, width / 2 - font.width(l) / 2, ly, 0xFFFFFF, true);
            ly += 10;
        }
    }
}
