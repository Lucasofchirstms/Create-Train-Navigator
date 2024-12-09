package de.mrjulsen.crn.block.display.properties.components;

import java.util.List;

import de.mrjulsen.crn.block.display.properties.IDisplaySettings;
import de.mrjulsen.crn.client.gui.ModGuiIcons;
import de.mrjulsen.crn.client.gui.widgets.DLCreateScrollInput;
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

public interface ICarriageIndexSetting {

    public static final String GUI_LINE_CARRIAGE_INDEX_NAME = "carriage_index";

    public static final String NBT_CARRIAGE_INDEX = "CarriageIndexOffset";
    public static final String NBT_OVERWRITE_CARRIAGE_INDEX = "OverwriteCarriageIndex";

    public static final MutableComponent textOverwriteCarriageIndex = TextUtils.translate("gui.createrailwaysnavigator.advanced_display_settings.overwrite_carriage_index");
    public static final MutableComponent textOverwriteCarriageIndexDescription = TextUtils.translate("gui.createrailwaysnavigator.advanced_display_settings.overwrite_carriage_index.description");
    
    byte getCarriageIndex();
    boolean shouldOverwriteCarriageIndex();
    void setCarriageIndex(byte b);
    void setOverwriteCarriageIndex(boolean b);

    @Environment(EnvType.CLIENT)
    default void buildCarriageIndexGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        builder.addLine(GUI_LINE_CARRIAGE_INDEX_NAME, (line) -> {
            line.add(new IconSlotWidget(line.getCurrentX(), line.y() + 2, ModGuiIcons.CARRIAGE_NUMBER.getAsSprite(16, 16)));
            int w = 22;
            line.add(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 6, line.y() + 2, w, 18))
                .setRenderArrow(true)
                .titled(TextUtils.translate("gui.createrailwaysnavigator.advanced_display_settings.carriage_index"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.advanced_display_settings.carriage_index.description"))
                .withRange(0, 100)
                .withShiftStep(4)
                .setState(getCarriageIndex())
                .calling((i) -> {
                    setCarriageIndex(i.byteValue());
                })
            ;
            line.add(new DLCheckBox(line.getCurrentX() + 4, line.y() + line.height() / 2 - 8, line.getRemainingWidth(), textOverwriteCarriageIndex.getString(), shouldOverwriteCarriageIndex(), (c) -> setOverwriteCarriageIndex(c.isChecked())) {
                @Override
                public void renderFrontLayer(Graphics graphics, int mouseX, int mouseY, float partialTicks) {
                    super.renderFrontLayer(graphics, mouseX, mouseY, partialTicks);                    
                    if (!isMouseSelected()) {
                        return;
                    }
                    GuiUtils.renderTooltip(container.getParentScreen(), this, List.of(textOverwriteCarriageIndexDescription), container.getParentScreen().width() / 3, graphics, mouseX, mouseY);
                }
            });
        });
    }
    
    default void copyCarriageIndexSetting(IDisplaySettings oldSettings) {
        if (oldSettings instanceof ICarriageIndexSetting o) {
            setCarriageIndex(o.getCarriageIndex());
            setOverwriteCarriageIndex(o.shouldOverwriteCarriageIndex());
        }
    }
}
