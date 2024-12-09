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

public interface IShowNextConnections {

    public static final String GUI_LINE_SHOW_CONNECTIONS_NAME = "show_connections";

    public static final String NBT_SHOW_CONNECTIONS = "ShowConnections";

    public static final MutableComponent textShowConnections = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.show_connections");

    boolean showConnections();
    void setShowConnection(boolean b);

    @Environment(EnvType.CLIENT)
    default void buildShowConnectionGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_SHOW_CONNECTIONS_NAME, (line) -> {            
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.CONNECTIONS.getAsSprite(16, 16)));            
            line.add(new DLCheckBox(line.getCurrentX() + 4, line.y() + line.height() / 2 - 8, line.getRemainingWidth(), textShowConnections.getString(), showConnections(), (cb) -> setShowConnection(cb.isChecked())));
        });
    }
    
    default void copyShowConnectionSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof IShowNextConnections o) {
            setShowConnection(o.showConnections());
        }
    }
}
