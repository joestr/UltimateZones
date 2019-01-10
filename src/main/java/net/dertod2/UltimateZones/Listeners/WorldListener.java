package net.dertod2.UltimateZones.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import net.dertod2.UltimateZones.Binary.UltimateZones;

public class WorldListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
        UltimateZones.worldConfigControl.loadWorld(event.getWorld());
    }
}