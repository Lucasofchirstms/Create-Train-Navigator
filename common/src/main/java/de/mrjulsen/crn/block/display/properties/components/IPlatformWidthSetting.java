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
 * property {@code platformWidth} from the Advanced Displays.
 * If the class should adopt this property, this interface must be
 * implemented or the value will not be converted!
 */
public interface IPlatformWidthSetting extends ICustomTextWidthSetting {

    public static final String NBT_PLATFORM_WIDTH = "PlatformWidth";

    byte getPlatformWidth();
    void setPlatformWidth(byte b);

    @Environment(EnvType.CLIENT)
    default void buildPlatformWidthGui(ModularWidgetContainer container, ModularWidgetBuilder builder, boolean allowAuto) {
        buildBasicTextWidthGui(container, builder);
        builder.addToLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {
            int w = (line.getWidth() - USED_LINE_SPACE) / 4 - 3;
            line.add(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 4, line.y() + 2, w, 18))
                .titled(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.platform_width"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.platform_width.description"))
                .withRange(allowAuto ? -1 : 0, 65)
                .withShiftStep(4)
                .setState(getPlatformWidth())
                .format((val) -> {
                    if (val >= 0) {
                        return TextUtils.text(String.valueOf(val) + "px");
                    }
                    return TextUtils.translate("gui.createrailwaysnavigator.common.auto");
                })
                .calling((i) -> {
                    setPlatformWidth(i.byteValue());
                })
            ;            
        });
    }
    
    default void copyPlatformWidthSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof IPlatformWidthSetting o) {
            setPlatformWidth(o.getPlatformWidth());
        }
    }

    default boolean isAutoPlatformWidth() {
        return getPlatformWidth() < 0;
    }
}
