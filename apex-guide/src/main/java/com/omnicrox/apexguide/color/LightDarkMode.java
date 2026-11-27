package com.omnicrox.apexguide.color;

import com.omnicrox.apexguide.internal.GuideMEClient;

public enum LightDarkMode {
    LIGHT_MODE,
    DARK_MODE;

    public static LightDarkMode current() {
        return GuideMEClient.currentLightDarkMode();
    }
}
