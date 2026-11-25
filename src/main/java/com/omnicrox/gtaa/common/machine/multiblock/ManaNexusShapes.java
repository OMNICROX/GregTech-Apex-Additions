package com.omnicrox.gtaa.common.machine.multiblock;

import com.omnicrox.gtaa.GTAA;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Sylvan Arbiter is the Botania Sanctuary, a glass-domed conservatory around a great tree. Build by howester84.
 * The shape comes from his schematic (tools/sanctuary/convert.py turns it into
 * data/gtapexadditions/structures/sylvan_arbiter.txt), hill and all. It grows a floor at a time:
 * <ol>
 * <li>Roots: the hill, the ground floor and its 4 lower Mana Cores (up to y10).</li>
 * <li>Gallery: the second floor and the 4 upper Mana Cores (up to y21).</li>
 * <li>Terrace: the third floor with its petal ring (up to y29).</li>
 * <li>Canopy: the tree's crown and the dome walls (up to y37).</li>
 * <li>Crown: the top of the dome (everything).</li>
 * </ol>
 * Aisles are bottom-up, rows front (the door) to back, columns as in the schematic (LEFT, tested: not mirrored).
 */
public final class ManaNexusShapes {

    private ManaNexusShapes() {}

    public static final int STAGES = 5;
    public static final String[] STAGE_NAMES = { "", "Roots", "Gallery", "Terrace", "Canopy", "Crown" };
    public static final String[] STAGE_ADDS = { "",
            "the hill, the ground floor and the 4 lower Mana Cores",
            "the second floor and the 4 upper Mana Cores",
            "the third floor and its ring of petals",
            "the tree's canopy and the dome walls",
            "the top of the dome" };
    /** the highest layer (schematic y) each stage needs */
    private static final int[] TOP = { 0, 10, 21, 29, 37, 99 };

    public record Symbol(char c, String kind, String block, String props) {}

    private static int sx, sy, sz;
    private static List<String[]> layers;
    private static Map<Character, Symbol> legend;

    private static synchronized void load() {
        if (layers != null) return;
        Map<Character, Symbol> lg = new LinkedHashMap<>();
        List<String> rows = new ArrayList<>();
        String path = "/data/gtapexadditions/structures/sylvan_arbiter.txt";
        try (InputStream in = ManaNexusShapes.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("missing " + path);
            BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            boolean grid = false;
            for (String line; (line = r.readLine()) != null;) {
                if (grid) { rows.add(line); continue; }
                if (line.equals("GRID")) { grid = true; continue; }
                String[] p = line.split("\t", -1);
                if (line.startsWith("SIZE ")) {
                    String[] s = line.split(" ");
                    sx = Integer.parseInt(s[1]); sy = Integer.parseInt(s[2]); sz = Integer.parseInt(s[3]);
                } else if (p[0].equals("K")) {
                    char c = (char) Integer.parseInt(p[1]);
                    lg.put(c, new Symbol(c, p[2], p[3], p[4]));
                }
            }
        } catch (Exception e) {
            GTAA.LOGGER.error("Sylvan Arbiter: could not read its structure", e);
            sx = sy = sz = 1;
            rows.clear();
            rows.add("");
            lg.clear();
        }
        List<String[]> ls = new ArrayList<>();
        for (int y = 0; y < sy; y++) {
            String[] aisle = new String[sz];
            for (int z = 0; z < sz; z++) {
                int i = y * sz + z;
                String row = i < rows.size() ? rows.get(i) : "";
                if (row.equals(".")) row = "";
                StringBuilder b = new StringBuilder(row);
                while (b.length() < sx) b.append(' ');
                aisle[z] = b.toString();
            }
            ls.add(aisle);
        }
        legend = lg;
        layers = ls;
    }

    public static Map<Character, Symbol> legend() {
        load();
        return legend;
    }

    /** aisles for a stage: aisle y (bottom up), rows z (front to back), columns x */
    public static List<String[]> aisles(int stage) {
        load();
        int top = Math.min(sy - 1, TOP[Math.max(1, Math.min(STAGES, stage))]);
        // trim empty layers at the top so every stage ends on something real
        while (top > 0 && isEmpty(layers.get(top))) top--;
        return new ArrayList<>(layers.subList(0, top + 1));
    }

    private static boolean isEmpty(String[] aisle) {
        for (String row : aisle) if (!row.isBlank()) return false;
        return true;
    }
}
