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

public interface IShowTrainStatsSetting {

    public static final String GUI_LINE_SHOW_ARRIVAL_NAME = "show_stats";

    public static final String NBT_SHOW_STATS = "ShowStats";

    public static final MutableComponent textShowStats = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.show_stats");

    boolean showStats();
    void setShowStats(boolean b);

    @Environment(EnvType.CLIENT)
    default void buildShowStatsGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_SHOW_ARRIVAL_NAME, (line) -> {            
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.TRAIN_INFO.getAsSprite(16, 16)));            
            line.add(new DLCheckBox(line.getCurrentX() + 4, line.y() + line.height() / 2 - 8, line.getRemainingWidth(), textShowStats.getString(), showStats(), (cb) -> setShowStats(cb.isChecked())));
        });
    }
    
    default void copyShowStatsSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof IShowTrainStatsSetting o) {
            setShowStats(o.showStats());
        }
    }
}
