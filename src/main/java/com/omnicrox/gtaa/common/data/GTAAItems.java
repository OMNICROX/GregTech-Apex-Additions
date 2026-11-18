package com.omnicrox.gtaa.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.item.Item;

import static com.omnicrox.gtaa.GTAA.REGISTRATE;

public class GTAAItems {

    static {
        REGISTRATE.creativeModeTab(() -> GTAACreativeTab.MAIN);
    }

    /** Mining drones LV..OpV (index = GT voltage tier). Textures from GTNH-Intergalactic. */
    @SuppressWarnings("unchecked")
    public static final ItemEntry<Item>[] MINING_DRONES = new ItemEntry[GTValues.OpV + 1];

    static {
        for (int tier = GTValues.LV; tier <= GTValues.OpV; tier++) {
            String vn = GTValues.VN[tier].toLowerCase(java.util.Locale.ROOT);
            MINING_DRONES[tier] = REGISTRATE.item("mining_drone_" + vn, Item::new)
                    .lang(GTValues.VN[tier] + " Mining Drone")
                    .model(NonNullBiConsumer.noop())
                    .register();
        }
    }

    public static void init() {}
}
