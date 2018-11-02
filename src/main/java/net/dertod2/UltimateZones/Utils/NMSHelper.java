package net.dertod2.UltimateZones.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.minecraft.server.v1_13_R2.Block;
import net.minecraft.server.v1_13_R2.Item;
import net.minecraft.server.v1_13_R2.MinecraftKey;

public class NMSHelper {

    public static String getMojangTranslatableName(ItemStack itemStack) {
        String foundType = null;

        net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsStack == null) {
            net.minecraft.server.v1_13_R2.Item item = CraftMagicNumbers.getItem(itemStack.getType());
            if (item == null) {
                net.minecraft.server.v1_13_R2.Block block = CraftMagicNumbers.getBlock(itemStack.getType());
                if (block != null) {
                    try {
                        foundType = net.minecraft.server.v1_13_R2.LocaleLanguage.class.newInstance().a(block.a() + ".name");
                    } catch (InstantiationException | IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    foundType = net.minecraft.server.v1_13_R2.LocaleLanguage.class.newInstance().a(item.getName() + ".name");
                } catch (InstantiationException | IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            foundType = nmsStack.getItem().h(nmsStack) + ".name";
        }

        return foundType;
    }

    public static String getMojangIdentifierName(ItemStack itemStack) {
        String foundType = null;

        net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsStack == null) {
            net.minecraft.server.v1_13_R2.Item item = CraftMagicNumbers.getItem(itemStack.getType());
            if (item == null) {
                net.minecraft.server.v1_13_R2.Block block = CraftMagicNumbers.getBlock(itemStack.getType());
                if (block != null) {
                    foundType = String.valueOf(Block.REGISTRY_ID.getId(block.getBlockData()));
                }
            } else {
                foundType = String.valueOf(Item.getId(item));
            }
        } else {
            foundType = String.valueOf(Item.getId(nmsStack.getItem()));
        }

        return foundType;
    }

    /**
     * Lookups Mojangs items to Bukkit Materials
     * 
     * @param itemName
     * @return Material
     */
    public static Material lookupMojangItem(String itemName) {
        //return CraftMagicNumbers.getMaterial(Item.REGISTRY.get(new MinecraftKey(itemName)));
        return Material.matchMaterial(itemName);
    }

    /**
     * Lookups Mojangs blocks to Bukkit Materials
     * 
     * @param itemName
     * @return Material
     */
    public static Material lookupMojangBlock(String itemName) {
        //return CraftMagicNumbers.getMaterial(Block.REGISTRY.get(new MinecraftKey(itemName)));
        return Material.matchMaterial(itemName);
    }

    public static boolean registerCommand(String command, Plugin plugin, CommandExecutor commandExecutor,
            String... aliases) {
        try {
            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            CommandMap commandMap = craftServer.getCommandMap();

            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class,
                    Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCommand = constructor.newInstance(command, plugin);
            pluginCommand.setExecutor(commandExecutor);
            if (aliases.length > 0)
                pluginCommand.setAliases(Arrays.asList(aliases));

            return commandMap.register(command, UltimateZones.getInstance().getName(), pluginCommand);
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
    }

    public static boolean unregisterCommand(String command, Plugin plugin) {
        try {
            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            SimpleCommandMap commandMap = craftServer.getCommandMap();

            Field field = commandMap.getClass().getDeclaredField("knownCommands");
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) field.get(commandMap);

            knownCommands.remove(command);
            Command unregisteredCommand = knownCommands.remove(plugin.getName().toLowerCase().trim() + ":" + command);

            if (unregisteredCommand != null)
                unregisteredCommand.unregister(commandMap);

            return true;
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches the name over http reuqests
     * 
     * @param uuid
     * @return
     */
    public static OfflinePlayer lookupPlayer(UUID uuid) {
        CraftServer craftServer = (CraftServer) Bukkit.getServer();

        try {
            URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(new InputStreamReader(urlConnection.getInputStream()));

                JsonArray jsonArray = jsonElement.getAsJsonArray();
                Iterator<JsonElement> iterator = jsonArray.iterator();

                String username = null;
                while (iterator.hasNext()) {
                    JsonObject jsonObject = iterator.next().getAsJsonObject();
                    username = jsonObject.get("name").getAsString();
                }

                if (username != null) {
                    GameProfile gameProfile = new GameProfile(uuid, username);

                    craftServer.getHandle().getServer().getUserCache().a(gameProfile);
                    UltimateUtils.notify("Lookup of UUID " + ChatColor.GOLD + uuid.toString() + ChatColor.DARK_GREEN
                            + " to username " + ChatColor.GOLD + username + ChatColor.DARK_GREEN
                            + " succesfully. Saved in usercache.json!");

                    return craftServer.getOfflinePlayer(gameProfile);
                } else {
                    UltimateUtils.error("Failed to lookup UUID " + ChatColor.GOLD + uuid.toString());
                }
            }
        } catch (Exception exc) {
            UltimateUtils.error("Fatal error while lookup UUID " + ChatColor.GOLD + uuid.toString());
            Bukkit.shutdown();
        }

        return null;
    }
}