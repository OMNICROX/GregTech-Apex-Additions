package com.omnicrox.apexguide.internal.item;

import com.omnicrox.apexguide.internal.GuideME;
import com.omnicrox.apexguide.internal.GuideMEProxy;
import com.omnicrox.apexguide.internal.GuidebookText;
import java.util.List;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GuideItem extends Item {
    public static final ResourceLocation ID = GuideME.makeId("guide");
    public static final ResourceLocation BASE_MODEL_ID = ID.withPrefix("item/").withSuffix("_base");

    public static final Properties PROPERTIES = new Properties();

    public static final String TAG_GUIDE_ID = "guideId";

    public GuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        var guideId = getGuideId(stack);
        if (guideId != null) {
            var name = GuideMEProxy.instance().getGuideDisplayName(guideId);
            if (name != null) {
                return name;
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag tooltipFlag) {
        var guideId = getGuideId(stack);
        if (guideId != null) {
            GuideMEProxy.instance().addGuideTooltip(
                    guideId,
                    lines,
                    tooltipFlag);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);

        var guideId = getGuideId(player.getItemInHand(hand));

        if (level.isClientSide) {
            if (guideId == null) {
                player.sendSystemMessage(GuidebookText.ItemNoGuideId.text());
            } else if (GuideMEProxy.instance().openGuide(player, guideId)) {
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return InteractionResultHolder.success(stack);
    }

    /**
     * The guide this book opens. A book without a guide id, or with one that isn't registered, falls back to the
     * pack's guide when exactly one guide is registered (Apex Guide behavior), so a plain book from /give still works.
     */
    @Nullable
    public static ResourceLocation getGuideId(ItemStack stack) {
        var tag = stack.getTag();
        ResourceLocation id = null;
        if (tag != null && tag.contains(TAG_GUIDE_ID, Tag.TAG_STRING)) {
            id = ResourceLocation.tryParse(tag.getString(TAG_GUIDE_ID));
        }
        if (id == null || com.omnicrox.apexguide.internal.GuideRegistry.getById(id) == null) {
            var all = com.omnicrox.apexguide.internal.GuideRegistry.getAll();
            if (all.size() == 1) {
                return all.iterator().next().getId();
            }
        }
        return id;
    }
}
