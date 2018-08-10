package net.dertod2.UltimateZones.Listeners;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.RightResult;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Events.InventoryActionEvent;
import net.dertod2.UltimateZones.Utils.MessageUtils;

public class InventoryListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventory(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;
		if (!isCheckableInventory(event.getInventory().getType())) return;

		AbstractZone abstractZone = AbstractZone.getZone(getLocation(event.getInventory().getHolder()));
		if (abstractZone == null) return;
		
		RightResult rightResult = abstractZone.hasRight((Player) event.getPlayer(), Flag.ContainerShow);
		if (rightResult.rightEnum != RightEnum.Success) {
			MessageUtils.sendFailed(abstractZone, (Player) event.getPlayer(), rightResult);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onInventoryAction(InventoryActionEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;
		if (!InventoryListener.isCheckableInventory(event.getInventory().getType())) return;
		
		AbstractZone abstractZone = AbstractZone.getZone(event.getLocation());
		if (abstractZone == null) return;

		if (event.isToContainer()) {
			RightResult rightResult = abstractZone.hasRight((Player) event.getPlayer(), Flag.ContainerPut, event.getToContainer().getType());
			if (rightResult.rightEnum != RightEnum.Success) {
				MessageUtils.sendFailed(abstractZone, (Player) event.getPlayer(), rightResult);
				event.setCancelled(true);
			}
		}

		if (event.isToEntity()) {
			RightResult rightResult = abstractZone.hasRight((Player) event.getPlayer(), Flag.ContainerGet, event.getToEntity().getType());
			if (rightResult.rightEnum != RightEnum.Success) {
				MessageUtils.sendFailed(abstractZone, (Player) event.getPlayer(), rightResult);
				event.setCancelled(true);
			}
		}
	}
	
	private static boolean isCheckableInventory(InventoryType inventoryType) {
		if (inventoryType.equals(InventoryType.ANVIL)) return false;
		if (inventoryType.equals(InventoryType.BEACON)) return false;
		if (inventoryType.equals(InventoryType.CRAFTING)) return false;
		if (inventoryType.equals(InventoryType.CREATIVE)) return false;
		if (inventoryType.equals(InventoryType.ENCHANTING)) return false;
		if (inventoryType.equals(InventoryType.ENDER_CHEST)) return false;
		if (inventoryType.equals(InventoryType.MERCHANT)) return false;
		if (inventoryType.equals(InventoryType.PLAYER)) return false;
		if (inventoryType.equals(InventoryType.WORKBENCH)) return false;
		
		return true;
	}
	
	private static Location getLocation(InventoryHolder inventoryHolder) {
		if (inventoryHolder instanceof Entity) return ((Entity) inventoryHolder).getLocation();
		if (inventoryHolder instanceof DoubleChest) return ((DoubleChest) inventoryHolder).getLocation();
		if (inventoryHolder instanceof BlockState) return ((BlockState) inventoryHolder).getLocation();
		
		return null;
	}
}