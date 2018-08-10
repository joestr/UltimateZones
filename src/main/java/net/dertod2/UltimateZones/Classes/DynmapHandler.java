package net.dertod2.UltimateZones.Classes;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.CircleMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerDescription;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PolyLineMarker;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Classes.Zones.MainZone;
import net.dertod2.UltimateZones.Classes.Zones.SubZone;
import net.dertod2.UltimateZones.Utils.UltimateUtils;
import net.dertod2.ZonesLib.Classes.CuboidZone;
import net.dertod2.ZonesLib.Classes.CylinderZone;
import net.dertod2.ZonesLib.Classes.HeightZone;
import net.dertod2.ZonesLib.Classes.OriginZone;
import net.dertod2.ZonesLib.Classes.PolygonZone;
import net.dertod2.ZonesLib.Classes.RoundedZone;
import net.dertod2.ZonesLib.Classes.SphereZone;
import net.dertod2.ZonesLib.Classes.Zone;

public class DynmapHandler {
	private static boolean initialized = false;
	
	private static Locale locale;
	
	private static DateFormat dateFormat;
	private static DecimalFormat decimalFormat = new DecimalFormat();
	
	private static MarkerAPI markerAPI;
	
	private static MarkerSet mainSet;
	private static MarkerSet childSet;
	private static MarkerSet serverSet;
	
	private static String cuboidDescription;
	private static String cylinderDescription;
	private static String sphereDescription;
	private static String polygonDescription;
	
	private static boolean showMainSet;
	private static boolean showChildSet;
	private static boolean showServerSet;
	
	private static Map<Integer, MarkerDescription> markerList; // Do not init static cause the class MarkerDescription can be not existing
	
	public DynmapHandler() {
		if (!UltimateZones.isDynmapInstalled) return;
		
		DynmapHandler.locale = Locale.get(UltimateZones.getConfiguration().getString("dm-locale"));
		
		DynmapHandler.dateFormat =  new SimpleDateFormat(UltimateZones.getConfiguration().getString("date-layouts." + DynmapHandler.locale.getTag()));
		
		DynmapHandler.showMainSet = UltimateZones.getConfiguration().getBoolean("dm-activate-main-set");
		DynmapHandler.showChildSet = UltimateZones.getConfiguration().getBoolean("dm-activate-child-set");
		DynmapHandler.showServerSet = UltimateZones.getConfiguration().getBoolean("dm-activate-server-set");
		
		DynmapHandler.markerAPI = ((DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap")).getMarkerAPI();

		DynmapHandler.mainSet = DynmapHandler.markerAPI.getMarkerSet("ultimatezones.main");
		if (DynmapHandler.mainSet != null) DynmapHandler.mainSet.deleteMarkerSet(); // To always have an valid layout, like database changes
		
		if (DynmapHandler.showMainSet) {
			DynmapHandler.mainSet = DynmapHandler.markerAPI.createMarkerSet("ultimatezones.main", DynmapHandler.locale.parse("dynmap", "markersets.mainzones"), null, false);
			DynmapHandler.mainSet.setLayerPriority(1);
			DynmapHandler.mainSet.setLabelShow(false);
			DynmapHandler.mainSet.setHideByDefault(false);
		}
		
		DynmapHandler.childSet = DynmapHandler.markerAPI.getMarkerSet("ultimatezones.child");
		if (DynmapHandler.childSet != null) DynmapHandler.childSet.deleteMarkerSet(); // To always have an valid layout, like database changes
		
		if (DynmapHandler.showChildSet) {
			DynmapHandler.childSet = DynmapHandler.markerAPI.createMarkerSet("ultimatezones.child", DynmapHandler.locale.parse("dynmap", "markersets.childzones"), null, false);
			DynmapHandler.childSet.setLayerPriority(2);
			DynmapHandler.childSet.setLabelShow(true);
			DynmapHandler.childSet.setHideByDefault(true);
		}
		
		DynmapHandler.serverSet = DynmapHandler.markerAPI.getMarkerSet("ultimatezones.server");
		if (DynmapHandler.serverSet != null) DynmapHandler.serverSet.deleteMarkerSet(); // To always have an valid layout, like database changes
		
		if (DynmapHandler.showServerSet) {
			DynmapHandler.serverSet = DynmapHandler.markerAPI.createMarkerSet("ultimatezones.server", DynmapHandler.locale.parse("dynmap", "markersets.serverzones"), null, false);
			DynmapHandler.serverSet.setLayerPriority(3);
			DynmapHandler.serverSet.setLabelShow(true);
			DynmapHandler.serverSet.setHideByDefault(true);
		}
		
		DynmapHandler.markerList = new HashMap<Integer, MarkerDescription>();
		
		
		DynmapHandler.cuboidDescription = DynmapHandler.locale.parse("dynmap", "descriptions.html.cuboid");
		DynmapHandler.cylinderDescription = DynmapHandler.locale.parse("dynmap", "descriptions.html.cylinder");
		DynmapHandler.sphereDescription = DynmapHandler.locale.parse("dynmap", "descriptions.html.sphere");
		DynmapHandler.polygonDescription = DynmapHandler.locale.parse("dynmap", "descriptions.html.polygon");
		
		DynmapHandler.initialized = true;
	}
	
	public static void addZone(AbstractZone abstractZone) {
		if (!initialized) return;
		
		DynmapHandler.deleteZone(abstractZone); // To be on the save side
		Zone zone = abstractZone.getZone();
		
		if (!DynmapHandler.showMainSet && abstractZone instanceof MainZone) return;
		if (!DynmapHandler.showChildSet && abstractZone instanceof SubZone) return;
		if (UltimateZones.getConfiguration().getStringList("dm-ignored-worlds").contains(zone.getWorldName())) return;
		
		MarkerSet markerSet = abstractZone instanceof MainZone ? DynmapHandler.mainSet : DynmapHandler.childSet;
		
		if (UltimateZones.getConfiguration().getBoolean("dm-activate-server-set")) {
			if (UltimateZones.getConfiguration().getStringList("dm-server-set-players").contains(abstractZone.getPlayer().getName())) {
				markerSet = DynmapHandler.serverSet;
			}
		}
		
		String infoWindow = "<div class=\"regioninfo\">" + 
				(zone instanceof CuboidZone ? DynmapHandler.cuboidDescription : zone instanceof CylinderZone ? DynmapHandler.cylinderDescription : zone instanceof SphereZone ? DynmapHandler.sphereDescription : DynmapHandler.polygonDescription)
				+ "</div>";
		
		infoWindow = infoWindow.replace("%id%", String.valueOf(abstractZone.getId()));
		infoWindow = infoWindow.replace("%synonym%", abstractZone.hasSynonym() ? abstractZone.getSynonym() : "-");
		infoWindow = infoWindow.replace("%owner%", abstractZone.getPlayer().getName());		
		if (zone.getCreated() != null && DynmapHandler.dateFormat != null) infoWindow = infoWindow.replace("%created%", DynmapHandler.dateFormat.format(zone.getCreated()));
		infoWindow = infoWindow.replace("%area%", UltimateUtils.formattedArea(zone.getArea(), DynmapHandler.locale.getJavaLocale()));
		infoWindow = infoWindow.replace("%volume%", UltimateUtils.formattedVolume(zone.getVolume(), DynmapHandler.locale.getJavaLocale()));
		
		MarkerDescription markerDescription = null;
		
		if (zone instanceof HeightZone) {
			infoWindow = infoWindow.replace("%height%", ((HeightZone) zone).isFullHeight() ? DynmapHandler.locale.parse("dynmap", "descriptions.fullheightzone") : decimalFormat.format(((HeightZone) zone).getHeight()));
		}
		
		if (zone instanceof RoundedZone) {			
			double radius = ((RoundedZone) zone).getRadius();
			infoWindow = infoWindow.replace("%radius%", decimalFormat.format(radius));
			
			CircleMarker circleMarker = markerSet.createCircleMarker(
					String.valueOf(abstractZone.getId()),
					"Zone: " + abstractZone.getId(), 
					false, zone.getWorld().getName(),
					((OriginZone) zone).getX() + .5, ((OriginZone) zone).getY(), ((OriginZone) zone).getZ() + .5,
					radius, radius, true);
			
			if (abstractZone instanceof MainZone) {
				circleMarker.setLineStyle(UltimateZones.getConfiguration().getInt("dm-main-weight"), UltimateZones.getConfiguration().getDouble("dm-main-lineopacity"), UltimateZones.getConfiguration().getInt("dm-main-linecolor"));
				circleMarker.setFillStyle(UltimateZones.getConfiguration().getDouble("dm-main-fillopacity"), UltimateZones.getConfiguration().getInt("dm-main-fillcolor"));
			} else {
				circleMarker.setLineStyle(UltimateZones.getConfiguration().getInt("dm-child-weight"), UltimateZones.getConfiguration().getDouble("dm-child-lineopacity"), UltimateZones.getConfiguration().getInt("dm-child-linecolor"));
				circleMarker.setFillStyle(UltimateZones.getConfiguration().getDouble("dm-child-fillopacity"), UltimateZones.getConfiguration().getInt("dm-child-fillcolor"));
			}
			
			markerDescription = circleMarker;
		} else if (zone instanceof PolygonZone) {
			PolygonZone polygonZone = (PolygonZone) zone;
			
			infoWindow = infoWindow.replace("%points%", decimalFormat.format(polygonZone.getPolygonCount()));
			
			double[] polysX = new double[polygonZone.getPolygonCount() + 1];
			double[] polysY = new double[polygonZone.getPolygonCount() + 1];		
			double[] polysZ = new double[polygonZone.getPolygonCount() + 1];

			for (int i = 0; i < polygonZone.getPolygonCount() + 1; i++) { 
				if (i < polygonZone.getPolygonCount()) {
					polysX[i] = polygonZone.getPolysX().get(i) + 0.5D;
					polysY[i] = 64; 
					polysZ[i] = polygonZone.getPolysZ().get(i) + 0.5D;
				} else {
					polysX[i] = polygonZone.getPolysX().get(0) + 0.5D;
					polysY[i] = 64; 
					polysZ[i] = polygonZone.getPolysZ().get(0) + 0.5D;	
				}
			}
			
			PolyLineMarker polygonMarker = markerSet.createPolyLineMarker(
					String.valueOf(abstractZone.getId()), 
					"Zone: " + abstractZone.getId(), 
					false, zone.getWorld().getName(), 
					polysX,
					polysY,
					polysZ,
					true);
			
			polygonMarker.setLineStyle(UltimateZones.getConfiguration().getInt("dm-main-weight") + 1, UltimateZones.getConfiguration().getDouble("dm-main-lineopacity"), UltimateZones.getConfiguration().getInt("dm-main-linecolor"));
			
			markerDescription = polygonMarker;
		} else {
			CuboidZone cuboidZone = (CuboidZone) zone;
			
			infoWindow = infoWindow.replace("%length%", decimalFormat.format(cuboidZone.getLength()));
			infoWindow = infoWindow.replace("%width%", decimalFormat.format(cuboidZone.getWidth()));
			
			//get position centered on blocks
			Location min = cuboidZone.getMinimum().add(0.5, 0, 0.5);
			Location max = cuboidZone.getMaximum().add(0.5, 0, 0.5);
			
			//resize to include full blocks
			min = min.subtract(0.5, 0, 0.5);
			max = max.add(0.5, 0, 0.5);
			
			infoWindow = infoWindow.replace("%minX%", decimalFormat.format(min.getX()));
			infoWindow = infoWindow.replace("%minZ%", decimalFormat.format(min.getZ()));
			infoWindow = infoWindow.replace("%maxX%", decimalFormat.format(max.getX()));
			infoWindow = infoWindow.replace("%maxZ%", decimalFormat.format(max.getZ()));
			
			AreaMarker areaMarker = markerSet.createAreaMarker(
					String.valueOf(abstractZone.getId()),
					"Zone: " + abstractZone.getId(),
					false,
					zone.getWorld().getName(),
					new double[] {min.getX(), max.getX()}, 
					new double[] {min.getZ(), max.getZ()}, 
					true);
			
			if (abstractZone instanceof MainZone) {
				areaMarker.setLineStyle(UltimateZones.getConfiguration().getInt("dm-main-weight"), UltimateZones.getConfiguration().getDouble("dm-main-lineopacity"), UltimateZones.getConfiguration().getInt("dm-main-linecolor"));
				areaMarker.setFillStyle(UltimateZones.getConfiguration().getDouble("dm-main-fillopacity"), UltimateZones.getConfiguration().getInt("dm-main-fillcolor"));
			} else {
				areaMarker.setLineStyle(UltimateZones.getConfiguration().getInt("dm-child-weight"), UltimateZones.getConfiguration().getDouble("dm-child-lineopacity"), UltimateZones.getConfiguration().getInt("dm-child-linecolor"));
				areaMarker.setFillStyle(UltimateZones.getConfiguration().getDouble("dm-child-fillopacity"), UltimateZones.getConfiguration().getInt("dm-child-fillcolor"));
			}
			
//			if (zone instanceof HeightZone && !((HeightZone) zone).isFullHeight()) { 
//				int halfHeight = (int) (cuboidZone.getHeight() / 2);
//				
//				double y1 = cuboidZone.getY() + halfHeight;
//				double y2 = cuboidZone.getY() - halfHeight;
//				
//				areaMarker.setRangeY(y1, y2);
//			}
			
			markerDescription = areaMarker;
		}
		
		if (markerDescription != null) {
			markerDescription.setDescription(infoWindow);
			DynmapHandler.markerList.put(abstractZone.getId(), markerDescription);
		}
	}
	
	public static void deleteZone(AbstractZone abstractZone) {
		if (!initialized) return;
				
		MarkerDescription markerDescription = DynmapHandler.markerList.remove(abstractZone.getId());
		if (markerDescription != null) markerDescription.deleteMarker();	
	}
}