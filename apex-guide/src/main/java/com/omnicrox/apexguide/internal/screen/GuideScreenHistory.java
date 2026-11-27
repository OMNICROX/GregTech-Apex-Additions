package com.omnicrox.apexguide.internal.screen;

import com.omnicrox.apexguide.PageAnchor;
import java.util.Optional;

public interface GuideScreenHistory {
    /**
     * Append to history if it's not already appended
     */
    void push(PageAnchor anchor);

    PageAnchor get(int index);

    Optional<PageAnchor> current();

    Optional<PageAnchor> forward();

    Optional<PageAnchor> peekForward();

    Optional<PageAnchor> back();

    Optional<PageAnchor> peekBack();
}
