package com.omnicrox.apexguide.ui;

import com.omnicrox.apexguide.PageAnchor;
import com.omnicrox.apexguide.PageCollection;
import com.omnicrox.apexguide.document.LytPoint;
import com.omnicrox.apexguide.document.LytRect;
import com.omnicrox.apexguide.document.interaction.InteractiveElement;
import com.omnicrox.apexguide.internal.screen.GuideNavigation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface GuideUiHost {
    void navigateTo(ResourceLocation pageId);

    void navigateTo(PageAnchor anchor);

    void reloadPage();

    default void openUrl(String href) {
        GuideNavigation.openUrl(href);
    }

    @Nullable
    UiPoint getDocumentPoint(double screenX, double screenY);

    UiPoint getDocumentPointUnclamped(double screenX, double screenY);

    LytPoint getScreenPoint(LytPoint documentPoint);

    LytRect getDocumentRect();

    LytRect getDocumentViewport();

    PageCollection getGuide();

    @Nullable
    InteractiveElement getMouseCaptureTarget();

    void captureMouse(InteractiveElement element);

    void releaseMouseCapture(InteractiveElement element);
}
