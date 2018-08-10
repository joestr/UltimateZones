package net.dertod2.UltimateZones.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Classes.Preset;
import net.dertod2.UltimateZones.Classes.RightReference;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Utils.ChatComponent;
import net.dertod2.UltimateZones.Utils.ChatComponent.Click;
import net.dertod2.UltimateZones.Utils.HelpUtils;
import net.dertod2.UltimateZones.Utils.NMSHelper;
import net.dertod2.UltimateZones.Utils.StringUtils;
import net.dertod2.UltimateZones.Utils.UltimateUtils;

public class PresetCommand implements TabExecutor {
	private final Map<String, String> tabCategoryMap = ImmutableMap.<String, String>builder()
			.put("list", "ultimatezones.commands.preset.list")
			.put("create", "ultimatezones.commands.preset.create")
			.put("edit", "")
			.put("delete", "ultimatezones.commands.preset.delete")
			.put("apply", "ultimatezones.commands.preset.apply")
			.build();
	
	private final Map<String, String> tabEditMap = ImmutableMap.<String, String>builder()
			.put("flag", "ultimatezones.commands.preset.edit.flag")
			.put("name", "ultimatezones.commands.preset.edit.name")
			.put("description", "ultimatezones.commands.preset.edit.description")
			.put("place", "ultimatezones.commands.preset.edit.material")
			.put("break", "ultimatezones.commands.preset.edit.material")
			.build();
	
	/*
	 * /preset apply <Preset> <Spieler> [<Zone>]
	 * /preset create <Name> [<Spieler>] [<Zone>]
	 * /preset delete <Preset>
	 * /preset list [<Preset>]
	 * /preset edit name <Preset> <Name>
	 * /preset edit flag <Preset> <Flag>
	 * /preset edit description <Preset> <Description>
	 * /preset edit place/break <Preset> <Material>
	 */

	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("ultimatezones.commands.preset")) {
			Locale.sendPlain(sender, "shared.missingrights", alias);
		} else {
			if ((args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("apply") && sender.hasPermission("ultimatezones.commands.preset.apply")) {
				this.apply(sender, args);
			} else if ((args.length == 2 || args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("create") && sender.hasPermission("ultimatezones.commands.preset.create")) {	
				this.create(sender, args);
			} else if (args.length == 2 && args[0].equalsIgnoreCase("delete") && sender.hasPermission("ultimatezones.commands.preset.delete")) {
				this.delete(sender, args);
			} else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("list") && sender.hasPermission("ultimatezones.commands.preset.list")) {
				this.list(sender, args);
			} else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("name") && sender.hasPermission("ultimatezones.commands.preset.edit.name")) {
				this.editName(sender, args);
			} else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("flag") && sender.hasPermission("ultimatezones.commands.preset.edit.flag")) {
				this.editFlag(sender, args);
			} else if (args.length >= 4 && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("description") && sender.hasPermission("ultimatezones.commands.preset.edit.description")) {
				this.editDescription(sender, args);
			} else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && (args[1].equalsIgnoreCase("place") || args[1].equalsIgnoreCase("break")) && sender.hasPermission("ultimatezones.commands.preset.edit.material")) {			
				this.editBlock(sender, args);
			} else {
				HelpUtils.help(sender, alias, "help.commands.preset.apply", new Permission("ultimatezones.commands.preset.apply"));
				HelpUtils.help(sender, alias, "help.commands.preset.create", new Permission("ultimatezones.commands.preset.create"));
				HelpUtils.help(sender, alias, "help.commands.preset.delete", new Permission("ultimatezones.commands.preset.delete"));
				HelpUtils.help(sender, alias, "help.commands.preset.list", new Permission("ultimatezones.commands.preset.list"));
				HelpUtils.help(sender, alias, "help.commands.preset.edit.name", new Permission("ultimatezones.commands.preset.edit.name"));
				HelpUtils.help(sender, alias, "help.commands.preset.edit.flag", new Permission("ultimatezones.commands.preset.edit.flag"));
				HelpUtils.help(sender, alias, "help.commands.preset.edit.description", new Permission("ultimatezones.commands.preset.edit.description"));
				HelpUtils.help(sender, alias, "help.commands.preset.edit.material", new Permission("ultimatezones.commands.preset.edit.material"));
			}
		}
				
		return true;
	}
	
	// /preset apply <Preset> <Spieler> [<Zone>]
	private void apply(CommandSender sender, String[] args) {
		Preset preset = Preset.getPreset(args[1]);
		if (preset != null) {
			OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[2]);
			if (offlinePlayer != null) {
				AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 4 ? args[3] : null);
				if (abstractZone != null) {
					abstractZone.getRights(offlinePlayer, true).apply(preset);
					Locale.sendPlain(sender, "commands.preset.apply", offlinePlayer.getName(), preset.getName());
				} else {
					Locale.sendPlain(sender, "zonenotfound");
				}
			} else {
				Locale.sendPlain(sender, "playernotfound", args[2]);
			}
		} else {
			Locale.sendPlain(sender, "commands.preset.notfound", args[1]);
		}
	}
	
	// /preset create <Name> [<Spieler>] [<Zone>]
	private void create(CommandSender sender, String[] args) {
		Preset preset = Preset.getPreset(args[1]);
		if (preset == null) {
			if (args.length == 2) {
				preset = Preset.createPreset(args[1]);
				Locale.sendPlain(sender, "commands.preset.create.1", args[1]);
			} else {
				OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[2]);
				if (offlinePlayer != null) {
					AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 4 ? args[3] : null);
					if (abstractZone != null) {
						RightReference rightReference = abstractZone.getRights(offlinePlayer);
						if (rightReference != null) {
							preset = Preset.createPreset(args[1], rightReference);
							Locale.json(sender, "commands.preset.create.2", args[1], offlinePlayer.getName(), abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(), abstractZone.getId(), abstractZone.getPlayer().getName());
						} else {
							Locale.sendPlain(sender, "commands.preset.create.error.norights", offlinePlayer.getName());
						}
					} else {
						Locale.sendPlain(sender, "shared.zonenotfound");
					}
				} else {
					Locale.sendPlain(sender, "shared.playernotfound", args[2]);
				}
			}
		} else {
			Locale.sendPlain(sender, "commands.preset.create.error.presetexist", args[1]);
		}
	}
	
	// /preset delete <Preset>
	private void delete(CommandSender sender, String[] args) {
		Preset preset = Preset.getPreset(args[1]);
		if (preset != null) {
			preset.delete();
			Locale.sendPlain(sender, "commands.preset.delete", args[1]);
		} else {
			Locale.sendPlain(sender, "commands.preset.notfound", args[1]);
		}
	}
	
	// /preset list [<Preset>]
	private void list(CommandSender sender, String[] args) {
		if (args.length == 2) {
			Preset preset = Preset.getPreset(args[1]);
			if (preset != null) {
				Locale.sendPlain(sender, "commands.preset.list.info", args[1]);
				Locale.sendPlain(sender, "commands.preset.list.info.description", preset.getDescription());
				
				ChatComponent chatComponent = UltimateUtils.generateRightsInfo(sender, preset);				
				chatComponent.to(sender);
			} else {
				Locale.sendPlain(sender, "commands.preset.notfound", args[1]);
			}
			
		} else {
			List<Preset> presetList = Preset.getPresets();
			if (presetList.size() > 0) {
				Locale.sendPlain(sender, "commands.preset.list.all", presetList.size());
				
				ChatComponent chatComponent = new ChatComponent();			
				for (Preset preset : presetList) {
					chatComponent.last().then(preset.getName(), ChatColor.GOLD)
						.click(Click.RunCommand, "/preset list " + preset.getName())
					.then(", ", ChatColor.GRAY);
				}
				
				chatComponent.last().delete().to(sender);
			} else {
				Locale.sendPlain(sender, "commands.preset.list.empty");
			}
		}
	}
	
	// /preset edit name <Preset> <Name>
	private void editName(CommandSender sender, String[] args) {
		Preset preset = Preset.getPreset(args[2]);
		if (preset != null) {
			if (Preset.getPreset(args[3]) == null) {
				preset.setName(args[3]);
				Locale.sendPlain(sender, "commands.preset.edit.name.success", args[2], args[3]);
			} else {
				Locale.sendPlain(sender, "commands.preset.edit.name.error.presetexist", args[3]);
			}
		} else {
			Locale.sendPlain(sender, "commands.preset.notfound", args[2]);
		}
	}
	
	// /preset edit flag <Preset> <Flag>
	private void editFlag(CommandSender sender, String[] args) {
		Preset preset = Preset.getPreset(args[2]);
		if (preset != null) {
			Flag flag = Flag.fromString(args[3]);
			if (flag != null) {
				if (preset.setFlag(flag)) {
					Locale.sendPlain(sender, "commands.preset.edit.flag.set", flag.typeName, args[2]);
				} else {
					Locale.sendPlain(sender, "commands.preset.edit.flag.unset", flag.typeName, args[2]);
				}
			} else {
				Locale.sendPlain(sender, "commands.preset.edit.flag.notexist", args[3]);
			}
		} else {
			Locale.sendPlain(sender, "commands.preset.notfound", args[2]);
		}
	}
	
	// /preset edit description <Preset> <Description>
	private void editDescription(CommandSender sender, String[] args) {
		Preset preset = Preset.getPreset(args[2]);
		if (preset != null) {
			preset.setDescription(StringUtils.getString(args, 3, " "));
			Locale.sendPlain(sender, "commands.preset.edit.description.set", args[2]);
			sender.sendMessage(ChatColor.GOLD + preset.getDescription());
		} else {
			Locale.sendPlain(sender, "commands.preset.notfound", args[2]);
		}
	}
	
	// /preset edit place/break <Preset> <Material>
	private void editBlock(CommandSender sender, String[] args) {
		Preset preset = Preset.getPreset(args[2]);
		if (preset != null) {
			Material material = UltimateUtils.lookupMaterial(args[3]);
			if (material != null) {
				ItemStack itemStack = new ItemStack(material);
				
				if (args[1].equalsIgnoreCase("place")) {
					String phrase = Locale.plain(sender, preset.setPlace(material) ? "shared.added" : "shared.withdrawn");
					Locale.json(sender, "commands.preset.edit.material.success", 
							NMSHelper.getMojangIdentifierName(itemStack),
							NMSHelper.getMojangTranslatableName(itemStack),
							"place", preset.getName(), phrase);
				} else if (args[1].equalsIgnoreCase("break")) {
					String phrase = Locale.plain(sender, preset.setBreak(material) ? "shared.added" : "shared.withdrawn");
					Locale.json(sender, "commands.preset.edit.material.success", 
							NMSHelper.getMojangIdentifierName(itemStack),
							NMSHelper.getMojangTranslatableName(itemStack),
							"break", preset.getName(), phrase);
				} else {
					Locale.sendPlain(sender, "commands.preset.edit.material.unknownmethod");
				}
			} else {
				Locale.sendPlain(sender, "commands.preset.edit.material.notfound", args[3]);
			}
		} else {
			Locale.sendPlain(sender, "commands.preset.notfound", args[2]);
		}
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return UltimateUtils.tabCompleteList(args[0], this.tabCategoryMap, sender);
		} else if (args.length == 2 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("delete")|| args[0].equalsIgnoreCase("apply"))) {
			List<Preset> presets = Preset.getPresets(); List<String> presetList = new ArrayList<String>(presets.size());
			for (Preset preset : presets) presetList.add(preset.getName());
			return UltimateUtils.tabCompleteList(args[1], presetList);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
			return UltimateUtils.tabCompleteList(args[1], this.tabEditMap, sender);
		} else if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
			List<Preset> presets = Preset.getPresets(); List<String> presetList = new ArrayList<String>(presets.size());
			for (Preset preset : presets) presetList.add(preset.getName());
			return UltimateUtils.tabCompleteList(args[2], presetList);
		} else if (args.length == 3 && args[0].equalsIgnoreCase("apply")) {
			return null;
		} else if (args.length == 4 && (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("apply"))) {
			return UltimateUtils.findZoneMatches(sender, args[3]);
		} else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("flag")) {
			List<String> flagList = new ArrayList<String>(Flag.values().length);
			for (Flag flag : Flag.values()) flagList.add(flag.name());
			return UltimateUtils.tabCompleteList(args[3], flagList);
		} else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && (args[1].equalsIgnoreCase("place") || args[1].equalsIgnoreCase("break"))) {
			List<String> materialList = new ArrayList<String>(Material.values().length);
			for (Material material : Material.values()) materialList.add(material.name());
			return UltimateUtils.tabCompleteList(args[3], materialList);
		}
		
		return ImmutableList.of();
	}
}