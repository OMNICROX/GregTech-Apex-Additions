package com.omnicrox.apexguide.color;

public interface ColorValue {
    /**
     * Resolve as ARGB 32-bit.
     */
    int resolve(LightDarkMode lightDarkMode);
}
