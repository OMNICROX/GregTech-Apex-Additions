package com.omnicrox.gtaa.client;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.registry.registrate.forge.GTClientFluidTypeExtensions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAAMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Client-only visuals:
 *  - GTNH fluid textures for gtna's Spatially Enlarged / Tachyon Rich Temporal fluids
 *  - built-in "GTNH textures" pack (fast Infinity animation, GTNH art)
 *  - GTNH-style dark halo + pulse behind Infinity items in inventories (GT5U InfinityRenderer)
 */
public final class GTAAClient {

    private GTAAClient() {}

    private static final ResourceLocation HALO = GTAA.id("textures/gui/halo.png");
    private static final Random RAND = new Random();
    private static final Map<Item, Boolean> INFINITY_CACHE = new IdentityHashMap<>();
    private static Material infinity;

    @Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModEvents {

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                retexture(GTAAMaterials.SPATIALLY_ENLARGED_FLUID, "spatial_fluid");
                retexture(GTAAMaterials.TACHYON_RICH_TEMPORAL_FLUID, "temporal_fluid");
            });
        }
    }

    private static void retexture(Material material, String texture) {
        if (material == null || material.isNull()) return;
        try {
            Fluid fluid = material.getFluid();
            if (fluid == null) return;
            if (IClientFluidTypeExtensions.of(fluid) instanceof GTClientFluidTypeExtensions ext) {
                ResourceLocation tex = GTAA.id("block/fluids/fluid." + texture);
                ext.setStillTexture(tex);
                ext.setFlowingTexture(tex);
                ext.setTintColor(0xFFFFFFFF);
            }
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[GTAA] could not retexture {}: {}", material.getName(), t.toString());
        }
    }

    private static boolean isInfinity(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return INFINITY_CACHE.computeIfAbsent(stack.getItem(), item -> {
            if (infinity == null) {
                infinity = GTCEuAPI.materialManager.getMaterial("gtna:infinity");
                if (infinity == null) return false;
            }
            try {
                MaterialEntry entry = ChemicalHelper.getMaterialEntry(item);
                return entry != null && entry.material() == infinity;
            } catch (Throwable t) {
                return false;
            }
        });
    }

    @Mod.EventBusSubscriber(modid = GTAA.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeEvents {

        @SubscribeEvent
        public static void containerBackground(ContainerScreenEvent.Render.Background event) {
            AbstractContainerScreen<?> screen = event.getContainerScreen();
            GuiGraphics g = event.getGuiGraphics();
            boolean any = false;
            for (Slot slot : screen.getMenu().slots) {
                if (!slot.isActive() || !isInfinity(slot.getItem())) continue;
                int x = screen.getGuiLeft() + slot.x;
                int y = screen.getGuiTop() + slot.y;
                if (!any) {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    any = true;
                }
                renderHalo(g, x, y);
                renderPulse(g, slot.getItem(), x, y);
            }
            if (any) {
                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }
        }
    }

    /** Dark fuzzy square, 10px past every side of the slot (GT5U InfinityRenderer.renderHalo). */
    public static void renderHalo(GuiGraphics g, int x, int y) {
        int spread = 10;
        RenderSystem.setShaderColor(20 / 255f, 20 / 255f, 20 / 255f, 1f);
        g.blit(HALO, x - spread, y - spread, 16 + 2 * spread, 16 + 2 * spread, 0, 0, 32, 32, 32, 32);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    /** Randomly scaled translucent copy of the item sprite (GT5U InfinityRenderer.renderPulse). */
    public static void renderPulse(GuiGraphics g, ItemStack stack, int x, int y) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getItemRenderer()
                .getModel(stack, null, null, 0).getParticleIcon();
        if (sprite == null) return;
        float scale = (float) RAND.nextGaussian() * 0.15f + 0.95f;
        float size = 16 * scale;
        float off = (16 - size) / 2f;
        g.pose().pushPose();
        g.pose().translate(x + off, y + off, 0);
        g.pose().scale(scale, scale, 1);
        RenderSystem.setShaderColor(1, 1, 1, 0.6f);
        g.blit(0, 0, 0, 16, 16, sprite);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        g.pose().popPose();
    }
}
