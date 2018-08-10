package net.dertod2.UltimateZones.CreationWizard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Classes.Zones.UltimateZoneType;
import net.dertod2.ZonesLib.Classes.ZoneType;
import net.dertod2.ZonesLib.DisplayAPI.DisplayControl;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class AbstractCreation {
	private static Map<UUID, AbstractCreation> creationList = new HashMap<UUID, AbstractCreation>();
	
	protected Player zoneCreator;
	protected OfflinePlayer zoneOwner;
	
	protected UltimateZoneType ultimateZoneType;
	protected ZoneType zoneType;
	
	protected boolean force3D;
	protected int zonePriority = UltimateZones.getConfiguration().getInt("zone-priority");
	
	protected int displayId = -1;
	
	protected CreationState creationState = CreationState.NoPositionSet;
	
	protected AbstractCreation(Player zoneCreator, OfflinePlayer zoneOwner, UltimateZoneType ultimateZoneType, ZoneType zoneType, boolean force3D) {
		this.zoneCreator = zoneCreator;
		this.zoneOwner = zoneOwner;
		
		this.ultimateZoneType = ultimateZoneType;
		this.zoneType = zoneType;
		
		this.force3D = force3D;
	}
	
	protected CreationMethod getMethod() {
		return CreationMethod.get(this.getClass());
	}
	
	public static boolean startCreation(AbstractCreation abstractCreation) {
		if (AbstractCreation.creationList.containsKey(abstractCreation.zoneCreator.getUniqueId())) return false;
		
		AbstractCreation.creationList.put(abstractCreation.zoneCreator.getUniqueId(), abstractCreation);
		abstractCreation.info();
		
		return true;
	}
	
	public static AbstractCreation getCreation(Player player) {
		if (!AbstractCreation.creationList.containsKey(player.getUniqueId())) return null;		
		return AbstractCreation.creationList.get(player.getUniqueId());
	}
	
	public int getPriority() {
		return this.zonePriority;
	}
	
	public void setPriority(int zonePriority) {
		this.zonePriority = zonePriority;
	}
	
	public CreationState getCreationState() {
		return this.creationState;
	}
	
	/**
	 * Cancels the creation of an new zone
	 * @param silent Send Message to creator or not
	 */
	public void abort(boolean silent) {
		if (AbstractCreation.creationList.remove(this.zoneCreator.getUniqueId()) != null) {
			if (!silent) Locale.sendPlain(this.zoneCreator, "creation.abstract.aborted");
			DisplayControl.stopViewing(this.zoneCreator, this.displayId);
		}		
	}
	
	/**
	 * Sets the different needed positions to create an Zone
	 * @param event
	 */
	public abstract void setLocation(PlayerInteractEvent event);
	
	/**
	 * Checks if the zone can be created (needs enough positions set)
	 * @return whenever the zone can be created or not
	 */
	public abstract boolean check(boolean silent);
	
	/**
	 * Creates the new Zone, to run this method the check method must be executed first!
	 * @return
	 */
	public abstract boolean create();
	
	/** 
	 * This method will automatically called when an new Creation is started
	 */
	public abstract void info();
}