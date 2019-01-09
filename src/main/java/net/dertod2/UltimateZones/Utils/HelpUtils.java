package net.dertod2.UltimateZones.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Utils.ChatComponent.Click;
import net.dertod2.UltimateZones.Utils.ChatComponent.Hover;

public class HelpUtils {

    /**
     * Allows to send special modified Help Messages<br />
     * There are two different methods to allow one global help message and one too
     * define an help message for each argument<br />
     * This is the first method.
     * 
     * @param sender
     *            The Sender which should receive this help message
     * @param alias
     *            The alias of the used command
     * @param phrase
     *            The Phrase of the Command Help
     */
    public static void help(CommandSender sender, String alias, String phrase) {
        HelpUtils.help(sender, alias, phrase, null);
    }

    /**
     * Allows to send special modified Help Messages<br />
     * There are two different methods to allow one global help message and one too
     * define an help message for each argument<br />
     * This is the second method.
     * 
     * @param sender
     *            The Sender which should receive this help message
     * @param alias
     *            The alias of the used command
     * @param phrase
     *            The Phrase of the Command Help
     * @param permission
     *            The Permission to check. If the sender hasn't the permission the
     *            message won't be sent
     */
    public static void help(CommandSender sender, String alias, String phrase, Permission permission) {
        if (permission != null && !sender.hasPermission(permission))
            return;

        Locale locale = sender instanceof Player ? Locale.get((Player) sender) : Locale.get(sender);

        ChatComponent chatComponent = HelpUtils.parse(locale, alias, phrase);
        chatComponent.to(sender);
    }

    private static String getCommandToArgument(String label, String command) {
        String commandLine = "/" + label.trim() + " ";

        int indexOf = command.indexOf("<");
        if (indexOf == -1 || (command.indexOf("[") != -1 && command.indexOf("[") < indexOf))
            indexOf = command.indexOf("[");

        return (commandLine + command.substring(0, indexOf != -1 ? indexOf : command.length()).trim());
    }

    // RegEx: (\s[a-zA-Z]*\s)|(\[[a-zA-Z\s]*\])|(\[<[a-zA-Z\s]*\>])|(<[a-zA-Z\s]*>)
    private static ChatComponent parse(Locale locale, String alias, String phrase) {
        String commandLine = " " + locale.parse("plain", phrase) + " "; // ugly fix for missing first argument :/ hate
                                                                        // regex >.<
        String clickLine = HelpUtils.getCommandToArgument(alias, commandLine);

        Pattern pattern = Pattern.compile(
                "([^<][a-zA-Z0-9\\/]*\\s)|(\\[[a-zA-Z0-9\\/\\s]*\\])|(\\[\\<[a-zA-Z0-9\\/\\s]*\\>\\])|(<[a-zA-Z0-9\\/\\s]*>)"); // Pattern.compile("(\\s[a-zA-Z]*\\s)|(\\[[a-zA-Z\\s]*\\])|(\\[<[a-zA-Z\\s]*\\>])|(<[a-zA-Z\\s]*>)");

        Matcher matcher = pattern.matcher(commandLine);

        ChatComponent chatComponent = new ChatComponent("/" + alias, ChatColor.DARK_GRAY);
        chatComponent.hover(Hover.ShowText, new ChatComponent("/" + alias + " " + commandLine, ChatColor.AQUA).newline()
                .then(locale.parse("plain", phrase + ".descriptions.main"), ChatColor.GRAY));
        chatComponent.click(Click.SuggestCommand, clickLine);

        int descId = 0;
        while (matcher.find()) {
            String argument = matcher.group().trim();

            String description = locale.parse("plain", phrase + ".descriptions." + ++descId);
            if (description == null || description.length() == 0)
                description = locale.parse("plain", phrase + ".descriptions.main"); // TODO <- use main description?

            if (argument.startsWith("<")) { // Importante
                chatComponent.last().then(" ").then(argument, ChatColor.RED)
                        .hover(Hover.ShowText,
                                new ChatComponent(argument, ChatColor.AQUA).newline()
                                        .then(locale.parse("plain", "shared.help.commands.static.important"),
                                                ChatColor.DARK_GRAY)
                                        .newline().then(description, ChatColor.GRAY))
                        .click(Click.SuggestCommand, clickLine);
            } else if (argument.startsWith("[<")) { // Optionale
                chatComponent.last().then(" ").then(argument, ChatColor.DARK_GRAY)
                        .hover(Hover.ShowText,
                                new ChatComponent(argument, ChatColor.AQUA).newline()
                                        .then(locale.parse("plain", "shared.help.commands.static.optional"),
                                                ChatColor.DARK_GRAY)
                                        .newline().then(description, ChatColor.GRAY))
                        .click(Click.SuggestCommand, clickLine);
            } else if (argument.startsWith("[")) { // Importante
                chatComponent.last().then(" ").then(argument, ChatColor.DARK_BLUE)
                        .hover(Hover.ShowText,
                                new ChatComponent(argument, ChatColor.AQUA).newline()
                                        .then(locale.parse("plain", "shared.help.commands.static.important"),
                                                ChatColor.DARK_GRAY)
                                        .newline().then(description, ChatColor.GRAY))
                        .click(Click.SuggestCommand, clickLine);
            } else { // Normale
                chatComponent.last().then(" ").then(argument, ChatColor.WHITE)
                        .hover(Hover.ShowText,
                                new ChatComponent(argument, ChatColor.AQUA).newline()
                                        .then(locale.parse("plain", "shared.help.commands.static.important"),
                                                ChatColor.DARK_GRAY)
                                        .newline().then(description, ChatColor.GRAY))
                        .click(Click.SuggestCommand, clickLine);
            }
        }

        return chatComponent;
    }
}