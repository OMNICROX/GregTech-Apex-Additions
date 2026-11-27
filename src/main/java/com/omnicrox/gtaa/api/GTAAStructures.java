package com.omnicrox.gtaa.api;

import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.omnicrox.apexlib.multiblock.ApexStructures;

/** GTAA's structure JSONs (resources/gtapexadditions/structures); see {@link ApexStructures}. */
public final class GTAAStructures {

    private GTAAStructures() {}

    public static String[][] get(String file, String key) {
        return ApexStructures.get("gtapexadditions", file, key);
    }

    public static FactoryBlockPattern pattern(String file, String key) {
        return ApexStructures.pattern("gtapexadditions", file, key);
    }
}
