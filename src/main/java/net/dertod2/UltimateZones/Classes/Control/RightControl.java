package net.dertod2.UltimateZones.Classes.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.google.common.collect.ImmutableMap;

import net.dertod2.DatabaseHandler.Binary.DatabaseHandler;
import net.dertod2.DatabaseHandler.Table.TableEntry;
import net.dertod2.UltimateZones.Classes.RightReference;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;

public class RightControl {

	public RightControl() {
		try {
			DatabaseHandler.get().getHandler().updateLayout(new RightReference());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public Map<UUID, RightReference> getZoneRights(AbstractZone abstractZone) {
		List<TableEntry> dataList = new ArrayList<TableEntry>();
		Map<UUID, RightReference> rightsList = new HashMap<UUID, RightReference>();
		
		try {
			DatabaseHandler.get().getHandler().load(new RightReference(), dataList, ImmutableMap.<String, Object>builder().put("zone", abstractZone.getId()).build());
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {			
			for (TableEntry tableEntry : dataList) {
				RightReference rightReference = (RightReference) tableEntry;
				rightsList.put(rightReference.getPlayerUniqueId(), rightReference);
			}
		}
				
		return rightsList;
	}
	
	public List<RightReference> getPlayerRights(OfflinePlayer offlinePlayer) {
		List<TableEntry> dataList = new ArrayList<TableEntry>();
		List<RightReference> rightsList = new ArrayList<RightReference>();
		
		try {
			DatabaseHandler.get().getHandler().load(new RightReference(), dataList, ImmutableMap.<String, Object>builder().put("player", offlinePlayer.getUniqueId().toString()).build());
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			for (TableEntry tableEntry : dataList) {
				RightReference rightReference = (RightReference) tableEntry;
				rightsList.add(rightReference);
			}
		}
		
		return rightsList;
	}
	
	public boolean refreshPresetName(String oldPreset, String newPreset) {
		try {
			return DatabaseHandler.get().getHandler().update(new RightReference(), 
					ImmutableMap.<String, Object>builder().put("preset", oldPreset).build(), 
					ImmutableMap.<String, Object>builder().put("preset", newPreset).build());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		return false;
	}
	
	public void clearRights(AbstractZone abstractZone) {
		try {
			DatabaseHandler.get().getHandler().remove(new RightReference(), ImmutableMap.<String, Object>builder()
				.put("zone", abstractZone.getId())
				.build());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void updateRights(RightReference rightReference) {
		try {
			DatabaseHandler.get().getHandler().update(rightReference, ImmutableMap.<String, Object>builder()
				.put("player", rightReference.getPlayerUniqueIdString())
				.put("zone", rightReference.getZoneId())
				.build());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void deleteRights(RightReference rightReference) {
		try {
			DatabaseHandler.get().getHandler().remove(rightReference, ImmutableMap.<String, Object>builder()
				.put("player", rightReference.getPlayerUniqueIdString())
				.put("zone", rightReference.getZoneId())
				.build());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public RightReference addRights(RightReference rightReference) {
		try {
			DatabaseHandler.get().getHandler().insert(rightReference);
			return rightReference;
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
}