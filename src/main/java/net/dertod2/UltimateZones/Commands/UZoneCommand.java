package net.dertod2.UltimateZones.Commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permission;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Utils.HelpUtils;
import net.dertod2.UltimateZones.Utils.UltimateUtils;

public class UZoneCommand implements TabExecutor {
	private final Map<String, String> tabCategoryMap = ImmutableMap.<String, String>builder()
			.put("find", "ultimatezones.commands.find")
			.put("preset", "ultimatezones.commands.preset")
			.put("zone", "ultimatezones.commands.zone")
			.put("rights", "ultimatezones.commands.rights")
			.put("reload", "ultimatezones.commands.reload")
			.put("update", "ultimatezones.updatemessage")
			.build();
	
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("ultimatezones.commands.main")) {
			Locale.sendPlain(sender, "shared.missingrights", alias);
		} else {
			if (args.length != 0) {
				if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("ultimatezones.commands.reload")) {
					this.reload(sender, args);
					return true;
				} else if (args.length == 1 && args[0].equalsIgnoreCase("update") && sender.hasPermission("ultimatezones.updatemessage")) {
					this.updates(sender, args);
					return true;
				} 
				
				TabExecutor tabExecutor = UltimateZones.commandMap.get(args[0].toLowerCase());
				if (tabExecutor != null) {
					tabExecutor.onCommand(sender, command, alias + " " + args[0], args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[] {});
				} else {
					Locale.sendPlain(sender, "commands.uzone.notfound", args[0]);
				}
			} else {
				HelpUtils.help(sender, alias, "help.commands.uzone.find", new Permission("ultimatezones.commands.find"));
				HelpUtils.help(sender, alias, "help.commands.uzone.preset", new Permission("ultimatezones.commands.preset"));
				HelpUtils.help(sender, alias, "help.commands.uzone.zone", new Permission("ultimatezones.commands.zone"));
				HelpUtils.help(sender, alias, "help.commands.uzone.rights", new Permission("ultimatezones.commands.rights"));
				HelpUtils.help(sender, alias, "help.commands.uzone.reload", new Permission("ultimatezones.commands.reload"));
				HelpUtils.help(sender, alias, "help.commands.uzone.update", new Permission("ultimatezones.updatemessage"));
			}
		}
		
		return true;
	}
	
	private void reload(CommandSender sender, String[] args) {
		UltimateZones.getInstance().reloadConfig();
		
		UltimateZones.getInstance().onDisable();		
		UltimateZones.getInstance().onEnable();
		
		sender.sendMessage(ChatColor.GREEN + "UltimateZones reloaded!");
	}
	
	private void updates(CommandSender sender, String[] args) {
		sender.sendMessage(ChatColor.GREEN + "Checking for updates... When no further messages appears there are no updates available!");
		UltimateZones.updater.check(sender);
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return UltimateUtils.tabCompleteList(args[0], this.tabCategoryMap, sender);
		} else if (args.length > 1) {
			TabExecutor tabExecutor = UltimateZones.commandMap.get(args[0].toLowerCase());
			if (tabExecutor != null) return tabExecutor.onTabComplete(sender, command, alias + " " + args[0], Arrays.copyOfRange(args, 1, args.length));
		}
		
		return ImmutableList.<String>of();
	}
}