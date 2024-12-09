package de.mrjulsen.crn.block.display.properties.components;

import java.util.Arrays;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.block.display.properties.IDisplaySettings;
import de.mrjulsen.crn.block.properties.ETimeDisplay;
import de.mrjulsen.crn.client.gui.ModGuiIcons;
import de.mrjulsen.crn.client.gui.widgets.DLCreateSelectionScrollInput;
import de.mrjulsen.crn.client.gui.widgets.IconSlotWidget;
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
public interface ITimeDisplaySetting extends ICustomTextWidthSetting {

    public static final String GUI_LINE_TIME_NAME = "time";
    public static final String NBT_TIME_DISPLAY = "TimeDisplay";

    ETimeDisplay getTimeDisplay();
    void setTimeDisplay(ETimeDisplay display);

    @Environment(EnvType.CLIENT)
    default void buildTimeDisplayGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_TIME_NAME, (line) -> {            
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.TIME.getAsSprite(16, 16)));            
            line.add(new DLCreateSelectionScrollInput(container.getParentScreen(), line.getCurrentX() + 6, line.y() + 2, 32, 18))
                .setRenderArrow(true)
                .forOptions(Arrays.stream(ETimeDisplay.values()).map(x -> TextUtils.translate(x.getValueInfoTranslationKey(CreateRailwaysNavigator.MOD_ID))).toList())
                .titled(TextUtils.translate("enum.createrailwaysnavigator.time_display"))
                .addHint(TextUtils.translate("enum.createrailwaysnavigator.time_display.description"))
                .format((val) -> {
                    return TextUtils.translate(ETimeDisplay.getById(val).getValueTranslationKey(CreateRailwaysNavigator.MOD_ID));
                })
                .setState(getTimeDisplay().getId())
                .calling((i) -> {
                    setTimeDisplay(ETimeDisplay.getById(i));
                })
            ;
        });
    }
    
    default void copyTimeDisplaySetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof ITimeDisplaySetting o) {
            setTimeDisplay(o.getTimeDisplay());
        }
    }
}
