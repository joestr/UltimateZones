package net.dertod2.UltimateZones.Commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Classes.Zones.MainZone;
import net.dertod2.UltimateZones.Classes.Zones.SubZone;
import net.dertod2.UltimateZones.Classes.Zones.UltimateZoneType;
import net.dertod2.UltimateZones.CreationWizard.AbstractCreation;
import net.dertod2.UltimateZones.CreationWizard.NormalCreation;
import net.dertod2.UltimateZones.Utils.HelpUtils;
import net.dertod2.UltimateZones.Utils.UltimateUtils;
import net.dertod2.ZonesLib.Classes.HeightZone;
import net.dertod2.ZonesLib.Classes.Zone;
import net.dertod2.ZonesLib.Classes.ZoneType;
import net.dertod2.ZonesLib.DisplayAPI.DisplayControl;
import net.dertod2.ZonesLib.DisplayAPI.ParticleEffect;

public class ZoneCommand implements TabExecutor {
	private final Map<String, String> tabAddMap = ImmutableMap.<String, String>builder()
			.put("main", "")
			.put("independent", "")
			.put("extended", "")
			.build();
	
	private final Map<String, String> tabCategoryMap = ImmutableMap.<String, String>builder()
			.put("add", "ultimatezones.commands.zone.add")
			.put("info", "ultimatezones.commands.zone.info")
			.put("list", "ultimatezones.commands.zone.list")
			.put("delete", "ultimatezones.commands.zone.delete")
			.put("edit", "")
			.put("display", "ultimatezones.commands.zone.display")
			.build();
	
	private final Map<String, String> tabEditMap = ImmutableMap.<String, String>builder()
			.put("name", "ultimatezones.commands.zone.edit.name")
			.put("priority", "ultimatezones.commands.zone.edit.priority")
			.put("owner", "ultimatezones.commands.zone.edit.owner")
			.build();
	
	public static Map<UUID, Integer> deleteList = new HashMap<UUID, Integer>();
	
	/*
	 * 
	 * TODO -> Set Flags for Zone o.O is missing >.>
	 * /zone add main [<Form>] [<3D/2D>] ([<Priority>])
	 * /zone add <Independent/Extended> [<Form>] [<3D/2D>]
	 * /zone info [<Zone>]
	 * /zone list [<Spieler>]
	 * /zone delete [<Zone>]
	 * /zone edit <name/priority/owner> <Wert> [<Zone>]
	 * 
	 * /zone display [<Zone>]
	 * 
	 * /zone confirm
	 * /zone check
	 * /zone cancel
	 */

	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("ultimatezones.commands.zone")) {
			Locale.sendPlain(sender, "shared.missingrights", alias);
		} else {
			if ((args.length >= 2 && args.length <= 5) && args[0].equalsIgnoreCase("add") && sender instanceof Player && sender.hasPermission("ultimatezones.commands.zone.add")) {
				this.add((Player) sender, args);
			} else if ((args.length >= 1 && args.length <= 3) && args[0].equalsIgnoreCase("info") && sender.hasPermission("ultimatezones.commands.zone.info")) {
				this.info(sender, args);
			} else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("list") && sender.hasPermission("ultimatezones.commands.zone.list")) {
				this.list(sender, args);
			} else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("delete") && sender.hasPermission("ultimatezones.commands.zone.delete")) {
				this.delete(sender, args);
			} else if ((args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("name") && sender.hasPermission("ultimatezones.commands.zone.edit.name")) {
				this.editName(sender, args);
			} else if ((args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("priority") && sender.hasPermission("ultimatezones.commands.zone.edit.priority")) {
				this.editPriority(sender, args);
			} else if ((args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("owner") && sender.hasPermission("ultimatezones.commands.zone.edit.owner")) {
				this.editOwner(sender, args);
			} else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("display") && sender instanceof Player && sender.hasPermission("ultimatezones.commands.zone.display")) {
				this.display((Player) sender, args);	
			} else if (args.length == 1 && args[0].equalsIgnoreCase("confirm") && sender instanceof Player && sender.hasPermission("ultimatezones.commands.zone.add")) {
				this.confirm((Player) sender, args);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("check") && sender instanceof Player && sender.hasPermission("ultimatezones.commands.zone.add")) {
				this.check((Player) sender, args);
			} else if (args.length == 1 && args[0].equalsIgnoreCase("cancel") && sender instanceof Player && sender.hasPermission("ultimatezones.commands.zone.add")) {
				this.cancel((Player) sender, args);
			} else {
				HelpUtils.help(sender, alias, "help.commands.zone.add.main", new Permission("ultimatezones.commands.zone.add"));
				HelpUtils.help(sender, alias, "help.commands.zone.add.child", new Permission("ultimatezones.commands.zone.add"));
				HelpUtils.help(sender, alias, "help.commands.zone.info", new Permission("ultimatezones.commands.zone.info"));
				HelpUtils.help(sender, alias, "help.commands.zone.list", new Permission("ultimatezones.commands.zone.list"));
				HelpUtils.help(sender, alias, "help.commands.zone.delete", new Permission("ultimatezones.commands.zone.delete"));
				HelpUtils.help(sender, alias, "help.commands.zone.edit");
				HelpUtils.help(sender, alias, "help.commands.zone.display", new Permission("ultimatezones.commands.zone.display"));
			}
		}
		
		return true;
	}
	
	// /zone add main [<Form>] [<3D/2D>]
	// /zone add <Independent/Extended> [<Form>] [<3D/2D>]
	private void add(Player sender, String[] args) {
		AbstractCreation abstractCreation = AbstractCreation.getCreation(sender);
		if (abstractCreation == null) {
			UltimateZoneType ultimateZoneType = UltimateZoneType.byName(args[1]);
			if (ultimateZoneType == null) {
				Locale.sendPlain(sender, "commands.zone.add.error.zonetype.ultimatezones", args[1]);
				return;
			}
			
			ZoneType zoneType = ZoneType.CUBOID;
			if (args.length == 3) {
				zoneType = ZoneType.byName(args[2]);
				if (zoneType == null) {
					Locale.sendPlain(sender, "commands.zone.add.error.zonetype.zoneslib", args[2]);
					return;
				}
			}
			
			boolean force3D = ultimateZoneType == UltimateZoneType.MAIN ? false : true;
			if (args.length == 4) force3D = args[3].equalsIgnoreCase("3D");
			
			// Permission to allow creation in world
			if (!sender.hasPermission("ultimatezones.creation.world." + sender.getWorld().getName().toLowerCase())) {
				Locale.sendPlain(sender, "commands.zone.add.forbiddenworld", sender.getWorld().getName());
				return;
			}
			
			// Disabled Zones in this world
			if (!UltimateZones.getConfiguration(sender.getWorld()).getBoolean("activate-zones")) {
				Locale.sendPlain(sender, "commands.zone.add.forbiddenworld", sender.getWorld().getName());
				return;
			}
			
			// Permission to allow creation of main/child of specific geometry for 2d/3d
			// Allow only Main 2D Cuboid: ultimatezones.creation.main.2d.cuboid
			// Allow all Child Types in 2D and 3D: ultimatezones.creation.child.*
			if (!sender.hasPermission("ultimatezones.creation." + (ultimateZoneType == UltimateZoneType.MAIN ? "main" : "child") + "." + (force3D ? "3d" : "2d") + "." + zoneType.getName().toLowerCase())) {
				switch (ultimateZoneType) {
				case MAIN:
					Locale.sendPlain(sender, "commands.zone.add.forbiddentype.main", force3D ? "3D" : "2D", zoneType.getName());
					break;		
				default:
					Locale.sendPlain(sender, "commands.zone.add.forbiddentype.child", force3D ? "3D" : "2D", zoneType.getName());
					break;
				}
				
				return;
			}
			
			// Check if Player is allowed to create more Main Zones (other Checks only inside Creation Classes)
			if (ultimateZoneType == UltimateZoneType.MAIN && UltimateZones.getConfiguration(sender.getWorld()).getInt("max-zones") != -1) {
				if (!sender.hasPermission("ultimatezones.ignorerestrictions") && AbstractZone.getZones(sender).size() >= UltimateZones.getConfiguration(sender.getWorld()).getInt("max-zones")) {
					Locale.sendPlain(sender, "commands.zone.add.error.maxzones", sender.getWorld().getName(), UltimateZones.getConfiguration(sender.getWorld()).getInt("max-zones"));
					return;
				}
			}
			
			
			abstractCreation = new NormalCreation(sender, ultimateZoneType, zoneType, force3D);
			if (args.length == 5 && ultimateZoneType == UltimateZoneType.MAIN && sender.hasPermission("ultimatezones.commands.zone.edit.priority")) {
				if (UltimateUtils.isInteger(args[4])) {
					abstractCreation.setPriority(Integer.parseInt(args[4]));
					Locale.sendPlain(sender, "commands.zone.add.priority.success", args[4]);
				} else {
					Locale.sendPlain(sender, "commands.zone.add.priority.error", args[4]);
					return;
				}
			}
			
			AbstractCreation.startCreation(abstractCreation);
		} else {
			Locale.sendPlain(sender, "commands.zone.add.error.started");
		}
	}
	
	// /zone info [<Zone>]
	private void info(CommandSender sender, String[] args) {
		AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 2 ? args[1] : null);
		if (abstractZone != null) {
			Zone zone = abstractZone.getZone();
			AbstractZone parent = abstractZone instanceof SubZone ? ((SubZone) abstractZone).getParent() : null;			
			String dimension = zone instanceof HeightZone ? ((HeightZone) zone).isFullHeight() ? "2D" : "3D" : "2D";			
			DateFormat dateFormat = new SimpleDateFormat(UltimateZones.getConfiguration().getString("date-layouts." + Locale.get(sender).getTag()));
			
			Locale.json(sender, "commands.zone.info.information", abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(), abstractZone.getId(), abstractZone.getPlayer().getName(), abstractZone.getType().getName(), zone.getType().getName(), dimension);
			Locale.sendPlain(sender, "commands.zone.info.created", dateFormat.format(zone.getCreated()));
			if (parent != null) Locale.json(sender, "commands.zone.info.parent", parent.hasSynonym() ? parent.getSynonym() : parent.getId(), parent.getId(), parent.getPlayer().getName());
			Locale.sendPlain(sender, "commands.zone.info.area_volume", UltimateUtils.formattedArea(zone.getArea(), Locale.get(sender).getJavaLocale()), UltimateUtils.formattedVolume(zone.getVolume(), Locale.get(sender).getJavaLocale()));
			Locale.sendPlain(sender, "commands.zone.info.childs", abstractZone.getZones(false).size());
		} else {
			Locale.sendPlain(sender, "shared.zonenotfound");
		}
	}
	
	// /zone list [<Spieler>]
	private void list(CommandSender sender, String[] args) {
		OfflinePlayer offlinePlayer = args.length == 2 ? UltimateUtils.getPlayer(args[1]) : sender instanceof Player ? (Player) sender : null;
		if (offlinePlayer != null) {
			List<MainZone> zoneList = AbstractZone.getZones(offlinePlayer);
			int childs = UltimateUtils.getChildZones(zoneList);
			
			Locale.sendPlain(sender, "commands.zone.list.main", offlinePlayer.getName(), zoneList.size(), childs);
			for (MainZone mainZone : zoneList) {
				String synonym = mainZone.hasSynonym() ? mainZone.getSynonym() : String.valueOf(mainZone.getId());
				
				int childs_zone = mainZone.getZones(false).size();
				int childs_recursive = mainZone.getZones(true).size();
				
				Locale.json(sender, "commands.zone.list.zone", synonym, mainZone.getId(), offlinePlayer.getName(), childs_zone, childs_recursive);
			}
		}
	}
	
	// /zone delete [<Zone>]
	private void delete(CommandSender sender, String[] args) {
		AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 2 ? args[1] : null);
		if (abstractZone != null) {
			if (sender instanceof ConsoleCommandSender || abstractZone.isOwner(sender) || (abstractZone instanceof SubZone && sender instanceof Player && abstractZone.hasRight((Player) sender, Flag.AdministrateZones).rightEnum == RightEnum.Success)) {
				Integer zoneId = ZoneCommand.deleteList.remove(sender instanceof ConsoleCommandSender ? UltimateZones.getUniqueId() : ((Player) sender).getUniqueId());
				
				String synonym = abstractZone.hasSynonym() ? abstractZone.getSynonym() : String.valueOf(abstractZone.getId());
				int childs = abstractZone.getZones(true).size();
				
				if (zoneId != null && abstractZone.getId() == zoneId) {				
					abstractZone.delete();
					Locale.json(sender, "commands.zone.delete.success", synonym, zoneId, abstractZone.getPlayer().getName(), childs);
				} else {
					ZoneCommand.deleteList.put(sender instanceof ConsoleCommandSender ? UltimateZones.getUniqueId() : ((Player) sender).getUniqueId(), abstractZone.getId());
					Locale.json(sender, "commands.zone.delete.question", synonym, abstractZone.getId(), abstractZone.getPlayer().getName(), childs);
				}
			} else {
				Locale.sendPlain(sender, "commands.zone.delete.missingrights");
			}
		} else {
			Locale.sendPlain(sender, "shared.zonenotfound");
		}
	}
	
	// /zone edit name <Wert> [<Zone>]
	private void editName(CommandSender sender, String[] args) {
		AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 4 ? args[3] : null);
		if (abstractZone != null) {
			if (sender instanceof ConsoleCommandSender || abstractZone.isOwner(sender)) {
				if (abstractZone.setSynonym(args[2])) {
					Locale.sendPlain(sender, "commands.zone.edit.name.success", abstractZone.getId(), abstractZone.getPlayer().getName(), args[2]);
				} else {
					Locale.sendPlain(sender, "commands.zone.edit.name.error", args[2]);
				}
			} else {
				Locale.sendPlain(sender, "commands.zone.edit.name.missingrights");
			}
		} else {
			Locale.sendPlain(sender, "shared.zonenotfound");
		}
	}
	
	// /zone edit priority <Wert> [<Zone>]
	private void editPriority(CommandSender sender, String[] args) {
		AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 4 ? args[3] : null);
		if (abstractZone != null) {
			if (abstractZone instanceof MainZone) {
				if (UltimateUtils.isInteger(args[2])) {
					((MainZone) abstractZone).setPriority(Integer.parseInt(args[2]));
					String synonym = abstractZone.hasSynonym() ? abstractZone.getSynonym() : String.valueOf(abstractZone.getId());
					Locale.json(sender, "commands.zone.edit.priority.success", synonym, abstractZone.getId(), abstractZone.getPlayer().getName());
				} else {
					Locale.sendPlain(sender, "commands.zone.edit.priority.error", args[2]);
				}
			} else {
				Locale.sendPlain(sender, "commands.zone.edit.priority.error.mainzone");
			}
		} else {
			Locale.sendPlain(sender, "shared.zonenotfound");
		}
	}
	
	// /zone edit owner <Wert> [<Zone>]
	private void editOwner(CommandSender sender, String[] args) {
		AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 4 ? args[3] : null);
		if (abstractZone != null) {
			if (abstractZone instanceof MainZone) {
				OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[2]);
				if (offlinePlayer != null) {
					if (((MainZone) abstractZone).move(offlinePlayer)) {
						Locale.sendPlain(sender, "commands.zone.edit.owner.success", offlinePlayer.getName());
					} else {
						Locale.sendPlain(sender, "commands.zone.edit.owner.error");
					}
				} else {
					Locale.sendPlain(sender, "commands.zone.edit.owner.error.player", args[2]);
				}
			} else {
				Locale.sendPlain(sender, "commands.zone.edit.owner.error.mainzone");
			}
		} else {
			Locale.sendPlain(sender, "shared.zonenotfound");
		}
	}
	
	// /zone display [<Zone>]
	private void display(Player sender, String[] args) {
		AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 2 ? args[1] : null);
		if (abstractZone != null) {
			DisplayControl.startViewing(abstractZone.getZone(), ParticleEffect.REDSTONE, 15000, sender);
			Locale.sendPlain(sender, "commands.zone.display.success", 15);
		} else {
			Locale.sendPlain(sender, "shared.zonenotfound");
		}
	}
	
	// /zone confirm
	private void confirm(Player sender, String[] args) {
		AbstractCreation abstractCreation = AbstractCreation.getCreation(sender);
		if (abstractCreation != null) {
			abstractCreation.create();
		} else {
			Locale.sendPlain(sender, "commands.zone.creation.error");
		}
	}
	
	// /zone check
	private void check(Player sender, String[] args) {
		AbstractCreation abstractCreation = AbstractCreation.getCreation(sender);
		if (abstractCreation != null) {
			abstractCreation.check(false);
		} else {
			Locale.sendPlain(sender, "commands.zone.creation.error");
		}
	}
	
	// /zone cancel
	private void cancel(Player sender, String[] args) {
		AbstractCreation abstractCreation = AbstractCreation.getCreation(sender);
		if (abstractCreation != null) {
			abstractCreation.abort(false);
		} else {
			Locale.sendPlain(sender, "commands.zone.creation.error");
		}
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return UltimateUtils.tabCompleteList(args[0], this.tabCategoryMap, sender);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
			return UltimateUtils.tabCompleteList(args[1], this.tabAddMap, sender);
		} else if (args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("delete"))) {
			return UltimateUtils.findZoneMatches(sender, args[1]);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
			return null;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
			return UltimateUtils.tabCompleteList(args[1], this.tabEditMap, sender);
		} else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
			List<String> typeList = new ArrayList<String>();
			for (ZoneType zoneType : ZoneType.values()) typeList.add(zoneType.getName());
			return UltimateUtils.tabCompleteList(args[2], typeList);
		} else if (args.length == 3 && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("owner")) {
			return null;
		} else if (args.length == 4 && args[0].equalsIgnoreCase("add")) {
			List<String> typeList = ImmutableList.<String>of("3D", "2D");
			return UltimateUtils.tabCompleteList(args[3], typeList);
		} else if (args.length == 4 && args[0].equalsIgnoreCase("edit")) {
			return UltimateUtils.findZoneMatches(sender, args[3]);
		}
		
		return ImmutableList.of();
	}
}