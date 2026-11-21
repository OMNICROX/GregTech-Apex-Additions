package com.omnicrox.gtaa.common.item;

import com.omnicrox.gtaa.common.infernal.InfernalMobs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Put in an Extreme Entity Crusher input bus: it crushes that infernal rank of the mob (not used up). */
public class InfernalSigilItem extends Item {

    public final InfernalMobs.Rank rank;

    public InfernalSigilItem(InfernalMobs.Rank rank, Properties props) {
        super(props.stacksTo(1).rarity(rank == InfernalMobs.Rank.ELITE ? Rarity.UNCOMMON :
                rank == InfernalMobs.Rank.ULTRA ? Rarity.RARE : Rarity.EPIC));
        this.rank = rank;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Extreme Entity Crusher: every hostile mob crushed is infernal").withStyle(rank.color));
        tooltip.add(Component.literal(switch (rank) {
            case ELITE -> "Normal rank odds (Ultra 1 in 10, Infernal 1 in 70)";
            case ULTRA -> "Better rank odds (Ultra 1 in 3, Infernal 1 in 21)";
            case INFERNAL -> "Best rank odds (Ultra 1 in 2, Infernal 1 in 6)";
        }).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Rare-rank drops still roll, so lower ranks stay useful").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal("Put it in an input bus; it isn't used up").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return rank != InfernalMobs.Rank.ELITE;
    }
}
