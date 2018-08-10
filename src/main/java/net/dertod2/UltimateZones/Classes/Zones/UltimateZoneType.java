package net.dertod2.UltimateZones.Classes.Zones;

public enum UltimateZoneType {
	MAIN(0, "Main", MainZone.class),
	INDEPENDENT(1, "Independent", IndependentZone.class),
	EXTENDED(2, "Extended", ExtendedZone.class);
	
	private int typeId;
	private String typeName;
	private Class<? extends AbstractZone> typeClass;
	
	private static UltimateZoneType[] values = UltimateZoneType.values();
	
	private UltimateZoneType(int typeId, String typeName, Class<? extends AbstractZone> typeClass) {
		this.typeId = typeId;
		this.typeName = typeName;
		this.typeClass = typeClass;
	}
	
	public int getId() {
		return this.typeId;
	}
	
	public String getName() {
		return this.typeName;
	}
	
	public Class<? extends AbstractZone> getTypeClass() {
		return this.typeClass;
	}
	
	public static UltimateZoneType byId(int typeId) {
		for (UltimateZoneType zoneType : values) {
			if (zoneType.typeId == typeId) {
				return zoneType;
			}
		}
		
		return null;
	}
	
	public static UltimateZoneType byName(String typeName) {
		for (UltimateZoneType zoneType : values) {
			if (zoneType.typeName.equalsIgnoreCase(typeName)) {
				return zoneType;
			}
		}
		
		return null;
	}
	
	public static UltimateZoneType byClass(Class<? extends AbstractZone> typeClass) {
		for (UltimateZoneType zoneType : values) {
			if (zoneType.typeClass.isAssignableFrom(typeClass)) {
				return zoneType;
			}
		}
		
		return null;
	}
}