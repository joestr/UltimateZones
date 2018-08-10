package net.dertod2.UltimateZones.Classes;

import java.util.List;

import org.bukkit.Material;

public interface IRights {

	/**
	 * Fetches all Flags that has been given to this class
	 * @return A List with all given Flags
	 */
	public List<Flag> getFlagList();
	
	/**
	 * Returns all Blocks (as {@link Material} List) that can be placed (or negated)
	 * @return A List contains all Blocks
	 */
	public List<Material> getPlaceList();
	
	/**
	 * Returns all Blocks (as {@link Material} List) that can be broken (or negated)
	 * @return A List contains all Blocks
	 */
	public List<Material> getBreakList();
	
	/**
	 * The List of Flags as Integers for saving in databases
	 * @return A List with all Flags
	 */
	public List<Integer> getPlainFlagList();
	
	/**
	 * The List of all placeable blocks as Strings for saving in database
	 * @return A List contains all Blocks
	 */
	public List<String> getPlainBreakList();
	
	/**
	 * The List of all breakable blocks as Strings for saving in database
	 * @return A List contains all Blocks
	 */
	public List<String> getPlainPlaceList();
	
	/**
	 * Sets uor unsets an Flag for this Class
	 * @param flag The {@link Flag}
	 * @return Wherever the Flag was given (true) or removed (false)
	 */
	public boolean setFlag(Flag flag);
	
	/**
	 * Adds or removes an material from the placable List
	 * @param material The Material
	 * @return Wherever the Material was added (true) or removed (false)
	 */
	public boolean setPlace(Material material);
	
	/**
	 * Adds or removes an material from the breakable List
	 * @param material The Material
	 * @return Wherever the Material was added (true) or removed (false)
	 */
	public boolean setBreak(Material material);
}