package de.mrjulsen.crn.block.display.properties.components;

import java.util.List;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.block.display.properties.IDisplaySettings;
import de.mrjulsen.crn.client.gui.ModGuiIcons;
import de.mrjulsen.crn.client.gui.widgets.IconSlotWidget;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import de.mrjulsen.mcdragonlib.client.gui.widgets.DLCheckBox;
import de.mrjulsen.mcdragonlib.client.util.Graphics;
import de.mrjulsen.mcdragonlib.client.util.GuiUtils;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.MutableComponent;

public interface IShowLineColorSetting {

    public static final String GUI_LINE_SHOW_LINE_COLOR_NAME = "show_line_color";

    public static final String NBT_SHOW_LINE_COLOR = "ShowLineColor";

    public static final MutableComponent textShowLineColor = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.show_line_color");
    public static final MutableComponent textShowLineColorDescription = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.show_line_color.description");

    boolean showLineColor();
    void setShowLineColor(boolean b);

    @Environment(EnvType.CLIENT)
    default void buildShowLineColorGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_SHOW_LINE_COLOR_NAME, (line) -> {            
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.COLOR_PALETTE.getAsSprite(16, 16)));            
            line.add(new DLCheckBox(line.getCurrentX() + 4, line.y() + line.height() / 2 - 8, line.getRemainingWidth(), textShowLineColor.getString(), showLineColor(), (cb) -> setShowLineColor(cb.isChecked())) {
                @Override
                public void renderFrontLayer(Graphics graphics, int mouseX, int mouseY, float partialTicks) {
                    super.renderFrontLayer(graphics, mouseX, mouseY, partialTicks);                    
                    if (!isMouseSelected()) {
                        return;
                    }
                    GuiUtils.renderTooltip(container.getParentScreen(), this, List.of(textShowLineColorDescription), container.getParentScreen().width() / 3, graphics, mouseX, mouseY);
                }
            });
        });
    }
    
    default void copyShowLineColorSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof IShowLineColorSetting o) {
            setShowLineColor(o.showLineColor());
        }
    }
}
