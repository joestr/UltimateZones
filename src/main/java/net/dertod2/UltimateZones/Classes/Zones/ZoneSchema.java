package net.dertod2.UltimateZones.Classes.Zones;

import java.util.List;
import java.util.UUID;

import net.dertod2.DatabaseHandler.Table.Column;
import net.dertod2.DatabaseHandler.Table.Column.ColumnType;
import net.dertod2.DatabaseHandler.Table.Column.DataType;
import net.dertod2.DatabaseHandler.Table.Column.EntryType;
import net.dertod2.DatabaseHandler.Table.TableEntry;
import net.dertod2.DatabaseHandler.Table.TableInfo;

@TableInfo(tableName = "uz_zones")
public class ZoneSchema extends TableEntry {
	@Column(columnName = "id", columnType = ColumnType.Primary, dataType = DataType.Integer, order = 1)
	public int id;
	
	@Column(columnName = "zone_id", dataType = DataType.Integer, order = 2)
	public int libZoneId;
	
	@Column(columnName = "synonym", dataType = DataType.String, order = 5)
	public String zoneSynonym;
	
	@Column(columnName = "flags", entryType = EntryType.List, dataType = DataType.Integer, order = 8)
	public List<Integer> flagList;
	
	@Column(columnName = "breaks", entryType = EntryType.List, dataType = DataType.String, order = 9)
	public List<String> placeList;
	@Column(columnName = "places", entryType = EntryType.List, dataType = DataType.String, order = 10)
	public List<String> breakList;
	
	@Column(columnName = "priority", dataType = DataType.Integer, order = 4)
	public int priority;
	@Column(columnName = "parent_id", dataType = DataType.Integer, order = 6)
	public int parentId;
	@Column(columnName = "owner", dataType = DataType.String, order = 7)
	public String owner;
	
	@Column(columnName = "type_id", dataType = DataType.Integer, order = 3)
	public int zoneType;
	
	public ZoneSchema() { }
	
	public ZoneSchema getInstance() {
		return new ZoneSchema();
	}
	
	public AbstractZone toZone() {
		UltimateZoneType ultimateZoneType = UltimateZoneType.byId(this.zoneType);
		
		switch (ultimateZoneType) {
			case EXTENDED:
				return new ExtendedZone(this.id, this.libZoneId, this.zoneSynonym, this.flagList, this.breakList, this.placeList, this.parentId);
			case INDEPENDENT:
				return new IndependentZone(this.id, this.libZoneId, this.zoneSynonym, this.flagList, this.breakList, this.placeList, this.parentId);
			case MAIN:
				return new MainZone(this.id, this.libZoneId, this.zoneSynonym, this.flagList, this.breakList, this.placeList, UUID.fromString(this.owner), this.priority);
			default:
				return null;
		}
	}
	
	public static ZoneSchema toSchema(AbstractZone abstractZone) {
		ZoneSchema zoneSchema = new ZoneSchema();
		
		zoneSchema.id = abstractZone.getId();
		zoneSchema.libZoneId = abstractZone.getZoneId();
		
		zoneSchema.zoneSynonym = abstractZone.getSynonym();
		
		zoneSchema.flagList = abstractZone.flagList;
		zoneSchema.breakList = abstractZone.breakList;
		zoneSchema.placeList = abstractZone.placeList;
		
		zoneSchema.zoneType = abstractZone.getType().getId();
		
		zoneSchema.owner = abstractZone instanceof MainZone ? abstractZone.getPlayerUniqueId().toString() : "";
		zoneSchema.priority = abstractZone instanceof MainZone ? ((MainZone) abstractZone).getPriority() : 0;
		zoneSchema.parentId = abstractZone instanceof SubZone ? ((SubZone) abstractZone).getParentId() : 0;
		
		zoneSchema.isLoadedEntry = true;
		return zoneSchema;
	}
}