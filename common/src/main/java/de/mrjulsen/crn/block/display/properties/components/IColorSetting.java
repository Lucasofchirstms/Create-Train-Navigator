package de.mrjulsen.crn.block.display.properties.components;

import java.util.List;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.block.display.properties.IDisplaySettings;
import de.mrjulsen.crn.client.gui.ModGuiIcons;
import de.mrjulsen.crn.client.gui.widgets.ColorSlotWidget;
import de.mrjulsen.crn.client.gui.widgets.IconSlotWidget;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import de.mrjulsen.crn.util.ModUtils;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public interface IColorSetting {

    public static final String GUI_LINE_COLORS_NAME = "color";

    public static final String NBT_FONT_COLOR = "FontColor";
    public static final String NBT_BACK_COLOR = "BackColor";

    public static final MutableComponent textFontColor = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.font_color");
    public static final MutableComponent textBackColor = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.back_color");
    public static final MutableComponent textClickToEdit = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".common.click_to_edit").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC);

    int getFontColor();
    int getBackColor();
    void setFontColor(int color);
    void setBackColor(int color);

    @Environment(EnvType.CLIENT)
    default void buildColorGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_COLORS_NAME, (line) -> {
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.COLOR_PALETTE.getAsSprite(16, 16)));
            line.add(new ColorSlotWidget(
                container.getParentScreen(),
                line.getCurrentX() + 4,
                line.y() + 2,
                getFontColor() == 0 ? 0 : (0xFF << 24) | (getFontColor() & 0x00FFFFFF),
                ModUtils.getDyeColors(),
                false,
                false,
                List.of(textFontColor, textClickToEdit),
                () -> 0,
                (b) -> setFontColor(b.getSelectedColor())
            ));
            line.add(new ColorSlotWidget(
                container.getParentScreen(),
                line.getCurrentX() + 4,
                line.y() + 2,
                getBackColor() == 0 ? 0 : (0xFF << 24) | (getBackColor() & 0x00FFFFFF),
                ModUtils.getDyeColors(),
                false,
                true,
                List.of(textBackColor, textClickToEdit),
                () -> 0,
                (b) -> setBackColor(b.getSelectedColor())
            ));
        });
    }
    
    default void copyColorSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof IColorSetting o) {
            setBackColor(o.getBackColor());
            setFontColor(o.getFontColor());
        }
    }
}
