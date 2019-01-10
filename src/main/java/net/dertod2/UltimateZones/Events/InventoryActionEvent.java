package net.dertod2.UltimateZones.Events;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryActionEvent extends InventoryEvent implements Cancellable {
    private static HandlerList handlerList = new HandlerList();

    private final ItemStack toEntity;
    private final ItemStack toContainer;

    private final Action action;

    private boolean cancel;

    public InventoryActionEvent(InventoryView inventoryView, ItemStack toEntity, ItemStack toContainer, Action action) {
        super(inventoryView);

        this.toEntity = toEntity;
        this.toContainer = toContainer;

        this.action = action;
    }

    public HumanEntity getPlayer() {
        return transaction.getPlayer();
    }

    /**
     * Wherever the Entity fetches Items out of the Container
     */
    public boolean isToEntity() {
        return this.toEntity != null;
    }

    /**
     * The Items that the Entity selected and fetches out of the Container
     */
    public ItemStack getToEntity() {
        return this.toEntity;
    }

    /**
     * Wherever the Entity moves Items to the Container
     */
    public boolean isToContainer() {
        return this.toContainer != null;
    }

    /**
     * The Items that the Entity selected and puts inside the Container
     */
    public ItemStack getToContainer() {
        return this.toContainer;
    }

    /**
     * The involving upper Inventory
     */
    public Inventory getTopInventory() {
        return this.transaction.getTopInventory();
    }

    /**
     * The involving lower Inventory
     */
    public Inventory getBottomInventory() {
        return this.transaction.getBottomInventory();
    }

    /**
     * The Location of the Inventory. Returns null when the InventoryHolder isn't a
     * Block or Entity
     */
    public Location getLocation() {
        InventoryHolder inventoryHolder = this.transaction.getTopInventory().getHolder();

        if (inventoryHolder instanceof Entity)
            return ((Entity) inventoryHolder).getLocation();
        if (inventoryHolder instanceof DoubleChest)
            return ((DoubleChest) inventoryHolder).getLocation();
        if (inventoryHolder instanceof BlockState)
            return ((BlockState) inventoryHolder).getLocation();

        return null;
    }

    /**
     * The performed Action by the Entity
     */
    public Action getAction() {
        return this.action;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public enum Action {
        Move, Drag;
    }
}
