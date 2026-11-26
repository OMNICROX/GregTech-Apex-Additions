package com.omnicrox.gtaa.common.stargate;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

import java.util.ArrayList;
import java.util.List;

/** The Ascension credits: what this player did on the way to the Apex Stargate. */
public final class AscensionCredits {

    private AscensionCredits() {}

    public static List<String> stats(ServerPlayer p) {
        var s = p.getStats();
        List<String> l = new ArrayList<>();
        long ticks = s.getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        long mined = 0, crafted = 0;
        for (var b : net.minecraft.core.registries.BuiltInRegistries.BLOCK) mined += s.getValue(Stats.BLOCK_MINED.get(b));
        for (var i : net.minecraft.core.registries.BuiltInRegistries.ITEM) crafted += s.getValue(Stats.ITEM_CRAFTED.get(i));
        long walked = s.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM)) + s.getValue(Stats.CUSTOM.get(Stats.SPRINT_ONE_CM));
        long flown = s.getValue(Stats.CUSTOM.get(Stats.FLY_ONE_CM)) + s.getValue(Stats.CUSTOM.get(Stats.AVIATE_ONE_CM));
        l.add("Ascended: " + p.getGameProfile().getName());
        l.add("Day " + (p.level().getDayTime() / 24000L + 1) + " of the world");
        l.add(String.format("Time played: %,d hours %d minutes", ticks / 72000, (ticks / 1200) % 60));
        l.add(String.format("Blocks mined: %,d", mined));
        l.add(String.format("Things crafted: %,d", crafted));
        l.add(String.format("Mobs defeated: %,d", s.getValue(Stats.CUSTOM.get(Stats.MOB_KILLS))));
        l.add(String.format("Deaths: %,d", s.getValue(Stats.CUSTOM.get(Stats.DEATHS))));
        l.add(String.format("Walked: %,d km   Flown: %,d km", walked / 100000, flown / 100000));
        l.add(String.format("Jumps: %,d", s.getValue(Stats.CUSTOM.get(Stats.JUMP))));
        l.add("Maxwell approves.");
        return l;
    }

}
