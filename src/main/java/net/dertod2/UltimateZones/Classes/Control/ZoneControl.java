package net.dertod2.UltimateZones.Classes.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import com.google.common.collect.ImmutableList;

import net.dertod2.DatabaseHandler.Binary.DatabaseHandler;
import net.dertod2.DatabaseHandler.Table.TableEntry;
import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.DynmapHandler;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Classes.Zones.ExtendedZone;
import net.dertod2.UltimateZones.Classes.Zones.IndependentZone;
import net.dertod2.UltimateZones.Classes.Zones.MainZone;
import net.dertod2.UltimateZones.Classes.Zones.SubZone;
import net.dertod2.UltimateZones.Classes.Zones.UltimateZoneType;
import net.dertod2.UltimateZones.Classes.Zones.ZoneSchema;
import net.dertod2.UltimateZones.Utils.UltimateUtils;
import net.dertod2.ZonesLib.Classes.Zone;

public class ZoneControl {

    private Map<Integer, AbstractZone> zoneList; // Contains all zones

    private Map<Integer, Integer> fastZoneList; // fast access of the ZonesLib ID
    private Map<UUID, List<Integer>> fastPlayerList; // Contains only the main zones for fast access per player
    private Map<UUID, Map<String, Integer>> fastSynonymList; // Contains fast access over unique User UUID and Synonym
                                                             // for Zones

    public ZoneControl() {
        try {
            DatabaseHandler.get().getHandler().updateLayout(new ZoneSchema());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void load() {
        if (this.zoneList == null)
            this.zoneList = new HashMap<Integer, AbstractZone>();
        if (this.fastZoneList == null)
            this.fastZoneList = new HashMap<Integer, Integer>();
        if (this.fastPlayerList == null)
            this.fastPlayerList = new HashMap<UUID, List<Integer>>();
        if (this.fastSynonymList == null)
            this.fastSynonymList = new HashMap<UUID, Map<String, Integer>>();

        this.zoneList.clear();
        this.fastZoneList.clear();
        this.fastPlayerList.clear();
        this.fastSynonymList.clear();

        List<TableEntry> dataList = new ArrayList<TableEntry>();

        List<String> skippedMissingConfig = new ArrayList<String>();
        List<String> skippedDeactivated = new ArrayList<String>();

        try {
            DatabaseHandler.get().getHandler().load(new ZoneSchema(), dataList);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            for (TableEntry tableEntry : dataList) {
                AbstractZone abstractZone = ((ZoneSchema) tableEntry).toZone();

                if (abstractZone.getZone() == null) {
                    UltimateUtils.error("Zone " + ChatColor.GOLD + abstractZone.getId() + ChatColor.RED
                            + " is no longer valid. Skipped Zone!");
                    continue;
                }

                if (abstractZone.getZone().getWorld() == null
                        || UltimateZones.getConfiguration(abstractZone.getZone().getWorldName()) == null) {
                    if (!skippedMissingConfig.contains(abstractZone.getZone().getWorldName()))
                        skippedMissingConfig.add(abstractZone.getZone().getWorldName());
                    continue;
                }

                if (!UltimateZones.getConfiguration(abstractZone.getZone().getWorldName()).getBoolean("activate-zones",
                        true)) {
                    if (!skippedDeactivated.contains(abstractZone.getZone().getWorldName()))
                        skippedDeactivated.add(abstractZone.getZone().getWorldName());
                    continue;
                }

                this.zoneList.put(abstractZone.getId(), abstractZone);

                this.fastZoneList.put(abstractZone.getZoneId(), abstractZone.getId());
                UUID uniqueId = abstractZone.getPlayerUniqueId();

                if (abstractZone.hasSynonym()) {
                    if (!this.fastSynonymList.containsKey(uniqueId))
                        this.fastSynonymList.put(uniqueId, new HashMap<String, Integer>());
                    this.fastSynonymList.get(uniqueId).put(abstractZone.getSynonym(), abstractZone.getId());
                }

                if (abstractZone instanceof MainZone) {
                    if (!this.fastPlayerList.containsKey(uniqueId))
                        this.fastPlayerList.put(uniqueId, new ArrayList<Integer>());
                    this.fastPlayerList.get(uniqueId).add(abstractZone.getId());
                } else {
                    ((SubZone) abstractZone).getParent().subList.add(abstractZone.getId());
                }

                if (UltimateZones.isDynmapInstalled)
                    DynmapHandler.addZone(abstractZone);
            }

            if (skippedMissingConfig.size() > 0) {
                for (String worldName : skippedMissingConfig) {
                    UltimateUtils.error("Skipped zones of world " + ChatColor.GOLD + worldName + ChatColor.RED
                            + " cause of missing world or world configuration.");
                }
            }

            if (skippedDeactivated.size() > 0) {
                for (String worldName : skippedMissingConfig) {
                    UltimateUtils.notify("Ignored zones of world " + ChatColor.GOLD + worldName + ChatColor.RED
                            + " cause of 'activate-zones' set to false.");
                }
            }

            UltimateUtils.notify("Loaded and initialized " + ChatColor.GOLD + this.zoneList.size()
                    + ChatColor.DARK_GREEN + " zones!");
        }
    }

    public void move(AbstractZone abstractZone, UUID oldPlayer, UUID newPlayer) {
        this.fastPlayerList.get(oldPlayer).remove((Integer) abstractZone.getId());

        if (!this.fastPlayerList.containsKey(newPlayer))
            this.fastPlayerList.put(newPlayer, new ArrayList<Integer>());
        this.fastPlayerList.get(newPlayer).add(abstractZone.getId());

        if (abstractZone.hasSynonym()) {
            this.fastSynonymList.remove(oldPlayer);

            if (!this.fastSynonymList.containsKey(newPlayer))
                this.fastSynonymList.put(newPlayer, new HashMap<String, Integer>());
            this.fastSynonymList.get(newPlayer).put(abstractZone.getSynonym(), abstractZone.getId());

            this.fastSynonymList.put(oldPlayer, new HashMap<String, Integer>());
            List<Integer> zoneList = this.fastPlayerList.get(oldPlayer);

            for (Integer zoneId : zoneList) {
                AbstractZone oldZone = this.zoneList.get(zoneId);
                if (oldZone.hasSynonym())
                    this.fastSynonymList.get(oldPlayer).put(oldZone.getSynonym(), zoneId);
            }
        }

        if (UltimateZones.isDynmapInstalled)
            DynmapHandler.addZone(abstractZone); // To Update the content
    }

    public void setZone(AbstractZone abstractZone, Zone zone) {
        this.fastZoneList.remove(abstractZone.getZoneId());
        this.fastZoneList.put(zone.getId(), abstractZone.getId());

        if (UltimateZones.isDynmapInstalled)
            DynmapHandler.addZone(abstractZone); // To Update the content
    }

    // *************

    public AbstractZone getZone(int zoneId) {
        return this.zoneList.get(zoneId);
    }

    public AbstractZone getZone(Zone zone) {
        return this.zoneList.get(this.fastZoneList.getOrDefault(zone.getId(), -1));
    }

    public AbstractZone getZone(OfflinePlayer player, String synonym) {
        Map<String, Integer> synonymList = this.fastSynonymList.get(player.getUniqueId());
        if (synonymList == null || synonymList.size() <= 0)
            return null;
        return this.zoneList.get(synonymList.getOrDefault(synonym.toLowerCase(), -1));
    }

    public List<String> getSynonyms(OfflinePlayer offlinePlayer) {
        Map<String, Integer> synonymList = this.fastSynonymList.get(offlinePlayer.getUniqueId());
        if (synonymList == null)
            return ImmutableList.<String>of();
        return ImmutableList.copyOf(synonymList.keySet());
    }

    public List<MainZone> getZones(OfflinePlayer player, World world) {
        List<MainZone> zoneList = new ArrayList<MainZone>();

        List<Integer> mainList = this.fastPlayerList.get(player.getUniqueId());
        if (mainList == null)
            return zoneList;

        for (Integer zoneId : mainList) {
            AbstractZone mainZone = this.zoneList.get(zoneId);
            if (mainZone != null && (world == null || mainZone.getZone().getWorldName().equals(world.getName())))
                zoneList.add((MainZone) mainZone);
        }

        return zoneList;
    }

    public List<AbstractZone> getZones(Location location) {
        List<AbstractZone> zoneList = new ArrayList<AbstractZone>();

        List<Zone> baseList = Zone.getZones(location);
        if (baseList.size() <= 0)
            return zoneList;

        for (Zone zone : baseList) {
            AbstractZone abstractZone = this.zoneList.get(this.fastZoneList.getOrDefault(zone.getId(), -1));
            if (abstractZone != null)
                zoneList.add(abstractZone);
        }

        return zoneList;
    }

    public List<AbstractZone> getZones(Zone zone) {
        List<AbstractZone> zoneList = new ArrayList<AbstractZone>();

        List<Zone> baseList = Zone.getZones(zone);
        if (baseList.size() <= 0)
            return zoneList;

        for (Zone collisionZone : baseList) {
            AbstractZone abstractZone = this.zoneList.get(this.fastZoneList.getOrDefault(collisionZone.getId(), -1));
            if (abstractZone != null)
                zoneList.add(abstractZone);
        }

        return zoneList;
    }

    public AbstractZone getZone(Location location) {
        List<AbstractZone> zoneList = this.getZones(location);
        if (zoneList.size() <= 0)
            return null;

        AbstractZone returnableZone = null;
        for (AbstractZone abstractZone : zoneList) {
            if (abstractZone instanceof SubZone)
                continue;
            MainZone mainZone = (MainZone) abstractZone;

            if (returnableZone == null)
                returnableZone = mainZone;
            if (mainZone.getPriority() > ((MainZone) returnableZone).getPriority())
                returnableZone = mainZone;
        }

        List<SubZone> childList;
        while ((childList = returnableZone.getZones(false)).size() > 0) {
            boolean hasCollidedZone = false;

            for (AbstractZone childZone : childList) {
                if (childZone.getZone().intersect(location.clone())) {
                    returnableZone = childZone;
                    hasCollidedZone = true;
                    break;
                }
            }

            if (!hasCollidedZone)
                break;
        }

        return returnableZone;
    }

    public AbstractZone addZone(Zone zone, UltimateZoneType zoneType, OfflinePlayer player, int parentId,
            int priority) {
        AbstractZone abstractZone = null;

        switch (zoneType) {
        case EXTENDED:
            abstractZone = new ExtendedZone(-1, zone.getId(), "", new ArrayList<Integer>(), new ArrayList<String>(),
                    new ArrayList<String>(), parentId);
            break;
        case INDEPENDENT:
            abstractZone = new IndependentZone(-1, zone.getId(), "", new ArrayList<Integer>(), new ArrayList<String>(),
                    new ArrayList<String>(), parentId);
            break;
        case MAIN:
            abstractZone = new MainZone(-1, zone.getId(), "", new ArrayList<Integer>(), new ArrayList<String>(),
                    new ArrayList<String>(), player.getUniqueId(), priority);
            break;
        }

        if (abstractZone != null) {
            ZoneSchema zoneSchema = ZoneSchema.toSchema(abstractZone);

            try {
                DatabaseHandler.get().getHandler().insert(zoneSchema);
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            abstractZone = zoneSchema.toZone();

            this.zoneList.put(abstractZone.getId(), abstractZone);
            this.fastZoneList.put(abstractZone.getZoneId(), abstractZone.getId());

            if (abstractZone instanceof MainZone) {
                if (!this.fastPlayerList.containsKey(player.getUniqueId()))
                    this.fastPlayerList.put(player.getUniqueId(), new ArrayList<Integer>());
                this.fastPlayerList.get(player.getUniqueId()).add(abstractZone.getId());
            } else {
                ((SubZone) abstractZone).getParent().subList.add(abstractZone.getId());
            }

            if (UltimateZones.isDynmapInstalled)
                DynmapHandler.addZone(abstractZone);

            return abstractZone;
        }

        return null;
    }

    public void delZone(AbstractZone abstractZone) {
        this.zoneList.remove(abstractZone.getId());
        this.fastZoneList.remove(abstractZone.getZoneId());
        this.fastPlayerList.get(abstractZone.getPlayerUniqueId()).remove((Integer) abstractZone.getId());
        if (abstractZone.hasSynonym())
            this.fastSynonymList.get(abstractZone.getPlayerUniqueId()).remove(abstractZone.getSynonym());

        try {
            abstractZone.getZone().delete();
        } catch (Exception exc) {
        }

        if (UltimateZones.isDynmapInstalled)
            DynmapHandler.deleteZone(abstractZone);
    }

    public void updateSynonym(AbstractZone abstractZone, String newSynonym) {
        Map<String, Integer> synonymList = this.fastSynonymList.get(abstractZone.getPlayerUniqueId());
        if (abstractZone.hasSynonym())
            synonymList.remove(abstractZone.getSynonym());

        if (newSynonym.length() > 0) {
            if (synonymList == null)
                this.fastSynonymList.put(abstractZone.getPlayerUniqueId(), new HashMap<String, Integer>());
            this.fastSynonymList.get(abstractZone.getPlayerUniqueId()).put(newSynonym, abstractZone.getId());
        }

        if (UltimateZones.isDynmapInstalled)
            DynmapHandler.addZone(abstractZone); // To Update the content
    }
}