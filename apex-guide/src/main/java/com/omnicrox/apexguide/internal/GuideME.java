package com.omnicrox.apexguide.internal;

import com.omnicrox.apexguide.internal.command.GuideCommand;
import com.omnicrox.apexguide.internal.command.GuideIdArgument;
import com.omnicrox.apexguide.internal.command.PageAnchorArgument;
import com.omnicrox.apexguide.internal.item.GuideItem;
import com.omnicrox.apexguide.internal.network.OpenGuideRequest;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;

@Mod(value = GuideME.MOD_ID)
public class GuideME {
    static GuideMEProxy PROXY = new GuideMEServerProxy();

    public static final String MOD_ID = "apexguide";

    private static final DeferredRegister<Item> DR_ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID);
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> DR_ARGUMENT_TYPE_INFOS = DeferredRegister
            .create(Registries.COMMAND_ARGUMENT_TYPE, MOD_ID);

    public static final Supplier<GuideItem> GUIDE_ITEM = DR_ITEMS.register("guide",
            () -> new GuideItem(GuideItem.PROPERTIES));

    private final SimpleChannel networkChannel;

    private static GuideME INSTANCE;

    public GuideME() {
        ModLoadingContext context = ModLoadingContext.get();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        INSTANCE = this;

        DR_ARGUMENT_TYPE_INFOS.register("guide_id", () -> ArgumentTypeInfos.registerByClass(GuideIdArgument.class,
                SingletonArgumentInfo.contextFree(GuideIdArgument::argument)));
        DR_ARGUMENT_TYPE_INFOS.register("page_anchor", () -> ArgumentTypeInfos.registerByClass(PageAnchorArgument.class,
                SingletonArgumentInfo.contextFree(PageAnchorArgument::argument)));

        DR_ARGUMENT_TYPE_INFOS.register(modBus);
        DR_ITEMS.register(modBus);

        networkChannel = NetworkRegistry.newSimpleChannel(
                makeId("channel"),
                () -> "1",
                s -> true,
                s -> true);
        networkChannel.registerMessage(
                0,
                OpenGuideRequest.class,
                OpenGuideRequest::write,
                OpenGuideRequest::read,
                this::handlePacket);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        // Apex Guide: a ready-to-use book for every registered guide in the Tools tab (and so in JEI)
        modBus.addListener((net.minecraftforge.event.BuildCreativeModeTabContentsEvent e) -> {
            if (e.getTabKey() == net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES) {
                for (var guide : com.omnicrox.apexguide.internal.GuideRegistry.getStaticGuides()) {
                    e.accept(com.omnicrox.apexguide.Guides.createGuideItem(guide.getId()));
                }
            }
        });

        if (FMLLoader.getDist().isClient()) {
            new GuideMEClient(context, modBus);
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        GuideCommand.register(event.getDispatcher());
    }

    private void handlePacket(OpenGuideRequest packet, Supplier<NetworkEvent.Context> contextSource) {
        NetworkEvent.Context ctx = contextSource.get();
        ctx.setPacketHandled(true);

        ctx.enqueueWork(
                () -> {
                    var player = Objects.requireNonNullElse(
                            ctx.getSender(),
                            GuideMEProxy.instance().getLocalPlayer());
                    var anchor = packet.pageAnchor().orElse(null);
                    if (anchor != null) {
                        GuideMEProxy.instance().openGuide(player, packet.guideId(), anchor);
                    } else {
                        GuideMEProxy.instance().openGuide(player, packet.guideId());
                    }
                });
    }

    public void sendPacket(PacketDistributor.PacketTarget target, OpenGuideRequest request) {
        networkChannel.send(target, request);
    }

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static GuideME instance() {
        return Objects.requireNonNull(INSTANCE, "instance");
    }
}
