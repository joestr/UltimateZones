package net.dertod2.UltimateZones.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.dertod2.UltimateZones.Classes.BlockCategory;

public class InteractUtils {
	public static List<Material> interactRightBlockList = new ArrayList<Material>(); // Rightclick on an Block - public to allow seperate checks
	private static List<Material> interactLeftBlockList = new ArrayList<Material>(); // Rightclick on an Block
	private static List<Material> interactHandList = new ArrayList<Material>(); // Clicked with this item on an Block
	
	public static boolean leftClickCheck(Material material) {
		return InteractUtils.interactLeftBlockList.contains(material);
	}
	
	public static boolean righClickCheck(Material material, ItemStack itemStack) {
			return InteractUtils.interactRightBlockList.contains(material) || 
					(itemStack != null && InteractUtils.interactHandList.contains(itemStack.getType()));
	}
	
	public static BlockCategory identify(Material material) {
		if (InteractUtils.checkContainer(material)) return BlockCategory.Container;
		if (InteractUtils.checkUsableContainer(material)) return BlockCategory.UsableContainer;
		if (InteractUtils.checkSpecialContainer(material)) return BlockCategory.SpecialContainer;
		if (InteractUtils.checkRedstone(material)) return BlockCategory.Redstone;
		return BlockCategory.Normal;
	}	
	
	public static boolean isFenceGate(Material material) {
		if (material == Material.FENCE_GATE) return true;
		if (material == Material.ACACIA_FENCE_GATE) return true;
		if (material == Material.DARK_OAK_FENCE_GATE) return true;
		if (material == Material.JUNGLE_FENCE_GATE) return true;
		if (material == Material.BIRCH_FENCE_GATE) return true;
		if (material == Material.SPRUCE_FENCE_GATE) return true;
		return false;
	}
	
	// for interactentity event
	public static boolean checkVehicle(Material material) {
		if (material == Material.BOAT) return true;
		if (material == Material.MINECART) return true;
		if (material == Material.COMMAND_MINECART) return true;
		return false;
	}
	
	private static boolean checkContainer(Material material) {
		if (material == Material.BREWING_STAND) return true;
		if (material == Material.BURNING_FURNACE) return true;
		if (material == Material.CHEST) return true;
		if (material == Material.TRAPPED_CHEST) return true;
		if (material == Material.DISPENSER) return true;
		if (material == Material.FURNACE) return true;
		if (material == Material.DROPPER) return true;
		if (material == Material.HOPPER) return true;
		return false;
	}
	
	private static boolean checkUsableContainer(Material material) {
		if (material == Material.ANVIL) return true;
		if (material == Material.BEACON) return true;
		if (material == Material.CAULDRON) return true;
		return false;
	}
	
	private static boolean checkSpecialContainer(Material material) {
		if (material == Material.JUKEBOX) return true;
		if (material == Material.FLOWER_POT) return true;
		return false;
	}
	
	private static boolean checkRedstone(Material material) {
		if (material == Material.LEVER) return true;
		if (material == Material.REDSTONE_COMPARATOR_OFF) return true;
		if (material == Material.REDSTONE_COMPARATOR_ON) return true;
		if (material == Material.DIODE_BLOCK_OFF) return true;
		if (material == Material.DIODE_BLOCK_ON) return true;
		if (material == Material.STONE_BUTTON) return true;
		if (material == Material.NOTE_BLOCK) return true; // -> TODO As Extra Flag in next version?
		return false;
	}
	
	static {
		InteractUtils.interactRightBlockList.add(Material.ACACIA_FENCE_GATE);
		InteractUtils.interactRightBlockList.add(Material.ANVIL);
		InteractUtils.interactRightBlockList.add(Material.BEACON);
		InteractUtils.interactRightBlockList.add(Material.BED_BLOCK);
		InteractUtils.interactRightBlockList.add(Material.BIRCH_FENCE_GATE);
		InteractUtils.interactRightBlockList.add(Material.BREWING_STAND);
		InteractUtils.interactRightBlockList.add(Material.BURNING_FURNACE);
		InteractUtils.interactRightBlockList.add(Material.CAKE_BLOCK);
		InteractUtils.interactRightBlockList.add(Material.CAULDRON);
		InteractUtils.interactRightBlockList.add(Material.CHEST);
		InteractUtils.interactRightBlockList.add(Material.COMMAND); // TODO -> possible over abort inventory open?
		InteractUtils.interactRightBlockList.add(Material.CROPS);
		InteractUtils.interactRightBlockList.add(Material.DARK_OAK_FENCE_GATE);
		InteractUtils.interactRightBlockList.add(Material.DISPENSER);
		InteractUtils.interactRightBlockList.add(Material.DROPPER);
		InteractUtils.interactRightBlockList.add(Material.FENCE_GATE);
		InteractUtils.interactRightBlockList.add(Material.FLOWER_POT);
		InteractUtils.interactRightBlockList.add(Material.FURNACE);
		InteractUtils.interactRightBlockList.add(Material.HOPPER);
		InteractUtils.interactRightBlockList.add(Material.JUKEBOX);
		InteractUtils.interactRightBlockList.add(Material.JUNGLE_FENCE_GATE);
		InteractUtils.interactRightBlockList.add(Material.LEVER);
		InteractUtils.interactRightBlockList.add(Material.NOTE_BLOCK);
		InteractUtils.interactRightBlockList.add(Material.REDSTONE_COMPARATOR_OFF);
		InteractUtils.interactRightBlockList.add(Material.REDSTONE_COMPARATOR_ON);
		InteractUtils.interactRightBlockList.add(Material.DIODE_BLOCK_OFF);
		InteractUtils.interactRightBlockList.add(Material.DIODE_BLOCK_ON);
		InteractUtils.interactRightBlockList.add(Material.SPRUCE_FENCE_GATE);
		InteractUtils.interactRightBlockList.add(Material.STONE_BUTTON);
		InteractUtils.interactRightBlockList.add(Material.TRAP_DOOR);
		InteractUtils.interactRightBlockList.add(Material.TRAPPED_CHEST);
		
		InteractUtils.interactLeftBlockList.add(Material.STONE_BUTTON); // TODO -> Really only Stone Button?
		
		InteractUtils.interactHandList.add(Material.BOAT);
		InteractUtils.interactHandList.add(Material.DIAMOND_HOE);
		InteractUtils.interactHandList.add(Material.GOLD_HOE);
		InteractUtils.interactHandList.add(Material.INK_SACK);
		InteractUtils.interactHandList.add(Material.IRON_HOE);
		InteractUtils.interactHandList.add(Material.MINECART);
		InteractUtils.interactHandList.add(Material.COMMAND_MINECART);
		InteractUtils.interactHandList.add(Material.HOPPER_MINECART);
		InteractUtils.interactHandList.add(Material.EXPLOSIVE_MINECART);
		InteractUtils.interactHandList.add(Material.POWERED_MINECART);
		InteractUtils.interactHandList.add(Material.STORAGE_MINECART);
		InteractUtils.interactHandList.add(Material.STONE_HOE);
		InteractUtils.interactHandList.add(Material.WHEAT);
		InteractUtils.interactHandList.add(Material.WOOD_HOE);
	}
}