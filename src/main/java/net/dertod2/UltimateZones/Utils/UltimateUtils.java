package net.dertod2.UltimateZones.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.IRights;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Classes.Zones.MainZone;
import net.dertod2.UltimateZones.Classes.Zones.SubZone;

@SuppressWarnings("deprecation")
public class UltimateUtils {

    public static Material getItemType(Hanging hanging) {
        if (hanging instanceof Painting)
            return Material.PAINTING;
        else if (hanging instanceof LeashHitch)
            return Material.LEGACY_LEASH;
        else if (hanging instanceof ItemFrame)
            return Material.ITEM_FRAME;
        else
            return null;
    }

    public static Player getAttacker(Entity entity) {
        if (entity instanceof Player)
            return (Player) entity;

        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            if (projectile.getShooter() instanceof Player)
                return (Player) projectile.getShooter();
        }

        return null;
    }

    /**
     * Returns the Player Object (when online) or the OfflinePlayer Object<br />
     * when not online but played at least once on the Server
     * 
     * @param playerName
     *            The Playername
     * @return The searched Player or NULL
     */
    public static OfflinePlayer getPlayer(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null)
            return player;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer != null && offlinePlayer.hasPlayedBefore())
            return offlinePlayer;

        return null;
    }

    /**
     * Tries to lookup an Zone <br />
     * Case when Sender is Console and no name given: NULL Return<br />
     * Case when Sender is Player and no name given: Zone at current Location or
     * NULL<br />
     * Case when Sender is Console or Player and name given: <br />
     * -> Name contains ':' (PlayerName:ZoneSynonym) -> return when found else
     * null<br />
     * -> Without ':': Console -> NULL; Player -> try to lookup over own zones and
     * Synonym
     * 
     * @param sender
     * @param optionalName
     * @return The found Zone or NULL
     */
    public static AbstractZone lookupZone(CommandSender sender, String optionalName) {
        if (optionalName != null && UltimateUtils.isInteger(optionalName)) {
            return AbstractZone.getZone(Integer.parseInt(optionalName));
        }

        if (optionalName == null) {
            if (sender instanceof Player) {
                return AbstractZone.getZone(((Player) sender).getLocation());
            } else {
                return null;
            }
        }

        if (optionalName.contains(":")) {
            String[] name = optionalName.split(":");
            OfflinePlayer owner = UltimateUtils.getPlayer(name[0]);
            if (owner != null)
                return AbstractZone.getZone(owner, name[1]);

            return null;
        } else {
            if (sender instanceof Player) {
                return AbstractZone.getZone((Player) sender, optionalName);
            } else {
                return null;
            }
        }
    }

    /**
     * Lookups the {@link Material} out of an String.<br />
     * Supports the Bukkit Material name and Mojang Identifiers
     * 
     * @param name
     * @return The found Material or NULL
     */
    public static Material lookupMaterial(String name) {
        if (UltimateUtils.isInteger(name)) {
            throw new IllegalArgumentException("IDs are not supported anymore");
            //return Material.getMaterial(Integer.parseInt(name));
        } else {
            Material material = Material.getMaterial(name);

            if (material == null)
                material = Material.matchMaterial(name);
            if (material == null)
                material = NMSHelper.lookupMojangItem(name);
            if (material == null)
                material = NMSHelper.lookupMojangBlock(name);

            return material;
        }
    }

    /**
     * Finds all Zones with synonyms owned by the caller or all zones with synonyms
     * by the given Player
     * 
     * @param sender
     * @param arg
     * @return
     */
    public static List<String> findZoneMatches(CommandSender sender, String arg) {
        if (arg.contains(":")) {
            String[] args = arg.split(":");
            OfflinePlayer offlinePlayer = UltimateUtils.getPlayer(args[0]);
            if (offlinePlayer == null)
                return ImmutableList.of();

            List<String> list = new ArrayList<String>();
            for (String synonym : AbstractZone.getSynonyms(offlinePlayer))
                list.add(offlinePlayer.getName() + synonym);
            return list;
        } else {
            if (sender instanceof Player) {
                List<String> list = new ArrayList<String>();
                for (String synonym : AbstractZone.getSynonyms((Player) sender))
                    list.add(sender.getName() + synonym);
                return list;
            } else {
                return ImmutableList.of();
            }
        }
    }

    public static int getChildZones(List<MainZone> zoneList) {
        int childs = 0;

        for (MainZone mainZone : zoneList) {
            childs += mainZone.getZones(true).size();
        }

        return childs;
    }

    public static boolean equalsHandItem(Player player, MaterialData materialData) {
        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            return materialData == null || materialData.getItemType() == Material.AIR;
        } else {
            return materialData.equals(player.getItemInHand().getData());
        }
    }

    public static boolean isCorrectFeedingItem(Ageable ageable, ItemStack itemStack) {
        if (itemStack == null)
            return false;
        Material material = itemStack.getType();
        if (material == null || material == Material.AIR)
            return false;

        if (ageable instanceof Chicken) {
            if (material == Material.LEGACY_SEEDS)
                return true;
        } else if (ageable instanceof Cow) {
            if (material == Material.WHEAT)
                return true;
        } else if (ageable instanceof Horse) {
            if (material == Material.GOLDEN_CARROT)
                return true;
            if (material == Material.GOLDEN_APPLE)
                return true;
        } else if (ageable instanceof MushroomCow) {
            if (material == Material.WHEAT)
                return true;
        } else if (ageable instanceof Ocelot) {
            if (material == Material.LEGACY_RAW_FISH)
                return true;
        } else if (ageable instanceof Pig) {
            if (material == Material.CARROT)
                return true; // TODO -> Carrot Item?
        } else if (ageable instanceof Rabbit) {
            if (material == Material.CARROT)
                return true;
            if (material == Material.GOLDEN_CARROT)
                return true;
            if (material == Material.LEGACY_YELLOW_FLOWER)
                return true; // TODO Löwenzahn
        } else if (ageable instanceof Sheep) {
            if (material == Material.WHEAT)
                return true;
        } else if (ageable instanceof Wolf) {
            if (material == Material.LEGACY_RAW_BEEF)
                return true;
            if (material == Material.LEGACY_PORK)
                return true; // TODO -> correct ?
            if (material == Material.LEGACY_RAW_CHICKEN)
                return true;
            if (material == Material.COOKED_CHICKEN)
                return true;
            if (material == Material.COOKED_BEEF)
                return true;
            if (material == Material.ROTTEN_FLESH)
                return true;
        }

        return false;
    }

    public static boolean containsIgnoreCase(List<String> stringList, String entry) {
        for (String string : stringList) {
            if (string.equalsIgnoreCase(entry))
                return true;
        }

        return false;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String formattedArea(double area, Locale locale) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);

        if (area <= 1000000)
            return decimalFormat.format(area) + "m²";
        area = area * 0.001; // Kilometers

        return decimalFormat.format(area) + "km²";
    }

    public static String formattedVolume(double volume, Locale locale) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);

        if (volume <= 1000000)
            return decimalFormat.format(volume) + "m³";
        volume = volume * 0.001; // Kilometers

        return decimalFormat.format(volume) + "km³";
    }

    public static void saveRessource(String ressource, File target) throws IOException {
        InputStream inputStream = UltimateZones.getInstance().getResource(ressource);
        FileOutputStream fileOutputStream = new FileOutputStream(target);

        IOUtils.copy(inputStream, fileOutputStream);

        inputStream.close();
        fileOutputStream.close();
    }

    public static void extractRessources(File targetFolder, String... ressources) throws IOException {
        targetFolder.mkdirs();

        for (String ressource : ressources) {
            File target = new File(targetFolder, ressource.substring(ressource.lastIndexOf("/")));
            if (!target.exists())
                UltimateUtils.saveRessource(ressource, target);
        }
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[UltimateZones] " + ChatColor.RED + message);
    }

    public static void notify(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[UltimateZones] " + ChatColor.DARK_GREEN + message);
    }

    /**
     * Shorter grabber for
     * {@link StringUtil#copyPartialMatches(String, Iterable, java.util.Collection)}
     * 
     * @param token
     * @param originals
     * @return
     */
    public static List<String> tabCompleteList(final String token, final List<String> originals) {
        return StringUtil.copyPartialMatches(token, originals, new ArrayList<String>(originals.size()));
    }

    /**
     * Extended version of {@link UltimateUtils#tabCompleteList(String, List)} with
     * support for Permissions
     * 
     * @param token
     * @param originals
     * @param permissible
     * @return
     */
    public static List<String> tabCompleteList(final String token, final Map<String, String> originals,
            final Permissible permissible) {
        List<String> collection = new ArrayList<String>(originals.size());

        for (Entry<String, String> entry : originals.entrySet()) {
            if (entry.getValue().length() == 0 || permissible.hasPermission(entry.getValue())) {
                if (StringUtil.startsWithIgnoreCase(entry.getKey(), token)) {
                    collection.add(entry.getKey());
                }
            }
        }

        return collection;
    }

    public static ChatComponent generateRightsInfo(CommandSender sender, IRights iRights) {
        ChatComponent chatComponent = new ChatComponent(
                net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.flags"), ChatColor.DARK_GREEN)
                        .newline();
        for (Flag flag : iRights.getFlagList()) {
            chatComponent.last().then(flag.typeName, ChatColor.GOLD).hover(
                    net.dertod2.UltimateZones.Classes.Locale.plain(sender, "flags." + flag.typeName.toLowerCase()))
                    .then(", ", ChatColor.GRAY);
        }

        chatComponent.last().newline();

        boolean placeWhiteList = iRights.getFlagList().contains(Flag.PlaceAsWhiteList);
        boolean breakWhiteList = iRights.getFlagList().contains(Flag.BreakAsWhiteList);

        List<Material> placeList = iRights.getPlaceList();
        List<Material> breakList = iRights.getBreakList();

        if (placeWhiteList) {
            chatComponent.last().then(
                    net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.place.a"),
                    ChatColor.DARK_GREEN);
        } else {
            chatComponent.last().then(
                    net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.place.b"),
                    ChatColor.DARK_GREEN);
        }

        if (placeList.isEmpty()) {
            if (placeWhiteList) {
                chatComponent.last().then(
                        net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.denied"),
                        ChatColor.GREEN);
            } else {
                chatComponent.last().then(
                        net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.allowed"),
                        ChatColor.GREEN);
            }
        } else {
            chatComponent.last().newline();
            for (Material material : placeList) {
                chatComponent.last().then(new ItemStack(material), ChatColor.GOLD).then(", ");
            }
        }

        if (breakWhiteList) {
            chatComponent.last().then(
                    net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.break.a"),
                    ChatColor.DARK_GREEN);
        } else {
            chatComponent.last().then(
                    net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.break.b"),
                    ChatColor.DARK_GREEN);
        }

        if (breakList.isEmpty()) {
            if (breakWhiteList) {
                chatComponent.last().then(
                        net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.denied"),
                        ChatColor.GREEN);
            } else {
                chatComponent.last().then(
                        net.dertod2.UltimateZones.Classes.Locale.plain(sender, "shared.rightsinfo.allowed"),
                        ChatColor.GREEN);
            }
        } else {
            chatComponent.last().newline();
            for (Material material : breakList) {
                chatComponent.last().then(new ItemStack(material), ChatColor.GOLD).then(", ");
            }
        }

        return chatComponent;
    }

    public static int getRecursiveZoneDepth(AbstractZone checkZone) {
        if (checkZone instanceof MainZone)
            return 0;

        int depth = 1;
        AbstractZone abstractZone = ((SubZone) checkZone).getParent();
        while (abstractZone instanceof SubZone) {
            abstractZone = ((SubZone) abstractZone).getParent();
            depth++;
        }

        return depth;
    }

}