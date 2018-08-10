package net.dertod2.UltimateZones.Utils;

public class StringUtils {
	
	/**
	 * Parses an new String out of an String Array
	 * @param args
	 * @return String
	 */
	public static String getString(String[] args) {
		return StringUtils.getString(args, 0, args.length, "");
	}
	
	/**
	 * Parses an new String out of an String Array
	 * @param args
	 * @param delimeter
	 * @return String
	 */
	public static String getString(String[] args, String delimeter) {
		return StringUtils.getString(args, 0, args.length, delimeter);
	}
	
	/**
	 * Parses an new String out of an String Array<br />
	 * The New String start at the given Index in String[]
	 * @param args
	 * @param beginIndex
	 * @return String
	 */
	public static String getString(String[] args, int beginIndex) {
		return StringUtils.getString(args, beginIndex, args.length, "");
	}
	
	/**
	 * Parses an new String out of an String Array<br />
	 * The New String start at the given Index in String[]<br />
	 * Adds between two String the given delimeter. Not after the last Args Entry
	 * @param args
	 * @param beginIndex
	 * @param delimeter
	 * @return String
	 */
	public static String getString(String[] args, int beginIndex, String delimeter) {
		return StringUtils.getString(args, beginIndex, args.length, delimeter);
	}
	
	/**
	 * Parses an new String out of an String Array<br />
	 * The New String start at the given Index in String[]<br />
	 * The New String ends at the given Index in String[]<br />
	 * Adds between two String the given delimeter. Not after the last Args Entry
	 * @param args
	 * @param beginIndex
	 * @param endIndex
	 * @param delimeter
	 * @return String
	 */
	public static String getString(String[] args, int beginIndex, int endIndex, String delimeter) {
		if (args.length <= 0) return "";
		
		StringBuilder stringBuilder = new StringBuilder("");
		
		for (int i = beginIndex; i < endIndex; i++) {
			stringBuilder.append(args[i]);
			stringBuilder.append(delimeter);
		}
		
		if (stringBuilder.length() == 0) return "";
		if (delimeter.length() > 0) return stringBuilder.delete(stringBuilder.length() - delimeter.length(), stringBuilder.length()).toString().toString();
		return stringBuilder.toString().trim();
	}
}