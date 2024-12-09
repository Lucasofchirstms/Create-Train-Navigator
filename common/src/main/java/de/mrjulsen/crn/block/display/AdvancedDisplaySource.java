package de.mrjulsen.crn.block.display;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Lang;

import de.mrjulsen.crn.block.properties.ETimeDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;

public class AdvancedDisplaySource extends DisplaySource {

    public static final String NBT_ADVANCED_DISPLAY = "AdvancedDisplay";
    public static final String NBT_FILTER = "Filter";
    public static final String NBT_TRAIN_NAME_WIDTH = "TrainNameWidth";
    public static final String NBT_PLATFORM_WIDTH = "PlatformWidth";
    public static final String NBT_TIME_DISPLAY_TYPE = "TimeDisplay";

	@Override
	public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
		return EMPTY;
	}

	@Override
	public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
		context.sourceConfig().putBoolean(NBT_ADVANCED_DISPLAY, true);
        return List.of();
	}

	@Override
	protected String getTranslationKey() {
		return "advanced_display";
	}

	@Override
	public void populateData(DisplayLinkContext context) {
		CompoundTag conf = context.sourceConfig();

		if (!conf.contains(NBT_TRAIN_NAME_WIDTH))
			conf.putInt(NBT_TRAIN_NAME_WIDTH, 16);
		if (!conf.contains(NBT_PLATFORM_WIDTH))
			conf.putInt(NBT_PLATFORM_WIDTH, -1);
		if (!conf.contains(NBT_TIME_DISPLAY_TYPE))
			conf.putInt(NBT_TIME_DISPLAY_TYPE, ETimeDisplay.ABS.getId());

		if (conf.contains(NBT_FILTER))
			return;
		if (!(context.getSourceBlockEntity() instanceof StationBlockEntity stationBe))
			return;
		
            GlobalStation station = stationBe.getStation();
		if (station == null)
			return;

		conf.putString(NBT_FILTER, station.name);
	}

	@Override
	public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
		if (isFirstLine) {
			builder.addTextInput(0, 137, (e, t) -> {
				e.setValue("");
				t.withTooltip(ImmutableList.of(Lang.translateDirect("display_source.station_summary.filter")
					.withStyle(s -> s.withColor(0x5391E1)),
					Lang.translateDirect("gui.schedule.lmb_edit")
						.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));
			}, NBT_FILTER);
			return;
		}
	}
}