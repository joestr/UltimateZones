package net.dertod2.UltimateZones.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.dertod2.UltimateZones.Binary.DelayThread;
import net.dertod2.UltimateZones.Classes.BlockCategory;
import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.RightResult;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.CreationWizard.AbstractCreation;
import net.dertod2.UltimateZones.Utils.InteractUtils;
import net.dertod2.UltimateZones.Utils.MessageUtils;

public class InteractListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockLeftClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		
		AbstractCreation abstractCreation = AbstractCreation.getCreation(event.getPlayer());
		if (abstractCreation != null) {
			abstractCreation.setLocation(event);
			if (event.isCancelled()) return;
		}		
		
		// ##########
		
		Material blockMaterial = event.getClickedBlock().getType();
		
		if (!InteractUtils.leftClickCheck(blockMaterial) && !event.getClickedBlock().getRelative(0, 1, 0).getType().equals(Material.FIRE)) return;
		AbstractZone abstractZone = AbstractZone.getZone(event.getClickedBlock().getLocation());
		if (abstractZone == null) return;
		
		RightResult rightResult = RightResult.Success;
		BlockCategory blockCategory = InteractUtils.identify(blockMaterial);
		
		switch (blockCategory) {
			case Container:
				rightResult = abstractZone.hasRight(event.getPlayer(), Flag.ContainerShow);
				break;
			case Normal:
				if (blockMaterial == Material.TRAP_DOOR) {
					rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractTrapdoor);
				} else if (InteractUtils.isFenceGate(blockMaterial)) {
					rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractFenceGate);
				} else if (event.getClickedBlock().getRelative(0, 1, 0).getType().equals(Material.FIRE)) {
					rightResult = abstractZone.hasRight(event.getPlayer(), Flag.Break, Material.FIRE);
				} else {
					//System.out.println("Remember: Handle LeftClickEvent for Type '" + BlockType.get(event.getClickedBlock()).getMachineName() + "'");
				}
				break;
			case Redstone:
				rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractRedstone);
				break;
			case SpecialContainer:
				if (event.getClickedBlock() instanceof Jukebox) {
					rightResult = abstractZone.hasRight(event.getPlayer(), ((Jukebox) event.getClickedBlock()).getPlaying() != Material.AIR ? Flag.ContainerGet : Flag.ContainerPut);
				} else {
					rightResult = abstractZone.hasRight(event.getPlayer(), Flag.ContainerPut);
				}
				break;
			case UsableContainer:
				rightResult = abstractZone.hasRight(event.getPlayer(), Flag.ContainerUse);
				break;
		}
		
		if (rightResult.rightEnum != RightEnum.Success) {
			MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockRightClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		AbstractCreation abstractCreation = AbstractCreation.getCreation(event.getPlayer());
		if (abstractCreation != null) {
			abstractCreation.setLocation(event);
			if (event.isCancelled()) return;
		}
		
		// ##########
		
		Material blockMaterial = event.getClickedBlock().getType();
		
		if (!InteractUtils.righClickCheck(blockMaterial, event.getPlayer().getItemInHand())) return;
		AbstractZone abstractZone = AbstractZone.getZone(event.getClickedBlock().getLocation());
		if (abstractZone == null) return;
		
		RightResult rightResult = RightResult.Success;
		BlockCategory blockCategory = InteractUtils.identify(blockMaterial);
		
		switch (blockCategory) {
		case Container:
			rightResult = abstractZone.hasRight(event.getPlayer(), Flag.ContainerShow);
			break;
		case Normal:
			if (blockMaterial == Material.TRAP_DOOR) {
				rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractTrapdoor);
			} else if (InteractUtils.isFenceGate(blockMaterial)) {
				rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractFenceGate);
			} else {
				//System.out.println("Not handled RightClick Block '" + BlockType.get(event.getClickedBlock()).getMachineName() + "' in UltimateZones v3");
			}
			break;
		case Redstone:
			rightResult = abstractZone.hasRight(event.getPlayer(), Flag.InteractRedstone);
			break;
		case SpecialContainer: // Jukebox & Flowerpot
			if (event.getClickedBlock() instanceof Jukebox) {
				rightResult = abstractZone.hasRight(event.getPlayer(), ((Jukebox) event.getClickedBlock()).getPlaying() != Material.AIR ? Flag.ContainerGet : Flag.ContainerPut);
			} else {
				rightResult = abstractZone.hasRight(event.getPlayer(), Flag.ContainerPut);
			}
			break;
		case UsableContainer:
			rightResult = abstractZone.hasRight(event.getPlayer(), Flag.ContainerUse);
			break;
		}
		
		if (rightResult.rightEnum != RightEnum.Success) {
			MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPhysical(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL) return;
		
		AbstractZone abstractZone = AbstractZone.getZone(event.getClickedBlock().getLocation());
		if (abstractZone == null) return;
		
		RightResult rightResult = RightResult.Success;
		
		Material material = event.getClickedBlock().getType();
		if (material == Material.SOIL) {
			rightResult = abstractZone.hasRight(event.getPlayer(), Flag.Break, material);
		} else if (material == Material.STONE_PLATE) {
			rightResult = abstractZone.hasRight(event.getPlayer(), Flag.TriggerStone, material);
		} else if (material == Material.WOOD_PLATE) {
			rightResult = abstractZone.hasRight(event.getPlayer(), Flag.TriggerWood, material);
		}

		if (rightResult.rightEnum != RightEnum.Success) {
			if (!DelayThread.plateList.contains(event.getPlayer().getUniqueId())) {			
				MessageUtils.sendFailed(abstractZone, event.getPlayer(), rightResult);
				DelayThread.plateList.add(event.getPlayer().getUniqueId());
			}
			
			event.setCancelled(true);
		}
	}
}