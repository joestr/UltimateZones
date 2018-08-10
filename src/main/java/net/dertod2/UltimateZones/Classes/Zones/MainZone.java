package net.dertod2.UltimateZones.Classes.Zones;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Utils.NMSHelper;

public class MainZone extends AbstractZone {
	private UUID player;
	private int zonePriority;
	
	public MainZone(int zoneId, int libZoneId, String synonym, List<Integer> flagList, List<String> breakList, List<String> placeList, UUID player, int zonePriority) {
		super(zoneId, libZoneId, synonym, flagList, breakList, placeList);
		
		this.player = player;
		this.zonePriority = zonePriority;
	}
	
	public OfflinePlayer getPlayer() {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.player);
		if (offlinePlayer == null) offlinePlayer = NMSHelper.lookupPlayer(this.player);
		return offlinePlayer;
	}
	
	public UUID getPlayerUniqueId() {
		return this.player;
	}

	public boolean isOwner(CommandSender sender) {
		if (!(sender instanceof Player)) return false;
		return this.player.compareTo(((Player) sender).getUniqueId()) == 0;
	}
	
	public boolean isOwner(OfflinePlayer offlinePlayer) {
		return this.player.compareTo(offlinePlayer.getUniqueId()) == 0;
	}

	public int getPriority() {
		return this.zonePriority;
	}

	public boolean setPriority(int priority) {
		this.zonePriority = priority;

		return this.update();
	}
	
	public boolean move(OfflinePlayer player) {
		UUID oldPlayer = this.player;
		this.player = player.getUniqueId();
		
		UltimateZones.zoneControl.move(this, oldPlayer, this.player);
		return this.update();
	}
}