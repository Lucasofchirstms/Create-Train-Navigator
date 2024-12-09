package de.mrjulsen.crn.data.train.portable;

import java.util.List;
import java.util.Objects;

import de.mrjulsen.crn.exceptions.RuntimeSideException;
import de.mrjulsen.crn.data.train.TrainData;
import de.mrjulsen.crn.data.train.TrainListener;
import de.mrjulsen.crn.data.train.TrainStop;
import de.mrjulsen.crn.data.train.TrainTravelSection;
import de.mrjulsen.crn.event.ModCommonEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class StationDisplayData {
    private final BasicTrainDisplayData trainData;
    private final TrainStopDisplayData stationData;
    private final String firstStopName;
    private final boolean isLastStop;
    private final boolean isNextSectionExcluded;
    private final List<String> stopovers;

    private static final String NBT_TRAIN = "Train";
    private static final String NBT_STATION = "Station";
    private static final String NBT_STOPOVERS = "Stopovers";
    private static final String NBT_FIRST_STOP = "FirstStop";
    private static final String NBT_IS_LAST = "IsLast";
    private static final String NBT_IS_NEXT_EXCLUDED = "IsNextSectionExcluded";

    

    public StationDisplayData(
        BasicTrainDisplayData trainData,
        TrainStopDisplayData stationData,
        String firstStopName,
        boolean isLastStop,
        boolean isNextSectionExcluded,
        List<String> stopovers
    ) {
        this.trainData = trainData;
        this.stationData = stationData;
        this.stopovers = stopovers;
        this.firstStopName = firstStopName;
        this.isLastStop = isLastStop;
        this.isNextSectionExcluded = isNextSectionExcluded;
    }

    public static StationDisplayData empty() {
        return new StationDisplayData(BasicTrainDisplayData.empty(), TrainStopDisplayData.empty(), "", false, false, List.of());
    }

    /** Server-side only! */
    public static StationDisplayData of(TrainStop stop) throws RuntimeSideException {
        if (!ModCommonEvents.hasServer()) {
            throw new RuntimeSideException(false);
        }
        if (!TrainListener.data.containsKey(stop.getTrainId())) {
            return empty();
        }
        TrainData data = TrainListener.data.get(stop.getTrainId());
        TrainTravelSection section = data.getSectionByIndex(stop.getSectionIndex());
        TrainTravelSection previousSection = section.previousSection();
        String firstStop = section.getFirstStop().isPresent() ? section.getFirstStop().get().getStationTag().getTagName().get() : "";
        boolean isLastStopOfSection = section.getFinalStop().isPresent() && (previousSection.shouldIncludeNextStationOfNextSection() && previousSection.getFinalStop().isPresent() ? previousSection.getFinalStop().get() : section.getFinalStop().get()).getEntryIndex() == stop.getScheduleIndex();
        if (isLastStopOfSection) {
            if (previousSection.shouldIncludeNextStationOfNextSection() && previousSection.getFinalStop().isPresent() && previousSection.getFinalStop().get().getEntryIndex() == stop.getScheduleIndex()) {
                firstStop = previousSection.getFirstStop().isPresent() ? previousSection.getFirstStop().get().getStationTag().getTagName().get() : "";
                if ((data.isWaitingAtStation() && data.getCurrentScheduleIndex() == stop.getScheduleIndex()) || !previousSection.isUsable()) {
                    isLastStopOfSection = false; 
                }
                if (!section.isUsable()) {
                    isLastStopOfSection = true;
                }
            }
        }
        return new StationDisplayData(
            BasicTrainDisplayData.of(stop),
            TrainStopDisplayData.of(stop),
            firstStop,
            isLastStopOfSection,
            isLastStopOfSection && !section.nextSection().isUsable(),
            section.getStopoversFrom(stop.getScheduleIndex())
        );
    }

    public BasicTrainDisplayData getTrainData() {
        return trainData;
    }

    public TrainStopDisplayData getStationData() {
        return stationData;
    }

    public List<String> getStopovers() {
        return stopovers;
    }

    public String getFirstStopName() {
        return firstStopName;
    }

    public boolean isLastStop() {
        return isLastStop;
    }

    public boolean isNextSectionExcluded() {
        return isNextSectionExcluded;
    }

    public boolean isDelayed() {
        return isLastStop() ? getStationData().isArrivalDelayed() : getStationData().isDepartureDelayed();
    }

    public long getScheduledTime() {
        return isLastStop() ? getStationData().getScheduledArrivalTime() : getStationData().getScheduledDepartureTime();
    }

    public long getRealTime() {
        return isLastStop() ? getStationData().getRealTimeArrivalTime() : getStationData().getRealTimeDepartureTime();
    }


    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();

        ListTag stopoversList = new ListTag();
        List<String> stations = getStopovers();
        for (String name : stations) {
            stopoversList.add(StringTag.valueOf(name));
        }

        nbt.put(NBT_TRAIN, trainData.toNbt());
        nbt.put(NBT_STATION, stationData.toNbt());
        nbt.putString(NBT_FIRST_STOP, firstStopName);
        nbt.putBoolean(NBT_IS_LAST, isLastStop);
        nbt.putBoolean(NBT_IS_NEXT_EXCLUDED, isNextSectionExcluded);
        nbt.put(NBT_STOPOVERS, stopoversList);
        return nbt;
    }

    public static StationDisplayData fromNbt(CompoundTag nbt) {
        return new StationDisplayData(
            BasicTrainDisplayData.fromNbt(nbt.getCompound(NBT_TRAIN)),
            TrainStopDisplayData.fromNbt(nbt.getCompound(NBT_STATION)),
            nbt.getString(NBT_FIRST_STOP),
            nbt.getBoolean(NBT_IS_LAST),
            nbt.getBoolean(NBT_IS_NEXT_EXCLUDED),
            nbt.getList(NBT_STOPOVERS, Tag.TAG_STRING).stream().map(x -> ((StringTag)x).getAsString()).toList()
        );
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof StationDisplayData o && o.getTrainData().equals(getTrainData()) && o.getStationData().equals(getStationData());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getTrainData(), getStationData());
    }
}
