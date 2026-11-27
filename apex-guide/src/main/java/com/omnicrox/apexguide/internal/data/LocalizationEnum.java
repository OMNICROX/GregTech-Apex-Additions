package com.omnicrox.apexguide.internal.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface LocalizationEnum {

    String getEnglishText();

    String getTranslationKey();

    default MutableComponent text() {
        return Component.translatableWithFallback(getTranslationKey(), getEnglishText());
    }

    default MutableComponent text(Object... args) {
        return Component.translatableWithFallback(getTranslationKey(), getEnglishText(), args);
    }

    default MutableComponent withSuffix(String text) {
        return text().copy().append(text);
    }

    default MutableComponent withSuffix(Component text) {
        return text().copy().append(text);
    }

}
