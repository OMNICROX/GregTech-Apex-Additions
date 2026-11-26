package com.omnicrox.gtaa.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.entity.AscendingMaxwellEntity;
import com.omnicrox.gtaa.common.entity.GTAAEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Draws the ascending Maxwell with Placeable Maxwell's model, tumbling as he comes through the gate. */
public class AscendingMaxwellRenderer extends EntityRenderer<AscendingMaxwellEntity> {

    public AscendingMaxwellRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        shadowRadius = 0.3f;
    }

    @Override
    public void render(AscendingMaxwellEntity e, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        float t = e.age() + partial;
        boolean flying = e.age() < AscendingMaxwellEntity.FLIGHT_TICKS;
        pose.pushPose();
        pose.translate(0, 0.3, 0);
        pose.mulPose(Axis.YP.rotationDegrees(flying ? t * 24 : 180 - e.facing().toYRot()));
        if (flying) pose.mulPose(Axis.XP.rotationDegrees(t * 11));
        float scale = flying ? 1.0f + 0.6f * (float) Math.sin(Math.min(1, t / 20f) * Math.PI / 2) : 1.6f;
        pose.scale(scale, scale, scale);
        pose.translate(-0.5, -0.3, -0.5);
        if (BuiltInRegistries.BLOCK.containsKey(AscendingMaxwellEntity.MAXWELL_BLOCK)) {
            BlockState s = BuiltInRegistries.BLOCK.get(AscendingMaxwellEntity.MAXWELL_BLOCK).defaultBlockState();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(s, pose, buffers, flying ? 0xF000F0 : light,
                    OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        }
        pose.popPose();
        super.render(e, yaw, partial, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(AscendingMaxwellEntity e) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(GTAAEntities.ASCENDING_MAXWELL.get(), AscendingMaxwellRenderer::new);
        }
    }
}
