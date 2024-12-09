package de.mrjulsen.crn.block.display.properties;

import com.simibubi.create.foundation.gui.widget.ScrollInput;

import de.mrjulsen.crn.block.display.properties.components.IPlatformWidthSetting;
import de.mrjulsen.crn.block.display.properties.components.IShowArrivalSetting;
import de.mrjulsen.crn.block.display.properties.components.IShowLineColorSetting;
import de.mrjulsen.crn.block.display.properties.components.ITimeDisplaySetting;
import de.mrjulsen.crn.block.display.properties.components.ITrainNameWidthSetting;
import de.mrjulsen.crn.block.properties.ETimeDisplay;
import de.mrjulsen.crn.client.gui.widgets.DLCreateScrollInput;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import de.mrjulsen.mcdragonlib.data.Single.MutableSingle;
import de.mrjulsen.mcdragonlib.util.DLUtils;
import de.mrjulsen.mcdragonlib.util.MathUtils;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

public class DepartureBoardDisplayTableSettings extends BasicDisplaySettings implements ITimeDisplaySetting, ITrainNameWidthSetting, IPlatformWidthSetting, IShowArrivalSetting, IShowLineColorSetting {

    protected static final String NBT_INFO_WIDTH = "InfoWidth";
    protected static final String NBT_STOPOVERS_WIDTH = "StopoversWidth";
    
    protected ETimeDisplay timeDisplay = ETimeDisplay.ABS;
    protected byte trainNameWidth = ITrainNameWidthSetting.DEFAULT_TRAIN_NAME_WIDTH;
    protected byte platformWidth = ITrainNameWidthSetting.DEFAULT_TRAIN_NAME_WIDTH;
    protected boolean showArrival = false;
    protected boolean showLineColor = false;
    protected float infoWidthPercentage = 0.25f;
    protected float stopoversWidthPercentage = 0.33f;

    @Override
    public void deserializeNbt(CompoundTag nbt) {
        super.deserializeNbt(nbt);
        if (nbt.contains(NBT_TIME_DISPLAY)) this.timeDisplay = ETimeDisplay.getById(nbt.getByte(NBT_TIME_DISPLAY));
        if (nbt.contains(NBT_TRAIN_NAME_WIDTH)) this.trainNameWidth = nbt.getByte(NBT_TRAIN_NAME_WIDTH);
        if (nbt.contains(NBT_PLATFORM_WIDTH)) this.platformWidth = nbt.getByte(NBT_PLATFORM_WIDTH);
        if (nbt.contains(NBT_SHOW_ARRIVAL)) this.showArrival = nbt.getBoolean(NBT_SHOW_ARRIVAL);
        if (nbt.contains(NBT_SHOW_LINE_COLOR)) this.showLineColor = nbt.getBoolean(NBT_SHOW_LINE_COLOR);
        if (nbt.contains(NBT_INFO_WIDTH)) this.infoWidthPercentage = MathUtils.clamp(nbt.getFloat(NBT_INFO_WIDTH), 0, 1);
        if (nbt.contains(NBT_STOPOVERS_WIDTH)) this.stopoversWidthPercentage = MathUtils.clamp(nbt.getFloat(NBT_STOPOVERS_WIDTH), 0, 1);
    }

    @Override
    public void serializeNbt(CompoundTag nbt) {
        super.serializeNbt(nbt);
        nbt.putByte(NBT_TIME_DISPLAY, timeDisplay.getId());
        nbt.putByte(NBT_TRAIN_NAME_WIDTH, trainNameWidth);
        nbt.putByte(NBT_PLATFORM_WIDTH, platformWidth);
        nbt.putBoolean(NBT_SHOW_ARRIVAL, showArrival);
        nbt.putBoolean(NBT_SHOW_LINE_COLOR, showLineColor);
        nbt.putFloat(NBT_INFO_WIDTH, infoWidthPercentage);
        nbt.putFloat(NBT_STOPOVERS_WIDTH, stopoversWidthPercentage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        this.buildColorGui(container, builder);
        this.buildTimeDisplayGui(container, builder);
        this.buildTrainNameGui(container, builder, false, false);
        this.buildPlatformWidthGui(container, builder, false);

        MutableSingle<ScrollInput> stopovers = new MutableSingle<ScrollInput>(null);
        MutableSingle<ScrollInput> info = new MutableSingle<ScrollInput>(null);
        builder.addToLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {
            int w = (line.getWidth() - USED_LINE_SPACE) / 4 - 3;
            stopovers.setFirst(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 4, line.y() + 2, w, 18)
                .titled(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.stopovers_width"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.stopovers_width.description"))
                .withRange(0, 101)
                .withShiftStep(5)
                .setState((int)(getStopoversWidthPercentage() * 100))
                .format((val) -> {
                    return TextUtils.text(String.valueOf(val) + "%");
                })
                .calling((i) -> {
                    setStopoversWidthPercentageInt(i.byteValue());
                    DLUtils.doIfNotNull(info.getFirst(), x -> x.withRange(0, MathUtils.clamp(101 - i, 0, 101)));
                })
            );
            line.add(stopovers.getFirst());
            if (stopovers.getFirst() != null && info.getFirst() != null) {
                stopovers.getFirst().withRange(0, MathUtils.clamp(101 - info.getFirst().getState(), 0, 101));
                info.getFirst().withRange(0, MathUtils.clamp(101 - stopovers.getFirst().getState(), 0, 101));
            }
        });
        builder.addToLine(GUI_LINE_TEXT_SIZE_NAME, (line) -> {
            int w = (line.getWidth() - USED_LINE_SPACE) / 4 - 3;
            info.setFirst(new DLCreateScrollInput(container.getParentScreen(), line.getCurrentX() + 4, line.y() + 2, w, 18)
                .titled(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.info_width"))
                .addHint(TextUtils.translate("gui.createrailwaysnavigator.display_source.advanced_display.info_width.description"))
                .withRange(0, 101)
                .withShiftStep(5)
                .setState((int)(getInfoWidthPercentage() * 100))
                .format((val) -> {
                    return TextUtils.text(String.valueOf(val) + "%");
                })
                .calling((i) -> {
                    setInfoWidthPercentageInt(i.byteValue());
                    DLUtils.doIfNotNull(stopovers.getFirst(), x -> x.withRange(0, MathUtils.clamp(101 - i, 0, 101)));
                })
            );
            line.add(info.getFirst());
            if (stopovers.getFirst() != null && info.getFirst() != null) {
                stopovers.getFirst().withRange(0, MathUtils.clamp(101 - info.getFirst().getState(), 0, 101));
                info.getFirst().withRange(0, MathUtils.clamp(101 - stopovers.getFirst().getState(), 0, 101));
            }
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

    @Override
    public byte getTrainNameWidth() {
        return trainNameWidth;
    }

    @Override
    public void setTrainNameWidth(byte b) {
        this.trainNameWidth = b;
    }

    @Override
    public boolean showArrival() {
        return showArrival;
    }

    @Override
    public void setShowArrival(boolean b) {
        this.showArrival = b;
    }
    
    public float getInfoWidthPercentage() {
        return infoWidthPercentage;
    }

    public float getStopoversWidthPercentage() {
        return stopoversWidthPercentage;
    }

    public void setInfoWidthPercentage(float f) {
        this.infoWidthPercentage = MathUtils.clamp(f, 0, 1);
    }

    public void setStopoversWidthPercentage(float f) {
        this.stopoversWidthPercentage = MathUtils.clamp(f, 0, 1);
    }

    public void setInfoWidthPercentageInt(byte f) {
        this.infoWidthPercentage = (float)MathUtils.clamp(f, 0, 100) / 100f;
    }

    public void setStopoversWidthPercentageInt(byte f) {
        this.stopoversWidthPercentage = (float)MathUtils.clamp(f, 0, 100) / 100f;
    }

    @Override
    public boolean showLineColor() {
        return showLineColor;
    }

    @Override
    public void setShowLineColor(boolean b) {
        this.showLineColor = b;
    }
}