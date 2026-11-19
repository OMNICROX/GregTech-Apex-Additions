package com.omnicrox.gtaa.api;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.omnicrox.gtaa.GTAA;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Soft link to GTM Things' wireless energy network (no compile dependency). If GTM Things isn't installed,
 * wireless mode is unavailable.
 */
public final class GTAAWireless {

    private GTAAWireless() {}

    private static Boolean available;
    private static Method getOrCreate, addEnergy;

    public static boolean isAvailable() {
        if (available != null) return available;
        available = false;
        if (!ModList.get().isLoaded("gtmthings")) return false;
        try {
            Class<?> c = Class.forName("com.hepdd.gtmthings.api.misc.WirelessEnergyContainer");
            getOrCreate = c.getMethod("getOrCreateContainer", UUID.class);
            addEnergy = c.getMethod("addEnergy", long.class, MetaMachine.class);
            available = true;
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[GTAA] GTM Things found but its wireless API changed: {}", t.toString());
        }
        return available;
    }

    /** @return EU actually added to the owner's wireless network */
    public static long addEU(UUID owner, long eu, MetaMachine machine) {
        if (owner == null || eu <= 0 || !isAvailable()) return 0;
        try {
            Object container = getOrCreate.invoke(null, owner);
            return (long) addEnergy.invoke(container, eu, machine);
        } catch (Throwable t) {
            return 0;
        }
    }
}
