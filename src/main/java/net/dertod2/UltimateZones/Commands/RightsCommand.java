package net.dertod2.UltimateZones.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Classes.RightReference;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Utils.ChatComponent;
import net.dertod2.UltimateZones.Utils.HelpUtils;
import net.dertod2.UltimateZones.Utils.NMSHelper;
import net.dertod2.UltimateZones.Utils.UltimateUtils;
import net.dertod2.UltimateZones.Utils.ChatComponent.Click;

public class RightsCommand implements TabExecutor {
    private final Map<String, String> tabCategoryMap = ImmutableMap.<String, String>builder().put("edit", "")
            .put("copy", "").put("list", "ultimatezones.commands.rights.list")
            .put("clear", "ultimatezones.commands.rights.clear").put("flags", "ultimatezones.commands.rights.flags")
            .build();

    private final Map<String, String> tabEditMap = ImmutableMap.<String, String>builder()
            .put("flag", "ultimatezones.commands.rights.edit.flag")
            .put("place", "ultimatezones.commands.rights.edit.material")
            .put("break", "ultimatezones.commands.rights.edit.material").build();

    private final Map<String, String> tabCopyMap = ImmutableMap.<String, String>builder()
            .put("zone", "ultimatezones.commands.rights.copy.zone")
            .put("player", "ultimatezones.commands.rights.copy.player").build();

    /*
     * /rights edit flag <Flag> <Player> [<Zone>] - Gibt oder nimmt einem Spieler
     * ein recht /rights edit place/break <Block> <Player> [<Zone>] - Fügt einen
     * Block der Place Liste hinzu oder entfernt ihn /rights copy zone <Zone1>
     * <Zone2> [<Player>] - Kopiert alle Rechte einer Zone auf eine andere /rights
     * copy player <Player1> <Player2> [<Zone>] - Kopiert alle Rechte eines Spielers
     * auf einen Anderen /rights list [<Zone>] [<Player>] - Listet alle Rechte einer
     * Zone/eines Spielers auf /rights clear [<Zone>] [<Player>] - Entfernt alle
     * Rechte einer Zone /rights flags - Listet alle Rechte auf
     */

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("ultimatezones.commands.rights")) {
            Locale.sendPlain(sender, "shared.missingrights", alias);
        } else {
            if ((args.length == 4 || args.length == 5) && args[0].equalsIgnoreCase("edit")
                    && args[1].equalsIgnoreCase("flag")
                    && sender.hasPermission("ultimatezones.commands.rights.edit.flag")) {
                this.editFlag(sender, args);
            } else if ((args.length == 4 || args.length == 5) && args[0].equalsIgnoreCase("edit")
                    && (args[1].equalsIgnoreCase("place") || args[1].equalsIgnoreCase("break"))
                    && sender.hasPermission("ultimatezones.commands.rights.edit.material")) {
                this.editBlock(sender, args);
            } else if ((args.length == 4 || args.length == 5) && args[0].equalsIgnoreCase("copy")
                    && args[1].equalsIgnoreCase("zone")
                    && sender.hasPermission("ultimatezones.commands.rights.copy.zone")) {
                this.copyZone(sender, args);
            } else if ((args.length == 4 || args.length == 5) && args[0].equalsIgnoreCase("copy")
                    && args[1].equalsIgnoreCase("player")
                    && sender.hasPermission("ultimatezones.commands.rights.copy.player")) {
                this.copyPlayer(sender, args);
            } else if (args.length >= 1 && args.length <= 3 && args[0].equalsIgnoreCase("list")
                    && sender.hasPermission("ultimatezones.commands.rights.list")) {
                this.list(sender, args);
            } else if (args.length >= 1 && args.length <= 3 && args[0].equalsIgnoreCase("clear")
                    && sender.hasPermission("ultimatezones.commands.rights.clear")) {
                this.clear(sender, args);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("flags")
                    && sender.hasPermission("ultimatezones.commands.rights.flags")) {
                this.flags(sender, args);
            } else {
                HelpUtils.help(sender, alias, "help.commands.rights.edit.flag",
                        new Permission("ultimatezones.commands.rights.edit.flag"));
                HelpUtils.help(sender, alias, "help.commands.rights.edit.material",
                        new Permission("ultimatezones.commands.rights.edit.material"));
                HelpUtils.help(sender, alias, "help.commands.rights.copy.zone",
                        new Permission("ultimatezones.commands.rights.copy.zone"));
                HelpUtils.help(sender, alias, "help.commands.rights.copy.player",
                        new Permission("ultimatezones.commands.rights.copy.player"));
                HelpUtils.help(sender, alias, "help.commands.rights.list",
                        new Permission("ultimatezones.commands.rights.list"));
                HelpUtils.help(sender, alias, "help.commands.rights.clear",
                        new Permission("ultimatezones.commands.rights.clear"));
                HelpUtils.help(sender, alias, "help.commands.rights.flags",
                        new Permission("ultimatezones.commands.rights.flags"));
            }
        }

        return true;
    }

    // /rights edit flag <Flag> <Player> [<Zone>]
    private void editFlag(CommandSender sender, String[] args) {
        AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 5 ? args[4] : null);
        if (abstractZone != null) {
            Flag flag = Flag.fromString(args[2]);
            if (flag != null) {
                if (sender.hasPermission("ultimatezones.flags." + flag.name().toLowerCase())) {
                    OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[3]);
                    if (offlinePlayer != null) {
                        String synonym = abstractZone.hasSynonym() ? abstractZone.getSynonym()
                                : String.valueOf(abstractZone.getId());

                        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && abstractZone
                                .hasRight((Player) sender, Flag.AdministrateRights).rightEnum == RightEnum.Success)) {
                            String phrase = Locale.plain(sender,
                                    abstractZone.getRights(offlinePlayer, true).setFlag(flag) ? "shared.added"
                                            : "shared.withdrawn");
                            Locale.json(sender, "commands.rights.edit.flag.success", offlinePlayer.getName(),
                                    flag.name(), synonym, abstractZone.getId(), abstractZone.getPlayer().getName(),
                                    phrase);
                        } else {
                            Locale.json(sender, "missingrights.general", Flag.AdministrateRights.name(), synonym,
                                    abstractZone.getId(), abstractZone.getPlayer().getName());
                        }
                    } else {
                        Locale.sendPlain(sender, "commands.rights.edit.flag.unknownplayer", args[3]);
                    }
                } else {
                    Locale.sendPlain(sender, "commands.rights.edit.flag.flagrights", flag.name());
                }
            } else {
                Locale.sendPlain(sender, "commands.rights.edit.flag.unknownflag", args[2]);
            }
        } else {
            Locale.sendPlain(sender, "shared.zonenotfound");
        }
    }

    // /rights edit place/break <Block> <Player> [<Zone>]
    private void editBlock(CommandSender sender, String[] args) {
        AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 5 ? args[4] : null);
        if (abstractZone != null) {
            Material material = UltimateUtils.lookupMaterial(args[2]);
            if (material != null) {
                OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[3]);
                if (offlinePlayer != null) {
                    if (sender instanceof ConsoleCommandSender || (sender instanceof Player && abstractZone
                            .hasRight((Player) sender, Flag.AdministrateRights).rightEnum == RightEnum.Success)) {
                        String synonym = abstractZone.hasSynonym() ? abstractZone.getSynonym()
                                : String.valueOf(abstractZone.getId());
                        ItemStack itemStack = new ItemStack(material);
                        boolean doneSomething = false;

                        if (args[1].equalsIgnoreCase("place") || args[1].equalsIgnoreCase("both")
                                || args[1].equalsIgnoreCase("*")) {
                            String phrase = Locale.plain(sender,
                                    abstractZone.getRights(offlinePlayer, true).setPlace(material) ? "shared.added"
                                            : "shared.withdrawn");
                            Locale.json(sender, "commands.rights.edit.material.success",
                                    NMSHelper.getMojangIdentifierName(itemStack),
                                    NMSHelper.getMojangTranslatableName(itemStack), "place", offlinePlayer.getName(),
                                    synonym, abstractZone.getId(), abstractZone.getPlayer().getName(), phrase);

                            doneSomething = true;
                        }

                        if (args[1].equalsIgnoreCase("break") || args[1].equalsIgnoreCase("both")
                                || args[1].equalsIgnoreCase("*")) {
                            String phrase = Locale.plain(sender,
                                    abstractZone.getRights(offlinePlayer, true).setBreak(material) ? "shared.added"
                                            : "shared.withdrawn");
                            Locale.json(sender, "commands.rights.edit.material.success",
                                    NMSHelper.getMojangIdentifierName(itemStack),
                                    NMSHelper.getMojangTranslatableName(itemStack), "place", offlinePlayer.getName(),
                                    synonym, abstractZone.getId(), abstractZone.getPlayer().getName(), phrase);

                            doneSomething = true;
                        }

                        if (!doneSomething) {
                            Locale.sendPlain(sender, "commands.rights.edit.block.unknownaction", args[1]);
                        }
                    } else {
                        Locale.json(sender, "missingrights.general", Flag.AdministrateRights.name(),
                                abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                                abstractZone.getId(), abstractZone.getPlayer().getName());
                    }
                } else {
                    Locale.sendPlain(sender, "commands.rights.edit.block.unknownplayer", args[3]);
                }
            } else {
                Locale.sendPlain(sender, "commands.rights.edit.block.unknownmaterial", args[2]);
            }
        } else {
            Locale.sendPlain(sender, "shared.zonenotfound");
        }
    }

    // copy zone <Zone1> <Zone2> [<Player>]
    private void copyZone(CommandSender sender, String[] args) {
        AbstractZone fromZone = UltimateUtils.lookupZone(sender, args[2]);
        if (fromZone != null) {
            AbstractZone toZone = UltimateUtils.lookupZone(sender, args[3]);
            if (toZone != null) {
                if (args.length == 4) {
                    if (sender instanceof ConsoleCommandSender || (sender instanceof Player && toZone
                            .hasRight((Player) sender, Flag.AdministrateRights).rightEnum == RightEnum.Success)) {
                        OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[3]);
                        if (offlinePlayer != null) {
                            RightReference fromReference = fromZone.getRights(offlinePlayer);
                            if (fromReference != null) {
                                toZone.getRights(offlinePlayer, true).apply(fromReference);
                                Locale.json(sender, "commands.rights.copy.zone.player.success", offlinePlayer.getName(),
                                        fromZone.hasSynonym() ? fromZone.getSynonym() : fromZone.getId(),
                                        fromZone.getId(), fromZone.getPlayer().getName(),
                                        toZone.hasSynonym() ? toZone.getSynonym() : toZone.getId(), toZone.getId(),
                                        toZone.getPlayer().getName());
                            } else {
                                Locale.json(sender, "commands.rights.copy.zone.error.playerrights",
                                        offlinePlayer.getName(),
                                        fromZone.hasSynonym() ? fromZone.getSynonym() : fromZone.getId(),
                                        fromZone.getId(), fromZone.getPlayer().getName());
                            }
                        } else {
                            Locale.sendPlain(sender, "commands.rights.copy.zone.unknownplayer", args[3]);
                        }
                    } else {
                        Locale.json(sender, "missingrights.general", Flag.AdministrateRights.name(),
                                toZone.hasSynonym() ? toZone.getSynonym() : toZone.getId(), toZone.getId(),
                                toZone.getPlayer().getName());
                    }
                } else {
                    if (sender instanceof ConsoleCommandSender || toZone.isOwner(sender)
                            || sender.hasPermission("ultimatezones.admin")) {
                        toZone.apply(fromZone);
                        Locale.json(sender, "commands.rights.copy.zone.zone.success",
                                fromZone.hasSynonym() ? fromZone.getSynonym() : fromZone.getId(), fromZone.getId(),
                                fromZone.getPlayer().getName());
                    } else {
                        Locale.sendPlain(sender, "commands.rights.copy.zone.owneronly");
                    }
                }
            } else {
                Locale.sendPlain(sender, "commands.rights.copy.zone.unknownzone", args[3]);
            }
        } else {
            Locale.sendPlain(sender, "commands.rights.copy.zone.unknownzone", args[2]);
        }
    }

    // copy player <Player1> <Player2> [<Zone>]
    private void copyPlayer(CommandSender sender, String[] args) {
        OfflinePlayer fromPlayer = UltimateUtils.getPlayer(args[1]);
        if (fromPlayer != null) {
            OfflinePlayer toPlayer = UltimateUtils.getPlayer(args[2]);
            if (toPlayer != null) {
                AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length == 4 ? args[3] : null);
                if (abstractZone != null) {
                    RightReference fromReference = abstractZone.getRights(fromPlayer);
                    if (fromReference != null) {
                        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && abstractZone
                                .hasRight((Player) sender, Flag.AdministrateRights).rightEnum == RightEnum.Success)) {
                            abstractZone.getRights(toPlayer, true).apply(fromReference);
                            Locale.plain(sender, "commands.rights.copy.player.success", toPlayer.getName(),
                                    fromPlayer.getName());
                        } else {
                            Locale.json(sender, "missingrights.general", Flag.AdministrateRights.name(),
                                    abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                                    abstractZone.getId(), abstractZone.getPlayer().getName());
                        }
                    } else {
                        Locale.json(sender, "commands.rights.copy.player.error.playerrights", fromPlayer.getName(),
                                abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                                abstractZone.getId(), abstractZone.getPlayer().getName());
                    }
                } else {
                    Locale.sendPlain(sender, "shared.zonenotfound");
                }
            } else {
                Locale.sendPlain(sender, "commands.rights.copy.player.unknownplayer", args[2]);
            }
        } else {
            Locale.sendPlain(sender, "commands.rights.copy.player.unknownplayer", args[1]);
        }
    }

    // /rights list [<Zone>] [<Player>]
    private void list(CommandSender sender, String[] args) {
        AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length >= 2 ? args[1] : null);
        if (abstractZone != null) {
            if (args.length == 3) {
                OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[2]);
                if (offlinePlayer != null) {
                    RightReference rightReference = abstractZone.getRights(offlinePlayer);
                    if (rightReference != null) {
                        Locale.json(sender, "commands.rights.list.player", offlinePlayer.getName(),
                                abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                                abstractZone.getId(), abstractZone.getPlayer().getName());

                        ChatComponent chatComponent = UltimateUtils.generateRightsInfo(sender, rightReference);
                        chatComponent.to(sender);
                    } else {
                        Locale.json(sender, "commands.rights.list.playerrights", offlinePlayer.getName(),
                                abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                                abstractZone.getId(), abstractZone.getPlayer().getName());
                    }
                } else {
                    Locale.sendPlain(sender, "commands.rights.list.unknownplayer", args[2]);
                }
            } else {
                List<RightReference> rightList = abstractZone.getRights();
                if (rightList.size() > 0) {
                    Locale.json(sender, "commands.rights.list.global", rightList.size(),
                            abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                            abstractZone.getId(), abstractZone.getPlayer().getName());

                    ChatComponent chatComponent = new ChatComponent();
                    for (RightReference rightReference : rightList) {
                        chatComponent.last().then(rightReference.getPlayer().getName(), ChatColor.GOLD)
                                .click(Click.RunCommand, "/uzone rights list " + rightReference.getPlayer().getName())
                                .then(", ", ChatColor.GRAY);
                    }

                    chatComponent.last().delete().to(sender);
                } else {
                    Locale.sendPlain(sender, "commands.rights.list.global.empty");
                }
            }
        } else {
            Locale.sendPlain(sender, "shared.zonenotfound");
        }
    }

    // /rights clear [<Zone>] [<Player>]
    private void clear(CommandSender sender, String[] args) {
        AbstractZone abstractZone = UltimateUtils.lookupZone(sender, args.length >= 2 ? args[1] : null);
        if (abstractZone != null) {
            if (args.length == 3) {
                OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[2]);
                if (offlinePlayer != null) {
                    RightReference rightReference = abstractZone.getRights(offlinePlayer);
                    if (rightReference != null) {
                        rightReference.delete();
                        Locale.json(sender, "commands.rights.clear.global", offlinePlayer.getName(),
                                abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                                abstractZone.getId(), abstractZone.getPlayer().getName());
                    } else {
                        Locale.json(sender, "commands.rights.clear.playerrights", offlinePlayer.getName(),
                                abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                                abstractZone.getId(), abstractZone.getPlayer().getName());
                    }
                } else {
                    Locale.sendPlain(sender, "commands.rights.clear.unknownplayer", args[2]);
                }
            } else {
                int deletedRights = abstractZone.getRights().size();
                abstractZone.clearRights(false);
                Locale.json(sender, "commands.rights.clear.global", deletedRights,
                        abstractZone.hasSynonym() ? abstractZone.getSynonym() : abstractZone.getId(),
                        abstractZone.getId(), abstractZone.getPlayer().getName());
            }
        } else {
            Locale.sendPlain(sender, "shared.zonenotfound");
        }
    }

    // /rights flags
    private void flags(CommandSender sender, String[] args) {
        Flag[] flagList = Flag.values();

        Locale.sendPlain(sender, "commands.rights.flags", flagList.length);
        ChatComponent chatComponent = new ChatComponent();
        for (Flag flag : flagList) {
            if (!sender.hasPermission("ultimatezones.flags." + flag.name().toLowerCase()))
                continue;

            chatComponent.last().then(flag.typeName, ChatColor.GOLD).hover(
                    net.dertod2.UltimateZones.Classes.Locale.plain(sender, "flags." + flag.typeName.toLowerCase()))
                    .then(", ", ChatColor.GRAY);
        }

        chatComponent.last().delete().to(sender);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return UltimateUtils.tabCompleteList(args[0], this.tabCategoryMap, sender);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            return UltimateUtils.tabCompleteList(args[1], this.tabEditMap, sender);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("copy")) {
            return UltimateUtils.tabCompleteList(args[1], this.tabCopyMap, sender);
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("clear"))) {
            return UltimateUtils.findZoneMatches(sender, args[1]);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("flag")) {
            List<String> flagList = new ArrayList<String>(Flag.values().length);
            for (Flag flag : Flag.values())
                flagList.add(flag.name());
            return UltimateUtils.tabCompleteList(args[2], flagList);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("edit")
                && (args[1].equalsIgnoreCase("place") || args[1].equalsIgnoreCase("break"))) {
            List<String> materialList = new ArrayList<String>(Material.values().length);
            for (Material material : Material.values())
                materialList.add(material.name());
            return UltimateUtils.tabCompleteList(args[2], materialList);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("copy") && args[1].equalsIgnoreCase("zone")) {
            return UltimateUtils.findZoneMatches(sender, args[2]);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("copy") && args[1].equalsIgnoreCase("player")) {
            return null;
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("clear"))) {
            return null;
        } else if (args.length == 4 && (args[0].equalsIgnoreCase("edit")
                || (args[0].equalsIgnoreCase("copy") && args[1].equalsIgnoreCase("player")))) {
            return null;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("copy") && args[1].equalsIgnoreCase("zone")) {
            return UltimateUtils.findZoneMatches(sender, args[3]);
        } else if (args.length == 5 && (args[0].equalsIgnoreCase("edit")
                || (args[0].equalsIgnoreCase("copy") && args[1].equalsIgnoreCase("player")))) {
            return UltimateUtils.findZoneMatches(sender, args[4]);
        } else if (args.length == 5 && args[0].equalsIgnoreCase("copy") && args[1].equalsIgnoreCase("zone")) {
            return null;
        }

        return ImmutableList.of();
    }
}