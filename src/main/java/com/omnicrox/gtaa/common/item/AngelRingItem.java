package com.omnicrox.gtaa.common.item;

import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAAItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Angel Ring: creative-style flight while it is worn in a Curios ring slot or carried anywhere in the inventory.
 * Flight granted by the ring is taken away again when the ring is gone (creative / spectator players untouched).
 * Curios is read through reflection, so it is optional.
 */
public class AngelRingItem extends Item {

    private static final String TAG = "gtapexadditions_angel_flight";
    private static Method curiosInventory;
    private static Method findFirstCurio;
    private static boolean curiosLookedUp;

    public AngelRingItem(Properties props) {
        super(props.stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    }

    public static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(AngelRingItem.class);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Lets you fly while worn as a ring or carried").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("Forged from the sky itself").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    private static boolean wornAsCurio(LivingEntity entity, Item item) {
        if (!curiosLookedUp) {
            curiosLookedUp = true;
            try {
                Class<?> api = Class.forName("top.theillusivec4.curios.api.CuriosApi");
                curiosInventory = api.getMethod("getCuriosInventory", LivingEntity.class);
                findFirstCurio = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler")
                        .getMethod("findFirstCurio", Item.class);
            } catch (Throwable t) {
                curiosInventory = null;
                GTAA.LOGGER.info("Angel Ring: Curios not found, the ring works from the inventory only");
            }
        }
        if (curiosInventory == null) return false;
        try {
            Object lazy = curiosInventory.invoke(null, entity);
            Optional<?> handler = (Optional<?>) lazy.getClass().getMethod("resolve").invoke(lazy);
            if (handler.isEmpty()) return false;
            Optional<?> found = (Optional<?>) findFirstCurio.invoke(handler.get(), item);
            return found.isPresent();
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean hasRing(Player player) {
        Item ring = GTAAItems.ANGEL_RING.get();
        return player.getInventory().contains(new ItemStack(ring)) || wornAsCurio(player, ring);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
        Player player = event.player;
        if (player.tickCount % 10 != 0) return;
        boolean granted = player.getPersistentData().getBoolean(TAG);
        boolean has = hasRing(player);
        if (has && !player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
            player.getPersistentData().putBoolean(TAG, true);
        } else if (!has && granted) {
            player.getPersistentData().remove(TAG);
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }
}
