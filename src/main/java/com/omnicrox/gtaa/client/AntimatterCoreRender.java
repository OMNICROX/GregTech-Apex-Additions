package com.omnicrox.gtaa.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.machine.multiblock.AntimatterForgeMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Antimatter core of the SASS, 16 blocks behind the controller. Port of GoodGenerator's AntimatterRenderer
 * (GT5-Unofficial, LGPL-3.0): spiky core whose size follows the stored antimatter, a black containment shell,
 * a cyan glow ring and the protomatter beam while it is running. The GLSL from GTNH is replaced by vanilla
 * render types so it stays visible with shader packs.
 */
public class AntimatterCoreRender extends DynamicRender<AntimatterForgeMachine, AntimatterCoreRender> {

    public static final Codec<AntimatterCoreRender> CODEC = Codec.unit(AntimatterCoreRender::new);
    public static final DynamicRenderType<AntimatterForgeMachine, AntimatterCoreRender> TYPE = new DynamicRenderType<>(CODEC);

    private static final ResourceLocation WHITE = GTAA.id("textures/misc/white.png");
    private static final float SPIKE_R = 0.153f, SPIKE_G = 0.435f, SPIKE_B = 1f;
    private static final float CORE_R = 0.435f, CORE_G = 0.718f, CORE_B = 1f;
    private static final float MAX_RADIUS = 7f;
    private static final float NORMALIZE = 0.5f;
    private static final int FULL_BRIGHT = LightTexture.FULL_BRIGHT;

    private static float[][][] core, sphere, ring;
    private static boolean failed;

    private float shownScale = 0;

    public AntimatterCoreRender() {}

    @Override
    public DynamicRenderType<AntimatterForgeMachine, AntimatterCoreRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(AntimatterForgeMachine machine, Vec3 cameraPos) {
        return machine.isFormed() && machine.isCoreRenderEnabled() && machine.getGuiAntimatter() > 0;
    }

    @Override
    public void render(AntimatterForgeMachine machine, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        if (!loadModels()) return;
        long amount = machine.getGuiAntimatter();
        if (amount <= 0) return;

        Direction back = machine.getFrontFacing().getOpposite();
        double timer = machine.getOffsetTimer() + partialTick;

        // GTNH: diameter = antimatter^0.17, smoothly animated toward the target
        float target = Math.min((float) Math.pow(amount, 0.17) * NORMALIZE, MAX_RADIUS / 1.01f);
        if (shownScale <= 0) shownScale = target;
        shownScale = Mth.lerp(Math.min(1f, Minecraft.getInstance().getDeltaFrameTime() * 0.02f), shownScale, target);

        pose.pushPose();
        pose.translate(back.getStepX() * 16 + 0.5, back.getStepY() * 16 + 0.5, back.getStepZ() * 16 + 0.5);

        // core
        pose.pushPose();
        double angle = timer % (20 * 60 * 60);
        pose.mulPose(Axis.YP.rotationDegrees((float) (angle % 360)));
        pose.mulPose(Axis.XP.rotationDegrees((float) ((angle / 8) % 360)));
        pose.scale(shownScale, shownScale, shownScale);
        float time = (float) ((timer % 80) / 20);
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(WHITE));
        Matrix4f m = pose.last().pose();
        Matrix3f n = pose.last().normal();
        for (float[][] face : core) {
            for (int k = 0; k < 4; k++) {
                float[] v = face[Math.min(k, face.length - 1)];
                float t = triangle(fract(time / 4f + hash(v[0], v[1], v[2]) / 2f));
                vertex(vc, m, n, v, Mth.lerp(t, CORE_R, SPIKE_R), Mth.lerp(t, CORE_G, SPIKE_G), Mth.lerp(t, CORE_B, SPIKE_B));
            }
        }
        pose.popPose();

        // black containment shell (inside faces only, like GTNH's scale(-7))
        pose.pushPose();
        pose.scale(-MAX_RADIUS, -MAX_RADIUS, -MAX_RADIUS);
        vc = buffers.getBuffer(RenderType.entityCutout(WHITE));
        m = pose.last().pose();
        n = pose.last().normal();
        for (float[][] face : sphere) {
            for (int k = 0; k < 4; k++) vertex(vc, m, n, face[Math.min(k, face.length - 1)], 0.02f, 0.02f, 0.04f);
        }
        pose.popPose();

        // glow ring + protomatter beam while the forge runs
        if (machine.isProtomatterActive()) {
            vc = buffers.getBuffer(RenderType.entityCutoutNoCull(WHITE));
            m = pose.last().pose();
            n = pose.last().normal();
            for (float[][] face : ring) {
                for (int k = 0; k < 4; k++) vertex(vc, m, n, face[Math.min(k, face.length - 1)], 0f, 1f, 1f);
            }
            renderBeam(pose, vc, (float) (timer % 100), Math.min(shownScale / 2f, 0.5f));
        }
        pose.popPose();
    }

    /** 32 tumbling cubes spiralling down toward the core (GTNH protomatter.vert). */
    private static void renderBeam(PoseStack pose, VertexConsumer vc, float time, float spiral) {
        final int cubes = 32;
        final float maxDistance = 22.5f, loop = 100f, scale = 0.2f;
        for (int id = 0; id < cubes; id++) {
            float cubeTime = (time + id / (float) cubes * loop) % loop;
            float x = cubeTime / 20f;
            float dist = (float) Math.max(7.6331796059e-12 * Math.pow(x, 17.827335640), x / 10.0);
            float half = (float) Math.PI * (id % 2);
            float ox = Mth.sin(id / (float) cubes * 3 * Mth.PI + half) * spiral;
            float oz = Mth.cos(id / (float) cubes * 3 * Mth.PI + half) * spiral;
            float oy = maxDistance - dist;
            float size = Math.min((cubeTime / loop) / 0.8f, 1f) * scale;
            float tall = size * Math.max(1, maxDistance - oy);
            float width = Math.min(size / (float) Math.sqrt(Math.max(tall, 1e-4f)), size);
            float trim = Math.max(0, tall / 2 - oy);
            tall -= trim;
            oy += trim / 2;
            if (tall <= 0) continue;
            cube(pose, vc, ox, oy, oz, width, tall, 0.2f, 0.2f, 0.2f);
        }
    }

    private static void cube(PoseStack pose, VertexConsumer vc, float x, float y, float z, float w, float h,
                             float r, float g, float b) {
        Matrix4f m = pose.last().pose();
        Matrix3f n = pose.last().normal();
        float x0 = x - w / 2, x1 = x + w / 2, y0 = y - h / 2, y1 = y + h / 2, z0 = z - w / 2, z1 = z + w / 2;
        float[][] q = {
                { x0, y1, z1 }, { x0, y0, z1 }, { x1, y0, z1 }, { x1, y1, z1 },
                { x0, y0, z0 }, { x0, y1, z0 }, { x1, y1, z0 }, { x1, y0, z0 },
                { x0, y0, z0 }, { x0, y0, z1 }, { x0, y1, z1 }, { x0, y1, z0 },
                { x1, y0, z0 }, { x1, y1, z0 }, { x1, y1, z1 }, { x1, y0, z1 },
                { x0, y1, z0 }, { x0, y1, z1 }, { x1, y1, z1 }, { x1, y1, z0 },
                { x0, y0, z1 }, { x0, y0, z0 }, { x1, y0, z0 }, { x1, y0, z1 } };
        for (float[] v : q) vertex(vc, m, n, v, r, g, b);
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, Matrix3f n, float[] v, float r, float g, float b) {
        vc.vertex(m, v[0], v[1], v[2]).color(r, g, b, 1f).uv(0.5f, 0.5f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_BRIGHT).normal(n, 0, 1, 0).endVertex();
    }

    private static float fract(float x) {
        return x - (float) Math.floor(x);
    }

    private static float triangle(float x) {
        return 1f - Math.abs(2f * (x - 0.5f));
    }

    private static float hash(float x, float y, float z) {
        float vx = fract(x * 1.23456f + 3.1456f) * 7f;
        float vy = fract(y * 1.23456f + 3.1456f) * 7f;
        float vz = fract(z * 1.23456f + 3.1456f) * 7f;
        return fract(vy + vx * (vz + 1f));
    }

    private static synchronized boolean loadModels() {
        if (core != null) return true;
        if (failed) return false;
        try {
            core = obj("antimatter_core");
            sphere = obj("smooth_sphere");
            ring = obj("glow_ring");
            return true;
        } catch (Exception e) {
            failed = true;
            GTAA.LOGGER.error("[GTAA] could not load antimatter core models", e);
            return false;
        }
    }

    private static float[][][] obj(String name) throws Exception {
        ResourceLocation loc = GTAA.id("models/obj/" + name + ".obj");
        List<float[]> verts = new ArrayList<>();
        List<float[][]> faces = new ArrayList<>();
        try (var res = Minecraft.getInstance().getResourceManager().open(loc);
             var reader = new BufferedReader(new InputStreamReader(res, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.trim().split("\\s+");
                if (p.length < 4) continue;
                if (p[0].equals("v")) {
                    verts.add(new float[] { Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]) });
                } else if (p[0].equals("f")) {
                    float[][] face = new float[Math.min(4, p.length - 1)][];
                    for (int k = 0; k < face.length; k++) face[k] = verts.get(Integer.parseInt(p[k + 1].split("/")[0]) - 1);
                    faces.add(face);
                }
            }
        }
        return faces.toArray(float[][][]::new);
    }

    @Override
    public boolean shouldRenderOffScreen(AntimatterForgeMachine machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public AABB getRenderBoundingBox(AntimatterForgeMachine machine) {
        return new AABB(machine.getPos()).inflate(40);
    }
}
