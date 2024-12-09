package de.mrjulsen.crn.block.display.properties;

import de.mrjulsen.crn.block.display.properties.components.IPlatformWidthSetting;
import de.mrjulsen.crn.block.display.properties.components.IShowArrivalSetting;
import de.mrjulsen.crn.block.display.properties.components.IShowLineColorSetting;
import de.mrjulsen.crn.block.display.properties.components.ITimeDisplaySetting;
import de.mrjulsen.crn.block.display.properties.components.ITrainNameWidthSetting;
import de.mrjulsen.crn.block.properties.ETimeDisplay;
import de.mrjulsen.crn.client.gui.widgets.DLCreateScrollInput;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

public class PlatformDisplayFocusSettings extends BasicDisplaySettings implements ITimeDisplaySetting, ITrainNameWidthSetting, IPlatformWidthSetting, IShowArrivalSetting, IShowLineColorSetting {

    public static final String NBT_TRAIN_NAME_WIDTH_NEXT_STOP = "TrainNameWidthNextStop";
    public static final String NBT_PLATFORM_WIDTH_NEXT_STOP = "PlatformWidthNextStop";

    protected ETimeDisplay timeDisplay = ETimeDisplay.ABS;
    protected byte trainNameWidth = 12;
    protected byte trainNameWidthNextStop = -1;
    protected byte platformWidth = -1;
    protected byte platformWidthNextStop = -1;
    protected boolean showArrival = true;
    protected boolean showTrainLineColor = false;

    @Override
    public void deserializeNbt(CompoundTag nbt) {
        super.deserializeNbt(nbt);
        if (nbt.contains(NBT_TIME_DISPLAY)) this.timeDisplay = ETimeDisplay.getById(nbt.getByte(NBT_TIME_DISPLAY));
        if (nbt.contains(NBT_TRAIN_NAME_WIDTH)) this.trainNameWidth = nbt.getByte(NBT_TRAIN_NAME_WIDTH);
        if (nbt.contains(NBT_TRAIN_NAME_WIDTH_NEXT_STOP)) this.trainNameWidthNextStop = nbt.getByte(NBT_TRAIN_NAME_WIDTH_NEXT_STOP);
        if (nbt.contains(NBT_PLATFORM_WIDTH)) this.platformWidth = nbt.getByte(NBT_PLATFORM_WIDTH);
        if (nbt.contains(NBT_PLATFORM_WIDTH_NEXT_STOP)) this.platformWidthNextStop = nbt.getByte(NBT_PLATFORM_WIDTH_NEXT_STOP);
        if (nbt.contains(NBT_SHOW_ARRIVAL)) this.showArrival = nbt.getBoolean(NBT_SHOW_ARRIVAL);
        if (nbt.contains(NBT_SHOW_LINE_COLOR)) this.showTrainLineColor = nbt.getBoolean(NBT_SHOW_LINE_COLOR);
    }

    @Override
    public void serializeNbt(CompoundTag nbt) {
        super.serializeNbt(nbt);
        nbt.putByte(NBT_TIME_DISPLAY, timeDisplay.getId());
        nbt.putByte(NBT_TRAIN_NAME_WIDTH, trainNameWidth);
        nbt.putByte(NBT_TRAIN_NAME_WIDTH_NEXT_STOP, trainNameWidthNextStop);
        nbt.putByte(NBT_PLATFORM_WIDTH, platformWidth);
        nbt.putByte(NBT_PLATFORM_WIDTH_NEXT_STOP, platformWidthNextStop);
        nbt.putBoolean(NBT_SHOW_ARRIVAL, showArrival);
        nbt.putBoolean(NBT_SHOW_LINE_COLOR, showTrainLineColor);

    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        this.buildColorGui(container, builder);
        this.buildTimeDisplayGui(container, builder);
        this.buildBasicTextWidthGui(container, builder);
        builder.addToLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {
            int w = (line.getWidth() - USED_LINE_SPACE) / 4 - 3;
            line.add(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 4, line.y() + 2, w, 18))
                .titled(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.train_name_width_table"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.train_name_width.description"))
                .withRange(-1, 100)
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
        builder.addToLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {
            int w = (line.getWidth() - USED_LINE_SPACE) / 4 - 3;
            line.add(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 4, line.y() + 2, w, 18))
                .titled(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.platform_width_table"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.platform_width.description"))
                .withRange(-1, 65)
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


        builder.addToLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {
            int w = (line.getWidth() - USED_LINE_SPACE) / 4 - 3;
            line.add(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 4, line.y() + 2, w, 18))
                .titled(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.train_name_width_next"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.train_name_width.description"))
                .withRange(-1, 100)
                .withShiftStep(5)
                .setState(getTrainNameWidthNextStop())
                .format((val) -> {
                    if (val < 0) {
                        return TextUtils.translate("gui.createrailwaysnavigator.common.auto");
                    } else if (val >= 100) {
                        return TextUtils.translate("gui.createrailwaysnavigator.common.max");
                    }
                    return TextUtils.text(String.valueOf(val) + "px");
                })
                .calling((i) -> {
                    setTrainNameWidthNextStop(i.byteValue());
                })
            ;
        });
        builder.addToLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {
            int w = (line.getWidth() - USED_LINE_SPACE) / 4 - 3;
            line.add(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 4, line.y() + 2, w, 18))
                .titled(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.platform_width_next"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.platform_width.description"))
                .withRange(-1, 65)
                .withShiftStep(4)
                .setState(getPlatformWidthNextStop())
                .format((val) -> {
                    if (val >= 0) {
                        return TextUtils.text(String.valueOf(val) + "px");
                    }
                    return TextUtils.translate("gui.createrailwaysnavigator.common.auto");
                })
                .calling((i) -> {
                    setPlatformWidthNextStop(i.byteValue());
                })
            ;            
        });
        this.buildShowArrivalGui(container, builder);
        this.buildShowLineColorGui(container, builder);
    }

    @Override
    public void onChangeSettings(IDisplaySettings oldSettings) {
        super.onChangeSettings(oldSettings);
        copyTimeDisplaySetting(oldSettings);
        copyTrainNameSetting(oldSettings);
        copyPlatformWidthSetting(oldSettings);
        copyShowArrivalSetting(oldSettings);
        copyShowLineColorSetting(oldSettings);
        
        if (oldSettings instanceof PlatformDisplayFocusSettings o) {
            setTrainNameWidthNextStop(o.getTrainNameWidthNextStop());
        }
    }

    @Override
    public ETimeDisplay getTimeDisplay() {
        return timeDisplay;
    }

    @Override
    public void setTimeDisplay(ETimeDisplay display) {
        this.timeDisplay = display;
    }

    @Override
    public byte getPlatformWidth() {
        return platformWidth;
    }

    @Override
    public void setPlatformWidth(byte b) {
        this.platformWidth = b;
    }

    public byte getPlatformWidthNextStop() {
        return platformWidthNextStop;
    }

    public void setPlatformWidthNextStop(byte b) {
        this.platformWidthNextStop = b;
    }

    @Override
    public byte getTrainNameWidth() {
        return trainNameWidth;
    }

    @Override
    public void setTrainNameWidth(byte b) {
        this.trainNameWidth = b;
    }

    public byte getTrainNameWidthNextStop() {
        return trainNameWidthNextStop;
    }

    public void setTrainNameWidthNextStop(byte b) {
        this.trainNameWidthNextStop = b;
    }

    @Override
    public boolean showArrival() {
        return showArrival;
    }

    @Override
    public void setShowArrival(boolean b) {
        this.showArrival = b;
    }    

    public boolean isAutoTrainNameWidthNextStop() {
        return getTrainNameWidthNextStop() < 0;
    }

    public boolean isAutoPlatformWidthNextStop() {
        return getPlatformWidthNextStop() < 0;
    }

    @Override
    public void setShowLineColor(boolean b) {
        this.showTrainLineColor = b;
    }

    @Override
    public boolean showLineColor() {
        return showTrainLineColor;
    }
}
