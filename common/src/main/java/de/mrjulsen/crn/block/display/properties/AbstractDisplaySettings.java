package de.mrjulsen.crn.block.display.properties;

import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetBuilder;
import de.mrjulsen.crn.client.gui.widgets.modular.ModularWidgetContainer;
import net.minecraft.nbt.CompoundTag;

public abstract class AbstractDisplaySettings implements IDisplaySettings {

    public static final class EmptyDisplaySettings extends AbstractDisplaySettings {

        @Override
        public void deserializeNbt(CompoundTag nbt) { }

        @Override
        public void buildGui(ModularWidgetContainer container, ModularWidgetBuilder builder) { }

        @Override
        public void serializeNbt(CompoundTag nbt) { }

        @Override
        public void onChangeSettings(IDisplaySettings oldSettings) { }
    }

    public AbstractDisplaySettings() {}

    @Override
    public final CompoundTag serializeNbt() {
        CompoundTag nbt = new CompoundTag();
        this.serializeNbt(nbt);
        return nbt;
    }

    public abstract void serializeNbt(CompoundTag nbt);
}
