package de.mrjulsen.crn.block.display.properties.components;

import de.mrjulsen.crn.block.display.properties.IDisplaySettings;
import de.mrjulsen.crn.client.gui.widgets.DLCreateScrollInput;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * For data conversion: Indicates that this class adopts the original
 * property {@code timeDisplay} from the Advanced Displays.
 * If the class should adopt this property, this interface must be
 * implemented or the value will not be converted!
 */
public interface ITrainNameWidthSetting extends ICustomTextWidthSetting {

    public static final int DEFAULT_TRAIN_NAME_WIDTH = 16;
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 100;
    public static final String NBT_TRAIN_NAME_WIDTH = "TrainNameWidth";
    
    byte getTrainNameWidth();
    void setTrainNameWidth(byte b);

    @Environment(EnvType.CLIENT)
    default void buildTrainNameGui(ModularWidgetContainer container, ModularWidgetBuilder builder, boolean allowAuto, boolean allowMax) {
        buildBasicTextWidthGui(container, builder);
        builder.addToLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {
            int w = (line.getWidth() - USED_LINE_SPACE) / 4 - 3;
            line.add(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 4, line.y() + 2, w, 18))
                .titled(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.train_name_width"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.train_name_width.description"))
                .withRange(MIN_VALUE - (allowAuto ? 1 : 0), MAX_VALUE + (allowMax ? 1 : 0))
                .withShiftStep(5)
                .setState(getTrainNameWidth())
                .format((val) -> {
                    if (val < 0) {
                        return TextUtils.translate("gui.createrailwaysnavigator.common.auto");
                    } else if (val >= 100) {
                        return TextUtils.translate("gui.createrailwaysnavigator.common.max");
                    }
                    return TextUtils.text(String.valueOf(val) + "px");
                })
                .calling((i) -> {
                    setTrainNameWidth(i.byteValue());
                })
            ;
        });
    }
    
    default void copyTrainNameSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof ITrainNameWidthSetting o) {
            setTrainNameWidth(o.getTrainNameWidth());
        }
    }

    default boolean isAutoTrainNameWidth() {
        return getTrainNameWidth() < MIN_VALUE;
    }

    default boolean isFullTrainNameWidth() {
        return getTrainNameWidth() >= MAX_VALUE;
    }
}
