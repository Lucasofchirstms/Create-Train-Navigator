package de.mrjulsen.crn.block.display.properties;

import de.mrjulsen.crn.block.display.properties.components.ITimeDisplaySetting;
import de.mrjulsen.crn.block.properties.ETimeDisplay;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

public class PlatformDisplayScrollingTextSettings extends BasicDisplaySettings implements ITimeDisplaySetting {

    protected ETimeDisplay timeDisplay = ETimeDisplay.ABS;

    @Override
    public void deserializeNbt(CompoundTag nbt) {
        super.deserializeNbt(nbt);
        if (nbt.contains(NBT_TIME_DISPLAY)) this.timeDisplay = ETimeDisplay.getById(nbt.getByte(NBT_TIME_DISPLAY));
    }

    @Override
    public void serializeNbt(CompoundTag nbt) {
        super.serializeNbt(nbt);
        nbt.putByte(NBT_TIME_DISPLAY, timeDisplay.getId());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        this.buildColorGui(container, builder);
        this.buildTimeDisplayGui(container, builder);
    }

    @Override
    public void onChangeSettings(IDisplaySettings oldSettings) {
        super.onChangeSettings(oldSettings);
        copyTimeDisplaySetting(oldSettings);
    }

    @Override
    public ETimeDisplay getTimeDisplay() {
        return timeDisplay;
    }

    @Override
    public void setTimeDisplay(ETimeDisplay display) {
        this.timeDisplay = display;
    }
    
}
