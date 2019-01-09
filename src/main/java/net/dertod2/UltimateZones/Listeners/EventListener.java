package net.dertod2.UltimateZones.Listeners;

import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.dertod2.UltimateZones.Events.InventoryActionEvent;
import net.dertod2.UltimateZones.Events.InventoryActionEvent.Action;

public class EventListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		InventoryAction inventoryAction = event.getAction();
		ClickType clickType = event.getClick();

		if (inventoryAction.equals(InventoryAction.NOTHING) || inventoryAction.equals(InventoryAction.CLONE_STACK) || inventoryAction.equals(InventoryAction.DROP_ALL_CURSOR) || inventoryAction.equals(InventoryAction.DROP_ONE_CURSOR)) return;
		if (clickType.equals(ClickType.CREATIVE)) return;
		if ((event.getClick().equals(ClickType.DOUBLE_CLICK) && event.getInventory().getType().equals(InventoryType.PLAYER)) || !isCheckableInventory(event.getInventory().getType())) return;

		if (clickType.equals(ClickType.UNKNOWN)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Unknown Inventory Action by " + event.getWhoClicked().getName() + ", aborted Event!");
			event.setCancelled(true);
			return;
		}	

		InventoryType inventoryType = getActionInventory(event.getInventory(), event.getRawSlot());
		if (inventoryType.equals(InventoryType.PLAYER)) {
			if (clickType.equals(ClickType.CONTROL_DROP)) return;
			if (clickType.equals(ClickType.DROP)) return;
			if (clickType.equals(ClickType.NUMBER_KEY)) return;
			if (!inventoryAction.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && !clickType.equals(ClickType.DOUBLE_CLICK)) return;
		}

		boolean moveToPlayer = ((!inventoryType.equals(InventoryType.PLAYER) && event.getCurrentItem().getType().getId() != 0) || (inventoryType.equals(InventoryType.PLAYER) && clickType.equals(ClickType.DOUBLE_CLICK)  && event.getCursor().getType().getId() != 0));
		boolean moveToContainer = (event.getCursor().getType().getId() != 0 && !clickType.equals(ClickType.DOUBLE_CLICK)) || (inventoryType.equals(InventoryType.PLAYER) && clickType.isShiftClick() && event.getCurrentItem().getType().getId() != 0);

		ItemStack toPlayerStack = moveToPlayer ? (!clickType.equals(ClickType.DOUBLE_CLICK) ? event.getCurrentItem().clone() : getDoubleClickStack(event.getView().getTopInventory(), event.getCursor())) : null;
		ItemStack toContainerStack = moveToContainer ? (isPlaceItem(inventoryAction) ? event.getCursor().clone() : event.getCurrentItem().clone()) : null;

		if (moveToPlayer) toPlayerStack.setAmount(inventoryAction.equals(InventoryAction.PICKUP_ALL) ? toPlayerStack.getAmount() : inventoryAction.equals(InventoryAction.PICKUP_ONE) ? 1 : inventoryAction.equals(InventoryAction.PICKUP_HALF) ? toPlayerStack.getAmount() / 2 : toPlayerStack.getAmount());
		if (moveToContainer) toContainerStack.setAmount(inventoryAction.equals(InventoryAction.PLACE_ALL) ? toContainerStack.getAmount() : inventoryAction.equals(InventoryAction.PLACE_ONE) ? 1 : toContainerStack.getAmount());

		// Comfort Checks - Ignore Inventory Event if...
		if (clickType.equals(ClickType.DOUBLE_CLICK) && !event.getView().getTopInventory().contains(event.getCursor().getType())) return;

		InventoryActionEvent inventoryActionEvent = new InventoryActionEvent(event.getView(), toPlayerStack, toContainerStack, Action.Move);
		Bukkit.getPluginManager().callEvent(inventoryActionEvent);

		if (inventoryActionEvent.isCancelled()) { 
			event.setCancelled(true);
			event.setResult(Result.DENY);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (!isCheckableInventory(event.getInventory().getType())) return;
		if (getDragActionInventory(event.getInventory(), event.getRawSlots()).equals(InventoryType.PLAYER)) return;

		// Fetch Zones - Throw Event
		HumanEntity humanEntity = event.getWhoClicked();
		
		InventoryActionEvent inventoryActionEvent = new InventoryActionEvent(event.getView(), null, event.getOldCursor(), Action.Drag);
		Bukkit.getPluginManager().callEvent(inventoryActionEvent);

		if (inventoryActionEvent.isCancelled()) { 
			event.setResult(Result.DENY);
			event.setCursor(null);
			
			humanEntity.setItemOnCursor(new ItemStack(Material.AIR));
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
	
	private static InventoryType getActionInventory(Inventory inventory, int rawSlot) {
		if (rawSlot <= (inventory.getSize() - 1)) return inventory.getType();
		return InventoryType.PLAYER;
	}
	
	private static ItemStack getDoubleClickStack(Inventory inventory, ItemStack identifierStack) {
		Map<Integer, ? extends ItemStack> stackList = inventory.all(identifierStack.getType());
		
		@SuppressWarnings("deprecation")
		ItemStack foundStack = new ItemStack(identifierStack.getType(), 0, identifierStack.getData().getData());
		for (ItemStack itemStack : stackList.values()) if (itemStack.isSimilar(identifierStack)) foundStack.setAmount(foundStack.getAmount() + itemStack.getAmount());
		
		return foundStack;
	}
	
	private static boolean isPlaceItem(InventoryAction inventoryAction) {
		return inventoryAction.equals(InventoryAction.PLACE_ALL) || inventoryAction.equals(InventoryAction.PLACE_ONE) || inventoryAction.equals(InventoryAction.PLACE_SOME);
	}
	
	private static InventoryType getDragActionInventory(Inventory inventory, Set<Integer> rawSlots) {
		for (Integer rawSlot : rawSlots) {
			if (rawSlot > (inventory.getSize() - 1)) continue;
			return inventory.getType();
		}
		
		return InventoryType.PLAYER;
	}
}