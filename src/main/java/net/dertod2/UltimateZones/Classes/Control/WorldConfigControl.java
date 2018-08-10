package net.dertod2.UltimateZones.Classes.Control;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Utils.UltimateUtils;

public class WorldConfigControl {
	private Map<String, YamlConfiguration> worldList;
	
	public WorldConfigControl() {
		this.worldList = new HashMap<String, YamlConfiguration>();
		
		for (World world : Bukkit.getWorlds()) {
			this.loadWorld(world);
		}
	}
	
	public void loadWorld(World world) {
		File folder = new File(UltimateZones.getInstance().getDataFolder(), "worlds");
		if (!folder.exists()) folder.mkdirs();
			
		File file = new File(folder, world.getName() + ".yml");
		
		if (!file.exists()) {
			try {
				UltimateUtils.saveRessource("dummyWorld.yml", file);
			} catch (IOException exc) {
				UltimateUtils.error("Error while saving default world config for World " + ChatColor.GOLD + world.getName());
			} finally {
				UltimateUtils.notify(ChatColor.DARK_GREEN + "Saved world configuration for world " + ChatColor.GOLD + world.getName());
			}
		}
			
		this.worldList.put(world.getName(), YamlConfiguration.loadConfiguration(file));
	}
	
	public YamlConfiguration getWorld(World world) {
		return this.worldList.get(world.getName());
	}
	
	public YamlConfiguration getWorld(String worldName) {
		return this.worldList.get(worldName);
	}
}