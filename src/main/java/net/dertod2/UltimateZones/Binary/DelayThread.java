package net.dertod2.UltimateZones.Binary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DelayThread implements Runnable {
	public static List<UUID> pickupList = new ArrayList<UUID>();
	public static List<UUID> plateList = new ArrayList<UUID>();
	
	public void run() {
		try {
			DelayThread.pickupList.clear();
			DelayThread.plateList.clear();
		} catch (Exception exc) {
			
		}
	 }
}