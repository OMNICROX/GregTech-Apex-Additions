package com.omnicrox.apexguide.style;

import com.omnicrox.apexguide.color.ColorValue;
import com.omnicrox.apexguide.color.ConstantColor;

public record BorderStyle(ColorValue color, int width) {
    public static BorderStyle NONE = new BorderStyle(ConstantColor.TRANSPARENT, 0);
}
