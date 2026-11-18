package com.omnicrox.gtaa.common.data;

import com.omnicrox.gtaa.GTAA;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.omnicrox.gtaa.GTAA.REGISTRATE;

public class GTAACreativeTab {

    public static final RegistryEntry<CreativeModeTab> MAIN = REGISTRATE.defaultCreativeTab("main",
            b -> b.icon(() -> new ItemStack(Items.NETHER_STAR))
                    .title(REGISTRATE.addLang("itemGroup", GTAA.id("main"), GTAA.NAME))
                    .build())
            .register();

    static {
        REGISTRATE.creativeModeTab(() -> MAIN);
    }

    public static void init() {}
}
