package de.mrjulsen.crn.data.schedule.condition;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Pair;

import de.mrjulsen.crn.CreateRailwaysNavigator;
import de.mrjulsen.crn.client.ClientWrapper;
import de.mrjulsen.crn.data.schedule.IConditionsRequiresInstruction;
import de.mrjulsen.crn.data.schedule.INavigationExtension;
import de.mrjulsen.crn.data.train.StationDepartureHistory;
import de.mrjulsen.crn.data.train.StationDepartureHistory.ETrainFilter;
import de.mrjulsen.mcdragonlib.DragonLib;
import de.mrjulsen.mcdragonlib.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TrainSeparationCondition extends ScheduledDelay implements IDelayedWaitCondition, IConditionsRequiresInstruction {

    public static final String NBT_TIME = "Value";
    public static final String NBT_TRAIN_FILTER = "TrainFilter";
    public static final String NBT_TIME_UNIT = "TimeUnit";
    
    public TrainSeparationCondition() {
        super();
		data.putByte(NBT_TRAIN_FILTER, ETrainFilter.ANY.getIndex());
    }

	@Override
	public Pair<ItemStack, Component> getSummary() {
		return Pair.of(ItemStack.EMPTY, TextUtils.translate(CreateRailwaysNavigator.MOD_ID + ".schedule.condition." + getId().getPath() + ".title", formatTime(true)));
	}

	@Override
	public ItemStack getSecondLineIcon() {
		return new ItemStack(Items.OBSERVER);
	}

    @Override
	public List<Component> getTitleAs(String type) {
		return ImmutableList.of(
			TextUtils.translate(CreateRailwaysNavigator.MOD_ID + ".schedule." + type + "." + getId().getPath()),
			TextUtils.translate(CreateRailwaysNavigator.MOD_ID + ".schedule." + type + "." + getId().getPath() + ".description",
				formatTime(false)
			).withStyle(ChatFormatting.DARK_AQUA),
			TextUtils.translate(getTrainFilter().getValueTranslationKey(CreateRailwaysNavigator.MOD_ID)).withStyle(ChatFormatting.AQUA)
        );
	}

	@Override
	public boolean tickCompletion(Level level, Train train, CompoundTag context) {
		ScheduleEntry entry = train.runtime.getSchedule().entries.get(train.runtime.currentEntry);
		((INavigationExtension)(Object)train.navigation).addDelayedWaitCondition(de.mrjulsen.mcdragonlib.data.Pair.of(this, new DelayedWaitConditionContext(level, train, context, train.getCurrentStation(), entry)));
		return true;
	}


	@Override
	public boolean runDelayed(DelayedWaitConditionContext context) {
		int delayValue = totalWaitTicks();
		long lastDepartureTimestamp = Long.MIN_VALUE;
		String stationName = "";
		ScheduleEntry entry = context.scheduleEntry();
		if (entry.instruction instanceof DestinationInstruction instruction) {
			stationName = instruction.getFilter();
			lastDepartureTimestamp = StationDepartureHistory.getLastMatchingDepartureTime(getTrainFilter(), context.train(), stationName);
		}

		if (lastDepartureTimestamp + delayValue < DragonLib.getCurrentServer().get().overworld().getGameTime()) {
			StationDepartureHistory.updateDepartureHistory(context.train(), context.station().name);
			return true;
		}
		return false;
	}

	@Override
	public ResourceLocation getId() {
		return new ResourceLocation(CreateRailwaysNavigator.MOD_ID, "train_separation");
	}
    
	public ETrainFilter getTrainFilter() {
		return ETrainFilter.getByIndex(data.getByte(NBT_TRAIN_FILTER));
	}

    @Override
	@Environment(EnvType.CLIENT)
	public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
		ClientWrapper.initTimingAdjustmentGui(this, builder);
	}

}
