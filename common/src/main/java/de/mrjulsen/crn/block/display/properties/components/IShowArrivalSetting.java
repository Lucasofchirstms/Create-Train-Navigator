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

public interface IShowArrivalSetting {

    public static final String GUI_LINE_SHOW_ARRIVAL_NAME = "show_arrival";

    public static final String NBT_SHOW_ARRIVAL = "ShowArrival";

    public static final MutableComponent textShowArrival = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.show_arrival");
    public static final MutableComponent textShowArrivalDescription = TextUtils.translate("gui." + CreateRailwaysNavigator.MOD_ID + ".advanced_display_settings.show_arrival.description");

    boolean showArrival();
    void setShowArrival(boolean b);

    @Environment(EnvType.CLIENT)
    default void buildShowArrivalGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_SHOW_ARRIVAL_NAME, (line) -> {            
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.TARGET.getAsSprite(16, 16)));            
            line.add(new DLCheckBox(line.getCurrentX() + 4, line.y() + line.height() / 2 - 8, line.getRemainingWidth(), textShowArrival.getString(), showArrival(), (cb) -> setShowArrival(cb.isChecked())) {
                @Override
                public void renderFrontLayer(Graphics graphics, int mouseX, int mouseY, float partialTicks) {
                    super.renderFrontLayer(graphics, mouseX, mouseY, partialTicks);                    
                    if (!isMouseSelected()) {
                        return;
                    }
                    GuiUtils.renderTooltip(container.getParentScreen(), this, List.of(textShowArrivalDescription), container.getParentScreen().width() / 3, graphics, mouseX, mouseY);
                }
            });
        });
    }
    
    default void copyShowArrivalSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof IShowArrivalSetting o) {
            setShowArrival(o.showArrival());
        }
    }
}
