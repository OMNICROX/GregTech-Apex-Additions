package com.omnicrox.gtaa.common.guide;

import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.config.GTAACommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** the Apex Guide on a player's first join (gtapexadditions-common.toml: guide.giveOnFirstJoin) */
@Mod.EventBusSubscriber(modid = GTAA.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GuideGift {

    private GuideGift() {}

    private static final String TAG = "gtapexadditions_guide_given";

    public static ItemStack guide() {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("guideme", "guide"));
        if (item == null) return ItemStack.EMPTY;
        ItemStack s = new ItemStack(item);
        CompoundTag t = new CompoundTag();
        t.putString("guideId", GTAA.id("guide").toString());
        s.setTag(t);
        return s;
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        Player p = e.getEntity();
        if (p.level().isClientSide || !GTAACommonConfig.GIVE_GUIDE.get()) return;
        CompoundTag data = p.getPersistentData();
        CompoundTag keep = data.getCompound(Player.PERSISTED_NBT_TAG);
        if (keep.getBoolean(TAG)) return;
        ItemStack g = guide();
        if (g.isEmpty()) return;
        if (!p.getInventory().add(g)) p.drop(g, false);
        keep.putBoolean(TAG, true);
        data.put(Player.PERSISTED_NBT_TAG, keep);
    }
}
