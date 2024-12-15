package de.mrjulsen.crn.client.gui;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.AllIcons;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.mcdragonlib.client.render.Sprite;
import de.mrjulsen.mcdragonlib.client.util.Graphics;
import de.mrjulsen.mcdragonlib.client.util.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public enum ModGuiIcons {
    EMPTY("empty", 0, 0),
    CHECK("check", 1, 0),
    CROSS("cross", 2, 0),
    WARN("warn", 3, 0),
    IMPORTANT("important", 4, 0),
    
    SETTINGS("settings", 0, 1),
    FILTER("filter", 1, 1),
    BOOKMARK("bookmark", 2, 1),
    PIN("pin", 3, 1),
    UNPIN("unpin", 4, 1),
    SOUND_ON("sound_on", 5, 1),
    SOUND_OFF("sound_off", 6, 1),
    SCALE("scale", 7, 1),
    TOP_LEFT("top_left", 8, 1),
    TOP_RIGHT("top_right", 9, 1),
    BOTTOM_LEFT("bottom_left", 10, 1),
    BOTTOM_RIGHT("bottom_right", 11, 1),
    POSITION("position", 12, 1),
    TARGET("target", 13, 1),
    TIME("target", 14, 1),
    WALK("walk", 15, 1),

    INFO("info", 0, 2),
    EXPAND("expand", 1, 2),
    COLLAPSE("collapse", 2, 2),
    DELETE("delete", 3, 2),
    ADD("add", 4, 2),
    DOUBLE_SIDED("double_sided", 5, 2),
    TRAIN_DESTINATION("train_destination", 6, 2),
    PASSENGER_INFORMATION("passenger_information", 7, 2),
    PLATFORM_INFORMATION("platform_information", 8, 2),
    LESS_DETAILS("less_details", 9, 2),
    DETAILED("detailed", 10, 2),
    VERY_DETAILED("very_detailed", 11, 2),
    ARROW_RIGHT("arrow_right", 12, 2),
    ARROW_LEFT("arrow_left", 13, 2),
    REFRESH("refresh", 14, 2),
    POPUP("refresh", 15, 2),

    USER("user", 0, 3),
    MAP_PATH("map_path", 1, 3),
    BOOKMARK_FILLED("bookmark_filled", 2, 3),
    HELP("help", 3, 3),
    ROUTE_START("route_start", 4, 3),
    ROUTE_END("route_end", 5, 3),
    CALENDAR("calendar", 6, 3),
    BELL("bell", 7, 3),
    COLOR_PALETTE("color_palette", 8, 3),
    TRAIN("train", 9, 3),
    X("x", 10, 3),
    CHECKMARK("checkmark", 11, 3),
    SHARE("share", 12, 3),
    TEXT("text", 13, 3),
    WIDTH("width", 14, 3),
    CARRIAGE_NUMBER("carriage_number", 15, 3),

    COPY("copy", 0, 4),
    PASTE("paste", 1, 4),
    TRAIN_INFO("train_info", 2, 4),
    EXIT("exit", 3, 4),
    CONNECTIONS("connections", 4, 4);

    private String id;
    private int u;
    private int v;

    public static final int ICON_SIZE = 16;
    public static final ResourceLocation ICON_LOCATION = new ResourceLocation(CreateRailwaysNavigator.MOD_ID, "textures/gui/icons.png");

    ModGuiIcons(String id, int u, int v) {
        this.id = id;
        this.u = u;
        this.v = v;
    }

    public String getId() {
        return id;
    }

    public int getUMultiplier() {
        return u;
    }

    public int getVMultiplier() {
        return v;
    }

    public int getU() {
        return u * ICON_SIZE;
    }

    public int getV() {
        return v * ICON_SIZE;
    }

    public static ModGuiIcons getByStringId(String id) {
        return Arrays.stream(values()).filter(x -> x.getId().equals(id)).findFirst().orElse(ModGuiIcons.EMPTY);
    }

    public AllIcons getAsCreateIcon() {
        return new ModAllIcons(u, v);
    }

    public void render(Graphics graphics, int x, int y) {
        GuiUtils.drawTexture(ModGuiIcons.ICON_LOCATION, graphics, x, y, getU(), getV(), ICON_SIZE, ICON_SIZE);
    }
    
    public Sprite getAsSprite(int renderWidth, int renderHeight) {
        return new Sprite(ICON_LOCATION, 256, 256, getU(), getV(), ICON_SIZE, ICON_SIZE, renderWidth, renderHeight);
    }

    public static class ModAllIcons extends AllIcons {
        int u, v;

        public ModAllIcons(int x, int y) {
            super(x, y);
            this.u = x * ICON_SIZE;
            this.v = y * ICON_SIZE;
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y) {
            graphics.blit(ICON_LOCATION, x, y, 0, u, v, ICON_SIZE, ICON_SIZE, 256, 256);
        }

        @Override
        public void bind() {
            RenderSystem.setShaderTexture(0, ICON_LOCATION);
        }      
        
    }
}
