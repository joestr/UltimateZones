package net.dertod2.UltimateZones.Classes.Zones;

import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public abstract class SubZone extends AbstractZone {
    private final int parentId;

    public SubZone(int zoneId, int libZoneId, String synonym, List<Integer> flagList, List<String> breakList,
            List<String> placeList, int parentId) {
        super(zoneId, libZoneId, synonym, flagList, breakList, placeList);

        this.parentId = parentId;
    }

    public AbstractZone getParent() {
        return AbstractZone.getZone(this.parentId);
    }

    public int getParentId() {
        return this.parentId;
    }

    public MainZone getMain() {
        AbstractZone abstractZone = this.getParent();

        while (abstractZone instanceof SubZone) {
            abstractZone = ((SubZone) abstractZone).getParent();
        }

        return (MainZone) abstractZone;
    }

    public int getMainId() {
        AbstractZone abstractZone = this.getParent();

        while (abstractZone instanceof SubZone) {
            abstractZone = ((SubZone) abstractZone).getParent();
        }

        return abstractZone.getId();
    }

    public OfflinePlayer getPlayer() {
        AbstractZone abstractZone = this.getParent();

        while (abstractZone instanceof SubZone) {
            abstractZone = ((SubZone) abstractZone).getParent();
        }

        return abstractZone.getPlayer();
    }

    public UUID getPlayerUniqueId() {
        AbstractZone abstractZone = this.getParent();

        while (abstractZone instanceof SubZone) {
            abstractZone = ((SubZone) abstractZone).getParent();
        }

        return abstractZone.getPlayerUniqueId();
    }

    public boolean isOwner(CommandSender sender) {
        AbstractZone abstractZone = this.getParent();

        while (abstractZone instanceof SubZone) {
            abstractZone = ((SubZone) abstractZone).getParent();
        }

        return abstractZone.isOwner(sender);
    }

    public boolean isOwner(OfflinePlayer offlinePlayer) {
        AbstractZone abstractZone = this.getParent();

        while (abstractZone instanceof SubZone) {
            abstractZone = ((SubZone) abstractZone).getParent();
        }

        return abstractZone.isOwner(offlinePlayer);
    }
}