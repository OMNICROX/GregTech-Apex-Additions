package com.omnicrox.gtaa.client.preview;

import com.lowdragmc.lowdraglib.client.scene.WorldSceneRenderer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * LDLib's FBO scene renderer, but it puts back the framebuffer AND the viewport it found. LDLib's own one leaves the
 * viewport at the FBO's size, which is fine in its own screens but blanks everything drawn after it inside NEI's
 * recipe pages (that was the empty grey box).
 */
public class ApexSceneRenderer extends WorldSceneRenderer {

    private RenderTarget target;
    private int width, height;

    public ApexSceneRenderer(Level world, int width, int height) {
        super(world);
        resize(width, height);
    }

    public void resize(int w, int h) {
        if (target != null && w == width && h == height) return;
        width = w;
        height = h;
        RenderSystem.assertOnRenderThread();
        if (target != null) target.destroyBuffers();
        target = new TextureTarget(w, h, true, Minecraft.ON_OSX);
    }

    public void release() {
        if (target != null) {
            target.destroyBuffers();
            target = null;
        }
    }

    public void draw(PoseStack pose, float x, float y, float w, float h, float mouseX, float mouseY) {
        if (target == null) return;
        int prevFb = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int[] vp = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, vp);
        // a page that clips (GuideME's scrolling document) leaves the scissor test on, in screen coordinates: inside
        // the FBO that cuts the scene down to a sliver, so switch it off while drawing the scene
        boolean scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        int[] box = new int[4];
        if (scissor) {
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, box);
            GlStateManager._disableScissorTest();
        }
        try {
            target.setClearColor(0, 0, 0, 0);
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);
            int mx = (int) (width * (mouseX - x) / w), my = (int) (height * (1 - (mouseY - y) / h));
            super.render(new PoseStack(), 0, 0, width, height, mx, my);
        } finally {
            target.unbindWrite();
            GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFb);
            RenderSystem.viewport(vp[0], vp[1], vp[2], vp[3]);
            if (scissor) {
                GlStateManager._enableScissorTest();
                GlStateManager._scissorBox(box[0], box[1], box[2], box[3]);
            }
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, target.getColorTextureId());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Matrix4f m = pose.last().pose();
        BufferBuilder b = Tesselator.getInstance().getBuilder();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, x + w, y + h, 0).uv(1, 0).endVertex();
        b.vertex(m, x + w, y, 0).uv(1, 1).endVertex();
        b.vertex(m, x, y, 0).uv(0, 1).endVertex();
        b.vertex(m, x, y + h, 0).uv(0, 0).endVertex();
        Tesselator.getInstance().end();
    }
}
