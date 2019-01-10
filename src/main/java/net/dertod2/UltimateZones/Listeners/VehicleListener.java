package net.dertod2.UltimateZones.Listeners;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.RightResult;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Utils.MessageUtils;

public class VehicleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player))
            return;

        AbstractZone abstractZone = AbstractZone.getZone(event.getVehicle().getLocation());
        if (abstractZone == null)
            return;

        RightResult rightResult = abstractZone.hasRight((Player) event.getAttacker(), Flag.BreakVehicles);
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, (Player) event.getAttacker(), rightResult);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player))
            return;
        if (event.getVehicle() instanceof Horse)
            return; // Ignore Horses

        AbstractZone abstractZone = AbstractZone.getZone(event.getVehicle().getLocation());
        if (abstractZone == null)
            return;

        RightResult rightResult = abstractZone.hasRight((Player) event.getEntered(), Flag.InteractVehicles);
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, (Player) event.getEntered(), rightResult);
            event.setCancelled(true);
        }
    }
}