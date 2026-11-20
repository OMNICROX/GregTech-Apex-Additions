package com.omnicrox.gtaa.client;

import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.omnicrox.gtaa.common.machine.multiblock.suit.SuitCatalog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Client side of the Suit Fabrication Station: the dressed-up dummy used by the picker preview and the hologram. */
public final class SuitPreviewClient {

    private SuitPreviewClient() {}

    private static RemotePlayer dummy;
    private static boolean broken;
    private static final Map<String, List<ItemStack>> PARTS = new HashMap<>();

    private static RemotePlayer dummy() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || broken) return null;
        if (dummy == null || dummy.level() != mc.level) {
            try {
                dummy = new RemotePlayer(mc.level, mc.player.getGameProfile());
            } catch (Throwable t) {
                broken = true;
                return null;
            }
        }
        return dummy;
    }

    /** The dummy wearing this suit (null if it can't be made). */
    public static LivingEntity dressed(List<Item> armor) {
        RemotePlayer p = dummy();
        if (p == null) return null;
        EquipmentSlot[] order = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
        for (EquipmentSlot s : order) p.setItemSlot(s, ItemStack.EMPTY);
        for (int i = 0; i < armor.size(); i++) {
            ItemStack stack = new ItemStack(armor.get(i));
            EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
            if (slot.getType() != EquipmentSlot.Type.ARMOR) slot = order[Math.min(i, 3)];
            p.setItemSlot(slot, stack);
        }
        p.tickCount = (int) (Minecraft.getInstance().level.getGameTime() % 100000);
        return p;
    }

    /** Draws the suited dummy in a GUI, feet at (cx, feetY), turned by yaw (radians). */
    public static void renderInGui(GuiGraphics g, int cx, int feetY, int scale, float yaw, List<Item> armor) {
        LivingEntity e = dressed(armor);
        if (e == null) return;
        e.yBodyRot = 180;
        e.yBodyRotO = 180;
        e.setYRot(180);
        e.yRotO = 180;
        e.yHeadRot = 180;
        e.yHeadRotO = 180;
        e.setXRot(0);
        e.xRotO = 0;
        try {
            Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI).rotateY(yaw);
            InventoryScreen.renderEntityInInventory(g, cx, feetY, scale, pose, null, e);
        } catch (Throwable t) {
            broken = true;
        }
    }

    /** Draws the suited dummy in the world (the station hologram); pose is already at the pad. */
    public static void renderInWorld(PoseStack pose, MultiBufferSource buffers, int light, float yawDeg, List<Item> armor) {
        LivingEntity e = dressed(armor);
        if (e == null) return;
        e.yBodyRot = e.yBodyRotO = e.yHeadRot = e.yHeadRotO = 0;
        e.setYRot(0);
        e.yRotO = 0;
        try {
            pose.pushPose();
            pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(yawDeg));
            Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(e)
                    .render(e, 0, 0, pose, buffers, light);
            pose.popPose();
        } catch (Throwable t) {
            pose.popPose();
            broken = true;
        }
    }

    /** Every input of the suit's station recipe (parts + tier extras), as the client sees it. */
    public static List<ItemStack> parts(SuitCatalog.Entry e) {
        return PARTS.computeIfAbsent(e.suit(), k -> {
            List<ItemStack> out = new ArrayList<>();
            try {
                var level = Minecraft.getInstance().level;
                var r = level == null ? null : level.getRecipeManager().byKey(e.recipeId()).orElse(null);
                if (r instanceof GTRecipe gt) {
                    for (Content c : gt.inputs.getOrDefault(ItemRecipeCapability.CAP, List.of())) {
                        if (c.content instanceof IntCircuitIngredient) continue;
                        Ingredient ing = ItemRecipeCapability.CAP.of(c.content);
                        ItemStack[] items = ing.getItems();
                        if (items.length == 0) continue;
                        int n = c.content instanceof SizedIngredient s ? s.getAmount() : 1;
                        out.add(items[0].copyWithCount(n));
                    }
                }
            } catch (Throwable ignored) {}
            if (out.isEmpty()) for (ItemStack s : e.parts()) out.add(s.copy());
            return out;
        });
    }
}
