package net.dertod2.UltimateZones.Listeners;

import java.util.Vector;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.InventoryHolder;

import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.RightResult;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Utils.MessageUtils;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        AbstractZone abstractZone = AbstractZone.getZone(event.getBlockPlaced().getLocation());
        if (abstractZone == null)
            return;

        RightResult rightResult = abstractZone.hasRight(event.getPlayer(), Flag.Place,
                event.getBlockPlaced().getType());
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDestroy(BlockBreakEvent event) {
        AbstractZone abstractZone = AbstractZone.getZone(event.getBlock().getLocation());
        if (abstractZone == null)
            return;

        RightResult rightResult = abstractZone.hasRight(event.getPlayer(),
                event.getBlock() instanceof InventoryHolder ? Flag.BreakContainer : Flag.Break,
                event.getBlock().getType());
        if (rightResult.rightEnum != RightEnum.Success) {
            MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (event.getBlocks().isEmpty())
            return;

        AbstractZone pistonZone = AbstractZone.getZone(event.getBlock().getLocation());

        Vector<Block> blockList = new Vector<Block>();
        for (int i = 0; i < event.getBlocks().size() + 2; i++)
            blockList.add(event.getBlock().getRelative(event.getDirection(), i));

        for (Block block : blockList) {
            AbstractZone blockZone = AbstractZone.getZone(block.getLocation());

            if (pistonZone == null && blockZone == null)
                continue;
            if (pistonZone != null && blockZone == null)
                continue;
            if (pistonZone != null && blockZone != null && pistonZone.getId() == blockZone.getId())
                continue;
            // TODO Check if Zones having the same owner & parent -> problem with cities ->
            // extra Zone Setting/Flag ?

            event.setCancelled(true);
            return;
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.getRetractLocation().getBlock().isEmpty() || !event.isSticky())
            return;

        Block block = event.getRetractLocation().getBlock();

        AbstractZone retractZone = AbstractZone.getZone(block.getLocation());
        if (retractZone == null)
            return;

        Block target = block.getRelative(event.getDirection().getOppositeFace());
        AbstractZone targetZone = AbstractZone.getZone(target.getLocation());

        if (targetZone == null || retractZone.getId() != targetZone.getId())
            event.setCancelled(true);
    }
}