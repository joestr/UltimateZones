package net.dertod2.UltimateZones.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.dertod2.UltimateZones.Binary.DelayThread;
import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.RightResult;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Classes.Zones.MainZone;
import net.dertod2.UltimateZones.Classes.Zones.SubZone;
import net.dertod2.UltimateZones.CreationWizard.AbstractCreation;
import net.dertod2.UltimateZones.Utils.MessageUtils;
import net.dertod2.ZonesLib.Events.ZoneInfoEvent;

public class PlayerListener implements Listener {
	private static Map<UUID, UUID> dropList = new HashMap<UUID, UUID>();
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (UltimateZones.updater.updateAvailable()) UltimateZones.updater.check(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		AbstractZone abstractZone = AbstractZone.getZone(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
		if (abstractZone == null) return;
		
		RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.Place, event.getBucket() == Material.WATER_BUCKET ? Material.WATER : Material.LAVA);
		if (rightResult.rightEnum != RightEnum.Success) {
			MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		AbstractZone abstractZone = AbstractZone.getZone(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
		if (abstractZone == null) return;
		
		if (event.getBucket() == Material.MILK_BUCKET) {
			RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractAnimals);
			if (rightResult.rightEnum != RightEnum.Success) {
				MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
				event.setCancelled(true);
			}
		} else {
			Material material = event.getBlockClicked().getRelative(event.getBlockFace()).getType();

			RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.Break, material == Material.WATER || material == Material.STATIONARY_WATER ? Material.WATER : Material.LAVA);
			if (rightResult.rightEnum != RightEnum.Success) {
				MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemDrop(PlayerDropItemEvent event) {
		AbstractZone abstractZone = AbstractZone.getZone(event.getItemDrop().getLocation());
		if (abstractZone == null) return;
		
		PlayerListener.dropList.put(event.getItemDrop().getUniqueId(), event.getPlayer().getUniqueId());
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onItemPickup(PlayerPickupItemEvent event) {
		AbstractZone abstractZone = AbstractZone.getZone(event.getItem().getLocation());
		if (abstractZone == null) return;
		
		UUID uuid = PlayerListener.dropList.get(event.getItem().getUniqueId());
		if (uuid != null && event.getPlayer().getUniqueId().compareTo(uuid) == 0) {
			dropList.remove(event.getItem().getUniqueId());
			return;
		}
		
		RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.PickupItems, event.getItem().getItemStack().getType());
		if (rightResult.rightEnum != RightEnum.Success) {
			if (!DelayThread.pickupList.contains(event.getPlayer().getUniqueId())) {			
				MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
				DelayThread.pickupList.add(event.getPlayer().getUniqueId());
			}
		
			event.setCancelled(true);
		} else {
			dropList.remove(event.getItem().getUniqueId());
			return;
		}		
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBedEnter(PlayerBedEnterEvent event) {
		AbstractZone abstractZone = AbstractZone.getZone(event.getBed().getLocation());
		if (abstractZone == null) return;
		
		RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractBed);
		if (rightResult.rightEnum != RightEnum.Success) {
			MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().split(" ")[0].toLowerCase();	
		if (!UltimateZones.getConfiguration().getStringList("sethome-commands").contains(command)) return;
		
		AbstractZone abstractZone = AbstractZone.getZone(event.getPlayer().getLocation());
		if (abstractZone == null) return;
		
		RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.DenySetHome);
		if (rightResult.rightEnum != RightEnum.Success) {
			MessageUtils.sendNegatedFailed(abstractZone, event.getPlayer(), rightResult);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		AbstractCreation abstractCreation = AbstractCreation.getCreation(event.getPlayer());
		if (abstractCreation != null) {
			abstractCreation.abort(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		AbstractCreation abstractCreation = AbstractCreation.getCreation(event.getPlayer());
		if (abstractCreation != null) {
			abstractCreation.abort(false);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
		ArmorStand armorStand = event.getRightClicked();

		AbstractZone abstractZone = AbstractZone.getZone(armorStand.getLocation());
		if (abstractZone == null) return;
		
		if (event.getArmorStandItem() == null || event.getArmorStandItem().getType() == Material.AIR) {
			if (event.getPlayerItem() != null && event.getPlayerItem().getType() != Material.AIR) {
				RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.ContainerPut);
				if (rightResult.rightEnum != RightEnum.Success) {
					MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
					event.setCancelled(true);
				}
			}
		} else {
			RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.ContainerGet);
			if (rightResult.rightEnum != RightEnum.Success) {
				MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		World world = event.getEntity().getWorld();
		Location location = event.getEntity().getLocation();
		
		AbstractZone abstractZone = AbstractZone.getZone(location);
		if (abstractZone == null) return;
		
		for (ItemStack itemStack : event.getDrops()) {
			Item item = world.dropItem(location, itemStack);
			
			PlayerListener.dropList.put(item.getUniqueId(), event.getEntity().getUniqueId());
		}
		
		event.getDrops().clear();
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onZoneInfo(ZoneInfoEvent event) {
		if (event.getPlugin() != null) return;
		
		AbstractZone abstractZone = AbstractZone.getZone(event.getZone());
		if (abstractZone == null) return;
		
		event.setPlugin(UltimateZones.getInstance());
		
		event.addInformation("id", abstractZone.getId());
		event.addInformation("owner", abstractZone.getPlayer());
		if (abstractZone.hasSynonym()) event.addInformation("synonym", abstractZone.getSynonym());
		event.addInformation("type", abstractZone.getType().getName());
		event.addInformation("haschilds", abstractZone.getZones(false).size() > 0);
		
		if (abstractZone instanceof SubZone) {
			event.addInformation("parent", ((SubZone) abstractZone).getParentId());
			event.addInformation("main", ((SubZone) abstractZone).getMainId());
		} else {
			event.addInformation("priority", ((MainZone) abstractZone).getPriority());
		}
	}
}