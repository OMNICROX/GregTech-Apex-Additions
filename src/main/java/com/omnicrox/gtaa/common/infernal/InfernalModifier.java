package com.omnicrox.gtaa.common.infernal;

import net.minecraft.ChatFormatting;

/** Clean-room take on the classic Infernal Mobs modifiers, plus a few GregTech-flavoured ones. */
public enum InfernalModifier {
    VAMPIRIC("Vampiric", ChatFormatting.DARK_RED),
    ENDER("Ender", ChatFormatting.DARK_PURPLE),
    BLASTOFF("Blastoff", ChatFormatting.WHITE),
    STICKY("Sticky", ChatFormatting.GREEN),
    WITHERING("Withering", ChatFormatting.DARK_GRAY),
    POISONOUS("Poisonous", ChatFormatting.DARK_GREEN),
    FIERY("Fiery", ChatFormatting.GOLD),
    FREEZING("Freezing", ChatFormatting.AQUA),
    GRAVITY("Gravity", ChatFormatting.LIGHT_PURPLE),
    REGENERATING("Regenerating", ChatFormatting.RED),
    ARMORED("Armored", ChatFormatting.GRAY),
    RUSTING("Rusting", ChatFormatting.GOLD),
    SWIFT("Swift", ChatFormatting.YELLOW),
    BERSERK("Berserk", ChatFormatting.RED),
    THORNY("Thorny", ChatFormatting.DARK_GREEN),
    STORM("Storm", ChatFormatting.BLUE),
    BLINDING("Blinding", ChatFormatting.BLACK),
    CLOAKING("Cloaking", ChatFormatting.DARK_AQUA),
    UNSTABLE("Unstable", ChatFormatting.RED),
    IRRADIATED("Irradiated", ChatFormatting.GREEN),
    MAGNETIC("Magnetic", ChatFormatting.BLUE);

    public final String title;
    public final ChatFormatting color;

    InfernalModifier(String title, ChatFormatting color) {
        this.title = title;
        this.color = color;
    }

    public String id() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
