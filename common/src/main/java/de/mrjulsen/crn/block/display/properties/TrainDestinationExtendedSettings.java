package de.mrjulsen.crn.block.display.properties;

import de.mrjulsen.crn.block.display.properties.components.ITrainNameWidthSetting;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

public class TrainDestinationExtendedSettings extends BasicDisplaySettings implements ITrainNameWidthSetting {

    protected byte trainNameWidth = -1;

    @Override
    public void deserializeNbt(CompoundTag nbt) {
        super.deserializeNbt(nbt);
        if (nbt.contains(NBT_TRAIN_NAME_WIDTH)) this.trainNameWidth = nbt.getByte(NBT_TRAIN_NAME_WIDTH);
    }

    @Override
    public void serializeNbt(CompoundTag nbt) {
        super.serializeNbt(nbt);
        nbt.putByte(NBT_TRAIN_NAME_WIDTH, trainNameWidth);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildGui(ModularWidgetContainer container, ModularWidgetBuilder builder) {
        this.buildColorGui(container, builder);
        this.buildTrainNameGui(container, builder, true, true);
    }

    @Override
    public void onChangeSettings(IDisplaySettings oldSettings) {
        super.onChangeSettings(oldSettings);
        copyTrainNameSetting(oldSettings);
    }

    @Override
    public byte getTrainNameWidth() {
        return trainNameWidth;
    }

    @Override
    public void setTrainNameWidth(byte b) {
        this.trainNameWidth = b;
    }
    
}
