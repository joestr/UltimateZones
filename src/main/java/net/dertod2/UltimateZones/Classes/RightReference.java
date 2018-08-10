package net.dertod2.UltimateZones.Classes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import net.dertod2.DatabaseHandler.Table.Column;
import net.dertod2.DatabaseHandler.Table.Column.DataType;
import net.dertod2.DatabaseHandler.Table.Column.EntryType;
import net.dertod2.DatabaseHandler.Table.TableEntry;
import net.dertod2.DatabaseHandler.Table.TableInfo;
import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;

@TableInfo(tableName = "uz_rights")
public class RightReference extends TableEntry implements IRights {
	@Column(columnName = "player", dataType = DataType.String, order = 1)
	private String player;
	@Column(columnName = "zone", dataType = DataType.Integer, order = 2)
	private int zoneId;
	
	@Column(columnName = "flags", entryType = EntryType.List, dataType = DataType.Integer, order = 4)
	private List<Integer> flagList = new ArrayList<Integer>();
	
	@Column(columnName = "breaks", entryType = EntryType.List, dataType = DataType.String, order = 5)
	private List<String> breakList = new ArrayList<String>();
	@Column(columnName = "places", entryType = EntryType.List, dataType = DataType.String, order = 6)
	private List<String> placeList = new ArrayList<String>();
	
	@Column(columnName = "preset", dataType = DataType.String, order = 3)
	private String presetName;
	
	public RightReference() { }
	
	public RightReference(OfflinePlayer offlinePlayer, AbstractZone abstractZone) {
		this.player = offlinePlayer.getUniqueId().toString();
		this.zoneId = abstractZone.getId();
	}
	
	public static RightReference getRightReference(AbstractZone abstractZone, OfflinePlayer offlinePlayer) {
		return abstractZone.getRights(offlinePlayer);
	}
	
	public static List<RightReference> getPlayerRights(OfflinePlayer offlinePlayer) {
		return UltimateZones.rightControl.getPlayerRights(offlinePlayer);
	}
	
	public static void refreshPresetName(String oldPreset, String newPreset) {
		UltimateZones.rightControl.refreshPresetName(oldPreset, newPreset);
	}
	
	public RightReference getInstance() {
		return new RightReference();
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(this.getPlayerUniqueId());
	}
	
	public UUID getPlayerUniqueId() {
		return UUID.fromString(this.player);
	}
	
	public String getPlayerUniqueIdString() {
		return this.player;
	}
	
	public AbstractZone getZone() {
		return AbstractZone.getZone(this.zoneId);
	}
	
	public int getZoneId() {
		return this.zoneId;
	}
	
	public boolean isPresetSet() {
		return this.presetName.length() > 0;
	}
	
	public Preset getPreset() {
		return Preset.getPreset(this.presetName);
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
	
	public RightResult hasRight(Flag flag, Material material) {
		if (!flag.negatedFlag && !this.flagList.contains(flag.typeId)) return RightResult.Missing_Right.setBoth(flag, material);
		if (flag.negatedFlag && this.flagList.contains(flag.typeId)) return RightResult.Missing_Right.setBoth(flag, material);
		
		// The serialized Type can be used to compare :)
		String comperator = material != null ? material.name() : null;
		
		if (flag.usePlaceList != null) {
			if (flag.usePlaceList) {
				if (this.flagList.contains(Flag.PlaceAsWhiteList.typeId) ? !this.placeList.contains(comperator) : this.placeList.contains(comperator)) return RightResult.Missing_Block.setBoth(flag, material);
			} else {
				if (this.flagList.contains(Flag.BreakAsWhiteList.typeId) ? !this.breakList.contains(comperator) : this.breakList.contains(comperator)) return RightResult.Missing_Block.setBoth(flag, material);
			}
		}
		
		return RightResult.Success;
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
		
		this.presetName = "";
		
		UltimateZones.rightControl.updateRights(this);
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
		
		this.presetName = "";
		
		UltimateZones.rightControl.updateRights(this);
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
		
		this.presetName = "";
		
		UltimateZones.rightControl.updateRights(this);
		return result;
	}
	
	public void apply(IRights iRights) {
		this.flagList = new ArrayList<Integer>(iRights.getPlainFlagList());
		
		this.breakList = new ArrayList<String>(iRights.getPlainBreakList());
		this.placeList = new ArrayList<String>(iRights.getPlainPlaceList());
		
		this.presetName = iRights instanceof Preset ? ((Preset) iRights).getName() : "";
		
		UltimateZones.rightControl.updateRights(this);
	}
	
	public void delete() {
		UltimateZones.rightControl.deleteRights(this);
		this.getZone().rightList.remove(this.getPlayerUniqueId());
	}
}