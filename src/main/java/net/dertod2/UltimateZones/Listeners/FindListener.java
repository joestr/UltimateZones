package net.dertod2.UltimateZones.Listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Classes.Zones.MainZone;
import net.dertod2.UltimateZones.Classes.Zones.SubZone;
import net.dertod2.UltimateZones.Commands.FindCommand;

public class FindListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!FindCommand.findList.contains(event.getPlayer().getUniqueId())) return;
		
		if (UltimateZones.findItem == null || UltimateZones.findItem.getType() == Material.AIR || UltimateZones.findItem.getType() == event.getMaterial()) {
			event.setCancelled(true);
			
			AbstractZone abstractZone = AbstractZone.getZone(event.getClickedBlock().getLocation());
			if (abstractZone != null) {
				String synonym = abstractZone.hasSynonym() ? abstractZone.getSynonym() : String.valueOf(abstractZone.getId());
				
				if (abstractZone instanceof SubZone) {
					MainZone mainZone = ((SubZone) abstractZone).getMain();
					Locale.json(event.getPlayer(), "find.subzone", synonym, abstractZone.getId(), abstractZone.getPlayer().getName(), mainZone.hasSynonym() ? mainZone.getSynonym() : mainZone.getId(), mainZone.getId(), mainZone.getPlayer().getName());
				} else {
					Locale.json(event.getPlayer(), "find.mainzone", synonym, abstractZone.getId(), abstractZone.getPlayer().getName());
				}
			} else {
				Locale.sendPlain(event.getPlayer(), "find.null");
			}
		}
	}
}