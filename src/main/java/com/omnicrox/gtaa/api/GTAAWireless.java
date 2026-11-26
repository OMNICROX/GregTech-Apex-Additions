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

    /** Takes up to max EU out of the owner's GTM Things wireless network. @return EU taken */
    public static long drawEU(UUID owner, long max) {
        if (owner == null || max <= 0 || !isAvailable()) return 0;
        try {
            Object container = getOrCreate.invoke(null, owner);
            java.math.BigInteger b = (java.math.BigInteger) getStorage.invoke(container);
            if (b.signum() <= 0) return 0;
            long take = b.compareTo(java.math.BigInteger.valueOf(max)) >= 0 ? max : b.longValue();
            setStorage.invoke(container, b.subtract(java.math.BigInteger.valueOf(take)));
            return take;
        } catch (Throwable t) {
            return 0;
        }
    }

    private static Boolean nexusAvailable;
    private static Method nexusGet, nexusConsume;
    private static java.lang.reflect.Constructor<?> int128;

    /** Takes up to max EU out of the owner's gtna "Nexus" wireless network (where gtna's generators send power). */
    public static long drawNexusEU(net.minecraft.server.level.ServerLevel level, UUID owner, long max) {
        if (owner == null || max <= 0 || level == null) return 0;
        if (nexusAvailable == null) {
            nexusAvailable = false;
            if (ModList.get().isLoaded("gtna")) {
                try {
                    Class<?> m = Class.forName("com.raishxn.gtna.api.capability.WirelessEnergyManager");
                    Class<?> i;
                    try { // gtna 0.6.1+ moved Int128 into TypeOmega Lib
                        i = Class.forName("com.typeomega.lib.data.Int128");
                    } catch (ClassNotFoundException e) {
                        i = Class.forName("com.raishxn.gtna.utils.datastructure.Int128");
                    }
                    nexusGet = m.getMethod("getEnergy", net.minecraft.server.level.ServerLevel.class, UUID.class);
                    nexusConsume = m.getMethod("consumeEnergy", net.minecraft.server.level.ServerLevel.class, UUID.class, i);
                    int128 = i.getConstructor(long.class);
                    nexusAvailable = true;
                } catch (Throwable t) {
                    GTAA.LOGGER.warn("[GTAA] gtna found but its wireless API changed: {}", t.toString());
                }
            }
        }
        if (!nexusAvailable) return 0;
        try {
            Number have = (Number) nexusGet.invoke(null, level, owner);
            if (have == null) return 0;
            @SuppressWarnings("unchecked")
            Comparable<Object> c = (Comparable<Object>) have;
            Object maxI = int128.newInstance(max);
            long take = c.compareTo(maxI) >= 0 ? max : have.longValue();
            if (take <= 0) return 0;
            return (boolean) nexusConsume.invoke(null, level, owner, int128.newInstance(take)) ? take : 0;
        } catch (Throwable t) {
            return 0;
        }
    }

    /** the owner's wireless network balance, formatted, or "" */
    public static String storedText(UUID owner) {
        if (owner == null || !isAvailable()) return "";
        try {
            java.math.BigInteger b = (java.math.BigInteger) getStorage.invoke(getOrCreate.invoke(null, owner));
            return GTAAFormat.num(b);
        } catch (Throwable t) {
            return "";
        }
    }
}
