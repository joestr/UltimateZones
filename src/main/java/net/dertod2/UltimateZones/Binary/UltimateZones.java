package net.dertod2.UltimateZones.Binary;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.dertod2.UltimateZones.Classes.DynmapHandler;
import net.dertod2.UltimateZones.Classes.Control.LocaleControl;
import net.dertod2.UltimateZones.Classes.Control.PresetControl;
import net.dertod2.UltimateZones.Classes.Control.RightControl;
import net.dertod2.UltimateZones.Classes.Control.WorldConfigControl;
import net.dertod2.UltimateZones.Classes.Control.ZoneControl;
import net.dertod2.UltimateZones.Commands.FindCommand;
import net.dertod2.UltimateZones.Commands.PresetCommand;
import net.dertod2.UltimateZones.Commands.RightsCommand;
import net.dertod2.UltimateZones.Commands.UZoneCommand;
import net.dertod2.UltimateZones.Commands.ZoneCommand;
import net.dertod2.UltimateZones.Listeners.*;
import net.dertod2.UltimateZones.Utils.NMSHelper;
import net.dertod2.UltimateZones.Utils.UltimateUtils;
import net.md_5.bungee.api.ChatColor;

public class UltimateZones extends JavaPlugin {
    private static UltimateZones ultimateZones;
    private static UUID serverUniqueId;

    public static LocaleControl localeControl;
    public static WorldConfigControl worldConfigControl;

    public static ZoneControl zoneControl;
    public static RightControl rightControl;
    public static PresetControl presetControl;

    // Defines if optional plugins are installed and loaded
    public static boolean isDynmapInstalled;

    // Different Settings or cached Objects
    public static ItemStack zoneItem;
    public static ItemStack findItem;

    public static Updater updater;

    public static Map<String, TabExecutor> commandMap; // Only to provide the UZone Shared Command

    public void onEnable() {
        this.saveDefaultConfig();

        UltimateZones.ultimateZones = this;
        UltimateZones.serverUniqueId = UUID.randomUUID();

        UltimateZones.localeControl = new LocaleControl();
        UltimateZones.worldConfigControl = new WorldConfigControl();

        PluginManager pluginManager = Bukkit.getPluginManager();

        UltimateZones.isDynmapInstalled = pluginManager.getPlugin("dynmap") != null;

        if (UltimateZones.isDynmapInstalled) {
            UltimateUtils.notify("Plugin detected installed " + ChatColor.GOLD + "dynmap" + ChatColor.DARK_GREEN
                    + ". Activated support!");
            new DynmapHandler();
        }

        String itemName = UltimateZones.getConfiguration().getString("zone-item", "minecraft:wooden_sword");
        UltimateZones.zoneItem = new ItemStack(NMSHelper.lookupMojangItem(itemName));

        itemName = UltimateZones.getConfiguration().getString("find-item", "minecraft:wooden_sword");
        UltimateZones.findItem = new ItemStack(NMSHelper.lookupMojangItem(itemName));

        UltimateZones.zoneControl = new ZoneControl();
        UltimateZones.zoneControl.load();
        UltimateZones.rightControl = new RightControl();
        UltimateZones.presetControl = new PresetControl();
        UltimateZones.presetControl.load();

        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new EntityListener(), this);
        pluginManager.registerEvents(new EventListener(), this);
        pluginManager.registerEvents(new FindListener(), this);
        pluginManager.registerEvents(new InteractListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new VehicleListener(), this);
        pluginManager.registerEvents(new WorldListener(), this);

        UltimateZones.commandMap = new HashMap<String, TabExecutor>();
        UltimateZones.commandMap.put("find", new FindCommand());
        UltimateZones.commandMap.put("preset", new PresetCommand());
        UltimateZones.commandMap.put("rights", new RightsCommand());
        UltimateZones.commandMap.put("zone", new ZoneCommand());
        getCommand("uzone").setExecutor(new UZoneCommand());

        if (UltimateZones.getConfiguration().getBoolean("split-commands", false)) {
            NMSHelper.registerCommand("find", this, UltimateZones.commandMap.get("find"));
            NMSHelper.registerCommand("preset", this, UltimateZones.commandMap.get("preset"));
            NMSHelper.registerCommand("rights", this, UltimateZones.commandMap.get("rights"));
            NMSHelper.registerCommand("zone", this, UltimateZones.commandMap.get("zone"));
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new DelayThread(), 100L, 100L);

        // Check for new updates
        UltimateZones.updater = new Updater(this.getFile());
        if (Updater.autoCheck)
            UltimateZones.updater.check(Bukkit.getConsoleSender());
    }

    public void onDisable() {
        if (UltimateZones.presetControl != null)
            UltimateZones.presetControl = null;
        if (UltimateZones.rightControl != null)
            UltimateZones.rightControl = null;
        if (UltimateZones.zoneControl != null)
            UltimateZones.zoneControl = null;

        HandlerList.unregisterAll(this);

        NMSHelper.unregisterCommand("find", this);
        NMSHelper.unregisterCommand("preset", this);
        NMSHelper.unregisterCommand("rights", this);
        NMSHelper.unregisterCommand("zone", this);

        Bukkit.getScheduler().cancelTasks(this);
        //UltimateZones.updater.delayCheckThread = null;
    }

    public static FileConfiguration getConfiguration() {
        return UltimateZones.ultimateZones.getConfig();
    }

    public static YamlConfiguration getConfiguration(World world) {
        return UltimateZones.worldConfigControl.getWorld(world);
    }

    public static YamlConfiguration getConfiguration(String worldName) {
        return UltimateZones.worldConfigControl.getWorld(worldName);
    }

    public static UltimateZones getInstance() {
        return UltimateZones.ultimateZones;
    }

    /**
     * The random unique ID of the server needed for some operations<br />
     * Will be re-generated each restart or reload
     * 
     * @return UUID
     */
    public static UUID getUniqueId() {
        return UltimateZones.serverUniqueId;
    }
}