package net.dertod2.UltimateZones.Classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import net.dertod2.DatabaseHandler.Binary.DatabaseHandler;
import net.dertod2.DatabaseHandler.Table.Column;
import net.dertod2.DatabaseHandler.Table.Column.ColumnType;
import net.dertod2.DatabaseHandler.Table.Column.DataType;
import net.dertod2.DatabaseHandler.Table.Column.EntryType;
import net.dertod2.DatabaseHandler.Table.TableEntry;
import net.dertod2.DatabaseHandler.Table.TableInfo;
import net.dertod2.UltimateZones.Binary.UltimateZones;

// TODO -> Update ALL Player Rights (with this preset as the base) when changing an preset

@TableInfo(tableName = "uz_presets")
public class Preset extends TableEntry implements IRights {
	@Column(columnName = "id", columnType = ColumnType.Primary, dataType = DataType.Integer, order = 1)
	private int id;
	
	@Column(columnName = "name", dataType = DataType.String, order = 2)
	private String presetName;
	
	@Column(columnName = "description", dataType = DataType.String, order = 3)
	private String presetDescription;
	
	@Column(columnName = "flags", entryType = EntryType.List, dataType = DataType.Integer, order = 4)
	private List<Integer> flagList;
	
	@Column(columnName = "places", entryType = EntryType.List, dataType = DataType.String, order = 5)
	private List<String> placeList;
	@Column(columnName = "breaks", entryType = EntryType.List, dataType = DataType.String, order = 6)
	private List<String> breakList;
	
	public Preset() { }
	
	public Preset(String presetName, List<Integer> flagList, List<String> breakList, List<String> placeList) {
		this.presetName = presetName;
		
		this.flagList = new ArrayList<Integer>(flagList);
		
		this.breakList = new ArrayList<String>(breakList);
		this.placeList = new ArrayList<String>(placeList);
	}
	
	public static Preset getPreset(String presetName) {
		return UltimateZones.presetControl.getPreset(presetName);
	}
	
	public static List<Preset> getPresets() {
		return UltimateZones.presetControl.getPresets();
	}
	
	public static Preset createPreset(String presetName, List<Integer> flagList, List<String> breakList, List<String> placeList) {
		return UltimateZones.presetControl.createPreset(presetName, flagList, breakList, placeList);
	}
	
	public static Preset createPreset(String presetName, RightReference rightReference) {
		return UltimateZones.presetControl.createPreset(presetName, rightReference.getPlainFlagList(), rightReference.getPlainBreakList(), rightReference.getPlainPlaceList());
	}
	
	public static Preset createPreset(String presetName) {
		return UltimateZones.presetControl.createPreset(presetName, new ArrayList<Integer>(), new ArrayList<String>(), new ArrayList<String>());
	}

	public Preset getInstance() {
		return new Preset();
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.presetName;
	}
	
	public void setName(String presetName) {
		if (Preset.getPreset(presetName) != null) return;
		
		String oldName = this.presetName;
		this.presetName = presetName;
		this.update();
		
		RightReference.refreshPresetName(oldName, presetName);
		UltimateZones.presetControl.load(); // Reload Presets
	}
	
	public String getDescription() {
		return this.presetDescription == null ? "" : this.presetDescription;
	}
	
	public void setDescription(String description) {
		this.presetDescription = description;
		this.update();
	}

	public List<Flag> getFlagList() {
		List<Flag> returnList = new ArrayList<Flag>();
		for(int flagId : this.flagList){
			returnList.add(Flag.fromInt(flagId));
		}
		
		return returnList;
	}
	
	public List<Material> getPlaceList() {
		List<Material> returnList = new ArrayList<Material>();
		for(String material : this.placeList){
			returnList.add(Material.getMaterial(material));
		}
		
		return returnList;
	}
	
	public List<Material> getBreakList() {
		List<Material> returnList = new ArrayList<Material>();
		for(String material : this.breakList){
			returnList.add(Material.getMaterial(material));
		}
		
		return returnList;
	}
	
	public List<Integer> getPlainFlagList() {
		return this.flagList;
	}
	
	public List<String> getPlainBreakList() {
		return this.breakList;
	}
	
	public List<String> getPlainPlaceList() {
		return this.placeList;
	}
	
	public boolean setFlag(Flag flag) {
		boolean result;

		if (this.flagList.contains(flag.typeId)) {
			this.flagList.remove((Integer) flag.typeId);
			result = false;
		} else {
			this.flagList.add(flag.typeId);
			result = true;
		}
		
		this.update();
		return result;
	}
	
	public boolean setPlace(Material material) {
		boolean result;
		
		String serialized = material.name();
		
		if (this.placeList.contains(serialized)) {
			this.placeList.remove(serialized);
			result = false;
		} else {
			this.placeList.add(serialized);
			result = true;
		}
		
		this.update();
		return result;
	}
	
	public boolean setBreak(Material material) {
		boolean result;
		
		String serialized = material.name();
		
		if (this.breakList.contains(serialized)) {
			this.breakList.remove(serialized);
			result = false;
		} else {
			this.breakList.add(serialized);
			result = true;
		}
		
		this.update();
		return result;
	}
	
	private boolean update() {
		try {
			return DatabaseHandler.get().getHandler().update(this);
		} catch (Exception exc) {
			return false;
		}
	}

	public void delete() {
		UltimateZones.presetControl.delPreset(this);
	}
}