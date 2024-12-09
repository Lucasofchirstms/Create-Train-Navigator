package de.mrjulsen.crn.client.gui.widgets.flyouts;

import java.util.function.Consumer;

import de.mrjulsen.crn.client.gui.CreateDynamicWidgets.ColorShade;
import de.mrjulsen.crn.client.gui.widgets.AbstractFlyoutWidget;
import de.mrjulsen.crn.client.gui.widgets.ColorPickerWidget;
import de.mrjulsen.mcdragonlib.client.gui.DLScreen;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class FlyoutColorPicker<T extends GuiEventListener & Widget & NarratableEntry> extends AbstractFlyoutWidget<T> {

    private final ColorPickerWidget colorPicker;

    public FlyoutColorPicker(DLScreen screen, int initialColor, int[] defaultColors, int colorsPerLine, boolean allowCustom, boolean allowNone, Consumer<T> addRenderableWidgetFunc, Consumer<GuiEventListener> removeWidgetFunc) {
        super(screen, 100, 50, FlyoutPointer.RIGHT, ColorShade.LIGHT, addRenderableWidgetFunc, removeWidgetFunc);
        colorPicker = addRenderableWidget(new ColorPickerWidget(screen, x() + 10, y() + 10, defaultColors, colorsPerLine, initialColor, allowCustom, allowNone, (picker) -> {
            closeImmediately();
        }));
        set_width(colorPicker.width() + 20);
        set_height(colorPicker.height() + 20);
    }

    public ColorPickerWidget getColorPicker() {
        return colorPicker;
    }
}
