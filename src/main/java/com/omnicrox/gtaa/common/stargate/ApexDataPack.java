package com.omnicrox.gtaa.common.stargate;

import com.omnicrox.gtaa.GTAA;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * GTAA's built-in, always-on "Apex Stargate" data pack (src/main/resources/datapacks/apex_stargate, made by
 * tools/gen_apex_stargate_pack.py): Stargate Journey / More Gates recipes off, gates cut out of the structures,
 * the Stargate Journey planets as Ad Astra destinations. Only added when Stargate Journey is installed.
 */
@Mod.EventBusSubscriber(modid = GTAA.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ApexDataPack {

    private ApexDataPack() {}

    @SubscribeEvent
    public static void addPacks(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA || !ModList.get().isLoaded("sgjourney")) return;
        try {
            Path path = ModList.get().getModFileById(GTAA.MOD_ID).getFile().findResource("datapacks", "apex_stargate");
            if (!Files.exists(path)) return;
            Pack pack = Pack.readMetaAndCreate("gtapexadditions:apex_stargate", Component.literal("Apex Stargate (GTAA)"), true,
                    id -> new PathPackResources(id, path, true), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN);
            if (pack != null) event.addRepositorySource(consumer -> consumer.accept(pack));
        } catch (Throwable t) {
            GTAA.LOGGER.warn("[GTAA] could not add the Apex Stargate data pack: {}", t.toString());
        }
    }
}
