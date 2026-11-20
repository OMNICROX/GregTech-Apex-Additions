package com.omnicrox.gtaa.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.machine.multiblock.SuitFabricationStationMachine;
import com.omnicrox.gtaa.common.machine.multiblock.suit.SuitCatalog;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Suit Fabrication Station hologram: the picked (or last made) suit turning on a holo pad in the middle of the
 * booth, with a cyan pad, a light column and rising scan rings while it works - the GW2 customisation booth.
 */
public class SuitHologramRender extends DynamicRender<SuitFabricationStationMachine, SuitHologramRender> {

    public static final Codec<SuitHologramRender> CODEC = Codec.unit(SuitHologramRender::new);
    public static final DynamicRenderType<SuitFabricationStationMachine, SuitHologramRender> TYPE = new DynamicRenderType<>(CODEC);
    private static final ResourceLocation WHITE = GTAA.id("textures/misc/white.png");

    @Override
    public DynamicRenderType<SuitFabricationStationMachine, SuitHologramRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(SuitFabricationStationMachine machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public void render(SuitFabricationStationMachine m, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        BlockPos off = m.padOffset();
        BlockPos pad = m.getPos().offset(off);
        // stand on top of whatever is on the pad spot
        boolean blocked = !m.getLevel().getBlockState(pad).isAir();
        double time = m.getOffsetTimer() + partialTick;
        boolean working = m.getRecipeLogic().isWorking();

        pose.pushPose();
        pose.translate(off.getX() + 0.5, off.getY() + (blocked ? 1.0 : 0.0), off.getZ() + 0.5);

        VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucentEmissive(WHITE));
        Matrix4f mat = pose.last().pose();
        Matrix3f n = pose.last().normal();
        float glow = working ? 0.85f + 0.15f * Mth.sin((float) time * 0.3f) : 0.6f + 0.1f * Mth.sin((float) time * 0.1f);
        // pad: two flat rings
        ring(vc, mat, n, 0.02f, 0.85f, 1.15f, 0.37f, 0.95f, 1f, glow, (float) time * 2f);
        ring(vc, mat, n, 0.03f, 0.4f, 0.6f, 0.82f, 0.48f, 1f, glow, -(float) time * 3f);
        if (working) {
            // light column + two scan rings climbing the suit
            column(vc, mat, n, 0.65f, 2.2f, 0.37f, 0.95f, 1f, 0.22f);
            for (int i = 0; i < 2; i++) {
                float h = (float) ((time * 0.04 + i * 0.5) % 1.0) * 2.1f;
                ring(vc, mat, n, h, 0.6f, 0.7f, 0.37f, 0.95f, 1f, 0.6f * (1 - h / 2.2f), (float) time * 4f);
            }
        }

        SuitCatalog.Entry e = SuitCatalog.get(m.getDisplaySuit());
        if (e != null) {
            float yaw = (float) (time * (working ? 3.0 : 1.2)) % 360f;
            SuitPreviewClient.renderInWorld(pose, buffers, LightTexture.FULL_BRIGHT, yaw, e.armor());
        }
        pose.popPose();
    }

    private static void ring(VertexConsumer vc, Matrix4f m, Matrix3f n, float y, float r0, float r1,
                             float r, float g, float b, float a, float spinDeg) {
        int seg = 32;
        float spin = spinDeg * Mth.DEG_TO_RAD;
        for (int i = 0; i < seg; i++) {
            if (i % 4 == 3) continue; // dashed
            float a0 = spin + i * Mth.TWO_PI / seg, a1 = spin + (i + 1) * Mth.TWO_PI / seg;
            v(vc, m, n, Mth.cos(a0) * r0, y, Mth.sin(a0) * r0, r, g, b, a);
            v(vc, m, n, Mth.cos(a0) * r1, y, Mth.sin(a0) * r1, r, g, b, a);
            v(vc, m, n, Mth.cos(a1) * r1, y, Mth.sin(a1) * r1, r, g, b, a);
            v(vc, m, n, Mth.cos(a1) * r0, y, Mth.sin(a1) * r0, r, g, b, a);
        }
    }

    private static void column(VertexConsumer vc, Matrix4f m, Matrix3f n, float rad, float h,
                               float r, float g, float b, float a) {
        int seg = 16;
        for (int i = 0; i < seg; i++) {
            float a0 = i * Mth.TWO_PI / seg, a1 = (i + 1) * Mth.TWO_PI / seg;
            float x0 = Mth.cos(a0) * rad, z0 = Mth.sin(a0) * rad, x1 = Mth.cos(a1) * rad, z1 = Mth.sin(a1) * rad;
            v(vc, m, n, x0, 0, z0, r, g, b, a);
            v(vc, m, n, x0, h, z0, r, g, b, 0);
            v(vc, m, n, x1, h, z1, r, g, b, 0);
            v(vc, m, n, x1, 0, z1, r, g, b, a);
        }
    }

    private static void v(VertexConsumer vc, Matrix4f m, Matrix3f n, float x, float y, float z,
                          float r, float g, float b, float a) {
        vc.vertex(m, x, y, z).color(r, g, b, a).uv(0.5f, 0.5f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT).normal(n, 0, 1, 0).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(SuitFabricationStationMachine machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public AABB getRenderBoundingBox(SuitFabricationStationMachine machine) {
        return new AABB(machine.getPos()).inflate(5);
    }
}
