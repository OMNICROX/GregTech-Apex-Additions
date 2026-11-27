package com.omnicrox.gtaa.common.network;

import com.omnicrox.gtaa.GTAA;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/** GTAA's packets: the Structure Upgrader screen, Ascension credits, infernal mob data. */
public final class GTAANetwork {

    private GTAANetwork() {}

    private static final String VERSION = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(GTAA.id("main"),
            () -> VERSION, v -> true, v -> true); // optional on both sides (old clients fall back to chat)

    public static void register() {
        CHANNEL.registerMessage(0, UpgraderScreenPacket.class, UpgraderScreenPacket::write, UpgraderScreenPacket::read,
                UpgraderScreenPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(1, AscensionPacket.class, AscensionPacket::write, AscensionPacket::read,
                AscensionPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(2, InfernalPacket.class, InfernalPacket::write, InfernalPacket::read,
                InfernalPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(3, PersonalSpacePacket.class, PersonalSpacePacket::write, PersonalSpacePacket::read,
                PersonalSpacePacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    /** opens the Personal Space setup screen (floors and times to pick from) */
    public record PersonalSpacePacket(List<String> floors, List<String> times) {

        void write(FriendlyByteBuf buf) {
            buf.writeCollection(floors, FriendlyByteBuf::writeUtf);
            buf.writeCollection(times, FriendlyByteBuf::writeUtf);
        }

        static PersonalSpacePacket read(FriendlyByteBuf buf) {
            return new PersonalSpacePacket(buf.readList(FriendlyByteBuf::readUtf), buf.readList(FriendlyByteBuf::readUtf));
        }

        void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.omnicrox.gtaa.client.PersonalSpaceScreen.open(this)));
            ctx.get().setPacketHandled(true);
        }
    }

    public static void send(ServerPlayer player, Object msg) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    /**
     * One clickable choice; the command is run by the client exactly like the old chat buttons. tag is the short
     * note on the button ("+12", "\u2193"); hover explains it.
     */
    public record Choice(String label, String command, String hover, boolean ok, String itemId, String tag) {}

    /**
     * One section of the screen: a block family, a hatch or the machine status. info says what it does in this
     * machine; verdict 0 = maxed, 1 = optional, 2 = needed (recipes are locked without it).
     */
    public record Row(String title, String current, String itemId, List<Choice> choices, List<String> info, int verdict,
                      String verdictText) {}

    public record UpgraderScreenPacket(String machine, List<Row> rows, String footer) {

        void write(FriendlyByteBuf buf) {
            buf.writeUtf(machine);
            buf.writeUtf(footer);
            buf.writeVarInt(rows.size());
            for (Row r : rows) {
                buf.writeUtf(r.title);
                buf.writeUtf(r.current);
                buf.writeUtf(r.itemId);
                buf.writeVarInt(r.verdict);
                buf.writeUtf(r.verdictText);
                buf.writeVarInt(r.info.size());
                for (String line : r.info) buf.writeUtf(line);
                buf.writeVarInt(r.choices.size());
                for (Choice c : r.choices) {
                    buf.writeUtf(c.label);
                    buf.writeUtf(c.command);
                    buf.writeUtf(c.hover);
                    buf.writeBoolean(c.ok);
                    buf.writeUtf(c.itemId);
                    buf.writeUtf(c.tag);
                }
            }
        }

        static UpgraderScreenPacket read(FriendlyByteBuf buf) {
            String machine = buf.readUtf();
            String footer = buf.readUtf();
            int n = buf.readVarInt();
            List<Row> rows = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String title = buf.readUtf(), current = buf.readUtf(), item = buf.readUtf();
                int verdict = buf.readVarInt();
                String verdictText = buf.readUtf();
                int k = buf.readVarInt();
                List<String> info = new ArrayList<>();
                for (int j = 0; j < k; j++) info.add(buf.readUtf());
                int m = buf.readVarInt();
                List<Choice> choices = new ArrayList<>();
                for (int j = 0; j < m; j++)
                    choices.add(new Choice(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readUtf(),
                            buf.readUtf()));
                rows.add(new Row(title, current, item, choices, info, verdict, verdictText));
            }
            return new UpgraderScreenPacket(machine, rows, footer);
        }

        void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.omnicrox.gtaa.client.StructureUpgraderScreen.open(this)));
            ctx.get().setPacketHandled(true);
        }
    }

    /** Ascension Gate credits; celebrate = the gate just opened (pink glow for a minute) */
    public record AscensionPacket(boolean celebrate, List<String> lines) {

        void write(FriendlyByteBuf buf) {
            buf.writeBoolean(celebrate);
            buf.writeVarInt(lines.size());
            for (String l : lines) buf.writeUtf(l);
        }

        static AscensionPacket read(FriendlyByteBuf buf) {
            boolean c = buf.readBoolean();
            int n = buf.readVarInt();
            List<String> l = new ArrayList<>();
            for (int i = 0; i < n; i++) l.add(buf.readUtf());
            return new AscensionPacket(c, l);
        }

        void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.omnicrox.gtaa.client.AscensionClient.show(this)));
            ctx.get().setPacketHandled(true);
        }
    }

    /** An infernal mob's modifier names and title words, for the boss bar and particles */
    public record InfernalPacket(int entityId, List<String> mods, String prefix, String suffix) {

        void write(FriendlyByteBuf buf) {
            buf.writeVarInt(entityId);
            buf.writeVarInt(mods.size());
            for (String m : mods) buf.writeUtf(m);
            buf.writeUtf(prefix);
            buf.writeUtf(suffix);
        }

        static InfernalPacket read(FriendlyByteBuf buf) {
            int id = buf.readVarInt();
            int n = buf.readVarInt();
            List<String> m = new ArrayList<>();
            for (int i = 0; i < n; i++) m.add(buf.readUtf());
            return new InfernalPacket(id, m, buf.readUtf(), buf.readUtf());
        }

        void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.omnicrox.gtaa.client.InfernalClient.receive(this)));
            ctx.get().setPacketHandled(true);
        }
    }
}
