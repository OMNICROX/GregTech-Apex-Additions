package com.omnicrox.gtaa.client;

import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

/** Personal Space setup: floor, time and mobs, then Create (sends /gtapexadditions personalspace create ...) */
public class PersonalSpaceScreen extends Screen {

    private static final Map<String, String> NAMES = Map.of("void", "Void + platform", "stone", "Flat stone",
            "grass", "Flat grass", "cycle", "Day and night", "day", "Always day", "night", "Always night");
    private final List<String> floors, times;
    private String floor, time;
    private boolean mobs;

    public PersonalSpaceScreen(List<String> floors, List<String> times) {
        super(Component.literal("Personal Space"));
        this.floors = floors;
        this.times = times;
        this.floor = floors.get(0);
        this.time = times.get(0);
    }

    public static void open(GTAANetwork.PersonalSpacePacket p) {
        Minecraft.getInstance().setScreen(new PersonalSpaceScreen(p.floors(), p.times()));
    }

    private static Component name(String s) {
        return Component.literal(NAMES.getOrDefault(s, s));
    }

    @Override
    protected void init() {
        int x = width / 2 - 100, y = height / 2 - 50;
        addRenderableWidget(CycleButton.<String>builder(PersonalSpaceScreen::name).withValues(floors).withInitialValue(floor)
                .create(x, y, 200, 20, Component.literal("Floor"), (b, v) -> floor = v));
        addRenderableWidget(CycleButton.<String>builder(PersonalSpaceScreen::name).withValues(times).withInitialValue(time)
                .create(x, y + 24, 200, 20, Component.literal("Time"), (b, v) -> time = v));
        addRenderableWidget(CycleButton.onOffBuilder(mobs).create(x, y + 48, 200, 20, Component.literal("Mobs spawn"),
                (b, v) -> mobs = v));
        addRenderableWidget(Button.builder(Component.literal("Create my world"), b -> {
            var player = Minecraft.getInstance().player;
            if (player != null) player.connection.sendCommand("gtapexadditions personalspace create " + floor + " " + time + " " + mobs);
            onClose();
        }).bounds(x, y + 80, 98, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose()).bounds(x + 102, y + 80, 98, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        g.drawCenteredString(font, title, width / 2, height / 2 - 80, 0xFFE0A0FF);
        g.drawCenteredString(font, Component.literal("Your own world. These can't be changed later."), width / 2, height / 2 - 66, 0xFFAAAAAA);
        super.render(g, mx, my, pt);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
