package de.mrjulsen.crn.block.properties;

import java.util.Arrays;

import de.mrjulsen.crn.client.gui.ModGuiIcons;
import de.mrjulsen.mcdragonlib.core.ITranslatableEnum;
import net.minecraft.util.StringRepresentable;

public enum EDisplayType implements StringRepresentable, ITranslatableEnum {
	TRAIN_DESTINATION((byte)0, "train_destination", ModGuiIcons.TRAIN_DESTINATION, EDisplayTypeDataSource.TRAIN_INFORMATION),
    PASSENGER_INFORMATION((byte)1, "passenger_information", ModGuiIcons.PASSENGER_INFORMATION, EDisplayTypeDataSource.TRAIN_INFORMATION),
	PLATFORM((byte)2, "platform", ModGuiIcons.PLATFORM_INFORMATION, EDisplayTypeDataSource.PLATFORM),
	DEPARTURE_BOARD((byte)3, "departure_board", ModGuiIcons.PLATFORM_INFORMATION, EDisplayTypeDataSource.PLATFORM);
	
	private String name;
	private byte id;
	private ModGuiIcons icon;
	private EDisplayTypeDataSource source;
	
	private EDisplayType(byte id, String name, ModGuiIcons icon, EDisplayTypeDataSource source) {
		this.name = name;
		this.id = id;
		this.icon = icon;
		this.source = source;
	}
	
	public String getInfoTypeName() {
		return this.name;
	}

	public byte getId() {
		return this.id;
	}	

	public ModGuiIcons getIcon() {
		return icon;
	}

	public EDisplayTypeDataSource getSource() {
		return source;
	}

	public static EDisplayType getTypeById(int id) {
		return Arrays.stream(values()).filter(x -> x.getId() == (byte)id).findFirst().orElse(EDisplayType.TRAIN_DESTINATION);
	}

	public static EDisplayType getTypeByName(String name) {
		return Arrays.stream(values()).filter(x -> x.name.equals(name)).findFirst().orElse(EDisplayType.TRAIN_DESTINATION);
	}

    @Override
    public String getSerializedName() {
        return name;
    }

	@Override
	public String getEnumName() {
		return "display_type";
	}

	@Override
	public String getEnumValueName() {
		return this.name;
	}

	public static enum EDisplayTypeDataSource {
		TRAIN_INFORMATION(0),
		PLATFORM(1);

		private int index;

		EDisplayTypeDataSource(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public static EDisplayTypeDataSource getByIndex(int index) {
			return Arrays.stream(EDisplayTypeDataSource.values()).filter(x -> x.getIndex() == index).findFirst().orElse(PLATFORM);
		}
	} 
}
