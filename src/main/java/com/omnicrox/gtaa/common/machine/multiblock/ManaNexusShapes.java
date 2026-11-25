package com.omnicrox.gtaa.common.machine.multiblock;

import java.util.ArrayList;
import java.util.List;

/**
 * The Mana Nexus grows in five stages around a Great Mana Tree (aisles bottom-up, rows front to back, columns left
 * to right; the controller is in the middle of the front edge of the platform).
 * <ol>
 * <li>Grove: 9x9 platform, a ring of petal blocks around a livingwood tree whose canopy holds the Mana Cores.</li>
 * <li>Pools: the platform widens to 13x13 and eight mana pools ring the tree.</li>
 * <li>Pylons: four tall livingrock pillars topped with pylons stand at the corners.</li>
 * <li>Halo: a floating ring of elven glass circles the canopy.</li>
 * <li>Gaia: 15x15, an outer ring of petals, and the pylons must be Gaia Pylons.</li>
 * </ol>
 * Symbols: ~ controller, a casing, I i E e M m O o w n hatch slots (as before), b livingrock bricks, L livingwood
 * log, l Mana Bloom Leaves, c Mana Core, f petal block, p mana pool, P any pylon, G gaia pylon, h elven glass.
 */
public final class ManaNexusShapes {

    private ManaNexusShapes() {}

    public static final int STAGES = 5;
    public static final String[] STAGE_NAMES = { "", "Grove", "Pools", "Pylons", "Halo", "Gaia" };
    public static final String[] STAGE_ADDS = { "",
            "the tree, its canopy of Mana Cores and a ring of petals",
            "8 mana pools around the tree (platform 13x13)",
            "4 livingrock pillars with pylons at the corners",
            "a floating ring of elven glass around the canopy",
            "15x15, an outer ring of petals, Gaia Pylons on the pillars" };

    public static int radius(int stage) {
        return stage >= 5 ? 7 : stage >= 2 ? 6 : 4;
    }

    public static int height(int stage) {
        return stage >= 4 ? 11 : 10;
    }

    /** aisles for a stage: aisle y, then rows z (front -R .. back +R), then columns x (-R .. R) */
    public static List<String[]> aisles(int stage) {
        int r = radius(stage), size = 2 * r + 1;
        List<String[]> out = new ArrayList<>();
        for (int y = 0; y < height(stage); y++) {
            String[] rows = new String[size];
            for (int zi = 0; zi < size; zi++) {
                int z = zi - r;
                StringBuilder sb = new StringBuilder();
                for (int xi = 0; xi < size; xi++) sb.append(at(stage, r, xi - r, y, z));
                rows[zi] = sb.toString();
            }
            out.add(rows);
        }
        return out;
    }

    private static final String FRONT = "IiE~Mme", BACK = "aOowwan";

    private static char at(int stage, int r, int x, int y, int z) {
        int cheb = Math.max(Math.abs(x), Math.abs(z));
        if (y == 0) {
            if (z == -r && Math.abs(x) <= 3) return FRONT.charAt(x + 3);
            if (z == r && Math.abs(x) <= 3) return BACK.charAt(x + 3);
            return 'a';
        }
        // corner pillars and pylons (stage 3+)
        if (stage >= 3 && Math.abs(x) == r - 1 && Math.abs(z) == r - 1) {
            if (y <= 5) return 'b';
            if (y == 6) return stage >= 5 ? 'G' : 'P';
        }
        // the tree: trunk up to y7, a wide canopy with the Mana Cores in its lowest layer
        if (x == 0 && z == 0 && y <= 7) return 'L';
        if (y == 1) {
            if (cheb == r) return 'b';
            if (cheb == 2) return 'f';
            if (stage >= 2 && isPool(x, z)) return 'p';
            if (stage >= 5 && cheb == r - 1 && !(Math.abs(x) == r - 1 && Math.abs(z) == r - 1)) return 'f';
            return ' ';
        }
        boolean corner3 = Math.abs(x) == 3 && Math.abs(z) == 3;
        if (y == 5) {
            if ((Math.abs(x) == 2 && z == 0) || (Math.abs(z) == 2 && x == 0)) return 'c';
            if (cheb <= 2) return 'l';
        }
        if (y == 6 && cheb <= 3 && !corner3) return 'l';
        if (y == 7 && cheb <= 2) return 'l';
        if (y == 8 && cheb <= 1) return 'l';
        if (y == 9 && (Math.abs(x) + Math.abs(z)) <= 1) return 'l';
        if (y == 10 && stage >= 4 && cheb == 4) return 'h';
        return ' ';
    }

    private static boolean isPool(int x, int z) {
        return (Math.abs(x) == 4 && z == 0) || (Math.abs(z) == 4 && x == 0) || (Math.abs(x) == 3 && Math.abs(z) == 3);
    }
}
