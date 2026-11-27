package com.omnicrox.gtaa.common.personal;

import com.omnicrox.gtaa.GTAA;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.network.GTAANetwork;
import com.omnicrox.gtaa.config.GTAACommonConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * GT New Horizons-style Personal Space: every player can make one world of their own through a Personal Space
 * Portal. Its floor (void with a platform, stone, or grass), its time (normal, always day, always night) and whether
 * mobs spawn are picked when it's made. Worlds are made with Infiniverse and made again on every server start.
 * Toggle: gtapexadditions-common.toml, personalSpace.enabled.
 */
@Mod.EventBusSubscriber(modid = GTAA.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PersonalSpace {

    private PersonalSpace() {}

    public static final List<String> FLOORS = List.of("void", "stone", "grass");
    public static final List<String> TIMES = List.of("cycle", "day", "night");
    static final int SURFACE = 64;

    public static boolean available() {
        return GTAACommonConfig.PERSONAL_SPACE.get() && ModList.get().isLoaded("infiniverse");
    }

    static ResourceKey<Level> key(UUID owner) {
        return ResourceKey.create(Registries.DIMENSION, GTAA.id("personal/" + owner.toString().replace("-", "")));
    }

    static boolean isPersonal(ResourceKey<Level> k) {
        return k.location().getNamespace().equals(GTAA.MOD_ID) && k.location().getPath().startsWith("personal/");
    }

    // ---------------- portal ----------------
    static void usePortal(ServerPlayer player, BlockPos pos) {
        if (!available()) {
            player.sendSystemMessage(Component.literal("Personal Space is turned off on this server.").withStyle(ChatFormatting.RED));
            return;
        }
        MinecraftServer server = player.server;
        Data data = Data.get(server);
        if (isPersonal(player.level().dimension())) {
            goBack(player, data);
            return;
        }
        Data.World w = data.worlds.get(player.getUUID());
        if (w == null) {
            // first time: pick the world's settings (screen; falls back to clickable chat)
            GTAANetwork.send(player, new GTAANetwork.PersonalSpacePacket(FLOORS, TIMES));
            return;
        }
        enter(player, w, data);
    }

    static void enter(ServerPlayer player, Data.World w, Data data) {
        ServerLevel level = level(player.server, player.getUUID(), w);
        if (level == null) return;
        data.back.put(player.getUUID(), new Data.Back(player.level().dimension().location(), player.blockPosition()));
        data.setDirty();
        player.teleportTo(level, 0.5, SURFACE + 1, 0.5, player.getYRot(), player.getXRot());
    }

    static void goBack(ServerPlayer player, Data data) {
        Data.Back b = data.back.get(player.getUUID());
        ServerLevel to = b == null ? null : player.server.getLevel(ResourceKey.create(Registries.DIMENSION, b.dim()));
        BlockPos p;
        if (to == null) {
            to = player.server.overworld();
            p = to.getSharedSpawnPos();
        } else p = b.pos();
        player.teleportTo(to, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, player.getYRot(), player.getXRot());
    }

    /** /gtapexadditions personalspace create <floor> <time> <mobs>: the screen's "Create" button */
    static int create(ServerPlayer player, String floor, String time, boolean mobs) {
        if (!available()) return 0;
        if (!FLOORS.contains(floor) || !TIMES.contains(time)) return 0;
        Data data = Data.get(player.server);
        if (data.worlds.containsKey(player.getUUID())) {
            player.sendSystemMessage(Component.literal("You already have a Personal Space.").withStyle(ChatFormatting.YELLOW));
            return 0;
        }
        Data.World w = new Data.World(floor, time, mobs, false);
        data.worlds.put(player.getUUID(), w);
        data.setDirty();
        ServerLevel level = level(player.server, player.getUUID(), w);
        if (level == null) return 0;
        decorate(level, floor);
        w = new Data.World(floor, time, mobs, true);
        data.worlds.put(player.getUUID(), w);
        data.setDirty();
        player.sendSystemMessage(Component.literal("Your Personal Space is ready. Use the portal there to come back.")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        enter(player, w, data);
        return 1;
    }

    /** the platform (void floor) and the way back */
    private static void decorate(ServerLevel level, String floor) {
        if (floor.equals("void")) {
            for (int x = -3; x <= 3; x++)
                for (int z = -3; z <= 3; z++) level.setBlockAndUpdate(new BlockPos(x, SURFACE, z), Blocks.SMOOTH_STONE.defaultBlockState());
        }
        level.setBlockAndUpdate(new BlockPos(0, SURFACE + 1, -2), GTAABlocks.PERSONAL_PORTAL.get().defaultBlockState());
        level.setDefaultSpawnPos(new BlockPos(0, SURFACE + 1, 0), 0);
    }

    private static ServerLevel level(MinecraftServer server, UUID owner, Data.World w) {
        try {
            return Infiniverse.getOrCreate(server, key(owner), () -> stem(server, w));
        } catch (Throwable t) {
            GTAA.LOGGER.error("[Personal Space] could not make the world for {}", owner, t);
            return null;
        }
    }

    private static LevelStem stem(MinecraftServer server, Data.World w) {
        var access = server.registryAccess();
        Holder<DimensionType> type = access.registryOrThrow(Registries.DIMENSION_TYPE)
                .getHolderOrThrow(ResourceKey.create(Registries.DIMENSION_TYPE, GTAA.id("personal_" + w.time())));
        Holder<Biome> biome = access.registryOrThrow(Registries.BIOME).getHolderOrThrow(ResourceKey.create(Registries.BIOME,
                w.mobs() ? new ResourceLocation("minecraft", "plains") : GTAA.id("personal_space")));
        List<FlatLayerInfo> layers = new ArrayList<>();
        switch (w.floor()) {
            case "stone" -> {
                layers.add(new FlatLayerInfo(1, Blocks.BEDROCK));
                layers.add(new FlatLayerInfo(SURFACE + 64, Blocks.STONE));
            }
            case "grass" -> {
                layers.add(new FlatLayerInfo(1, Blocks.BEDROCK));
                layers.add(new FlatLayerInfo(SURFACE + 60, Blocks.STONE));
                layers.add(new FlatLayerInfo(3, Blocks.DIRT));
                layers.add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
            }
            default -> {}
        }
        FlatLevelGeneratorSettings s = new FlatLevelGeneratorSettings(Optional.empty(), biome, List.of())
                .withBiomeAndLayers(layers, Optional.empty(), biome);
        return new LevelStem(type, new FlatLevelSource(s));
    }

    // ---------------- events, commands ----------------
    @SubscribeEvent
    public static void onStarted(ServerStartedEvent event) {
        if (!available()) return;
        MinecraftServer server = event.getServer();
        Data data = Data.get(server);
        int n = 0;
        for (var e : data.worlds.entrySet()) if (level(server, e.getKey(), e.getValue()) != null) n++;
        if (n > 0) GTAA.LOGGER.info("[Personal Space] loaded {} personal worlds", n);
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gtapexadditions").then(Commands.literal("personalspace")
                .then(Commands.literal("create").then(Commands.argument("floor", StringArgumentType.word())
                        .then(Commands.argument("time", StringArgumentType.word())
                                .then(Commands.argument("mobs", StringArgumentType.word()).executes(ctx -> create(
                                        ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "floor"),
                                        StringArgumentType.getString(ctx, "time"),
                                        StringArgumentType.getString(ctx, "mobs").equals("true")))))))
                .then(Commands.literal("home").executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    if (isPersonal(p.level().dimension())) goBack(p, Data.get(p.server));
                    return 1;
                }))));
    }

    // ---------------- saved data (in the overworld) ----------------
    static final class Data extends SavedData {
        record World(String floor, String time, boolean mobs, boolean built) {}

        record Back(ResourceLocation dim, BlockPos pos) {}

        final Map<UUID, World> worlds = new HashMap<>();
        final Map<UUID, Back> back = new HashMap<>();

        static Data get(MinecraftServer server) {
            return server.overworld().getDataStorage().computeIfAbsent(Data::load, Data::new, "gtapexadditions_personal_space");
        }

        static Data load(CompoundTag tag) {
            Data d = new Data();
            for (Tag t : tag.getList("worlds", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                d.worlds.put(c.getUUID("owner"), new World(c.getString("floor"), c.getString("time"), c.getBoolean("mobs"), true));
            }
            for (Tag t : tag.getList("back", Tag.TAG_COMPOUND)) {
                CompoundTag c = (CompoundTag) t;
                d.back.put(c.getUUID("player"), new Back(new ResourceLocation(c.getString("dim")), BlockPos.of(c.getLong("pos"))));
            }
            return d;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            ListTag w = new ListTag();
            worlds.forEach((k, v) -> {
                CompoundTag c = new CompoundTag();
                c.putUUID("owner", k);
                c.putString("floor", v.floor());
                c.putString("time", v.time());
                c.putBoolean("mobs", v.mobs());
                w.add(c);
            });
            tag.put("worlds", w);
            ListTag b = new ListTag();
            back.forEach((k, v) -> {
                CompoundTag c = new CompoundTag();
                c.putUUID("player", k);
                c.putString("dim", v.dim().toString());
                c.putLong("pos", v.pos().asLong());
                b.add(c);
            });
            tag.put("back", b);
            return tag;
        }
    }

    /** the only class touching Infiniverse, so GTAA loads without it */
    private static final class Infiniverse {
        static ServerLevel getOrCreate(MinecraftServer server, ResourceKey<Level> key, java.util.function.Supplier<LevelStem> stem) {
            return commoble.infiniverse.api.InfiniverseAPI.get().getOrCreateLevel(server, key, stem);
        }
    }

    @SuppressWarnings("unused")
    private static Block portal() {
        return GTAABlocks.PERSONAL_PORTAL.get();
    }
}
