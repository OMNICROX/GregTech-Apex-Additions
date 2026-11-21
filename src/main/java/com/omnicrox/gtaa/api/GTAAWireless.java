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
    private static Method getOrCreate, addEnergy, getStorage, setStorage;

    public static boolean isAvailable() {
        if (available != null) return available;
        available = false;
        if (!ModList.get().isLoaded("gtmthings")) return false;
        try {
            Class<?> c = Class.forName("com.hepdd.gtmthings.api.misc.WirelessEnergyContainer");
            getOrCreate = c.getMethod("getOrCreateContainer", UUID.class);
            addEnergy = c.getMethod("addEnergy", long.class, MetaMachine.class);
            getStorage = c.getMethod("getStorage");
            setStorage = c.getMethod("setStorage", java.math.BigInteger.class);
            available = true;
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[GTAA] GTM Things found but its wireless API changed: {}", t.toString());
        }
        return available;
    }

    /**
     * Adds EU to the owner's wireless network. GTM Things caps each transfer at the network's rate (0 until a
     * battery buffer / substation is bound when isWirelessRateEnable is on); generators that work in big bursts
     * (the SLAM) pass {@code ignoreRate} so a burst isn't thrown away. Their own limits apply before this.
     * @return EU actually added
     */
    public static long addEU(UUID owner, long eu, MetaMachine machine, boolean ignoreRate) {
        if (owner == null || eu <= 0 || !isAvailable()) return 0;
        try {
            Object container = getOrCreate.invoke(null, owner);
            long added = (long) addEnergy.invoke(container, eu, machine);
            if (ignoreRate && added < eu) {
                java.math.BigInteger b = (java.math.BigInteger) getStorage.invoke(container);
                setStorage.invoke(container, b.add(java.math.BigInteger.valueOf(eu - added)));
                added = eu;
            }
            return added;
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[GTAA] wireless transfer failed: {}", t.toString());
            return 0;
        }
    }

    public static long addEU(UUID owner, long eu, MetaMachine machine) {
        return addEU(owner, eu, machine, false);
    }

    /** the owner's wireless network balance, formatted, or "" */
    public static String storedText(UUID owner) {
        if (owner == null || !isAvailable()) return "";
        try {
            java.math.BigInteger b = (java.math.BigInteger) getStorage.invoke(getOrCreate.invoke(null, owner));
            if (b.bitLength() < 63) return com.gregtechceu.gtceu.utils.FormattingUtil.formatNumbers(b.longValue());
            String d = b.toString();
            return d.charAt(0) + "." + d.substring(1, 4) + "e" + (d.length() - 1);
        } catch (Throwable t) {
            return "";
        }
    }
}
