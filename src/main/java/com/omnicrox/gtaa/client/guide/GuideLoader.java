package com.omnicrox.gtaa.client.guide;

/** keeps GuideME classes out of GTAA's constructor (only loaded when GuideME is installed) */
public final class GuideLoader {

    private GuideLoader() {}

    public static void load() {
        try {
            ApexGuide.init();
        } catch (Throwable t) {
            com.omnicrox.gtaa.GTAA.LOGGER.error("[Apex Guide] could not register the guide", t);
        }
    }
}
