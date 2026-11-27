package com.omnicrox.apexlib;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Apex Lib: shared code for the Apex Ascension mods (GregTech Apex Additions, Apex Core, Apex Fixes).
 * Like GTNHLib for GT New Horizons: multiblock helpers (parallels, counted casings, hatches, JSON structures),
 * GTNH-style tooltips, number formatting and soft compatibility with other mods' APIs.
 */
@Mod(ApexLib.MOD_ID)
public final class ApexLib {

    public static final String MOD_ID = "apexlib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ApexLib() {
        LOGGER.info("Apex Lib loaded");
    }
}
