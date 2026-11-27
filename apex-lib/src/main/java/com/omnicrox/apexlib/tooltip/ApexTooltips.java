package com.omnicrox.apexlib.tooltip;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * GTNH-style controller tooltips, the same layout on every Apex machine:
 * machine type, a flavour line, what it does, the numbers, then where it was ported from.
 * <pre>
 * ApexTooltips.of("Rock Breaker").flavor("Hey, I heard you come from a sky island?")
 *     .info("Runs every Industrial Rock Breaker recipe").parallel("4, x4 per voltage tier above LV")
 *     .portedFrom("Twist Space Technology").build()
 * </pre>
 */
public final class ApexTooltips {

    private final List<Component> lines = new ArrayList<>();
    private String credit;

    private ApexTooltips(String type) {
        lines.add(Component.literal("§7Machine Type: §e" + type));
    }

    public static ApexTooltips of(String machineType) {
        return new ApexTooltips(machineType);
    }

    public ApexTooltips flavor(String text) {
        lines.add(Component.literal("§d§o" + text));
        return this;
    }

    public ApexTooltips info(String text) {
        lines.add(Component.literal("§7" + text));
        return this;
    }

    public ApexTooltips parallel(String text) {
        lines.add(Component.literal("§7Parallels: §b" + text));
        return this;
    }

    public ApexTooltips speed(String text) {
        lines.add(Component.literal("§7Speed: §a" + text));
        return this;
    }

    public ApexTooltips bonus(String text) {
        lines.add(Component.literal("§6" + text));
        return this;
    }

    public ApexTooltips warning(String text) {
        lines.add(Component.literal("§c" + text));
        return this;
    }

    /** "Ported from <source> (GTNH addon)" in grey, always the last line */
    public ApexTooltips portedFrom(String source) {
        credit = source;
        return this;
    }

    public Component[] build() {
        List<Component> out = new ArrayList<>(lines);
        if (credit != null) out.add(Component.literal("§8Ported from " + credit));
        return out.toArray(Component[]::new);
    }
}
