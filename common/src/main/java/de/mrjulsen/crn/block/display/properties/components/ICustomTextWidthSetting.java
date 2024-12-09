package de.mrjulsen.crn.block.display.properties.components;

import de.mrjulsen.crn.client.gui.ModGuiIcons;
import de.mrjulsen.crn.client.gui.widgets.IconSlotWidget;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ICustomTextWidthSetting {    
    public static final String GUI_LINE_TEXT_SIZE_NAME = "text_width";
    
    public static final int USED_LINE_SPACE = 18 + 4;

    @Environment(EnvType.CLIENT)
    default void buildBasicTextWidthGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {            
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.WIDTH.getAsSprite(16, 16)));
        });
    }
}
