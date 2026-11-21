package com.omnicrox.gtaa.common.item;

import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Maxwell: the Ascension Gate's prize. Completely useless and very important. Right-click: he meows and shows your
 * Ascension credits again. Can't burn. Any cat named Maxwell spins while sitting.
 */
public class MaxwellItem extends Item {

    public MaxwellItem(Properties props) {
        super(props.stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    public static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(MaxwellItem.class);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("He came through the Ascension Gate.").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("He does nothing. He is perfect.").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        tooltip.add(Component.literal("Right-click: Maxwell").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(null, player.blockPosition(), SoundEvents.CAT_AMBIENT, SoundSource.PLAYERS, 1.0f,
                0.9f + level.random.nextFloat() * 0.3f);
        if (player instanceof ServerPlayer sp)
            GTAANetwork.send(sp, new GTAANetwork.AscensionPacket(false, stats(sp)));
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    /** the credits: what this player did on the way here */
    public static List<String> stats(ServerPlayer p) {
        var s = p.getStats();
        List<String> l = new ArrayList<>();
        long ticks = s.getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        long mined = 0, crafted = 0;
        for (var b : net.minecraft.core.registries.BuiltInRegistries.BLOCK) mined += s.getValue(Stats.BLOCK_MINED.get(b));
        for (var i : net.minecraft.core.registries.BuiltInRegistries.ITEM) crafted += s.getValue(Stats.ITEM_CRAFTED.get(i));
        long walked = s.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM)) + s.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        long flown = s.getValue(Stats.CUSTOM.get(Stats.FLY_ONE_CM)) + s.getValue(Stats.CUSTOM.get(Stats.AVIATE_ONE_CM));
        l.add("Ascended: " + p.getGameProfile().getName());
        l.add("Day " + (p.level().getDayTime() / 24000L + 1) + " of the world");
        l.add(String.format("Time played: %,d hours %d minutes", ticks / 72000, (ticks / 1200) % 60));
        l.add(String.format("Blocks mined: %,d", mined));
        l.add(String.format("Things crafted: %,d", crafted));
        l.add(String.format("Mobs defeated: %,d", s.getValue(Stats.CUSTOM.get(Stats.MOB_KILLS))));
        l.add(String.format("Deaths: %,d", s.getValue(Stats.CUSTOM.get(Stats.DEATHS))));
        l.add(String.format("Walked: %,d km   Flown: %,d km", walked / 100000, flown / 100000));
        l.add(String.format("Jumps: %,d", s.getValue(Stats.CUSTOM.get(Stats.JUMP))));
        l.add("Maxwell approves.");
        return l;
    }

    /** Maxwell (any cat named Maxwell) spins while sitting. Both sides, so it looks smooth. */
    @SubscribeEvent
    public static void spin(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Cat cat) || !cat.isInSittingPose() || !cat.hasCustomName()) return;
        if (!"Maxwell".equalsIgnoreCase(cat.getCustomName().getString())) return;
        float r = cat.getYRot() + 18f;
        cat.setYRot(r);
        cat.yBodyRot = r;
        cat.yHeadRot = r;
        cat.yRotO = r - 18f;
        cat.yBodyRotO = r - 18f;
        cat.yHeadRotO = r - 18f;
    }
}
