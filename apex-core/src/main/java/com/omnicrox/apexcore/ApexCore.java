package com.omnicrox.apexcore;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Apex Core: the Apex Ascension pack's own changes (like GTNH's NewHorizonsCoreMod). Each KubeJS script that moves
 * here becomes a class under {@code recipe/} (or {@code worldgen/}, {@code integration/}) and the script is deleted.
 */
@Mod(ApexCore.MOD_ID)
public final class ApexCore {

    public static final String MOD_ID = "apexcore";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(MOD_ID);

    public ApexCore() {
        LOGGER.info("Apex Core loaded");
    }
}
