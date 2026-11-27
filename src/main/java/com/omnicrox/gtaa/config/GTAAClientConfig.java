package com.omnicrox.gtaa.config;

import net.minecraftforge.common.ForgeConfigSpec;

/** config/gtapexadditions-client.toml */
public final class GTAAClientConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue PREVIEW_ENABLED, PREVIEW_HIDE_GREGTECH, PREVIEW_INVERT_X, PREVIEW_INVERT_Y;
    public static final ForgeConfigSpec.DoubleValue PREVIEW_ROTATE_SPEED, PREVIEW_ZOOM_SPEED;
    public static final ForgeConfigSpec.IntValue PREVIEW_CACHE;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Multiblock Structure page (GT New Horizons-style multiblock preview in JEI / NEI)").push("multiblockPreview");
        PREVIEW_ENABLED = b.comment("Show the Multiblock Structure page. Off: GregTech's own multiblock page comes back.",
                "Takes effect on the next world join.").define("enabled", true);
        PREVIEW_HIDE_GREGTECH = b.comment("Hide GregTech's own multiblock page while ours is on").define("hideGregTechPage", true);
        PREVIEW_ROTATE_SPEED = b.comment("Degrees turned per pixel dragged (GT New Horizons: 1)").defineInRange("rotateSpeed", 1.0, 0.1, 10.0);
        PREVIEW_ZOOM_SPEED = b.comment("Blocks of zoom per scroll notch (GT New Horizons: 5)").defineInRange("scrollZoom", 5.0, 0.5, 50.0);
        PREVIEW_INVERT_X = b.comment("Turn the other way when dragging left/right").define("invertHorizontal", false);
        PREVIEW_INVERT_Y = b.comment("Tilt the other way when dragging up/down").define("invertVertical", false);
        PREVIEW_CACHE = b.comment("How many previews keep their 3D scene in memory").defineInRange("cachedScenes", 8, 1, 64);
        b.pop();
        SPEC = b.build();
    }

    private GTAAClientConfig() {}
}
