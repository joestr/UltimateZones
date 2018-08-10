package net.dertod2.UltimateZones.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Utils.NMSHelper;

public class FindCommand implements TabExecutor {
	public static List<UUID> findList = new ArrayList<UUID>();
	
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("ultimatezones.commands.find") || !(sender instanceof Player)) {
			Locale.sendPlain(sender, "shared.missingrights", alias);
		} else {
			UUID uuid = ((Player) sender).getUniqueId();
			
			if (FindCommand.findList.remove(uuid)) {
				Locale.sendPlain(sender, "commands.find.deactivated");
			} else {
				FindCommand.findList.add(uuid);
				if (UltimateZones.findItem != null && UltimateZones.findItem.getType() != Material.AIR) {
					Locale.json(sender, "commands.find.activated.withitem", NMSHelper.getMojangIdentifierName(UltimateZones.findItem), NMSHelper.getMojangTranslatableName(UltimateZones.findItem));
				} else {
					Locale.json(sender, "commands.find.activated.withoutitem");
				}
			}
		}
		
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return ImmutableList.of();
	}
}