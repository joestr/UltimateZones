package net.dertod2.UltimateZones.Classes.Zones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableList;

import net.dertod2.DatabaseHandler.Binary.DatabaseHandler;
import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.IRights;
import net.dertod2.UltimateZones.Classes.RightReference;
import net.dertod2.UltimateZones.Classes.RightResult;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Utils.UltimateUtils;
import net.dertod2.ZonesLib.Classes.Zone;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractZone implements IRights {
	private int zoneId;
	private int libZoneId;
	
	private String zoneSynonym;
	
	// Right List
	protected List<Integer> flagList;
	
	protected List<String> breakList;
	protected List<String> placeList;
	
	public Map<UUID, RightReference> rightList = null;
	
	// Fast Access -> Sub Zones
	public Set<Integer> subList = new HashSet<Integer>();
	
	public AbstractZone(int zoneId, int libZoneId, String zoneSynonym, List<Integer> flagList, List<String> breakList, List<String> placeList) {
		this.zoneId = zoneId;
		this.libZoneId = libZoneId;
		
		this.zoneSynonym = zoneSynonym;
		
		this.flagList = flagList;
		
		this.breakList = breakList;
		this.placeList = placeList;
	}
	
	public static AbstractZone getZone(int zoneId) {
		return UltimateZones.zoneControl.getZone(zoneId);
	}
	
	public static AbstractZone getZone(Zone zone) {
		return UltimateZones.zoneControl.getZone(zone);
	}
	
	public static AbstractZone getZone(OfflinePlayer player, String synonym) {
		return UltimateZones.zoneControl.getZone(player, synonym);
	}
	
	public static List<String> getSynonyms(OfflinePlayer offlinePlayer) {
		return UltimateZones.zoneControl.getSynonyms(offlinePlayer);
	}
	
	public static List<MainZone> getZones(OfflinePlayer player) {
		return UltimateZones.zoneControl.getZones(player, null);
	}
	
	public static List<MainZone> getZones(OfflinePlayer player, World world) {
		return UltimateZones.zoneControl.getZones(player, world);
	}
	
	public static List<AbstractZone> getZones(Location location) {
		return UltimateZones.zoneControl.getZones(location);
	}
	
	public static List<AbstractZone> getZones(Zone zone) {
		return UltimateZones.zoneControl.getZones(zone);
	}
	
	public static AbstractZone getZone(Location location) {
		return UltimateZones.zoneControl.getZone(location);
	}
	
	public int getId() {
		return this.zoneId;
	}
	
	// Only for selfish purposes - not database related
	public AbstractZone setId(int zoneId) {
		this.zoneId = zoneId;
		return this;
	}
	
	public Zone getZone() {
		return Zone.getZone(this.libZoneId);
	}
	
	public boolean setZone(Zone zone) {
		if (zone.isTemporary()) return false; // Dont allow temporary zones
		
		UltimateZones.zoneControl.setZone(this, zone);
		
		this.getZone().delete();
		this.libZoneId = zone.getId();
		
		this.update();
		return true;
	}
	
	public int getZoneId() {
		return this.libZoneId;
	}
	
	public String getSynonym() {
		return this.zoneSynonym;
	}
	
	public boolean hasSynonym() {
		return this.zoneSynonym != null && this.zoneSynonym.length() > 0;
	}
	
	public boolean setSynonym(String synonym) {
		if (synonym == null) synonym = "";
		
		List<String> synonymList = AbstractZone.getSynonyms(this.getPlayer());
		if (UltimateUtils.containsIgnoreCase(synonymList, synonym)) return false;
				
		UltimateZones.zoneControl.updateSynonym(this, synonym);
		this.zoneSynonym = synonym;
		
		return this.update();	
	}
	
	public abstract OfflinePlayer getPlayer();
	
	public abstract UUID getPlayerUniqueId();
	
	public abstract boolean isOwner(CommandSender sender);
	
	public abstract boolean isOwner(OfflinePlayer offlinePlayer);
	
	public UltimateZoneType getType() {
		return UltimateZoneType.byClass(this.getClass());
	}
	
	public List<SubZone> getZones(boolean recursive) {
		List<SubZone> zoneList = new ArrayList<SubZone>();
		
		for (Integer zoneId : this.subList) {
			SubZone subZone = (SubZone) AbstractZone.getZone(zoneId);
			
			if (recursive) zoneList.addAll(subZone.getZones(true));			
			zoneList.add(subZone);
		}
		
		return zoneList;
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
	
	public boolean apply(IRights iRights) {
		this.flagList = new ArrayList<Integer>(iRights.getPlainFlagList());
		
		this.breakList = new ArrayList<String>(iRights.getPlainBreakList());
		this.placeList = new ArrayList<String>(iRights.getPlainPlaceList());
		
		return this.update();
	}
	
	public RightResult hasRight(Player player, Flag flag) {
		return this.hasRight(player, flag, null);
	}
	
	public RightResult hasRight(Player player, Flag flag, Material material) {
		if (this.getPlayerUniqueId().compareTo(player.getUniqueId()) == 0) return RightResult.Success;
		if (player.hasPermission("ultimatezones.admin")) return RightResult.Success;
		if (flag != Flag.AdministrateRights && flag != Flag.AdministrateZones && player.hasPermission("ultimatezones.mod")) return RightResult.Success;
		
		if (!flag.negatedFlag && this.flagList.contains(flag.typeId)) {
			if (flag.usePlaceList != null) {
				String comperator = material.name();
				
				if (flag.usePlaceList) {
					if (this.flagList.contains(Flag.PlaceAsWhiteList.typeId) ? this.placeList.contains(comperator) : !this.placeList.contains(comperator)) return RightResult.Success;
				} else {
					if (this.flagList.contains(Flag.BreakAsWhiteList.typeId) ? this.breakList.contains(comperator) : !this.breakList.contains(comperator)) return RightResult.Success;
				}
			} else {
				return RightResult.Success;
			}
		} else if (flag.negatedFlag && this.flagList.contains(flag.typeId)) {
			return RightResult.Missing_Right.setBoth(flag, null);
		}
		
		if (this.rightList == null) this.rightList = UltimateZones.rightControl.getZoneRights(this);
		RightReference rightReference = this.rightList.get(player.getUniqueId());
		
		if (this instanceof MainZone || this instanceof IndependentZone) {		
			if (rightReference == null) {
				if (flag.negatedFlag) return RightResult.Success;
				return RightResult.Missing_Right.setBoth(flag, material);
			}
			
			return rightReference.hasRight(flag, material);
		} else if (this instanceof ExtendedZone) {
			if (rightReference != null && rightReference.hasRight(flag, material).rightEnum == RightEnum.Success) return RightResult.Success;
			
			return ((ExtendedZone) this).getParent().hasRight(player, flag, material);
		}

		UltimateUtils.error("hasRight() method in AbstractZone class found unknown ZoneType " + ChatColor.GOLD + this.toString()); 
		return RightResult.Missing_Right.setBoth(flag, material);
	}
	
	public List<RightReference> getRights() {
		if (this.rightList == null) this.rightList = UltimateZones.rightControl.getZoneRights(this);
        return ImmutableList.<RightReference>copyOf(this.rightList.values());
	}
	
	public RightReference getRights(OfflinePlayer offlinePlayer) {
		return this.getRights(offlinePlayer, false);
	}
	
	public RightReference getRights(OfflinePlayer offlinePlayer, boolean create) {
		if (this.rightList == null) this.rightList = UltimateZones.rightControl.getZoneRights(this);
		
		RightReference rightReference = this.rightList.get(offlinePlayer.getUniqueId());
		if (rightReference == null && create) {
			rightReference = new RightReference(offlinePlayer, this);
			UltimateZones.rightControl.addRights(rightReference);
			this.rightList.put(offlinePlayer.getUniqueId(), rightReference);
		}
		
		return rightReference;
	}
	
	/**
	 * Clears the rights inside this zone. 
	 * @param zoneRights defines if the method should clear the rights of the zone or of all players
	 */
	public void clearRights(boolean zoneRights) {
		if (zoneRights) {
			this.flagList.clear();
			this.placeList.clear();
			this.breakList.clear();
			this.update();
		} else {
			UltimateZones.rightControl.clearRights(this);
			if (this.rightList != null) this.rightList.clear();
		}
	}
	
	public boolean update()  {
		try {
			DatabaseHandler.get().getHandler().update(ZoneSchema.toSchema(this));
			return true;
		} catch (Exception exc) {
			exc.printStackTrace();
			return false;
		}
	}
	
	public boolean delete() {
		List<SubZone> zoneList = this.getZones(false);
				
		for (AbstractZone abstractZone : zoneList) abstractZone.delete();
		
		if (this instanceof SubZone) ((SubZone) this).getParent().subList.remove(this.zoneId);
		UltimateZones.rightControl.clearRights(this);
		
		try { 
			DatabaseHandler.get().getHandler().remove(ZoneSchema.toSchema(this));
			UltimateZones.zoneControl.delZone(this);
			return true;
		} catch (Exception exc) {
			exc.printStackTrace();
			return false;
		}
	}
}