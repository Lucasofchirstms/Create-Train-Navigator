package de.mrjulsen.crn.block.display.properties.components;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.block.display.properties.IDisplaySettings;
import de.mrjulsen.crn.client.gui.ModGuiIcons;
import de.mrjulsen.crn.client.gui.widgets.IconSlotWidget;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import de.mrjulsen.mcdragonlib.client.gui.widgets.DLCheckBox;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.MutableComponent;

public interface IShowTimeAndDateSetting {

    public static final String GUI_LINE_SHOW_TIME_NAME = "show_time";

    public static final String NBT_SHOW_TIME_AND_DATE = "ShowTime";

    public static final MutableComponent textShowStats = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.show_time_and_date");

    boolean showTimeAndDate();
    void setShowTimeAndDate(boolean b);

    @Environment(EnvType.CLIENT)
    default void buildShowTimeAndDateGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_SHOW_TIME_NAME, (line) -> {            
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.TIME.getAsSprite(16, 16)));            
            line.add(new DLCheckBox(line.getCurrentX() + 4, line.y() + line.height() / 2 - 8, line.getRemainingWidth(), textShowStats.getString(), showTimeAndDate(), (cb) -> setShowTimeAndDate(cb.isChecked())));
        });
    }
    
    default void copyShowTimeAndDateSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof IShowTimeAndDateSetting o) {
            setShowTimeAndDate(o.showTimeAndDate());
        }
    }
}
