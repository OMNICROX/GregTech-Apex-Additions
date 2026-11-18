package com.omnicrox.gtaa.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.block.SpaceElevatorCableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Space Elevator cable + climber. Port of GTNH-Intergalactic's RenderSpaceElevatorCable (MIT):
 * an octagonal cable 512 blocks tall and the climber model (model by Adam, texture/UV by Jimbno).
 */
public class SpaceElevatorCableRenderer implements BlockEntityRenderer<SpaceElevatorCableBlockEntity> {

    private static final ResourceLocation CABLE_TEX = GTAA.id("textures/block/space_elevator/cable.png");
    private static final ResourceLocation CLIMBER_TEX = GTAA.id("textures/entity/climber.png");
    private static final ResourceLocation CLIMBER_OBJ = GTAA.id("models/obj/climber.obj");

    private static final float LONG = (float) ((1.0 + Math.sqrt(2.0)) / 5.4);
    private static final float SHORT = (float) (1.0 / 5.4);
    private static final float[] EDGE_X = { LONG, LONG, SHORT, -SHORT, -LONG, -LONG, -SHORT, SHORT };
    private static final float[] EDGE_Z = { SHORT, -SHORT, -LONG, -LONG, -SHORT, SHORT, LONG, LONG };
    private static final float CABLE_HEIGHT = 512f;
    private static final float CLIMBER_OFFSET = 50f;
    private static final int MIN_CLIMBER_HEIGHT = 100;

    /** climber triangles: x,y,z,u,v,nx,ny,nz per vertex */
    private static float[] climber;
    private static boolean climberFailed;

    public SpaceElevatorCableRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(SpaceElevatorCableBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        if (!be.shouldRender()) return;
        // Shader-friendly: only cutout render types and real world lighting (no translucent/emissive layers),
        // so shader packs (Complementary etc.) don't flicker between lit and unlit passes.
        renderCable(pose, buffers, light);
        renderClimber(be, partialTick, pose, buffers);
    }

    private void renderCable(PoseStack pose, MultiBufferSource buffers, int light) {
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(CABLE_TEX));
        Matrix4f m = pose.last().pose();
        Matrix3f n = pose.last().normal();
        int fullBright = skyLit(light);
        int c = 255;
        float grow = 0f;
        float vMax = CABLE_HEIGHT / 2f; // texture repeats every 2 blocks
        for (int i = 0; i < 8; i++) {
            int j = (i + 1) % 8;
            float x0 = 0.5f + EDGE_X[i] * (1 + grow), z0 = 0.5f + EDGE_Z[i] * (1 + grow);
            float x1 = 0.5f + EDGE_X[j] * (1 + grow), z1 = 0.5f + EDGE_Z[j] * (1 + grow);
            float nx = (EDGE_X[i] + EDGE_X[j]) / 2f, nz = (EDGE_Z[i] + EDGE_Z[j]) / 2f;
            float y0 = 0.5f, y1 = 0.5f + CABLE_HEIGHT;
            vertex(vc, m, n, x0, y0, z0, 0, vMax, nx, 0, nz, fullBright, c);
            vertex(vc, m, n, x1, y0, z1, 1, vMax, nx, 0, nz, fullBright, c);
            vertex(vc, m, n, x1, y1, z1, 1, 0, nx, 0, nz, fullBright, c);
            vertex(vc, m, n, x0, y1, z0, 0, 0, nx, 0, nz, fullBright, c);
        }
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, Matrix3f n, float x, float y, float z, float u, float v,
                               float nx, float ny, float nz, int light, int c) {
        vc.vertex(m, x, y, z).color(c, c, c, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(n, nx, ny, nz).endVertex();
    }

    private void renderClimber(SpaceElevatorCableBlockEntity be, float partialTick, PoseStack pose,
                               MultiBufferSource buffers) {
        float[] tris = climber();
        if (tris == null) return;
        int baseY = be.getBlockPos().getY();
        float offset = (CLIMBER_OFFSET + baseY) < MIN_CLIMBER_HEIGHT ? MIN_CLIMBER_HEIGHT - baseY : CLIMBER_OFFSET;
        pose.pushPose();
        pose.translate(0.5, 0.5 + be.climberHeight(partialTick) + offset, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(be.climberRotation(partialTick)));
        pose.scale(4f, 4f, 4f);
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(CLIMBER_TEX));
        Matrix4f m = pose.last().pose();
        Matrix3f n = pose.last().normal();
        int light = skyLit(LightTexture.FULL_BRIGHT);
        for (int i = 0; i < tris.length; i += 8) {
            vc.vertex(m, tris[i], tris[i + 1], tris[i + 2]).color(255, 255, 255, 255).uv(tris[i + 3], tris[i + 4])
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, tris[i + 5], tris[i + 6], tris[i + 7])
                    .endVertex();
            // the vertex format is quads: emit each triangle's last vertex twice
            if ((i / 8) % 3 == 2) {
                vc.vertex(m, tris[i], tris[i + 1], tris[i + 2]).color(255, 255, 255, 255).uv(tris[i + 3], tris[i + 4])
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                        .normal(n, tris[i + 5], tris[i + 6], tris[i + 7]).endVertex();
            }
        }
        pose.popPose();
    }

    /** Up in the sky: full sky light, block light from the cable position. */
    private static int skyLit(int light) {
        return LightTexture.pack(LightTexture.block(light), 15);
    }

    private static synchronized float[] climber() {
        if (climber != null || climberFailed) return climber;
        try (var res = Minecraft.getInstance().getResourceManager().open(CLIMBER_OBJ);
             var reader = new BufferedReader(new InputStreamReader(res, StandardCharsets.UTF_8))) {
            List<float[]> v = new ArrayList<>(), vt = new ArrayList<>(), vn = new ArrayList<>();
            List<Float> out = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.trim().split("\\s+");
                if (p.length == 0) continue;
                switch (p[0]) {
                    case "v" -> v.add(new float[] { Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]) });
                    case "vt" -> vt.add(new float[] { Float.parseFloat(p[1]), 1f - Float.parseFloat(p[2]) });
                    case "vn" -> vn.add(new float[] { Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]) });
                    case "f" -> {
                        for (int k = 1; k <= 3 && k < p.length; k++) {
                            String[] idx = p[k].split("/");
                            float[] pos = v.get(Integer.parseInt(idx[0]) - 1);
                            float[] uv = idx.length > 1 && !idx[1].isEmpty() ? vt.get(Integer.parseInt(idx[1]) - 1) : new float[] { 0, 0 };
                            float[] nn = idx.length > 2 ? vn.get(Integer.parseInt(idx[2]) - 1) : new float[] { 0, 1, 0 };
                            out.add(pos[0]); out.add(pos[1]); out.add(pos[2]);
                            out.add(uv[0]); out.add(uv[1]);
                            out.add(nn[0]); out.add(nn[1]); out.add(nn[2]);
                        }
                    }
                    default -> {}
                }
            }
            float[] arr = new float[out.size()];
            for (int i = 0; i < arr.length; i++) arr[i] = out.get(i);
            climber = arr;
        } catch (Exception e) {
            GTAA.LOGGER.error("Failed to load the space elevator climber model", e);
            climberFailed = true;
        }
        return climber;
    }

    @Override
    public boolean shouldRenderOffScreen(SpaceElevatorCableBlockEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 512;
    }

    @Override
    public boolean shouldRender(SpaceElevatorCableBlockEntity be, Vec3 cameraPos) {
        return be.shouldRender();
    }
}
