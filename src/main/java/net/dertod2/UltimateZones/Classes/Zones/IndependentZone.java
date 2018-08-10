package net.dertod2.UltimateZones.Classes.Zones;

import java.util.List;


public class IndependentZone extends SubZone {

	public IndependentZone(int zoneId, int libZoneId, String synonym, List<Integer> flagList, List<String> breakList, List<String> placeList, int parentId) {
		super(zoneId, libZoneId, synonym, flagList, breakList, placeList, parentId);
	}

}