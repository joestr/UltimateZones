package net.dertod2.UltimateZones.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Golem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.RightResult;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Utils.MessageUtils;
import net.dertod2.UltimateZones.Utils.UltimateUtils;

@SuppressWarnings("deprecation")
public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player))
            return;

        AbstractZone abstractZone = AbstractZone.getZone(event.getEntity().getLocation());
        if (abstractZone == null)
            return;

        RightResult rightResult = abstractZone.hasRight((Player) event.getOwner(), Flag.TameAnimals);
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, (Player) event.getOwner(), rightResult);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingEntityDestroy(HangingBreakByEntityEvent event) {
        Player player = UltimateUtils.getAttacker(event.getRemover());

        if (player == null && UltimateZones.getConfiguration().getBoolean("deactivate-hanging-damage-by-mobs", false)) {
            event.setCancelled(true);
            return;
        }

        AbstractZone abstractZone = AbstractZone.getZone(event.getEntity().getLocation());
        if (abstractZone == null)
            return;

        RightResult rightResult = abstractZone.hasRight(player, Flag.Break,
                UltimateUtils.getItemType(event.getEntity()));
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, player, rightResult);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Animals || event.getEntity() instanceof Golem
                || event.getEntity() instanceof Villager) {
            Player player = UltimateUtils.getAttacker(event.getDamager());
            if (player == null)
                return;

            AbstractZone abstractZone = AbstractZone.getZone(event.getEntity().getLocation());
            if (abstractZone == null)
                return;

            RightResult rightResult = abstractZone.hasRight(player, Flag.SlapAnimals);
            if (rightResult.rightEnum != RightEnum.Success) {
                MessageUtils.sendFailed(abstractZone, player, rightResult);
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof ArmorStand) {
            Player player = UltimateUtils.getAttacker(event.getDamager());
            if (player == null)
                return;

            AbstractZone abstractZone = AbstractZone.getZone(event.getEntity().getLocation());
            if (abstractZone == null)
                return;

            RightResult rightResult = abstractZone.hasRight(player, Flag.Break);
            if (rightResult.rightEnum != RightEnum.Success) {
                MessageUtils.sendFailed(abstractZone, player, rightResult);
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof ItemFrame) {
            Player player = UltimateUtils.getAttacker(event.getDamager());
            if (player == null)
                return;

            ItemFrame itemFrame = (ItemFrame) event.getEntity();

            AbstractZone abstractZone = AbstractZone.getZone(itemFrame.getLocation());
            if (abstractZone == null)
                return;

            ItemStack itemStack = itemFrame.getItem();
            RightResult rightResult = abstractZone.hasRight(player,
                    itemStack == null || itemStack.getType() == Material.AIR ? Flag.Break : Flag.ContainerGet); // TODO
                                                                                                                // null
                                                                                                                // check
                                                                                                                // ?
            if (rightResult.rightEnum != RightEnum.Success) {
                MessageUtils.sendFailed(abstractZone, player, rightResult);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSheepRightClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Sheep))
            return;
        if (!UltimateUtils.equalsHandItem(event.getPlayer(), new MaterialData(Material.SHEARS)))
            return;

        AbstractZone abstractZone = AbstractZone.getZone(event.getRightClicked().getLocation());
        if (abstractZone == null)
            return;

        RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractAnimals);
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityFeed(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Ageable))
            return;
        if (!UltimateUtils.isCorrectFeedingItem((Ageable) event.getRightClicked(), event.getPlayer().getItemInHand()))
            return;

        AbstractZone abstractZone = AbstractZone.getZone(event.getRightClicked().getLocation());
        if (abstractZone == null)
            return;

        RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.FeedAnimals);
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemFrameRotate(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame))
            return;
        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

        AbstractZone abstractZone = AbstractZone.getZone(itemFrame.getLocation());
        if (abstractZone == null)
            return;

        ItemStack itemStack = itemFrame.getItem();
        RightResult rightResult = abstractZone.hasRight(event.getPlayer(),
                itemStack == null || itemStack.getType() == Material.AIR ? Flag.ContainerPut : Flag.RotateFrames); // TODO
                                                                                                                   // null
                                                                                                                   // check
                                                                                                                   // ?
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
            event.setCancelled(true);
        }
    }

}